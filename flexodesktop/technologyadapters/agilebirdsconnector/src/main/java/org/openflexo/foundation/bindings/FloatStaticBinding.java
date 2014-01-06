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
import org.openflexo.foundation.AgileBirdsObject;

public class FloatStaticBinding extends StaticBinding<Double> {

	private double value;

	public FloatStaticBinding() {
		super();
	}

	public FloatStaticBinding(double aValue) {
		super();
		value = aValue;
	}

	public FloatStaticBinding(BindingDefinition bindingDefinition, AgileBirdsObject owner, double aValue) {
		super(bindingDefinition, owner);
		value = aValue;
	}

	@Override
	public EvaluationType getEvaluationType() {
		return EvaluationType.ARITHMETIC_FLOAT;
	}

	@Override
	public String getStringRepresentation() {
		return Double.toString(value);
	}

	@Override
	public String getWodStringRepresentation() {
		return getStringRepresentation();
	}

	@Override
	public Double getValue() {
		return value;
	}

	@Override
	public void setValue(Double aValue) {
		value = aValue;
	}

	@Override
	protected boolean _areTypesMatching() {
		if (getBindingDefinition().getType() == null) {
			return true;
		}

		if (getBindingDefinition().getType().isDouble() || getBindingDefinition().getType().isFloat()) {
			return true;
		}

		return super._areTypesMatching();
	}

	@Override
	public Class<Double> getStaticBindingClass() {
		return Double.class;
	}

	@Override
	public FloatStaticBinding clone() {
		FloatStaticBinding returned = new FloatStaticBinding();
		returned.setsWith(this);
		return returned;
	}

}
