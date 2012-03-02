/**
 * 
 */
package org.openflexo.foundation.sg.implmodel.event;

/**
 * Represents an object removed from list modification in one source generator model class.
 * 
 * @author Nicolas Daniels
 */
public class SGObjectRemovedFromListModification<T extends Object> extends SGDataModification {
	public SGObjectRemovedFromListModification(String listName, T object) {
		super(listName, object, null);
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
