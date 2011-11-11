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
package org.openflexo.foundation.bpel;

import org.openflexo.antar.expr.ArithmeticBinaryOperator;
import org.openflexo.antar.expr.ArithmeticUnaryOperator;
import org.openflexo.antar.expr.BinaryOperator;
import org.openflexo.antar.expr.BooleanBinaryOperator;
import org.openflexo.antar.expr.BooleanUnaryOperator;
import org.openflexo.antar.expr.ExpressionGrammar;
import org.openflexo.antar.expr.Operator;
import org.openflexo.antar.expr.OperatorNotSupportedException;
import org.openflexo.antar.expr.UnaryOperator;

public class BPELGrammar implements ExpressionGrammar {

	private static final BinaryOperator[] allSupportedBinaryOperators = { BooleanBinaryOperator.AND, BooleanBinaryOperator.OR,
			BooleanBinaryOperator.EQUALS, BooleanBinaryOperator.NOT_EQUALS, BooleanBinaryOperator.LESS_THAN,
			BooleanBinaryOperator.LESS_THAN_OR_EQUALS, BooleanBinaryOperator.GREATER_THAN, BooleanBinaryOperator.GREATER_THAN_OR_EQUALS,
			ArithmeticBinaryOperator.ADDITION, ArithmeticBinaryOperator.SUBSTRACTION, ArithmeticBinaryOperator.MULTIPLICATION,
			ArithmeticBinaryOperator.DIVISION,
	// ArithmeticBinaryOperator.POWER,
	};

	private static final UnaryOperator[] allSupportedUnaryOperators = { BooleanUnaryOperator.NOT, ArithmeticUnaryOperator.UNARY_MINUS,
	// ArithmeticUnaryOperator.SIN,
	// ArithmeticUnaryOperator.ASIN,
	// ArithmeticUnaryOperator.COS,
	// ArithmeticUnaryOperator.ACOS,
	// ArithmeticUnaryOperator.TAN,
	// ArithmeticUnaryOperator.ATAN,
	// ArithmeticUnaryOperator.EXP,
	// ArithmeticUnaryOperator.LOG,
	// ArithmeticUnaryOperator.SQRT
	};

	@Override
	public BinaryOperator[] getAllSupportedBinaryOperators() {
		return allSupportedBinaryOperators;
	}

	@Override
	public UnaryOperator[] getAllSupportedUnaryOperators() {
		return allSupportedUnaryOperators;
	}

	public String getSymbol(UnaryOperator operator) throws OperatorNotSupportedException {
		if (operator == BooleanUnaryOperator.NOT)
			return "not";
		if (operator == ArithmeticUnaryOperator.UNARY_MINUS)
			return "-";
		// if (operator == ArithmeticUnaryOperator.SIN) return "sin";
		// if (operator == ArithmeticUnaryOperator.ASIN) return "asin";
		// if (operator == ArithmeticUnaryOperator.COS) return "cos";
		// if (operator == ArithmeticUnaryOperator.ACOS) return "acos";
		// if (operator == ArithmeticUnaryOperator.TAN) return "tan";
		// if (operator == ArithmeticUnaryOperator.ATAN) return "atan";
		// if (operator == ArithmeticUnaryOperator.EXP) return "exp";
		// if (operator == ArithmeticUnaryOperator.LOG) return "log";
		// if (operator == ArithmeticUnaryOperator.SQRT) return "sqrt";
		throw new OperatorNotSupportedException();
	}

	public String getAlternativeSymbol(UnaryOperator operator) throws OperatorNotSupportedException {
		return null;
	}

	public String getSymbol(BinaryOperator operator) throws OperatorNotSupportedException {
		if (operator == BooleanBinaryOperator.AND)
			return "and";
		if (operator == BooleanBinaryOperator.OR)
			return "or";
		if (operator == BooleanBinaryOperator.EQUALS)
			return "=";
		if (operator == BooleanBinaryOperator.NOT_EQUALS)
			return "!=";
		if (operator == BooleanBinaryOperator.LESS_THAN)
			return "<";
		if (operator == BooleanBinaryOperator.LESS_THAN_OR_EQUALS)
			return "<=";
		if (operator == BooleanBinaryOperator.GREATER_THAN)
			return ">";
		if (operator == BooleanBinaryOperator.GREATER_THAN_OR_EQUALS)
			return ">=";
		if (operator == ArithmeticBinaryOperator.ADDITION)
			return "+";
		if (operator == ArithmeticBinaryOperator.SUBSTRACTION)
			return "-";
		if (operator == ArithmeticBinaryOperator.MULTIPLICATION)
			return "*";
		if (operator == ArithmeticBinaryOperator.DIVISION)
			return "/";
		// if (operator == ArithmeticBinaryOperator.POWER) return "^";
		throw new OperatorNotSupportedException();
	}

	public String getAlternativeSymbol(BinaryOperator operator) throws OperatorNotSupportedException {
		if (operator == BooleanBinaryOperator.AND)
			return "&&";
		if (operator == BooleanBinaryOperator.OR)
			return "||";
		if (operator == BooleanBinaryOperator.EQUALS)
			return "==";
		if (operator == ArithmeticBinaryOperator.DIVISION)
			return ":";
		return null;
	}

	@Override
	public String getAlternativeSymbol(Operator operator) throws OperatorNotSupportedException {
		if (operator instanceof UnaryOperator)
			return getAlternativeSymbol((UnaryOperator) operator);
		if (operator instanceof BinaryOperator)
			return getAlternativeSymbol((BinaryOperator) operator);
		throw new OperatorNotSupportedException();
	}

	@Override
	public String getSymbol(Operator operator) throws OperatorNotSupportedException {
		if (operator instanceof UnaryOperator)
			return getSymbol((UnaryOperator) operator);
		if (operator instanceof BinaryOperator)
			return getSymbol((BinaryOperator) operator);
		throw new OperatorNotSupportedException();
	}

}
