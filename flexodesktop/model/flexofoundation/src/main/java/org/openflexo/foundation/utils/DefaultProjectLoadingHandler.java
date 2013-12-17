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
package org.openflexo.foundation.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.logging.FlexoLogger;

public class DefaultProjectLoadingHandler implements ProjectLoadingHandler {
	private static final Logger logger = FlexoLogger.getLogger(DefaultProjectLoadingHandler.class.getPackage().getName());

	@Override
	public boolean loadAndConvertAllOldResourcesToLatestVersion(FlexoProject project, FlexoProgress progress)
			throws ProjectLoadingCancelledException {
		return false;
	}

	@Override
	public boolean useOlderMappingWhenLoadingFailure(FlexoXMLStorageResource resource) throws ProjectLoadingCancelledException {
		return true;
	}

	@Override
	public boolean upgradeResourceToLatestVersion(FlexoXMLStorageResource resource) throws ProjectLoadingCancelledException {
		return true;
	}

	@Override
	public void notifySevereLoadingFailure(FlexoResource r, Exception e) {
		if (logger.isLoggable(Level.SEVERE)) {
			logger.log(Level.SEVERE, "Error loading resource " + r.getFullyQualifiedName(), e);
		}
	}
}
