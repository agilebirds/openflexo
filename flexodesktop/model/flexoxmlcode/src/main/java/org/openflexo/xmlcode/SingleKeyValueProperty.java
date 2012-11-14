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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Date;

/**
 * <p>
 * A KeyValue property represents a single-like property, accessible directly by related field or related accessors.
 * </p>
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see KeyValueCoder
 * @see KeyValueDecoder
 * 
 */
public class SingleKeyValueProperty extends KeyValueProperty {

	private static final String NO_OBJECT_IS_SPECIFIED = "No object is specified";

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
	public SingleKeyValueProperty(Class anObjectClass, String propertyName, boolean setMethodIsMandatory)
			throws InvalidKeyValuePropertyException {

		super(anObjectClass, propertyName);
		init(propertyName, setMethodIsMandatory);
	}

	/**
	 * Initialize this property, given a propertyName.<br>
	 * This method is called during constructor invokation.
	 */
	@Override
	protected void init(String propertyName, boolean setMethodIsMandatory) throws InvalidKeyValuePropertyException {

		super.init(propertyName, setMethodIsMandatory);
	}

	/**
	 * Returns boolean indicating if primitive (a primitive or a directely string convertable object)
	 * 
	 * @deprecated use the one with the string encoder instance
	 */
	@Deprecated
	public boolean classIsPrimitive() {
		return classIsPrimitive(StringEncoder.getDefaultInstance());
	}

	/**
	 * Returns boolean indicating if primitive (a primitive or a directely string convertable object)
	 */
	public boolean classIsPrimitive(StringEncoder encoder) {
		return getType().isPrimitive() || encoder._isEncodable(getType());
	}

	/**
	 * Returns int value, asserting that this property represents an int property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @return an <code>int</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public int getIntValue(Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (getMethod != null) {

				Integer returnedValue;
				try {
					returnedValue = (Integer) getMethod.invoke(object, null);
					return returnedValue.intValue();
				} catch (InvocationTargetException e) {
					throw new AccessorInvocationException("Exception thrown while invoking: " + getMethod, e);
				} catch (ClassCastException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not an int");
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not an int");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {

				try {
					return field.getInt(object);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not an int");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor get method found !!!");
			}

		}
	}

	/**
	 * Returns long value, asserting that this property represents an long property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @return an <code>long</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * 
	 */
	public long getLongValue(Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (getMethod != null) {

				Long returnedValue;
				try {
					returnedValue = (Long) getMethod.invoke(object, null);
					return returnedValue.longValue();
				} catch (InvocationTargetException e) {
					throw new AccessorInvocationException("Exception thrown while invoking: " + getMethod, e);
				} catch (ClassCastException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a long");
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a long");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {

				try {
					return field.getLong(object);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a long");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor get method found !!!");
			}

		}
	}

	/**
	 * Returns short value, asserting that this property represents an long property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @return an <code>short</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * 
	 */
	public short getShortValue(Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (getMethod != null) {

				Short returnedValue;
				try {
					returnedValue = (Short) getMethod.invoke(object, null);
					return returnedValue.shortValue();
				} catch (InvocationTargetException e) {
					throw new AccessorInvocationException("Exception thrown while invoking: " + getMethod, e);
				} catch (ClassCastException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a short");
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a short");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {

				try {
					return field.getShort(object);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a short");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor get method found !!!");
			}

		}
	}

	/**
	 * Returns char value, asserting that this property represents an char property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @return an <code>char</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * 
	 */
	public char getCharValue(Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (getMethod != null) {

				Character returnedValue;
				try {
					returnedValue = (Character) getMethod.invoke(object, null);
					return returnedValue.charValue();
				} catch (InvocationTargetException e) {
					throw new AccessorInvocationException("Exception thrown while invoking: " + getMethod, e);
				} catch (ClassCastException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a char");
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a char");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {

				try {
					return field.getChar(object);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a char");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor get method found !!!");
			}

		}
	}

	/**
	 * Returns boolean value, asserting that this property represents an boolean property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @return an <code>boolean</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * 
	 */
	public boolean getBooleanValue(Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (getMethod != null) {

				Boolean returnedValue;
				try {
					returnedValue = (Boolean) getMethod.invoke(object, null);
					return returnedValue.booleanValue();
				} catch (InvocationTargetException e) {
					throw new AccessorInvocationException("Exception thrown while invoking: " + getMethod, e);
				} catch (ClassCastException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a boolean");
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a boolean");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {

				try {
					return field.getBoolean(object);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a boolean");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor get method found !!!");
			}

		}
	}

	/**
	 * Returns byte value, asserting that this property represents an byte property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @return an <code>byte</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * 
	 */
	public byte getByteValue(Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (getMethod != null) {

				Byte returnedValue;
				try {
					returnedValue = (Byte) getMethod.invoke(object, null);
					return returnedValue.byteValue();
				} catch (InvocationTargetException e) {
					throw new AccessorInvocationException("Exception thrown while invoking: " + getMethod, e);
				} catch (ClassCastException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a byte");
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a byte");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {

				try {
					return field.getByte(object);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a byte");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor get method found !!!");
			}

		}
	}

	/**
	 * Returns float value, asserting that this property represents a float property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @return an <code>float</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * 
	 */
	public float getFloatValue(Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (getMethod != null) {

				Float returnedValue;
				try {
					returnedValue = (Float) getMethod.invoke(object, null);
					return returnedValue.floatValue();
				} catch (InvocationTargetException e) {
					throw new AccessorInvocationException("Exception thrown while invoking: " + getMethod, e);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a float");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {
				try {
					return field.getFloat(object);
				} catch (ClassCastException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a float");
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a float");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor get method found !!!");
			}

		}
	}

	/**
	 * Returns double value, asserting that this property represents an double property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @return an <code>double</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * 
	 */
	public double getDoubleValue(Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (getMethod != null) {

				Double returnedValue;
				try {
					returnedValue = (Double) getMethod.invoke(object, null);
					return returnedValue.doubleValue();
				} catch (InvocationTargetException e) {
					throw new AccessorInvocationException("Exception thrown while invoking: " + getMethod, e);
				} catch (ClassCastException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a double");
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a double");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {

				try {
					return field.getDouble(object);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a double");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor get method found !!!");
			}

		}
	}

	/**
	 * Sets int value, asserting that this property represents an int property (if not, throw an InvalidKeyValuePropertyException exception)
	 * 
	 * @param aValue
	 *            an <code>int</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * 
	 */
	public void setIntValue(int aValue, Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (setMethod != null) {

				Integer setValue = new Integer(aValue);
				Object params[] = new Object[1];
				params[0] = setValue;

				try {
					setMethod.invoke(object, params);
				} catch (InvocationTargetException e) {
					e.getTargetException().printStackTrace();
					throw new AccessorInvocationException("Exception thrown while invoking: " + setMethod, e);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not an int");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {

				try {
					field.setInt(object, aValue);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not an int");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor set method found !!!");
			}

		}
	}

	/**
	 * Sets long value, asserting that this property represents an long property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @param aValue
	 *            a <code>long</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * 
	 */
	public void setLongValue(long aValue, Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (setMethod != null) {

				Long setValue = new Long(aValue);
				Object params[] = new Object[1];
				params[0] = setValue;

				try {
					setMethod.invoke(object, params);
				} catch (InvocationTargetException e) {
					e.getTargetException().printStackTrace();
					throw new AccessorInvocationException("Exception thrown while invoking: " + setMethod, e);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a long");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {

				try {
					field.setLong(object, aValue);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a long");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor set method found !!!");
			}

		}
	}

	/**
	 * Sets short value, asserting that this property represents an short property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @param aValue
	 *            an <code>short</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * 
	 */
	public void setShortValue(short aValue, Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (setMethod != null) {

				Short setValue = new Short(aValue);
				Object params[] = new Object[1];
				params[0] = setValue;

				try {
					setMethod.invoke(object, params);
				} catch (InvocationTargetException e) {
					e.getTargetException().printStackTrace();
					throw new AccessorInvocationException("Exception thrown while invoking: " + setMethod, e);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a short");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {

				try {
					field.setShort(object, aValue);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a short");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor set method found !!!");
			}

		}
	}

	/**
	 * Sets char value, asserting that this property represents an char property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @param aValue
	 *            a <code>char</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * 
	 */
	public void setCharValue(char aValue, Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (setMethod != null) {

				Character setValue = new Character(aValue);
				Object params[] = new Object[1];
				params[0] = setValue;

				try {
					setMethod.invoke(object, params);
				} catch (InvocationTargetException e) {
					e.getTargetException().printStackTrace();
					throw new AccessorInvocationException("Exception thrown while invoking: " + setMethod, e);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a char");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {

				try {
					field.setChar(object, aValue);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a char");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor set method found !!!");
			}

		}
	}

	/**
	 * Sets boolean value, asserting that this property represents an boolean property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @param aValue
	 *            a <code>boolean</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * 
	 */
	public void setBooleanValue(boolean aValue, Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (setMethod != null) {

				Boolean setValue = new Boolean(aValue);
				Object params[] = new Object[1];
				params[0] = setValue;

				try {
					setMethod.invoke(object, params);
				} catch (InvocationTargetException e) {
					e.getTargetException().printStackTrace();
					throw new AccessorInvocationException("Exception thrown while invoking: " + setMethod, e);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a boolean");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {
				try {
					field.setBoolean(object, aValue);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a boolean");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor set method found !!!");
			}

		}
	}

	/**
	 * Sets byte value, asserting that this property represents an byte property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @param aValue
	 *            a <code>byte</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * 
	 */
	public void setByteValue(byte aValue, Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (setMethod != null) {

				Byte setValue = new Byte(aValue);
				Object params[] = new Object[1];
				params[0] = setValue;

				try {
					setMethod.invoke(object, params);
				} catch (InvocationTargetException e) {
					e.getTargetException().printStackTrace();
					throw new AccessorInvocationException("Exception thrown while invoking: " + setMethod, e);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a byte");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {
				try {
					field.setByte(object, aValue);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a byte");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor set method found !!!");
			}

		}
	}

	/**
	 * Sets float value, asserting that this property represents a float property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @param aValue
	 *            a <code>float</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * 
	 */
	public void setFloatValue(float aValue, Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (setMethod != null) {

				Float setValue = new Float(aValue);
				Object params[] = new Object[1];
				params[0] = setValue;

				try {
					setMethod.invoke(object, params);
				} catch (InvocationTargetException e) {
					e.getTargetException().printStackTrace();
					throw new AccessorInvocationException("Exception thrown while invoking: " + setMethod, e);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a float");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {
				try {
					field.setFloat(object, aValue);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a float");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor set method found !!!");
			}

		}
	}

	/**
	 * Sets double value, asserting that this property represents an double property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @param aValue
	 *            a <code>double</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * 
	 */
	public void setDoubleValue(double aValue, Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (setMethod != null) {

				Double setValue = new Double(aValue);
				Object params[] = new Object[1];
				params[0] = setValue;

				try {
					setMethod.invoke(object, params);
				} catch (InvocationTargetException e) {
					e.getTargetException().printStackTrace();
					throw new AccessorInvocationException("Exception thrown while invoking: " + setMethod, e);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a double");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {

				try {
					field.setDouble(object, aValue);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a double");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor set method found !!!");
			}

		}
	}

	/**
	 * Sets String value, asserting that this property represents a String property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @param aValue
	 *            a <code>String</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public void setStringValue(String aValue, Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (setMethod != null) {

				Object params[] = new Object[1];
				params[0] = aValue;

				try {
					setMethod.invoke(object, params);
				} catch (InvocationTargetException e) {
					e.getTargetException().printStackTrace();
					throw new AccessorInvocationException("Exception thrown while invoking: " + setMethod, e);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a String");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {

				try {
					field.set(object, aValue);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a String");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor set method found !!!");
			}

		}
	}

	/**
	 * Sets Date value, asserting that this property represents an Date property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @param aValue
	 *            a <code>Date</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public void setDateValue(Date aValue, Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (setMethod != null) {

				Object params[] = new Object[1];
				params[0] = aValue;

				try {
					setMethod.invoke(object, params);
				} catch (InvocationTargetException e) {
					e.getTargetException().printStackTrace();
					throw new AccessorInvocationException("Exception thrown while invoking: " + setMethod, e);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a Date");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {

				try {
					field.set(object, aValue);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a Date");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor set method found !!!");
			}

		}
	}

	/**
	 * Sets File value, asserting that this property represents an File property (if not, throw an InvalidKeyValuePropertyException
	 * exception)
	 * 
	 * @param aValue
	 *            a <code>File</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public void setFileValue(File aValue, Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (setMethod != null) {

				Object params[] = new Object[1];
				params[0] = aValue;

				try {
					setMethod.invoke(object, params);
				} catch (InvocationTargetException e) {
					e.getTargetException().printStackTrace();
					throw new AccessorInvocationException("Exception thrown while invoking: " + setMethod, e);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a File");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {

				try {
					field.set(object, aValue);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a File");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor set method found !!!");
			}

		}
	}

	/**
	 * Sets URL value, asserting that this property represents an URL property (if not, throw an InvalidKeyValuePropertyException exception)
	 * 
	 * @param aValue
	 *            a <code>URL</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	public void setURLValue(URL aValue, Object object) {

		if (object == null) {
			throw new InvalidKeyValuePropertyException(NO_OBJECT_IS_SPECIFIED);
		} else {

			if (setMethod != null) {

				Object params[] = new Object[1];
				params[0] = aValue;

				try {
					setMethod.invoke(object, params);
				} catch (InvocationTargetException e) {
					e.getTargetException().printStackTrace();
					throw new AccessorInvocationException("Exception thrown while invoking: " + setMethod, e);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a URL");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}

			}

			else if (field != null) {

				try {
					field.set(object, aValue);
				} catch (IllegalArgumentException e) {
					throw new InvalidKeyValuePropertyException("Argument type mismatch: " + getName() + " is not a URL");
				} catch (Exception e) {
					throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
							+ ": field " + field.getName() + " Exception raised: " + e.toString());
				}
			}

			else {
				throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no field nor set method found !!!");
			}

		}
	}

	/**
	 * Return a string representation of this object (debug purposes)
	 */
	@Override
	public String toString() {
		return "Field: " + field + "\nGetMethod: " + getMethod + "\nSetMethod: " + setMethod + "\nType: " + type;

	}

}
