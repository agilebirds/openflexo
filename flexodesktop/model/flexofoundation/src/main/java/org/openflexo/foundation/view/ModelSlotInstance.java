package org.openflexo.foundation.view;

import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;

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
		return modelSlot;
	}

	public void setModel(M model) {
		this.model = model;
	}

	public M getModel() {
		return model;
	}

	@Override
	public String getClassNameKey() {
		return "model_slot_instance";
	}

}
