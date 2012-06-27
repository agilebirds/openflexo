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

import java.text.Collator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;

import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;

public class OntologyIndividual extends OntologyObject<Individual> implements Comparable<OntologyIndividual> {

	private static final Logger logger = Logger.getLogger(OntologyIndividual.class.getPackage().getName());

	private Individual individual;

	private final Vector<OntologyClass> superClasses;

	protected OntologyIndividual(Individual anIndividual, FlexoOntology ontology) {
		super(anIndividual, ontology);
		individual = anIndividual;
		superClasses = new Vector<OntologyClass>();
	}

	/**
	 * Update this OntologyIndividual, given base Individual
	 */
	protected void init() {
		updateOntologyStatements(individual);
		updateSuperClasses(individual);
	}

	@Override
	public void delete() {
		getFlexoOntology().removeIndividual(this);
		getOntResource().remove();
		getFlexoOntology().updateConceptsAndProperties();
		super.delete();
		deleteObservers();
	}

	/**
	 * Update this OntologyIndividual, given base Individual
	 */
	@Override
	protected void update() {
		updateOntologyStatements(individual);
		updateSuperClasses(individual);
	}

	/**
	 * Update this OntologyIndividual, given base Individual which is assumed to extends base Individual
	 * 
	 * @param anOntClass
	 */
	protected void update(Individual anIndividual) {
		updateOntologyStatements(anIndividual);
		updateSuperClasses(anIndividual);
	}

	/*@Override
	public void setName(String aName)
	{
		String oldURI = getURI();
		String oldName = getName();
		String newURI;
		if (getURI().indexOf("#") > -1) {
			newURI = getURI().substring(0,getURI().indexOf("#")+1)+aName;
		} else {
			newURI = aName;
		}
		logger.info("Rename individual "+getURI()+" to "+newURI);
		individual = (Individual) (ResourceUtils.renameResource(individual, newURI).as(Individual.class));
		_updateNameAndURIAfterRenaming(aName,newURI);
		getFlexoOntology().renameIndividual(this, oldURI, newURI);
		update();
		setChanged();
		notifyObservers(new NameChanged(oldName,aName));
		logger.info("Les references: "+getEditionPatternReferences());
	}*/

	@Override
	public void setName(String aName) {
		renameURI(aName, individual, Individual.class);
	}

	@Override
	protected void _setOntResource(Individual r) {
		individual = r;
	}

	private void updateSuperClasses(Individual anIndividual) {
		// superClasses.clear();
		Iterator<OntClass> it = anIndividual.listOntClasses(true);
		while (it.hasNext()) {
			try {
				OntClass father = it.next();
				OntologyClass fatherClass = getOntology().retrieveOntologyClass(father);// getOntologyLibrary().getClass(father.getURI());
				if (fatherClass != null) {
					if (!superClasses.contains(fatherClass)) {
						superClasses.add(fatherClass);
					}
					// System.out.println("Add "+fatherClass.getName()+" as a super class of "+getName());
					if (!fatherClass.individuals.contains(this)) {
						// System.out.println("Add "+getName()+" as an individual of "+fatherClass.getName());
						fatherClass.individuals.add(this);
					}
				}
			} catch (ConversionException e) {
				// This happen when loading OWL2 ontology
				// com.hp.hpl.jena.ontology.ConversionException: Cannot convert node http://www.w3.org/2002/07/owl#ObjectProperty to
				// OntClass: it does not have rdf:type owl:Class or equivalent
				// Please investigate this
				logger.warning("Exception thrown while processing updateSuperClasses() for " + getURI());
			} catch (Exception e) {
				logger.warning("Exception thrown while processing updateSuperClasses() for " + getURI());
				e.printStackTrace();
			}
		}
	}

	public Vector<OntologyClass> getSuperClasses() {
		return superClasses;
	}

	@Override
	public String getClassNameKey() {
		return "ontology_individual";
	}

	@Override
	public String getFullyQualifiedName() {
		return "OntologyIndividual:" + getURI();
	}

	public static final Comparator<OntologyIndividual> COMPARATOR = new Comparator<OntologyIndividual>() {
		@Override
		public int compare(OntologyIndividual o1, OntologyIndividual o2) {
			return Collator.getInstance().compare(o1.getName(), o2.getName());
		}
	};

	@Override
	public String getInspectorName() {
		if (getIsReadOnly()) {
			return Inspectors.VE.ONTOLOGY_INDIVIDUAL_READ_ONLY_INSPECTOR; // read-only
		} else {
			return Inspectors.VE.ONTOLOGY_INDIVIDUAL_INSPECTOR;
		}
	}

	@Override
	public int compareTo(OntologyIndividual o) {
		return COMPARATOR.compare(this, o);
	}

	@Override
	public Individual getOntResource() {
		return individual;
	}

	public Individual getIndividual() {
		return getOntResource();
	}

	@Override
	public boolean isSuperConceptOf(OntologyObject concept) {
		return false;
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
		if (property.isAnnotationProperty() && property instanceof OntologyDataProperty
				&& getAnnotationStatements((OntologyDataProperty) property).size() > 1) {
			return getAnnotationValue((OntologyDataProperty) property, FlexoLocalization.getCurrentLanguage());
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

	// Return first property value matching supplied data property
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

	// Return first property value matching supplied data property
	public Object getAnnotationObjectValue(OntologyObjectProperty property) {
		List<ObjectPropertyStatement> annotations = getAnnotationObjectStatements(property);
		for (ObjectPropertyStatement annotation : annotations) {
			OntologyObject returned = annotation.getStatementObject();
			if (returned != null)
				return returned;
		}
		return null;
	}

	/*public void setPropertyValue(OntologyProperty property, Object newValue) {
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
	}*/

	@Override
	public String getDisplayableDescription() {
		String extendsLabel = " extends ";
		boolean isFirst = true;
		for (OntologyClass s : superClasses) {
			extendsLabel += (isFirst ? "" : ",") + s.getName();
			isFirst = false;
		}
		return "Individual " + getName() + extendsLabel;
	}

	@Override
	public boolean isOntologyIndividual() {
		return true;
	}

	@Override
	protected void recursivelySearchRangeAndDomains() {
		super.recursivelySearchRangeAndDomains();
		Vector<OntologyClass> alreadyComputed = new Vector<OntologyClass>();
		for (OntologyClass aClass : getSuperClasses()) {
			_appendRangeAndDomains(aClass, alreadyComputed);
		}
	}

	private void _appendRangeAndDomains(OntologyClass superClass, Vector<OntologyClass> alreadyComputed) {
		if (alreadyComputed.contains(superClass)) {
			return;
		}
		alreadyComputed.add(superClass);
		for (OntologyProperty p : superClass.getDeclaredPropertiesTakingMySelfAsDomain()) {
			if (!propertiesTakingMySelfAsDomain.contains(p)) {
				propertiesTakingMySelfAsDomain.add(p);
			}
		}
		for (OntologyProperty p : superClass.getDeclaredPropertiesTakingMySelfAsRange()) {
			if (!propertiesTakingMySelfAsRange.contains(p)) {
				propertiesTakingMySelfAsRange.add(p);
			}
		}
		for (OntologyClass superSuperClass : superClass.getSuperClasses()) {
			_appendRangeAndDomains(superSuperClass, alreadyComputed);
		}
	}

	@Override
	public String getHTMLDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("Individual <b>" + getName() + "</b><br>");
		sb.append("<i>" + getURI() + "</i><br>");
		sb.append("<b>Asserted in:</b> " + getOntology().getURI() + "<br>");
		if (redefinesOriginalDefinition()) {
			sb.append("<b>Redefines:</b> " + getOriginalDefinition() + "<br>");
		}
		sb.append("<b>Types:</b>");
		for (OntologyClass c : getSuperClasses()) {
			sb.append(" " + c.getDisplayableDescription() + "(" + c.getOntology() + ")");
		}
		sb.append("</html>");
		return sb.toString();
	}

	@Override
	public String getDescription() {
		return (String) getPropertyValue(getOntology().getDataProperty(OntologyLibrary.OPENFLEXO_DESCRIPTION_URI));
	}

	@Override
	public void setDescription(String aDescription) {
		setPropertyValue(getOntology().getDataProperty(OntologyLibrary.OPENFLEXO_DESCRIPTION_URI), aDescription);
	}

	@Override
	public String getTechnicalDescription() {
		return (String) getPropertyValue(getOntology().getDataProperty(OntologyLibrary.TECHNICAL_DESCRIPTION_URI));
	}

	@Override
	public void setTechnicalDescription(String technicalDescription) {
		setPropertyValue(getOntology().getDataProperty(OntologyLibrary.TECHNICAL_DESCRIPTION_URI), technicalDescription);
	};

	@Override
	public String getBusinessDescription() {
		return (String) getPropertyValue(getOntology().getDataProperty(OntologyLibrary.BUSINESS_DESCRIPTION_URI));
	}

	@Override
	public void setBusinessDescription(String businessDescription) {
		setPropertyValue(getOntology().getDataProperty(OntologyLibrary.BUSINESS_DESCRIPTION_URI), businessDescription);
	}

	@Override
	public String getUserManualDescription() {
		return (String) getPropertyValue(getOntology().getDataProperty(OntologyLibrary.USER_MANUAL_DESCRIPTION_URI));
	}

	@Override
	public void setUserManualDescription(String userManualDescription) {
		setPropertyValue(getOntology().getDataProperty(OntologyLibrary.USER_MANUAL_DESCRIPTION_URI), userManualDescription);
	}
}
