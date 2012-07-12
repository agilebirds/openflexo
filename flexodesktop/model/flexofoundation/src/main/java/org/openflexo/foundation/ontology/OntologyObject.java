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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.dm.DuplicateMethodSignatureException;
import org.openflexo.foundation.ontology.dm.OntologyObjectStatementsChanged;
import org.openflexo.foundation.ontology.dm.URIChanged;
import org.openflexo.foundation.ontology.dm.URINameChanged;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.LocalizedString;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder.Converter;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.ResourceUtils;

public abstract class OntologyObject<R extends OntResource> extends AbstractOntologyObject implements InspectableObject,
		StringConvertable<OntologyObject> {

	private static final Logger logger = Logger.getLogger(OntologyObject.class.getPackage().getName());

	public static class OntologyObjectConverter extends Converter<OntologyObject> {
		private FlexoOntology _ontology;

		public OntologyObjectConverter(FlexoOntology ontology) {
			super(OntologyObject.class);
			_ontology = ontology;
		}

		@Override
		public OntologyObject convertFromString(String value) {
			return _ontology.getOntologyObject(value);
		}

		@Override
		public String convertToString(OntologyObject value) {
			return value.getURI();
		};
	}

	private final Vector<OntologyStatement> _statements;
	private final Vector<PropertyStatement> _annotationStatements;
	private final Vector<ObjectPropertyStatement> _annotationObjectsStatements;
	private final Vector<OntologyStatement> _semanticStatements;

	private boolean domainsAndRangesAreUpToDate = false;
	private boolean domainsAndRangesAreRecursivelyUpToDate = false;
	private Set<OntologyProperty> declaredPropertiesTakingMySelfAsRange;
	private Set<OntologyProperty> declaredPropertiesTakingMySelfAsDomain;
	protected Set<OntologyProperty> propertiesTakingMySelfAsRange;
	protected Set<OntologyProperty> propertiesTakingMySelfAsDomain;

	private String uri;
	private String name;
	private final FlexoOntology _ontology;

	private OntologyObject<R> originalDefinition;

	public OntologyObject(OntResource ontResource, FlexoOntology ontology) {
		super();

		_ontology = ontology;
		if (ontResource != null) {
			uri = ontResource.getURI();
			if (uri != null && uri.indexOf("#") > -1) {
				name = uri.substring(uri.indexOf("#") + 1);
			} else {
				name = uri;
			}
		}

		_statements = new Vector<OntologyStatement>();
		_semanticStatements = new Vector<OntologyStatement>();
		_annotationStatements = new Vector<PropertyStatement>();
		_annotationObjectsStatements = new Vector<ObjectPropertyStatement>();
		propertiesTakingMySelfAsRange = new HashSet<OntologyProperty>();
		propertiesTakingMySelfAsDomain = new HashSet<OntologyProperty>();
		declaredPropertiesTakingMySelfAsRange = new HashSet<OntologyProperty>();
		declaredPropertiesTakingMySelfAsDomain = new HashSet<OntologyProperty>();
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
	public FlexoOntology getFlexoOntology() {
		return _ontology;
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
		R returned = (ResourceUtils.renameResource(resource, newURI).as(resourceClass));
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

			OntologyStatement newStatement = null;

			if (!s.getSubject().equals(anOntResource)) {
				logger.warning("Inconsistant data: subject is not " + this);
			} else {
				Property predicate = s.getPredicate();
				if (predicate.getURI().equals(TYPE_URI)) {
					if (s.getObject() instanceof Resource && StringUtils.isNotEmpty(((Resource) s.getObject()).getURI())) {
						if (((Resource) s.getObject()).getURI().equals(OWL_CLASS_URI)) {
							newStatement = new IsClassStatement(this, s);
						} else if (((Resource) s.getObject()).getURI().equals(OWL_OBJECT_PROPERTY_URI)) {
							newStatement = new IsObjectPropertyStatement(this, s);
						} else if (((Resource) s.getObject()).getURI().equals(OWL_DATA_PROPERTY_URI)) {
							newStatement = new IsDatatypePropertyStatement(this, s);
						} else {
							newStatement = new TypeStatement(this, s);
						}
					} else {
						newStatement = new TypeStatement(this, s);
					}
				} else if (predicate.getURI().equals(RDFS_SUB_CLASS_URI)) {
					newStatement = new SubClassStatement(this, s);
				} else if (predicate.getURI().equals(RDFS_RANGE_URI)) {
					newStatement = new RangeStatement(this, s);
				} else if (predicate.getURI().equals(RDFS_DOMAIN_URI)) {
					newStatement = new DomainStatement(this, s);
				} else if (predicate.getURI().equals(OWL_INVERSE_OF_URI)) {
					newStatement = new InverseOfStatement(this, s);
				} else if (predicate.getURI().equals(RDFS_SUB_PROPERTY_URI)) {
					newStatement = new SubPropertyStatement(this, s);
				} else if (predicate.getURI().equals(OWL_EQUIVALENT_CLASS_URI)) {
					newStatement = new EquivalentClassStatement(this, s);
				} else {
					OntologyObject predicateProperty = getOntology().getOntologyObject(predicate.getURI());
					if (predicateProperty instanceof OntologyObjectProperty) {
						newStatement = new ObjectPropertyStatement(this, s);
					} else if (predicateProperty instanceof OntologyDataProperty) {
						newStatement = new DataPropertyStatement(this, s);
					} else {
						logger.warning("Inconsistant data: unkwown property " + predicate);
					}
				}
			}

			if (newStatement != null) {
				_statements.add(newStatement);
				if ((newStatement instanceof PropertyStatement) && ((PropertyStatement) newStatement).isAnnotationProperty()) {
					if (((PropertyStatement) newStatement).hasLitteralValue()) {
						_annotationStatements.add(((PropertyStatement) newStatement));
					} else if (newStatement instanceof ObjectPropertyStatement) {
						_annotationObjectsStatements.add(((ObjectPropertyStatement) newStatement));
					}
				} else {
					_semanticStatements.add(newStatement);
				}
			}

		}

		for (OntologyStatement s : _statements) {
			// System.out.println("> "+s.toString());
		}

		setChanged();
		notifyObservers(new OntologyObjectStatementsChanged(this));

	}

	public Vector<OntologyStatement> getStatements() {
		return _statements;
	}

	public Vector<OntologyStatement> getSemanticStatements() {
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
		for (OntologyStatement statement : getStatements()) {
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
		for (OntologyStatement statement : getAnnotationStatements()) {
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
		for (OntologyStatement statement : getAnnotationObjectStatements()) {
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
		for (OntologyStatement statement : getStatements()) {
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
		for (OntologyStatement statement : getStatements()) {
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
		for (OntologyStatement statement : getStatements()) {
			if ((statement instanceof DataPropertyStatement) && (((DataPropertyStatement) statement).getProperty() == property)) {
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
		for (OntologyStatement statement : getStatements()) {
			if ((statement instanceof ObjectPropertyStatement) && (((ObjectPropertyStatement) statement).getProperty() == property)) {
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
		for (OntologyStatement statement : getStatements()) {
			if ((statement instanceof SubClassStatement) && ((SubClassStatement) statement).getParent().equals(father)) {
				return (SubClassStatement) statement;
			}
		}
		return null;
	}

	public abstract boolean isSuperConceptOf(OntologyObject concept);

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
		OntologyProperty property = getOntology().getProperty(propertyURI);
		if (property != null) {
			return addPropertyStatement(property, "label", Language.ENGLISH);
		} else {
			logger.warning("Could not find property " + property);
			return null;
		}
	}

	public PropertyStatement createNewAnnotation(String propertyURI, OntologyObject object) {
		if (object == null) {
			logger.warning("Cannot create annotation " + propertyURI + " with null object");
			return null;
		}
		OntologyProperty property = getOntology().getProperty(propertyURI);
		if (property instanceof OntologyObjectProperty) {
			return addPropertyStatement((OntologyObjectProperty) property, object);
		} else {
			logger.warning("Could not find property " + property);
			return null;
		}
	}

	public void deleteAnnotation(PropertyStatement annotation) throws DuplicateMethodSignatureException {
		removePropertyStatement(annotation);
	}

	public boolean isAnnotationAddable() {
		return (!getIsReadOnly());
	}

	public boolean isAnnotationDeletable(PropertyStatement annotation) {
		return (!getIsReadOnly());
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
	public Object getPropertyValue(OntologyProperty property) {
		if (property == null) {
			logger.warning("getPropertyValue() called for null property");
			return null;
		}
		if (property.isAnnotationProperty() && property instanceof OntologyDataProperty
				&& getAnnotationStatements((OntologyDataProperty) property).size() > 1) {
			return getAnnotationValue((OntologyDataProperty) property, FlexoLocalization.getCurrentLanguage());
		}

		// Special case for label annotation
		if (property.equalsToConcept(property.getOntology().getProperty(RDFS_LABEL_URI)) && getPropertyStatement(property) == null) {
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

	public void setPropertyValue(OntologyProperty property, Object newValue) {
		PropertyStatement s = getPropertyStatement(property);
		if (s != null) {
			if (s.hasLitteralValue() && (newValue instanceof String)) {
				s.setStringValue((String) newValue);
				return;
			} else if ((s instanceof ObjectPropertyStatement) && (newValue instanceof OntologyObject)) {
				((ObjectPropertyStatement) s).setStatementObject((OntologyObject) newValue);
				return;
			}
		} else {
			if (newValue instanceof String) {
				getOntResource().addProperty(property.getOntProperty(), (String) newValue);
				updateOntologyStatements();
			} else if (newValue instanceof OntologyObject) {
				getOntResource().addProperty(property.getOntProperty(), ((OntologyObject) newValue).getOntResource());
				updateOntologyStatements();
			}
		}
	}

	/**
	 * Return value of specified property, asserting this property is an annotation property matching a literal value
	 * 
	 * @param property
	 * @param language
	 * @return
	 */
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
	public Object getAnnotationObjectValue(OntologyObjectProperty property) {
		List<ObjectPropertyStatement> annotations = getAnnotationObjectStatements(property);
		for (ObjectPropertyStatement annotation : annotations) {
			OntologyObject returned = annotation.getStatementObject();
			if (returned != null)
				return returned;
		}
		return null;
	}

	public void setAnnotationObjectValue(Object value, OntologyObjectProperty property, Language language) {
		// TODO: implement this
		logger.warning("setAnnotationObjectValue not implemented !");
	}

	public ObjectPropertyStatement addPropertyStatement(OntologyObjectProperty property, OntologyObject object) {
		// System.out.println("Subject: "+this+" resource="+getOntResource());
		// System.out.println("Predicate: "+property+" resource="+property.getOntProperty());
		// System.out.println("Object: "+object+" resource="+object.getOntResource());

		getOntResource().addProperty(property.getOntProperty(), object.getResource());
		updateOntologyStatements();
		getOntology().setChanged();
		return getPropertyStatement(property, object);
	}

	public PropertyStatement addPropertyStatement(OntologyProperty property, String value) {
		getOntResource().addProperty(property.getOntProperty(), value);
		updateOntologyStatements();
		getOntology().setChanged();
		return getPropertyStatement(property, value);
	}

	public PropertyStatement addPropertyStatement(OntologyProperty property, String value, Language language) {
		// System.out.println("****** Add statement for property "+property.getName()+" value="+value+" language="+language);
		getOntResource().addProperty(property.getOntProperty(), value, language.getTag());
		updateOntologyStatements();
		getOntology().setChanged();
		return getPropertyStatement(property, value, language);
	}

	public DataPropertyStatement addDataPropertyStatement(OntologyDataProperty property, Object value) {
		getOntResource().addLiteral(property.getOntProperty(), value);
		updateOntologyStatements();
		getOntology().setChanged();
		return getDataPropertyStatement(property, value);
	}

	public void removePropertyStatement(PropertyStatement statement) {
		getFlexoOntology().getOntModel().remove(statement.getStatement());
		updateOntologyStatements();
		getOntology().setChanged();
	}

	public PropertyStatement addLiteral(OntologyProperty property, Object value) {
		if (value instanceof String) {
			getOntResource().addProperty(property.getOntProperty(), (String) value);
		} else if (value instanceof LocalizedString) {
			if (!StringUtils.isEmpty(((LocalizedString) value).string)) {
				getOntResource().addProperty(property.getOntProperty(), ((LocalizedString) value).string,
						((LocalizedString) value).language.getTag());
			}
		} else if (value instanceof Double) {
			getOntResource().addLiteral(property.getOntProperty(), ((Double) value).doubleValue());
		} else if (value instanceof Float) {
			getOntResource().addLiteral(property.getOntProperty(), ((Float) value).floatValue());
		} else if (value instanceof Long) {
			getOntResource().addLiteral(property.getOntProperty(), ((Long) value).longValue());
		} else if (value instanceof Integer) {
			getOntResource().addLiteral(property.getOntProperty(), ((Integer) value).longValue());
		} else if (value instanceof Short) {
			getOntResource().addLiteral(property.getOntProperty(), ((Short) value).longValue());
		} else if (value instanceof Boolean) {
			getOntResource().addLiteral(property.getOntProperty(), ((Boolean) value).booleanValue());
		} else if (value != null) {
			logger.warning("Unexpected " + value + " of " + value.getClass());
		} else {
			// If value is null, just ignore
		}
		getOntology().setChanged();
		return getPropertyStatement(property);
	}

	public boolean getIsReadOnly() {
		return (getFlexoOntology().getIsReadOnly());
	}

	public boolean isOntology() {
		return false;
	}

	public boolean isOntologyClass() {
		return false;
	}

	public boolean isOntologyIndividual() {
		return false;
	}

	public boolean isOntologyObjectProperty() {
		return false;
	}

	public boolean isOntologyDataProperty() {
		return false;
	}

	protected void updateDomainsAndRanges() {
		domainsAndRangesAreUpToDate = false;
		domainsAndRangesAreRecursivelyUpToDate = false;
	}

	public Set<OntologyProperty> getDeclaredPropertiesTakingMySelfAsRange() {
		if (!domainsAndRangesAreUpToDate) {
			searchRangeAndDomains();
		}
		return declaredPropertiesTakingMySelfAsRange;
	}

	public Set<OntologyProperty> getDeclaredPropertiesTakingMySelfAsDomain() {
		if (!domainsAndRangesAreUpToDate) {
			searchRangeAndDomains();
		}
		return declaredPropertiesTakingMySelfAsDomain;
	}

	public Set<OntologyProperty> getPropertiesTakingMySelfAsRange() {
		getDeclaredPropertiesTakingMySelfAsRange(); // Required in some cases: TODO: investigate this
		if (!domainsAndRangesAreRecursivelyUpToDate) {
			recursivelySearchRangeAndDomains();
		}
		return propertiesTakingMySelfAsRange;
	}

	public Set<OntologyProperty> getPropertiesTakingMySelfAsDomain() {
		getDeclaredPropertiesTakingMySelfAsDomain(); // Required in some cases: TODO: investigate this
		if (!domainsAndRangesAreRecursivelyUpToDate) {
			recursivelySearchRangeAndDomains();
		}
		return propertiesTakingMySelfAsDomain;
	}

	// TODO implement a nice and documented API here !
	public Vector<OntologyDataProperty> getDataPropertiesTakingMySelfAsDomain(Object range) {
		Vector<OntologyDataProperty> returned = new Vector<OntologyDataProperty>();
		Vector<OntologyProperty> allProperties = getPropertiesTakingMyselfAsDomain(true, false, false, false, null, null, getOntology());
		for (OntologyProperty p : allProperties) {
			returned.add((OntologyDataProperty) p);
		}
		return returned;
	}

	public Vector<OntologyObjectProperty> getObjectPropertiesTakingMySelfAsDomain(OntologyObject<?> range) {
		Vector<OntologyObjectProperty> returned = new Vector<OntologyObjectProperty>();
		Vector<OntologyProperty> allProperties = getPropertiesTakingMyselfAsDomain(false, true, false, false, null, null, getOntology());
		for (OntologyProperty p : allProperties) {
			returned.add((OntologyObjectProperty) p);
		}
		return returned;
	}

	public Vector<OntologyProperty> getPropertiesTakingMySelfAsDomain(OntologyObject<?> range) {
		return getPropertiesTakingMyselfAsDomain(true, true, false, false, null, null, getOntology());
	}

	private Vector<OntologyProperty> getPropertiesTakingMyselfAsDomain(boolean includeDataProperties, boolean includeObjectProperties,
			boolean includeAnnotationProperties, boolean includeBaseOntologies, OntologyObject<?> range, OntologicDataType dataType,
			FlexoOntology... ontologies) {
		Vector<OntologyProperty> allProperties = new Vector(getPropertiesTakingMySelfAsDomain());
		Vector<OntologyProperty> returnedProperties = new Vector<OntologyProperty>();
		for (OntologyProperty p : allProperties) {
			boolean takeIt = (includeDataProperties && p instanceof OntologyDataProperty)
					|| (includeObjectProperties && p instanceof OntologyObjectProperty)
					|| (includeAnnotationProperties && p.isAnnotationProperty());
			if (range != null && (p instanceof OntologyObjectProperty) && !((OntologyObjectProperty) p).getRange().isSuperConceptOf(range)) {
				takeIt = false;
			}
			if (dataType != null && (p instanceof OntologyDataProperty) && ((OntologyDataProperty) p).getDataType() != dataType) {
				takeIt = false;
			}
			FlexoOntology containerOntology = p.getOntology();
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
				for (FlexoOntology o : ontologies) {
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

		Vector<FlexoOntology> alreadyDone = new Vector<FlexoOntology>();
		for (FlexoOntology ontology : getOntology().getAllImportedOntologies()) {
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

	private void searchRangeAndDomains(Set<OntologyProperty> rangeProperties, Set<OntologyProperty> domainProperties,
			FlexoOntology ontology, Vector<FlexoOntology> alreadyDone) {
		if (alreadyDone.contains(ontology)) {
			return;
		}
		alreadyDone.add(ontology);
		for (OntologyProperty p : ontology.getObjectProperties()) {
			for (OntologyObject o : p.getRangeList()) {
				if (o == this) {
					rangeProperties.add(p);
				}
			}
			for (OntologyObject o : p.getDomainList()) {
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
		for (OntologyProperty p : ontology.getDataProperties()) {
			for (OntologyObject o : p.getRangeList()) {
				if (o == this) {
					rangeProperties.add(p);
				}
			}
			for (OntologyObject o : p.getDomainList()) {
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
		for (FlexoOntology o : ontology.getImportedOntologies()) {
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

	public String getHTMLDescription() {
		return getDisplayableDescription();
	}

	public OntologyObject<R> getOriginalDefinition() {
		return originalDefinition;
	}

	public void setOriginalDefinition(OntologyObject<R> originalDefinition) {
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
	public boolean equalsToConcept(OntologyObject o) {
		if (o == null)
			return false;
		return StringUtils.isNotEmpty(getURI()) && getURI().equals(o.getURI());
	}
}
