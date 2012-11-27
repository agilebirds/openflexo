package org.openflexo.foundation.viewpoint;

import java.util.logging.Level;

import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.technologyadapter.ModelSlot;

/**
 * <p>
 * Stores all the informations needed to create a new {@link ModelSlot}. </br>Provides a {@link #Create()} method to do so.
 * 
 * @author Luka Le Roux
 * 
 */
public class ModelSlotParameters extends TemporaryFlexoModelObject {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(ModelSlotParameters.class
			.getPackage().getName());

	private String modelSlotName;
	private boolean modelSlotIsRequired;
	private boolean modelSlotIsReadOnly;
	private IFlexoOntology modelSlotMetaModel;

	private TechnologicalSpace modelSlotTechnologicalSpace;

	/**
	 * <p>
	 * Creates a new ModelSlotParameters object with default values. </br>Note both name and meta-model don't have default values.
	 */
	public ModelSlotParameters() {
		setModelSlotTechnologicalSpace(TechnologicalSpace.OWL);
		setModelSlotIsReadOnly(true);
		setModelSlotIsRequired(true);
	}

	public String getModelSlotName() {
		return modelSlotName;
	}

	public void setModelSlotName(String modeSlotName) {
		this.modelSlotName = modeSlotName;
	}

	public boolean getModelSlotIsRequired() {
		return modelSlotIsRequired;
	}

	public void setModelSlotIsRequired(boolean modelSlotIsRequired) {
		this.modelSlotIsRequired = modelSlotIsRequired;
	}

	public boolean getModelSlotIsReadOnly() {
		return modelSlotIsReadOnly;
	}

	public void setModelSlotIsReadOnly(boolean modelSlotIsReadOnly) {
		this.modelSlotIsReadOnly = modelSlotIsReadOnly;
	}

	public IFlexoOntology getModelSlotMetaModel() {
		return modelSlotMetaModel;
	}

	public void setModelSlotMetaModel(IFlexoOntology modelSlotMetaModel) {
		this.modelSlotMetaModel = modelSlotMetaModel;
	}

	public TechnologicalSpace getModelSlotTechnologicalSpace() {
		return modelSlotTechnologicalSpace;
	}

	public void setModelSlotTechnologicalSpace(TechnologicalSpace modeSlotTechnologicalSpace) {
		this.modelSlotTechnologicalSpace = modeSlotTechnologicalSpace;
	}

	/**
	 * <p>
	 * Check if both the name and the meta-model aren't null.
	 * 
	 * @return a boolean
	 */
	public boolean hasEnoughInformations() {
		return getModelSlotName() != null && getModelSlotMetaModel() != null;
	}

	/**
	 * <p>
	 * The created {@link ModelSlot} will match the provided {@link TechnologicalSpace} and other parameters. </br>It will return null if
	 * some parameters are still missing.
	 * 
	 * @return a new {@link ModelSlot}
	 * @see #hasEnoughInformations()
	 */
	public ModelSlot<IFlexoOntology> create() {
		if (hasEnoughInformations() == false) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Failed to create a new ModelSlot, some parameters are missing.");
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		ModelSlot<IFlexoOntology> newModelSlot = (ModelSlot<IFlexoOntology>) getModelSlotTechnologicalSpace().newSlot();
		newModelSlot.setName(this.getModelSlotName());
		newModelSlot.setMetaModel(this.getModelSlotMetaModel());
		newModelSlot.setIsReadOnly(this.getModelSlotIsReadOnly());
		newModelSlot.setIsRequired(this.getModelSlotIsRequired());
		if (logger.isLoggable(Level.INFO)) {
			logger.warning("Created the ModelSlot " + newModelSlot.getName());
		}
		return newModelSlot;
	}

}
