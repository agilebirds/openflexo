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

import java.util.Date;

import org.openflexo.toolbox.Duration;

public abstract class Value extends Token {

	private boolean prefixedBy$ = false;

	public static Value createConstantValue(Object value) {
		if (value == null) {
			return null;
		}
		Class valueClass = value.getClass();
		if (valueClass.equals(Boolean.class) || valueClass.equals(Boolean.TYPE)) {
			return new BooleanValue((Boolean) value);
		}
		if (valueClass.equals(Character.class) || valueClass.equals(Character.TYPE)) {
			return new CharValue((Character) value);
		}
		if (valueClass.equals(Date.class)) {
			return new DateValue((Date) value);
		}
		if (valueClass.equals(Duration.class)) {
			return new DurationValue((Duration) value);
		}
		if (valueClass.equals(String.class)) {
			return new StringValue((String) value);
		}
		if (valueClass.equals(Float.class) || valueClass.equals(Float.TYPE)) {
			return new FloatValue((Float) value);
		}
		if (valueClass.equals(Double.class) || valueClass.equals(Double.TYPE)) {
			return new FloatValue((Double) value);
		}
		if (valueClass.equals(Byte.class) || valueClass.equals(Byte.TYPE)) {
			return new IntValue((Byte) value);
		}
		if (valueClass.equals(Short.class) || valueClass.equals(Short.TYPE)) {
			return new IntValue((Short) value);
		}
		if (valueClass.equals(Integer.class) || valueClass.equals(Integer.TYPE)) {
			return new IntValue((Integer) value);
		}
		if (valueClass.equals(Long.class) || valueClass.equals(Long.TYPE)) {
			return new IntValue((Long) value);
		}
		if (valueClass.isEnum()) {
			return new EnumValue((Enum) value);
		}
		return new StringValue(value.toString());
	}

	public static Value createValue(double value) {
		if (value - (int) value < 0.0001) {
			return new IntValue((int) value);
		} else {
			return new FloatValue(value);
		}
	}

	public abstract String getStringValue();

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Value) {
			return getStringValue().equals(((Value) obj).getStringValue());
		}
		return super.equals(obj);
	}

	public boolean isPrefixedBy$() {
		return prefixedBy$;
	}

	public void setPrefixedBy$(boolean prefBy$) {
		this.prefixedBy$ = prefBy$;
	}

	public String getPrefix() {
		return (prefixedBy$ ? "$" : "");
	}
}
