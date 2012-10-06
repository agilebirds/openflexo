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
package org.openflexo.components.widget;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OWL2URIDefinitions;
import org.openflexo.foundation.ontology.OntologicDataType;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.OntologyRestrictionClass;
import org.openflexo.foundation.ontology.RDFSURIDefinitions;
import org.openflexo.foundation.ontology.RDFURIDefinitions;
import org.openflexo.toolbox.StringUtils;

/**
 * Model supporting browsing inside ontologies<br>
 * 
 * Developers note: this model is shared by many widgets. Please modify it with caution.
 * 
 * @see FIBClassSelector
 * @see FIBIndividualSelector
 * @see FIBPropertySelector
 * 
 * @author sguerin
 */
public class OntologyBrowserModel extends Observable implements FlexoObserver {

	static final Logger logger = Logger.getLogger(OntologyBrowserModel.class.getPackage().getName());

	private FlexoOntology context;
	private boolean hierarchicalMode = true;
	private boolean strictMode = false;
	private OntologyClass rootClass;
	private boolean displayPropertiesInClasses = true;
	private boolean showOWLAndRDFConcepts = false;
	private OntologyClass domain = null;
	private OntologyClass range = null;
	private OntologicDataType dataType = null;

	private boolean showObjectProperties = true;
	private boolean showDataProperties = true;
	private boolean showAnnotationProperties = true;
	private boolean showClasses = true;
	private boolean showIndividuals = true;

	private Vector<OntologyObject<?>> roots = null;
	private Hashtable<OntologyObject<?>, Vector<OntologyObject<?>>> structure = null;

	public OntologyBrowserModel(FlexoOntology context) {
		super();
		setContext(context);
	}

	public List<OntologyObject<?>> getRoots() {
		if (roots == null) {
			recomputeStructure();
		}
		return roots;
	}

	public List<OntologyObject<?>> getChildren(OntologyObject<?> father) {
		return structure.get(father);
	}

	public void recomputeStructure() {
		if (getHierarchicalMode()) {
			computeHierarchicalStructure();
		} else {
			computeNonHierarchicalStructure();
		}
	}

	public void delete() {
		context = null;
	}

	public FlexoOntology getContext() {
		return context;
	}

	public void setContext(FlexoOntology context) {
		if (this.context != null) {
			((FlexoObservable) context).deleteObserver(this);
		}
		this.context = context;
		if (this.context != null) {
			((FlexoObservable) context).addObserver(this);
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		System.out.println("ok, je recalcule tout");
		recomputeStructure();
	}

	public OntologyClass getRootClass() {
		return rootClass;
	}

	public void setRootClass(OntologyClass rootClass) {
		this.rootClass = rootClass;
	}

	public boolean getHierarchicalMode() {
		return hierarchicalMode;
	}

	public void setHierarchicalMode(boolean hierarchicalMode) {
		this.hierarchicalMode = hierarchicalMode;
	}

	public boolean getStrictMode() {
		return strictMode;
	}

	public void setStrictMode(boolean strictMode) {
		this.strictMode = strictMode;
	}

	public boolean getDisplayPropertiesInClasses() {
		return displayPropertiesInClasses;
	}

	public void setDisplayPropertiesInClasses(boolean displayPropertiesInClasses) {
		this.displayPropertiesInClasses = displayPropertiesInClasses;
	}

	public boolean getShowObjectProperties() {
		return showObjectProperties;
	}

	public void setShowObjectProperties(boolean showObjectProperties) {
		this.showObjectProperties = showObjectProperties;
	}

	public boolean getShowDataProperties() {
		return showDataProperties;
	}

	public void setShowDataProperties(boolean showDataProperties) {
		this.showDataProperties = showDataProperties;
	}

	public boolean getShowAnnotationProperties() {
		return showAnnotationProperties;
	}

	public void setShowAnnotationProperties(boolean showAnnotationProperties) {
		this.showAnnotationProperties = showAnnotationProperties;
	}

	public boolean getShowClasses() {
		return showClasses;
	}

	public void setShowClasses(boolean showClasses) {
		this.showClasses = showClasses;
	}

	public boolean getShowIndividuals() {
		return showIndividuals;
	}

	public void setShowIndividuals(boolean showIndividuals) {
		this.showIndividuals = showIndividuals;
	}

	public boolean getShowOWLAndRDFConcepts() {
		return showOWLAndRDFConcepts;
	}

	public void setShowOWLAndRDFConcepts(boolean showOWLAndRDFConcepts) {
		this.showOWLAndRDFConcepts = showOWLAndRDFConcepts;
	}

	public OntologyClass getDomain() {
		return domain;
	}

	public void setDomain(OntologyClass domain) {
		this.domain = domain;
	}

	public OntologyClass getRange() {
		return range;
	}

	public void setRange(OntologyClass range) {
		this.range = range;
	}

	public OntologicDataType getDataType() {
		return dataType;
	}

	public void setDataType(OntologicDataType dataType) {
		this.dataType = dataType;
	}

	public boolean isDisplayable(OntologyObject<?> object) {

		if (object instanceof FlexoOntology) {
			if ((object == object.getOntologyLibrary().getRDFOntology() || object == object.getOntologyLibrary().getRDFSOntology() || object == object
					.getOntologyLibrary().getOWLOntology()) && object != getContext()) {
				return getShowOWLAndRDFConcepts();
			}
			return true;
		}
		if (!getShowOWLAndRDFConcepts() && StringUtils.isNotEmpty(object.getURI()) && object.getOntology() != getContext()) {
			if (object.getURI().startsWith(RDFURIDefinitions.RDF_ONTOLOGY_URI)
					|| object.getURI().startsWith(RDFSURIDefinitions.RDFS_ONTOLOGY_URI)
					|| object.getURI().startsWith(OWL2URIDefinitions.OWL_ONTOLOGY_URI)) {
				return false;
			}
		}

		boolean returned = false;
		if (object instanceof OntologyClass && showClasses) {
			if (getRootClass() != null) {
				returned = getRootClass().isSuperConceptOf(object);
			} else {
				returned = true;
			}
		}
		if (object instanceof OntologyIndividual && showIndividuals) {
			if (getRootClass() != null) {
				returned = getRootClass().isSuperConceptOf(object);
			} else {
				returned = true;
			}
		}
		if (object instanceof OntologyObjectProperty && showObjectProperties) {
			returned = true;
		}
		if (object instanceof OntologyDataProperty && showDataProperties) {
			returned = true;
		}
		if (object instanceof OntologyProperty && ((OntologyProperty) object).isAnnotationProperty() && showAnnotationProperties) {
			returned = true;
		}

		if (returned == false) {
			return false;
		}

		if (object instanceof OntologyProperty && getRootClass() != null) {
			boolean foundAPreferredLocationAsSubClassOfRootClass = false;
			List<OntologyClass> preferredLocation = getPreferredStorageLocations((OntologyProperty) object, null);
			for (OntologyClass pl : preferredLocation) {
				if (rootClass.isSuperConceptOf(pl)) {
					foundAPreferredLocationAsSubClassOfRootClass = true;
					break;
				}
			}
			if (!foundAPreferredLocationAsSubClassOfRootClass) {
				return false;
			}
		}

		if (object instanceof OntologyProperty && getDomain() != null) {
			OntologyProperty p = (OntologyProperty) object;
			if (p.getDomain() instanceof OntologyClass) {
				if (!((OntologyClass) p.getDomain()).isSuperClassOf(getDomain())) {
					/*System.out.println("Dismiss " + object + " becasuse " + p.getDomain().getName() + " is not superclass of "
							+ getDomain().getName());*/
					return false;
				}
				/*if (!getDomain().isSuperClassOf(((OntologyClass) p.getDomain()))) {
					return false;
				}*/
			} else {
				// System.out.println("Dismiss " + object + " becasuse domain=" + p.getDomain());
				return false;
			}
		}

		if (object instanceof OntologyObjectProperty && getRange() != null) {
			OntologyObjectProperty p = (OntologyObjectProperty) object;
			if (p.getRange() instanceof OntologyClass) {
				if (!((OntologyClass) p.getRange()).isSuperClassOf(getRange())) {
					return false;
				}
				/*if (!getRange().isSuperClassOf(((OntologyClass) p.getRange()))) {
					return false;
				}*/
			} else {
				return false;
			}
		}

		if (object instanceof OntologyDataProperty && getDataType() != null) {
			OntologyDataProperty p = (OntologyDataProperty) object;
			if (p.getDataType() != getDataType()) {
				// System.out.println("Dismiss " + object + " becasuse " + p.getDataType() + " is not  " + getDataType());
				return false;
			}
		}

		return true;
	}

	private void appendOntologyContents(FlexoOntology o, OntologyObject<?> parent) {
		List<OntologyProperty> properties = new Vector<OntologyProperty>();
		List<OntologyIndividual> individuals = new Vector<OntologyIndividual>();
		Hashtable<OntologyProperty, List<OntologyClass>> storedProperties = new Hashtable<OntologyProperty, List<OntologyClass>>();
		Hashtable<OntologyIndividual, OntologyClass> storedIndividuals = new Hashtable<OntologyIndividual, OntologyClass>();
		List<OntologyProperty> unstoredProperties = new Vector<OntologyProperty>();
		List<OntologyIndividual> unstoredIndividuals = new Vector<OntologyIndividual>();
		List<OntologyClass> storageClasses = new Vector<OntologyClass>();
		properties = retrieveDisplayableProperties(o);
		individuals = retrieveDisplayableIndividuals(o);

		if (getDisplayPropertiesInClasses()) {
			for (OntologyProperty p : properties) {
				List<OntologyClass> preferredLocations = getPreferredStorageLocations(p, o);
				if (preferredLocations != null && preferredLocations.size() > 0) {
					storedProperties.put(p, preferredLocations);
					for (OntologyClass preferredLocation : preferredLocations) {
						if (!storageClasses.contains(preferredLocation)) {
							storageClasses.add(preferredLocation);
						}
					}
				} else {
					unstoredProperties.add(p);
				}
			}
		}

		if (showIndividuals) {
			for (OntologyIndividual i : individuals) {
				OntologyClass preferredLocation = getPreferredStorageLocation(i);
				if (preferredLocation != null) {
					storedIndividuals.put(i, preferredLocation);
					if (!storageClasses.contains(preferredLocation)) {
						storageClasses.add(preferredLocation);
					}
				} else {
					unstoredIndividuals.add(i);
				}
			}
		}

		if (parent != null && parent != o) {
			addChildren(parent, o);
		}

		if (showClasses) {
			List<OntologyClass> classes = retrieveDisplayableClasses(o);
			if (classes.size() > 0) {
				removeOriginalFromRedefinedObjects(classes);
				addClassesAsHierarchy(parent == null ? null : o, classes);
			}
		} else if (getDisplayPropertiesInClasses() || showIndividuals) {
			removeOriginalFromRedefinedObjects(storageClasses);
			appendParentClassesToStorageClasses(storageClasses);
			addClassesAsHierarchy(parent == null ? null : o, storageClasses);
		}

		for (OntologyIndividual i : storedIndividuals.keySet()) {
			OntologyClass preferredLocation = storedIndividuals.get(i);
			addChildren(preferredLocation, i);
		}

		for (OntologyIndividual i : unstoredIndividuals) {
			addChildren(parent == null ? null : o, i);
		}

		if (getDisplayPropertiesInClasses()) {
			for (OntologyProperty p : storedProperties.keySet()) {
				List<OntologyClass> preferredLocations = storedProperties.get(p);
				for (OntologyClass preferredLocation : preferredLocations) {
					addChildren(preferredLocation, p);
				}
			}

			addPropertiesAsHierarchy(parent == null ? null : o, unstoredProperties);
		}

		else {
			addPropertiesAsHierarchy(parent == null ? null : o, properties);
		}
	}

	private void computeNonHierarchicalStructure() {
		if (roots != null) {
			roots.clear();
		} else {
			roots = new Vector<OntologyObject<?>>();
		}
		if (structure != null) {
			structure.clear();
		} else {
			structure = new Hashtable<OntologyObject<?>, Vector<OntologyObject<?>>>();
		}

		if (getContext() == null) {
			return;
		}

		if (strictMode) {

			appendOntologyContents(getContext(), null);

		} else {
			roots.add(getContext());
			appendOntologyContents(getContext(), getContext());
			for (FlexoOntology o : getContext().getAllImportedOntologies()) {
				if (o != getContext() && isDisplayable(o)) {
					appendOntologyContents(o, getContext());
				}
			}
		}
	}

	private void addPropertiesAsHierarchy(OntologyObject<?> parent, List<OntologyProperty> someProperties) {
		for (OntologyProperty p : someProperties) {
			if (!hasASuperPropertyDefinedInList(p, someProperties)) {
				appendPropertyInHierarchy(parent, p, someProperties);
			}
		}
	}

	private void appendPropertyInHierarchy(OntologyObject<?> parent, OntologyProperty p, List<OntologyProperty> someProperties) {
		if (parent == null) {
			roots.add(p);
		} else {
			addChildren(parent, p);
		}
		for (OntologyProperty subProperty : p.getSubProperties()) {
			if (someProperties.contains(subProperty)) {
				appendPropertyInHierarchy(p, subProperty, someProperties);
			}
		}
	}

	private boolean hasASuperPropertyDefinedInList(OntologyProperty p, List<OntologyProperty> someProperties) {
		if (p.getSuperProperties() == null) {
			return false;
		} else {
			for (OntologyProperty sp : p.getSuperProperties()) {
				if (someProperties.contains(sp)) {
					return true;
				}
			}
			return false;
		}
	}

	private void addChildren(OntologyObject parent, OntologyObject child) {
		Vector<OntologyObject<?>> v = structure.get(parent);
		if (v == null) {
			v = new Vector<OntologyObject<?>>();
			structure.put(parent, v);
		}
		if (!v.contains(child)) {
			v.add(child);
		}
	}

	private void computeHierarchicalStructure() {

		System.out.println("computeHierarchicalStructure()");

		logger.fine("computeHierarchicalStructure()");

		if (roots != null) {
			roots.clear();
		} else {
			roots = new Vector<OntologyObject<?>>();
		}
		if (structure != null) {
			structure.clear();
		} else {
			structure = new Hashtable<OntologyObject<?>, Vector<OntologyObject<?>>>();
		}

		List<OntologyProperty> properties = new Vector<OntologyProperty>();
		Hashtable<OntologyProperty, List<OntologyClass>> storedProperties = new Hashtable<OntologyProperty, List<OntologyClass>>();
		List<OntologyProperty> unstoredProperties = new Vector<OntologyProperty>();
		List<OntologyClass> storageClasses = new Vector<OntologyClass>();

		List<OntologyIndividual> individuals = new Vector<OntologyIndividual>();
		Hashtable<OntologyIndividual, OntologyClass> storedIndividuals = new Hashtable<OntologyIndividual, OntologyClass>();
		List<OntologyIndividual> unstoredIndividuals = new Vector<OntologyIndividual>();

		if (getContext() == null) {
			return;
		}

		if (strictMode) {
			properties = retrieveDisplayableProperties(getContext());
			individuals = retrieveDisplayableIndividuals(getContext());
		} else {
			for (FlexoOntology o : getContext().getAllImportedOntologies()) {
				properties.addAll(retrieveDisplayableProperties(o));
				individuals.addAll(retrieveDisplayableIndividuals(o));
			}
		}
		if (getDisplayPropertiesInClasses()) {
			for (OntologyProperty p : properties) {
				List<OntologyClass> preferredLocations = getPreferredStorageLocations(p, null);
				if (preferredLocations != null) {
					storedProperties.put(p, preferredLocations);
					for (OntologyClass preferredLocation : preferredLocations) {
						if (!storageClasses.contains(preferredLocation)) {
							storageClasses.add(preferredLocation);
						}
					}
				} else {
					unstoredProperties.add(p);
				}
			}
		}

		if (showIndividuals) {
			for (OntologyIndividual i : individuals) {
				OntologyClass preferredLocation = getPreferredStorageLocation(i);
				if (preferredLocation != null) {
					storedIndividuals.put(i, preferredLocation);
					if (!storageClasses.contains(preferredLocation)) {
						storageClasses.add(preferredLocation);
					}
				} else {
					unstoredIndividuals.add(i);
				}
			}
		}

		if (getShowClasses()) {
			List<OntologyClass> classes = new Vector<OntologyClass>();
			if (strictMode) {
				classes = retrieveDisplayableClasses(getContext());
			} else {
				for (FlexoOntology o : getContext().getAllImportedOntologies()) {
					classes.addAll(retrieveDisplayableClasses(o));
				}
			}
			removeOriginalFromRedefinedObjects(classes);
			addClassesAsHierarchy(null, classes);
		} else if (getDisplayPropertiesInClasses() || showIndividuals) {
			removeOriginalFromRedefinedObjects(storageClasses);
			appendParentClassesToStorageClasses(storageClasses);
			removeOriginalFromRedefinedObjects(storageClasses);
			addClassesAsHierarchy(null, storageClasses);
		}

		for (OntologyIndividual i : storedIndividuals.keySet()) {
			OntologyClass preferredLocation = storedIndividuals.get(i);
			addChildren(preferredLocation, i);
		}

		for (OntologyIndividual i : unstoredIndividuals) {
			addChildren(getContext().getThingConcept(), i);
		}

		if (getDisplayPropertiesInClasses()) {
			for (OntologyProperty p : storedProperties.keySet()) {
				List<OntologyClass> preferredLocations = storedProperties.get(p);
				for (OntologyClass preferredLocation : preferredLocations) {
					addChildren(preferredLocation, p);
				}
			}
			addPropertiesAsHierarchy(null, unstoredProperties);
		} else {
			addPropertiesAsHierarchy(null, properties);
		}

	}

	/**
	 * Compute a list of preferred location for an ontology property to be displayed.<br>
	 * If searchedOntology is not null, restrict returned list to classes declared in supplied ontology
	 * 
	 * @param p
	 * @param searchedOntology
	 * @return
	 */
	private List<OntologyClass> getPreferredStorageLocations(OntologyProperty p, FlexoOntology searchedOntology) {
		List<OntologyClass> potentialStorageClasses = new ArrayList<OntologyClass>();

		// First we look if property has a defined domain
		if (p.getDomain() instanceof OntologyClass) {
			// Return the most specialized definition
			OntologyClass c = (searchedOntology != null ? searchedOntology : getContext()).getClass(((OntologyClass) p.getDomain())
					.getURI());
			if (c != null && (searchedOntology == null || c.getOntology() == searchedOntology)) {
				potentialStorageClasses.add(c);
				return potentialStorageClasses;
			}
		}

		for (OntologyClass c : getContext().getAccessibleClasses()) {
			if (c.isNamedClass()) {
				for (OntologyClass superClass : c.getSuperClasses()) {
					if (superClass instanceof OntologyRestrictionClass
							&& ((OntologyRestrictionClass) superClass).getProperty().equalsToConcept(p)) {
						if (searchedOntology == null || c.getOntology() == searchedOntology) {
							potentialStorageClasses.add(c);
						}
					}
				}
			}
		}

		return potentialStorageClasses;

		/*if (potentialStorageClasses.size() > 0) {
			return potentialStorageClasses.get(0);
		}*/

		/*if (p.getStorageLocations().size() > 0) {
			return p.getStorageLocations().get(0);
		}*/
		// return null;
	}

	private OntologyClass getPreferredStorageLocation(OntologyIndividual i) {

		// Return the first class which is not the Thing concept
		for (OntologyClass c : i.getSuperClasses()) {
			if (c.isNamedClass() && !c.isThing()) {
				OntologyClass returned = getContext().getClass(c.getURI());
				if (returned != null) {
					return returned;
				}
			}
		}
		return getContext().getThingConcept();
	}

	private void addClassesAsHierarchy(OntologyObject<?> parent, List<OntologyClass> someClasses) {
		if (someClasses.contains(getContext().getThingConcept())) {
			appendClassInHierarchy(parent, getContext().getThingConcept(), someClasses);
		} else {
			List<OntologyClass> listByExcludingRootClasses = new ArrayList<OntologyClass>(someClasses);
			List<OntologyClass> localRootClasses = new ArrayList<OntologyClass>();
			for (OntologyClass c : someClasses) {
				if (!hasASuperClassDefinedInList(c, someClasses)) {
					localRootClasses.add(c);
					listByExcludingRootClasses.remove(c);
				}
			}
			for (OntologyClass c : localRootClasses) {
				List<OntologyClass> potentialChildren = new ArrayList<OntologyClass>();
				for (OntologyClass c2 : listByExcludingRootClasses) {
					if (c.isSuperConceptOf(c2)) {
						potentialChildren.add(c2);
					}
				}
				appendClassInHierarchy(parent, c, potentialChildren);
			}
		}
	}

	private void appendClassInHierarchy(OntologyObject<?> parent, OntologyClass c, List<OntologyClass> someClasses) {

		List<OntologyClass> listByExcludingCurrentClass = new ArrayList<OntologyClass>(someClasses);
		listByExcludingCurrentClass.remove(c);

		if (parent == null) {
			roots.add(c);
		} else {
			addChildren(parent, c);
		}
		if (listByExcludingCurrentClass.size() > 0) {
			addClassesAsHierarchy(c, listByExcludingCurrentClass);
		}
	}

	private boolean hasASuperClassDefinedInList(OntologyClass c, List<OntologyClass> someClasses) {
		if (c.getSuperClasses() == null) {
			return false;
		} else {
			for (OntologyClass c2 : someClasses) {
				if (c2.isSuperConceptOf(c) && c2 != c) {
					return true;
				}
			}

			return false;
		}
	}

	private List<OntologyObject> retrieveDisplayableObjects(FlexoOntology ontology) {
		Vector<OntologyObject> returned = new Vector<OntologyObject>();
		for (OntologyClass c : ontology.getClasses()) {
			if (isDisplayable(c)) {
				returned.add(c);
			}
		}
		for (OntologyIndividual i : ontology.getIndividuals()) {
			if (isDisplayable(i)) {
				returned.add(i);
			}
		}
		for (OntologyProperty p : ontology.getObjectProperties()) {
			if (isDisplayable(p)) {
				returned.add(p);
			}
		}
		for (OntologyProperty p : ontology.getDataProperties()) {
			if (isDisplayable(p)) {
				returned.add(p);
			}
		}
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
				list.remove(c.getOriginalDefinition());
			}
			if (c instanceof OntologyClass && ((OntologyClass) c).isThing() && c.getOntology() != getContext()
					&& list.contains(getContext().getThingConcept())) {
				list.remove(c);
			}
		}
	}

	private void appendParentClassesToStorageClasses(List<OntologyClass> someClasses) {
		// System.out.println("appendParentClassesToStorageClasses with " + someClasses);

		// First compute the list of all top-level classes
		List<OntologyClass> topLevelClasses = new ArrayList<OntologyClass>();
		for (OntologyClass c : someClasses) {
			boolean requireAddInTopClasses = true;
			List<OntologyClass> classesToRemove = new ArrayList<OntologyClass>();
			for (OntologyClass tpC : topLevelClasses) {
				if (tpC.isSuperClassOf(c)) {
					requireAddInTopClasses = false;
				}
				if (c.isSuperClassOf(tpC)) {
					classesToRemove.add(tpC);
				}
			}
			if (requireAddInTopClasses) {
				topLevelClasses.add(c);
				for (OntologyClass c2r : classesToRemove) {
					topLevelClasses.remove(c2r);
				}
			}
		}

		List<OntologyClass> classesToAdd = new ArrayList<OntologyClass>();
		if (someClasses.size() > 1) {
			for (int i = 0; i < topLevelClasses.size(); i++) {
				for (int j = i + 1; j < topLevelClasses.size(); j++) {
					// System.out.println("i=" + i + " j=" + j + " someClasses.size()=" + someClasses.size());
					// System.out.println("i=" + i + " j=" + j + " someClasses.size()=" + someClasses.size() + " someClasses=" +
					// someClasses);
					OntologyClass c1 = topLevelClasses.get(i);
					OntologyClass c2 = topLevelClasses.get(j);
					OntologyClass ancestor = OntologyClass.getFirstCommonAncestor(c1, c2);
					// System.out.println("Ancestor of " + c1 + " and " + c2 + " is " + ancestor);
					if (ancestor != null /*&& !ancestor.isThing()*/) {
						OntologyClass ancestorSeenFromContextOntology = getContext().getClass(ancestor.getURI());
						if (ancestorSeenFromContextOntology != null) {
							if (!someClasses.contains(ancestorSeenFromContextOntology)
									&& !classesToAdd.contains(ancestorSeenFromContextOntology)) {
								classesToAdd.add(ancestorSeenFromContextOntology);
								// System.out.println("Add parent " + ancestorSeenFromContextOntology + " because of c1=" + c1.getName()
								// + " and c2=" + c2.getName());
							}
						}
					}
				}
			}
			if (classesToAdd.size() > 0) {
				for (OntologyClass c : classesToAdd) {
					someClasses.add(c);
				}

				// Do it again whenever there are classes to add
				appendParentClassesToStorageClasses(someClasses);
			}
		}
	}

	/*private void removeOriginalFromRedefinedClasses(List<OntologyClass> list) {
		// Remove originals from redefined classes
		for (OntologyClass c : new ArrayList<OntologyClass>(list)) {
			if (c.redefinesOriginalDefinition()) {
				list.remove(c.getOriginalDefinition());
			}
		}
	}*/

	private List<OntologyClass> retrieveDisplayableClasses(FlexoOntology ontology) {
		Vector<OntologyClass> returned = new Vector<OntologyClass>();
		for (OntologyClass c : ontology.getClasses()) {
			if (isDisplayable(c)) {
				returned.add(c);
			}
		}
		/*if (!ontology.getURI().equals(OntologyLibrary.RDF_ONTOLOGY_URI) && !ontology.getURI().equals(OntologyLibrary.RDFS_ONTOLOGY_URI)) {
			System.out.println("Thing " + ontology.getRootClass() + " refines " + ontology.getRootClass().getOriginalDefinition());
		}*/
		removeOriginalFromRedefinedObjects(returned);
		return returned;
	}

	private List<OntologyIndividual> retrieveDisplayableIndividuals(FlexoOntology ontology) {
		Vector<OntologyIndividual> returned = new Vector<OntologyIndividual>();
		for (OntologyIndividual c : ontology.getIndividuals()) {
			if (isDisplayable(c)) {
				returned.add(c);
			}
		}
		return returned;
	}

	private List<OntologyProperty> retrieveDisplayableProperties(FlexoOntology ontology) {
		Vector<OntologyProperty> returned = new Vector<OntologyProperty>();
		for (OntologyProperty p : ontology.getObjectProperties()) {
			if (isDisplayable(p)) {
				returned.add(p);
			}
		}
		for (OntologyProperty p : ontology.getDataProperties()) {
			if (isDisplayable(p)) {
				returned.add(p);
			}
		}
		return returned;
	}

	public Font getFont(OntologyObject object, Font baseFont) {
		if (object.getOntology() != getContext()) {
			return baseFont.deriveFont(Font.ITALIC);
		}
		return baseFont;
	}

}