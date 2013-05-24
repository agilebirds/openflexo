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
package org.openflexo.letparser;

public class StringValue extends Value {

	private String _value;

	public static StringValue createStringValue(String value) {
		return new StringValue(value);
	}

	public StringValue(String value) {
		super();
		_value = value;
	}

	@Override
	public String getStringValue() {
		return _value;
	}

	@Override
	public String toString() {
		return getPrefix() + "String[" + _value + "]";
	}

	@Override
	public String getSerializationValue() {
		return "$\"" + _value + "\"";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StringValue) {
			return getStringValue().equals(((StringValue) obj).getStringValue());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getStringValue().hashCode();
	}
}
