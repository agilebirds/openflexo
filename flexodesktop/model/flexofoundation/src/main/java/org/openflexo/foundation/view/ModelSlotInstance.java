package org.openflexo.foundation.view;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.xmlcode.XMLMapping;

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
public class ModelSlotInstance<MS extends ModelSlot<M, MM>, M extends FlexoModel<MM>, MM extends FlexoMetaModel> extends FlexoModelObject {

	private View view;
	private ModelSlot<M, MM> modelSlot;
	private M model;

	public ModelSlotInstance(View view) {
		super(view.getProject());
		this.view = view;
	}

	public ModelSlotInstance(View view, ModelSlot<M, MM> modelSlot) {
		this(view);
		this.modelSlot = modelSlot;
	}

	@Override
	public FlexoProject getProject() {
		return view.getProject();
	}

	@Override
	public String getFullyQualifiedName() {
		return getProject().getFullyQualifiedName() + "." + view.getShemaDefinition().getName() + "." + modelSlot.getName();
	}

	@Override
	public String getClassNameKey() {
		return "model_slot_association";
	}

	@Override
	public XMLMapping getXMLMapping() {
		// Not defined in this context
		return null;
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		// Not defined in this context
		return null;
	}

	public void setView(View view) {
		this.view = view;
	}

	public View getView() {
		return view;
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

}
