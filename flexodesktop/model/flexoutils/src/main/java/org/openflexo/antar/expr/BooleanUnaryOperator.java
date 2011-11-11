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

public abstract class BooleanUnaryOperator extends UnaryOperator {

	public static final BooleanUnaryOperator NOT = new BooleanUnaryOperator() {
		@Override
		public int getPriority() {
			return 4;
		}

		@Override
		public Constant evaluate(Constant arg) throws TypeMismatchException {
			if (arg instanceof BooleanConstant) {
				return BooleanConstant.get(!((BooleanConstant) arg).getValue());
			}
			throw new TypeMismatchException(this, arg.getEvaluationType(), EvaluationType.BOOLEAN);
		}

		@Override
		public String getName() {
			return "logical_not";
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType operandType) throws TypeMismatchException {
			if (operandType.isBooleanOrLiteral()) {
				return EvaluationType.BOOLEAN;
			}
			throw new TypeMismatchException(this, operandType, EvaluationType.BOOLEAN, EvaluationType.LITERAL);
		}
	};

}
