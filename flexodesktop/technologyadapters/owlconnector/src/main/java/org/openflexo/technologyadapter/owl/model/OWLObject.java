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
package org.openflexo.technologyadapter.owl.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.dm.DuplicateMethodSignatureException;
import org.openflexo.foundation.ontology.OntologicDataType;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectConverter;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.dm.OntologyObjectStatementsChanged;
import org.openflexo.foundation.ontology.dm.URIChanged;
import org.openflexo.foundation.ontology.dm.URINameChanged;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.LocalizedString;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.StringConvertable;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.ResourceUtils;

public abstract class OWLObject<R extends OntResource> extends AbstractOWLObject implements OntologyObject, InspectableObject,
		StringConvertable<OntologyObject> {

	private static final Logger logger = Logger.getLogger(OntologyObject.class.getPackage().getName());

	private final Vector<OWLStatement> _statements;
	private final Vector<PropertyStatement> _annotationStatements;
	private final Vector<ObjectPropertyStatement> _annotationObjectsStatements;
	private final Vector<OWLStatement> _semanticStatements;

	private boolean domainsAndRangesAreUpToDate = false;
	private boolean domainsAndRangesAreRecursivelyUpToDate = false;
	private Set<OWLProperty> declaredPropertiesTakingMySelfAsRange;
	private Set<OWLProperty> declaredPropertiesTakingMySelfAsDomain;
	protected Set<OWLProperty> propertiesTakingMySelfAsRange;
	protected Set<OWLProperty> propertiesTakingMySelfAsDomain;

	private String uri;
	private String name;
	private final OWLOntology _ontology;

	private OWLObject<R> originalDefinition;

	public OWLObject(OntResource ontResource, OWLOntology ontology, OWLTechnologyAdapter adapter) {
		super(adapter);

		_ontology = ontology;
		if (ontResource != null) {
			uri = ontResource.getURI();
			if (uri != null && uri.indexOf("#") > -1) {
				name = uri.substring(uri.indexOf("#") + 1);
			} else {
				name = uri;
			}
		}

		_statements = new Vector<OWLStatement>();
		_semanticStatements = new Vector<OWLStatement>();
		_annotationStatements = new Vector<PropertyStatement>();
		_annotationObjectsStatements = new Vector<ObjectPropertyStatement>();
		propertiesTakingMySelfAsRange = new HashSet<OWLProperty>();
		propertiesTakingMySelfAsDomain = new HashSet<OWLProperty>();
		declaredPropertiesTakingMySelfAsRange = new HashSet<OWLProperty>();
		declaredPropertiesTakingMySelfAsDomain = new HashSet<OWLProperty>();
	}

	@Override
	public OntologyObjectConverter getConverter() {
		if (getOntologyLibrary() != null) {
			return getOntologyLibrary().getOntologyObjectConverter();
		}
		return null;
	}

	protected abstract void update();

	@Override
	public String getURI() {
		return uri;
	}

	@Override
	public OWLOntology getFlexoOntology() {
		return _ontology;
	}

	@Override
	public OWLOntology getOntology() {
		return getFlexoOntology();
	}

	public OWLOntologyLibrary getOntologyLibrary() {
		if (getOntology() != null) {
			return getOntology().getOntologyLibrary();
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public abstract void setName(String aName);

	protected R renameURI(String newName, R resource, Class<R> resourceClass) {
		String oldURI = getURI();
		String oldName = getName();
		String newURI;
		if (getURI().indexOf("#") > -1) {
			newURI = getURI().substring(0, getURI().indexOf("#") + 1) + newName;
		} else {
			newURI = newName;
		}
		logger.info("Rename object " + getURI() + " to " + newURI);
		R returned = ResourceUtils.renameResource(resource, newURI).as(resourceClass);
		_setOntResource(returned);
		name = newName;
		uri = newURI;
		getFlexoOntology().renameObject(this, oldURI, newURI);
		update();
		setChanged();
		notifyObservers(new NameChanged(oldName, newName));
		setChanged();
		notifyObservers(new URINameChanged(oldName, newName));
		setChanged();
		notifyObservers(new URIChanged(oldURI, newURI));
		for (EditionPatternReference ref : getEditionPatternReferences()) {
			EditionPatternInstance i = ref.getEditionPatternInstance();
			for (FlexoModelObject o : i.getActors().values()) {
				if (o.getXMLResourceData() != null && !o.getXMLResourceData().getFlexoResource().isModified()) {
					o.getXMLResourceData().setIsModified();
				}
			}
		}
		return returned;
	}

	public abstract R getOntResource();

	protected abstract void _setOntResource(R r);

	public Resource getResource() {
		return getOntResource();
	}

	@Override
	public String getDescription() {
		if (getOntResource() != null) {
			return getOntResource().getComment(FlexoLocalization.getCurrentLanguage().getTag());
		}
		return null;
	}

	@Override
	public void setDescription(String aDescription) {
		if (getOntResource() != null) {
			getOntResource().setComment(aDescription, FlexoLocalization.getCurrentLanguage().getTag());
		}
	}

	public String simpleRepresentation() {
		return getClass().getSimpleName() + ":" + getName();
	}

	public String fullQualifiedRepresentation() {
		return getURI();
	}

	public void updateOntologyStatements() {
		updateOntologyStatements(getOntResource());
	}

	protected void updateOntologyStatements(R anOntResource) {
		// TODO: optimize this (do not always recalculate)

		_statements.clear();
		_semanticStatements.clear();
		_annotationStatements.clear();
		_annotationObjectsStatements.clear();

		for (StmtIterator j = anOntResource.listProperties(); j.hasNext();) {
			Statement s = j.nextStatement();

			OWLStatement newStatement = null;

			if (!s.getSubject().equals(anOntResource)) {
				logger.warning("Inconsistant data: subject is not " + this);
			} else {
				Property predicate = s.getPredicate();
				if (predicate.getURI().equals(TYPE_URI)) {
					if (s.getObject() instanceof Resource && StringUtils.isNotEmpty(((Resource) s.getObject()).getURI())) {
						if (((Resource) s.getObject()).getURI().equals(OWL_CLASS_URI)) {
							newStatement = new IsClassStatement(this, s, getTechnologyAdapter());
						} else if (((Resource) s.getObject()).getURI().equals(OWL_OBJECT_PROPERTY_URI)) {
							newStatement = new IsObjectPropertyStatement(this, s, getTechnologyAdapter());
						} else if (((Resource) s.getObject()).getURI().equals(OWL_DATA_PROPERTY_URI)) {
							newStatement = new IsDatatypePropertyStatement(this, s, getTechnologyAdapter());
						} else {
							newStatement = new TypeStatement(this, s, getTechnologyAdapter());
						}
					} else {
						newStatement = new TypeStatement(this, s, getTechnologyAdapter());
					}
				} else if (predicate.getURI().equals(RDFS_SUB_CLASS_URI)) {
					newStatement = new SubClassStatement(this, s, getTechnologyAdapter());
				} else if (predicate.getURI().equals(RDFS_RANGE_URI)) {
					newStatement = new RangeStatement(this, s, getTechnologyAdapter());
				} else if (predicate.getURI().equals(RDFS_DOMAIN_URI)) {
					newStatement = new DomainStatement(this, s, getTechnologyAdapter());
				} else if (predicate.getURI().equals(OWL_INVERSE_OF_URI)) {
					newStatement = new InverseOfStatement(this, s, getTechnologyAdapter());
				} else if (predicate.getURI().equals(RDFS_SUB_PROPERTY_URI)) {
					newStatement = new SubPropertyStatement(this, s, getTechnologyAdapter());
				} else if (predicate.getURI().equals(OWL_EQUIVALENT_CLASS_URI)) {
					newStatement = new EquivalentClassStatement(this, s, getTechnologyAdapter());
				} else {
					OntologyObject predicateProperty = getOntology().getOntologyObject(predicate.getURI());
					if (predicateProperty instanceof OntologyObjectProperty) {
						newStatement = new ObjectPropertyStatement(this, s, getTechnologyAdapter());
					} else if (predicateProperty instanceof OntologyDataProperty) {
						newStatement = new DataPropertyStatement(this, s, getTechnologyAdapter());
					} else {
						logger.warning("Inconsistant data: unkwown property " + predicate);
					}
				}
			}

			if (newStatement != null) {
				_statements.add(newStatement);
				if (newStatement instanceof PropertyStatement && ((PropertyStatement) newStatement).isAnnotationProperty()) {
					if (((PropertyStatement) newStatement).hasLitteralValue()) {
						_annotationStatements.add((PropertyStatement) newStatement);
					} else if (newStatement instanceof ObjectPropertyStatement) {
						_annotationObjectsStatements.add((ObjectPropertyStatement) newStatement);
					}
				} else {
					_semanticStatements.add(newStatement);
				}
			}

		}

		for (OWLStatement s : _statements) {
			// System.out.println("> "+s.toString());
		}

		setChanged();
		notifyObservers(new OntologyObjectStatementsChanged(this));

	}

	public Vector<OWLStatement> getStatements() {
		return _statements;
	}

	public Vector<OWLStatement> getSemanticStatements() {
		return _semanticStatements;
	}

	public Vector<PropertyStatement> getAnnotationStatements() {
		return _annotationStatements;
	}

	public Vector<ObjectPropertyStatement> getAnnotationObjectStatements() {
		return _annotationObjectsStatements;
	}

	/**
	 * Return all statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	public Vector<PropertyStatement> getPropertyStatements(OntologyProperty property) {
		Vector<PropertyStatement> returned = new Vector<PropertyStatement>();
		for (OWLStatement statement : getStatements()) {
			if (statement instanceof PropertyStatement) {
				PropertyStatement s = (PropertyStatement) statement;
				if (s.getProperty().equalsToConcept(property)) {
					returned.add(s);
				}
			}
		}
		return returned;
	}

	/**
	 * Return all annotation statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	public Vector<DataPropertyStatement> getAnnotationStatements(OntologyDataProperty property) {
		Vector<DataPropertyStatement> returned = new Vector<DataPropertyStatement>();
		for (OWLStatement statement : getAnnotationStatements()) {
			if (statement instanceof DataPropertyStatement) {
				DataPropertyStatement s = (DataPropertyStatement) statement;
				if (s.getProperty().equalsToConcept(property)) {
					returned.add(s);
				}
			}
		}
		return returned;
	}

	/**
	 * Return all annotation object statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	public Vector<ObjectPropertyStatement> getAnnotationObjectStatements(OntologyProperty property) {
		Vector<ObjectPropertyStatement> returned = new Vector<ObjectPropertyStatement>();
		for (OWLStatement statement : getAnnotationObjectStatements()) {
			if (statement instanceof PropertyStatement) {
				ObjectPropertyStatement s = (ObjectPropertyStatement) statement;
				if (s.getProperty().equalsToConcept(property)) {
					returned.add(s);
				}
			}
		}
		return returned;
	}

	/**
	 * Return all statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	public Vector<ObjectPropertyStatement> getObjectPropertyStatements(OntologyObjectProperty property) {
		Vector<ObjectPropertyStatement> returned = new Vector<ObjectPropertyStatement>();
		for (OWLStatement statement : getStatements()) {
			if (statement instanceof ObjectPropertyStatement) {
				ObjectPropertyStatement s = (ObjectPropertyStatement) statement;
				if (s.getProperty().equalsToConcept(property)) {
					returned.add(s);
				}
			}
		}
		return returned;
	}

	/**
	 * Return all statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	public Vector<DataPropertyStatement> getDataPropertyStatements(OntologyDataProperty property) {
		Vector<DataPropertyStatement> returned = new Vector<DataPropertyStatement>();
		for (OWLStatement statement : getStatements()) {
			if (statement instanceof DataPropertyStatement) {
				DataPropertyStatement s = (DataPropertyStatement) statement;
				if (s.getProperty() == property) {
					returned.add(s);
				}
			}
		}
		return returned;
	}

	/**
	 * Return first found statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	public PropertyStatement getPropertyStatement(OntologyProperty property) {
		Vector<PropertyStatement> returned = getPropertyStatements(property);
		if (returned.size() > 0) {
			return returned.firstElement();
		}
		return null;
	}

	/**
	 * Return statement related to supplied property and value
	 * 
	 * @param property
	 * @return
	 */
	public DataPropertyStatement getDataPropertyStatement(OntologyDataProperty property, Object value) {
		Vector<DataPropertyStatement> returned = getDataPropertyStatements(property);
		for (DataPropertyStatement statement : returned) {
			if (statement.getValue().equals(value)) {
				return statement;
			}
		}
		return null;
	}

	/**
	 * Return statement related to supplied property and value
	 * 
	 * @param property
	 * @return
	 */
	public PropertyStatement getPropertyStatement(OntologyProperty property, String value) {
		Vector<PropertyStatement> returned = getPropertyStatements(property);
		for (PropertyStatement statement : returned) {
			if (statement.hasLitteralValue() && statement.getStringValue().equals(value)) {
				return statement;
			}
		}
		return null;
	}

	/**
	 * Return statement related to supplied property and value
	 * 
	 * @param property
	 * @return
	 */
	public PropertyStatement getPropertyStatement(OntologyProperty property, Object value) {
		Vector<PropertyStatement> returned = getPropertyStatements(property);
		for (PropertyStatement statement : returned) {
			if (statement.hasLitteralValue() && statement.getLiteral().getValue().equals(value)) {
				return statement;
			}
		}
		return null;
	}

	/**
	 * Return statement related to supplied property and value
	 * 
	 * @param property
	 * @return
	 */
	public ObjectPropertyStatement getPropertyStatement(OntologyObjectProperty property, OntologyObject object) {
		Vector<ObjectPropertyStatement> returned = getObjectPropertyStatements(property);
		for (ObjectPropertyStatement statement : returned) {
			if (statement.getStatementObject() == object) {
				return statement;
			}
		}
		return null;
	}

	/**
	 * Return statement related to supplied property, value and language
	 * 
	 * @param property
	 * @return
	 */
	public PropertyStatement getPropertyStatement(OntologyProperty property, String value, Language language) {
		Vector<PropertyStatement> returned = getPropertyStatements(property);
		for (PropertyStatement statement : returned) {
			if (statement.hasLitteralValue() && statement.getStringValue().equals(value)
					&& statement.getLanguage().equals(language.getTag())) {
				return statement;
			}
		}
		return null;
	}

	/**
	 * Return first found statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	// TODO: need to handle multiple statements
	public DataPropertyStatement getDataPropertyStatement(OntologyDataProperty property) {
		for (OWLStatement statement : getStatements()) {
			if (statement instanceof DataPropertyStatement && ((DataPropertyStatement) statement).getProperty() == property) {
				return (DataPropertyStatement) statement;
			}
		}
		return null;
	}

	/**
	 * Return first found statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	// TODO: need to handle multiple statements
	public ObjectPropertyStatement getObjectPropertyStatement(OntologyObjectProperty property) {
		for (OWLStatement statement : getStatements()) {
			if (statement instanceof ObjectPropertyStatement && ((ObjectPropertyStatement) statement).getProperty() == property) {
				return (ObjectPropertyStatement) statement;
			}
		}
		return null;
	}

	/**
	 * Return statement related to supplied property and referencing supplied object
	 * 
	 * @param property
	 * @return
	 */
	public ObjectPropertyStatement getObjectPropertyStatement(OntologyObjectProperty property, OntologyObject object) {
		for (OWLStatement statement : getStatements()) {
			if (statement instanceof ObjectPropertyStatement && ((ObjectPropertyStatement) statement).getProperty() == property
					&& ((ObjectPropertyStatement) statement).getStatementObject() == object) {
				return (ObjectPropertyStatement) statement;
			}
		}
		return null;
	}

	/**
	 * Return first found statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	// TODO: need to handle multiple statements
	public SubClassStatement getSubClassStatement(OntologyObject father) {
		for (OWLStatement statement : getStatements()) {
			if (statement instanceof SubClassStatement && ((SubClassStatement) statement).getParent().equals(father)) {
				return (SubClassStatement) statement;
			}
		}
		return null;
	}

	@Override
	public abstract boolean isSuperConceptOf(OntologyObject concept);

	@Override
	public boolean isSubConceptOf(OntologyObject concept) {
		return concept.isSuperConceptOf(concept);
	}

	public PropertyStatement createNewCommentAnnotation() {
		return createNewAnnotation("http://www.w3.org/2000/01/rdf-schema#comment");
	}

	public PropertyStatement createNewLabelAnnotation() {
		return createNewAnnotation("http://www.w3.org/2000/01/rdf-schema#label");
	}

	public PropertyStatement createNewSeeAlsoAnnotation() {
		return createNewAnnotation("http://www.w3.org/2000/01/rdf-schema#seeAlso", this);
	}

	public PropertyStatement createNewIsDefinedByAnnotation() {
		return createNewAnnotation("http://www.w3.org/2000/01/rdf-schema#isDefinedBy", getFlexoOntology());
	}

	public PropertyStatement createNewAnnotation(String propertyURI) {
		OWLProperty property = getOntology().getProperty(propertyURI);
		if (property != null) {
			return addPropertyStatement(property, "label", Language.ENGLISH);
		} else {
			logger.warning("Could not find property " + property);
			return null;
		}
	}

	public PropertyStatement createNewAnnotation(String propertyURI, OWLObject<?> object) {
		if (object == null) {
			logger.warning("Cannot create annotation " + propertyURI + " with null object");
			return null;
		}
		OWLProperty property = getOntology().getProperty(propertyURI);
		if (property instanceof OWLObjectProperty) {
			return addPropertyStatement((OWLObjectProperty) property, object);
		} else {
			logger.warning("Could not find property " + property);
			return null;
		}
	}

	public void deleteAnnotation(PropertyStatement annotation) throws DuplicateMethodSignatureException {
		removePropertyStatement(annotation);
	}

	public boolean isAnnotationAddable() {
		return !getIsReadOnly();
	}

	public boolean isAnnotationDeletable(PropertyStatement annotation) {
		return !getIsReadOnly();
	}

	/**
	 * Return the value defined for supplied property, asserting that current individual defines one and only one assertion for this
	 * property.<br>
	 * <ul>
	 * <li>If many assertions for this properties are defined for this individual, then the first assertion is used<br>
	 * Special case: if supplied property is an annotation property defined on a literal (datatype property) then the returned value will
	 * match the current language as defined in FlexoLocalization.</li>
	 * <li>If no assertion is defined for this property, then the result will be null</li>
	 * </ul>
	 * 
	 * @param property
	 * @return
	 */
	@Override
	public Object getPropertyValue(OntologyProperty property) {
		if (property == null) {
			logger.warning("getPropertyValue() called for null property");
			return null;
		}
		if (property.isAnnotationProperty() && property instanceof OWLDataProperty
				&& getAnnotationStatements((OWLDataProperty) property).size() > 1) {
			return getAnnotationValue((OWLDataProperty) property, FlexoLocalization.getCurrentLanguage());
		}

		// Special case for label annotation
		if (property.equalsToConcept(property.getFlexoOntology().getProperty(RDFS_LABEL_URI)) && getPropertyStatement(property) == null) {
			// If label is requested and no label annotation are set, return uriName
			return getName();
		}

		PropertyStatement s = getPropertyStatement(property);
		if (s != null) {
			if (s.hasLitteralValue()) {
				return s.getLiteral().getValue();
			} else if (s instanceof ObjectPropertyStatement) {
				return ((ObjectPropertyStatement) s).getStatementObject();
			}
		}
		return null;
	}

	/**
	 * Sets the value defined for supplied property, asserting that current individual defines one and only one assertion for this property.<br>
	 * 
	 * @param property
	 * @param newValue
	 */
	@Override
	public void setPropertyValue(OntologyProperty property, Object newValue) {
		if (property instanceof OWLProperty) {
			PropertyStatement s = getPropertyStatement(property);
			if (s != null) {
				if (s.hasLitteralValue() && newValue instanceof String) {
					s.setStringValue((String) newValue);
					return;
				} else if (s instanceof ObjectPropertyStatement && newValue instanceof OWLObject) {
					((ObjectPropertyStatement) s).setStatementObject((OWLObject<?>) newValue);
					return;
				}
			} else {
				if (newValue instanceof String) {
					getOntResource().addProperty(((OWLProperty) property).getOntProperty(), (String) newValue);
					updateOntologyStatements();
				} else if (newValue instanceof OWLObject) {
					getOntResource().addProperty(((OWLProperty) property).getOntProperty(), ((OWLObject<?>) newValue).getOntResource());
					updateOntologyStatements();
				}
			}
		}
		logger.warning("Property " + property + " is not a OWLProperty");
	}

	/**
	 * Return value of specified property, asserting this property is an annotation property matching a literal value
	 * 
	 * @param property
	 * @param language
	 * @return
	 */
	@Override
	public Object getAnnotationValue(OntologyDataProperty property, Language language) {
		List<DataPropertyStatement> literalAnnotations = getAnnotationStatements(property);
		for (DataPropertyStatement annotation : literalAnnotations) {
			if (annotation != null && annotation.getLanguage() == language) {
				if (annotation.hasLitteralValue()) {
					return annotation.getLiteral().getValue();
				}
			}
		}
		return null;
	}

	@Override
	public void setAnnotationValue(Object value, OntologyDataProperty property, Language language) {
		// TODO: implement this
		logger.warning("setAnnotationValue not implemented !");
	}

	/**
	 * Return value of specified property, asserting this property is an annotation property matching an object value
	 * 
	 * @param property
	 * @param language
	 * @return
	 */
	@Override
	public Object getAnnotationObjectValue(OntologyObjectProperty property) {
		List<ObjectPropertyStatement> annotations = getAnnotationObjectStatements(property);
		for (ObjectPropertyStatement annotation : annotations) {
			OntologyObject returned = annotation.getStatementObject();
			if (returned != null) {
				return returned;
			}
		}
		return null;
	}

	/**
	 * Sets value of specified property, asserting this property is an annotation property matching an object value
	 * 
	 * @param value
	 * @param property
	 * @param language
	 */
	@Override
	public void setAnnotationObjectValue(Object value, OntologyObjectProperty property, Language language) {
		// TODO: implement this
		logger.warning("setAnnotationObjectValue not implemented !");
	}

	/**
	 * Append object property statement for specified property and object
	 * 
	 * @param property
	 * @param object
	 * @return an object representing the added statement
	 */
	@Override
	public ObjectPropertyStatement addPropertyStatement(OntologyObjectProperty property, OntologyObject object) {
		if (property instanceof OWLObjectProperty && object instanceof OWLObject) {
			// System.out.println("Subject: "+this+" resource="+getOntResource());
			// System.out.println("Predicate: "+property+" resource="+property.getOntProperty());
			// System.out.println("Object: "+object+" resource="+object.getOntResource());

			getOntResource().addProperty(((OWLProperty) property).getOntProperty(), ((OWLObject) object).getResource());
			updateOntologyStatements();
			getOntology().setChanged();
			return getPropertyStatement(property, object);
		}
		logger.warning("Property " + property + " is not a OWLObjectProperty");
		return null;
	}

	/**
	 * Append property statement for specified property and object
	 * 
	 * @param property
	 * @param value
	 * @return an object representing the added statement
	 */
	@Override
	public PropertyStatement addPropertyStatement(OntologyProperty property, Object value) {
		if (property instanceof OWLProperty) {
			if (value instanceof String) {
				getOntResource().addProperty(((OWLProperty) property).getOntProperty(), (String) value);
				updateOntologyStatements();
				getOntology().setChanged();
				return getPropertyStatement((OWLProperty) property, (String) value);
			} else {
				getOntResource().addLiteral(((OWLProperty) property).getOntProperty(), value);
				updateOntologyStatements();
				getOntology().setChanged();
				return getPropertyStatement(property, value);
			}
		}
		logger.warning("Property " + property + " is not a OWLProperty");
		return null;
	}

	/**
	 * Append property statement for specified property, object and language
	 * 
	 * @param property
	 * @param object
	 * @return an object representing the added statement
	 */
	@Override
	public PropertyStatement addPropertyStatement(OntologyProperty property, String value, Language language) {
		if (property instanceof OWLProperty) {
			// System.out.println("****** Add statement for property "+property.getName()+" value="+value+" language="+language);
			getOntResource().addProperty(((OWLProperty) property).getOntProperty(), value, language.getTag());
			updateOntologyStatements();
			getOntology().setChanged();
			return getPropertyStatement(property, value, language);
		}
		logger.warning("Property " + property + " is not a OWLProperty");
		return null;
	}

	/**
	 * Append property statement for specified property and value
	 * 
	 * @param property
	 * @param object
	 * @return an object representing the added statement
	 */
	@Override
	public DataPropertyStatement addDataPropertyStatement(OntologyDataProperty property, Object value) {
		if (property instanceof OWLDataProperty) {
			getOntResource().addLiteral(((OWLProperty) property).getOntProperty(), value);
			updateOntologyStatements();
			getOntology().setChanged();
			return getDataPropertyStatement(property, value);
		}
		logger.warning("Property " + property + " is not a OWLDataProperty");
		return null;
	}

	public void removePropertyStatement(PropertyStatement statement) {
		getFlexoOntology().getOntModel().remove(statement.getStatement());
		updateOntologyStatements();
		getOntology().setChanged();
	}

	public PropertyStatement addLiteral(OntologyProperty property, Object value) {
		if (property instanceof OWLProperty) {
			if (value instanceof String) {
				getOntResource().addProperty(((OWLProperty) property).getOntProperty(), (String) value);
			} else if (value instanceof LocalizedString) {
				if (!StringUtils.isEmpty(((LocalizedString) value).string)) {
					getOntResource().addProperty(((OWLProperty) property).getOntProperty(), ((LocalizedString) value).string,
							((LocalizedString) value).language.getTag());
				}
			} else if (value instanceof Double) {
				getOntResource().addLiteral(((OWLProperty) property).getOntProperty(), ((Double) value).doubleValue());
			} else if (value instanceof Float) {
				getOntResource().addLiteral(((OWLProperty) property).getOntProperty(), ((Float) value).floatValue());
			} else if (value instanceof Long) {
				getOntResource().addLiteral(((OWLProperty) property).getOntProperty(), ((Long) value).longValue());
			} else if (value instanceof Integer) {
				getOntResource().addLiteral(((OWLProperty) property).getOntProperty(), ((Integer) value).longValue());
			} else if (value instanceof Short) {
				getOntResource().addLiteral(((OWLProperty) property).getOntProperty(), ((Short) value).longValue());
			} else if (value instanceof Boolean) {
				getOntResource().addLiteral(((OWLProperty) property).getOntProperty(), ((Boolean) value).booleanValue());
			} else if (value != null) {
				logger.warning("Unexpected " + value + " of " + value.getClass());
			} else {
				// If value is null, just ignore
			}
			getOntology().setChanged();
			return getPropertyStatement(property);
		}
		return null;
	}

	@Override
	public boolean getIsReadOnly() {
		return getFlexoOntology().getIsReadOnly();
	}

	protected void updateDomainsAndRanges() {
		domainsAndRangesAreUpToDate = false;
		domainsAndRangesAreRecursivelyUpToDate = false;
	}

	public Set<OWLProperty> getDeclaredPropertiesTakingMySelfAsRange() {
		if (!domainsAndRangesAreUpToDate) {
			searchRangeAndDomains();
		}
		return declaredPropertiesTakingMySelfAsRange;
	}

	public Set<OWLProperty> getDeclaredPropertiesTakingMySelfAsDomain() {
		if (!domainsAndRangesAreUpToDate) {
			searchRangeAndDomains();
		}
		return declaredPropertiesTakingMySelfAsDomain;
	}

	@Override
	public Set<OWLProperty> getPropertiesTakingMySelfAsRange() {
		getDeclaredPropertiesTakingMySelfAsRange(); // Required in some cases: TODO: investigate this
		if (!domainsAndRangesAreRecursivelyUpToDate) {
			recursivelySearchRangeAndDomains();
		}
		return propertiesTakingMySelfAsRange;
	}

	@Override
	public Set<OWLProperty> getPropertiesTakingMySelfAsDomain() {
		getDeclaredPropertiesTakingMySelfAsDomain(); // Required in some cases: TODO: investigate this
		if (!domainsAndRangesAreRecursivelyUpToDate) {
			recursivelySearchRangeAndDomains();
		}
		return propertiesTakingMySelfAsDomain;
	}

	// TODO implement a nice and documented API here !
	public Vector<OWLDataProperty> getDataPropertiesTakingMySelfAsDomain(Object range) {
		Vector<OWLDataProperty> returned = new Vector<OWLDataProperty>();
		Vector<OWLProperty> allProperties = getPropertiesTakingMyselfAsDomain(true, false, false, false, null, null, getOntology());
		for (OWLProperty p : allProperties) {
			returned.add((OWLDataProperty) p);
		}
		return returned;
	}

	public Vector<OWLObjectProperty> getObjectPropertiesTakingMySelfAsDomain(OWLObject<?> range) {
		Vector<OWLObjectProperty> returned = new Vector<OWLObjectProperty>();
		Vector<OWLProperty> allProperties = getPropertiesTakingMyselfAsDomain(false, true, false, false, null, null, getOntology());
		for (OWLProperty p : allProperties) {
			returned.add((OWLObjectProperty) p);
		}
		return returned;
	}

	private Vector<OWLProperty> getPropertiesTakingMyselfAsDomain(boolean includeDataProperties, boolean includeObjectProperties,
			boolean includeAnnotationProperties, boolean includeBaseOntologies, OWLObject<?> range, OntologicDataType dataType,
			OWLOntology... ontologies) {
		Vector<OWLProperty> allProperties = new Vector(getPropertiesTakingMySelfAsDomain());
		Vector<OWLProperty> returnedProperties = new Vector<OWLProperty>();
		for (OWLProperty p : allProperties) {
			boolean takeIt = includeDataProperties && p instanceof OWLDataProperty || includeObjectProperties
					&& p instanceof OWLObjectProperty || includeAnnotationProperties && p.isAnnotationProperty();
			if (range != null && p instanceof OWLObjectProperty && !((OWLObjectProperty) p).getRange().isSuperConceptOf(range)) {
				takeIt = false;
			}
			if (dataType != null && p instanceof OWLDataProperty && ((OWLDataProperty) p).getDataType() != dataType) {
				takeIt = false;
			}
			OWLOntology containerOntology = p.getOntology();
			if (containerOntology == p.getOntologyLibrary().getOWLOntology() && !includeBaseOntologies) {
				takeIt = false;
			}
			if (containerOntology == p.getOntologyLibrary().getRDFOntology() && !includeBaseOntologies) {
				takeIt = false;
			}
			if (containerOntology == p.getOntologyLibrary().getRDFSOntology() && !includeBaseOntologies) {
				takeIt = false;
			}
			if (ontologies != null) {
				boolean containedInGivenOntologies = false;
				for (OWLOntology o : ontologies) {
					if (containerOntology == o) {
						containedInGivenOntologies = true;
					}
				}
				if (!containedInGivenOntologies) {
					takeIt = false;
				}
			}
			if (takeIt) {
				returnedProperties.add(p);
			}
		}
		return returnedProperties;
	}

	private void searchRangeAndDomains() {
		declaredPropertiesTakingMySelfAsRange.clear();
		declaredPropertiesTakingMySelfAsDomain.clear();
		if (redefinesOriginalDefinition()) {
			declaredPropertiesTakingMySelfAsRange.addAll(getOriginalDefinition().declaredPropertiesTakingMySelfAsRange);
			declaredPropertiesTakingMySelfAsDomain.addAll(getOriginalDefinition().declaredPropertiesTakingMySelfAsDomain);
		}

		Vector<OWLOntology> alreadyDone = new Vector<OWLOntology>();
		for (OWLOntology ontology : getOntology().getAllImportedOntologies()) {
			searchRangeAndDomains(declaredPropertiesTakingMySelfAsRange, declaredPropertiesTakingMySelfAsDomain, ontology, alreadyDone);
		}
		domainsAndRangesAreUpToDate = true;
	}

	protected void recursivelySearchRangeAndDomains() {
		propertiesTakingMySelfAsRange.clear();
		propertiesTakingMySelfAsDomain.clear();
		propertiesTakingMySelfAsRange.addAll(getDeclaredPropertiesTakingMySelfAsRange());
		propertiesTakingMySelfAsDomain.addAll(getDeclaredPropertiesTakingMySelfAsDomain());
		if (redefinesOriginalDefinition()) {
			propertiesTakingMySelfAsRange.addAll(getOriginalDefinition().getPropertiesTakingMySelfAsRange());
			propertiesTakingMySelfAsDomain.addAll(getOriginalDefinition().getPropertiesTakingMySelfAsDomain());
		}
		domainsAndRangesAreRecursivelyUpToDate = true;
	}

	private void searchRangeAndDomains(Set<OWLProperty> rangeProperties, Set<OWLProperty> domainProperties, OWLOntology ontology,
			Vector<OWLOntology> alreadyDone) {
		if (alreadyDone.contains(ontology)) {
			return;
		}
		alreadyDone.add(ontology);
		for (OWLProperty p : ontology.getObjectProperties()) {
			for (OWLObject<?> o : p.getRangeList()) {
				if (o == this) {
					rangeProperties.add(p);
				}
			}
			for (OWLObject<?> o : p.getDomainList()) {
				if (o == this) {
					domainProperties.add(p);
				}
			}
			/* if (p.getRange() != null && p.getRange() == this) {
				rangeProperties.add(p);
			}
			if (p.getDomain() != null && p.getDomain() == this) {
				domainProperties.add(p);
			}*/
		}
		for (OWLProperty p : ontology.getDataProperties()) {
			for (OWLObject<?> o : p.getRangeList()) {
				if (o == this) {
					rangeProperties.add(p);
				}
			}
			for (OWLObject<?> o : p.getDomainList()) {
				if (o == this) {
					domainProperties.add(p);
				}
			}
			/*if (p.getRange() != null && p.getRange() == this) {
				rangeProperties.add(p);
			}
			if (p.getDomain() != null && p.getDomain() == this) {
				domainProperties.add(p);
			}*/
		}

		// TODO in 1.5: Manage this with inheritance
		if (this instanceof OWLClass) {
			for (OWLClass superClass : ((OWLClass) this).getSuperClasses()) {
				if (superClass instanceof OntologyRestrictionClass) {
					OWLProperty p = (OWLProperty) ((OntologyRestrictionClass) superClass).getProperty();
					domainProperties.add(p);
				}
			}
		}

		for (OWLOntology o : ontology.getImportedOntologies()) {
			searchRangeAndDomains(rangeProperties, domainProperties, o, alreadyDone);
		}
	}

	@Override
	public Vector<EditionPatternReference> getEditionPatternReferences() {
		if (getProject() != null) {
			getProject()._retrievePendingEditionPatternReferences(this);
		}
		return super.getEditionPatternReferences();
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.ontology.IOntologyObject#getHTMLDescription()
	 */
	@Override
	public String getHTMLDescription() {
		return getDisplayableDescription();
	}

	public OWLObject<R> getOriginalDefinition() {
		return originalDefinition;
	}

	public void setOriginalDefinition(OWLObject<R> originalDefinition) {
		this.originalDefinition = originalDefinition;
		logger.info("*** " + getOntology() + " Declare object " + this + " as a redefinition of object property initially asserted in "
				+ originalDefinition.getOntology());
	}

	public boolean redefinesOriginalDefinition() {
		return originalDefinition != null;
	}

	/**
	 * This equals has a particular semantics in the way that it returns true only and only if compared objects are representing same
	 * concept regarding URI. This does not guarantee that both objects will respond the same way to some methods.<br>
	 * This method returns true if and only if objects are same, or if one of both object redefine the other one (with eventual many levels)
	 * 
	 * @param o
	 * @return
	 */
	@Override
	public boolean equalsToConcept(OntologyObject o) {
		if (o == null) {
			return false;
		}
		return StringUtils.isNotEmpty(getURI()) && getURI().equals(o.getURI());
	}
}
