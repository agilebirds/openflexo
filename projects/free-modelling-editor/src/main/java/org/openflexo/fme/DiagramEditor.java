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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

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
import org.openflexo.fme.dialog.RemoveConceptDialog;
import org.openflexo.fme.model.Concept;
import org.openflexo.fme.model.ConceptGRAssociation;
import org.openflexo.fme.model.Connector;
import org.openflexo.fme.model.Diagram;
import org.openflexo.fme.model.DiagramElement;
import org.openflexo.fme.model.DiagramFactory;
import org.openflexo.fme.model.Instance;
import org.openflexo.fme.model.PropertyDefinition;
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
	public String filter = "";
	private Instance instance;
	

	private static final File NEW_CONCEPT_NEW_INSTANCE_DIALOG = new FileResource("Fib/Dialog/NewConceptNewInstanceDialog.fib");
	private static final File NEW_CONCEPT_DIALOG = new FileResource("Fib/Dialog/NewConceptDialog.fib");
	private static final File NEW_INSTANCE_DIALOG = new FileResource("Fib/Dialog/NewInstanceDialog.fib");
	private static final File REMOVE_CONCEPT_DIALOG = new FileResource("Fib/Dialog/RemoveConceptDialog.fib");

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
	
	//static Connector newConnector;
	public Connector createNewConnector(DiagramElement container, Shape from, Shape to) {

		CompoundEdit edit = null;
		ConceptGRAssociation association;
		Connector newConnector;

		if (!getFactory().getUndoManager().isBeeingRecording()) {
			edit = getFactory().getUndoManager().startRecording("Creating new connector");
		}

		final List<ConnectorGraphicalRepresentation> connectorGrs = new ArrayList<ConnectorGraphicalRepresentation>();
		for(Connector connector : getDiagram().getConnectors()){
			connectorGrs.add(connector.getGraphicalRepresentation());
		}
		/*if(!connectorGrs.isEmpty()){
			JPopupMenu grSelectionMenu = new JPopupMenu();
			for(ConnectorGraphicalRepresentation cGR :connectorGrs ){
				JMenuItem newConceptItem = new JMenuItem("Connector");
				newConceptItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						newConnector = getFactory().makeNewConnector(connectorGrs.get(0),from, to, container.getDiagram());
					}
				});
				grSelectionMenu.add(newConceptItem);
			}
			grSelectionMenu.show(getController().getDrawingView(), 50, 50);
		}
		else{*/
		newConnector = getFactory().makeNewConnector(from, to, container.getDiagram());

		// Lets see about ConceptGRAssociation

		association = retrieveAssociation(newConnector.getGraphicalRepresentation());
		if (association == null) {

			// No other association with same GR found

			association = getFactory().newInstance(ConceptGRAssociation.class);

			ConnectorGraphicalRepresentation connectorGR = (ConnectorGraphicalRepresentation) newConnector.getGraphicalRepresentation().cloneObject();

			association.setGraphicalRepresentation(connectorGR);
			association.setConcept(getDiagram().getDataModel().getConceptNamed(Concept.NONE_CONCEPT));

			getDiagram().addToAssociations(association);
		}

		else {
				// An association with same GR has been found, use it
		}
		//}
		

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

		newConnector.setAssociation(association);
		newConnector.setInstance(instance);
		container.addToConnectors(newConnector);
				
		if (edit != null) {
			getFactory().getUndoManager().stopRecording(edit);
		}

		return newConnector;
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
	
	public Concept createNewConcept(String name) {
		CreateConceptDialog dialogData = new CreateConceptDialog(getDiagram().getDataModel(),name);
		FIBDialog dialog = FIBDialog.instanciateAndShowDialog(NEW_CONCEPT_DIALOG, dialogData, application.getFrame(), true,
				application.LOCALIZATION);
		if (dialog.getStatus() == Status.VALIDATED) {
			Concept returned = getFactory().newInstance(Concept.class);
			returned.setName(dialogData.getConceptName());
			returned.setReadOnly(false);
			System.out.println("Created " + returned);
			getDiagram().getDataModel().addToConcepts(returned);
			return returned;
		}
		return null;
	}
	
	public PropertyValue createNewPropertyValue(Instance instance, String key, String value){
		PropertyValue newPropertyValue = getFactory().newInstance(PropertyValue.class);
		newPropertyValue.setKey(key);
		instance.addToPropertyValues(newPropertyValue);
		instance.getPropertyNamed(key).setValue(value);
		return newPropertyValue;
	}
	
	public PropertyDefinition createNewPropertyDefinition(Concept concept, String name){
		PropertyDefinition newPropertyDefinition = getFactory().newInstance(PropertyDefinition.class);
		newPropertyDefinition.setName(name);
		concept.addToProperties(newPropertyDefinition);
		return newPropertyDefinition;
	}
	
	public boolean removeConcept(Concept concept) {
		RemoveConceptDialog dialogData = new RemoveConceptDialog(concept);
		FIBDialog dialog = FIBDialog.instanciateAndShowDialog(REMOVE_CONCEPT_DIALOG, dialogData, application.getFrame(), true,
				application.LOCALIZATION);
		if (dialog.getStatus() == Status.VALIDATED) {
			getDiagram().getDataModel().removeFromConcepts(concept);
			return true;
		}
		return false;
	}

	public Instance createNewConceptAndNewInstance(DiagramElement<?, ?> diagramElement) {
		CreateConceptAndInstanceDialog dialogData = new CreateConceptAndInstanceDialog(getDiagram().getDataModel(), diagramElement
				.getInstance().getName());
		FIBDialog dialog = FIBDialog.instanciateAndShowDialog(NEW_CONCEPT_NEW_INSTANCE_DIALOG, dialogData, application.getFrame(), true,
				application.LOCALIZATION);
		if (dialog.getStatus() == Status.VALIDATED) {
			Concept concept = getFactory().newInstance(Concept.class);
			concept.setName(dialogData.getConceptName());
			concept.setReadOnly(false);
			System.out.println("Created " + concept);
			getDiagram().getDataModel().addToConcepts(concept);
			diagramElement.getAssociation().setConcept(concept);
			Instance returned = getFactory().newInstance(Instance.class);
			returned.setName(dialogData.getInstanceName());
			returned.setConcept(concept);
			//Copy properties
			for(PropertyValue pv : diagramElement.getInstance().getPropertyValues()){
				createNewPropertyValue(returned, pv.getKey(), pv.getValue());
			}
			
			diagramElement.getInstance().getConcept().removeFromInstances(diagramElement.getInstance());
			diagramElement.getInstance().delete();
			System.out.println("Created " + returned);
			concept.addToInstances(returned);
			diagramElement.setInstance(returned);
			updatePropertyValues();
			return returned;
		}
		return null;
	}

	public Instance createNewInstance(DiagramElement<?, ?> diagramElement) {
		CreateInstanceDialog dialogData = new CreateInstanceDialog(getDiagram().getDataModel(), diagramElement.getInstance().getName());
		FIBDialog dialog = FIBDialog.instanciateAndShowDialog(NEW_INSTANCE_DIALOG, dialogData, application.getFrame(), true,
				application.LOCALIZATION);
		if (dialog.getStatus() == Status.VALIDATED) {
			diagramElement.getAssociation().setConcept(dialogData.getConcept());
			Instance returned = getFactory().newInstance(Instance.class);
			returned.setName(dialogData.getInstanceName());
			returned.setConcept(dialogData.getConcept());
			//Copy properties
			for(PropertyValue pv : diagramElement.getInstance().getPropertyValues()){
				createNewPropertyValue(returned, pv.getKey(), pv.getValue());
			}
			diagramElement.getInstance().getConcept().removeFromInstances(diagramElement.getInstance());
			diagramElement.getInstance().delete();
			
			System.out.println("Created " + returned);
			dialogData.getConcept().addToInstances(returned);
			diagramElement.setInstance(returned);
			updatePropertyValues();
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

	private boolean isSelectingObjectOnDiagram = false;

	@Override
	public synchronized void selectionChanged(Vector<Object> selection) {
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
		application.getConceptBrowser().getFIBController().selectionCleared();
		for (DiagramElement e : diagramElements) {
			application.getRepresentedConceptBrowser().getFIBController().objectAddedToSelection(e.getInstance());
			if(e.getInstance()!=null){
				application.getConceptBrowser().getFIBController().objectAddedToSelection(e.getInstance().getConcept());
			}	
		}
		if(diagramElements!=null && !diagramElements.isEmpty()){
			if(diagramElements.get(0).getInstance()!=instance){
				setInstance(diagramElements.get(0).getInstance());
			}
		}
	
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public List<Concept> getFilteredConcepts(){
		List<Concept> filteredConcepts = new ArrayList<Concept>();
		if(filter.equals("*")){
			for(Concept concept : getDiagram().getDataModel().getConcepts()){
				concept.setHtmlLabel(concept.produceHtmlLabel(concept.getName()));
				filteredConcepts.add(concept);
			}
		}
		if(filter.equals("")){
			filteredConcepts.clear();
		}
		else{
			for(Concept concept : getDiagram().getDataModel().getConcepts()){
				if(concept.getName().contains(filter)){
					String name = concept.getName();
					StringBuilder sb = new StringBuilder(name);

					int placeOfFiler = name.indexOf(filter);
					
					sb.insert(placeOfFiler, "<b>");
					sb.insert(placeOfFiler+3+filter.length(), "</b>");
					
					concept.setHtmlLabel(concept.produceHtmlLabel(sb.toString()));
					filteredConcepts.add(concept);
				}
			}
		}
		return filteredConcepts;
	}
	
	private void setInstance(Instance instance){
		this.instance = instance;
		if(instance!=null){
			// Update the property values for this instance
			updatePropertyValues();
		}
	}
	
	public void updatePropertyValues(){
		if(instance!=null){
			// Firts get the description and retrieve the new property values
			updateDescriptionValues(instance.getDescription());
			// Update the property values and property definition
			updateProperties();
			// Create a text from the property values
			instance.buildDescription();
		}
	}

	public void updateProperties(){
		if(instance!=null && instance.getConcept()!=null)
		{
			Concept concept = instance.getConcept();
			
			// If no concept has been affected to the instance then no properties are store in the concept
			if(concept.getName().equals("None")){
				return;
			}
			
			// Instanciate property values for the instance according to the property definition.
			for(PropertyDefinition propDef : concept.getProperties()){
				if(!instance.containsKeyNamed(propDef.getName())){
					createNewPropertyValue(instance, propDef.getName(), "");
				}
			}
			
			// Create property definition into the concept from the instance property values
			for (PropertyValue propertyValue: instance.getPropertyValues()){
				boolean contains = false;
				for(PropertyDefinition propDef : concept.getProperties()){
					if(propDef.getName().equals(propertyValue.getKey()))
						contains=true;
				}
				if(!contains&&propertyValue.getValue()!=null&&propertyValue.getValue()!=""){
					createNewPropertyDefinition(concept, propertyValue.getKey());
				}
			}
			
			// Remove the unused properties(no values for all instance using it)
			concept.removeUnusedProperties();
			
		}
	}
	
	
	
	// Update the properties of diagram element from the description
	private void updateDescriptionValues(String description){
		// Ensure that the concept properties are here
		//updateProperties();
		// If the description is not null then parse the description
		if(description!=null){
			
			// Go through the ligns
			StringTokenizer lignsTk = new StringTokenizer(description,"\n");
			String key,value = "";
	
			while (lignsTk.hasMoreTokens() ) {
				String lign = lignsTk.nextToken();
				
				// Go through one lign
				if(lign.contains("=")){
					StringTokenizer lignTk = new StringTokenizer(lign,"=");
					PropertyValue propertyValue;
					while (lignTk.hasMoreTokens() ) {
						// First token is the Key
						key=lignTk.nextToken();
						
						// If there is no property with this key, then create new property, else, retrieve it
						if(instance.getPropertyNamed(key)==null){
							propertyValue = createNewPropertyValue(instance, key,null);
						}
						else{
							propertyValue = instance.getPropertyNamed(key);
						}
						
						// Second token if exists is the value
						if(lignTk.hasMoreTokens()){
							propertyValue.setValue(lignTk.nextToken());
						}
						else{
							propertyValue.setValue(value);
						}
					}
				}
				
			}
		}
	}
	
}
