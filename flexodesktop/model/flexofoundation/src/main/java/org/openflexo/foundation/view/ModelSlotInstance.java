/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2013 Openflexo
 *
 * This file is part of Openflexo.
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
 * along with Openflexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.foundation.view;

import java.util.logging.Logger;

import org.openflexo.foundation.rm.VirtualModelInstanceResource;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.viewpoint.VirtualModelModelSlot;
import org.openflexo.foundation.xml.ViewBuilder;
import org.openflexo.foundation.xml.VirtualModelInstanceBuilder;
import org.openflexo.toolbox.StringUtils;

/**
 * Concretize the binding of a {@link ModelSlot} to a concrete {@link FlexoModel}<br>
 * This is the binding point between a {@link ModelSlot} and its concretization in a {@link VirtualModelInstance}
 * 
 * The {@link ModelSlotInstance} are instantiated inside a {@link View}
 * 
 * @author Luka Le Roux, Sylvain Guerin
 * @see ModelSlot
 * @see FlexoModel
 * @see View
 * 
 */
public class ModelSlotInstance<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends VirtualModelInstanceObject {

	private static final Logger logger = Logger.getLogger(ModelSlotInstance.class.getPackage().getName());

	private View view;
	private VirtualModelInstance<?, ?> vmInstance;
	private ModelSlot<M, MM> modelSlot;
	private M model;
	// Serialization/deserialization only, do not use
	private String modelURI;
	// Serialization/deserialization only, do not use
	private String modelSlotName;

	/**
	 * Constructor invoked during deserialization
	 * 
	 */
	public ModelSlotInstance(ViewBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	/**
	 * Constructor invoked during deserialization
	 * 
	 */
	public ModelSlotInstance(VirtualModelInstanceBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	public ModelSlotInstance(View view, ModelSlot<M, MM> modelSlot) {
		super(view.getProject());
		this.view = view;
		this.modelSlot = modelSlot;
	}

	public ModelSlotInstance(VirtualModelInstance<?, ?> vmInstance, ModelSlot<M, MM> modelSlot) {
		super(vmInstance.getProject());
		this.vmInstance = vmInstance;
		this.view = vmInstance.getView();
		this.modelSlot = modelSlot;
	}

	@Override
	public String getFullyQualifiedName() {
		return getProject().getFullyQualifiedName() + "." + view.getName() + "." + modelSlot.getName();
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return getVirtualModelInstance();
	}

	public void setView(View view) {
		this.view = view;
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public VirtualModelInstance<?, ?> getVirtualModelInstance() {
		return vmInstance;
	}

	public void setVirtualModelInstance(VirtualModelInstance<?, ?> vmInstance) {
		this.vmInstance = vmInstance;
	}

	public void setModelSlot(ModelSlot<M, MM> modelSlot) {
		this.modelSlot = modelSlot;
	}

	public ModelSlot<M, MM> getModelSlot() {
		if (getVirtualModelInstance() != null && modelSlot == null && StringUtils.isNotEmpty(modelSlotName)) {
			modelSlot = (ModelSlot<M, MM>) getVirtualModelInstance().getVirtualModel().getModelSlot(modelSlotName);
		}
		return modelSlot;
	}

	public void setModel(M model) {
		this.model = model;
	}

	public M getModel() {
		if (getVirtualModelInstance() != null && model == null && StringUtils.isNotEmpty(modelURI)) {
			if (getModelSlot() instanceof VirtualModelModelSlot) {
				VirtualModelInstanceResource vmiResource = getProject().getViewLibrary().getVirtualModelInstance(modelURI);
				if (vmiResource != null) {
					model = (M) vmiResource.getVirtualModelInstance();
				}
			} else {
				FlexoModelResource<M, MM> modelResource = (FlexoModelResource<M, MM>) getVirtualModelInstance().getInformationSpace()
						.getModelWithURI(modelURI);
				if (modelResource != null) {
					model = modelResource.getModel();
				}
			}
		}
		if (model == null && StringUtils.isNotEmpty(modelURI)) {
			logger.warning("cannot find model " + modelURI);
		}
		return model;
	}

	// Serialization/deserialization only, do not use
	public String getModelURI() {
		if (getModel() != null) {
			return getModel().getURI();
		}
		return modelURI;
	}

	// Serialization/deserialization only, do not use
	public void setModelURI(String modelURI) {
		this.modelURI = modelURI;
	}

	// Serialization/deserialization only, do not use
	public String getModelSlotName() {
		if (getModelSlot() != null) {
			return getModelSlot().getName();
		}
		return modelSlotName;
	}

	// Serialization/deserialization only, do not use
	public void setModelSlotName(String modelSlotName) {
		this.modelSlotName = modelSlotName;
	}

	@Override
	public String getClassNameKey() {
		return "model_slot_instance";
	}

	@Override
	public String toString() {
		return "ModelSlotInstance:"
				+ (getModelSlot() != null ? getModelSlot().getName() + ":" + getModelSlot().getClass().getSimpleName() + "_"
						+ (getName() != null ? getName() : getFlexoID()) : "null");
	}
}
