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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.view.diagram.viewpoint.action.GRShapeTemplate;

public class ExampleDiagramShape extends ExampleDiagramObject implements GRShapeTemplate {

	private static final Logger logger = Logger.getLogger(ExampleDiagramShape.class.getPackage().getName());

	// private String multilineText;
	private Vector<ExampleDiagramConnector> incomingConnectors;
	private Vector<ExampleDiagramConnector> outgoingConnectors;

	// private EditionPatternInstance editionPatternInstance;

	/**
	 * Constructor invoked during deserialization
	 * 
	 * @param componentDefinition
	 */
	public ExampleDiagramShape(ExampleDiagramBuilder builder) {
		super(builder);
		incomingConnectors = new Vector<ExampleDiagramConnector>();
		outgoingConnectors = new Vector<ExampleDiagramConnector>();
	}

	@Override
	public void delete() {
		if (getParent() != null) {
			getParent().removeFromChilds(this);
		}
		for (ExampleDiagramConnector c : incomingConnectors) {
			c.delete();
		}
		for (ExampleDiagramConnector c : outgoingConnectors) {
			c.delete();
		}
		super.delete();
		deleteObservers();
	}

	@Override
	public ShapeGraphicalRepresentation<?> getGraphicalRepresentation() {
		return (ShapeGraphicalRepresentation<?>) super.getGraphicalRepresentation();
	}

	@Override
	public String getFullyQualifiedName() {
		return getExampleDiagram().getFullyQualifiedName() + "." + getName();
	}

	public Vector<ExampleDiagramConnector> getIncomingConnectors() {
		return incomingConnectors;
	}

	public void setIncomingConnectors(Vector<ExampleDiagramConnector> incomingConnectors) {
		this.incomingConnectors = incomingConnectors;
	}

	public void addToIncomingConnectors(ExampleDiagramConnector connector) {
		incomingConnectors.add(connector);
	}

	public void removeFromIncomingConnectors(ExampleDiagramConnector connector) {
		incomingConnectors.remove(connector);
	}

	public Vector<ExampleDiagramConnector> getOutgoingConnectors() {
		return outgoingConnectors;
	}

	public void setOutgoingConnectors(Vector<ExampleDiagramConnector> outgoingConnectors) {
		this.outgoingConnectors = outgoingConnectors;
	}

	public void addToOutgoingConnectors(ExampleDiagramConnector connector) {
		outgoingConnectors.add(connector);
	}

	public void removeFromOutgoingConnectors(ExampleDiagramConnector connector) {
		outgoingConnectors.remove(connector);
	}

	@Override
	public boolean isContainedIn(ExampleDiagramObject o) {
		if (o == this) {
			return true;
		}
		if (getParent() != null && getParent() == o) {
			return true;
		}
		if (getParent() != null) {
			return getParent().isContainedIn(o);
		}
		return false;
	}

	@Override
	public DiagramSpecification getDiagramSpecification() {
		return getExampleDiagram().getDiagramSpecification();
	}

}
