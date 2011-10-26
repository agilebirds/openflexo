/**
 * 
 */
package org.openflexo.foundation.sg.implmodel.event;

import org.openflexo.foundation.FlexoModelObject;

/**
 * Represents a new source generator model object creation.
 * 
 * @author Nicolas Daniels
 */
public class SGObjectCreatedModification<T extends FlexoModelObject> extends SGDataModification {

	public SGObjectCreatedModification(T createdObject) {
		super(null, createdObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T newValue() {
		return (T) super.newValue();
	}
}
