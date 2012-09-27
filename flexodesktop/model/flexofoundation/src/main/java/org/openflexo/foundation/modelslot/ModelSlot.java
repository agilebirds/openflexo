package org.openflexo.foundation.modelslot;

import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.ProjectOntology;

/**
 * <p>
 * A model slot is a named object pointing to a meta-model ({@link FlexoOntology}). </br>It is defined at viewpoint level. </br>A view
 * (viewpoint instance) binds used slots to their models within the project.
 * 
 * @author Luka Le Roux
 * @see org.openflexo.foundation.viewpoint.ViewPoint
 * @see org.openflexo.foundation.view.View
 * 
 */
public interface ModelSlot<Ontology extends FlexoOntology> {

	public String getName();

	public TechnologicalSpace getTechnologicalSpace();

	public Ontology getMetaModel();

	public void setMetaModel(Ontology metaModel);

	public void setName(String name);

	public boolean getIsReadOnly();

	public void setIsReadOnly(boolean isReadOnly);

	public boolean getIsRequired();

	public void setIsRequired(boolean isRequired);

	/**
	 * <p>
	 * Returns an empty ontology to be used as model for this slot. </br>It matches the model slot's technological space. </br>The returned
	 * ontology also implements {@link ProjectOntology}.
	 * 
	 * @return an ontology
	 */
	public Ontology createEmptyModel();
}
