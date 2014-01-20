/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2013 Openflexo
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

package org.openflexo.technologyadapter.emf;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclareFetchRequests;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.viewpoint.EMFClassClassPatternRole;
import org.openflexo.technologyadapter.emf.viewpoint.EMFEnumClassPatternRole;

/**
 * Implementation of the ModelSlot class for the EMF technology adapter<br>
 * We expect here to connect an EMF meta model
 * 
 * @author sylvain
 * 
 */
@DeclarePatternRoles({ // All pattern roles available through this model slot
@DeclarePatternRole(FML = "EMFClassClass", patternRoleClass = EMFClassClassPatternRole.class),
		@DeclarePatternRole(FML = "EMFEnumClass", patternRoleClass = EMFEnumClassPatternRole.class) })
@DeclareEditionActions({ // All edition actions available through this model slot
})
@DeclareFetchRequests({ // All requests available through this model slot
})
public interface EMFMetaModelSlot extends ModelSlot<EMFMetaModel> {

	public abstract static class EMFMetaModelSlotImpl extends ModelSlotImpl<EMFMetaModel> implements EMFMetaModelSlot {

		private static final Logger logger = Logger.getLogger(EMFMetaModelSlot.class.getPackage().getName());

		/**
		 * 
		 * Constructor.
		 * 
		 * @param virtualModel
		 * @param adapter
		 */
		public EMFMetaModelSlotImpl(VirtualModel virtualModel, EMFTechnologyAdapter adapter) {
			super(virtualModel, adapter);
		}

		@Override
		public Class<EMFTechnologyAdapter> getTechnologyAdapterClass() {
			return EMFTechnologyAdapter.class;
		}

		/**
		 * Instanciate a new model slot instance configuration for this model slot
		 */
		@Override
		public EMFMetaModelSlotInstanceConfiguration createConfiguration(CreateVirtualModelInstance<?> action) {
			return new EMFMetaModelSlotInstanceConfiguration(this, action);
		}

		@Override
		public <PR extends PatternRole<?>> String defaultPatternRoleName(Class<PR> patternRoleClass) {
			if (EMFClassClassPatternRole.class.isAssignableFrom(patternRoleClass)) {
				return "class";
			} else if (EMFEnumClassPatternRole.class.isAssignableFrom(patternRoleClass)) {
				return "enum";
			}
			return super.defaultPatternRoleName(patternRoleClass);
		}

		@Override
		public Type getType() {
			return EMFMetaModel.class;
		}

		@Override
		public EMFTechnologyAdapter getTechnologyAdapter() {
			return (EMFTechnologyAdapter) super.getTechnologyAdapter();
		}

		@Override
		public String getURIForObject(ModelSlotInstance<? extends ModelSlot<EMFMetaModel>, EMFMetaModel> msInstance, Object o) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object retrieveObjectWithURI(ModelSlotInstance<? extends ModelSlot<EMFMetaModel>, EMFMetaModel> msInstance, String objectURI) {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
