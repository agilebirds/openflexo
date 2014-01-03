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

import org.openflexo.foundation.technologyadapter.InformationSpace;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;

/**
 * Super class for any object involved in Openflexo and beeing part of a {@link FlexoProject}<br>
 * Provides a direct access to {@link FlexoServiceManager} (and all services) through {@link FlexoProject}<br>
 * Also provides support for storing references to {@link EditionPatternInstance}
 * 
 * @author sguerin
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(FlexoProjectObject.FlexoProjectObjectImpl.class)
public interface FlexoProjectObject extends FlexoObject {

	@PropertyIdentifier(type = FlexoProject.class)
	public static final String PROJECT = "project";

	@Getter(value = PROJECT, ignoreType = true)
	public FlexoProject getProject();

	@Setter(PROJECT)
	public void setProject(FlexoProject project);

	@Override
	public FlexoServiceManager getServiceManager();

	public InformationSpace getInformationSpace();

	public abstract class FlexoProjectObjectImpl extends DefaultFlexoObject implements FlexoProjectObject {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(FlexoProjectObject.class.getPackage().getName());

		private FlexoProject project;

		public FlexoProjectObjectImpl() {
			super();
		}

		public FlexoProjectObjectImpl(FlexoProject project) {
			this();
			this.project = project;
		}

		@Override
		public boolean delete() {

			project = null;
			return super.delete();

		}

		@Override
		public FlexoProject getProject() {
			return project;
		}

		@Override
		public void setProject(FlexoProject project) {
			this.project = project;
		}

		// TODO: Should be refactored with injectors
		@Override
		public FlexoServiceManager getServiceManager() {
			if (getProject() != null) {
				return getProject().getServiceManager();
			}
			return null;
		}

		@Override
		public InformationSpace getInformationSpace() {
			if (getServiceManager() != null) {
				return getServiceManager().getInformationSpace();
			}
			return null;
		}

	}
}
