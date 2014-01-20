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
package org.openflexo.technologyadapter.powerpoint.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlide;

@ModelEntity
@ImplementationClass(PowerpointSlidePatternRole.PowerpointSlidePatternRoleImpl.class)
@XMLElement
public interface PowerpointSlidePatternRole extends PatternRole<PowerpointSlide> {

	public static abstract class PowerpointSlidePatternRoleImpl extends PatternRoleImpl<PowerpointSlide> {

		@Override
		public Type getType() {
			return PowerpointSlide.class;
		}

		@Override
		public String getPreciseType() {
			return PowerpointSlide.class.getSimpleName();
		}

		@Override
		public boolean defaultBehaviourIsToBeDeleted() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public ActorReference<PowerpointSlide> makeActorReference(PowerpointSlide object, EditionPatternInstance epi) {
			return new PowerpointActorReference(object, this, epi);
		}

	}

}
