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
package org.openflexo.fme.model;

import java.util.Observable;

import org.openflexo.fge.GraphicalRepresentation;

public abstract class DiagramElementImpl<M extends DiagramElement<M, G>, G extends GraphicalRepresentation> implements DiagramElement<M, G> {

	private Diagram diagram;

	// private List<Shape> shapes;
	// private List<Connector> connectors;

	// @Inject
	// private G graphicalRepresentation;

	public DiagramElementImpl(Diagram diagram) {
		// shapes = new ArrayList<Shape>();
		// connectors = new ArrayList<Connector>();
		this.diagram = diagram;
	}

	// This method has no functional effect, it is just to test super calls over internal PAMELA management
	@Override
	public void addToShapes(Shape aShape) {
		performSuperAdder(SHAPES, aShape);
	}

	// This method has no functional effect, it is just to test super calls over internal PAMELA management
	@Override
	public void removeFromShapes(Shape aShape) {
		performSuperRemover(SHAPES, aShape);
	}

	/*@Override
	public List<Shape> getShapes() {
		return shapes;
	}

	@Override
	public void setShapes(List<Shape> someShapes) {
		if (someShapes != null) {
			shapes.addAll(someShapes);
		} else {
			shapes.clear();
		}
	}

	@Override
	public void addToShapes(Shape aShape) {
		shapes.add(aShape);
		setChanged();
		notifyObservers();
		getPropertyChangeSupport().firePropertyChange(SHAPES, null, shapes);
	}

	@Override
	public void removeFromShapes(Shape aShape) {
		System.out.println("!!!!!!!!!!!!!!!!!! Remove Shape " + aShape);
		shapes.remove(aShape);
		setChanged();
		notifyObservers();
		getPropertyChangeSupport().firePropertyChange(SHAPES, null, shapes);
	}
	*/

	/*@Override
	public List<Connector> getConnectors() {
		return connectors;
	}

	@Override
	public void setConnectors(List<Connector> someConnectors) {
		if (someConnectors != null) {
			connectors.addAll(someConnectors);
		} else {
			connectors.clear();
		}
	}

	@Override
	public void addToConnectors(Connector aConnector) {
		connectors.add(aConnector);
		setChanged();
		notifyObservers();
		getPropertyChangeSupport().firePropertyChange(CONNECTORS, null, shapes);
	}

	@Override
	public void removeFromConnectors(Connector aConnector) {
		connectors.remove(aConnector);
		setChanged();
		notifyObservers();
		getPropertyChangeSupport().firePropertyChange(CONNECTORS, null, shapes);
	}*/

	@Override
	public Diagram getDiagram() {
		return diagram;
	}

	@Override
	public void setDiagram(Diagram drawing) {
		diagram = drawing;
	}

	/*@Override
	public final G getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	@Override
	public void setGraphicalRepresentation(G graphicalRepresentation) {
		this.graphicalRepresentation = graphicalRepresentation;
		// graphicalRepresentation.setDrawable((M) this);
		// graphicalRepresentation.addObserver(this);
	}*/

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
	public DiagramElement<M, G> clone() {
		return (DiagramElement<M, G>) cloneObject();

		/*try {
			return (DiagramElementImpl<M, G>) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			// cannot happen since we are clonable
			return null;
		}*/
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == getGraphicalRepresentation()) {
			if (getDiagram() != null) {
				getDiagram().setChanged();
			}
		}
	}

	/*@Override
	public void setChanged() {
		super.setChanged();
	}*/

	@Override
	public String getName() {
		// System.out.println("On me demande mon nom, je retourne " + performSuperGetter(NAME));
		return (String) performSuperGetter(NAME);
	}
}
