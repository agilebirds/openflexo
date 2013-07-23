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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation.Parameters;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeParameters;
import org.openflexo.foundation.view.diagram.viewpoint.DropScheme;
import org.openflexo.foundation.view.diagram.viewpoint.LinkScheme;
import org.openflexo.foundation.view.diagram.viewpoint.ShapePatternRole;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.xml.DiagramBuilder;

public class DiagramShape extends DiagramElement<ShapeGraphicalRepresentation> {

	private static final Logger logger = Logger.getLogger(DiagramShape.class.getPackage().getName());

	// private String multilineText;
	private Vector<DiagramConnector> incomingConnectors;
	private Vector<DiagramConnector> outgoingConnectors;

	// private EditionPatternInstance editionPatternInstance;

	/**
	 * Constructor invoked during deserialization
	 * 
	 * @param componentDefinition
	 */
	public DiagramShape(DiagramBuilder builder) {
		this((Diagram) builder.vmInstance);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor for OEShema
	 * 
	 * @param shemaDefinition
	 */
	public DiagramShape(Diagram diagram) {
		super(diagram);
		incomingConnectors = new Vector<DiagramConnector>();
		outgoingConnectors = new Vector<DiagramConnector>();
	}

	@Override
	public void setDescription(String description) {
		super.setDescription(description);
	}

	@Override
	public ShapeGraphicalRepresentation<DiagramShape> getGraphicalRepresentation() {
		return super.getGraphicalRepresentation();
	}

	/**
	 * Reset graphical representation to be the one defined in related pattern role
	 */
	@Override
	public void resetGraphicalRepresentation() {
		getGraphicalRepresentation().setsWith(getPatternRole().getGraphicalRepresentation(), Parameters.text, Parameters.isVisible,
				Parameters.absoluteTextX, Parameters.absoluteTextY, ShapeParameters.x, ShapeParameters.y, ShapeParameters.width,
				ShapeParameters.height, ShapeParameters.relativeTextX, ShapeParameters.relativeTextY);
		refreshGraphicalRepresentation();
	}

	/**
	 * Refresh graphical representation
	 */
	@Override
	public void refreshGraphicalRepresentation() {
		super.refreshGraphicalRepresentation();
		getGraphicalRepresentation().updateConstraints();
		getGraphicalRepresentation().notifyShapeNeedsToBeRedrawn();
		getGraphicalRepresentation().notifyObjectHasMoved();
	}

	@Override
	public void delete() {
		if (getParent() != null) {
			getParent().removeFromChilds(this);
		}
		for (DiagramConnector c : incomingConnectors) {
			c.delete();
		}
		for (DiagramConnector c : outgoingConnectors) {
			c.delete();
		}
		super.delete();
		deleteObservers();
	}

	@Override
	public String getClassNameKey() {
		return "diagram_shape";
	}

	@Override
	public String getFullyQualifiedName() {
		return getDiagram().getFullyQualifiedName() + "." + getName();
	}

	public Vector<DiagramConnector> getIncomingConnectors() {
		return incomingConnectors;
	}

	public void setIncomingConnectors(Vector<DiagramConnector> incomingConnectors) {
		this.incomingConnectors = incomingConnectors;
	}

	public void addToIncomingConnectors(DiagramConnector connector) {
		incomingConnectors.add(connector);
	}

	public void removeFromIncomingConnectors(DiagramConnector connector) {
		incomingConnectors.remove(connector);
	}

	public Vector<DiagramConnector> getOutgoingConnectors() {
		return outgoingConnectors;
	}

	public void setOutgoingConnectors(Vector<DiagramConnector> outgoingConnectors) {
		this.outgoingConnectors = outgoingConnectors;
	}

	public void addToOutgoingConnectors(DiagramConnector connector) {
		outgoingConnectors.add(connector);
	}

	public void removeFromOutgoingConnectors(DiagramConnector connector) {
		outgoingConnectors.remove(connector);
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
		return "ShapeSpecification" + (getEditionPattern() != null ? " representing " + getEditionPattern() : "");
	}

	public static class DropAndLinkScheme {
		public DropAndLinkScheme(DropScheme dropScheme, LinkScheme linkScheme) {
			super();
			this.dropScheme = dropScheme;
			this.linkScheme = linkScheme;
		}

		public DropScheme dropScheme;
		public LinkScheme linkScheme;

	}

	public Vector<DropAndLinkScheme> getAvailableDropAndLinkSchemeFromThisShape(EditionPattern targetEditionPattern) {
		if (getEditionPattern() == null) {
			return null;
		}

		Vector<DropAndLinkScheme> availableLinkSchemeFromThisShape = null;

		ViewPoint viewPoint = getDiagram().getViewPoint();
		if (viewPoint == null) {
			return null;
		}

		availableLinkSchemeFromThisShape = new Vector<DropAndLinkScheme>();

		for (EditionPattern ep1 : getDiagramSpecification().getEditionPatterns()) {
			for (DropScheme ds : ep1.getDropSchemes()) {
				if (ds.getTargetEditionPattern() == targetEditionPattern || ds.getTopTarget() && targetEditionPattern == null) {
					for (EditionPattern ep2 : getDiagramSpecification().getEditionPatterns()) {
						for (LinkScheme ls : ep2.getLinkSchemes()) {
							// Let's directly reuse the code that exists in the LinkScheme instead of re-writing it here.
							if (ls.isValidTarget(ep2, ds.getEditionPattern()) && ls.getIsAvailableWithFloatingPalette()) {
								// This candidate is acceptable
								availableLinkSchemeFromThisShape.add(new DropAndLinkScheme(ds, ls));
							}
						}
					}
				}
			}
		}

		return availableLinkSchemeFromThisShape;
	}

	public Vector<LinkScheme> getAvailableLinkSchemeFromThisShape() {
		if (getEditionPattern() == null) {
			return null;
		}

		Vector<LinkScheme> availableLinkSchemeFromThisShape = null;

		ViewPoint calc = getDiagram().getViewPoint();
		if (calc == null) {
			return null;
		}

		availableLinkSchemeFromThisShape = new Vector<LinkScheme>();

		for (EditionPattern ep : getDiagramSpecification().getEditionPatterns()) {
			for (LinkScheme ls : ep.getLinkSchemes()) {
				if (ls.getFromTargetEditionPattern() != null && ls.getFromTargetEditionPattern().isAssignableFrom(getEditionPattern())
						&& ls.getIsAvailableWithFloatingPalette()) {
					// This candidate is acceptable
					availableLinkSchemeFromThisShape.add(ls);
				}
			}
		}

		return availableLinkSchemeFromThisShape;
	}

	@Override
	public ShapePatternRole getPatternRole() {
		return (ShapePatternRole) super.getPatternRole();
	}
}
