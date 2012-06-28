package org.openflexo.toolbox;

import java.beans.PropertyChangeSupport;

public interface HasPropertyChangeSupport {

	/**
	 * Returns the property change support instance of this bean.
	 * 
	 * @return the property change support instance of this bean.
	 */
	public PropertyChangeSupport getPropertyChangeSupport();

	/**
	 * Returns the name of the property indicating whether the bean is deleted or not. This can be used by observers to detect if it is
	 * still relevant to observe that object. This method may return <code>null</code>.
	 * 
	 * @return the name of the property indicating whether the bean is deleted or not.
	 */
	public String getDeletedProperty();
}
