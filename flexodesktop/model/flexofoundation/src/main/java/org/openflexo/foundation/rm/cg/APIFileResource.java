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
package org.openflexo.foundation.rm.cg;

import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.generator.COMPONENT_CODE_TYPE;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.ResourceType;

/**
 * @author sylvain
 * 
 */
public class APIFileResource<G extends IFlexoResourceGenerator, F extends CGFile> extends ASCIIFileResource<G, F> {

	public APIFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	public APIFileResource(FlexoProject aProject) {
		super(aProject);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.API_FILE;
	}

	@Override
	protected APIFile createGeneratedResourceData() {
		return new APIFile(getFile());
	}

	public APIFile getAPIFile() {
		return (APIFile) getGeneratedResourceData();
	}

	@Override
	public String getGenerationResultKey() {
		return COMPONENT_CODE_TYPE.API.toString();
	}

}
