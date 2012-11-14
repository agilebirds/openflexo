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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGFile.FileContentEditor;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.toolbox.FileUtils;

/**
 * @author sylvain
 * 
 */
public abstract class ASCIIFileResource<G extends IFlexoResourceGenerator, F extends CGFile> extends
		CGRepositoryFileResource<ASCIIFile, G, F> {

	private static final Logger logger = Logger.getLogger(ASCIIFileResource.class.getPackage().getName());

	public ASCIIFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	public ASCIIFileResource(FlexoProject aProject) {
		super(aProject);
	}

	@Override
	protected abstract ASCIIFile createGeneratedResourceData();

	@Override
	public boolean isGeneratedResourceDataReadable() {
		return getFile().exists();
	}

	@Override
	public ASCIIFile readGeneratedResourceData() throws LoadGeneratedResourceIOException {
		ASCIIFile returned = createGeneratedResourceData();
		returned.load();
		return returned;
	}

	public ASCIIFile getASCIIFile() {
		return getGeneratedResourceData();
	}

	public String getCurrentGeneration() {
		if (getGenerator() != null && !hasGenerationError()) {
			if (getGenerator().getGeneratedCode() == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Generator is not null and there are no generation errors but the generated code is null");
				}
				return null;
			}

			return getGenerator().getGeneratedCode().get(getGenerationResultKey());
		}
		return null;
	}

	public abstract String getGenerationResultKey();

	@Override
	public void saveEditedVersion(FileContentEditor editor) throws SaveGeneratedResourceIOException {
		File path = getFile().getParentFile();

		// Creates directory when non existant
		if (!path.exists()) {
			path.mkdirs();
		}

		// Save content stored in supplied editor
		FileWritingLock lock = null;
		try {
			lock = willWriteOnDisk();
			FileUtils.saveToFile(getFile(), getEditedVersion(editor));
			hasWrittenOnDisk(lock);
		}

		catch (IOException e) {
			hasWrittenOnDisk(lock);
			throw new SaveGeneratedResourceIOException(this, e);
		}

		getGeneratedResourceData().notifyVersionChangedOnDisk(getEditedVersion(editor));
	}

	public String getEditedVersion(FileContentEditor editor) {
		return editor.getEditedContentForKey(getGenerationResultKey());
	}

}
