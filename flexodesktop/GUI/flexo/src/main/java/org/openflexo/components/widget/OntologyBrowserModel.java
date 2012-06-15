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
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;

/**
 * Model supporting browsing inside ontologies<br>
 * 
 * Developers note: this model is shared by many widgets. Please modify it with caution.
 * 
 * @author sguerin
 * 
 */
public class OntologyBrowserModel {

	static final Logger logger = Logger.getLogger(OntologyBrowserModel.class.getPackage().getName());

	private FlexoOntology context;
	private boolean hierarchicalMode = true;
	private boolean strictMode = false;
	private OntologyClass rootClass;
	private boolean displayPropertiesInClasses = true;

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
		this.context = context;
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

	public boolean isSelectable(OntologyObject p) {
		boolean returned = false;
		if (p instanceof OntologyClass && showClasses) {
			returned = true;
		}
		if (p instanceof OntologyIndividual && showIndividuals) {
			returned = true;
		}
		if (p instanceof OntologyObjectProperty && showObjectProperties) {
			returned = true;
		}
		if (p instanceof OntologyDataProperty && showDataProperties) {
			returned = true;
		}
		if (p instanceof OntologyProperty && ((OntologyProperty) p).isAnnotationProperty() && showAnnotationProperties) {
			returned = true;
		}

		if (returned == false) {
			return false;
		}

		return true;
	}

	private void appendOntologyContents(FlexoOntology o, OntologyObject<?> parent) {
		List<OntologyProperty> properties = new Vector<OntologyProperty>();
		Hashtable<OntologyProperty, OntologyClass> storedProperties = new Hashtable<OntologyProperty, OntologyClass>();
		List<OntologyProperty> unstoredProperties = new Vector<OntologyProperty>();
		List<OntologyClass> storageClasses = new Vector<OntologyClass>();
		properties = retrieveDisplayableProperties(o);

		if (getDisplayPropertiesInClasses()) {
			for (OntologyProperty p : properties) {
				OntologyClass preferredLocation = getPreferredStorageLocation(p);
				if (preferredLocation != null) {
					storedProperties.put(p, preferredLocation);
					if (!storageClasses.contains(preferredLocation)) {
						storageClasses.add(preferredLocation);
					}
				} else {
					unstoredProperties.add(p);
				}
			}
		}

		if (parent != null) {
			addChildren(parent, o);
		}

		if (showClasses) {
			List<OntologyClass> classes = retrieveDisplayableClasses(o);
			if (classes.size() > 0) {
				addClassesAsHierarchy(parent == null ? null : o, classes);
			}
		} else if (getDisplayPropertiesInClasses()) {
			addClassesAsHierarchy(parent == null ? null : o, storageClasses);
		}

		if (getDisplayPropertiesInClasses()) {
			for (OntologyProperty p : storedProperties.keySet()) {
				OntologyClass preferredLocation = storedProperties.get(p);
				addChildren(preferredLocation, p);
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
		if (strictMode) {

			/*List<OntologyProperty> properties = new Vector<OntologyProperty>();
			Hashtable<OntologyProperty, OntologyClass> storedProperties = new Hashtable<OntologyProperty, OntologyClass>();
			List<OntologyProperty> unstoredProperties = new Vector<OntologyProperty>();
			List<OntologyClass> storageClasses = new Vector<OntologyClass>();
			properties = retrieveDisplayableProperties(getContext());

			for (OntologyProperty p : properties) {
				OntologyClass preferredLocation = getPreferredStorageLocation(p);
				if (preferredLocation != null) {
					storedProperties.put(p, preferredLocation);
					if (!storageClasses.contains(preferredLocation)) {
						storageClasses.add(preferredLocation);
					}
				} else {
					unstoredProperties.add(p);
				}
			}

			roots.add(getContext());

			if (showClasses) {
				List<OntologyClass> classes = retrieveDisplayableClasses(getContext());
				if (classes.size() > 0) {
					addClassesAsHierarchy(getContext(), classes);
				}
			} else {
				addClassesAsHierarchy(getContext(), storageClasses);
			}

			for (OntologyProperty p : storedProperties.keySet()) {
				OntologyClass preferredLocation = storedProperties.get(p);
				addChildren(preferredLocation, p);
			}

			addPropertiesAsHierarchy(null, unstoredProperties);*/

			appendOntologyContents(getContext(), null);

		} else {
			/*for (FlexoOntology o : getContext().getAllImportedOntologies()) {
				if (showClasses) {
					List<OntologyClass> classes = retrieveDisplayableClasses(o);
					if (classes.size() > 0) {
						roots.add(o);
						addClassesAsHierarchy(o, classes);
					}
				}
				List<OntologyProperty> properties = retrieveDisplayableProperties(o);
				if (properties.size() > 0) {
					roots.add(o);
					addPropertiesAsHierarchy(o, properties);
				}
			}*/
			roots.add(getContext());
			for (FlexoOntology o : getContext().getAllImportedOntologies()) {
				appendOntologyContents(o, getContext());
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

		logger.info("computeHierarchicalStructure()");

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
		Hashtable<OntologyProperty, OntologyClass> storedProperties = new Hashtable<OntologyProperty, OntologyClass>();
		List<OntologyProperty> unstoredProperties = new Vector<OntologyProperty>();
		List<OntologyClass> storageClasses = new Vector<OntologyClass>();
		if (strictMode) {
			properties = retrieveDisplayableProperties(getContext());
		} else {
			for (FlexoOntology o : getContext().getAllImportedOntologies()) {
				properties.addAll(retrieveDisplayableProperties(o));
			}
		}

		for (OntologyProperty p : properties) {
			OntologyClass preferredLocation = getPreferredStorageLocation(p);
			if (preferredLocation != null) {
				storedProperties.put(p, preferredLocation);
				if (!storageClasses.contains(preferredLocation)) {
					storageClasses.add(preferredLocation);
				}
			} else {
				unstoredProperties.add(p);
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
			addClassesAsHierarchy(null, classes);
		} else {
			addClassesAsHierarchy(null, storageClasses);
		}

		for (OntologyProperty p : storedProperties.keySet()) {
			OntologyClass preferredLocation = storedProperties.get(p);
			addChildren(preferredLocation, p);
		}

		addPropertiesAsHierarchy(null, unstoredProperties);
	}

	private OntologyClass getPreferredStorageLocation(OntologyProperty p) {
		if (p.getDomain() instanceof OntologyClass) {
			return (OntologyClass) p.getDomain();
		}

		/*if (p.getStorageLocations().size() > 0) {
			return p.getStorageLocations().get(0);
		}*/
		return null;
	}

	private void addClassesAsHierarchy(OntologyObject<?> parent, List<OntologyClass> someClasses) {
		for (OntologyClass c : someClasses) {
			if (!hasASuperClassDefinedInList(c, someClasses)) {
				appendClassInHierarchy(parent, c, someClasses);
			}
		}
	}

	private void appendClassInHierarchy(OntologyObject<?> parent, OntologyClass c, List<OntologyClass> someClasses) {
		if (parent == null) {
			roots.add(c);
		} else {
			addChildren(parent, c);
		}
		for (OntologyClass subClass : c.getSubClasses()) {
			if (someClasses.contains(subClass)) {
				appendClassInHierarchy(c, subClass, someClasses);
			}
		}
	}

	private boolean hasASuperClassDefinedInList(OntologyClass c, List<OntologyClass> someClasses) {
		if (c.getSuperClasses() == null) {
			return false;
		} else {
			for (OntologyClass superClass : c.getSuperClasses()) {
				if (someClasses.contains(superClass)) {
					return true;
				}
			}
			return false;
		}
	}

	private List<OntologyObject> retrieveDisplayableObjects(FlexoOntology ontology) {
		Vector<OntologyObject> returned = new Vector<OntologyObject>();
		for (OntologyClass c : ontology.getClasses()) {
			if (isSelectable(c)) {
				returned.add(c);
			}
		}
		for (OntologyIndividual i : ontology.getIndividuals()) {
			if (isSelectable(i)) {
				returned.add(i);
			}
		}
		for (OntologyProperty p : ontology.getObjectProperties()) {
			if (isSelectable(p)) {
				returned.add(p);
			}
		}
		for (OntologyProperty p : ontology.getDataProperties()) {
			if (isSelectable(p)) {
				returned.add(p);
			}
		}
		return returned;
	}

	private List<OntologyClass> retrieveDisplayableClasses(FlexoOntology ontology) {
		Vector<OntologyClass> returned = new Vector<OntologyClass>();
		for (OntologyClass c : ontology.getClasses()) {
			if (isSelectable(c)) {
				returned.add(c);
			}
		}
		return returned;
	}

	private List<OntologyIndividual> retrieveDisplayableIndividuals(FlexoOntology ontology) {
		Vector<OntologyIndividual> returned = new Vector<OntologyIndividual>();
		for (OntologyIndividual c : ontology.getIndividuals()) {
			if (isSelectable(c)) {
				returned.add(c);
			}
		}
		return returned;
	}

	private List<OntologyProperty> retrieveDisplayableProperties(FlexoOntology ontology) {
		Vector<OntologyProperty> returned = new Vector<OntologyProperty>();
		for (OntologyProperty p : ontology.getObjectProperties()) {
			if (isSelectable(p)) {
				returned.add(p);
			}
		}
		for (OntologyProperty p : ontology.getDataProperties()) {
			if (isSelectable(p)) {
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