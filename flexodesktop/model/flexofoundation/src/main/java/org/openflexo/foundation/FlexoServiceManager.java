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
import java.util.List;

import org.openflexo.foundation.FlexoService.ServiceNotification;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;

/**
 * Default implementation of a manager of {@link FlexoService}<br>
 * All {@link FlexoService} are registered in the {@link FlexoServiceManager} which broadcast all service events to all services<br>
 * 
 * Please note that this class provides the basic support for Information Space<br>
 * The Information Space is obtained through two services from the {@link FlexoServiceManager}, and results from the merging of the
 * {@link FlexoResourceCenterService} and the {@link TechnologyAdapterService}.<br>
 * For each {@link FlexoResourceCenter} and for each {@link TechnologyAdapter}, a repository of {@link FlexoModel} and
 * {@link FlexoMetaModel} are managed.
 * 
 * 
 * @author sylvain
 * 
 */
public class FlexoServiceManager extends FlexoObject {

	private ArrayList<FlexoService> registeredServices;

	public FlexoServiceManager() {
		registeredServices = new ArrayList<FlexoService>();
	}

	@Override
	public String getFullyQualifiedName() {
		return "FlexoServiceManager";
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

	public List<FlexoService> getRegisteredServices() {
		return registeredServices;
	}

	public TechnologyAdapterService getTechnologyAdapterService() {
		return getService(TechnologyAdapterService.class);
	}

	public FlexoResourceCenterService getResourceCenterService() {
		return getService(FlexoResourceCenterService.class);
	}

	public ViewPointLibrary getViewPointLibrary() {
		return getService(ViewPointLibrary.class);
	}

	public class ServiceRegistered implements ServiceNotification {

	}
}
