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
package org.openflexo.technologyadapter.xsd.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyAnnotation;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyContainer;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.OntologyUtils;
import org.openflexo.foundation.ontology.W3URIDefinitions;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.w3c.dom.Document;

import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;

public abstract class XSOntology extends AbstractXSOntObject implements IFlexoOntology, XSOntologyURIDefinitions, W3URIDefinitions {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSOntology.class.getPackage()
			.getName());

	private final File originalXsdFile;
	private XSSchemaSet schemaSet;
	private XSDeclarationsFetcher fetcher;

	private boolean isLoaded = false;
	private boolean isLoading = false;
	private boolean isReadOnly = true;

	private XSOntClass thingClass;

	private final Map<String, XSOntClass> classes = new HashMap<String, XSOntClass>();
	private final Map<String, XSOntDataProperty> dataProperties = new HashMap<String, XSOntDataProperty>();
	private final Map<String, XSOntObjectProperty> objectProperties = new HashMap<String, XSOntObjectProperty>();
	private final Map<String, XSOntIndividual> individuals = new HashMap<String, XSOntIndividual>();
	private final Map<XSSimpleType, XSDDataType> dataTypes = new HashMap<XSSimpleType, XSDDataType>();

	public XSOntology(String ontologyURI, File xsdFile, XSDTechnologyAdapter adapter) {
		super(null, computeName(xsdFile), ontologyURI, adapter);
		this.originalXsdFile = xsdFile;
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

	public Set<XSOntology> getAllImportedOntologies() {
		return OntologyUtils.getAllImportedOntologies(this);
	}

	@Override
	public boolean getIsReadOnly() {
		return isReadOnly;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	@Override
	public XSOntology getFlexoOntology() {
		return this;
	}

	@Override
	public XSOntology getOntology() {
		return this;
	}

	protected XSDeclarationsFetcher getFetcher() {
		return fetcher;
	}

	private boolean addClass(XSOntClass c) {
		if (classes.containsKey(c.getURI()) == false) {
			classes.put(c.getURI(), c);
			return true;
		}
		return false;
	}

	@Override
	public XSOntClass getRootConcept() {
		return thingClass;
	}

	private static boolean mapsToClass(XSElementDecl element) {
		if (element.getType().isComplexType()) {
			logger.info("CG DEBUG XML : this element maps to a class : " + element.getName());
			return true;
		}
		else {

			logger.info("CG DEBUG XML : this element does no map to a class : " + element.getName());
			return false;	
		}
		// TODO check if there's a need to check for attribute if SimpleType.
	}

	private XSOntClass loadClass(XSDeclaration declaration) {
		String name = declaration.getName();
		String uri = fetcher.getUri(declaration);
		XSOntClass xsClass = new XSOntClass(this, name, uri, getTechnologyAdapter());
		classes.put(uri, xsClass);
		return xsClass;
	}

	private void loadClasses() {
		// TODO if a declaration (base) type is derived, get the correct superclass
		classes.clear();
		thingClass = new XSOntClass(this, "Thing", XS_THING_URI, getTechnologyAdapter());
		addClass(thingClass);
/*
 * Ne semble pas nécessaire étant donné qu'on ne peut pas créer des choses de ce type
		for (XSComplexType complexType : fetcher.getComplexTypes()) {
			XSOntClass xsClass = loadClass(complexType);
			xsClass.addToSuperClasses(getRootConcept());
		}
	*/	
		
		for (XSElementDecl element : fetcher.getElementDecls()) {
			if (mapsToClass(element)) {
				XSOntClass xsClass = loadClass(element);
				try {
					/* XSOntClass superClass = classes.get(fetcher.getUri(element.getType())); */
					xsClass.addToSuperClasses(getRootConcept());
				} catch (Exception e) {

				}
			}
		}
		for (XSAttGroupDecl attGroup : fetcher.getAttGroupDecls()) {
			XSOntClass xsClass = loadClass(attGroup);
			xsClass.addToSuperClasses(getRootConcept());
		}
		for (XSModelGroupDecl modelGroup : fetcher.getModelGroupDecls()) {
			XSOntClass xsClass = loadClass(modelGroup);
			xsClass.addToSuperClasses(getRootConcept());
		}
	}

	private XSDDataType computeDataType(XSSimpleType simpleType) {
		XSDDataType returned = dataTypes.get(simpleType);
		if (returned == null) {
			returned = new XSDDataType(simpleType, this, getTechnologyAdapter());
			dataTypes.put(simpleType, returned);
		}
		return returned;
	}

	private void addDomainIfPossible(XSOntProperty property, String conceptUri) {
		String ownerUri = fetcher.getOwnerUri(conceptUri);
		if (ownerUri != null) {
			XSOntClass owner = getClass(ownerUri);
			if (owner != null) {
				property.newDomainFound(owner);
				owner.addPropertyTakingMyselfAsDomain(property);
			}
		}
	}

	private XSOntDataProperty loadDataProperty(XSDeclaration declaration) {
		String name = declaration.getName();
		String uri = fetcher.getUri(declaration);
		XSOntDataProperty xsDataProperty = new XSOntDataProperty(this, name, uri, getTechnologyAdapter());
		dataProperties.put(uri, xsDataProperty);
		addDomainIfPossible(xsDataProperty, uri);
		return xsDataProperty;
	}

	private void loadDataProperties() {
		dataProperties.clear();
		for (XSSimpleType simpleType : fetcher.getSimpleTypes()) {
			XSOntDataProperty xsDataProperty = loadDataProperty(simpleType);
			xsDataProperty.setDataType(computeDataType(simpleType));
		}
		for (XSElementDecl element : fetcher.getElementDecls()) {
			logger.info("CG XML DEBUG load Data Properties for : " + element.getName());
			if (mapsToClass(element) == false) {
				XSOntDataProperty xsDataProperty = loadDataProperty(element);
				xsDataProperty.setDataType(computeDataType(element.getType().asSimpleType()));
			}
		}
		for (XSAttributeDecl attribute : fetcher.getAttributeDecls()) {
			XSOntDataProperty xsDataProperty = loadDataProperty(attribute);
			xsDataProperty.setIsFromAttribute(true);
			xsDataProperty.setDataType(computeDataType(attribute.getType()));
		}
	}

	private XSOntObjectProperty loadPrefixedProperty(XSDeclaration declaration, XSOntObjectProperty parent) {
		String prefix = parent.getName();
		String name = prefix + declaration.getName();
		String uri = fetcher.getNamespace(declaration) + "#" + name;
		XSOntObjectProperty property = new XSOntObjectProperty(this, name, uri, getTechnologyAdapter());
		property.addSuperProperty(parent);
		objectProperties.put(property.getURI(), property);
		return property;
	}

	private void loadObjectProperties() {
		objectProperties.clear();
		XSOntObjectProperty hasChild = new XSOntObjectProperty(this, XS_HASCHILD_PROPERTY_NAME, getTechnologyAdapter());
		objectProperties.put(hasChild.getURI(), hasChild);
		XSOntObjectProperty hasParent = new XSOntObjectProperty(this, XS_HASPARENT_PROPERTY_NAME, getTechnologyAdapter());
		objectProperties.put(hasParent.getURI(), hasParent);

		for (XSElementDecl element : fetcher.getElementDecls()) {
			if (mapsToClass(element)) {
				String uri = fetcher.getUri(element);
				XSOntClass ontClass = getClass(uri);
				ontClass.addPropertyTakingMyselfAsRange(hasChild);
				ontClass.addPropertyTakingMyselfAsDomain(hasChild);
				ontClass.addPropertyTakingMyselfAsRange(hasParent);
				ontClass.addPropertyTakingMyselfAsDomain(hasParent);
			}
		}

		for (XSComplexType complexType : fetcher.getComplexTypes()) {
			XSOntClass c = getClass(fetcher.getUri(complexType));
			if (c != null){
				XSOntObjectProperty cHasChild = loadPrefixedProperty(complexType, hasChild);
				cHasChild.newRangeFound(c);
				addDomainIfPossible(cHasChild, c.getURI());
				XSOntObjectProperty cHasParent = loadPrefixedProperty(complexType, hasParent);
				cHasParent.newRangeFound(c);
				addDomainIfPossible(cHasParent, c.getURI());
			}
		}

		for (XSElementDecl element : fetcher.getElementDecls()) {
			if (mapsToClass(element)) {
				XSOntClass c = getClass(fetcher.getUri(element));
				XSOntObjectProperty cHasChild = loadPrefixedProperty(element, hasChild);
				cHasChild.newRangeFound(c);
				addDomainIfPossible(cHasChild, c.getURI());
				XSOntObjectProperty cHasParent = loadPrefixedProperty(element, hasParent);
				cHasParent.newRangeFound(c);
				addDomainIfPossible(cHasParent, c.getURI());
			}
		}
	}

	private void loadRestrictions() {
		// Attributes
		for (XSOntClass xsClass : classes.values()) {
			XSDeclaration declaration = fetcher.getDeclaration(xsClass.getURI());
			if (fetcher.getAttributeUses(declaration) != null) {
				for (XSAttributeUse attributeUse : fetcher.getAttributeUses(declaration)) {
					XSOntAttributeRestriction restriction = new XSOntAttributeRestriction(this, xsClass, attributeUse,
							getTechnologyAdapter());
					xsClass.addToSuperClasses(restriction);
				}
			}
		}
		// Elements
		// TODO
	}

	private void loadIndividuals() {
		individuals.clear();
	}

	private void clearAllRangeAndDomain() {
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

	public boolean load() {
		if (isLoading() == true) {
			return false;
		}
		isLoading = true;
		isLoaded = false;
		schemaSet = XSOMUtils.read(originalXsdFile);
		if (schemaSet != null) {
			fetcher = new XSDeclarationsFetcher();
			fetcher.fetch(schemaSet);
			clearAllRangeAndDomain(); // TODO CG, pas sur que ce soit utile cette merde!
			loadClasses();
			loadDataProperties();
			loadObjectProperties();
			loadRestrictions();
			loadIndividuals();
			isLoaded = true;
		}
		isLoading = false;
		return isLoaded;
	}

	public boolean loadWhenUnloaded() {
		if (isLoaded() == false) {
			return load();
		}
		return false;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public boolean isLoading() {
		return isLoading;
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

	public Document toXML() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		for (XSOntIndividual individual : getRootElements()) {
			doc.appendChild(individual.toXML(doc));
		}

		return doc;
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
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Couldn't find ontology class from URI " + classURI);
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
	public List<XSOntDataProperty> getDataProperties() {
		return new ArrayList<XSOntDataProperty>(dataProperties.values());
	}

	@Override
	public List<XSOntDataProperty> getAccessibleDataProperties() {
		Map<String, XSOntDataProperty> result = new HashMap<String, XSOntDataProperty>();
		for (XSOntology o : getAllImportedOntologies()) {
			for (XSOntDataProperty c : o.getDataProperties()) {
				result.put(c.getURI(), c);
			}
		}
		return new ArrayList<XSOntDataProperty>(result.values());
	}

	@Override
	public XSOntDataProperty getDataProperty(String propertyURI) {
		return dataProperties.get(propertyURI);
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
		for (XSOntology o : getAllImportedOntologies()) {
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

	@Override
	public XSOntIndividual getIndividual(String individualURI) {
		return individuals.get(individualURI);
	}

	@Override
	public List<XSOntIndividual> getAccessibleIndividuals() {
		Map<String, XSOntIndividual> result = new HashMap<String, XSOntIndividual>();
		for (XSOntology o : getAllImportedOntologies()) {
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

	public void addIndividual(XSOntIndividual individual) throws DuplicateURIException {
		String indUri = individual.getURI();
		if ( individuals.get(indUri) != null ) {
			throw new  DuplicateURIException(indUri);
		}
		else {
			individuals.put(indUri, individual);
		}
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

	public XSOntClass createOntologyClass(String name) throws DuplicateURIException {
		throw new UnsupportedOperationException();
	}

	public XSOntClass createOntologyClass(String name, XSOntClass superClass) throws DuplicateURIException {
		throw new UnsupportedOperationException();
	}

	public XSOntDataProperty createDataProperty(String name, XSOntDataProperty superProperty, XSOntClass domain, XSDDataType dataType)
			throws DuplicateURIException {
		throw new UnsupportedOperationException();
	}

	public XSOntObjectProperty createObjectProperty(String name, XSOntObjectProperty superProperty, XSOntClass domain, XSOntClass range)
			throws DuplicateURIException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isOntology() {
		return true;
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

}
