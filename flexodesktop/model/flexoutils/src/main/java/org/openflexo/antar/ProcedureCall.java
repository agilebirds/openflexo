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

import java.util.Vector;

import org.openflexo.antar.expr.Expression;

public class ProcedureCall extends Instruction {

	private Procedure procedure;
	private Vector<Expression> arguments;

	public ProcedureCall(Procedure procedure) {
		super();
		this.procedure = procedure;
		this.arguments = new Vector<Expression>();
	}

	public ProcedureCall(Procedure procedure, Expression... arguments) {
		this(procedure);
		for (Expression arg : arguments)
			addArgument(arg);
	}

	public ProcedureCall(Procedure procedure, Vector<Expression> arguments) {
		this(procedure);
		for (Expression arg : arguments)
			addArgument(arg);
	}

	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	public Vector<Expression> getArguments() {
		return arguments;
	}

	public void setArguments(Vector<Expression> arguments) {
		this.arguments = arguments;
	}

	public void addArgument(Expression arg) {
		arguments.add(arg);
	}

	public void removeArgument(Expression arg) {
		arguments.remove(arg);
	}

	@Override
	public ProcedureCall clone() {
		ProcedureCall returned = new ProcedureCall(procedure, arguments);
		returned.setHeaderComment(getHeaderComment());
		returned.setInlineComment(getInlineComment());
		return returned;
	}

}
