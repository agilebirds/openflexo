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

public class ConditionalExpression extends Expression {

	private Expression condition;
	private Expression thenExpression;
	private Expression elseExpression;

	public ConditionalExpression(Expression condition, Expression thenExpression, Expression elseExpression) {
		super();
		this.condition = condition;
		this.thenExpression = thenExpression;
		this.elseExpression = elseExpression;
	}

	@Override
	public int getDepth() {
		return Math.max(Math.max(thenExpression.getDepth(), elseExpression.getDepth()), condition.getDepth()) + 1;
	}

	/*@Override
	public Expression evaluate(EvaluationContext context) throws TypeMismatchException {
		_checkSemanticallyAcceptable();

		Expression evaluatedCondition = condition.evaluate(context);
		Expression evaluatedThenExpression = thenExpression.evaluate(context);
		Expression evaluatedElseExpression = elseExpression.evaluate(context);

		// special case if condition has been evaluated
		if (evaluatedCondition == BooleanConstant.FALSE) {
			return evaluatedElseExpression; // No need to analyze further
		} else if (evaluatedCondition == BooleanConstant.TRUE) {
			return evaluatedThenExpression; // No need to analyze further
		}

		return new ConditionalExpression(evaluatedCondition, evaluatedThenExpression, evaluatedElseExpression);
	}*/

	@Override
	public Expression transform(ExpressionTransformer transformer) throws TransformException {

		Expression expression = this;
		Expression transformedCondition = condition.transform(transformer);
		Expression transformedThenExpression = thenExpression.transform(transformer);
		Expression transformedElseExpression = elseExpression.transform(transformer);

		if ((!transformedCondition.equals(condition)) || (!transformedThenExpression.equals(thenExpression))
				|| (!transformedElseExpression.equals(elseExpression))) {
			expression = new ConditionalExpression(transformedCondition, transformedThenExpression, transformedElseExpression);
		}

		return transformer.performTransformation(expression);
	}

	@Override
	public EvaluationType getEvaluationType() throws TypeMismatchException {
		EvaluationType thenEvaluationType = thenExpression.getEvaluationType();
		EvaluationType elseEvaluationType = elseExpression.getEvaluationType();
		if (thenEvaluationType != elseEvaluationType) {
			throw TypeMismatchException.buildIncompatibleEvaluationTypeException(thenEvaluationType, elseEvaluationType);
		}
		return thenEvaluationType;
	}

	@Override
	protected Vector<Expression> getChilds() {
		Vector<Expression> returned = new Vector<Expression>();
		returned.add(getCondition());
		returned.add(getThenExpression());
		returned.add(getElseExpression());
		return returned;
	}

	public Expression getCondition() {
		return condition;
	}

	public void setCondition(Expression condition) {
		this.condition = condition;
	}

	public Expression getThenExpression() {
		return thenExpression;
	}

	public void setThenExpression(Expression thenExpression) {
		this.thenExpression = thenExpression;
	}

	public Expression getElseExpression() {
		return elseExpression;
	}

	public void setElseExpression(Expression elseExpression) {
		this.elseExpression = elseExpression;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConditionalExpression) {
			ConditionalExpression e = (ConditionalExpression) obj;
			return getCondition().equals(e.getCondition()) && getThenExpression().equals(e.getThenExpression())
					&& getElseExpression().equals(e.getElseExpression());
		}
		return super.equals(obj);
	}

}
