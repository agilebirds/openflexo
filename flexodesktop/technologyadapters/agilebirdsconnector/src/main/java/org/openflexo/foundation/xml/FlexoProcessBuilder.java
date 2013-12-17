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
package org.openflexo.foundation.xml;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.rm.FlexoProcessResource;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;

/**
 * Used only during XML serialization to build process
 * 
 * @author sguerin
 * 
 */
public class FlexoProcessBuilder extends FlexoBuilder<FlexoProcessResource> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FlexoProcessBuilder.class.getPackage().getName());

	public FlexoProcess process = null;

	public String defaultProcessName = null;

	public FlexoWorkflow workflow = null;

	public boolean isBeingCloned = false;

	public FlexoProcessBuilder(FlexoProcessResource resource) {
		super(resource);
	}

	public FlexoProcessBuilder(FlexoProject project) {
		super(null);
		setProject(project);
	}

}
