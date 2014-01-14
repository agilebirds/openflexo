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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.openflexo.components.widget.OntologyBrowserModel.OntologyBrowserModelRecomputed;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.fib.view.widget.DefaultFIBCustomComponent;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.foundation.ontology.BuiltInDataType;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.controller.IFlexoOntologyTechnologyAdapterController;
import org.openflexo.view.controller.TechnologyAdapterController;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

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
@SuppressWarnings("serial")
public class FIBOntologyBrowser extends DefaultFIBCustomComponent<FIBOntologyBrowser> {

	static final Logger logger = Logger.getLogger(FIBOntologyBrowser.class.getPackage().getName());

	public static final FileResource FIB_FILE = new FileResource("Fib/FIBOntologyBrowser.fib");

	private IFlexoOntology ontology;
	private boolean hierarchicalMode = true;
	private boolean strictMode = false;
	private IFlexoOntologyClass rootClass;
	private boolean displayPropertiesInClasses = true;

	private boolean showObjectProperties = true;
	private boolean showDataProperties = true;
	private boolean showAnnotationProperties = true;
	private boolean showClasses = true;
	private boolean showIndividuals = true;

	private IFlexoOntologyClass domain = null;
	private IFlexoOntologyClass range = null;
	private BuiltInDataType dataType = null;

	private boolean allowsSearch = true;
	private boolean displayOptions = true;

	protected OntologyBrowserModel model = null;
	private TechnologyAdapter technologyAdapter;

	private boolean isSearching = false;
	private String filteredName;
	private final List<IFlexoOntologyConcept> matchingValues;
	private IFlexoOntologyConcept selectedValue;

	public FIBOntologyBrowser(IFlexoOntology ontology) {
		super(FIB_FILE, null, FlexoLocalization.getMainLocalizer());
		matchingValues = new ArrayList<IFlexoOntologyConcept>();
		setOntology(ontology);
		setEditedObject(this);
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
	}

	public IFlexoOntology getOntology() {
		return ontology;
	}

	@CustomComponentParameter(name = "ontology", type = CustomComponentParameter.Type.MANDATORY)
	public void setOntology(IFlexoOntology context) {
		this.ontology = context;
		// ontology.loadWhenUnloaded();
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

	public IFlexoOntologyClass getRootClass() {
		return rootClass;
	}

	@CustomComponentParameter(name = "rootClass", type = CustomComponentParameter.Type.OPTIONAL)
	public void setRootClass(IFlexoOntologyClass rootClass) {
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

	public IFlexoOntologyClass getDomain() {
		return domain;
	}

	@CustomComponentParameter(name = "domain", type = CustomComponentParameter.Type.OPTIONAL)
	public void setDomain(IFlexoOntologyClass domain) {
		this.domain = domain;
		update();
	}

	public IFlexoOntologyClass getRange() {
		return range;
	}

	@CustomComponentParameter(name = "range", type = CustomComponentParameter.Type.OPTIONAL)
	public void setRange(IFlexoOntologyClass range) {
		this.range = range;
		update();
	}

	public BuiltInDataType getDataType() {
		return dataType;
	}

	@CustomComponentParameter(name = "dataType", type = CustomComponentParameter.Type.OPTIONAL)
	public void setDataType(BuiltInDataType dataType) {
		this.dataType = dataType;
		update();
	}

	public TechnologyAdapter getTechnologyAdapter() {
		return technologyAdapter;
	}

	public void setTechnologyAdapter(TechnologyAdapter technologyAdapter) {
		this.technologyAdapter = technologyAdapter;
	}

	/**
	 * Build browser model Override this method when required
	 * 
	 * @return
	 */
	protected OntologyBrowserModel makeBrowserModel() {
		OntologyBrowserModel returned = null;
		if (getTechnologyAdapter() != null) {
			// Use technology specific browser model
			TechnologyAdapterController<?> technologyAdapterController = getTechnologyAdapter().getTechnologyAdapterService()
					.getServiceManager().getService(TechnologyAdapterControllerService.class)
					.getTechnologyAdapterController(technologyAdapter);
			if (technologyAdapterController instanceof IFlexoOntologyTechnologyAdapterController) {
				returned = ((IFlexoOntologyTechnologyAdapterController) technologyAdapterController)
						.makeOntologyBrowserModel(getOntology());
			}
		}
		if (returned == null) {
			// Use default
			returned = new OntologyBrowserModel(getOntology());
		}
		return returned;
	}

	public OntologyBrowserModel getModel() {
		if (model == null) {
			model = makeBrowserModel();
			model.setStrictMode(getStrictMode());
			model.setHierarchicalMode(getHierarchicalMode());
			model.setDisplayPropertiesInClasses(getDisplayPropertiesInClasses());
			model.setRootClass(getRootClass());
			model.setShowClasses(getShowClasses());
			model.setShowIndividuals(getShowIndividuals());
			model.setShowObjectProperties(getShowObjectProperties());
			model.setShowDataProperties(getShowDataProperties());
			model.setShowAnnotationProperties(getShowAnnotationProperties());
			model.setDomain(getDomain());
			model.setRange(getRange());
			model.setDataType(getDataType());
			model.recomputeStructure();
			model.addObserver(new Observer() {
				@Override
				public void update(Observable o, Object arg) {
					if (arg instanceof OntologyBrowserModelRecomputed) {
						performFireModelUpdated();
					}
				}
			});
		}
		return model;
	}

	public void update() {
		if (model != null) {
			model.delete();
			model = null;
			setEditedObject(this);
			performFireModelUpdated();
		}
	}

	private boolean modelWillBeUpdated = false;

	private void performFireModelUpdated() {
		if (modelWillBeUpdated) {
			return;
		} else {
			modelWillBeUpdated = true;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					getPropertyChangeSupport().firePropertyChange("model", null, getModel());
					modelWillBeUpdated = false;
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

	public List<IFlexoOntologyConcept> getMatchingValues() {
		return matchingValues;
	}

	public void search() {
		if (StringUtils.isNotEmpty(getFilteredName())) {
			logger.info("Searching " + getFilteredName());
			matchingValues.clear();
			for (IFlexoOntologyConcept o : getAllSelectableValues()) {
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
	protected Vector<IFlexoOntologyConcept> getAllSelectableValues() {
		Vector<IFlexoOntologyConcept> returned = new Vector<IFlexoOntologyConcept>();
		FIBBrowserWidget browserWidget = retrieveFIBBrowserWidget();
		if (browserWidget == null) {
			return null;
		}
		Iterator<Object> it = browserWidget.getBrowserModel().recursivelyExploreModelToRetrieveContents();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof IFlexoOntologyConcept) {
				returned.add((IFlexoOntologyConcept) o);
			}
		}
		return returned;
	}

	public FIBBrowser getFIBBrowser() {
		if (getFIBComponent() instanceof FIBContainer) {
			List<FIBComponent> listComponent = ((FIBContainer) getFIBComponent()).getAllSubComponents();
			for (FIBComponent c : listComponent) {
				if (c instanceof FIBBrowser) {
					return (FIBBrowser) c;
				}
			}
		}
		return null;
	}

	private FIBBrowserWidget retrieveFIBBrowserWidget() {
		if (getFIBComponent() instanceof FIBContainer) {
			List<FIBComponent> listComponent = ((FIBContainer) getFIBComponent()).getAllSubComponents();
			for (FIBComponent c : listComponent) {
				if (c instanceof FIBBrowser) {
					return (FIBBrowserWidget) getController().viewForComponent(c);
				}
			}
		}
		return null;
	}

	public IFlexoOntologyConcept getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(IFlexoOntologyConcept selectedValue) {
		IFlexoOntologyConcept oldSelected = this.selectedValue;
		this.selectedValue = selectedValue;
		getPropertyChangeSupport().firePropertyChange("selectedValue", oldSelected, selectedValue);
	}

	public void selectValue(IFlexoOntologyConcept selectedValue) {
		getController().selectionCleared();
		getController().objectAddedToSelection(selectedValue);
	}

}
