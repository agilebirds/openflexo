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

import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.expr.BindingValue.AbstractBindingPathElement;
import org.openflexo.antar.expr.BindingValue.MethodCallBindingPathElement;
import org.openflexo.antar.expr.BindingValue.NormalBindingPathElement;
import org.openflexo.antar.expr.Constant.BooleanConstant;
import org.openflexo.antar.expr.Constant.DateConstant;
import org.openflexo.antar.expr.Constant.DurationConstant;
import org.openflexo.antar.expr.Constant.EnumConstant;
import org.openflexo.antar.expr.Constant.FloatConstant;
import org.openflexo.antar.expr.Constant.IntegerConstant;
import org.openflexo.antar.expr.Constant.ObjectConstant;
import org.openflexo.antar.expr.Constant.StringConstant;
import org.openflexo.antar.pp.ExpressionPrettyPrinter;
import org.openflexo.toolbox.Duration;
import org.openflexo.xmlcode.StringEncoder;

public class DefaultExpressionPrettyPrinter extends ExpressionPrettyPrinter {

	private StringEncoder.DateConverter dateConverter = new StringEncoder.DateConverter();
	private Duration.DurationStringConverter durationConverter = new Duration.DurationStringConverter();

	public DefaultExpressionPrettyPrinter() {
		this(new DefaultGrammar());
	}

	protected DefaultExpressionPrettyPrinter(ExpressionGrammar grammar) {
		super(grammar);
	}

	@Override
	protected String makeStringRepresentation(BooleanConstant constant) {
		if (constant == BooleanConstant.FALSE) {
			return "false";
		} else if (constant == BooleanConstant.TRUE) {
			return "true";
		}
		return "???";
	}

	@Override
	protected String makeStringRepresentation(FloatConstant constant) {
		return Double.toString(constant.getValue());
	}

	@Override
	protected String makeStringRepresentation(IntegerConstant constant) {
		return Long.toString(constant.getValue());
	}

	@Override
	protected String makeStringRepresentation(StringConstant constant) {
		return '"' + constant.getValue() + '"';
	}

	@Override
	protected String makeStringRepresentation(SymbolicConstant constant) {
		return constant.getSymbol();
	}

	@Override
	protected String makeStringRepresentation(UnaryOperatorExpression expression) {
		try {
			return "(" + getSymbol(expression.getOperator()) + "(" + getStringRepresentation(expression.getArgument()) + ")" + ")";
		} catch (OperatorNotSupportedException e) {
			return "<unsupported>";
		}
	}

	@Override
	protected String makeStringRepresentation(BinaryOperatorExpression expression) {
		try {
			return "(" + getStringRepresentation(expression.getLeftArgument()) + " " + getSymbol(expression.getOperator()) + " "
					+ getStringRepresentation(expression.getRightArgument()) + ")";
		} catch (OperatorNotSupportedException e) {
			return "<unsupported>";
		}
	}

	@Override
	protected String makeStringRepresentation(DateConstant constant) {
		if (constant == null || constant.getDate() == null) {
			return "[null]";
		}
		return "[" + dateConverter.convertToString(constant.getDate()) + "]";
	}

	@Override
	protected String makeStringRepresentation(DurationConstant constant) {
		if (constant == null || constant.getDuration() == null) {
			return "[null]";
		}
		return "[" + durationConverter.convertToString(constant.getDuration()) + "]";
	}

	@Override
	protected final String makeStringRepresentation(EnumConstant constant) {
		return constant.getName();
	}

	@Override
	protected String makeStringRepresentation(ObjectConstant constant) {
		return "[Object:" + constant.getValue().toString() + "]";
	}

	@Override
	protected String makeStringRepresentation(BindingValue bv) {
		if (bv.isValid()) {
			StringBuffer sb = new StringBuffer();
			if (bv.getBindingVariable() != null) {
				sb.append(bv.getBindingVariable().getVariableName());
				for (BindingPathElement e : bv.getBindingPath()) {
					sb.append("." + e.getSerializationRepresentation());
				}
			}
			return sb.toString();

		} else {
			StringBuffer sb = new StringBuffer();
			boolean isFirst = true;
			if (bv != null) {
				for (AbstractBindingPathElement e : bv.getParsedBindingPath()) {
					sb.append((isFirst ? "" : ".") + makeStringRepresentation(e));
					isFirst = false;
				}
			}
			return sb.toString();
		}
	}

	@Override
	protected String makeStringRepresentation(AbstractBindingPathElement e) {
		if (e instanceof NormalBindingPathElement) {
			return ((NormalBindingPathElement) e).property;
		} else if (e instanceof MethodCallBindingPathElement) {
			StringBuffer sb = new StringBuffer();
			sb.append(((MethodCallBindingPathElement) e).method);
			sb.append("(");
			boolean isFirst = true;
			for (Expression arg : ((MethodCallBindingPathElement) e).args) {
				sb.append((isFirst ? "" : ",") + getStringRepresentation(arg));
				isFirst = false;
			}
			sb.append(")");
			return sb.toString();
		}
		return e.toString();
	}

	@Override
	protected String makeStringRepresentation(ConditionalExpression expression) {
		return "(" + getStringRepresentation(expression.getCondition()) + " ? " + getStringRepresentation(expression.getThenExpression())
				+ " : " + getStringRepresentation(expression.getElseExpression()) + ")";
	}

	@Override
	protected String makeStringRepresentation(CastExpression expression) {
		return "(" + makeStringRepresentation(expression.getCastType()) + ")" + getStringRepresentation(expression.getArgument());
	}

	@Override
	protected String makeStringRepresentation(TypeReference tr) {
		StringBuffer sb = new StringBuffer();
		sb.append("$" + tr.getBaseType());
		if (tr.getParameters().size() > 0) {
			sb.append("<");
			boolean isFirst = true;
			for (TypeReference param : tr.getParameters()) {
				sb.append((isFirst ? "" : ",") + makeStringRepresentation(param));
				isFirst = false;
			}
			sb.append(">");
		}
		return sb.toString();
	}
}
