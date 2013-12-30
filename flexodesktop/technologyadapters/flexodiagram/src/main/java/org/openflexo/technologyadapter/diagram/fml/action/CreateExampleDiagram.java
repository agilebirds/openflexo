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
package org.openflexo.technologyadapter.diagram.fml.action;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.resource.InvalidFileNameException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.model.DiagramImpl;
import org.openflexo.technologyadapter.diagram.rm.DiagramResource;
import org.openflexo.toolbox.StringUtils;

public class CreateExampleDiagram extends FlexoAction<CreateExampleDiagram, DiagramSpecification, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(CreateExampleDiagram.class.getPackage().getName());

	public static FlexoActionType<CreateExampleDiagram, DiagramSpecification, ViewPointObject> actionType = new FlexoActionType<CreateExampleDiagram, DiagramSpecification, ViewPointObject>(
			"create_example_diagram", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateExampleDiagram makeNewAction(DiagramSpecification focusedObject, Vector<ViewPointObject> globalSelection,
				FlexoEditor editor) {
			return new CreateExampleDiagram(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DiagramSpecification object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(DiagramSpecification object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(CreateExampleDiagram.actionType, DiagramSpecification.class);
	}

	public String newDiagramName;
	public String newDiagramTitle;
	public String description;
	public DrawingGraphicalRepresentation graphicalRepresentation;

	private DiagramResource newDiagramResource;

	CreateExampleDiagram(DiagramSpecification focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws NotImplementedException, InvalidParameterException, SaveResourceException,
			InvalidFileNameException {
		logger.info("Add example diagram");

		String newDiagramURI = getFocusedObject().getURI() + "/" + newDiagramName;
		File newDiagramFile = new File(getFocusedObject().getResource().getDirectory(), newDiagramName + DiagramResource.DIAGRAM_SUFFIX);
		newDiagramResource = DiagramImpl.newDiagramResource(newDiagramName, newDiagramTitle, newDiagramURI, newDiagramFile,
				getFocusedObject(), getServiceManager());
		newDiagramResource.getDiagram().setDescription(description);
		newDiagramResource.save(null);

	}

	private String nameValidityMessage = EMPTY_NAME;

	private static final String NAME_IS_VALID = FlexoLocalization.localizedForKey("name_is_valid");
	private static final String DUPLICATED_NAME = FlexoLocalization.localizedForKey("this_name_is_already_used_please_choose_an_other_one");
	private static final String EMPTY_NAME = FlexoLocalization.localizedForKey("empty_name");

	public String getNameValidityMessage() {
		return nameValidityMessage;
	}

	public boolean isNameValid() {
		if (StringUtils.isEmpty(newDiagramName)) {
			nameValidityMessage = EMPTY_NAME;
			return false;
		} else if (getFocusedObject().getExampleDiagram(newDiagramName) != null) {
			nameValidityMessage = DUPLICATED_NAME;
			return false;
		} else {
			nameValidityMessage = NAME_IS_VALID;
			return true;
		}
	}

}