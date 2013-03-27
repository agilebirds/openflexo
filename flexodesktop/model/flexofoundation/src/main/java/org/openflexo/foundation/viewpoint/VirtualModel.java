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
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.VirtualModelResource;
import org.openflexo.foundation.rm.VirtualModelResourceImpl;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.viewpoint.ViewPointObject.LanguageRepresentationContext.LanguageRepresentationOutput;
import org.openflexo.foundation.viewpoint.dm.EditionPatternCreated;
import org.openflexo.foundation.viewpoint.dm.EditionPatternDeleted;
import org.openflexo.foundation.viewpoint.dm.ModelSlotAdded;
import org.openflexo.foundation.viewpoint.dm.ModelSlotRemoved;
import org.openflexo.localization.FlexoLocalization;
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
public class VirtualModel<VM extends VirtualModel<VM>> extends EditionPattern implements FlexoMetaModel<VM> {

	private static final Logger logger = Logger.getLogger(VirtualModel.class.getPackage().getName());

	private ViewPoint viewPoint;
	private Vector<EditionPattern> editionPatterns;
	private List<ModelSlot<?, ?>> modelSlots;
	private BindingModel bindingModel;
	private VirtualModelResource<VM> resource;
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
	public static VirtualModel<?> newVirtualModel(String baseName, ViewPoint viewPoint) {
		File diagramSpecificationDirectory = new File(viewPoint.getResource().getDirectory(), baseName);
		File diagramSpecificationXMLFile = new File(diagramSpecificationDirectory, baseName + ".xml");
		ViewPointLibrary viewPointLibrary = viewPoint.getViewPointLibrary();
		VirtualModelResource vmRes = VirtualModelResourceImpl.makeVirtualModelResource(diagramSpecificationDirectory,
				diagramSpecificationXMLFile, viewPoint.getResource(), viewPointLibrary);
		VirtualModel virtualModel = new VirtualModel(viewPoint);
		vmRes.setResourceData(virtualModel);
		virtualModel.setResource(vmRes);
		virtualModel.save();
		return virtualModel;
	}

	// Used during deserialization, do not use it
	public VirtualModel(VirtualModelBuilder builder) {
		super(builder);
		if (builder != null) {
			builder.setVirtualModel(this);
			resource = (VirtualModelResource<VM>) builder.resource;
			viewPoint = builder.getViewPoint();
		}
		modelSlots = new ArrayList<ModelSlot<?, ?>>();
		editionPatterns = new Vector<EditionPattern>();
	}

	/**
	 * Creates a new VirtualModel in supplied viewpoint
	 * 
	 * @param viewPoint
	 */
	public VirtualModel(ViewPoint viewPoint) {
		this((VirtualModel.VirtualModelBuilder) null);
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		for (EditionPattern ep : getEditionPatterns()) {
			ep.finalizeEditionPatternDeserialization();
		}
		super.finalizeDeserialization(builder);
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
	}

	private void createBindingModel() {
		bindingModel = new BindingModel();
		for (EditionPattern ep : getEditionPatterns()) {
			// bindingModel.addToBindingVariables(new EditionPatternPathElement<ViewPoint>(ep, this));
			bindingModel.addToBindingVariables(new BindingVariable(ep.getName(), ep));
		}
	}

	@Override
	public String simpleRepresentation() {
		return "VirtualModel:" + FlexoLocalization.localizedForKey(getLocalizedDictionary(), getName());
	}

	// ==========================================================================
	// ============================== Model Slots ===============================
	// ==========================================================================

	public void setModelSlots(List<ModelSlot<?, ?>> modelSlots) {
		this.modelSlots = modelSlots;
	}

	public List<ModelSlot<?, ?>> getModelSlots() {
		// System.out.println("getModelSlots=" + modelSlots);
		return modelSlots;
	}

	public void addToModelSlots(ModelSlot<?, ?> modelSlot) {
		// System.out.println("Add to model slots " + modelSlot);
		modelSlots.add(modelSlot);
		modelSlot.setVirtualModel(this);
		setChanged();
		notifyObservers(new ModelSlotAdded(modelSlot, this));
	}

	public void removeFromModelSlots(ModelSlot<?, ?> modelSlot) {
		// System.out.println("Remove from model slots " + modelSlot);
		modelSlots.remove(modelSlot);
		modelSlot.setVirtualModel(null);
		setChanged();
		notifyObservers(new ModelSlotRemoved(modelSlot, this));
	}

	public <MS extends ModelSlot<?, ?>> List<MS> getModelSlots(Class<MS> msType) {
		List<MS> returned = new ArrayList<MS>();
		for (ModelSlot<?, ?> ms : getModelSlots()) {
			if (TypeUtils.isTypeAssignableFrom(msType, ms.getClass())) {
				returned.add((MS) ms);
			}
		}
		return returned;
	}

	public ModelSlot<?, ?> getModelSlot(String modelSlotName) {
		for (ModelSlot<?, ?> ms : getModelSlots()) {
			if (ms.getName().equals(modelSlotName)) {
				return ms;
			}
		}
		return null;
	}

	public List<ModelSlot<?, ?>> getRequiredModelSlots() {
		List<ModelSlot<?, ?>> requiredModelSlots = new ArrayList<ModelSlot<?, ?>>();
		for (ModelSlot<?, ?> modelSlot : getModelSlots()) {
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
	 * Return the list of all models used in the scope of current project<br>
	 * To compute this this, iterate on each View, then each ModelSlotInstance
	 * 
	 * @return
	 */
	public Set<FlexoMetaModel<?>> getAllReferencedMetaModels() {
		HashSet<FlexoMetaModel<?>> returned = new HashSet<FlexoMetaModel<?>>();
		for (ModelSlot<?, ?> modelSlot : getModelSlots()) {
			if (modelSlot.getMetaModelResource() != null) {
				returned.add(modelSlot.getMetaModelResource().getMetaModelData());
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
	public VirtualModelResource<VM> getResource() {
		return resource;
	}

	@Override
	public void setResource(FlexoResource<VM> resource) {
		this.resource = (VirtualModelResource<VM>) resource;
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
	public TechnologyAdapter<?, ?> getTechnologyAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalizedDictionary getLocalizedDictionary() {
		if (localizedDictionary == null) {
			localizedDictionary = new LocalizedDictionary((VirtualModelBuilder) null);
			localizedDictionary.setOwner(this);
		}
		return localizedDictionary;
	}

	public void setLocalizedDictionary(LocalizedDictionary localizedDictionary) {
		localizedDictionary.setOwner(this);
		this.localizedDictionary = localizedDictionary;
	}

	/**
	 * This is the builder used to deserialize {@link VirtualModel} objects.
	 * 
	 * @author sylvain
	 * 
	 */
	public static class VirtualModelBuilder {
		private VirtualModel<?> virtualModel;
		private FlexoVersion modelVersion;
		private ViewPointLibrary viewPointLibrary;
		private ViewPoint viewPoint;
		VirtualModelResource<?> resource;

		public VirtualModelBuilder(ViewPointLibrary vpLibrary, ViewPoint viewPoint, VirtualModelResource<?> resource) {
			this.viewPointLibrary = vpLibrary;
			this.viewPoint = viewPoint;
			this.resource = resource;
		}

		public VirtualModelBuilder(ViewPointLibrary vpLibrary, ViewPoint viewPoint, VirtualModel<?> virtualModel) {
			this.virtualModel = virtualModel;
			this.viewPointLibrary = vpLibrary;
			this.viewPoint = viewPoint;
			this.resource = virtualModel.getResource();
		}

		public VirtualModelBuilder(ViewPointLibrary vpLibrary, ViewPoint viewPoint, VirtualModelResource<?> resource,
				FlexoVersion modelVersion) {
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

		public VirtualModel<?> getVirtualModel() {
			return virtualModel;
		}

		public void setVirtualModel(VirtualModel<?> virtualModel) {
			this.virtualModel = virtualModel;
		}

		public ViewPoint getViewPoint() {
			return viewPoint;
		}
	}

	@Override
	public String getLanguageRepresentation(LanguageRepresentationContext context) {
		// Voir du cote de GeneratorFormatter pour formatter tout ca
		LanguageRepresentationOutput out = new LanguageRepresentationOutput(context);
		out.append("VirtualModel " + getName() + " type=" + getClass().getSimpleName() + " uri=\"" + getURI() + "\"");
		out.append(" {" + StringUtils.LINE_SEPARATOR);

		for (ModelSlot<?, ?> modelSlot : getModelSlots()) {
			// out.append("Prout");
			if (modelSlot.getMetaModelResource() != null) {
				out.append(modelSlot);
			}
			// out.append("Hop");
		}

		out.append(StringUtils.LINE_SEPARATOR);
		for (EditionPattern ep : getEditionPatterns()) {
			out.append(ep);
			out.append(StringUtils.LINE_SEPARATOR);
		}
		out.append("}" + StringUtils.LINE_SEPARATOR);
		return out.toString();
	}

}
