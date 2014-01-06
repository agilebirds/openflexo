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
package org.openflexo.foundation;

/**
 * Implements a service (an object with operational state) in the Openflexo architecture. A {@link FlexoService} is responsible for a
 * particular task, and works in conjunction with other services within a {@link FlexoServiceManager} which receives and broadcast all
 * {@link ServiceNotification} to all registered services.
 * 
 * @author sylvain
 * 
 */
public interface FlexoService {

	/**
	 * Called by the {@link FlexoServiceManager} to register the service manager
	 * 
	 * @param serviceManager
	 */
	public void register(FlexoServiceManager serviceManager);

	/**
	 * Return the {@link FlexoServiceManager} where this {@link FlexoService} is registered. If not registered return null. This is the
	 * implemenation responsability to register itself the service manager
	 * 
	 * @return
	 */
	public FlexoServiceManager getServiceManager();

	/**
	 * Called after registration, after all services have been notified that this service has been registered
	 */
	public void initialize();

	/**
	 * Receives a new {@link ServiceNotification} broadcasted from the {@link FlexoServiceManager}
	 * 
	 * @param caller
	 * @param notification
	 */
	public void receiveNotification(FlexoService caller, ServiceNotification notification);

	/**
	 * A notification broadcasted by the {@link FlexoServiceManager}
	 * 
	 * @author sylvain
	 * 
	 */
	public static interface ServiceNotification {

	}
}
