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
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.technologyadapter.xml.model.XMLIndividual;

/**
 * @author xtof
 * 
 */
@ModelEntity
@ImplementationClass(XMLIndividualPatternRole.XMLIndividualPatternRoleImpl.class)
public interface XMLIndividualPatternRole extends PatternRole<XMLIndividual> {

	@PropertyIdentifier(type = String.class)
	public static final String INDIVIDUAL_URI_KEY = "individualURI";

	@Getter(value = INDIVIDUAL_URI_KEY)
	@XMLAttribute
	public String getIndividualURI();

	@Setter(INDIVIDUAL_URI_KEY)
	public void setIndividualURI(String conceptURI);

	public static abstract class XMLIndividualPatternRoleImpl extends PatternRoleImpl<XMLIndividual> implements XMLIndividualPatternRole {

		private String individualURI;

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
		public boolean defaultBehaviourIsToBeDeleted() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public ActorReference<XMLIndividual> makeActorReference(XMLIndividual object, EditionPatternInstance epi) {
			return new XMLActorReference(object, this, epi);
		}

		@Override
		public String getIndividualURI() {
			return individualURI;
		}

		@Override
		public void setIndividualURI(String conceptURI) {
			this.individualURI = conceptURI;
		}

	}

}
