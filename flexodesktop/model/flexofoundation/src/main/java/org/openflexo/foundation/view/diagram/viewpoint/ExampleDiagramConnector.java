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

import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.foundation.view.diagram.viewpoint.action.GRConnectorTemplate;

public class ExampleDiagramConnector extends ExampleDiagramObject implements GRConnectorTemplate {

	private static final Logger logger = Logger.getLogger(ExampleDiagramConnector.class.getPackage().getName());

	private ExampleDiagramShape startShape;
	private ExampleDiagramShape endShape;

	/**
	 * Constructor invoked during deserialization
	 * 
	 * @param componentDefinition
	 */
	public ExampleDiagramConnector(ExampleDiagramBuilder builder) {
		super(builder);
	}

	/**
	 * Common constructor for OEShema
	 * 
	 * @param shemaDefinition
	 */
	public ExampleDiagramConnector(ExampleDiagramShape aStartShape, ExampleDiagramShape anEndShape) {
		super(null);
		setStartShape(aStartShape);
		setEndShape(anEndShape);
	}

	@Override
	public boolean delete() {
		if (getParent() != null) {
			getParent().removeFromChilds(this);
		}
		super.delete();
		deleteObservers();
		return true;
	}

	@Override
	public ConnectorGraphicalRepresentation<?> getGraphicalRepresentation() {
		return (ConnectorGraphicalRepresentation<?>) super.getGraphicalRepresentation();
	}

	@Override
	public String getFullyQualifiedName() {
		return getExampleDiagram().getFullyQualifiedName() + "." + getName();
	}

	public ExampleDiagramShape getEndShape() {
		return endShape;
	}

	public void setEndShape(ExampleDiagramShape endShape) {
		this.endShape = endShape;
		endShape.addToIncomingConnectors(this);
	}

	public ExampleDiagramShape getStartShape() {
		return startShape;
	}

	public void setStartShape(ExampleDiagramShape startShape) {
		this.startShape = startShape;
		startShape.addToOutgoingConnectors(this);
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
