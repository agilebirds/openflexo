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
package org.openflexo.foundation.view;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.viewpoint.DropScheme;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.LinkScheme;
import org.openflexo.foundation.viewpoint.ShapePatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.xml.VEShemaBuilder;

public class ViewShape extends ViewElement {

	private static final Logger logger = Logger.getLogger(ViewShape.class.getPackage().getName());

	// private String multilineText;
	private Vector<ViewConnector> incomingConnectors;
	private Vector<ViewConnector> outgoingConnectors;

	// private EditionPatternInstance editionPatternInstance;

	/**
	 * Constructor invoked during deserialization
	 * 
	 * @param componentDefinition
	 */
	public ViewShape(VEShemaBuilder builder) {
		this(builder.shema);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor for OEShema
	 * 
	 * @param shemaDefinition
	 */
	public ViewShape(View shema) {
		super(shema);
		incomingConnectors = new Vector<ViewConnector>();
		outgoingConnectors = new Vector<ViewConnector>();
	}

	@Override
	public void setDescription(String description) {
		super.setDescription(description);
	}

	@Override
	public ShapeGraphicalRepresentation<ViewShape> getGraphicalRepresentation() {
		return (ShapeGraphicalRepresentation<ViewShape>) super.getGraphicalRepresentation();
	}

	/**
	 * Reset graphical representation to be the one defined in related pattern role
	 */
	@Override
	public void resetGraphicalRepresentation() {
		getGraphicalRepresentation().setsWith(getPatternRole().getGraphicalRepresentation(), GraphicalRepresentation.Parameters.text,
				GraphicalRepresentation.Parameters.isVisible, GraphicalRepresentation.Parameters.absoluteTextX,
				GraphicalRepresentation.Parameters.absoluteTextY, ShapeGraphicalRepresentation.Parameters.x,
				ShapeGraphicalRepresentation.Parameters.y, ShapeGraphicalRepresentation.Parameters.width,
				ShapeGraphicalRepresentation.Parameters.height, ShapeGraphicalRepresentation.Parameters.relativeTextX,
				ShapeGraphicalRepresentation.Parameters.relativeTextY);
		applyGraphicalElementSpecifications();
	}

	/**
	 * Refresh graphical representation
	 */
	@Override
	public void refreshGraphicalRepresentation() {
		super.refreshGraphicalRepresentation();
		getGraphicalRepresentation().updateConstraints();
		getGraphicalRepresentation().notifyShapeNeedsToBeRedrawn();
	}

	@Override
	public void delete() {
		if (getParent() != null) {
			getParent().removeFromChilds(this);
		}
		for (ViewConnector c : incomingConnectors) {
			c.delete();
		}
		for (ViewConnector c : outgoingConnectors) {
			c.delete();
		}
		super.delete();
		deleteObservers();
	}

	@Override
	public String getClassNameKey() {
		return "oe_shape";
	}

	@Override
	public String getFullyQualifiedName() {
		return getShema().getFullyQualifiedName() + "." + getName();
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VE.OE_SHAPE_INSPECTOR;
	}

	/*@Override
	public AddShemaElementAction getEditionAction() 
	{
		return getAddShapeAction();
	}
	
	public AddShape getAddShapeAction()
	{
		if (getEditionPattern() != null && getPatternRole() != null)
			return getEditionPattern().getAddShapeAction(getPatternRole());
		return null;
	}*/

	/*public String getMultilineText() 
	{
		return multilineText;
	}

	public void setMultilineText(String multilineText) 
	{
		this.multilineText = multilineText;
	}*/

	public Vector<ViewConnector> getIncomingConnectors() {
		return incomingConnectors;
	}

	public void setIncomingConnectors(Vector<ViewConnector> incomingConnectors) {
		this.incomingConnectors = incomingConnectors;
	}

	public void addToIncomingConnectors(ViewConnector connector) {
		incomingConnectors.add(connector);
	}

	public void removeFromIncomingConnectors(ViewConnector connector) {
		incomingConnectors.remove(connector);
	}

	public Vector<ViewConnector> getOutgoingConnectors() {
		return outgoingConnectors;
	}

	public void setOutgoingConnectors(Vector<ViewConnector> outgoingConnectors) {
		this.outgoingConnectors = outgoingConnectors;
	}

	public void addToOutgoingConnectors(ViewConnector connector) {
		outgoingConnectors.add(connector);
	}

	public void removeFromOutgoingConnectors(ViewConnector connector) {
		outgoingConnectors.remove(connector);
	}

	@Override
	public boolean isContainedIn(ViewObject o) {
		if (o == this) {
			return true;
		}
		if ((getParent() != null) && (getParent() == o)) {
			return true;
		}
		if (getParent() != null) {
			return getParent().isContainedIn(o);
		}
		return false;
	}

	@Override
	public String getDisplayableDescription() {
		return "Shape" + (getEditionPattern() != null ? " representing " + getEditionPattern() : "");
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

		ViewPoint calc = getShema().getCalc();
		if (calc == null) {
			return null;
		}
		calc.loadWhenUnloaded();

		availableLinkSchemeFromThisShape = new Vector<DropAndLinkScheme>();

		for (EditionPattern ep1 : calc.getEditionPatterns()) {
			for (DropScheme ds : ep1.getDropSchemes()) {
				if (ds.getTargetEditionPattern() == targetEditionPattern || (ds.getTopTarget() && targetEditionPattern == null)) {
					for (EditionPattern ep2 : calc.getEditionPatterns()) {
						for (LinkScheme ls : ep2.getLinkSchemes()) {
							if (ls.getFromTargetEditionPattern().isAssignableFrom(getEditionPattern())
									&& ls.getToTargetEditionPattern().isAssignableFrom(ds.getEditionPattern())
									&& ls.getIsAvailableWithFloatingPalette()) {
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

		ViewPoint calc = getShema().getCalc();
		if (calc == null) {
			return null;
		}
		calc.loadWhenUnloaded();

		availableLinkSchemeFromThisShape = new Vector<LinkScheme>();

		for (EditionPattern ep : calc.getEditionPatterns()) {
			for (LinkScheme ls : ep.getLinkSchemes()) {
				if (ls.getFromTargetEditionPattern().isAssignableFrom(getEditionPattern()) && ls.getIsAvailableWithFloatingPalette()) {
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
