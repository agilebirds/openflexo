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
package org.openflexo.foundation.ontology;

import java.io.File;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.foundation.ontology.OntologyObject.OntologyObjectConverter;
import org.openflexo.foundation.ontology.dm.OntologyImported;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

import com.hp.hpl.jena.graph.GraphMaker;
import com.hp.hpl.jena.graph.impl.SimpleGraphMaker;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.ModelReader;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.shared.AlreadyExistsException;
import com.hp.hpl.jena.shared.DoesNotExistException;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OntologyLibrary extends TemporaryFlexoModelObject implements ModelMaker, InspectableObject, DataFlexoObserver {

	private static final Logger logger = Logger.getLogger(OntologyLibrary.class.getPackage().getName());

	// public static OntologyLibrary INSTANCE;

	// public static final File ONTOLOGY_LIBRARY_DIR = new FileResource("Ontologies");
	// public static final String FLEXO_ONTOLOGY_ROOT_URI = "http://www.agilebirds.com/openflexo/ontologies";

	public static final String RDFS_ONTOLOGY_URI = "http://www.w3.org/2000/01/rdf-schema";
	public static final String RDF_ONTOLOGY_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns";
	public static final String OWL_ONTOLOGY_URI = "http://www.w3.org/2002/07/owl";
	public static final String FLEXO_CONCEPT_ONTOLOGY_URI = "http://www.agilebirds.com/openflexo/ontologies/FlexoConceptsOntology.owl";

	public static final String OWL_THING_URI = OWL_ONTOLOGY_URI + "#Thing";
	public static final String OWL_CLASS_URI = OWL_ONTOLOGY_URI + "#Class";
	public static final String OWL_DATA_PROPERTY_URI = OWL_ONTOLOGY_URI + "#DatatypeProperty";
	public static final String OWL_OBJECT_PROPERTY_URI = OWL_ONTOLOGY_URI + "#ObjectProperty";

	public static final String OPENFLEXO_DESCRIPTION_URI = FLEXO_CONCEPT_ONTOLOGY_URI + "#openflexoDescription";
	public static final String BUSINESS_DESCRIPTION_URI = FLEXO_CONCEPT_ONTOLOGY_URI + "#businessDescription";
	public static final String TECHNICAL_DESCRIPTION_URI = FLEXO_CONCEPT_ONTOLOGY_URI + "#technicalDescription";
	public static final String USER_MANUAL_DESCRIPTION_URI = FLEXO_CONCEPT_ONTOLOGY_URI + "#userManualDescription";

	public static final String RDFS_LITERAL_URI = RDFS_ONTOLOGY_URI + "#Literal";
	public static final String RDFS_RESOURCE_URI = RDFS_ONTOLOGY_URI + "#Resource";

	// public OntologyClass THING;

	// private FlexoProject _project;
	protected Hashtable<String, FlexoOntology> ontologies;
	private SimpleGraphMaker graphMaker;

	/*private Hashtable<String, OntologyClass> classes;
	private Hashtable<String, OntologyIndividual> individuals;
	private Hashtable<String, OntologyDataProperty> dataProperties;
	private Hashtable<String, OntologyObjectProperty> objectProperties;*/

	private Vector<FlexoOntology> _allOntologies;
	/*private Vector<OntologyClass> _allClasses;
	private Vector<OntologyIndividual> _allIndividuals;
	private Vector<OntologyObjectProperty> _allObjectProperties;
	private Vector<OntologyDataProperty> _allDataProperties;*/

	private FlexoResourceCenter resourceCenter;
	private OntologyLibrary parentOntologyLibrary = null;

	private OntologyFolder rootFolder;

	private OntologyObjectConverter ontologyObjectConverter;

	protected Hashtable<OntologyClass, IndividualOfClass> individualsOfClass = new Hashtable<OntologyClass, IndividualOfClass>();
	protected Hashtable<OntologyClass, SubClassOfClass> subclassesOfClass = new Hashtable<OntologyClass, SubClassOfClass>();
	protected Hashtable<OntologyProperty, SubPropertyOfProperty> subpropertiesOfProperty = new Hashtable<OntologyProperty, SubPropertyOfProperty>();

	public OntologyLibrary(FlexoResourceCenter resourceCenter, OntologyLibrary parentOntologyLibrary) {
		super();
		this.resourceCenter = resourceCenter;
		if (parentOntologyLibrary != null) {
			this.parentOntologyLibrary = parentOntologyLibrary;
			parentOntologyLibrary.addObserver(this);
		}
		ontologyObjectConverter = new OntologyObjectConverter(null/*this*/);
		// INSTANCE = this;
		// _project = project;
		ontologies = new Hashtable<String, FlexoOntology>();
		if (parentOntologyLibrary == null) {
			graphMaker = new SimpleGraphMaker();
		}
		/*classes = new Hashtable<String, OntologyClass>();
		individuals = new Hashtable<String, OntologyIndividual>();
		dataProperties = new Hashtable<String, OntologyDataProperty>();
		objectProperties = new Hashtable<String, OntologyObjectProperty>();*/
		// findOntologies(ONTOLOGY_LIBRARY_DIR, FLEXO_ONTOLOGY_ROOT_URI);

		rootFolder = new OntologyFolder("root", null, this);

	}

	public OntologyObjectConverter getOntologyObjectConverter() {
		return ontologyObjectConverter;
	}

	public FlexoResourceCenter getResourceCenter() {
		return resourceCenter;
	}

	public void setResourceCenter(FlexoResourceCenter resourceCenter) {
		this.resourceCenter = resourceCenter;
	}

	/*public void init()
	{
		getRDFSOntology().loadWhenUnloaded();
		getRDFOntology().loadWhenUnloaded();
		getOWLOntology().loadWhenUnloaded();
		THING = getClass(OWL_ONTOLOGY_URI+"#Thing");
		getRDFSOntology().updateConceptsAndProperties();
		getRDFOntology().updateConceptsAndProperties();
		getFlexoConceptOntology().loadWhenUnloaded();
	}*/

	/*public OntologyClass getRootClass() {
		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.getRootClass();
		}
		return THING;
	}*/

	public Collection<FlexoOntology> getAllOntologies() {
		if (_allOntologies == null) {
			_allOntologies = new Vector<FlexoOntology>();
			_allOntologies.addAll(ontologies.values());
			if (parentOntologyLibrary != null) {
				_allOntologies.addAll(parentOntologyLibrary.getAllOntologies());
			}
		}
		return _allOntologies;
	}

	/*public Collection<OntologyDataProperty> getAllDataProperties() {
		if (_allDataProperties == null) {
			_allDataProperties = new Vector<OntologyDataProperty>();
			_allDataProperties.addAll(dataProperties.values());
			if (parentOntologyLibrary != null) {
				_allDataProperties.addAll(parentOntologyLibrary.getAllDataProperties());
			}
		}
		return _allDataProperties;
	}

	public Collection<OntologyObjectProperty> getAllObjectProperties() {
		if (_allObjectProperties == null) {
			_allObjectProperties = new Vector<OntologyObjectProperty>();
			_allObjectProperties.addAll(objectProperties.values());
			if (parentOntologyLibrary != null) {
				_allObjectProperties.addAll(parentOntologyLibrary.getAllObjectProperties());
			}
		}
		return _allObjectProperties;
	}

	public Collection<OntologyClass> getAllClasses() {
		if (_allClasses == null) {
			_allClasses = new Vector<OntologyClass>();
			_allClasses.addAll(classes.values());
			if (parentOntologyLibrary != null) {
				_allClasses.addAll(parentOntologyLibrary.getAllClasses());
			}
		}
		return _allClasses;
	}

	public Collection<OntologyIndividual> getAllIndividuals() {
		if (_allIndividuals == null) {
			_allIndividuals = new Vector<OntologyIndividual>();
			_allIndividuals.addAll(individuals.values());
			if (parentOntologyLibrary != null) {
				_allIndividuals.addAll(parentOntologyLibrary.getAllIndividuals());
			}
		}
		return _allIndividuals;
	}*/

	public FlexoOntology getOntology(String ontologyUri) {
		FlexoOntology returned = ontologies.get(ontologyUri);
		if (returned == null && parentOntologyLibrary != null) {
			return parentOntologyLibrary.getOntology(ontologyUri);
		}
		return returned;
	}

	public FlexoOntology getFlexoConceptOntology() {
		return getOntology(FLEXO_CONCEPT_ONTOLOGY_URI);
	}

	public FlexoOntology getRDFOntology() {
		return getOntology(RDF_ONTOLOGY_URI);
	}

	public FlexoOntology getRDFSOntology() {
		return getOntology(RDFS_ONTOLOGY_URI);
	}

	public FlexoOntology getOWLOntology() {
		return getOntology(OWL_ONTOLOGY_URI);
	}

	public ImportedOntology importOntology(String ontologyUri, File alternativeLocalFile) {
		return importOntology(ontologyUri, alternativeLocalFile, null);
	}

	public ImportedOntology importOntology(String ontologyUri, File alternativeLocalFile, OntologyFolder folder) {
		logger.fine("Import ontology " + ontologyUri + " as " + alternativeLocalFile);
		if (_allOntologies != null) {
			_allOntologies.clear();
		}
		_allOntologies = null;
		ImportedOntology newOntology = new ImportedOntology(ontologyUri, alternativeLocalFile, this);
		ontologies.put(ontologyUri, newOntology);
		if (folder != null) {
			folder.addToOntologies(newOntology);
		}
		setChanged();
		notifyObservers(new OntologyImported(newOntology));
		return newOntology;
	}

	protected Model description;

	@Override
	public GraphMaker getGraphMaker() {
		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.getGraphMaker();
		}
		return graphMaker;
	}

	@Override
	public void close() {
		getGraphMaker().close();
	}

	public Model openModel() {
		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.openModel();
		}
		return new ModelCom(getGraphMaker().openGraph());
	}

	@Override
	public OntModel openModelIfPresent(String name) {
		return getGraphMaker().hasGraph(name) ? openModel(name) : null;
	}

	@Override
	public OntModel openModel(String name, boolean strict) {
		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.openModel(name, strict);
		}
		getGraphMaker().openGraph(name, strict);
		FlexoOntology ont = getOntology(name);
		if (ont != null) {
			ont.loadWhenUnloaded();
			return ont.getOntModel();
		}
		if (!strict) {
			ont = new ImportedOntology(name, null, this);
			ont.setOntModel(createFreshModel());
			ontologies.put(name, ont);
			setChanged();
			notifyObservers(new OntologyImported(ont));
			return ont.getOntModel();
		} else {
			throw new DoesNotExistException(name);
		}
	}

	@Override
	public OntModel openModel(String name) {
		return openModel(name, false);
	}

	@Override
	public OntModel createModel(String name, boolean strict) {
		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.createModel(name, strict);
		}
		getGraphMaker().createGraph(name, strict);
		FlexoOntology ont = getOntology(name);
		if (ont != null) {
			if (strict) {
				throw new AlreadyExistsException(name);
			}
			return createDefaultModel();
		}
		ont = new ImportedOntology(name, null, this);
		ont.setOntModel(createFreshModel());
		ontologies.put(name, ont);
		setChanged();
		notifyObservers(new OntologyImported(ont));
		return ont.getOntModel();
	}

	@Override
	public OntModel createModel(String name) {
		return createModel(name, false);
	}

	public OntModel createModelOver(String name) {
		return createModel(name);
	}

	@Override
	public OntModel createFreshModel() {
		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.createFreshModel();
		}
		return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, this, null);
	}

	@Override
	public OntModel createDefaultModel() {
		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.createDefaultModel();
		}
		return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, this, null);
	}

	@Override
	public void removeModel(String name) {
		getGraphMaker().removeGraph(name);
	}

	@Override
	public boolean hasModel(String name) {
		logger.info("hasModel " + name + " ? ");
		if (ontologies.get(name) != null) {
			return true;
		}
		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.hasModel(name);
		}
		return getGraphMaker().hasGraph(name);
	}

	@Override
	public ExtendedIterator listModels() {
		return getGraphMaker().listGraphs();
	}

	/**
	 * ModelGetter implementation component.
	 */
	@Override
	public Model getModel(String URL) {
		return hasModel(URL) ? openModel(URL) : null;
	}

	@Override
	public Model getModel(String URL, ModelReader loadIfAbsent) {
		Model already = getModel(URL);
		return already == null ? loadIfAbsent.readModel(createModel(URL), URL) : already;
	}

	public void debug() {
		for (FlexoOntology ont : ontologies.values()) {
			System.out.println("URI: " + ont.getOntologyURI() + " " + (ont.isLoaded() ? "LOADED" : "NOT LOADED") + " : " + ont);
		}
	}

	/*public FlexoProject getProject() 
	{
		return _project;
	}*/

	/*public OntologyObject getOntologyObject(String objectURI) {

		if (objectURI == null) {
			return null;
		}

		if (objectURI.endsWith("#")) {
			objectURI = objectURI.substring(0, objectURI.length() - 1);
		}

		OntologyObject returned = getOntology(objectURI);
		if (returned != null) {
			return returned;
		}
		returned = getClass(objectURI);
		if (returned != null) {
			return returned;
		}
		returned = getIndividual(objectURI);
		if (returned != null) {
			return returned;
		}
		returned = getObjectProperty(objectURI);
		if (returned != null) {
			return returned;
		}
		returned = getDataProperty(objectURI);
		if (returned != null) {
			return returned;
		}

		if (returned == null && objectURI.indexOf("#") > 0) {
			// Maybe required ontology is not loaded ???
			// This is an other chance to get it
			String ontologyURI = objectURI.substring(0, objectURI.indexOf("#"));
			FlexoOntology o = getOntology(ontologyURI);
			if (o != null && !o.isLoaded() && !o.isLoading()) {
				o.loadWhenUnloaded();
				return getOntologyObject(objectURI);
			}
		}

		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.getOntologyObject(objectURI);
		}

		return null;
	}*/

	/*public OntologyProperty getProperty(String objectURI) {
		OntologyProperty returned = getObjectProperty(objectURI);
		if (returned != null) {
			return returned;
		}
		returned = getDataProperty(objectURI);
		if (returned != null) {
			return returned;
		}

		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.getProperty(objectURI);
		}

		return null;
	}*/

	/**
	 * Return true if URI is well formed and valid regarding its unicity (no one other object has same URI)
	 * 
	 * @param uri
	 * @return
	 */
	public boolean testValidURI(String ontologyURI, String conceptURI) {
		if (StringUtils.isEmpty(conceptURI)) {
			return false;
		}
		if (StringUtils.isEmpty(conceptURI.trim())) {
			return false;
		}
		return (conceptURI.equals(ToolBox.getJavaName(conceptURI, true, false)) && !isDuplicatedURI(ontologyURI, conceptURI));
	}

	/**
	 * Return true if URI is duplicated
	 * 
	 * @param uri
	 * @return
	 */
	public boolean isDuplicatedURI(String ontologyURI, String conceptURI) {
		FlexoOntology o = getOntology(ontologyURI);
		if (o != null) {
			return o.getOntologyObject(ontologyURI + "#" + conceptURI) != null;
		}
		return false;
	}

	/*public OntologyClass getClass(String classURI) {
		if (classURI == null) {
			return null;
		}
		OntologyClass returned = classes.get(classURI);
		if (returned != null) {
			return returned;
		}
		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.getClass(classURI);
		}
		return null;
	}

	protected void registerNewClass(OntologyClass aClass) {
		classes.put(aClass.getURI(), aClass);
		if (_allClasses != null) {
			_allClasses.clear();
		}
		_allClasses = null;
		setChanged();
		notifyObservers(new OntologyClassInserted(aClass));
	}

	protected void unregisterClass(OntologyClass aClass) {
		classes.remove(aClass.getURI());
		if (_allClasses != null) {
			_allClasses.clear();
		}
		_allClasses = null;
		setChanged();
		notifyObservers(new OntologyClassRemoved(aClass));
	}

	protected void renameClass(OntologyClass object, String oldURI, String newURI) {
		classes.remove(oldURI);
		classes.put(object.getURI(), object);
		if (_allClasses != null) {
			_allClasses.clear();
		}
		_allClasses = null;
		setChanged();
		notifyObservers(new OntologyObjectRenamed(object, oldURI, newURI));
	}

	public OntologyIndividual getIndividual(String individualURI) {
		if (individualURI == null) {
			return null;
		}
		OntologyIndividual returned = individuals.get(individualURI);
		if (returned != null) {
			return returned;
		}
		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.getIndividual(individualURI);
		}
		return null;
	}

	protected void registerNewIndividual(OntologyIndividual anIndividual) {
		individuals.put(anIndividual.getURI(), anIndividual);
		if (_allIndividuals != null) {
			_allIndividuals.clear();
		}
		_allIndividuals = null;
		setChanged();
		notifyObservers(new OntologyIndividualInserted(anIndividual));
	}

	protected void unregisterIndividual(OntologyIndividual anIndividual) {
		individuals.remove(anIndividual.getURI());
		if (_allIndividuals != null) {
			_allIndividuals.clear();
		}
		_allIndividuals = null;
		setChanged();
		notifyObservers(new OntologyIndividualRemoved(anIndividual));
	}

	protected void renameIndividual(OntologyIndividual object, String oldURI, String newURI) {
		individuals.remove(oldURI);
		individuals.put(object.getURI(), object);
		if (_allIndividuals != null) {
			_allIndividuals.clear();
		}
		_allIndividuals = null;
		setChanged();
		notifyObservers(new OntologyObjectRenamed(object, oldURI, newURI));
	}

	public OntologyDataProperty getDataProperty(String propertyURI) {
		if (propertyURI == null) {
			return null;
		}
		OntologyDataProperty returned = dataProperties.get(propertyURI);
		if (returned != null) {
			return returned;
		}
		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.getDataProperty(propertyURI);
		}
		return null;
	}

	protected void registerNewDataProperty(OntologyDataProperty property) {
		dataProperties.put(property.getURI(), property);
		if (_allDataProperties != null) {
			_allDataProperties.clear();
		}
		_allDataProperties = null;
		setChanged();
		notifyObservers(new OntologyDataPropertyInserted(property));
	}

	protected void unregisterDataProperty(OntologyDataProperty aProperty) {
		dataProperties.remove(aProperty.getURI());
		if (_allDataProperties != null) {
			_allDataProperties.clear();
		}
		_allDataProperties = null;
		setChanged();
		notifyObservers(new OntologyDataPropertyRemoved(aProperty));
	}

	protected void renameDataProperty(OntologyDataProperty object, String oldURI, String newURI) {
		dataProperties.remove(oldURI);
		dataProperties.put(object.getURI(), object);
		if (_allDataProperties != null) {
			_allDataProperties.clear();
		}
		_allDataProperties = null;
		setChanged();
		notifyObservers(new OntologyObjectRenamed(object, oldURI, newURI));
	}

	public OntologyObjectProperty getObjectProperty(String propertyURI) {
		if (propertyURI == null) {
			return null;
		}
		OntologyObjectProperty returned = objectProperties.get(propertyURI);
		if (returned != null) {
			return returned;
		}
		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.getObjectProperty(propertyURI);
		}
		return null;
	}

	protected void registerNewObjectProperty(OntologyObjectProperty property) {
		objectProperties.put(property.getURI(), property);
		if (_allObjectProperties != null) {
			_allObjectProperties.clear();
		}
		_allObjectProperties = null;
		setChanged();
		notifyObservers(new OntologyObjectPropertyInserted(property));
	}

	protected void unregisterObjectProperty(OntologyObjectProperty aProperty) {
		objectProperties.remove(aProperty.getURI());
		if (_allObjectProperties != null) {
			_allObjectProperties.clear();
		}
		_allObjectProperties = null;
		setChanged();
		notifyObservers(new OntologyObjectPropertyRemoved(aProperty));
	}

	protected void renameObjectProperty(OntologyObjectProperty object, String oldURI, String newURI) {
		objectProperties.remove(oldURI);
		objectProperties.put(object.getURI(), object);
		if (_allObjectProperties != null) {
			_allObjectProperties.clear();
		}
		_allObjectProperties = null;
		setChanged();
		notifyObservers(new OntologyObjectRenamed(object, oldURI, newURI));
	}*/

	@Override
	public String getInspectorName() {
		return Inspectors.VE.ONTOLOGY_LIBRARY_INSPECTOR;
	}

	/*public Vector<OntologyObjectProperty> getRootObjectProperties() {
		Vector<OntologyObjectProperty> topLevelProperties = new Vector<OntologyObjectProperty>();
		for (String uri : objectProperties.keySet()) {
			OntologyObjectProperty property = objectProperties.get(uri);
			addTopLevelOntologyObjectProperty(property, topLevelProperties);
		}
		return topLevelProperties;
	}

	private void addTopLevelOntologyObjectProperty(OntologyObjectProperty property, Vector<OntologyObjectProperty> topLevelProperties) {
		if (property.getSuperProperties().size() == 0) {
			if (!topLevelProperties.contains(property)) {
				topLevelProperties.add(property);
			}
			return;
		}
		for (OntologyProperty superProperty : property.getSuperProperties()) {
			if (superProperty instanceof OntologyObjectProperty) {
				addTopLevelOntologyObjectProperty((OntologyObjectProperty) superProperty, topLevelProperties);
			}
		}
	}

	public Vector<OntologyDataProperty> getRootDataProperties() {
		Vector<OntologyDataProperty> topLevelProperties = new Vector<OntologyDataProperty>();
		for (String uri : dataProperties.keySet()) {
			OntologyDataProperty property = dataProperties.get(uri);
			addTopLevelOntologyDataProperty(property, topLevelProperties);
		}
		return topLevelProperties;
	}

	private void addTopLevelOntologyDataProperty(OntologyDataProperty property, Vector<OntologyDataProperty> topLevelProperties) {
		if (property.getSuperProperties().size() == 0) {
			if (!topLevelProperties.contains(property)) {
				topLevelProperties.add(property);
			}
			return;
		}
		for (OntologyProperty superProperty : property.getSuperProperties()) {
			if (superProperty instanceof OntologyDataProperty) {
				addTopLevelOntologyDataProperty((OntologyDataProperty) superProperty, topLevelProperties);
			}
		}
	}*/

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == parentOntologyLibrary) {
			if (dataModification instanceof OntologyImported) {
				if (_allOntologies != null) {
					_allOntologies.clear();
				}
				_allOntologies = null;
			} /*else if (dataModification instanceof OntologyClassInserted) {
				if (_allClasses != null) {
					_allClasses.clear();
				}
				_allClasses = null;
				} else if (dataModification instanceof OntologyIndividualInserted) {
				if (_allIndividuals != null) {
					_allIndividuals.clear();
				}
				_allIndividuals = null;
				} else if (dataModification instanceof OntologyDataPropertyInserted) {
				if (_allDataProperties != null) {
					_allDataProperties.clear();
				}
				_allDataProperties = null;
				} else if (dataModification instanceof OntologyObjectPropertyInserted) {
				if (_allObjectProperties != null) {
					_allObjectProperties.clear();
				}
				_allObjectProperties = null;
				}*/
		}
	}

	public OntologyFolder getRootFolder() {
		return rootFolder;
	}

	/*public OntologyObject getOntologyObject(String objectURI) {

		if (objectURI == null) {
			return null;
		}

		if (objectURI.endsWith("#")) {
			objectURI = objectURI.substring(0, objectURI.length() - 1);
		}

		OntologyObject returned = getOntology(objectURI);
		if (returned != null) {
			return returned;
		}
		returned = getClass(objectURI);
		if (returned != null) {
			return returned;
		}
		returned = getIndividual(objectURI);
		if (returned != null) {
			return returned;
		}
		returned = getObjectProperty(objectURI);
		if (returned != null) {
			return returned;
		}
		returned = getDataProperty(objectURI);
		if (returned != null) {
			return returned;
		}

		if (returned == null && objectURI.indexOf("#") > 0) {
			// Maybe required ontology is not loaded ???
			// This is an other chance to get it
			String ontologyURI = objectURI.substring(0, objectURI.indexOf("#"));
			FlexoOntology o = getOntology(ontologyURI);
			if (o != null && !o.isLoaded() && !o.isLoading()) {
				o.loadWhenUnloaded();
				return getOntologyObject(objectURI);
			}
		}

		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.getOntologyObject(objectURI);
		}

		return null;
	}*/

}
