package org.openflexo.foundation.view;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.ProjectOntology;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.viewpoint.ModelSlot;
import org.openflexo.xmlcode.XMLMapping;

/**
 * <p>
 * Binds a model (ProjectOntology) to a View and a {@link ModelSlot}.
 * 
 * @author Luka Le Roux
 * @see org.openflexo.foundation.ontology.ProjectOntology
 * @see org.openflexo.foundation.view.View
 * 
 */
public class ModelSlotAssociation extends FlexoModelObject {

	private View view;
	private ModelSlot<?> modelSlot;
	private ProjectOntology model;

	public ModelSlotAssociation(View view) {
		super(view.getProject());
		this.view = view;
	}

	public ModelSlotAssociation(View view, ModelSlot<?> modelSlot) {
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

	public void setModelSlot(ModelSlot<?> modelSlot) {
		this.modelSlot = modelSlot;
	}

	public ModelSlot<?> getModelSlot() {
		return modelSlot;
	}

	public void setModel(ProjectOntology model) {
		this.model = model;
	}

	public ProjectOntology getModel() {
		return model;
	}

}
