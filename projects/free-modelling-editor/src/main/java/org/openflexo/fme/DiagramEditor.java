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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jdom2.JDOMException;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.model.listener.FIBSelectionListener;
import org.openflexo.fme.dialog.CreateConceptAndInstanceDialog;
import org.openflexo.fme.dialog.CreateConceptDialog;
import org.openflexo.fme.dialog.CreateInstanceDialog;
import org.openflexo.fme.model.Concept;
import org.openflexo.fme.model.ConceptGRAssociation;
import org.openflexo.fme.model.Diagram;
import org.openflexo.fme.model.DiagramElement;
import org.openflexo.fme.model.DiagramFactory;
import org.openflexo.fme.model.Instance;
import org.openflexo.fme.model.PropertyValue;
import org.openflexo.fme.model.Shape;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.undo.CompoundEdit;
import org.openflexo.toolbox.FileResource;

public class DiagramEditor implements FIBSelectionListener {

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

	public Shape createNewShape(DiagramElement container, ShapeGraphicalRepresentation graphicalRepresentation, FGEPoint dropLocation) {

		CompoundEdit edit = null;

		if (!getFactory().getUndoManager().isBeeingRecording()) {
			edit = getFactory().getUndoManager().startRecording("Dragging new Element");
		}

		Shape newShape = getFactory().makeNewShape(graphicalRepresentation, dropLocation, container.getDiagram());

		// Lets see about ConceptGRAssociation

		ConceptGRAssociation association = retrieveAssociation(newShape.getGraphicalRepresentation());
		if (association == null) {

			// No other association with same GR found

			association = getFactory().newInstance(ConceptGRAssociation.class);

			ShapeGraphicalRepresentation shapeGR = (ShapeGraphicalRepresentation) newShape.getGraphicalRepresentation().cloneObject();

			association.setGraphicalRepresentation(shapeGR);
			association.setConcept(getDiagram().getDataModel().getConceptNamed(Concept.NONE_CONCEPT));

			getDiagram().addToAssociations(association);
			// getApplication().getDynamicPaletteModel().addAssociation(association);
			getApplication().getDynamicPaletteModel().update();
		}

		else {

			// An association with same GR has been found, use it

		}

		String baseName = "concept";
		String instanceName = baseName;
		int i = 1;
		while (association.getConcept().getInstanceNamed(instanceName) != null) {
			instanceName = baseName + i;
			i++;
		}

		Instance instance = getFactory().newInstance(Instance.class);
		instance.setName(instanceName);
		instance.setConcept(association.getConcept());
		newShape.setInstance(instance);

		newShape.setAssociation(association);
		container.addToShapes(newShape);

		if (edit != null) {
			getFactory().getUndoManager().stopRecording(edit);
		}

		return newShape;
	}

	public Shape createNewShape(DiagramElement container, ConceptGRAssociation association, FGEPoint dropLocation) {

		CompoundEdit edit = null;

		if (!getFactory().getUndoManager().isBeeingRecording()) {
			edit = getFactory().getUndoManager().startRecording("Dragging new Element");
		}

		Shape newShape = getFactory().makeNewShape((ShapeGraphicalRepresentation) association.getGraphicalRepresentation(), dropLocation,
				container.getDiagram());

		newShape.setAssociation(association);

		String instanceName = association.getConcept().getName().substring(0, 1).toLowerCase()
				+ association.getConcept().getName().substring(1);
		String baseName = instanceName;
		int i = 1;
		while (association.getConcept().getInstanceNamed(instanceName) != null) {
			instanceName = baseName + i;
			i++;
		}

		Instance returned = getFactory().newInstance(Instance.class);
		returned.setName(instanceName);
		returned.setConcept(association.getConcept());
		newShape.setInstance(returned);

		container.addToShapes(newShape);

		if (edit != null) {
			getFactory().getUndoManager().stopRecording(edit);
		}

		return newShape;
	}

	/**
	 * Look-up in existing associations an association where gr exactely maps same ShapeSpecification, ForegroundStyle, BackgroundStyle and
	 * TextStyle
	 * 
	 * @param gr
	 * @return
	 */
	protected ConceptGRAssociation retrieveAssociation(GraphicalRepresentation gr) {
		for (ConceptGRAssociation a : getDiagram().getAssociations()) {
			if (gr instanceof ShapeGraphicalRepresentation && a.getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
				ShapeGraphicalRepresentation searchedGR = (ShapeGraphicalRepresentation) gr;
				ShapeGraphicalRepresentation iteratedGR = (ShapeGraphicalRepresentation) a.getGraphicalRepresentation();
				if ((searchedGR.getShapeSpecification().equalsObject(iteratedGR.getShapeSpecification()))
						&& (searchedGR.getBackground().equalsObject(iteratedGR.getBackground()))
						&& (searchedGR.getForeground().equalsObject(iteratedGR.getForeground()))
						&& (searchedGR.getTextStyle().equalsObject(iteratedGR.getTextStyle()))) {
					return a;
				}
			} else if (gr instanceof ConnectorGraphicalRepresentation
					&& a.getGraphicalRepresentation() instanceof ConnectorGraphicalRepresentation) {
				ConnectorGraphicalRepresentation searchedGR = (ConnectorGraphicalRepresentation) gr;
				ConnectorGraphicalRepresentation iteratedGR = (ConnectorGraphicalRepresentation) a.getGraphicalRepresentation();
				if ((searchedGR.getConnectorSpecification().equalsObject(iteratedGR.getConnectorSpecification()))
						&& (searchedGR.getForeground().equalsObject(iteratedGR.getForeground()))
						&& (searchedGR.getTextStyle().equalsObject(iteratedGR.getTextStyle()))) {
					return a;
				}
			}
		}
		return null;
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
		CreateConceptAndInstanceDialog dialogData = new CreateConceptAndInstanceDialog(getDiagram().getDataModel(), diagramElement
				.getInstance().getName());
		FIBDialog dialog = FIBDialog.instanciateAndShowDialog(NEW_CONCEPT_NEW_INSTANCE_DIALOG, dialogData, application.getFrame(), true,
				application.LOCALIZATION);
		if (dialog.getStatus() == Status.VALIDATED) {
			Concept concept = getFactory().newInstance(Concept.class);
			concept.setName(dialogData.getConceptName());
			System.out.println("Created " + concept);
			getDiagram().getDataModel().addToConcepts(concept);
			diagramElement.getAssociation().setConcept(concept);
			diagramElement.getInstance().getConcept().removeFromInstances(diagramElement.getInstance());
			diagramElement.getInstance().delete();
			Instance returned = getFactory().newInstance(Instance.class);
			returned.setName(dialogData.getInstanceName());
			returned.setConcept(concept);
			System.out.println("Created " + returned);
			concept.addToInstances(returned);
			diagramElement.setInstance(returned);
			return returned;
		}
		return null;
	}

	public Instance createNewInstance(DiagramElement<?, ?> diagramElement) {
		CreateInstanceDialog dialogData = new CreateInstanceDialog(getDiagram().getDataModel(), diagramElement.getInstance().getName());
		FIBDialog dialog = FIBDialog.instanciateAndShowDialog(NEW_INSTANCE_DIALOG, dialogData, application.getFrame(), true,
				application.LOCALIZATION);
		if (dialog.getStatus() == Status.VALIDATED) {
			diagramElement.getInstance().getConcept().removeFromInstances(diagramElement.getInstance());
			diagramElement.getInstance().delete();
			Instance returned = getFactory().newInstance(Instance.class);
			returned.setName(dialogData.getInstanceName());
			returned.setConcept(dialogData.getConcept());
			diagramElement.getAssociation().setConcept(dialogData.getConcept());
			System.out.println("Created " + returned);
			dialogData.getConcept().addToInstances(returned);
			diagramElement.setInstance(returned);
			return returned;
		}
		return null;
	}

	public PropertyValue createPropertyValue(DiagramElement<?, ?> element) {
		PropertyValue returned = getFactory().newInstance(PropertyValue.class);
		returned.setKey("property");
		element.addToPropertyValues(returned);
		return returned;
	}

	public void delete(List<DiagramElement<?, ?>> objectsToDelete) {
		System.out.println("Deleting objects: " + objectsToDelete);
		Object[] context = objectsToDelete.toArray(new Object[objectsToDelete.size()]);
		for (DiagramElement<?, ?> o : objectsToDelete) {
			o.delete(context);
		}
	}

	private boolean isSelectingObjectOnDiagram = false;

	@Override
	public synchronized void selectionChanged(List<Object> selection) {
		List<DiagramElement<?, ?>> reflectingSelection = new ArrayList<DiagramElement<?, ?>>();

		for (Shape s : getDiagram().getShapes()) {
			s.getGraphicalRepresentation().getForeground().setUseTransparency(false);
			s.getGraphicalRepresentation().getBackground().setUseTransparency(false);
		}

		if (selection.size() > 0) {
			for (Object o : selection) {
				Concept concept = null;
				if (o instanceof Concept) {
					concept = (Concept) o;
				} else if (o instanceof Instance) {
					concept = ((Instance) o).getConcept();
				}
				for (Shape s : getDiagram().getShapes()) {
					if (s.getAssociation().getConcept() != concept) {
						s.getGraphicalRepresentation().getForeground().setUseTransparency(true);
						s.getGraphicalRepresentation().getForeground().setTransparencyLevel(0.1f);
						s.getGraphicalRepresentation().getBackground().setUseTransparency(true);
						s.getGraphicalRepresentation().getBackground().setTransparencyLevel(0.1f);
					}
				}
			}
		}

		for (Object o : selection) {
			if (o instanceof Instance) {
				reflectingSelection.addAll(getDiagram().getElementsRepresentingInstance((Instance) o));
			}
		}

		List<DrawingTreeNode<?, ?>> dtnSelection = new ArrayList<DrawingTreeNode<?, ?>>();
		for (DiagramElement<?, ?> e : reflectingSelection) {
			DrawingTreeNode<?, ?> dtn = getDrawing().getDrawingTreeNode(e);
			if (dtn != null) {
				dtnSelection.add(dtn);
			}
		}
		isSelectingObjectOnDiagram = true;
		getController().setSelectedObjects(dtnSelection);
		isSelectingObjectOnDiagram = false;
	}

	public synchronized void fireDrawingSelectionChanged(List<DiagramElement> diagramElements) {
		if (isSelectingObjectOnDiagram) {
			return;
		}

		for (Shape s : getDiagram().getShapes()) {
			s.getGraphicalRepresentation().getForeground().setUseTransparency(false);
			s.getGraphicalRepresentation().getBackground().setUseTransparency(false);
		}

		application.getRepresentedConceptBrowser().getFIBController().selectionCleared();
		for (DiagramElement e : diagramElements) {
			application.getRepresentedConceptBrowser().getFIBController().objectAddedToSelection(e.getInstance());
		}
	}
}
