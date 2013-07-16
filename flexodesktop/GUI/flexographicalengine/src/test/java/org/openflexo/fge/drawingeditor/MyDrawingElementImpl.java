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
package org.openflexo.fge.drawingeditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.inject.Inject;

import org.openflexo.fge.GraphicalRepresentation;

public abstract class MyDrawingElementImpl<M extends MyDrawingElement<M, G>, G extends GraphicalRepresentation> extends Observable
		implements MyDrawingElement<M, G> {

	private MyDrawing _drawing;
	private List<MyShape> shapes;
	private List<MyConnector> connectors;

	@Inject
	private G graphicalRepresentation;

	public MyDrawingElementImpl(MyDrawing drawing) {
		shapes = new ArrayList<MyShape>();
		connectors = new ArrayList<MyConnector>();
		_drawing = drawing;
	}

	@Override
	public List<MyShape> getShapes() {
		return shapes;
	}

	@Override
	public void setShapes(List<MyShape> someShapes) {
		shapes.addAll(someShapes);
	}

	@Override
	public void addToShapes(MyShape aShape) {
		shapes.add(aShape);
		// System.out.println("Add "+aShape+" isDeserializing="+isDeserializing());
		if (!isDeserializing()) {
			getDrawing().getEditedDrawing().updateGraphicalObjectsHierarchy(this);
			// getDrawing().getEditedDrawing().addDrawable(aShape, this);
		}
	}

	@Override
	public void removeFromShapes(MyShape aShape) {
		shapes.remove(aShape);
	}

	@Override
	public List<MyConnector> getConnectors() {
		return connectors;
	}

	@Override
	public void setConnectors(List<MyConnector> someConnectors) {
		connectors.addAll(someConnectors);
	}

	@Override
	public void addToConnectors(MyConnector aConnector) {
		connectors.add(aConnector);
		// System.out.println("Add "+aConnector+" isDeserializing="+isDeserializing());
		if (!isDeserializing()) {
			getDrawing().getEditedDrawing().updateGraphicalObjectsHierarchy(this);
			// getDrawing().getEditedDrawing().addDrawable(aConnector, this);
		}
	}

	@Override
	public void removeFromConnectors(MyConnector aConnector) {
		connectors.remove(aConnector);
	}

	@Override
	public MyDrawing getDrawing() {
		return _drawing;
	}

	@Override
	public void setDrawing(MyDrawing drawing) {
		_drawing = drawing;
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
	public MyDrawingElementImpl<M, G> clone() {
		try {
			return (MyDrawingElementImpl<M, G>) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			// cannot happen since we are clonable
			return null;
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == getGraphicalRepresentation()) {
			if (getDrawing() != null) {
				getDrawing().setChanged();
			}
		}
	}

	@Override
	public void setChanged() {
		super.setChanged();
	}

}
