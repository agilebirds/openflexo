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
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.viewpoint.binding.EditionPatternBindingFactory;
import org.openflexo.foundation.viewpoint.binding.OntologyObjectPathElement.OntologyIndividualPathElement;
import org.openflexo.toolbox.FileResource;

/**
 * Widget allowing to select an OntologyIndividual<br>
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
 * {@link #setRepresentationForIndividualOfClass(String, String, OntologyClass)}
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class FIBIndividualSelector extends FIBModelObjectSelector<OntologyIndividual> implements Bindable {
	static final Logger logger = Logger.getLogger(FIBIndividualSelector.class.getPackage().getName());

	public static final FileResource FIB_FILE = new FileResource("Fib/FIBIndividualSelector.fib");

	private FlexoOntology context;
	private OntologyClass type;
	private boolean hierarchicalMode = true;
	private boolean strictMode = false;
	private boolean showOWLAndRDFConcepts = false;

	private OntologyBrowserModel model = null;

	private BindingModel bindingModel;

	private static EditionPatternBindingFactory EDITION_PATTERN_BINDING_FACTORY = new EditionPatternBindingFactory();

	private HashMap<OntologyClass, DataBinding<String>> renderers;

	public FIBIndividualSelector(OntologyIndividual editedObject) {
		super(editedObject);
		bindingModel = new BindingModel();
		renderers = new HashMap<OntologyClass, DataBinding<String>>();
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
	public Class<OntologyIndividual> getRepresentedType() {
		return OntologyIndividual.class;
	}

	public String renderObject(OntologyObject object) {
		if (object instanceof OntologyIndividual) {
			return renderedString((OntologyIndividual) object);
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
	public void setRepresentationForIndividualOfClass(String variableName, String expression, OntologyClass type) {
		renderers.put(type, new DataBinding<String>(expression, this, String.class, BindingDefinitionType.GET));
		OntologyIndividualPathElement newPathElement = new OntologyIndividualPathElement(variableName, type, null, type.getFlexoOntology());
		bindingModel.addToBindingVariables(newPathElement);
	}

	protected DataBinding<String> getRenderer(OntologyIndividual individual) {
		if (renderers == null) {
			return null;
		}
		List<OntologyClass> matchingClasses = new ArrayList<OntologyClass>();
		for (OntologyClass cl : renderers.keySet()) {
			if (cl.isSuperConceptOf(individual)) {
				matchingClasses.add(cl);
			}
		}
		OntologyClass mostSpecializedClass = OntologyClass.getMostSpecializedClass(matchingClasses);
		return renderers.get(mostSpecializedClass);
	}

	public class BindingEvaluator implements BindingEvaluationContext {

		public BindingEvaluator(OntologyIndividual individual) {
		}

		@Override
		public Object getValue(BindingVariable variable) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	@Override
	public String renderedString(final OntologyIndividual editedObject) {

		DataBinding<String> binding = getRenderer(editedObject);

		if (binding == null) {
			return null;
		}

		if (editedObject != null) {
			try {
				return binding.getBindingValue(new BindingEvaluationContext() {
					@Override
					public Object getValue(BindingVariable variable) {
						return editedObject;
					}
				});
			} catch (Exception e) {
				return editedObject.getName();
			}
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
		if (getProject() != null) {
			FlexoOntology context = getProject().getResourceCenter().retrieveBaseOntologyLibrary().getOntology(ontologyURI);
			if (context != null) {
				setContext(context);
			}
		}
	}

	public FlexoOntology getContext() {
		return context;
	}

	@CustomComponentParameter(name = "context", type = CustomComponentParameter.Type.MANDATORY)
	public void setContext(FlexoOntology context) {
		this.context = context;
		update();
		setRepresentationForIndividualOfClass("individual", "individual.uriName", context.getThingConcept());
	}

	public OntologyClass getType() {
		return type;
	}

	@CustomComponentParameter(name = "type", type = CustomComponentParameter.Type.OPTIONAL)
	public void setType(OntologyClass rootClass) {
		this.type = rootClass;
		update();
		setRepresentationForIndividualOfClass("rootClass", "individual.uriName", rootClass);
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
			OntologyClass typeClass = getContext().getClass(aClassURI);
			if (typeClass != null) {
				setType(typeClass);
			}
		}
	}

	@CustomComponentParameter(name = "renderer", type = CustomComponentParameter.Type.OPTIONAL)
	public void setRenderer(String renderer) {
		setRepresentationForIndividualOfClass("individual", renderer, getType());
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
			model = new OntologyBrowserModel(getContext()) {
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
			model.setDisplayPropertiesInClasses(false);
			model.setRootClass(getType());
			model.setShowClasses(false);
			model.setShowIndividuals(true);
			model.setShowObjectProperties(false);
			model.setShowDataProperties(false);
			model.setShowAnnotationProperties(false);
			model.setShowOWLAndRDFConcepts(showOWLAndRDFConcepts);
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
	public void setProject(FlexoProject project) {
		super.setProject(project);
		if (project != null) {
			setContext(project.getProjectOntology());
		}
	}

	// Please uncomment this for a live test
	// Never commit this uncommented since it will not compile on continuous build
	// To have icon, you need to choose "Test interface" in the editor (otherwise, flexo controller is not insanciated in EDIT mode)
	/*public static void main(String[] args) {
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				FlexoResourceCenter testResourceCenter = LocalResourceCenterImplementation
						.instanciateTestLocalResourceCenterImplementation(new FileResource("TestResourceCenter"));
				FIBIndividualSelector selector = new FIBIndividualSelector(null);
				// selector.setContext(resourceCenter.retrieveBaseOntologyLibrary().getFlexoConceptOntology());
				FlexoOntology o = testResourceCenter.retrieveBaseOntologyLibrary().getOntology(
				// "http://www.thalesgroup.com/ontologies/sepel-ng/MappingSpecifications.owl");
				// "http://www.openflexo.org/test/TestInstances.owl");
						"http://www.openflexo.org/test/Family.owl");
				o.loadWhenUnloaded();
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
				return new FlexoFIBController<FIBViewPointSelector>(component);
			}
		};
		editor.launch();
	}*/

}
