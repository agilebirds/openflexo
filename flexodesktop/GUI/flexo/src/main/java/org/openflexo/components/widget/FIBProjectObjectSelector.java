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
package org.openflexo.components.widget;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.FlexoProjectObject;

/**
 * Widget allowing to select an {@link FlexoProjectObject} (an object located in a {@link FlexoProject})<br>
 * 
 * Defines the scope of a project where to look-up a {@link FlexoProjectObject}
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public abstract class FIBProjectObjectSelector<T extends FlexoProjectObject> extends FIBFlexoObjectSelector<T> {

	static final Logger logger = Logger.getLogger(FIBProjectObjectSelector.class.getPackage().getName());

	private FlexoProject project;

	public FIBProjectObjectSelector(T editedObject) {
		super(editedObject);
	}

	@Override
	public void delete() {
		super.delete();
		project = null;
	}

	public FlexoProject getProject() {
		return project;
	}

	@CustomComponentParameter(name = "project", type = CustomComponentParameter.Type.MANDATORY)
	public void setProject(FlexoProject project) {
		if (this.project != project) {
			FlexoProject oldProject = this.project;
			if (project == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Passing null project. If you rely on project this is unlikely to work");
				}
			}
			System.out.println(">>>>>>>>> Sets project with " + project);
			this.project = project;
			getPropertyChangeSupport().firePropertyChange("project", oldProject, project);
		}
	}

}