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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.xml.GeneratedCodeBuilder;
import org.openflexo.localization.FlexoLocalization;

public abstract class FlexoGeneratedOutputResource<GO extends GeneratedOutput> extends FlexoXMLStorageResource<GO> {

	private static final Logger logger = Logger.getLogger(FlexoGeneratedOutputResource.class.getPackage().getName());

	public FlexoGeneratedOutputResource(FlexoProject project) {
		super(project);
	}

	public FlexoGeneratedOutputResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	@Override
	public boolean hasBuilder() {
		return true;
	}

	@Override
	public Object instanciateNewBuilder() {
		GeneratedCodeBuilder builder = new GeneratedCodeBuilder(this);
		builder.generatedCode = _resourceData;
		return builder;
	}

	@Override
	protected boolean isDuplicateSerializationIdentifierRepairable() {
		return false;
	}

	@Override
	protected boolean repairDuplicateSerializationIdentifier() {
		return false;
	}

	@Override
	public String getName() {
		return getProject().getName();
	}

	@Override
	public GO performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler) throws LoadXMLResourceException,
			ProjectLoadingCancelledException, MalformedXMLException {
		GO cg;
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("loading_generated_data"));
		}
		if (logger.isLoggable(Level.FINE))
			logger.fine("performLoadResourceData() in GeneratedCodeResource");
		try {
			cg = super.performLoadResourceData(progress, loadingHandler);
		} catch (FlexoFileNotFoundException e) {
			if (logger.isLoggable(Level.SEVERE))
				logger.severe("File " + getFile().getName() + " NOT found");
			e.printStackTrace();
			return null;
		}
		cg.setProject(getProject());

		for (GenerationRepository repository : cg.getGeneratedRepositories()) {
			repository.updatePreferredTemplateRepository();
			repository.clearIsModified(true);
		}
		cg.clearIsModified(true);

		return cg;
	}

	public FlexoResource getFlexoResource() {
		return this;
	}

}
