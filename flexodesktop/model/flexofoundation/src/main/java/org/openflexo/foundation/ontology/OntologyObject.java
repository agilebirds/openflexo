package org.openflexo.foundation.ontology;

import java.util.Set;

import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.localization.Language;

public interface OntologyObject {

	public String getURI();

	public String getName();

	public void setName(String aName) throws Exception;

	public abstract boolean getIsReadOnly();

	public FlexoOntology getFlexoOntology();

	public boolean isSuperConceptOf(OntologyObject concept);

	public boolean isSubConceptOf(OntologyObject concept);

	public String getDescription();

	public void setDescription(String aDescription);

	public String getDisplayableDescription();

	public String getHTMLDescription();

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
	public Object getPropertyValue(OntologyProperty property);

	/**
	 * Sets the value defined for supplied property, asserting that current individual defines one and only one assertion for this property.<br>
	 * 
	 * @param property
	 * @param newValue
	 */
	public void setPropertyValue(OntologyProperty property, Object newValue);

	/**
	 * Return value of specified property, asserting this property is an annotation property matching a literal value
	 * 
	 * @param property
	 * @param language
	 * @return
	 */
	public Object getAnnotationValue(OntologyDataProperty property, Language language);

	/**
	 * Sets value of specified property, asserting this property is an annotation property matching a literal value
	 * 
	 * @param value
	 * @param property
	 * @param language
	 */
	public void setAnnotationValue(Object value, OntologyDataProperty property, Language language);

	/**
	 * Return value of specified property, asserting this property is an annotation property matching an object value
	 * 
	 * @param property
	 * @param language
	 * @return
	 */
	public Object getAnnotationObjectValue(OntologyObjectProperty property);

	/**
	 * Sets value of specified property, asserting this property is an annotation property matching an object value
	 * 
	 * @param value
	 * @param property
	 * @param language
	 */
	public void setAnnotationObjectValue(Object value, OntologyObjectProperty property, Language language);

	/**
	 * Append object property statement for specified property and object
	 * 
	 * @param property
	 * @param object
	 * @return an object representing the added statement
	 */
	public Object addPropertyStatement(OntologyObjectProperty property, OntologyObject object);

	/**
	 * Append property statement for specified property and object
	 * 
	 * @param property
	 * @param object
	 * @return an object representing the added statement
	 */
	public Object addPropertyStatement(OntologyProperty property, Object value);

	/**
	 * Append property statement for specified property, object and language
	 * 
	 * @param property
	 * @param object
	 * @return an object representing the added statement
	 */
	public Object addPropertyStatement(OntologyProperty property, String value, Language language);

	/**
	 * Append property statement for specified property and value
	 * 
	 * @param property
	 * @param object
	 * @return an object representing the added statement
	 */
	public Object addDataPropertyStatement(OntologyDataProperty property, Object value);

	public Set<? extends OntologyProperty> getPropertiesTakingMySelfAsRange();

	public Set<? extends OntologyProperty> getPropertiesTakingMySelfAsDomain();

	/**
	 * This equals has a particular semantics in the way that it returns true only and only if compared objects are representing same
	 * concept regarding URI. This does not guarantee that both objects will respond the same way to some methods.<br>
	 * This method returns true if and only if objects are same, or if one of both object redefine the other one (with eventual many levels)
	 * 
	 * @param o
	 * @return
	 */
	public boolean equalsToConcept(OntologyObject o);

	// NB: implemented in FlexoModelObject
	public void registerEditionPatternReference(EditionPatternInstance editionPatternInstance, PatternRole patternRole);

	// NB: implemented in FlexoModelObject
	public void unregisterEditionPatternReference(EditionPatternInstance editionPatternInstance, PatternRole patternRole);

}