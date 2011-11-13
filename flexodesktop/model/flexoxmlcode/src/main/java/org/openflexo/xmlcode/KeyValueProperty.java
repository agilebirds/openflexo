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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

/**
 * <p>
 * A KeyValue property represents a property, accessible directly by related field or through accessors methods (get/set pair for single
 * properties (see {@link SingleKeyValueProperty}), addTo../removeFrom.. pair for vector-like properties (see {@link VectorKeyValueProperty}
 * ), and set...ForKey/remove...WithKey for hashtable-like properties {@link HashtableKeyValueProperty}.
 * </p>
 * <p>
 * NB: to be valid, a KeyValueProperty object should be identified by at least the field or accessors methods.
 * </p>
 * <p>
 * <b>Important note:</b>If related field exist (is public) and accessors methods exists also, the operations are processed using accessors
 * (field won't be used directly).
 * </p>
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see KeyValueCoder
 * @see KeyValueDecoder
 * 
 */
public abstract class KeyValueProperty {

	/** Stores date format */
	public static final String dateFormat = "yyyy.MM.dd G 'at' HH:mm:ss a zzz";

	/** Stores property's name */
	protected String name;

	/** Stores related object (could be null) */
	// protected Object object;

	/** Stores related object'class */
	protected Class objectClass;

	/**
	 * Stores related field (if this one is public) or null if field is protected or non-existant
	 */
	protected Field field;

	/** Stores related type (the class of related property) */
	protected Class type;

	/**
	 * Stores related "get" method (if this one is public) or null if method is protected or non-existant
	 */
	protected Method getMethod;

	/**
	 * Stores related "set" method (if this one is public) or null if method is protected or non-existant
	 */
	protected Method setMethod;

	/**
	 * Creates a new <code>KeyValueProperty</code> instance, given an object class.<br>
	 * To be usable, this property should be set with a correct object (according to object class)
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 */
	public KeyValueProperty(Class anObjectClass, String propertyName) throws InvalidKeyValuePropertyException {

		super();
		objectClass = anObjectClass;
	}

	protected Vector<KeyValueProperty> compoundKeyValueProperties;

	protected boolean isCompound;

	/**
	 * Initialize this property, given a propertyName.<br>
	 * This method is called during constructor invokation. NB: to be valid, a property should be identified by at least the field or the
	 * get/set methods pair. If the field is accessible, and only the get or the set method is accessible, a warning will be thrown.
	 */
	protected void init(String propertyName, boolean setMethodIsMandatory) throws InvalidKeyValuePropertyException {
		String lastName;
		Class lastClass = null;

		name = propertyName;

		if (name.lastIndexOf(".") > -1) {
			isCompound = true;
			// System.out.println ("Register compound key-value property
			// "+propertyName);
			PathTokenizer st = new PathTokenizer(name);
			String nextKey = st.nextToken();
			Class nextClass = objectClass;
			compoundKeyValueProperties = new Vector<KeyValueProperty>();
			while (st.hasMoreTokens()) {
				SingleKeyValueProperty skvp;
				if (ParameteredKeyValueProperty.isParameteredKeyValuePropertyPattern(nextKey)) {
					skvp = new ParameteredKeyValueProperty(nextClass, nextKey, false);
				} else {
					skvp = new SingleKeyValueProperty(nextClass, nextKey, false);
				}
				compoundKeyValueProperties.add(skvp);
				// System.out.println ("Register compound "+nextKey+" with
				// "+nextClass);
				nextKey = st.nextToken();
				nextClass = skvp.getType();
			}
			lastName = nextKey;
			lastClass = nextClass;
		} else {
			isCompound = false;
			lastName = propertyName;
			lastClass = objectClass;
		}

		String propertyNameWithFirstCharToUpperCase = lastName.substring(0, 1).toUpperCase() + lastName.substring(1, lastName.length());

		field = null;

		try {
			field = lastClass.getField(lastName);
		} catch (NoSuchFieldException e) {
			// Debugging.debug ("NoSuchFieldException, trying to find get/set
			// methods pair");
		} catch (SecurityException e) {
			// Debugging.debug ("SecurityException, trying to find get/set
			// methods pair");
		}

		getMethod = searchMatchingGetMethod(lastClass, lastName);

		if (field == null) {
			if (getMethod == null) {
				throw new InvalidKeyValuePropertyException("No public field " + lastName + " found, nor method matching " + lastName
						+ "() nor " + "_" + lastName + "() nor " + "get" + propertyNameWithFirstCharToUpperCase + "() nor " + "_get"
						+ propertyNameWithFirstCharToUpperCase + "() found in class:" + lastClass.getName());
			} else {
				type = getMethod.getReturnType();
			}
		} else { // field != null
			type = field.getType();
			if (getMethod != null) {
				if (getMethod.getReturnType() != type) {
					Debugging.warn("Class " + objectClass + " Public field " + lastName + " found, with type " + type.getName() + " found "
							+ " and method " + getMethod.getName() + " found " + " declaring return type " + getMethod.getReturnType()
							+ " Ignoring method...");
					getMethod = null;
				}
			}
		}

		setMethod = searchMatchingSetMethod(lastClass, lastName, type);

		if (setMethodIsMandatory) {
			if (setMethod == null) {
				if (field == null) {
					throw new InvalidKeyValuePropertyException("No public field " + lastName + " found, nor method matching " + "set"
							+ propertyNameWithFirstCharToUpperCase + "(" + type.getName() + ") or " + "_set"
							+ propertyNameWithFirstCharToUpperCase + "(" + type.getName() + ") found " + "in class " + lastClass);
				} else {
					if (getMethod != null) {
						// Debugging.debug ("Public field "+propertyName+"
						// found, with type "
						// + type.getName()+ " found "
						// + " and method "+getMethod.getName()+" found "
						// + " but no method matching "
						// +"set"+propertyNameWithFirstCharToUpperCase+"("+type.getName()+")
						// or "
						// +"_set"+propertyNameWithFirstCharToUpperCase+"("+type.getName()+")
						// found."
						// +" Will use directly the field to set values.");
					}
				}
			}
		}

		if ((getMethod != null) && (setMethod != null)) {
			// If related field exist (is public) and accessors methods exists
			// also,
			// the operations are processed using accessors (field won't be used
			// directly,
			// and should be set to null).
			field = null;
		}

	}

	/**
	 * Try to find a matching "get" method, such as (in order):
	 * <ul>
	 * <li>propertyName()</li>
	 * <li>_propertyName()</li>
	 * <li>getPropertyName()</li>
	 * <li>_getPropertyName()</li>
	 * </ul>
	 * Returns corresponding method, null if no such method exist
	 */
	protected Method searchMatchingGetMethod(Class lastClass, String propertyName) {

		Method returnedMethod;
		String propertyNameWithFirstCharToUpperCase = propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1, propertyName.length());

		String[] tries = new String[4];

		tries[0] = "get" + propertyNameWithFirstCharToUpperCase;
		tries[1] = propertyName;
		tries[2] = "_" + propertyName;
		tries[3] = "_get" + propertyNameWithFirstCharToUpperCase;

		for (int i = 0; i < 4; i++) {
			try {
				return lastClass.getMethod(tries[i], null);
			} catch (SecurityException err) {
				// we continue
			} catch (NoSuchMethodException err) {
				// we continue
			}
		}

		// Debugging.debug ("No method matching "
		// +propertyName+"() or "
		// +"_"+propertyName+"() or "
		// +"get"+propertyNameWithFirstCharToUpperCase+"() or "
		// +"_get"+propertyNameWithFirstCharToUpperCase+"() found.");

		return null;

	}

	/**
	 * Try to find a matching "set" method, such as (in order):
	 * <ul>
	 * <li>setPropertyName(Type)</li>
	 * <li>_setPropertyName(Type)</li>
	 * </ul>
	 * Returns corresponding method, null if no such method exist
	 */
	protected Method searchMatchingSetMethod(Class lastClass, String propertyName, Class aType) {

		Method returnedMethod;
		String propertyNameWithFirstCharToUpperCase = propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1, propertyName.length());

		String[] tries = new String[2];

		Class params[] = new Class[1];
		params[0] = aType;

		tries[0] = "set" + propertyNameWithFirstCharToUpperCase;
		tries[1] = "_set" + propertyNameWithFirstCharToUpperCase;

		for (int i = 0; i < 2; i++) {
			try {
				return lastClass.getMethod(tries[i], params);
			} catch (SecurityException err) {
				// we continue
			} catch (NoSuchMethodException err) {
				// we continue
			}
		}

		// Debugging.debug ("No method matching "
		// +"set"+propertyNameWithFirstCharToUpperCase+"("+type.getName()+") or
		// "
		// +"_set"+propertyNameWithFirstCharToUpperCase+"("+type.getName()+")
		// found.");

		if (aType.getSuperclass() != null) {
			// Try with a super class
			return searchMatchingSetMethod(lastClass, propertyName, aType.getSuperclass());
		}

		return null;

	}

	/**
	 * Stores related "get" method (if this one is public) or null if method is protected/private or non-existant
	 */
	public Method getGetMethod() {

		return getMethod;
	}

	/**
	 * Stores related "set" method (if this one is public) or null if method is protected/private or non-existant
	 */
	public Method getSetMethod() {

		return setMethod;
	}

	/**
	 * Returns name of this property
	 */
	public String getName() {

		return name;
	}

	/**
	 * Sets related object, asserting that the type is correct according to property type. Don't forget to encapsulate your calls within a
	 * synchronized block/method on this KeyValueProperty object.
	 */
	public void setObject(Object anObject) {

		if (objectClass.isAssignableFrom(anObject.getClass())) {
		} else {
			throw new InvalidKeyValuePropertyException("Invalid object type, expected: " + objectClass.getName());
		}
	}

	/**
	 * Returns related object class (never null)
	 */
	public Class getObjectClass() {

		return objectClass;
	}

	/**
	 * Returns related field (if this one is public) or null if field is protected or non-existant
	 */
	public Field getField() {

		return field;
	}

	/**
	 * Returns related type
	 */
	public Class getType() {
		return type;
	}

	/**
	 * Search and returns all methods (as {@link AccessorMethod} objects) of related class whose names is in the specified string list, with
	 * exactly the specified number of parameters, ascendant ordered regarding parameters specialization.
	 * 
	 * @see AccessorMethod
	 */
	protected TreeSet searchMethodsWithNameAndParamsNumber(String[] searchedNames, int paramNumber) {

		TreeSet returnedTreeSet = new TreeSet();
		Method[] allMethods = objectClass.getMethods();

		for (int i = 0; i < allMethods.length; i++) {
			Method tempMethod = allMethods[i];
			for (int j = 0; j < searchedNames.length; j++) {
				if ((tempMethod.getName().equalsIgnoreCase(searchedNames[j])) && (tempMethod.getParameterTypes().length == paramNumber)) {
					// This is a good candidate
					returnedTreeSet.add(new AccessorMethod(this, tempMethod));
				}
			}
		}
		// Debugging.debug ("Class "+objectClass.getName()+": found "
		// +returnedTreeSet.size()+" accessors:");
		// for (Iterator i = returnedTreeSet.iterator(); i.hasNext();) {
		// Debugging.debug ("> "+((AccessorMethod)i.next()).getMethod());
		// }
		return returnedTreeSet;
	}

	/**
	 * Returns Object value, asserting that this property represents an Object property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @return an <code>Object</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 */
	public synchronized Object getObjectValue(Object object) {
		return getObjectValue(object, object);
	}

	/**
	 * Returns Object value, asserting that this property represents an Object property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @return an <code>Object</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 */
	protected synchronized Object getObjectValue(Object object, Object initialObject) {
		if (object == null) {
			throw new InvalidKeyValuePropertyException("No object is specified");
		} else {
			Object currentObject = object;
			if (isCompound) {
				for (KeyValueProperty p : compoundKeyValueProperties) {
					if (currentObject != null) {
						currentObject = p.getObjectValue(currentObject, initialObject);
					}
				}
				if (currentObject == null) {
					return null;
				}
			}

			if (field != null) {
				try {
					return field.get(currentObject);
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else if (getMethod != null) {

				try {
					return getMethod.invoke(currentObject, null);
				} catch (InvocationTargetException e) {
					e.getTargetException().printStackTrace();
					throw new AccessorInvocationException("AccessorInvocationException: class " + getObjectClass().getName() + ": method "
							+ getMethod.getName() + " Exception raised: " + e.getTargetException().toString(), e);
				} catch (Exception e) {

					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": method " + getMethod.getName() + " Exception raised: " + e.toString());
				}

			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor get method found !!!");
			}

		}
	}

	/**
	 * Sets Object value, asserting that this property represents an Object property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @param aValue
	 *            an <code>Object</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 */

	/**
	 * Sets Object value, asserting that this property represents an Object property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @param aValue
	 *            an <code>Object</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 */
	public synchronized void setObjectValue(Object aValue, Object object) {
		setObjectValue(aValue, object, object);
	}

	/**
	 * Sets Object value, asserting that this property represents an Object property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @param aValue
	 *            an <code>Object</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 */

	/**
	 * Sets Object value, asserting that this property represents an Object property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @param aValue
	 *            an <code>Object</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 */
	public synchronized void setObjectValue(Object aValue, Object object, Object initialObject) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException("No object is specified");
		} else {

			Object currentObject = object;
			if (isCompound) {
				for (KeyValueProperty p : compoundKeyValueProperties) {
					if (currentObject != null) {
						currentObject = p.getObjectValue(currentObject, initialObject);
					}
				}
				if (currentObject == null) {
					return;
				}
			}

			if (field != null) {
				try {
					field.set(currentObject, aValue);
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else if (setMethod != null) {

				Object params[] = new Object[1];
				params[0] = aValue;

				try {
					setMethod.invoke(currentObject, params);
				} catch (InvocationTargetException e) {
					// e.getTargetException().printStackTrace();
					throw new AccessorInvocationException("AccessorInvocationException: class " + getObjectClass().getName() + ": method "
							+ setMethod.getName() + " Exception raised: " + e.getTargetException().toString(), e);
				} catch (IllegalArgumentException e) {
					// e.printStackTrace();
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": method " + setMethod.getName() + "Argument mismatch: tried to pass a '" + aValue.getClass().getName()
							+ " instead of a " + setMethod.getParameterTypes()[0] + " Exception raised: " + e.toString());

				} catch (Exception e) {
					// e.printStackTrace();
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + setMethod.getName() + " Exception raised: " + e.toString());
				}

			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor set method found !!!");
			}

		}
	}

	public static class PathTokenizer {
		private Vector<String> _tokens;

		private Enumeration<String> enumeration;

		public PathTokenizer(String value) {
			super();
			_tokens = new Vector<String>();
			StringTokenizer st = new StringTokenizer(value, ".()", true);
			String current = "";
			int level = 0;
			while (st.hasMoreElements()) {
				String next = st.nextToken();
				if ((next.equals(".")) && (current.trim().length() > 0) && (level == 0)) {
					_tokens.add(current);
					current = "";
				} else if (next.equals("(")) {
					current += next;
					level++;
				} else if (next.equals(")")) {
					current += next;
					level--;
				} else {
					current += next;
				}
			}
			if ((current.trim().length() > 0) && (level == 0)) {
				_tokens.add(current);
				current = "";
			}
			enumeration = _tokens.elements();
		}

		public boolean hasMoreTokens() {
			return enumeration.hasMoreElements();
		}

		public String nextToken() {
			String returned = enumeration.nextElement();
			return returned;
		}
	}

	public static boolean isBoolean(Type type) {
		return type.equals(Boolean.class) || type.equals(Boolean.TYPE);
	}

	public static boolean isByte(Type type) {
		return type.equals(Byte.class) || type.equals(Byte.TYPE);
	}

	public static boolean isChar(Type type) {
		return type.equals(Character.class) || type.equals(Character.TYPE);
	}

	public static boolean isClassAncestorOf(Class<?> parentClass, Class<?> childClass) {
		return parentClass.isAssignableFrom(childClass);
	}

	public static boolean isDouble(Type type) {
		return type.equals(Double.class) || type.equals(Double.TYPE);
	}

	public static boolean isFloat(Type type) {
		return type.equals(Float.class) || type.equals(Float.TYPE);
	}

	public static boolean isInteger(Type type) {
		return type.equals(Integer.class) || type.equals(Integer.TYPE);
	}

	public static boolean isLong(Type type) {
		return type.equals(Long.class) || type.equals(Long.TYPE);
	}

	public static boolean isObject(Type type) {
		return type.equals(Object.class);
	}

	public static boolean isShort(Type type) {
		return type.equals(Short.class) || type.equals(Short.TYPE);
	}

	public static boolean isString(Type type) {
		return type.equals(String.class);
	}

	public static boolean isVoid(Type type) {
		return type.equals(Void.class) || type.equals(Void.TYPE);
	}

}
