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
package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.toolbox.StringUtils;

/**
 * Abstract class representing an EditionAction with the particularity of returning a value which can be assigned
 * 
 * @author sylvain
 * 
 */
public abstract class AssignableAction extends EditionAction {

	private static final Logger logger = Logger.getLogger(AssignableAction.class.getPackage().getName());

	private ViewPointDataBinding assignation;

	private BindingDefinition ASSIGNATION = new BindingDefinition("assignation", Object.class, BindingDefinitionType.GET_SET, false) {
		@Override
		public java.lang.reflect.Type getType() {
			return getAssignableType();
		};

		@Override
		public boolean getIsMandatory() {
			return isAssignationRequired();
		};
	};

	public AssignableAction() {
	}

	public boolean isAssignationRequired() {
		return false;
	}

	@Override
	public abstract EditionActionType getEditionActionType();

	public abstract Type getAssignableType();

	public BindingDefinition getAssignationBindingDefinition() {
		return ASSIGNATION;
	}

	public ViewPointDataBinding getAssignation() {
		if (assignation == null) {
			assignation = new ViewPointDataBinding(this, EditionActionBindingAttribute.assignation, getAssignationBindingDefinition());
		}
		return assignation;
	}

	public void setAssignation(ViewPointDataBinding assignation) {
		if (assignation != null) {
			assignation.setOwner(this);
			assignation.setBindingAttribute(EditionActionBindingAttribute.assignation);
			assignation.setBindingDefinition(getAssignationBindingDefinition());
		}
		this.assignation = assignation;
		notifyBindingChanged(this.assignation);
	}

	public PatternRole getPatternRole() {
		if (getEditionPattern() == null) {
			return null;
		}
		return getEditionPattern().getPatternRole(getAssignation().toString());
	}

	@Override
	public String getStringRepresentation() {
		return getClass().getSimpleName()
				+ (StringUtils.isNotEmpty(getAssignation().toString()) ? " (" + getAssignation().toString() + ")" : "");
	}

	@Deprecated
	public String _getPatternRoleName() {
		return getAssignation().toString();
	}

	@Deprecated
	public void _setPatternRoleName(String patternRole) {
		getAssignation().setUnparsedBinding(patternRole);
	}

	public static class AssignationBindingMustBeValid extends BindingMustBeValid<AssignableAction> {
		public AssignationBindingMustBeValid() {
			super("'assign'_binding_is_not_valid", AssignableAction.class);
		}

		@Override
		public ViewPointDataBinding getBinding(AssignableAction object) {
			return object.getAssignation();
		}

		@Override
		public BindingDefinition getBindingDefinition(AssignableAction object) {
			return object.getAssignationBindingDefinition();
		}

	}

}
