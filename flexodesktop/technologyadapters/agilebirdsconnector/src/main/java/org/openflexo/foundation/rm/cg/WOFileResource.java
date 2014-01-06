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
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGFile.FileContentEditor;
import org.openflexo.foundation.cg.generator.COMPONENT_CODE_TYPE;
import org.openflexo.foundation.cg.generator.GeneratedComponent;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;

/**
 * @author sylvain
 * 
 */
public class WOFileResource<G extends IFlexoResourceGenerator, F extends CGFile> extends CGRepositoryFileResource<WOFile, G, F> {
	protected static final Logger logger = FlexoLogger.getLogger(WOFileResource.class.getPackage().getName());

	/**
	 * @param builder
	 */
	public WOFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	/**
	 * @param aProject
	 */
	public WOFileResource(FlexoProject aProject) {
		super(aProject);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.WO_FILE;
	}

	@Override
	public boolean isGeneratedResourceDataReadable() {
		return getFile().exists();
	}

	@Override
	public WOFile readGeneratedResourceData() throws LoadGeneratedResourceIOException {
		WOFile returned = createGeneratedResourceData();
		returned.load();
		return returned;
	}

	@Override
	protected WOFile createGeneratedResourceData() {
		return new WOFile(getFile());
	}

	public WOFile getWOFile() {
		return getGeneratedResourceData();
	}

	@Override
	public void saveEditedVersion(FileContentEditor editor) throws SaveGeneratedResourceIOException {
		FileWritingLock lock = willWriteOnDisk();

		// Creates directory when non existant
		if (!getFile().exists()) {
			getFile().mkdirs();
		}

		// Save content stored in supplied editor
		try {
			String name = getFile().getName().substring(0, getFile().getName().indexOf(".wo"));
			File htmlFile = new File(getFile(), name + ".html");
			File wodFile = new File(getFile(), name + ".wod");
			File wooFile = new File(getFile(), name + ".woo");
			FileUtils.saveToFile(htmlFile, editor.getEditedContentForKey(COMPONENT_CODE_TYPE.HTML.toString()));
			FileUtils.saveToFile(wodFile, editor.getEditedContentForKey(COMPONENT_CODE_TYPE.WOD.toString()));
			FileUtils.saveToFile(wooFile, editor.getEditedContentForKey(COMPONENT_CODE_TYPE.WOO.toString()));
			getGeneratedResourceData().notifyVersionChangedOnDisk(editor.getEditedContentForKey(COMPONENT_CODE_TYPE.HTML.toString()),
					editor.getEditedContentForKey(COMPONENT_CODE_TYPE.WOD.toString()),
					editor.getEditedContentForKey(COMPONENT_CODE_TYPE.WOO.toString()));
		}

		catch (IOException e) {
			hasWrittenOnDisk(lock);
			throw new SaveGeneratedResourceIOException(this, e);
		}

		hasWrittenOnDisk(lock);

	}

	public String getCurrentHTMLGeneration() {
		if (getGenerator() != null && !hasGenerationError() && getGenerator().getGeneratedCode() instanceof GeneratedComponent) {
			return ((GeneratedComponent) getGenerator().getGeneratedCode()).html();
		}
		return null;
	}

	public String getCurrentWODGeneration() {
		if (getGenerator() != null && !hasGenerationError() && getGenerator().getGeneratedCode() instanceof GeneratedComponent) {
			return ((GeneratedComponent) getGenerator().getGeneratedCode()).wod();
		}
		return null;
	}

	public String getCurrentWOOGeneration() {
		if (getGenerator() != null && !hasGenerationError() && getGenerator().getGeneratedCode() instanceof GeneratedComponent) {
			return ((GeneratedComponent) getGenerator().getGeneratedCode()).woo();
		}
		return null;
	}

}
