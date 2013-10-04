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
package org.openflexo.fge.drawingeditor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.inject.Inject;

import org.openflexo.fge.GraphicalRepresentation;

public abstract class DiagramElementImpl<M extends DiagramElement<M, G>, G extends GraphicalRepresentation> extends Observable implements
		DiagramElement<M, G> {

	private Diagram diagram;
	private List<Shape> shapes;
	private List<Connector> connectors;

	@Inject
	private G graphicalRepresentation;

	public DiagramElementImpl(Diagram drawing) {
		shapes = new ArrayList<Shape>();
		connectors = new ArrayList<Connector>();
		diagram = drawing;
	}

	@Override
	public List<Shape> getShapes() {
		return shapes;
	}

	@Override
	public void setShapes(List<Shape> someShapes) {
		shapes.addAll(someShapes);
	}

	@Override
	public void addToShapes(Shape aShape) {
		shapes.add(aShape);
		setChanged();
		notifyObservers();

		// System.out.println("Add "+aShape+" isDeserializing="+isDeserializing());
		/*if (!isDeserializing()) {
			getDiagram().getEditedDrawing().updateGraphicalObjectsHierarchy(this);
		}*/
	}

	@Override
	public void removeFromShapes(Shape aShape) {
		shapes.remove(aShape);
	}

	@Override
	public List<Connector> getConnectors() {
		return connectors;
	}

	@Override
	public void setConnectors(List<Connector> someConnectors) {
		connectors.addAll(someConnectors);
	}

	@Override
	public void addToConnectors(Connector aConnector) {
		connectors.add(aConnector);
		// System.out.println("Add "+aConnector+" isDeserializing="+isDeserializing());
		/*if (!isDeserializing()) {
			getDiagram().getEditedDrawing().updateGraphicalObjectsHierarchy(this);
		}*/
	}

	@Override
	public void removeFromConnectors(Connector aConnector) {
		connectors.remove(aConnector);
	}

	@Override
	public Diagram getDiagram() {
		return diagram;
	}

	@Override
	public void setDiagram(Diagram drawing) {
		diagram = drawing;
	}

	@Override
	public final G getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	@Override
	public void setGraphicalRepresentation(G graphicalRepresentation) {
		this.graphicalRepresentation = graphicalRepresentation;
		// graphicalRepresentation.setDrawable((M) this);
		graphicalRepresentation.addObserver(this);
	}

	/*private boolean isDeserializing = false;

	@Override
	public void initializeDeserialization() {
		isDeserializing = true;
	}

	@Override
	public void finalizeDeserialization() {
		isDeserializing = false;
	}

	@Override
	public boolean isDeserializing() {
		return isDeserializing;
	}*/

	@Override
	public DiagramElementImpl<M, G> clone() {
		try {
			return (DiagramElementImpl<M, G>) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			// cannot happen since we are clonable
			return null;
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == getGraphicalRepresentation()) {
			if (getDiagram() != null) {
				getDiagram().setChanged();
			}
		}
	}

	@Override
	public void setChanged() {
		super.setChanged();
	}

}
