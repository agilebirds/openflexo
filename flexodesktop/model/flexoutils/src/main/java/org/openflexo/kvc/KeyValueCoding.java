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
package org.openflexo.kvc;

/**
 * Classes implementing this interface are intented to implement Key/Value coding.
 * 
 * This scheme allows powerfull introspection capabilities on such classes since they are handlable by basic accessors such as
 * objectForKey(String), setObjectForKey (Object,String) and integerValueForKey(String), setIntegerValueForKey(int,String) (defined for all
 * primitives: boolean, byte, char, double, float, integer, long, short). Key is a String identifying a key/value pair public methods such
 * as getKey()/setKey() or _getKey()/_setKey(). If no method is found, research on public field is done and performed.
 * 
 * This scheme could also be seen as a dynamic redefinition of java dynamic liaison scheme.
 * 
 * Additionnaly, 2 methods valueForKey(String) and setValueForKey (Object,String) could be used as wrapers allowing to handle primitives and
 * simple classes (such as Date, String, File and URL) with string representations.
 * 
 * Package org.openflexo.kvc provides a default implementation of such class: org.openflexo.kvc.KVCObject
 * 
 * @see org.openflexo.kvc.KVCObject
 * 
 * @author sguerin
 * 
 */
public interface KeyValueCoding {

	// General String-oriented interface

	public String valueForKey(String key);

	public void setValueForKey(String valueAsString, String key);

	// Privitives interface

	public boolean booleanValueForKey(String key);

	public byte byteValueForKey(String key);

	public char characterForKey(String key);

	public double doubleValueForKey(String key);

	public float floatValueForKey(String key);

	public int integerValueForKey(String key);

	public long longValueForKey(String key);

	public short shortValueForKey(String key);

	public void setBooleanValueForKey(boolean value, String key);

	public void setByteValueForKey(byte value, String key);

	public void setCharacterForKey(char value, String key);

	public void setDoubleValueForKey(double value, String key);

	public void setFloatValueForKey(float value, String key);

	public void setIntegerValueForKey(int value, String key);

	public void setLongValueForKey(long value, String key);

	public void setShortValueForKey(short value, String key);

	// Objects interface

	public Object objectForKey(String key);

	public void setObjectForKey(Object value, String key);

	// Retrieving type

	public Class getTypeForKey(String key);

	public boolean isSingleProperty(String key);

	public boolean isArrayProperty(String key);

	public boolean isVectorProperty(String key);

	public boolean isHashtableProperty(String key);

}
