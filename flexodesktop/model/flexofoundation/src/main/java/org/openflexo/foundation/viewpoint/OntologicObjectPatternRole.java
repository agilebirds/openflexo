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
package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.technologyadapter.FlexoOntologyModelSlot;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public abstract class OntologicObjectPatternRole<T extends IFlexoOntologyObject> extends PatternRole<T> {

	public OntologicObjectPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	public boolean getIsPrimaryConceptRole() {
		if (getEditionPattern() == null) {
			return false;
		}
		return getEditionPattern().getPrimaryConceptRole() == this;
	}

	public void setIsPrimaryConceptRole(boolean isPrimary) {
		if (getEditionPattern() == null) {
			return;
		}
		if (isPrimary) {
			getEditionPattern().setPrimaryConceptRole(this);
		} else {
			getEditionPattern().setPrimaryConceptRole(null);
		}
	}

	@Override
	public boolean getIsPrimaryRole() {
		return getIsPrimaryConceptRole();
	}

	@Override
	public void setIsPrimaryRole(boolean isPrimary) {
		setIsPrimaryConceptRole(isPrimary);
	}

	@Override
	public FlexoOntologyModelSlot<?, ?> getModelSlot() {
		FlexoOntologyModelSlot<?, ?> returned = (FlexoOntologyModelSlot<?, ?>) super.getModelSlot();
		if (returned == null) {
			if (getViewPoint() != null && getViewPoint().getModelSlots(FlexoOntologyModelSlot.class).size() > 0) {
				return getViewPoint().getModelSlots(FlexoOntologyModelSlot.class).get(0);
			}
		}
		return returned;
	}
}
