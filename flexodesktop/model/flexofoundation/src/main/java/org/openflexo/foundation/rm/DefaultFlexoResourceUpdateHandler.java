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
import java.util.logging.Level;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;

public class DefaultFlexoResourceUpdateHandler extends FlexoResourceUpdateHandler {

	/**
     * 
     */
	public DefaultFlexoResourceUpdateHandler() {
	}

	@Override
	protected OptionWhenImportedResourceFoundAsModifiedOnDisk getOptionWhenImportedResourceFoundAsModifiedOnDisk(
			FlexoImportedResource resource) {
		return OptionWhenImportedResourceFoundAsModifiedOnDisk.Ignore;
	}

	@Override
	protected OptionWhenStorageResourceFoundAsConflicting getOptionWhenStorageResourceFoundAsConflicting(FlexoStorageResource resource) {
		return OptionWhenStorageResourceFoundAsConflicting.Ignore;
	}

	@Override
	protected OptionWhenStorageResourceFoundAsModifiedOnDisk getOptionWhenStorageResourceFoundAsModifiedOnDisk(FlexoStorageResource resource) {
		return OptionWhenStorageResourceFoundAsModifiedOnDisk.Ignore;
	}

	@Override
	public void handlesResourceUpdate(final FlexoFileResource fileResource) {
		if (fileResource instanceof FlexoGeneratedResource) {

			FlexoGeneratedResource generatedResource = (FlexoGeneratedResource) fileResource;

			if (logger.isLoggable(Level.INFO)) {
				logger.info("Update detected on resource " + generatedResource);
			}

			generatedResourceModified(generatedResource);
		}

		else if (fileResource instanceof CustomTemplatesResource) {
			logger.info("Updating " + fileResource);
			CustomCGTemplateRepository rep = fileResource.getProject().getGeneratedCode().getTemplates()
					.getCustomCGTemplateRepository((CustomTemplatesResource) fileResource);
			if (rep != null) {
				rep.update();
			}
			rep = fileResource.getProject().getGeneratedDoc().getTemplates()
					.getCustomCGTemplateRepository((CustomTemplatesResource) fileResource);
			if (rep != null) {
				rep.update();
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("NOT IMPLEMENTED: handlesResourceUpdate() for " + fileResource + ".");
			}
		}
	}

	@Override
	public void handlesResourcesUpdate(List<FlexoFileResource<? extends FlexoResourceData>> updatedResources) {
		for (FlexoFileResource<? extends FlexoResourceData> fileResource : updatedResources) {
			handlesResourceUpdate(fileResource);
		}
	}

	@Override
	public void reloadProject(FlexoStorageResource fileResource) throws NotImplementedException {
		throw new NotImplementedException("reload_project_not_implemented_in_non_interactive_mode");
	}

	@Override
	public void handleException(String unlocalizedMessage, FlexoException exception) {
		exception.printStackTrace();
		logger.warning(unlocalizedMessage);
	}

}
