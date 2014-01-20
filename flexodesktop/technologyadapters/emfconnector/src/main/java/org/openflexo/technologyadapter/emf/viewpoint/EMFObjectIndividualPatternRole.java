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
package org.openflexo.technologyadapter.emf.viewpoint;

import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;

@ModelEntity
@ImplementationClass(EMFObjectIndividualPatternRole.EMFObjectIndividualPatternRoleImpl.class)
@XMLElement
public interface EMFObjectIndividualPatternRole extends IndividualPatternRole<EMFObjectIndividual> {

	public static abstract class EMFObjectIndividualPatternRoleImpl extends IndividualPatternRoleImpl<EMFObjectIndividual> implements
			EMFObjectIndividualPatternRole {

		public EMFObjectIndividualPatternRoleImpl() {
			super();
		}

	}
}
