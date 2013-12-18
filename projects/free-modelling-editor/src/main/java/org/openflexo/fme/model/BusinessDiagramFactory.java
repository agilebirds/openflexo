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

	private static final File AND_OPERATOR = new FileResource("Images/AndOperator.gif");
	private static final File COMPLEX_OPERATOR = new FileResource("Images/ComplexOperator.gif");
	private static final File EXCLUSIVE_OPERATOR = new FileResource("Images/ExclusiveEventBasedOperator.gif");
	private static final File IF_OPERATOR = new FileResource("Images/IfOperator.gif");
	private static final File LOOP_OPERATOR = new FileResource("Images/LoopOperator.gif");
	private static final File INCLUSIVE_OPERATOR = new FileResource("Images/InclusiveOperator.gif");
	private static final File OR_OPERATOR = new FileResource("Images/OrOperator.gif");

	private static final File START_MESSAGE_EVENT = new FileResource("Images/StartMessage.png");
	private static final File START_CONDITION_EVENT = new FileResource("Images/StartCondition.png");
	private static final File START_ERROR_EVENT = new FileResource("Images/StartError.png");
	private static final File START_SIGNAL_EVENT = new FileResource("Images/StartSignal.png");
	private static final File START_TIMER_EVENT = new FileResource("Images/StartTimer.png");

	private static final File INTERMEDIATE_MESSAGE_EVENT = new FileResource("Images/IntermediateMessage.png");
	private static final File INTERMEDIATE_EVENT = new FileResource("Images/IntermediateMessage.png");
	private static final File INTERMEDIATE_CANCEL_EVENT = new FileResource("Images/IntermediateMessage.png");
	private static final File INTERMEDIATE_TIMER_EVENT = new FileResource("Images/IntermediateMessage.png");
	private static final File INTERMEDIATE_CONDITION_EVENT = new FileResource("Images/IntermediateMessage.png");
	private static final File INTERMEDIATE_ERROR_EVENT = new FileResource("Images/IntermediateMessage.png");
	private static final File INTERMEDIATE_SIGNAL_EVENT = new FileResource("Images/IntermediateMessage.png");
	private static final File INTERMEDIATE_LINK_EVENT = new FileResource("Images/IntermediateMessage.png");

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

		ShapeGraphicalRepresentation hwGR = createHWGR();
		ShapeGraphicalRepresentation swGR = createSWGR();

		codesignConceptAndRelations.add(createNewAssociation(hwComponent, diagram, hwGR));
		codesignConceptAndRelations.add(createNewAssociation(swComponent, diagram, swGR));

		// BPMN
		Concept activity = createConcept(diagram, "Activity");
		Concept gateway = createConcept(diagram, "Gateway");
		Concept and = createConcept(diagram, "And");
		Concept complex = createConcept(diagram, "Complex");
		Concept exclusive = createConcept(diagram, "Exclusive");
		Concept ifOp = createConcept(diagram, "If");
		Concept inclusive = createConcept(diagram, "Inclusive");
		Concept loop = createConcept(diagram, "Loop");
		Concept or = createConcept(diagram, "Or");
		Concept startEvent = createConcept(diagram, "StartEvent");
		Concept startMessageEvent = createConcept(diagram, "StartMessageEvent");
		Concept startConditionEvent = createConcept(diagram, "StartConditionEvent");
		Concept startErrorEvent = createConcept(diagram, "StartErrorEvent");
		Concept startSignalEvent = createConcept(diagram, "StartSignalEvent");
		Concept startTimerEvent = createConcept(diagram, "StartTimerEvent");
		Concept intermediateEvent = createConcept(diagram, "IntermediateEvent");
		Concept intermediateMessageEvent = createConcept(diagram, "IntermediateMessageEvent");
		Concept endEvent = createConcept(diagram, "EndEvent");

		ShapeGraphicalRepresentation activityGR = createActivityGR();
		ShapeGraphicalRepresentation gatewayGR = createGatewayGR();
		ShapeGraphicalRepresentation andGR = createOperatorGR(AND_OPERATOR);
		ShapeGraphicalRepresentation complexGR = createOperatorGR(COMPLEX_OPERATOR);
		ShapeGraphicalRepresentation exclusiveGR = createOperatorGR(EXCLUSIVE_OPERATOR);
		ShapeGraphicalRepresentation ifGR = createOperatorGR(IF_OPERATOR);
		ShapeGraphicalRepresentation inclusiveGR = createOperatorGR(INCLUSIVE_OPERATOR);
		ShapeGraphicalRepresentation loopGR = createOperatorGR(LOOP_OPERATOR);
		ShapeGraphicalRepresentation orGR = createOperatorGR(OR_OPERATOR);
		ShapeGraphicalRepresentation startEventGR = createEventGR(null);
		ShapeGraphicalRepresentation startMessageEventGR = createEventGR(START_MESSAGE_EVENT);
		ShapeGraphicalRepresentation startConditionEventGR = createEventGR(START_CONDITION_EVENT);
		ShapeGraphicalRepresentation startErrorEventGR = createEventGR(START_ERROR_EVENT);
		ShapeGraphicalRepresentation startSignalEventGR = createEventGR(START_SIGNAL_EVENT);
		ShapeGraphicalRepresentation startTimerEventGR = createEventGR(START_TIMER_EVENT);
		ShapeGraphicalRepresentation intermediateEventGR = createEventGR(null);
		ShapeGraphicalRepresentation intermediateMessageEventGR = createEventGR(INTERMEDIATE_MESSAGE_EVENT);
		ShapeGraphicalRepresentation endEventGR = createEndEventGR(null);

		bpmnConceptAndRelations.add(createNewAssociation(activity, diagram, activityGR));
		bpmnConceptAndRelations.add(createNewAssociation(startEvent, diagram, startEventGR));
		bpmnConceptAndRelations.add(createNewAssociation(startMessageEvent, diagram, startMessageEventGR));
		bpmnConceptAndRelations.add(createNewAssociation(startConditionEvent, diagram, startConditionEventGR));
		bpmnConceptAndRelations.add(createNewAssociation(startErrorEvent, diagram, startErrorEventGR));
		bpmnConceptAndRelations.add(createNewAssociation(startSignalEvent, diagram, startSignalEventGR));
		bpmnConceptAndRelations.add(createNewAssociation(startTimerEvent, diagram, startTimerEventGR));
		bpmnConceptAndRelations.add(createNewAssociation(endEvent, diagram, endEventGR));
		bpmnConceptAndRelations.add(createNewAssociation(intermediateEvent, diagram, intermediateEventGR));
		bpmnConceptAndRelations.add(createNewAssociation(intermediateMessageEvent, diagram, intermediateMessageEventGR));
		bpmnConceptAndRelations.add(createNewAssociation(gateway, diagram, gatewayGR));
		bpmnConceptAndRelations.add(createNewAssociation(and, diagram, andGR));
		bpmnConceptAndRelations.add(createNewAssociation(complex, diagram, complexGR));
		bpmnConceptAndRelations.add(createNewAssociation(exclusive, diagram, exclusiveGR));
		bpmnConceptAndRelations.add(createNewAssociation(ifOp, diagram, ifGR));
		bpmnConceptAndRelations.add(createNewAssociation(inclusive, diagram, inclusiveGR));
		bpmnConceptAndRelations.add(createNewAssociation(loop, diagram, loopGR));
		bpmnConceptAndRelations.add(createNewAssociation(or, diagram, orGR));

		/*
		 * End pre-existing BusinessModel
		 */

		return diagram;
	}

	public List<ConceptGRAssociation> getBpmnConceptAndRelations() {
		return bpmnConceptAndRelations;
	}

	public List<ConceptGRAssociation> getCodesignConceptAndRelations() {
		return codesignConceptAndRelations;
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

	private ConceptGRAssociation createNewAssociation(Concept concept, Diagram diagram, ShapeGraphicalRepresentation sGR) {
		ConceptGRAssociation conceptAssoc = newInstance(ConceptGRAssociation.class);
		conceptAssoc.setConcept(concept);
		conceptAssoc.setGraphicalRepresentation(sGR);
		diagram.addToAssociations(conceptAssoc);
		return conceptAssoc;
	}

	private ShapeGraphicalRepresentation createShapeGraphicalRepresentation() {
		ShapeGraphicalRepresentation sGR = newInstance(ShapeGraphicalRepresentation.class);
		sGR.setShapeType(ShapeType.RECTANGLE);
		applyDefaultProperties(sGR);
		sGR.setWidth(40);
		sGR.setHeight(30);
		return sGR;
	}

	private ShapeGraphicalRepresentation createImage(ShapeGraphicalRepresentation sGR, File image, boolean stroke, boolean shadow) {
		sGR.setBackgroundType(BackgroundStyleType.IMAGE);
		sGR.getForeground().setNoStroke(stroke);
		sGR.getShadowStyle().setDrawShadow(shadow);
		BackgroundImageBackgroundStyle backgroundImage = (BackgroundImageBackgroundStyle) sGR.getBackground();
		backgroundImage.setFitToShape(true);
		backgroundImage.setImageFile(image);
		return sGR;
	}

	private ShapeGraphicalRepresentation createHWGR() {
		ShapeGraphicalRepresentation sGR = createShapeGraphicalRepresentation();
		sGR = createImage(sGR, HW_COMPONENT, true, false);
		return sGR;
	}

	private ShapeGraphicalRepresentation createSWGR() {
		ShapeGraphicalRepresentation sGR = createShapeGraphicalRepresentation();
		sGR = createImage(sGR, SW_COMPONENT, true, false);
		return sGR;
	}

	private ShapeGraphicalRepresentation createActivityGR() {
		ShapeSpecification shapeSpecification = makeShape(ShapeType.RECTANGLE);
		ShapeGraphicalRepresentation gr = makeShapeGraphicalRepresentation(shapeSpecification);
		gr.setWidth(200);
		gr.setHeight(150);
		((Rectangle) shapeSpecification).setIsRounded(true);
		((Rectangle) shapeSpecification).setArcSize(15);
		gr.setText("Activity");
		gr.setIsFloatingLabel(false);
		return gr;
	}

	private ShapeGraphicalRepresentation createGatewayGR() {
		ShapeSpecification shapeSpecification = makeShape(ShapeType.LOSANGE);
		ShapeGraphicalRepresentation gr = makeShapeGraphicalRepresentation(shapeSpecification);
		applyDefaultProperties(gr);
		gr.setWidth(90);
		gr.setHeight(90);
		gr.setText("Gateway");
		gr.setIsFloatingLabel(false);
		return gr;
	}

	private ShapeGraphicalRepresentation createOperatorGR(File image) {
		ShapeSpecification shapeSpecification = makeShape(ShapeType.LOSANGE);
		ShapeGraphicalRepresentation gr = makeShapeGraphicalRepresentation(shapeSpecification);
		applyDefaultProperties(gr);
		gr.setWidth(40);
		gr.setHeight(40);
		if (image != null) {
			gr = createImage(gr, image, false, false);
		}
		gr.setIsFloatingLabel(true);
		gr.setAbsoluteTextX(40);
		gr.setAbsoluteTextY(70);
		return gr;
	}

	private ShapeGraphicalRepresentation createEventGR(File image) {
		ShapeSpecification shapeSpecification = makeShape(ShapeType.CIRCLE);
		ShapeGraphicalRepresentation gr = makeShapeGraphicalRepresentation(shapeSpecification);
		gr.setWidth(30);
		gr.setHeight(30);
		if (image != null) {
			gr = createImage(gr, image, false, false);
		}
		gr.setIsFloatingLabel(true);
		gr.setAbsoluteTextX(40);
		gr.setAbsoluteTextY(70);
		return gr;
	}

	private ShapeGraphicalRepresentation createEndEventGR(File image) {
		ShapeSpecification shapeSpecification = makeShape(ShapeType.CIRCLE);
		ShapeGraphicalRepresentation gr = makeShapeGraphicalRepresentation(shapeSpecification);
		gr.setWidth(30);
		gr.setHeight(30);
		gr.getForeground().setLineWidth(3);
		if (image != null) {
			gr = createImage(gr, image, false, false);
		}
		gr.setIsFloatingLabel(true);
		gr.setAbsoluteTextX(40);
		gr.setAbsoluteTextY(70);
		return gr;
	}

}
