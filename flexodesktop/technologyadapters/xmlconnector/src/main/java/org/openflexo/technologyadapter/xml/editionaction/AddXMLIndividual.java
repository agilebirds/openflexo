/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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

package org.openflexo.technologyadapter.xml.editionaction;

import java.lang.reflect.Type;

import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.technologyadapter.xml.XMLModelSlot;
import org.openflexo.technologyadapter.xml.model.XMLIndividual;
import org.openflexo.technologyadapter.xml.viewpoint.XMLIndividualPatternRole;

/**
 * @author xtof
 * 
 */
@ModelEntity
@ImplementationClass(XMLIndividualPatternRole.XMLIndividualPatternRoleImpl.class)
public interface AddXMLIndividual extends AssignableAction<XMLModelSlot, XMLIndividual> {

	public abstract static class AddXMLIndividualImpl extends AssignableActionImpl<XMLModelSlot, XMLIndividual> implements AddXMLIndividual {

		@Override
		public Type getAssignableType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public XMLIndividual performAction(EditionSchemeAction action) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
