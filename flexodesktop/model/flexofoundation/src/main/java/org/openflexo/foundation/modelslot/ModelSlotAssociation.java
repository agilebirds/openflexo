package org.openflexo.foundation.modelslot;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.ProjectOntology;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.view.View;
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

	private FlexoProject project;
	private View view;
	private ModelSlot<?> modelSlot;
	private ProjectOntology model;

	public ModelSlotAssociation(FlexoProject project) {
		super(project);
		this.project = project;
	}

	public ModelSlotAssociation(FlexoProject project, View view, ModelSlot<?> modelSlot) {
		this(project);
		this.view = view;
		this.modelSlot = modelSlot;
	}

	@Override
	public FlexoProject getProject() {
		return project;
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO It's not really fully qualified (unique)
		return "ModelReference " + modelSlot.getName();
	}

	@Override
	public String getClassNameKey() {
		return "model_reference";
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
