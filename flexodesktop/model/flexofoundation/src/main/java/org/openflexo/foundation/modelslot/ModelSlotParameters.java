package org.openflexo.foundation.modelslot;

import java.util.logging.Level;

import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.ProjectOntology;

/**
 * <p>
 * Stores all the informations needed to create a new {@link ModelSlot}. </br>Provides a {@link #Create()} method to do so.
 * 
 * @author Luka Le Roux
 * 
 */
public class ModelSlotParameters extends AbstractModelSlot<FlexoOntology> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(ModelSlotParameters.class
			.getPackage().getName());

	private TechnologicalSpace technologicalSpace;

	/**
	 * <p>
	 * Creates a new ModelSlotParameters object with default values. </br>Note both name and meta-model don't have default values.
	 */
	public ModelSlotParameters() {
		setTechnologicalSpace(TechnologicalSpace.OWL);
		setIsReadOnly(true);
		setIsRequired(true);
	}

	@Override
	public TechnologicalSpace getTechnologicalSpace() {
		return technologicalSpace;
	}

	public void setTechnologicalSpace(TechnologicalSpace technologicalSpace) {
		this.technologicalSpace = technologicalSpace;
	}

	@Override
	public ProjectOntology createEmptyModel() {
		throw new UnsupportedOperationException("Can't create an empty model from a ModelSlotParameters object");
	}

	/**
	 * <p>
	 * Check if both the name and the meta-model aren't null.
	 * 
	 * @return a boolean
	 */
	public boolean hasEnoughInformations() {
		return getName() != null && getMetaModel() != null;
	}

	/**
	 * <p>
	 * The created {@link ModelSlot} will match the provided {@link TechnologicalSpace} and other parameters. </br>It will return null if
	 * some parameters are still missing.
	 * 
	 * @return a new {@link ModelSlot}
	 * @see #hasEnoughInformations()
	 */
	public ModelSlot<FlexoOntology> create() {
		if (hasEnoughInformations() == false) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Failed to create a new ModelSlot, some parameters are missing.");
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		ModelSlot<FlexoOntology> newModelSlot = (ModelSlot<FlexoOntology>) technologicalSpace.newSlot();
		newModelSlot.setName(this.getName());
		newModelSlot.setMetaModel(this.getMetaModel());
		newModelSlot.setIsReadOnly(this.getIsReadOnly());
		newModelSlot.setIsRequired(this.getIsRequired());
		if (logger.isLoggable(Level.INFO)) {
			logger.warning("Created the ModelSlot " + newModelSlot.getName());
		}
		return newModelSlot;
	}
}
