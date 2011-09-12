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

import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * <p>
 * Utility class providing cloning facilities
 * </p>
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see XMLCoder
 * @see XMLDecoder
 * @see XMLMapping
 */
public class Cloner
{

    /** Stores mapping that will be used for decoding */
    private XMLMapping xmlMapping;

    /**
     * Stores already serialized objects where key is the serialized object and
     * value is a
     * 
     * <pre>
     * Integer
     * </pre>
     * 
     * instance coding the unique identifier of the object
     */
    private Hashtable<Object,Object> alreadyCloned;

    /** Stores builder object that will be used for cloning */
    private Object builder;

    private XMLDecoder decoder;

    private StringEncoder stringEncoder;

    /**
     * Creates a new <code>Cloner</code> instance given an
     * <code>XMLMapping</code> object
     * 
     * @param anXmlMapping
     *            an <code>XMLMapping</code> value
     */
    public Cloner(XMLMapping anXmlMapping)
    {
        this(anXmlMapping, null, StringEncoder.getDefaultInstance());
    }

    /**
     * Creates a new <code>Cloner</code> instance given an
     * <code>XMLMapping</code> object
     * 
     * @param anXmlMapping
     *            an <code>XMLMapping</code> value
     */
    public Cloner(XMLMapping anXmlMapping, StringEncoder encoder)
    {
        this(anXmlMapping, null, encoder);
    }
    
    /**
     * Creates a new <code>Cloner</code> instance given an
     * <code>XMLMapping</code> object and a builder
     * 
     * @param anXmlMapping
     *            an <code>XMLMapping</code> value
     */
    public Cloner(XMLMapping anXmlMapping, Object aBuilder)
    {
        this(anXmlMapping, aBuilder, StringEncoder.getDefaultInstance());
    }

    /**
     * Creates a new <code>Cloner</code> instance given an
     * <code>XMLMapping</code> object and a builder
     * 
     * @param anXmlMapping
     *            an <code>XMLMapping</code> value
     * @param encoder TODO
     */
    public Cloner(XMLMapping anXmlMapping, Object aBuilder, StringEncoder encoder)
    {

        super();
        xmlMapping = anXmlMapping;
        alreadyCloned = new Hashtable<Object, Object>();
        decoder = new XMLDecoder(xmlMapping, aBuilder, encoder);
        builder = aBuilder;
        stringEncoder = encoder;
    }

    /**
     * Clone <code>anObject</code> according to mapping
     * <code>xmlMapping</code>, and returns a newly created object
     * 
     * @param anObject
     *            an <code>Object</code> value
     * @param xmlMapping
     *            a <code>XMLMapping</code> value
     * @return an <code>Object</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs
     * @exception SAXException
     *                if an error occurs
     * @exception ParserConfigurationException
     *                if an error occurs
     * @exception InvalidModelException
     *                if an error occurs
     * @exception InvalidXMLDataException
     *                if an error occurs
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static Object cloneObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping) throws InvalidObjectSpecificationException,
            InvalidModelException, AccessorInvocationException
    {
        return cloneObjectWithMapping(anObject, xmlMapping, StringEncoder.getDefaultInstance());
    }

    /**
     * Clone <code>anObject</code> with <code>stringEncoder</code> according to mapping
     * <code>xmlMapping</code>, and returns a newly created object
     * 
     * @param anObject
     *            an <code>Object</code> value
     * @param xmlMapping
     *            a <code>XMLMapping</code> value
     * @param encoder
     *            a <code>StringEncoder</code> value
     * @return an <code>Object</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs
     * @exception SAXException
     *                if an error occurs
     * @exception ParserConfigurationException
     *                if an error occurs
     * @exception InvalidModelException
     *                if an error occurs
     * @exception InvalidXMLDataException
     *                if an error occurs
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static Object cloneObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping, StringEncoder encoder) throws InvalidObjectSpecificationException,
            InvalidModelException, AccessorInvocationException
    {

        return cloneObjectWithMapping(anObject, xmlMapping, null, encoder);
    }

    /**
     * Clone <code>anObject</code> according to mapping
     * <code>xmlMapping</code>, and returns a newly created object
     * 
     * @param anObject
     *            an <code>Object</code> value
     * @param xmlMapping
     *            a <code>XMLMapping</code> value
     * @return an <code>Object</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs
     * @exception SAXException
     *                if an error occurs
     * @exception ParserConfigurationException
     *                if an error occurs
     * @exception InvalidModelException
     *                if an error occurs
     * @exception InvalidXMLDataException
     *                if an error occurs
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static Object cloneObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping, Object aBuilder)
            throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException
    {
        return cloneObjectWithMapping(anObject, xmlMapping, aBuilder, StringEncoder.getDefaultInstance());
    }

    /**
     * Clone <code>anObject</code> with <code>stringEncoder</code> according to mapping
     * <code>xmlMapping</code>, and returns a newly created object
     * 
     * @param anObject
     *            an <code>Object</code> value
     * @param xmlMapping
     *            a <code>XMLMapping</code> value
     * @param stringEncoder
     *            a <code>StringEncoder</code> value
     * @return an <code>Object</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs
     * @exception SAXException
     *                if an error occurs
     * @exception ParserConfigurationException
     *                if an error occurs
     * @exception InvalidModelException
     *                if an error occurs
     * @exception InvalidXMLDataException
     *                if an error occurs
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static Object cloneObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping, Object aBuilder, StringEncoder stringEncoder)
            throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException
    {

        Cloner cloner = new Cloner(xmlMapping, aBuilder, stringEncoder);
        Object returned = cloner.cloneObject(anObject);
        cloner.runCloningFinalization();
        return returned;
    }

    /**
     * Encode to an XML string object <code>anObject</code> according to
     * mapping defined for this <code>XMLCoder</code>, and returns this newly
     * created string.
     * 
     * @param anObject
     *            an <code>Object</code> value
     * @return an <code>Object</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs
     * @exception SAXException
     *                if an error occurs
     * @exception ParserConfigurationException
     *                if an error occurs
     * @exception InvalidModelException
     *                if no valid mapping nor mapping file were specified
     * @exception InvalidXMLDataException
     *                if an error occurs
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    @SuppressWarnings("unchecked")
	private Object cloneObject(Object anObject) throws InvalidObjectSpecificationException, InvalidModelException,
            AccessorInvocationException
    {

        if (anObject == null) {
            throw new InvalidObjectSpecificationException("Object is null");
        }
        if (xmlMapping == null) {
            throw new InvalidModelException("No mapping specified.");
        }
        if (stringEncoder._isEncodable(anObject.getClass())) {
        	return stringEncoder._decodeObject(stringEncoder._encodeObject(anObject),anObject.getClass());
        }
        if (anObject instanceof PropertiesKeyValueProperty.UndecodableProperty) {
        	return ((PropertiesKeyValueProperty.UndecodableProperty)anObject).clone();
        }
        return cloneObject(anObject, entityForObject(anObject));
    }

    /**
     * Internally used during coding process.<br>
     * Build and returns new element given an object <code>anObject</code> and
     * an XML document <code>aDocument</code>
     * 
     * @param anObject
     *            an <code>Object</code> value
     * @param aDocument
     *            a <code>Document</code> value
     * @return an <code>Element</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs
     * @exception InvalidModelException
     *                if an error occurs
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    private ModelEntity entityForObject(Object anObject) throws InvalidObjectSpecificationException, InvalidModelException,
            AccessorInvocationException
    {

        ModelEntity modelEntity = null;

        if (anObject == null) {
            throw new InvalidObjectSpecificationException("Object is null");
        }

        // Search the right ModelEntity from class name
        // NB: the best one is the more specialized.

        Class searchedClass = anObject.getClass();
        while ((searchedClass != null) && (xmlMapping.entityWithClassName(searchedClass.getName()) == null)) {
            searchedClass = searchedClass.getSuperclass();
        }

        if (searchedClass != null) {
            modelEntity = xmlMapping.entityWithClassName(searchedClass.getName());
        }

        if (modelEntity == null) {
            throw new InvalidModelException("Tag matching '" + anObject.getClass().getName() + "' not found in model");
        }

        return modelEntity;
    }

    /**
     * Internally used during coding process.<br>
     * Build and returns new element given an object <code>anObject</code>,
     * an XML document <code>aDocument</code> and a model entity
     * <code>aModelEntity</code>
     * 
     * @param anObject
     *            an <code>Object</code> value
     * @param aModelEntity
     *            a <code>ModelEntity</code> value
     * @param aDocument
     *            a <code>Document</code> value
     * @return an <code>Element</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs
     * @exception InvalidModelException
     *                if an error occurs
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    private Object cloneObject(Object anObject, ModelEntity aModelEntity) throws InvalidObjectSpecificationException, InvalidModelException,
            AccessorInvocationException
    {

    	Object returnedObject;
        ModelProperty modelProperty;

        if (anObject == null) {
            return null;
        }

        // Is this object already cloned ?
        returnedObject = alreadyCloned.get(anObject);
        if (returnedObject != null) {
            // Already cloned object
            return returnedObject;
        }

        // Clone new object
        returnedObject = decoder.instanciateNewObject(aModelEntity);
        alreadyCloned.put(anObject, returnedObject);

        for (Enumeration e = aModelEntity.getModelProperties(); e.hasMoreElements();) {

            modelProperty = (ModelProperty) e.nextElement();
            try {
            	cloneProperty(anObject, returnedObject, modelProperty);
            }
            catch (AccessorInvocationException ex) {
            	// May happen when a primitive object is cast to a primitive
            	// (eg an Integer null value casted to int)
            }

        } // end of for ()

        return returnedObject;
    }

    private void runCloningFinalization() throws AccessorInvocationException
    {
        Object[] params = { builder };
        for (Enumeration e = alreadyCloned.elements(); e.hasMoreElements();) {
            Object next = e.nextElement();
            ModelEntity entity = xmlMapping.entityWithClassName(next.getClass().getName());
            try {
                if (xmlMapping.hasBuilderClass()) {
                    if (entity.hasFinalizerWithParameter()) {
                        entity.getFinalizerWithParameter().invoke(next, params);
                    } else if (entity.hasFinalizerWithoutParameter()) {
                        entity.getFinalizerWithoutParameter().invoke(next, (Object[])null);
                    }
                }
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e2) {
                e2.getTargetException().printStackTrace();
                throw new AccessorInvocationException("Exception " + e2.getClass().getName() + " caught during finalization.", e2);
            }
        }
        for (Enumeration e = alreadyCloned.keys(); e.hasMoreElements();) {
            Object next = e.nextElement();
            alreadyCloned.remove(next);
        }
    }

    /**
     * Internally used during coding process.<br>
     * Build and returns new element given an object <code>anObject</code>,
     * an XML document <code>aDocument</code> and a model entity
     * <code>aModelEntity</code>
     * 
     * @param anObject
     *            an <code>Object</code> value
     * @param aModelEntity
     *            a <code>ModelEntity</code> value
     * @param aDocument
     *            a <code>Document</code> value
     * @return an <code>Element</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs
     * @exception InvalidModelException
     *                if an error occurs
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    private void cloneProperty(Object clonedObject, Object newObject, ModelProperty modelProperty)
            throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException
    {
    	if (!modelProperty.isCopyable())
    		return;
        boolean isCloneable = modelProperty.isCloneable();

        // First, get the key-value property
        KeyValueProperty keyValueProperty = modelProperty.getKeyValueProperty();

        if (keyValueProperty instanceof SingleKeyValueProperty) {

            SingleKeyValueProperty singleKeyValueProperty = (SingleKeyValueProperty) keyValueProperty;

            boolean primitiveProperty = singleKeyValueProperty.classIsPrimitive(stringEncoder);
            if (primitiveProperty) {
                String stringValue = KeyValueDecoder.valueForKey(clonedObject, singleKeyValueProperty, stringEncoder);
                if (stringValue != null) {
                    KeyValueCoder.setValueForKey(newObject, stringValue, singleKeyValueProperty, stringEncoder);
                }
            } else {
                Object newValue = KeyValueDecoder.objectForKey(clonedObject, singleKeyValueProperty);
                if ((isCloneable) && (newValue != null)) {
                    newValue = cloneObject(newValue);
                }
                if (newValue != null) {
                    KeyValueCoder.setObjectForKey(newObject, newValue, singleKeyValueProperty);
                }
            }
        }

        else if (keyValueProperty instanceof VectorKeyValueProperty) {
            VectorKeyValueProperty vectorKeyValueProperty = (VectorKeyValueProperty) keyValueProperty;

            Vector values = KeyValueDecoder.vectorForKey(clonedObject, (VectorKeyValueProperty) keyValueProperty);
            if (values != null) {
                Vector<Object> newVector = new Vector<Object>();
                for (Enumeration e = values.elements(); e.hasMoreElements();) {
                	Object temp = e.nextElement();
                	
                    if ((isCloneable) && (temp != null)) {
                        temp = cloneObject(temp);
                    }
                    newVector.add(temp);
                }
                KeyValueCoder.setVectorForKey(newObject, newVector, vectorKeyValueProperty);
            }
        }

        else if (keyValueProperty instanceof ArrayKeyValueProperty) {

            ArrayKeyValueProperty arrayKeyValueProperty = (ArrayKeyValueProperty) keyValueProperty;

            Object[] values = KeyValueDecoder.arrayForKey(clonedObject, arrayKeyValueProperty);
            if (values != null) {
                Vector<Object> newVector = new Vector<Object>();
                for (int i = 0; i < values.length; i++) {
                	Object temp =values[i];
                    if ((isCloneable) && (temp != null)) {
                        temp = cloneObject(temp);
                    }
                    newVector.add(temp);
                }
                KeyValueCoder.setArrayForKey(newObject, newVector, arrayKeyValueProperty);
            }
        }

        else if (keyValueProperty instanceof HashtableKeyValueProperty) {

            HashtableKeyValueProperty hashtableKeyValueProperty = (HashtableKeyValueProperty) keyValueProperty;

            Hashtable values = KeyValueDecoder.hashtableForKey(clonedObject, hashtableKeyValueProperty);
            if (values != null) {
                Hashtable<Object,Object> newHashtable = new Hashtable<Object, Object>();
                for (Enumeration e = values.keys(); e.hasMoreElements();) {
                    Object key = e.nextElement();
                    Object newValue = values.get(key);
                    Object newKey;
                    if ((key instanceof XMLSerializable) && (isCloneable) && (key != null)) {
                        newKey = cloneObject(key);
                    } else {
                        newKey = key;
                    }
                    if ((isCloneable) && (newValue != null)) {
                        newValue = cloneObject(newValue);
                    }
                    newHashtable.put(newKey, newValue);
                }
                KeyValueCoder.setHashtableForKey(newObject, newHashtable, hashtableKeyValueProperty);
            }
        }

    }

}
