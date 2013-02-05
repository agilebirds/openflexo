package org.openflexo.foundation.view;

import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.xml.ViewBuilder;
import org.openflexo.foundation.xml.VirtualModelInstanceBuilder;
import org.openflexo.toolbox.StringUtils;

/**
 * Concretize the binding of a {@link ModelSlot} to a concrete {@link FlexoModel}
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
		// Not defined in this context
		return null;
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
			model = (M) getVirtualModelInstance().getInformationSpace().getModel(modelURI);
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

}
