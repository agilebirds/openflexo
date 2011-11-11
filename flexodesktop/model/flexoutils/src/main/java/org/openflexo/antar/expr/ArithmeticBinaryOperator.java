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
import org.openflexo.antar.expr.Constant.FloatConstant;
import org.openflexo.antar.expr.Constant.IntegerConstant;
import org.openflexo.antar.expr.Constant.StringConstant;
import org.openflexo.toolbox.Duration;

public abstract class ArithmeticBinaryOperator extends BinaryOperator {

	public static final ArithmeticBinaryOperator ADDITION = new ArithmeticBinaryOperator() {
		@Override
		public int getPriority() {
			return 3;
		}

		@Override
		public Constant evaluate(Constant leftArg, Constant rightArg) throws TypeMismatchException {
			if (leftArg instanceof ArithmeticConstant) {
				if (rightArg instanceof ArithmeticConstant) {
					if (leftArg instanceof IntegerConstant && rightArg instanceof IntegerConstant) {
						return new IntegerConstant(((IntegerConstant) leftArg).getValue() + ((IntegerConstant) rightArg).getValue());
					}
					return new FloatConstant(((ArithmeticConstant) leftArg).getArithmeticValue()
							+ ((ArithmeticConstant) rightArg).getArithmeticValue());
				}
				throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(), EvaluationType.values());
			} else if (leftArg instanceof StringConstant) {
				if (rightArg instanceof StringConstant) {
					return new StringConstant(((StringConstant) leftArg).getValue() + ((StringConstant) rightArg).getValue());
				} else if (rightArg instanceof IntegerConstant) {
					return new StringConstant(((StringConstant) leftArg).getValue() + ((IntegerConstant) rightArg).getValue());
				} else if (rightArg instanceof FloatConstant) {
					return new StringConstant(((StringConstant) leftArg).getValue() + ((FloatConstant) rightArg).getValue());
				} else if (rightArg instanceof BooleanConstant) {
					return new StringConstant(((StringConstant) leftArg).getValue() + ((BooleanConstant) rightArg).getValue());
				} else if (rightArg instanceof DateConstant) {
					return new StringConstant(((StringConstant) leftArg).getValue() + ((DateConstant) rightArg).getDate().toString());
				} else if (rightArg instanceof DurationConstant) {
					return new StringConstant(((StringConstant) leftArg).getValue()
							+ ((DurationConstant) rightArg).getDuration().getSerializationRepresentation());
				}
			} else if (leftArg instanceof DurationConstant) {
				if (rightArg instanceof DurationConstant) {
					return new DurationConstant(Duration.durationPlusDuration(((DurationConstant) leftArg).getDuration(),
							((DurationConstant) rightArg).getDuration()));
				} else if (rightArg instanceof DateConstant) {
					return new DateConstant(Duration.datePlusDuration(((DateConstant) rightArg).getDate(),
							((DurationConstant) leftArg).getDuration()));
				}
			} else if (leftArg instanceof DateConstant) {
				if (rightArg instanceof DurationConstant) {
					return new DateConstant(Duration.datePlusDuration(((DateConstant) leftArg).getDate(),
							((DurationConstant) rightArg).getDuration()));
				}
			}
			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(), EvaluationType.values());
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType leftOperandType, EvaluationType rightOperandType)
				throws TypeMismatchException {
			if (leftOperandType.isLiteral()) {
				return EvaluationType.LITERAL; // Undecided
			} else if (leftOperandType.isArithmetic()) {
				if (rightOperandType.isArithmetic()) {
					if (leftOperandType.isArithmeticInteger() && rightOperandType.isArithmeticInteger())
						return EvaluationType.ARITHMETIC_INTEGER;
					else
						return EvaluationType.ARITHMETIC_FLOAT;
				} else if (rightOperandType.isLiteral())
					return EvaluationType.ARITHMETIC_FLOAT; // Undecided
			} else if (leftOperandType.isString()) {
				return EvaluationType.STRING;
			} else if (leftOperandType.isDuration()) {
				if (rightOperandType.isDurationOrLiteral())
					return EvaluationType.DURATION;
				if (rightOperandType.isDateOrLiteral())
					return EvaluationType.DATE;
			} else if (leftOperandType.isDate()) {
				if (rightOperandType.isDurationOrLiteral())
					return EvaluationType.DATE;
			}
			throw new TypeMismatchException(this, leftOperandType, rightOperandType, EvaluationType.ARITHMETIC_FLOAT,
					EvaluationType.ARITHMETIC_INTEGER, EvaluationType.DATE, EvaluationType.DURATION, EvaluationType.LITERAL);
		}

		@Override
		public String getName() {
			return "addition";
		}
	};

	public static final ArithmeticBinaryOperator SUBSTRACTION = new ArithmeticBinaryOperator() {
		@Override
		public int getPriority() {
			return 3;
		}

		@Override
		public Constant evaluate(Constant leftArg, Constant rightArg) throws TypeMismatchException {
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				if (leftArg instanceof IntegerConstant && rightArg instanceof IntegerConstant) {
					return new IntegerConstant(((IntegerConstant) leftArg).getValue() - ((IntegerConstant) rightArg).getValue());
				}
				return new FloatConstant(((ArithmeticConstant) leftArg).getArithmeticValue()
						- ((ArithmeticConstant) rightArg).getArithmeticValue());
			} else if (leftArg instanceof DurationConstant) {
				if (rightArg instanceof DurationConstant) {
					return new DurationConstant(Duration.durationMinusDuration(((DurationConstant) leftArg).getDuration(),
							((DurationConstant) rightArg).getDuration()));
				}
			} else if (leftArg instanceof DateConstant) {
				if (rightArg instanceof DurationConstant) {
					return new DateConstant(Duration.dateMinusDuration(((DateConstant) leftArg).getDate(),
							((DurationConstant) rightArg).getDuration()));
				}
			} else if (leftArg instanceof DateConstant) {
				if (rightArg instanceof DateConstant) {
					return new DurationConstant(Duration.dateMinusDate(((DateConstant) leftArg).getDate(),
							((DateConstant) rightArg).getDate()));
				}
			}
			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(),
					EvaluationType.ARITHMETIC_FLOAT, EvaluationType.ARITHMETIC_INTEGER, EvaluationType.DATE, EvaluationType.DURATION);
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType leftOperandType, EvaluationType rightOperandType)
				throws TypeMismatchException {

			if (leftOperandType.isLiteral()) {
				return EvaluationType.LITERAL; // Undecided
			} else if (leftOperandType.isArithmetic()) {
				if (rightOperandType.isArithmetic()) {
					if (leftOperandType.isArithmeticInteger() && rightOperandType.isArithmeticInteger())
						return EvaluationType.ARITHMETIC_INTEGER;
					else
						return EvaluationType.ARITHMETIC_FLOAT;
				} else if (rightOperandType.isLiteral())
					return EvaluationType.LITERAL; // Undecided
			} else if (leftOperandType.isDuration()) {
				if (rightOperandType.isDurationOrLiteral())
					return EvaluationType.DURATION;
			} else if (leftOperandType.isDate()) {
				if (rightOperandType.isDurationOrLiteral())
					return EvaluationType.DATE;
				if (rightOperandType.isDateOrLiteral())
					return EvaluationType.DURATION;
			}
			throw new TypeMismatchException(this, leftOperandType, rightOperandType, EvaluationType.ARITHMETIC_FLOAT,
					EvaluationType.ARITHMETIC_INTEGER, EvaluationType.DATE, EvaluationType.DURATION, EvaluationType.LITERAL);
		}

		@Override
		public String getName() {
			return "substraction";
		}
	};

	public static final ArithmeticBinaryOperator MULTIPLICATION = new ArithmeticBinaryOperator() {
		@Override
		public int getPriority() {
			return 2;
		}

		@Override
		public Constant evaluate(Constant leftArg, Constant rightArg) throws TypeMismatchException {
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				if (leftArg instanceof IntegerConstant && rightArg instanceof IntegerConstant) {
					return new IntegerConstant(((IntegerConstant) leftArg).getValue() * ((IntegerConstant) rightArg).getValue());
				}
				return new FloatConstant(((ArithmeticConstant) leftArg).getArithmeticValue()
						* ((ArithmeticConstant) rightArg).getArithmeticValue());
			}
			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(),
					EvaluationType.ARITHMETIC_FLOAT, EvaluationType.ARITHMETIC_INTEGER);
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType leftOperandType, EvaluationType rightOperandType)
				throws TypeMismatchException {
			if (leftOperandType.isArithmeticOrLiteral()) {
				if (rightOperandType.isArithmeticOrLiteral()) {
					if (leftOperandType.isArithmeticInteger() && rightOperandType.isArithmeticInteger())
						return EvaluationType.ARITHMETIC_INTEGER;
					else
						return EvaluationType.ARITHMETIC_FLOAT;
				}
			}
			throw new TypeMismatchException(this, leftOperandType, rightOperandType, EvaluationType.ARITHMETIC_FLOAT,
					EvaluationType.ARITHMETIC_INTEGER, EvaluationType.LITERAL);
		}

		@Override
		public String getName() {
			return "multiplication";
		}
	};

	public static final ArithmeticBinaryOperator DIVISION = new ArithmeticBinaryOperator() {
		@Override
		public int getPriority() {
			return 2;
		}

		@Override
		public Constant evaluate(Constant leftArg, Constant rightArg) throws TypeMismatchException {
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				return new FloatConstant(((ArithmeticConstant) leftArg).getArithmeticValue()
						/ ((ArithmeticConstant) rightArg).getArithmeticValue());
			}
			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(),
					EvaluationType.ARITHMETIC_FLOAT, EvaluationType.ARITHMETIC_INTEGER);
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType leftOperandType, EvaluationType rightOperandType)
				throws TypeMismatchException {
			if (leftOperandType.isArithmeticOrLiteral()) {
				if (rightOperandType.isArithmeticOrLiteral()) {
					return EvaluationType.ARITHMETIC_FLOAT;
				}
			}
			throw new TypeMismatchException(this, leftOperandType, rightOperandType, EvaluationType.ARITHMETIC_FLOAT,
					EvaluationType.ARITHMETIC_INTEGER, EvaluationType.LITERAL);
		}

		@Override
		public String getName() {
			return "division";
		}
	};

	public static final ArithmeticBinaryOperator POWER = new ArithmeticBinaryOperator() {
		@Override
		public int getPriority() {
			return 2;
		}

		@Override
		public Constant evaluate(Constant leftArg, Constant rightArg) throws TypeMismatchException {
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				return new FloatConstant(Math.pow(((ArithmeticConstant) leftArg).getArithmeticValue(),
						((ArithmeticConstant) rightArg).getArithmeticValue()));
			}
			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(),
					EvaluationType.ARITHMETIC_FLOAT, EvaluationType.ARITHMETIC_INTEGER);
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType leftOperandType, EvaluationType rightOperandType)
				throws TypeMismatchException {
			if (leftOperandType.isArithmeticOrLiteral()) {
				if (rightOperandType.isArithmeticOrLiteral()) {
					return EvaluationType.ARITHMETIC_FLOAT;
				}
			}
			throw new TypeMismatchException(this, leftOperandType, rightOperandType, EvaluationType.ARITHMETIC_FLOAT,
					EvaluationType.ARITHMETIC_INTEGER, EvaluationType.LITERAL);
		}

		@Override
		public String getName() {
			return "power";

		}
	};

}
