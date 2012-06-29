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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;
import java.util.zip.ZipException;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.JDOMException;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Format;
import org.openflexo.foundation.ImportException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.GeneratedDoc;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.foundation.cg.templates.action.AddCustomTemplateRepository;
import org.openflexo.foundation.cg.templates.action.ImportTemplates;
import org.openflexo.foundation.cg.utils.TemplateRepositoryType;
import org.openflexo.foundation.ie.action.ImportImage;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.foundation.toc.action.AddTOCRepository;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ZipUtils;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.InvalidXMLDataException;
import org.xml.sax.SAXException;

public class ImportDocumentationTemplates extends FlexoAction<ImportDocumentationTemplates, FlexoModelObject, FlexoModelObject> {

	public static final FlexoActionType<ImportDocumentationTemplates, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<ImportDocumentationTemplates, FlexoModelObject, FlexoModelObject>(
			"import_documentation_templates") {

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object != null;
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object != null;
		}

		@Override
		public ImportDocumentationTemplates makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection,
				FlexoEditor editor) {
			return new ImportDocumentationTemplates(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoModelObject.addActionForClass(ImportDocumentationTemplates.actionType, FlexoProject.class);
		FlexoModelObject.addActionForClass(ImportDocumentationTemplates.actionType, GeneratedDoc.class);
	}

	private File zipFile;
	private File documentationGenerationDirectory;

	/**
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 */
	protected ImportDocumentationTemplates(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	/**
	 * Overrides doAction
	 * 
	 * @see org.openflexo.foundation.action.FlexoAction#doAction(java.lang.Object)
	 */
	@Override
	protected void doAction(Object context) throws FlexoException {
		makeFlexoProgress("importing_documentation_templates", 2);
		try {
			File documentationTemplateDirectory = FileUtils.createTempDirectory("DocumentTemplates", ".tmp");
			ZipUtils.unzip(zipFile, documentationTemplateDirectory, getFlexoProgress());
			Collection<File> tocFiles = org.apache.commons.io.FileUtils.listFiles(documentationTemplateDirectory,
					new String[] { "toc.xml" }, true);
			makeFlexoProgress("importing_documentation_templates", tocFiles.size());
			for (File tocfile : tocFiles) {
				importTemplatesFromDir(tocfile);
			}
		} catch (ZipException e) {
			e.printStackTrace();
			throw new ImportException(FlexoLocalization.localizedForKey("invalid_zip_file") + " (" + e.getMessage() + ")", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ImportException(FlexoLocalization.localizedForKey("error_while_reading_file") + " (" + e.getMessage() + ")", e);
		} catch (InvalidXMLDataException e) {
			e.printStackTrace();
			throw new ImportException(FlexoLocalization.localizedForKey("invalid_xml_data") + " (" + e.getMessage() + ")", e);
		} catch (InvalidObjectSpecificationException e) {
			e.printStackTrace();
			throw new ImportException(FlexoLocalization.localizedForKey("invalid_object_specification") + " (" + e.getMessage() + ")", e);
		} catch (InvalidModelException e) {
			e.printStackTrace();
			throw new ImportException(FlexoLocalization.localizedForKey("invalid_model") + " (" + e.getMessage() + ")", e);
		} catch (AccessorInvocationException e) {
			e.printStackTrace();
			throw new ImportException(FlexoLocalization.localizedForKey("model_error") + " (" + e.getMessage() + ")", e);
		} catch (SAXException e) {
			e.printStackTrace();
			throw new ImportException(FlexoLocalization.localizedForKey("xml_error") + " (" + e.getMessage() + ")", e);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new ImportException(FlexoLocalization.localizedForKey("parser_configuration_exception") + " (" + e.getMessage() + ")", e);
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new ImportException(FlexoLocalization.localizedForKey("xml_error") + " (" + e.getMessage() + ")", e);
		}
	}

	private void importTemplatesFromDir(File tocfile) throws InvalidXMLDataException, InvalidObjectSpecificationException,
			InvalidModelException, AccessorInvocationException, FileNotFoundException, IOException, SAXException,
			ParserConfigurationException, JDOMException, FlexoException {
		final String name = tocfile.getParentFile().getName();
		File templatesDir = new File(tocfile.getParentFile(), "Templates");
		File imagesDir = new File(tocfile.getParentFile(), "Images");
		TOCRepository toc = importTOC(tocfile);
		CustomCGTemplateRepository templateRepository = importTemplates(name, templatesDir);
		importImages(imagesDir);
		GenerationRepository newGeneratedCodeRepository = createGeneratedDocRepository(name, toc);
		newGeneratedCodeRepository.setPreferredTemplateRepository(templateRepository);

	}

	private GenerationRepository createGeneratedDocRepository(final String name, TOCRepository toc) throws FlexoException {
		File documentationRepository = new File(documentationGenerationDirectory, name);
		documentationRepository.mkdirs();
		String attempt = name;
		int i = 0;
		while (getProject().getGeneratedDoc().getRepositoryNamed(attempt) != null) {
			attempt = name + "-" + i++;
		}
		AddGeneratedCodeRepository add = AddGeneratedCodeRepository.actionType.makeNewEmbeddedAction(getProject().getGeneratedDoc(), null,
				this);
		add.setNewGeneratedCodeRepositoryName(attempt);
		add.setTocRepository(toc);
		add.setNewGeneratedCodeRepositoryDirectory(documentationRepository);
		add.setNewDocType(toc.getDocType());
		add.setFormat(Format.DOCX);
		add.doAction();

		if (!add.hasActionExecutionSucceeded()) {
			throw new FlexoException(FlexoLocalization.localizedForKey("could_not_create_repository"));
		}
		GenerationRepository newGeneratedCodeRepository = add.getNewGeneratedCodeRepository();
		return newGeneratedCodeRepository;
	}

	private void importImages(File imagesDir) {
		if (imagesDir.exists() && imagesDir.isDirectory()) {
			java.io.FileFilter filter = new java.io.FileFilter() {

				@Override
				public boolean accept(File pathname) {
					if (pathname.isFile()) {
						String lower = pathname.getName().toLowerCase();
						return lower.endsWith(".jpg") || lower.endsWith(".gif") || lower.endsWith(".png");
					}
					return false;
				}
			};
			for (File image : imagesDir.listFiles(filter)) {
				ImportImage importImage = ImportImage.actionType.makeNewEmbeddedAction(getProject(), null, this);
				importImage.setFileToImport(image);
				importImage.doAction();
			}
		}
	}

	private CustomCGTemplateRepository importTemplates(final String name, File templatesDir) throws FlexoException {
		CustomCGTemplateRepository templateRepository = null;
		if (templatesDir.exists() && templatesDir.isDirectory()) {
			AddCustomTemplateRepository custom = AddCustomTemplateRepository.actionType.makeNewEmbeddedAction(getProject()
					.getGeneratedDoc().getTemplates(), null, this);
			String localizedForKey = FlexoLocalization.localizedForKey("templates");
			String templateName = null;
			if (name.toLowerCase().contains(localizedForKey)) {
				templateName = name;
			} else {
				templateName = name + " " + localizedForKey;
			}
			int i = 0;
			String attempt = templateName;
			while (getProject().getGeneratedDoc().getTemplates().getCustomCGTemplateRepositoryForName(attempt) != null) {
				attempt = templateName + "-" + i++;
			}
			templateName = attempt;
			i = 0;
			String templateDirName = name + "Templates";
			FlexoProjectFile attemptFile = new FlexoProjectFile(getProject(), templateDirName);
			while (getProject().resourceForFileName(attemptFile) != null) {
				attemptFile = new FlexoProjectFile(getProject(), templateDirName + "-" + i++);
			}
			templateDirName = attemptFile.getRelativePath();
			custom.setNewCustomTemplatesRepositoryName(templateName);
			custom.setNewCustomTemplatesRepositoryDirectory(new FlexoProjectFile(getProject(), templateDirName));
			custom.setRepositoryType(TemplateRepositoryType.Documentation);
			custom.setAssociateTemplateRepository(false);
			custom.doAction();
			if (!custom.hasActionExecutionSucceeded() || custom.getThrownException() != null) {
				throw new FlexoException(FlexoLocalization.localizedForKey("could_not_add_templates"));
			}
			templateRepository = custom.getNewCustomTemplatesRepository();
			ImportTemplates importTemplates = ImportTemplates.actionType.makeNewEmbeddedAction(getProject().getGeneratedDoc()
					.getTemplates().getApplicationRepository(), null, this);
			importTemplates.setExternalTemplateDirectory(templatesDir);
			importTemplates.setRepository(custom.getNewCustomTemplatesRepository());
			importTemplates.doAction();
			if (!importTemplates.hasActionExecutionSucceeded()) {
				throw new FlexoException(FlexoLocalization.localizedForKey("could_not_import_templates"));
			}
		}
		return templateRepository;
	}

	private TOCRepository importTOC(File tocfile) throws FlexoException {
		System.out.println("tocfile=" + tocfile.getAbsolutePath());
		AddTOCRepository addToc = AddTOCRepository.actionType.makeNewEmbeddedAction(getProject().getTOCData(), null, this);
		addToc.setTocTemplate(tocfile);
		addToc.doAction();
		if (!addToc.hasActionExecutionSucceeded()) {
			throw new FlexoException(FlexoLocalization.localizedForKey("could_not_create_toc"));
		}
		TOCRepository toc = addToc.getNewRepository();
		return toc;
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
