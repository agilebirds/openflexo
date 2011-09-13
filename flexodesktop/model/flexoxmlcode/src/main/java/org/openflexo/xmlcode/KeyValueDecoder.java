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

import java.util.Hashtable;
import java.util.Vector;

/**
 * <p>
 * <code>KeyValueDecoder</code> is an utility class that allow to manipulate
 * (get methods) properties of object (field or get/set methods pair) from
 * values represented as String (very usefull for xml coding/decoding)
 * </p>
 * Those operations are done using the {@link java.lang.reflect} package and all
 * developments done in {@link KeyValueProperty} class.<br>
 * This class is used in the context of XML coding using a mapping model
 * allowing to instanciate directly object from XML strings or streams (and
 * reverse operation).<br>
 * Manipulated types are all the java primitives (<code>int</code>,
 * <code>long</code>, <code>short</code>, <code>double</code>,
 * <code>float</code>, <code>boolean</code>, <code>byte</code>,
 * <code>char</code>) or a {@link java.util.Date}, a
 * {@link java.lang.String} a {@link java.io.File} or a {@link java.net.URL})<br>
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see XMLCoder
 * @see XMLDecoder
 * @see XMLMapping
 * @see KeyValueCoder
 * 
 */
public class KeyValueDecoder
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
    protected static KeyValueProperty getKeyValuePropertyFromName(Object object, String propertyName) throws InvalidObjectSpecificationException
    {
        return KeyValueCoder.getKeyValuePropertyFromName(object.getClass(), propertyName, false);
    }

    /**
     * <p>
     * Returns value of keyValueProperty <code>keyValueProperty</code>
     * represented as String, asserting that corresponding keyValueProperty type
     * is a java primitive (<code>int</code>, <code>long</code>,
     * <code>short</code>, <code>double</code>, <code>float</code>,
     * <code>boolean</code>, <code>byte</code>, <code>char</code>) or a
     * {@link java.util.Date}, a {@link java.lang.String} a
     * {@link java.io.File} or a {@link java.net.URL})
     * </p>
     * No assertion is done on specific type and value is returned according to
     * corresponding type.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if keyValueProperty type is not a java primitive nor a
     *                <code>Date</code> nor a <code>String</code>
     */
    public static String valueForKey(Object object, SingleKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException
    {
        return valueForKey(object, keyValueProperty, StringEncoder.getDefaultInstance());
    }

    /**
     * <p>
     * Returns value of keyValueProperty <code>keyValueProperty</code>
     * represented as String, asserting that corresponding keyValueProperty type
     * is a java primitive (<code>int</code>, <code>long</code>,
     * <code>short</code>, <code>double</code>, <code>float</code>,
     * <code>boolean</code>, <code>byte</code>, <code>char</code>) or a
     * {@link java.util.Date}, a {@link java.lang.String} a
     * {@link java.io.File} or a {@link java.net.URL} or an object
     * that can be decoded by <code>stringEncoder</code>
     * </p>
     * No assertion is done on specific type and value is returned according to
     * corresponding type.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @param stringEncoder
     *            a <code>StringEncoder</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if keyValueProperty type is not a java primitive nor a
     *                <code>Date</code> nor a <code>String</code>
     */
    public static String valueForKey(Object object, SingleKeyValueProperty keyValueProperty, StringEncoder stringEncoder) throws InvalidObjectSpecificationException
    {

        // Debugging.debug ("valueForKey() called for keyValueProperty
        // '"+keyValueProperty.getName()+"' for object of class
        // "+object.getClass().getName());

        if (keyValueProperty.getType().isPrimitive()) {
            // Class of keyValueProperty is a primitive such as int, long,
            // short, double, float, boolean, byte, char
            if (keyValueProperty.getType() == Boolean.TYPE) {
                return booleanAsStringForKey(object, keyValueProperty);
            } else if (keyValueProperty.getType() == Character.TYPE) {
                return characterAsStringForKey(object, keyValueProperty);
            } else if (keyValueProperty.getType() == Byte.TYPE) {
                return byteAsStringForKey(object, keyValueProperty);
            } else if (keyValueProperty.getType() == Short.TYPE) {
                return shortAsStringForKey(object, keyValueProperty);
            } else if (keyValueProperty.getType() == Integer.TYPE) {
                return integerAsStringForKey(object, keyValueProperty);
            } else if (keyValueProperty.getType() == Long.TYPE) {
                return longAsStringForKey(object, keyValueProperty);
            } else if (keyValueProperty.getType() == Float.TYPE) {
                return floatAsStringForKey(object, keyValueProperty);
            } else if (keyValueProperty.getType() == Double.TYPE) {
                return doubleAsStringForKey(object, keyValueProperty);
            }
        } else {
            return stringEncoder._encodeObject(objectForKey(object, keyValueProperty));
        }
        return null;
    }

    /**
     * <p>
     * Returns value of keyValueProperty <code>propertyName</code> for object
     * <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a java primitive (<code>int</code>,
     * <code>long</code>, <code>short</code>, <code>double</code>,
     * <code>float</code>, <code>boolean</code>, <code>byte</code>,
     * <code>char</code>) or a <code>Date</code> or a <code>String</code>
     * object.
     * </p>
     * No assertion is done on specific type and value is returned according to
     * corresponding type.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param propertyName
     *            a <code>String</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if keyValueProperty type is not a java primitive nor a
     *                <code>Date</code> nor a <code>String</code>
     */
    public static String valueForKey(Object object, String propertyName) throws InvalidObjectSpecificationException
    {

        return valueForKey(object, (SingleKeyValueProperty) getKeyValuePropertyFromName(object, propertyName), StringEncoder.getDefaultInstance());

    }

    /**
     * <p>
     * Returns value of keyValueProperty <code>propertyName</code> for object
     * <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a java primitive (<code>int</code>,
     * <code>long</code>, <code>short</code>, <code>double</code>,
     * <code>float</code>, <code>boolean</code>, <code>byte</code>,
     * <code>char</code>) or a <code>Date</code> or a <code>String</code>
     * object or an object that can be decoded by <code>stringEncoder</code>.
     * </p>
     * No assertion is done on specific type and value is returned according to
     * corresponding type.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param propertyName
     *            a <code>String</code> value
     * @param stringEncoder
     *            a <code>StringEncoder</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if keyValueProperty type is not a java primitive nor a
     *                <code>Date</code> nor a <code>String</code>
     */
    public static String valueForKey(Object object, String propertyName, StringEncoder stringEncoder) throws InvalidObjectSpecificationException
    {

        return valueForKey(object, (SingleKeyValueProperty) getKeyValuePropertyFromName(object, propertyName), stringEncoder);

    }
    
    /**
     * Returns value of keyValueProperty <code>keyValueProperty</code> for
     * object <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>boolean</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>boolean</code>)
     */
    public static boolean booleanValueForKey(Object object, SingleKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException,
            AccessorInvocationException
    {

        try {
            return keyValueProperty.getBooleanValue(object);
        } catch (AccessorInvocationException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
                    + keyValueProperty.getName() + " Exception raised: " + e.toString());
        }
    }

    /**
     * Returns value of keyValueProperty <code>keyValueProperty</code> for
     * object <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>boolean</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>boolean</code>)
     */
    public static String booleanAsStringForKey(Object object, SingleKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException,
            AccessorInvocationException
    {
        return StringEncoder.encodeBoolean(booleanValueForKey(object, keyValueProperty));
    }

    /**
     * Returns value of keyValueProperty <code>propertyName</code> for object
     * <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>boolean</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param propertyName
     *            a <code>String</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>boolean</code>)
     */
    public static boolean booleanValueForKey(Object object, String propertyName) throws InvalidObjectSpecificationException
    {

        return booleanValueForKey(object, (SingleKeyValueProperty) getKeyValuePropertyFromName(object, propertyName));

    }

    /**
     * Returns value of keyValueProperty <code>keyValueProperty</code> for
     * object <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>character</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>char</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>character</code>)
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static char characterValueForKey(Object object, SingleKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException,
            AccessorInvocationException
    {

        try {
            return keyValueProperty.getCharValue(object);
        } catch (AccessorInvocationException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
                    + keyValueProperty.getName() + " Exception raised: " + e.toString());
        }
    }

    /**
     * Returns value of keyValueProperty <code>keyValueProperty</code> for
     * object <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>character</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>character</code>)
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static String characterAsStringForKey(Object object, SingleKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException,
            AccessorInvocationException
    {
        return StringEncoder.encodeCharacter(characterValueForKey(object, keyValueProperty));
    }

    /**
     * Returns value of keyValueProperty <code>propertyName</code> for object
     * <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>character</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param propertyName
     *            a <code>String</code> value
     * @return a <code>char</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>character</code>)
     */
    public static char characterValueForKey(Object object, String propertyName) throws InvalidObjectSpecificationException
    {

        return characterValueForKey(object, (SingleKeyValueProperty) getKeyValuePropertyFromName(object, propertyName));

    }

    /**
     * Returns value of keyValueProperty <code>keyValueProperty</code> for
     * object <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>byte</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>byte</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>byte</code>)
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static byte byteValueForKey(Object object, SingleKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException,
            AccessorInvocationException
    {

        try {
            return keyValueProperty.getByteValue(object);
        } catch (AccessorInvocationException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
                    + keyValueProperty.getName() + " Exception raised: " + e.toString());
        }
    }

    /**
     * Returns value of keyValueProperty <code>keyValueProperty</code> for
     * object <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>byte</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>byte</code>)
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static String byteAsStringForKey(Object object, SingleKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException,
            AccessorInvocationException
    {
        return StringEncoder.encodeByte(byteValueForKey(object, keyValueProperty));
    }

    /**
     * Returns value of keyValueProperty <code>propertyName</code> for object
     * <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>byte</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param propertyName
     *            a <code>String</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>byte</code>)
     */
    public static byte byteValueForKey(Object object, String propertyName) throws InvalidObjectSpecificationException
    {

        return byteValueForKey(object, (SingleKeyValueProperty) getKeyValuePropertyFromName(object, propertyName));

    }

    /**
     * Returns value of keyValueProperty <code>keyValueProperty</code> for
     * object <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>short</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>short</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>short</code>)
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static short shortValueForKey(Object object, SingleKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException,
            AccessorInvocationException
    {

        try {
            return keyValueProperty.getShortValue(object);
        } catch (AccessorInvocationException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
                    + keyValueProperty.getName() + " Exception raised: " + e.toString());
        }
    }

    /**
     * Returns value of keyValueProperty <code>keyValueProperty</code> for
     * object <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>short</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>short</code>)
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static String shortAsStringForKey(Object object, SingleKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException,
            AccessorInvocationException
    {
        return StringEncoder.encodeShort(keyValueProperty.getShortValue(object));
    }

    /**
     * Returns value of keyValueProperty <code>propertyName</code> for object
     * <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>short</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param propertyName
     *            a <code>String</code> value
     * @return a <code>short</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>short</code>)
     */
    public static short shortValueForKey(Object object, String propertyName) throws InvalidObjectSpecificationException
    {

        return shortValueForKey(object, (SingleKeyValueProperty) getKeyValuePropertyFromName(object, propertyName));

    }

    /**
     * Returns value of keyValueProperty <code>keyValueProperty</code> for
     * object <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>int</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>int</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>int</code>)
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static int integerValueForKey(Object object, SingleKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException,
            AccessorInvocationException
    {

        try {
            return keyValueProperty.getIntValue(object);
        } catch (AccessorInvocationException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
                    + keyValueProperty.getName() + " Exception raised: " + e.toString());
        }
    }

    /**
     * Returns value of keyValueProperty <code>keyValueProperty</code> for
     * object <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>int</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>int</code>)
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static String integerAsStringForKey(Object object, SingleKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException,
            AccessorInvocationException
    {
        return StringEncoder.encodeInteger(integerValueForKey(object, keyValueProperty));
    }

    /**
     * Returns value of keyValueProperty <code>propertyName</code> for object
     * <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>int</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param propertyName
     *            a <code>String</code> value
     * @return a <code>int</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>int</code>)
     */
    public static int integerValueForKey(Object object, String propertyName) throws InvalidObjectSpecificationException
    {

        return integerValueForKey(object, (SingleKeyValueProperty) getKeyValuePropertyFromName(object, propertyName));

    }

    /**
     * Returns value of keyValueProperty <code>keyValueProperty</code> for
     * object <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>long</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>long</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>long</code>)
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static long longValueForKey(Object object, SingleKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException,
            AccessorInvocationException
    {

        try {
            return keyValueProperty.getLongValue(object);
        } catch (AccessorInvocationException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
                    + keyValueProperty.getName() + " Exception raised: " + e.toString());
        }
    }

    /**
     * Returns value of keyValueProperty <code>keyValueProperty</code> for
     * object <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>long</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>long</code>)
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static String longAsStringForKey(Object object, SingleKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException,
            AccessorInvocationException
    {
        return StringEncoder.encodeLong(longValueForKey(object, keyValueProperty));
    }

    /**
     * Returns value of keyValueProperty <code>propertyName</code> for object
     * <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>long</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param propertyName
     *            a <code>String</code> value
     * @return a <code>long</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>long</code>)
     */
    public static long longValueForKey(Object object, String propertyName) throws InvalidObjectSpecificationException
    {

        return longValueForKey(object, (SingleKeyValueProperty) getKeyValuePropertyFromName(object, propertyName));

    }

    /**
     * Returns value of keyValueProperty <code>keyValueProperty</code> for
     * object <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>float</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>float</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>float</code>)
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static float floatValueForKey(Object object, SingleKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException,
            AccessorInvocationException
    {

        try {
            return keyValueProperty.getFloatValue(object);
        } catch (AccessorInvocationException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
                    + keyValueProperty.getName() + " Exception raised: " + e.toString());
        }
    }

    /**
     * Returns value of keyValueProperty <code>keyValueProperty</code> for
     * object <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>float</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>float</code>)
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static String floatAsStringForKey(Object object, SingleKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException,
            AccessorInvocationException
    {
        return StringEncoder.encodeFloat(floatValueForKey(object, keyValueProperty));
    }

    /**
     * Returns value of keyValueProperty <code>propertyName</code> for object
     * <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>float</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param propertyName
     *            a <code>String</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>float</code>)
     */
    public static float floatValueForKey(Object object, String propertyName) throws InvalidObjectSpecificationException
    {

        return floatValueForKey(object, (SingleKeyValueProperty) getKeyValuePropertyFromName(object, propertyName));

    }

    /**
     * Returns value of keyValueProperty <code>keyValueProperty</code> for
     * object <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>double</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>double</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>double</code>)
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static double doubleValueForKey(Object object, SingleKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException,
            AccessorInvocationException
    {

        try {
            return keyValueProperty.getDoubleValue(object);
        } catch (AccessorInvocationException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
                    + keyValueProperty.getName() + " Exception raised: " + e.toString());
        }
    }

    /**
     * Returns value of keyValueProperty <code>keyValueProperty</code> for
     * object <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>double</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>double</code>)
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static String doubleAsStringForKey(Object object, SingleKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException,
            AccessorInvocationException
    {
        return StringEncoder.encodeDouble(doubleValueForKey(object, keyValueProperty));
    }

    /**
     * Returns value of keyValueProperty <code>propertyName</code> for object
     * <code>object</code>, represented as String, asserting that
     * corresponding keyValueProperty type is a <code>double</code>.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param propertyName
     *            a <code>String</code> value
     * @return a <code>double</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a <code>double</code>)
     */
    public static double doubleValueForKey(Object object, String propertyName) throws InvalidObjectSpecificationException
    {

        return doubleValueForKey(object, (SingleKeyValueProperty) getKeyValuePropertyFromName(object, propertyName));

    }

    /**
     * Returns value of keyValueProperty <code>keyValueProperty</code> for
     * object <code>object</code>, asserting that corresponding
     * keyValueProperty type is a {@link java.lang.Object}.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a {@link java.lang.Object})
     * @exception AccessorInvocationException
     *                if an error occurs during accessor invocation
     */
    public static Object objectForKey(Object object, KeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException, AccessorInvocationException
    {

        try {
            return keyValueProperty.getObjectValue(object);
        } catch (AccessorInvocationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidObjectSpecificationException("Class " + keyValueProperty.getObjectClass().getName() + ": keyValueProperty "
                    + keyValueProperty.getName() + " Exception raised: " + e.toString());
        }
    }

    /**
     * Returns value of keyValueProperty <code>propertyName</code> for object
     * <code>object</code>, asserting that corresponding keyValueProperty
     * type is a {@link java.lang.Object}.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param propertyName
     *            a <code>String</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a {@link java.lang.Object})
     */
    public static Object objectForKey(Object object, String propertyName) throws InvalidObjectSpecificationException
    {
    	/*if (propertyName.equals("availableContainerValues")) {
    		System.out.println("propertyName="+propertyName);
    		System.out.println("Object="+object);
    		KeyValueProperty keyValueProperty = getKeyValuePropertyFromName(object, propertyName);
       		System.out.println("KV="+keyValueProperty);
       		Object returned = keyValueProperty.getObjectValue(object);
      		System.out.println("returned="+returned);
      		return returned;
    	}*/
        return objectForKey(object, getKeyValuePropertyFromName(object, propertyName));

    }

    /**
     * Returns type of keyValueProperty <code>propertyName</code> for object
     * <code>object</code>, asserting that corresponding keyValueProperty
     * type is a {@link java.lang.Object}.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param propertyName
     *            a <code>String</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a {@link java.lang.Object})
     */
    public static Class getTypeForKey(Object object, String propertyName) throws InvalidObjectSpecificationException
    {

        return getKeyValuePropertyFromName(object, propertyName).getType();

    }

    public static boolean isSingleProperty(Object object, String propertyName) throws InvalidObjectSpecificationException
    {
        return getKeyValuePropertyFromName(object, propertyName) instanceof SingleKeyValueProperty;
   }
    
    public static boolean isArrayProperty(Object object, String propertyName) throws InvalidObjectSpecificationException
    {
        return getKeyValuePropertyFromName(object, propertyName) instanceof ArrayKeyValueProperty;
   }
    
    public static boolean isVectorProperty(Object object, String propertyName) throws InvalidObjectSpecificationException
    {
        return getKeyValuePropertyFromName(object, propertyName) instanceof VectorKeyValueProperty;
   }
    
    public static boolean isHashtableProperty(Object object, String propertyName) throws InvalidObjectSpecificationException
    {
        return getKeyValuePropertyFromName(object, propertyName) instanceof HashtableKeyValueProperty;
   }
    
    public static boolean isPropertiesProperty(Object object, String propertyName) throws InvalidObjectSpecificationException
    {
        return getKeyValuePropertyFromName(object, propertyName) instanceof PropertiesKeyValueProperty;
   }
    
    /**
     * Returns list of values of keyValueProperty <code>keyValueProperty</code>
     * for object <code>object</code>, represented as a
     * {@link java.util.Vector} asserting that corresponding keyValueProperty
     * type is a vector-like property (matching a {@link java.util.Vector} or a
     * subclass).
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a {@link java.util.Vector} nor a subclass)
     */
    public static Vector vectorForKey(Object object, VectorKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException
    {

        try {
            return (Vector) objectForKey(object, keyValueProperty);
        } catch (ClassCastException e) {
            throw new InvalidObjectSpecificationException("Class " + object.getClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
                    + " class must inherits from java.lang.Vector.");
        }
    }

    /**
     * Returns array of values of keyValueProperty <code>keyValueProperty</code>
     * for object <code>object</code>, represented as a <code>Object[]</code>
     * asserting that corresponding keyValueProperty type is a array property.
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a {@link java.util.Vector} nor a subclass)
     */
    public static Object[] arrayForKey(Object object, ArrayKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException
    {

        try {
            return (Object[]) objectForKey(object, keyValueProperty);
        } catch (ClassCastException e) {
            throw new InvalidObjectSpecificationException("Class " + object.getClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
                    + " class must be an object array.");
        }
    }

    /**
     * Returns list of values of keyValueProperty <code>propertyName</code>
     * for object <code>object</code>, represented as a
     * {@link java.util.Vector} asserting that corresponding keyValueProperty
     * type is a vector-like property (matching a {@link java.util.Vector} or a
     * subclass).
     * 
     * @param object
     *            an <code>Object</code> value
     * @param propertyName
     *            a <code>String</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a {@link java.util.Vector} nor a subclass)
     */
    public static Vector vectorForKey(Object object, String propertyName) throws InvalidObjectSpecificationException
    {

        return vectorForKey(object, (VectorKeyValueProperty) getKeyValuePropertyFromName(object, propertyName));

    }

    /**
     * Returns list of values of keyValueProperty <code>keyValueProperty</code>
     * for object <code>object</code>, represented as a
     * {@link java.util.Hashtable} asserting that corresponding keyValueProperty
     * type is a hashtable-like property (matching a {@link java.util.Hashtable}
     * or a subclass).
     * 
     * @param object
     *            an <code>Object</code> value
     * @param keyValueProperty
     *            a <code>KeyValueProperty</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a {@link java.util.Hashtable} nor a subclass)
     */
    public static Hashtable hashtableForKey(Object object, HashtableKeyValueProperty keyValueProperty) throws InvalidObjectSpecificationException
    {

        try {
            return (Hashtable) objectForKey(object, keyValueProperty);
        } catch (ClassCastException e) {
            throw new InvalidObjectSpecificationException("Class " + object.getClass().getName() + ": keyValueProperty " + keyValueProperty.getName()
                    + " class must inherits from java.lang.Hashtable.");
        }
    }

    /**
     * Returns list of values of keyValueProperty <code>propertyName</code>
     * for object <code>object</code>, represented as a
     * {@link java.util.Hashtable} asserting that corresponding keyValueProperty
     * type is a hashtable-like property (matching a {@link java.util.Hashtable}
     * or a subclass).
     * 
     * @param object
     *            an <code>Object</code> value
     * @param propertyName
     *            a <code>String</code> value
     * @return a <code>String</code> value
     * @exception InvalidObjectSpecificationException
     *                if an error occurs (eg. if corresponding keyValueProperty
     *                type is not a {@link java.util.Hashtable} nor a subclass)
     */
    public static Hashtable hashtableForKey(Object object, String propertyName) throws InvalidObjectSpecificationException
    {

        return hashtableForKey(object, (HashtableKeyValueProperty) getKeyValuePropertyFromName(object, propertyName));

    }

}
