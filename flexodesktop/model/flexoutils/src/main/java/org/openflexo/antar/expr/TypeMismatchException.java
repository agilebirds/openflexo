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

public class TypeMismatchException extends Exception {

	private Operator concernedOperator;
	private EvaluationType suppliedType;
	private EvaluationType leftSuppliedType;
	private EvaluationType rightSuppliedType;
	private EvaluationType[] expectedTypes;

	private String message;

	public TypeMismatchException(UnaryOperator operator, EvaluationType suppliedType, EvaluationType... expectedTypes) {
		super();
		concernedOperator = operator;
		this.suppliedType = suppliedType;
		this.expectedTypes = expectedTypes;
		message = "TypeMismatchException on operator " + operator.getName() + " : supplied type is " + suppliedType
				+ " while expected type(s) is(are) " + typesAsString(expectedTypes);
	}

	public TypeMismatchException(BinaryOperator operator, EvaluationType leftSuppliedType, EvaluationType rightSuppliedType,
			EvaluationType... expectedTypes) {
		super();
		concernedOperator = operator;
		this.leftSuppliedType = leftSuppliedType;
		this.rightSuppliedType = rightSuppliedType;
		this.expectedTypes = expectedTypes;
		message = "TypeMismatchException on operator " + operator.getName() + " : supplied types are " + leftSuppliedType + " and "
				+ rightSuppliedType + " while expected type(s) is(are) " + typesAsString(expectedTypes);
	}

	private TypeMismatchException() {
		super();
	}

	public static TypeMismatchException buildIncompatibleEvaluationTypeException(EvaluationType type1, EvaluationType type2) {
		TypeMismatchException returned = new TypeMismatchException();
		returned.leftSuppliedType = type1;
		returned.rightSuppliedType = type2;
		returned.message = "Incompatible types: " + type1 + " and " + type2;
		return returned;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getLocalizedMessage() {
		if (concernedOperator instanceof BinaryOperator) {
			return FlexoLocalization.localizedForKeyWithParams(
					"type_mismatch_on_operator_($0)_supplied_types_are_($1)_and_($2)_while_expected_types_are_($3)",
					concernedOperator.getLocalizedName(), leftSuppliedType.getLocalizedName(), rightSuppliedType.getLocalizedName(),
					typesAsString(expectedTypes));
		} else {
			return FlexoLocalization.localizedForKeyWithParams(
					"type_mismatch_on_operator_($0)_supplied_type_is_($1)_while_expected_types_are_($2)",
					concernedOperator.getLocalizedName(), suppliedType.getLocalizedName(), typesAsString(expectedTypes));
		}
	}

	public String getHTMLLocalizedMessage() {
		if (concernedOperator instanceof BinaryOperator) {
			return FlexoLocalization.localizedForKeyWithParams(
					"<html>type_mismatch_on_operator_($0)<br>supplied_types_are_($1)_and_($2)<br>while_expected_types_are_($3)</html>",
					concernedOperator.getLocalizedName(), leftSuppliedType.getLocalizedName(), rightSuppliedType.getLocalizedName(),
					typesAsString(expectedTypes));
		} else {
			return FlexoLocalization.localizedForKeyWithParams(
					"<html>type_mismatch_on_operator_($0)<br>supplied_type_is_($1)<br>while_expected_types_are_($2)</html>",
					concernedOperator.getLocalizedName(), suppliedType.getLocalizedName(), typesAsString(expectedTypes));
		}
	}

	private String typesAsString(EvaluationType... types) {
		StringBuffer sb = new StringBuffer();
		boolean isFirst = true;
		for (EvaluationType t : types) {
			sb.append((isFirst ? t.getLocalizedName() : "," + t.getLocalizedName()));
			isFirst = false;
		}
		return sb.toString();
	}
}
