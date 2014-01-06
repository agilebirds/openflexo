/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fge;

import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.model.factory.CloneableProxyObject;
import org.openflexo.model.factory.DeletableProxyObject;
import org.openflexo.model.factory.KeyValueCoding;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.xmlcode.XMLSerializable;

/**
 * Root interface for all model objects involved in Diana modelling layer
 * 
 * @author sylvain
 * 
 */
public interface FGEObject extends AccessibleProxyObject, DeletableProxyObject, CloneableProxyObject, XMLSerializable, Cloneable,
		KeyValueCoding, FGEConstants, HasPropertyChangeSupport {

	// TODO: remove this, don't reference the factory in the object
	@Deprecated
	public FGEModelFactory getFactory();

	// TODO: remove this, don't reference the factory in the object
	@Deprecated
	public void setFactory(FGEModelFactory factory);

	public <T> void notifyChange(GRParameter<T> parameter, T oldValue, T newValue);

	public <T> void notifyChange(GRParameter<T> parameter);

	public <T> void notifyAttributeChange(GRParameter<T> parameter);

	public void notify(FGEAttributeNotification notification);

	// *******************************************************************************
	// * Deletion management
	// *******************************************************************************

	/**
	 * Delete this object
	 */
	@Override
	public boolean delete(Object... context);

	/**
	 * Return a flag indicating if this object has been deleted
	 * 
	 * @return
	 */
	@Override
	public boolean isDeleted();

	/**
	 * Clone this FGE object using persistant properties defined as PAMELA model
	 * 
	 * @return
	 */
	public Object clone();
}
