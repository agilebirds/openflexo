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
import org.openflexo.antar.expr.Constant.DurationConstant;
import org.openflexo.antar.expr.Constant.FloatConstant;
import org.openflexo.antar.expr.Constant.IntegerConstant;
import org.openflexo.toolbox.Duration;


public abstract class ArithmeticUnaryOperator extends UnaryOperator {

	public static final ArithmeticUnaryOperator UNARY_MINUS = new ArithmeticUnaryOperator() {
		@Override
		public int getPriority() { return 3; }
		@Override
		public Constant evaluate(Constant arg) throws TypeMismatchException {
			if (arg instanceof IntegerConstant) {
				return new IntegerConstant(-((IntegerConstant)arg).getValue());
			}
			else if (arg instanceof FloatConstant) {
				return new FloatConstant(-((FloatConstant)arg).getValue());
			}
			else if (arg instanceof ArithmeticConstant) {
				return new FloatConstant(-((ArithmeticConstant)arg).getArithmeticValue());
			}
			else if (arg instanceof DurationConstant) {
				Duration inverse = ((DurationConstant)arg).getDuration().clone();
				inverse.setValue(-inverse.getValue());
				return new DurationConstant(inverse);
			}
			throw new TypeMismatchException(this,arg.getEvaluationType(),EvaluationType.ARITHMETIC_FLOAT,EvaluationType.ARITHMETIC_INTEGER);
		}
		@Override
		public EvaluationType getEvaluationType(EvaluationType operandType) throws TypeMismatchException {
			if (operandType.isLiteral()) return EvaluationType.LITERAL;
			if (operandType.isArithmeticInteger()) return EvaluationType.ARITHMETIC_INTEGER;
			if (operandType.isArithmeticFloat()) return EvaluationType.ARITHMETIC_FLOAT;
			if (operandType.isDuration()) return EvaluationType.DURATION;
			throw new TypeMismatchException(this,operandType,EvaluationType.ARITHMETIC_FLOAT,EvaluationType.ARITHMETIC_INTEGER,EvaluationType.DURATION,EvaluationType.LITERAL);
		}
		@Override
		public String getName() {
			return "unary_minus";
		}
	};
	
	public static final ArithmeticUnaryOperator SIN = new ScientificOperator() {
		@Override
		public int getPriority() { return 1; }
		@Override
		public Constant evaluate(Constant arg) throws TypeMismatchException {
			if (arg instanceof ArithmeticConstant) {
				return new FloatConstant(Math.sin(((ArithmeticConstant)arg).getArithmeticValue()));
			}
			throw new TypeMismatchException(this,arg.getEvaluationType(),EvaluationType.ARITHMETIC_FLOAT,EvaluationType.ARITHMETIC_INTEGER);
		}
		@Override
		public String getName() {
			return "trigonometric_sinus";
		}
	};
	
	public static final ArithmeticUnaryOperator ASIN = new ScientificOperator() {
		@Override
		public int getPriority() { return 1; }
		@Override
		public Constant evaluate(Constant arg) throws TypeMismatchException {
			if (arg instanceof ArithmeticConstant) {
				return new FloatConstant(Math.asin(((ArithmeticConstant)arg).getArithmeticValue()));
			}
			throw new TypeMismatchException(this,arg.getEvaluationType(),EvaluationType.ARITHMETIC_FLOAT,EvaluationType.ARITHMETIC_INTEGER);
		}
		@Override
		public String getName() {
			return "trigonometric_arc_sinus";
		}
	};
	
	public static final ArithmeticUnaryOperator COS = new ScientificOperator() {
		@Override
		public int getPriority() { return 1; }
		@Override
		public Constant evaluate(Constant arg) throws TypeMismatchException {
			if (arg instanceof ArithmeticConstant) {
				return new FloatConstant(Math.cos(((ArithmeticConstant)arg).getArithmeticValue()));
			}
			throw new TypeMismatchException(this,arg.getEvaluationType(),EvaluationType.ARITHMETIC_FLOAT,EvaluationType.ARITHMETIC_INTEGER);
		}
		@Override
		public String getName() {
			return "trigonometric_cosinus";
		}
	};
	
	public static final ArithmeticUnaryOperator ACOS = new ScientificOperator() {
		@Override
		public int getPriority() { return 1; }
		@Override
		public Constant evaluate(Constant arg) throws TypeMismatchException {
			if (arg instanceof ArithmeticConstant) {
				return new FloatConstant(Math.acos(((ArithmeticConstant)arg).getArithmeticValue()));
			}
			throw new TypeMismatchException(this,arg.getEvaluationType(),EvaluationType.ARITHMETIC_FLOAT,EvaluationType.ARITHMETIC_INTEGER);
		}
		@Override
		public String getName() {
			return "trigonometric_arc_cosinus";
		}
	};
	
	public static final ArithmeticUnaryOperator TAN = new ScientificOperator() {
		@Override
		public int getPriority() { return 1; }
		@Override
		public Constant evaluate(Constant arg) throws TypeMismatchException {
			if (arg instanceof ArithmeticConstant) {
				return new FloatConstant(Math.tan(((ArithmeticConstant)arg).getArithmeticValue()));
			}
			throw new TypeMismatchException(this,arg.getEvaluationType(),EvaluationType.ARITHMETIC_FLOAT,EvaluationType.ARITHMETIC_INTEGER);
		}
		@Override
		public String getName() {
			return "trigonometric_tangent";
		}
	};
	
	public static final ArithmeticUnaryOperator ATAN = new ScientificOperator() {
		@Override
		public int getPriority() { return 1; }
		@Override
		public Constant evaluate(Constant arg) throws TypeMismatchException {
			if (arg instanceof ArithmeticConstant) {
				return new FloatConstant(Math.atan(((ArithmeticConstant)arg).getArithmeticValue()));
			}
			throw new TypeMismatchException(this,arg.getEvaluationType(),EvaluationType.ARITHMETIC_FLOAT,EvaluationType.ARITHMETIC_INTEGER);
		}
		@Override
		public String getName() {
			return "trigonometric_arc_tangent";
			
		}
	};
	
	public static final ArithmeticUnaryOperator EXP = new ScientificOperator() {
		@Override
		public int getPriority() { return 1; }
		@Override
		public Constant evaluate(Constant arg) throws TypeMismatchException {
			if (arg instanceof ArithmeticConstant) {
				return new FloatConstant(Math.exp(((ArithmeticConstant)arg).getArithmeticValue()));
			}
			throw new TypeMismatchException(this,arg.getEvaluationType(),EvaluationType.ARITHMETIC_FLOAT,EvaluationType.ARITHMETIC_INTEGER);
		}
		@Override
		public String getName() {
			return "euler_number_raised_to_power";			
		}
	};
	
	public static final ArithmeticUnaryOperator LOG = new ScientificOperator() {
		@Override
		public int getPriority() { return 1; }
		@Override
		public Constant evaluate(Constant arg) throws TypeMismatchException {
			if (arg instanceof ArithmeticConstant) {
				return new FloatConstant(Math.log(((ArithmeticConstant)arg).getArithmeticValue()));
			}
			throw new TypeMismatchException(this,arg.getEvaluationType(),EvaluationType.ARITHMETIC_FLOAT,EvaluationType.ARITHMETIC_INTEGER);
		}
		@Override
		public String getName() {
			return "natural_logarithm_(base_e)";
			
		}
	};
	
	public static final ArithmeticUnaryOperator SQRT = new ScientificOperator() {
		@Override
		public int getPriority() { return 1; }
		@Override
		public Constant evaluate(Constant arg) throws TypeMismatchException {
			if (arg instanceof ArithmeticConstant) {
				return new FloatConstant(Math.sqrt(((ArithmeticConstant)arg).getArithmeticValue()));
			}
			throw new TypeMismatchException(this,arg.getEvaluationType(),EvaluationType.ARITHMETIC_FLOAT,EvaluationType.ARITHMETIC_INTEGER);
		}
		@Override
		public String getName() {
			return "square_root";
			
		}
	};
	
	public static abstract class ScientificOperator extends ArithmeticUnaryOperator
	{
		@Override
		public EvaluationType getEvaluationType(EvaluationType operandType) throws TypeMismatchException {
			if (operandType.isArithmeticOrLiteral()) return EvaluationType.ARITHMETIC_FLOAT;
			throw new TypeMismatchException(this,operandType,EvaluationType.ARITHMETIC_FLOAT,EvaluationType.ARITHMETIC_INTEGER,EvaluationType.LITERAL);
		}
	}

}
