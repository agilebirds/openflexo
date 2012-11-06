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

/**
 * This exception is thrown when an operator is invoked with ambiguous semantic, in a null operand context
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public class NullReferenceException extends TransformException {

	private Operator concernedOperator;

	private String message;

	public NullReferenceException(Operator operator) {
		super();
		concernedOperator = operator;
		message = "NullReferenceException on operator " + operator.getName();
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getLocalizedMessage() {
		return FlexoLocalization.localizedForKeyWithParams("NullReferenceException_on_operator_($0)", concernedOperator.getLocalizedName());
	}

	public String getHTMLLocalizedMessage() {
		return FlexoLocalization.localizedForKeyWithParams("<html>NullReferenceException_on_operator_($0)</html>",
				concernedOperator.getLocalizedName());
	}

}
