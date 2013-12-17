/**
 * 
 */
package org.openflexo.foundation.sg.implmodel.event;

/**
 * Represents an object added to list modification in one source generator model class.
 * 
 * @author Nicolas Daniels
 */
public class SGObjectAddedToListModification extends SGDataModification {

	public SGObjectAddedToListModification(String listName, Object object) {
		super(listName, null, object);
	}
}
