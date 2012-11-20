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
package org.openflexo.foundation.cg.action;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openflexo.foundation.ExportException;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.foundation.ie.action.ImportImage;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FileUtils.CopyStrategy;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ZipUtils;

public class ExportDocumentationTemplates extends FlexoAction<ExportDocumentationTemplates, DGRepository, CGObject> {

	public static final FlexoActionType<ExportDocumentationTemplates, DGRepository, CGObject> actionType = new FlexoActionType<ExportDocumentationTemplates, DGRepository, CGObject>(
			"export_documentation_templates") {

		@Override
		protected boolean isEnabledForSelection(DGRepository object, Vector<CGObject> globalSelection) {
			return object != null;
		}

		@Override
		protected boolean isVisibleForSelection(DGRepository object, Vector<CGObject> globalSelection) {
			return object != null && object.getTocRepository() != null;
		}

		@Override
		public ExportDocumentationTemplates makeNewAction(DGRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new ExportDocumentationTemplates(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoModelObject.addActionForClass(ExportDocumentationTemplates.actionType, DGRepository.class);
	}

	private File zipFile;
	private File documentationGenerationDirectory;

	/**
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 */
	protected ExportDocumentationTemplates(DGRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	/**
	 * Overrides doAction
	 * 
	 * @see org.openflexo.foundation.action.FlexoAction#doAction(java.lang.Object)
	 */
	@Override
	protected void doAction(Object context) throws FlexoException {
		makeFlexoProgress("exporting_documentation_templates", 2);
		try {
			File rootTempDir = FileUtils.createTempDirectory("DocumentTemplates", getFocusedObject().getProject().getProjectName());
			File tempDir = new File(rootTempDir, getProject().getName() + "Templates");
			List<DGRepository> repos = new ArrayList<DGRepository>();
			repos.add(getFocusedObject());
			if (getGlobalSelection() != null) {
				for (CGObject object : getGlobalSelection()) {
					if (object instanceof DGRepository && ((DGRepository) object).getTocRepository() != null) {
						repos.add((DGRepository) object);
					}
				}
			}
			for (DGRepository repository : repos) {
				exportRepository(repository, tempDir, getFlexoProgress());
			}
			ZipUtils.makeZip(zipFile, tempDir, getFlexoProgress());
			FileUtils.deleteDir(rootTempDir);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExportException(FlexoLocalization.localizedForKey("error_while_creating_file") + " (" + e.getMessage() + ")", e);
		}

	}

	private void exportRepository(DGRepository repository, File tempDir, FlexoProgress progress) throws FlexoException {
		progress.resetSecondaryProgress(3);
		File repoDir = new File(tempDir, repository.getName());
		repoDir.mkdirs();
		File templatesDir = new File(repoDir, "Templates");
		File imagesDir = new File(repoDir, "Images");
		exportTOC(repository, repoDir, progress);
		exportTemplates(repository, templatesDir, progress);
		exportImages(imagesDir, progress);
	}

	private void exportImages(File imagesDir, FlexoProgress progress) throws FlexoException {
		progress.setSecondaryProgress(FlexoLocalization.localizedForKey("exporting_images"));
		try {
			FileUtils.copyContentDirToDir(getProject().getImportedImagesDir(), imagesDir, CopyStrategy.REPLACE, new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					for (File file : FlexoProject.INITIAL_IMAGES_DIR.listFiles()) {
						if (ImportImage.getImageNameFor(file.getName()).equals(pathname.getName())) {
							return false;
						}
					}
					return true;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			throw new FlexoException(FlexoLocalization.localizedForKey("could_not_export_images"));
		}
	}

	private void exportTemplates(DGRepository repository, File templatesDir, FlexoProgress progress) throws FlexoException {
		progress.setSecondaryProgress(FlexoLocalization.localizedForKey("exporting_templates"));
		CustomCGTemplateRepository templates = repository.getPreferredTemplateRepository();
		if (templates != null && templates.getAllTemplateFiles().size() > 0) {
			templatesDir.mkdirs();
			try {
				FileUtils.copyContentDirToDir(templates.getDirectory(), templatesDir);
			} catch (IOException e) {
				e.printStackTrace();
				throw new FlexoException(FlexoLocalization.localizedForKey("could_not_copy_templates") + " "
						+ repository.getTocRepository().getTitle());
			}
		}
	}

	public void exportTOC(DGRepository repository, File repoDir, FlexoProgress progress) throws FlexoException {
		progress.setSecondaryProgress(FlexoLocalization.localizedForKey("exporting_toc"));
		ExportTOCAsTemplate export = ExportTOCAsTemplate.actionType.makeNewEmbeddedAction(repository.getTocRepository(), null, this);
		String tocFileName = StringUtils.convertAccents(repository.getTocRepository().getTitle());
		tocFileName = StringUtils.replaceNonMatchingPatterns(tocFileName, ZipUtils.VALID_ENTRY_NAME_REGEXP, "-");
		export.setDestinationFile(new File(repoDir, tocFileName + ".toc.xml"));
		export.doAction();
		if (!export.hasActionExecutionSucceeded()) {
			throw new FlexoException(FlexoLocalization.localizedForKey("could_not_export_toc") + " " + tocFileName);
		}
	}

	protected FlexoProject getProject() {
		return getFocusedObject().getProject();
	}

	public File getZipFile() {
		return zipFile;
	}

	public void setZipFile(File zipFile) {
		this.zipFile = zipFile;
	}

	public File getDocumentationGenerationDirectory() {
		if (documentationGenerationDirectory == null) {
			documentationGenerationDirectory = new File(FileUtils.getDocumentFolder(), getProject().getName() + "/Documents");
		}
		return documentationGenerationDirectory;
	}

	public void setDocumentationGenerationDirectory(File documentationGenerationDirectory) {
		this.documentationGenerationDirectory = documentationGenerationDirectory;
	}
}
