package org.openflexo.fme.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.fge.BackgroundImageBackgroundStyle;
import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.undo.CompoundEdit;
import org.openflexo.toolbox.FileResource;

public class BusinessDiagramFactory extends DiagramFactory {

	private static final File HW_COMPONENT = new FileResource("Images/hardware.png");
	private static final File SW_COMPONENT = new FileResource("Images/software.png");

	private List<ConceptGRAssociation> bpmnConceptAndRelations;
	private List<ConceptGRAssociation> codesignConceptAndRelations;

	public BusinessDiagramFactory() throws ModelDefinitionException {
		super();
		bpmnConceptAndRelations = new ArrayList<ConceptGRAssociation>();
		codesignConceptAndRelations = new ArrayList<ConceptGRAssociation>();
	}

	@Override
	public Diagram makeNewDiagram() {
		Diagram diagram = super.makeNewDiagram();

		/*
		 * Add a pre-Existing BusinessModel and a graphical representation
		 */

		// CODESIGN
		Concept hwComponent = createConcept(diagram, "HWComponent");
		Concept swComponent = createConcept(diagram, "SWComponent");

		codesignConceptAndRelations.add(createNewAssociation(hwComponent, diagram, null, HW_COMPONENT));
		codesignConceptAndRelations.add(createNewAssociation(swComponent, diagram, null, SW_COMPONENT));

		// BPMN
		Concept activity = createConcept(diagram, "Activity");
		Concept gateway = createConcept(diagram, "Gateway");
		Concept event = createConcept(diagram, "Event");

		bpmnConceptAndRelations.add(createNewAssociation(activity, diagram, createActivityGR(), null));
		bpmnConceptAndRelations.add(createNewAssociation(event, diagram, createEventGR(), null));
		bpmnConceptAndRelations.add(createNewAssociation(gateway, diagram, createGatewayGR(), null));
		bpmnConceptAndRelations.add(createNewAssociation(gateway, diagram, createGatewayGR(), null));
		/*
		 * End pre-existing BusinessModel
		 */

		return diagram;
	}

	private Concept createConcept(Diagram diagram, String name) {
		CompoundEdit edit = getUndoManager().startRecording("Create new Business Concept");
		Concept concept = newInstance(Concept.class);
		concept.setName(name);
		concept.setReadOnly(true);
		diagram.getDataModel().addToConcepts(concept);
		getUndoManager().stopRecording(edit);
		return concept;
	}

	private ConceptGRAssociation createNewAssociation(Concept concept, Diagram diagram, ShapeGraphicalRepresentation sGR, File image) {

		ConceptGRAssociation conceptAssoc = newInstance(ConceptGRAssociation.class);
		conceptAssoc.setConcept(concept);

		if (sGR == null) {
			sGR = newInstance(ShapeGraphicalRepresentation.class);
			sGR.setShapeType(ShapeType.RECTANGLE);
			applyDefaultProperties(sGR);
			sGR.setWidth(40);
			sGR.setHeight(30);
		}

		if (image != null) {
			sGR.setBackgroundType(BackgroundStyleType.IMAGE);
			sGR.getForeground().setNoStroke(true);
			sGR.getShadowStyle().setDrawShadow(false);
			BackgroundImageBackgroundStyle backgroundImage = (BackgroundImageBackgroundStyle) sGR.getBackground();
			backgroundImage.setFitToShape(true);
			backgroundImage.setImageFile(image);
		}

		conceptAssoc.setGraphicalRepresentation(sGR);
		diagram.addToAssociations(conceptAssoc);

		return conceptAssoc;
	}

	public List<ConceptGRAssociation> getBpmnConceptAndRelations() {
		return bpmnConceptAndRelations;
	}

	public List<ConceptGRAssociation> getCodesignConceptAndRelations() {
		return codesignConceptAndRelations;
	}

	private ShapeGraphicalRepresentation createActivityGR() {
		ShapeSpecification shapeSpecification = makeShape(ShapeType.RECTANGLE);
		ShapeGraphicalRepresentation gr = makeShapeGraphicalRepresentation(shapeSpecification);
		// applyDefaultProperties(gr);
		gr.setWidth(40);
		gr.setHeight(30);
		((Rectangle) shapeSpecification).setIsRounded(true);
		((Rectangle) shapeSpecification).setArcSize(30);
		return gr;
	}

	private ShapeGraphicalRepresentation createGatewayGR() {
		ShapeSpecification shapeSpecification = makeShape(ShapeType.LOSANGE);
		ShapeGraphicalRepresentation gr = makeShapeGraphicalRepresentation(shapeSpecification);
		applyDefaultProperties(gr);
		gr.setWidth(40);
		gr.setHeight(30);
		return gr;
	}

	private ShapeGraphicalRepresentation createEventGR() {
		ShapeSpecification shapeSpecification = makeShape(ShapeType.CIRCLE);
		ShapeGraphicalRepresentation gr = makeShapeGraphicalRepresentation(shapeSpecification);
		// applyDefaultProperties(gr);
		gr.setWidth(40);
		gr.setHeight(30);
		return gr;
	}
}
