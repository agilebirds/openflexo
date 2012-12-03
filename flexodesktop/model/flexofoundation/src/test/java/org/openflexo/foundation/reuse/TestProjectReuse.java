package org.openflexo.foundation.reuse;

import java.io.File;
import java.util.List;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.action.ImportProject;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.LocalResourceCenterImplementation;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.foundation.rm.ProjectData;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.FileUtils;

public class TestProjectReuse extends FlexoTestCase {

	private static final String SUB_PROCESS_NAME = "My Sub Process";
	private static final String SUB_PROCESS_NODE_NAME = "A Sub Process Node";
	private FlexoResourceCenterService resourceCenter;
	private File rootProjectDirectory;
	private File importedProjectDirectory;
	private FlexoEditor rootEditor;
	private FlexoProject rootProject;
	private FlexoEditor importedProjectEditor;
	private FlexoProject importedProject;

	class ProjectReferenceLoader implements FlexoProjectReferenceLoader {

		@Override
		public void loadProjects(List<FlexoProjectReference> references) throws ProjectLoadingCancelledException {
			if (importedProject == null) {
				importedProject = reloadProject(importedProjectDirectory, resourceCenter, this).getProject();
			}
			references.get(0).setReferredProject(importedProject);
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
		importedProject.save();
		rootProject.save();
		rootProject.close();
		importedProject.close();
		importedProject = null;
		rootProject = reloadProject(rootProjectDirectory, resourceCenter, new ProjectReferenceLoader()).getProject();
		assertNotNull(importedProject); // Imported project should be automatically re-assigned a new value with the project reference
		// loader
		assertNotNull(rootProject.getProjectData(false));
		assertEquals(1, rootProject.getProjectData().getImportedProjects().size());
		assertEquals(importedProject, rootProject.getProjectData().getImportedProjects().get(0).getReferredProject());
	}

	public void testReuseProcess() throws SaveResourceException {
		ImportProject importProject = ImportProject.actionType.makeNewAction(rootProject, null, rootEditor);
		importProject.setProjectToImport(importedProject);
		importProject.doAction();
		assertTrue("Import project action failed", importProject.hasActionExecutionSucceeded());
		FlexoProcess subProcess = createSubProcess(SUB_PROCESS_NAME, null, importedProjectEditor);
		SubProcessNode subProcessNode = instanciateLoopSubProcess(subProcess, rootProject.getRootProcess(), 400, 200, rootEditor);
		subProcessNode.setName(SUB_PROCESS_NODE_NAME);
		assertDepends(rootProject.getRootProcess().getFlexoResource(), subProcess.getFlexoResource());
		importedProject.save();
		rootProject.save();
		rootProject.close();
		importedProject.close();
		importedProject = null;
		rootProject = reloadProject(rootProjectDirectory, resourceCenter, new ProjectReferenceLoader()).getProject();
		assertNotNull(importedProject); // Imported project should be automatically re-assigned a new value with the project reference
										// loader
		subProcess = importedProject.getWorkflow().getLocalFlexoProcessWithName(SUB_PROCESS_NAME);
		subProcessNode = rootProject.getRootProcess().getActivityPetriGraph().getSubProcessNodeNamed(SUB_PROCESS_NODE_NAME);
		assertNotNull(subProcessNode);
		assertNotNull(subProcessNode.getSubProcess());
		assertEquals(subProcessNode.getSubProcess(), subProcess);
		assertEquals(subProcessNode.getSubProcess().getProject(), subProcess.getProject());
		assertEquals(importedProject, subProcessNode.getSubProcess().getProject());
		assertDepends(rootProject.getRootProcess().getFlexoResource(), subProcess.getFlexoResource());
	}
}
