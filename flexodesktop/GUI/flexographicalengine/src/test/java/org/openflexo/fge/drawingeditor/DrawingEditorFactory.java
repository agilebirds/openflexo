package org.openflexo.fge.drawingeditor;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class DrawingEditorFactory extends FGEModelFactory {

	private static int totalOccurences = 0;

	public DrawingEditorFactory() throws ModelDefinitionException {
		super(MyDrawing.class, MyShape.class, MyConnector.class, MyDrawingGraphicalRepresentation.class,
				MyShapeGraphicalRepresentation.class, MyConnectorGraphicalRepresentation.class);
	}

	// Called for NEW
	public MyDrawing makeNewDrawing() {
		totalOccurences++;
		MyDrawing returned = newInstance(MyDrawing.class);
		returned.setFactory(this);
		returned.setIndex(totalOccurences);
		returned.getEditedDrawing().init(this);
		return returned;
	}

	public MyConnector makeNewConnector(MyShape from, MyShape to, EditedDrawing drawing) {
		MyConnector returned = newInstance(MyConnector.class);
		returned.setDrawing(drawing.getModel());
		returned.setGraphicalRepresentation(makeNewConnectorGR(ConnectorType.LINE,
				(MyShapeGraphicalRepresentation) drawing.getGraphicalRepresentation(from),
				(MyShapeGraphicalRepresentation) drawing.getGraphicalRepresentation(to), returned, drawing));
		return returned;
	}

	public MyShape makeNewShape(ShapeType shape, FGEPoint p, EditedDrawing drawing) {
		MyShape returned = newInstance(MyShape.class);
		returned.setDrawing(drawing.getModel());
		MyShapeGraphicalRepresentation gr = makeNewShapeGR(shape, returned, drawing);
		if (gr.getDimensionConstraints() == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
			gr.setWidth(80);
			gr.setHeight(80);
		} else {
			gr.setWidth(100);
			gr.setHeight(80);
		}
		gr.setX(p.x);
		gr.setY(p.y);
		returned.setGraphicalRepresentation(gr);
		return returned;
	}

	public MyShape makeNewShape(ShapeGraphicalRepresentation<?> aGR, FGEPoint p, EditedDrawing drawing) {
		MyShape returned = newInstance(MyShape.class);
		returned.setDrawing(drawing.getModel());
		MyShapeGraphicalRepresentation gr = makeNewShapeGR(aGR, returned, drawing);
		gr.setX(p.x);
		gr.setY(p.y);
		returned.setGraphicalRepresentation(gr);
		return returned;
	}

	public MyDrawingGraphicalRepresentation makeNewDrawingGR(EditedDrawing aDrawing) {
		MyDrawingGraphicalRepresentation returned = newInstance(MyDrawingGraphicalRepresentation.class, true, true);
		returned.setFGEModelFactory(this);
		returned.setDrawable(aDrawing.getModel());
		returned.setDrawing(aDrawing);
		return returned;
	}

	public MyShapeGraphicalRepresentation makeNewShapeGR(ShapeType shapeType, MyShape aDrawable, EditedDrawing aDrawing) {
		MyShapeGraphicalRepresentation returned = newInstance(MyShapeGraphicalRepresentation.class, true, true);
		returned.setFGEModelFactory(this);
		returned.setDrawable(aDrawable);
		returned.setDrawing(aDrawing);
		returned.setShapeType(shapeType);
		returned.setIsFocusable(true);
		returned.setIsSelectable(true);
		returned.setIsReadOnly(false);
		returned.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
		return returned;

	}

	public MyShapeGraphicalRepresentation makeNewShapeGR(ShapeGraphicalRepresentation<?> aGR, MyShape aDrawable, EditedDrawing aDrawing) {
		MyShapeGraphicalRepresentation returned = newInstance(MyShapeGraphicalRepresentation.class, true, true);
		returned.setFGEModelFactory(this);
		returned.setDrawable(aDrawable);
		returned.setDrawing(aDrawing);
		returned.setsWith(aGR);
		returned.setIsFocusable(true);
		returned.setIsSelectable(true);
		returned.setIsReadOnly(false);
		returned.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
		return returned;
	}

	public MyConnectorGraphicalRepresentation makeNewConnectorGR(ConnectorType aConnectorType, MyShapeGraphicalRepresentation aStartObject,
			MyShapeGraphicalRepresentation anEndObject, MyConnector aDrawable, EditedDrawing aDrawing) {
		MyConnectorGraphicalRepresentation returned = newInstance(MyConnectorGraphicalRepresentation.class);
		returned.setFGEModelFactory(this);
		returned.setDrawable(aDrawable);
		returned.setDrawing(aDrawing);
		returned.setConnectorType(aConnectorType);
		returned.setStartObject(aStartObject);
		returned.setEndObject(anEndObject);
		applyDefaultProperties(returned);
		applyBasicControls(returned);
		return returned;
	}

	@Override
	public <I> I newInstance(Class<I> implementedInterface) {
		if (implementedInterface == MyShapeGraphicalRepresentation.class) {
			return (I) newInstance(MyShapeGraphicalRepresentation.class, true, true);
		} else if (implementedInterface == MyConnectorGraphicalRepresentation.class) {
			return (I) newInstance(MyConnectorGraphicalRepresentation.class, true, true);
		} else if (implementedInterface == MyDrawingGraphicalRepresentation.class) {
			return (I) newInstance(MyDrawingGraphicalRepresentation.class, true, true);
		}
		return super.newInstance(implementedInterface);
	}

	@Override
	public void applyBasicControls(ConnectorGraphicalRepresentation<?> connectorGraphicalRepresentation) {
		super.applyBasicControls(connectorGraphicalRepresentation);
	}

	@Override
	public void applyBasicControls(DrawingGraphicalRepresentation<?> drawingGraphicalRepresentation) {
		super.applyBasicControls(drawingGraphicalRepresentation);
		drawingGraphicalRepresentation.addToMouseClickControls(new ShowContextualMenuControl());
		drawingGraphicalRepresentation.addToMouseDragControls(new DrawEdgeControl(this));
	}

	@Override
	public void applyBasicControls(ShapeGraphicalRepresentation<?> shapeGraphicalRepresentation) {
		super.applyBasicControls(shapeGraphicalRepresentation);
		shapeGraphicalRepresentation.addToMouseClickControls(new ShowContextualMenuControl());
		shapeGraphicalRepresentation.addToMouseDragControls(new DrawEdgeControl(this));
	}

}
