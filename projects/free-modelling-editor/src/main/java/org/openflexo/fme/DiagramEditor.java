/*
 * (c) Copyright 2010-2013 AgileBirds
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
package org.openflexo.fme;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.jdom2.JDOMException;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fme.dialog.CreateConceptAndInstanceDialog;
import org.openflexo.fme.dialog.CreateConceptDialog;
import org.openflexo.fme.dialog.CreateInstanceDialog;
import org.openflexo.fme.model.Concept;
import org.openflexo.fme.model.Diagram;
import org.openflexo.fme.model.DiagramElement;
import org.openflexo.fme.model.DiagramFactory;
import org.openflexo.fme.model.Instance;
import org.openflexo.fme.model.PropertyValue;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.undo.CompoundEdit;
import org.openflexo.toolbox.FileResource;

public class DiagramEditor {

	private static final Logger logger = FlexoLogger.getLogger(DiagramEditor.class.getPackage().getName());

	private Diagram diagram;
	private DiagramDrawing drawing;
	private DianaDrawingEditor controller;
	private int index;
	private File file = null;
	private DiagramFactory factory;
	private FreeModellingEditorApplication application;

	private static final File NEW_CONCEPT_NEW_INSTANCE_DIALOG = new FileResource("Fib/Dialog/NewConceptNewInstanceDialog.fib");
	private static final File NEW_CONCEPT_DIALOG = new FileResource("Fib/Dialog/NewConceptDialog.fib");
	private static final File NEW_INSTANCE_DIALOG = new FileResource("Fib/Dialog/NewInstanceDialog.fib");

	public static DiagramEditor newDiagramEditor(DiagramFactory factory, FreeModellingEditorApplication application) {

		DiagramEditor returned = new DiagramEditor(factory, application);
		returned.diagram = factory.makeNewDiagram();
		return returned;

	}

	public static DiagramEditor loadDiagramEditor(File file, DiagramFactory factory, FreeModellingEditorApplication application) {
		logger.info("Loading " + file);

		DiagramEditor returned = new DiagramEditor(factory, application);

		try {
			returned.diagram = (Diagram) factory.deserialize(new FileInputStream(file));
			returned.file = file;
			System.out.println("Loaded " + factory.stringRepresentation(returned.diagram));
			return returned;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		} catch (InvalidDataException e) {
			e.printStackTrace();
		}
		return null;

		/*XMLDecoder decoder = new XMLDecoder(mapping, new DrawingBuilder());

		try {
			DiagramImpl drawing = (DiagramImpl) decoder.decodeObject(new FileInputStream(file));
			drawing.file = file;
			drawing.editedDrawing.init(factory);
			logger.info("Succeeded to load: " + file);
			return drawing;
		} catch (Exception e) {
			logger.warning("Failed to load: " + file + " unexpected exception: " + e.getMessage());
			e.printStackTrace();
			return null;
		}*/
	}

	private DiagramEditor(DiagramFactory factory, FreeModellingEditorApplication application) {
		this.factory = factory;
		this.application = application;
	}

	public Diagram getDiagram() {
		return diagram;
	}

	public DiagramDrawing getDrawing() {
		if (drawing == null) {
			drawing = new DiagramDrawing(getDiagram(), factory);
		}
		return drawing;
	}

	public DianaDrawingEditor getController() {
		if (controller == null) {
			CompoundEdit edit = factory.getUndoManager().startRecording("Initialize diagram");
			controller = new DianaDrawingEditor(getDrawing(), factory, this);
			factory.getUndoManager().stopRecording(edit);
		}
		return controller;
	}

	public String getTitle() {
		if (file != null) {
			return file.getName();
		} else {
			return FlexoLocalization.localizedForKey(FreeModellingEditorApplication.LOCALIZATION, "untitled") + "-" + index;
		}
	}

	public boolean save() {
		System.out.println("Saving " + file);

		try {
			factory.serialize(diagram, new FileOutputStream(file));
			System.out.println("Saved " + file.getAbsolutePath());
			System.out.println(factory.stringRepresentation(diagram));
			return true;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

		/*XMLCoder coder = new XMLCoder(mapping);

		try {
			coder.encodeObject(this, new FileOutputStream(file));
			clearChanged();
			logger.info("Succeeded to save: " + file);
			System.out.println("> " + new XMLCoder(mapping).encodeObject(this));
			System.out.println("Et j'ai ca aussi: " + getFactory().getSerializer().serializeAsString(this));
			return true;
		} catch (Exception e) {
			logger.warning("Failed to save: " + file + " unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
				return false;
		 */

	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public FreeModellingEditorApplication getApplication() {
		return application;
	}

	@Override
	public String toString() {
		return "DiagramEditor:" + getTitle();
	}

	public DiagramFactory getFactory() {
		return getApplication().getFactory();
	}

	public PropertyValue createPropertyValue(DiagramElement<?, ?> element) {
		PropertyValue returned = getFactory().newInstance(PropertyValue.class);
		returned.setKey("property");
		element.addToPropertyValues(returned);
		return returned;
	}

	public Concept createNewConcept() {
		CreateConceptDialog dialogData = new CreateConceptDialog(getDiagram().getDataModel());
		FIBDialog dialog = FIBDialog.instanciateAndShowDialog(NEW_CONCEPT_DIALOG, dialogData, application.getFrame(), true,
				application.LOCALIZATION);
		if (dialog.getStatus() == Status.VALIDATED) {
			Concept returned = getFactory().newInstance(Concept.class);
			returned.setName(dialogData.getConceptName());
			System.out.println("Created " + returned);
			getDiagram().getDataModel().addToConcepts(returned);
			return returned;
		}
		return null;
	}

	public Instance createNewConceptAndNewInstance(DiagramElement<?, ?> diagramElement) {
		CreateConceptAndInstanceDialog dialogData = new CreateConceptAndInstanceDialog(getDiagram().getDataModel(),
				diagramElement.getName());
		FIBDialog dialog = FIBDialog.instanciateAndShowDialog(NEW_CONCEPT_NEW_INSTANCE_DIALOG, dialogData, application.getFrame(), true,
				application.LOCALIZATION);
		if (dialog.getStatus() == Status.VALIDATED) {
			Concept concept = getFactory().newInstance(Concept.class);
			concept.setName(dialogData.getConceptName());
			System.out.println("Created " + concept);
			getDiagram().getDataModel().addToConcepts(concept);
			Instance returned = getFactory().newInstance(Instance.class);
			returned.setName(dialogData.getInstanceName());
			returned.setConcept(concept);
			System.out.println("Created " + returned);
			concept.addToInstances(returned);
			diagramElement.setName(dialogData.getInstanceName());
			return returned;
		}
		return null;
	}

	public Instance createNewInstance(DiagramElement<?, ?> diagramElement) {
		CreateInstanceDialog dialogData = new CreateInstanceDialog(getDiagram().getDataModel(), diagramElement.getName());
		FIBDialog dialog = FIBDialog.instanciateAndShowDialog(NEW_INSTANCE_DIALOG, dialogData, application.getFrame(), true,
				application.LOCALIZATION);
		if (dialog.getStatus() == Status.VALIDATED) {
			Instance returned = getFactory().newInstance(Instance.class);
			returned.setName(dialogData.getInstanceName());
			returned.setConcept(dialogData.getConcept());
			System.out.println("Created " + returned);
			dialogData.getConcept().addToInstances(returned);
			diagramElement.setName(dialogData.getInstanceName());
			return returned;
		}
		return null;
	}

	public void delete(List<DiagramElement<?, ?>> objectsToDelete) {
		System.out.println("Deleting objects: " + objectsToDelete);
		Object[] context = objectsToDelete.toArray(new Object[objectsToDelete.size()]);
		for (DiagramElement<?, ?> o : objectsToDelete) {
			o.delete(context);
		}
	}

}
