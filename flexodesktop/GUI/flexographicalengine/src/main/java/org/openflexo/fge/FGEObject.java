package org.openflexo.fge;

import org.openflexo.fge.GraphicalRepresentation.GRParameter;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.kvc.KeyValueCoding;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.xmlcode.XMLSerializable;

public interface FGEObject extends /*AccessibleProxyObject,*/XMLSerializable, Cloneable, IObservable, KeyValueCoding, FGEConstants,
		HasPropertyChangeSupport {

	public FGEModelFactory getFactory();

	public void setFactory(FGEModelFactory factory);

	public void notifyChange(GRParameter parameter, Object oldValue, Object newValue);

	public void notifyChange(GRParameter parameter);

	public void notifyAttributeChange(GRParameter parameter);

	public void notify(FGENotification notification);

	// *******************************************************************************
	// * Deletion management
	// *******************************************************************************

	/**
	 * Delete this object
	 */
	public void delete();

	/**
	 * Return a flag indicating if this object has been deleted
	 * 
	 * @return
	 */
	public boolean isDeleted();

}
