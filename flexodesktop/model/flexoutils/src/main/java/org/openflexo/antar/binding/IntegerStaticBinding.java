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
package org.openflexo.antar.binding;

import java.lang.reflect.Type;

import org.openflexo.antar.expr.EvaluationType;

public class IntegerStaticBinding extends StaticBinding<Long> {

	private long value;

	public IntegerStaticBinding() {
		super();
	}

	public IntegerStaticBinding(long aValue) {
		super();
		value = aValue;
	}

	public IntegerStaticBinding(BindingDefinition bindingDefinition, Bindable owner, long aValue) {
		super(bindingDefinition, owner);
		value = aValue;
	}

	@Override
	public EvaluationType getEvaluationType() {
		return EvaluationType.ARITHMETIC_INTEGER;
	}

	@Override
	public String getStringRepresentation() {
		return Long.toString(value);
	}

	@Override
	public Long getValue() {
		return value;
	}

	@Override
	public void setValue(Long aValue) {
		value = aValue;
		accessedType = null;
	}

	@Override
	protected boolean _areTypesMatching() {
		if (getBindingDefinition().getType() == null)
			return true;

		if (getBindingDefinition().getType().equals(Double.class) || getBindingDefinition().getType().equals(Double.TYPE)
				|| getBindingDefinition().getType().equals(Float.class) || getBindingDefinition().getType().equals(Float.TYPE)
				|| getBindingDefinition().getType().equals(Long.class) || getBindingDefinition().getType().equals(Long.TYPE))
			return true;

		if (getBindingDefinition().getType().equals(Integer.class) || getBindingDefinition().getType().equals(Integer.TYPE))
			return (getValue() >= Integer.MIN_VALUE && getValue() <= Integer.MAX_VALUE);

		if (getBindingDefinition().getType().equals(Short.class) || getBindingDefinition().getType().equals(Short.TYPE))
			return (getValue() >= Short.MIN_VALUE && getValue() <= Short.MAX_VALUE);

		if (getBindingDefinition().getType().equals(Byte.class) || getBindingDefinition().getType().equals(Byte.TYPE))
			return (getValue() >= Byte.MIN_VALUE && getValue() <= Byte.MAX_VALUE);

		if (getBindingDefinition().getType().equals(Character.class) || getBindingDefinition().getType().equals(Character.TYPE))
			return (getValue() >= Character.MIN_VALUE && getValue() <= Character.MAX_VALUE);

		return super._areTypesMatching();
	}

	@Override
	public Type getAccessedType() {
		if (accessedType == null) {
			if (getValue() >= Short.MIN_VALUE && getValue() <= Short.MAX_VALUE)
				accessedType = Short.class;
			else if (getValue() >= Integer.MIN_VALUE && getValue() <= Integer.MAX_VALUE)
				accessedType = Integer.class;
			else
				accessedType = Long.class;
		}
		return accessedType;
	}

	@Override
	public Class<Long> getStaticBindingClass() {
		return Long.class;
	}

	@Override
	public IntegerStaticBinding clone() {
		IntegerStaticBinding returned = new IntegerStaticBinding();
		returned.setsWith(this);
		return returned;
	}

}
