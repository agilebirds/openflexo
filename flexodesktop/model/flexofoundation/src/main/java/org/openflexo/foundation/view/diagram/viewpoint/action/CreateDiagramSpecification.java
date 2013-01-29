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
package org.openflexo.foundation.view.diagram.viewpoint.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramSpecification;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.toolbox.StringUtils;

public class CreateDiagramSpecification extends FlexoAction<CreateDiagramSpecification, ViewPoint, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(CreateDiagramSpecification.class.getPackage().getName());

	public static FlexoActionType<CreateDiagramSpecification, ViewPoint, ViewPointObject> actionType = new FlexoActionType<CreateDiagramSpecification, ViewPoint, ViewPointObject>(
			"create_diagram_specification", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateDiagramSpecification makeNewAction(ViewPoint focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
			return new CreateDiagramSpecification(focusedObject, globalSelection, editor);
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
		FlexoModelObject.addActionForClass(CreateDiagramSpecification.actionType, ViewPoint.class);
	}

	public String newDiagramSpecificationName;
	public String newDiagramSpecificationDescription;
	private DiagramSpecification newDiagramSpecification;

	CreateDiagramSpecification(ViewPoint focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws IOFlexoException {

		newDiagramSpecification = DiagramSpecification.newDiagramSpecification(newDiagramSpecificationName, getFocusedObject());
		newDiagramSpecification.setDescription(newDiagramSpecificationDescription);
		getFocusedObject().addToVirtualModels(newDiagramSpecification);

	}

	public boolean isNewVirtualModelNameValid() {
		if (StringUtils.isEmpty(newDiagramSpecificationName)) {
			errorMessage = "please_supply_valid_virtual_model_name";
			return false;
		}
		return true;
	}

	public String errorMessage;

	public boolean isValid() {
		if (!isNewVirtualModelNameValid()) {
			return false;
		}
		return true;
	}

	public DiagramSpecification getNewDiagramSpecification() {
		return newDiagramSpecification;
	}

}
