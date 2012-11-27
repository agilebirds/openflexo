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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.foundation.ontology.dm.OntologyImported;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
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

// TODO: an ontology library must handle MetaModel only, and not IFlexoOntology
// TODO: should be renamed as MetaModelLibrary
@Deprecated
public class OntologyLibrary extends TemporaryFlexoModelObject implements ModelMaker, InspectableObject, DataFlexoObserver {

	private static final Logger logger = Logger.getLogger(OntologyLibrary.class.getPackage().getName());

	// public static OntologyLibrary INSTANCE;

	// public static final File ONTOLOGY_LIBRARY_DIR = new FileResource("Ontologies");
	// public static final String FLEXO_ONTOLOGY_ROOT_URI = "http://www.agilebirds.com/openflexo/ontologies";

	public static final String FLEXO_CONCEPT_ONTOLOGY_URI = "http://www.agilebirds.com/openflexo/ontologies/FlexoConceptsOntology.owl";

	public static final String OPENFLEXO_DESCRIPTION_URI = FLEXO_CONCEPT_ONTOLOGY_URI + "#openflexoDescription";
	public static final String BUSINESS_DESCRIPTION_URI = FLEXO_CONCEPT_ONTOLOGY_URI + "#businessDescription";
	public static final String TECHNICAL_DESCRIPTION_URI = FLEXO_CONCEPT_ONTOLOGY_URI + "#technicalDescription";
	public static final String USER_MANUAL_DESCRIPTION_URI = FLEXO_CONCEPT_ONTOLOGY_URI + "#userManualDescription";

	// public IFlexoOntologyClass THING;

	// private FlexoProject _project;
	protected Hashtable<String, IFlexoOntology> ontologies;
	private SimpleGraphMaker graphMaker;

	/*private Hashtable<String, IFlexoOntologyClass> classes;
	private Hashtable<String, IFlexoOntologyIndividual> individuals;
	private Hashtable<String, IFlexoOntologyDataProperty> dataProperties;
	private Hashtable<String, IFlexoOntologyObjectProperty> objectProperties;*/

	private Vector<IFlexoOntology> _allOntologies;
	/*private Vector<IFlexoOntologyClass> _allClasses;
	private Vector<IFlexoOntologyIndividual> _allIndividuals;
	private Vector<IFlexoOntologyObjectProperty> _allObjectProperties;
	private Vector<IFlexoOntologyDataProperty> _allDataProperties;*/

	private FlexoResourceCenter resourceCenter;
	private OntologyLibrary parentOntologyLibrary = null;

	private OntologyFolder rootFolder;

	private OntologyObjectConverter ontologyObjectConverter;

	protected Hashtable<IFlexoOntologyClass, IndividualOfClass> individualsOfClass = new Hashtable<IFlexoOntologyClass, IndividualOfClass>();
	protected Hashtable<IFlexoOntologyClass, SubClassOfClass> subclassesOfClass = new Hashtable<IFlexoOntologyClass, SubClassOfClass>();
	protected Hashtable<IFlexoOntologyStructuralProperty, SubPropertyOfProperty> subpropertiesOfProperty = new Hashtable<IFlexoOntologyStructuralProperty, SubPropertyOfProperty>();

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
		ontologies = new Hashtable<String, IFlexoOntology>();
		if (parentOntologyLibrary == null) {
			graphMaker = new SimpleGraphMaker();
		}
		/*classes = new Hashtable<String, IFlexoOntologyClass>();
		individuals = new Hashtable<String, IFlexoOntologyIndividual>();
		dataProperties = new Hashtable<String, IFlexoOntologyDataProperty>();
		objectProperties = new Hashtable<String, IFlexoOntologyObjectProperty>();*/
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

	/*public IFlexoOntologyClass getRootClass() {
		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.getRootClass();
		}
		return THING;
	}*/

	public Collection<IFlexoOntology> getAllOntologies() {
		if (_allOntologies == null) {
			_allOntologies = new Vector<IFlexoOntology>();
			_allOntologies.addAll(ontologies.values());
			if (parentOntologyLibrary != null) {
				_allOntologies.addAll(parentOntologyLibrary.getAllOntologies());
			}
		}
		return _allOntologies;
	}

	/*public Collection<IFlexoOntologyDataProperty> getAllDataProperties() {
		if (_allDataProperties == null) {
			_allDataProperties = new Vector<IFlexoOntologyDataProperty>();
			_allDataProperties.addAll(dataProperties.values());
			if (parentOntologyLibrary != null) {
				_allDataProperties.addAll(parentOntologyLibrary.getAllDataProperties());
			}
		}
		return _allDataProperties;
	}

	public Collection<IFlexoOntologyObjectProperty> getAllObjectProperties() {
		if (_allObjectProperties == null) {
			_allObjectProperties = new Vector<IFlexoOntologyObjectProperty>();
			_allObjectProperties.addAll(objectProperties.values());
			if (parentOntologyLibrary != null) {
				_allObjectProperties.addAll(parentOntologyLibrary.getAllObjectProperties());
			}
		}
		return _allObjectProperties;
	}

	public Collection<IFlexoOntologyClass> getAllClasses() {
		if (_allClasses == null) {
			_allClasses = new Vector<IFlexoOntologyClass>();
			_allClasses.addAll(classes.values());
			if (parentOntologyLibrary != null) {
				_allClasses.addAll(parentOntologyLibrary.getAllClasses());
			}
		}
		return _allClasses;
	}

	public Collection<IFlexoOntologyIndividual> getAllIndividuals() {
		if (_allIndividuals == null) {
			_allIndividuals = new Vector<IFlexoOntologyIndividual>();
			_allIndividuals.addAll(individuals.values());
			if (parentOntologyLibrary != null) {
				_allIndividuals.addAll(parentOntologyLibrary.getAllIndividuals());
			}
		}
		return _allIndividuals;
	}*/

	public IFlexoOntology getOntology(String ontologyUri) {
		IFlexoOntology returned = ontologies.get(ontologyUri);
		if (returned == null && parentOntologyLibrary != null) {
			return parentOntologyLibrary.getOntology(ontologyUri);
		}
		return returned;
	}

	public IFlexoOntology getFlexoConceptOntology() {
		return getOntology(FLEXO_CONCEPT_ONTOLOGY_URI);
	}

	public IFlexoOntology getRDFOntology() {
		return getOntology(RDFURIDefinitions.RDF_ONTOLOGY_URI);
	}

	public IFlexoOntology getRDFSOntology() {
		return getOntology(RDFSURIDefinitions.RDFS_ONTOLOGY_URI);
	}

	public IFlexoOntology getOWLOntology() {
		return getOntology(OWL2URIDefinitions.OWL_ONTOLOGY_URI);
	}

	public FlexoMetaModel importMetaModel(FlexoMetaModel metaModel) {
		return importMetaModel(metaModel, null);
	}

	public FlexoMetaModel importMetaModel(FlexoMetaModel metaModel, OntologyFolder folder) {
		logger.fine("Import meta model " + metaModel.getURI());
		if (_allOntologies != null) {
			_allOntologies.clear();
		}
		_allOntologies = null;

		// TODO: an ontology library must handle MetaModel only, and not IFlexoOntology

		registerOntology((IFlexoOntology) metaModel);
		// ontologies.put(ontologyUri, newOntology);
		if (folder != null) {
			folder.addToOntologies((IFlexoOntology) metaModel);
		}
		setChanged();
		notifyObservers(new OntologyImported((IFlexoOntology) metaModel));
		return metaModel;
	}

	public void registerOntology(IFlexoOntology ontology) {
		ontologies.put(ontology.getURI(), ontology);
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
		IFlexoOntology ont = getOntology(name);
		if (ont instanceof OWLOntology) {
			ont.loadWhenUnloaded();
			return ((OWLOntology) ont).getOntModel();
		}
		if (!strict) {
			OWLMetaModel newOntology = new OWLMetaModel(name, null, this);
			newOntology.setOntModel(createFreshModel());
			ontologies.put(name, newOntology);
			setChanged();
			notifyObservers(new OntologyImported(newOntology));
			return newOntology.getOntModel();
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
		IFlexoOntology ont = getOntology(name);
		if (ont != null) {
			if (strict) {
				throw new AlreadyExistsException(name);
			}
			return createDefaultModel();
		}
		OWLMetaModel newOntology = new OWLMetaModel(name, null, this);
		newOntology.setOntModel(createFreshModel());
		ontologies.put(name, newOntology);
		setChanged();
		notifyObservers(new OntologyImported(newOntology));
		return newOntology.getOntModel();
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
		for (IFlexoOntology ont : ontologies.values()) {
			System.out.println("URI: " + ont.getOntologyURI() + " " + (ont.isLoaded() ? "LOADED" : "NOT LOADED") + " : " + ont);
		}
	}

	/*public FlexoProject getProject() 
	{
		return _project;
	}*/

	/*public IFlexoOntologyConcept getOntologyObject(String objectURI) {

		if (objectURI == null) {
			return null;
		}

		if (objectURI.endsWith("#")) {
			objectURI = objectURI.substring(0, objectURI.length() - 1);
		}

		IFlexoOntologyConcept returned = getOntology(objectURI);
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
			IFlexoOntology o = getOntology(ontologyURI);
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

	/*public IFlexoOntologyStructuralProperty getProperty(String objectURI) {
		IFlexoOntologyStructuralProperty returned = getObjectProperty(objectURI);
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
		return conceptURI.equals(ToolBox.getJavaName(conceptURI, true, false)) && !isDuplicatedURI(ontologyURI, conceptURI);
	}

	/**
	 * Return true if URI is duplicated
	 * 
	 * @param uri
	 * @return
	 */
	public boolean isDuplicatedURI(String ontologyURI, String conceptURI) {
		IFlexoOntology o = getOntology(ontologyURI);
		if (o != null) {
			return o.getOntologyObject(ontologyURI + "#" + conceptURI) != null;
		}
		return false;
	}

	/*public IFlexoOntologyClass getClass(String classURI) {
		if (classURI == null) {
			return null;
		}
		IFlexoOntologyClass returned = classes.get(classURI);
		if (returned != null) {
			return returned;
		}
		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.getClass(classURI);
		}
		return null;
	}

	protected void registerNewClass(IFlexoOntologyClass aClass) {
		classes.put(aClass.getURI(), aClass);
		if (_allClasses != null) {
			_allClasses.clear();
		}
		_allClasses = null;
		setChanged();
		notifyObservers(new OntologyClassInserted(aClass));
	}

	protected void unregisterClass(IFlexoOntologyClass aClass) {
		classes.remove(aClass.getURI());
		if (_allClasses != null) {
			_allClasses.clear();
		}
		_allClasses = null;
		setChanged();
		notifyObservers(new OntologyClassRemoved(aClass));
	}

	protected void renameClass(IFlexoOntologyClass object, String oldURI, String newURI) {
		classes.remove(oldURI);
		classes.put(object.getURI(), object);
		if (_allClasses != null) {
			_allClasses.clear();
		}
		_allClasses = null;
		setChanged();
		notifyObservers(new OntologyObjectRenamed(object, oldURI, newURI));
	}

	public IFlexoOntologyIndividual getIndividual(String individualURI) {
		if (individualURI == null) {
			return null;
		}
		IFlexoOntologyIndividual returned = individuals.get(individualURI);
		if (returned != null) {
			return returned;
		}
		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.getIndividual(individualURI);
		}
		return null;
	}

	protected void registerNewIndividual(IFlexoOntologyIndividual anIndividual) {
		individuals.put(anIndividual.getURI(), anIndividual);
		if (_allIndividuals != null) {
			_allIndividuals.clear();
		}
		_allIndividuals = null;
		setChanged();
		notifyObservers(new OntologyIndividualInserted(anIndividual));
	}

	protected void unregisterIndividual(IFlexoOntologyIndividual anIndividual) {
		individuals.remove(anIndividual.getURI());
		if (_allIndividuals != null) {
			_allIndividuals.clear();
		}
		_allIndividuals = null;
		setChanged();
		notifyObservers(new OntologyIndividualRemoved(anIndividual));
	}

	protected void renameIndividual(IFlexoOntologyIndividual object, String oldURI, String newURI) {
		individuals.remove(oldURI);
		individuals.put(object.getURI(), object);
		if (_allIndividuals != null) {
			_allIndividuals.clear();
		}
		_allIndividuals = null;
		setChanged();
		notifyObservers(new OntologyObjectRenamed(object, oldURI, newURI));
	}

	public IFlexoOntologyDataProperty getDataProperty(String propertyURI) {
		if (propertyURI == null) {
			return null;
		}
		IFlexoOntologyDataProperty returned = dataProperties.get(propertyURI);
		if (returned != null) {
			return returned;
		}
		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.getDataProperty(propertyURI);
		}
		return null;
	}

	protected void registerNewDataProperty(IFlexoOntologyDataProperty property) {
		dataProperties.put(property.getURI(), property);
		if (_allDataProperties != null) {
			_allDataProperties.clear();
		}
		_allDataProperties = null;
		setChanged();
		notifyObservers(new OntologyDataPropertyInserted(property));
	}

	protected void unregisterDataProperty(IFlexoOntologyDataProperty aProperty) {
		dataProperties.remove(aProperty.getURI());
		if (_allDataProperties != null) {
			_allDataProperties.clear();
		}
		_allDataProperties = null;
		setChanged();
		notifyObservers(new OntologyDataPropertyRemoved(aProperty));
	}

	protected void renameDataProperty(IFlexoOntologyDataProperty object, String oldURI, String newURI) {
		dataProperties.remove(oldURI);
		dataProperties.put(object.getURI(), object);
		if (_allDataProperties != null) {
			_allDataProperties.clear();
		}
		_allDataProperties = null;
		setChanged();
		notifyObservers(new OntologyObjectRenamed(object, oldURI, newURI));
	}

	public IFlexoOntologyObjectProperty getObjectProperty(String propertyURI) {
		if (propertyURI == null) {
			return null;
		}
		IFlexoOntologyObjectProperty returned = objectProperties.get(propertyURI);
		if (returned != null) {
			return returned;
		}
		if (parentOntologyLibrary != null) {
			return parentOntologyLibrary.getObjectProperty(propertyURI);
		}
		return null;
	}

	protected void registerNewObjectProperty(IFlexoOntologyObjectProperty property) {
		objectProperties.put(property.getURI(), property);
		if (_allObjectProperties != null) {
			_allObjectProperties.clear();
		}
		_allObjectProperties = null;
		setChanged();
		notifyObservers(new OntologyObjectPropertyInserted(property));
	}

	protected void unregisterObjectProperty(IFlexoOntologyObjectProperty aProperty) {
		objectProperties.remove(aProperty.getURI());
		if (_allObjectProperties != null) {
			_allObjectProperties.clear();
		}
		_allObjectProperties = null;
		setChanged();
		notifyObservers(new OntologyObjectPropertyRemoved(aProperty));
	}

	protected void renameObjectProperty(IFlexoOntologyObjectProperty object, String oldURI, String newURI) {
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

	/*public Vector<IFlexoOntologyObjectProperty> getRootObjectProperties() {
		Vector<IFlexoOntologyObjectProperty> topLevelProperties = new Vector<IFlexoOntologyObjectProperty>();
		for (String uri : objectProperties.keySet()) {
			IFlexoOntologyObjectProperty property = objectProperties.get(uri);
			addTopLevelOntologyObjectProperty(property, topLevelProperties);
		}
		return topLevelProperties;
	}

	private void addTopLevelOntologyObjectProperty(IFlexoOntologyObjectProperty property, Vector<IFlexoOntologyObjectProperty> topLevelProperties) {
		if (property.getSuperProperties().size() == 0) {
			if (!topLevelProperties.contains(property)) {
				topLevelProperties.add(property);
			}
			return;
		}
		for (IFlexoOntologyStructuralProperty superProperty : property.getSuperProperties()) {
			if (superProperty instanceof IFlexoOntologyObjectProperty) {
				addTopLevelOntologyObjectProperty((IFlexoOntologyObjectProperty) superProperty, topLevelProperties);
			}
		}
	}

	public Vector<IFlexoOntologyDataProperty> getRootDataProperties() {
		Vector<IFlexoOntologyDataProperty> topLevelProperties = new Vector<IFlexoOntologyDataProperty>();
		for (String uri : dataProperties.keySet()) {
			IFlexoOntologyDataProperty property = dataProperties.get(uri);
			addTopLevelOntologyDataProperty(property, topLevelProperties);
		}
		return topLevelProperties;
	}

	private void addTopLevelOntologyDataProperty(IFlexoOntologyDataProperty property, Vector<IFlexoOntologyDataProperty> topLevelProperties) {
		if (property.getSuperProperties().size() == 0) {
			if (!topLevelProperties.contains(property)) {
				topLevelProperties.add(property);
			}
			return;
		}
		for (IFlexoOntologyStructuralProperty superProperty : property.getSuperProperties()) {
			if (superProperty instanceof IFlexoOntologyDataProperty) {
				addTopLevelOntologyDataProperty((IFlexoOntologyDataProperty) superProperty, topLevelProperties);
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

	/*public IFlexoOntologyConcept getOntologyObject(String objectURI) {

		if (objectURI == null) {
			return null;
		}

		if (objectURI.endsWith("#")) {
			objectURI = objectURI.substring(0, objectURI.length() - 1);
		}

		IFlexoOntologyConcept returned = getOntology(objectURI);
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
			IFlexoOntology o = getOntology(ontologyURI);
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
