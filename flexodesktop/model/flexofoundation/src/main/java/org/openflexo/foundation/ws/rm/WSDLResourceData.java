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
package org.openflexo.foundation.ws.rm;

import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoWSDLResource;
import org.openflexo.foundation.rm.ImportedResourceData;
import org.openflexo.foundation.utils.FlexoProjectFile;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class WSDLResourceData extends FlexoObservable implements ImportedResourceData {

	private FlexoProject _project;

	private FlexoWSDLResource _resource;

	public WSDLResourceData(FlexoProject aProject, FlexoWSDLResource resource) {
		super();
		_project = aProject;
		_resource = resource;
	}

	public FlexoProjectFile getWSDLFile() {
		return _resource.getResourceFile();
	}

	@Override
	public FlexoWSDLResource getFlexoResource() {
		return _resource;
	}

	@Override
	public void setFlexoResource(FlexoResource resource) {
		_resource = (FlexoWSDLResource) resource;
	}

	@Override
	public FlexoProject getProject() {
		return _project;
	}

	@Override
	public void setProject(FlexoProject aProject) {
		_project = aProject;
	}

}
