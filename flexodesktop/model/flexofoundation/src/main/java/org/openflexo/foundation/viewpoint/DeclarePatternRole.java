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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class DeclarePatternRole<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, T> extends
		AssignableAction<M, MM, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(DeclarePatternRole.class.getPackage().getName());

	public DeclarePatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.DeclarePatternRole;
	}

	/*@Override
	public List<PatternRole> getAvailablePatternRoles() {
		return getEditionPattern().getPatternRoles();
	}*/

	@Override
	public String getInspectorName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAssignationRequired() {
		return true;
	}

	public Object getDeclaredObject(EditionSchemeAction action) {
		return getObject().getBindingValue(action);
	}

	private ViewPointDataBinding object;

	private BindingDefinition OBJECT = new BindingDefinition("object", Object.class, BindingDefinitionType.GET, true);

	public BindingDefinition getObjectBindingDefinition() {
		return OBJECT;
	}

	public ViewPointDataBinding getObject() {
		if (object == null) {
			object = new ViewPointDataBinding(this, EditionActionBindingAttribute.object, getObjectBindingDefinition());
		}
		return object;
	}

	public void setObject(ViewPointDataBinding object) {
		object.setOwner(this);
		object.setBindingAttribute(EditionActionBindingAttribute.object);
		object.setBindingDefinition(getObjectBindingDefinition());
		this.object = object;
	}

	@Override
	public Type getAssignableType() {
		return Object.class;
	}

	public static class AssignationBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<DeclarePatternRole> {
		public AssignationBindingIsRequiredAndMustBeValid() {
			super("'assign'_binding_is_not_valid", DeclarePatternRole.class);
		}

		@Override
		public ViewPointDataBinding getBinding(DeclarePatternRole object) {
			return object.getAssignation();
		}

		@Override
		public BindingDefinition getBindingDefinition(DeclarePatternRole object) {
			return object.getAssignationBindingDefinition();
		}

	}

	public static class ObjectBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<DeclarePatternRole> {
		public ObjectBindingIsRequiredAndMustBeValid() {
			super("'object'_binding_is_not_valid", DeclarePatternRole.class);
		}

		@Override
		public ViewPointDataBinding getBinding(DeclarePatternRole object) {
			return object.getObject();
		}

		@Override
		public BindingDefinition getBindingDefinition(DeclarePatternRole object) {
			return object.getObjectBindingDefinition();
		}

	}

	@Override
	public FlexoModelObject performAction(EditionSchemeAction action) {
		return (FlexoModelObject) getDeclaredObject(action);
	}

	@Override
	public void finalizePerformAction(EditionSchemeAction action, FlexoModelObject initialContext) {
		// TODO Auto-generated method stub

	}

}
