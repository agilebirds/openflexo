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
package org.openflexo.fge;

import java.util.List;

/**
 * This interface is implemented by all objects representing a graphical drawing, that is a complex graphical representation involving an
 * object tree where all objects have their own graphical representation.
 * 
 * To perform this, two major features are required here:
 * <ul>
 * <li>First, a
 * 
 * <pre>
 * Drawing
 * </pre>
 * 
 * must indicate how to map a given object (called drawable) to its graphical representation (@see
 * {@link #getGraphicalRepresentation(Object)})</li>
 * <li>Then, this
 * 
 * <pre>
 * Drawing
 * </pre>
 * 
 * must encode the objects hierarchy, by implementing following methods: (@see {@link #getContainer(Object)} and @see
 * {@link #getContainedObjects(Object)})</li>
 * </ul>
 * 
 * Note that at top level, this drawing is associated with its own {@link GraphicalRepresentation} which is this case is a
 * {@link DrawingGraphicalRepresentation<M>} of
 * 
 * <pre>
 * M
 * </pre>
 * 
 * .
 * 
 * To implement those schemes, note that there is a default implementation {@link DefaultDrawing}.
 * 
 * @author sylvain
 * 
 * @param <M>
 *            Type of object which is handled as root object
 */
public interface Drawing<M> {
	public M getModel();

	public <O> GraphicalRepresentation<O> getGraphicalRepresentation(O aDrawable);

	public Object getContainer(Object aDrawable);

	public List<?> getContainedObjects(Object aDrawable);

	public DrawingGraphicalRepresentation<M> getDrawingGraphicalRepresentation();

	public boolean isEditable();

}
