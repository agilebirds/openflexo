/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.xmlcode;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLMapId {

    private Vector _mapEntryList;
    private String _description;
    
    public XMLMapId(Node mapIdNode, XMLMapping anXMLMapping) throws InvalidModelException
    {

        super();

        Node tempAttribute;
        NamedNodeMap attributes;
        
         NodeList propertiesNodeList;
        Node tempNode;

        if (!(mapIdNode.getNodeName().equals(XMLMapping.mapIdLabel))) {
            throw new InvalidModelException("Invalid tag '" + mapIdNode.getNodeName() + "' found in model file");
        } 

        attributes = mapIdNode.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            tempAttribute = attributes.item(i);
            /*if (tempAttribute.getNodeName().equals(XMLMapping.nameLabel)) {
                nameIsSpecified = true;
                name = tempAttribute.getNodeValue();
            } else {
                throw new InvalidModelException("Invalid attribute '" + tempAttribute.getNodeName() + "' found in model file for tag 'entity'");
            }*/
        }

        _mapEntryList = new Vector();
        
        propertiesNodeList = mapIdNode.getChildNodes();
        for (int i = 0; i < propertiesNodeList.getLength(); i++) {
            tempNode = propertiesNodeList.item(i);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                if (tempNode.getNodeName().equals(XMLMapping.descriptionLabel)) {
                    if ((tempNode.getChildNodes().getLength() == 1) && (tempNode.getFirstChild().getNodeType() == Node.TEXT_NODE)) {
                        setDescription(tempNode.getFirstChild().getNodeValue());
                        //System.out.println("Description = "+getDescription());
                    }
                } else if (tempNode.getNodeName().equals(XMLMapping.mapLabel)) {
                    MapEntry newMapEntry = new MapEntry(tempNode);
                    _mapEntryList.add(newMapEntry);
                    //System.out.println("Register map entry "+newMapEntry.getEntityClass()+" and "+newMapEntry.getKeyValueProperty().getName());
                } else {
                    throw new InvalidModelException("Invalid tag '" + tempNode.getNodeName() + "' found in model file for tag 'entity'");
                }
            } else if (tempNode.getNodeType() == Node.TEXT_NODE) {
                // Non significative text will be simply ignored
                if (tempNode.getNodeValue().trim().length() > 0) {
                    throw new InvalidModelException("Invalid text found in model file");
                }
            } else if (tempNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                // Simply ignore it
            } else if (tempNode.getNodeType() == Node.COMMENT_NODE) {
                // Simply ignore it
            } else {
                throw new InvalidModelException("Invalid xml tag found as child of 'mapId' tag in model file");
            }
        }

        if (_mapEntryList.size() == 0) {
            throw new InvalidModelException("No identifier mapping defined");
        }
 
    }
    
    public String getDescription() 
    {
        return _description;
    }

    public void setDescription(String description) 
    {
        _description = description;
    }

    public class MapEntry 
    {
        private Class entityClass;
        private SingleKeyValueProperty keyValueProperty;
        private String entityClassName;
        private String identifierAccessorName;
                
        public MapEntry(Node mapEntryNode) throws InvalidModelException
        {

            super();

            Node tempAttribute;
            NamedNodeMap attributes;
            
            if (!(mapEntryNode.getNodeName().equals(XMLMapping.mapLabel))) {
                throw new InvalidModelException("Invalid tag '" + mapEntryNode.getNodeName() + "' found in model file");
            } 

            attributes = mapEntryNode.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                tempAttribute = attributes.item(i);
                if (tempAttribute.getNodeName().equals(XMLMapping.entityClassLabel)) {
                     entityClassName = tempAttribute.getNodeValue();
                } else if (tempAttribute.getNodeName().equals(XMLMapping.identifierAccessorLabel)) {
                    identifierAccessorName = tempAttribute.getNodeValue();
                } else {
                    throw new InvalidModelException("Invalid attribute '" + tempAttribute.getNodeName() + "' found in model file for tag 'entity'");
                }
            }

            try {
                entityClass = Class.forName(entityClassName);
            } catch (ClassNotFoundException e) {
                throw new InvalidModelException("Class " + entityClassName + " not found.");
            }
            
            keyValueProperty = new SingleKeyValueProperty(entityClass, identifierAccessorName, false);
 
        }

        public Class getEntityClass()
        {
            return entityClass;
        }

        public SingleKeyValueProperty getKeyValueProperty() 
        {
            return keyValueProperty;
        }

    }

    private Hashtable mapEntriesForClass = new Hashtable();
    
    private MapEntry mapEntryForClass (Class aClass) throws NoMapIdEntryException
    {
        MapEntry returned = (MapEntry)mapEntriesForClass.get(aClass);
        if (returned == null) {
            for (Enumeration en=_mapEntryList.elements(); en.hasMoreElements();) {
                MapEntry next = (MapEntry)en.nextElement();
                if (next.getEntityClass().isAssignableFrom(aClass)) {
                    // May match
                    if ((returned == null) 
                            || (returned.getEntityClass().isAssignableFrom(next.getEntityClass()))) {
                        returned = next;
                    }
                }
            }
            if (returned == null) {
                throw new NoMapIdEntryException("Could not find identifier map entry for "+aClass);
            }
            //System.out.println("MapEntry for "+aClass+" is "+returned);
            mapEntriesForClass.put(aClass, returned);
        }
        return returned;
    }
    
    public String getIdentifierAsStringForObject (XMLSerializable object) throws NoMapIdEntryException
    {
        SingleKeyValueProperty kvProperty = mapEntryForClass(object.getClass()).getKeyValueProperty();
        return KeyValueDecoder.valueForKey(object, kvProperty, StringEncoder.getDefaultInstance());
    }
    
    public String getIdentifierAsStringForObject (XMLSerializable object, StringEncoder stringEncoder) throws NoMapIdEntryException
    {
        SingleKeyValueProperty kvProperty = mapEntryForClass(object.getClass()).getKeyValueProperty();
        return KeyValueDecoder.valueForKey(object, kvProperty, stringEncoder);
    }
    
    public static class NoMapIdEntryException extends Exception {
    	
    	public NoMapIdEntryException(String message) {
			super(message);
		}
    }
}
