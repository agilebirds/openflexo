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
package org.openflexo.foundation.bindings;

import org.openflexo.antar.expr.EvaluationType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMType;

public class IntegerStaticBinding extends StaticBinding<Long> {

	private long value;

	public IntegerStaticBinding() {
		super();
	}

	public IntegerStaticBinding(long aValue) {
		super();
		value = aValue;
	}

	public IntegerStaticBinding(BindingDefinition bindingDefinition, FlexoModelObject owner, long aValue) {
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
	public String getWodStringRepresentation() {
		return getStringRepresentation();
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

		if (getBindingDefinition().getType().isDouble() || getBindingDefinition().getType().isFloat()
				|| getBindingDefinition().getType().isLong())
			return true;

		if (getBindingDefinition().getType().isInteger())
			return (getValue() >= Integer.MIN_VALUE && getValue() <= Integer.MAX_VALUE);

		if (getBindingDefinition().getType().isShort())
			return (getValue() >= Short.MIN_VALUE && getValue() <= Short.MAX_VALUE);

		if (getBindingDefinition().getType().isByte())
			return (getValue() >= Byte.MIN_VALUE && getValue() <= Byte.MAX_VALUE);

		if (getBindingDefinition().getType().isChar())
			return (getValue() >= Character.MIN_VALUE && getValue() <= Character.MAX_VALUE);

		return super._areTypesMatching();
	}

	@Override
	public DMType getAccessedType() {
		if (getOwner() != null && getOwner().getProject() != null && accessedType == null) {
			if (getValue() >= Short.MIN_VALUE && getValue() <= Short.MAX_VALUE)
				accessedType = DMType.makeShortDMType(getOwner().getProject());
			else if (getValue() >= Integer.MIN_VALUE && getValue() <= Integer.MAX_VALUE)
				accessedType = DMType.makeShortDMType(getOwner().getProject());
			else
				accessedType = DMType.makeLongDMType(getOwner().getProject());
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
