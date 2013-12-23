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

import java.util.Observable;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.technologyadapter.diagram.fml.DiagramEditionScheme;

public abstract class DiagramElementImpl<G extends GraphicalRepresentation> extends FlexoObjectImpl implements DiagramElement<G> {

	public DiagramElementImpl() {
	}

	@Override
	public Diagram getDiagram() {
		if (getParent() != null) {
			if (getParent() instanceof Diagram) {
				return (Diagram) getParent();
			} else {
				return getParent().getDiagram();
			}
		}
		return null;
	}

	@Override
	public boolean isContainedIn(DiagramElement<?> element) {
		if (element == this) {
			return true;
		}
		if (getParent() != null && getParent() == element) {
			return true;
		}
		if (getParent() != null) {
			return getParent().isContainedIn(element);
		}
		return false;
	}

	@Override
	public DiagramElement<G> clone() {
		return (DiagramElement<G>) cloneObject();

	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == getGraphicalRepresentation()) {
			if (getDiagram() != null) {
				getDiagram().setChanged();
			}
		}
	}

	/*@Override
	public void setChanged() {
		super.setChanged();
	}*/

	/*@Override
	public String getName() {
		// System.out.println("On me demande mon nom, je retourne " + performSuperGetter(NAME));
		return (String) performSuperGetter(NAME);
	}*/

	@Override
	public synchronized void setChanged() {
		super.setChanged();
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals(DiagramEditionScheme.DIAGRAM)
				|| variable.getVariableName().equals(DiagramEditionScheme.TOP_LEVEL)) {
			return this;
		}
		return null;
	}

}
