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
import java.util.Map;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.ontology.AbstractOntologyObject;
import org.openflexo.foundation.ontology.BuiltInDataType;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.OntologyUtils;

/**
 * Model supporting browsing through models or metamodels conform to {@link FlexoOntology} API<br>
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

	private IFlexoOntology context;
	private boolean hierarchicalMode = true;
	private boolean strictMode = false;
	private IFlexoOntologyClass rootClass;
	private boolean displayPropertiesInClasses = true;
	private IFlexoOntologyClass domain = null;
	private IFlexoOntologyClass range = null;
	private BuiltInDataType dataType = null;

	private boolean showObjectProperties = true;
	private boolean showDataProperties = true;
	private boolean showAnnotationProperties = true;
	private boolean showClasses = true;
	private boolean showIndividuals = true;

	private List<IFlexoOntologyObject> roots = null;
	private Map<AbstractOntologyObject, List<AbstractOntologyObject>> structure = null;

	public OntologyBrowserModel(IFlexoOntology context) {
		super();
		setContext(context);
	}

	public List<IFlexoOntologyObject> getRoots() {
		if (roots == null) {
			recomputeStructure();
		}
		return roots;
	}

	public List<AbstractOntologyObject> getChildren(AbstractOntologyObject father) {
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

	public IFlexoOntology getContext() {
		return context;
	}

	public void setContext(IFlexoOntology context) {
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

	public IFlexoOntologyClass getRootClass() {
		return rootClass;
	}

	public void setRootClass(IFlexoOntologyClass rootClass) {
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

	public IFlexoOntologyClass getDomain() {
		return domain;
	}

	public void setDomain(IFlexoOntologyClass domain) {
		this.domain = domain;
	}

	public IFlexoOntologyClass getRange() {
		return range;
	}

	public void setRange(IFlexoOntologyClass range) {
		this.range = range;
	}

	public BuiltInDataType getDataType() {
		return dataType;
	}

	public void setDataType(BuiltInDataType dataType) {
		this.dataType = dataType;
	}

	public boolean isDisplayable(IFlexoOntologyObject object) {

		boolean returned = false;
		if (object instanceof IFlexoOntologyClass && showClasses) {
			if (getRootClass() != null && object instanceof IFlexoOntologyConcept) {
				returned = getRootClass().isSuperConceptOf((IFlexoOntologyConcept) object);
			} else {
				returned = true;
			}
		}
		if (object instanceof IFlexoOntologyIndividual && showIndividuals) {
			if (getRootClass() != null && object instanceof IFlexoOntologyConcept) {
				returned = getRootClass().isSuperConceptOf((IFlexoOntologyConcept) object);
			} else {
				returned = true;
			}
		}
		if (object instanceof IFlexoOntologyObjectProperty && showObjectProperties) {
			returned = true;
		}
		if (object instanceof IFlexoOntologyDataProperty && showDataProperties) {
			returned = true;
		}
		if (object instanceof IFlexoOntologyStructuralProperty && ((IFlexoOntologyStructuralProperty) object).isAnnotationProperty()
				&& showAnnotationProperties) {
			returned = true;
		}

		if (returned == false) {
			return false;
		}

		if (object instanceof IFlexoOntologyStructuralProperty && getRootClass() != null) {
			boolean foundAPreferredLocationAsSubClassOfRootClass = false;
			List<IFlexoOntologyClass> preferredLocation = getPreferredStorageLocations((IFlexoOntologyStructuralProperty) object, null);
			for (IFlexoOntologyClass pl : preferredLocation) {
				if (rootClass.isSuperConceptOf(pl)) {
					foundAPreferredLocationAsSubClassOfRootClass = true;
					break;
				}
			}
			if (!foundAPreferredLocationAsSubClassOfRootClass) {
				return false;
			}
		}

		if (object instanceof IFlexoOntologyStructuralProperty && getDomain() != null) {
			IFlexoOntologyStructuralProperty p = (IFlexoOntologyStructuralProperty) object;
			if (p.getDomain() instanceof IFlexoOntologyClass) {
				if (!((IFlexoOntologyClass) p.getDomain()).isSuperClassOf(getDomain())) {
					/*System.out.println("Dismiss " + object + " becasuse " + p.getDomain().getName() + " is not superclass of "
							+ getDomain().getName());*/
					return false;
				}
				/*if (!getDomain().isSuperClassOf(((IFlexoOntologyClass) p.getDomain()))) {
					return false;
				}*/
			} else {
				// System.out.println("Dismiss " + object + " becasuse domain=" + p.getDomain());
				return false;
			}
		}

		if (object instanceof IFlexoOntologyObjectProperty && getRange() != null) {
			IFlexoOntologyObjectProperty p = (IFlexoOntologyObjectProperty) object;
			if (p.getRange() instanceof IFlexoOntologyClass) {
				if (!((IFlexoOntologyClass) p.getRange()).isSuperClassOf(getRange())) {
					return false;
				}
				/*if (!getRange().isSuperClassOf(((IFlexoOntologyClass) p.getRange()))) {
					return false;
				}*/
			} else {
				return false;
			}
		}

		if (object instanceof IFlexoOntologyDataProperty && getDataType() != null) {
			IFlexoOntologyDataProperty p = (IFlexoOntologyDataProperty) object;
			if (p.getRange() != getDataType()) {
				// System.out.println("Dismiss " + object + " becasuse " + p.getDataType() + " is not  " + getDataType());
				return false;
			}
		}

		return true;
	}

	private void appendOntologyContents(IFlexoOntology o, IFlexoOntologyObject parent) {
		List<IFlexoOntologyStructuralProperty> properties = new Vector<IFlexoOntologyStructuralProperty>();
		List<IFlexoOntologyIndividual> individuals = new Vector<IFlexoOntologyIndividual>();
		Hashtable<IFlexoOntologyStructuralProperty, List<IFlexoOntologyClass>> storedProperties = new Hashtable<IFlexoOntologyStructuralProperty, List<IFlexoOntologyClass>>();
		Hashtable<IFlexoOntologyIndividual, IFlexoOntologyClass> storedIndividuals = new Hashtable<IFlexoOntologyIndividual, IFlexoOntologyClass>();
		List<IFlexoOntologyStructuralProperty> unstoredProperties = new Vector<IFlexoOntologyStructuralProperty>();
		List<IFlexoOntologyIndividual> unstoredIndividuals = new Vector<IFlexoOntologyIndividual>();
		List<IFlexoOntologyClass> storageClasses = new Vector<IFlexoOntologyClass>();
		properties = retrieveDisplayableProperties(o);
		individuals = retrieveDisplayableIndividuals(o);

		if (getDisplayPropertiesInClasses()) {
			for (IFlexoOntologyStructuralProperty p : properties) {
				List<IFlexoOntologyClass> preferredLocations = getPreferredStorageLocations(p, o);
				if (preferredLocations != null && preferredLocations.size() > 0) {
					storedProperties.put(p, preferredLocations);
					for (IFlexoOntologyClass preferredLocation : preferredLocations) {
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
			for (IFlexoOntologyIndividual i : individuals) {
				IFlexoOntologyClass preferredLocation = getPreferredStorageLocation(i);
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
			List<IFlexoOntologyClass> classes = retrieveDisplayableClasses(o);
			if (classes.size() > 0) {
				removeOriginalFromRedefinedObjects(classes);
				addClassesAsHierarchy(parent == null ? null : o, classes);
			}
		} else if (getDisplayPropertiesInClasses() || showIndividuals) {
			removeOriginalFromRedefinedObjects(storageClasses);
			appendParentClassesToStorageClasses(storageClasses);
			addClassesAsHierarchy(parent == null ? null : o, storageClasses);
		}

		for (IFlexoOntologyIndividual i : storedIndividuals.keySet()) {
			IFlexoOntologyClass preferredLocation = storedIndividuals.get(i);
			addChildren(preferredLocation, i);
		}

		for (IFlexoOntologyIndividual i : unstoredIndividuals) {
			addChildren(parent == null ? null : o, i);
		}

		if (getDisplayPropertiesInClasses()) {
			for (IFlexoOntologyStructuralProperty p : storedProperties.keySet()) {
				List<IFlexoOntologyClass> preferredLocations = storedProperties.get(p);
				for (IFlexoOntologyClass preferredLocation : preferredLocations) {
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
			roots = new Vector<IFlexoOntologyObject>();
		}
		if (structure != null) {
			structure.clear();
		} else {
			structure = new Hashtable<AbstractOntologyObject, List<AbstractOntologyObject>>();
		}

		if (getContext() == null) {
			return;
		}

		if (strictMode) {

			appendOntologyContents(getContext(), null);

		} else {
			roots.add(getContext());
			appendOntologyContents(getContext(), getContext());
			for (IFlexoOntology o : OntologyUtils.getAllImportedOntologies(getContext())) {
				if (o != getContext() && isDisplayable(o)) {
					appendOntologyContents(o, getContext());
				}
			}
		}
	}

	private void addPropertiesAsHierarchy(IFlexoOntologyObject parent, List<IFlexoOntologyStructuralProperty> someProperties) {
		for (IFlexoOntologyStructuralProperty p : someProperties) {
			if (!hasASuperPropertyDefinedInList(p, someProperties)) {
				appendPropertyInHierarchy(parent, p, someProperties);
			}
		}
	}

	private void appendPropertyInHierarchy(IFlexoOntologyObject parent, IFlexoOntologyStructuralProperty p,
			List<IFlexoOntologyStructuralProperty> someProperties) {
		if (parent == null) {
			roots.add(p);
		} else {
			addChildren(parent, p);
		}
		for (IFlexoOntologyStructuralProperty subProperty : p.getSubProperties(getContext())) {
			if (someProperties.contains(subProperty)) {
				appendPropertyInHierarchy(p, subProperty, someProperties);
			}
		}
	}

	private boolean hasASuperPropertyDefinedInList(IFlexoOntologyStructuralProperty p, List<IFlexoOntologyStructuralProperty> someProperties) {
		if (p.getSuperProperties() == null) {
			return false;
		} else {
			for (IFlexoOntologyStructuralProperty sp : p.getSuperProperties()) {
				if (someProperties.contains(sp)) {
					return true;
				}
			}
			return false;
		}
	}

	private void addChildren(IFlexoOntologyObject parent, IFlexoOntologyObject child) {
		List<AbstractOntologyObject> v = structure.get(parent);
		if (v == null) {
			v = new Vector<AbstractOntologyObject>();
			structure.put((AbstractOntologyObject) parent, v);
		}
		if (!v.contains(child)) {
			v.add((AbstractOntologyObject) child);
		}
	}

	private void computeHierarchicalStructure() {

		logger.fine("computeHierarchicalStructure()");

		if (roots != null) {
			roots.clear();
		} else {
			roots = new Vector<IFlexoOntologyObject>();
		}
		if (structure != null) {
			structure.clear();
		} else {
			structure = new Hashtable<AbstractOntologyObject, List<AbstractOntologyObject>>();
		}

		List<IFlexoOntologyStructuralProperty> properties = new Vector<IFlexoOntologyStructuralProperty>();
		Hashtable<IFlexoOntologyStructuralProperty, List<IFlexoOntologyClass>> storedProperties = new Hashtable<IFlexoOntologyStructuralProperty, List<IFlexoOntologyClass>>();
		List<IFlexoOntologyStructuralProperty> unstoredProperties = new Vector<IFlexoOntologyStructuralProperty>();
		List<IFlexoOntologyClass> storageClasses = new Vector<IFlexoOntologyClass>();

		List<IFlexoOntologyIndividual> individuals = new Vector<IFlexoOntologyIndividual>();
		Hashtable<IFlexoOntologyIndividual, IFlexoOntologyClass> storedIndividuals = new Hashtable<IFlexoOntologyIndividual, IFlexoOntologyClass>();
		List<IFlexoOntologyIndividual> unstoredIndividuals = new Vector<IFlexoOntologyIndividual>();

		if (getContext() == null) {
			return;
		}

		if (strictMode) {
			properties = retrieveDisplayableProperties(getContext());
			individuals = retrieveDisplayableIndividuals(getContext());
		} else {
			for (IFlexoOntology o : OntologyUtils.getAllImportedOntologies(getContext())) {
				properties.addAll(retrieveDisplayableProperties(o));
				individuals.addAll(retrieveDisplayableIndividuals(o));
			}
		}
		if (getDisplayPropertiesInClasses()) {
			for (IFlexoOntologyStructuralProperty p : properties) {
				List<IFlexoOntologyClass> preferredLocations = getPreferredStorageLocations(p, null);
				if (preferredLocations != null && preferredLocations.size() > 0) {
					storedProperties.put(p, preferredLocations);
					for (IFlexoOntologyClass preferredLocation : preferredLocations) {
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
			for (IFlexoOntologyIndividual i : individuals) {
				IFlexoOntologyClass preferredLocation = getPreferredStorageLocation(i);
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
			List<IFlexoOntologyClass> classes = new Vector<IFlexoOntologyClass>();
			if (strictMode) {
				classes = retrieveDisplayableClasses(getContext());
			} else {
				for (IFlexoOntology o : OntologyUtils.getAllImportedOntologies(getContext())) {
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

		for (IFlexoOntologyIndividual i : storedIndividuals.keySet()) {
			IFlexoOntologyClass preferredLocation = storedIndividuals.get(i);
			addChildren(preferredLocation, i);
		}

		for (IFlexoOntologyIndividual i : unstoredIndividuals) {
			addChildren(getContext().getRootConcept(), i);
		}

		if (getDisplayPropertiesInClasses()) {
			for (IFlexoOntologyStructuralProperty p : storedProperties.keySet()) {
				List<IFlexoOntologyClass> preferredLocations = storedProperties.get(p);
				for (IFlexoOntologyClass preferredLocation : preferredLocations) {
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
	protected List<IFlexoOntologyClass> getPreferredStorageLocations(IFlexoOntologyStructuralProperty p, IFlexoOntology searchedOntology) {
		List<IFlexoOntologyClass> potentialStorageClasses = new ArrayList<IFlexoOntologyClass>();

		// First we look if property has a defined domain
		if (p.getDomain() instanceof IFlexoOntologyClass) {
			// Return the most specialized definition
			IFlexoOntologyClass c = (searchedOntology != null ? searchedOntology : getContext()).getClass(((IFlexoOntologyClass) p
					.getDomain()).getURI());
			if (c != null && (searchedOntology == null || c.getOntology() == searchedOntology)) {
				potentialStorageClasses.add(c);
				return potentialStorageClasses;
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

	private IFlexoOntologyClass getPreferredStorageLocation(IFlexoOntologyIndividual i) {

		// Return the first class which is not the Thing concept
		for (IFlexoOntologyClass c : i.getTypes()) {
			if (c.isNamedClass() && !c.isRootConcept()) {
				IFlexoOntologyClass returned = getContext().getClass(c.getURI());
				if (returned != null) {
					return returned;
				}
			}
		}
		return getContext().getRootConcept();
	}

	private void addClassesAsHierarchy(IFlexoOntologyObject parent, List<IFlexoOntologyClass> someClasses) {
		if (someClasses.contains(getContext().getRootConcept())) {
			appendClassInHierarchy(parent, getContext().getRootConcept(), someClasses);
		} else {
			List<IFlexoOntologyClass> listByExcludingRootClasses = new ArrayList<IFlexoOntologyClass>(someClasses);
			List<IFlexoOntologyClass> localRootClasses = new ArrayList<IFlexoOntologyClass>();
			for (IFlexoOntologyClass c : someClasses) {
				if (!hasASuperClassDefinedInList(c, someClasses)) {
					localRootClasses.add(c);
					listByExcludingRootClasses.remove(c);
				}
			}
			for (IFlexoOntologyClass c : localRootClasses) {
				List<IFlexoOntologyClass> potentialChildren = new ArrayList<IFlexoOntologyClass>();
				for (IFlexoOntologyClass c2 : listByExcludingRootClasses) {
					if (c.isSuperConceptOf(c2)) {
						potentialChildren.add(c2);
					}
				}
				appendClassInHierarchy(parent, c, potentialChildren);
			}
		}
	}

	private void appendClassInHierarchy(IFlexoOntologyObject parent, IFlexoOntologyClass c, List<IFlexoOntologyClass> someClasses) {

		List<IFlexoOntologyClass> listByExcludingCurrentClass = new ArrayList<IFlexoOntologyClass>(someClasses);
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

	private boolean hasASuperClassDefinedInList(IFlexoOntologyClass c, List<IFlexoOntologyClass> someClasses) {
		if (c.getSuperClasses() == null) {
			return false;
		} else {
			for (IFlexoOntologyClass c2 : someClasses) {
				if (c2.isSuperConceptOf(c) && c2 != c) {
					return true;
				}
			}

			return false;
		}
	}

	private List<IFlexoOntologyConcept> retrieveDisplayableObjects(IFlexoOntology ontology) {
		Vector<IFlexoOntologyConcept> returned = new Vector<IFlexoOntologyConcept>();
		for (IFlexoOntologyClass c : ontology.getClasses()) {
			if (isDisplayable(c)) {
				returned.add(c);
			}
		}
		for (IFlexoOntologyIndividual i : ontology.getIndividuals()) {
			if (isDisplayable(i)) {
				returned.add(i);
			}
		}
		for (IFlexoOntologyStructuralProperty p : ontology.getObjectProperties()) {
			if (isDisplayable(p)) {
				returned.add(p);
			}
		}
		for (IFlexoOntologyStructuralProperty p : ontology.getDataProperties()) {
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
	protected void removeOriginalFromRedefinedObjects(List<? extends IFlexoOntologyConcept> list) {
		for (IFlexoOntologyConcept c : new ArrayList<IFlexoOntologyConcept>(list)) {
			if (c instanceof IFlexoOntologyClass && ((IFlexoOntologyClass) c).isRootConcept() && c.getOntology() != getContext()
					&& list.contains(getContext().getRootConcept())) {
				list.remove(c);
			}
		}
	}

	private void appendParentClassesToStorageClasses(List<IFlexoOntologyClass> someClasses) {
		// System.out.println("appendParentClassesToStorageClasses with " + someClasses);

		// First compute the list of all top-level classes
		List<IFlexoOntologyClass> topLevelClasses = new ArrayList<IFlexoOntologyClass>();
		for (IFlexoOntologyClass c : someClasses) {
			boolean requireAddInTopClasses = true;
			List<IFlexoOntologyClass> classesToRemove = new ArrayList<IFlexoOntologyClass>();
			for (IFlexoOntologyClass tpC : topLevelClasses) {
				if (tpC.isSuperClassOf(c)) {
					requireAddInTopClasses = false;
				}
				if (c.isSuperClassOf(tpC)) {
					classesToRemove.add(tpC);
				}
			}
			if (requireAddInTopClasses) {
				topLevelClasses.add(c);
				for (IFlexoOntologyClass c2r : classesToRemove) {
					topLevelClasses.remove(c2r);
				}
			}
		}

		List<IFlexoOntologyClass> classesToAdd = new ArrayList<IFlexoOntologyClass>();
		if (someClasses.size() > 1) {
			for (int i = 0; i < topLevelClasses.size(); i++) {
				for (int j = i + 1; j < topLevelClasses.size(); j++) {
					// System.out.println("i=" + i + " j=" + j + " someClasses.size()=" + someClasses.size());
					// System.out.println("i=" + i + " j=" + j + " someClasses.size()=" + someClasses.size() + " someClasses=" +
					// someClasses);
					IFlexoOntologyClass c1 = topLevelClasses.get(i);
					IFlexoOntologyClass c2 = topLevelClasses.get(j);
					IFlexoOntologyClass ancestor = OntologyUtils.getFirstCommonAncestor(c1, c2);
					// System.out.println("Ancestor of " + c1 + " and " + c2 + " is " + ancestor);
					if (ancestor != null /*&& !ancestor.isThing()*/) {
						IFlexoOntologyClass ancestorSeenFromContextOntology = getContext().getClass(ancestor.getURI());
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
				for (IFlexoOntologyClass c : classesToAdd) {
					someClasses.add(c);
				}

				// Do it again whenever there are classes to add
				appendParentClassesToStorageClasses(someClasses);
			}
		}
	}

	/*private void removeOriginalFromRedefinedClasses(List<IFlexoOntologyClass> list) {
		// Remove originals from redefined classes
		for (IFlexoOntologyClass c : new ArrayList<IFlexoOntologyClass>(list)) {
			if (c.redefinesOriginalDefinition()) {
				list.remove(c.getOriginalDefinition());
			}
		}
	}*/

	private List<IFlexoOntologyClass> retrieveDisplayableClasses(IFlexoOntology ontology) {
		Vector<IFlexoOntologyClass> returned = new Vector<IFlexoOntologyClass>();
		for (IFlexoOntologyClass c : ontology.getClasses()) {
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

	private List<IFlexoOntologyIndividual> retrieveDisplayableIndividuals(IFlexoOntology ontology) {
		Vector<IFlexoOntologyIndividual> returned = new Vector<IFlexoOntologyIndividual>();
		for (IFlexoOntologyIndividual c : ontology.getIndividuals()) {
			if (isDisplayable(c)) {
				returned.add(c);
			}
		}
		return returned;
	}

	private List<IFlexoOntologyStructuralProperty> retrieveDisplayableProperties(IFlexoOntology ontology) {
		Vector<IFlexoOntologyStructuralProperty> returned = new Vector<IFlexoOntologyStructuralProperty>();
		for (IFlexoOntologyStructuralProperty p : ontology.getObjectProperties()) {
			if (isDisplayable(p)) {
				returned.add(p);
			}
		}
		for (IFlexoOntologyStructuralProperty p : ontology.getDataProperties()) {
			if (isDisplayable(p)) {
				returned.add(p);
			}
		}
		return returned;
	}

	public Font getFont(IFlexoOntologyConcept object, Font baseFont) {
		if (object.getOntology() != getContext()) {
			return baseFont.deriveFont(Font.ITALIC);
		}
		return baseFont;
	}

}
