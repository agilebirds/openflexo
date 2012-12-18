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
package org.openflexo.technologyadapter.emf.metamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EPackage;
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
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.io.EMFMetaModelConverter;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;

/**
 * EMF Ecore MetaModel seen as a Flexo Ontology.
 * 
 * @author gbesancon
 */
public class EMFMetaModel extends FlexoOntologyObjectImpl implements FlexoMetaModel<EMFMetaModel>, IFlexoOntologyMetaModel {
	/** MetaModel Resource. */
	protected EMFMetaModelResource metaModelResource;
	/** Adapter. */
	protected final EMFTechnologyAdapter adapter;
	/** Package. */
	protected final EPackage ePackage;
	/** Converter. */
	protected final EMFMetaModelConverter converter;

	/**
	 * Constructor.
	 */
	public EMFMetaModel(EMFMetaModelConverter converter, EPackage ePackage, EMFTechnologyAdapter adapter) {
		this.adapter = adapter;
		this.ePackage = ePackage;
		this.converter = converter;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getURI()
	 */
	@Override
	public String getURI() {
		return EMFMetaModelURIBuilder.getUri(ePackage);
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
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getName()
	 */
	@Override
	public String getName() {
		return ePackage.getName();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) throws Exception {
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.FlexoObject#getFullyQualifiedName()
	 */
	@Override
	public String getFullyQualifiedName() {
		return "EMFMetaModel." + getURI();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getDescription()
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
	public EMFMetaModelConverter getConverter() {
		return converter;
	}

	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
	// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
	// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
	public EMFMetaModelResource getFlexoResource() {
		return metaModelResource;
	}

	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
	// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
	// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
	public void setFlexoResource(@SuppressWarnings("rawtypes") FlexoResource resource) throws DuplicateResourceException {
		if (resource instanceof EMFMetaModelResource) {
			this.metaModelResource = (EMFMetaModelResource) resource;
		}
	}

	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
	// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
	// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
	@Override
	public org.openflexo.foundation.resource.FlexoResource<EMFMetaModel> getResource() {
		return getFlexoResource();
	}

	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
	// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
	// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
	@Override
	public void setResource(org.openflexo.foundation.resource.FlexoResource<EMFMetaModel> resource) {
		try {
			setFlexoResource((FlexoResource<?>) resource);
		} catch (DuplicateResourceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.FlexoMetaModel#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return true;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.FlexoMetaModel#setIsReadOnly(boolean)
	 */
	@Override
	public void setIsReadOnly(boolean b) {
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getRootConcept()
	 */
	@Override
	public IFlexoOntologyClass getRootConcept() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getImportedOntologies()
	 */
	@Override
	public List<IFlexoOntology> getImportedOntologies() {
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getSubContainers()
	 */
	@Override
	public List<IFlexoOntologyContainer> getSubContainers() {
		List<IFlexoOntologyContainer> result = new ArrayList<IFlexoOntologyContainer>();
		for (IFlexoOntologyContainer container : converter.getPackages().values()) {
			result.add(container);
		}
		return result;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.FlexoMetaModel#getObject(java.lang.String)
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
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getConcepts()
	 */
	@Override
	public List<IFlexoOntologyConcept> getConcepts() {
		List<IFlexoOntologyConcept> result = new ArrayList<IFlexoOntologyConcept>();
		result.addAll(getClasses());
		result.addAll(getIndividuals());
		result.addAll(getDataProperties());
		result.addAll(getObjectProperties());
		return Collections.unmodifiableList(result);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getIndividuals()
	 */
	@Override
	public List<IFlexoOntologyIndividual> getIndividuals() {
		List<IFlexoOntologyIndividual> result = new ArrayList<IFlexoOntologyIndividual>();
		for (IFlexoOntologyIndividual individual : converter.getEnumLiterals().values()) {
			result.add(individual);
		}
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
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getClasses()
	 */
	@Override
	public List<? extends IFlexoOntologyClass> getClasses() {
		List<IFlexoOntologyClass> result = new ArrayList<IFlexoOntologyClass>();
		for (EMFClassClass aClass : converter.getClasses().values()) {
			result.add(aClass);
		}
		for (EMFEnumClass aClass : converter.getEnums().values()) {
			result.add(aClass);
		}
		return Collections.unmodifiableList(result);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getClass(java.lang.String)
	 */
	@Override
	public IFlexoOntologyClass getClass(String classURI) {
		IFlexoOntologyClass result = null;
		for (IFlexoOntologyClass aClass : getClasses()) {
			if (aClass.getURI().equalsIgnoreCase(classURI)) {
				result = aClass;
			}
		}
		return result;
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getDataTypes()
	 */
	@Override
	public List<IFlexoOntologyDataType> getDataTypes() {
		List<IFlexoOntologyDataType> result = new ArrayList<IFlexoOntologyDataType>();
		for (IFlexoOntologyDataType dataType : converter.getDataTypes().values()) {
			result.add(dataType);
		}
		return Collections.unmodifiableList(result);
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
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getDataProperties()
	 */
	@Override
	public List<IFlexoOntologyDataProperty> getDataProperties() {
		List<IFlexoOntologyDataProperty> result = new ArrayList<IFlexoOntologyDataProperty>();
		for (IFlexoOntologyDataProperty dataProperty : converter.getDataAttributes().values()) {
			result.add(dataProperty);
		}
		return Collections.unmodifiableList(result);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getDataProperty(java.lang.String)
	 */
	@Override
	public IFlexoOntologyDataProperty getDataProperty(String propertyURI) {
		IFlexoOntologyDataProperty result = null;
		for (IFlexoOntologyDataProperty dataProperty : getDataProperties()) {
			if (dataProperty.getURI().equalsIgnoreCase(propertyURI)) {
				result = dataProperty;
			}
		}
		return result;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getObjectProperties()
	 */
	@Override
	public List<? extends IFlexoOntologyObjectProperty> getObjectProperties() {
		List<IFlexoOntologyObjectProperty> result = new ArrayList<IFlexoOntologyObjectProperty>();
		for (IFlexoOntologyObjectProperty objectProperty : converter.getObjectAttributes().values()) {
			result.add(objectProperty);
		}
		for (IFlexoOntologyObjectProperty objectProperty : converter.getReferences().values()) {
			result.add(objectProperty);
		}
		return Collections.unmodifiableList(result);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getObjectProperty(java.lang.String)
	 */
	@Override
	public IFlexoOntologyObjectProperty getObjectProperty(String propertyURI) {
		IFlexoOntologyObjectProperty result = null;
		for (IFlexoOntologyObjectProperty objectProperty : getObjectProperties()) {
			if (objectProperty.getURI().equalsIgnoreCase(propertyURI)) {
				result = objectProperty;
			}
		}
		return result;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getAnnotations()
	 */
	@Override
	public List<IFlexoOntologyAnnotation> getAnnotations() {
		List<IFlexoOntologyAnnotation> annotations = null;
		if (ePackage.getEAnnotations() != null && ePackage.getEAnnotations().size() != 0) {
			annotations = new ArrayList<IFlexoOntologyAnnotation>();
			for (EAnnotation annotation : ePackage.getEAnnotations()) {
				annotations.add(this.getConverter().convertAnnotation(this, annotation));
			}
		} else {
			annotations = Collections.emptyList();
		}
		return annotations;
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
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getAccessibleClasses()
	 */
	@Override
	public List<? extends IFlexoOntologyClass> getAccessibleClasses() {
		return getClasses();
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
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getAccessibleDataProperties()
	 */
	@Override
	public List<? extends IFlexoOntologyDataProperty> getAccessibleDataProperties() {
		return getDataProperties();
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
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getDeclaredIndividual(java.lang.String)
	 */
	@Override
	public IFlexoOntologyIndividual getDeclaredIndividual(String individualURI) {
		return getIndividual(individualURI);
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
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getDeclaredProperty(java.lang.String)
	 */
	@Override
	public IFlexoOntologyStructuralProperty getDeclaredProperty(String objectURI) {
		return getProperty(objectURI);
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
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getDeclaredObjectProperty(java.lang.String)
	 */
	@Override
	public IFlexoOntologyObjectProperty getDeclaredObjectProperty(String propertyURI) {
		return getObjectProperty(propertyURI);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.FlexoMetaModel#getTechnologyAdapter()
	 */
	@Override
	public EMFTechnologyAdapter getTechnologyAdapter() {
		return adapter;
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
