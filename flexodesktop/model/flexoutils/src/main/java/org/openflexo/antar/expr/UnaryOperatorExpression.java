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

public class UnaryOperatorExpression extends Expression {

	private UnaryOperator operator;
	private Expression argument;

	public UnaryOperatorExpression(UnaryOperator operator, Expression argument) {
		super();
		this.operator = operator;
		this.argument = argument;
	}

	@Override
	public int getDepth() {
		return argument.getDepth() + 1;
	}

	public int getPriority() {
		if (operator != null) {
			return operator.getPriority();
		}
		return -1;
	}

	public Expression getArgument() {
		return argument;
	}

	public void setArgument(Expression argument) {
		this.argument = argument;
	}

	public UnaryOperator getOperator() {
		return operator;
	}

	public void setOperator(UnaryOperator operator) {
		this.operator = operator;
	}

	/*@Override
	public Expression evaluate(EvaluationContext context, Bindable bindable) throws TypeMismatchException {
		_checkSemanticallyAcceptable();
		Expression evaluatedArgument = argument.evaluate(context, bindable);
		if (evaluatedArgument instanceof Constant) {
			Constant returned = operator.evaluate((Constant) evaluatedArgument);
			// if (context != null) return context.getConstantFactory().makeConstant(returned.getParsingValue());
			return returned;
		}
		return new UnaryOperatorExpression(operator, evaluatedArgument);
	}*/

	@Override
	public Expression transform(ExpressionTransformer transformer) throws TransformException {

		Expression expression = this;
		Expression transformedArgument = argument.transform(transformer);

		if (!transformedArgument.equals(argument)) {
			expression = new UnaryOperatorExpression(operator, transformedArgument);
		}

		return transformer.performTransformation(expression);
	}

	@Override
	public EvaluationType getEvaluationType() throws TypeMismatchException {
		return getOperator().getEvaluationType(getArgument().getEvaluationType());
	}

	@Override
	protected Vector<Expression> getChilds() {
		Vector<Expression> returned = new Vector<Expression>();
		returned.add(getArgument());
		return returned;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UnaryOperatorExpression) {
			UnaryOperatorExpression e = (UnaryOperatorExpression) obj;
			return getOperator().equals(e.getOperator()) && getArgument().equals(e.getArgument());
		}
		return super.equals(obj);
	}

}
