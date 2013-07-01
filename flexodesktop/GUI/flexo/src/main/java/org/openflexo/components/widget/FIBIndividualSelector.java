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
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.components.widget.OntologyBrowserModel.OntologyBrowserModelRecomputed;
import org.openflexo.foundation.ontology.FlexoOntologyObjectImpl;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.ontology.OntologyUtils;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.InformationSpace;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.viewpoint.binding.EditionPatternBindingFactory;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.controller.IFlexoOntologyTechnologyAdapterController;
import org.openflexo.view.controller.TechnologyAdapterController;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

/**
 * Widget allowing to select an IFlexoOntologyIndividual<br>
 * 
 * This widget provides many configuration options:
 * <ul>
 * <li>context: required, defines ontology context</li>
 * <li>strictMode: required, default is false, when true, indicates that properties are retrieved from declared context ontology only</li>
 * <li>hierarchicalMode: required, default is true, defines if properties are stored relative to a storage class, defined either as the
 * domain class, or the top-level class where this property is used as a restriction</li>
 * <li>type: when set, defines type class of searched individuals</li>
 * </ul>
 * 
 * Additionnaly, this widget provides a way to define custom renderers for different types of individuals. See
 * {@link #setRepresentationForIndividualOfClass(String, String, IFlexoOntologyClass)}
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class FIBIndividualSelector extends FIBModelObjectSelector<IFlexoOntologyIndividual> implements Bindable {
	static final Logger logger = Logger.getLogger(FIBIndividualSelector.class.getPackage().getName());

	public static final FileResource FIB_FILE = new FileResource("Fib/FIBIndividualSelector.fib");

	private InformationSpace informationSpace;
	private IFlexoOntology context;
	private IFlexoOntologyClass type;
	private boolean hierarchicalMode = true;
	private boolean strictMode = false;

	protected OntologyBrowserModel model = null;
	private TechnologyAdapter technologyAdapter;

	private BindingModel bindingModel;

	private String defaultRenderer = null;

	private static EditionPatternBindingFactory EDITION_PATTERN_BINDING_FACTORY = new EditionPatternBindingFactory(null);

	private HashMap<IFlexoOntologyClass, DataBinding<String>> renderers;

	public FIBIndividualSelector(IFlexoOntologyIndividual editedObject) {
		super(editedObject);
		bindingModel = new BindingModel();
		renderers = new HashMap<IFlexoOntologyClass, DataBinding<String>>();
	}

	@Override
	public BindingFactory getBindingFactory() {
		return EDITION_PATTERN_BINDING_FACTORY;
	}

	@Override
	public BindingModel getBindingModel() {
		return bindingModel;
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
	public Class<IFlexoOntologyIndividual> getRepresentedType() {
		return IFlexoOntologyIndividual.class;
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

	public String getRenderer() {
		if (getType() != null) {
			if (renderers.get(getType()) != null) {
				return renderers.get(getType()).toString();
			}
		}
		return defaultRenderer;
	}

	@CustomComponentParameter(name = "renderer", type = CustomComponentParameter.Type.OPTIONAL)
	public void setRenderer(String renderer) {
		defaultRenderer = renderer;
		if (getType() != null) {
			setRepresentationForIndividualOfClass("individual", renderer, getType());
		}
	}

	public String renderObject(FlexoOntologyObjectImpl object) {
		if (object instanceof IFlexoOntologyIndividual) {
			System.out.println("For " + object + " render " + renderedString((IFlexoOntologyIndividual) object));
			return renderedString((IFlexoOntologyIndividual) object);
		}
		return object.getName();
	}

	/**
	 * Provides custom renderer for individuals of a given ontology class.<br>
	 * For example call: <code>
	 * 	selector.setRepresentationForIndividualOfClass("personne", "personne.nom+' '+personne.prenom",
	 * 		o.getClass("http://www.openflexo.org/test/Family.owl#Personne"));
	 * </code>
	 * 
	 * @param variableName
	 * @param expression
	 * @param type
	 */
	public void setRepresentationForIndividualOfClass(String variableName, String expression, IFlexoOntologyClass type) {
		if (renderers.get(type) == null || !renderers.get(type).toString().equals(expression)) {
			if (renderers.get(type) != null) {
				logger.info("Was " + renderers.get(type).toString() + " now " + expression);
			}
			// OntologyIndividualPathElement newPathElement = new OntologyIndividualPathElement(variableName, type, null);
			BindingVariable newPathElement = new BindingVariable(variableName, IndividualOfClass.getIndividualOfClass(type));
			if (bindingModel.bindingVariableNamed(variableName) != null) {
				logger.warning("Duplicated binding variable " + variableName);
				bindingModel.removeFromBindingVariables(bindingModel.bindingVariableNamed(variableName));
			}
			bindingModel.addToBindingVariables(newPathElement);
			DataBinding<String> db = new DataBinding<String>(expression, this, String.class, DataBinding.BindingDefinitionType.GET);
			renderers.put(type, db);
		}
	}

	protected DataBinding<String> getRenderer(IFlexoOntologyIndividual individual) {

		if (individual == null) {
			return null;
		}

		// If default renderer was not already applied to default type, then do it now
		if (StringUtils.isNotEmpty(defaultRenderer) && getType() != null && renderers.get(getType()) == null) {
			setRepresentationForIndividualOfClass("individual", defaultRenderer, getType());
		}

		if (renderers == null) {
			return null;
		}
		List<IFlexoOntologyClass> matchingClasses = new ArrayList<IFlexoOntologyClass>();
		for (IFlexoOntologyClass cl : renderers.keySet()) {
			if (cl.isSuperConceptOf(individual)) {
				matchingClasses.add(cl);
			}
		}
		IFlexoOntologyClass mostSpecializedClass = OntologyUtils.getMostSpecializedClass(matchingClasses);

		return renderers.get(mostSpecializedClass);
	}

	public class BindingEvaluator implements BindingEvaluationContext {

		public BindingEvaluator(IFlexoOntologyIndividual individual) {
		}

		@Override
		public Object getValue(BindingVariable variable) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	@Override
	public String renderedString(final IFlexoOntologyIndividual editedObject) {

		if (editedObject == null) {
			return null;
		}

		System.out.println("Trying to render " + editedObject + " renderer=" + getRenderer(editedObject));
		DataBinding<String> renderer = getRenderer(editedObject);

		if (renderer == null) {
			return editedObject.getName();
		}

		if (editedObject != null) {
			try {
				String returned = renderer.getBindingValue(new BindingEvaluationContext() {
					@Override
					public Object getValue(BindingVariable variable) {
						return editedObject;
					}
				});
				return returned;
			} catch (Exception e) {
				return editedObject.getName();
			}
		}
		return editedObject.getName();
	}

	public String getContextOntologyURI() {
		if (getContext() != null) {
			return getContext().getURI();
		}
		return null;
	}

	@CustomComponentParameter(name = "contextOntologyURI", type = CustomComponentParameter.Type.MANDATORY)
	public void setContextOntologyURI(String ontologyURI) {
		// logger.info(">>>>>>>>>>>> Sets ontology with " + ontologyURI);
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
		System.out.println("Set context with " + context);
		this.context = context;
		update();
		setRepresentationForIndividualOfClass("defaultIndividual", "defaultIndividual.uriName", context.getRootConcept());
	}

	public IFlexoOntologyClass getType() {
		return type;
	}

	@CustomComponentParameter(name = "type", type = CustomComponentParameter.Type.OPTIONAL)
	public void setType(IFlexoOntologyClass rootClass) {
		this.type = rootClass;
		update();
		// setRepresentationForIndividualOfClass("defaultType", "defaultType.uriName", rootClass);
	}

	public String getTypeURI() {
		if (getType() != null) {
			return getType().getURI();
		}
		return null;
	}

	@CustomComponentParameter(name = "typeURI", type = CustomComponentParameter.Type.MANDATORY)
	public void setTypeURI(String aClassURI) {
		// logger.info("Sets typeClassURI with " + aClassURI + " context=" + getContext());
		if (getContext() != null) {
			IFlexoOntologyClass typeClass = getContext().getClass(aClassURI);
			if (typeClass != null) {
				setType(typeClass);
			}
		}
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

	public OntologyBrowserModel getModel() {
		if (model == null) {
			if (getTechnologyAdapter() != null) {
				// Use technology specific browser model
				TechnologyAdapterController<?> technologyAdapterController = getTechnologyAdapter().getTechnologyAdapterService()
						.getServiceManager().getService(TechnologyAdapterControllerService.class)
						.getTechnologyAdapterController(technologyAdapter);
				if (technologyAdapterController instanceof IFlexoOntologyTechnologyAdapterController) {
					model = ((IFlexoOntologyTechnologyAdapterController) technologyAdapterController).makeOntologyBrowserModel(getContext());
				}
			}
			if (model == null) {
				// Use default
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
			model.setDisplayPropertiesInClasses(false);
			model.setRootClass(getType());
			model.setShowClasses(false);
			model.setShowIndividuals(true);
			model.setShowObjectProperties(false);
			model.setShowDataProperties(false);
			model.setShowAnnotationProperties(false);
			model.recomputeStructure();
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
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
	}

	@Override
	public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
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
				// "http://www.openflexo.org/test/TestInstances.owl");
				// "http://www.openflexo.org/test/Family.owl");

				FIBIndividualSelector selector = new FIBIndividualSelector(null);

				selector.setContext(o);
				selector.setHierarchicalMode(true); // false
				selector.setStrictMode(true);
				selector.setRepresentationForIndividualOfClass("personne", "personne.nom+' '+personne.prenom",
						o.getClass("http://www.openflexo.org/test/Family.owl#Personne"));
				selector.setRepresentationForIndividualOfClass("sexe", "'SEXE:'+sexe.uriName",
						o.getClass("http://www.openflexo.org/test/Family.owl#Sexe"));
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
