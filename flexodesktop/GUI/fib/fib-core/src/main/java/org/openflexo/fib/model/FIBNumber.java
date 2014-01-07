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
package org.openflexo.fib.model;

import java.lang.reflect.Type;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBNumber.FIBNumberImpl.class)
@XMLElement(xmlTag = "Number")
public interface FIBNumber extends FIBWidget {

	public static enum NumberType {
		ByteType, ShortType, IntegerType, LongType, FloatType, DoubleType;
	}

	@PropertyIdentifier(type = boolean.class)
	public static final String VALIDATE_ON_RETURN_KEY = "validateOnReturn";
	@PropertyIdentifier(type = boolean.class)
	public static final String ALLOWS_NULL_KEY = "allowsNull";
	@PropertyIdentifier(type = Number.class)
	public static final String MIN_VALUE_KEY = "minValue";
	@PropertyIdentifier(type = Number.class)
	public static final String MAX_VALUE_KEY = "maxValue";
	@PropertyIdentifier(type = Number.class)
	public static final String INCREMENT_KEY = "increment";
	@PropertyIdentifier(type = NumberType.class)
	public static final String NUMBER_TYPE_KEY = "numberType";
	@PropertyIdentifier(type = Integer.class)
	public static final String COLUMNS_KEY = "columns";

	@Getter(value = VALIDATE_ON_RETURN_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getValidateOnReturn();

	@Setter(VALIDATE_ON_RETURN_KEY)
	public void setValidateOnReturn(boolean validateOnReturn);

	@Getter(value = ALLOWS_NULL_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getAllowsNull();

	@Setter(ALLOWS_NULL_KEY)
	public void setAllowsNull(boolean allowsNull);

	@Getter(value = MIN_VALUE_KEY)
	@XMLAttribute
	public Number getMinValue();

	@Setter(MIN_VALUE_KEY)
	public void setMinValue(Number minValue);

	@Getter(value = MAX_VALUE_KEY)
	@XMLAttribute
	public Number getMaxValue();

	@Setter(MAX_VALUE_KEY)
	public void setMaxValue(Number maxValue);

	@Getter(value = INCREMENT_KEY)
	@XMLAttribute
	public Number getIncrement();

	@Setter(INCREMENT_KEY)
	public void setIncrement(Number increment);

	@Getter(value = NUMBER_TYPE_KEY)
	@XMLAttribute
	public NumberType getNumberType();

	@Setter(NUMBER_TYPE_KEY)
	public void setNumberType(NumberType numberType);

	@Getter(value = COLUMNS_KEY)
	@XMLAttribute
	public Integer getColumns();

	@Setter(COLUMNS_KEY)
	public void setColumns(Integer columns);

	public Number retrieveMaxValue();

	public Number retrieveMinValue();

	public Number retrieveIncrement();

	public static abstract class FIBNumberImpl extends FIBWidgetImpl implements FIBNumber {

		private boolean allowsNull = false;
		private boolean validateOnReturn = false;
		private Number minValue;
		private Number maxValue;
		private Number increment;
		private NumberType numberType = NumberType.IntegerType;
		private Integer columns;

		public FIBNumberImpl() {
		}

		@Override
		public String getBaseName() {
			return "NumberSelector";
		}

		@Override
		public Number retrieveMinValue() {
			if (minValue == null) {
				switch (numberType) {
				case ByteType:
					minValue = Byte.MIN_VALUE;
					break;
				case ShortType:
					minValue = Short.MIN_VALUE;
					break;
				case IntegerType:
					minValue = Integer.MIN_VALUE;
					break;
				case LongType:
					minValue = Long.MIN_VALUE;
					break;
				case FloatType:
					minValue = -Float.MAX_VALUE;
					break;
				case DoubleType:
					minValue = -Double.MAX_VALUE;
					break;
				default:
					break;
				}
			}
			return minValue;
		}

		@Override
		public Number getMinValue() {
			return minValue;
		}

		@Override
		public void setMinValue(Number minValue) {
			this.minValue = minValue;
		}

		@Override
		public Number retrieveMaxValue() {
			if (maxValue == null) {
				switch (numberType) {
				case ByteType:
					maxValue = Byte.MAX_VALUE;
					break;
				case ShortType:
					maxValue = Short.MAX_VALUE;
					break;
				case IntegerType:
					maxValue = Integer.MAX_VALUE;
					break;
				case LongType:
					maxValue = Long.MAX_VALUE;
					break;
				case FloatType:
					maxValue = Float.MAX_VALUE;
					break;
				case DoubleType:
					maxValue = Double.MAX_VALUE;
					break;
				default:
					break;
				}
			}
			return maxValue;
		}

		@Override
		public Number getMaxValue() {
			return maxValue;
		}

		@Override
		public void setMaxValue(Number maxValue) {
			this.maxValue = maxValue;
		}

		@Override
		public Number retrieveIncrement() {
			if (increment == null) {
				switch (numberType) {
				case ByteType:
					increment = new Byte((byte) 1);
					break;
				case ShortType:
					increment = new Short((short) 1);
					break;
				case IntegerType:
					increment = new Integer(1);
					break;
				case LongType:
					increment = new Long(1);
					break;
				case FloatType:
					increment = new Float(1);
					break;
				case DoubleType:
					increment = new Double(1);
					break;
				default:
					break;
				}
			}
			return increment;
		}

		@Override
		public Number getIncrement() {
			return increment;
		}

		@Override
		public void setIncrement(Number increment) {
			this.increment = increment;
		}

		@Override
		public Type getDefaultDataClass() {
			switch (numberType) {
			case ByteType:
				return Byte.class;
			case ShortType:
				return Short.class;
			case IntegerType:
				return Integer.class;
			case LongType:
				return Long.class;
			case FloatType:
				return Float.class;
			case DoubleType:
				return Double.class;
			default:
				return Number.class;
			}
		}

		@Override
		public NumberType getNumberType() {
			return numberType;
		}

		@Override
		public void setNumberType(NumberType numberType) {
			FIBPropertyNotification<NumberType> notification = requireChange(NUMBER_TYPE_KEY, numberType);
			if (notification != null) {
				this.numberType = numberType;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getValidateOnReturn() {
			return validateOnReturn;
		}

		@Override
		public void setValidateOnReturn(boolean validateOnReturn) {
			FIBPropertyNotification<Boolean> notification = requireChange(VALIDATE_ON_RETURN_KEY, validateOnReturn);
			if (notification != null) {
				this.validateOnReturn = validateOnReturn;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getAllowsNull() {
			return allowsNull;
		}

		@Override
		public void setAllowsNull(boolean allowsNull) {
			FIBPropertyNotification<Boolean> notification = requireChange(ALLOWS_NULL_KEY, allowsNull);
			if (notification != null) {
				this.allowsNull = allowsNull;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getColumns() {
			return columns;
		}

		@Override
		public void setColumns(Integer columns) {
			FIBPropertyNotification<Integer> notification = requireChange(COLUMNS_KEY, columns);
			if (notification != null) {
				this.columns = columns;
				hasChanged(notification);
			}
		}

		public int getIncrementAsInteger() {
			return getIncrement().intValue();
		}

		public void setIncrementAsInteger(int incrementAsInteger) {
			setIncrement(incrementAsInteger);
		}

		public int getMinValueAsInteger() {
			return getMinValue().intValue();
		}

		public void setMinValueAsInteger(int minValueAsInteger) {
			setMinValue(minValueAsInteger);
		}

		public int getMaxValueAsInteger() {
			return getMaxValue().intValue();
		}

		public void setMaxValueAsInteger(int maxValueAsInteger) {
			setMaxValue(maxValueAsInteger);
		}

	}
}
