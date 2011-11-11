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

import java.lang.reflect.Array;

/**
 * <p>
 * A KeyValue property represents a array property, accessible directly by related field or related accessors.
 * </p>
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see KeyValueCoder
 * @see KeyValueDecoder
 * 
 */
public class ArrayKeyValueProperty extends KeyValueProperty {

	/**
	 * Creates a new <code>ArrayKeyValueProperty</code> instance, given an object class.<br>
	 * To be usable, this property should be set with a correct object (according to object class)
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 */
	public ArrayKeyValueProperty(Class anObjectClass, String propertyName, boolean setMethodIsMandatory)
			throws InvalidKeyValuePropertyException {

		super(anObjectClass, propertyName);
		init(propertyName, setMethodIsMandatory);
	}

	/**
	 * Returns the Class representing the component type of the array.
	 */
	public Class getComponentType() {

		return getType().getComponentType();
	}

	/**
	 * Initialize this property, given a propertyName.<br>
	 * This method is called during constructor invokation. NB: to be valid, a property should be identified by at least the field or the
	 * accessors methods. If the field is accessible, and only some of accessors methods are accessible, a warning will be thrown.
	 */
	@Override
	protected void init(String propertyName, boolean setMethodIsMandatory) throws InvalidKeyValuePropertyException {

		super.init(propertyName, setMethodIsMandatory);

		if (!getType().isArray()) {
			throw new InvalidKeyValuePropertyException("Property " + propertyName + " found, but is not an array");
		}

		if (getComponentType().isPrimitive()) {
			throw new InvalidKeyValuePropertyException("Property " + propertyName
					+ " found, but arrays of Java primitives are not yet implemented.");
		}
	}

	/**
	 * Add Object value for considered object <code>anObject</code>, asserting that this property represents an array property (if not,
	 * throw an InvalidKeyValuePropertyException exception)
	 * 
	 * @param aValue
	 *            an <code>Object</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 */
	public synchronized void setObjectValueAtIndex(Object aValue, int index, Object object) {

		Object arrayObject = getObjectValue(object);

		try {
			Array.set(arrayObject, index, aValue);
		} catch (IllegalArgumentException e) {
			throw new InvalidKeyValuePropertyException("Argument type mismatch: " + aValue.getClass().getName() + " is not a "
					+ getComponentType().getName());
		} catch (Exception e) {
			throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
					+ " Exception raised: " + e.toString());
		}
	}

	/**
	 * Add Object value for considered object <code>anObject</code>, asserting that this property represents a Vector-like property (if not,
	 * throw an InvalidKeyValuePropertyException exception)
	 * 
	 * @param aValue
	 *            an <code>Object</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 */
	public synchronized Object getObjectValueAtIndex(int index, Object object) {

		Object arrayObject = getObjectValue(object);

		try {
			return Array.get(arrayObject, index);
		} catch (IllegalArgumentException e) {
			throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " does not represent an array of objects");
		} catch (Exception e) {
			throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
					+ " Exception raised: " + e.toString());
		}
	}

	/**
	 * Creates a new instance of this represented class with specified size
	 */
	public Object newInstance(int arraySize) throws InvalidObjectSpecificationException {

		try {
			return Array.newInstance(getComponentType(), arraySize);
		} catch (Exception e) {
			throw new InvalidObjectSpecificationException("Could not instanciate a new " + type.getName() + ": reason " + e);
		}
	}

}
