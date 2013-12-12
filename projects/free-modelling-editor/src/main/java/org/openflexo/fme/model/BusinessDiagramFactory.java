package org.openflexo.fme.model;

import java.awt.Color;
import java.io.File;

import org.openflexo.fge.BackgroundImageBackgroundStyle;
import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.toolbox.FileResource;

public class BusinessDiagramFactory extends DiagramFactory {

	private static final File HW_COMPONENT = new FileResource("Images/hardware.png");
	private static final File SW_COMPONENT = new FileResource("Images/software.png");

	public BusinessDiagramFactory() throws ModelDefinitionException {
		super();
		createUndoManager();
	}

	@Override
	public Diagram makeNewDiagram() {
		Diagram diagram = super.makeNewDiagram();
		
		/*
		 * Add a pre-Existing BusinessModel and a graphical representation
		 */
		createNewShapeGraphicalRepresentation(diagram,"HWComponent", HW_COMPONENT);
		createNewShapeGraphicalRepresentation(diagram,"SWComponent", SW_COMPONENT);
		/*
		 * End pre-existing BusinessModel
		 */

		return diagram;
	}
	
	private void createNewShapeGraphicalRepresentation(Diagram diagram,String name, File image){
		Concept concept = newInstance(Concept.class);
		concept.setName(name);
		concept.setReadOnly(true);
		ConceptGRAssociation conceptAssoc = newInstance(ConceptGRAssociation.class);
		conceptAssoc.setConcept(concept);
		ShapeGraphicalRepresentation conceptGR = newInstance(ShapeGraphicalRepresentation.class);
		conceptGR.setShapeType(ShapeType.RECTANGLE);
		applyDefaultProperties(conceptGR);
		conceptGR.setWidth(40);
		conceptGR.setHeight(30);
		
		if(image!=null){
			conceptGR.setBackgroundType(BackgroundStyleType.IMAGE);
			conceptGR.getForeground().setNoStroke(true);
			conceptGR.getShadowStyle().setDrawShadow(false);
			BackgroundImageBackgroundStyle backgroundImage = (BackgroundImageBackgroundStyle)conceptGR.getBackground();
			backgroundImage.setFitToShape(true);
			backgroundImage.setImageFile(image);
		}
		
		conceptAssoc.setGraphicalRepresentation(conceptGR);
		diagram.addToAssociations(conceptAssoc);
		diagram.getDataModel().addToConcepts(concept);
	}

}
