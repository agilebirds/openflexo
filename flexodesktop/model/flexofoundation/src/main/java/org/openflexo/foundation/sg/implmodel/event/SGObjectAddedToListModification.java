/**
 * 
 */
package org.openflexo.foundation.sg.implmodel.event;


/**
 * Represents an object added to list modification in one source generator model class.
 * 
 * @author Nicolas Daniels
 */
public class SGObjectAddedToListModification<T extends Object> extends SGDataModification {

	public SGObjectAddedToListModification(String listName, T object) {
		super(listName, null, object);
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
