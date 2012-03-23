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
package org.openflexo.foundation.rm;

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.utils.ProjectInitializerException;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class FlexoResourceUpdateHandler implements ResourceUpdateHandler {

	protected static final Logger logger = Logger.getLogger(FlexoResourceUpdateHandler.class.getPackage().getName());

	protected enum OptionWhenStorageResourceFoundAsConflicting {
		UpdateFromDisk, OverwriteDiskChange, Ignore, MergeChanges
	}

	protected enum OptionWhenStorageResourceFoundAsModifiedOnDisk {
		UpdateFromDisk, OverwriteDiskChange, Ignore
	}

	protected enum OptionWhenImportedResourceFoundAsModifiedOnDisk {
		UpdateFromDisk, Ignore
	}

	public abstract void reloadProject(FlexoStorageResource fileResource) throws FlexoException, ProjectInitializerException;

	protected abstract OptionWhenStorageResourceFoundAsConflicting getOptionWhenStorageResourceFoundAsConflicting(
			FlexoStorageResource resource);

	protected abstract OptionWhenStorageResourceFoundAsModifiedOnDisk getOptionWhenStorageResourceFoundAsModifiedOnDisk(
			FlexoStorageResource resource);

	protected abstract OptionWhenImportedResourceFoundAsModifiedOnDisk getOptionWhenImportedResourceFoundAsModifiedOnDisk(
			FlexoImportedResource resource);

	protected abstract void handleException(String unlocalizedMessage, FlexoException exception);

	protected void generatedResourceModified(FlexoGeneratedResource generatedResource) {
		if (generatedResource instanceof CGRepositoryFileResource) {
			((CGRepositoryFileResource) generatedResource).notifyResourceChangedOnDisk();
		}
	}

	protected void generatedResourcesModified(List<FlexoGeneratedResource<? extends GeneratedResourceData>> generatedResource) {
		for (FlexoGeneratedResource<? extends GeneratedResourceData> resource : generatedResource) {
			generatedResourceModified(resource);
		}
	}
}
