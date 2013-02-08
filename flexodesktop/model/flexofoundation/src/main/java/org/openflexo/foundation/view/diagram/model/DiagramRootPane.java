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

import java.util.logging.Logger;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.foundation.xml.VirtualModelInstanceBuilder;

public class DiagramRootPane extends DiagramElement<DrawingGraphicalRepresentation> {

	private static final Logger logger = Logger.getLogger(DiagramRootPane.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization
	 * 
	 * @param componentDefinition
	 */
	public DiagramRootPane(VirtualModelInstanceBuilder builder) {
		this((Diagram) builder.vmInstance);
		initializeDeserialization(builder);
	}

	/**
	 * Explicit constructor for root pane
	 * 
	 * @param shemaDefinition
	 */
	public DiagramRootPane(Diagram diagram) {
		super(diagram);
		setGraphicalRepresentation(new DrawingGraphicalRepresentation());
	}

	@Override
	public boolean isContainedIn(DiagramElement<?> o) {
		return o == this;
	}

	@Override
	public String getDisplayableDescription() {
		return "RootPane " + (getEditionPattern() != null ? " representing " + getEditionPattern() : "");
	}

	@Override
	public void resetGraphicalRepresentation() {
		logger.warning("Please implement this");
	}

	@Override
	public String getFullyQualifiedName() {
		return getDiagram().getFullyQualifiedName() + "." + getName();
	}

	@Override
	public String getClassNameKey() {
		return "diagram_root_pane";
	}

}
