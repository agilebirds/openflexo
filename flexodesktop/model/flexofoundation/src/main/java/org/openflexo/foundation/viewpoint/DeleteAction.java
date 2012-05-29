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

import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class DeleteAction extends EditionAction {

	private static final Logger logger = Logger.getLogger(DeleteAction.class.getPackage().getName());

	public DeleteAction() {
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.DeleteAction;
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
	public String getStringRepresentation() {
		return "Delete " + getObject();
	}

	public static class ObjectToDeleteBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<DeleteAction> {
		public ObjectToDeleteBindingIsRequiredAndMustBeValid() {
			super("'object_to_delete'_binding_is_not_valid", DeleteAction.class);
		}

		@Override
		public ViewPointDataBinding getBinding(DeleteAction object) {
			return object.getObject();
		}

		@Override
		public BindingDefinition getBindingDefinition(DeleteAction object) {
			return object.getObjectBindingDefinition();
		}

	}

}
