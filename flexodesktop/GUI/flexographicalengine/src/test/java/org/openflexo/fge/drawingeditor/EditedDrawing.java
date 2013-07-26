package org.openflexo.fge.drawingeditor;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GRBinding.ConnectorGRBinding;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.ConnectorGRProvider;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureWalker;
import org.openflexo.fge.GraphicalRepresentation.Parameters;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class EditedDrawing extends DrawingImpl<MyDrawing> {

	public EditedDrawing(MyDrawing model, FGEModelFactory factory) {
		super(model, factory);
	}

	@Override
	public void init() {

		final DrawingGRBinding<MyDrawing> drawingBinding = bindDrawing(MyDrawing.class, "drawing", new DrawingGRProvider<MyDrawing>() {
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
		final ShapeGRBinding<MyShape> shapeBinding = bindShape(MyShape.class, "shape", new ShapeGRProvider<MyShape>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(MyShape drawable, FGEModelFactory factory) {
				if (drawable.getGraphicalRepresentation() != null) {
					drawable.getGraphicalRepresentation().setFactory(factory);
					return drawable.getGraphicalRepresentation();
				} else {
					ShapeGraphicalRepresentation returned = factory.makeShapeGraphicalRepresentation(EditedDrawing.this);
					drawable.setGraphicalRepresentation(returned);
					return returned;
				}
			}
		});
		final ConnectorGRBinding<MyConnector> connectorBinding = bindConnector(MyConnector.class, "connector", shapeBinding, shapeBinding,
				new ConnectorGRProvider<MyConnector>() {
					@Override
					public ConnectorGraphicalRepresentation provideGR(MyConnector drawable, FGEModelFactory factory) {
						return null;
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
		shapeBinding.addDynamicPropertyValue(Parameters.text, "drawable.name");

	}

	public static void main(String[] args) throws ModelDefinitionException {
		DrawingEditorFactory factory = new DrawingEditorFactory();
		MyDrawing myDrawing = factory.makeNewDrawing();
		new EditedDrawing(myDrawing, factory);
	}
}
