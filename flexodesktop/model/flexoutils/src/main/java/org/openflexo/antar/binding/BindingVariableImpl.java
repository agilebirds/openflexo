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
import java.util.Observable;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.toolbox.ToolBox;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class BindingVariableImpl<T> extends Observable implements BindingVariable<T> {

	private Bindable container;

	private String variableName;

	private Type type;

	public BindingVariableImpl(Bindable container, String variableName, Type type) {
		super();
		this.container = container;
		this.variableName = variableName;
		this.type = type;
	}

	@Override
	public Bindable getContainer() {
		return container;
	}

	@Override
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String aVariableName) {
		this.variableName = aVariableName;
	}

	@Override
	public String toString() {
		return getVariableName() + "/" + (getType() instanceof Class ? ((Class) getType()).getSimpleName() : getType());
	}

	@Override
	public Class<?> getDeclaringClass() {
		return getContainer().getClass();
	}

	@Override
	public String getSerializationRepresentation() {
		return variableName;
	}

	@Override
	public boolean isBindingValid() {
		return true;
	}

	@Override
	public String getLabel() {
		return getVariableName();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		String returned = "<html>";
		String resultingTypeAsString;
		if (getType() != null) {
			resultingTypeAsString = TypeUtils.simpleRepresentation(getType());
			resultingTypeAsString = ToolBox.replaceStringByStringInString("<", "&LT;", resultingTypeAsString);
			resultingTypeAsString = ToolBox.replaceStringByStringInString(">", "&GT;", resultingTypeAsString);
		} else {
			resultingTypeAsString = "???";
		}
		returned += "<p><b>" + resultingTypeAsString + " " + getVariableName() + "</b></p>";
		// returned +=
		// "<p><i>"+(bv.getDescription()!=null?bv.getDescription():FlexoLocalization.localizedForKey("no_description"))+"</i></p>";
		returned += "</html>";
		return returned;
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BindingVariable) {
			return getContainer() != null && getContainer().equals(((BindingVariable) obj).getContainer()) && getVariableName() != null
					&& getVariableName().equals(((BindingVariable) obj).getVariableName()) && getType() != null
					&& getType().equals(((BindingVariable) obj).getType());
		}
		return super.equals(obj);
	}

	@Override
	public T getBindingValue(Object target, BindingEvaluationContext context) {
		return (T) target;
	}

	@Override
	public void setBindingValue(T value, Object target, BindingEvaluationContext context) {
		// Not settable
	}
}
