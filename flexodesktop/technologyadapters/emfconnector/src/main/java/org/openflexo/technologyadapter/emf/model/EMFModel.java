/** Copyright (c) 2012, THALES SYSTEMES AEROPORTES - All Rights Reserved
 * Author : Gilles Besançon
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
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or 
 * combining it with eclipse EMF (or a modified version of that library), 
 * containing parts covered by the terms of EPL 1.0, the licensors of this 
 * Program grant you additional permission to convey the resulting work.
 *
 * Contributors :
 *
 */
package org.openflexo.technologyadapter.emf.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.ontology.FlexoOntologyObjectImpl;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyAnnotation;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyContainer;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyDataType;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IFlexoOntologyMetaModel;
import org.openflexo.foundation.ontology.IFlexoOntologyModel;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.io.EMFModelConverter;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;

/**
 * EMF Model.
 * 
 * @author gbesancon
 */
public class EMFModel extends FlexoOntologyObjectImpl implements FlexoModel<EMFModel, EMFMetaModel>, IFlexoOntologyModel {

	/** Resource. */
	protected EMFModelResource modelResource;
	/** MetaModel. */
	protected final EMFMetaModel metaModel;
	/** Converter. */
	protected final EMFModelConverter converter;
	/** Resource EMF. */
	protected final Resource resource;
	/** Project. */
	protected FlexoProject project;

	/**
	 * Constructor.
	 * 
	 * @param object
	 */
	public EMFModel(EMFMetaModel metaModel, EMFModelConverter converter, Resource resource) {
		this.metaModel = metaModel;
		this.converter = converter;
		this.resource = resource;
	}

	@Override
	@Deprecated
	public FlexoProject getProject() {
		// TODO should be removed from FlexoResourceData implementation
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#getName()
	 */
	@Override
	public String getName() {
		return EMFModelURIBuilder.getName(resource);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) throws Exception {
		System.out.println("Name can't be modified.");
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.FlexoObject#getFullyQualifiedName()
	 */
	@Override
	@Deprecated
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#getURI()
	 */
	@Override
	public String getURI() {
		return EMFModelURIBuilder.getUri(resource);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getVersion()
	 */
	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#getDescription()
	 */
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#getDisplayableDescription()
	 */
	@Override
	public String getDisplayableDescription() {
		return getDescription();
	}

	/**
	 * Getter of converter.
	 * 
	 * @return the converter value
	 */
	public EMFModelConverter getConverter() {
		return converter;
	}

	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
	// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
	// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
	@Override
	public EMFModelResource getFlexoResource() {
		return modelResource;
	}

	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
	// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
	// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
	@Override
	public void setFlexoResource(@SuppressWarnings("rawtypes") FlexoResource resource) throws DuplicateResourceException {
		if (resource instanceof EMFModelResource) {
			this.modelResource = (EMFModelResource) resource;
		}
	}

	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
	// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
	// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
	@Override
	public org.openflexo.foundation.resource.FlexoResource<EMFModel> getResource() {
		return getFlexoResource();
	}

	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
	// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
	// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
	@Override
	public void setResource(org.openflexo.foundation.resource.FlexoResource<EMFModel> resource) {
		try {
			setFlexoResource((FlexoResource) resource);
		} catch (DuplicateResourceException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save() throws SaveResourceException {
		getFlexoResource().saveResourceData();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyModel#getMetaModels()
	 */
	@Override
	public List<IFlexoOntologyMetaModel> getMetaModels() {
		return Collections.unmodifiableList(Collections.singletonList((IFlexoOntologyMetaModel) metaModel));
	}

	/**
	 * Getter of metaModel.
	 * 
	 * @return the metaModel value
	 */
	@Override
	public EMFMetaModel getMetaModel() {
		return metaModel;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getRootConcept()
	 */
	@Override
	public IFlexoOntologyClass getRootConcept() {
		return metaModel.getClass("http://www.eclipse.org/emf/2002/Ecore/EObject");
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getImportedOntologies()
	 */
	@Override
	public List<IFlexoOntology> getImportedOntologies() {
		return Collections.singletonList((IFlexoOntology) metaModel);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getSubContainers()
	 */
	@Override
	public List<IFlexoOntologyContainer> getSubContainers() {
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.FlexoModel#getObject(java.lang.String)
	 */
	@Override
	public Object getObject(String objectURI) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getOntologyObject(java.lang.String)
	 */
	@Override
	public IFlexoOntologyConcept getOntologyObject(String objectURI) {
		IFlexoOntologyConcept result = null;
		for (IFlexoOntologyConcept concept : getConcepts()) {
			if (concept.getURI().equalsIgnoreCase(objectURI)) {
				result = concept;
			}
		}
		return result;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getDeclaredOntologyObject(java.lang.String)
	 */
	@Override
	public IFlexoOntologyConcept getDeclaredOntologyObject(String objectURI) {
		return getOntologyObject(objectURI);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getAnnotations()
	 */
	@Override
	public List<IFlexoOntologyAnnotation> getAnnotations() {
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getDataTypes()
	 */
	@Override
	public List<IFlexoOntologyDataType> getDataTypes() {
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getConcepts()
	 */
	@Override
	public List<IFlexoOntologyConcept> getConcepts() {
		List<IFlexoOntologyConcept> concepts = new ArrayList<IFlexoOntologyConcept>();
		concepts.addAll(getIndividuals());
		return Collections.unmodifiableList(concepts);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getClasses()
	 */
	@Override
	public List<? extends IFlexoOntologyClass> getClasses() {
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getClass(java.lang.String)
	 */
	@Override
	public IFlexoOntologyClass getClass(String classURI) {
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getDeclaredClass(java.lang.String)
	 */
	@Override
	public IFlexoOntologyClass getDeclaredClass(String classURI) {
		return getClass(classURI);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getAccessibleClasses()
	 */
	@Override
	public List<? extends IFlexoOntologyClass> getAccessibleClasses() {
		return getClasses();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getIndividuals()
	 */
	@Override
	public List<? extends IFlexoOntologyIndividual> getIndividuals() {
		List<IFlexoOntologyIndividual> result = new ArrayList<IFlexoOntologyIndividual>();
		result.addAll(converter.getIndividuals().values());
		return Collections.unmodifiableList(result);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getIndividual(java.lang.String)
	 */
	@Override
	public IFlexoOntologyIndividual getIndividual(String individualURI) {
		IFlexoOntologyIndividual result = null;
		for (IFlexoOntologyIndividual individual : getIndividuals()) {
			if (individual.getURI().equalsIgnoreCase(individualURI)) {
				result = individual;
			}
		}
		return result;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getDeclaredIndividual(java.lang.String)
	 */
	@Override
	public IFlexoOntologyIndividual getDeclaredIndividual(String individualURI) {
		return getIndividual(individualURI);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getAccessibleIndividuals()
	 */
	@Override
	public List<? extends IFlexoOntologyIndividual> getAccessibleIndividuals() {
		return getIndividuals();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getProperty(java.lang.String)
	 */
	@Override
	public IFlexoOntologyStructuralProperty getProperty(String objectURI) {
		IFlexoOntologyStructuralProperty result = null;
		if (result == null) {
			result = getDataProperty(objectURI);
		}
		if (result == null) {
			result = getObjectProperty(objectURI);
		}
		return result;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getDeclaredProperty(java.lang.String)
	 */
	@Override
	public IFlexoOntologyStructuralProperty getDeclaredProperty(String objectURI) {
		return getProperty(objectURI);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getDataProperties()
	 */
	@Override
	public List<? extends IFlexoOntologyDataProperty> getDataProperties() {
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getDataProperty(java.lang.String)
	 */
	@Override
	public IFlexoOntologyDataProperty getDataProperty(String propertyURI) {
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getDeclaredDataProperty(java.lang.String)
	 */
	@Override
	public IFlexoOntologyDataProperty getDeclaredDataProperty(String propertyURI) {
		return getDataProperty(propertyURI);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getAccessibleDataProperties()
	 */
	@Override
	public List<? extends IFlexoOntologyDataProperty> getAccessibleDataProperties() {
		return getDataProperties();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getObjectProperties()
	 */
	@Override
	public List<? extends IFlexoOntologyObjectProperty> getObjectProperties() {
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getObjectProperty(java.lang.String)
	 */
	@Override
	public IFlexoOntologyObjectProperty getObjectProperty(String propertyURI) {
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getDeclaredObjectProperty(java.lang.String)
	 */
	@Override
	public IFlexoOntologyObjectProperty getDeclaredObjectProperty(String propertyURI) {
		return getObjectProperty(propertyURI);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getAccessibleObjectProperties()
	 */
	@Override
	public List<? extends IFlexoOntologyObjectProperty> getAccessibleObjectProperties() {
		return getObjectProperties();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.FlexoModel#getTechnologyAdapter()
	 */
	@Override
	public TechnologyAdapter<?, ?> getTechnologyAdapter() {
		return metaModel.getTechnologyAdapter();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#getFlexoOntology()
	 */
	@Override
	public IFlexoOntology getFlexoOntology() {
		return this;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#isOntology()
	 */
	@Override
	public boolean isOntology() {
		return true;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#isOntologyClass()
	 */
	@Override
	public boolean isOntologyClass() {
		return false;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#isOntologyIndividual()
	 */
	@Override
	public boolean isOntologyIndividual() {
		return false;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#isOntologyObjectProperty()
	 */
	@Override
	public boolean isOntologyObjectProperty() {
		return false;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#isOntologyDataProperty()
	 */
	@Override
	public boolean isOntologyDataProperty() {
		return false;
	}

}
