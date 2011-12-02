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

import org.openflexo.antar.expr.EvaluationType;

public class BooleanStaticBinding extends StaticBinding<Boolean> {

	private boolean value;

	public BooleanStaticBinding() {
		super();
	}

	public BooleanStaticBinding(boolean aValue) {
		super();
		value = aValue;
	}

	public BooleanStaticBinding(BindingDefinition bindingDefinition, Bindable owner, boolean aValue) {
		super(bindingDefinition, owner);
		value = aValue;
	}

	@Override
	public EvaluationType getEvaluationType() {
		return EvaluationType.BOOLEAN;
	}

	public String getWodStringRepresentation() {
		return Boolean.toString(value);
	}

	@Override
	public String getStringRepresentation() {
		return Boolean.toString(value);
	}

	@Override
	public Boolean getValue() {
		return value;
	}

	@Override
	public void setValue(Boolean aValue) {
		value = aValue;
	}

	@Override
	public Class<Boolean> getStaticBindingClass() {
		return Boolean.class;
	}

	@Override
	public BooleanStaticBinding clone() {
		BooleanStaticBinding returned = new BooleanStaticBinding();
		returned.setsWith(this);
		return returned;
	}

}
