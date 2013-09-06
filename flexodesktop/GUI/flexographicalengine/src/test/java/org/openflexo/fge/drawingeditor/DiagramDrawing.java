package org.openflexo.fge.drawingeditor;

import org.openflexo.antar.binding.DataBinding;
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
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.drawingeditor.model.Connector;
import org.openflexo.fge.drawingeditor.model.Diagram;
import org.openflexo.fge.drawingeditor.model.DiagramFactory;
import org.openflexo.fge.drawingeditor.model.Shape;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class DiagramDrawing extends DrawingImpl<Diagram> {

	public DiagramDrawing(Diagram model, FGEModelFactory factory) {
		super(model, factory, PersistenceMode.UniqueGraphicalRepresentations);
	}

	@Override
	public void init() {

		final DrawingGRBinding<Diagram> drawingBinding = bindDrawing(Diagram.class, "drawing", new DrawingGRProvider<Diagram>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(Diagram drawable, FGEModelFactory factory) {
				if (drawable.getGraphicalRepresentation() != null) {
					drawable.getGraphicalRepresentation().setFactory(factory);
					return drawable.getGraphicalRepresentation();
				} else {
					DrawingGraphicalRepresentation returned = factory.makeDrawingGraphicalRepresentation(DiagramDrawing.this);
					drawable.setGraphicalRepresentation(returned);
					return returned;
				}
			}
		});
		final ShapeGRBinding<Shape> shapeBinding = bindShape(Shape.class, "shape", new ShapeGRProvider<Shape>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(Shape drawable, FGEModelFactory factory) {
				if (drawable.getGraphicalRepresentation() != null) {
					drawable.getGraphicalRepresentation().setFactory(factory);
					return drawable.getGraphicalRepresentation();
				} else {
					ShapeGraphicalRepresentation returned = factory.makeShapeGraphicalRepresentation(DiagramDrawing.this);
					drawable.setGraphicalRepresentation(returned);
					return returned;
				}
			}
		});
		final ConnectorGRBinding<Connector> connectorBinding = bindConnector(Connector.class, "connector", shapeBinding, shapeBinding,
				new ConnectorGRProvider<Connector>() {
					@Override
					public ConnectorGraphicalRepresentation provideGR(Connector drawable, FGEModelFactory factory) {
						return null;
					}
				});

		// visitor plutot ?
		drawingBinding.addToWalkers(new GRStructureWalker<Diagram>() {

			@Override
			public void walk(Diagram myDrawing) {
				for (Shape shape : myDrawing.getShapes()) {
					drawShape(shapeBinding, shape, myDrawing);
					// drawShape(shape).as(shapeBinding).in(myDrawing);
				}
			}
		});

		drawingBinding.addToWalkers(new GRStructureWalker<Diagram>() {
			@Override
			public void walk(Diagram myDrawing) {
				for (Connector connector : myDrawing.getConnectors()) {
					drawConnector(connectorBinding, connector, connector.getStartShape(), connector.getEndShape());
				}
			}
		});

		shapeBinding.addToWalkers(new GRStructureWalker<Shape>() {
			@Override
			public void walk(Shape myShape) {
				for (Shape shape : myShape.getShapes()) {
					drawShape(shapeBinding, shape, shapeBinding, myShape);
					// drawShape(shape).as(shapeBinding).in(myShape).as(shapeBinding);
				}
			}
		});
		shapeBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);
		connectorBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);

	}

	public static void main(String[] args) throws ModelDefinitionException {
		DiagramFactory factory = new DiagramFactory();
		Diagram myDrawing = factory.makeNewDiagram();
		new DiagramDrawing(myDrawing, factory);
	}
}
