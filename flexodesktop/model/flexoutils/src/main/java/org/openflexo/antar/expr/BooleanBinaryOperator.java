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

import org.openflexo.antar.expr.Constant.ArithmeticConstant;
import org.openflexo.antar.expr.Constant.BooleanConstant;
import org.openflexo.antar.expr.Constant.DateConstant;
import org.openflexo.antar.expr.Constant.DurationConstant;
import org.openflexo.antar.expr.Constant.EnumConstant;
import org.openflexo.antar.expr.Constant.ObjectSymbolicConstant;
import org.openflexo.antar.expr.Constant.StringConstant;

public abstract class BooleanBinaryOperator extends BinaryOperator {

	public static final BooleanBinaryOperator AND = new LogicalBinaryOperator() {
		@Override
		public int getPriority() {
			return 6;
		}

		@Override
		public Constant evaluate(Constant leftArg, Constant rightArg) throws TypeMismatchException {
			if (leftArg instanceof BooleanConstant && rightArg instanceof BooleanConstant) {
				return BooleanConstant.get(((BooleanConstant) leftArg).getValue() && ((BooleanConstant) rightArg).getValue());
			}
			if (leftArg instanceof BooleanConstant && !((BooleanConstant) leftArg).getValue()) {
				return BooleanConstant.get(false);
			}
			if (rightArg instanceof BooleanConstant && !((BooleanConstant) rightArg).getValue()) {
				return BooleanConstant.get(false);
			}
			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(), EvaluationType.BOOLEAN);
		}

		@Override
		public String getName() {
			return "logical_and";
		}

		@Override
		public Expression evaluate(Expression leftArg, Constant rightArg) throws TypeMismatchException {
			if (rightArg == BooleanConstant.FALSE) {
				return BooleanConstant.FALSE;
			}
			if (rightArg == BooleanConstant.TRUE) {
				return leftArg;
			}
			return super.evaluate(leftArg, rightArg);
		}

		@Override
		public Expression evaluate(Constant leftArg, Expression rightArg) throws TypeMismatchException {
			if (leftArg == BooleanConstant.FALSE) {
				return BooleanConstant.FALSE;
			}
			if (leftArg == BooleanConstant.TRUE) {
				return rightArg;
			}
			return super.evaluate(leftArg, rightArg);
		}
	};

	public static final BooleanBinaryOperator OR = new LogicalBinaryOperator() {
		@Override
		public int getPriority() {
			return 7;
		}

		@Override
		public Constant evaluate(Constant leftArg, Constant rightArg) throws TypeMismatchException {
			if (leftArg instanceof BooleanConstant && rightArg instanceof BooleanConstant) {
				return BooleanConstant.get(((BooleanConstant) leftArg).getValue() || ((BooleanConstant) rightArg).getValue());
			}
			if (leftArg instanceof BooleanConstant && ((BooleanConstant) leftArg).getValue()) {
				return BooleanConstant.get(true);
			}
			if (rightArg instanceof BooleanConstant && ((BooleanConstant) rightArg).getValue()) {
				return BooleanConstant.get(true);
			}
			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(), EvaluationType.BOOLEAN);
		}

		@Override
		public String getName() {
			return "logical_or";
		}

		@Override
		public Expression evaluate(Expression leftArg, Constant rightArg) throws TypeMismatchException {
			if (rightArg == BooleanConstant.FALSE) {
				return leftArg;
			}
			if (rightArg == BooleanConstant.TRUE) {
				return BooleanConstant.TRUE;
			}
			return super.evaluate(leftArg, rightArg);
		}

		@Override
		public Expression evaluate(Constant leftArg, Expression rightArg) throws TypeMismatchException {
			if (leftArg == BooleanConstant.FALSE) {
				return rightArg;
			}
			if (leftArg == BooleanConstant.TRUE) {
				return BooleanConstant.TRUE;
			}
			return super.evaluate(leftArg, rightArg);
		}
	};

	public static final BooleanBinaryOperator EQUALS = new ComparisonBinaryOperator() {
		@Override
		public int getPriority() {
			return 5;
		}

		@Override
		public Constant evaluate(Constant leftArg, Constant rightArg) throws TypeMismatchException {
			// System.out.println("leftArg="+leftArg+" of "+leftArg.getEvaluationType());
			// System.out.println("rightArg="+rightArg+" of "+rightArg.getEvaluationType());
			if (leftArg instanceof BooleanConstant && rightArg instanceof BooleanConstant) {
				return ((BooleanConstant) leftArg).getValue() == ((BooleanConstant) rightArg).getValue() ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				return ((ArithmeticConstant) leftArg).getArithmeticValue() == ((ArithmeticConstant) rightArg).getArithmeticValue() ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			if (leftArg instanceof DateConstant && rightArg instanceof DateConstant) {
				return ((DateConstant) leftArg).getDate().equals(((DateConstant) rightArg).getDate()) ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			if (leftArg instanceof DurationConstant && rightArg instanceof DurationConstant) {
				return ((DurationConstant) leftArg).getDuration().equals(((DurationConstant) rightArg).getDuration()) ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			if (leftArg instanceof StringConstant && rightArg instanceof StringConstant) {
				return ((StringConstant) leftArg).getValue().equals(((StringConstant) rightArg).getValue()) ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			if (leftArg instanceof EnumConstant && rightArg instanceof EnumConstant) {
				return ((EnumConstant) leftArg).getName().equals(((EnumConstant) rightArg).getName()) ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			if (leftArg instanceof EnumConstant && rightArg instanceof StringConstant) {
				return ((EnumConstant) leftArg).getName().equals(((StringConstant) rightArg).getValue()) ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			// System.out.println("leftArg="+leftArg+" of "+leftArg.getEvaluationType());
			// System.out.println("rightArg="+rightArg+" of "+rightArg.getEvaluationType());
			if (rightArg == ObjectSymbolicConstant.NULL) {
				if (leftArg == ObjectSymbolicConstant.NULL) {
					return Constant.BooleanConstant.TRUE;
				}
				if (leftArg instanceof StringConstant && ((StringConstant) leftArg).getValue().equals("null")) {
					return Constant.BooleanConstant.TRUE;
				}
				return Constant.BooleanConstant.FALSE;
			}
			if (leftArg == ObjectSymbolicConstant.NULL) {
				if (rightArg == ObjectSymbolicConstant.NULL) {
					return Constant.BooleanConstant.TRUE;
				}
				if (rightArg instanceof StringConstant && ((StringConstant) rightArg).getValue().equals("null")) {
					return Constant.BooleanConstant.TRUE;
				}
				return Constant.BooleanConstant.FALSE;
			}
			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(), EvaluationType.values());
		}

		@Override
		public String getName() {
			return "equals_operator";
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType leftOperandType, EvaluationType rightOperandType)
				throws TypeMismatchException {
			if (leftOperandType.isStringOrLiteral() && rightOperandType.isStringOrLiteral()) {
				return EvaluationType.BOOLEAN;
			}
			if (leftOperandType.isBooleanOrLiteral() && rightOperandType.isBooleanOrLiteral()) {
				return EvaluationType.BOOLEAN;
			}
			if (leftOperandType.isEnumOrLiteral() && rightOperandType.isEnumOrLiteral()) {
				return EvaluationType.BOOLEAN;
			}
			return super.getEvaluationType(leftOperandType, rightOperandType);
		}

	};

	public static final BooleanBinaryOperator NOT_EQUALS = new ComparisonBinaryOperator() {
		@Override
		public int getPriority() {
			return 5;
		}

		@Override
		public Constant evaluate(Constant leftArg, Constant rightArg) throws TypeMismatchException, NullReferenceException {
			// TODO catch exception and replace EQUALS by NOT_EQUALS (not very important but who knows)
			return EQUALS.evaluate(leftArg, rightArg) == Constant.BooleanConstant.FALSE ? Constant.BooleanConstant.TRUE
					: Constant.BooleanConstant.FALSE;
		}

		@Override
		public String getName() {
			return "not_equals_operator";
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType leftOperandType, EvaluationType rightOperandType)
				throws TypeMismatchException {
			if (leftOperandType.isStringOrLiteral() && rightOperandType.isStringOrLiteral()) {
				return EvaluationType.BOOLEAN;
			}
			if (leftOperandType.isBooleanOrLiteral() && rightOperandType.isBooleanOrLiteral()) {
				return EvaluationType.BOOLEAN;
			}
			if (leftOperandType.isEnumOrLiteral() && rightOperandType.isEnumOrLiteral()) {
				return EvaluationType.BOOLEAN;
			}
			return super.getEvaluationType(leftOperandType, rightOperandType);
		}
	};

	public static final BooleanBinaryOperator LESS_THAN = new ComparisonBinaryOperator() {
		@Override
		public int getPriority() {
			return 5;
		}

		@Override
		public Constant evaluate(Constant leftArg, Constant rightArg) throws TypeMismatchException {
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				return ((ArithmeticConstant) leftArg).getArithmeticValue() < ((ArithmeticConstant) rightArg).getArithmeticValue() ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			if (leftArg instanceof DateConstant && rightArg instanceof DateConstant) {
				return ((DateConstant) leftArg).getDate().before(((DateConstant) rightArg).getDate()) ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			if (leftArg instanceof DurationConstant && rightArg instanceof DurationConstant) {
				return ((DurationConstant) leftArg).getDuration().lessThan(((DurationConstant) rightArg).getDuration()) ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(),
					EvaluationType.ARITHMETIC_FLOAT, EvaluationType.ARITHMETIC_INTEGER, EvaluationType.DATE, EvaluationType.DURATION);
		}

		@Override
		public String getName() {
			return "less_than_operator";
		}
	};

	public static final BooleanBinaryOperator LESS_THAN_OR_EQUALS = new ComparisonBinaryOperator() {
		@Override
		public int getPriority() {
			return 5;
		}

		@Override
		public Constant evaluate(Constant leftArg, Constant rightArg) throws TypeMismatchException {
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				return ((ArithmeticConstant) leftArg).getArithmeticValue() <= ((ArithmeticConstant) rightArg).getArithmeticValue() ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			if (leftArg instanceof DateConstant && rightArg instanceof DateConstant) {
				return ((DateConstant) leftArg).getDate().before(((DateConstant) rightArg).getDate())
						|| ((DateConstant) leftArg).getDate().equals(((DateConstant) rightArg).getDate()) ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			if (leftArg instanceof DurationConstant && rightArg instanceof DurationConstant) {
				return ((DurationConstant) leftArg).getDuration().lessOrEqualsThan(((DurationConstant) rightArg).getDuration()) ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(),
					EvaluationType.ARITHMETIC_FLOAT, EvaluationType.ARITHMETIC_INTEGER, EvaluationType.DATE, EvaluationType.DURATION);
		}

		@Override
		public String getName() {
			return "less_than_or_equals_operator";
		}
	};

	public static final BooleanBinaryOperator GREATER_THAN = new ComparisonBinaryOperator() {
		@Override
		public int getPriority() {
			return 5;
		}

		@Override
		public Constant evaluate(Constant leftArg, Constant rightArg) throws TypeMismatchException {
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				return ((ArithmeticConstant) leftArg).getArithmeticValue() > ((ArithmeticConstant) rightArg).getArithmeticValue() ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			if (leftArg instanceof DateConstant && rightArg instanceof DateConstant) {
				return ((DateConstant) leftArg).getDate().after(((DateConstant) rightArg).getDate()) ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			if (leftArg instanceof DurationConstant && rightArg instanceof DurationConstant) {
				return ((DurationConstant) leftArg).getDuration().greaterThan(((DurationConstant) rightArg).getDuration()) ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			// System.out.println("leftArg="+leftArg);
			// System.out.println("rightArg="+rightArg);
			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(),
					EvaluationType.ARITHMETIC_FLOAT, EvaluationType.ARITHMETIC_INTEGER, EvaluationType.DATE, EvaluationType.DURATION);
		}

		@Override
		public String getName() {
			return "greater_than_operator";
		}
	};

	public static final BooleanBinaryOperator GREATER_THAN_OR_EQUALS = new ComparisonBinaryOperator() {
		@Override
		public int getPriority() {
			return 5;
		}

		@Override
		public Constant evaluate(Constant leftArg, Constant rightArg) throws TypeMismatchException {
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				return ((ArithmeticConstant) leftArg).getArithmeticValue() >= ((ArithmeticConstant) rightArg).getArithmeticValue() ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			if (leftArg instanceof DateConstant && rightArg instanceof DateConstant) {
				return ((DateConstant) leftArg).getDate().after(((DateConstant) rightArg).getDate())
						|| ((DateConstant) leftArg).getDate().equals(((DateConstant) rightArg).getDate()) ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			if (leftArg instanceof DurationConstant && rightArg instanceof DurationConstant) {
				return ((DurationConstant) leftArg).getDuration().greaterOrEqualsThan(((DurationConstant) rightArg).getDuration()) ? Constant.BooleanConstant.TRUE
						: Constant.BooleanConstant.FALSE;
			}
			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(),
					EvaluationType.ARITHMETIC_FLOAT, EvaluationType.ARITHMETIC_INTEGER, EvaluationType.DATE, EvaluationType.DURATION);
		}

		@Override
		public String getName() {
			return "greater_than_or_equals_operator";
		}
	};

	public static abstract class LogicalBinaryOperator extends BooleanBinaryOperator {
		@Override
		public EvaluationType getEvaluationType(EvaluationType leftOperandType, EvaluationType rightOperandType)
				throws TypeMismatchException {
			if (leftOperandType.isBooleanOrLiteral() && rightOperandType.isBooleanOrLiteral()) {
				return EvaluationType.BOOLEAN;
			}
			throw new TypeMismatchException(this, leftOperandType, rightOperandType, EvaluationType.BOOLEAN, EvaluationType.LITERAL);
		}
	}

	public static abstract class ComparisonBinaryOperator extends BooleanBinaryOperator {
		@Override
		public EvaluationType getEvaluationType(EvaluationType leftOperandType, EvaluationType rightOperandType)
				throws TypeMismatchException {
			if (leftOperandType.isArithmeticOrLiteral() && rightOperandType.isArithmeticOrLiteral()) {
				return EvaluationType.BOOLEAN;
			}
			if (leftOperandType.isDateOrLiteral() && rightOperandType.isDateOrLiteral()) {
				return EvaluationType.BOOLEAN;
			}
			if (leftOperandType.isDurationOrLiteral() && rightOperandType.isDurationOrLiteral()) {
				return EvaluationType.BOOLEAN;
			}
			throw new TypeMismatchException(this, leftOperandType, rightOperandType, EvaluationType.ARITHMETIC_INTEGER,
					EvaluationType.ARITHMETIC_FLOAT, EvaluationType.DATE, EvaluationType.DURATION, EvaluationType.LITERAL);
		}
	}

}
