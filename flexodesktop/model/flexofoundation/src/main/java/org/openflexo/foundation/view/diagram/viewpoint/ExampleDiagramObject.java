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

import java.util.Collection;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.view.diagram.rm.ExampleDiagramResource;
import org.openflexo.foundation.viewpoint.NamedViewPointObject;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.dm.ExampleDiagramConnectorInserted;
import org.openflexo.foundation.viewpoint.dm.ExampleDiagramConnectorRemoved;
import org.openflexo.foundation.viewpoint.dm.ExampleDiagramShapeInserted;
import org.openflexo.foundation.viewpoint.dm.ExampleDiagramShapeRemoved;

public abstract class ExampleDiagramObject extends NamedViewPointObject implements Bindable {

	private static final Logger logger = Logger.getLogger(ExampleDiagramObject.class.getPackage().getName());

	private ExampleDiagramObject parent = null;
	private Vector<ExampleDiagramObject> childs;

	// But you can assert graphical representation is a org.openflexo.fge.GraphicalRepresentation.
	// For a CalcDrawingSchema, graphicalRepresentation is a DrawingGraphicalRepresentation
	// For a CalcDrawingShape, graphicalRepresentation is a ShapeGraphicalRepresentation
	// For a CalcDrawingConnector, graphicalRepresentation is a ConnectorGraphicalRepresentation
	private GraphicalRepresentation<?> _graphicalRepresentation;

	public static class ExampleDiagramBuilder {
		public DiagramSpecification diagramSpecification;
		public ExampleDiagram exampleDiagram;
		public ExampleDiagramResource resource;

		public ExampleDiagramBuilder(DiagramSpecification diagramSpecification, ExampleDiagramResource resource) {
			this.diagramSpecification = diagramSpecification;
			this.resource = resource;
		}

	}

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Never use this constructor except for ComponentLibrary
	 */
	public ExampleDiagramObject(ExampleDiagramBuilder builder) {
		super(builder);
		childs = new Vector<ExampleDiagramObject>();
	}

	@Override
	public String getURI() {
		return null;
	}

	@Override
	public Collection<ExampleDiagramObject> getEmbeddedValidableObjects() {
		return getChilds();
	}

	@Override
	public void delete() {
		Vector<ExampleDiagramObject> toRemove = new Vector<ExampleDiagramObject>(childs);
		for (ExampleDiagramObject o : toRemove) {
			o.delete();
		}
		super.delete();
		childs.clear();
	}

	public ExampleDiagram getExampleDiagram() {
		if (getParent() != null) {
			return getParent().getExampleDiagram();
		}
		return null;
	}

	@Override
	public ViewPoint getViewPoint() {
		if (getVirtualModel() != null) {
			return getVirtualModel().getViewPoint();
		}
		return null;
	}

	public DiagramSpecification getVirtualModel() {
		if (getExampleDiagram() != null) {
			return getExampleDiagram().getVirtualModel();
		}
		return null;
	}

	public Vector<ExampleDiagramObject> getChilds() {
		return childs;
	}

	public void setChilds(Vector<ExampleDiagramObject> someChilds) {
		childs.addAll(someChilds);
	}

	public void addToChilds(ExampleDiagramObject aChild) {
		// logger.info("****** addToChild() put "+aChild+" under "+this);
		childs.add(aChild);
		aChild.parent = this;
		setChanged();
		if (aChild instanceof ExampleDiagramShape) {
			notifyObservers(new ExampleDiagramShapeInserted((ExampleDiagramShape) aChild, this));
		}
		if (aChild instanceof ExampleDiagramConnector) {
			notifyObservers(new ExampleDiagramConnectorInserted((ExampleDiagramConnector) aChild, this));
		}
	}

	public void removeFromChilds(ExampleDiagramObject aChild) {
		childs.remove(aChild);
		aChild.parent = null;
		setChanged();
		if (aChild instanceof ExampleDiagramShape) {
			notifyObservers(new ExampleDiagramShapeRemoved((ExampleDiagramShape) aChild, this));
		}
		if (aChild instanceof ExampleDiagramConnector) {
			notifyObservers(new ExampleDiagramConnectorRemoved((ExampleDiagramConnector) aChild, this));
		}
	}

	public GraphicalRepresentation<?> getGraphicalRepresentation() {
		return _graphicalRepresentation;
	}

	public void setGraphicalRepresentation(GraphicalRepresentation<?> graphicalRepresentation) {
		_graphicalRepresentation = graphicalRepresentation;
		// setChanged();
	}

	private Vector<ExampleDiagramObject> ancestors;
	private Vector<ExampleDiagramObject> descendants;

	public ExampleDiagramObject getParent() {
		return parent;
	}

	public Vector<ExampleDiagramObject> getAncestors() {
		if (ancestors == null) {
			ancestors = new Vector<ExampleDiagramObject>();
			ExampleDiagramObject current = getParent();
			while (current != null) {
				ancestors.add(current);
				current = current.getParent();
			}
		}
		return ancestors;
	}

	public Vector<ExampleDiagramObject> getDescendants() {
		if (descendants == null) {
			descendants = new Vector<ExampleDiagramObject>();
			appendDescendants(this, descendants);
		}
		return descendants;
	}

	private void appendDescendants(ExampleDiagramObject current, Vector<ExampleDiagramObject> descendants) {
		descendants.add(current);
		for (ExampleDiagramObject child : current.getChilds()) {
			if (child != current) {
				appendDescendants(child, descendants);
			}
		}
	}

	public static ExampleDiagramObject getFirstCommonAncestor(ExampleDiagramObject child1, ExampleDiagramObject child2) {
		Vector<ExampleDiagramObject> ancestors1 = child1.getAncestors();
		Vector<ExampleDiagramObject> ancestors2 = child2.getAncestors();
		for (int i = 0; i < ancestors1.size(); i++) {
			ExampleDiagramObject o1 = ancestors1.elementAt(i);
			if (ancestors2.contains(o1)) {
				return o1;
			}
		}
		return null;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (getExampleDiagram() != null && !ignoreNotifications()) {
			getExampleDiagram().setIsModified();
		}
	}

	public abstract boolean isContainedIn(ExampleDiagramObject o);

	public final boolean contains(ExampleDiagramObject o) {
		return o.isContainedIn(this);
	}

	@Override
	public BindingModel getBindingModel() {
		return getVirtualModel().getBindingModel();
	}

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		return "<not_implemented:" + getFullyQualifiedName() + ">";
	}

}
