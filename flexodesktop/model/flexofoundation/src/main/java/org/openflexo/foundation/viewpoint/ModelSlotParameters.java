package org.openflexo.foundation.viewpoint;

import java.util.logging.Level;

import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;

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
	private FlexoMetaModel<?> modelSlotMetaModel;

	/**
	 * <p>
	 * Creates a new ModelSlotParameters object with default values. </br>Note both name and meta-model don't have default values.
	 */
	public ModelSlotParameters() {
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

	public FlexoMetaModel<?> getModelSlotMetaModel() {
		return modelSlotMetaModel;
	}

	public void setModelSlotMetaModel(FlexoMetaModel<?> modelSlotMetaModel) {
		this.modelSlotMetaModel = modelSlotMetaModel;
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
	public <M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> ModelSlot<M, MM> create(ViewPoint viewPoint,
			TechnologyAdapter<M, MM> technologyAdapter) {
		if (hasEnoughInformations() == false) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Failed to create a new ModelSlot, some parameters are missing.");
			}
			return null;
		}

		ModelSlot<M, MM> newModelSlot = technologyAdapter.createNewModelSlot(viewPoint);
		newModelSlot.setName(this.getModelSlotName());
		newModelSlot.setMetaModel((MM) this.getModelSlotMetaModel());
		newModelSlot.setIsReadOnly(this.getModelSlotIsReadOnly());
		newModelSlot.setIsRequired(this.getModelSlotIsRequired());
		if (logger.isLoggable(Level.INFO)) {
			logger.warning("Created the ModelSlot " + newModelSlot.getName());
		}
		return newModelSlot;
	}

}
