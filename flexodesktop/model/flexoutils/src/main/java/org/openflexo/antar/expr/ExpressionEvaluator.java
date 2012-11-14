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

import org.openflexo.antar.expr.Constant.BooleanConstant;
import org.openflexo.antar.expr.Constant.FloatConstant;
import org.openflexo.antar.expr.Constant.FloatSymbolicConstant;
import org.openflexo.antar.expr.Constant.ObjectSymbolicConstant;

/**
 * This ExpressionTransformer is used to evaluate expressions
 * 
 * @author sylvain
 * 
 */
public class ExpressionEvaluator implements ExpressionTransformer {

	/**
	 * Performs the transformation of a resulting expression e, asserting that all contained expressions have already been transformed (this
	 * method is not recursive, to do so, use Expression.transform(ExpressionTransformer) API)
	 */
	@Override
	public Expression performTransformation(Expression e) throws TransformException {
		// e._checkSemanticallyAcceptable();
		if (e instanceof BinaryOperatorExpression) {
			return transformBinaryOperatorExpression((BinaryOperatorExpression) e);
		} else if (e instanceof UnaryOperatorExpression) {
			return transformUnaryOperatorExpression((UnaryOperatorExpression) e);
		} else if (e instanceof ConditionalExpression) {
			return transformConditionalExpression((ConditionalExpression) e);
		} else if (e instanceof FloatSymbolicConstant) {
			return transformFloatSymbolicConstant((FloatSymbolicConstant) e);
		}
		return e;
	}

	private Expression transformBinaryOperatorExpression(BinaryOperatorExpression e) throws TransformException {
		if (e.getLeftArgument() instanceof Constant && e.getRightArgument() instanceof Constant
				&& e.getLeftArgument() != ObjectSymbolicConstant.NULL && e.getRightArgument() != ObjectSymbolicConstant.NULL) {
			Constant returned = e.getOperator().evaluate((Constant) e.getLeftArgument(), (Constant) e.getRightArgument());
			return returned;
		}
		if (e.getLeftArgument() instanceof Constant && e.getLeftArgument() != ObjectSymbolicConstant.NULL) {
			return e.getOperator().evaluate((Constant) e.getLeftArgument(), e.getRightArgument());
		}
		if (e.getRightArgument() instanceof Constant && e.getRightArgument() != ObjectSymbolicConstant.NULL) {
			return e.getOperator().evaluate(e.getLeftArgument(), (Constant) e.getRightArgument());
		}
		return e;
	}

	private Expression transformUnaryOperatorExpression(UnaryOperatorExpression e) throws TransformException {
		if (e.getArgument() instanceof Constant && e.getArgument() != ObjectSymbolicConstant.NULL) {
			Constant returned = e.getOperator().evaluate((Constant) e.getArgument());
			return returned;
		}
		return e;
	}

	private Expression transformConditionalExpression(ConditionalExpression e) throws TransformException {
		if (e.getCondition() == BooleanConstant.TRUE) {
			return e.getThenExpression();
		} else if (e.getCondition() == BooleanConstant.FALSE) {
			return e.getElseExpression();
		}
		return e;
	}

	private FloatConstant transformFloatSymbolicConstant(FloatSymbolicConstant e) {
		return new FloatConstant(e.getValue());
	}

}
