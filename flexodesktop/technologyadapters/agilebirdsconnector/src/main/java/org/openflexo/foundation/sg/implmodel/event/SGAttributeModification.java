/**
 * 
 */
package org.openflexo.foundation.sg.implmodel.event;

/**
 * Represents an attribute modification in one source generator model class.
 * 
 * @author Nicolas Daniels
 */
public class SGAttributeModification extends SGDataModification {

	public SGAttributeModification(String propertyName, Object oldValue, Object newValue) {
		super(propertyName, oldValue, newValue);
	}
}
