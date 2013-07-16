package org.openflexo.fge.drawingeditor;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GRBinding.ConnectorGRBinding;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.GRProvider;
import org.openflexo.fge.GRStructureWalker;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class EditedDrawing extends DrawingImpl<MyDrawing> {

	public EditedDrawing(MyDrawing model, FGEModelFactory factory) {
		super(model, factory);
	}

	@Override
	public void init() {

		final DrawingGRBinding<MyDrawing> drawingBinding = bindDrawing(MyDrawing.class, "drawing");
		final ShapeGRBinding<MyShape> shapeBinding = bindShape(MyShape.class, "shape");
		final ConnectorGRBinding<MyConnector> connectorBinding = bindConnector(MyConnector.class, "connector", shapeBinding, shapeBinding);

		drawingBinding.setGRProvider(new GRProvider<MyDrawing, DrawingGraphicalRepresentation>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(MyDrawing drawable, FGEModelFactory factory) {
				if (drawable.getGraphicalRepresentation() != null) {
					drawable.getGraphicalRepresentation().setFactory(factory);
					return drawable.getGraphicalRepresentation();
				} else {
					DrawingGraphicalRepresentation returned = factory.makeDrawingGraphicalRepresentation(EditedDrawing.this);
					drawable.setGraphicalRepresentation(returned);
					return returned;
				}
			}
		});
		drawingBinding.addToWalkers(new GRStructureWalker<MyDrawing>() {

			@Override
			public void walk(MyDrawing myDrawing) {
				for (MyShape shape : myDrawing.getShapes()) {
					drawShape(shapeBinding, shape, myDrawing);
					// drawShape(shape).as(shapeBinding).in(myDrawing);
				}
			}
		});

		drawingBinding.addToWalkers(new GRStructureWalker<MyDrawing>() {
			@Override
			public void walk(MyDrawing myDrawing) {
				for (MyConnector connector : myDrawing.getConnectors()) {
					drawConnector(connectorBinding, connector, connector.getStartShape(), connector.getEndShape());
				}
			}
		});

		shapeBinding.addToWalkers(new GRStructureWalker<MyShape>() {
			@Override
			public void walk(MyShape myShape) {
				for (MyShape shape : myShape.getShapes()) {
					drawShape(shapeBinding, shape, shapeBinding, myShape);
					// drawShape(shape).as(shapeBinding).in(myShape).as(shapeBinding);
				}
			}
		});

	}

	public static void main(String[] args) throws ModelDefinitionException {
		DrawingEditorFactory factory = new DrawingEditorFactory();
		MyDrawing myDrawing = factory.makeNewDrawing();
		new EditedDrawing(myDrawing, factory);
	}
}
