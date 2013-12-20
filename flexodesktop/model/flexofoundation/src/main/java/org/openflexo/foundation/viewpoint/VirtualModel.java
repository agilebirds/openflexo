/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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
package org.openflexo.foundation.viewpoint;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.dm.EditionPatternCreated;
import org.openflexo.foundation.viewpoint.dm.EditionPatternDeleted;
import org.openflexo.foundation.viewpoint.dm.ModelSlotAdded;
import org.openflexo.foundation.viewpoint.dm.ModelSlotRemoved;
import org.openflexo.foundation.viewpoint.rm.ViewPointResource;
import org.openflexo.foundation.viewpoint.rm.VirtualModelResource;
import org.openflexo.foundation.viewpoint.rm.VirtualModelResourceImpl;
import org.openflexo.toolbox.ChainedCollection;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

/**
 * A {@link VirtualModel} is the specification of a model which will be instantied in a {@link View} as a set of federated models.
 * 
 * The base modelling element of a {@link VirtualModel} is provided by {@link EditionPattern} concept.
 * 
 * A {@link VirtualModel} instance contains a set of {@link EditionPatternInstance}.
 * 
 * A {@link VirtualModel} is itself an {@link EditionPattern}
 * 
 * @author sylvain
 * 
 */
public class VirtualModel extends EditionPattern implements FlexoMetaModel<VirtualModel> {

	private static final Logger logger = Logger.getLogger(VirtualModel.class.getPackage().getName());

	private ViewPoint viewPoint;
	private Vector<EditionPattern> editionPatterns;
	private List<ModelSlot<?>> modelSlots;
	private BindingModel bindingModel;
	private VirtualModelResource resource;
	private LocalizedDictionary localizedDictionary;

	private boolean readOnly = false;

	/**
	 * Stores a chained collections of objects which are involved in validation
	 */
	private ChainedCollection<ViewPointObject> validableObjects = null;

	/**
	 * Creates a new VirtualModel on user request<br>
	 * Creates both the resource and the object
	 * 
	 * 
	 * @param baseName
	 * @param viewPoint
	 * @return
	 */
	public static VirtualModel newVirtualModel(String baseName, ViewPoint viewPoint) {
		File diagramSpecificationDirectory = new File(((ViewPointResource) viewPoint.getResource()).getDirectory(), baseName);
		File diagramSpecificationXMLFile = new File(diagramSpecificationDirectory, baseName + ".xml");
		ViewPointLibrary viewPointLibrary = viewPoint.getViewPointLibrary();
		VirtualModelResource vmRes = VirtualModelResourceImpl.makeVirtualModelResource(diagramSpecificationDirectory,
				diagramSpecificationXMLFile, (ViewPointResource) viewPoint.getResource(), viewPointLibrary);
		VirtualModel virtualModel = new VirtualModel(viewPoint);
		vmRes.setResourceData(virtualModel);
		virtualModel.setResource(vmRes);
		virtualModel.makeReflexiveModelSlot();
		virtualModel.save();
		return virtualModel;
	}

	// Used during deserialization, do not use it
	public VirtualModel(/*VirtualModelBuilder builder*/) {
		super(/*builder*/);
		/*if (builder != null) {
			builder.setVirtualModel(this);
			resource = builder.resource;
			viewPoint = builder.getViewPoint();
		}*/
		modelSlots = new ArrayList<ModelSlot<?>>();
		editionPatterns = new Vector<EditionPattern>();
	}

	/**
	 * Creates a new VirtualModel in supplied viewpoint
	 * 
	 * @param viewPoint
	 */
	public VirtualModel(ViewPoint viewPoint) {
		this();
		setViewPoint(viewPoint);
	}

	public void finalizeDeserialization() {
		finalizeEditionPatternDeserialization();
		for (EditionPattern ep : getEditionPatterns()) {
			ep.finalizeEditionPatternDeserialization();
		}
		// Ensure access to reflexive model slot
		getReflexiveModelSlot();
	}

	private VirtualModelModelSlot reflexiveModelSlot;

	public static final String REFLEXIVE_MODEL_SLOT_NAME = "this";

	/**
	 * Return reflexive model slot<br>
	 * The reflexive model slot is an abstraction which allow to consider the virtual model as a model which can be accessed from itself
	 * 
	 * @return
	 */
	public VirtualModelModelSlot getReflexiveModelSlot() {
		if (reflexiveModelSlot == null) {
			reflexiveModelSlot = (VirtualModelModelSlot) getModelSlot(REFLEXIVE_MODEL_SLOT_NAME);
			if (reflexiveModelSlot == null) {
				reflexiveModelSlot = makeReflexiveModelSlot();
			}
		}
		return reflexiveModelSlot;
	}

	protected VirtualModelModelSlot makeReflexiveModelSlot() {
		if (getViewPoint().getViewPointLibrary().getServiceManager() != null
				&& getViewPoint().getViewPointLibrary().getServiceManager().getService(TechnologyAdapterService.class) != null) {
			VirtualModelTechnologyAdapter builtInTA = getViewPoint().getViewPointLibrary().getServiceManager()
					.getService(TechnologyAdapterService.class).getTechnologyAdapter(VirtualModelTechnologyAdapter.class);
			VirtualModelModelSlot returned = builtInTA.makeModelSlot(VirtualModelModelSlot.class, this);
			returned.setVirtualModelResource(getResource());
			returned.setName(REFLEXIVE_MODEL_SLOT_NAME);
			addToModelSlots(returned);
			return returned;
		}
		logger.warning("Could not instanciate reflexive model slot");
		return null;
	}

	/**
	 * Return the URI of the {@link VirtualModel}<br>
	 * The convention for URI are following: <viewpoint_uri>/<virtual_model_name>#<edition_pattern_name>.<edition_scheme_name> <br>
	 * eg<br>
	 * http://www.mydomain.org/MyViewPoint/MyVirtualModel#MyEditionPattern.MyEditionScheme
	 * 
	 * @return String representing unique URI of this object
	 */
	@Override
	public String getURI() {
		return getViewPoint().getURI() + "/" + getName();
	}

	@Override
	public String getName() {
		if (getResource() != null) {
			return getResource().getName();
		}
		return super.getName();
	}

	@Override
	public void setName(String name) {
		if (requireChange(getName(), name)) {
			if (getResource() != null) {
				getResource().setName(name);
			} else {
				super.setName(name);
			}
		}
	}

	public FlexoVersion getVersion() {
		if (getResource() != null) {
			return getResource().getVersion();
		}
		return null;
	}

	public void setVersion(FlexoVersion aVersion) {
		if (requireChange(getVersion(), aVersion)) {
			if (getResource() != null) {
				getResource().setVersion(aVersion);
			}
		}
	}

	@Override
	public String toString() {
		return "VirtualModel:" + getURI();
	}

	@Override
	public ViewPoint getViewPoint() {
		return viewPoint;
	}

	public void setViewPoint(ViewPoint viewPoint) {
		this.viewPoint = viewPoint;
	}

	protected void notifyEditionSchemeModified() {
	}

	/**
	 * Return all {@link EditionPattern} defined in this {@link VirtualModel} which have no parent
	 * 
	 * @return
	 */
	public Vector<EditionPattern> getAllRootEditionPatterns() {
		Vector<EditionPattern> returned = new Vector<EditionPattern>();
		for (EditionPattern ep : getEditionPatterns()) {
			if (ep.isRoot()) {
				returned.add(ep);
			}
		}
		return returned;
	}

	/**
	 * Return all {@link EditionPattern} defined in this {@link VirtualModel}
	 * 
	 * @return
	 */
	public Vector<EditionPattern> getEditionPatterns() {
		return editionPatterns;
	}

	public void setEditionPatterns(Vector<EditionPattern> editionPatterns) {
		this.editionPatterns = editionPatterns;
	}

	public void addToEditionPatterns(EditionPattern pattern) {
		pattern.setVirtualModel(this);
		editionPatterns.add(pattern);
		setChanged();
		notifyObservers(new EditionPatternCreated(pattern));
	}

	public void removeFromEditionPatterns(EditionPattern pattern) {
		pattern.setVirtualModel(null);
		editionPatterns.remove(pattern);
		setChanged();
		notifyObservers(new EditionPatternDeleted(pattern));
	}

	/**
	 * Return EditionPattern matching supplied id represented as a string, which could be either the name of EditionPattern, or its URI
	 * 
	 * @param editionPatternId
	 * @return
	 */
	public EditionPattern getEditionPattern(String editionPatternId) {
		for (EditionPattern editionPattern : editionPatterns) {
			if (editionPattern.getName().equals(editionPatternId)) {
				return editionPattern;
			}
			if (editionPattern.getURI().equals(editionPatternId)) {
				return editionPattern;
			}
			// Special case to handle conversion from old VP version
			// TODO: to be removed when all VP are up-to-date
			if ((getViewPoint().getURI() + "#" + editionPattern.getName()).equals(editionPatternId)) {
				return editionPattern;
			}
		}
		// logger.warning("Not found EditionPattern:" + editionPatternId);
		return null;
	}

	public SynchronizationScheme createSynchronizationScheme() {
		SynchronizationScheme newSynchronizationScheme = new SynchronizationScheme();
		newSynchronizationScheme.setVirtualModel(this);
		newSynchronizationScheme.setName("synchronization");
		addToEditionSchemes(newSynchronizationScheme);
		return newSynchronizationScheme;
	}

	@Override
	public BindingModel getBindingModel() {
		if (bindingModel == null) {
			createBindingModel();
		}
		return bindingModel;
	}

	@Override
	public void updateBindingModel() {
		logger.fine("updateBindingModel()");
		bindingModel = null;
		createBindingModel();
		super.updateBindingModel();
	}

	private void createBindingModel() {
		bindingModel = new BindingModel();
		for (EditionPattern ep : getEditionPatterns()) {
			// bindingModel.addToBindingVariables(new EditionPatternPathElement<ViewPoint>(ep, this));
			bindingModel.addToBindingVariables(new BindingVariable(ep.getName(), EditionPatternInstanceType
					.getEditionPatternInstanceType(ep)));
		}
	}

	/*@Override
	public String simpleRepresentation() {
		return "VirtualModel:" + FlexoLocalization.localizedForKey(getLocalizedDictionary(), getName());
	}*/

	// ==========================================================================
	// ============================== Model Slots ===============================
	// ==========================================================================

	public void setModelSlots(List<ModelSlot<?>> modelSlots) {
		this.modelSlots = modelSlots;
	}

	public List<ModelSlot<?>> getModelSlots() {
		// System.out.println("getModelSlots=" + modelSlots);
		return modelSlots;
	}

	public void addToModelSlots(ModelSlot modelSlot) {
		// System.out.println("Add to model slots " + modelSlot);
		modelSlots.add(modelSlot);
		modelSlot.setVirtualModel(this);
		setChanged();
		notifyObservers(new ModelSlotAdded(modelSlot, this));
	}

	public void removeFromModelSlots(ModelSlot modelSlot) {
		// System.out.println("Remove from model slots " + modelSlot);
		modelSlots.remove(modelSlot);
		modelSlot.setVirtualModel(null);
		setChanged();
		notifyObservers(new ModelSlotRemoved(modelSlot, this));
	}

	public void deleteModelSlot(ModelSlot modelSlot) {
		removeFromModelSlots(modelSlot);
		modelSlot.delete();
	}

	public <MS extends ModelSlot> List<MS> getModelSlots(Class<MS> msType) {
		List<MS> returned = new ArrayList<MS>();
		for (ModelSlot ms : getModelSlots()) {
			if (TypeUtils.isTypeAssignableFrom(msType, ms.getClass())) {
				returned.add((MS) ms);
			}
		}
		return returned;
	}

	public ModelSlot getModelSlot(String modelSlotName) {
		for (ModelSlot ms : getModelSlots()) {
			if (ms.getName() != null && ms.getName().equals(modelSlotName)) {
				return ms;
			}
		}
		return null;
	}

	public List<ModelSlot> getRequiredModelSlots() {
		List<ModelSlot> requiredModelSlots = new ArrayList<ModelSlot>();
		for (ModelSlot modelSlot : getModelSlots()) {
			if (modelSlot.getIsRequired()) {
				requiredModelSlots.add(modelSlot);
			}
		}
		return requiredModelSlots;
	}

	/**
	 * Retrieve object referenced by its URI.<br>
	 * Note that search is performed in the scope of current project only
	 * 
	 * @param uri
	 * @return
	 */
	@Override
	public Object getObject(String uri) {
		for (FlexoMetaModel<?> mm : getAllReferencedMetaModels()) {
			if (mm != null) {
				Object o = mm.getObject(uri);
				if (o != null) {
					return o;
				}
			}
		}
		return null;
	}

	/**
	 * Retrieve ontology object from its URI.<br>
	 * Note that search is performed in the scope of current project only
	 * 
	 * @param uri
	 * @return
	 */
	public IFlexoOntologyObject getOntologyObject(String uri) {
		Object returned = getObject(uri);
		if (returned instanceof IFlexoOntologyObject) {
			return (IFlexoOntologyObject) returned;
		}
		return null;
	}

	/**
	 * Retrieve ontology class from its URI.<br>
	 * Note that search is performed in the scope of current project only
	 * 
	 * @param uri
	 * @return
	 */
	public IFlexoOntologyClass getOntologyClass(String uri) {
		Object returned = getOntologyObject(uri);
		if (returned instanceof IFlexoOntologyClass) {
			return (IFlexoOntologyClass) returned;
		}
		return null;
	}

	/**
	 * Retrieve ontology individual from its URI.<br>
	 * Note that search is performed in the scope of current project only
	 * 
	 * @param uri
	 * @return
	 */
	public IFlexoOntologyIndividual getOntologyIndividual(String uri) {
		Object returned = getOntologyObject(uri);
		if (returned instanceof IFlexoOntologyIndividual) {
			return (IFlexoOntologyIndividual) returned;
		}
		return null;
	}

	/**
	 * Retrieve ontology property from its URI.<br>
	 * Note that search is performed in the scope of current project only
	 * 
	 * @param uri
	 * @return
	 */
	public IFlexoOntologyStructuralProperty getOntologyProperty(String uri) {
		Object returned = getOntologyObject(uri);
		if (returned instanceof IFlexoOntologyStructuralProperty) {
			return (IFlexoOntologyStructuralProperty) returned;
		}
		return null;
	}

	/**
	 * Retrieve ontology object property from its URI.<br>
	 * Note that search is performed in the scope of current project only
	 * 
	 * @param uri
	 * @return
	 */
	public IFlexoOntologyObjectProperty getOntologyObjectProperty(String uri) {
		Object returned = getOntologyObject(uri);
		if (returned instanceof IFlexoOntologyObjectProperty) {
			return (IFlexoOntologyObjectProperty) returned;
		}
		return null;
	}

	/**
	 * Retrieve ontology object property from its URI.<br>
	 * Note that search is performed in the scope of current project only
	 * 
	 * @param uri
	 * @return
	 */
	public IFlexoOntologyDataProperty getOntologyDataProperty(String uri) {
		Object returned = getOntologyObject(uri);
		if (returned instanceof IFlexoOntologyDataProperty) {
			return (IFlexoOntologyDataProperty) returned;
		}
		return null;
	}

	/**
	 * Return true if URI is well formed and valid regarding its unicity (no one other object has same URI)
	 * 
	 * @param uri
	 * @return
	 */
	public boolean testValidURI(String ontologyURI, String conceptURI) {
		if (StringUtils.isEmpty(conceptURI)) {
			return false;
		}
		if (StringUtils.isEmpty(conceptURI.trim())) {
			return false;
		}
		return conceptURI.equals(ToolBox.getJavaName(conceptURI, true, false)) && !isDuplicatedURI(ontologyURI, conceptURI);
	}

	/**
	 * Return true if URI is duplicated in the context of this project
	 * 
	 * @param uri
	 * @return
	 */
	public boolean isDuplicatedURI(String modelURI, String conceptURI) {
		FlexoMetaModel<?> m = getMetaModel(modelURI);
		if (m != null) {
			return m.getObject(modelURI + "#" + conceptURI) != null;
		}
		return false;
	}

	/**
	 * Retrieve metamodel referenced by its URI<br>
	 * Note that search is performed in the scope of current project only
	 * 
	 * @param modelURI
	 * @return
	 */
	public FlexoMetaModel<?> getMetaModel(String metaModelURI) {
		for (FlexoMetaModel<?> m : getAllReferencedMetaModels()) {
			if (m.getURI().equals(metaModelURI)) {
				return m;
			}
		}
		return null;
	}

	/**
	 * Return the list of all metamodels used in the scope of this virtual model
	 * 
	 * @return
	 */
	@Deprecated
	public Set<FlexoMetaModel<?>> getAllReferencedMetaModels() {
		HashSet<FlexoMetaModel<?>> returned = new HashSet<FlexoMetaModel<?>>();
		for (ModelSlot modelSlot : getModelSlots()) {
			if (modelSlot instanceof TypeAwareModelSlot) {
				TypeAwareModelSlot tsModelSlot = (TypeAwareModelSlot) modelSlot;
				if (tsModelSlot.getMetaModelResource() != null) {
					returned.add(tsModelSlot.getMetaModelResource().getMetaModelData());
				}
			}
		}
		return returned;
	}

	@Override
	public Collection<ViewPointObject> getEmbeddedValidableObjects() {
		if (validableObjects == null) {
			validableObjects = new ChainedCollection<ViewPointObject>(getEditionPatterns(), getModelSlots());
		}
		return validableObjects;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public void setIsReadOnly(boolean b) {
		readOnly = b;
	}

	public FlexoVersion getModelVersion() {
		return getResource().getModelVersion();
	}

	public void setModelVersion(FlexoVersion aVersion) {
	}

	// Implementation of XMLStorageResourceData

	@Override
	public VirtualModelResource getResource() {
		return resource;
	}

	@Override
	public void setResource(FlexoResource<VirtualModel> resource) {
		this.resource = (VirtualModelResource) resource;
	}

	@Override
	public void save() {
		logger.info("Saving ViewPoint to " + getResource().getFile().getAbsolutePath() + "...");

		try {
			getResource().save(null);
		} catch (SaveResourceException e) {
			e.printStackTrace();
		}
	}

	@Override
	public TechnologyAdapter getTechnologyAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This is the builder used to deserialize {@link VirtualModel} objects.
	 * 
	 * @author sylvain
	 * 
	 */
	/*public static class VirtualModelBuilder {
		private VirtualModel virtualModel;
		private FlexoVersion modelVersion;
		private final ViewPointLibrary viewPointLibrary;
		private final ViewPoint viewPoint;
		VirtualModelResource resource;

		public VirtualModelBuilder(ViewPointLibrary vpLibrary, ViewPoint viewPoint, VirtualModelResource resource) {
			this.viewPointLibrary = vpLibrary;
			this.viewPoint = viewPoint;
			this.resource = resource;
		}

		public VirtualModelBuilder(ViewPointLibrary vpLibrary, ViewPoint viewPoint, VirtualModel virtualModel) {
			this.virtualModel = virtualModel;
			this.viewPointLibrary = vpLibrary;
			this.viewPoint = viewPoint;
			this.resource = virtualModel.getResource();
		}

		public VirtualModelBuilder(ViewPointLibrary vpLibrary, ViewPoint viewPoint, VirtualModelResource resource, FlexoVersion modelVersion) {
			this.modelVersion = modelVersion;
			this.viewPointLibrary = vpLibrary;
			this.viewPoint = viewPoint;
			this.resource = resource;
		}

		public ViewPointLibrary getViewPointLibrary() {
			return viewPointLibrary;
		}

		public FlexoVersion getModelVersion() {
			return modelVersion;
		}

		public VirtualModel getVirtualModel() {
			return virtualModel;
		}

		public void setVirtualModel(VirtualModel virtualModel) {
			this.virtualModel = virtualModel;
		}

		public ViewPoint getViewPoint() {
			return viewPoint;
		}
	}*/

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);
		out.append("VirtualModel " + getName() + " type=" + getClass().getSimpleName() + " uri=\"" + getURI() + "\"", context);
		out.append(" {" + StringUtils.LINE_SEPARATOR, context);

		if (getModelSlots().size() > 0) {
			out.append(StringUtils.LINE_SEPARATOR, context);
			for (ModelSlot modelSlot : getModelSlots()) {
				// if (modelSlot.getMetaModelResource() != null) {
				out.append(modelSlot.getFMLRepresentation(context), context, 1);
				out.append(StringUtils.LINE_SEPARATOR, context, 1);
				// }
			}
		}

		if (getPatternRoles().size() > 0) {
			out.append(StringUtils.LINE_SEPARATOR, context);
			for (PatternRole pr : getPatternRoles()) {
				out.append(pr.getFMLRepresentation(context), context, 1);
				out.append(StringUtils.LINE_SEPARATOR, context);
			}
		}

		if (getEditionSchemes().size() > 0) {
			out.append(StringUtils.LINE_SEPARATOR, context);
			for (EditionScheme es : getEditionSchemes()) {
				out.append(es.getFMLRepresentation(context), context, 1);
				out.append(StringUtils.LINE_SEPARATOR, context);
			}
		}

		if (getEditionPatterns().size() > 0) {
			out.append(StringUtils.LINE_SEPARATOR, context);
			for (EditionPattern ep : getEditionPatterns()) {
				out.append(ep.getFMLRepresentation(context), context, 1);
				out.append(StringUtils.LINE_SEPARATOR, context);
			}
		}
		out.append("}" + StringUtils.LINE_SEPARATOR, context);
		return out.toString();
	}

	/**
	 * Return flag indicating if supplied BindingVariable is set at runtime
	 * 
	 * @param variable
	 * @return
	 * @see VirtualModelInstance#getValueForVariable(BindingVariable)
	 */
	public boolean handleVariable(BindingVariable variable) {
		return false;
	}

}
