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
package org.openflexo.foundation.view.diagram.action;

import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.foundation.view.diagram.rm.DiagramResource;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramSpecification;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;

public class AddDiagram extends FlexoAction<AddDiagram, View, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(AddDiagram.class.getPackage().getName());

	public static FlexoActionType<AddDiagram, View, FlexoModelObject> actionType = new FlexoActionType<AddDiagram, View, FlexoModelObject>(
			"create_diagram", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddDiagram makeNewAction(View focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			return new AddDiagram(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(View object, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(View object, Vector<FlexoModelObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(AddDiagram.actionType, View.class);
	}

	private Diagram newDiagram;

	public String newDiagramName;
	public String newDiagramTitle;
	public DiagramSpecification diagramSpecification;

	public boolean skipChoosePopup = false;

	AddDiagram(View focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParameterException,
			InvalidFileNameException {
		logger.info("Add diagram in view " + getFocusedObject());

		if (StringUtils.isNotEmpty(newDiagramTitle) && StringUtils.isEmpty(newDiagramName)) {
			newDiagramName = JavaUtils.getClassName(newDiagramTitle);
		}

		if (StringUtils.isNotEmpty(newDiagramName) && StringUtils.isEmpty(newDiagramTitle)) {
			newDiagramTitle = newDiagramName;
		}

		if (StringUtils.isEmpty(newDiagramName)) {
			throw new InvalidParameterException("view name is undefined");
		}

		int index = 1;
		String baseName = newDiagramName;
		while (!getFocusedObject().isValidVirtualModelName(newDiagramName)) {
			newDiagramName = baseName + index;
			index++;
		}

		DiagramResource newDiagramResource = Diagram.newDiagramResource(newDiagramName, newDiagramTitle, diagramSpecification,
				getFocusedObject());
		newDiagram = newDiagramResource.getDiagram();

		logger.info("Added diagram " + newDiagram + " in view " + getFocusedObject());
		// Creates the resource here
	}

	public String errorMessage;

	public boolean isValid() {
		if (diagramSpecification == null) {
			errorMessage = FlexoLocalization.localizedForKey("no_diagram_type_selected");
			return false;
		}
		if (StringUtils.isEmpty(newDiagramTitle)) {
			errorMessage = FlexoLocalization.localizedForKey("no_diagram_title_defined");
			return false;
		}

		String diagramName = newDiagramName;
		if (StringUtils.isNotEmpty(newDiagramTitle) && StringUtils.isEmpty(newDiagramName)) {
			diagramName = JavaUtils.getClassName(newDiagramTitle);
		}

		if (getFocusedObject().getVirtualModelInstance(diagramName) != null) {
			errorMessage = FlexoLocalization.localizedForKey("a_diagram_with_that_name_already_exists");
			return false;
		}
		return true;
	}

	public Diagram getNewDiagram() {
		return newDiagram;
	}
}