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
import org.openflexo.antar.java.JavaExpressionPrettyPrinter;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.toolbox.Duration;

public class DurationStaticBinding extends StaticBinding<Duration> {

	private Duration value;

	public DurationStaticBinding() {
		super();
	}

	public DurationStaticBinding(Duration aValue) {
		super();
		value = aValue.clone();
	}

	public DurationStaticBinding(BindingDefinition bindingDefinition, FlexoModelObject owner, Duration aValue) {
		super(bindingDefinition, owner);
		if (aValue != null) {
			value = aValue.clone();
		}
	}

	@Override
	public EvaluationType getEvaluationType() {
		return EvaluationType.DURATION;
	}

	@Override
	public String getStringRepresentation() {
		if (value != null) {
			return "[" + value.getSerializationRepresentation() + "]";
		}
		return "[null]";
	}

	@Override
	public String getWodStringRepresentation() {
		logger.severe("duration in wod files isn't supported yet");
		return "\"duration in wod files isn't supported yet\"";
	}

	@Override
	public Duration getValue() {
		return value;
	}

	@Override
	public void setValue(Duration aValue) {
		value = aValue.clone();
	}

	@Override
	public Class<Duration> getStaticBindingClass() {
		return Duration.class;
	}

	@Override
	public DurationStaticBinding clone() {
		DurationStaticBinding returned = new DurationStaticBinding();
		returned.setsWith(this);
		return returned;
	}

	@Override
	public String getJavaCodeStringRepresentation() {
		return JavaExpressionPrettyPrinter.getJavaStringRepresentation(getValue());
	}

}
