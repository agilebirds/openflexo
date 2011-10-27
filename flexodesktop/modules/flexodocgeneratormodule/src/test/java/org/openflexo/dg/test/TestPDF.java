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
package org.openflexo.dg.test;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;

import javax.swing.SwingUtilities;

import org.openflexo.GeneralPreferences;
import org.openflexo.application.FlexoApplication;
import org.openflexo.dg.action.GeneratePDF;
import org.openflexo.dg.latex.ProjectDocLatexGenerator;
import org.openflexo.foundation.DocType;
import org.openflexo.foundation.DocType.DefaultDocType;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.action.FlexoActionEnableCondition;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.action.AddGeneratedCodeRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.utils.FlexoProgressFactory;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.generator.action.GCAction.ProjectGeneratorFactory;
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
import org.openflexo.view.controller.InteractiveFlexoEditor;


public class TestPDF extends DGTestCase
{

	/**
	 * This class extends the DefaultFlexoEditor for testing purposes. So far the implementation is just a simple extension of the
	 * default editor but it may be used later for additional purposes.
	 *
	 * @author gpolet
	 *
	 */
	public static class FlexoPseudoInteractiveEditor extends InteractiveFlexoEditor implements ProjectGeneratorFactory {

		private ProjectDocLatexGenerator projectLatexDocGenerator;

		public FlexoPseudoInteractiveEditor(FlexoProject project) {
			super(project);
		}

		@Override
		public FlexoProgressFactory getFlexoProgressFactory() {
			return null;
		}

		@Override
		public boolean performResourceScanning() {
			return false;
		}

		@Override
		public ProjectDocLatexGenerator generatorForRepository(GenerationRepository repository) {
			if (projectLatexDocGenerator == null) {
				try {
					projectLatexDocGenerator = new ProjectDocLatexGenerator(this.getProject(), (DGRepository) repository);
				} catch (GenerationException e) {
					e.printStackTrace();
					fail();
				}
			}
			return projectLatexDocGenerator;
		}

		@Override
		public  FlexoActionEnableCondition getEnableConditionFor(
				FlexoActionType actionType) {
			return null;
		}

		@Override
		public  FlexoActionInitializer getInitializerFor(FlexoActionType actionType) {
			return null;
		}

		@Override
		public  FlexoActionFinalizer getFinalizerFor(
				FlexoActionType actionType) {
			return null;
		}

		@Override
		public  FlexoExceptionHandler getExceptionHandlerFor(
				FlexoActionType actionType) {
			return null;
		}

		@Override
		public boolean isTestEditor(){
			return true;
		}

		@Override
		public boolean isAutoSaveEnabledByDefault() {
			return true;
		}
	}

	public TestPDF(String arg0)
	{
		super(arg0);
	}

	protected static final FlexoEditorFactory INTERACTIVE_EDITOR_FACTORY = new FlexoEditorFactory() {
		@Override
		public FlexoPseudoInteractiveEditor makeFlexoEditor(FlexoProject project) {
			return new FlexoPseudoInteractiveEditor(project);
		}
	};

	protected static final Logger logger = Logger.getLogger(TestPDF.class.getPackage().getName());

	/**
	 * Overrides setUp
	 * @see DGTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		FlexoApplication.initialize();
		super.setUp();
	}

	/**
	 * Overrides tearDown
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		SwingUtilities.invokeAndWait(new Runnable(){
			/**
			 * Overrides run
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
				ModuleLoader.closeCurrentProject();
				// FlexoSharedInspectorController.resetInstance();
				System.gc();
			}
		});
		super.tearDown();
	}
	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test0BuildPDF()
	{
		pdfBuilder("FBVPDFTest","FBV.prj.zip","FBV.prj",DefaultDocType.Business.name());
		pdfBuilder("FBVPDFTest","FBV.prj.zip","FBV.prj",DefaultDocType.Technical.name());
		pdfBuilder("FBVPDFTest","FBV.prj.zip","FBV.prj",DefaultDocType.UserManual.name());
	}

	public void test1BuildPDF()
	{
		pdfBuilder("ClimactPDFTest","Climact.prj.zip","Climact.prj",DefaultDocType.Business.name());
		pdfBuilder("ClimactPDFTest","Climact.prj.zip","Climact.prj",DefaultDocType.Technical.name());
		pdfBuilder("ClimactPDFTest","Climact.prj.zip","Climact.prj",DefaultDocType.UserManual.name());
	}

	public void test2BuildPDF()
	{
		pdfBuilder("HyperlinkPDFTest","HyperlinkTest.prj.zip","HyperlinkTest.prj",DefaultDocType.Business.name());
		pdfBuilder("HyperlinkPDFTest","HyperlinkTest.prj.zip","HyperlinkTest.prj",DefaultDocType.Technical.name());
		pdfBuilder("HyperlinkPDFTest","HyperlinkTest.prj.zip","HyperlinkTest.prj",DefaultDocType.UserManual.name());
	}

	protected void pdfBuilder(final String name, final String zipName, final String prjName, final String docTypeString){
		if (!SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					/**
					 * Overrides run
					 * @see java.lang.Runnable#run()
					 */
					@Override
					public void run()
					{
						pdfBuilder(name, zipName, prjName, docTypeString);
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
				fail();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				fail();
			}
			return;
		}


		log("Build PDF: "+name+" for target "+docTypeString);
		ToolBox.setPlatform();
		FlexoLoggingManager.forceInitialize();
		File outputDir = null;
		try {
			File f = File.createTempFile("TestPDFBuilding_", null);
			outputDir = new File(f.getParentFile(), f.getName() + "DIR");
			f.delete();
		} catch (IOException e) {
			e.printStackTrace();
			outputDir = new File(System.getProperty("java.io.tmpdir"), "TestPDFBuilding");
		}
		outputDir.mkdirs();

		File unzipDir = null;
		try {
			File f = File.createTempFile("TestPDFUnzip_", null);
			unzipDir = new File(f.getParentFile(), f.getName() + "Unzip"+name);
			f.delete();
		} catch (IOException e) {
			e.printStackTrace();
			unzipDir = new File(System.getProperty("java.io.tmpdir"), "TestPDFUnzip"+name);
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

		File projectDirectory = new File(unzipDir,prjName);

		try {
			projectDirectory = FileUtils.copyDirToDir(projectDirectory, outputDir);
		} catch (IOException e2) {
			e2.printStackTrace();
			fail("Copy of Test project failed!");
			return;
		}

		File latexDirectory = new File(outputDir, "Latex");
		File pdfDir = new File(outputDir, "PDF");
		FlexoEditor editor;
		try {
			editor = FlexoResourceManager.initializeExistingProject(projectDirectory, INTERACTIVE_EDITOR_FACTORY,null);
		} catch (ProjectInitializerException e1) {
			e1.printStackTrace();
			fail("Could not initialize test project");
			return;
		} catch (ProjectLoadingCancelledException e) {
			e.printStackTrace();
			fail();
			return;
		}
		try {
			if (editor != null) {
				initModuleLoader(projectDirectory, editor.getProject());
				editor.getProject().getGeneratedDoc().setFactory((FlexoPseudoInteractiveEditor) editor);
				DocType docType = editor.getProject().getDocTypeNamed(docTypeString);
				editor.getProject().computeDiff = false;
				AddGeneratedCodeRepository add = AddGeneratedCodeRepository.actionType.makeNewAction(editor.getProject().getGeneratedDoc(),
						null, editor);
				add.setNewDocType(docType);
				add.setNewGeneratedCodeRepositoryName("DGTestGeneratedDoc" + new Random().nextInt(1000000));
				add.setNewGeneratedCodeRepositoryDirectory(latexDirectory);
				add.doAction();
				((DGRepository) add.getNewGeneratedCodeRepository()).getPostBuildRepository(); // Initiates
				// the
				// repository
				((DGRepository) add.getNewGeneratedCodeRepository()).setPostBuildDirectory(pdfDir);
				((DGRepository) add.getNewGeneratedCodeRepository()).setPostProductName(((DGRepository) add.getNewGeneratedCodeRepository())
						.getName()
						+ ".pdf");
				((DGRepository) add.getNewGeneratedCodeRepository()).setManageHistory(false);
				if (!add.hasActionExecutionSucceeded()) {
					fail("Add generated doc repository failed");
				}
				SynchronizeRepositoryCodeGeneration sync = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(add
						.getNewGeneratedCodeRepository(), null, editor);
				sync.setSaveBeforeGenerating(false);
				sync.doAction();
				if (!sync.hasActionExecutionSucceeded()) {
					fail("Synchronize generated doc repository failed");
				}
				WriteModifiedGeneratedFiles write = WriteModifiedGeneratedFiles.actionType.makeNewAction(add
						.getNewGeneratedCodeRepository(), null, editor);
				write.setSaveBeforeGenerating(false);
				write.doAction();
				if (!write.hasActionExecutionSucceeded()) {
					fail("Write latex and screenshot files failed");
				}
				GeneratePDF pdf = GeneratePDF.actionType.makeNewAction((DGRepository) add.getNewGeneratedCodeRepository(), null, editor);
				pdf.setSaveBeforeGenerating(false);
				pdf.doAction();
				if (!pdf.hasActionExecutionSucceeded()) {
					fail("PDF generation failed");
				}

				File pdfFile = pdf.getGeneratedPDF();
				assertEquals(((DGRepository) add.getNewGeneratedCodeRepository()).getPostBuildFile().getName(), pdfFile.getName());
				assertTrue(pdfFile.exists());
			}
		} finally {
			// The last test must call this to stop the RM checking
			if (editor!=null) {
				editor.getProject().close();
			}
		}

	}

	/**
	 * @param projectDirectory
	 * @param flexoProject
	 */
	private void initModuleLoader(File projectDirectory, FlexoProject project)
	{
		ModuleLoader.setAllowsDocSubmission(false);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Init Module loader...");
		}
		if (GeneralPreferences.getFavoriteModuleName()==null) {
			GeneralPreferences.setFavoriteModuleName(Module.WKF_MODULE.getName());
		}
		ModuleLoader.fileNameToOpen = projectDirectory.getAbsolutePath();
		ModuleLoader.initializeModules(UserType.getUserTypeNamed("DEVELOPPER")/*, false*/);
		ModuleLoader.setProject(project);
		if (ExternalModuleDelegater.getModuleLoader()==null) {
			fail("Module loader is not there. Screenshots cannot be generated");
		} else if (ExternalModuleDelegater.getModuleLoader().getIEModuleInstance()==null) {
			fail("IE Module not on the classpath. Component screenshots cannot be generated");
		} else if (ExternalModuleDelegater.getModuleLoader().getWKFModuleInstance()==null) {
			fail("WKF Module not on the classpath. Process and activity screenshots cannot be generated");
		}
	}
}
