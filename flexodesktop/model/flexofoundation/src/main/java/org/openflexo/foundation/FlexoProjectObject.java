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

import java.util.logging.Logger;

import org.openflexo.foundation.rm.FlexoProject;

/**
 * Super class for any object involved in Openflexo and beeing part of a {@link FlexoProject}<br>
 * 
 * @author sguerin
 * 
 */
public abstract class FlexoProjectObject extends FlexoObject {

	private static final Logger logger = Logger.getLogger(FlexoProjectObject.class.getPackage().getName());

	private FlexoProject project;

	public FlexoProject getProject() {
		return project;
	}

	public void setProject(FlexoProject project) {
		this.project = project;
	}
}
