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
package org.openflexo.foundation.technologyadapter;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.SubClassOfClass;
import org.openflexo.foundation.ontology.SubPropertyOfProperty;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;

/**
 * A {@link TechnologyContextManager} manages for a technology, and shared by all {@link FlexoResourceCenter} declared in the scope of
 * {@link FlexoResourceCenterService}, all references and links between all resources managed by a given technology, it their original
 * technical space.
 * 
 * This class is responsible for maintaining consistency of related technology, in its original technological space (maintaining technical
 * links between all technology-specific stuff)
 * 
 * @author sylvain
 * 
 */
public abstract class TechnologyContextManager<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends
		TemporaryFlexoModelObject {

	private static final Logger logger = Logger.getLogger(TechnologyContextManager.class.getPackage().getName());

	protected Hashtable<OntologyClass, IndividualOfClass> individualsOfClass;
	protected Hashtable<OntologyClass, SubClassOfClass> subclassesOfClass;
	protected Hashtable<OntologyProperty, SubPropertyOfProperty> subpropertiesOfProperty;

	public IndividualOfClass getIndividualOfClass(OntologyClass anOntologyClass) {
		if (individualsOfClass.get(anOntologyClass) != null) {
			return individualsOfClass.get(anOntologyClass);
		} else {
			IndividualOfClass returned = new IndividualOfClass(anOntologyClass);
			individualsOfClass.put(anOntologyClass, returned);
			return returned;
		}
	}

	public SubClassOfClass getSubClassOfClass(OntologyClass anOntologyClass) {
		if (subclassesOfClass.get(anOntologyClass) != null) {
			return subclassesOfClass.get(anOntologyClass);
		} else {
			SubClassOfClass returned = new SubClassOfClass(anOntologyClass);
			subclassesOfClass.put(anOntologyClass, returned);
			return returned;
		}
	}

	public SubPropertyOfProperty getSubPropertyOfProperty(OntologyProperty anOntologyProperty) {
		if (subpropertiesOfProperty.get(anOntologyProperty) != null) {
			return subpropertiesOfProperty.get(anOntologyProperty);
		} else {
			SubPropertyOfProperty returned = new SubPropertyOfProperty(anOntologyProperty);
			subpropertiesOfProperty.put(anOntologyProperty, returned);
			return returned;
		}
	}

	public TechnologyContextManager() {
		individualsOfClass = new Hashtable<OntologyClass, IndividualOfClass>();
		subclassesOfClass = new Hashtable<OntologyClass, SubClassOfClass>();
		subpropertiesOfProperty = new Hashtable<OntologyProperty, SubPropertyOfProperty>();
	}

	/**
	 * Called when a new model was registered, notify the {@link TechnologyContextManager}
	 * 
	 * @param newModel
	 */
	public abstract void registerModel(FlexoResource<M> newModelResource);

	/**
	 * Called when a new meta model was registered, notify the {@link TechnologyContextManager}
	 * 
	 * @param newModel
	 */
	public abstract void registerMetaModel(FlexoResource<MM> newMetaModelResource);

}
