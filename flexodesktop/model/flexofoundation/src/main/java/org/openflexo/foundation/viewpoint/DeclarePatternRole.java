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
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public class DeclarePatternRole<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, T> extends
		AssignableAction<M, MM, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(DeclarePatternRole.class.getPackage().getName());

	private DataBinding<Object> object;

	public DeclarePatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.DeclarePatternRole;
	}

	@Override
	public boolean isAssignationRequired() {
		return true;
	}

	public Object getDeclaredObject(EditionSchemeAction action) {
		try {
			return getObject().getBindingValue(action);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		}
		return null;
	}

	public DataBinding<Object> getObject() {
		if (object == null) {
			object = new DataBinding<Object>(this, Object.class, BindingDefinitionType.GET);
			object.setBindingName("object");
		}
		return object;
	}

	public void setObject(DataBinding<Object> object) {
		if (object != null) {
			object.setOwner(this);
			object.setBindingName("object");
			object.setDeclaredType(Object.class);
			object.setBindingDefinitionType(BindingDefinitionType.GET);
		}
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
		public DataBinding<Object> getBinding(DeclarePatternRole object) {
			return object.getAssignation();
		}

	}

	public static class ObjectBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<DeclarePatternRole> {
		public ObjectBindingIsRequiredAndMustBeValid() {
			super("'object'_binding_is_not_valid", DeclarePatternRole.class);
		}

		@Override
		public DataBinding<Object> getBinding(DeclarePatternRole object) {
			return object.getObject();
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
