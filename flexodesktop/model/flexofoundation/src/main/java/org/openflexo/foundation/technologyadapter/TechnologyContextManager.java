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

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.ontology.SubClassOfClass;
import org.openflexo.foundation.ontology.SubPropertyOfProperty;
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
public abstract class TechnologyContextManager<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends FlexoObject {

	private static final Logger logger = Logger.getLogger(TechnologyContextManager.class.getPackage().getName());

	private TechnologyAdapter<M, MM> adapter;
	private FlexoResourceCenterService resourceCenterService;

	/** Stores all known metamodels where key is the URI of metamodel */
	protected Map<String, FlexoMetaModelResource<M, MM>> metamodels = new HashMap<String, FlexoMetaModelResource<M, MM>>();
	/** Stores all known models where key is the URI of model */
	protected Map<String, FlexoModelResource<M, MM>> models = new HashMap<String, FlexoModelResource<M, MM>>();

	protected Hashtable<IFlexoOntologyClass, IndividualOfClass> individualsOfClass;
	protected Hashtable<IFlexoOntologyClass, SubClassOfClass> subclassesOfClass;
	protected Hashtable<IFlexoOntologyStructuralProperty, SubPropertyOfProperty> subpropertiesOfProperty;

	public IndividualOfClass getIndividualOfClass(IFlexoOntologyClass anOntologyClass) {
		if (individualsOfClass.get(anOntologyClass) != null) {
			return individualsOfClass.get(anOntologyClass);
		} else {
			IndividualOfClass returned = new IndividualOfClass(anOntologyClass);
			individualsOfClass.put(anOntologyClass, returned);
			return returned;
		}
	}

	public SubClassOfClass getSubClassOfClass(IFlexoOntologyClass anOntologyClass) {
		if (subclassesOfClass.get(anOntologyClass) != null) {
			return subclassesOfClass.get(anOntologyClass);
		} else {
			SubClassOfClass returned = new SubClassOfClass(anOntologyClass);
			subclassesOfClass.put(anOntologyClass, returned);
			return returned;
		}
	}

	public SubPropertyOfProperty getSubPropertyOfProperty(IFlexoOntologyStructuralProperty anOntologyProperty) {
		if (subpropertiesOfProperty.get(anOntologyProperty) != null) {
			return subpropertiesOfProperty.get(anOntologyProperty);
		} else {
			SubPropertyOfProperty returned = new SubPropertyOfProperty(anOntologyProperty);
			subpropertiesOfProperty.put(anOntologyProperty, returned);
			return returned;
		}
	}

	public TechnologyContextManager(TechnologyAdapter<M, MM> adapter, FlexoResourceCenterService resourceCenterService) {
		this.adapter = adapter;
		this.resourceCenterService = resourceCenterService;
		individualsOfClass = new Hashtable<IFlexoOntologyClass, IndividualOfClass>();
		subclassesOfClass = new Hashtable<IFlexoOntologyClass, SubClassOfClass>();
		subpropertiesOfProperty = new Hashtable<IFlexoOntologyStructuralProperty, SubPropertyOfProperty>();
	}

	public TechnologyAdapter<M, MM> getTechnologyAdapter() {
		return adapter;
	}

	public FlexoResourceCenterService getResourceCenterService() {
		return resourceCenterService;
	}

	@Override
	public String getFullyQualifiedName() {
		return getClass().getName();
	}

	/**
	 * Called when a new meta model was registered, notify the {@link TechnologyContextManager}
	 * 
	 * @param newModel
	 */
	public void registerMetaModel(FlexoMetaModelResource<M, MM> newMetaModelResource) {
		metamodels.put(newMetaModelResource.getURI(), newMetaModelResource);
	}

	/**
	 * Called when a new model was registered, notify the {@link TechnologyContextManager}
	 * 
	 * @param newModel
	 */
	public void registerModel(FlexoModelResource<M, MM> newModelResource) {
		models.put(newModelResource.getURI(), newModelResource);
	}

	/**
	 * Return resource storing metamodel identified by supplied uri, asserting this metamodel has been registered in this technology
	 * 
	 * @param uri
	 * @return
	 */
	public FlexoMetaModelResource<M, MM> getMetaModelWithURI(String uri) {
		return metamodels.get(uri);
	}

	/**
	 * Return resource storing model identified by supplied uri, asserting this model has been registered in this technology
	 * 
	 * @param uri
	 * @return
	 */
	public FlexoModelResource<M, MM> getModelWithURI(String uri) {
		return models.get(uri);
	}

	public Collection<FlexoMetaModelResource<M, MM>> getAllMetaModels() {
		return metamodels.values();
	}
}
