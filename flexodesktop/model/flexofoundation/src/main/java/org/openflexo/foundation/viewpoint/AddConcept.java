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
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.view.TypeAwareModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

@ModelEntity(isAbstract = true)
@ImplementationClass(AddConcept.AddConceptImpl.class)
public abstract interface AddConcept<MS extends TypeAwareModelSlot<?, ?>, T> extends AssignableAction<MS, T> {

	public static abstract class AddConceptImpl<MS extends TypeAwareModelSlot<?, ?>, T> extends AssignableActionImpl<MS, T> implements
			AddConcept<MS, T> {

		protected static final Logger logger = FlexoLogger.getLogger(AddConcept.class.getPackage().getName());

		public AddConceptImpl() {
			super();
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
		public MS getModelSlot() {
			MS returned = super.getModelSlot();
			if (returned == null && getVirtualModel() != null) {
				@SuppressWarnings("rawtypes")
				List<TypeAwareModelSlot> msList = getVirtualModel().getModelSlots(TypeAwareModelSlot.class);
				if (msList.size() > 0) {
					return (MS) msList.get(0);
				}
			}
			return returned;
		}

		@Override
		public TypeAwareModelSlotInstance<?, ?, MS> getModelSlotInstance(EditionSchemeAction action) {
			return (TypeAwareModelSlotInstance<?, ?, MS>) super.getModelSlotInstance(action);
		}

	}
}
