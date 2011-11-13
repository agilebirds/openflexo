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
package org.openflexo.antar.expr.parser;

public class IntValue extends Value {

	private long _value;

	public IntValue(long value) {
		super();
		_value = value;
	}

	public long getIntValue() {
		return _value;
	}

	@Override
	public String getStringValue() {
		return "" + _value;
	}

	@Override
	public String toString() {
		return getPrefix() + "Int[" + _value + "]";
	}

	public String getSerializationValue() {
		return "$" + getStringValue();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntValue) {
			return (getIntValue() == ((IntValue) obj).getIntValue());
		}
		return super.equals(obj);
	}

}
