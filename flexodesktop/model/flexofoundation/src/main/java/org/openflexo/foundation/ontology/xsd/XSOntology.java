package org.openflexo.foundation.ontology.xsd;

import java.io.File;
import java.util.ArrayList;
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
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologicDataType;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.W3URIDefinitions;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.Language;
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

public abstract class XSOntology extends AbstractXSOntObject implements FlexoOntology, XSOntologyURIDefinitions, W3URIDefinitions {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSOntology.class.getPackage()
			.getName());

	private String name;
	private final String ontologyURI;
	private final File originalXsdFile;
	private final OntologyLibrary library;
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

	public XSOntology(String ontologyURI, File xsdFile, OntologyLibrary library) {
		super();
		name = computeName(xsdFile);
		this.ontologyURI = ontologyURI;
		this.originalXsdFile = xsdFile;
		this.library = library;
	}

	private static String computeName(File xsdFile) {
		return xsdFile.getName();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		// TODO
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("The ontology name changed, renaming of the ontology URI not implemented yet");
		}
	}

	@Override
	public String getURI() {
		return getOntologyURI();
	}

	@Override
	public String getOntologyURI() {
		return ontologyURI;
	}

	@Override
	public FlexoProject getProject() {
		return getOntologyLibrary().getProject();
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

	@Override
	public OntologyLibrary getOntologyLibrary() {
		return library;
	}

	private boolean addClass(XSOntClass c) {
		if (classes.containsKey(c.getURI()) == false) {
			classes.put(c.getURI(), c);
			return true;
		}
		return false;
	}

	@Override
	public XSOntClass getThingConcept() {
		return thingClass;
	}

	private static boolean mapsToClass(XSElementDecl element) {
		if (element.getType().isComplexType()) {
			return true;
		}
		return false;
		// TODO check if there's a need to check for attribute if SimpleType.
	}

	private XSOntClass loadClass(XSDeclaration declaration) {
		String name = declaration.getName();
		String uri = fetcher.getURI(declaration);
		XSOntClass xsClass = new XSOntClass(this, name, uri);
		classes.put(uri, xsClass);
		return xsClass;
	}

	private void loadClasses() {
		// TODO if a declaration (base) type is derived, get the correct superclass
		classes.clear();
		thingClass = new XSOntClass(this, "Thing", XS_THING_URI);
		addClass(thingClass);
		for (XSComplexType complexType : fetcher.getComplexTypes()) {
			XSOntClass xsClass = loadClass(complexType);
			xsClass.addSuperClass(getThingConcept());
		}
		for (XSElementDecl element : fetcher.getElementDecls()) {
			if (mapsToClass(element)) {
				XSOntClass xsClass = loadClass(element);
				XSOntClass superClass = classes.get(fetcher.getURI(element.getType()));
				xsClass.addSuperClass(superClass);
			}
		}
		for (XSAttGroupDecl attGroup : fetcher.getAttGroupDecls()) {
			XSOntClass xsClass = loadClass(attGroup);
			xsClass.addSuperClass(getThingConcept());
		}
		for (XSModelGroupDecl modelGroup : fetcher.getModelGroupDecls()) {
			XSOntClass xsClass = loadClass(modelGroup);
			xsClass.addSuperClass(getThingConcept());
		}
	}

	public static boolean isFromW3(XSDeclaration decl) {
		return decl.getTargetNamespace().equals(W3_NAMESPACE);
	}

	private static OntologicDataType computeDataType(XSSimpleType simpleType) {
		String dataTypeURI = null;
		if (isFromW3(simpleType)) {
			dataTypeURI = W3_URI + "#" + simpleType.getName();
		} else {
			dataTypeURI = W3_URI + "#" + simpleType.getBaseType().getName();
		}
		OntologicDataType result = OntologicDataType.fromURI(dataTypeURI);
		if (result == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not map a data type: " + dataTypeURI + ", String will be used instead.");
			}
			return OntologicDataType.String;
		}
		return result;
	}

	private XSOntDataProperty loadDataProperty(XSDeclaration declaration) {
		String name = declaration.getName();
		String uri = fetcher.getURI(declaration);
		XSOntDataProperty xsDataProperty = new XSOntDataProperty(this, name, uri);
		dataProperties.put(uri, xsDataProperty);
		return xsDataProperty;
	}

	private void loadDataProperties() {
		dataProperties.clear();
		for (XSSimpleType simpleType : fetcher.getSimpleTypes()) {
			XSOntDataProperty xsDataProperty = loadDataProperty(simpleType);
			xsDataProperty.setDataType(computeDataType(simpleType));
		}
		for (XSElementDecl element : fetcher.getElementDecls()) {
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

	private void loadObjectProperties() {
		objectProperties.clear();
		XSOntObjectProperty hasElement = new XSOntObjectProperty(this, XS_HASCHILD_PROPERTY_NAME);
		objectProperties.put(hasElement.getURI(), hasElement);
		XSOntObjectProperty isPartOfElement = new XSOntObjectProperty(this, XS_HASPARENT_PROPERTY_NAME);
		objectProperties.put(isPartOfElement.getURI(), isPartOfElement);
		// TODO have properties inheriting those two for all complex types and elements
		// How to name to make sure its URI is valid?
	}

	private void loadRestrictions() {
		// Attributes
		for (XSOntClass xsClass : classes.values()) {
			XSDeclaration declaration = fetcher.getDeclaration(xsClass.getURI());
			if (fetcher.getAttributeUses(declaration) != null) {
				for (XSAttributeUse attributeUse : fetcher.getAttributeUses(declaration)) {
					XSOntAttributeRestriction restriction = new XSOntAttributeRestriction(this, attributeUse);
					xsClass.addSuperClass(restriction);
				}
			}
		}
		// Elements
		// TODO
	}

	private void loadIndividuals() {
		individuals.clear();
	}

	public boolean load() {
		// TODO Should I create a w3 ontology? possibly .owl?
		// TODO Seems I should.
		if (isLoading() == true) {
			return false;
		}
		isLoading = true;
		isLoaded = false;
		schemaSet = XSOMUtils.read(originalXsdFile);
		if (schemaSet != null) {
			fetcher = new XSDeclarationsFetcher();
			fetcher.fetch(schemaSet);
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

	@Override
	public boolean loadWhenUnloaded() {
		if (isLoaded() == false) {
			return load();
		}
		return false;
	}

	@Override
	public boolean isLoaded() {
		return isLoaded;
	}

	@Override
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
	public List<XSOntology> getAllImportedOntologies() {
		List<XSOntology> result = new ArrayList<XSOntology>(1);
		result.add(this);
		return result;
	}

	@Override
	public List<XSOntology> getImportedOntologies() {
		List<XSOntology> result = new ArrayList<XSOntology>(1);
		result.add(this);
		return result;
	}

	@Override
	public List<XSOntClass> getClasses() {
		return new ArrayList<XSOntClass>(classes.values());
	}

	@Override
	public XSOntClass getClass(String classURI) {
		XSOntClass result = classes.get(classURI);
		if (result == null) {
			for (XSOntology o : getAllImportedOntologies()) {
				if (o != this) {
					result = o.getClass(classURI);
					if (result != null) {
						return result;
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
		for (XSOntology o : getAllImportedOntologies()) {
			for (XSOntClass c : o.getClasses()) {
				result.put(c.getURI(), c);
			}
		}
		return new ArrayList<XSOntClass>(result.values());
	}

	@Override
	public List<XSOntDataProperty> getDataProperties() {
		System.out.println("Just got a call!");
		for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
			System.out.println(e.getClassName() + "." + e.getMethodName());
		}
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
	public OntologyProperty getProperty(String objectURI) {
		OntologyProperty result = getDataProperty(objectURI);
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

	@Override
	public XSOntIndividual createOntologyIndividual(String name, OntologyClass type) throws DuplicateURIException {
		String uri = getURI() + "#" + name;
		if (getOntologyObject(uri) != null) {
			throw new DuplicateURIException(uri);
		}
		if (type instanceof XSOntClass == false) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Individual '" + name + "' with type '" + type.getURI() + "' can't be created in a XSOntology");
			}
			return null;
		}
		XSOntIndividual individual = new XSOntIndividual(this, name, uri);
		individual.addType(type);
		individuals.put(individual.getURI(), individual);
		return individual;
	}

	@Override
	public OntologyObject getOntologyObject(String objectURI) {
		OntologyObject result = getClass(objectURI);
		if (result == null) {
			result = getProperty(objectURI);
		}
		if (result == null) {
			result = getIndividual(objectURI);
		}
		return result;
	}

	@Override
	public OntologyClass createOntologyClass(String name) throws DuplicateURIException {
		throw new UnsupportedOperationException();
	}

	@Override
	public OntologyDataProperty createDataProperty(String name, OntologyDataProperty superProperty, OntologyClass domain,
			OntologicDataType dataType) throws DuplicateURIException {
		throw new UnsupportedOperationException();
	}

	@Override
	public OntologyObjectProperty createObjectProperty(String name, OntologyObjectProperty superProperty, OntologyClass domain,
			OntologyClass range) throws DuplicateURIException {
		throw new UnsupportedOperationException();
	}

	@Override
	public OntologyClass createOntologyClass(String name, OntologyClass superClass) throws DuplicateURIException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object addDataPropertyStatement(OntologyDataProperty property, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object addPropertyStatement(OntologyObjectProperty property, OntologyObject object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object addPropertyStatement(OntologyProperty property, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object addPropertyStatement(OntologyProperty property, String value, Language language) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isOntology() {
		return true;
	}

	public static String findOntologyURI(File f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayableDescription() {
		return "Ontology " + getName();
	}

}
