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
package org.openflexo.technologyadapter.diagram.model.action;

import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.InvalidArgumentException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.resource.InvalidFileNameException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramImpl;
import org.openflexo.technologyadapter.diagram.rm.DiagramResource;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;

/**
 * This action is called to create a new {@link Diagram} (a {@link VirtualModelInstance} in a {@link View}
 * 
 * @author sylvain
 */
public class CreateDiagram extends FlexoAction<CreateDiagram, FlexoObject, FlexoObject> {

	private static final Logger logger = Logger.getLogger(CreateDiagram.class.getPackage().getName());

	public static FlexoActionType<CreateDiagram, FlexoObject, FlexoObject> actionType = new FlexoActionType<CreateDiagram, FlexoObject, FlexoObject>(
			"create_diagram", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateDiagram makeNewAction(FlexoObject focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
			return new CreateDiagram(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoObject object, Vector<FlexoObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoObject object, Vector<FlexoObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(CreateDiagram.actionType, View.class);
	}

	private DiagramSpecification diagramSpecification;
	private String diagramName;
	private String diagramTitle;
	private String diagramURI;
	private File diagramFile;

	public File getDiagramFile() {
		return diagramFile;
	}

	public void setDiagramFile(File diagramFile) {
		this.diagramFile = diagramFile;
	}

	private DiagramResource diagramResource;

	CreateDiagram(FlexoObject focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws InvalidFileNameException, SaveResourceException, InvalidArgumentException {
		// TODO
		logger.info("Not implemented yet: Add diagram " + getFocusedObject());

		/*	newVirtualModelInstanceName = JavaUtils.getClassName(newVirtualModelInstanceName);

			if (StringUtils.isNotEmpty(newVirtualModelInstanceName) && StringUtils.isEmpty(newVirtualModelInstanceTitle)) {
				newVirtualModelInstanceTitle = newVirtualModelInstanceName;
			}

			if (StringUtils.isEmpty(newVirtualModelInstanceName)) {
				throw new InvalidParameterException("virtual model instance name is undefined");
			}

			int index = 1;
			String baseName = newVirtualModelInstanceName;
			while (!getFocusedObject().isValidVirtualModelName(newVirtualModelInstanceName)) {
				newVirtualModelInstanceName = baseName + index;
				index++;
			}

			VirtualModelInstanceResource newVirtualModelInstanceResource = makeVirtualModelInstanceResource();

			newVirtualModelInstance = newVirtualModelInstanceResource.getVirtualModelInstance();

			logger.info("Added virtual model instance " + newVirtualModelInstance + " in view " + getFocusedObject());

			System.out.println("OK, we have created the file " + newVirtualModelInstanceResource.getFile().getAbsolutePath());

			for (ModelSlot ms : virtualModel.getModelSlots()) {
				ModelSlotInstanceConfiguration<?, ?> configuration = getModelSlotInstanceConfiguration(ms);
				if (configuration.isValidConfiguration()) {
					newVirtualModelInstance.addToModelSlotInstances(configuration.createModelSlotInstance(newVirtualModelInstance));
				} else {
					throw new InvalidArgumentException("Wrong configuration for model slot " + configuration.getModelSlot() + " configuration="
							+ configuration);
				}
			}

			if (creationSchemeAction != null) {
				creationSchemeAction.initWithEditionPatternInstance(newVirtualModelInstance);
				creationSchemeAction.doAction();
			}

			System.out.println("Now, we try to synchronize the new virtual model instance");

			if (newVirtualModelInstance.isSynchronizable()) {
				System.out.println("Go for it");
				newVirtualModelInstance.synchronize(null);
			}

			System.out.println("Saving file again...");
			newVirtualModelInstanceResource.save(null);*/
	}

	public DiagramResource makeDiagramResource() throws InvalidFileNameException, SaveResourceException {
		return DiagramImpl.newDiagramResource(getDiagramName(), getDiagramTitle(), getDiagramURI(), getDiagramFile(),
				getDiagramSpecification(), getServiceManager());
	}

	private String errorMessage;

	public String getErrorMessage() {
		isValid();
		// System.out.println("valid=" + isValid());
		// System.out.println("errorMessage=" + errorMessage);
		return errorMessage;
	}

	@Override
	public boolean isValid() {
		if (diagramSpecification == null) {
			errorMessage = noDiagramSpecificationSelectedMessage();
			return false;
		}
		if (StringUtils.isEmpty(diagramName)) {
			errorMessage = noNameMessage();
			return false;
		}

		if (!diagramName.equals(JavaUtils.getClassName(diagramName)) && !diagramName.equals(JavaUtils.getVariableName(diagramName))) {
			errorMessage = invalidNameMessage();
			return false;
		}

		if (StringUtils.isEmpty(diagramTitle)) {
			errorMessage = noTitleMessage();
			return false;
		}

		if (getDiagramFile() == null) {

		}

		/*if (getFocusedObject().getVirtualModelInstance(diagramName) != null) {
			errorMessage = duplicatedNameMessage();
			return false;
		}*/
		// TODO: handle duplicated name and uri
		return true;
	}

	public String noDiagramSpecificationSelectedMessage() {
		return FlexoLocalization.localizedForKey("no_diagram_type_selected");
	}

	public String noTitleMessage() {
		return FlexoLocalization.localizedForKey("no_diagram_title_defined");
	}

	public String noFileMessage() {
		return FlexoLocalization.localizedForKey("no_diagram_file_defined");
	}

	public String existingFileMessage() {
		return FlexoLocalization.localizedForKey("file_already_existing");
	}

	public String noNameMessage() {
		return FlexoLocalization.localizedForKey("no_diagram_name_defined");
	}

	public String invalidNameMessage() {
		return FlexoLocalization.localizedForKey("invalid_name_for_new_diagram");
	}

	public String duplicatedNameMessage() {
		return FlexoLocalization.localizedForKey("a_diagram_with_that_name_already_exists");
	}

	public DiagramSpecification getDiagramSpecification() {
		return diagramSpecification;
	}

	public void setDiagramSpecification(DiagramSpecification diagramSpecification) {
		this.diagramSpecification = diagramSpecification;
	}

	public Diagram getNewDiagram() {
		if (getNewDiagramResource() != null) {
			return getNewDiagramResource().getDiagram();
		}
		return null;
	}

	public DiagramResource getNewDiagramResource() {
		return diagramResource;
	}

	public String getDiagramName() {
		return diagramName;
	}

	public void setDiagramName(String diagramName) {
		this.diagramName = diagramName;
	}

	public String getDiagramTitle() {
		return diagramTitle;
	}

	public void setDiagramTitle(String diagramTitle) {
		this.diagramTitle = diagramTitle;
	}

	public String getDiagramURI() {
		return diagramURI;
	}

	public void setDiagramURI(String diagramURI) {
		this.diagramURI = diagramURI;
	}
}