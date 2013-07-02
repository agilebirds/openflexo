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
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.InformationSpace;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.IFlexoOntologyTechnologyAdapterController;
import org.openflexo.view.controller.TechnologyAdapterController;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

/**
 * Widget allowing to select an IFlexoOntologyClass<br>
 * 
 * This widget provides many configuration options:
 * <ul>
 * <li>context: required, defines ontology context</li>
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
public class FIBClassSelector extends FIBModelObjectSelector<IFlexoOntologyClass> {
	static final Logger logger = Logger.getLogger(FIBClassSelector.class.getPackage().getName());

	public static final FileResource FIB_FILE = new FileResource("Fib/FIBClassSelector.fib");

	private InformationSpace informationSpace;
	private IFlexoOntology context;
	private IFlexoOntologyClass rootClass;
	private boolean hierarchicalMode = true;
	private boolean strictMode = false;

	protected OntologyBrowserModel model = null;
	private TechnologyAdapter technologyAdapter;

	public FIBClassSelector(IFlexoOntologyClass editedObject) {
		super(editedObject);
	}

	@Override
	public void delete() {
		super.delete();
		context = null;
	}

	@Override
	public File getFIBFile() {
		return FIB_FILE;
	}

	@Override
	public Class<IFlexoOntologyClass> getRepresentedType() {
		return IFlexoOntologyClass.class;
	}

	public InformationSpace getInformationSpace() {
		// Still use legacy: if InformationSpace is not specified by project, retrieve IS from Project
		if (informationSpace == null && getProject() != null) {
			informationSpace = getProject().getInformationSpace();
		}
		return informationSpace;
	}

	@CustomComponentParameter(name = "informationSpace", type = CustomComponentParameter.Type.OPTIONAL)
	public void setInformationSpace(InformationSpace informationSpace) {
		// System.out.println("Sets InformationSpace with " + informationSpace);
		this.informationSpace = informationSpace;
	}

	@Override
	public String renderedString(IFlexoOntologyClass editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return "";
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
		if (getInformationSpace() != null) {
			FlexoModelResource<?, ?> modelResource = getInformationSpace().getModelWithURI(ontologyURI);
			if (modelResource != null && modelResource.getModel() instanceof IFlexoOntology) {
				setContext((IFlexoOntology) modelResource.getModel());
			}
		}
	}

	public IFlexoOntology getContext() {
		return context;
	}

	@CustomComponentParameter(name = "context", type = CustomComponentParameter.Type.MANDATORY)
	public void setContext(IFlexoOntology context) {
		this.context = context;
		update();
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

	public IFlexoOntologyClass getRootClass() {
		return rootClass;
	}

	@CustomComponentParameter(name = "rootClass", type = CustomComponentParameter.Type.OPTIONAL)
	public void setRootClass(IFlexoOntologyClass rootClass) {
		this.rootClass = rootClass;
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

	public boolean getStrictMode() {
		return strictMode;
	}

	@CustomComponentParameter(name = "strictMode", type = CustomComponentParameter.Type.OPTIONAL)
	public void setStrictMode(boolean strictMode) {
		this.strictMode = strictMode;
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
				returned = ((IFlexoOntologyTechnologyAdapterController) technologyAdapterController).makeOntologyBrowserModel(getContext());
			}
		}
		if (returned == null) {
			// Use default
			returned = new OntologyBrowserModel(getContext());
		}
		return returned;
	}

	public OntologyBrowserModel getModel() {
		if (model == null) {
			model = makeBrowserModel();
			model.setStrictMode(getStrictMode());
			model.setHierarchicalMode(getHierarchicalMode());
			model.setDisplayPropertiesInClasses(false);
			model.setRootClass(getRootClass());
			model.setShowClasses(true);
			model.setShowIndividuals(false);
			model.setShowObjectProperties(false);
			model.setShowDataProperties(false);
			model.setShowAnnotationProperties(false);
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
				// "http://www.w3.org/2000/01/rdf-schema");

				FIBClassSelector selector = new FIBClassSelector(null);

				selector.setContext(o);
				selector.setHierarchicalMode(true); // false
				selector.setStrictMode(true);
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
