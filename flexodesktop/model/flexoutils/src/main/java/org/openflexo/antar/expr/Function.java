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

import java.util.Vector;

import org.openflexo.antar.binding.Bindable;

public class Function extends Expression {

	private String name;
	private Vector<Expression> args;

	public Function(String name, Vector<Expression> args) {
		super();
		this.name = name;
		this.args = new Vector<Expression>();
		this.args.addAll(args);
	}

	@Override
	public int getDepth() {
		return 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Vector<Expression> getArgs() {
		return args;
	}

	public void setArgs(Vector<Expression> args) {
		this.args = args;
	}

	public void addToArgs(Expression arg) {
		this.args.add(arg);
	}

	public void removeFromArgs(Expression arg) {
		this.args.remove(arg);
	}

	@Override
	public Expression evaluate(EvaluationContext context, Bindable bindable) throws TypeMismatchException {

		Vector<Expression> evaluatedArgs = new Vector<Expression>();
		for (Expression arg : getArgs()) {
			evaluatedArgs.add(arg.evaluate(context, bindable));
		}
		if (context != null) {
			return context.getFunctionFactory().makeFunction(getName(), evaluatedArgs, bindable);
		}
		return new Function(getName(), evaluatedArgs);
	}

	@Override
	public EvaluationType getEvaluationType() {
		return EvaluationType.LITERAL;
	}

	@Override
	protected Vector<Expression> getChilds() {
		return null;
	}

}
