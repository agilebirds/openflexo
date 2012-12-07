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

import java.util.ArrayList;

import org.openflexo.foundation.FlexoService.ServiceNotification;

/**
 * Default implementation of a manager of {@link FlexoService}<br>
 * All {@link FlexoService} are registered in the FlexoServiceManager which broadcast all service events to all services
 * 
 * @author sylvain
 * 
 */
public class FlexoServiceManager {

	private ArrayList<FlexoService> registeredServices;

	public FlexoServiceManager() {
		registeredServices = new ArrayList<FlexoService>();
	}

	/**
	 * Register the supplied service, by adding it in the list of all services managed by this {@link FlexoServiceManager} instance<br>
	 * Notify all already registered services that a new service has been registered, then initialize the service itself.
	 * 
	 * @param service
	 */
	public void registerService(FlexoService service) {
		registeredServices.add(service);
		service.register(this);
		notify(service, new ServiceRegistered());
		service.initialize();
	}

	public void notify(FlexoService caller, ServiceNotification notification) {
		for (FlexoService s : registeredServices) {
			if (s != caller) {
				s.receiveNotification(caller, notification);
			}
		}
	}

	public <S extends FlexoService> S getService(Class<S> serviceClass) {
		for (FlexoService s : registeredServices) {
			if (serviceClass.isAssignableFrom(s.getClass())) {
				return (S) s;
			}
		}
		return null;
	}

	public class ServiceRegistered implements ServiceNotification {

	}
}
