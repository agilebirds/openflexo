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
package org.openflexo.sg.generator;

import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.sg.file.SGTextFileResource;
import org.openflexo.sg.generationdef.FileEntry;


/**
 * 
 * @author sylvain
 */
public class SGTextFileGenerator extends SGGenerator<FlexoModelObject, GeneratedTextResource> {
	private static final Logger logger = FlexoLogger.getLogger(SGTextFileGenerator.class.getPackage().getName());

	protected SGTextFileResource textResource;

	protected SGTextFileGenerator(ModuleGenerator moduleGenerator, FileEntry fileEntry) {
		super(moduleGenerator, fileEntry);
		logger.info("Build new SGTextFileGenerator for " + fileEntry.name);
	}

	@Override
	public final void generate(boolean forceRegenerate) {
		if (!needGeneration(forceRegenerate))
			return;
		try {
			logger.info("Generate code for " + getFileEntry().name);
			startGeneration();
			String generatedText = formatGeneration(merge(getTemplateName(), defaultContext()));

			generatedCode = new GeneratedTextResource(getFileEntry().name, generatedText);
		} catch (GenerationException e) {
			setGenerationException(e);
		} catch (Exception e) {
			setGenerationException(new UnexpectedExceptionOccuredException(e, getProjectGenerator()));
		} finally {
			stopGeneration();
		}
	}

	@Override
	public GeneratedTextResource getGeneratedCode() {
		if (generatedCode == null && textResource != null && textResource.getTextFile() != null && textResource.getTextFile().hasLastAcceptedContent()) {
			generatedCode = new GeneratedTextResource(getFileEntry().name, textResource.getTextFile().getLastAcceptedContent());
		}
		return generatedCode;
	}

	@Override
	@Deprecated
	public final String getIdentifier() {
		return "<don't_use_this>";
		// return getEntityPackageName()+(getEntityPackageName().length()>0?".":"")+getEntityClassName();
	}

	public static String makeIdentifier(String fileName, String symbolicPathName, String relativePathName) {
		return symbolicPathName + File.separator + relativePathName + File.separator + fileName;
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	@Override
	public void buildResourcesAndSetGenerators(SourceRepository repository, Vector<CGRepositoryFileResource> resources) {
		// Nothing to do, performed in ModuleGenerator
	}

	public void rebuildDependanciesForResource(SGTextFileResource resource) {
		logger.warning("TODO: rebuildDependanciesForResource() !!!");
	}
}
