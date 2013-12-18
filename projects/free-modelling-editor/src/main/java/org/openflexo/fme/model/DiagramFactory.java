package org.openflexo.fme.model;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fme.DrawEdgeControl;
import org.openflexo.fme.PipetteControl;
import org.openflexo.fme.ShowContextualMenuControl;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.undo.CompoundEdit;

public class DiagramFactory extends FGEModelFactoryImpl {

	private int shapeIndex = 0;
	private int connectorIndex = 0;

	public DiagramFactory() throws ModelDefinitionException {
		super(Diagram.class, Shape.class, Connector.class);
		createUndoManager();
	}

	// Called for NEW
	public Diagram makeNewDiagram() {
		CompoundEdit edit = getUndoManager().startRecording("Create empty diagram");
		Diagram returned = newInstance(Diagram.class);
		returned.setDataModel(newInstance(DataModel.class));
		Concept noneConcept = newInstance(Concept.class);
		noneConcept.setName(Concept.NONE_CONCEPT);
		noneConcept.setReadOnly(true);
		returned.getDataModel().addToConcepts(noneConcept);

		// returned.setFactory(this);
		// returned.setIndex(totalOccurences);
		// returned.getEditedDrawing().init();
		getUndoManager().stopRecording(edit);
		return returned;
	}

	public Connector makeNewConnector(ConnectorGraphicalRepresentation aGR, Shape from, Shape to, Diagram diagram) {
		Connector returned = newInstance(Connector.class);
		// returned.setName("Connector" + connectorIndex);
		connectorIndex++;
		// returned.setDiagram(diagram);
		returned.setGraphicalRepresentation(makeNewConnectorGR(aGR));
		returned.setStartShape(from);
		returned.setEndShape(to);
		return returned;
	}

	public Connector makeNewConnector(Shape from, Shape to, Diagram diagram) {
		Connector returned = newInstance(Connector.class);
		// returned.setName("Connector" + connectorIndex);
		connectorIndex++;
		// returned.setDiagram(diagram);
		returned.setGraphicalRepresentation(makeNewConnectorGR(ConnectorType.LINE));
		returned.setStartShape(from);
		returned.setEndShape(to);

		return returned;
	}

	public Shape makeNewShape(ShapeType shape, FGEPoint p, Diagram diagram) {
		ShapeGraphicalRepresentation gr = makeNewShapeGR(shape);
		gr.setWidth(100);
		gr.setHeight(80);
		return makeNewShape(gr, p, diagram);
	}

	public Shape makeNewShape(ShapeGraphicalRepresentation aGR, FGEPoint p, Diagram diagram) {
		Shape returned = newInstance(Shape.class);
		// returned.setDiagram(diagram);
		// returned.setName("Shape" + shapeIndex);
		// System.out.println("New name: " + returned.getName());
		shapeIndex++;
		ShapeGraphicalRepresentation gr = makeNewShapeGR(aGR);
		gr.setX(p.x);
		gr.setY(p.y);
		returned.setGraphicalRepresentation(gr);
		return returned;
	}

	public Shape makeNewShape(ShapeGraphicalRepresentation aGR, Diagram diagram) {
		return makeNewShape(aGR, aGR.getLocation(), diagram);
	}

	public DrawingGraphicalRepresentation makeNewDrawingGR() {
		return makeDrawingGraphicalRepresentation(true);
	}

	public ShapeGraphicalRepresentation makeNewShapeGR(ShapeType shapeType) {
		ShapeGraphicalRepresentation returned = newInstance(ShapeGraphicalRepresentation.class, true, true);
		returned.setFactory(this);
		returned.setShapeType(shapeType);
		returned.setIsFocusable(true);
		returned.setIsSelectable(true);
		returned.setIsReadOnly(false);
		returned.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
		return returned;

	}

	public ShapeGraphicalRepresentation makeNewShapeGR(ShapeGraphicalRepresentation aGR) {
		ShapeGraphicalRepresentation returned = newInstance(ShapeGraphicalRepresentation.class, true, true);
		returned.setFactory(this);
		returned.setsWith(aGR);
		returned.setIsFocusable(true);
		returned.setIsSelectable(true);
		returned.setIsReadOnly(false);
		returned.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
		return returned;
	}

	public ConnectorGraphicalRepresentation makeNewConnectorGR(ConnectorType aConnectorType) {
		ConnectorGraphicalRepresentation returned = newInstance(ConnectorGraphicalRepresentation.class);
		returned.setFactory(this);
		returned.setConnectorType(aConnectorType);
		applyDefaultProperties(returned);
		applyBasicControls(returned);
		return returned;
	}

	public ConnectorGraphicalRepresentation makeNewConnectorGR(ConnectorGraphicalRepresentation aGR) {
		ConnectorGraphicalRepresentation returned = newInstance(ConnectorGraphicalRepresentation.class);
		returned.setFactory(this);
		returned.setsWith(aGR);
		return returned;
	}

	/*@Override
	public <I> I newInstance(Class<I> implementedInterface) {
		if (implementedInterface == ShapeGraphicalRepresentation.class) {
			return (I) newInstance(ShapeGraphicalRepresentation.class, true, true);
		} else if (implementedInterface == ConnectorGraphicalRepresentation.class) {
			return (I) newInstance(ConnectorGraphicalRepresentation.class, true, true);
		} else if (implementedInterface == DrawingGraphicalRepresentation.class) {
			return (I) newInstance(DrawingGraphicalRepresentation.class, true, true);
		}
		return super.newInstance(implementedInterface);
	}*/

	@Override
	public void applyBasicControls(ConnectorGraphicalRepresentation connectorGraphicalRepresentation) {
		super.applyBasicControls(connectorGraphicalRepresentation);
		connectorGraphicalRepresentation.addToMouseClickControls(new ShowContextualMenuControl(this));
		connectorGraphicalRepresentation.addToMouseClickControls(new PipetteControl(this), true);
	}

	@Override
	public void applyBasicControls(DrawingGraphicalRepresentation drawingGraphicalRepresentation) {
		super.applyBasicControls(drawingGraphicalRepresentation);
		drawingGraphicalRepresentation.addToMouseClickControls(new ShowContextualMenuControl(this));
		drawingGraphicalRepresentation.addToMouseDragControls(new DrawEdgeControl(this));
	}

	@Override
	public void applyBasicControls(ShapeGraphicalRepresentation shapeGraphicalRepresentation) {
		super.applyBasicControls(shapeGraphicalRepresentation);
		shapeGraphicalRepresentation.addToMouseClickControls(new ShowContextualMenuControl(this));
		shapeGraphicalRepresentation.addToMouseDragControls(new DrawEdgeControl(this));
		shapeGraphicalRepresentation.addToMouseClickControls(new PipetteControl(this), true);
	}

}
