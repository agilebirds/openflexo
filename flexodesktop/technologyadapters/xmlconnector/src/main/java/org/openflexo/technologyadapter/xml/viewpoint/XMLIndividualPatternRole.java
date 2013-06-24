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
package org.openflexo.technologyadapter.xml.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.technologyadapter.xml.model.XMLIndividual;

/**
 * @author xtof
 *
 */
public class XMLIndividualPatternRole extends PatternRole<XMLIndividual> {

	public XMLIndividualPatternRole(VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPreciseType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getIsPrimaryRole() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setIsPrimaryRole(boolean isPrimary) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean defaultBehaviourIsToBeDeleted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ActorReference<XMLIndividual> makeActorReference(
			XMLIndividual object, EditionPatternInstance epi) {
		// TODO Auto-generated method stub
		return null;
	}

}
