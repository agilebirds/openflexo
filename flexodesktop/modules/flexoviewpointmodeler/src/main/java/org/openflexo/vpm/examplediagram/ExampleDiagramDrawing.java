/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.vpm.examplediagram;

import java.util.logging.Logger;

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
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramConnector;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramFactory;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramObject;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramShape;
import org.openflexo.toolbox.ToolBox;

/**
 * This is the abstraction of a drawing representing an {@link ExampleDiagram}
 * 
 * @author sylvain
 * 
 */
public class ExampleDiagramDrawing extends DrawingImpl<ExampleDiagram> implements ExampleDiagramConstants {

	private static final Logger logger = Logger.getLogger(ExampleDiagramDrawing.class.getPackage().getName());

	public ExampleDiagramDrawing(ExampleDiagram model, boolean readOnly) {
		super(model, model.getResource().getFactory(), PersistenceMode.UniqueGraphicalRepresentations);
		setEditable(!readOnly);
	}

	@Override
	public void init() {

		final DrawingGRBinding<ExampleDiagram> drawingBinding = bindDrawing(ExampleDiagram.class, "drawing",
				new DrawingGRProvider<ExampleDiagram>() {
					@Override
					public DrawingGraphicalRepresentation provideGR(ExampleDiagram drawable, FGEModelFactory factory) {
						return retrieveGraphicalRepresentation(drawable, (ExampleDiagramFactory) factory);
					}
				});
		final ShapeGRBinding<ExampleDiagramShape> shapeBinding = bindShape(ExampleDiagramShape.class, "shape",
				new ShapeGRProvider<ExampleDiagramShape>() {
					@Override
					public ShapeGraphicalRepresentation provideGR(ExampleDiagramShape drawable, FGEModelFactory factory) {
						return retrieveGraphicalRepresentation(drawable, (ExampleDiagramFactory) factory);
					}
				});
		final ConnectorGRBinding<ExampleDiagramConnector> connectorBinding = bindConnector(ExampleDiagramConnector.class, "connector",
				shapeBinding, shapeBinding, new ConnectorGRProvider<ExampleDiagramConnector>() {
					@Override
					public ConnectorGraphicalRepresentation provideGR(ExampleDiagramConnector drawable, FGEModelFactory factory) {
						return retrieveGraphicalRepresentation(drawable, (ExampleDiagramFactory) factory);
					}
				});

		drawingBinding.addToWalkers(new GRStructureVisitor<ExampleDiagram>() {

			@Override
			public void visit(ExampleDiagram exampleDiagram) {
				for (ExampleDiagramObject child : exampleDiagram.getChilds()) {
					if (child instanceof ExampleDiagramShape) {
						drawShape(shapeBinding, (ExampleDiagramShape) child, exampleDiagram);
					}
					if (child instanceof ExampleDiagramConnector) {
						ExampleDiagramConnector connector = (ExampleDiagramConnector) child;
						drawConnector(connectorBinding, connector, connector.getStartShape(), connector.getEndShape(), exampleDiagram);
					}
				}
			}
		});

		shapeBinding.addToWalkers(new GRStructureVisitor<ExampleDiagramShape>() {
			@Override
			public void visit(ExampleDiagramShape aShape) {
				for (ExampleDiagramObject child : aShape.getChilds()) {
					if (child instanceof ExampleDiagramShape) {
						drawShape(shapeBinding, (ExampleDiagramShape) child, aShape);
					}
					if (child instanceof ExampleDiagramConnector) {
						ExampleDiagramConnector connector = (ExampleDiagramConnector) child;
						drawConnector(connectorBinding, connector, connector.getStartShape(), connector.getEndShape(), aShape);
					}
				}
			}
		});

		shapeBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);
		connectorBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);

	}

	@Override
	public void delete() {
		super.delete();
	}

	public ExampleDiagram getExampleDiagram() {
		return getModel();
	}

	private DrawingGraphicalRepresentation retrieveGraphicalRepresentation(ExampleDiagram diagram, ExampleDiagramFactory factory) {
		DrawingGraphicalRepresentation returned = null;
		if (diagram.getGraphicalRepresentation() != null) {
			diagram.getGraphicalRepresentation().setFactory(factory);
			returned = diagram.getGraphicalRepresentation();
		} else {
			returned = factory.makeDrawingGraphicalRepresentation();
			diagram.setGraphicalRepresentation(returned);
		}
		returned.addToMouseClickControls(new ExampleDiagramEditor.ShowContextualMenuControl(factory));
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			returned.addToMouseClickControls(new ExampleDiagramEditor.ShowContextualMenuControl(factory, true));
		}
		return returned;
	}

	private ShapeGraphicalRepresentation retrieveGraphicalRepresentation(ExampleDiagramShape shape, ExampleDiagramFactory factory) {
		ShapeGraphicalRepresentation returned = null;
		if (shape.getGraphicalRepresentation() != null) {
			shape.getGraphicalRepresentation().setFactory(factory);
			returned = shape.getGraphicalRepresentation();
		} else {
			returned = factory.makeShapeGraphicalRepresentation();
			shape.setGraphicalRepresentation(returned);
		}
		returned.addToMouseClickControls(new ExampleDiagramEditor.ShowContextualMenuControl(factory));
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			returned.addToMouseClickControls(new ExampleDiagramEditor.ShowContextualMenuControl(factory, true));
		}
		returned.addToMouseDragControls(new DrawEdgeControl(factory));
		return returned;
	}

	private ConnectorGraphicalRepresentation retrieveGraphicalRepresentation(ExampleDiagramConnector connector,
			ExampleDiagramFactory factory) {
		ConnectorGraphicalRepresentation returned = null;
		if (connector.getGraphicalRepresentation() != null) {
			connector.getGraphicalRepresentation().setFactory(factory);
			returned = connector.getGraphicalRepresentation();
		} else {
			returned = factory.makeConnectorGraphicalRepresentation();
			connector.setGraphicalRepresentation(returned);
		}
		returned.addToMouseClickControls(new ExampleDiagramEditor.ShowContextualMenuControl(factory));
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			returned.addToMouseClickControls(new ExampleDiagramEditor.ShowContextualMenuControl(factory, true));
		}
		return returned;
	}

}
