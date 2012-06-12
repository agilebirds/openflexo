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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.widget.DefaultFIBCustomComponent;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoResourceCenterService;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.controller.FlexoFIBController;

/**
 * Widget allowing to browse an ontology.<br>
 * 
 * This widget provides many configuration options:
 * <ul>
 * <li>context: required, defines ontology to browse</li>
 * <li>strictMode: required, default is false, when true, indicates that properties are retrieved from declared context ontology only</li>
 * <li>hierarchicalMode: required, default is true, defines if properties are stored relative to a storage class, defined either as the
 * domain class, or the top-level class where this property is used as a restriction</li>
 * <li>rootClass: when set, defines top-level class used as storage location, available in hierarchical mode only</li>
 * </ul>
 * 
 * @author sguerin
 * 
 */
public class FIBOntologyBrowser extends DefaultFIBCustomComponent<FIBOntologyBrowser> {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBOntologyBrowser.class.getPackage().getName());

	public static final FileResource FIB_FILE = new FileResource("Fib/OntologyBrowser.fib");

	private FlexoOntology ontology;
	private boolean hierarchicalMode = true;
	private boolean strictMode = false;
	private OntologyClass rootClass;

	private boolean showObjectProperties = true;
	private boolean showDataProperties = true;
	private boolean showAnnotationProperties = true;
	private boolean showClasses = true;
	private boolean showIndividuals = true;

	private boolean allowsSearch = true;

	private OntologyBrowserModel model = null;

	private boolean isSearching = false;
	private String filteredName;
	private List<OntologyObject<?>> matchingValues;
	private OntologyObject selectedValue;

	public FIBOntologyBrowser(FlexoOntology ontology) {
		super(FIB_FILE, null, FlexoLocalization.getMainLocalizer());
		matchingValues = new ArrayList<OntologyObject<?>>();
		setOntology(ontology);
		setEditedObject(this);
	}

	public FlexoOntology getOntology() {
		return ontology;
	}

	@CustomComponentParameter(name = "ontology", type = CustomComponentParameter.Type.MANDATORY)
	public void setOntology(FlexoOntology context) {
		this.ontology = context;
		update();
	}

	public boolean getStrictMode() {
		return strictMode;
	}

	@CustomComponentParameter(name = "strictMode", type = CustomComponentParameter.Type.OPTIONAL)
	public void setStrictMode(boolean strictMode) {
		this.strictMode = strictMode;
	}

	public boolean getHierarchicalMode() {
		return hierarchicalMode;
	}

	@CustomComponentParameter(name = "hierarchicalMode", type = CustomComponentParameter.Type.OPTIONAL)
	public void setHierarchicalMode(boolean hierarchicalMode) {
		this.hierarchicalMode = hierarchicalMode;
	}

	public OntologyClass getRootClass() {
		return rootClass;
	}

	@CustomComponentParameter(name = "rootClass", type = CustomComponentParameter.Type.OPTIONAL)
	public void setRootClass(OntologyClass rootClass) {
		this.rootClass = rootClass;
	}

	public boolean getAllowsSearch() {
		return allowsSearch;
	}

	@CustomComponentParameter(name = "allowsSearch", type = CustomComponentParameter.Type.OPTIONAL)
	public void setAllowsSearch(boolean allowsSearch) {
		this.allowsSearch = allowsSearch;
	}

	public boolean getShowObjectProperties() {
		return showObjectProperties;
	}

	@CustomComponentParameter(name = "showObjectProperties", type = CustomComponentParameter.Type.OPTIONAL)
	public void setShowObjectProperties(boolean showObjectProperties) {
		this.showObjectProperties = showObjectProperties;
	}

	public boolean getShowDataProperties() {
		return showDataProperties;
	}

	@CustomComponentParameter(name = "showDataProperties", type = CustomComponentParameter.Type.OPTIONAL)
	public void setShowDataProperties(boolean showDataProperties) {
		this.showDataProperties = showDataProperties;
	}

	public boolean getShowAnnotationProperties() {
		return showAnnotationProperties;
	}

	@CustomComponentParameter(name = "showAnnotationProperties", type = CustomComponentParameter.Type.OPTIONAL)
	public void setShowAnnotationProperties(boolean showAnnotationProperties) {
		this.showAnnotationProperties = showAnnotationProperties;
	}

	public boolean getShowClasses() {
		return showClasses;
	}

	@CustomComponentParameter(name = "showClasses", type = CustomComponentParameter.Type.OPTIONAL)
	public void setShowClasses(boolean showClasses) {
		this.showClasses = showClasses;
	}

	public boolean getShowIndividuals() {
		return showIndividuals;
	}

	@CustomComponentParameter(name = "showIndividuals", type = CustomComponentParameter.Type.OPTIONAL)
	public void setShowIndividuals(boolean showIndividuals) {
		this.showIndividuals = showIndividuals;
	}

	public OntologyBrowserModel getModel() {
		if (model == null) {
			model = new OntologyBrowserModel(getOntology());
			model.setHierarchicalMode(getHierarchicalMode());
			model.setStrictMode(getStrictMode());
			model.setRootClass(getRootClass());
			model.setShowClasses(getShowClasses());
			model.setShowIndividuals(getShowIndividuals());
			model.setShowObjectProperties(getShowObjectProperties());
			model.setShowDataProperties(getShowDataProperties());
			model.setShowAnnotationProperties(getShowAnnotationProperties());
			model.recomputeStructure();
		}
		return model;
	}

	public void update() {
		if (model != null) {
			model.delete();
			model = null;
		}
		setEditedObject(this);
	}

	// Please uncomment this for a live test
	// Never commit this uncommented since it will not compile on continuous build
	// To have icon, you need to choose "Test interface" in the editor (otherwise, flexo controller is not insanciated in EDIT mode)
	public static void main(String[] args) {
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				FlexoResourceCenter resourceCenter = FlexoResourceCenterService.instance().getFlexoResourceCenter();
				// selector.setContext(resourceCenter.retrieveBaseOntologyLibrary().getFlexoConceptOntology());
				FlexoOntology o = resourceCenter.retrieveBaseOntologyLibrary().getOntology(
				// "http://www.thalesgroup.com/ontologies/sepel-ng/MappingSpecifications.owl");
						"http://www.cpmf.org/ontologies/cpmfInstance");
				o.loadWhenUnloaded();
				FIBOntologyBrowser selector = new FIBOntologyBrowser(o);
				selector.setOntology(o);
				selector.setHierarchicalMode(false); // false
				selector.setShowAnnotationProperties(true);
				selector.setShowObjectProperties(true);
				selector.setShowDataProperties(true);
				selector.setShowClasses(true);
				selector.setShowIndividuals(true);
				selector.setStrictMode(false);
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

	public static void main2(String[] args) {
		JFrame frame = new JFrame();
		FlexoResourceCenter resourceCenter = FlexoResourceCenterService.instance().getFlexoResourceCenter();
		// selector.setContext(resourceCenter.retrieveBaseOntologyLibrary().getFlexoConceptOntology());
		FlexoOntology o = resourceCenter.retrieveBaseOntologyLibrary().getOntology(
				"http://www.thalesgroup.com/ontologies/sepel-ng/MappingSpecifications.owl");
		// "http://www.cpmf.org/ontologies/cpmfInstance");
		o.loadWhenUnloaded();
		System.out.println("ontology: " + o);
		FIBOntologyBrowser browser = new FIBOntologyBrowser(o);
		browser.setOntology(o);
		frame.getContentPane().add(browser);
		frame.pack();
		frame.setVisible(true);
	}

	public String getFilteredName() {
		return filteredName;
	}

	public void setFilteredName(String filteredName) {
		this.filteredName = filteredName;
	}

	public List<OntologyObject<?>> getMatchingValues() {
		return matchingValues;
	}

	public void search() {
		if (StringUtils.isNotEmpty(getFilteredName())) {
			logger.info("Searching " + getFilteredName());
			matchingValues.clear();
			for (OntologyObject o : getAllSelectableValues()) {
				if (o.getName().indexOf(getFilteredName()) > -1) {
					if (!matchingValues.contains(o)) {
						matchingValues.add(o);
					}
				}
			}

			isSearching = true;
			getPropertyChangeSupport().firePropertyChange("isSearching", false, true);
			getPropertyChangeSupport().firePropertyChange("matchingValues", null, matchingValues);

			if (matchingValues.size() == 1) {
				selectValue(matchingValues.get(0));
			}
		}

	}

	public void dismissSearch() {
		logger.info("Dismiss search");

		isSearching = false;
		getPropertyChangeSupport().firePropertyChange("isSearching", true, false);
	}

	public boolean isSearching() {
		return isSearching;
	}

	public ImageIcon getCancelIcon() {
		return UtilsIconLibrary.CANCEL_ICON;
	}

	public ImageIcon getSearchIcon() {
		return UtilsIconLibrary.SEARCH_ICON;
	}

	public String getButtonText() {
		if (isSearching()) {
			return "done";
		} else {
			return "search";
		}
	}

	@Override
	public Class<FIBOntologyBrowser> getRepresentedType() {
		return FIBOntologyBrowser.class;
	}

	/**
	 * This method is used to retrieve all potential values when implementing completion<br>
	 * Completion will be performed on that selectable values<br>
	 * Default implementation is to iterate on all values of browser, please take care to infinite loops.<br>
	 * 
	 * Override when required
	 */
	protected Vector<OntologyObject> getAllSelectableValues() {
		Vector<OntologyObject> returned = new Vector<OntologyObject>();
		FIBBrowserWidget browserWidget = retrieveFIBBrowserWidget();
		if (browserWidget == null) {
			return null;
		}
		Iterator<Object> it = browserWidget.getBrowserModel().retrieveContents();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof OntologyObject) {
				returned.add((OntologyObject) o);
			}
		}
		return returned;
	}

	private FIBBrowserWidget retrieveFIBBrowserWidget() {
		List<FIBComponent> listComponent = getFIBComponent().retrieveAllSubComponents();
		for (FIBComponent c : listComponent) {
			if (c instanceof FIBBrowser) {
				return (FIBBrowserWidget) getController().viewForComponent(c);
			}
		}
		return null;
	}

	public OntologyObject getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(OntologyObject selectedValue) {
		OntologyObject oldSelected = this.selectedValue;
		this.selectedValue = selectedValue;
		getPropertyChangeSupport().firePropertyChange("selectedValue", oldSelected, selectedValue);
	}

	public void selectValue(OntologyObject selectedValue) {
		getController().selectionCleared();
		getController().objectAddedToSelection(selectedValue);
	}

}