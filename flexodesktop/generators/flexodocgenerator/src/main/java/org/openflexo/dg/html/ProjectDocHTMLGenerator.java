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
package org.openflexo.dg.html;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.dg.ProjectDocGenerator;
import org.openflexo.dg.rm.GeneratedFileResourceFactory;
import org.openflexo.dg.rm.ProcessFolderJSFileResource;
import org.openflexo.dg.rm.ProcessJSFileResource;
import org.openflexo.dg.rm.ProjectHTMLFileResource;
import org.openflexo.dg.rm.WorkflowTextFileResource;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.dm.CustomTemplateRepositoryChanged;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplates;
import org.openflexo.foundation.cg.templates.TemplateFileNotification;
import org.openflexo.foundation.rm.FlexoCopiedResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.generator.PackagedResourceToCopyGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.TemplateNotFoundException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FileUtils.CopyStrategy;

public class ProjectDocHTMLGenerator extends ProjectDocGenerator {
	private static final String HTML_MACRO_LIBRARY_NAME = "js_macro_library.vm";
	public static final String HTMLEXTRA_DIRECTORY_NAME = "HTMLExtras";
	public static final String CSS_RESOURCES_RELATIVE_PATH = "css";
	public static final String IMG_RESOURCES_RELATIVE_PATH = "img";
	public static final String JS_RESOURCES_RELATIVE_PATH = "scripts";

	private static final String PROJECT_TEMPLATE_NAME = "project.html.vm";
	private static final String PROCESS_TEMPLATE_NAME = "process.js.vm";
	private static final String PROCESSFOLDER_TEMPLATE_NAME = "process_folder.js.vm";
	private static final String PROPERTIES_TEMPLATE_NAME = "project.properties.vm";

	public static final String HTMLDOC_PROPERTIES_FILE_NAME = "htmldoc.properties";
	protected static final Logger logger = FlexoLogger.getLogger(ProjectDocGenerator.class.getPackage().getName());

	protected DGHTMLGenerator<FlexoProject> rootGenerator;
	private final DGTextGenerator<FlexoWorkflow> propertiesGenerator;

	private final Hashtable<FlexoProcess, DGJSGenerator<FlexoProcess>> processGenerators;
	private final Hashtable<ProcessFolder, DGJSGenerator<ProcessFolder>> processFolderGenerators;
	protected boolean hasBeenInitialized = false;

	public ProjectDocHTMLGenerator(FlexoProject project, DGRepository repository) throws GenerationException {
		super(project, repository);
		processGenerators = new Hashtable<FlexoProcess, DGJSGenerator<FlexoProcess>>();
		processFolderGenerators = new Hashtable<ProcessFolder, DGJSGenerator<ProcessFolder>>();
		rootGenerator = new DGHTMLGenerator<FlexoProject>(this, project, PROJECT_TEMPLATE_NAME);
		propertiesGenerator = new DGTextGenerator<FlexoWorkflow>(this, project.getWorkflow(), PROPERTIES_TEMPLATE_NAME,
				HTMLDOC_PROPERTIES_FILE_NAME);
	}

	@Override
	public CGTemplates getDefaultTemplates() {
		return getProject().getGeneratedDoc().getTemplates();
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	@Override
	public String getFileExtension() {
		return DGHTMLGenerator.getHTMLFileExtension();
	}

	public String getMainColor() {
		StringBuilder sb = new StringBuilder();
		Color c = getProject().getCssSheet().getTextColor();
		sb.append(Math.round(c.getRed() * 10000d / 255) / 10000d);
		sb.append(',');
		sb.append(Math.round(c.getGreen() * 10000d / 255) / 10000d);
		sb.append(',');
		sb.append(Math.round(c.getBlue() * 10000d / 255) / 10000d);
		return sb.toString();
	}

	/**
	 * Overrides buildResourcesAndSetGenerators
	 * 
	 * @see org.openflexo.dg.DGGenerator#buildResourcesAndSetGenerators(DGRepository, java.util.Vector)
	 */
	@Override
	public void buildResourcesAndSetGenerators(DGRepository repository, Vector<CGRepositoryFileResource> resources) {
		hasBeenInitialized = true;
		// The root file
		ProjectHTMLFileResource projectRes = GeneratedFileResourceFactory.createNewProjectHTMLFileResource(repository, rootGenerator);
		resources.add(projectRes);

		// The properties file
		WorkflowTextFileResource propertiesRes = GeneratedFileResourceFactory.createNewWorkflowTextFileResource(repository,
				propertiesGenerator);
		resources.add(propertiesRes);

		buildResourcesForProject(repository, resources, getProject());
		for (FlexoProjectReference ref : getProject().getResolvedProjectReferences()) {
			buildResourcesForProject(repository, resources, ref.getReferredProject());
		}
		buildResourcesAndSetGeneratorsForCopyOfPackagedResources(resources);
		buildResourcesAndSetGeneratorsForCopiedResources(resources);
		screenshotsGenerator.buildResourcesAndSetGenerators(repository, resources);
	}

	private void buildResourcesForProject(DGRepository repository, Vector<CGRepositoryFileResource> resources, FlexoProject project) {
		if (project == null) {
			return;
		}
		Vector<FlexoProcess> processes = project.getAllLocalFlexoProcesses();
		resetSecondaryProgressWindow(processes.size());
		Set<ProcessFolder> allFolders = new HashSet<ProcessFolder>();
		allFolders.addAll(project.getWorkflow().getFolders());
		for (FlexoProcess process : processes) {
			if (process == null) {
				continue;
			}

			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + process.getName(), false);
			DGJSGenerator<FlexoProcess> generator = getProcessGenerator(process);

			if (generator != null) {
				ProcessJSFileResource res = GeneratedFileResourceFactory.createNewProcessJSFileResource(repository, generator);
				resources.add(res);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not instanciate ProcessDocGenerator for " + process);
				}
			}

			for (ProcessFolder folder : process.getProcessNode().getFolders()) {
				allFolders.add(folder);
				for (ProcessFolder subFolder : folder.getFolders()) {
					allFolders.add(subFolder);
				}
			}
		}

		// A JS file per process folder
		resetSecondaryProgressWindow(allFolders.size());
		for (ProcessFolder folder : allFolders) {
			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + folder.getName(), false);

			DGJSGenerator<ProcessFolder> generator = getProcessFolderGenerator(folder);

			if (generator != null) {
				ProcessFolderJSFileResource res = GeneratedFileResourceFactory.createNewProcessFolderJSFileResource(repository, generator);
				resources.add(res);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not instanciate ProcessDocGenerator for " + folder);
				}
			}
		}
	}

	protected static final Vector<FileResource> fileResourceToCopy = new Vector<FileResource>();

	static {
		FileResource cssDirectory = new FileResource(HTMLEXTRA_DIRECTORY_NAME + "/" + CSS_RESOURCES_RELATIVE_PATH);
		for (String fileName : cssDirectory.list(FileUtils.CVSFileNameFilter)) {
			fileResourceToCopy.add(new FileResource(HTMLEXTRA_DIRECTORY_NAME + "/" + CSS_RESOURCES_RELATIVE_PATH + "/" + fileName));
		}

		FileResource imgDirectory = new FileResource(HTMLEXTRA_DIRECTORY_NAME + "/" + IMG_RESOURCES_RELATIVE_PATH);
		for (String fileName : imgDirectory.list(FileUtils.CVSFileNameFilter)) {
			fileResourceToCopy.add(new FileResource(HTMLEXTRA_DIRECTORY_NAME + "/" + IMG_RESOURCES_RELATIVE_PATH + "/" + fileName));
		}

		FileResource scriptsDirectory = new FileResource(HTMLEXTRA_DIRECTORY_NAME + "/" + JS_RESOURCES_RELATIVE_PATH);
		for (String fileName : scriptsDirectory.list(FileUtils.CVSFileNameFilter)) {
			fileResourceToCopy.add(new FileResource(HTMLEXTRA_DIRECTORY_NAME + "/" + JS_RESOURCES_RELATIVE_PATH + "/" + fileName));
		}
	}

	private void buildResourcesAndSetGeneratorsForCopyOfPackagedResources(Vector<CGRepositoryFileResource> resources) {
		for (FileResource fileResource : fileResourceToCopy) {
			PackagedResourceToCopyGenerator<DGRepository> generator = getFileResourceGenerator(fileResource);
			generator.buildResourcesAndSetGenerators(getRepository(), resources);
		}
	}

	@Override
	public void copyAdditionalFiles() throws IOException {
		super.copyAdditionalFiles();
		FileUtils.copyDirToDir(getProject().getImportedImagesDir(), getRootOutputDirectory(), CopyStrategy.REPLACE_OLD_ONLY);
		FileUtils.copyContentDirToDir(getProject().getHTMLToEmbedDirectory(), getRootOutputDirectory(), CopyStrategy.REPLACE_OLD_ONLY);
	}

	@Override
	public boolean hasBeenInitialized() {
		return hasBeenInitialized;
	}

	public ProjectHTMLFileResource getProjectDocResource() {
		return (ProjectHTMLFileResource) getProject().resourceForKey(ResourceType.HTML_FILE,
				ProjectHTMLFileResource.nameForRepositoryAndProject(this.getRepository(), getProject()));
	}

	protected long lastLogUpdate;

	/**
	 * Overrides update
	 * 
	 * @see org.openflexo.dg.DGGenerator#update(org.openflexo.foundation.FlexoObservable, org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification.propertyName() != null && dataModification.propertyName().equals("docType")) {
			getTemplateLocator().notifyTemplateModified();
		}
		if (dataModification instanceof TemplateFileNotification) {
			getTemplateLocator().notifyTemplateModified();
		} else if (dataModification instanceof CustomTemplateRepositoryChanged) {
			getTemplateLocator().notifyTemplateModified();
		}
		super.update(observable, dataModification);
	}

	/**
	 * @param o
	 */
	@Override
	public FlexoCopiedResource getScreenshot(FlexoModelObject o) {
		return screenshotsGenerator.getScreenshot(o);
	}

	protected DGJSGenerator<FlexoProcess> getProcessGenerator(FlexoProcess process) {
		DGJSGenerator<FlexoProcess> returned = processGenerators.get(process);
		if (returned == null) {
			returned = new DGJSGenerator<FlexoProcess>(this, process, PROCESS_TEMPLATE_NAME);
			processGenerators.put(process, returned);
		}
		return returned;
	}

	protected DGJSGenerator<ProcessFolder> getProcessFolderGenerator(ProcessFolder processFolder) {
		DGJSGenerator<ProcessFolder> returned = processFolderGenerators.get(processFolder);
		if (returned == null) {
			returned = new DGJSGenerator<ProcessFolder>(this, processFolder, PROCESSFOLDER_TEMPLATE_NAME);
			processFolderGenerators.put(processFolder, returned);
		}
		return returned;
	}

	@Override
	public PackagedResourceToCopyGenerator<DGRepository> getFileResourceGenerator(FileResource r) {
		PackagedResourceToCopyGenerator<DGRepository> returned = packagedResourceToCopyGenerator.get(r);
		if (returned == null) {
			String relativePath = "";
			FileFormat format;
			if (r.getName().endsWith(".png")) {
				format = FileFormat.PNG;
				relativePath = IMG_RESOURCES_RELATIVE_PATH;
			} else if (r.getName().endsWith(".jpg")) {
				format = FileFormat.JPG;
				relativePath = IMG_RESOURCES_RELATIVE_PATH;
			} else if (r.getName().endsWith(".gif")) {
				format = FileFormat.GIF;
				relativePath = IMG_RESOURCES_RELATIVE_PATH;
			} else if (r.getName().endsWith(".js")) {
				format = FileFormat.JS;
				relativePath = JS_RESOURCES_RELATIVE_PATH;
			} else if (r.getName().endsWith(".css")) {
				format = FileFormat.CSS;
				relativePath = CSS_RESOURCES_RELATIVE_PATH;
			} else if (r.isDirectory()) {
				format = FileFormat.UNKNOWN_DIRECTORY;
			} else {
				format = FileFormat.UNKNOWN_BINARY_FILE;
			}

			returned = new PackagedResourceToCopyGenerator<DGRepository>(this, format, ResourceType.COPIED_FILE, r, getRepository()
					.getResourcesSymbolicDirectory(), relativePath);
			packagedResourceToCopyGenerator.put(r, returned);
		}
		return returned;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CGTemplate> getVelocityMacroTemplates() {
		List<CGTemplate> result = new ArrayList<CGTemplate>();
		try {
			result.add(templateWithName(HTML_MACRO_LIBRARY_NAME));
		} catch (TemplateNotFoundException e) {
			logger.warning("Should include velocity macro template for project generator but template is not found '"
					+ HTML_MACRO_LIBRARY_NAME + "'");
			e.printStackTrace();
		}
		return result;
	}
}
