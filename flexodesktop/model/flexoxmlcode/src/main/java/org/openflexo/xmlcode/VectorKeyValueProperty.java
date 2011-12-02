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
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

/**
 * <p>
 * A KeyValue property represents a vector-like property, accessible directly by related field or related accessors.
 * </p>
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see KeyValueCoder
 * @see KeyValueDecoder
 * 
 */
public class VectorKeyValueProperty extends KeyValueProperty {

	/**
	 * Stores all 'addTo...' methods, in order of the specialization of their arguments, as {@link AccessorMethod} objects. (the first
	 * methods are the first we will try to invoke, that's why vector needs to be sorted).<br>
	 * NB: 'addTo...' methods are methods with general form: <code>addToXXX(Class anObject)</code> or <code>_addToXXX(Class anObject)</code>
	 * , where XXX is the property name (try with or without a terminal 's' character), and Class could be anything...
	 */
	protected TreeSet<AccessorMethod> addToMethods;

	/**
	 * Stores all 'removeFrom...' methods, in order of the specialization of their arguments, as {@link AccessorMethod} objects. (the first
	 * methods are the first we will try to invoke, that's why vector needs to be sorted).<br>
	 * NB: 'removeFrom...' methods are methods with general form: <code>removeFromXXX(Class anObject)</code> or
	 * <code>_removeFromXXX(Class anObject)</code>, where XXX is the property name (try with or without a terminal 's' character), and Class
	 * could be anything...
	 */
	protected TreeSet<AccessorMethod> removeFromMethods;

	/**
	 * Creates a new <code>VectorKeyValueProperty</code> instance, given an object class.<br>
	 * To be usable, this property should be set with a correct object (according to object class)
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 */
	public VectorKeyValueProperty(Class<?> anObjectClass, String propertyName, boolean setMethodIsMandatory)
			throws InvalidKeyValuePropertyException {

		super(anObjectClass, propertyName);
		init(propertyName, setMethodIsMandatory);
	}

	/**
	 * Returns boolean indicating if related type inherits from Vector
	 */
	public boolean typeInheritsFromList() {
		return List.class.isAssignableFrom(getType());
	}

	public Type getAccessedType() {
		if (field != null) {
			return field.getGenericType();
		} else if (getGetMethod() != null) {
			return getGetMethod().getGenericReturnType();
		} else {
			return null;
		}
	}

	public Class<?> getContentType() {
		if (getAccessedType() instanceof ParameterizedType && ((ParameterizedType) getAccessedType()).getActualTypeArguments().length > 0) {
			Type returned = ((ParameterizedType) getAccessedType()).getActualTypeArguments()[0];
			if (returned instanceof Class) {
				return (Class<?>) returned;
			} else if (returned instanceof ParameterizedType && ((ParameterizedType) returned).getRawType() instanceof Class) {
				return (Class<?>) ((ParameterizedType) returned).getRawType();
			}
		}
		return Object.class;
	}

	/**
	 * Initialize this property, given a propertyName.<br>
	 * This method is called during constructor invokation. NB: to be valid, a property should be identified by at least the field or the
	 * accessors methods. If the field is accessible, and only some of accessors methods are accessible, a warning will be thrown.
	 */
	@Override
	protected void init(String propertyName, boolean setMethodIsMandatory) throws InvalidKeyValuePropertyException {

		super.init(propertyName, setMethodIsMandatory);

		if (!typeInheritsFromList()) {
			throw new InvalidKeyValuePropertyException("Property " + propertyName
					+ " found, but doesn't seem inherits from java.util.Vector");
		}

		// If related type is a sub-class of vector, check that there is a
		// a trivial constructor
		if (!getType().equals(Vector.class) && !getType().equals(ArrayList.class)) {
			if (!type.isAssignableFrom(Vector.class) || !type.isAssignableFrom(ArrayList.class)) {
				try {
					// Test instantiation
					type.newInstance();
				} catch (InstantiationException e) {
					throw new InvalidKeyValuePropertyException("Class " + type.getName()
							+ " cannot be instanciated directly (check that this class has a constructor with no arguments [public "
							+ type.getName() + "()] and that this class is not abstract.");
				} catch (Exception e) {
					throw new InvalidModelException("Unexpected error occurs during model initialization. Please send a bug report.");
				}
			}
		}

		if (field == null) {

			// Debugging.debug ("Trying to find vector-specific accessors
			// methods");

			addToMethods = searchMatchingAddToMethods(propertyName);
			if (addToMethods.size() == 0 && setMethodIsMandatory) {
				throw new InvalidKeyValuePropertyException("No public field " + propertyName
						+ " found, and no 'addTo' methods accessors found");
			}

			removeFromMethods = searchMatchingRemoveFromMethods(propertyName);
			if (removeFromMethods.size() == 0 && setMethodIsMandatory) {
				throw new InvalidKeyValuePropertyException("No public field " + propertyName
						+ " found, and no 'removeFrom' methods accessors found");
			}

		}

	}

	/**
	 * Search and return matching "addTo" methods<br>
	 * NB: 'addTo...' methods are methods with general form: <code>addToXXX(Class anObject)</code> or <code>_addToXXX(Class anObject)</code>
	 * , where XXX is the property name (try with or without a terminal 's' character), and Class could be anything... Returns an ordered
	 * TreeSet of {@link AccessorMethod} objects
	 */
	protected TreeSet<AccessorMethod> searchMatchingAddToMethods(String propertyName) {

		String singularPropertyName;
		String pluralPropertyName;

		if (propertyName.endsWith("s") || propertyName.endsWith("S")) {
			singularPropertyName = propertyName.substring(0, propertyName.length() - 1);
			pluralPropertyName = propertyName;
		} else {
			singularPropertyName = propertyName;
			pluralPropertyName = propertyName + "s";
		} // end of else

		String[] methodNameCondidates = new String[4];
		methodNameCondidates[0] = "addTo" + singularPropertyName;
		methodNameCondidates[1] = "_addTo" + singularPropertyName;
		methodNameCondidates[2] = "addTo" + pluralPropertyName;
		methodNameCondidates[3] = "_addTo" + pluralPropertyName;

		return searchMethodsWithNameAndParamsNumber(methodNameCondidates, 1);
	}

	/**
	 * Search and return matching "removeFrom" methods<br>
	 * NB: 'removeFrom...' methods are methods with general form: <code>removeFromXXX(Class anObject)</code> or
	 * <code>_removeFromXXX(Class anObject)</code>, where XXX is the property name (try with or without a terminal 's' character), and Class
	 * could be anything... Returns an ordered TreeSet of {@link AccessorMethod} objects
	 */
	protected TreeSet<AccessorMethod> searchMatchingRemoveFromMethods(String propertyName) {

		String singularPropertyName;
		String pluralPropertyName;

		if (propertyName.endsWith("s") || propertyName.endsWith("S")) {
			singularPropertyName = propertyName.substring(0, propertyName.length() - 1);
			pluralPropertyName = propertyName;
		} else {
			singularPropertyName = propertyName;
			pluralPropertyName = propertyName + "s";
		} // end of else

		String[] methodNameCondidates = new String[4];
		methodNameCondidates[0] = "removeFrom" + singularPropertyName;
		methodNameCondidates[1] = "_removeFrom" + singularPropertyName;
		methodNameCondidates[2] = "removeFrom" + pluralPropertyName;
		methodNameCondidates[3] = "_removeFrom" + pluralPropertyName;

		return searchMethodsWithNameAndParamsNumber(methodNameCondidates, 1);
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
	public synchronized void addObjectValue(Object aValue, Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException("No object is specified");
		} else {
			if (field != null) {
				try {
					List vector = (List<?>) field.get(object);
					vector.add(aValue);
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			} else {
				Object params[] = new Object[1];
				params[0] = aValue;
				for (Iterator<AccessorMethod> i = addToMethods.iterator(); i.hasNext();) {
					Method method = null;
					try {
						method = i.next().getMethod();
						// Debugging.debug ("Trying with "+method);
						method.invoke(object, params);
						return;
					} catch (InvocationTargetException e) {
						// e.getTargetException().printStackTrace();
						throw new AccessorInvocationException("Exception thrown while invoking: " + method, e);
					} catch (IllegalArgumentException e) {
						// Debugging.debug ("FAILED with
						// "+params[0].getClass().getName());
						// May be normal, we continue to find a good accessor
					} catch (Exception e) {
						// Debugging.debug ("Unexpected exception raised. Please
						// send a bug report.");
						e.printStackTrace();
					}
				}
				throw new InvalidKeyValuePropertyException("addObjectValue, class " + getObjectClass().getName()
						+ ": could not find a valid 'addTo' accessor method for property '" + name + "': object class was "
						+ aValue.getClass().getName() + " value= " + aValue);
			}
		}
	}

	/**
	 * Remove Object value for considered object <code>anObject</code>, asserting that this property represents a Vector-like property (if
	 * not, throw an InvalidKeyValuePropertyException exception)
	 * 
	 * @param aValue
	 *            an <code>Object</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 */
	public synchronized void removeObjectValue(Object aValue, Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException("No object is specified");
		} else {
			if (field != null) {
				try {
					List<?> vector = (List<?>) field.get(object);
					vector.remove(aValue);
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			} else {
				Object params[] = new Object[1];
				params[0] = aValue;
				for (Iterator<AccessorMethod> i = removeFromMethods.iterator(); i.hasNext();) {
					Method method = null;
					try {
						method = i.next().getMethod();
						method.invoke(object, params);
						return;
					} catch (InvocationTargetException e) {
						throw new AccessorInvocationException("Exception thrown while invoking: " + method, e);
					} catch (Exception e) {
						// try next one
					}
				}
				throw new InvalidKeyValuePropertyException("removeObjectValue, class " + getObjectClass().getName()
						+ ": could not find a valid 'removeFrom' accessor method: object class was " + aValue.getClass().getName());
			}
		}
	}

	/**
	 * Creates a new instance of this represented class, which MUST be a {@link Vector} or a subclass of {@link Vector}.
	 */
	public List<?> newInstance() throws InvalidObjectSpecificationException {
		try {
			if (type.isInterface()) {
				return new Vector();
			}
			return (List<?>) type.newInstance();
		} catch (Exception e) {
			throw new InvalidObjectSpecificationException("Could not instanciate a new " + type.getName() + ": reason " + e);
		}
	}

}
