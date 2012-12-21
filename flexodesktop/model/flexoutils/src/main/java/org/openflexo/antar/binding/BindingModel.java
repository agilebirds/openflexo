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

import java.util.List;
import java.util.Vector;

/**
 * A binding model represents a set of BindingVariable, which are variables accessible in the context of which this binding model is
 * declared
 * 
 * @author sguerin
 * 
 */
public class BindingModel {

	private List<BindingVariable> _bindingVariables;

	public BindingModel() {
		super();
		_bindingVariables = new Vector<BindingVariable>();
	}

	public BindingModel(BindingModel mainBindingModel) {
		this();
		if (mainBindingModel != null) {
			for (int i = 0; i < mainBindingModel.getBindingVariablesCount(); i++) {
				addToBindingVariables(mainBindingModel.getBindingVariableAt(i));
			}
		}
	}

	public void clear() {
		_bindingVariables.clear();
	}

	public int getBindingVariablesCount() {
		return _bindingVariables.size();
	}

	public BindingVariable getBindingVariableAt(int index) {
		return _bindingVariables.get(index);
	}

	public void addToBindingVariables(BindingVariable variable) {
		_bindingVariables.add(variable);
	}

	public void removeFromBindingVariables(BindingVariable variable) {
		_bindingVariables.remove(variable);
	}

	public BindingVariable bindingVariableNamed(String variableName) {
		for (int i = 0; i < getBindingVariablesCount(); i++) {
			BindingVariable next = getBindingVariableAt(i);
			if (next != null && next.getVariableName() != null && next.getVariableName().equals(variableName)) {
				return next;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "BindingModel: " + _bindingVariables;
	}
}
