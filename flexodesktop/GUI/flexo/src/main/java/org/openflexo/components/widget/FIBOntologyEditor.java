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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent.CustomComponentParameter;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.SelectionSynchronizedFIBView;
import org.openflexo.view.controller.FlexoController;

/**
 * Widget allowing to edit/view an ontology.<br>
 * 
 * @author sguerin
 * 
 */
public class FIBOntologyEditor extends SelectionSynchronizedFIBView {
	static final Logger logger = Logger.getLogger(FIBOntologyEditor.class.getPackage().getName());

	public static final FileResource FIB_FILE = new FileResource("Fib/FIBOntologyEditor.fib");

	private FlexoOntology ontology;
	private boolean hierarchicalMode = true;
	private boolean strictMode = false;
	private OntologyClass rootClass;
	private boolean displayPropertiesInClasses = true;

	private boolean showObjectProperties = true;
	private boolean showDataProperties = true;
	private boolean showAnnotationProperties = true;
	private boolean showClasses = true;
	private boolean showIndividuals = true;

	private boolean showOWLAndRDFConcepts = false;

	private boolean allowsSearch = true;
	private boolean displayOptions = true;

	private OntologyBrowserModel model = null;

	private boolean isSearching = false;
	private String filteredName;
	private List<OntologyObject> matchingValues;
	private OntologyObject selectedValue;

	public FIBOntologyEditor(FlexoOntology ontology, FlexoController controller) {
		super(null, controller, FIB_FILE);
		matchingValues = new ArrayList<OntologyObject>();
		setOntology(ontology);
		setDataObject(this);
	}

	public FlexoOntology getOntology() {
		return ontology;
	}

	@CustomComponentParameter(name = "ontology", type = CustomComponentParameter.Type.MANDATORY)
	public void setOntology(FlexoOntology context) {
		if (this.ontology instanceof HasPropertyChangeSupport && ((HasPropertyChangeSupport) ontology).getDeletedProperty() != null) {
			manager.removeListener(((HasPropertyChangeSupport) ontology).getDeletedProperty(), this,
					(HasPropertyChangeSupport) this.ontology);
		}
		this.ontology = context;
		if (this.ontology instanceof HasPropertyChangeSupport && ((HasPropertyChangeSupport) ontology).getDeletedProperty() != null) {
			manager.addListener(((HasPropertyChangeSupport) ontology).getDeletedProperty(), this, (HasPropertyChangeSupport) this.ontology);
		}
		ontology.loadWhenUnloaded();
		update();
	}

	public boolean getStrictMode() {
		return strictMode;
	}

	@CustomComponentParameter(name = "strictMode", type = CustomComponentParameter.Type.OPTIONAL)
	public void setStrictMode(boolean strictMode) {
		this.strictMode = strictMode;
		update();
	}

	public boolean getHierarchicalMode() {
		return hierarchicalMode;
	}

	@CustomComponentParameter(name = "hierarchicalMode", type = CustomComponentParameter.Type.OPTIONAL)
	public void setHierarchicalMode(boolean hierarchicalMode) {
		this.hierarchicalMode = hierarchicalMode;
		update();
	}

	public boolean getDisplayPropertiesInClasses() {
		return displayPropertiesInClasses;
	}

	@CustomComponentParameter(name = "displayPropertiesInClasses", type = CustomComponentParameter.Type.OPTIONAL)
	public void setDisplayPropertiesInClasses(boolean displayPropertiesInClasses) {
		this.displayPropertiesInClasses = displayPropertiesInClasses;
		update();
	}

	public OntologyClass getRootClass() {
		return rootClass;
	}

	@CustomComponentParameter(name = "rootClass", type = CustomComponentParameter.Type.OPTIONAL)
	public void setRootClass(OntologyClass rootClass) {
		this.rootClass = rootClass;
		update();
	}

	public boolean getAllowsSearch() {
		return allowsSearch;
	}

	@CustomComponentParameter(name = "allowsSearch", type = CustomComponentParameter.Type.OPTIONAL)
	public void setAllowsSearch(boolean allowsSearch) {
		this.allowsSearch = allowsSearch;
	}

	public boolean getDisplayOptions() {
		return displayOptions;
	}

	@CustomComponentParameter(name = "displayOptions", type = CustomComponentParameter.Type.OPTIONAL)
	public void setDisplayOptions(boolean displayOptions) {
		this.displayOptions = displayOptions;
	}

	public boolean getShowObjectProperties() {
		return showObjectProperties;
	}

	@CustomComponentParameter(name = "showObjectProperties", type = CustomComponentParameter.Type.OPTIONAL)
	public void setShowObjectProperties(boolean showObjectProperties) {
		this.showObjectProperties = showObjectProperties;
		update();
	}

	public boolean getShowDataProperties() {
		return showDataProperties;
	}

	@CustomComponentParameter(name = "showDataProperties", type = CustomComponentParameter.Type.OPTIONAL)
	public void setShowDataProperties(boolean showDataProperties) {
		this.showDataProperties = showDataProperties;
		update();
	}

	public boolean getShowAnnotationProperties() {
		return showAnnotationProperties;
	}

	@CustomComponentParameter(name = "showAnnotationProperties", type = CustomComponentParameter.Type.OPTIONAL)
	public void setShowAnnotationProperties(boolean showAnnotationProperties) {
		this.showAnnotationProperties = showAnnotationProperties;
		update();
	}

	public boolean getShowClasses() {
		return showClasses;
	}

	@CustomComponentParameter(name = "showClasses", type = CustomComponentParameter.Type.OPTIONAL)
	public void setShowClasses(boolean showClasses) {
		this.showClasses = showClasses;
		update();
	}

	public boolean getShowIndividuals() {
		return showIndividuals;
	}

	@CustomComponentParameter(name = "showIndividuals", type = CustomComponentParameter.Type.OPTIONAL)
	public void setShowIndividuals(boolean showIndividuals) {
		this.showIndividuals = showIndividuals;
		update();
	}

	public boolean getShowOWLAndRDFConcepts() {
		return showOWLAndRDFConcepts;
	}

	@CustomComponentParameter(name = "showOWLAndRDFConcepts", type = CustomComponentParameter.Type.OPTIONAL)
	public void setShowOWLAndRDFConcepts(boolean showOWLAndRDFConcepts) {
		this.showOWLAndRDFConcepts = showOWLAndRDFConcepts;
		update();
	}

	public OntologyBrowserModel getModel() {
		if (model == null) {
			model = new OntologyBrowserModel(getOntology()) {
				@Override
				public void recomputeStructure() {
					super.recomputeStructure();
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							getPropertyChangeSupport().firePropertyChange("model", null, getModel());
						}
					});
				}
			};
			model.setStrictMode(getStrictMode());
			model.setHierarchicalMode(getHierarchicalMode());
			model.setDisplayPropertiesInClasses(getDisplayPropertiesInClasses());
			model.setRootClass(getRootClass());
			model.setShowClasses(getShowClasses());
			model.setShowIndividuals(getShowIndividuals());
			model.setShowObjectProperties(getShowObjectProperties());
			model.setShowDataProperties(getShowDataProperties());
			model.setShowAnnotationProperties(getShowAnnotationProperties());
			model.setShowOWLAndRDFConcepts(showOWLAndRDFConcepts);
			model.recomputeStructure();
		}
		return model;
	}

	public void update() {
		if (model != null) {
			model.delete();
			model = null;
			setDataObject(this);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					getPropertyChangeSupport().firePropertyChange("model", null, getModel());
				}
			});
		}
	}

	public String getFilteredName() {
		return filteredName;
	}

	public void setFilteredName(String filteredName) {
		this.filteredName = filteredName;
	}

	public List<OntologyObject> getMatchingValues() {
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

	public FIBBrowser getFIBBrowser() {
		List<FIBComponent> listComponent = getFIBComponent().retrieveAllSubComponents();
		for (FIBComponent c : listComponent) {
			if (c instanceof FIBBrowser) {
				return (FIBBrowser) c;
			}
		}
		return null;
	}

	private FIBBrowserWidget retrieveFIBBrowserWidget() {
		List<FIBComponent> listComponent = getFIBComponent().retrieveAllSubComponents();
		for (FIBComponent c : listComponent) {
			if (c instanceof FIBBrowser) {
				return (FIBBrowserWidget) getFIBController().viewForComponent(c);
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
		getFIBController().selectionCleared();
		getFIBController().objectAddedToSelection(selectedValue);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == this.ontology && ((HasPropertyChangeSupport) ontology).getDeletedProperty().equals(evt.getPropertyName())) {
			deleteView();
		}
		super.propertyChange(evt);
	}
}
