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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.LocalResourceCenterImplementation;
import org.openflexo.foundation.ontology.action.CreateDataProperty;
import org.openflexo.foundation.ontology.action.CreateObjectProperty;
import org.openflexo.foundation.ontology.action.CreateOntologyClass;
import org.openflexo.foundation.ontology.action.CreateOntologyIndividual;
import org.openflexo.foundation.ontology.action.DeleteOntologyObjects;
import org.openflexo.foundation.ontology.dm.OntologyClassInserted;
import org.openflexo.foundation.ontology.dm.OntologyClassRemoved;
import org.openflexo.foundation.ontology.dm.OntologyDataPropertyInserted;
import org.openflexo.foundation.ontology.dm.OntologyDataPropertyRemoved;
import org.openflexo.foundation.ontology.dm.OntologyIndividualInserted;
import org.openflexo.foundation.ontology.dm.OntologyIndividualRemoved;
import org.openflexo.foundation.ontology.dm.OntologyObjectPropertyInserted;
import org.openflexo.foundation.ontology.dm.OntologyObjectPropertyRemoved;
import org.openflexo.foundation.ontology.dm.OntologyObjectRenamed;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.toolbox.StringUtils;

import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.ComplementClass;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.IntersectionClass;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.ResourceUtils;

public abstract class FlexoOntology extends OntologyObject {

	private static final Logger logger = Logger.getLogger(FlexoOntology.class.getPackage().getName());

	private String name;
	private final String ontologyURI;
	protected OntModel ontModel;
	private final File alternativeLocalFile;
	private final OntologyLibrary _library;
	protected boolean isLoaded = false;
	protected boolean isLoading = false;
	private boolean readOnly = true;

	private final Vector<FlexoOntology> importedOntologies;

	private final Hashtable<String, OntologyClass> classes;
	private final Hashtable<String, OntologyIndividual> individuals;
	private final Hashtable<String, OntologyDataProperty> dataProperties;
	private final Hashtable<String, OntologyObjectProperty> objectProperties;

	private final Hashtable<OntClass, OntologyClass> _classes;
	private final Hashtable<Individual, OntologyIndividual> _individuals;
	private final Hashtable<OntProperty, OntologyDataProperty> _dataProperties;
	private final Hashtable<OntProperty, OntologyObjectProperty> _objectProperties;

	private final Vector<OntologyClass> orderedClasses;
	private final Vector<OntologyIndividual> orderedIndividuals;
	private final Vector<OntologyDataProperty> orderedDataProperties;
	private final Vector<OntologyObjectProperty> orderedObjectProperties;

	private OntologyClass THING_CONCEPT;

	public FlexoOntology(String anURI, File owlFile, OntologyLibrary library) {
		super(null, null);

		logger.info("Register ontology " + anURI + " file: " + owlFile);

		ontologyURI = anURI;
		if (owlFile != null && owlFile.exists()) {
			name = findOntologyName(owlFile);
		}
		if (name == null) {
			name = ontologyURI.substring(ontologyURI.lastIndexOf("/") + 1);
		}
		alternativeLocalFile = owlFile;
		_library = library;

		importedOntologies = new Vector<FlexoOntology>();

		classes = new Hashtable<String, OntologyClass>();
		individuals = new Hashtable<String, OntologyIndividual>();
		dataProperties = new Hashtable<String, OntologyDataProperty>();
		objectProperties = new Hashtable<String, OntologyObjectProperty>();

		_classes = new Hashtable<OntClass, OntologyClass>();
		_individuals = new Hashtable<Individual, OntologyIndividual>();
		_dataProperties = new Hashtable<OntProperty, OntologyDataProperty>();
		_objectProperties = new Hashtable<OntProperty, OntologyObjectProperty>();

		orderedClasses = new Vector<OntologyClass>();
		orderedIndividuals = new Vector<OntologyIndividual>();
		orderedDataProperties = new Vector<OntologyDataProperty>();
		orderedObjectProperties = new Vector<OntologyObjectProperty>();
	}

	public static void main(String[] args) {
		File f = new File("/Users/sylvain/Library/OpenFlexo/FlexoResourceCenter/Ontologies/www.bolton.ac.uk/Archimate_from_Ecore.owl");
		String uri = findOntologyURI(f);
		System.out.println("uri: " + uri);
		System.out.println("uri: " + findOntologyName(f));
		FlexoResourceCenter resourceCenter = LocalResourceCenterImplementation.instanciateTestLocalResourceCenterImplementation(new File(
				"/Users/sylvain/Library/OpenFlexo/FlexoResourceCenter"));
		resourceCenter.retrieveBaseOntologyLibrary();
		// ImportedOntology o = ImportedOntology.createNewImportedOntology(uri, f, resourceCenter.retrieveBaseOntologyLibrary());
		// o.load();
		/*try {
			o.save();
		} catch (SaveResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*System.out.println("ontologies:" + o.getImportedOntologies());
		System.out.println("classes:" + o.getClasses());
		System.out.println("properties:" + o.getObjectProperties());*/
	}

	public static String findOntologyURI(File aFile) {
		String returned = findOntologyURIWithOntologyAboutMethod(aFile);
		if (StringUtils.isNotEmpty(returned) && returned.endsWith("#")) {
			returned = returned.substring(0, returned.lastIndexOf("#"));
		}
		if (StringUtils.isEmpty(returned)) {
			returned = findOntologyURIWithRDFBaseMethod(aFile);
		}
		if (StringUtils.isNotEmpty(returned) && returned.endsWith("#")) {
			returned = returned.substring(0, returned.lastIndexOf("#"));
		}
		if (StringUtils.isEmpty(returned)) {
			logger.warning("Could not find URI for ontology stored in file " + aFile.getAbsolutePath());
		}
		return returned;

	}

	private static String findOntologyURIWithRDFBaseMethod(File aFile) {
		Document document;
		try {
			logger.fine("Try to find URI for " + aFile);
			document = readXMLFile(aFile);
			Element root = getElement(document, "RDF");
			if (root != null) {
				Iterator it = root.getAttributes().iterator();
				while (it.hasNext()) {
					Attribute at = (Attribute) it.next();
					if (at.getName().equals("base")) {
						logger.fine("Returned " + at.getValue());
						return at.getValue();
					}
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.fine("Returned null");
		return null;
	}

	private static String findOntologyURIWithOntologyAboutMethod(File aFile) {
		Document document;
		try {
			logger.fine("Try to find URI for " + aFile);
			document = readXMLFile(aFile);
			Element root = getElement(document, "Ontology");
			if (root != null) {
				Iterator it = root.getAttributes().iterator();
				while (it.hasNext()) {
					Attribute at = (Attribute) it.next();
					if (at.getName().equals("about")) {
						logger.fine("Returned " + at.getValue());
						String returned = at.getValue();
						if (StringUtils.isNotEmpty(returned)) {
							return returned;
						}
					}
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.fine("Returned null");
		return findOntologyURIWithRDFBaseMethod(aFile);
	}

	public static String findOntologyName(File aFile) {
		if (aFile == null || !aFile.exists() || aFile.length() == 0) {
			if (aFile.length() == 0) {
				aFile.delete();
			}
			return null;
		}

		Document document;
		try {
			logger.fine("Try to find name for " + aFile);
			document = readXMLFile(aFile);
			Element root = getElement(document, "RDF");
			if (root != null) {
				Element ontology = getElement(root, "Ontology");
				if (ontology != null) {
					Element title = getElement(root, "title");
					if (title != null) {
						return title.getValue();
					}
					List l = ontology.getAttributes();
					for (int i = 0; i < l.size(); i++) {
						Attribute a = (Attribute) l.get(i);
						if (a.getName().equals("title")) {
							return a.getValue();
						}
					}
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static Document readXMLFile(File f) throws JDOMException, IOException {
		FileInputStream fio = new FileInputStream(f);
		SAXBuilder parser = new SAXBuilder();
		Document reply = parser.build(fio);
		return reply;
	}

	private static Element getElement(Document document, String name) {
		Iterator it = document.getDescendants(new ElementFilter(name));
		if (it.hasNext()) {
			return (Element) it.next();
		} else {
			return null;
		}
	}

	private static Element getElement(Element from, String name) {
		Iterator it = from.getDescendants(new ElementFilter(name));
		if (it.hasNext()) {
			return (Element) it.next();
		} else {
			return null;
		}
	}

	@Override
	public String getClassNameKey() {
		return "flexo_ontology";
	}

	@Override
	public String getURI() {
		return getOntologyURI();
	}

	public String getOntologyURI() {
		return ontologyURI;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String aName) {
		String newURI;
		if (getURI().indexOf("#") > -1) {
			newURI = getURI().substring(0, getURI().indexOf("#")) + aName;
		} else {
			newURI = aName;
		}
		logger.warning("Rename ontology " + getURI() + " to " + newURI + " not implemented yet");
	}

	@Override
	protected void _setOntResource(OntResource r) {
		// not relevant
	}

	public OntModel getOntModel() {
		loadWhenUnloaded();
		return ontModel;
	}

	public void setOntModel(OntModel ontModel) {
		this.ontModel = ontModel;
	}

	public File getAlternativeLocalFile() {
		return alternativeLocalFile;
	}

	/**
	 * Return a vector of all imported ontologies in the context of this ontology. This method is recursive. Ontologies are imported only
	 * once. This ontology is also appened to returned list.
	 * 
	 * @return
	 */
	public Vector<FlexoOntology> getAllImportedOntologies() {
		Vector<FlexoOntology> returned = new Vector<FlexoOntology>();
		appendToAllImportedOntologies(this, returned);
		return returned;
	}

	private static void appendToAllImportedOntologies(FlexoOntology o, Vector<FlexoOntology> v) {
		if (!v.contains(o)) {
			v.add(o);
			for (FlexoOntology importedOntology : o.getImportedOntologies()) {
				appendToAllImportedOntologies(importedOntology, v);
			}
		}
	}

	/**
	 * Return a vector of imported ontologies in the context of this ontology
	 * 
	 * @return
	 */
	public Vector<FlexoOntology> getImportedOntologies() {
		if (getURI().equals(OWL2URIDefinitions.OWL_ONTOLOGY_URI)) {
			// OWL ontology should at least import RDF and RDFS ontologies
			if (!importedOntologies.contains(getOntologyLibrary().getRDFOntology())) {
				importedOntologies.add(getOntologyLibrary().getRDFOntology());
			}
			if (!importedOntologies.contains(getOntologyLibrary().getRDFSOntology())) {
				importedOntologies.add(getOntologyLibrary().getRDFSOntology());
			}
		} else if (getURI().equals(RDFURIDefinitions.RDF_ONTOLOGY_URI)) {
			// RDF ontology should at least import RDFS ontology
			if (!importedOntologies.contains(getOntologyLibrary().getRDFSOntology())) {
				importedOntologies.add(getOntologyLibrary().getRDFSOntology());
			}
		} else if (getURI().equals(RDFSURIDefinitions.RDFS_ONTOLOGY_URI)) {
			// RDFS ontology has no requirement
			if (!importedOntologies.contains(getOntologyLibrary().getRDFOntology())) {
				importedOntologies.add(getOntologyLibrary().getRDFOntology());
			}
		} else {
			// All other ontologies should at least import OWL ontology
			if (!importedOntologies.contains(getOntologyLibrary().getOWLOntology())) {
				importedOntologies.add(getOntologyLibrary().getOWLOntology());
			}
		}

		/*if (importedOntologies.size() == 0) {
			if (getURI().equals(OntologyLibrary.OWL_ONTOLOGY_URI)) {
				importedOntologies.add(getOntologyLibrary().getRDFOntology());
				importedOntologies.add(getOntologyLibrary().getRDFSOntology());
			} else if (getURI().equals(OntologyLibrary.RDF_ONTOLOGY_URI)) {
				importedOntologies.add(getOntologyLibrary().getRDFSOntology());
			} else if (!getURI().equals(OntologyLibrary.RDFS_ONTOLOGY_URI)) {
				importedOntologies.add(getOntologyLibrary().getOWLOntology());
			}
		}*/
		return importedOntologies;
	}

	/**
	 * Return the local vision of OWL Thing concept
	 * 
	 * @return
	 */
	public OntologyClass getThingConcept() {
		return THING_CONCEPT;
	}

	/**
	 * Return the root concept class, which is the local vision of OWL Thing concept
	 * 
	 * @return
	 */
	public OntologyClass getRootClass() {
		return getThingConcept();
	}

	@Override
	public String toString() {
		return "ImportedOntology:" + getOntologyURI();
	}

	@Override
	public String getFullyQualifiedName() {
		return getOntologyURI();
	}

	public boolean importOntology(String ontologyURI) throws OntologyNotFoundException {
		if (_library.getOntology(ontologyURI) == null) {
			throw new OntologyNotFoundException();
		} else {
			return importOntology(_library.getOntology(ontologyURI));
		}
	}

	/**
	 * Import ontology if supplied ontology is not yet imported in this ontology.<br>
	 * Return a flag indicating if import was effective (otherwise, it was already done)
	 * 
	 * @param anOntology
	 * @return
	 * @throws OntologyNotFoundException
	 */
	public boolean importOntology(FlexoOntology anOntology) throws OntologyNotFoundException {
		loadWhenUnloaded();

		if (getAllImportedOntologies().contains(anOntology)) {
			return false;
		}

		if (_library.getOntology(anOntology.getOntologyURI()) == null) {
			throw new OntologyNotFoundException();
		} else if (_library.getOntology(anOntology.getOntologyURI()) != anOntology) {
			throw new OntologyNotFoundException();
		}

		String SOURCE = "@prefix rdf:         <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\n"
				+ "@prefix rdfs:        <http://www.w3.org/2000/01/rdf-schema#>.\n"
				+ "@prefix owl:         <http://www.w3.org/2002/07/owl#>.\n" + "<" + getOntologyURI() + "> a owl:Ontology \n"
				+ "   ; owl:imports <" + anOntology.getOntologyURI() + ">.\n";

		logger.fine("About to load source ontology:");
		logger.fine(SOURCE);
		ontModel.read(new StringReader(SOURCE), getOntologyURI(), "N3");

		importedOntologies.add(anOntology);

		setChanged();

		return true;
	}

	public static class OntologyNotFoundException extends Exception {

	};

	public static class DuplicatedOntologyException extends Exception {

	};

	private boolean isNamedClass(OntClass ontClass) {
		return !ontClass.isComplementClass() && !ontClass.isUnionClass() && !ontClass.isIntersectionClass() && !ontClass.isRestriction()
				&& !ontClass.isEnumeratedClass() && StringUtils.isNotEmpty(ontClass.getURI());
	}

	private boolean isNamedResourceOfThisOntology(OntResource ontResource) {
		return StringUtils.isNotEmpty(ontResource.getURI()) && ontResource.getURI().startsWith(getURI());
	}

	private void createConceptsAndProperties() {
		classes.clear();
		individuals.clear();
		dataProperties.clear();
		objectProperties.clear();
		_classes.clear();
		_individuals.clear();
		_dataProperties.clear();
		_objectProperties.clear();

		for (Iterator i = getOntModel().listClasses(); i.hasNext();) {
			OntClass ontClass = (OntClass) i.next();
			if (_classes.get(ontClass) == null && isNamedResourceOfThisOntology(ontClass)) {
				// Only named classes will be appended
				makeNewClass(ontClass);
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Made class " + ontClass.getURI());
				}
			}
		}

		for (Iterator i = getOntModel().listIndividuals(); i.hasNext();) {
			Individual individual = (Individual) i.next();
			if (_individuals.get(individual) == null && isNamedResourceOfThisOntology(individual)) {
				OntologyIndividual newIndividual = makeNewIndividual(individual);
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Made individual " + individual.getURI());
				}
			}
		}

		for (Iterator i = getOntModel().listDatatypeProperties(); i.hasNext();) {
			DatatypeProperty ontProperty = (DatatypeProperty) i.next();
			if (_dataProperties.get(ontProperty) == null && isNamedResourceOfThisOntology(ontProperty)) {
				makeNewDataProperty(ontProperty);
				if (logger.isLoggable(Level.FINE)) {
					logger.fine(getURI() + ": made DataProperty" + ontProperty.getURI() + " in " + getURI());
				}
			}
		}

		for (Iterator i = getOntModel().listObjectProperties(); i.hasNext();) {
			ObjectProperty ontProperty = (ObjectProperty) i.next();
			if (_objectProperties.get(ontProperty) == null && isNamedResourceOfThisOntology(ontProperty)) {
				makeNewObjectProperty(ontProperty);
				if (logger.isLoggable(Level.FINE)) {
					logger.fine(getURI() + ": made ObjectProperty" + ontProperty.getURI() + " in " + getURI());
				}
			}
		}

		// I dont understand why, but on some ontologies (RDF, RDFS and OWL), this is the only way to obtain those properties
		for (Iterator i = ontModel.listAllOntProperties(); i.hasNext();) {
			OntProperty ontProperty = (OntProperty) i.next();
			// Do it if and only if property is not yet existant
			if (getOntologyObject(ontProperty.getURI()) == null) {
				if (ontProperty.canAs(ObjectProperty.class)) {
					makeNewObjectProperty(ontProperty.as(ObjectProperty.class));
				} else if (ontProperty.canAs(DatatypeProperty.class)) {
					makeNewDataProperty(ontProperty.as(DatatypeProperty.class));
				} else if (ontProperty.canAs(AnnotationProperty.class)) {
					AnnotationProperty ap = ontProperty.as(AnnotationProperty.class);
					if (ap.getRange() != null && ap.getRange().getURI().equals(RDFSURIDefinitions.RDFS_LITERAL_URI)) {
						makeNewDataProperty(ontProperty);
					} else if (ap.getRange() != null && ap.getRange().getURI().equals(RDFSURIDefinitions.RDFS_RESOURCE_URI)) {
						makeNewObjectProperty(ontProperty);
					}
				} else {
					// default behaviour: create object property
					makeNewObjectProperty(ontProperty);
				}
			}

		}

		// I dont understand why, but on some ontologies, this is the only way to obtain those classes
		for (NodeIterator i = ontModel.listObjects(); i.hasNext();) {
			RDFNode node = i.nextNode();
			if (node instanceof Resource && ((Resource) node).canAs(OntClass.class)) {
				OntClass ontClass = ((Resource) node).as(OntClass.class);
				if (_classes.get(ontClass) == null && isNamedResourceOfThisOntology(ontClass)) {
					// Only named classes will be appended)
					makeNewClass(ontClass);
					// logger.info("Made class (2)" + ontClass.getURI());
				}
			}
		}

		/*if (getURI().equals(OntologyLibrary.RDFS_ONTOLOGY_URI)) {
			for (StmtIterator i = getOntModel().listStatements(); i.hasNext();) {
				Statement s = i.nextStatement();
				System.out.println("statement = " + s);
			}
			for (NodeIterator i = ontModel.listObjects(); i.hasNext();) {
				RDFNode node = i.nextNode();
				System.out.println("node = " + node);
			}
			for (NodeIterator i = ontModel.listObjects(); i.hasNext();) {
				RDFNode node = i.nextNode();
				if (node.canAs(Resource.class)) {
					System.out.println("resource = " + node.as(Resource.class));
				}
			}
			for (Iterator i = ontModel.listAllOntProperties(); i.hasNext();) {
				OntProperty ontProperty = (OntProperty) i.next();
				System.out.println("Property: " + ontProperty);
				if (ontProperty.canAs(ObjectProperty.class)) {
					System.out.println("ObjectProperty");
				} else if (ontProperty.canAs(DatatypeProperty.class)) {
					System.out.println("DataProperty");
				} else if (ontProperty.canAs(AnnotationProperty.class)) {
					AnnotationProperty ap = ontProperty.as(AnnotationProperty.class);
					System.out.println("AnnotationProperty " + ap.getRange());
				} else if (ontProperty.canAs(FunctionalProperty.class)) {
					System.out.println("FunctionalProperty");
				}
			}
			System.exit(-1);
		}*/

		if (!getURI().equals(OWL2URIDefinitions.OWL_ONTOLOGY_URI) && !getURI().equals(RDFURIDefinitions.RDF_ONTOLOGY_URI)
				&& !getURI().equals(RDFSURIDefinitions.RDFS_ONTOLOGY_URI)) {
			// Following will not apply to RDF, RDFS and OWL ontologies
			handleRedefinitionOfConceptsAndProperties();
		}

		// Special case for OWL ontology, register THING
		if (getURI().equals(OWL2URIDefinitions.OWL_ONTOLOGY_URI)) {
			THING_CONCEPT = getClass(OWL2URIDefinitions.OWL_THING_URI);
		}

		if (!getURI().equals(OWL2URIDefinitions.OWL_ONTOLOGY_URI) && !getURI().equals(RDFURIDefinitions.RDF_ONTOLOGY_URI)
				&& !getURI().equals(RDFSURIDefinitions.RDFS_ONTOLOGY_URI)) {
			// Following will not apply to RDF, RDFS and OWL ontologies
			if (getOntologyLibrary().getOWLOntology() != null) {
				if (!importedOntologies.contains(getOntologyLibrary().getOWLOntology())) {
					importedOntologies.add(getOntologyLibrary().getOWLOntology());
				}
				THING_CONCEPT = makeThingConcept();
			} else {
				logger.warning("Could not find OWL ontology");
			}
		}

		logger.info("Done created all concepts, now initialize them");

		for (OntologyClass aClass : new ArrayList<OntologyClass>(classes.values())) {
			aClass.init();
		}
		for (OntologyIndividual anIndividual : new ArrayList<OntologyIndividual>(individuals.values())) {
			anIndividual.init();
		}
		for (OntologyDataProperty property : new ArrayList<OntologyDataProperty>(dataProperties.values())) {
			property.init();
		}
		for (OntologyObjectProperty property : new ArrayList<OntologyObjectProperty>(objectProperties.values())) {
			property.init();
		}

	}

	private void handleRedefinitionOfConceptsAndProperties() {

		Set<OntClass> redefinedClasses = new HashSet<OntClass>();
		Set<Individual> redefinedIndividuals = new HashSet<Individual>();
		Set<ObjectProperty> redefinedObjectProperties = new HashSet<ObjectProperty>();
		Set<DatatypeProperty> redefinedDatatypeProperties = new HashSet<DatatypeProperty>();

		for (StmtIterator i = getOntModel().listStatements(); i.hasNext();) {
			Statement s = i.nextStatement();
			if (getOntModel().isInBaseModel(s)) {
				// This statement was asserted in this model, so may redefine an other concept defined in imported ontologies
				RDFNode object = s.getObject();
				RDFNode subject = s.getSubject();
				if (object.canAs(OntClass.class)) {
					redefinedClasses.add(object.as(OntClass.class));
				}
				if (subject.canAs(OntClass.class)) {
					redefinedClasses.add(subject.as(OntClass.class));
				}
				if (object.canAs(Individual.class)) {
					redefinedIndividuals.add(object.as(Individual.class));
					// System.out.println("Redefine individual as object because of statement " + s);
				}
				if (subject.canAs(Individual.class)) {
					redefinedIndividuals.add(subject.as(Individual.class));
					// System.out.println("Redefine individual as subject because of statement " + s);
				}
				if (object.canAs(ObjectProperty.class)) {
					redefinedObjectProperties.add(object.as(ObjectProperty.class));
				}
				if (subject.canAs(ObjectProperty.class)) {
					redefinedObjectProperties.add(subject.as(ObjectProperty.class));
				}
				if (object.canAs(DatatypeProperty.class)) {
					redefinedDatatypeProperties.add(object.as(DatatypeProperty.class));
				}
				if (subject.canAs(DatatypeProperty.class)) {
					redefinedDatatypeProperties.add(subject.as(DatatypeProperty.class));
				}
			}
		}
		for (OntClass ontClass : redefinedClasses) {
			// Thing and Class concepts are handled differently
			if (StringUtils.isNotEmpty(ontClass.getURI()) && !OWL2URIDefinitions.OWL_THING_URI.equals(ontClass.getURI())
					&& !OWL2URIDefinitions.OWL_CLASS_URI.equals(ontClass.getURI())
					&& !ontClass.getURI().startsWith("http://www.w3.org/2001/XMLSchema#") && getDeclaredClass(ontClass.getURI()) == null) {
				redefineClass(ontClass);
			}
		}

		// TODO
		/*for (Individual individual : redefinedIndividuals) {
			System.out.println("Ontology " + getURI() + ", redefine individual " + individual);
			redefineIndividual(individual);
		}*/

		for (ObjectProperty ontProperty : redefinedObjectProperties) {
			if (StringUtils.isNotEmpty(ontProperty.getURI()) && getDeclaredObjectProperty(ontProperty.getURI()) == null) {
				redefineObjectProperty(ontProperty);
			}
		}

		for (DatatypeProperty ontProperty : redefinedDatatypeProperties) {
			if (StringUtils.isNotEmpty(ontProperty.getURI()) && getDeclaredDataProperty(ontProperty.getURI()) == null) {
				redefineDataProperty(ontProperty);
			}
		}
	}

	@Override
	protected void update() {
		updateConceptsAndProperties();
	}

	public void updateConceptsAndProperties() {
		logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% " + ontologyURI);
		logger.info("Update ontology " + ontologyURI);

		for (OntologyClass aClass : classes.values()) {
			aClass.update();
		}
		for (OntologyIndividual anIndividual : individuals.values()) {
			anIndividual.update();
		}
		for (OntologyDataProperty property : dataProperties.values()) {
			property.update();
		}
		for (OntologyObjectProperty property : objectProperties.values()) {
			property.update();
		}
	}

	private OntologyClass makeThingConcept() {
		logger.fine("makeThingConcept() in ontology " + this);
		FlexoOntology owlOntology = getOntologyLibrary().getOWLOntology();
		OntologyClass thingInOWLOntology = owlOntology.getClass(OWL2URIDefinitions.OWL_THING_URI);
		THING_CONCEPT = makeNewClass(thingInOWLOntology.getOntResource());
		THING_CONCEPT.setOriginalDefinition(thingInOWLOntology);
		return THING_CONCEPT;
	}

	protected OntologyClass makeNewClass(OntClass ontClass) {
		if (StringUtils.isNotEmpty(ontClass.getURI())) {
			OntologyClass aClass = new OntologyClass(ontClass, this);
			classes.put(ontClass.getURI(), aClass);
			_classes.put(ontClass, aClass);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Made new class " + ontClass.getURI() + " in " + getOntologyURI());
			}
			// aClass.init();
			needsReordering = true;
			setChanged();
			notifyObservers(new OntologyClassInserted(aClass));
			return aClass;
		} else {
			logger.warning("Unexpected null URI for " + ontClass);
			return null;
		}
	}

	protected OntologyClass redefineClass(OntClass ontClass) {
		OntologyClass originalDefinition = getClass(ontClass.getURI());
		if (originalDefinition == null) {
			logger.warning("Tried to redefine " + this + " with null originalDefinition");
			return null;
		}
		logger.info("###### REDEFINE class " + ontClass.getURI() + " originalDefinition=" + originalDefinition);
		OntologyClass returned = makeNewClass(ontClass);
		returned.setOriginalDefinition(originalDefinition);
		logger.info("Declare class " + returned.getName() + " as a redefinition of class initially asserted in "
				+ originalDefinition.getOntology());
		return returned;
	}

	protected OntologyClass removeClass(OntologyClass aClass) {
		classes.remove(aClass.getURI());
		_classes.remove(aClass.getOntResource());
		needsReordering = true;
		setChanged();
		notifyObservers(new OntologyClassRemoved(aClass));
		return aClass;
	}

	protected void renameClass(OntologyClass object, String oldURI, String newURI) {
		if (classes.get(oldURI) == object) {
			classes.remove(oldURI);
			classes.put(newURI, object);
		} else if (classes.get(oldURI) == null) {
			logger.warning("Inconsistent data in Ontology: rename invoked for non previously-existant ontology class");
			classes.put(newURI, object);
		} else {
			logger.severe("Inconsistent data in Ontology: rename invoked while found an other class than the one renamed");
		}
		needsReordering = true;
		setChanged();
		notifyObservers(new OntologyObjectRenamed(object, oldURI, newURI));
	}

	protected OntologyIndividual makeNewIndividual(Individual individual) {
		if (StringUtils.isNotEmpty(individual.getURI())) {
			OntologyIndividual anIndividual = new OntologyIndividual(individual, this);
			individuals.put(individual.getURI(), anIndividual);
			_individuals.put(individual, anIndividual);
			logger.fine("Made new individual for " + anIndividual.getName() + " in " + getOntologyURI());
			// anIndividual.init();
			needsReordering = true;
			setChanged();
			notifyObservers(new OntologyIndividualInserted(anIndividual));
			return anIndividual;
		} else {
			logger.warning("Unexpected null URI for " + individual);
			return null;
		}
	}

	protected OntologyIndividual redefineIndividual(Individual individual) {
		OntologyIndividual originalDefinition = getIndividual(individual.getURI());
		if (originalDefinition == null) {
			logger.warning("Tried to redefine " + this + " with null originalDefinition");
			return null;
		}
		OntologyIndividual returned = makeNewIndividual(individual);
		returned.setOriginalDefinition(originalDefinition);
		logger.info("Declare individual " + returned.getName() + " as a redefinition of individual initially asserted in "
				+ originalDefinition.getOntology());
		return returned;
	}

	protected OntologyIndividual removeIndividual(OntologyIndividual anIndividual) {
		individuals.remove(anIndividual.getURI());
		_individuals.remove(anIndividual.getOntResource());
		needsReordering = true;
		setChanged();
		notifyObservers(new OntologyIndividualRemoved(anIndividual));
		return anIndividual;
	}

	protected void renameIndividual(OntologyIndividual object, String oldURI, String newURI) {
		if (individuals.get(oldURI) == object) {
			individuals.remove(oldURI);
			individuals.put(newURI, object);
		} else if (individuals.get(oldURI) == null) {
			logger.warning("Inconsistent data in Ontology: rename invoked for non previously-existant ontology individual");
			individuals.put(newURI, object);
		} else {
			logger.severe("Inconsistent data in Ontology: rename invoked while found an other individual than the one renamed");
		}
		needsReordering = true;
		setChanged();
		notifyObservers(new OntologyObjectRenamed(object, oldURI, newURI));
	}

	protected OntologyDataProperty makeNewDataProperty(OntProperty ontProperty) {
		if (StringUtils.isNotEmpty(ontProperty.getURI())) {
			OntologyDataProperty property = new OntologyDataProperty(ontProperty, this);
			dataProperties.put(ontProperty.getURI(), property);
			_dataProperties.put(ontProperty, property);
			logger.fine("Made new data property for " + property.getName() + " in " + getOntologyURI());
			// property.init();
			needsReordering = true;
			setChanged();
			notifyObservers(new OntologyDataPropertyInserted(property));
			return property;
		} else {
			logger.warning("Unexpected null URI for " + ontProperty);
			return null;
		}
	}

	protected OntologyDataProperty redefineDataProperty(OntProperty ontProperty) {
		OntologyDataProperty originalDefinition = getDataProperty(ontProperty.getURI());
		if (originalDefinition == null) {
			logger.warning("Tried to redefine " + this + " with null originalDefinition");
			return null;
		}
		OntologyDataProperty returned = makeNewDataProperty(ontProperty);
		returned.setOriginalDefinition(originalDefinition);
		logger.info("Declare data property " + returned.getName() + " as a redefinition of data property initially asserted in "
				+ originalDefinition.getOntology());
		return returned;
	}

	protected OntologyDataProperty removeDataProperty(OntologyDataProperty aProperty) {
		dataProperties.remove(aProperty.getURI());
		_dataProperties.remove(aProperty.getOntResource());
		needsReordering = true;
		setChanged();
		notifyObservers(new OntologyDataPropertyRemoved(aProperty));
		return aProperty;
	}

	protected void renameDataProperty(OntologyDataProperty object, String oldURI, String newURI) {
		if (dataProperties.get(oldURI) == object) {
			dataProperties.remove(oldURI);
			dataProperties.put(newURI, object);
		} else if (dataProperties.get(oldURI) == null) {
			logger.warning("Inconsistent data in Ontology: rename invoked for non previously-existant ontology data property");
			dataProperties.put(newURI, object);
		} else {
			logger.severe("Inconsistent data in Ontology: rename invoked while found an other data property than the one renamed");
		}
		needsReordering = true;
		setChanged();
		notifyObservers(new OntologyObjectRenamed(object, oldURI, newURI));
	}

	protected OntologyObjectProperty makeNewObjectProperty(OntProperty ontProperty) {
		if (StringUtils.isNotEmpty(ontProperty.getURI())) {
			OntologyObjectProperty property = new OntologyObjectProperty(ontProperty, this);
			objectProperties.put(ontProperty.getURI(), property);
			_objectProperties.put(ontProperty, property);
			logger.fine("Made new object property for " + property.getName() + " in " + getOntologyURI());
			// property.init();
			needsReordering = true;
			setChanged();
			notifyObservers(new OntologyObjectPropertyInserted(property));
			return property;
		} else {
			logger.warning("Unexpected null URI for " + ontProperty);
			return null;
		}
	}

	protected OntologyObjectProperty redefineObjectProperty(OntProperty ontProperty) {
		OntologyObjectProperty originalDefinition = getObjectProperty(ontProperty.getURI());
		if (originalDefinition == null) {
			logger.warning("Tried to redefine " + this + " with null originalDefinition");
			return null;
		}
		OntologyObjectProperty returned = makeNewObjectProperty(ontProperty);
		returned.setOriginalDefinition(originalDefinition);
		logger.info("Declare object property " + returned.getName() + " as a redefinition of object property initially asserted in "
				+ originalDefinition.getOntology());
		return returned;
	}

	protected OntologyObjectProperty removeObjectProperty(OntologyObjectProperty aProperty) {
		objectProperties.remove(aProperty.getURI());
		_objectProperties.remove(aProperty.getOntResource());
		needsReordering = true;
		setChanged();
		notifyObservers(new OntologyObjectPropertyRemoved(aProperty));
		return aProperty;
	}

	protected void renameObjectProperty(OntologyObjectProperty object, String oldURI, String newURI) {
		if (objectProperties.get(oldURI) == object) {
			objectProperties.remove(oldURI);
			objectProperties.put(newURI, object);
		} else if (objectProperties.get(oldURI) == null) {
			logger.warning("Inconsistent data in Ontology: rename invoked for non previously-existant ontology object property");
			objectProperties.put(newURI, object);
		} else {
			logger.severe("Inconsistent data in Ontology: rename invoked while found an other object property than the one renamed");
		}
		needsReordering = true;
		setChanged();
		notifyObservers(new OntologyObjectRenamed(object, oldURI, newURI));
	}

	protected void renameObject(OntologyObject object, String oldURI, String newURI) {
		if (object instanceof OntologyIndividual) {
			renameIndividual((OntologyIndividual) object, oldURI, newURI);
		} else if (object instanceof OntologyClass) {
			renameClass((OntologyClass) object, oldURI, newURI);
		} else if (object instanceof OntologyDataProperty) {
			renameDataProperty((OntologyDataProperty) object, oldURI, newURI);
		} else if (object instanceof OntologyObjectProperty) {
			renameObjectProperty((OntologyObjectProperty) object, oldURI, newURI);
		} else {
			logger.warning("Unexpected object " + object);
		}
	}

	protected OntologyObject<?> retrieveOntologyObject(Resource resource) {
		// First try to lookup with resource URI
		if (StringUtils.isNotEmpty(resource.getURI())) {
			OntologyObject<?> returned = getOntologyObject(resource.getURI());
			if (returned != null) {
				return returned;
			}
		}
		// Not found, may be we have to create this new concept
		if (resource.canAs(OntClass.class)) {
			return retrieveOntologyClass(resource.as(OntClass.class));
		} else if (resource.canAs(Individual.class)) {
			return retrieveOntologyIndividual(resource.as(Individual.class));
		} else if (resource.canAs(ObjectProperty.class)) {
			return retrieveOntologyObjectProperty(resource.as(ObjectProperty.class));
		} else if (resource.canAs(DatatypeProperty.class)) {
			return retrieveOntologyDataProperty(resource.as(DatatypeProperty.class));
		} else {
			logger.warning("Unexpected resource: " + resource);
			return null;
		}
	}

	protected OntologyProperty retrieveOntologyProperty(OntProperty property) {
		if (property.canAs(ObjectProperty.class)) {
			return retrieveOntologyObjectProperty(property.as(ObjectProperty.class));
		} else if (property.canAs(DatatypeProperty.class)) {
			return retrieveOntologyDataProperty(property.as(DatatypeProperty.class));
		} else {
			logger.warning("Unexpected property: " + property);
			return null;
		}
	}

	protected OntologyObjectProperty retrieveOntologyObjectProperty(ObjectProperty ontProperty) {

		OntologyObjectProperty returned = _objectProperties.get(ontProperty);
		if (returned != null) {
			return returned;
		}

		returned = makeNewObjectProperty(ontProperty);
		returned.init();

		return returned;
	}

	protected OntologyDataProperty retrieveOntologyDataProperty(DatatypeProperty ontProperty) {

		OntologyDataProperty returned = _dataProperties.get(ontProperty);
		if (returned != null) {
			return returned;
		}

		returned = makeNewDataProperty(ontProperty);
		returned.init();

		return returned;
	}

	protected OntologyIndividual retrieveOntologyIndividual(Individual individual) {

		OntologyIndividual returned = _individuals.get(individual);
		if (returned != null) {
			return returned;
		}

		// Special case for OWL, RDF and RDFS ontologies, don't create individuals !!!
		if (!getURI().equals(OWL2URIDefinitions.OWL_ONTOLOGY_URI) && !getURI().equals(RDFURIDefinitions.RDF_ONTOLOGY_URI)
				&& !getURI().equals(RDFSURIDefinitions.RDFS_ONTOLOGY_URI)) {
			// Following will not apply to RDF, RDFS and OWL ontologies
			if (isNamedResourceOfThisOntology(individual)) {
				returned = makeNewIndividual(individual);
				returned.init();
				return returned;
			}
		}

		return null;
	}

	protected OntologyClass retrieveOntologyClass(OntClass resource) {

		OntologyClass returned = _classes.get(resource);
		if (returned != null) {
			return returned;
		}

		if (isNamedClass(resource) && StringUtils.isNotEmpty(resource.getURI())) {
			returned = getClass(resource.getURI());
			if (returned != null) {
				return returned;
			}
		}

		if (isNamedResourceOfThisOntology(resource)) {
			returned = makeNewClass(resource);
			returned.init();
		}

		else if (resource.isRestriction()) {
			return retrieveRestriction(resource.asRestriction());
		}

		else if (resource.canAs(ComplementClass.class)) {
			returned = new OntologyComplementClass(resource.asComplementClass(), getOntology());
		} else if (resource.canAs(UnionClass.class)) {
			returned = new OntologyUnionClass(resource.asUnionClass(), getOntology());
		} else if (resource.canAs(IntersectionClass.class)) {
			returned = new OntologyIntersectionClass(resource.asIntersectionClass(), getOntology());
		} else {
			// logger.warning("Unexpected class: " + resource);
			return null;
		}

		_classes.put(resource, returned);
		return returned;

	}

	private OntologyRestrictionClass retrieveRestriction(Restriction restriction) {

		OntologyRestrictionClass returned = (OntologyRestrictionClass) _classes.get(restriction);
		if (returned != null) {
			return returned;
		}

		String OWL = getFlexoOntology().getOntModel().getNsPrefixURI("owl");
		Property ON_CLASS = ResourceFactory.createProperty(OWL + OntologyRestrictionClass.ON_CLASS);
		Property ON_DATA_RANGE = ResourceFactory.createProperty(OWL + OntologyRestrictionClass.ON_DATA_RANGE);
		Property QUALIFIED_CARDINALITY = ResourceFactory.createProperty(OWL + OntologyRestrictionClass.QUALIFIED_CARDINALITY);
		Property MIN_QUALIFIED_CARDINALITY = ResourceFactory.createProperty(OWL + OntologyRestrictionClass.MIN_QUALIFIED_CARDINALITY);
		Property MAX_QUALIFIED_CARDINALITY = ResourceFactory.createProperty(OWL + OntologyRestrictionClass.MAX_QUALIFIED_CARDINALITY);

		if (restriction.isSomeValuesFromRestriction()) {
			returned = new SomeValuesFromRestrictionClass(restriction.asSomeValuesFromRestriction(), getOntology());
		} else if (restriction.isAllValuesFromRestriction()) {
			returned = new AllValuesFromRestrictionClass(restriction.asAllValuesFromRestriction(), getOntology());
		} else if (restriction.isHasValueRestriction()) {
			returned = new HasValueRestrictionClass(restriction.asHasValueRestriction(), getOntology());
		} else if (restriction.isCardinalityRestriction()) {
			System.out.println("C'est une cardinality restriction !!!");
			returned = new CardinalityRestrictionClass(restriction.asCardinalityRestriction(), getOntology());
		} else if (restriction.isMinCardinalityRestriction()) {
			System.out.println("C'est une min cardinality restriction !!!");
			returned = new MinCardinalityRestrictionClass(restriction.asMinCardinalityRestriction(), getOntology());
		} else if (restriction.isMaxCardinalityRestriction()) {
			System.out.println("C'est une max cardinality restriction !!!");
			returned = new MaxCardinalityRestrictionClass(restriction.asMaxCardinalityRestriction(), getOntology());
		} else if (restriction.getProperty(ON_CLASS) != null || restriction.getProperty(ON_DATA_RANGE) != null) {
			if (restriction.getProperty(QUALIFIED_CARDINALITY) != null) {
				returned = new CardinalityRestrictionClass(restriction, getOntology());
			} else if (restriction.getProperty(MIN_QUALIFIED_CARDINALITY) != null) {
				returned = new MinCardinalityRestrictionClass(restriction, getOntology());
			} else if (restriction.getProperty(MAX_QUALIFIED_CARDINALITY) != null) {
				returned = new MaxCardinalityRestrictionClass(restriction, getOntology());
			}
		}

		if (returned != null) {
			_classes.put(restriction, returned);
			return returned;
		} else {
			logger.warning("While reading " + getURI() + ": unexpected restriction: " + restriction);
			return null;
		}

	}

	/**
	 * Return all classes accessible in the context of this ontology.<br>
	 * This means that classes are also retrieved from imported ontologies (non-strict mode)
	 * 
	 * @return
	 */
	public List<OntologyClass> getAccessibleClasses() {
		List<OntologyClass> returned = new ArrayList<OntologyClass>();
		for (FlexoOntology o : getAllImportedOntologies()) {
			returned.addAll(o.getClasses());
		}
		removeOriginalFromRedefinedObjects(returned);
		return returned;
	}

	/**
	 * Return all individuals accessible in the context of this ontology.<br>
	 * This means that individuals are also retrieved from imported ontologies (non-strict mode)
	 * 
	 * @return
	 */
	public List<OntologyIndividual> getAccessibleIndividuals() {
		List<OntologyIndividual> returned = new ArrayList<OntologyIndividual>();
		for (FlexoOntology o : getAllImportedOntologies()) {
			returned.addAll(o.getIndividuals());
		}
		removeOriginalFromRedefinedObjects(returned);
		return returned;
	}

	/**
	 * Return all object properties accessible in the context of this ontology.<br>
	 * This means that properties are also retrieved from imported ontologies (non-strict mode)
	 * 
	 * @return
	 */
	public List<OntologyObjectProperty> getAccessibleObjectProperties() {
		List<OntologyObjectProperty> returned = new ArrayList<OntologyObjectProperty>();
		for (FlexoOntology o : getAllImportedOntologies()) {
			returned.addAll(o.getObjectProperties());
		}
		removeOriginalFromRedefinedObjects(returned);
		return returned;
	}

	/**
	 * Return all data properties accessible in the context of this ontology.<br>
	 * This means that properties are also retrieved from imported ontologies (non-strict mode)
	 * 
	 * @return
	 */
	public List<OntologyDataProperty> getAccessibleDataProperties() {
		List<OntologyDataProperty> returned = new ArrayList<OntologyDataProperty>();
		for (FlexoOntology o : getAllImportedOntologies()) {
			returned.addAll(o.getDataProperties());
		}
		removeOriginalFromRedefinedObjects(returned);
		return returned;
	}

	/**
	 * Remove originals from redefined classes<br>
	 * Special case: original Thing definition is kept and redefinitions are excluded
	 * 
	 * @param list
	 */
	private void removeOriginalFromRedefinedObjects(List<? extends OntologyObject<?>> list) {
		for (OntologyObject c : new ArrayList<OntologyObject>(list)) {
			if (c.redefinesOriginalDefinition()) {
				if (c instanceof OntologyClass && ((OntologyClass) c).isThing()) {
					list.remove(c);
				} else {
					list.remove(c.getOriginalDefinition());
				}
			}
		}
	}

	/**
	 * Return all classes explicitely defined in this ontology (strict mode)
	 * 
	 * @return
	 */
	public Vector<OntologyClass> getClasses() {
		if (needsReordering) {
			reorderConceptAndProperties();
		}
		return orderedClasses;
	}

	/**
	 * Return all individuals explicitely defined in this ontology (strict mode)
	 * 
	 * @return
	 */
	public Vector<OntologyIndividual> getIndividuals() {
		if (needsReordering) {
			reorderConceptAndProperties();
		}
		return orderedIndividuals;
	}

	/**
	 * Return all datatype properties explicitely defined in this ontology (strict mode)
	 * 
	 * @return
	 */
	public Vector<OntologyDataProperty> getDataProperties() {
		if (needsReordering) {
			reorderConceptAndProperties();
		}
		return orderedDataProperties;
	}

	/**
	 * Return all object properties explicitely defined in this ontology (strict mode)
	 * 
	 * @return
	 */
	public Vector<OntologyObjectProperty> getObjectProperties() {
		if (needsReordering) {
			reorderConceptAndProperties();
		}
		return orderedObjectProperties;
	}

	private boolean needsReordering = true;

	private void reorderConceptAndProperties() {
		orderedClasses.clear();
		for (OntologyClass aClass : classes.values()) {
			aClass.updateDomainsAndRanges();
			orderedClasses.add(aClass);
		}
		Collections.sort(orderedClasses);

		orderedIndividuals.clear();
		for (OntologyIndividual anIndividual : individuals.values()) {
			anIndividual.updateDomainsAndRanges();
			orderedIndividuals.add(anIndividual);
		}
		Collections.sort(orderedIndividuals);

		orderedDataProperties.clear();
		for (OntologyDataProperty property : dataProperties.values()) {
			property.updateDomainsAndRanges();
			orderedDataProperties.add(property);
		}
		Collections.sort(orderedDataProperties);

		orderedObjectProperties.clear();
		for (OntologyObjectProperty property : objectProperties.values()) {
			property.updateDomainsAndRanges();
			orderedObjectProperties.add(property);
		}
		Collections.sort(orderedObjectProperties);

		needsReordering = false;
	}

	/**
	 * Force load ontology when unloaded
	 * 
	 * @return flag indicating if loading was performed
	 */
	public boolean loadWhenUnloaded() {
		if (!isLoaded && !isLoading) {
			isLoading = true;
			// logger.info("Perform load "+getURI());
			load();
			isLoading = false;
			return true;
		} else {
			// logger.info("Skip loading"+getURI());
			return false;
		}
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public boolean isLoading() {
		return isLoading;
	}

	private static void handleResource(OntResource resource, Hashtable<OntResource, String> renamedResources,
			Hashtable<String, OntResource> renamedURI) {
		for (StmtIterator j = resource.listProperties(); j.hasNext();) {
			Statement s = j.nextStatement();
			Property predicate = s.getPredicate();
			if (predicate.getURI().equals("http://www.w3.org/2000/01/rdf-schema#label")) {
				String baseName = s.getString();
				String newName = baseName;
				int k = 2;
				while (renamedURI.get(newName) != null) {
					System.out.println("Duplicated URI " + newName);
					newName = baseName + k;
					k++;
				}
				renamedResources.put(resource, newName);
				renamedURI.put(newName, resource);
			}
		}
	}

	public static void main2(String[] args) {
		Hashtable<OntResource, String> renamedResources = new Hashtable<OntResource, String>();
		Hashtable<String, OntResource> renamedURI = new Hashtable<String, OntResource>();

		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		String ontologyURI = "file:/tmp/UML2.owl";
		ontModel.read(ontologyURI);
		for (Iterator i = ontModel.listClasses(); i.hasNext();) {
			OntClass aClass = (OntClass) i.next();
			handleResource(aClass, renamedResources, renamedURI);
		}
		for (Iterator i = ontModel.listObjectProperties(); i.hasNext();) {
			OntProperty aProperty = (OntProperty) i.next();
			handleResource(aProperty, renamedResources, renamedURI);
		}
		for (Iterator i = ontModel.listDatatypeProperties(); i.hasNext();) {
			OntProperty aProperty = (OntProperty) i.next();
			handleResource(aProperty, renamedResources, renamedURI);
		}

		for (OntResource r : renamedResources.keySet()) {
			String oldURI = r.getURI();
			String newURI = r.getURI().substring(0, r.getURI().indexOf("#")) + "#" + renamedResources.get(r);
			System.out.println("Rename " + oldURI + " to " + newURI);
			ResourceUtils.renameResource(r, newURI);
		}

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File("/tmp/Prout.owl"));
			ontModel.write(out);
			logger.info("Wrote " + out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.warning("FileNotFoundException: " + e.getMessage());
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.warning("IOException: " + e.getMessage());
			}
		}

	}

	protected void load() {
		logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% " + ontologyURI);
		logger.info("Try to load ontology " + ontologyURI);

		ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, _library, null);
		// FIXES add strict to FALSE (XtoF)
		// FIXES OPENFLEXO-39, OPENFLEXO-40, OPENFLEXO-41, OPENFLEXO-42, OPENFLEXO-43, OPENFLEXO-44
		// ontModel.setStrictMode(false);

		// we have a local copy of flexo concept ontology
		if (alternativeLocalFile != null) {
			logger.fine("Alternative local file: " + alternativeLocalFile.getAbsolutePath());
			try {
				ontModel.getDocumentManager().addAltEntry(ontologyURI, alternativeLocalFile.toURL().toString());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		// read the source document
		try {
			logger.info("BEGIN Read " + ontologyURI);
			ontModel.read(ontologyURI);
			logger.info("END read " + ontologyURI);
		} catch (Exception e) {
			logger.warning("Unexpected exception while reading ontology " + ontologyURI);
			logger.warning("Exception " + e.getMessage() + ". See logs for details");
			e.printStackTrace();
		}

		isLoaded = true;

		for (Object o : ontModel.listImportedOntologyURIs()) {
			FlexoOntology importedOnt = _library.getOntology((String) o);
			logger.info("importedOnt= " + importedOnt);
			if (importedOnt != null) {
				importedOnt.loadWhenUnloaded();
				importedOntologies.add(importedOnt);
			}
		}

		logger.info("For " + ontologyURI + " Imported ontologies = " + getImportedOntologies());

		logger.info("Loaded ontology " + ontologyURI + " search for concepts and properties");

		for (FlexoOntology o : getImportedOntologies()) {
			logger.info("Imported ontology: " + o);
		}

		createConceptsAndProperties();

		logger.info("Finished loading ontology " + ontologyURI);

	}

	public void describe() {
		DescribeClass dc = new DescribeClass();
		DescribeDatatypeProperty dp = new DescribeDatatypeProperty();

		for (Iterator i = getOntModel().listClasses(); i.hasNext();) {
			dc.describeClass(System.out, (OntClass) i.next());
		}

		for (Iterator i = getOntModel().listObjectProperties(); i.hasNext();) {
			ObjectProperty property = (ObjectProperty) i.next();
			System.out.println("Object Property: " + property.getLocalName());
		}

		for (Iterator i = getOntModel().listDatatypeProperties(); i.hasNext();) {
			dp.describeProperty(System.out, (DatatypeProperty) i.next());
		}

	}

	@Override
	public FlexoOntology getFlexoOntology() {
		return this;
	}

	@Override
	public FlexoProject getProject() {
		return _library.getProject();
	}

	@Override
	public void setDescription(String description) {
		// TODO
	}

	@Override
	public OntResource getOntResource() {
		return null;
		// return ontModel.createTypedLiteral(this, XSDDatatype.XSDanyURI);
	}

	@Override
	public Resource getResource() {
		return getOntModel().createResource(getURI());
	}

	@Override
	public void saveToFile(File aFile) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(aFile);
			RDFWriter writer = ontModel.getWriter("RDF/XML-ABBREV");
			writer.setProperty("xmlbase", getOntologyURI());
			writer.write(ontModel.getBaseModel(), out, getOntologyURI());
			// getOntModel().setNsPrefix("base", getOntologyURI());
			// getOntModel().write(out, "RDF/XML-ABBREV", getOntologyURI()); // "RDF/XML-ABBREV"
			clearIsModified(true);
			logger.info("Wrote " + aFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.warning("FileNotFoundException: " + e.getMessage());
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.warning("IOException: " + e.getMessage());
			}
		}
	}

	public void save() throws SaveResourceException {

		saveToFile(getAlternativeLocalFile());
	}

	@Override
	public OntologyLibrary getOntologyLibrary() {
		return _library;
	}

	@Override
	public boolean isSuperConceptOf(OntologyObject concept) {
		return false;
	}

	/**
	 * Return a vector of Ontology class, which correspond to all classes necessary to see all classes and individuals belonging to current
	 * ontology
	 * 
	 * @param context
	 * @return
	 */
	public Vector<OntologyClass> getRootClasses() {
		Vector<OntologyClass> topLevelClasses = new Vector<OntologyClass>();
		for (OntologyClass aClass : getClasses()) {
			addTopLevelClass(aClass, topLevelClasses);
		}
		for (OntologyIndividual anIndividual : getIndividuals()) {
			addTopLevelClass(anIndividual, topLevelClasses);
		}
		return topLevelClasses;
	}

	private static void addTopLevelClass(OntologyClass aClass, Vector<OntologyClass> topLevelClasses) {
		// System.out.println("addTopLevelClass " + aClass + " for " + topLevelClasses);
		if (aClass.getSuperClasses().size() == 0) {
			if (!topLevelClasses.contains(aClass)) {
				topLevelClasses.add(aClass);
			}
			return;
		}
		for (OntologyClass superClass : aClass.getSuperClasses()) {
			if (superClass != aClass) {
				addTopLevelClass(superClass, topLevelClasses);
			}
		}
	}

	private static void addTopLevelClass(OntologyIndividual anIndividual, Vector<OntologyClass> topLevelClasses) {
		for (OntologyClass superClass : anIndividual.getSuperClasses()) {
			addTopLevelClass(superClass, topLevelClasses);
		}
	}

	/**
	 * Return a vector of Ontology properties, which correspond to all properties necessary to see all properties belonging to current
	 * ontology
	 * 
	 * @param context
	 * @return
	 */
	public Vector<OntologyProperty> getRootProperties() {
		Vector<OntologyProperty> topLevelProperties = new Vector<OntologyProperty>();
		for (OntologyProperty aProperty : getObjectProperties()) {
			addTopLevelProperty(aProperty, topLevelProperties);
		}
		for (OntologyProperty aProperty : getDataProperties()) {
			addTopLevelProperty(aProperty, topLevelProperties);
		}
		return topLevelProperties;
	}

	private void addTopLevelProperty(OntologyProperty aProperty, Vector<OntologyProperty> topLevelProperties) {
		if (aProperty.getSuperProperties().size() == 0) {
			if (!topLevelProperties.contains(aProperty)) {
				topLevelProperties.add(aProperty);
			}
			return;
		}
		for (OntologyProperty superProperty : aProperty.getSuperProperties()) {
			addTopLevelProperty(superProperty, topLevelProperties);
		}
	}

	@Override
	public boolean getIsReadOnly() {
		return readOnly;
	}

	public void setIsReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Return true if URI is valid regarding its unicity (no one other object has same URI)
	 * 
	 * @param uri
	 * @return
	 */
	public boolean testValidURI(String name) {
		return getOntologyLibrary().testValidURI(getURI(), name);
	}

	public String makeURI(String name) {
		return getURI() + "#" + name;
	}

	public void assumeOntologyImportForReference(OntologyObject object) {
		if (!getImportedOntologies().contains(object.getFlexoOntology())) {
			logger.info("Import ontology:" + object.getFlexoOntology());
			try {
				importOntology(object.getFlexoOntology());
			} catch (OntologyNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public OntologyIndividual createOntologyIndividual(String name, OntologyClass father) throws DuplicateURIException {
		assumeOntologyImportForReference(father);
		OntModel ontModel = getOntModel();
		String uri = makeURI(name);
		if (testValidURI(name)) {
			Individual individual = ontModel.createIndividual(uri, father.getOntResource());
			OntologyIndividual returned = makeNewIndividual(individual);
			returned.init();
			return returned;
		} else {
			throw new DuplicateURIException(uri);
		}
	}

	public OntologyClass createOntologyClass(String name) throws DuplicateURIException {
		return createOntologyClass(name, null);
	}

	public OntologyClass createOntologyClass(String name, OntologyClass father) throws DuplicateURIException {
		if (father != null) {
			assumeOntologyImportForReference(father);
		}
		OntModel ontModel = getOntModel();
		String uri = makeURI(name);
		if (testValidURI(name)) {
			OntClass aClass = ontModel.createClass(uri);
			if (father != null) {
				aClass.addSuperClass(father.getOntResource());
			}
			OntologyClass returned = makeNewClass(aClass);
			returned.init();
			return returned;
		} else {
			throw new DuplicateURIException(uri);
		}
	}

	public OntologyRestrictionClass createRestriction(OntologyClass subjectClass, OntologyProperty property,
			OntologyRestrictionClass.RestrictionType type, int cardinality, OntologyClass objectClass) {
		if (subjectClass != null) {
			assumeOntologyImportForReference(subjectClass);
		}
		if (objectClass != null) {
			assumeOntologyImportForReference(objectClass);
		}
		if (property != null) {
			assumeOntologyImportForReference(property);
		}

		OntModel ontModel = getOntModel();

		Restriction restriction = null;
		String OWL = getFlexoOntology().getOntModel().getNsPrefixURI("owl");
		Property ON_CLASS = ResourceFactory.createProperty(OWL + "onClass");

		switch (type) {
		case Some:
			restriction = ontModel.createSomeValuesFromRestriction(null, property.getOntProperty(), objectClass.getOntResource());
			break;
		case Only:
			restriction = ontModel.createAllValuesFromRestriction(null, property.getOntProperty(), objectClass.getOntResource());
			break;
		case Exact:
			Property QUALIFIED_CARDINALITY = ResourceFactory.createProperty(OWL + "qualifiedCardinality");
			restriction = ontModel.createRestriction(property.getOntProperty());
			restriction.addProperty(ON_CLASS, objectClass.getOntResource());
			restriction.addLiteral(QUALIFIED_CARDINALITY, cardinality);
			break;
		case Min:
			Property MIN_QUALIFIED_CARDINALITY = ResourceFactory.createProperty(OWL + "minQualifiedCardinality");
			restriction = ontModel.createRestriction(property.getOntProperty());
			restriction.addProperty(ON_CLASS, objectClass.getOntResource());
			restriction.addLiteral(MIN_QUALIFIED_CARDINALITY, cardinality);
			break;
		case Max:
			Property MAX_QUALIFIED_CARDINALITY = ResourceFactory.createProperty(OWL + "maxQualifiedCardinality");
			restriction = ontModel.createRestriction(property.getOntProperty());
			restriction.addProperty(ON_CLASS, objectClass.getOntResource());
			restriction.addLiteral(MAX_QUALIFIED_CARDINALITY, cardinality);
			break;

		default:
			break;
		}

		if (restriction != null) {
			return getOntology().retrieveRestriction(restriction);
		}

		setIsModified();

		logger.warning("Could not create restriction for " + property.getURI());
		return null;
	}

	public OntologyClass newOntologyClass(FlexoEditor editor) {
		CreateOntologyClass action = CreateOntologyClass.actionType.makeNewAction(this, null, editor).doAction();
		return action.getNewClass();
	}

	public OntologyIndividual newOntologyIndividual(FlexoEditor editor) {
		CreateOntologyIndividual action = CreateOntologyIndividual.actionType.makeNewAction(this, null, editor).doAction();
		return action.getNewIndividual();
	}

	public OntologyObjectProperty newOntologyObjectProperty(FlexoEditor editor) {
		CreateObjectProperty action = CreateObjectProperty.actionType.makeNewAction(this, null, editor).doAction();
		return action.getNewProperty();
	}

	public OntologyDataProperty newCreateDataProperty(FlexoEditor editor) {
		CreateDataProperty action = CreateDataProperty.actionType.makeNewAction(this, null, editor).doAction();
		return action.getNewProperty();
	}

	public OntologyObject deleteOntologyObject(OntologyObject o, FlexoEditor editor) {
		DeleteOntologyObjects.actionType.makeNewAction(o, null, editor).doAction();
		return o;
	}

	@Override
	public boolean isOntology() {
		return true;
	}

	/**
	 * Retrieve an ontology object from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public OntologyObject<?> getOntologyObject(String objectURI) {

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("retrieve OntologyObject " + objectURI);
		}

		if (objectURI == null) {
			return null;
		}

		if (objectURI.endsWith("#")) {
			String potentialOntologyURI = objectURI.substring(0, objectURI.lastIndexOf("#"));
			FlexoOntology returned = getOntologyLibrary().getOntology(ontologyURI);
			if (returned != null) {
				return returned;
			}
		}

		if (objectURI.equals(getURI())) {
			return this;
		}

		OntologyObject<?> returned = null;

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

		// logger.warning("Cannot find OntologyObject " + objectURI);
		return null;
	}

	/**
	 * Retrieve an class from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public OntologyClass getClass(String classURI) {
		if (classURI == null) {
			return null;
		}
		OntologyClass returned = getDeclaredClass(classURI);
		if (returned != null) {
			return returned;
		}

		for (FlexoOntology o : getAllImportedOntologies()) {
			returned = o.getDeclaredClass(classURI);
			if (returned != null) {
				return returned;
			}
		}
		return null;
	}

	public OntologyClass getDeclaredClass(String classURI) {
		if (classURI == null) {
			return null;
		}
		return classes.get(classURI);
	}

	/**
	 * Retrieve an individual from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public OntologyIndividual getIndividual(String individualURI) {
		if (individualURI == null) {
			return null;
		}
		OntologyIndividual returned = getDeclaredIndividual(individualURI);
		if (returned != null) {
			return returned;
		}
		for (FlexoOntology o : getAllImportedOntologies()) {
			returned = o.getDeclaredIndividual(individualURI);
			if (returned != null) {
				return returned;
			}
		}
		return null;
	}

	public OntologyIndividual getDeclaredIndividual(String individualURI) {
		if (individualURI == null) {
			return null;
		}
		return individuals.get(individualURI);
	}

	/**
	 * Retrieve an object property from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public OntologyObjectProperty getObjectProperty(String propertyURI) {
		if (propertyURI == null) {
			return null;
		}
		OntologyObjectProperty returned = getDeclaredObjectProperty(propertyURI);
		if (returned != null) {
			return returned;
		}
		for (FlexoOntology o : getAllImportedOntologies()) {
			returned = o.getDeclaredObjectProperty(propertyURI);
			if (returned != null) {
				return returned;
			}
		}
		return null;
	}

	public OntologyObjectProperty getDeclaredObjectProperty(String propertyURI) {
		if (propertyURI == null) {
			return null;
		}
		return objectProperties.get(propertyURI);
	}

	/**
	 * Retrieve a data property from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public OntologyDataProperty getDataProperty(String propertyURI) {
		if (propertyURI == null) {
			return null;
		}
		OntologyDataProperty returned = getDeclaredDataProperty(propertyURI);
		if (returned != null) {
			return returned;
		}
		for (FlexoOntology o : getAllImportedOntologies()) {
			returned = o.getDeclaredDataProperty(propertyURI);
			if (returned != null) {
				return returned;
			}
		}
		return null;
	}

	public OntologyDataProperty getDeclaredDataProperty(String propertyURI) {
		if (propertyURI == null) {
			return null;
		}
		return dataProperties.get(propertyURI);
	}

	/**
	 * Retrieve a property from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public OntologyProperty getProperty(String objectURI) {
		OntologyProperty returned = getObjectProperty(objectURI);
		if (returned != null) {
			return returned;
		}
		returned = getDataProperty(objectURI);
		if (returned != null) {
			return returned;
		}
		return null;
	}

}
