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
package org.openflexo.foundation.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.view.rm.ViewResource;
import org.openflexo.foundation.view.rm.ViewResourceImpl;
import org.openflexo.foundation.view.rm.VirtualModelInstanceResource;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.toolbox.FlexoVersion;

/**
 * A {@link View} is the run-time concept (instance) of a {@link ViewPoint}.<br>
 * 
 * As such, a {@link View} is instantiated inside a {@link FlexoProject}, and all model slot defined for the corresponding {@link ViewPoint}
 * are instantiated (reified) with existing or build-in managed {@link FlexoModel}.
 * 
 * @author sylvain
 * 
 */
public class View extends ViewObject implements ResourceData<View> {

	private static final Logger logger = Logger.getLogger(View.class.getPackage().getName());

	private ViewResource resource;
	private List<VirtualModelInstance> vmInstances;
	private List<ModelSlotInstance<?, ?>> modelSlotInstances;
	private String title;

	public static View newView(String viewName, String viewTitle, ViewPoint viewPoint, RepositoryFolder<ViewResource> folder,
			FlexoProject project) throws SaveResourceException {

		ViewResource newViewResource = ViewResourceImpl.makeViewResource(viewName, folder, viewPoint, project.getViewLibrary());

		View newView = new View(project);
		newViewResource.setResourceData(newView);
		newView.setResource(newViewResource);

		newView.setTitle(viewTitle);

		// Save it
		newViewResource.save(null);

		// newView.save();

		return newView;
	}

	/**
	 * Default Constructor
	 * 
	 */
	public View(FlexoProject project, ViewResource resource/*ViewBuilder builder*/) {
		super(project/*builder.getProject()*/);
		setResource(resource/*builder.getResource()*/);
		// builder.view = this;
		// initializeDeserialization(builder);
	}

	/**
	 * Default constructor for OEShema
	 * 
	 * @param shemaDefinition
	 */
	public View(FlexoProject project) {
		super(project);
		logger.info("Created new view with project " + project);
		vmInstances = new ArrayList<VirtualModelInstance>();
		modelSlotInstances = new ArrayList<ModelSlotInstance<?, ?>>();

	}

	/*@Override
	public String getURI() {
		if (getResource() == null) {
			return super.getURI();
		} else {
			return getResource().getURI();
		}
	}*/

	@Override
	public View getView() {
		return this;
	}

	@Override
	public FlexoProject getProject() {
		if (getResource() != null) {
			return getResource().getProject();
		}
		return super.getProject();
	}

	@Override
	public View getResourceData() {
		return this;
	}

	@Override
	public ViewResource getResource() {
		return resource;
	}

	@Override
	public void setResource(org.openflexo.foundation.resource.FlexoResource<View> resource) {
		this.resource = (ViewResource) resource;
	}

	/*@Override
	public void save() throws SaveResourceException {
		getResource().save(null);
	}*/

	/*@Override
	public String getClassNameKey() {
		return "view";
	}*/

	public String getName() {
		if (getResource() != null) {
			return getResource().getName();
		}
		return null;
	}

	public void setName(String name) {
		if (getResource() != null) {
			getResource().setName(name);
		}
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

	public ViewPoint getViewPoint() {
		if (getResource() != null) {
			return getResource().getViewPoint();
		}
		return null;
	}

	@Override
	public String toString() {
		return "View[name=" + getName() + "/viewpoint=" + (getViewPoint() != null ? getViewPoint().getName() : "null") + "/hash="
				+ Integer.toHexString(hashCode()) + "]";
	}

	// ==========================================================================
	// ======================== Virtual Model Instances =========================
	// ==========================================================================

	public List<VirtualModelInstance> getVirtualModelInstances() {
		loadVirtualModelInstancesWhenUnloaded();
		return vmInstances;
	}

	public void setVirtualModelInstances(List<VirtualModelInstance> instances) {
		this.vmInstances = instances;
	}

	public void addToVirtualModelInstances(VirtualModelInstance vmInstance) {
		vmInstances.add(vmInstance);
	}

	public void removeFromVirtualModelInstances(VirtualModelInstance vmInstance) {
		vmInstances.remove(vmInstance);
	}

	/**
	 * Load eventually unloaded VirtualModelInstances<br>
	 * After this call return, we can assert that all {@link VirtualModelInstance} are loaded.
	 */
	private void loadVirtualModelInstancesWhenUnloaded() {
		for (org.openflexo.foundation.resource.FlexoResource<?> r : getResource().getContents()) {
			if (r instanceof VirtualModelInstanceResource) {
				((VirtualModelInstanceResource) r).getVirtualModelInstance();
			}
		}
	}

	public VirtualModelInstance getVirtualModelInstance(String name) {
		for (VirtualModelInstance vmi : getVirtualModelInstances()) {
			String lName = vmi.getName();
			if (lName != null){
				if (vmi.getName().equals(name)) {
					return vmi;
				}
			}
			else{
				logger.warning("Name of VirtualModel is null: " + this.toString());
			}
		}
		return null;
	}

	public boolean isValidVirtualModelName(String virtualModelName) {
		return getVirtualModelInstance(virtualModelName) == null;
	}

	// ==========================================================================
	// ============================== Model Slots ===============================
	// ==========================================================================

	/**
	 * This is the binding point between a {@link ModelSlot} and its concretization in a {@link View} through notion of
	 * {@link ModelSlotInstance}
	 * 
	 * @param modelSlot
	 * @return
	 */
	public <RD extends ResourceData<RD>> ModelSlotInstance<?, RD> getModelSlotInstance(ModelSlot<RD> modelSlot) {
		for (ModelSlotInstance<?, ?> msInstance : getModelSlotInstances()) {
			if (msInstance.getModelSlot() == modelSlot) {
				return (ModelSlotInstance<?, RD>) msInstance;
			}
		}
		return null;
	}

	public void setModelSlotInstances(List<ModelSlotInstance<?, ?>> instances) {
		this.modelSlotInstances = instances;
	}

	public List<ModelSlotInstance<?, ?>> getModelSlotInstances() {
		return modelSlotInstances;
	}

	public void removeFromModelSlotInstance(ModelSlotInstance<?, ?> instance) {
		modelSlotInstances.remove(instance);
	}

	public void addToModelSlotInstances(ModelSlotInstance<?, ?> instance) {
		modelSlotInstances.add(instance);
	}

	/*public <M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> void setModel(ModelSlot modelSlot, M model) {
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

	public <M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> M getModel(ModelSlot modelSlot, boolean createIfDoesNotExist) {
		M model = (M) modelsMap.get(modelSlot);
		if (createIfDoesNotExist && model == null) {
			try {
				org.openflexo.foundation.resource.FlexoResource<M> modelResource = modelSlot.createEmptyModel(this,
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

	public <M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> M getModel(ModelSlot modelSlot) {
		return getModel(modelSlot, true);
	}*/

	@Deprecated
	public Set<FlexoMetaModel<?>> getAllMetaModels() {
		Set<FlexoMetaModel<?>> allMetaModels = new HashSet<FlexoMetaModel<?>>();
		for (ModelSlotInstance<?, ?> instance : getModelSlotInstances()) {
			if (instance.getModelSlot() instanceof TypeAwareModelSlot
					&& ((TypeAwareModelSlot) instance.getModelSlot()).getMetaModelResource() != null) {
				allMetaModels.add(((TypeAwareModelSlot) instance.getModelSlot()).getMetaModelResource().getMetaModelData());
			}
		}
		return allMetaModels;
	}

	@Deprecated
	public Set<FlexoModel<?, ?>> getAllModels() {
		Set<FlexoModel<?, ?>> allModels = new HashSet<FlexoModel<?, ?>>();
		for (ModelSlotInstance<?, ?> instance : getModelSlotInstances()) {
			if (instance.getResourceData() instanceof FlexoModel) {
				allModels.add(instance.getResourceData());
			}
		}
		return allModels;
	}

	// ==========================================================================
	// ================================= Delete ===============================
	// ==========================================================================

	@Override
	public final boolean delete() {

		logger.info("Deleting view " + this);

		// Delete the view resource from the view library
		// Dereference the resource
		if (getProject() != null && getProject().getViewLibrary() != null && resource != null) {
			getProject().getViewLibrary().unregisterResource(resource);
			resource = null;
		}

		// Delete view
		super.delete();

		// Delete observers
		deleteObservers();

		return true;
	}

	public ViewLibrary getViewLibrary() {
		return getProject().getViewLibrary();
	}

	public RepositoryFolder<ViewResource> getFolder() {
		if (getResource() != null) {
			return getViewLibrary().getParentFolder(getResource());
		}
		return null;
	}

	public String getViewPointURI() {
		if (getViewPoint() != null) {
			return getViewPoint().getURI();
		}
		return null;
	}

	// Not applicable
	public void setViewPointURI(String viewPointURI) {
	}

	public FlexoVersion getViewPointVersion() {
		if (getViewPoint() != null) {
			return getViewPoint().getVersion();
		}
		return null;
	}

	// Not applicable
	public void setViewPointVersion(FlexoVersion viewPointVersion) {
	}

}
