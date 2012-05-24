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

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;

/**
 * Utility class containing static methods used to restructure old Flexo Projects
 * 
 * @author sguerin
 * 
 */
public class ProjectRestructuration {

	public static final String OELIB_EXTENSION = ".oelib";

	public static final String OWL_EXTENSION = ".owl";

	public static final String WS_EXTENSION = ".ws";

	public static final String DKV_EXTENSION = ".dkv";

	public static final String DM_EXTENSION = ".dm";

	public static final String XML_EXTENSION = ".xml";

	public static final String WOXML_EXTENSION = ".woxml";

	public static final String LINKS_EXTENSION = ".links";

	public static final String MENU_EXTENSION = ".menu";

	public static final String TOC_EXTENSION = ".toc";

	public static final String DG_EXTENSION = ".dg";

	public static final String SG_EXTENSION = ".sg";

	public static final String CG_EXTENSION = ".cg";

	public static final String WOLIB_EXTENSION = ".wolib";

	public static final String WKF_EXTENSION = ".wkf";

	public static final String RM_EXTENSION = ".rmxml";

	public static final String TS_EXTENSION = ".rmxml.ts";

	public static final String SHEMA_EXTENSION = ".shema";

	public static final String[] DOT_FILE_EXTENSIONS = { WKF_EXTENSION, WOLIB_EXTENSION, WOXML_EXTENSION, CG_EXTENSION, DG_EXTENSION,
			SG_EXTENSION, TOC_EXTENSION, MENU_EXTENSION, LINKS_EXTENSION, XML_EXTENSION, DM_EXTENSION, DKV_EXTENSION, WS_EXTENSION,
			OWL_EXTENSION, OELIB_EXTENSION, RM_EXTENSION, TS_EXTENSION };

	public static final String[] FILE_EXTENSIONS;

	static {
		int i = 0;
		FILE_EXTENSIONS = new String[DOT_FILE_EXTENSIONS.length];
		for (String ext : DOT_FILE_EXTENSIONS) {
			FILE_EXTENSIONS[i++] = ext.substring(1);
		}
	}

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ProjectRestructuration.class.getPackage().getName());

	public static final String WORKFLOW_DIR = "Workflow";

	public static final String DATA_MODEL_DIR = "DataModel";

	public static final String DKV_MODEL_DIR = "DomainKeyValue";

	public static final String WS_LIBRARY_DIR = "WebService";

	public static final String COMPONENTS_DIR = "Components";

	public static final String POPUPS_DIR = "Popups";

	public static final String GENERATED_CODE_DIR = "GeneratedCode";

	public static final String GENERATED_SOURCES_DIR = "GeneratedSources";

	public static final String IMPORTED_OBJECTS_DIR = "ImportedObjects";

	public static final String GENERATED_DOC_DIR = "Documentation";

	public static final String ONTOLOGY_DIR = "Ontology";

	public static final String SKOS_DIR = "SKOS";

	public static File getExpectedWorkflowDirectory(File projectDirectory) {
		File returned = new File(projectDirectory, ProjectRestructuration.WORKFLOW_DIR);
		if (!returned.exists()) {
			returned.mkdir();
		}
		return returned;
	}

	public static File getExpectedImportedObjectsDirectory(File projectDirectory) {
		File returned = new File(projectDirectory, IMPORTED_OBJECTS_DIR);
		if (!returned.exists()) {
			returned.mkdirs();
		}
		return returned;
	}

	public static File getExpectedDataModelDirectory(File projectDirectory) {
		File returned = new File(projectDirectory, ProjectRestructuration.DATA_MODEL_DIR);
		if (!returned.exists()) {
			returned.mkdir();
		}
		return returned;
	}

	public static File getExpectedOntologyDirectory(File projectDirectory) {
		File returned = new File(projectDirectory, ProjectRestructuration.ONTOLOGY_DIR);
		if (!returned.exists()) {
			returned.mkdir();
		}
		return returned;
	}

	public static File getExpectedSKOSDirectory(File projectDirectory) {
		File returned = new File(projectDirectory, ProjectRestructuration.SKOS_DIR);
		if (!returned.exists()) {
			returned.mkdir();
		}
		return returned;
	}

	public static File getExpectedDomainKeyValueModelDirectory(File projectDirectory) {
		File returned = new File(projectDirectory, ProjectRestructuration.DKV_MODEL_DIR);
		if (!returned.exists()) {
			returned.mkdir();
		}
		return returned;
	}

	public static File getExpectedWSLibraryDirectory(File projectDirectory) {
		File returned = new File(projectDirectory, ProjectRestructuration.WS_LIBRARY_DIR);
		if (!returned.exists()) {
			returned.mkdir();
		}
		return returned;
	}

	public static File getExpectedComponentsDirectory(File projectDirectory) {
		File returned = new File(projectDirectory, ProjectRestructuration.COMPONENTS_DIR);
		if (!returned.exists()) {
			returned.mkdir();
		}
		return returned;
	}

	public static File getExpectedGeneratedCodeDirectory(File projectDirectory) {
		File returned = new File(projectDirectory, ProjectRestructuration.GENERATED_CODE_DIR);
		if (!returned.exists()) {
			returned.mkdir();
		}
		return returned;
	}

	public static File getExpectedGeneratedSourcesDirectory(File projectDirectory) {
		File returned = new File(projectDirectory, ProjectRestructuration.GENERATED_SOURCES_DIR);
		if (!returned.exists()) {
			returned.mkdir();
		}
		return returned;
	}

	public static File getExpectedImplementationModelDirectory(File projectDirectory, String implModelName) {
		File returned = new File(getExpectedGeneratedSourcesDirectory(projectDirectory), implModelName);
		if (!returned.exists()) {
			returned.mkdirs();
		}
		return returned;
	}

	public static File getExpectedGeneratedDocDirectory(File projectDirectory) {
		File returned = new File(projectDirectory, ProjectRestructuration.GENERATED_DOC_DIR);
		if (!returned.exists()) {
			returned.mkdir();
		}
		return returned;
	}

	public static File getExpectedDirectoryForComponent(File projectDirectory, ComponentDefinition component) {

		String relativePath;
		if (component instanceof PopupComponentDefinition) {
			relativePath = POPUPS_DIR;
		} else {
			relativePath = "";
		}

		return new File(getExpectedComponentsDirectory(projectDirectory), relativePath);
	}

	public static File getExpectedWorkflowFile(FlexoProject project, String workflowName) {
		return new File(getExpectedWorkflowDirectory(project.getProjectDirectory()), workflowName + WKF_EXTENSION);
	}

	public static String getWOComponentLibraryFileName(FlexoProject project) {
		return project.getProjectName() + WOLIB_EXTENSION;
	}

	public static File getExpectedComponentLibFile(FlexoProject project) {
		return new File(project.getProjectDirectory(), getWOComponentLibraryFileName(project));
	}

	public static String getGeneratedCodeFileName(FlexoProject project) {
		return project.getProjectName() + CG_EXTENSION;
	}

	public static String getGeneratedSourcesFileName(FlexoProject project) {
		return project.getProjectName() + SG_EXTENSION;
	}

	public static String getGeneratedDocFileName(FlexoProject project) {
		return project.getProjectName() + DG_EXTENSION;
	}

	public static String getImportedRoleLibraryFileName(FlexoProject project) {
		return project.getProjectName() + "ImportedRoles.xml";
	}

	public static String getImportedProcessLibraryFileName(FlexoProject project) {
		return project.getProjectName() + "ImportedProcesses.xml";
	}

	public static File getExpectedImportedLibraryFile(FlexoProject project, String uniqueFileName) {
		return new File(getExpectedImportedObjectsDirectory(project.getProjectDirectory()), uniqueFileName);
	}

	public static String getTOCFileName(FlexoProject project) {
		return project.getProjectName() + TOC_EXTENSION;
	}

	public static File getExpectedGeneratedCodeFile(FlexoProject project) {
		return new File(getExpectedGeneratedCodeDirectory(project.getProjectDirectory()), getGeneratedCodeFileName(project));
	}

	public static File getExpectedGeneratedSourcesFile(FlexoProject project) {
		return new File(getExpectedGeneratedSourcesDirectory(project.getProjectDirectory()), getGeneratedSourcesFileName(project));
	}

	public static File getExpectedGeneratedDocFile(FlexoProject project) {
		return new File(getExpectedGeneratedDocDirectory(project.getProjectDirectory()), getGeneratedDocFileName(project));
	}

	public static File getExpectedTOCFile(FlexoProject project) {
		return new File(getExpectedGeneratedDocDirectory(project.getProjectDirectory()), getTOCFileName(project));
	}

	public static String getFlexoNavigationMenuFileName(FlexoProject project) {
		return project.getProjectName() + MENU_EXTENSION;
	}

	public static File getExpectedNavigationMenuFile(FlexoProject project) {
		return new File(project.getProjectDirectory(), getFlexoNavigationMenuFileName(project));
	}

	public static String getFlexoLinksFileName(FlexoProject project) {
		return project.getProjectName() + LINKS_EXTENSION;
	}

	public static File getExpectedLinksFile(FlexoProject project) {
		return new File(project.getProjectDirectory(), getFlexoLinksFileName(project));
	}

	public static File getExpectedProcessFile(FlexoProject project, String processName) {
		return new File(getExpectedWorkflowDirectory(project.getProjectDirectory()), processName + XML_EXTENSION);
	}

	public static File getExpectedComponentFile(FlexoProject project, String componentName) {
		return new File(getExpectedComponentsDirectory(project.getProjectDirectory()), componentName + WOXML_EXTENSION);
	}

	public static File getExpectedDataModelFile(FlexoProject project, String dataModelName) {
		return new File(getExpectedDataModelDirectory(project.getProjectDirectory()), dataModelName + DM_EXTENSION);
	}

	public static File getExpectedDKVModelFile(FlexoProject project, String dkvName) {
		return new File(getExpectedDomainKeyValueModelDirectory(project.getProjectDirectory()), dkvName + DKV_EXTENSION);
	}

	public static File getExpectedWSLibraryFile(FlexoProject project, String wslibName) {
		return new File(getExpectedWSLibraryDirectory(project.getProjectDirectory()), wslibName + WS_EXTENSION);
	}

	public static File getExpectedProjectOntologyFile(FlexoProject project, String ontologyName) {
		return new File(getExpectedOntologyDirectory(project.getProjectDirectory()), ontologyName + OWL_EXTENSION);
	}

	public static String getShemaLibraryFileName(FlexoProject project) {
		return project.getProjectName() + OELIB_EXTENSION;
	}

	public static File getExpectedShemaLibFile(FlexoProject project) {
		return new File(getExpectedOntologyDirectory(project.getProjectDirectory()), getShemaLibraryFileName(project));
	}

}
