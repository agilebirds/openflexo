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

import java.util.Date;
import java.util.Vector;

import org.openflexo.antar.expr.parser.BooleanValue;
import org.openflexo.antar.expr.parser.DateValue;
import org.openflexo.antar.expr.parser.DurationValue;
import org.openflexo.antar.expr.parser.FloatValue;
import org.openflexo.antar.expr.parser.IntValue;
import org.openflexo.antar.expr.parser.StringValue;
import org.openflexo.antar.expr.parser.Value;
import org.openflexo.toolbox.Duration;

public abstract class Constant<V> extends Expression {

	@Override
	public Expression evaluate(EvaluationContext context) {
		return this;
	}

	@Override
	public int getDepth() {
		return 0;
	}

	@Override
	protected Vector<Expression> getChilds() {
		return null;
	}

	public abstract V getValue();

	public abstract Value getParsingValue();

	public static abstract class BooleanConstant extends Constant<Boolean> {
		public static BooleanConstant get(boolean value) {
			if (value)
				return TRUE;
			else
				return FALSE;
		}

		@Override
		public EvaluationType getEvaluationType() {
			return EvaluationType.BOOLEAN;
		}

		@Override
		public abstract Boolean getValue();

		public static final BooleanConstant TRUE = new BooleanConstant() {
			@Override
			public Boolean getValue() {
				return true;
			}

			@Override
			public Value getParsingValue() {
				return new BooleanValue(true);
			}
		};

		public static final BooleanConstant FALSE = new BooleanConstant() {
			@Override
			public Boolean getValue() {
				return false;
			}

			@Override
			public Value getParsingValue() {
				return new BooleanValue(false);
			}
		};
	}

	public static class StringConstant extends Constant<String> {
		private String value;

		@Override
		public EvaluationType getEvaluationType() {
			return EvaluationType.STRING;
		}

		public StringConstant(String value) {
			super();
			this.value = value;
		}

		@Override
		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public Value getParsingValue() {
			return new StringValue(value);
		}
	}

	public static class EnumConstant extends Constant<Enum> {
		private String name;

		@Override
		public EvaluationType getEvaluationType() {
			return EvaluationType.ENUM;
		}

		public EnumConstant(String aName) {
			super();
			this.name = aName;
		}

		public String getName() {
			return name;
		}

		public void setName(String value) {
			this.name = value;
		}

		@Override
		public Value getParsingValue() {
			return new StringValue(name);
		}

		@Override
		public Enum getValue() {
			// TODO !
			return null;
		}
	}

	public static abstract class ArithmeticConstant<V extends Number> extends Constant<V> {
		public abstract double getArithmeticValue();

	}

	public static class IntegerConstant extends ArithmeticConstant<Long> {
		private long value;

		@Override
		public EvaluationType getEvaluationType() {
			return EvaluationType.ARITHMETIC_INTEGER;
		}

		public IntegerConstant(long value) {
			super();
			this.value = value;
		}

		@Override
		public Long getValue() {
			return value;
		}

		public void setValue(long value) {
			this.value = value;
		}

		@Override
		public double getArithmeticValue() {
			return getValue();
		}

		@Override
		public Value getParsingValue() {
			return new IntValue(value);
		}

	}

	public static class FloatConstant extends ArithmeticConstant<Double> {
		private double value;

		@Override
		public EvaluationType getEvaluationType() {
			return EvaluationType.ARITHMETIC_FLOAT;
		}

		public FloatConstant(double value) {
			super();
			this.value = value;
		}

		@Override
		public Double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}

		@Override
		public double getArithmeticValue() {
			return getValue();
		}

		@Override
		public Value getParsingValue() {
			return new FloatValue(value);
		}

	}

	public static class FloatSymbolicConstant extends FloatConstant implements SymbolicConstant {
		private String symbol;

		private FloatSymbolicConstant(String symbol, double value) {
			super(value);
			this.symbol = symbol;
		}

		@Override
		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		public static FloatSymbolicConstant PI = new FloatSymbolicConstant("pi", Math.PI);
		public static FloatSymbolicConstant E = new FloatSymbolicConstant("e", Math.E);

		@Override
		public String getValueAsString() {
			return Double.toString(getValue());
		}

		@Override
		public Expression evaluate(EvaluationContext context) {
			return new FloatConstant(getValue());
		}

	}

	@Override
	public abstract EvaluationType getEvaluationType();

	public static class DateConstant extends Constant<Date> {
		private Date date;

		@Override
		public EvaluationType getEvaluationType() {
			return EvaluationType.DATE;
		}

		public DateConstant(Date date) {
			super();
			this.date = date;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		@Override
		public Value getParsingValue() {
			return new DateValue(date);
		}

		@Override
		public Date getValue() {
			return getDate();
		}

	}

	public static abstract class DateSymbolicConstant extends DateConstant implements SymbolicConstant {
		private String symbol;

		DateSymbolicConstant(String symbol) {
			super(null);
			this.symbol = symbol;
		}

		@Override
		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		public static final DateSymbolicConstant TODAY = new DateSymbolicConstant("today") {
			@Override
			public Date computeDateForNow() {
				// TODO replace with new implementation of org.openflexo.toolbox.Date
				return new Date();
			}
		};

		public static final DateSymbolicConstant NOW = new DateSymbolicConstant("now") {
			@Override
			public Date computeDateForNow() {
				// TODO replace with new implementation of org.openflexo.toolbox.Date
				return new Date();
			}
		};

		@Override
		public String getValueAsString() {
			return getSymbol();
		}

		@Override
		public Expression evaluate(EvaluationContext context) {
			return new DateConstant(computeDateForNow());
		}

		public abstract Date computeDateForNow();

	}

	public static class DurationConstant extends Constant<Duration> {
		private Duration duration;

		@Override
		public EvaluationType getEvaluationType() {
			return EvaluationType.DURATION;
		}

		public DurationConstant(Duration duration) {
			super();
			this.duration = duration;
		}

		public Duration getDuration() {
			return duration;
		}

		public void setDuration(Duration duration) {
			this.duration = duration;
		}

		@Override
		public Value getParsingValue() {
			return new DurationValue(duration);
		}

		@Override
		public Duration getValue() {
			return getDuration();
		}
	}

	public static class ObjectSymbolicConstant extends Constant<Object> implements SymbolicConstant {
		private String symbol;

		private ObjectSymbolicConstant(String symbol) {
			super();
			this.symbol = symbol;
		}

		@Override
		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		public static final ObjectSymbolicConstant NULL = new ObjectSymbolicConstant("null");
		public static final ObjectSymbolicConstant THIS = new ObjectSymbolicConstant("this");

		@Override
		public String getValueAsString() {
			return getSymbol();
		}

		@Override
		public Expression evaluate(EvaluationContext context) {
			return this;
		}

		@Override
		public EvaluationType getEvaluationType() {
			return EvaluationType.LITERAL;
		}

		@Override
		public Value getParsingValue() {
			return null;
		}

		@Override
		public Object getValue() {
			// TODO
			return null;
		}
	}

}
