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
package org.openflexo.dg.rm;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.dg.docx.DGDocxXMLGenerator;
import org.openflexo.dg.docx.DocxTemplatesEnum;
import org.openflexo.dg.file.DGDocxXmlFile;
import org.openflexo.dg.file.DGHTMLFile;
import org.openflexo.dg.file.DGJSFile;
import org.openflexo.dg.file.DGLatexFile;
import org.openflexo.dg.file.DGPptxXmlFile;
import org.openflexo.dg.file.DGTextFile;
import org.openflexo.dg.html.DGHTMLGenerator;
import org.openflexo.dg.html.DGJSGenerator;
import org.openflexo.dg.html.DGTextGenerator;
import org.openflexo.dg.latex.DGLatexGenerator;
import org.openflexo.dg.latex.StyleDocGenerator;
import org.openflexo.dg.pptx.DGPptxXMLGenerator;
import org.openflexo.dg.pptx.PptxTemplatesEnum;


import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.ie.menu.FlexoNavigationMenu;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoCopiedResource;
import org.openflexo.foundation.rm.FlexoFileResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.TextFileResource;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;

public class GeneratedFileResourceFactory {

	static final Logger logger = FlexoLogger.getLogger(GeneratedFileResourceFactory.class.getPackage().getName());

	public static <CD extends ComponentDefinition> ComponentLatexFileResource<CD> createNewComponentLatexFileResource(DGRepository repository, DGLatexGenerator<CD> generator) {
		ComponentLatexFileResource<CD> returned = (ComponentLatexFileResource<CD>) generator.getProject().resourceForKey(ResourceType.LATEX_FILE,
				ComponentLatexFileResource.nameForRepositoryAndComponent(repository, generator.getObject()));
		if (returned != null && returned.getCGFile() == null) {
			returned.delete(false);
			returned = null;
		}
		if (returned == null) {
			returned = new ComponentLatexFileResource<CD>(generator.getProject());
			returned.setGenerator(generator);
			returned.setName(ComponentLatexFileResource.nameForRepositoryAndComponent(repository, generator.getObject()));
			DGLatexFile cgFile = new DGLatexFile(repository, returned);
			initCGFile(repository, returned, cgFile);
			registerDGFile(returned, DGLatexGenerator.nameForComponent(generator.getObject(), repository));
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created page LATEX resource " + returned.getName());
		} else {
			returned.setGenerator(generator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved page resource " + returned.getName());
		}
		generator.setLatexResource(returned);
		returned.registerObserverWhenRequired();
		return returned;
	}

	public static ComponentLibraryLatexFileResource createNewComponentLibraryLatexFileResource(DGRepository repository, DGLatexGenerator<FlexoComponentLibrary> generator) {
		FlexoProject project = generator.getProject();
		ComponentLibraryLatexFileResource res = (ComponentLibraryLatexFileResource) project.resourceForKey(ResourceType.LATEX_FILE, ComponentLibraryLatexFileResource.nameForRepositoryAndModel(
				repository, generator.getObject()));
		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			res = new ComponentLibraryLatexFileResource(project);
			res.setGenerator(generator);
			res.setName(ComponentLibraryLatexFileResource.nameForRepositoryAndModel(repository, generator.getObject()));
			DGLatexFile cgFile = new DGLatexFile(repository, res);
			initCGFile(repository, res, cgFile);
			registerDGFile(res, DGLatexGenerator.nameForComponentLibrary(repository));
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created ComponentLibrary LATEX resource " + res.getName());
		} else {
			res.setGenerator(generator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved ComponentLibrary resource " + res.getName());
		}
		generator.setLatexResource(res);
		return res;
	}

	public static DefinitionsLatexFileResource createNewDefinitionsLatexFileResource(DGRepository repository, DGLatexGenerator<FlexoProject> definitionsGenerator) {
		FlexoProject project = definitionsGenerator.getProject();
		DefinitionsLatexFileResource res = (DefinitionsLatexFileResource) project.resourceForKey(ResourceType.LATEX_FILE, LatexFileResource.nameForRepositoryAndIdentifier(repository,
				definitionsGenerator.getIdentifier()));
		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			res = new DefinitionsLatexFileResource(project);
			res.setGenerator(definitionsGenerator);
			res.setName(LatexFileResource.nameForRepositoryAndIdentifier(repository, definitionsGenerator.getIdentifier()));
			DGLatexFile cgFile = new DGLatexFile(repository, res);
			initCGFile(repository, res, cgFile);
			registerUniqueDGFile(res, DGLatexGenerator.nameForDefinitions(repository));
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created Definitions LATEX resource " + res.getName());
		} else {
			res.setGenerator(definitionsGenerator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved Definitions LATEX resource " + res.getName());
		}
		definitionsGenerator.setLatexResource(res);
		return res;
	}

	public static DKVModelLatexFileResource createNewDKVLatexFileResource(DGRepository repository, DGLatexGenerator<DKVModel> generator) {
		FlexoProject project = generator.getProject();
		DKVModelLatexFileResource res = (DKVModelLatexFileResource) project.resourceForKey(ResourceType.LATEX_FILE, DKVModelLatexFileResource
				.nameForRepositoryAndDKV(repository, generator.getObject()));
		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			res = new DKVModelLatexFileResource(project);
			res.setGenerator(generator);
			res.setName(DKVModelLatexFileResource.nameForRepositoryAndDKV(repository, generator.getObject()));
			DGLatexFile cgFile = new DGLatexFile(repository, res);
			initCGFile(repository, res, cgFile);
			registerDGFile(res, DGLatexGenerator.nameForDKV(repository));
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created DKV LATEX resource " + res.getName());
		} else {
			res.setGenerator(generator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved DKV resource " + res.getName());
		}
		res.registerObserverWhenRequired();
		generator.setLatexResource(res);
		return res;
	}

	public static DMEOEntityLatexFileResource createNewEOEntityLatexFileResource(DGRepository repository, DGLatexGenerator<DMEOEntity> generator) {
		DMEOEntityLatexFileResource res = (DMEOEntityLatexFileResource) generator.getProject().resourceForKey(ResourceType.LATEX_FILE,
				DMEOEntityLatexFileResource.nameForRepositoryAndEntity(repository, generator.getObject()));
		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			FlexoProject project = generator.getProject();
			res = new DMEOEntityLatexFileResource(project);
			res.setGenerator(generator);
			res.setName(DMEOEntityLatexFileResource.nameForRepositoryAndEntity(repository, generator.getObject()));
			DGLatexFile cgFile = new DGLatexFile(repository, res);
			initCGFile(repository, res, cgFile);
			DMEOEntity entity = generator.getObject();
			registerDGFile(res, DGLatexGenerator.nameForEntity(entity, repository));
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created EOEntity LATEX resource " + res.getName());
		} else {
			res.setGenerator(generator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved EOEntity resource " + res.getName());
		}
		generator.setLatexResource(res);
		res.registerObserverWhenRequired();
		return res;
	}

	public static DMModelLatexFileResource createNewDMModelLatexFileResource(DGRepository repository, DGLatexGenerator<DMModel> generator) {
		FlexoProject project = generator.getProject();
		DMModelLatexFileResource res = (DMModelLatexFileResource) project
				.resourceForKey(ResourceType.LATEX_FILE, DMModelLatexFileResource.nameForRepositoryAndModel(repository, generator.getObject()));
		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			res = new DMModelLatexFileResource(project);
			res.setGenerator(generator);
			res.setName(DMModelLatexFileResource.nameForRepositoryAndModel(repository, generator.getObject()));
			DGLatexFile cgFile = new DGLatexFile(repository, res);
			initCGFile(repository, res, cgFile);
			registerDGFile(res, DGLatexGenerator.nameForDataModel(repository));
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created DMModel LATEX resource " + res.getName());
		} else {
			res.setGenerator(generator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved DMModel resource " + res.getName());
		}
		res.registerObserverWhenRequired();
		generator.setLatexResource(res);
		return res;
	}

	public static MenuLatexFileResource createNewMenuLatexFileResource(DGRepository repository, DGLatexGenerator<FlexoNavigationMenu> generator) {
		FlexoProject project = generator.getProject();
		MenuLatexFileResource res = (MenuLatexFileResource) project.resourceForKey(ResourceType.LATEX_FILE, MenuLatexFileResource.nameForRepositoryAndMenu(repository, generator.getObject()));
		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			res = new MenuLatexFileResource(project);
			res.setGenerator(generator);
			res.setName(MenuLatexFileResource.nameForRepositoryAndMenu(repository, generator.getObject()));

			DGLatexFile cgFile = new DGLatexFile(repository, res);
			initCGFile(repository, res, cgFile);

			registerDGFile(res, DGLatexGenerator.nameForMenu(repository));
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created Menu LATEX resource " + res.getName());
		} else {
			res.setGenerator(generator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved Menu resource " + res.getName());
		}
		res.registerObserverWhenRequired();
		generator.setLatexResource(res);
		return res;
	}

	public static OperationLatexFileResource createNewOperationLatexFileResource(DGRepository repository, DGLatexGenerator<OperationNode> generator) {
		FlexoProject project = generator.getProject();
		OperationLatexFileResource res = (OperationLatexFileResource) project.resourceForKey(ResourceType.LATEX_FILE, OperationLatexFileResource.nameForRepositoryAndOperation(repository, generator
				.getObject()));
		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			res = new OperationLatexFileResource(project);
			res.setGenerator(generator);
			res.setName(OperationLatexFileResource.nameForRepositoryAndOperation(repository, generator.getObject()));
			DGLatexFile cgFile = new DGLatexFile(repository, res);
			initCGFile(repository, res, cgFile);
			registerDGFile(res, DGLatexGenerator.nameForOperation(generator.getObject(), repository));
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created Operation LATEX resource " + res.getName());
		} else {
			res.setGenerator(generator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved operation resource " + res.getName());
		}
		res.registerObserverWhenRequired();
		generator.setLatexResource(res);
		return res;
	}

	public static ProcessLatexFileResource createNewProcessLatexFileResource(DGRepository repository, DGLatexGenerator<FlexoProcess> generator) {
		FlexoProject project = generator.getProject();
		ProcessLatexFileResource res = (ProcessLatexFileResource) project.resourceForKey(ResourceType.LATEX_FILE, ProcessLatexFileResource.nameForRepositoryAndProcess(repository, generator
				.getObject()));
		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			res = new ProcessLatexFileResource(project);
			res.setGenerator(generator);
			res.setName(ProcessLatexFileResource.nameForRepositoryAndProcess(repository, generator.getObject()));
			DGLatexFile cgFile = new DGLatexFile(repository, res);
			initCGFile(repository, res, cgFile);
			registerDGFile(res, DGLatexGenerator.nameForProcess(generator.getObject(), repository));
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created Process LATEX resource " + res.getName());
		} else {
			res.setGenerator(generator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved process resource " + res.getName());
		}
		res.registerObserverWhenRequired();
		generator.setLatexResource(res);
		return res;
	}

	public static ProjectLatexFileResource createNewProjectLatexFileResource(DGRepository repository, DGLatexGenerator<FlexoProject> generator) {
		FlexoProject project = generator.getProject();
		ProjectLatexFileResource res = (ProjectLatexFileResource) project.resourceForKey(ResourceType.LATEX_FILE, ProjectLatexFileResource.nameForRepositoryAndProject(repository, project));
		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			res = new ProjectLatexFileResource(project);
			res.setGenerator(generator);
			res.setName(ProjectLatexFileResource.nameForRepositoryAndProject(repository, generator.getObject()));
			DGLatexFile cgFile = new DGLatexFile(repository, res);
			initCGFile(repository, res, cgFile);
			registerDGFile(res, DGLatexGenerator.nameForProject(repository));
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created Menu LATEX resource " + res.getName());
		} else {
			res.setGenerator(generator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved Menu resource " + res.getName());
		}
		res.registerObserverWhenRequired();
		generator.setLatexResource(res);
		return res;
	}

	public static ReadersGuideLatexFileResource createNewReadersGuideLatexFileResource(DGRepository repository, DGLatexGenerator<FlexoProject> readersGuideGenerator) {
		FlexoProject project = readersGuideGenerator.getProject();
		ReadersGuideLatexFileResource res = (ReadersGuideLatexFileResource) project.resourceForKey(ResourceType.LATEX_FILE, LatexFileResource.nameForRepositoryAndIdentifier(repository,
				readersGuideGenerator.getIdentifier()));
		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			res = new ReadersGuideLatexFileResource(project);
			res.setGenerator(readersGuideGenerator);
			res.setName(LatexFileResource.nameForRepositoryAndIdentifier(repository, readersGuideGenerator.getIdentifier()));
			DGLatexFile cgFile = new DGLatexFile(repository, res);
			initCGFile(repository, res, cgFile);
			registerUniqueDGFile(res, DGLatexGenerator.nameForReadersGuide(repository));
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created Reader's guide LATEX resource " + res.getName());
		} else {
			res.setGenerator(readersGuideGenerator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved Reader\'s guide LATEX resource " + res.getName());
		}
		readersGuideGenerator.setLatexResource(res);
		return res;
	}

	public static StyleLatexFileResource createNewStyleLatexFileResource(DGRepository repository, StyleDocGenerator generator, String styleName) {
		FlexoProject project = generator.getProject();
		StyleLatexFileResource returned = new StyleLatexFileResource(project);
		returned.setGenerator(generator);
		returned.setName(LatexFileResource.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));

		DGLatexFile cgFile = new DGLatexFile(repository, returned);
		initCGFile(repository, returned, cgFile);

		return registerUniqueDGFile(returned, styleName);
	}

	public static WorkflowLatexFileResource createNewWorkflowLatexFileResource(DGRepository repository, DGLatexGenerator<FlexoWorkflow> generator) {
		FlexoProject project = generator.getProject();
		WorkflowLatexFileResource res = (WorkflowLatexFileResource) project.resourceForKey(ResourceType.LATEX_FILE, WorkflowLatexFileResource.nameForRepositoryAndModel(repository, generator
				.getObject()));
		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			res = new WorkflowLatexFileResource(project);
			res.setGenerator(generator);
			res.setName(WorkflowLatexFileResource.nameForRepositoryAndModel(repository, generator.getObject()));
			DGLatexFile cgFile = new DGLatexFile(repository, res);
			initCGFile(repository, res, cgFile);
			registerDGFile(res, DGLatexGenerator.nameForWorkflow(repository));
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created Workflow LATEX resource " + res.getName());
		} else {
			res.setGenerator(generator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved Workflow resource " + res.getName());
		}
		generator.setLatexResource(res);
		return res;
	}

	public static ProjectHTMLFileResource createNewProjectHTMLFileResource(DGRepository repository, DGHTMLGenerator<FlexoProject> generator) {
		FlexoProject project = generator.getProject();
		ProjectHTMLFileResource res = (ProjectHTMLFileResource) project.resourceForKey(ResourceType.HTML_FILE, ProjectHTMLFileResource.nameForRepositoryAndProject(repository, project));
		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			res = new ProjectHTMLFileResource(project);
			res.setGenerator(generator);
			res.setName(ProjectHTMLFileResource.nameForRepositoryAndProject(repository, generator.getObject()));
			DGHTMLFile cgFile = new DGHTMLFile(repository, res);
			initCGFile(repository, res, cgFile);
			registerDGFile(res, DGHTMLGenerator.nameForProject(repository));
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created Project html resource " + res.getName());
		} else {
			res.setGenerator(generator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved Project resource" + res.getName());
		}
		generator.setHtmlResource(res);
		return res;
	}

	public static ProjectTextFileResource createNewProjectTextFileResource(DGRepository repository, DGTextGenerator<FlexoProject> generator) {
		FlexoProject project = generator.getProject();
		ProjectTextFileResource res = (ProjectTextFileResource) project.resourceForKey(ResourceType.TEXT_FILE, ProjectTextFileResource.nameForRepositoryAndProject(repository, project));

		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			res = new ProjectTextFileResource(project);
			res.setGenerator(generator);
			res.setName(ProjectTextFileResource.nameForRepositoryAndProject(repository, generator.getObject()));
			DGTextFile dgtextFile = new DGTextFile(repository, res);
			initCGFile(repository, res, dgtextFile);
			registerDGFile(res, generator.getFileName());
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created Project Text resource " + res.getName());
		} else {
			res.setGenerator(generator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved Project text resource " + res.getName());
		}
		generator.setTextResource(res);
		return res;
	}

	public static WorkflowTextFileResource createNewWorkflowTextFileResource(DGRepository repository, DGTextGenerator<FlexoWorkflow> generator) {
		FlexoProject project = generator.getProject();
		WorkflowTextFileResource res = (WorkflowTextFileResource) project.resourceForKey(ResourceType.TEXT_FILE, WorkflowTextFileResource.nameForRepositoryAndWorkflow(repository, generator
				.getObject()));

		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			res = new WorkflowTextFileResource(project);
			res.setGenerator(generator);
			res.setName(WorkflowTextFileResource.nameForRepositoryAndWorkflow(repository, generator.getObject()));
			DGTextFile dgtextFile = new DGTextFile(repository, res);
			initCGFile(repository, res, dgtextFile);
			registerDGFile(res, generator.getFileName());
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created Workflow Text resource " + res.getName());
		} else {
			res.setGenerator(generator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved Project text resource " + res.getName());
		}
		generator.setTextResource(res);
		return res;
	}

	public static ProcessJSFileResource createNewProcessJSFileResource(DGRepository repository, DGJSGenerator<FlexoProcess> generator) {
		FlexoProject project = generator.getProject();
		ProcessJSFileResource res = (ProcessJSFileResource) project.resourceForKey(ResourceType.JS_FILE, ProcessJSFileResource.nameForRepositoryAndProcess(repository, generator.getObject()));

		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			res = new ProcessJSFileResource(project);
			res.setGenerator(generator);
			res.setName(ProcessJSFileResource.nameForRepositoryAndProcess(repository, generator.getObject()));
			DGJSFile cgFile = new DGJSFile(repository, res);

			cgFile.setSymbolicDirectory(generator.getSymbolicDirectory(repository));
			repository.addToFiles(cgFile);
			res.setCGFile(cgFile);

			registerDGFile(res, DGJSGenerator.nameForProcess(generator.getObject(), repository));
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created Process JS resource " + res.getName());
		} else {
			res.setGenerator(generator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved process resource " + res.getName());
		}
		res.registerObserverWhenRequired();
		generator.setJSResource(res);
		return res;
	}

	public static ProcessFolderJSFileResource createNewProcessFolderJSFileResource(DGRepository repository, DGJSGenerator<ProcessFolder> generator) {
		FlexoProject project = generator.getProject();
		ProcessFolderJSFileResource res = (ProcessFolderJSFileResource) project.resourceForKey(ResourceType.JS_FILE, ProcessFolderJSFileResource.nameForRepositoryAndProcessFolder(repository,
				generator.getObject()));

		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			res = new ProcessFolderJSFileResource(project);
			res.setGenerator(generator);
			res.setName(ProcessFolderJSFileResource.nameForRepositoryAndProcessFolder(repository, generator.getObject()));
			DGJSFile cgFile = new DGJSFile(repository, res);

			cgFile.setSymbolicDirectory(generator.getSymbolicDirectory(repository));
			repository.addToFiles(cgFile);
			res.setCGFile(cgFile);

			registerDGFile(res, DGJSGenerator.nameForProcessFolder(generator.getObject(), repository));
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created Process Folder JS resource " + res.getName());
		} else {
			res.setGenerator(generator);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved process Folder resource " + res.getName());
		}
		res.registerObserverWhenRequired();
		generator.setJSResource(res);
		return res;
	}

	public static ProjectDocxXmlFileResource createNewProjectDocxXmlFileResource(DGRepository repository, DGDocxXMLGenerator<FlexoProject> generator, DocxTemplatesEnum docxTemplate) {
		FlexoProject project = generator.getProject();
		ProjectDocxXmlFileResource res = (ProjectDocxXmlFileResource) project.resourceForKey(ResourceType.DOCXXML_FILE, ProjectDocxXmlFileResource.nameForRepositoryAndDocxTemplate(repository,
				docxTemplate));
		
		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			res = new ProjectDocxXmlFileResource(generator, docxTemplate);
			res.setName(ProjectDocxXmlFileResource.nameForRepositoryAndDocxTemplate(repository, docxTemplate));
			DGDocxXmlFile cgFile = new DGDocxXmlFile(repository, res);

			cgFile.setSymbolicDirectory(generator.getSymbolicDirectory(repository));
			repository.addToFiles(cgFile);
			res.setCGFile(cgFile);

			registerDGFile(res, docxTemplate.getFilePath());
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created Project Docx Xml resource " + res.getName());
		} else {
			res.setGenerator(generator);
			generator.addDocxResource(res, docxTemplate);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved project resource " + res.getName());
		}

		return res;
	}
	
	
	/**
	 *@author MOSTAFA
	 *MOS
	 *
	 *
	 */
	public static ProjectPptxXmlFileResource createNewProjectPptXmlFileResource(DGRepository repository, DGPptxXMLGenerator<FlexoProject> generator, PptxTemplatesEnum pptxTemplate) {
		FlexoProject project = generator.getProject();
		ProjectPptxXmlFileResource res = (ProjectPptxXmlFileResource) project.resourceForKey(ResourceType.PPTXXML_FILE
				, ProjectPptxXmlFileResource.nameForRepositoryAndPptxTemplate(repository,pptxTemplate));

		if (res != null && res.getCGFile() == null) {
			res.delete(false);
			res = null;
		}
		if (res == null) {
			res = new ProjectPptxXmlFileResource(generator, pptxTemplate);
			res.setName(ProjectPptxXmlFileResource.nameForRepositoryAndPptxTemplate(repository, pptxTemplate));
			DGPptxXmlFile cgFile = new DGPptxXmlFile(repository, res);

			cgFile.setSymbolicDirectory(generator.getSymbolicDirectory(repository));
			repository.addToFiles(cgFile);
			res.setCGFile(cgFile);
			//MARKER 1.3.1
			registerDGFile(res, pptxTemplate.getFilePath());
			if (logger.isLoggable(Level.FINE))
				logger.fine("Created Project Pptx Xml resource " + res.getName());
		} else {
			res.setGenerator(generator);
			generator.addPptxResource(res, pptxTemplate);
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully retrieved project resource " + res.getName());
		}

		return res;
	}
	
	//

	private static void initCGFile(DGRepository repository, TextFileResource returned, CGFile cgFile) {
		cgFile.setSymbolicDirectory(repository.getSrcSymbolicDirectory());
		repository.addToFiles(cgFile);
		returned.setCGFile(cgFile);
	}

	public static FlexoCopiedResource createNewCopiedFileResource(GenerationRepository repository, CGFile cgFile, CGSymbolicDirectory symbolicDirectory, FlexoFileResource resourceToCopy) {
		return createNewCopiedFileResource(repository, cgFile, symbolicDirectory, resourceToCopy, null);
	}

	public static FlexoCopiedResource createNewCopiedFileResource(GenerationRepository repository, CGFile cgFile, CGSymbolicDirectory symbolicDirectory, FlexoFileResource resourceToCopy,
			String folderPath) {
		FlexoProject project = resourceToCopy.getProject();
		FlexoCopiedResource returned = new FlexoCopiedResource(project, resourceToCopy);

		if (repository.getSymbolicDirectories().get(symbolicDirectory.getName()) != symbolicDirectory) {
			if (logger.isLoggable(Level.SEVERE))
				logger.severe("Hu oh!!! you added a file to a repository but you passed a symbolic directory that is not in it? I will continue, but I would expect major failures later");
		}
		cgFile.setResource(returned);
		cgFile.setSymbolicDirectory(symbolicDirectory);
		repository.addToFiles(cgFile);
		returned.setCGFile(cgFile);

		return registerUniqueDGFile(returned, resourceToCopy.getFileName(), folderPath);
	}

	private static <DGLF extends CGRepositoryFileResource> DGLF registerDGFile(DGLF returned, String fileName) {
		//MARKER 1.3.2
		FlexoProjectFile file = returned.makeFlexoProjectFile("", fileName);
		try {
			returned.setResourceFile(file);
		} catch (InvalidFileNameException e1) {
			String attempt = FileUtils.getValidFileName(file.getRelativePath());
			int i = 0;
			file = new FlexoProjectFile(attempt);
			while (i < 100) {
				try {
					returned.setResourceFile(file);
					break;
				} catch (InvalidFileNameException e) {
					i++;
					file = new FlexoProjectFile(attempt + "-" + i);
					if (i == 100)
						e.printStackTrace();
				}
			}
			if (returned.getResourceFile() == null)
				return null;
		}
		try {
			returned.getProject().registerResource(returned);
		} catch (DuplicateResourceException e) {
			// Warns about the exception
			logger.severe("DuplicateResourceException: " + e.getMessage() + ". See console for details.");
			e.printStackTrace();
		}

		returned.rebuildDependancies();

		return returned;
	}

	private static <DGLF extends CGRepositoryFileResource> DGLF registerUniqueDGFile(DGLF returned, String fileName) {
		return registerUniqueDGFile(returned, fileName, null);
	}

	private static <DGLF extends CGRepositoryFileResource> DGLF registerUniqueDGFile(DGLF returned, String fileName, String folderPath) {
		FlexoProjectFile file = returned.makeFlexoProjectFile(folderPath == null ? "" : folderPath, fileName);
		try {
			returned.setResourceFile(file);
		} catch (InvalidFileNameException e1) {
			file = new FlexoProjectFile(FileUtils.getValidFileName(file.getRelativePath()));
			try {
				returned.setResourceFile(file);
			} catch (InvalidFileNameException e) {
				if (logger.isLoggable(Level.SEVERE))
					logger.severe("Invalid file name: " + file.getRelativePath() + ". This should never happen.");
				return null;
			}
		}
		try {
			returned.getProject().registerResource(returned);
		} catch (DuplicateResourceException e) {
			// Warns about the exception
			logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			e.printStackTrace();
		}

		returned.rebuildDependancies();

		return returned;
	}


}
