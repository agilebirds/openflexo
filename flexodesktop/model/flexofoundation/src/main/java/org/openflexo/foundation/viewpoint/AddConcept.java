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

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.FlexoOntologyModelSlot;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.logging.FlexoLogger;

public abstract class AddConcept<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, T> extends AssignableAction<M, MM, T> {

	protected static final Logger logger = FlexoLogger.getLogger(AddConcept.class.getPackage().getName());

	public AddConcept(ViewPointBuilder builder) {
		super(builder);
	}

	public abstract IFlexoOntologyClass getOntologyClass();

	public abstract void setOntologyClass(IFlexoOntologyClass ontologyClass);

	/*public IFlexoOntologyConcept getOntologyObject(FlexoProject project)
	{
		getCalc().loadWhenUnloaded();
		if (StringUtils.isEmpty(getConceptURI())) return null;
		return project.getOntologyLibrary().getOntologyObject(getConceptURI());
	}*/

	/*@Override
	public R getPatternRole() {
		try {
			return super.getPatternRole();
		} catch (ClassCastException e) {
			logger.warning("Unexpected pattern role type");
			setPatternRole(null);
			return null;
		}
	}*/

	// FIXME: if we remove this useless code, some FIB won't work (see EditionPatternView.fib, inspect an AddIndividual)
	// Need to be fixed in KeyValueProperty.java
	/*@Override
	public void setPatternRole(R patternRole) {
		super.setPatternRole(patternRole);
	}*/

	@Override
	public abstract Type getAssignableType();

	/**
	 * Overrides parent method by returning default model slot if model slot is not defined for this action
	 */
	@Override
	public ModelSlot<M, MM> getModelSlot() {
		ModelSlot<M, MM> returned = super.getModelSlot();
		if (returned == null && getViewPoint() != null) {
			List<FlexoOntologyModelSlot> msList = getViewPoint().getModelSlots(FlexoOntologyModelSlot.class);
			if (msList.size() > 0) {
				return msList.get(0);
			}
		}
		return returned;
	}

}
