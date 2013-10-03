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
package org.openflexo.foundation.view.diagram.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.view.diagram.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.action.GRConnectorTemplate;
import org.openflexo.foundation.xml.DiagramBuilder;

public class DiagramConnector extends DiagramElement<ConnectorGraphicalRepresentation> implements GRConnectorTemplate {

	private static final Logger logger = Logger.getLogger(DiagramShape.class.getPackage().getName());

	private DiagramShape startShape;
	private DiagramShape endShape;

	/**
	 * Constructor invoked during deserialization
	 * 
	 * @param componentDefinition
	 */
	public DiagramConnector(DiagramBuilder builder) {
		this((Diagram) builder.vmInstance);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor for OEShema
	 * 
	 * @param shemaDefinition
	 */
	public DiagramConnector(Diagram diagram) {
		super(diagram);
	}

	/**
	 * Common constructor for OEShema
	 * 
	 * @param shemaDefinition
	 */
	public DiagramConnector(Diagram diagram, DiagramShape aStartShape, DiagramShape anEndShape) {
		super(diagram);
		setStartShape(aStartShape);
		setEndShape(anEndShape);
	}

	/**
	 * Reset graphical representation to be the one defined in related pattern role
	 */
	@Override
	public void resetGraphicalRepresentation() {
		getGraphicalRepresentation().setsWith(getPatternRole().getGraphicalRepresentation(), GraphicalRepresentation.TEXT,
				GraphicalRepresentation.IS_VISIBLE, GraphicalRepresentation.TRANSPARENCY, GraphicalRepresentation.ABSOLUTE_TEXT_X,
				GraphicalRepresentation.ABSOLUTE_TEXT_Y);
		refreshGraphicalRepresentation();
	}

	/**
	 * Refresh graphical representation
	 */
	@Override
	public void refreshGraphicalRepresentation() {
		super.refreshGraphicalRepresentation();
		getGraphicalRepresentation().notifyConnectorChanged();
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

	/* @Override
	 public AddSchemaElementAction getEditionAction() 
	 {
	 	return getAddConnectorAction();
	 }
	 
	public AddConnector getAddConnectorAction()
	{
		if (getEditionPattern() != null && getPatternRole() != null)
			return getEditionPattern().getAddConnectorAction(getPatternRole());
		return null;
	}*/

	@Override
	public String getClassNameKey() {
		return "oe_connector";
	}

	@Override
	public String getFullyQualifiedName() {
		return getParent().getFullyQualifiedName() + "." + getName();
	}

	public DiagramShape getEndShape() {
		return endShape;
	}

	public void setEndShape(DiagramShape endShape) {
		this.endShape = endShape;
		// NPE Protection
		if (endShape != null) {
			endShape.addToIncomingConnectors(this);
		}
	}

	public DiagramShape getStartShape() {
		return startShape;
	}

	public void setStartShape(DiagramShape startShape) {
		this.startShape = startShape;
		startShape.addToOutgoingConnectors(this);
	}

	@Override
	public boolean isContainedIn(DiagramElement<?> o) {
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
	public String getDisplayableDescription() {
		return "ConnectorSpecification" + (getEditionPattern() != null ? " representing " + getEditionPattern() : "");
	}

	@Override
	public ConnectorPatternRole getPatternRole() {
		return (ConnectorPatternRole) super.getPatternRole();
	}

	private List<DiagramElement<?>> descendants;

	@Override
	public List<DiagramElement<?>> getDescendants() {
		if (descendants == null) {
			descendants = new ArrayList<DiagramElement<?>>();
			appendDescendants(this, descendants);
		}
		return descendants;
	}

	private void appendDescendants(DiagramElement<?> current, List<DiagramElement<?>> descendants) {
		descendants.add(current);
		for (DiagramElement<?> child : current.getChilds()) {
			if (child != current) {
				appendDescendants(child, descendants);
			}
		}
	}

}
