/*
 * (c) Copyright 2010-2011 AgileBirds
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
package org.openflexo.foundation.rm;

import java.util.Date;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.ResourceData;

public interface StorageResourceData<RD extends StorageResourceData<RD>> extends FlexoResourceData, ResourceData<RD> {

	/**
	 * Return the resource related to this object
	 * 
	 * @return a FlexoResource instance
	 */
	@Override
	public FlexoStorageResource<RD> getFlexoResource();

	/**
	 * Save the resource related to this object (save the object) using RM scheme
	 * 
	 * @throws SaveResourceException
	 */
	public void save() throws SaveResourceException;

	/**
	 * Sets the resource to be modified
	 */
	public void setIsModified();

	/**
	 * Sets the resource to be not modified anymore. Also resets last memory update if required (default the passed value should be false)
	 */
	public void clearIsModified(boolean clearLastMemoryUpdate);

	/**
	 * Return a boolean indicating if resource related to this object is modified
	 * 
	 * @return
	 */
	public boolean isModified();

	/**
	 * Return the last date this object was modified in memory
	 * 
	 * @return
	 */
	public Date lastMemoryUpdate();

	/**
	 * 
	 * Notify
	 * 
	 * @see org.openflexo.foundation.rm.RMNotification to the related resource, in order that the ResourceManager handles and propagates
	 *      this notification
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#notifyRM(org.openflexo.foundation.rm.RMNotification)
	 */
	public void notifyRM(RMNotification notification) throws FlexoException;

	/**
	 * 
	 * Receive a notification that has been propagated by the ResourceManager scheme and coming from a modification on an other resource
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#notifyRM(org.openflexo.foundation.rm.RMNotification)
	 */
	public void receiveRMNotification(RMNotification notification) throws FlexoException;

}
