package org.openflexo.foundation.ontology;

import java.util.List;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.SaveResourceException;

/**
 * This interface is implemented by any class handled as an ontology in openflexo.<br>
 * Basic implementation for a FlexoOntology is for example OWLOntology
 * 
 * @author sylvain
 * 
 */
public interface FlexoOntology extends OntologyObject {

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
	public List<? extends FlexoOntology> getAllImportedOntologies();

	/**
	 * Return a vector of imported ontologies in the context of this ontology
	 * 
	 * @return
	 */
	public List<? extends FlexoOntology> getImportedOntologies();

	/**
	 * Return all classes explicitely defined in this ontology (strict mode)
	 * 
	 * @return
	 */
	public abstract List<? extends OntologyClass> getClasses();

	/**
	 * Return all individuals explicitely defined in this ontology (strict mode)
	 * 
	 * @return
	 */
	public abstract List<? extends OntologyIndividual> getIndividuals();

	/**
	 * Return all datatype properties explicitely defined in this ontology (strict mode)
	 * 
	 * @return
	 */
	public abstract List<? extends OntologyDataProperty> getDataProperties();

	/**
	 * Return all object properties explicitely defined in this ontology (strict mode)
	 * 
	 * @return
	 */
	public abstract List<? extends OntologyObjectProperty> getObjectProperties();

	/**
	 * Retrieve an ontology object from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract OntologyObject getOntologyObject(String objectURI);

	/**
	 * Retrieve an class from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract OntologyClass getClass(String classURI);

	/**
	 * Retrieve an individual from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract OntologyIndividual getIndividual(String individualURI);

	/**
	 * Retrieve an object property from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract OntologyObjectProperty getObjectProperty(String propertyURI);

	/**
	 * Retrieve an datatype property from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract OntologyDataProperty getDataProperty(String propertyURI);

	/**
	 * Retrieve a property from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract OntologyProperty getProperty(String objectURI);

	/**
	 * Return all classes accessible in the context of this ontology.<br>
	 * This means that classes are also retrieved from imported ontologies (non-strict mode)
	 * 
	 * @return
	 */
	public List<? extends OntologyClass> getAccessibleClasses();

	/**
	 * Return all individuals accessible in the context of this ontology.<br>
	 * This means that individuals are also retrieved from imported ontologies (non-strict mode)
	 * 
	 * @return
	 */
	public List<? extends OntologyIndividual> getAccessibleIndividuals();

	/**
	 * Return all object properties accessible in the context of this ontology.<br>
	 * This means that properties are also retrieved from imported ontologies (non-strict mode)
	 * 
	 * @return
	 */
	public List<? extends OntologyObjectProperty> getAccessibleObjectProperties();

	/**
	 * Return all data properties accessible in the context of this ontology.<br>
	 * This means that properties are also retrieved from imported ontologies (non-strict mode)
	 * 
	 * @return
	 */
	public List<? extends OntologyDataProperty> getAccessibleDataProperties();

	/**
	 * Creates a new class with specified name, and with specified superClass
	 * 
	 * @param name
	 * @param father
	 * @return
	 * @throws DuplicateURIException
	 */
	public OntologyClass createOntologyClass(String name, OntologyClass superClass) throws DuplicateURIException;

	/**
	 * Creates a new class with specified name
	 * 
	 * @param name
	 * @param father
	 * @return
	 * @throws DuplicateURIException
	 */
	public OntologyClass createOntologyClass(String name) throws DuplicateURIException;

	/**
	 * Creates an new individual with specified name, and with specified type
	 * 
	 * @param name
	 * @param father
	 * @return
	 * @throws DuplicateURIException
	 */
	public OntologyIndividual createOntologyIndividual(String name, OntologyClass type) throws DuplicateURIException;

	/**
	 * Creates an new data property with specified name, super property, domain and range
	 * 
	 * @param name
	 * @param father
	 * @return
	 * @throws DuplicateURIException
	 */
	public OntologyObjectProperty createObjectProperty(String name, OntologyObjectProperty superProperty, OntologyClass domain,
			OntologyClass range) throws DuplicateURIException;

	/**
	 * Creates an new data property with specified name, super property, domain and datatype
	 * 
	 * @param name
	 * @param father
	 * @return
	 * @throws DuplicateURIException
	 */
	public OntologyDataProperty createDataProperty(String name, OntologyDataProperty superProperty, OntologyClass domain,
			OntologicDataType dataType) throws DuplicateURIException;

	/**
	 * Return the local vision of Thing concept (root class for all)
	 * 
	 * @return
	 */
	public OntologyClass getThingConcept();

}
