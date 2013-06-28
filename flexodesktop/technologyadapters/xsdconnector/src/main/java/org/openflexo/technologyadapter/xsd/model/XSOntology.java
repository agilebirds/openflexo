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


package org.openflexo.technologyadapter.xsd.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyAnnotation;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyContainer;
import org.openflexo.foundation.ontology.IFlexoOntologyModel;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.OntologyUtils;
import org.openflexo.foundation.ontology.W3URIDefinitions;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.metamodel.XSDDataType;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntDataProperty;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntObjectProperty;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntProperty;
import org.w3c.dom.Document;

import com.sun.xml.xsom.XSSimpleType;




/**
 *
 * This class defines and implements the XSD/XML Abstract Ontology Class
 * 
 * @author sylvain, luka, Christophe
 * 
 */

public abstract class XSOntology extends AbstractXSOntObject implements IFlexoOntology, XSOntologyURIDefinitions, W3URIDefinitions, IFlexoOntologyModel {


	protected FlexoResource<?>  modelResource;
	
	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSOntology.class.getPackage()
			.getName());


	// Objects contained in the Model + MetaModel / Ontology
	
	protected final Map<XSSimpleType, XSDDataType> dataTypes = new HashMap<XSSimpleType, XSDDataType>();
	protected final Map<String, XSOntDataProperty> dataProperties = new HashMap<String, XSOntDataProperty>();
	protected final Map<String, XSOntClass> classes = new HashMap<String, XSOntClass>();
	protected final Map<String, XSOntObjectProperty> objectProperties = new HashMap<String, XSOntObjectProperty>();
	protected final Map<String, XSOntIndividual> individuals = new HashMap<String, XSOntIndividual>();

	public XSOntology(String ontologyURI, File xmlFile, XSDTechnologyAdapter adapter) {
		super(null, computeName(xmlFile), ontologyURI, adapter);
	}

	private static String computeName(File xsdFile) {
		return xsdFile.getName();
	}

	public String getOntologyURI() {
		return getURI();
	}

	@Override
	public List<XSOntology> getImportedOntologies() {
		return Collections.emptyList();
	}


	@Override
	public XSOntClass getRootConcept() {
		return null;
	}

	@Override
	public List<XSOntClass> getClasses() {
		return new ArrayList<XSOntClass>(classes.values());
	}

	@Override
	public XSOntClass getClass(String classURI) {
		XSOntClass result = classes.get(classURI);
		if (result == null) {
			if (getImportedOntologies() != null) {
				for (XSOntology o : getImportedOntologies()) {
					if (o != this) {
						result = o.getClass(classURI);
						if (result != null) {
							return result;
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public List<XSOntClass> getAccessibleClasses() {
		Map<String, XSOntClass> result = new HashMap<String, XSOntClass>();
		for (XSOntology o : OntologyUtils.getAllImportedOntologies(this)) {
			for (XSOntClass c : o.getClasses()) {
				result.put(c.getURI(), c);
			}
		}
		return new ArrayList<XSOntClass>(result.values());
	}

	
	@Override
	public List<XSOntObjectProperty> getObjectProperties() {
		return new ArrayList<XSOntObjectProperty>(objectProperties.values());
	}

	@Override
	public XSOntObjectProperty getObjectProperty(String propertyURI) {
		return objectProperties.get(propertyURI);
	}

	@Override
	public List<XSOntObjectProperty> getAccessibleObjectProperties() {
		Map<String, XSOntObjectProperty> result = new HashMap<String, XSOntObjectProperty>();
		for (XSOntology o : getImportedOntologies()) {
			for (XSOntObjectProperty op : o.getObjectProperties()) {
				result.put(op.getURI(), op);
			}
		}
		return new ArrayList<XSOntObjectProperty>(result.values());
	}

	@Override
	public IFlexoOntologyStructuralProperty getProperty(String objectURI) {
		IFlexoOntologyStructuralProperty result = getDataProperty(objectURI);
		if (result == null) {
			result = getObjectProperty(objectURI);
		}
		return result;
	}

	@Override
	public List<XSOntIndividual> getIndividuals() {
		return new ArrayList<XSOntIndividual>(individuals.values());
	}


	// TODO, TO BE OPTIMIZED
	public List<XSOntIndividual> getIndividualsOfClass(XSOntClass aClass) {
		ArrayList<XSOntIndividual> returned = new ArrayList<XSOntIndividual>();
		for (XSOntIndividual o : individuals.values()){
			if (o.getType() == aClass) {
				returned.add(o);
			}
		}
		return returned;
	}

	@Override
	public XSOntIndividual getIndividual(String individualURI) {
		return individuals.get(individualURI);
	}

	@Override
	public List<XSOntIndividual> getAccessibleIndividuals() {
		Map<String, XSOntIndividual> result = new HashMap<String, XSOntIndividual>();
		for (XSOntology o : getImportedOntologies()) {
			for (XSOntIndividual i : o.getIndividuals()) {
				result.put(i.getURI(), i);
			}
		}
		return new ArrayList<XSOntIndividual>(result.values());
	}

	/**
	 * 
	 * createOntologyIndividual
	 * 
	 * 			Creates a new ontology individual WITHOUT adding it to the individuals of that particular ontology yet
	 * 			This is because we need to have all the object property values before calculating URIs (by ModelSlot) 
	 * 	
	 * @param type
	 * @return
	 * @throws DuplicateURIException
	 */

	public XSOntIndividual createOntologyIndividual(XSOntClass type)  {

		XSOntIndividual individual = new XSOntIndividual(getTechnologyAdapter());
		individual.setName(type.getName());
		individual.setType(type);
		return individual;
	}	

	/**
	 * 
	 * addIndividual
	 * 
	 * 			add a new Individual to the ontoloty
	 * 	
	 * @param name
	 * @param type
	 * @return
	 * @throws DuplicateURIException
	 */

	public void addIndividual(XSOntIndividual individual) {
		String indUri = individual.getURI();
		individuals.put(indUri, individual);
	}



	@Override
	public IFlexoOntologyConcept getOntologyObject(String objectURI) {
		IFlexoOntologyConcept result = getClass(objectURI);
		if (result == null) {
			result = getProperty(objectURI);
		}
		if (result == null) {
			result = getIndividual(objectURI);
		}
		return result;
	}
	
	public static String findOntologyURI(File f) {
		return "http://www.openflexo.org/XSD/" + f.getName();
	}

	@Override
	public String getDisplayableDescription() {
		return "Ontology " + getName();
	}

	public Object getObject(String objectURI) {
		return getOntologyObject(objectURI);
	}


	private Set<XSOntIndividual> getRootElements() {
		Set<XSOntIndividual> result = new HashSet<XSOntIndividual>();
		for (XSOntIndividual individual : this.getIndividuals()) {
			if (individual.getParent() == null) {
				result.add(individual);
			}
		}
		return result;
	}
	
	@Override
	public List<IFlexoOntologyContainer> getSubContainers() {
		// TODO implement this
		return null;
	}

	@Override
	public List<AbstractXSOntConcept> getConcepts() {
		ArrayList<AbstractXSOntConcept> returned = new ArrayList<AbstractXSOntConcept>();
		returned.addAll(classes.values());
		returned.addAll(individuals.values());
		returned.addAll(objectProperties.values());
		returned.addAll(dataProperties.values());
		return returned;
	}

	@Override
	public List<XSDDataType> getDataTypes() {
		ArrayList<XSDDataType> returned = new ArrayList<XSDDataType>();
		return returned;
	}

	@Override
	public String getVersion() {
		// TODO implement this
		return null;
	}

	@Override
	public List<? extends IFlexoOntologyAnnotation> getAnnotations() {
		// TODO implement this
		return null;
	}

	@Override
	public AbstractXSOntConcept getDeclaredOntologyObject(String objectURI) {
		AbstractXSOntConcept result = getDeclaredClass(objectURI);
		if (result == null) {
			result = getDeclaredProperty(objectURI);
		}
		if (result == null) {
			result = getDeclaredIndividual(objectURI);
		}
		return result;
	}

	@Override
	public XSOntClass getDeclaredClass(String classURI) {
		return classes.get(classURI);
	}

	@Override
	public XSOntIndividual getDeclaredIndividual(String individualURI) {
		return individuals.get(individualURI);
	}

	@Override
	public XSOntObjectProperty getDeclaredObjectProperty(String propertyURI) {
		return objectProperties.get(propertyURI);
	}

	@Override
	public XSOntDataProperty getDeclaredDataProperty(String propertyURI) {
		return dataProperties.get(propertyURI);
	}

	@Override
	public XSOntProperty getDeclaredProperty(String objectURI) {
		XSOntProperty result = getDeclaredObjectProperty(objectURI);
		if (result == null) {
			result = getDeclaredDataProperty(objectURI);
		}
		return result;
	}

	
	public void clearAllRangeAndDomain() {
		for (XSOntClass o : classes.values()) {
			o.clearPropertiesTakingMyselfAsRangeOrDomain();
		}
		for (XSOntDataProperty o : dataProperties.values()) {
			o.clearPropertiesTakingMyselfAsRangeOrDomain();
			o.resetDomain();
		}
		for (XSOntObjectProperty o : objectProperties.values()) {
			o.clearPropertiesTakingMyselfAsRangeOrDomain();
			o.resetDomain();
			o.resetRange();
			o.clearSuperProperties();
		}
		for (XSOntIndividual o : individuals.values()) {
			o.clearPropertiesTakingMyselfAsRangeOrDomain();
		}
	}
	
	public Document toXML() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		for (XSOntIndividual individual : getRootElements()) {
			doc.appendChild(individual.toXML(doc));
		}

		return doc;
	}

	
}
