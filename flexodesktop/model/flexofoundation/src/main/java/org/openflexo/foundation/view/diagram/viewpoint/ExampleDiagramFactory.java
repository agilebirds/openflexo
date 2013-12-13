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
package org.openflexo.foundation.view.diagram.viewpoint;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.undo.CompoundEdit;

/**
 * Example Diagram factory<br>
 * Only one instance of this class should be used in a session
 * 
 * @author sylvain
 * 
 */
public class ExampleDiagramFactory extends FGEModelFactoryImpl {

	private int shapeIndex = 0;
	private int connectorIndex = 0;

	public ExampleDiagramFactory() throws ModelDefinitionException {
		super(/*ExampleDiagram.class, ExampleDiagramShape.class, ExampleDiagramConnector.class*/);
	}

	// Called for NEW
	public ExampleDiagram makeNewDiagram() {
		CompoundEdit edit = getUndoManager().startRecording("Create empty diagram");
		ExampleDiagram returned = newInstance(ExampleDiagram.class);
		// returned.setFactory(this);
		// returned.setIndex(totalOccurences);
		// returned.getEditedDrawing().init();
		getUndoManager().stopRecording(edit);
		return returned;
	}

	public ExampleDiagramConnector makeNewConnector(ConnectorGraphicalRepresentation aGR, ExampleDiagramShape from, ExampleDiagramShape to,
			ExampleDiagram diagram) {
		ExampleDiagramConnector returned = newInstance(ExampleDiagramConnector.class);
		returned.setName("Connector" + connectorIndex);
		connectorIndex++;
		returned.setGraphicalRepresentation(makeNewConnectorGR(aGR));
		returned.setStartShape(from);
		returned.setEndShape(to);
		return returned;
	}

	public ExampleDiagramConnector makeNewConnector(ExampleDiagramShape from, ExampleDiagramShape to, ExampleDiagram diagram) {
		ExampleDiagramConnector returned = newInstance(ExampleDiagramConnector.class);
		returned.setName("Connector" + connectorIndex);
		connectorIndex++;
		returned.setGraphicalRepresentation(makeNewConnectorGR(ConnectorType.LINE));
		returned.setStartShape(from);
		returned.setEndShape(to);
		return returned;
	}

	public ExampleDiagramShape makeNewShape(ShapeType shape, FGEPoint p, ExampleDiagram diagram) {
		ShapeGraphicalRepresentation gr = makeNewShapeGR(shape);
		gr.setWidth(100);
		gr.setHeight(80);
		return makeNewShape(gr, p, diagram);
	}

	public ExampleDiagramShape makeNewShape(ShapeGraphicalRepresentation aGR, FGEPoint p, ExampleDiagram diagram) {
		ExampleDiagramShape returned = newInstance(ExampleDiagramShape.class);
		returned.setName("Shape" + shapeIndex);
		shapeIndex++;
		ShapeGraphicalRepresentation gr = makeNewShapeGR(aGR);
		gr.setX(p.x);
		gr.setY(p.y);
		returned.setGraphicalRepresentation(gr);
		return returned;
	}

	public ExampleDiagramShape makeNewShape(ShapeGraphicalRepresentation aGR, ExampleDiagram diagram) {
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

}
