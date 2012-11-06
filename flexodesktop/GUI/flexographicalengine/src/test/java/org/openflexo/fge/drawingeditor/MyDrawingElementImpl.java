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

import java.util.Observable;
import java.util.Vector;

import javax.inject.Inject;

import org.openflexo.fge.GraphicalRepresentation;

public abstract class MyDrawingElementImpl<M extends MyDrawingElement<M, G>, G extends GraphicalRepresentation<M>> extends Observable
		implements MyDrawingElement<M, G> {

	private MyDrawing _drawing;
	private Vector<MyDrawingElement/*<?, ?>*/> childs;

	@Inject
	private G graphicalRepresentation;

	public MyDrawingElementImpl(MyDrawing drawing) {
		childs = new Vector<MyDrawingElement/*<?, ?>*/>();
		_drawing = drawing;
	}

	@Override
	public Vector<MyDrawingElement/*<?, ?>*/> getChilds() {
		return childs;
	}

	@Override
	public void setChilds(Vector<MyDrawingElement/*<?, ?>*/> someChilds) {
		childs.addAll(someChilds);
	}

	@Override
	public void addToChilds(MyDrawingElement/*<?, ?>*/aChild) {
		childs.add(aChild);
		// System.out.println("Add "+aChild+" isDeserializing="+isDeserializing());
		if (!isDeserializing()) {
			getDrawing().getEditedDrawing().addDrawable(aChild, this);
		}
	}

	@Override
	public void removeFromChilds(MyDrawingElement/*<?, ?>*/aChild) {
		childs.remove(aChild);
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
		graphicalRepresentation.setDrawable((M) this);
		graphicalRepresentation.addObserver(this);
	}

	private boolean isDeserializing = false;

	@Override
	public void initializeDeserialization() {
		isDeserializing = true;
	}

	@Override
	public void finalizeDeserialization() {
		isDeserializing = false;
		/*for (MyDrawingElement e : childs) {
			getDrawing().getEditedDrawing().addDrawable(e, this);
		}*/
	}

	@Override
	public boolean isDeserializing() {
		return isDeserializing;
	}

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
			getDrawing().setChanged();
		}
	}

	@Override
	public void setChanged() {
		super.setChanged();
	}

}
