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
package org.openflexo.foundation.viewpoint;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.viewpoint.dm.CalcDrawingConnectorInserted;
import org.openflexo.foundation.viewpoint.dm.CalcDrawingConnectorRemoved;
import org.openflexo.foundation.viewpoint.dm.CalcDrawingShapeInserted;
import org.openflexo.foundation.viewpoint.dm.CalcDrawingShapeRemoved;
import org.openflexo.xmlcode.XMLMapping;

public abstract class ExampleDrawingObject extends ViewPointObject implements Bindable {

	private static final Logger logger = Logger.getLogger(ExampleDrawingObject.class.getPackage().getName());

	private String _name;
	private ExampleDrawingObject parent = null;
	private Vector<ExampleDrawingObject> childs;

	// We dont want to import graphical engine in foundation
	// But you can assert graphical representation is a org.openflexo.fge.GraphicalRepresentation.
	// For a CalcDrawingSchema, graphicalRepresentation is a DrawingGraphicalRepresentation
	// For a CalcDrawingShape, graphicalRepresentation is a ShapeGraphicalRepresentation
	// For a CalcDrawingConnector, graphicalRepresentation is a ConnectorGraphicalRepresentation
	private Object _graphicalRepresentation;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Never use this constructor except for ComponentLibrary
	 */
	public ExampleDrawingObject() {
		super();
		childs = new Vector<ExampleDrawingObject>();
	}

	@Override
	public void delete() {
		for (ExampleDrawingObject o : childs) {
			o.delete();
		}
		super.delete();
		childs.clear();
	}

	public ExampleDrawingShema getShema() {
		if (getParent() != null) {
			return getParent().getShema();
		}
		return null;
	}

	@Override
	public ViewPoint getCalc() {
		if (getShema() != null) {
			return getShema().getCalc();
		}
		return null;
	}

	@Override
	public XMLMapping getXMLMapping() {
		return getViewPointLibrary().get_EXAMPLE_DRAWING_MODEL();
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public void setName(String name) {
		if (requireChange(_name, name)) {
			String oldName = _name;
			_name = name;
			setChanged();
			notifyObservers(new NameChanged(oldName, name));
		}
	}

	public Vector<ExampleDrawingObject> getChilds() {
		return childs;
	}

	public void setChilds(Vector<ExampleDrawingObject> someChilds) {
		childs.addAll(someChilds);
	}

	public void addToChilds(ExampleDrawingObject aChild) {
		// logger.info("****** addToChild() put "+aChild+" under "+this);
		childs.add(aChild);
		aChild.parent = this;
		setChanged();
		if (aChild instanceof ExampleDrawingShape) {
			notifyObservers(new CalcDrawingShapeInserted((ExampleDrawingShape) aChild, this));
		}
		if (aChild instanceof ExampleDrawingConnector) {
			notifyObservers(new CalcDrawingConnectorInserted((ExampleDrawingConnector) aChild, this));
		}
	}

	public void removeFromChilds(ExampleDrawingObject aChild) {
		childs.remove(aChild);
		aChild.parent = null;
		setChanged();
		if (aChild instanceof ExampleDrawingShape) {
			notifyObservers(new CalcDrawingShapeRemoved((ExampleDrawingShape) aChild, this));
		}
		if (aChild instanceof ExampleDrawingConnector) {
			notifyObservers(new CalcDrawingConnectorRemoved((ExampleDrawingConnector) aChild, this));
		}
	}

	public Object getGraphicalRepresentation() {
		return _graphicalRepresentation;
	}

	public void setGraphicalRepresentation(Object graphicalRepresentation) {
		_graphicalRepresentation = graphicalRepresentation;
		// setChanged();
	}

	private Vector<ExampleDrawingObject> ancestors;

	public ExampleDrawingObject getParent() {
		return parent;
	}

	public Vector<ExampleDrawingObject> getAncestors() {
		if (ancestors == null) {
			ancestors = new Vector<ExampleDrawingObject>();
			ExampleDrawingObject current = getParent();
			while (current != null) {
				ancestors.add(current);
				current = current.getParent();
			}
		}
		return ancestors;
	}

	public static ExampleDrawingObject getFirstCommonAncestor(ExampleDrawingObject child1, ExampleDrawingObject child2) {
		Vector<ExampleDrawingObject> ancestors1 = child1.getAncestors();
		Vector<ExampleDrawingObject> ancestors2 = child2.getAncestors();
		for (int i = 0; i < ancestors1.size(); i++) {
			ExampleDrawingObject o1 = ancestors1.elementAt(i);
			if (ancestors2.contains(o1)) {
				return o1;
			}
		}
		return null;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (getShema() != null && !ignoreNotifications()) {
			getShema().setIsModified();
		}
	}

	public abstract boolean isContainedIn(ExampleDrawingObject o);

	public final boolean contains(ExampleDrawingObject o) {
		return o.isContainedIn(this);
	}

	@Override
	public BindingModel getBindingModel() {
		return getCalc().getBindingModel();
	}
}
