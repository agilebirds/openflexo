package org.openflexo.foundation.reuse;

import java.io.File;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.action.ImportProject;
import org.openflexo.foundation.action.RemoveImportedProject;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.LocalResourceCenterImplementation;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.foundation.rm.ProjectData;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.action.AddRole;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.FileUtils;

public class TestProjectReuse extends FlexoTestCase {

	private static final String SUB_PROCESS_NAME = "My Sub Process";
	private static final String SUB_PROCESS_NODE_NAME = "A Sub Process Node";
	private static final String ROLE_NAME = "My role";
	private FlexoResourceCenterService resourceCenter;
	private File rootProjectDirectory;
	private File importedProjectDirectory;
	private FlexoEditor rootEditor;
	private FlexoProject rootProject;
	private FlexoEditor importedProjectEditor;
	private FlexoProject importedProject;

	class ProjectReferenceLoader implements FlexoProjectReferenceLoader {

		@Override
		public FlexoProject loadProject(FlexoProjectReference reference, boolean silentlyOnly) {
			if (importedProject == null) {
				importedProject = reloadProject(importedProjectDirectory, resourceCenter, this).getProject();
			}
			return importedProject;
		}

	}

	public void testProjectDataClassValidity() throws ModelDefinitionException {
		ModelFactory factory = new ModelFactory(ProjectData.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		resourceCenter = getNewResourceCenter("TestImport");
		rootEditor = createProject("RootTestImport", resourceCenter);
		rootProject = rootEditor.getProject();
		importedProjectEditor = createProject("ImportedProject", resourceCenter);
		importedProject = importedProjectEditor.getProject();
		rootProjectDirectory = rootProject.getProjectDirectory();
		importedProjectDirectory = importedProject.getProjectDirectory();
	}

	@Override
	protected void tearDown() throws Exception {
		if (resourceCenter != null && resourceCenter.getOpenFlexoResourceCenter() instanceof LocalResourceCenterImplementation) {
			FileUtils.deleteDir(((LocalResourceCenterImplementation) resourceCenter.getOpenFlexoResourceCenter()).getLocalDirectory());
		}
		if (rootProject != null) {
			rootProject.close();
			FileUtils.deleteDir(rootProject.getProjectDirectory());
		}
		if (importedProject != null) {
			importedProject.close();
			FileUtils.deleteDir(importedProject.getProjectDirectory());
		}

		super.tearDown();
	}

	public void testImportProject() throws SaveResourceException, ProjectLoadingCancelledException {
		ImportProject importProject = ImportProject.actionType.makeNewAction(rootProject, null, rootEditor);
		importProject.setProjectToImport(importedProject);
		importProject.doAction();
		assertTrue("Import project action failed", importProject.hasActionExecutionSucceeded());
		ImportProject importProjectThatShouldFail = ImportProject.actionType.makeNewAction(rootProject, null, rootEditor);
		importProjectThatShouldFail.setProjectToImport(importedProject);
		importProjectThatShouldFail.doAction();
		assertFalse("Import project action succeeded while it should have failed",
				importProjectThatShouldFail.hasActionExecutionSucceeded());
		importedProject.save();
		rootProject.save();
		rootProject.close();
		importedProject.close();
		importedProject = null;
		rootEditor = reloadProject(rootProjectDirectory, resourceCenter, new ProjectReferenceLoader());
		rootProject = rootEditor.getProject();
		assertEquals(1, rootProject.getProjectData().getImportedProjects().size());
		importProjectThatShouldFail = ImportProject.actionType.makeNewAction(rootProject, null, rootEditor);
		importProjectThatShouldFail
				.setProjectToImport(reloadProject(importedProjectDirectory, resourceCenter, new ProjectReferenceLoader()).getProject());
		importProjectThatShouldFail.doAction();
		assertFalse("Import project action succeeded while it should have failed",
				importProjectThatShouldFail.hasActionExecutionSucceeded());
		FlexoProjectReference importedProjectReference = rootProject.getProjectData().getImportedProjects().get(0);
		FlexoWorkflow workflow = importedProjectReference.getWorkflow();
		assertNotNull(workflow);
		FlexoWorkflow resourceWorkflow = rootProject.getImportedWorkflow(importedProjectReference, false);
		assertNotNull(resourceWorkflow);
		assertSame(resourceWorkflow, workflow);
		importedProjectReference.getReferredProject(true);
		assertNotNull(rootProject.getProjectData(false));
		assertNotNull(importedProject); // Imported project should be automatically re-assigned a new value with the project reference
		// loader
		assertSame(importedProject.getWorkflow(), importedProjectReference.getWorkflow());
		assertNotSame(importedProject.getWorkflow(), workflow);
		assertEquals(1, rootProject.getProjectData().getImportedProjects().size());
		assertEquals(importedProject, importedProjectReference.getReferredProject());
	}

	public void testReuseProcess() throws SaveResourceException {
		AddRole addRole = AddRole.actionType.makeNewAction(importedProject.getFlexoWorkflow(true), null, importedProjectEditor);
		addRole.setNewRoleName(ROLE_NAME);
		addRole.doAction();
		assertTrue(addRole.hasActionExecutionSucceeded());
		assertNotNull(addRole.getNewRole());
		assertEquals(addRole.getNewRole(), importedProject.getWorkflow().getRoleList().roleWithName(ROLE_NAME));
		ImportProject importProject = ImportProject.actionType.makeNewAction(rootProject, null, rootEditor);
		importProject.setProjectToImport(importedProject);
		importProject.doAction();
		assertTrue("Import project action failed", importProject.hasActionExecutionSucceeded());
		FlexoProcess subProcess = createSubProcess(SUB_PROCESS_NAME, null, importedProjectEditor);
		SubProcessNode subProcessNode = instanciateLoopSubProcess(subProcess, rootProject.getRootProcess(), 400, 200, rootEditor);
		subProcessNode.setName(SUB_PROCESS_NODE_NAME);
		subProcessNode.setRole(importedProject.getWorkflow().getRoleList().roleWithName(ROLE_NAME));
		assertDepends(rootProject.getRootProcess().getFlexoResource(), subProcess.getFlexoResource());
		importedProject.save();
		rootProject.save();
		rootProject.close();
		importedProject.close();
		importedProject = null;
		rootEditor = reloadProject(rootProjectDirectory, resourceCenter, new ProjectReferenceLoader());
		rootProject = rootEditor.getProject();
		rootProject.getProjectData().getImportedProjects().get(0).getReferredProject(true);
		assertNotNull(importedProject); // Imported project should be automatically re-assigned a new value with the project reference
										// loader
		subProcess = importedProject.getWorkflow().getLocalFlexoProcessWithName(SUB_PROCESS_NAME);
		subProcessNode = rootProject.getRootProcess().getActivityPetriGraph().getSubProcessNodeNamed(SUB_PROCESS_NODE_NAME);
		assertNotNull(subProcessNode);
		assertNotNull(subProcessNode.getSubProcess());
		assertNotNull(subProcessNode.getRole());
		assertEquals(importedProject.getWorkflow().getRoleList().roleWithName(ROLE_NAME), subProcessNode.getRole());
		assertEquals(subProcessNode.getSubProcess(), subProcess);
		assertEquals(subProcessNode.getSubProcess().getProject(), subProcess.getProject());
		assertEquals(importedProject, subProcessNode.getSubProcess().getProject());
		assertDepends(rootProject.getRootProcess().getFlexoResource(), subProcess.getFlexoResource());
		RemoveImportedProject remove = RemoveImportedProject.actionType.makeNewAction(rootProject, null, rootEditor);
		remove.setProjectToRemoveURI(importedProject.getURI());
		remove.doAction();
		assertTrue(remove.hasActionExecutionSucceeded());
		assertNull(subProcessNode.getRole());
		assertNull(subProcessNode.getSubProcess());
		assertNull(subProcessNode.getSubProcessReference());
	}
}
