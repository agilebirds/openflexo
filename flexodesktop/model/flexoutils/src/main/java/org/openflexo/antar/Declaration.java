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
package org.openflexo.antar;

import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.Variable;

public class Declaration extends Instruction {

	private Type type;
	private Variable variable;
	private Expression initializationValue;

	public Declaration(Type type, Variable variable, Expression initializationValue) {
		super();
		this.type = type;
		this.variable = variable;
		this.initializationValue = initializationValue;
	}

	public Expression getInitializationValue() {
		return initializationValue;
	}

	public void setInitializationValue(Expression initializationValue) {
		this.initializationValue = initializationValue;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Variable getVariable() {
		return variable;
	}

	public void setVariable(Variable variable) {
		this.variable = variable;
	}

	@Override
	public Declaration clone() {
		Declaration returned = new Declaration(type, variable, initializationValue);
		returned.setHeaderComment(getHeaderComment());
		returned.setInlineComment(getInlineComment());
		return returned;
	}

}
