/**
 * 
 */
package org.openflexo.foundation.sg.implmodel.event;

/**
 * Represents an object removed from list modification in one source generator model class.
 * 
 * @author Nicolas Daniels
 */
public class SGObjectRemovedFromListModification extends SGDataModification {
	public SGObjectRemovedFromListModification(String listName, Object object) {
		super(listName, object, null);
	}
}
