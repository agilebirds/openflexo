package org.openflexo.fge.drawingeditor;

import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class DrawingEditorFactory extends FGEModelFactory {

	public DrawingEditorFactory() throws ModelDefinitionException {
		super();
		importClass(MyDrawingGraphicalRepresentation.class);
		importClass(MyShapeGraphicalRepresentation.class);
		importClass(MyConnectorGraphicalRepresentation.class);
	}

	public MyDrawingGraphicalRepresentation makeNewDrawing(EditedDrawing aDrawing) {
		MyDrawingGraphicalRepresentation returned = newInstance(MyDrawingGraphicalRepresentation.class);
		returned.setDrawing(aDrawing);
		returned.addToMouseClickControls(new ShowContextualMenuControl());
		return returned;
	}

	public MyShapeGraphicalRepresentation makeNewShape(ShapeType shapeType, MyShape aDrawable, EditedDrawing aDrawing) {
		MyShapeGraphicalRepresentation returned = newInstance(MyShapeGraphicalRepresentation.class);
		returned.setShapeType(shapeType);
		returned.setDrawable(aDrawable);
		returned.setDrawing(aDrawing);
		return returned;
	}

	public MyShapeGraphicalRepresentation makeNewShape(ShapeGraphicalRepresentation<?> aGR, MyShape aDrawable, EditedDrawing aDrawing) {
		MyShapeGraphicalRepresentation returned = newInstance(MyShapeGraphicalRepresentation.class);
		returned.setDrawable(aDrawable);
		returned.setDrawing(aDrawing);
		returned.setsWith(aGR);
		return returned;
	}

	public MyConnectorGraphicalRepresentation makeNewConnector(ConnectorType aConnectorType, MyShapeGraphicalRepresentation aStartObject,
			MyShapeGraphicalRepresentation anEndObject, MyConnector aDrawable, EditedDrawing aDrawing) {
		MyConnectorGraphicalRepresentation returned = newInstance(MyConnectorGraphicalRepresentation.class);
		returned.setConnectorType(aConnectorType);
		returned.setStartObject(aStartObject);
		returned.setEndObject(anEndObject);
		returned.setDrawable(aDrawable);
		returned.setDrawing(aDrawing);
		return returned;
	}

}
