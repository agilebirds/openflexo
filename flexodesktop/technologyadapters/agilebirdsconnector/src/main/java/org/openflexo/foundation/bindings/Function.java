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
package org.openflexo.foundation.bindings;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openflexo.antar.expr.EvaluationType;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.ExpressionTransformer;
import org.openflexo.antar.expr.ExpressionVisitor;
import org.openflexo.antar.expr.TransformException;
import org.openflexo.antar.expr.VisitorException;

@Deprecated
public class Function extends Expression {

	private String name;
	private List<Expression> args;

	public Function(String name, List<Expression> args) {
		super();
		this.name = name;
		this.args = new ArrayList<Expression>(args);
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

	public List<Expression> getArgs() {
		return args;
	}

	public void setArgs(List<Expression> args) {
		this.args = args;
	}

	public void addToArgs(Expression arg) {
		this.args.add(arg);
	}

	public void removeFromArgs(Expression arg) {
		this.args.remove(arg);
	}

	/*@Override
	public Expression evaluate(EvaluationContext context, Bindable bindable) throws TypeMismatchException {

		List<Expression> evaluatedArgs = new ArrayList<Expression>();
		for (Expression arg : getArgs()) {
			evaluatedArgs.add(arg.evaluate(context, bindable));
		}
		if (context != null) {
			return context.getFunctionFactory().makeFunction(getName(), evaluatedArgs, bindable);
		}
		return new Function(getName(), evaluatedArgs);
	}*/

	@Override
	public Expression transform(ExpressionTransformer transformer) throws TransformException {

		Vector<Expression> transformedArgs = new Vector<Expression>();
		for (Expression arg : getArgs()) {
			transformedArgs.add(arg.transform(transformer));
		}
		Function f = new Function(getName(), transformedArgs);
		return transformer.performTransformation(f);
	}

	@Override
	public EvaluationType getEvaluationType() {
		return EvaluationType.LITERAL;
	}

	@Override
	protected Vector<Expression> getChilds() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Function) {
			Function e = (Function) obj;
			return getName().equals(e.getName()) && getArgs().equals(e.getArgs());
		}
		return super.equals(obj);
	}

	@Override
	public void visit(ExpressionVisitor visitor) throws VisitorException {
		for (Expression arg : args) {
			arg.visit(visitor);
		}
	}

}
