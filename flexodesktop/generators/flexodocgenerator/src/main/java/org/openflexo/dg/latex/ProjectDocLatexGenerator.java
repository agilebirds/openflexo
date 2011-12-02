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
package org.openflexo.dg.latex;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.dg.DGGenerator;
import org.openflexo.dg.ProjectDocGenerator;
import org.openflexo.dg.rm.ComponentLatexFileResource;
import org.openflexo.dg.rm.ComponentLibraryLatexFileResource;
import org.openflexo.dg.rm.DKVModelLatexFileResource;
import org.openflexo.dg.rm.DMEOEntityLatexFileResource;
import org.openflexo.dg.rm.DMModelLatexFileResource;
import org.openflexo.dg.rm.DefinitionsLatexFileResource;
import org.openflexo.dg.rm.GeneratedFileResourceFactory;
import org.openflexo.dg.rm.MenuLatexFileResource;
import org.openflexo.dg.rm.OperationLatexFileResource;
import org.openflexo.dg.rm.ProcessLatexFileResource;
import org.openflexo.dg.rm.ProjectLatexFileResource;
import org.openflexo.dg.rm.ReadersGuideLatexFileResource;
import org.openflexo.dg.rm.WorkflowLatexFileResource;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplates;
import org.openflexo.foundation.cg.utils.DocConstants.DocSection;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.menu.FlexoNavigationMenu;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.generator.PackagedResourceToCopyGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.TemplateNotFoundException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FileUtils.CopyStrategy;
import org.openflexo.toolbox.LogListener;
import org.openflexo.toolbox.ToolBox;

public class ProjectDocLatexGenerator extends ProjectDocGenerator {
	private static final String LATEX_MACRO_LIBRARY_NAME = "doc_macro_library.vm";
	private static final String LATEX_EXTRAS_DIRECTORY = "LatexExtras";
	private static final String LATEX_EMBEDDED_DIRECTORY = "LatexEmbedded";

	private static final String PROJECT_TEMPLATE_NAME = "project.tex.vm";
	private static final String EOENTITY_TEMPLATE_NAME = "eoentity.tex.vm";
	private static final String PAGE_TEMPLATE_NAME = "page.tex.vm";
	private static final String POPUP_TEMPLATE_NAME = "popup.tex.vm";
	private static final String TAB_TEMPLATE_NAME = "tab.tex.vm";
	private static final String WKF_TEMPLATE_NAME = "workflow.tex.vm";
	private static final String PROCESS_TEMPLATE_NAME = "process.tex.vm";
	private static final String OPERATION_TEMPLATE_NAME = "operation.tex.vm";
	private static final String CL_TEMPLATE_NAME = "componentlibrary.tex.vm";
	private static final String DKV_TEMPLATE_NAME = "dkvmodel.tex.vm";
	private static final String DM_TEMPLATE_NAME = "dmmodel.tex.vm";
	private static final String MENU_TEMPLATE_NAME = "menu.tex.vm";
	private static final String READERS_TEMPLATE_NAME = "readersguide.tex.vm";
	private static final String DEFINITIONS_TEMPLATE_NAME = "definitions.tex.vm";
	private static final String ROLES_TEMPLATE_NAME = "roles.tex.vm";

	protected static final Logger logger = FlexoLogger.getLogger(ProjectDocLatexGenerator.class.getPackage().getName());

	private DGLatexGenerator<FlexoProject> rootGenerator;

	private DGLatexGenerator<FlexoWorkflow> workflowGenerator;

	private DGLatexGenerator<FlexoComponentLibrary> clGenerator;

	private DGLatexGenerator<DKVModel> dkvGenerator;

	private DGLatexGenerator<DMModel> dmGenerator;

	private DGLatexGenerator<FlexoNavigationMenu> menuGenerator;

	private DGLatexGenerator<FlexoProject> readersGuideGenerator;

	private DGLatexGenerator<FlexoProject> definitionsGenerator;

	private DGLatexGenerator<FlexoProject> rolesGenerator;

	private StylesAndImagesGenerator stylesAndImagesGenerator;

	private long latexTimeOutInMillis = 15000;

	private Hashtable<FlexoProcess, DGLatexGenerator<FlexoProcess>> processGenerators;
	private Hashtable<OperationNode, DGLatexGenerator<OperationNode>> operationGenerators;
	private Hashtable<ComponentDefinition, DGLatexGenerator<? extends ComponentDefinition>> componentGenerators;
	private Hashtable<DMEOEntity, DGLatexGenerator<DMEOEntity>> entityGenerators;
	private boolean hasBeenInitialized = false;

	public ProjectDocLatexGenerator(FlexoProject project, DGRepository repository) throws GenerationException {
		super(project, repository);
		processGenerators = new Hashtable<FlexoProcess, DGLatexGenerator<FlexoProcess>>();
		operationGenerators = new Hashtable<OperationNode, DGLatexGenerator<OperationNode>>();
		componentGenerators = new Hashtable<ComponentDefinition, DGLatexGenerator<? extends ComponentDefinition>>();
		entityGenerators = new Hashtable<DMEOEntity, DGLatexGenerator<DMEOEntity>>();
	}

	@Override
	public CGTemplates getDefaultTemplates() {
		return getProject().getGeneratedDoc().getTemplates();
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	public String getMainColor() {
		StringBuilder sb = new StringBuilder();
		Color c = getProject().getCssSheet().getTextColor();
		sb.append(Math.round(c.getRed() * 10000 / 255) / 10000d);
		sb.append(',');
		sb.append(Math.round(c.getGreen() * 10000 / 255) / 10000d);
		sb.append(',');
		sb.append(Math.round(c.getBlue() * 10000d / 255d) / 10000d);
		return sb.toString();
	}

	@Override
	public String getFileExtension() {
		return DGLatexGenerator.getLatexFileExtension();
	}

	/**
	 * Overrides buildResourcesAndSetGenerators
	 * 
	 * @see org.openflexo.dg.DGGenerator#buildResourcesAndSetGenerators(DGRepository, java.util.Vector)
	 */
	@Override
	public void buildResourcesAndSetGenerators(DGRepository repository, Vector<CGRepositoryFileResource> resources) {
		hasBeenInitialized = true;
		if (rootGenerator == null) {
			rootGenerator = new DGLatexGenerator<FlexoProject>(this, getProject(), PROJECT_TEMPLATE_NAME);
		}
		if (workflowGenerator == null) {
			workflowGenerator = new DGLatexGenerator<FlexoWorkflow>(this, getProject().getFlexoWorkflow(), WKF_TEMPLATE_NAME, getProject()
					.getFlexoWorkflow().getFullyQualifiedName(),
					DGGenerator.nameForObjectNoExt(getProject().getFlexoWorkflow(), repository), repository.getTocRepository()
							.getTOCEntryWithID(DocSection.PROCESSES));
		}
		if (clGenerator == null) {
			clGenerator = new DGLatexGenerator<FlexoComponentLibrary>(this, getProject().getFlexoComponentLibrary(), CL_TEMPLATE_NAME);
		}
		if (dmGenerator == null) {
			dmGenerator = new DGLatexGenerator<DMModel>(this, getProject().getDataModel(), DM_TEMPLATE_NAME);
		}
		if (dkvGenerator == null) {
			dkvGenerator = new DGLatexGenerator<DKVModel>(this, getProject().getDKVModel(), DKV_TEMPLATE_NAME);
		}
		if (menuGenerator == null) {
			menuGenerator = new DGLatexGenerator<FlexoNavigationMenu>(this, getProject().getFlexoNavigationMenu(), MENU_TEMPLATE_NAME);
		}
		if (readersGuideGenerator == null) {
			readersGuideGenerator = new DGLatexGenerator<FlexoProject>(this, getProject(), READERS_TEMPLATE_NAME, "ReadersGuide",
					DGGenerator.nameForReadersGuideNoExt(getRepository()), getRepository().getTOCEntryWithID(DocSection.READERS_GUIDE));
		}
		if (definitionsGenerator == null) {
			definitionsGenerator = new DGLatexGenerator<FlexoProject>(this, getProject(), DEFINITIONS_TEMPLATE_NAME, "Definitions",
					DGGenerator.nameForDefinitionsNoExt(getRepository()), getRepository().getTOCEntryWithID(DocSection.DEFINITIONS));
		}
		if (rolesGenerator == null) {
			rolesGenerator = new DGLatexGenerator<FlexoProject>(this, getProject(), ROLES_TEMPLATE_NAME, "Roles",
					DGGenerator.nameForDefinitionsNoExt(getRepository()), getRepository().getTOCEntryWithID(DocSection.ROLES));
		}
		if (stylesAndImagesGenerator == null) {
			stylesAndImagesGenerator = new StylesAndImagesGenerator(this, getProject());
		}

		// The root file
		ProjectLatexFileResource projectRes = GeneratedFileResourceFactory.createNewProjectLatexFileResource(repository, rootGenerator);
		resources.add(projectRes);
		// The compound generators
		buildResourcesAndSetGeneratorsForWorkflow(repository, resources);

		buildResourcesAndSetGeneratorsForComponentLibrary(repository, resources);

		buildResourcesAndSetGeneratorsForDataModel(repository, resources);

		// The DKV
		DKVModelLatexFileResource dkvRes = GeneratedFileResourceFactory.createNewDKVLatexFileResource(repository, dkvGenerator);
		resources.add(dkvRes);
		TOCEntry entry = dkvGenerator.getTOCEntry();
		if (entry != null) {
			associateEntryWithResource(entry, dkvRes);
		}

		// The Menu
		MenuLatexFileResource menuRes = GeneratedFileResourceFactory.createNewMenuLatexFileResource(repository, menuGenerator);
		resources.add(menuRes);
		entry = menuGenerator.getTOCEntry();
		if (entry != null) {
			associateEntryWithResource(entry, menuRes);
		}

		ReadersGuideLatexFileResource readersRes = GeneratedFileResourceFactory.createNewReadersGuideLatexFileResource(repository,
				readersGuideGenerator);
		entry = readersGuideGenerator.getTOCEntry();
		if (entry != null) {
			associateEntryWithResource(entry, readersRes);
		}
		resources.add(readersRes);

		DefinitionsLatexFileResource definitionRes = GeneratedFileResourceFactory.createNewDefinitionsLatexFileResource(repository,
				definitionsGenerator);
		entry = definitionsGenerator.getTOCEntry();
		if (entry != null) {
			associateEntryWithResource(entry, definitionRes);
		}
		resources.add(definitionRes);

		stylesAndImagesGenerator.buildResourcesAndSetGenerators(repository, resources);
		screenshotsGenerator.buildResourcesAndSetGenerators(repository, resources);
		buildResourcesAndSetGeneratorsForCopiedResources(resources);
		buildResourcesAndSetGeneratorsForCopyOfPackagedResources(resources);
	}

	/**
	 * @param repository
	 * @param resources
	 */
	private void buildResourcesAndSetGeneratorsForDataModel(DGRepository repository, Vector<CGRepositoryFileResource> resources) {
		// The Datamodel itself
		DMModelLatexFileResource dmRes = GeneratedFileResourceFactory.createNewDMModelLatexFileResource(repository, dmGenerator);
		resources.add(dmRes);
		TOCEntry entry = dmGenerator.getTOCEntry();
		if (entry != null) {
			associateEntryWithResource(entry, dmRes);
		}

		Hashtable<DMEOEntity, DGLatexGenerator<DMEOEntity>> generators = new Hashtable<DMEOEntity, DGLatexGenerator<DMEOEntity>>();
		// All the EOEntities
		Enumeration<DMEORepository> en = getProject().getDataModel().getDMEORepositories().elements();
		while (en.hasMoreElements()) {
			DMEORepository eoRep = en.nextElement();
			resetSecondaryProgressWindow(eoRep.getEntitiesForEOEntity().size());
			Enumeration<DMEOEntity> en1 = eoRep.getEntitiesForEOEntity().elements();
			while (en1.hasMoreElements()) {
				DMEOEntity entity = en1.nextElement();
				if (entity == null || entity.getDontGenerate()) {
					continue;
				}
				DGLatexGenerator<DMEOEntity> generator = getEntityGenerator(entity);
				refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + entity.getName(), false);
				if (generator != null) {
					generators.put(entity, generator);
					DMEOEntityLatexFileResource res = GeneratedFileResourceFactory
							.createNewEOEntityLatexFileResource(repository, generator);
					resources.add(res);
					entry = generator.getTOCEntry();
					if (entry != null) {
						associateEntryWithResource(entry, res);
					}
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Could not instanciate EOEntityDocGenerator for " + entity.getName());
					}
				}
			}
		}
		entityGenerators = generators;
	}

	private static final Vector<FileResource> fileResourceToCopy = new Vector<FileResource>();

	static {
		FileResource latexExtrasDirectory = new FileResource(LATEX_EXTRAS_DIRECTORY);
		for (String fileName : latexExtrasDirectory.list(FileUtils.CVSFileNameFilter)) {
			fileResourceToCopy.add(new FileResource(LATEX_EXTRAS_DIRECTORY + "/" + fileName));
		}
		if (ToolBox.getPLATFORM() != ToolBox.WINDOWS) {
			FileResource latexEmbeddedDirectory = new FileResource(LATEX_EMBEDDED_DIRECTORY);
			for (String fileName : latexEmbeddedDirectory.list(FileUtils.CVSFileNameFilter)) {
				fileResourceToCopy.add(new FileResource(LATEX_EMBEDDED_DIRECTORY + "/" + fileName));
			}
		}
	}

	private void buildResourcesAndSetGeneratorsForCopyOfPackagedResources(Vector<CGRepositoryFileResource> resources) {
		for (FileResource fileResource : fileResourceToCopy) {
			PackagedResourceToCopyGenerator<DGRepository> generator = getFileResourceGenerator(fileResource);
			generator.buildResourcesAndSetGenerators(getRepository(), resources);
		}
	}

	/**
	 * @param repository
	 * @param resources
	 */
	private void buildResourcesAndSetGeneratorsForComponentLibrary(DGRepository repository, Vector<CGRepositoryFileResource> resources) {
		// The component library itself
		ComponentLibraryLatexFileResource clRes = GeneratedFileResourceFactory.createNewComponentLibraryLatexFileResource(repository,
				clGenerator);
		resources.add(clRes);
		TOCEntry entry = clGenerator.getTOCEntry();
		if (entry != null) {
			associateEntryWithResource(entry, clRes);
		}

		Hashtable<ComponentDefinition, DGLatexGenerator<? extends ComponentDefinition>> generators = new Hashtable<ComponentDefinition, DGLatexGenerator<? extends ComponentDefinition>>();
		// All the components
		Vector<ComponentDefinition> components = getProject().getFlexoComponentLibrary().getRootFolder().getAllComponents();
		resetSecondaryProgressWindow(components.size());
		for (ComponentDefinition cd : components) {
			if (cd == null || cd.getDontGenerate()) {
				continue;
			}
			DGLatexGenerator<? extends ComponentDefinition> generator = getComponentGenerator(cd);
			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + cd.getName(), false);
			if (generator != null) {
				generators.put(cd, generator);
				ComponentLatexFileResource<? extends ComponentDefinition> res = GeneratedFileResourceFactory
						.createNewComponentLatexFileResource(repository, generator);
				resources.add(res);
				entry = generator.getTOCEntry();
				if (entry != null) {
					associateEntryWithResource(entry, res);
				}
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not instanciate ComponentDocGenerator for " + cd);
				}
			}
		}
		componentGenerators = generators;
	}

	/**
	 * @param repository
	 * @param resources
	 */
	private void buildResourcesAndSetGeneratorsForWorkflow(DGRepository repository, Vector<CGRepositoryFileResource> resources) {
		// The Workflow itself
		WorkflowLatexFileResource wkfResource = GeneratedFileResourceFactory.createNewWorkflowLatexFileResource(repository,
				workflowGenerator);
		resources.add(wkfResource);
		TOCEntry entry = workflowGenerator.getTOCEntry();
		if (entry != null) {
			associateEntryWithResource(entry, wkfResource);
		}

		Hashtable<FlexoProcess, DGLatexGenerator<FlexoProcess>> generators = new Hashtable<FlexoProcess, DGLatexGenerator<FlexoProcess>>();
		// All its processes
		Vector<FlexoProcess> processes = getProject().getAllLocalFlexoProcesses();
		resetSecondaryProgressWindow(processes.size());
		for (FlexoProcess process : processes) {
			if (process == null || process.getDontGenerate()) {
				continue;
			}
			DGLatexGenerator<FlexoProcess> generator = getProcessGenerator(process);
			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + process.getName(), false);
			if (generator != null) {
				generators.put(process, generator);
				ProcessLatexFileResource res = GeneratedFileResourceFactory.createNewProcessLatexFileResource(repository, generator);
				resources.add(res);
				entry = generator.getTOCEntry();
				if (entry != null) {
					associateEntryWithResource(entry, res);
				}
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not instanciate ProcessDocGenerator for " + process);
				}
			}
			buildResourcesAndSetGeneratorsForProcess(repository, resources, process);
		}
		processGenerators = generators;
	}

	/**
	 * @param repository
	 * @param resources
	 * @param process
	 * @return
	 */
	private void buildResourcesAndSetGeneratorsForProcess(DGRepository repository, Vector<CGRepositoryFileResource> resources,
			FlexoProcess process) {
		Hashtable<OperationNode, DGLatexGenerator<OperationNode>> generators = new Hashtable<OperationNode, DGLatexGenerator<OperationNode>>();
		Vector<OperationNode> operations = process.getAllEmbeddedOperationNodes();
		for (OperationNode node : operations) {
			if (node == null || node.getDontGenerate()) {
				continue;
			}
			DGLatexGenerator<OperationNode> generator = getOperationGenerator(node);
			if (generator != null) {
				generators.put(node, generator);
				OperationLatexFileResource res = GeneratedFileResourceFactory.createNewOperationLatexFileResource(repository, generator);
				resources.add(res);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not instanciate OperationDocGenerator for " + node);
				}
			}
		}
		operationGenerators = generators;
	}

	@Override
	public void copyAdditionalFiles() throws IOException {
		super.copyAdditionalFiles();
		FileUtils.copyDirToDir(getProject().getImportedImagesDir(), getRootOutputDirectory(), CopyStrategy.REPLACE_OLD_ONLY);
		FileUtils.copyContentDirToDir(getProject().getLatexToEmbedDirectory(), getRootOutputDirectory(), CopyStrategy.REPLACE_OLD_ONLY);
	}

	@Override
	public boolean hasBeenInitialized() {
		return hasBeenInitialized;
	}

	/**
	 * @param process
	 * @return
	 */
	private DGLatexGenerator<FlexoProcess> getProcessGenerator(FlexoProcess process) {
		DGLatexGenerator<FlexoProcess> returned = processGenerators.get(process);
		if (returned == null) {
			processGenerators.put(process, returned = new DGLatexGenerator<FlexoProcess>(this, process, PROCESS_TEMPLATE_NAME));
		}
		return returned;
	}

	/**
	 * @param node
	 * @return
	 */
	private DGLatexGenerator<OperationNode> getOperationGenerator(OperationNode node) {
		DGLatexGenerator<OperationNode> returned = operationGenerators.get(node);
		if (returned == null) {
			operationGenerators.put(node, returned = new DGLatexGenerator<OperationNode>(this, node, OPERATION_TEMPLATE_NAME));
		}
		return returned;
	}

	public DGLatexGenerator<DMEOEntity> getEntityGenerator(DMEOEntity entity) {
		DGLatexGenerator<DMEOEntity> returned = entityGenerators.get(entity);
		if (returned == null) {
			entityGenerators.put(entity, returned = new DGLatexGenerator<DMEOEntity>(this, entity, EOENTITY_TEMPLATE_NAME));
		}
		return returned;
	}

	public ProjectLatexFileResource getProjectDocResource() {
		return ((ProjectLatexFileResource) getProject().resourceForKey(ResourceType.LATEX_FILE,
				ProjectLatexFileResource.nameForRepositoryAndProject(this.getRepository(), getProject())));
	}

	/**
	 * @param process
	 * @return
	 */
	private DGLatexGenerator<? extends ComponentDefinition> getComponentGenerator(ComponentDefinition cd) {
		DGLatexGenerator<? extends ComponentDefinition> returned = componentGenerators.get(cd);
		if (returned == null) {
			if (cd instanceof PopupComponentDefinition) {
				componentGenerators.put(cd, returned = new DGLatexGenerator<PopupComponentDefinition>(this, (PopupComponentDefinition) cd,
						POPUP_TEMPLATE_NAME));
			} else if (cd instanceof OperationComponentDefinition) {
				componentGenerators.put(cd, returned = new DGLatexGenerator<OperationComponentDefinition>(this,
						(OperationComponentDefinition) cd, PAGE_TEMPLATE_NAME));
			} else if (cd instanceof TabComponentDefinition) {
				componentGenerators.put(cd, returned = new DGLatexGenerator<TabComponentDefinition>(this, (TabComponentDefinition) cd,
						TAB_TEMPLATE_NAME));
			} else if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("components of type " + cd.getClass().getName() + " are not handled");
			}
		}
		return returned;
	}

	protected long lastLogUpdate;

	/**
	 * @throws IOException
	 * 
	 */
	public File generatePDF(String latexCommand) throws IOException {
		boolean endedWithSuccess = true;
		File projectFile = getProjectDocResource().getFile();
		Process p = null;
		try {
			getRepository().notifyPostBuildStart();
			if (latexCommand.toLowerCase().indexOf("texify") > -1) {
				try {
					String[] command = new String[4];
					command[0] = latexCommand;
					command[1] = "-b";
					command[2] = "-p";
					command[3] = projectFile.getAbsolutePath();
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Executing " + command[0] + " with args " + command[1] + " " + command[2] + " " + command[3]);
					}
					p = Runtime.getRuntime().exec(command, null, projectFile.getParentFile());
					final InputStream is = p.getInputStream();
					Thread readThread = new Thread(new Runnable() {
						/**
						 * Overrides run
						 * 
						 * @see java.lang.Runnable#run()
						 */
						@Override
						public void run() {
							InputStreamReader isr = new InputStreamReader(is);
							BufferedReader reader = new BufferedReader(isr);
							String line;
							Vector<LogListener> listeners = (Vector<LogListener>) logListeners.clone();
							try {
								while ((line = reader.readLine()) != null) {
									for (LogListener l : listeners) {
										l.log(line);
									}
									if (logger.isLoggable(Level.FINE)) {
										logger.fine(line);
									}
									lastLogUpdate = System.currentTimeMillis();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
					readThread.start();
					final InputStream errIs = p.getErrorStream();
					Thread errReadThread = new Thread(new Runnable() {
						/**
						 * Overrides run
						 * 
						 * @see java.lang.Runnable#run()
						 */
						@Override
						public void run() {
							InputStreamReader isr = new InputStreamReader(errIs);
							BufferedReader reader = new BufferedReader(isr);
							String line;
							Vector<LogListener> listeners = (Vector<LogListener>) logListeners.clone();
							try {
								while ((line = reader.readLine()) != null) {
									for (LogListener l : listeners) {
										l.err(line);
									}
									if (logger.isLoggable(Level.FINE)) {
										logger.fine(line);
									}
									lastLogUpdate = System.currentTimeMillis();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
					errReadThread.start();
					final Thread current = Thread.currentThread();
					Thread timeout = new Thread(new Runnable() {
						/**
						 * Overrides run
						 * 
						 * @see java.lang.Runnable#run()
						 */
						@Override
						public void run() {
							while (true) {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									return;
								}
								if (System.currentTimeMillis() - lastLogUpdate > getLatexTimeOutInMillis()) {
									current.interrupt();
									if (logger.isLoggable(Level.WARNING)) {
										logger.warning("pdflatex timeout: no log for " + (getLatexTimeOutInMillis() / 1000) + " seconds.");
									}
									return;
								}
							}
						}
					});
					lastLogUpdate = System.currentTimeMillis();
					timeout.start();
					p.waitFor();
					timeout.interrupt();
				} catch (IOException e) {
					e.printStackTrace();
					endedWithSuccess = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
					endedWithSuccess = false;
					if (p != null) {
						p.destroy();
					}
				}
			} else {
				for (int i = 0; i < 3 && endedWithSuccess; i++) {
					try {
						String[] command = new String[3];
						command[0] = latexCommand;
						command[1] = "-interaction=nonstopmode";
						command[2] = projectFile.getAbsolutePath();
						if (logger.isLoggable(Level.INFO)) {
							logger.info("Executing " + command[0] + " with args " + command[1] + " " + command[2]);
						}
						p = Runtime.getRuntime().exec(command, null, projectFile.getParentFile());
						final InputStream is = p.getInputStream();
						Thread readThread = new Thread(new Runnable() {
							/**
							 * Overrides run
							 * 
							 * @see java.lang.Runnable#run()
							 */
							@Override
							public void run() {
								InputStreamReader isr = new InputStreamReader(is);
								BufferedReader reader = new BufferedReader(isr);
								String line;
								Vector<LogListener> listeners = (Vector<LogListener>) logListeners.clone();
								try {
									while ((line = reader.readLine()) != null) {
										for (LogListener l : listeners) {
											l.log(line);
										}
										lastLogUpdate = System.currentTimeMillis();
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
						readThread.start();
						final InputStream errIs = p.getErrorStream();
						Thread errReadThread = new Thread(new Runnable() {
							/**
							 * Overrides run
							 * 
							 * @see java.lang.Runnable#run()
							 */
							@Override
							public void run() {
								InputStreamReader isr = new InputStreamReader(errIs);
								BufferedReader reader = new BufferedReader(isr);
								String line;
								Vector<LogListener> listeners = (Vector<LogListener>) logListeners.clone();
								try {
									while ((line = reader.readLine()) != null) {
										for (LogListener l : listeners) {
											l.err(line);
										}
										if (logger.isLoggable(Level.FINE)) {
											logger.fine(line);
										}
										lastLogUpdate = System.currentTimeMillis();
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
						errReadThread.start();
						final Thread current = Thread.currentThread();
						Thread timeout = new Thread(new Runnable() {
							/**
							 * Overrides run
							 * 
							 * @see java.lang.Runnable#run()
							 */
							@Override
							public void run() {
								while (true) {
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										return;
									}
									if (System.currentTimeMillis() - lastLogUpdate > getLatexTimeOutInMillis()) {
										current.interrupt();
										if (logger.isLoggable(Level.WARNING)) {
											logger.warning("pdflatex timeout: no log for " + (getLatexTimeOutInMillis() / 1000)
													+ " seconds.");
										}
										return;
									}
								}
							}
						});
						lastLogUpdate = System.currentTimeMillis();
						timeout.start();
						p.waitFor();
						timeout.interrupt();
					} catch (IOException e) {
						e.printStackTrace();
						endedWithSuccess = false;
					} catch (InterruptedException e) {
						e.printStackTrace();
						endedWithSuccess = false;
					}
				}
			}
		} finally {
			if (p != null) {
				p.destroy();// We kill the process
			}
			getRepository().notifyPostBuildStop();
		}
		if (endedWithSuccess) {
			File generated = new File(projectFile.getParentFile(), projectFile.getName().substring(0, projectFile.getName().length() - 3)
					+ "pdf").getCanonicalFile();
			File out = getRepository().getPostBuildFile().getCanonicalFile();
			if (!generated.exists()) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not find generated PDF file at " + generated.getAbsolutePath());
				}
				return null;
			}
			if (!generated.equals(out)) {
				FileUtils.copyFileToFile(generated, out);
			}
			return out;
		} else {
			return null;
		}
	}

	public String getPdfName() {
		return getRepository().getPostProductName();
	}

	public long getLatexTimeOutInMillis() {
		if (latexTimeOutInMillis <= 0) {
			latexTimeOutInMillis = 15000;
		}
		return latexTimeOutInMillis;
	}

	public void setLatexTimeOutInMillis(long latexTimeOutInMillis) {
		this.latexTimeOutInMillis = latexTimeOutInMillis;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CGTemplate> getVelocityMacroTemplates() {
		List<CGTemplate> result = new ArrayList<CGTemplate>();
		try {
			result.add(templateWithName(LATEX_MACRO_LIBRARY_NAME));
		} catch (TemplateNotFoundException e) {
			logger.warning("Should include velocity macro template for project generator but template is not found '"
					+ LATEX_MACRO_LIBRARY_NAME + "'");
			e.printStackTrace();
		}
		return result;
	}
}
