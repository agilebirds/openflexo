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

import java.util.Observable;

import org.openflexo.xmlcode.KeyValueCoder;
import org.openflexo.xmlcode.KeyValueDecoder;
import org.openflexo.xmlcode.StringEncoder;

/**
 * Default class implementing Key/Value coding.
 * 
 * This scheme allows introspection capabilities on such classes since they are handlable by basic accessors such as objectForKey(String),
 * setObjectForKey (Object,String) and integerValueForKey(String), setIntegerValueForKey(int,String) (defined for all primitives: boolean,
 * byte, char, double, float, integer, long, short). Key is a String identifying a key/value pair public methods such as getKey()/setKey()
 * or _getKey()/_setKey(). If no method is found, research on public field is done and performed.
 * 
 * Additionnaly, 2 methods valueForKey(String) and setValueForKey (Object,String) could be used as wrapers allowing to handle primitives and
 * simple classes (such as Date, String, File and URL) with string representations. NB: Those simple classes that are easily serializable
 * from/to string must declare how they are coded in StringEncoder class.
 * 
 * @author sguerin
 * 
 * @see org.openflexo.kvc.KeyValueCoding
 */
public class KVCObservableObject extends Observable implements KeyValueCoding {

	@Override
	public String valueForKey(String key) {
		return KeyValueDecoder.valueForKey(this, key, getStringEncoder());
	}

	@Override
	public void setValueForKey(String valueAsString, String key) {
		KeyValueCoder.setValueForKey(this, valueAsString, key, getStringEncoder());
	}

	@Override
	public boolean booleanValueForKey(String key) {
		return KeyValueDecoder.booleanValueForKey(this, key);
	}

	@Override
	public byte byteValueForKey(String key) {
		return KeyValueDecoder.byteValueForKey(this, key);
	}

	@Override
	public char characterForKey(String key) {
		return KeyValueDecoder.characterValueForKey(this, key);
	}

	@Override
	public double doubleValueForKey(String key) {
		return KeyValueDecoder.doubleValueForKey(this, key);
	}

	@Override
	public float floatValueForKey(String key) {
		return KeyValueDecoder.floatValueForKey(this, key);
	}

	@Override
	public int integerValueForKey(String key) {
		return KeyValueDecoder.integerValueForKey(this, key);
	}

	@Override
	public long longValueForKey(String key) {
		return KeyValueDecoder.longValueForKey(this, key);
	}

	@Override
	public short shortValueForKey(String key) {
		return KeyValueDecoder.shortValueForKey(this, key);
	}

	@Override
	public void setBooleanValueForKey(boolean value, String key) {
		KeyValueCoder.setBooleanValueForKey(this, value, key);
	}

	@Override
	public void setByteValueForKey(byte value, String key) {
		KeyValueCoder.setByteValueForKey(this, value, key);
	}

	@Override
	public void setCharacterForKey(char value, String key) {
		KeyValueCoder.setCharacterValueForKey(this, value, key);
	}

	@Override
	public void setDoubleValueForKey(double value, String key) {
		KeyValueCoder.setDoubleValueForKey(this, value, key);
	}

	@Override
	public void setFloatValueForKey(float value, String key) {
		KeyValueCoder.setFloatValueForKey(this, value, key);
	}

	@Override
	public void setIntegerValueForKey(int value, String key) {
		KeyValueCoder.setIntegerValueForKey(this, value, key);
	}

	@Override
	public void setLongValueForKey(long value, String key) {
		KeyValueCoder.setLongValueForKey(this, value, key);
	}

	@Override
	public void setShortValueForKey(short value, String key) {
		KeyValueCoder.setShortValueForKey(this, value, key);
	}

	@Override
	public Object objectForKey(String key) {
		return KeyValueDecoder.objectForKey(this, key);
	}

	@Override
	public void setObjectForKey(Object value, String key) {
		KeyValueCoder.setObjectForKey(this, value, key);
	}

	// Retrieving type

	@Override
	public Class getTypeForKey(String key) {
		return KeyValueDecoder.getTypeForKey(this, key);
	}

	@Override
	public boolean isSingleProperty(String key) {
		return KeyValueDecoder.isSingleProperty(this, key);
	}

	@Override
	public boolean isArrayProperty(String key) {
		return KeyValueDecoder.isArrayProperty(this, key);
	}

	@Override
	public boolean isVectorProperty(String key) {
		return KeyValueDecoder.isVectorProperty(this, key);
	}

	@Override
	public boolean isHashtableProperty(String key) {
		return KeyValueDecoder.isHashtableProperty(this, key);
	}

	public StringEncoder getStringEncoder() {
		return StringEncoder.getDefaultInstance();
	}

}
