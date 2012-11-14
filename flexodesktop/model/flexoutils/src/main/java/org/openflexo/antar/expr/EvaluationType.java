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
package org.openflexo.antar.expr;

import org.openflexo.localization.FlexoLocalization;

public enum EvaluationType {
	LITERAL, BOOLEAN, ARITHMETIC_INTEGER, ARITHMETIC_FLOAT, STRING, DATE, DURATION, ENUM;

	public String getName() {
		return toString();
	}

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	public boolean isLiteral() {
		return this == LITERAL;
	}

	public boolean isArithmetic() {
		return this == ARITHMETIC_FLOAT || this == ARITHMETIC_INTEGER;
	}

	public boolean isArithmeticInteger() {
		return this == ARITHMETIC_INTEGER;
	}

	public boolean isArithmeticFloat() {
		return this == ARITHMETIC_FLOAT;
	}

	public boolean isArithmeticOrLiteral() {
		return this == ARITHMETIC_FLOAT || this == ARITHMETIC_INTEGER || this == LITERAL;
	}

	public boolean isBoolean() {
		return this == BOOLEAN;
	}

	public boolean isBooleanOrLiteral() {
		return this == BOOLEAN || this == LITERAL;
	}

	public boolean isEnum() {
		return this == ENUM;
	}

	public boolean isEnumOrLiteral() {
		return this == ENUM || this == LITERAL;
	}

	public boolean isString() {
		return this == STRING;
	}

	public boolean isStringOrLiteral() {
		return this == STRING || this == LITERAL;
	}

	public boolean isDate() {
		return this == DATE;
	}

	public boolean isDateOrLiteral() {
		return this == DATE || this == LITERAL;
	}

	public boolean isDuration() {
		return this == DURATION;
	}

	public boolean isDurationOrLiteral() {
		return this == DURATION || this == LITERAL;
	}

}
