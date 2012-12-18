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

import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.technologyadapter.DefaultTechnologyAdapterService;
import org.openflexo.foundation.technologyadapter.InformationSpace;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.xml.XMLSerializationService;

/**
 * Default implementation of {@link FlexoServiceManager}
 * 
 * 
 * @author sylvain
 * 
 */
public abstract class DefaultFlexoServiceManager extends FlexoServiceManager {

	public DefaultFlexoServiceManager() {
		XMLSerializationService xmlSerializationService = XMLSerializationService.createInstance();
		registerService(xmlSerializationService);
		FlexoProjectReferenceLoader projectReferenceLoader = createProjectReferenceLoader();
		if (projectReferenceLoader != null) {
			registerService(projectReferenceLoader);
		}
		FlexoResourceCenterService resourceCenterService = createResourceCenterService();
		registerService(resourceCenterService);
		TechnologyAdapterService technologyAdapterService = createTechnologyAdapterService(resourceCenterService);
		registerService(technologyAdapterService);
		InformationSpace informationSpace = createInformationSpace();
		registerService(informationSpace);
		ViewPointLibrary viewPointLibrary = createViewPointLibraryService();
		registerService(viewPointLibrary);
	}

	@Override
	protected XMLSerializationService createXMLSerializationService() {
		return XMLSerializationService.createInstance();
	}

	@Override
	protected FlexoResourceCenterService createResourceCenterService() {
		return DefaultResourceCenterService.getNewInstance();
	}

	@Override
	protected TechnologyAdapterService createTechnologyAdapterService(FlexoResourceCenterService resourceCenterService) {
		return DefaultTechnologyAdapterService.getNewInstance(resourceCenterService);
	}

	@Override
	protected ViewPointLibrary createViewPointLibraryService() {
		return new ViewPointLibrary();
	}

	@Override
	protected InformationSpace createInformationSpace() {
		return new InformationSpace();
	}

}
