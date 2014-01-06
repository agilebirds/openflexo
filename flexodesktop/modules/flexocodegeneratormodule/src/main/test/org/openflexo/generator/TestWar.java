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
package org.openflexo.generator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.zip.ZipException;

import javax.swing.SwingUtilities;

import org.openflexo.GeneralPreferences;
import org.openflexo.FlexoModuleTestCase.InteractiveFlexoTestEditor;
import org.openflexo.application.FlexoApplication;
import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.Format;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.DuplicateCodeRepositoryNameException;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.action.AddGeneratedCodeRepository;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.generator.action.GenerateWAR;
import org.openflexo.generator.action.SynchronizeRepositoryCodeGeneration;
import org.openflexo.generator.action.WriteModifiedGeneratedFiles;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.UserType;
import org.openflexo.module.external.ExternalModuleDelegater;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.toolbox.ZipUtils;
import org.openflexo.view.controller.FlexoSharedInspectorController;
import org.openflexo.view.controller.InteractiveFlexoEditor;

public class TestWar extends CGTestCase {

	protected static final FlexoEditorFactory INTERACTIVE_EDITOR_FACTORY = new FlexoEditorFactory() {
		public InteractiveFlexoEditor makeFlexoEditor(FlexoProject project) {
			return new InteractiveFlexoTestEditor(project);
		}
	};

	public TestWar(String arg0) {
		super(arg0);
	}

	// protected static final Logger logger = Logger.getLogger(TestWar.class.getPackage().getName());

	@Override
	protected void reloadGeneratedResources() {
		super.reloadGeneratedResources();
	}

	@Override
	public ProjectGenerator generatorForRepository(GenerationRepository repository) {
		if (projectGenerator == null || projectGenerator.getRepository() != repository) {
			try {
				projectGenerator = new ProjectGenerator(repository.getProject(), (CGRepository) repository);
				// GPO: I commented the following because it seems quite weird to invoke that method outside of FlexoAction
				// However, if we were to find out that this call is inevitable, then we may uncomment.
				// projectGenerator.buildResourcesAndSetGenerators(repository, new Vector<CGRepositoryFileResource>());
			} catch (GenerationException e) {
				System.err.println("ERROR:" + e.getMessage());
				e.printStackTrace();
				fail("Generation exception " + e.getMessage());
			}
		}
		return projectGenerator;
	}

	/**
	 * Creates a new empty project in a temp directory
	 * 
	 * @throws Throwable
	 */
	public void test0BuildWar() throws Throwable {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					warBuilder("Worker Remittance WarTest", "Workers Remittances-GUI.prj.zip", "Workers Remittances-GUI-19-03.prj",
							CodeType.PROTOTYPE);
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw e.getCause();
		}
	}

	private File findWar(File warDir) {
		File[] files = warDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isFile() && file.getName().toLowerCase().endsWith(".war")) {
				return file;
			}
		}
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				File f = findWar(file);
				if (f != null) {
					return f;
				}
			}
		}
		return null;
	}

	protected void warBuilder(String name, String zipName, String prjName, CodeType codeType) {
		String warName = name;
		String origWarName = name;
		log("Build War : " + name);
		ToolBox.setPlatform();
		FlexoLoggingManager.forceInitialize();
		File outputDir = null;
		try {
			File f = File.createTempFile("TestWARBuilding_", null);
			outputDir = new File(f.getParentFile(), f.getName() + "DIR");
			f.delete();
		} catch (IOException e) {
			e.printStackTrace();
			outputDir = new File(System.getProperty("java.io.tmpdir"), "TestWARBuilding");
		}
		outputDir.mkdirs();

		File unzipDir = null;
		try {
			File f = File.createTempFile("TestWARUnzip_", null);
			unzipDir = new File(f.getParentFile(), f.getName() + "Unzip" + name);
			f.delete();
		} catch (IOException e) {
			e.printStackTrace();
			unzipDir = new File(System.getProperty("java.io.tmpdir"), "TestWARUnzip" + name);
		}
		unzipDir.mkdirs();

		try {
			ZipUtils.unzip(new FileResource(zipName), unzipDir);
		} catch (ZipException e3) {
			e3.printStackTrace();
			fail();
		} catch (IOException e3) {
			e3.printStackTrace();
			fail();
		}

		File projectDirectory = new File(unzipDir, prjName);

		try {
			projectDirectory = FileUtils.copyDirToDir(projectDirectory, outputDir);
		} catch (IOException e2) {
			e2.printStackTrace();
			fail("Copy of Test project failed!");
			return;
		}
		File generatedCodeDirectory = new File(outputDir, "GeneratedCode");
		File warDir = new File(outputDir, "WAR");
		FlexoEditor editor;
		try {
			editor = FlexoResourceManager.initializeExistingProject(projectDirectory, INTERACTIVE_EDITOR_FACTORY, null);
		} catch (ProjectInitializerException e1) {
			e1.printStackTrace();
			fail("Could not initialize test project");
			return;
		} catch (ProjectLoadingCancelledException e) {
			e.printStackTrace();
			fail();
			return;
		}
		initModuleLoader(projectDirectory, editor.getProject());
		editor.getProject().setTargetType(codeType);
		editor.getProject().getGeneratedCode().setFactory(this);
		AddGeneratedCodeRepository addDoc = AddGeneratedCodeRepository.actionType.makeNewAction(editor.getProject().getGeneratedDoc(),
				null, editor);
		addDoc.setNewGeneratedCodeRepositoryName("Reader repository test");
		File directory = new File(projectDirectory.getParentFile(), "GeneratedReaderFor" + editor.getProject().getProjectName());
		directory.mkdirs();
		addDoc.setNewGeneratedCodeRepositoryDirectory(directory);
		addDoc.setFormat(Format.HTML);
		addDoc.doAction();
		DGRepository addedDocRepository = (DGRepository) addDoc.getNewGeneratedCodeRepository();
		assertNotNull(addedDocRepository);
		assertTrue(addDoc.hasActionExecutionSucceeded());

		AddGeneratedCodeRepository add = AddGeneratedCodeRepository.actionType.makeNewAction(editor.getProject().getGeneratedCode(), null,
				editor);
		add.setNewTargetType(codeType);
		add.setReaderRepository(addedDocRepository);
		add.setIncludeReader(true);
		add.setNewGeneratedCodeRepositoryName("FlexoTestGeneratedCode");
		add.setNewGeneratedCodeRepositoryDirectory(generatedCodeDirectory);
		add.doAction();
		// ((CGRepository) add.getNewGeneratedCodeRepository()).getWarRepository(); // Initiates
		// the
		// repository
		boolean ok = false;
		int i = 1;
		while (!ok) {
			try {
				warDir.mkdirs();
				((CGRepository) add.getNewGeneratedCodeRepository()).setWarDirectory(warDir);

				((CGRepository) add.getNewGeneratedCodeRepository()).setWarName(warName);
				ok = true;
			} catch (DuplicateCodeRepositoryNameException e) {
				warName = origWarName + i++;
			}
		}
		if (!add.hasActionExecutionSucceeded()) {
			fail("Add repository action failed");
		}
		SynchronizeRepositoryCodeGeneration sync = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(
				add.getNewGeneratedCodeRepository(), null, editor);
		sync.setContinueAfterValidation(true);
		sync.doAction();
		if (!sync.hasActionExecutionSucceeded()) {
			System.out.println(sync.getValidationErrorAsString());
			fail("Synchronize repository action failed");
		}
		WriteModifiedGeneratedFiles write = WriteModifiedGeneratedFiles.actionType.makeNewAction(add.getNewGeneratedCodeRepository(), null,
				editor);
		if (write.getFilesToWrite().size() > 0) {
			write.doAction();
			if (!write.hasActionExecutionSucceeded()) {
				fail("Write Modified Files action failed");
			}
		}
		GenerateWAR war = GenerateWAR.actionType.makeNewAction((CGRepository) add.getNewGeneratedCodeRepository(), null, editor);
		war.doAction();
		if (!war.hasActionExecutionSucceeded()) {
			fail("WAR generation Files action failed");
		}
		File projectDir = editor.getProject().getProjectDirectory();
		editor.getProject().close();
		File warFile = findWar(((CGRepository) add.getNewGeneratedCodeRepository()).getWarDirectory());
		assertEquals(((CGRepository) add.getNewGeneratedCodeRepository()).getWarName() + ".war", warFile.getName());
		assertTrue(warFile.exists());
		// The last test must call this to stop the RM checking

		editor.getProject().close();
		FileUtils.deleteDir(projectDir);
		resetVariables();
	}

	/**
	 * @param projectDirectory
	 * @param flexoProject
	 */
	private void initModuleLoader(File projectDirectory, FlexoProject project) {
		getModuleLoader().setAllowsDocSubmission(false);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Init Module loader...");
		}
		if (GeneralPreferences.getFavoriteModuleName() == null) {
			GeneralPreferences.setFavoriteModuleName(Module.WKF_MODULE.getName());
		}
		getModuleLoader().fileNameToOpen = projectDirectory.getAbsolutePath();
		if (ExternalModuleDelegater.getModuleLoader() == null) {
			fail("Module loader is not there. Screenshots cannot be generated");
		} else if (ExternalModuleDelegater.getModuleLoader().getIEModuleInstance() == null) {
			fail("IE Module not on the classpath. Component screenshots cannot be generated");
		} else if (ExternalModuleDelegater.getModuleLoader().getWKFModuleInstance() == null) {
			fail("WKF Module not on the classpath. Process and activity screenshots cannot be generated");
		}
	}

    private ModuleLoader getModuleLoader(){
        return ModuleLoader.instance();
    }

	@Override
	protected void setUp() throws Exception {
		FlexoApplication.initialize();
		super.setUp();
	}

	/**
	 * Overrides tearDown
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {
			/**
			 * Overrides run
			 * 
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				ModuleLoader.closeCurrentProject();
				FlexoSharedInspectorController.resetInstance();
				System.gc();
			}
		});
		super.tearDown();
	}

}
