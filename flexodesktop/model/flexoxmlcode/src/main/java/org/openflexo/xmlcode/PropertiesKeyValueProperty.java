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

/**
 * <p>
 * A PropertiesKeyValueProperty property represents a hashtable-like property, accessible directly by related field or related accessors:
 * this property matches a dynamic list of objects stored in a {@link java.util.Hashtable} or a sub-class of {@link java.util.Hashtable}
 * where key is a String obtained and parsed from/as XML element name: therefore, there is no need to implement model for contained data
 * </p>
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see KeyValueCoder
 * @see KeyValueDecoder
 * 
 */
public class PropertiesKeyValueProperty extends HashtableKeyValueProperty {

	private boolean safe = false;

	/**
	 * Creates a new <code>PropertiesKeyValueProperty</code> instance, given an object class.<br>
	 * To be usable, this property should be set with a correct object (according to object class)
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param propertyName
	 *            a <code>String</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 */
	public PropertiesKeyValueProperty(Class<?> anObjectClass, String propertyName, boolean setMethodIsMandatory)
			throws InvalidKeyValuePropertyException {

		super(anObjectClass, propertyName, setMethodIsMandatory);
		init(propertyName, setMethodIsMandatory);
	}

	/**
	 * Add Object value for considered object <code>anObject</code>, asserting that this property represents a Properties-like property (if
	 * not, throw an InvalidKeyValuePropertyException exception)
	 * 
	 * @param aValue
	 *            an <code>Object</code> value
	 * @param aKey
	 *            a <code>String</code> value
	 * @param anObject
	 *            an <code>Object</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 */
	public void setObjectValueForKey(Object aValue, String aKey, Object anObject) {
		super.setObjectValueForKey(aValue, aKey, anObject);
	}

	/**
	 * Remove Object value for considered object <code>anObject</code>, asserting that this property represents a Properties-like property
	 * (if not, throw an InvalidKeyValuePropertyException exception)
	 * 
	 * @param aKey
	 *            a <code>String</code> value
	 * @param anObject
	 *            an <code>Object</code> value
	 * @exception InvalidKeyValuePropertyException
	 *                if an error occurs
	 */
	public void removeWithKeyValue(String aKey, Object anObject) {
		super.removeWithKeyValue(aKey, anObject);
	}

	public boolean isSafe() {
		return safe;
	}

	public void setSafe(boolean safe) {
		this.safe = safe;
	}

	public static class UndecodableProperty {
		public String className;
		public String value;

		public UndecodableProperty(String className, String value) {
			this.className = className;
			this.value = value;
		}

		@Override
		public UndecodableProperty clone() {
			return new UndecodableProperty(this.className, this.value);
		}
	}
}
