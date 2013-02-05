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
package org.openflexo.foundation.view;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoXMLFileResource;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.VirtualModelInstanceResource;
import org.openflexo.foundation.rm.VirtualModelInstanceResourceImpl;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.xml.VirtualModelInstanceBuilder;
import org.openflexo.xmlcode.XMLMapping;

/**
 * A {@link VirtualModelInstance} is the run-time concept (instance) of a {@link VirtualModel}.<br>
 * 
 * As such, a {@link VirtualModelInstance} is instantiated inside a {@link View}, and all model slot defined for the corresponding
 * {@link ViewPoint} are instantiated (reified) with existing or build-in managed {@link FlexoModel}.<br>
 * 
 * A {@link VirtualModelInstance} mostly manages a collection of {@link EditionPatternInstance} and is itself an
 * {@link EditionPatternInstance}.<br>
 * 
 * A {@link VirtualModelInstance} might be used in the Design Space (for example to encode a Diagram)
 * 
 * @author sylvain
 * 
 */
public class VirtualModelInstance<VMI extends VirtualModelInstance<VMI, VM>, VM extends VirtualModel<VM>> extends EditionPatternInstance
		implements XMLStorageResourceData<VMI>, FlexoModel<VMI, VM> {

	private static final Logger logger = Logger.getLogger(VirtualModelInstance.class.getPackage().getName());

	private VirtualModelInstanceResource<VMI> resource;
	private List<ModelSlotInstance<?, ?>> modelSlotInstances;
	private Map<ModelSlot<?, ?>, FlexoModel<?, ?>> modelsMap = new HashMap<ModelSlot<?, ?>, FlexoModel<?, ?>>(); // Do not serialize this.
	private String title;
	private View view;

	public static VirtualModelInstanceResource<?> newVirtualModelInstance(String virtualModelName, String virtualModelTitle,
			VirtualModel virtualModel, View view) throws InvalidFileNameException, SaveResourceException {

		VirtualModelInstanceResource newVirtualModelResource = VirtualModelInstanceResourceImpl.makeVirtualModelInstanceResource(
				virtualModelName, virtualModel, view);

		VirtualModelInstance newVirtualModelInstance = new VirtualModelInstance(view, virtualModel);
		newVirtualModelResource.setResourceData(newVirtualModelInstance);
		newVirtualModelInstance.setResource(newVirtualModelResource);
		newVirtualModelInstance.setTitle(virtualModelTitle);

		newVirtualModelInstance.save();

		return newVirtualModelResource;
	}

	/**
	 * Constructor invoked during deserialization
	 */
	public VirtualModelInstance(VirtualModelInstanceBuilder builder) {
		this(builder.getView(), builder.getVirtualModel());
		builder.vmInstance = this;
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor for OEShema
	 * 
	 * @param shemaDefinition
	 */
	public VirtualModelInstance(View view, VirtualModel virtualModel) {
		super(virtualModel, view.getProject());
		logger.info("Created new VirtualModelInstance for virtual model " + virtualModel);
		this.view = view;
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public VM getEditionPattern() {
		return (VM) super.getEditionPattern();
	}

	public ViewPoint getViewPoint() {
		if (getVirtualModel() != null) {
			return getVirtualModel().getViewPoint();
		}
		return null;
	}

	public VM getVirtualModel() {
		return getEditionPattern();
	}

	@Override
	public VM getMetaModel() {
		return getEditionPattern();
	}

	@Override
	public TechnologyAdapter<?, ?> getTechnologyAdapter() {
		// TODO
		return null;
	}

	@Override
	public FlexoProject getProject() {
		if (getView() != null) {
			return getView().getProject();
		}
		return super.getProject();
	}

	@Override
	public Object getObject(String objectURI) {
		return null;
	}

	public Collection<EditionPatternInstance> getEPInstances(String epName) {
		if (getViewPoint() == null) {
			return Collections.emptyList();
		}
		EditionPattern ep = getVirtualModel().getEditionPattern(epName);
		return getEPInstances(ep);
	}

	public Collection<EditionPatternInstance> getEPInstances(EditionPattern ep) {
		/*Collection<DiagramShape> shapes = getChildrenOfType(DiagramShape.class);
		Collection<DiagramConnector> connectors = getChildrenOfType(DiagramConnector.class);
		Collection<EditionPatternInstance> epis = new LinkedHashSet<EditionPatternInstance>();
		for (DiagramShape shape : shapes) {
			EditionPatternReference epr = shape.getEditionPatternReference();
			if (epr == null) {
				continue;
			}
			if (epr.getEditionPattern() == ep) {
				epis.add(epr.getEditionPatternInstance());
			}
		}
		for (DiagramConnector conn : connectors) {
			EditionPatternReference epr = conn.getEditionPatternReference();
			if (epr == null) {
				continue;
			}
			if (epr.getEditionPattern() == ep) {
				epis.add(epr.getEditionPatternInstance());
			}
		}
		return epis;*/
		return null;
	}

	public List<EditionPatternInstance> getEPInstancesWithPropertyEqualsTo(String epName, String epProperty, Object value) {
		/*List<EditionPatternInstance> returned = new ArrayList<EditionPatternInstance>();
		Collection<EditionPatternInstance> epis = getEPInstances(epName);
		for (EditionPatternInstance epi : epis) {
			Object evaluate = epi.evaluate(epProperty);
			if (value == null && evaluate == value || value != null && value.equals(evaluate)) {
				returned.add(epi);
			}
		}
		return returned;*/
		return null;
	}

	@Deprecated
	@Override
	public FlexoStorageResource<VMI> getFlexoResource() {
		return null;
	}

	@Deprecated
	@Override
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {
	}

	@Override
	public FlexoXMLFileResource<VMI> getFlexoXMLFileResource() {
		return getResource();
	}

	@Override
	public VirtualModelInstanceResource<VMI> getResource() {
		return resource;
	}

	@Override
	public void setResource(org.openflexo.foundation.resource.FlexoResource<VMI> resource) {
		this.resource = (VirtualModelInstanceResource<VMI>) resource;
	}

	@Override
	public void save() throws SaveResourceException {
		getResource().save(null);
		// getFlexoResource().saveResourceData();
	}

	@Override
	public String getName() {
		if (getResource() != null) {
			return getResource().getName();
		}
		return null;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		String oldTitle = this.title;
		if (requireChange(oldTitle, title)) {
			this.title = title;
			setChanged();
			notifyObservers(new VEDataModification("title", oldTitle, title));
		}
	}

	@Override
	public String getFullyQualifiedName() {
		return getProject().getFullyQualifiedName() + "." + getName();
	}

	@Override
	public XMLMapping getXMLMapping() {
		return getProject().getXmlMappings().getShemaMapping();
	}

	@Override
	public String toString() {
		return "VirtualModelInstance[name=" + getName() + "/virtualModel=" + getVirtualModel() + "/hash=" + Integer.toHexString(hashCode())
				+ "]";
	}

	// ==========================================================================
	// ============================== Model Slots ===============================
	// ==========================================================================

	/**
	 * This is the binding point between a {@link ModelSlot} and its concretization in a {@link VirtualModelInstance} through notion of
	 * {@link ModelSlotInstance}
	 * 
	 * @param modelSlot
	 * @return
	 */
	public <M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> ModelSlotInstance<M, MM> getModelSlotInstance(
			ModelSlot<M, MM> modelSlot) {
		// TODO
		logger.warning("Please implement me");
		return null;
	}

	public void setModelSlotInstances(List<ModelSlotInstance<?, ?>> instances) {
		this.modelSlotInstances = instances;
		modelsMap.clear();
		for (ModelSlotInstance<?, ?> model : instances) {
			modelsMap.put(model.getModelSlot(), model.getModel());
		}
	}

	public List<ModelSlotInstance<?, ?>> getModelSlotInstances() {
		return modelSlotInstances;
	}

	public void removeFromModelSlotInstance(ModelSlotInstance<?, ?> instance) {
		modelSlotInstances.remove(instance);
		modelsMap.remove(instance.getModelSlot());
	}

	public void addToModelSlotInstances(ModelSlotInstance<?, ?> instance) {
		Iterator<ModelSlotInstance<?, ?>> it = modelSlotInstances.iterator();
		while (it.hasNext()) {
			if (it.next().getModelSlot().equals(instance.getModelSlot())) {
				it.remove();
			}
		}
		modelSlotInstances.add(instance);
		modelsMap.put(instance.getModelSlot(), instance.getModel());
	}

	public <M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> void setModel(ModelSlot<M, MM> modelSlot, M model) {
		modelsMap.put(modelSlot, model);
		for (ModelSlotInstance instance : modelSlotInstances) {
			if (instance.getModelSlot().equals(modelSlot)) {
				instance.setModel(model);
				return;
			}
		}
		ModelSlotInstance<M, MM> instance = new ModelSlotInstance<M, MM>(this, modelSlot);
		instance.setModel(model);
		modelSlotInstances.add(instance);
	}

	public <M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> M getModel(ModelSlot<M, MM> modelSlot, boolean createIfDoesNotExist) {
		M model = (M) modelsMap.get(modelSlot);
		if (createIfDoesNotExist && model == null) {
			try {
				org.openflexo.foundation.resource.FlexoResource<M> modelResource = modelSlot.createEmptyModel(getView(),
						modelSlot.getMetaModelResource());
				model = modelResource.getResourceData(null);
				setModel(modelSlot, model);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResourceLoadingCancelledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResourceDependencyLoopException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FlexoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return model;
	}

	public <M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> M getModel(ModelSlot<M, MM> modelSlot) {
		return getModel(modelSlot, true);
	}

	public Set<FlexoMetaModel> getAllMetaModels() {
		Set<FlexoMetaModel> allMetaModels = new HashSet<FlexoMetaModel>();
		for (ModelSlotInstance instance : getModelSlotInstances()) {
			if (instance.getModelSlot() != null && instance.getModelSlot().getMetaModelResource() != null) {
				allMetaModels.add(instance.getModelSlot().getMetaModelResource().getMetaModelData());
			}
		}
		return allMetaModels;
	}

	public Set<FlexoModel<?, ?>> getAllModels() {
		Set<FlexoModel<?, ?>> allModels = new HashSet<FlexoModel<?, ?>>();
		for (ModelSlotInstance instance : getModelSlotInstances()) {
			if (instance.getModel() != null) {
				allModels.add(instance.getModel());
			}
		}
		return allModels;
	}

	// ==========================================================================
	// ================================= Delete ===============================
	// ==========================================================================

	@Override
	public final void delete() {
		// tests on this deleted object
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("delete: View " + getName());
		}
		if (getFlexoResource() != null) {
			getFlexoResource().delete();
		}

		if (getFlexoResource() != null) {
			getFlexoResource().delete();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("View " + getName() + " has no ViewDefinition associated!");
			}
		}

		super.delete();

		setChanged();
		notifyObservers(new VirtualModelInstanceDeleted(this));
		deleteObservers();
	}

}
