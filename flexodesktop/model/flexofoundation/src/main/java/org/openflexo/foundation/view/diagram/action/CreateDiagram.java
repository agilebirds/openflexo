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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.foundation.view.diagram.rm.DiagramResource;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramSpecification;
import org.openflexo.localization.FlexoLocalization;

/**
 * This action is called to create a new {@link Diagram} (a {@link VirtualModelInstance} in a {@link View}
 * 
 * @author sylvain
 */
public class CreateDiagram extends CreateVirtualModelInstance<CreateDiagram> {

	private static final Logger logger = Logger.getLogger(CreateDiagram.class.getPackage().getName());

	public static FlexoActionType<CreateDiagram, View, FlexoModelObject> actionType = new FlexoActionType<CreateDiagram, View, FlexoModelObject>(
			"create_diagram", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateDiagram makeNewAction(View focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			return new CreateDiagram(focusedObject, globalSelection, editor);
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
		FlexoModelObject.addActionForClass(CreateDiagram.actionType, View.class);
	}

	CreateDiagram(View focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public DiagramResource makeVirtualModelInstanceResource() throws InvalidFileNameException, SaveResourceException {
		return Diagram.newDiagramResource(getNewVirtualModelInstanceName(), getNewVirtualModelInstanceTitle(), getDiagramSpecification(),
				getFocusedObject(), getServiceManager().getXMLSerializationService().getDiagramFactory());
	}

	@Override
	public String noVirtualModelSelectedMessage() {
		return FlexoLocalization.localizedForKey("no_diagram_type_selected");
	}

	@Override
	public String noTitleMessage() {
		return FlexoLocalization.localizedForKey("no_diagram_title_defined");
	}

	@Override
	public String noNameMessage() {
		return FlexoLocalization.localizedForKey("no_diagram_name_defined");
	}

	@Override
	public String invalidNameMessage() {
		return FlexoLocalization.localizedForKey("invalid_name_for_new_diagram");
	}

	@Override
	public String duplicatedNameMessage() {
		return FlexoLocalization.localizedForKey("a_diagram_with_that_name_already_exists");
	}

	public DiagramSpecification getDiagramSpecification() {
		return (DiagramSpecification) getVirtualModel();
	}

	public void setDiagramSpecification(DiagramSpecification diagramSpecification) {
		setVirtualModel(diagramSpecification);
	}

	public Diagram getNewDiagram() {
		return (Diagram) super.getNewVirtualModelInstance();
	}
}