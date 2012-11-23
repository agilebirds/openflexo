package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.owl.OWLModelSlot;
import org.openflexo.foundation.ontology.xsd.XSModelSlot;

/**
 * <p>
 * Lists the supported technological spaces. </br>Also provides (eventually) meta informations about them.
 * 
 * @author Luka Le Roux
 * 
 */
public enum TechnologicalSpace {

	XSD {
		@Override
		protected XSModelSlot newSlot() {
			return new XSModelSlot();
		}
	},

	OWL {
		@Override
		protected OWLModelSlot newSlot() {
			return new OWLModelSlot();
		}
	};

	/**
	 * <p>
	 * Don't use this method. </br>To create a {@link ModelSlot} use the {@link ModelSlotParameters} class and its
	 * {@link #ModelSlotParameters.create()} method instead.
	 * 
	 * @return a new {@link ModelSlot}
	 */
	protected abstract ModelSlot<?> newSlot();

}
