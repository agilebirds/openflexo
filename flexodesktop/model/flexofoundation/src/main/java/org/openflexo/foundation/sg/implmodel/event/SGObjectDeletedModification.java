/**
 * 
 */
package org.openflexo.foundation.sg.implmodel.event;

import org.openflexo.foundation.FlexoModelObject;

/**
 * Represents a source generator model object deletion.
 * 
 * @author Nicolas Daniels
 */
public class SGObjectDeletedModification<T extends FlexoModelObject> extends SGDataModification {

	public SGObjectDeletedModification(T deletedObject) {
		super(deletedObject, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T oldValue() {
		return (T) super.oldValue();
	}
}
