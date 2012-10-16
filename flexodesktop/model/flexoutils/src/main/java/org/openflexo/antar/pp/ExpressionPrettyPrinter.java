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
package org.openflexo.antar.pp;

import java.io.PrintStream;

import org.openflexo.antar.expr.BinaryOperator;
import org.openflexo.antar.expr.BinaryOperatorExpression;
import org.openflexo.antar.expr.BindingValueAsExpression;
import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.Constant.BooleanConstant;
import org.openflexo.antar.expr.Constant.DateConstant;
import org.openflexo.antar.expr.Constant.DurationConstant;
import org.openflexo.antar.expr.Constant.EnumConstant;
import org.openflexo.antar.expr.Constant.FloatConstant;
import org.openflexo.antar.expr.Constant.IntegerConstant;
import org.openflexo.antar.expr.Constant.StringConstant;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.ExpressionGrammar;
import org.openflexo.antar.expr.Function;
import org.openflexo.antar.expr.Operator;
import org.openflexo.antar.expr.OperatorNotSupportedException;
import org.openflexo.antar.expr.SymbolicConstant;
import org.openflexo.antar.expr.UnaryOperator;
import org.openflexo.antar.expr.UnaryOperatorExpression;
import org.openflexo.antar.expr.Variable;

public abstract class ExpressionPrettyPrinter {

	private ExpressionGrammar grammar;

	public ExpressionPrettyPrinter(ExpressionGrammar grammar) {
		super();
		this.grammar = grammar;
	}

	public void print(Expression expression, PrintStream out) {
		out.print(getStringRepresentation(expression));
	}

	public BinaryOperator[] getAllSupportedBinaryOperators() {
		return grammar.getAllSupportedBinaryOperators();
	}

	public UnaryOperator[] getAllSupportedUnaryOperators() {
		return grammar.getAllSupportedUnaryOperators();
	}

	public String getAlternativeSymbol(Operator operator) throws OperatorNotSupportedException {
		return grammar.getAlternativeSymbol(operator);
	}

	public String getSymbol(Operator operator) throws OperatorNotSupportedException {
		return grammar.getSymbol(operator);
	}

	public String getStringRepresentation(Expression expression) {
		if (expression == null) {
			return "null";
		}
		if (expression instanceof BindingValueAsExpression) {
			return makeStringRepresentation((BindingValueAsExpression) expression);
		}
		if (expression instanceof Variable) {
			return makeStringRepresentation((Variable) expression);
		}
		if (expression instanceof Constant) {
			return makeStringRepresentation((Constant) expression);
		}
		if (expression instanceof Function) {
			return makeStringRepresentation((Function) expression);
		}
		if (expression instanceof UnaryOperatorExpression) {
			return makeStringRepresentation((UnaryOperatorExpression) expression);
		}
		if (expression instanceof BinaryOperatorExpression) {
			return makeStringRepresentation((BinaryOperatorExpression) expression);
		}
		// return "<unknown "+expression.getClass().getSimpleName()+">";
		return expression.toString();
	}

	protected String makeStringRepresentation(Variable variable) {
		return variable.getName();
	}

	protected String makeStringRepresentation(BindingValueAsExpression bv) {
		return bv.getUnparsed();
	}

	protected String makeStringRepresentation(Constant constant) {
		if (constant instanceof SymbolicConstant) {
			return makeStringRepresentation((SymbolicConstant) constant);
		} else if (constant instanceof BooleanConstant) {
			return makeStringRepresentation((BooleanConstant) constant);
		} else if (constant instanceof FloatConstant) {
			return makeStringRepresentation((FloatConstant) constant);
		} else if (constant instanceof IntegerConstant) {
			return makeStringRepresentation((IntegerConstant) constant);
		} else if (constant instanceof StringConstant) {
			return makeStringRepresentation((StringConstant) constant);
		} else if (constant instanceof DateConstant) {
			return makeStringRepresentation((DateConstant) constant);
		} else if (constant instanceof DurationConstant) {
			return makeStringRepresentation((DurationConstant) constant);
		} else if (constant instanceof EnumConstant) {
			return makeStringRepresentation((EnumConstant) constant);
		}
		return "???";
	}

	protected abstract String makeStringRepresentation(BooleanConstant constant);

	protected abstract String makeStringRepresentation(FloatConstant constant);

	protected abstract String makeStringRepresentation(IntegerConstant constant);

	protected abstract String makeStringRepresentation(StringConstant constant);

	protected abstract String makeStringRepresentation(SymbolicConstant constant);

	protected abstract String makeStringRepresentation(DateConstant constant);

	protected abstract String makeStringRepresentation(DurationConstant constant);

	protected abstract String makeStringRepresentation(EnumConstant constant);

	protected abstract String makeStringRepresentation(Function function);

	protected abstract String makeStringRepresentation(UnaryOperatorExpression expression);

	protected abstract String makeStringRepresentation(BinaryOperatorExpression expression);

}
