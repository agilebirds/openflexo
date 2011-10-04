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
 * Utility class containing static methods used to restructure old Flexo
 * Projects
 *
 * @author sguerin
 *
 */
public class ProjectRestructuration {


	private static final String OELIB_EXTENSION = ".oelib";

	private static final String OWL_EXTENSION = ".owl";

	private static final String WS_EXTENSION = ".ws";

	private static final String DKV_EXTENSION = ".dkv";

	private static final String DM_EXTENSION = ".dm";

	private static final String XML_EXTENSION = ".xml";

	private static final String LINKS_EXTENSION = ".links";

	private static final String MENU_EXTENSION = ".menu";

	private static final String TOC_EXTENSION = ".toc";

	private static final String DG_EXTENSION = ".dg";

	private static final String SG_EXTENSION = ".sg";

	private static final String CG_EXTENSION = ".cg";

	private static final String WOLIB_EXTENSION = ".wolib";

	private static final String WKF_EXTENSION = ".wkf";

	private static final String RM_EXTENSION = ".rmxml";

	private static final String TS_EXTENSION = ".rmxml.ts";

	public static final String[] FILE_EXTENSIONS = { WKF_EXTENSION, WOLIB_EXTENSION, CG_EXTENSION, DG_EXTENSION, SG_EXTENSION,
			TOC_EXTENSION, MENU_EXTENSION, LINKS_EXTENSION, XML_EXTENSION, DM_EXTENSION, DKV_EXTENSION, WS_EXTENSION, OWL_EXTENSION,
			OELIB_EXTENSION,
		RM_EXTENSION, TS_EXTENSION };

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

	/*
	 * private static FlexoProgress progress;
	 *
	 * private static boolean restructureProjectHierarchy;
	 */

	// ==========================================================================
	// ================= Loading without RM management
	// ==========================
	// ==========================================================================

	/*
	 * public static FlexoProject loadWithoutRM(File rmFile, File
	 * aProjectDirectory, boolean needsToRestructureProjectHierarchy,
	 * FlexoProgress aProgress) throws RuntimeException,
	 * ProjectInitializerException { FlexoProject project = new
	 * FlexoProject(aProjectDirectory); // File[] projectFiles =
	 * aProjectDirectory.listFiles();
	 *
	 * progress = aProgress; restructureProjectHierarchy =
	 * needsToRestructureProjectHierarchy;
	 *
	 * if (logger.isLoggable(Level.INFO))
	 * logger.info("Building Resource Manager File for project: " +
	 * rmFile.getAbsolutePath()); if (progress != null) { int steps =
	 * aProjectDirectory.listFiles().length;
	 * progress.resetSecondaryProgress(steps); }
	 *
	 * if (restructureProjectHierarchy) { if
	 * (!ProjectRestructuration.getExpectedWorkflowDirectory
	 * (aProjectDirectory).exists()) {
	 * ProjectRestructuration.getExpectedWorkflowDirectory
	 * (aProjectDirectory).mkdir(); } }
	 *
	 * FlexoProjectFile projectRMFile = new FlexoProjectFile(rmFile, project);
	 * FlexoRMResource rmResource = new FlexoRMResource(project, projectRMFile);
	 * try { project.setFlexoResource(rmResource);
	 * project.registerResource(rmResource); } catch (DuplicateResourceException
	 * e1) { // Warns about the exception if (logger.isLoggable(Level.WARNING))
	 * logger.warning("Exception raised: " + e1.getClass().getName() +
	 * ". See console for details."); e1.printStackTrace(); }
	 *
	 * // First, load the workflow if (progress != null) {
	 * progress.setSecondaryProgress
	 * (FlexoLocalization.localizedForKey("load_workflow_file")); }
	 * FlexoWorkflowResource wkfRes = loadWorkflowWithoutRM(project);
	 *
	 * // Then, load the processes loadProcessesWithoutRM(project, wkfRes);
	 *
	 * registerCustomInspectors(project); registerCustomTemplates(project);
	 *
	 * try { // Eventually log the result if (logger.isLoggable(Level.FINE)) {
	 * if (logger.isLoggable(Level.FINE)) logger.fine("Getting this RM File:\n"
	 * + project.getFlexoRMResource().getResourceXMLRepresentation()); } // Save
	 * RM file project.getFlexoRMResource().saveResourceData(); } catch
	 * (Exception e) { // Warns about the exception if
	 * (logger.isLoggable(Level.WARNING)) logger.warning("Exception raised: " +
	 * e.getClass().getName() + ". See console for details.");
	 * e.printStackTrace(); }
	 *
	 * // Yes !!! We succeeded to convert
	 *
	 * return project; }
	 *
	 * private static FlexoWorkflowResource loadWorkflowWithoutRM(FlexoProject
	 * proj) throws ProjectInitializerException { // Load the workflow File
	 * wkf_File = ProjectRestructuration.getWorkflowFile(proj,
	 * restructureProjectHierarchy); FlexoProjectFile workflow_File = new
	 * FlexoProjectFile(wkf_File, proj); FlexoWorkflowResource wkf_Res; try {
	 * wkf_Res = new FlexoWorkflowResource(proj, workflow_File); } catch
	 * (InvalidFileNameException e1) { workflow_File = new
	 * FlexoProjectFile(FileUtils
	 * .getValidFileName(workflow_File.getRelativePath()));
	 * workflow_File.setProject(proj); try { wkf_Res = new
	 * FlexoWorkflowResource(proj, workflow_File); } catch
	 * (InvalidFileNameException e) { if (logger.isLoggable(Level.SEVERE))
	 * logger.severe("Invalid workflow name. This should never happen"); return
	 * null; } } FlexoWorkflow _workflow = wkf_Res.getResourceData();
	 * _workflow.setWorkflowName(proj.getProjectName()); try {
	 * proj.registerResource(wkf_Res); } catch (DuplicateResourceException e) {
	 * // Warns about the exception if (logger.isLoggable(Level.WARNING))
	 * logger.warning("Exception raised: " + e.getClass().getName() +
	 * ". See console for details."); e.printStackTrace(); } return wkf_Res; }
	 */
	/*
	 * private static void registerCustomInspectors(FlexoProject project) { File
	 * projectDirectory = project.getProjectDirectory();
	 *
	 * File customInspectorsFile = new File(projectDirectory, "Inspector"); if
	 * (customInspectorsFile.exists()) { // Regitering the resource
	 * FlexoProjectFile resourceCustomInspectorsFile = new
	 * FlexoProjectFile(customInspectorsFile, project); try {
	 * CustomInspectorsResource ciRes = new CustomInspectorsResource(project,
	 * resourceCustomInspectorsFile); project.registerResource(ciRes); } catch
	 * (DuplicateResourceException e) { // Warns about the exception if
	 * (logger.isLoggable(Level.WARNING)) logger.warning("Exception raised: " +
	 * e.getClass().getName() + ". See console for details.");
	 * e.printStackTrace(); return; } catch (InvalidFileNameException e) { if
	 * (logger.isLoggable(Level.WARNING)) logger.warning("Exception raised:");
	 * e.printStackTrace(); return; } if (logger.isLoggable(Level.INFO))
	 * logger.info("Registered Custom Inspectors"); }
	 *
	 * }
	 *
	 * private static void registerCustomTemplates(FlexoProject project) { File
	 * projectDirectory = project.getProjectDirectory();
	 *
	 * File customTemplatesFile = new File(projectDirectory, "Templates"); if
	 * (customTemplatesFile.exists()) { // Regitering the resource
	 * FlexoProjectFile resourceCustomTemplatesFile = new
	 * FlexoProjectFile(customTemplatesFile, project); try {
	 * CustomTemplatesResource ctRes = new CustomTemplatesResource(project,
	 * "Templates", resourceCustomTemplatesFile);
	 * project.registerResource(ctRes); } catch (DuplicateResourceException e) {
	 * // Warns about the exception if (logger.isLoggable(Level.WARNING))
	 * logger.warning("Exception raised: " + e.getClass().getName() +
	 * ". See console for details."); e.printStackTrace(); return; } catch
	 * (InvalidFileNameException e) { e.printStackTrace(); return; } if
	 * (logger.isLoggable(Level.INFO))
	 * logger.info("Registered Custom Templates"); }
	 *
	 * }
	 */
	/*
	 * private static void loadProcessesWithoutRM(FlexoProject project,
	 * FlexoWorkflowResource workflowResource) throws RuntimeException { File
	 * projectDirectory = project.getProjectDirectory();
	 *
	 * for (Enumeration e = project.getFlexoWorkflow()._allProcesses();
	 * e.hasMoreElements();) { FlexoProcessNode processNode = (FlexoProcessNode)
	 * e.nextElement(); File processFile = new File(projectDirectory,
	 * processNode.getFileName()); if (processFile.exists()) { if
	 * (logger.isLoggable(Level.INFO)) logger.info("File " +
	 * processFile.getAbsolutePath() + " found."); if
	 * (restructureProjectHierarchy) { File expectedFile =
	 * getExpectedProcessFile(project, processNode.getName()); // new File //
	 * (ProjectRestructuration.getExpectedWorkflowDirectory(projectDirectory),
	 * // processNode.getFileName()); if (processFile.renameTo(expectedFile)) {
	 * if (logger.isLoggable(Level.INFO)) logger.info("File " +
	 * processFile.getAbsolutePath() + " has been renamed to " +
	 * expectedFile.getAbsolutePath()); processFile = expectedFile; } else { if
	 * (logger.isLoggable(Level.INFO)) logger.info("File " +
	 * processFile.getAbsolutePath() + " could not be renamed to " +
	 * expectedFile.getAbsolutePath()); } } } else { processFile = new
	 * File(ProjectRestructuration
	 * .getExpectedWorkflowDirectory(projectDirectory),
	 * processNode.getFileName()); if (!processFile.exists()) { if
	 * (logger.isLoggable(Level.SEVERE)) logger.severe("File " +
	 * processFile.getAbsolutePath() + " NOT found."); if
	 * (logger.isLoggable(Level.INFO)) logger.info("Exiting application...");
	 * throw new RuntimeException(); } } if (processFile.exists()) {
	 * FlexoProjectFile flexoProjectFile = new FlexoProjectFile(processFile,
	 * project); FlexoProcessResource processRes; try { processRes = new
	 * FlexoProcessResource(project, processNode.getName(), workflowResource,
	 * flexoProjectFile); } catch (InvalidFileNameException e1) { if
	 * (logger.isLoggable(Level.SEVERE))
	 * logger.severe("File name is not valid. This should not happen."); throw
	 * new RuntimeException(); } // FlexoProcess process; try { if (progress !=
	 * null) {progress.setSecondaryProgress(FlexoLocalization.localizedForKey(
	 * "load_and_convert_process") + " " + processFile.getName()); } // process
	 * = (FlexoProcess) processRes.loadResourceData();
	 * project.registerResource(processRes);
	 * findAndRegisterComponents(processRes, project); } catch
	 * (LoadXMLResourceException e2) { // Warns about the exception if
	 * (logger.isLoggable(Level.WARNING))
	 * logger.warning("Could not load process " + processNode.getName() +
	 * " Exception raised: " + e2.getClass().getName() +
	 * ". See console for details."); if (logger.isLoggable(Level.WARNING))
	 * logger.warning(e2.getExtendedMessage()); e2.printStackTrace(); throw new
	 * RuntimeException(); } catch (DuplicateResourceException e2) { // Warns
	 * about the exception if (logger.isLoggable(Level.WARNING))
	 * logger.warning("Exception raised: " + e2.getClass().getName() +
	 * ". See console for details."); e2.printStackTrace(); throw new
	 * RuntimeException(); } catch (FlexoFileNotFoundException e2) { // Warns
	 * about the exception if (logger.isLoggable(Level.WARNING))
	 * logger.warning("Exception raised: " + e.getClass().getName() +
	 * ". See console for details."); e2.printStackTrace(); throw new
	 * RuntimeException(); } catch (FlexoException e2) { // Warns about the
	 * exception if (logger.isLoggable(Level.WARNING))
	 * logger.warning("Exception raised: " + e.getClass().getName() +
	 * ". See console for details."); e2.printStackTrace(); throw new
	 * RuntimeException(); } } else { if (logger.isLoggable(Level.SEVERE))
	 * logger.severe("File " + processFile.getAbsolutePath() + " NOT found.");
	 * throw new RuntimeException(); } } }
	 *
	 * private static void findAndRegisterComponents(FlexoProcessResource
	 * processRes, FlexoProject project) throws RuntimeException { File
	 * projectDirectory = project.getProjectDirectory(); FlexoProcess process =
	 * processRes.getResourceData();
	 *
	 * if (logger.isLoggable(Level.INFO))
	 * logger.info("Finding and registering components for process " +
	 * process.getName()); for (Enumeration e =
	 * process.getAllComponents().elements(); e.hasMoreElements();) {
	 * ComponentDefinition component = (ComponentDefinition) e.nextElement();
	 * String componentFileName = component.getName() + ".woxml"; File
	 * componentFile = new File(projectDirectory, componentFileName); if
	 * (componentFile.exists()) { if (logger.isLoggable(Level.INFO))
	 * logger.info("File " + componentFile.getAbsolutePath() + " found."); if
	 * (restructureProjectHierarchy) { File expectedFile = new
	 * File(ProjectRestructuration
	 * .getExpectedDirectoryForComponent(projectDirectory, component),
	 * componentFileName); if (!expectedFile.getParentFile().exists()) {
	 * expectedFile.getParentFile().mkdirs(); } if
	 * (componentFile.renameTo(expectedFile)) { if
	 * (logger.isLoggable(Level.INFO)) logger.info("File " +
	 * componentFile.getAbsolutePath() + " has been renamed to " +
	 * expectedFile.getAbsolutePath()); componentFile = expectedFile; } else {
	 * if (logger.isLoggable(Level.WARNING)) logger.warning("File " +
	 * componentFile.getAbsolutePath() + " could not be renamed to " +
	 * expectedFile.getAbsolutePath()); } } } else { componentFile = new
	 * File(ProjectRestructuration
	 * .getExpectedDirectoryForComponent(projectDirectory, component),
	 * componentFileName); if (!componentFile.exists()) { if
	 * (logger.isLoggable(Level.WARNING)) logger.warning("File " +
	 * componentFile.getAbsolutePath() + " NOT found."); } } if
	 * (componentFile.exists()) { // Regitering the resource if (progress !=
	 * null) {progress.setSecondaryProgress(FlexoLocalization.localizedForKey(
	 * "convert_component_file") + " " + componentFile.getName()); }
	 * FlexoProjectFile resourceComponentFile = new
	 * FlexoProjectFile(componentFile, project); FlexoComponentResource compRes
	 * = null; FlexoComponentLibraryResource clResource =
	 * project.getFlexoComponentLibraryResource(); try { if (component
	 * instanceof OperationComponentDefinition) { compRes = new
	 * FlexoOperationComponentResource(project, component.getName(), clResource,
	 * resourceComponentFile); project.registerResource(compRes); } else if
	 * (component instanceof TabComponentDefinition) { TabComponentDefinition
	 * thumbnail = (TabComponentDefinition) component; compRes = new
	 * FlexoTabComponentResource(project, thumbnail.getName(), clResource,
	 * resourceComponentFile); project.registerResource(compRes); } else if
	 * (component instanceof PopupComponentDefinition) { compRes = new
	 * FlexoPopupComponentResource(project, component.getName(), clResource,
	 * resourceComponentFile); project.registerResource(compRes); } } catch
	 * (DuplicateResourceException e1) { // Warns about the exception if
	 * (logger.isLoggable(Level.WARNING)) logger.warning("Exception raised: " +
	 * e1.getClass().getName() + ". See console for details.");
	 * e1.printStackTrace(); } catch (InvalidFileNameException e2) { if
	 * (logger.isLoggable(Level.SEVERE))
	 * logger.severe("Could not create component named: "
	 * +resourceComponentFile.getRelativePath()); } if (compRes != null) { try {
	 * compRes.loadResourceData(); if (compRes != null) {
	 * compRes.saveResourceData(); // if (logger.isLoggable(Level.INFO))
	 * logger.info // ("Registered component //
	 * "+compRes.getComponentDefinition().getName()); } else { if
	 * (logger.isLoggable(Level.WARNING))
	 * logger.warning("Could not register component " + component.getName()); }
	 * } catch (LoadXMLResourceException exception) { // Warns about the
	 * exception if (logger.isLoggable(Level.WARNING))
	 * logger.warning("Could not load component " + component.getName() +
	 * " Exception raised: " + exception.getClass().getName() +
	 * ". See console for details."); if (logger.isLoggable(Level.WARNING))
	 * logger.warning(exception.getExtendedMessage());
	 * exception.printStackTrace(); if (logger.isLoggable(Level.INFO))
	 * logger.info("Exiting application..."); throw new RuntimeException(); }
	 * catch (SaveXMLResourceException exception) { // Warns about the exception
	 * if (logger.isLoggable(Level.WARNING))
	 * logger.warning("Could not save component " + component.getName() +
	 * " Exception raised: " + exception.getClass().getName() +
	 * ". See console for details."); exception.printStackTrace(); if
	 * (logger.isLoggable(Level.INFO)) logger.info("Exiting application...");
	 * throw new RuntimeException(); } catch
	 * (SaveResourcePermissionDeniedException exception) { // Warns about the
	 * exception if (logger.isLoggable(Level.WARNING))
	 * logger.warning("Could not save component " + component.getName() +
	 * " Write permission is denied !"); if (logger.isLoggable(Level.INFO))
	 * logger.info("Exiting application..."); throw new RuntimeException();
	 *
	 * } catch (FlexoFileNotFoundException exception) { // Warns about the
	 * exception if (logger.isLoggable(Level.WARNING))
	 * logger.warning("Could not load component " + component.getName() +
	 * " File not found !"); if (logger.isLoggable(Level.INFO))
	 * logger.info("Exiting application..."); throw new RuntimeException(); }
	 * catch (FlexoException exception) { // Warns about the exception if
	 * (logger.isLoggable(Level.WARNING))
	 * logger.warning("Could not load component " + component.getName() +
	 * " File not found !"); if (logger.isLoggable(Level.INFO))
	 * logger.info("Exiting application..."); throw new RuntimeException(); } }
	 * else { if (logger.isLoggable(Level.WARNING))
	 * logger.warning("Could not register component " + component.getName()); }
	 * } } }
	 */
	/*
	 * private static File findWKFFile(File dir) { File[] fileArray =
	 * dir.listFiles(); for (int i = 0; i < fileArray.length; i++) { if
	 * (fileArray[i].getName().endsWith(".wkf")) return fileArray[i]; } return
	 * null; }
	 *
	 * private static File getWorkflowFile(FlexoProject project, boolean
	 * restructureProjectHierarchyFlag) throws ProjectInitializerException {
	 * File wkfFile = getExpectedWorkflowFile(project,
	 * project.getProjectName()); if (!wkfFile.exists()) { if
	 * (logger.isLoggable(Level.INFO)) logger.info(wkfFile.getAbsolutePath() +
	 * " NOT found, looking for a .wkf file..."); wkfFile =
	 * findWKFFile(project.getProjectDirectory()); if (wkfFile == null) {
	 * wkfFile =
	 * findWKFFile(getExpectedWorkflowDirectory(project.getProjectDirectory()));
	 * } if (wkfFile != null) { if (logger.isLoggable(Level.INFO))
	 * logger.info(wkfFile.getAbsolutePath() + " found, choosing this one."); if
	 * (restructureProjectHierarchyFlag) { if
	 * (!wkfFile.renameTo(getExpectedWorkflowFile(project,
	 * project.getProjectName()))) { if (logger.isLoggable(Level.WARNING))
	 * logger.warning("Renaming to " + getExpectedWorkflowFile(project,
	 * project.getProjectName()) + "FAILED"); } else { if
	 * (logger.isLoggable(Level.INFO)) logger.info(wkfFile.getAbsolutePath() +
	 * " has been renamed to " + getExpectedWorkflowFile(project,
	 * project.getProjectName())); wkfFile = getExpectedWorkflowFile(project,
	 * project.getProjectName()); } } } } if ((wkfFile == null) ||
	 * (!wkfFile.exists())) { if (logger.isLoggable(Level.SEVERE))
	 * logger.severe("Cannot find workflow file !"); if
	 * (logger.isLoggable(Level.INFO)) logger.info("Exiting application...");
	 * throw new ProjectInitializerException(); } return wkfFile; }
	 */
	// ==========================================================================
	// ================= Expected files and directories
	// =========================
	// ==========================================================================

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
