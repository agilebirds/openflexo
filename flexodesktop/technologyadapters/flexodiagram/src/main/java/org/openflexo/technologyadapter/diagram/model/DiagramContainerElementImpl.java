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
package org.openflexo.technologyadapter.diagram.model;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.fge.GraphicalRepresentation;

public abstract class DiagramContainerElementImpl<G extends GraphicalRepresentation> extends DiagramElementImpl<G> implements
		DiagramContainerElement<G> {

	private void appendDescendants(DiagramContainerElement<?> current, List<DiagramElement<?>> descendants) {
		descendants.add(current);
		for (DiagramShape shape : current.getShapes()) {
			appendDescendants(shape, descendants);
		}
		for (DiagramConnector connector : current.getConnectors()) {
			descendants.add(connector);
		}
	}

	@Override
	public List<DiagramElement<?>> getDescendants() {
		List<DiagramElement<?>> descendants = new ArrayList<DiagramElement<?>>();
		appendDescendants(this, descendants);
		return descendants;
	}

}
