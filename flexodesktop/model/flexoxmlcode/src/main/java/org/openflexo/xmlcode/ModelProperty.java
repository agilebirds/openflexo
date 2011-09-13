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
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * <p>
 * <code>ModelProperty</code> is internally used in
 * {@link org.openflexo.xmlcode.XMLMapping}
 * <p>
 * You never need to used it directly, this class maps
 * <code>&lt;property&gt;<code> tags in <i>model file</i>.
 *
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see XMLMapping
 */
public class ModelProperty
{

	public enum PropertyType {
		 SINGLE_PROPERTY_TYPE, ARRAY_PROPERTY_TYPE, VECTOR_PROPERTY_TYPE, HASHTABLE_PROPERTY_TYPE, PROPERTIES_PROPERTY_TYPE, UNMAPPED_ATTRIBUTES_TYPE, COMPLEX_PROPERTY_TYPE;
	}
	
    /** Stores name of this property */
    private String name = null;

    /** Stores xmlTag(s) of this property */
    private String[] xmlTag;

    /** Stores contains value of this property */
    private String contains = null;

    /** Stores contains class of this property */
    private Class containsClass = null;

    /** Stores boolean indicating if this property is attribute or not */
    private boolean isAttribute = false;

    /** Stores boolean indicating if this property is a text attribute or not */
    private boolean isText = false;

    /**
     * Stores String indicating what attribute to use as a key to store
     * hashtable (if this property is a HASHTABLE_PROPERTY_TYPE): this is not
     * specified if this property use built-in key scheme
     */
    private String keyToUse = null;

    /**
     * Stores context for this property
     */
    private String context = null;

    /**
     * Stores boolean indicating if this property corresponds to the primary
     * relation relative to embedding perspective of the model
     */
    private boolean isPrimary = true;

    /**
     * Stores boolean indicating if this property must be duplicated while
     * objects are cloned
     */
    private boolean isCloneable = true;

    /**
     * Stores boolean indicating if this property must be copied while
     * objects are cloned
     */
    private boolean isCopyable = true;
    
    /**
     * Stores type of this property, which could be:
     * <ul>
     * <li>{@link #SINGLE_PROPERTY_TYPE}: this property matches a single
     * object or primitive</li>
     * <li>{@link #ARRAY_PROPERTY_TYPE}: this property matches a array of
     * objects or primitives</li>
     * <li>{@link #VECTOR_PROPERTY_TYPE}: this property matches a list of
     * objects stored in a {@link java.util.Vector} or a sub-class of
     * {@link java.util.Vector}.</li>
     * <li>{@link #HASHTABLE_PROPERTY_TYPE}: this property matches a list of
     * objects stored in a {@link java.util.Hashtable} or a sub-class of
     * {@link java.util.Hashtable}.</li>
     * <li>{@link #PROPERTIES_PROPERTY_TYPE}: this property matches a dynamic
     * list of objects stored in a {@link java.util.Hashtable} or a sub-class of
     * {@link java.util.Hashtable} where key is a String obtained and parsed
     * from/as XML element name: therefore, there is no need to implement model
     * for contained data</li>
     * </ul>
     */
    private PropertyType propertyType;

    /** Stores related model entity */
    private ModelEntity modelEntity;

    /** Stores related KeyValueProperty */
    private KeyValueProperty keyValueProperty;

    /**
     * Handled xml tags (it could have many handled tags is this property
     * represents a list of values (vector or hashtable)
     */
    private String[] handledXMLTags = null;

    /** Description of this property */
    private String description;

    /** Default value to be ignored */
    protected String ignoreDefaultValue = null;
    

    /**
     * Creates a new <code>ModelEntity</code> instance<br>
     * This constructor should be called for dynamically XMLMapping building
     * purposes.<br>
     * Use {@link ModelEntity#registerNewModelProperty(ModelProperty)} to
     * register this new <code>ModelProperty</code> object in an
     * <code>ModelEntity</code> instance.
     * 
     * @param aModelEntity
     *            a <code>ModelEntity</code> value
     * @param aPropertyType
     *            a <code>int</code> value, which could be:
     *            <ul>
     *            <li>{@link #SINGLE_PROPERTY_TYPE}: this property matches a
     *            single object or primitive</li>
     *            <li>{@link #ARRAY_PROPERTY_TYPE}: this property matches a
     *            array of objects or primitives</li>
     *            <li>{@link #VECTOR_PROPERTY_TYPE}: this property matches a
     *            list of objects stored in a {@link java.util.Vector} or a
     *            sub-class of {@link java.util.Vector}.</li>
     *            <li>{@link #HASHTABLE_PROPERTY_TYPE}: this property matches
     *            a list of objects stored in a {@link java.util.Hashtable} or a
     *            sub-class of {@link java.util.Hashtable}.</li>
     *            <li>{@link #PROPERTIES_PROPERTY_TYPE}: this property matches
     *            a dynamic list of objects stored in a
     *            {@link java.util.Hashtable} or a sub-class of
     *            {@link java.util.Hashtable} where key is a String obtained and
     *            parsed from/as XML element name: therefore, there is no need
     *            to implement model for contained data</li>
     *            </ul>
     * @exception InvalidModelException
     *                if an error occurs
     */
    public ModelProperty(ModelEntity aModelEntity, String aName, String someXMLTags, String aContainsTag, String aKeyToUse, PropertyType aPropertyType,
            boolean isAttributeFlag, boolean isTextFlag) throws InvalidModelException
    {

        super();

        modelEntity = aModelEntity;
        name = aName;
        parseXMLTags(someXMLTags);
        contains = aContainsTag;
        keyToUse = aKeyToUse;
        isAttribute = isAttributeFlag;
        isText = isTextFlag;
        propertyType = aPropertyType;

        if (modelEntity == null) {
            throw new InvalidModelException("Specified ModelEntity object is null");
        }

        init();
    }

    /**
     * Internaly used to initialize ModelProperty
     */
    private void init()
    {
        if (contains != null) {
            try {
                containsClass = Class.forName(contains);
            } catch (ClassNotFoundException e) {
                throw new InvalidModelException("Invalid attribute contains found: matches no known class: " + contains);
            }
        }

        if (isAttribute) {
            if (isText) {
                throw new InvalidModelException("Invalid attribute tag found: only one of 'text' and 'attribute' tags could be set to true ('YES' value)'");
            }
            if (propertyType != PropertyType.SINGLE_PROPERTY_TYPE) {
                throw new InvalidModelException(
                        "Invalid attribute tag found: 'attribute' tag could not be set to true ('YES' value)' while attribute type is not single.");
            }
        }
        if (propertyType != PropertyType.SINGLE_PROPERTY_TYPE) {
            if (isText) {
                throw new InvalidModelException(
                        "Invalid attribute tag found: 'text' tag could not be set to true ('YES' value)' while attribute type is not single.");
            }
        }

        if (name == null) {
            throw new InvalidModelException("No attribute 'name' defined for tag 'property' in model file");
        }

        if ((xmlTag == null) && (!isText) && (propertyType != PropertyType.PROPERTIES_PROPERTY_TYPE) && (contains == null)) {
            throw new InvalidModelException(
                    "No attribute 'xmlTag' or 'contains' defined for tag 'property' in model file while xml tag 'text' is not set to true.");
        }

        if ((xmlTag != null) && (getDefaultXmlTag() != null)) {
            if (getDefaultXmlTag().equalsIgnoreCase(XMLMapping.classNameLabel)) {
                // throw new InvalidModelException("Invalid xml property name:
                // "+xmlTag+" is a reserved keyword");
            }
            if (getDefaultXmlTag().equalsIgnoreCase(XMLMapping.keyLabel)) {
                // throw new InvalidModelException("Invalid xml property name:
                // "+xmlTag+" is a reserved keyword");
            }
        }

        if (name.lastIndexOf(".") > -1) {
            if (!getModel().serializeOnly) {
                throw new InvalidModelException("Invalid xml property name: " + xmlTag + " compound keys are allowed only in 'serializeOnly' models");
            }
        }

        if (propertyType == PropertyType.SINGLE_PROPERTY_TYPE) {
			if (ParameteredKeyValueProperty.isParameteredKeyValuePropertyPattern(name)) {
				propertyType = PropertyType.COMPLEX_PROPERTY_TYPE;
				keyValueProperty = new ParameteredKeyValueProperty(modelEntity.getRelatedClass(), name, !getModel().serializeOnly);
			}
			else {
				keyValueProperty = new SingleKeyValueProperty(modelEntity.getRelatedClass(), name, !getModel().serializeOnly);
			}
        }

        if (propertyType == PropertyType.ARRAY_PROPERTY_TYPE) {
            keyValueProperty = new ArrayKeyValueProperty(modelEntity.getRelatedClass(), name, !getModel().serializeOnly);
        }

        else if (propertyType == PropertyType.VECTOR_PROPERTY_TYPE) {
            keyValueProperty = new VectorKeyValueProperty(modelEntity.getRelatedClass(), name, !getModel().serializeOnly);
        }

        else if (propertyType == PropertyType.HASHTABLE_PROPERTY_TYPE) {
            keyValueProperty = new HashtableKeyValueProperty(modelEntity.getRelatedClass(), name, !getModel().serializeOnly);
        }

        else if (propertyType == PropertyType.PROPERTIES_PROPERTY_TYPE) {
            keyValueProperty = new PropertiesKeyValueProperty(modelEntity.getRelatedClass(), name, !getModel().serializeOnly);
            handledXMLTags = null;
        }
        
        else if (propertyType == PropertyType.UNMAPPED_ATTRIBUTES_TYPE) {
            keyValueProperty = new PropertiesKeyValueProperty(modelEntity.getRelatedClass(), name, !getModel().serializeOnly);
            handledXMLTags = null;
        }
    }

    protected XMLMapping getModel()
    {
        return modelEntity.getModel();
    }

    /**
     * Creates a new <code>ModelProperty</code> instance, given a node
     * <code>aPropertyNode</code>
     * 
     * @param aPropertyNode
     *            a <code>Node</code> value
     * @param aModelEntity
     *            a <code>ModelEntity</code> value
     */
    public ModelProperty(Node aPropertyNode, ModelEntity aModelEntity)
    {

        super();

        Node tempAttribute;
        NamedNodeMap attributes;
        boolean nameIsSpecified = false;
        boolean xmlTagIsSpecified = false;
        isAttribute = false;
        isText = false;

        propertyType = PropertyType.SINGLE_PROPERTY_TYPE;

        modelEntity = aModelEntity;
        if (modelEntity == null) {
            throw new InvalidModelException("Specified ModelEntity object is null");
        }

        if (!(aPropertyNode.getNodeName().equals(XMLMapping.propertyLabel))) {
            throw new InvalidModelException("Invalid tag '" + aPropertyNode.getNodeName() + "' found in model file");
        } // end of if ()

        attributes = aPropertyNode.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            tempAttribute = attributes.item(i);
            if (tempAttribute.getNodeName().equals(XMLMapping.nameLabel)) {
                nameIsSpecified = true;
                name = tempAttribute.getNodeValue();
            } else if (tempAttribute.getNodeName().equals(XMLMapping.xmlTagLabel)) {
                xmlTagIsSpecified = true;
                parseXMLTags(tempAttribute.getNodeValue());
            } else if (tempAttribute.getNodeName().equals(XMLMapping.descriptionLabel)) {
                setDescription(tempAttribute.getNodeValue());
            } else if (tempAttribute.getNodeName().equals(XMLMapping.containsLabel)) {
                contains = tempAttribute.getNodeValue();
            } else if (tempAttribute.getNodeName().equals(XMLMapping.attributeLabel)) {
                isAttribute = tempAttribute.getNodeValue().equalsIgnoreCase("yes");
            } else if (tempAttribute.getNodeName().equals(XMLMapping.primaryLabel)) {
                isPrimary = tempAttribute.getNodeValue().equalsIgnoreCase("yes");
            } else if (tempAttribute.getNodeName().equals(XMLMapping.cloneableLabel)) {
                isCloneable = tempAttribute.getNodeValue().equalsIgnoreCase("yes");
            } else if (tempAttribute.getNodeName().equals(XMLMapping.copyableLabel)) {
            	isCopyable = tempAttribute.getNodeValue().equalsIgnoreCase("yes");
            } else if (tempAttribute.getNodeName().equals(XMLMapping.ignoreDefaultValueLabel)) {
            	ignoreDefaultValue = tempAttribute.getNodeValue();
            } else if (tempAttribute.getNodeName().equals(XMLMapping.keyLabel)) {
                keyToUse = tempAttribute.getNodeValue();
            } else if (tempAttribute.getNodeName().equals(XMLMapping.typeLabel)) {
                if (tempAttribute.getNodeValue().equalsIgnoreCase(XMLMapping.singleLabel)) {
                    propertyType = PropertyType.SINGLE_PROPERTY_TYPE;
                } else if (tempAttribute.getNodeValue().equalsIgnoreCase(XMLMapping.arrayLabel)) {
                    propertyType = PropertyType.ARRAY_PROPERTY_TYPE;
                } else if (tempAttribute.getNodeValue().equalsIgnoreCase(XMLMapping.vectorLabel)) {
                    propertyType = PropertyType.VECTOR_PROPERTY_TYPE;
                } else if (tempAttribute.getNodeValue().equalsIgnoreCase(XMLMapping.hashtableLabel)) {
                    propertyType = PropertyType.HASHTABLE_PROPERTY_TYPE;
                } else if (tempAttribute.getNodeValue().equalsIgnoreCase(XMLMapping.propertiesLabel)) {
                    propertyType = PropertyType.PROPERTIES_PROPERTY_TYPE;
                } else if (tempAttribute.getNodeValue().equalsIgnoreCase(XMLMapping.unmappedAttributesLabel)) {
                    propertyType = PropertyType.UNMAPPED_ATTRIBUTES_TYPE;
                } else {
                    throw new InvalidModelException("Invalid attribute value '" + tempAttribute.getNodeValue() + "' found in model file for tag '"
                            + XMLMapping.typeLabel + "'");
                }
            } else if (tempAttribute.getNodeName().equals(XMLMapping.textLabel)) {
                isText = tempAttribute.getNodeValue().equalsIgnoreCase("yes");
            } else if (tempAttribute.getNodeName().equals(XMLMapping.contextLabel)) {
                context = tempAttribute.getNodeValue();
            } else {
                throw new InvalidModelException("Invalid attribute '" + tempAttribute.getNodeName() + "' found in model file for tag 'property'");
            }
        }

        init();
    }

    /**
     * Returns <code>name</code> of this <code>ModelProperty</code>
     * 
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns related KeyValueProperty
     */
    public KeyValueProperty getKeyValueProperty()
    {

        return keyValueProperty;
    }

    /**
     * Returns String indicating what attribute to use as a key to store
     * hashtable (if this property is a HASHTABLE_PROPERTY_TYPE): this is not
     * specified if this property use built-in key scheme
     * 
     * @return a <code>String</code> value
     */
    public String getKeyToUse()
    {

        return keyToUse;
    }

    /** Return default value to be ignored, if any
     *  
     * @return null when none defined
     */

    public String getIgnoreDefaultValue()
	{
		return ignoreDefaultValue;
	}
    
    /**
     * Returns <code>isAttribute</code> flag of this
     * <code>ModelProperty</code>
     * 
     * @return a <code>String</code> value
     */
    public boolean getIsAttribute()
    {
        return isAttribute;
    }

    /**
     * Returns <code>propertyType</code> value of this
     * <code>ModelProperty</code>
     * 
     * @return a <code>int</code> value
     */
    public PropertyType getPropertyType()
    {
        return propertyType;
    }

    /**
     * Returns <code>isText</code> flag of this <code>ModelProperty</code>
     * 
     * @return a <code>String</code> value
     */
    public boolean getIsText()
    {
        return isText;
    }

    /**
     * Returns a String representation of this object suitable for debugging
     * purposes
     * 
     * @return a <code>String</code> value
     */
    @Override
	public String toString()
    {
        String returnedString = "    <property name=" + '"' + getName() + '"';

        if (getXmlTags() != null) {
            returnedString += " " + XMLMapping.xmlTagLabel + "=" + '"' + getConcatenedXmlTag() + '"';
        }
        if (isAttribute) {
            returnedString += " " + XMLMapping.attributeLabel + "=" + '"' + "yes" + '"';
        }
        if (propertyType != PropertyType.SINGLE_PROPERTY_TYPE) {
            if (propertyType == PropertyType.ARRAY_PROPERTY_TYPE) {
                returnedString += " " + XMLMapping.typeLabel + "=" + '"' + XMLMapping.arrayLabel + '"';
            } else if (propertyType == PropertyType.VECTOR_PROPERTY_TYPE) {
                returnedString += " " + XMLMapping.typeLabel + "=" + '"' + XMLMapping.vectorLabel + '"';
            } else if (propertyType == PropertyType.HASHTABLE_PROPERTY_TYPE) {
                returnedString += " " + XMLMapping.typeLabel + "=" + '"' + XMLMapping.hashtableLabel + '"';
            } else if (propertyType == PropertyType.PROPERTIES_PROPERTY_TYPE) {
                returnedString += " " + XMLMapping.typeLabel + "=" + '"' + XMLMapping.propertiesLabel + '"';
            } else if (propertyType == PropertyType.UNMAPPED_ATTRIBUTES_TYPE) {
                returnedString += " " + XMLMapping.typeLabel + "=" + '"' + XMLMapping.unmappedAttributesLabel + '"';
            } else {
                returnedString += " " + XMLMapping.typeLabel + "=" + '"' + "???" + '"';
            }
        }
        if (isText) {
            returnedString += " text=" + '"' + "yes" + '"';
        }
        if (getKeyToUse() != null) {
            returnedString += " " + XMLMapping.keyLabel + "=" + '"' + getKeyToUse() + '"';
        }
        if (containsClass != null) {
            returnedString += " " + XMLMapping.containsLabel + "=" + '"' + containsClass.getName() + '"';
        }
        if (context != null) {
            returnedString += " " + XMLMapping.contextLabel + "=" + '"' + context + '"';
        }
        returnedString += "/>\n";

        return returnedString;
    }

    /**
     * Return boolean indicating if XML tag <code>aTagName</code> is handled
     * by this property.
     */
    public boolean handlesXMLTag(String aTagName)
    {

        if (xmlTag == null) {
            return false;
        } else {
            for (int i = 0; i < xmlTag.length; i++) {
                if ((xmlTag[i] != null) && (xmlTag[i].equals(aTagName))) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Return handled xml tags (it could have many handled tags is this property
     * represents a list of values (vector or hashtable) or not well-defined
     * values
     */
    public String[] getXmlTags()
    {
        if (((xmlTag == null) || (handledXMLTagsNeedsUpdate)) && (containsClass != null)) {
            XMLMapping model = modelEntity.getModel();
            // System.out.println ("updateHandledXMLTags() for "+getName()+"
            // contains="+containsClass.getName()+" already
            // "+model.modelEntitiesStoredByClassName.size()+" entities");
            Vector v = new Vector();
            for (Enumeration e = model.modelEntitiesStoredByClassName.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                // System.out.println ("Class "+key);
                Class type = null;
                try {
                    type = Class.forName(key);
                } catch (ClassNotFoundException e2) {
                }
                ;
                ModelEntity entity = model.modelEntitiesStoredByClassName.get(key);
                if (containsClass.isAssignableFrom(type)) {
                    if (context == null) {
                        if (entity.getXmlTags() != null) {
                            for (int i = 0; i < entity.getXmlTags().length; i++) {
                                v.add(entity.getXmlTags()[i]);
                            }
                        }
                    }
                    else {
                        if (entity.getXmlTags(context) != null) {
                            for (int i = 0; i < entity.getXmlTags(context).length; i++) {
                                v.add(entity.getXmlTags(context)[i]);
                            }
                        }
                    }
                }
            }
            xmlTag = new String[v.size()];
            for (int i = 0; i < v.size(); i++) {
                xmlTag[i] = (String) v.elementAt(i);
            }
            handledXMLTagsNeedsUpdate = false;
        }
        return xmlTag;
    }

    protected boolean handledXMLTagsNeedsUpdate = true;

    public void updateHandledXMLTags()
    {
        handledXMLTagsNeedsUpdate = true;
    }

    /**
     * Used if this property represent a list of values (vector or hashtable) to
     * parse xmlTag (using ',') and extract handled xml tag
     */
    private void parseXMLTags(String someXMLTags)
    {
        StringTokenizer st = new StringTokenizer(someXMLTags, ",");
        Vector temp = new Vector();
        while (st.hasMoreElements()) {
            String anXMLTag = (String) st.nextElement();
            temp.add(anXMLTag);
        }
        if (temp.size() == 0) {
            throw new InvalidModelException("No XML tags specified in model file for entity " + getName());
        } else {
            xmlTag = new String[temp.size()];
            int i = 0;
            for (Enumeration e = temp.elements(); e.hasMoreElements(); i++) {
                String next = (String) e.nextElement();
                xmlTag[i] = next;
                // Debugging.debug ("Found tag "+next+" for entity "+getName());
            }
        }
    }

    public boolean hasXmlTag()
    {
    	return ((xmlTag != null) && (xmlTag.length > 0));
    }
    
    /**
     * Returns default <code>xmlTag</code> of this <code>ModelEntity</code>
     * 
     * @return a <code>String</code> value
     */
    public String getDefaultXmlTag()
    {

        if ((xmlTag != null) && (xmlTag.length > 0)) {
            return xmlTag[0];
        } else {
            throw new InvalidModelException("No XML tag defined for property '" + getName() + "'. Is it an abstract entity ?");
            //return null;
        }
    }

    /**
     * Returns <code>xmlTag</code> of this <code>ModelEntity</code>
     * 
     * @return a <code>String</code> value
     */
    public String getConcatenedXmlTag()
    {
        String returned = "";
        if (xmlTag != null) {
            for (int i = 0; i < xmlTag.length; i++) {
                if (i > 0) {
                    returned += ",";
                }
                returned += xmlTag[i];
            }
            return returned;
        } else {
            return "null";
        }
    }

    public boolean isInherited(ModelEntity entity)
    {
        return (entity.inheritedModelProperties.get(getName()) != null);
    }

    public ModelEntity getInheritedEntity(ModelEntity entity)
    {
        return entity.inheritedModelProperties.get(getName());
    }

    public boolean isPrimitive()
    {

        return ((getType().isPrimitive()) 
        		|| (StringEncoder.isConvertable(getType())) 
        		|| (propertyType == PropertyType.PROPERTIES_PROPERTY_TYPE)
        		|| (propertyType == PropertyType.UNMAPPED_ATTRIBUTES_TYPE));
    }
    
    public boolean isSingle() {
    	return propertyType==PropertyType.SINGLE_PROPERTY_TYPE || propertyType==null;
    }
    
    public boolean isArray() {
    	return propertyType==PropertyType.ARRAY_PROPERTY_TYPE;
    }
    
    public boolean isVector() {
    	return propertyType==PropertyType.VECTOR_PROPERTY_TYPE;
    }

    public boolean isHashtable() {
    	return propertyType==PropertyType.HASHTABLE_PROPERTY_TYPE;
    }
    
    public boolean isProperties() {
    	return propertyType==PropertyType.PROPERTIES_PROPERTY_TYPE;
    }
    
    public boolean isUnmappedAttributes() {
    	return propertyType==PropertyType.UNMAPPED_ATTRIBUTES_TYPE;
    }
    
    public boolean isComplex() {
    	return propertyType==PropertyType.COMPLEX_PROPERTY_TYPE;
    }
    
    /**
     * Wheter this property should be serialized as an attribute of the XML node or as child node of the XML node
     * @return
     */
    public boolean isAttribute()
    {
        return isAttribute;
    }

    /**
	 * If there are several references to this object, specifies if this one
	 * should be the one that performs the serialization or if another node
	 * should do it and this one should rather use a reference
	 * 
	 * @return
	 */
    public boolean isPrimary()
    {
    	return isPrimary;
    }

    /**
     * Wheter the value of this property should be cloned or just copies a reference of it
     * @return
     */
    public boolean isCloneable()
    {
        return isCloneable;
    }
    
    public boolean isCopyable() {
		return isCopyable;
	}

    public Class getType()
    {
        return getKeyValueProperty().getType();
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

	public ModelEntity getModelEntity() {
		return modelEntity;
	}

}
