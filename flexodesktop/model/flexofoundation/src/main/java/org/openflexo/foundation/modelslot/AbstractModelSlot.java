package org.openflexo.foundation.modelslot;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.view.View;

/**
 * <p>
 * Abstract implementation of the {@link ModelSlot} interface.
 * 
 * @author Luka Le Roux
 * 
 * @param <Ontology>
 */
public abstract class AbstractModelSlot<Ontology extends FlexoOntology> extends FlexoModelObject implements ModelSlot<Ontology> {

	private String name;
	private boolean isRequired;
	private boolean isReadOnly;
	private Ontology metaModel;

	/**
	 * <p>
	 * Don't use this default constructor. </br>To create a {@link ModelSlot} use the {@link ModelSlotParameters} class and its
	 * {@link #ModelSlotParameters.create()} method instead.
	 */
	protected AbstractModelSlot() {

	}

	@Override
	public Ontology getMetaModel() {
		return metaModel;
	}

	@Override
	public void setMetaModel(Ontology metaModel) {
		this.metaModel = metaModel;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean getIsReadOnly() {
		return isReadOnly;
	}

	@Override
	public void setIsReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	@Override
	public boolean getIsRequired() {
		return isRequired;
	}

	@Override
	public void setIsRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	@Override
	public abstract Ontology createEmptyModel(View view);

}
