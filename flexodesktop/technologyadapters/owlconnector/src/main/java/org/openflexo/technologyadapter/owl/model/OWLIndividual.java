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

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IFlexoOntologyPropertyValue;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;

import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Resource;

public class OWLIndividual extends OWLConcept<Individual> implements IFlexoOntologyIndividual, Comparable<IFlexoOntologyIndividual> {

	private static final Logger logger = Logger.getLogger(IFlexoOntologyIndividual.class.getPackage().getName());

	private Individual individual;

	private final Vector<OWLClass> types;

	protected OWLIndividual(Individual anIndividual, OWLOntology ontology, OWLTechnologyAdapter adapter) {
		super(anIndividual, ontology, adapter);
		individual = anIndividual;
		types = new Vector<OWLClass>();
	}

	/**
	 * Update this IFlexoOntologyIndividual, given base Individual
	 */
	protected void init() {
		updateOntologyStatements(individual);
		updateSuperClasses(individual);
	}

	@Override
	public boolean delete() {
		getFlexoOntology().removeIndividual(this);
		getOntResource().remove();
		getFlexoOntology().updateConceptsAndProperties();
		super.delete();
		deleteObservers();
		return true;
	}

	/**
	 * Update this IFlexoOntologyIndividual, given base Individual
	 */
	@Override
	protected void update() {
		updateOntologyStatements(individual);
		updateSuperClasses(individual);
	}

	/**
	 * Update this IFlexoOntologyIndividual, given base Individual which is assumed to extends base Individual
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
		Iterator<Resource> it = anIndividual.listRDFTypes(true);
		while (it.hasNext()) {
			try {
				Resource father = it.next();
				if (father.canAs(OntClass.class)) {
					OWLClass fatherClass = getOntology().retrieveOntologyClass(father.as(OntClass.class));// getOntologyLibrary().getClass(father.getURI());
					if (fatherClass != null) {
						if (!types.contains(fatherClass)) {
							types.add(fatherClass);
						}
					}
				}
			} catch (ConversionException e) {
				// This happen when loading OWL2 ontology
				// com.hp.hpl.jena.ontology.ConversionException: Cannot convert node http://www.w3.org/2002/07/owl#ObjectProperty to
				// OntClass: it does not have rdf:type owl:Class or equivalent
				// Please investigate this
				logger.warning("Exception thrown while processing updateSuperClasses() for " + getURI());
				// e.printStackTrace();
			} catch (Exception e) {
				logger.warning("Exception thrown while processing updateSuperClasses() for " + getURI());
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<OWLClass> getTypes() {
		return types;
	}

	// Use use getTypes() instead
	@Deprecated
	public List<OWLClass> getSuperClasses() {
		return getTypes();
	}

	/**
	 * Add type to this individual
	 * 
	 * @param type
	 */
	public SubClassStatement addToTypes(OWLClass aType) {
		if (aType != null) {
			getOntResource().addOntClass(aType.getOntResource());
			updateOntologyStatements();
			return getSubClassStatement(aType);
		}
		logger.warning("Type " + aType + " is null");
		return null;
	}

	public void removeFromTypes(OWLClass aType) {
		if (aType != null) {
			getOntResource().removeOntClass(aType.getOntResource());
			updateOntologyStatements();
		}
		logger.warning("Type " + aType + " is not a OWLClass");
	}

	public static final Comparator<IFlexoOntologyIndividual> COMPARATOR = new Comparator<IFlexoOntologyIndividual>() {
		@Override
		public int compare(IFlexoOntologyIndividual o1, IFlexoOntologyIndividual o2) {
			return Collator.getInstance().compare(o1.getName(), o2.getName());
		}
	};

	@Override
	public int compareTo(IFlexoOntologyIndividual o) {
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
	public boolean isSuperConceptOf(IFlexoOntologyConcept concept) {
		return false;
	}

	@Override
	public String getDisplayableDescription() {
		String extendsLabel = " extends ";
		boolean isFirst = true;
		for (IFlexoOntologyClass s : types) {
			extendsLabel += (isFirst ? "" : ",") + s.getName();
			isFirst = false;
		}
		return "Individual " + getName() + extendsLabel;
	}

	@Override
	protected void recursivelySearchRangeAndDomains() {
		super.recursivelySearchRangeAndDomains();
		for (OWLClass aClass : getSuperClasses()) {
			propertiesTakingMySelfAsRange.addAll(aClass.getPropertiesTakingMySelfAsRange());
			propertiesTakingMySelfAsDomain.addAll(aClass.getPropertiesTakingMySelfAsDomain());
		}
	}

	/*@Override
	protected void recursivelySearchRangeAndDomains() {
		super.recursivelySearchRangeAndDomains();
		Vector<IFlexoOntologyClass> alreadyComputed = new Vector<IFlexoOntologyClass>();
		for (IFlexoOntologyClass aClass : getSuperClasses()) {
			_appendRangeAndDomains(aClass, alreadyComputed);
		}
	}

	private void _appendRangeAndDomains(IFlexoOntologyClass superClass, Vector<IFlexoOntologyClass> alreadyComputed) {
		if (alreadyComputed.contains(superClass)) {
			return;
		}
		alreadyComputed.add(superClass);
		for (IFlexoOntologyStructuralProperty p : superClass.getDeclaredPropertiesTakingMySelfAsDomain()) {
			if (!propertiesTakingMySelfAsDomain.contains(p)) {
				propertiesTakingMySelfAsDomain.add(p);
			}
		}
		for (IFlexoOntologyStructuralProperty p : superClass.getDeclaredPropertiesTakingMySelfAsRange()) {
			if (!propertiesTakingMySelfAsRange.contains(p)) {
				propertiesTakingMySelfAsRange.add(p);
			}
		}
		for (IFlexoOntologyClass superSuperClass : superClass.getSuperClasses()) {
			_appendRangeAndDomains(superSuperClass, alreadyComputed);
		}
	}*/

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
		for (OWLClass c : getSuperClasses()) {
			sb.append(" " + c.getDisplayableDescription() + "(" + c.getOntology() + ")");
		}
		sb.append("</html>");
		return sb.toString();
	}

	@Override
	public String getDescription() {
		return (String) getPropertyValue(getOntology().getDataProperty(OWLOntologyLibrary.OPENFLEXO_DESCRIPTION_URI));
	}

	@Override
	public void setDescription(String aDescription) {
		setPropertyValue(getOntology().getDataProperty(OWLOntologyLibrary.OPENFLEXO_DESCRIPTION_URI), aDescription);
	}

	@Override
	public List<? extends IFlexoOntologyFeatureAssociation> getStructuralFeatureAssociations() {
		// No feature associations for this kind of concept
		return Collections.emptyList();
	}

	@Override
	public boolean isIndividualOf(IFlexoOntologyClass aClass) {
		return aClass.isSuperConceptOf(this);
	}

	@Override
	public List<? extends IFlexoOntologyPropertyValue> getPropertyValues() {
		return getAllPropertyStatements();
	}

	@Override
	public IFlexoOntologyPropertyValue getPropertyValue(IFlexoOntologyStructuralProperty property) {
		return getPropertyStatement(property);
	}

	@Override
	public IFlexoOntologyPropertyValue addToPropertyValue(IFlexoOntologyStructuralProperty property, Object newValue) {
		logger.warning("Not implemented yet");
		return null;
	}

	@Override
	public IFlexoOntologyPropertyValue removeFromPropertyValue(IFlexoOntologyStructuralProperty property, Object valueToRemove) {
		logger.warning("Not implemented yet");
		return null;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + getURI();
	}
}
