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

public class NullStaticBinding extends StaticBinding<Object> {

	public NullStaticBinding() {
		super();
	}

	public NullStaticBinding(BindingDefinition bindingDefinition, Bindable owner) {
		super(bindingDefinition, owner);
	}

	@Override
	public EvaluationType getEvaluationType() {
		return EvaluationType.LITERAL;
	}

	@Override
	public String getStringRepresentation() {
		return "null";
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public void setValue(Object aValue) {
	}

	@Override
	public Class<Object> getStaticBindingClass() {
		return Object.class;
	}

	@Override
	public NullStaticBinding clone() {
		NullStaticBinding returned = new NullStaticBinding();
		returned.setsWith(this);
		return returned;
	}

}
