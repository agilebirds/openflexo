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
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.components.widget.OntologyBrowserModel.OntologyBrowserModelRecomputed;
import org.openflexo.foundation.ontology.BuiltInDataType;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.TechnologyAdapterController;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

/**
 * Widget allowing to select an IFlexoOntologyStructuralProperty.<br>
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
public class FIBPropertySelector extends FIBModelObjectSelector<IFlexoOntologyStructuralProperty> {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBPropertySelector.class.getPackage().getName());

	public static final FileResource FIB_FILE = new FileResource("Fib/FIBPropertySelector.fib");

	private IFlexoOntology context;
	private IFlexoOntologyClass rootClass;
	private IFlexoOntologyClass domain;
	private IFlexoOntologyClass range;
	private BuiltInDataType dataType;
	private boolean hierarchicalMode = true;
	private boolean selectObjectProperties = true;
	private boolean selectDataProperties = true;
	private boolean selectAnnotationProperties = false;
	private boolean strictMode = false;
	private boolean displayPropertiesInClasses = true;

	protected OntologyBrowserModel model = null;

	public FIBPropertySelector(IFlexoOntologyStructuralProperty editedObject) {
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
	public Class<IFlexoOntologyStructuralProperty> getRepresentedType() {
		return IFlexoOntologyStructuralProperty.class;
	}

	@Override
	public String renderedString(IFlexoOntologyStructuralProperty editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return "";
	}

	public IFlexoOntology getContext() {
		return context;
	}

	@CustomComponentParameter(name = "context", type = CustomComponentParameter.Type.MANDATORY)
	public void setContext(IFlexoOntology context) {
		this.context = context;
		update();
	}

	public String getContextOntologyURI() {
		if (getContext() != null) {
			return getContext().getURI();
		}
		return null;
	}

	@CustomComponentParameter(name = "contextOntologyURI", type = CustomComponentParameter.Type.MANDATORY)
	public void setContextOntologyURI(String ontologyURI) {
		// logger.info("Sets ontology with " + ontologyURI);
		if (getProject() != null) {
			IFlexoOntology context = getProject().getFlexoOntology(ontologyURI);
			if (context != null) {
				setContext(context);
			}
		}
	}

	public IFlexoOntologyClass getRootClass() {
		return rootClass;
	}

	@CustomComponentParameter(name = "rootClass", type = CustomComponentParameter.Type.MANDATORY)
	public void setRootClass(IFlexoOntologyClass rootClass) {
		this.rootClass = rootClass;
	}

	public String getRootClassURI() {
		if (getRootClass() != null) {
			return getRootClass().getURI();
		}
		return null;
	}

	@CustomComponentParameter(name = "rootClassURI", type = CustomComponentParameter.Type.MANDATORY)
	public void setRootClassURI(String aRootClassURI) {
		// logger.info("Sets rootClassURI with " + aRootClassURI + " context=" + getContext());
		if (getContext() != null) {
			IFlexoOntologyClass rootClass = getContext().getClass(aRootClassURI);
			if (rootClass != null) {
				setRootClass(rootClass);
			}
		}
	}

	public IFlexoOntologyClass getDomain() {
		return domain;
	}

	@CustomComponentParameter(name = "domain", type = CustomComponentParameter.Type.OPTIONAL)
	public void setDomain(IFlexoOntologyClass domain) {
		System.out.println("INGORED !!!!! PropertySelector, setDomain() with " + domain);
		this.domain = domain;
	}

	public String getDomainClassURI() {
		if (getDomain() != null) {
			return getDomain().getURI();
		}
		return null;
	}

	@CustomComponentParameter(name = "domainClassURI", type = CustomComponentParameter.Type.MANDATORY)
	public void setDomainClassURI(String aDomainClassURI) {
		// logger.info("Sets domainClassURI with " + aDomainClassURI + " context=" + getContext());
		if (getContext() != null) {
			IFlexoOntologyClass rootClass = getContext().getClass(aDomainClassURI);
			if (rootClass != null) {
				setDomain(rootClass);
			}
		}
	}

	public IFlexoOntologyClass getRange() {
		return range;
	}

	@CustomComponentParameter(name = "range", type = CustomComponentParameter.Type.OPTIONAL)
	public void setRange(IFlexoOntologyClass range) {
		this.range = range;
	}

	public String getRangeClassURI() {
		if (getRange() != null) {
			return getRange().getURI();
		}
		return null;
	}

	@CustomComponentParameter(name = "rangeClassURI", type = CustomComponentParameter.Type.MANDATORY)
	public void setRangeClassURI(String aRangeClassURI) {
		// logger.info("Sets rangeClassURI with " + aRangeClassURI + " context=" + getContext());
		if (getContext() != null) {
			IFlexoOntologyClass rootClass = getContext().getClass(aRangeClassURI);
			if (rootClass != null) {
				setRange(rootClass);
			}
		}
	}

	public BuiltInDataType getDataType() {
		return dataType;
	}

	@CustomComponentParameter(name = "dataType", type = CustomComponentParameter.Type.OPTIONAL)
	public void setDataType(BuiltInDataType dataType) {
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

	public boolean getDisplayPropertiesInClasses() {
		return displayPropertiesInClasses;
	}

	@CustomComponentParameter(name = "displayPropertiesInClasses", type = CustomComponentParameter.Type.OPTIONAL)
	public void setDisplayPropertiesInClasses(boolean displayPropertiesInClasses) {
		this.displayPropertiesInClasses = displayPropertiesInClasses;
		update();
	}

	private TechnologyAdapter technologyAdapter;

	public TechnologyAdapter getTechnologyAdapter() {
		return technologyAdapter;
	}

	public void setTechnologyAdapter(TechnologyAdapter technologyAdapter) {
		this.technologyAdapter = technologyAdapter;
	}

	public OntologyBrowserModel getModel() {
		if (model == null) {
			if (getTechnologyAdapter() != null) {
				// Use technology specific browser model
				TechnologyAdapterController technologyAdapterController = getTechnologyAdapter().getTechnologyAdapterService()
						.getServiceManager().getService(TechnologyAdapterControllerService.class)
						.getTechnologyAdapterController(technologyAdapter);
				model = technologyAdapterController.makeOntologyBrowserModel(getContext());
			} else { // Use default
				model = new OntologyBrowserModel(getContext());
			}
			model.addObserver(new Observer() {
				@Override
				public void update(Observable o, Object arg) {
					if (arg instanceof OntologyBrowserModelRecomputed) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								getPropertyChangeSupport().firePropertyChange("model", null, getModel());
							}
						});
					}
				}
			});
			model.setStrictMode(getStrictMode());
			model.setHierarchicalMode(getHierarchicalMode());
			model.setDisplayPropertiesInClasses(getDisplayPropertiesInClasses());
			model.setRootClass(getRootClass());
			model.setDomain(getDomain());
			model.setRange(getRange());
			model.setDataType(getDataType());
			model.setShowClasses(false);
			model.setShowIndividuals(false);
			model.setShowObjectProperties(getSelectObjectProperties());
			model.setShowDataProperties(getSelectDataProperties());
			model.setShowAnnotationProperties(getSelectAnnotationProperties());
			/*System.out.println("Recomputing...");
			System.out.println("context=" + getContext());
			System.out.println("getStrictMode()=" + getStrictMode());
			System.out.println("getHierarchicalMode()=" + getHierarchicalMode());
			System.out.println("getDisplayPropertiesInClasses()=" + getDisplayPropertiesInClasses());
			System.out.println("getRootClass()=" + getRootClass());
			System.out.println("getDomain()=" + getDomain());
			System.out.println("getRange()=" + getRange());
			System.out.println("getDataType()=" + getDataType());
			System.out.println("getSelectObjectProperties()=" + getSelectObjectProperties());
			System.out.println("getSelectDataProperties()=" + getSelectDataProperties());
			System.out.println("getSelectAnnotationProperties()=" + getSelectAnnotationProperties());
			System.out.println("getShowOWLAndRDFConcepts()=" + getShowOWLAndRDFConcepts());
			model.recomputeStructure();*/
		}
		return model;
	}

	public void update() {
		if (model != null) {
			model.delete();
			model = null;
			// setEditedObject(this);
			fireEditedObjectChanged();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					getPropertyChangeSupport().firePropertyChange("model", null, getModel());
				}
			});
		}
	}

	@Override
	public void setProject(FlexoProject project) {
		super.setProject(project);
		// With model slots, setContext has to be called explicitly
		// if (project != null) {setContext(project.getProjectOntology());}
	}

	// Please uncomment this for a live test
	// Never commit this uncommented since it will not compile on continuous build
	// To have icon, you need to choose "Test interface" in the editor (otherwise, flexo controller is not insanciated in EDIT mode)
	/*public static void main(String[] args) {
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				try {
					FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ApplicationContext ac = new TestApplicationContext(new FileResource("TestResourceCenter"));

				IFlexoOntology o = (IFlexoOntology) ac.getInformationSpace()
						.getMetaModel("http://www.agilebirds.com/openflexo/ontologies/UML/UML2.owl").getMetaModelData();

				// "http://www.thalesgroup.com/ontologies/sepel-ng/MappingSpecifications.owl");
				// "http://www.cpmf.org/ontologies/cpmfInstance");
				// "http://www.agilebirds.com/openflexo/ontologies/FlexoConceptsOntology.owl");
				// "http://www.w3.org/2002/07/owl");
				// "http://www.thalesgroup.com/ontologies/sepel-ng/SEPELOutputModel1.owl");
				// "http://www.openflexo.org/test/TestProperties.owl");
				// "http://www.w3.org/2000/01/rdf-schema");
						"http://www.agilebirds.com/openflexo/ontologies/UML/UML2.owl");

				FIBPropertySelector selector = new FIBPropertySelector(null);

				selector.setContext(o);
				selector.setHierarchicalMode(true); // false
				selector.setSelectAnnotationProperties(true);
				selector.setSelectObjectProperties(true);
				selector.setSelectDataProperties(true);
				selector.setStrictMode(true);
				selector.setDomainClassURI("http://www.agilebirds.com/openflexo/ontologies/UML/UML2.owl#Property");
				// selector.setRangeClassURI("http://www.thalesgroup.com/ontologies/sepel-ng/SEPELOutputModel1.owl#EmSubMode");
				return makeArray(selector);
			}

			@Override
			public File getFIBFile() {
				return FIB_FILE;
			}

			@Override
			public FIBController makeNewController(FIBComponent component) {
				return new FlexoFIBController(component);
			}
		};
		editor.launch();
	}*/
}
