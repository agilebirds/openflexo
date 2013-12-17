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
package org.openflexo.foundation.viewpoint.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.toolbox.StringUtils;

public class CreateVirtualModel extends FlexoAction<CreateVirtualModel, ViewPoint, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(CreateVirtualModel.class.getPackage().getName());

	public static FlexoActionType<CreateVirtualModel, ViewPoint, ViewPointObject> actionType = new FlexoActionType<CreateVirtualModel, ViewPoint, ViewPointObject>(
			"create_virtual_model", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateVirtualModel makeNewAction(ViewPoint focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
			return new CreateVirtualModel(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ViewPoint object, Vector<ViewPointObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(ViewPoint object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObject.addActionForClass(CreateVirtualModel.actionType, ViewPoint.class);
	}

	public String newVirtualModelName;
	public String newVirtualModelDescription;
	private VirtualModel newVirtualModel;

	public Vector<IFlexoOntology> importedOntologies = new Vector<IFlexoOntology>();

	// private boolean createsOntology = false;

	CreateVirtualModel(ViewPoint focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws IOFlexoException {

		newVirtualModel = VirtualModel.newVirtualModel(newVirtualModelName, getFocusedObject());
		newVirtualModel.setDescription(newVirtualModelDescription);
		getFocusedObject().addToVirtualModels(newVirtualModel);

	}

	public boolean isNewVirtualModelNameValid() {
		if (StringUtils.isEmpty(newVirtualModelName)) {
			errorMessage = "please_supply_valid_virtual_model_name";
			return false;
		}
		return true;
	}

	public String errorMessage;

	@Override
	public boolean isValid() {
		if (!isNewVirtualModelNameValid()) {
			return false;
		}
		return true;
	}

	public VirtualModel getNewVirtualModel() {
		return newVirtualModel;
	}

}
