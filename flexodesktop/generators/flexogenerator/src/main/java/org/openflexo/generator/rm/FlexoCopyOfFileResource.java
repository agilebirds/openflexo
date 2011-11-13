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
package org.openflexo.generator.rm;

import java.util.logging.Logger;

import org.openflexo.foundation.rm.FlexoCopiedResource;
import org.openflexo.foundation.rm.FlexoFileResourceResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.cg.CopyOfFileResource;
import org.openflexo.generator.PackagedResourceToCopyGenerator;
import org.openflexo.generator.file.CGPackagedResourceFile;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.FileResource;

public class FlexoCopyOfFileResource extends
		CopyOfFileResource<CopyOfFileResourceData, PackagedResourceToCopyGenerator, CGPackagedResourceFile> implements
		GenerationAvailableFileResource {

	public static final Logger logger = FlexoLogger.getLogger(FlexoCopiedResource.class.getPackage().getName());

	public FlexoCopyOfFileResource(FlexoProjectBuilder builder) {
		this(builder.project);
	}

	/**
	 * @param aProject
	 */
	public FlexoCopyOfFileResource(FlexoProject aProject) {
		super(aProject);
	}

	/**
	 * @param aProject
	 */
	public FlexoCopyOfFileResource(FlexoProject aProject, FileResource resourceToCopy) {
		super(aProject, resourceToCopy);
	}

	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		if (getGenerator() != null && getGenerator().getSource() != null)
			addToDependantResources(FlexoFileResourceResource.getResource(getGenerator().getSource(), getProject()));
	}

	@Override
	public FileFormat getResourceFormat() {
		if (getGenerator() != null)
			return getGenerator().getFileFormat();
		return super.getResourceFormat();
	}

	@Override
	protected CopyOfFileResourceData createGeneratedResourceData() {
		return new CopyOfFileResourceData(this);
	}

}
