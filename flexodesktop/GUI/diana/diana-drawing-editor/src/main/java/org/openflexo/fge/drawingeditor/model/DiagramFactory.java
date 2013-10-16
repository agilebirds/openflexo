package org.openflexo.fge.drawingeditor.model;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.drawingeditor.DiagramDrawing;
import org.openflexo.fge.drawingeditor.DrawEdgeControl;
import org.openflexo.fge.drawingeditor.ShowContextualMenuControl;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class DiagramFactory extends FGEModelFactoryImpl {

	private int shapeIndex = 0;
	private int connectorIndex = 0;

	public DiagramFactory() throws ModelDefinitionException {
		super(Diagram.class, Shape.class, Connector.class);
	}

	// Called for NEW
	public Diagram makeNewDiagram() {
		Diagram returned = newInstance(Diagram.class);
		// returned.setFactory(this);
		// returned.setIndex(totalOccurences);
		// returned.getEditedDrawing().init();
		return returned;
	}

	public Connector makeNewConnector(Shape from, Shape to, DiagramDrawing drawing) {
		Connector returned = newInstance(Connector.class);
		returned.setName("Connector" + connectorIndex);
		connectorIndex++;
		returned.setDiagram(drawing.getModel());
		returned.setGraphicalRepresentation(makeNewConnectorGR(ConnectorType.LINE, returned, drawing));
		returned.setStartShape(from);
		returned.setEndShape(to);
		return returned;
	}

	public Shape makeNewShape(ShapeType shape, FGEPoint p, DiagramDrawing drawing) {
		ShapeGraphicalRepresentation gr = makeNewShapeGR(shape, drawing);
		gr.setWidth(100);
		gr.setHeight(80);
		return makeNewShape(gr, p, drawing);
	}

	public Shape makeNewShape(ShapeGraphicalRepresentation aGR, FGEPoint p, DiagramDrawing drawing) {
		Shape returned = newInstance(Shape.class);
		returned.setDiagram(drawing.getModel());
		returned.setName("Shape" + shapeIndex);
		System.out.println("New name: " + returned.getName());
		shapeIndex++;
		ShapeGraphicalRepresentation gr = makeNewShapeGR(aGR, drawing);
		gr.setX(p.x);
		gr.setY(p.y);
		returned.setGraphicalRepresentation(gr);
		return returned;
	}

	public DrawingGraphicalRepresentation makeNewDrawingGR(DiagramDrawing aDrawing) {
		DrawingGraphicalRepresentation returned = newInstance(DrawingGraphicalRepresentation.class, true, true);
		returned.setFactory(this);
		returned.setDrawing(aDrawing);
		return returned;
	}

	public ShapeGraphicalRepresentation makeNewShapeGR(ShapeType shapeType, /*Shape aDrawable,*/DiagramDrawing aDrawing) {
		ShapeGraphicalRepresentation returned = newInstance(ShapeGraphicalRepresentation.class, true, true);
		returned.setFactory(this);
		returned.setDrawing(aDrawing);
		returned.setShapeType(shapeType);
		returned.setIsFocusable(true);
		returned.setIsSelectable(true);
		returned.setIsReadOnly(false);
		returned.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
		return returned;

	}

	public ShapeGraphicalRepresentation makeNewShapeGR(ShapeGraphicalRepresentation aGR,/* Shape aDrawable,*/DiagramDrawing aDrawing) {
		ShapeGraphicalRepresentation returned = newInstance(ShapeGraphicalRepresentation.class, true, true);
		returned.setFactory(this);
		returned.setDrawing(aDrawing);
		returned.setsWith(aGR);
		returned.setIsFocusable(true);
		returned.setIsSelectable(true);
		returned.setIsReadOnly(false);
		returned.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
		return returned;
	}

	public ConnectorGraphicalRepresentation makeNewConnectorGR(ConnectorType aConnectorType/*, ShapeGraphicalRepresentation aStartObject,
																							ShapeGraphicalRepresentation anEndObject*/,
			Connector aDrawable, DiagramDrawing aDrawing) {
		ConnectorGraphicalRepresentation returned = newInstance(ConnectorGraphicalRepresentation.class);
		returned.setFactory(this);
		returned.setDrawing(aDrawing);
		returned.setConnectorType(aConnectorType);
		// returned.setStartObject(aStartObject);
		// returned.setEndObject(anEndObject);
		applyDefaultProperties(returned);
		applyBasicControls(returned);
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
	}

}
