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

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.view.action.EditionSchemeAction;

public class DeleteAction<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends EditionAction<M, MM, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(DeleteAction.class.getPackage().getName());

	private DataBinding<Object> object;

	public DeleteAction(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.DeleteAction;
	}

	public Object getDeclaredObject(EditionSchemeAction action) {
		try {
			return getObject().getBindingValue(action);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
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
	public String getStringRepresentation() {
		return "Delete " + getObject();
	}

	public PatternRole getPatternRole() {
		if (getEditionPattern() == null) {
			return null;
		}
		return getEditionPattern().getPatternRole(getObject().toString());
	}

	public static class ObjectToDeleteBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<DeleteAction> {
		public ObjectToDeleteBindingIsRequiredAndMustBeValid() {
			super("'object_to_delete'_binding_is_not_valid", DeleteAction.class);
		}

		@Override
		public DataBinding<Object> getBinding(DeleteAction object) {
			return object.getObject();
		}

	}

	@Override
	public FlexoModelObject performAction(EditionSchemeAction action) {
		FlexoModelObject objectToDelete = null;
		try {
			objectToDelete = (FlexoModelObject) getObject().getBindingValue(action);
		} catch (TypeMismatchException e1) {
			e1.printStackTrace();
		} catch (NullReferenceException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
		if (objectToDelete == null) {
			return null;
		}
		try {
			logger.info("Delete object " + objectToDelete + " for object " + getObject() + " this=" + this);
			objectToDelete.delete();
		} catch (Exception e) {
			logger.warning("Unexpected exception occured during deletion: " + e.getMessage());
			e.printStackTrace();
		}
		return objectToDelete;
	}

	@Override
	public void finalizePerformAction(EditionSchemeAction action, FlexoModelObject initialContext) {
		// TODO Auto-generated method stub

	}

}
