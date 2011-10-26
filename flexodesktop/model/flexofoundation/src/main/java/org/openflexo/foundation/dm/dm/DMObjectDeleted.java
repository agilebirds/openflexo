/**
 * 
 */
package org.openflexo.foundation.dm.dm;

import org.openflexo.foundation.FlexoModelObject;

/**
 * Represents a source generator model object deletion.
 * 
 * @author Nicolas Daniels
 */
public class DMObjectDeleted<T extends FlexoModelObject> extends DMDataModification {

	public DMObjectDeleted(T deletedObject) {
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
