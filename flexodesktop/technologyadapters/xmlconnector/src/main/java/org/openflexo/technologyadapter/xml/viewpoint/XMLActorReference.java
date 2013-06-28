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

import java.util.logging.Logger;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.ConceptActorReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.xml.VirtualModelInstanceBuilder;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.technologyadapter.xml.model.XMLIndividual;
import org.openflexo.technologyadapter.xml.model.XMLObject;

public class XMLActorReference<T extends XMLObject> extends ActorReference<T> {

	private static final Logger logger = FlexoLogger.getLogger(XMLActorReference.class.getPackage().toString());

	private T object;
	private String objectURI;

	// Constructor used during deserialization
	public XMLActorReference(VirtualModelInstanceBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	public XMLActorReference(T o,
			PatternRole<T> aPatternRole,
			EditionPatternInstance epi) {	
			
			super(epi.getProject());
			setEditionPatternInstance(epi);
			setPatternRole(aPatternRole);
			object = o;

			ModelSlotInstance msInstance = getModelSlotInstance();
			/** Model Slot is responsible for URI mapping */
		    objectURI = msInstance.getModelSlot().getURIForObject(msInstance, o);
		
	}

	
	@Override
	public T retrieveObject() {
		if (object == null) {
			ModelSlotInstance msInstance = getModelSlotInstance();
			if (msInstance.getModel() != null) {
				// object = (T) getProject().getObject(objectURI); 
				/** Model Slot is responsible for URI mapping */
				 object = (T) msInstance.getModelSlot().retrieveObjectWithURI(msInstance, objectURI);
			} else {
				logger.warning("Could not access to model in model slot " + getModelSlotInstance());
				logger.warning("Searched " + getModelSlotInstance().getModelURI());
			}
		}
		if (object == null) {
			logger.warning("Could not retrieve object " + objectURI);
		}
		return object;

	}


	public String _getObjectURI() {
		if (object != null) {
			ModelSlotInstance msInstance = getModelSlotInstance();
		    objectURI = msInstance.getModelSlot().getURIForObject(msInstance, object);
		}
		return objectURI;
	}

	public void _setObjectURI(String objectURI) {
		this.objectURI = objectURI;
	}

	
	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClassNameKey() {
		return "xml_actor_reference";
	}


}
