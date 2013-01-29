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

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.toolbox.StringUtils;

/**
 * Abstract class representing an EditionAction with the particularity of returning a value which can be assigned
 * 
 * @author sylvain
 * 
 */
public abstract class AssignableAction<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, T> extends EditionAction<M, MM, T> {

	private static final Logger logger = Logger.getLogger(AssignableAction.class.getPackage().getName());

	private DataBinding<Object> assignation;

	public AssignableAction(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	public boolean isAssignationRequired() {
		return false;
	}

	@Override
	public abstract EditionActionType getEditionActionType();

	public abstract Type getAssignableType();

	public DataBinding<Object> getAssignation() {
		if (assignation == null) {
			assignation = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.GET_SET);
			assignation.setDeclaredType(getAssignableType());
			assignation.setBindingName("assignation");
			assignation.setMandatory(isAssignationRequired());
		}
		assignation.setDeclaredType(getAssignableType());
		return assignation;
	}

	public void setAssignation(DataBinding<Object> assignation) {
		if (assignation != null) {
			assignation.setOwner(this);
			assignation.setBindingName("assignation");
			assignation.setDeclaredType(getAssignableType());
			assignation.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET_SET);
			assignation.setMandatory(isAssignationRequired());
		}
		this.assignation = assignation;
		notifiedBindingChanged(this.assignation);
	}

	public PatternRole getPatternRole() {
		if (getEditionPattern() == null) {
			return null;
		}
		if (assignation != null) {
			return getEditionPattern().getPatternRole(assignation.toString());
		}
		return null;
	}

	@Override
	public String getStringRepresentation() {
		return getClass().getSimpleName()
				+ (StringUtils.isNotEmpty(getAssignation().toString()) ? " (" + getAssignation().toString() + ")" : "");
	}

	/*@Deprecated
	public String _getPatternRoleName() {
		return getAssignation().toString();
	}

	@Deprecated
	public void _setPatternRoleName(String patternRole) {
		getAssignation().setUnparsedBinding(patternRole);
	}*/

	public static class AssignationBindingMustBeValid extends BindingMustBeValid<AssignableAction> {
		public AssignationBindingMustBeValid() {
			super("'assign'_binding_is_not_valid", AssignableAction.class);
		}

		@Override
		public DataBinding<Object> getBinding(AssignableAction object) {
			return object.getAssignation();
		}

	}

}
