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

public class FIBNumber extends FIBWidget {

	public static enum Parameters implements FIBModelAttribute {
		numberType, validateOnReturn, minValue, maxValue, increment, columns
	}

	private boolean validateOnReturn = false;
	private Number minValue;
	private Number maxValue;
	private Number increment;
	private NumberType numberType = NumberType.IntegerType;
	private Integer columns;

	public FIBNumber() {
	}

	public static enum NumberType {
		ByteType, ShortType, IntegerType, LongType, FloatType, DoubleType;
	}

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

	public Number getMinValue() {
		return minValue;
	}

	public void setMinValue(Number minValue) {
		this.minValue = minValue;
	}

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

	public Number getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Number maxValue) {
		this.maxValue = maxValue;
	}

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

	public Number getIncrement() {
		return increment;
	}

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

	public NumberType getNumberType() {
		return numberType;
	}

	public void setNumberType(NumberType numberType) {
		FIBAttributeNotification<NumberType> notification = requireChange(Parameters.numberType, numberType);
		if (notification != null) {
			this.numberType = numberType;
			hasChanged(notification);
		}
	}

	public boolean getValidateOnReturn() {
		return validateOnReturn;
	}

	public void setValidateOnReturn(boolean validateOnReturn) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.validateOnReturn, validateOnReturn);
		if (notification != null) {
			this.validateOnReturn = validateOnReturn;
			hasChanged(notification);
		}
	}

	public Integer getColumns() {
		return columns;
	}

	public void setColumns(Integer columns) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.columns, columns);
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
