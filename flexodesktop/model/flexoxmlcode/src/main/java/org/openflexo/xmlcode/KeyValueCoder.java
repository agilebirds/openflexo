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

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * <p>
 * <code>KeyValueCoder</code> is an utility class that allow to manipulate
 * (set methods) properties of object (field or get/set methods pair) from
 * values represented as String (very usefull for xml coding/decoding)
 * </p>
 * Those operations are done using the {@link java.lang.reflect} package and all
 * developments done in {@link KeyValueProperty} class.<br>
 * This class is used in the context of XML decoding using a mapping model
 * allowing to instanciate directly object from XML strings or streams (and
 * reverse operation).<br>
 * Manipulated types are all the java primitives (<code>int</code>,
 * <code>long</code>, <code>short</code>, <code>double</code>,
 * <code>float</code>, <code>boolean</code>, <code>byte</code>,
 * <code>char</code>) or a {@link java.util.Date}, a
 * {@link java.lang.String} a {@link java.io.File} or a {@link java.net.URL})<br>
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see KeyValueProperty
 * @see XMLCoder
 * @see XMLDecoder
 * @see XMLMapping
 * @see KeyValueDecoder
 * 
 */
public class KeyValueCoder
{

	/**
	 * Returns <code>KeyValueProperty</code> object matching
	 * <code>propertyName</code> value
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @return a <code>KeyValueProperty</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	protected static KeyValueProperty getKeyValuePropertyFromName (Class clazz, String propertyName, boolean setMethodIsMandatory)
			throws InvalidObjectSpecificationException
			{
		int modifiers = clazz.getModifiers();
		//System.out.println("Class "+clazz.getName()+" public="+Modifier.isPublic(modifiers));

		while (!(Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) && clazz != null) {
			clazz = clazz.getSuperclass();
			modifiers = clazz.getModifiers();
		}

		Hashtable<String, KeyValueProperty> kvpHash = classCache.get(clazz);
		if (kvpHash == null) {
			classCache.put(clazz, kvpHash = new Hashtable<String, KeyValueProperty>());
		}
		KeyValueProperty resp = kvpHash.get(propertyName);
		if (resp != null) {
			return resp;
		}
		try {
			resp = new HashtableKeyValueProperty(clazz, propertyName, setMethodIsMandatory);
		} catch (InvalidKeyValuePropertyException e) {
		}
		if (resp == null) {
			try {
				resp = new VectorKeyValueProperty(clazz, propertyName, setMethodIsMandatory);
			} catch (InvalidKeyValuePropertyException e) {
			}
		}
		if (resp == null) {
			try {
				if (ParameteredKeyValueProperty.isParameteredKeyValuePropertyPattern(propertyName)) {
					resp = new ParameteredKeyValueProperty(clazz, propertyName, setMethodIsMandatory);
				}
				else {
					resp = new SingleKeyValueProperty(clazz, propertyName, setMethodIsMandatory);
				}
			} catch (InvalidKeyValuePropertyException e) {
				throw new InvalidObjectSpecificationException("Can't handle property " + propertyName + " for object " + clazz.getName() + ". See following for details: " + e);
			}
		}
		kvpHash.put(propertyName, resp);
		return resp;
			}

	private static final Hashtable<Class, Hashtable<String, KeyValueProperty>> classCache = new Hashtable<Class, Hashtable<String,KeyValueProperty>>();
	/**
	 * <p>
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty </code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a java primitive (<code>int</code>,
	 * <code>long</code>, <code>short</code>, <code>double</code>,
	 * <code>float</code>, <code>boolean</code>, <code>byte</code>,
	 * <code>char</code>) or a {@link java.util.Date}, a
	 * {@link java.lang.String} a {@link java.io.File} or a {@link java.net.URL})
	 * </p> . No assertion is done on specific type and value is parsed from
	 * <code>textValue</code> according to corresponding type.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param textValue
	 *            a <code>String</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if keyValueProperty type is not a java primitive nor a
	 *                <code>Date</code> nor a <code>String</code>
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setValueForKey(Object object, String textValue, SingleKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{
		setValueForKey(object, textValue, keyValueProperty, StringEncoder.getDefaultInstance());
	}

	/**
	 * <p>
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty </code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a java primitive (<code>int</code>,
	 * <code>long</code>, <code>short</code>, <code>double</code>,
	 * <code>float</code>, <code>boolean</code>, <code>byte</code>,
	 * <code>char</code>) or a {@link java.util.Date}, a
	 * {@link java.lang.String} a {@link java.io.File} or a {@link java.net.URL}
	 * of an object that can be encoded by <code>stringEncoder</code>.
	 * </p>No assertion is done on specific type and value is parsed from
	 * <code>textValue</code> according to corresponding type.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param textValue
	 *            a <code>String</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @param stringEncoder
	 *            a <code>StringEncoder</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if keyValueProperty type is not a java primitive nor a
	 *                <code>Date</code> nor a <code>String</code>
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setValueForKey(Object object, String textValue, SingleKeyValueProperty keyValueProperty, StringEncoder stringEncoder) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		// Debugging.debug ("setValueForKey() called with '"+textValue+"' for
		// keyValueProperty '"+keyValueProperty.getName()+"' for object of class
		// "+object.getClass().getName());

		if (keyValueProperty.getType().isPrimitive()) {
			// Class of keyValueProperty is a primitive such as int, long,
			// short, double, float, boolean, byte, char
			if (keyValueProperty.getType() == Boolean.TYPE) {
				setBooleanAsStringForKey(object, textValue, keyValueProperty);
			} else if (keyValueProperty.getType() == Character.TYPE) {
				setCharacterAsStringForKey(object, textValue, keyValueProperty);
			} else if (keyValueProperty.getType() == Byte.TYPE) {
				setByteAsStringForKey(object, textValue, keyValueProperty);
			} else if (keyValueProperty.getType() == Short.TYPE) {
				setShortAsStringForKey(object, textValue, keyValueProperty);
			} else if (keyValueProperty.getType() == Integer.TYPE) {
				setIntegerAsStringForKey(object, textValue, keyValueProperty);
			} else if (keyValueProperty.getType() == Long.TYPE) {
				setLongAsStringForKey(object, textValue, keyValueProperty);
			} else if (keyValueProperty.getType() == Float.TYPE) {
				setFloatAsStringForKey(object, textValue, keyValueProperty);
			} else if (keyValueProperty.getType() == Double.TYPE) {
				setDoubleAsStringForKey(object, textValue, keyValueProperty);
			}
		} else {
			if (textValue == null) {
				throw new InvalidXMLDataException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
						+ " supplied value is null");
			} else {
				Object objectValue = null;
				try {
					objectValue = stringEncoder._decodeObject(textValue, keyValueProperty.getType());
				}
				catch (InvalidDataException e) {
					//System.out.println("InvalidDataException raised while evaluating "+keyValueProperty.getName()+" with value="+textValue+" for object "+object);
					e.printStackTrace();
				}
				if (objectValue != null) {
					setObjectForKey(object, objectValue, keyValueProperty);
				}
			}
		}

	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>propertyName</code> for object <code>object</code> asserting
	 * that corresponding keyValueProperty type is a java primitive (<code>int</code>,
	 * <code>long</code>, <code>short</code>, <code>double</code>,
	 * <code>float</code>, <code>boolean</code>, <code>byte</code>,
	 * <code>char</code>) or a <code>Date</code> or a <code>String</code>
	 * object. No assertion is done on specific type and value is parsed from
	 * <code>textValue</code> according to corresponding type.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param textValue
	 *            a <code>String</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setValueForKey(Object object, String textValue, String propertyName) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		setValueForKey(object, textValue, (SingleKeyValueProperty) getKeyValuePropertyFromName(object.getClass(), propertyName, true), StringEncoder.getDefaultInstance());

	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>propertyName</code> for object <code>object</code> asserting
	 * that corresponding keyValueProperty type is a java primitive (<code>int</code>,
	 * <code>long</code>, <code>short</code>, <code>double</code>,
	 * <code>float</code>, <code>boolean</code>, <code>byte</code>,
	 * <code>char</code>) or a <code>Date</code> or a <code>String</code>
	 * object. No assertion is done on specific type and value is parsed from
	 * <code>textValue</code> according to corresponding type.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param textValue
	 *            a <code>String</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setValueForKey(Object object, String textValue, String propertyName, StringEncoder stringEncoder) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{
		setValueForKey(object, textValue, (SingleKeyValueProperty) getKeyValuePropertyFromName(object.getClass(), propertyName, true), stringEncoder);
	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty</code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a
	 * <code>boolean</code>. Value is parsed from string
	 * <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param value
	 *            a <code>boolean</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setBooleanValueForKey(Object object, boolean value, SingleKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		try {
			keyValueProperty.setBooleanValue(value, object);
		} catch (AccessorInvocationException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
					+ keyValueProperty.getName() + " Exception raised: " + e.toString());
		}
	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty</code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a
	 * <code>boolean</code>. Value is parsed from string
	 * <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param textValue
	 *            a <code>String</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setBooleanAsStringForKey(Object object, String textValue, SingleKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		if (textValue == null) {
			throw new InvalidXMLDataException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
					+ " supplied value is null");
		} else {
			setBooleanValueForKey(object, StringEncoder.decodeAsBoolean(textValue), keyValueProperty);
		}
	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>propertyName</code> for object <code>object</code> asserting
	 * that corresponding keyValueProperty type is a <code>boolean</code>.
	 * Value is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param value
	 *            a <code>char</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setBooleanValueForKey(Object object, boolean value, String propertyName) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		setBooleanValueForKey(object, value, (SingleKeyValueProperty) getKeyValuePropertyFromName(object.getClass(), propertyName, true));

	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty</code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a <code>char</code>.
	 * Value is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param value
	 *            a <code>char</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	public static void setCharacterValueForKey(Object object, char value, SingleKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		try {
			keyValueProperty.setCharValue(value, object);
		} catch (AccessorInvocationException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
					+ keyValueProperty.getName() + " Exception raised: " + e.toString());
		}
	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty</code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a <code>char</code>.
	 * Value is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param textValue
	 *            a <code>String</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	public static void setCharacterAsStringForKey(Object object, String textValue, SingleKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		if (textValue == null) {
			throw new InvalidXMLDataException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
					+ " supplied value is null");
		} else {
			setCharacterValueForKey(object, StringEncoder.decodeAsCharacter(textValue), keyValueProperty);
		}
	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>propertyName</code> for object <code>object</code> asserting
	 * that corresponding keyValueProperty type is a <code>char</code>. Value
	 * is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param value
	 *            a <code>char</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setCharacterValueForKey(Object object, char value, String propertyName) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		setCharacterValueForKey(object, value, (SingleKeyValueProperty) getKeyValuePropertyFromName(object.getClass(), propertyName, true));

	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty</code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a <code>byte</code>.
	 * Value is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param value
	 *            a <code>byte</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	public static void setByteValueForKey(Object object, byte value, SingleKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		try {
			keyValueProperty.setByteValue(value, object);
		} catch (NumberFormatException e) {
			throw new InvalidXMLDataException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
					+ " supplied value is not parsable as a byte.");
		} catch (AccessorInvocationException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
					+ keyValueProperty.getName() + " Exception raised: " + e.toString());
		}
	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty</code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a <code>byte</code>.
	 * Value is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param textValue
	 *            a <code>String</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	public static void setByteAsStringForKey(Object object, String textValue, SingleKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		if (textValue == null) {
			throw new InvalidXMLDataException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
					+ " supplied value is null");
		} else {
			setByteValueForKey(object, StringEncoder.decodeAsByte(textValue), keyValueProperty);
		}
	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>propertyName</code> for object <code>object</code> asserting
	 * that corresponding keyValueProperty type is a <code>byte</code>. Value
	 * is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param value
	 *            a <code>byte</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setByteValueForKey(Object object, byte value, String propertyName) throws InvalidXMLDataException, InvalidObjectSpecificationException,
	AccessorInvocationException
	{

		setByteValueForKey(object, value, (SingleKeyValueProperty) getKeyValuePropertyFromName(object.getClass(), propertyName, true));

	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty</code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a
	 * <code>short</code>. Value is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param value
	 *            a <code>short</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	public static void setShortValueForKey(Object object, short value, SingleKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		try {
			keyValueProperty.setShortValue(value, object);
		} catch (NumberFormatException e) {
			throw new InvalidXMLDataException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
					+ " supplied value is not parsable as a short.");
		} catch (AccessorInvocationException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
					+ keyValueProperty.getName() + " Exception raised: " + e.toString());
		}
	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty</code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a
	 * <code>short</code>. Value is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param textValue
	 *            a <code>String</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	public static void setShortAsStringForKey(Object object, String textValue, SingleKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		if (textValue == null) {
			throw new InvalidXMLDataException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
					+ " supplied value is null");
		} else {
			setShortValueForKey(object, StringEncoder.decodeAsShort(textValue), keyValueProperty);
		}
	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>propertyName</code> for object <code>object</code> asserting
	 * that corresponding keyValueProperty type is a <code>short</code>.
	 * Value is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param short
	 *            a <code>short</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setShortValueForKey(Object object, short value, String propertyName) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		setShortValueForKey(object, value, (SingleKeyValueProperty) getKeyValuePropertyFromName(object.getClass(), propertyName, true));

	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty</code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a <code>int</code>.
	 * Value is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param value
	 *            a <code>int</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	public static void setIntegerValueForKey(Object object, int value, SingleKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		try {
			keyValueProperty.setIntValue(value, object);
		} catch (NumberFormatException e) {
			throw new InvalidXMLDataException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
					+ " supplied value is not parsable as an int.");
		} catch (AccessorInvocationException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
					+ keyValueProperty.getName() + " Exception raised: " + e.toString());
		}
	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty</code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a <code>int</code>.
	 * Value is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param textValue
	 *            a <code>String</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	public static void setIntegerAsStringForKey(Object object, String textValue, SingleKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		if (textValue == null) {
			throw new InvalidXMLDataException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
					+ " supplied value is null");
		} else {
			setIntegerValueForKey(object, StringEncoder.decodeAsInteger(textValue), keyValueProperty);
		}
	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>propertyName</code> for object <code>object</code> asserting
	 * that corresponding keyValueProperty type is a <code>int</code>. Value
	 * is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param value
	 *            a <code>int</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setIntegerValueForKey(Object object, int value, String propertyName) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		setIntegerValueForKey(object, value, (SingleKeyValueProperty) getKeyValuePropertyFromName(object.getClass(), propertyName, true));

	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty</code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a <code>long</code>.
	 * Value is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param value
	 *            a <code>long</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	public static void setLongValueForKey(Object object, long value, SingleKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		try {
			keyValueProperty.setLongValue(value, object);
		} catch (NumberFormatException e) {
			throw new InvalidXMLDataException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
					+ " supplied value is not parsable as a long.");
		} catch (AccessorInvocationException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
					+ keyValueProperty.getName() + " Exception raised: " + e.toString());
		}
	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty</code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a <code>long</code>.
	 * Value is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param textValue
	 *            a <code>String</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	public static void setLongAsStringForKey(Object object, String textValue, SingleKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		if (textValue == null) {
			throw new InvalidXMLDataException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
					+ " supplied value is null");
		} else {
			setLongValueForKey(object, StringEncoder.decodeAsLong(textValue), keyValueProperty);
		}
	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>propertyName</code> for object <code>object</code> asserting
	 * that corresponding keyValueProperty type is a <code>long</code>. Value
	 * is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param value
	 *            a <code>long</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setLongValueForKey(Object object, long value, String propertyName) throws InvalidXMLDataException, InvalidObjectSpecificationException,
	AccessorInvocationException
	{

		setLongValueForKey(object, value, (SingleKeyValueProperty) getKeyValuePropertyFromName(object.getClass(), propertyName, true));

	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty</code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a
	 * <code>float</code>. Value is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param value
	 *            a <code>float</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	public static void setFloatValueForKey(Object object, float value, SingleKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		try {
			keyValueProperty.setFloatValue(value, object);
		} catch (NumberFormatException e) {
			throw new InvalidXMLDataException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
					+ " supplied value is not parsable as a float.");
		} catch (AccessorInvocationException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
					+ keyValueProperty.getName() + " Exception raised: " + e.toString());
		}
	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty</code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a
	 * <code>float</code>. Value is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param textValue
	 *            a <code>String</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	public static void setFloatAsStringForKey(Object object, String textValue, SingleKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		if (textValue == null) {
			throw new InvalidXMLDataException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
					+ " supplied value is null");
		} else {
			setFloatValueForKey(object, StringEncoder.decodeAsFloat(textValue), keyValueProperty);
		}
	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>propertyName</code> for object <code>object</code> asserting
	 * that corresponding keyValueProperty type is a <code>float</code>.
	 * Value is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param value
	 *            a <code>float</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setFloatValueForKey(Object object, float value, String propertyName) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		setFloatValueForKey(object, value, (SingleKeyValueProperty) getKeyValuePropertyFromName(object.getClass(), propertyName, true));

	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty</code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a
	 * <code>double</code>. Value is parsed from string
	 * <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param value
	 *            a <code>double</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	public static void setDoubleValueForKey(Object object, double value, SingleKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		try {
			keyValueProperty.setDoubleValue(value, object);
		} catch (NumberFormatException e) {
			throw new InvalidXMLDataException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
					+ " supplied value is not parsable as a double.");
		} catch (AccessorInvocationException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
					+ keyValueProperty.getName() + " Exception raised: " + e.toString());
		}
	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>keyValueProperty</code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is a
	 * <code>double</code>. Value is parsed from string
	 * <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param textValue
	 *            a <code>String</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	public static void setDoubleAsStringForKey(Object object, String textValue, SingleKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		if (textValue == null) {
			throw new InvalidXMLDataException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
					+ " supplied value is null");
		} else {
			setDoubleValueForKey(object, StringEncoder.decodeAsDouble(textValue), keyValueProperty);
		}
	}

	/**
	 * Sets value <code>textValue</code> for keyValueProperty
	 * <code>propertyName</code> for object <code>object</code> asserting
	 * that corresponding keyValueProperty type is a <code>double</code>.
	 * Value is parsed from string <code>textValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param value
	 *            a <code>double</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setDoubleValueForKey(Object object, double value, String propertyName) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		setDoubleValueForKey(object, value, (SingleKeyValueProperty) getKeyValuePropertyFromName(object.getClass(), propertyName, true));

	}

	/**
	 * Sets value <code>objectValue</code> for keyValueProperty
	 * <code>keyValueProperty</code> for object <code>object</code>
	 * asserting that corresponding keyValueProperty type is compatible with
	 * <code>objectValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param objectValue
	 *            an <code>Object</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	public static void setObjectForKey(Object object, Object objectValue, KeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		// Debugging.debug ("setObjectForKey() called with
		// '"+objectValue.toString()
		// +"' for keyValueProperty '"+keyValueProperty.getName()
		// +"' for object of class "+object.getClass().getName());

		/*
		 * if (objectValue == null) { throw new InvalidXMLDataException ("Class
		 * "+keyValueProperty.getObjectClass().getName() +": keyValueProperty
		 * "+keyValueProperty.getName()+" supplied object value is null"); }
		 * 
		 * else {
		 */
		try {
			if (objectValue != null) {
				if (!objectValue.getClass().equals(keyValueProperty.getType())) {
					objectValue = castTo(objectValue,keyValueProperty.getType());
				}
			}
			keyValueProperty.setObjectValue(objectValue, object);
		} catch (AccessorInvocationException e) {
			throw e;
		} catch (Exception e) {
			// e.printStackTrace();
			//System.out.println("objectValue="+objectValue);
			throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
					+ keyValueProperty.getName() + " Exception raised: " + e.toString());
		}
		// }
	}

	private static Object castTo (Object object, Type desiredType)
	{
		if (object == null) {
			return null;
		}

		//System.out.println("Object type: "+object.getClass());
		//System.out.println("desiredType: "+desiredType);
		if (object.getClass().equals(desiredType)) {
			return object;
		}

		if (object instanceof Number) {
			if (KeyValueProperty.isByte(desiredType)) {
				return ((Number)object).byteValue();
			}
			if (KeyValueProperty.isShort(desiredType)) {
				return ((Number)object).shortValue();
			}
			if (KeyValueProperty.isInteger(desiredType)) {
				return ((Number)object).intValue();
			}
			if (KeyValueProperty.isLong(desiredType)) {
				return ((Number)object).longValue();
			}
			if (KeyValueProperty.isDouble(desiredType)) {
				return ((Number)object).doubleValue();
			}
			if (KeyValueProperty.isFloat(desiredType)) {
				return ((Number)object).floatValue();
			}
		}
		return object;
	}


	/**
	 * Sets value <code>objectValue</code> for keyValueProperty
	 * <code>propertyName</code> for object <code>object</code> asserting
	 * that corresponding keyValueProperty type is compatible with
	 * <code>objectValue</code>.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param objectValue
	 *            an <code>Object</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setObjectForKey(Object object, Object objectValue, String propertyName) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		setObjectForKey(object, objectValue, getKeyValuePropertyFromName(object.getClass(), propertyName, true));

	}

	/**
	 * Sets values contained in <code>Vector</code> <code>values</code> for
	 * keyValueProperty <code>keyValueProperty</code> for object
	 * <code>object</code> asserting that corresponding keyValueProperty is a
	 * vector-like property.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param values
	 *            a <code>Vector</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	public static void setVectorForKey(Object object, Vector values, VectorKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		// Debugging.debug ("setValuesListForKey() called with 'Vector'"
		// +values.toString()+" for keyValueProperty '"
		// +keyValueProperty.getName()+"' for object of class "
		// +object.getClass().getName());

		Vector vectorObject;

		vectorObject = keyValueProperty.newInstance();
		setObjectForKey(object, vectorObject, keyValueProperty);
		for (Enumeration e = values.elements(); e.hasMoreElements();) {
			Object obj = e.nextElement();
			// System.out.println ("Adding "+obj+obj.getClass().getName()+" for
			// property "+keyValueProperty.getName());
			keyValueProperty.addObjectValue(obj, object);
		}

	}

	/**
	 * Sets values contained in <code>Vector</code> <code>values</code> for
	 * keyValueProperty <code>keyValueProperty</code> for object
	 * <code>object</code> asserting that corresponding keyValueProperty is an
	 * array property.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param values
	 *            a <code>Vector</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setArrayForKey(Object object, Vector values, ArrayKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		// Debugging.debug ("setValuesListForKey() called with 'Vector'"
		// +values.toString()+" for keyValueProperty '"
		// +keyValueProperty.getName()+"' for object of class "
		// +object.getClass().getName());

		Object arrayObject;

		arrayObject = keyValueProperty.newInstance(values.size());
		setObjectForKey(object, arrayObject, keyValueProperty);
		int index = 0;
		for (Enumeration e = values.elements(); e.hasMoreElements();) {
			keyValueProperty.setObjectValueAtIndex(e.nextElement(), index, object);
			index++;
		}

	}

	/**
	 * Sets values contained in <code>Hashtable</code> <code>values</code>
	 * for keyValueProperty <code>keyValueProperty</code> for object
	 * <code>object</code> asserting that corresponding keyValueProperty is a
	 * hashtable-like property.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param values
	 *            a <code>Hashtable</code> value
	 * @param keyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	public static void setHashtableForKey(Object object, Hashtable values, HashtableKeyValueProperty keyValueProperty) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		// Debugging.debug ("setValuesListForKey() called with 'Hashtable'"
		// +values.toString()+" for keyValueProperty '"
		// +keyValueProperty.getName()+"' for object of class "
		// +object.getClass().getName());

		Hashtable hashtableObject;

		hashtableObject = keyValueProperty.newInstance();
		setObjectForKey(object, hashtableObject, keyValueProperty);
		for (Enumeration e = values.keys(); e.hasMoreElements();) {
			Object key = e.nextElement();
			keyValueProperty.setObjectValueForKey(values.get(key), key, object);
		}

	}

	/**
	 * Sets values contained in <code>Vector</code> <code>values</code> for
	 * keyValueProperty <code>propertyName</code> for object
	 * <code>object</code> asserting that corresponding keyValueProperty'type
	 * is a <code>Vector</code> class (or inherits from).
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param values
	 *            a <code>Vector</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setVectorForKey(Object object, Vector values, String propertyName) throws InvalidXMLDataException, InvalidObjectSpecificationException,
	AccessorInvocationException
	{

		setVectorForKey(object, values, (VectorKeyValueProperty) getKeyValuePropertyFromName(object.getClass(), propertyName, true));

	}

	/**
	 * Sets values contained in <code>Hashtable</code> <code>values</code>
	 * for keyValueProperty <code>propertyName</code> for object
	 * <code>object</code> asserting that corresponding keyValueProperty'type
	 * is a <code>Hashtable</code> class (or inherits from).
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @param values
	 *            a <code>Hashtable</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public static void setHashtableForKey(Object object, Hashtable values, String propertyName) throws InvalidXMLDataException,
	InvalidObjectSpecificationException, AccessorInvocationException
	{

		setHashtableForKey(object, values, (HashtableKeyValueProperty) getKeyValuePropertyFromName(object.getClass(), propertyName, true));

	}

}
