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
package org.openflexo.fme.model;

import org.openflexo.fge.GraphicalRepresentation;

public abstract class DiagramElementImpl<M extends DiagramElement<M, G>, G extends GraphicalRepresentation> implements DiagramElement<M, G> {

	public DiagramElementImpl() {
	}

	@Override
	public void setName(String aName) {
		System.out.println("Set new name: " + aName);
		performSuperSetter(NAME, aName);
	}

	@Override
	public Diagram getDiagram() {
		if (this instanceof Diagram) {
			return (Diagram) this;
		}
		if (getContainer() != null) {
			return getContainer().getDiagram();
		}
		return null;
	}
}
