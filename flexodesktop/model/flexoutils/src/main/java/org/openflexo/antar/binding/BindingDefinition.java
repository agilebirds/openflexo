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
import java.text.Collator;
import java.util.Comparator;
import java.util.Observable;
import java.util.logging.Logger;

import org.openflexo.localization.FlexoLocalization;

/**
 * Represents the specification of a DataBinding
 * 
 * @author sguerin
 * 
 */
public class BindingDefinition extends Observable {

	static final Logger logger = Logger.getLogger(BindingDefinition.class.getPackage().getName());

	private String variableName;

	private Type type;

	private boolean isMandatory;

	private BindingDefinitionType _bindingDefinitionType = BindingDefinitionType.GET;

	public static enum BindingDefinitionType {
		GET, SET, GET_SET, EXECUTE
	}

	public BindingDefinition(String variableName, Type type, BindingDefinitionType bindingType, boolean mandatory) {
		super();
		this.variableName = variableName;
		this.type = type;
		isMandatory = mandatory;
		_bindingDefinitionType = bindingType;
	}

	@Override
	public int hashCode() {
		return (variableName == null ? 0 : variableName.hashCode()) + (type == null ? 0 : type.hashCode());
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof BindingDefinition) {
			BindingDefinition bd = (BindingDefinition) object;
			if (variableName == null) {
				if (bd.variableName != null) {
					return false;
				}
			} else {
				if (!variableName.equals(bd.variableName)) {
					return false;
				}
			}
			return type == bd.type && isMandatory == bd.isMandatory;
		} else {
			return super.equals(object);
		}
	}

	public boolean getIsMandatory() {
		return isMandatory;
	}

	public void setIsMandatory(boolean mandatory) {
		isMandatory = mandatory;
	}

	public boolean getIsSettable() {
		return getBindingDefinitionType() == BindingDefinitionType.SET || getBindingDefinitionType() == BindingDefinitionType.GET_SET;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public static final Comparator<BindingDefinition> bindingDefinitionComparator = new BindingDefinitionComparator();

	/**
	 * Used to sort binding definition according to name alphabetic ordering
	 * 
	 * @author sguerin
	 * 
	 */
	public static class BindingDefinitionComparator implements Comparator<BindingDefinition> {

		BindingDefinitionComparator() {

		}

		/**
		 * Implements
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(BindingDefinition o1, BindingDefinition o2) {
			String s1 = o1.getVariableName();
			String s2 = o2.getVariableName();
			if (s1 != null && s2 != null) {
				return Collator.getInstance().compare(s1, s2);
			} else {
				return 0;
			}
		}

	}

	public BindingDefinitionType getBindingDefinitionType() {
		return _bindingDefinitionType;
	}

	public void setBindingDefinitionType(BindingDefinitionType bdType) {
		_bindingDefinitionType = bdType;
	}

	public String getTypeStringRepresentation() {
		if (getType() == null) {
			return FlexoLocalization.localizedForKey("no_type");
		} else {
			return TypeUtils.simpleRepresentation(getType());
		}
	}

	@Override
	public String toString() {
		return "BindingDefinition[name=" + variableName + ",type=" + type + ",mandatory=" + isMandatory + ",kind=" + _bindingDefinitionType
				+ "]";
	}

	public void notifyBindingDefinitionTypeChanged() {
		setChanged();
		notifyObservers(new BindingDefinitionTypeChanged());
	}
}