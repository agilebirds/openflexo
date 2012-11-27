package org.openflexo.foundation.ontology;

import java.util.List;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.SaveResourceException;

/**
 * This interface is implemented by any class handled as an ontology in openflexo.<br>
 * Basic implementation for a IFlexoOntology is for example OWLOntology
 * 
 * @author sylvain
 * 
 */
public interface IFlexoOntology extends IFlexoOntologyConcept {

	public String getOntologyURI();

	public abstract FlexoProject getProject();

	@Override
	public abstract OntologyLibrary getOntologyLibrary();

	/**
	 * Force load ontology when unloaded
	 * 
	 * @return flag indicating if loading was performed
	 */
	public abstract boolean loadWhenUnloaded();

	public abstract boolean isLoaded();

	public abstract boolean isLoading();

	public abstract void save() throws SaveResourceException;

	/**
	 * Return a vector of all imported ontologies in the context of this ontology. This method is recursive. Ontologies are imported only
	 * once. This ontology is also appened to returned list.
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntology> getAllImportedOntologies();

	/**
	 * Return a vector of imported ontologies in the context of this ontology
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntology> getImportedOntologies();

	/**
	 * Return all classes explicitely defined in this ontology (strict mode)
	 * 
	 * @return
	 */
	public abstract List<? extends IFlexoOntologyClass> getClasses();

	/**
	 * Return all individuals explicitely defined in this ontology (strict mode)
	 * 
	 * @return
	 */
	public abstract List<? extends IFlexoOntologyIndividual> getIndividuals();

	/**
	 * Return all datatype properties explicitely defined in this ontology (strict mode)
	 * 
	 * @return
	 */
	public abstract List<? extends IFlexoOntologyDataProperty> getDataProperties();

	/**
	 * Return all object properties explicitely defined in this ontology (strict mode)
	 * 
	 * @return
	 */
	public abstract List<? extends IFlexoOntologyObjectProperty> getObjectProperties();

	/**
	 * Retrieve an ontology object from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyConcept getOntologyObject(String objectURI);

	/**
	 * Retrieve an class from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyClass getClass(String classURI);

	/**
	 * Retrieve an individual from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyIndividual getIndividual(String individualURI);

	/**
	 * Retrieve an object property from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyObjectProperty getObjectProperty(String propertyURI);

	/**
	 * Retrieve an datatype property from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyDataProperty getDataProperty(String propertyURI);

	/**
	 * Retrieve a property from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyStructuralProperty getProperty(String objectURI);

	/**
	 * Return all classes accessible in the context of this ontology.<br>
	 * This means that classes are also retrieved from imported ontologies (non-strict mode)
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyClass> getAccessibleClasses();

	/**
	 * Return all individuals accessible in the context of this ontology.<br>
	 * This means that individuals are also retrieved from imported ontologies (non-strict mode)
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyIndividual> getAccessibleIndividuals();

	/**
	 * Return all object properties accessible in the context of this ontology.<br>
	 * This means that properties are also retrieved from imported ontologies (non-strict mode)
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyObjectProperty> getAccessibleObjectProperties();

	/**
	 * Return all data properties accessible in the context of this ontology.<br>
	 * This means that properties are also retrieved from imported ontologies (non-strict mode)
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyDataProperty> getAccessibleDataProperties();

	/**
	 * Creates a new class with specified name, and with specified superClass
	 * 
	 * @param name
	 * @param father
	 * @return
	 * @throws DuplicateURIException
	 */
	public IFlexoOntologyClass createOntologyClass(String name, IFlexoOntologyClass superClass) throws DuplicateURIException;

	/**
	 * Creates a new class with specified name
	 * 
	 * @param name
	 * @param father
	 * @return
	 * @throws DuplicateURIException
	 */
	public IFlexoOntologyClass createOntologyClass(String name) throws DuplicateURIException;

	/**
	 * Creates an new individual with specified name, and with specified type
	 * 
	 * @param name
	 * @param father
	 * @return
	 * @throws DuplicateURIException
	 */
	public IFlexoOntologyIndividual createOntologyIndividual(String name, IFlexoOntologyClass type) throws DuplicateURIException;

	/**
	 * Creates an new data property with specified name, super property, domain and range
	 * 
	 * @param name
	 * @param father
	 * @return
	 * @throws DuplicateURIException
	 */
	public IFlexoOntologyObjectProperty createObjectProperty(String name, IFlexoOntologyObjectProperty superProperty, IFlexoOntologyClass domain,
			IFlexoOntologyClass range) throws DuplicateURIException;

	/**
	 * Creates an new data property with specified name, super property, domain and datatype
	 * 
	 * @param name
	 * @param father
	 * @return
	 * @throws DuplicateURIException
	 */
	public IFlexoOntologyDataProperty createDataProperty(String name, IFlexoOntologyDataProperty superProperty, IFlexoOntologyClass domain,
			OntologicDataType dataType) throws DuplicateURIException;

	/**
	 * Return the local vision of Thing concept (root class for all)
	 * 
	 * @return
	 */
	public IFlexoOntologyClass getThingConcept();

}
