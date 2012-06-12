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

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologicDataType;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.module.FlexoResourceCenterService;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.FlexoFIBController;

/**
 * Widget allowing to select an OntologyProperty.<br>
 * 
 * This widget provides many configuration options:
 * <ul>
 * <li>context: required, defines ontology context</li>
 * <li>strictMode: required, default is false, when true, indicates that properties are retrieved from declared context ontology only</li>
 * <li>hierarchicalMode: required, default is true, defines if properties are stored relative to a storage class, defined either as the
 * domain class, or the top-level class where this property is used as a restriction</li>
 * <li>rootClass: when set, defines top-level class used as storage location, available in hierarchical mode only</li>
 * <li>domain: when set, defines the domain class, properties not declared having a domain are excluded from this selector</li>
 * <li>range: when set, defines the range class for object properties, properties not declared having a range are excluded from this
 * selector</li>
 * <li>dataType: when set, defines the dataType for data properties, properties not declared having a datatype are excluded from this
 * selector</li>
 * <li>selectObjectProperties, indicated if object properties should be retrieved</li>
 * <li>selectDataProperties, indicated if data properties should be retrieved</li>
 * <li>selectAnnotationProperties, indicated if annotation properties should be retrieved</li>
 * </ul>
 * 
 * @author sguerin
 * 
 */
public class FIBPropertySelector extends FIBModelObjectSelector<OntologyProperty> {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBPropertySelector.class.getPackage().getName());

	public static final FileResource FIB_FILE = new FileResource("Fib/PropertySelector.fib");

	private FlexoOntology context;
	private OntologyClass rootClass;
	private OntologyClass domain;
	private OntologyClass range;
	private OntologicDataType dataType;
	private boolean hierarchicalMode = true;
	private boolean selectObjectProperties = true;
	private boolean selectDataProperties = true;
	private boolean selectAnnotationProperties = false;
	private boolean strictMode = false;

	public FIBPropertySelector(OntologyProperty editedObject) {
		super(editedObject);
	}

	@Override
	public void delete() {
		super.delete();
		context = null;
		domain = null;
		range = null;
		range = null;
	}

	@Override
	public File getFIBFile() {
		return FIB_FILE;
	}

	@Override
	public Class<OntologyProperty> getRepresentedType() {
		return OntologyProperty.class;
	}

	@Override
	public String renderedString(OntologyProperty editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return "";
	}

	/**
	 * This method must be implemented if we want to implement completion<br>
	 * Completion will be performed on that selectable values<br>
	 * Return all viewpoints of this library
	 */
	@Override
	protected Enumeration<OntologyProperty> getAllSelectableValues() {
		return super.getAllSelectableValues();
	}

	public FlexoOntology getContext() {
		return context;
	}

	@CustomComponentParameter(name = "context", type = CustomComponentParameter.Type.MANDATORY)
	public void setContext(FlexoOntology context) {
		this.context = context;
	}

	public OntologyClass getRootClass() {
		return rootClass;
	}

	public void setRootClass(OntologyClass rootClass) {
		this.rootClass = rootClass;
	}

	public OntologyClass getDomain() {
		return domain;
	}

	@CustomComponentParameter(name = "domain", type = CustomComponentParameter.Type.OPTIONAL)
	public void setDomain(OntologyClass domain) {
		this.domain = domain;
	}

	public OntologyClass getRange() {
		return range;
	}

	@CustomComponentParameter(name = "range", type = CustomComponentParameter.Type.OPTIONAL)
	public void setRange(OntologyClass range) {
		this.range = range;
	}

	public OntologicDataType getDataType() {
		return dataType;
	}

	@CustomComponentParameter(name = "dataType", type = CustomComponentParameter.Type.OPTIONAL)
	public void setDataType(OntologicDataType dataType) {
		this.dataType = dataType;
	}

	public boolean getHierarchicalMode() {
		return hierarchicalMode;
	}

	@CustomComponentParameter(name = "hierarchicalMode", type = CustomComponentParameter.Type.OPTIONAL)
	public void setHierarchicalMode(boolean hierarchicalMode) {
		this.hierarchicalMode = hierarchicalMode;
	}

	public boolean getSelectObjectProperties() {
		return selectObjectProperties;
	}

	@CustomComponentParameter(name = "selectObjectProperties", type = CustomComponentParameter.Type.OPTIONAL)
	public void setSelectObjectProperties(boolean selectObjectProperties) {
		this.selectObjectProperties = selectObjectProperties;
	}

	public boolean getSelectDataProperties() {
		return selectDataProperties;
	}

	@CustomComponentParameter(name = "selectDataProperties", type = CustomComponentParameter.Type.OPTIONAL)
	public void setSelectDataProperties(boolean selectDataProperties) {
		this.selectDataProperties = selectDataProperties;
	}

	public boolean getSelectAnnotationProperties() {
		return selectAnnotationProperties;
	}

	@CustomComponentParameter(name = "selectAnnotationProperties", type = CustomComponentParameter.Type.OPTIONAL)
	public void setSelectAnnotationProperties(boolean selectAnnotationProperties) {
		this.selectAnnotationProperties = selectAnnotationProperties;
	}

	public boolean getStrictMode() {
		return strictMode;
	}

	@CustomComponentParameter(name = "strictMode", type = CustomComponentParameter.Type.OPTIONAL)
	public void setStrictMode(boolean strictMode) {
		this.strictMode = strictMode;
	}

	// Please uncomment this for a live test
	// Never commit this uncommented since it will not compile on continuous build
	// To have icon, you need to choose "Test interface" in the editor (otherwise, flexo controller is not insanciated in EDIT mode)
	public static void main(String[] args) {
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				FlexoResourceCenter resourceCenter = FlexoResourceCenterService.instance().getFlexoResourceCenter();
				FIBPropertySelector selector = new FIBPropertySelector(null);
				// selector.setContext(resourceCenter.retrieveBaseOntologyLibrary().getFlexoConceptOntology());
				FlexoOntology o = resourceCenter.retrieveBaseOntologyLibrary().getOntology(
				// "http://www.thalesgroup.com/ontologies/sepel-ng/MappingSpecifications.owl");
						"http://www.cpmf.org/ontologies/cpmfInstance");
				o.loadWhenUnloaded();
				selector.setContext(o);
				selector.setHierarchicalMode(true); // false
				selector.setSelectAnnotationProperties(true);
				selector.setSelectObjectProperties(true);
				selector.setSelectDataProperties(true);
				selector.setStrictMode(true);
				/*OntologyClass transformationRule = resourceCenter.retrieveBaseOntologyLibrary().getClass(
						"http://www.thalesgroup.com/ontologies/sepel-ng/MappingSpecifications.owl#TransformationRule");*/
				// selector.setRootClass(transformationRule);
				return makeArray(selector);
			}

			@Override
			public File getFIBFile() {
				return FIB_FILE;
			}

			@Override
			public FIBController makeNewController(FIBComponent component) {
				return new FlexoFIBController<FIBViewPointSelector>(component);
			}
		};
		editor.launch();
	}

	private Vector<OntologyObject<?>> roots = null;
	private Hashtable<OntologyObject<?>, Vector<OntologyObject<?>>> structure = null;

	public List<OntologyObject<?>> getRoots() {
		if (roots == null) {
			recomputeStructure();
		}
		return roots;
	}

	public List<OntologyObject<?>> getChildren(OntologyObject<?> father) {
		return structure.get(father);
	}

	private void recomputeStructure() {
		if (getHierarchicalMode()) {
			computeHierarchicalStructure();
		} else {
			computeNonHierarchicalStructure();
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
			List<OntologyProperty> properties = retrieveSelectableProperties(getContext());
			if (properties.size() > 0) {
				roots.add(getContext());
				addPropertiesAsHierarchy(getContext(), properties);
			}
		} else {
			for (FlexoOntology o : getContext().getAllImportedOntologies()) {
				List<OntologyProperty> properties = retrieveSelectableProperties(o);
				if (properties.size() > 0) {
					roots.add(o);
					addPropertiesAsHierarchy(o, properties);
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
			properties = retrieveSelectableProperties(getContext());
		} else {
			for (FlexoOntology o : getContext().getAllImportedOntologies()) {
				properties.addAll(retrieveSelectableProperties(o));
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

		addClassesAsHierarchy(null, storageClasses);

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

	private boolean isSelectable(OntologyProperty p) {
		boolean returned = false;
		if (p instanceof OntologyObjectProperty && selectObjectProperties) {
			returned = true;
		}
		if (p instanceof OntologyDataProperty && selectDataProperties) {
			returned = true;
		}
		if (p.isAnnotationProperty() && selectAnnotationProperties) {
			returned = true;
		}

		if (returned == false) {
			return false;
		}

		if (getDomain() != null && p.getDomain() != null) {
			if (!getDomain().isSuperConceptOf(p.getDomain())) {
				return false;
			}
		}

		if (getRange() != null && p.getRange() != null) {
			if (!getRange().isSuperConceptOf(p.getRange())) {
				return false;
			}
		}

		if (getDataType() != null && p instanceof OntologyDataProperty && ((OntologyDataProperty) p).getDataType() != null) {
			if (getDataType() != ((OntologyDataProperty) p).getDataType()) {
				return false;
			}
		}

		if (getRootClass() != null && getHierarchicalMode()) {
			OntologyClass preferredLocation = getPreferredStorageLocation(p);
			if (preferredLocation == null)
				return false;
			if (!getRootClass().isSuperConceptOf(preferredLocation))
				return false;
		}

		return true;
	}

	private List<OntologyProperty> retrieveSelectableProperties(FlexoOntology ontology) {
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

}