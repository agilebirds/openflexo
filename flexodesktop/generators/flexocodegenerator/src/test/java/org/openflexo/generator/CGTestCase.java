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
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.AssertionFailedError;

import org.openflexo.dg.rm.JSFileResource;
import org.openflexo.dg.rm.ProcessJSFileResource;
import org.openflexo.dg.rm.WorkflowTextFileResource;
import org.openflexo.diff.merge.MergeChange;
import org.openflexo.diff.merge.MergeChange.MergeChangeSource;
import org.openflexo.diff.merge.MergeChange.MergeChangeType;
import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.Format;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.action.AddGeneratedCodeRepository;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.FlexoExecutionModelRepository;
import org.openflexo.foundation.dm.eo.EOPrototypeRepository;
import org.openflexo.foundation.ie.IEOperationComponent;
import org.openflexo.foundation.ie.OperationComponentInstance;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.rm.FlexoComponentLibraryResource;
import org.openflexo.foundation.rm.FlexoCopiedResource;
import org.openflexo.foundation.rm.FlexoDKVResource;
import org.openflexo.foundation.rm.FlexoDMResource;
import org.openflexo.foundation.rm.FlexoEOModelResource;
import org.openflexo.foundation.rm.FlexoNavigationMenuResource;
import org.openflexo.foundation.rm.FlexoOperationComponentResource;
import org.openflexo.foundation.rm.FlexoProcessResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoRMResource;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.FlexoTabComponentResource;
import org.openflexo.foundation.rm.FlexoWorkflowResource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.ScreenshotResource;
import org.openflexo.foundation.rm.StorageResourceData;
import org.openflexo.foundation.rm.cg.CopyOfFlexoResource;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.foundation.rm.cg.JavaFileResource;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.generator.action.GCAction.ProjectGeneratorFactory;
import org.openflexo.generator.action.SynchronizeRepositoryCodeGeneration;
import org.openflexo.generator.action.ValidateProject;
import org.openflexo.generator.action.WriteModifiedGeneratedFiles;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.rm.ApplicationConfProdResource;
import org.openflexo.generator.rm.BuildPropertiesResource;
import org.openflexo.generator.rm.FlexoCopyOfFlexoResource;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.generator.rm.OperationComponentAPIFileResource;
import org.openflexo.generator.rm.OperationComponentJavaFileResource;
import org.openflexo.generator.rm.OperationComponentWOFileResource;
import org.openflexo.generator.rm.ProjectTextFileResource;
import org.openflexo.generator.rm.TabComponentAPIFileResource;
import org.openflexo.generator.rm.TabComponentJavaFileResource;
import org.openflexo.generator.rm.TabComponentWOFileResource;
import org.openflexo.generator.rm.UtilComponentAPIFileResource;
import org.openflexo.generator.rm.UtilComponentJavaFileResource;
import org.openflexo.generator.rm.UtilComponentWOFileResource;
import org.openflexo.generator.rm.UtilJavaFileResource;
import org.openflexo.generator.utils.ApplicationConfProdGenerator;
import org.openflexo.generator.utils.BuildPropertiesGenerator;
import org.openflexo.generator.utils.DotClasspathGenerator;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ToolBox;

public abstract class CGTestCase extends FlexoTestCase implements ProjectGeneratorFactory {

	public CGTestCase(String name) {
		super(name);
	}

	public static final Logger logger = Logger.getLogger(CGTestCase.class.getPackage().getName());

	protected static final String TEST_SUB_PROCESS = "TestSubProcess";
	protected static final String TEST_SUB_PROCESS_NODE = "TestSubProcessNode";

	protected static final String TEST_ACTIVITY_IN_SUB_PROCESS = "TestActivityInSubProcess";
	protected static final String TEST_OPERATION_NODE_1 = "TestOperation1";
	protected static final String TEST_OPERATION_NODE_2 = "TestOperation2";
	protected static final String TEST_OPERATION_NODE_3 = "TestOperation3";
	protected static final String TEST_OPERATION_NODE_4 = "TestOperation4";
	protected static final String OPERATION_COMPONENT_1 = "Operation1";
	protected static final String OPERATION_COMPONENT_2 = "Operation2";
	protected static final String OPERATION_COMPONENT_3 = "Operation3";
	protected static final String TAB_COMPONENT1 = "Tab1";
	protected static final String TAB_COMPONENT2 = "Tab2";

	protected static FlexoRMResource _rmResource;
	protected static FlexoWorkflowResource _wkfResource;
	protected static FlexoDMResource _dmResource;
	protected static FlexoDKVResource _dkvResource;
	protected static FlexoComponentLibraryResource _clResource;
	protected static FlexoNavigationMenuResource _menuResource;
	protected static FlexoProcessResource _rootProcessResource;
	protected static FlexoProcessResource _subProcessResource;
	protected static FlexoEOModelResource _eoPrototypesResource;
	protected static FlexoEOModelResource _executionModelResource;
	protected static SubProcessNode _subProcessNode;
	protected static OperationNode _operationNode;
	protected static FlexoOperationComponentResource _operationComponentResource1;
	protected static IEOperationComponent _operationComponent1;
	protected static FlexoOperationComponentResource _operationComponentResource2;
	protected static IEOperationComponent _operationComponent2;
	protected static FlexoOperationComponentResource _operationComponentResource3;
	protected static IEOperationComponent _operationComponent3;
	protected static IEBlocWidget _bloc2;
	protected static FlexoTabComponentResource _tab1ComponentResource;
	protected static FlexoTabComponentResource _tab2ComponentResource;
	protected static TabComponentDefinition _tab1;
	protected static TabComponentDefinition _tab2;

	/**
	 * Overrides setUp
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		// CGRepository.refreshImmediately = true;
		super.setUp();
	}

	protected static CGRepository codeRepository;
	protected static DGRepository docRepository;
	protected static ProjectGenerator projectGenerator;

	@Override
	public ProjectGenerator generatorForRepository(GenerationRepository repository) {
		if (repository == codeRepository) {
			if (projectGenerator == null || projectGenerator.getRepository() != repository) {
				try {
					projectGenerator = new ProjectGenerator(repository.getProject(), (CGRepository) repository);
				} catch (GenerationException e) {
					e.printStackTrace();
					fail("Generation exception " + e.getMessage());
				}
			}
			return projectGenerator;
		}
		fail("Inconsistent data: wrong code repository");
		return null;
	}

	protected void checkThatAllFilesAreUpToDate() {
		AssertionFailedError failed = null;
		for (CGFile file : codeRepository.getFiles()) {
			try {
				assertEquals("Expecting '" + file.getFileName() + "' to be uptodate but was : " + file.getGenerationStatus(),
						GenerationStatus.UpToDate, file.getGenerationStatus());
			} catch (AssertionFailedError e) {
				logger.warning("RESOURCE status problem: " + file.getFileName() + "(" + file.getPathName()
						+ ") MUST be up-to-date; Status is currently " + file.getGenerationStatus() + " reason:\n"
						+ file.getResource().getNeedsUpdateReason());
				if (file.getGenerationStatus() == GenerationStatus.GenerationError) {
					GenerationAvailableFileResource resource = (GenerationAvailableFileResource) file.getResource();
					FlexoResourceGenerator generator = (FlexoResourceGenerator) resource.getGenerator();
					((Exception) generator.getGenerationException()).printStackTrace();
				}
				failed = e;
				// System.exit(0);
			}
		}
		if (failed != null) {
			throw failed;
		}
	}

	protected void checkThatAllFilesAreUpToDateExcept(GenerationStatus alternateStatus, CGFile... someFilesThatMustBeMarkedAsModified) {
		AssertionFailedError failed = null;

		for (CGFile file : codeRepository.getFiles()) {

			boolean shouldBeModified = false;
			for (CGFile aFile : someFilesThatMustBeMarkedAsModified) {
				if (file == aFile) {
					shouldBeModified = true;
				}
			}

			if (!shouldBeModified) {
				try {
					assertEquals(file.getFileName() + "[" + file.getName() + "]", GenerationStatus.UpToDate, file.getGenerationStatus());
				} catch (AssertionFailedError e) {
					logger.warning("RESOURCE status problem: " + file.getFileName() + " MUST be up-to-date; Status is currently "
							+ file.getGenerationStatus() + " reason:\n" + file.getResource().getNeedsUpdateReason());
					failed = e;
				}
			} else {
				try {
					if (alternateStatus != null) {
						assertEquals("File:" + file.getFileName(), alternateStatus, file.getGenerationStatus());
					}
				} catch (AssertionFailedError e) {
					logger.warning("RESOURCE status problem: " + file.getFileName() + " MUST be modified; Status is currently "
							+ file.getGenerationStatus() + " reason:" + file.getResource().getNeedsUpdateReason());
					failed = e;
				}
			}
		}
		if (failed != null) {
			throw failed;
		}
	}

	protected void checkThatAllFilesAreUpToDateExcept(CGFile... someFilesThatMustBeMarkedAsModified) {
		checkThatAllFilesAreUpToDateExcept((GenerationStatus) null, someFilesThatMustBeMarkedAsModified);
	}

	protected void checkDependingOnTemplate(CGTemplate templateFile, CGFile... onlyThoseFileShouldDependOnThatTemplate) {
		AssertionFailedError failed = null;

		for (CGFile file : codeRepository.getFiles()) {

			boolean shouldDependsOnTemplate = false;
			for (CGFile aFile : onlyThoseFileShouldDependOnThatTemplate) {
				if (file == aFile) {
					shouldDependsOnTemplate = true;
				}
			}

			if (shouldDependsOnTemplate) {
				try {
					assertTrue("Was expecting that template " + templateFile + " being part of the used templates. Used templates : "
							+ ((GenerationAvailableFileResource) file.getResource()).getGenerator().getUsedTemplates(),
							((GenerationAvailableFileResource) file.getResource()).getGenerator().getUsedTemplates().contains(templateFile));
				} catch (AssertionFailedError e) {
					logger.warning("File: " + file.getFileName() + " should depends on template " + templateFile.getTemplateName()
							+ " which is not the case");
					failed = e;
				}
			} else {
				try {
					assertFalse(((GenerationAvailableFileResource) file.getResource()).getGenerator().getUsedTemplates()
							.contains(templateFile));
				} catch (AssertionFailedError e) {
					logger.warning("File: " + file.getFileName() + " should NOT depends on template " + templateFile.getTemplateName()
							+ " which is the case");
					failed = e;
				}
			}
		}
		if (failed != null) {
			throw failed;
		}
	}

	protected static OperationComponentJavaFileResource operationComponent1JavaResource;
	protected static OperationComponentJavaFileResource operationComponent2JavaResource;
	protected static OperationComponentJavaFileResource operationComponent3JavaResource;
	protected static TabComponentJavaFileResource tabComponent1JavaResource;
	protected static TabComponentJavaFileResource tabComponent2JavaResource;

	protected static JavaFileResource workflowComponentInstanceResource;

	protected static OperationComponentAPIFileResource operationComponent1APIResource;
	protected static OperationComponentAPIFileResource operationComponent2APIResource;
	protected static OperationComponentAPIFileResource operationComponent3APIResource;
	protected static TabComponentAPIFileResource tabComponent1APIResource;
	protected static TabComponentAPIFileResource tabComponent2APIResource;

	protected static OperationComponentWOFileResource operationComponent1WOResource;
	protected static OperationComponentWOFileResource operationComponent2WOResource;
	protected static OperationComponentWOFileResource operationComponent3WOResource;
	protected static TabComponentWOFileResource tabComponent1WOResource;
	protected static TabComponentWOFileResource tabComponent2WOResource;

	protected static UtilComponentJavaFileResource headerFooterJavaResource;
	protected static UtilComponentWOFileResource headerFooterWOResource;
	protected static UtilComponentAPIFileResource headerFooterAPIResource;

	protected static ProjectTextFileResource classpathTextResource;
	protected static UtilJavaFileResource daJavaResource;
	protected static UtilJavaFileResource cstJavaResource;
	protected static BuildPropertiesResource buildPropertiesResource;
	protected static ApplicationConfProdResource appConfProdResource;

	/*	protected static FlexoCopyOfFlexoResource rootProcessScreenshotCopy;
	protected static FlexoCopyOfFlexoResource subProcessNodeScreenshotCopy;

	protected static FlexoCopyOfFlexoResource subProcessScreenshotCopy;
	protected static FlexoCopyOfFlexoResource activityInSubProcessScreenshotCopy;*/

	protected static FlexoCopyOfFlexoResource rootProcessJSCopy;
	protected static FlexoCopyOfFlexoResource subProcessJSCopy;
	protected static FlexoCopyOfFlexoResource htmlPropertiesCopy;

	// Copy of Copy of Component screenshots
	protected static FlexoCopyOfFlexoResource operationComponent1ScreenshotCopyOfCopy;
	protected static FlexoCopyOfFlexoResource operationComponent2ScreenshotCopyOfCopy;
	protected static FlexoCopyOfFlexoResource operationComponent3ScreenshotCopyOfCopy;
	protected static FlexoCopyOfFlexoResource tabComponent1ScreenshotCopyOfCopy;
	protected static FlexoCopyOfFlexoResource tabComponent2ScreenshotCopyOfCopy;

	// Copy of Copy of Screenshot of Root process
	protected static FlexoCopyOfFlexoResource rootProcessScreenshotCopyOfCopy;
	protected static FlexoCopyOfFlexoResource subProcessNodeScreenshotInRootProcessCopyOfCopy;
	protected static FlexoCopyOfFlexoResource operationNode1ScreenshotCopyOfCopy;

	// Copy of Copy of Screenshot of Sub process
	protected static FlexoCopyOfFlexoResource subProcessScreenshotCopyOfCopy;
	protected static FlexoCopyOfFlexoResource activityInSubProcessScreenshotCopyOfCopy;
	protected static FlexoCopyOfFlexoResource operationNode2ScreenshotCopyOfCopy;
	protected static FlexoCopyOfFlexoResource operationNode3ScreenshotCopyOfCopy;

	/*	protected static ProcessorJavaFileResource processorRootProcessResource;
	protected static ProcessorJavaFileResource processorSubProcessResource;*/

	protected void reloadGeneratedResources() {
		reloadGeneratedResources(TAB_COMPONENT1, TAB_COMPONENT2);
	}

	protected void reloadGeneratedResources(String tab1Name, String tab2Name) {
		projectGenerator = null;
		if (_project.getGeneratedDoc().getGeneratedRepositories().size() == 0) {
			createDefaultDGRepository();
		} else {
			docRepository = (DGRepository) _project.getGeneratedDoc().getGeneratedRepositories().firstElement();
		}

		if (_project.getGeneratedCode().getGeneratedRepositories().size() == 0) {
			createDefaultGCRepository();
		} else {
			codeRepository = (CGRepository) _project.getGeneratedCode().getGeneratedRepositories().firstElement();
		}

		operationComponent1JavaResource = (OperationComponentJavaFileResource) _project.resourceForKey(ResourceType.JAVA_FILE,
				codeRepository.getName() + "." + OPERATION_COMPONENT_1);
		assertNotNull(operationComponent1JavaResource);
		operationComponent2JavaResource = (OperationComponentJavaFileResource) _project.resourceForKey(ResourceType.JAVA_FILE,
				codeRepository.getName() + "." + OPERATION_COMPONENT_2);
		assertNotNull(operationComponent2JavaResource);
		operationComponent3JavaResource = (OperationComponentJavaFileResource) _project.resourceForKey(ResourceType.JAVA_FILE,
				codeRepository.getName() + "." + OPERATION_COMPONENT_3);
		assertNotNull(operationComponent3JavaResource);
		tabComponent1JavaResource = (TabComponentJavaFileResource) _project.resourceForKey(ResourceType.JAVA_FILE, codeRepository.getName()
				+ "." + tab1Name);
		// assertNotNull(tabComponent1JavaResource);
		tabComponent2JavaResource = (TabComponentJavaFileResource) _project.resourceForKey(ResourceType.JAVA_FILE, codeRepository.getName()
				+ "." + tab2Name);
		assertNotNull(tabComponent2JavaResource);

		workflowComponentInstanceResource = (JavaFileResource) _project.resourceForKey(ResourceType.JAVA_FILE, codeRepository.getName()
				+ "." + "org.openflexo.workflowcontext.WorkflowComponentInstance");

		operationComponent1APIResource = (OperationComponentAPIFileResource) _project.resourceForKey(ResourceType.API_FILE,
				codeRepository.getName() + "." + OPERATION_COMPONENT_1);
		assertNotNull(operationComponent1APIResource);
		operationComponent2APIResource = (OperationComponentAPIFileResource) _project.resourceForKey(ResourceType.API_FILE,
				codeRepository.getName() + "." + OPERATION_COMPONENT_2);
		assertNotNull(operationComponent2APIResource);
		operationComponent3APIResource = (OperationComponentAPIFileResource) _project.resourceForKey(ResourceType.API_FILE,
				codeRepository.getName() + "." + OPERATION_COMPONENT_3);
		assertNotNull(operationComponent3APIResource);
		tabComponent1APIResource = (TabComponentAPIFileResource) _project.resourceForKey(ResourceType.API_FILE, codeRepository.getName()
				+ "." + tab1Name);
		// assertNotNull(tabComponent1APIResource);
		tabComponent2APIResource = (TabComponentAPIFileResource) _project.resourceForKey(ResourceType.API_FILE, codeRepository.getName()
				+ "." + tab2Name);
		assertNotNull(tabComponent2APIResource);

		operationComponent1WOResource = (OperationComponentWOFileResource) _project.resourceForKey(ResourceType.WO_FILE,
				codeRepository.getName() + "." + OPERATION_COMPONENT_1);
		assertNotNull(operationComponent1WOResource);
		operationComponent2WOResource = (OperationComponentWOFileResource) _project.resourceForKey(ResourceType.WO_FILE,
				codeRepository.getName() + "." + OPERATION_COMPONENT_2);
		assertNotNull(operationComponent2WOResource);
		operationComponent3WOResource = (OperationComponentWOFileResource) _project.resourceForKey(ResourceType.WO_FILE,
				codeRepository.getName() + "." + OPERATION_COMPONENT_3);
		assertNotNull(operationComponent3WOResource);
		tabComponent1WOResource = (TabComponentWOFileResource) _project.resourceForKey(ResourceType.WO_FILE, codeRepository.getName() + "."
				+ tab1Name);
		// assertNotNull(tabComponent1WOResource);
		tabComponent2WOResource = (TabComponentWOFileResource) _project.resourceForKey(ResourceType.WO_FILE, codeRepository.getName() + "."
				+ tab2Name);
		assertNotNull(tabComponent2WOResource);

		classpathTextResource = (ProjectTextFileResource) _project.resourceForKey(ResourceType.TEXT_FILE,
				GeneratorUtils.nameForRepositoryAndIdentifier(codeRepository, DotClasspathGenerator.IDENTIFIER));
		assertNotNull(classpathTextResource);
		buildPropertiesResource = (BuildPropertiesResource) _project.resourceForKey(ResourceType.TEXT_FILE,
				GeneratorUtils.nameForRepositoryAndIdentifier(codeRepository, BuildPropertiesGenerator.IDENTIFIER));
		assertNotNull(buildPropertiesResource);
		appConfProdResource = (ApplicationConfProdResource) _project.resourceForKey(ResourceType.TEXT_FILE,
				GeneratorUtils.nameForRepositoryAndIdentifier(codeRepository, ApplicationConfProdGenerator.IDENTIFIER));
		assertNotNull(appConfProdResource);

		headerFooterJavaResource = (UtilComponentJavaFileResource) _project.resourceForKey(ResourceType.JAVA_FILE, codeRepository.getName()
				+ "." + _project.getPrefix() + "HeaderFooter");
		assertNotNull(headerFooterJavaResource);
		headerFooterWOResource = (UtilComponentWOFileResource) _project.resourceForKey(ResourceType.WO_FILE, codeRepository.getName() + "."
				+ _project.getPrefix() + "HeaderFooter");
		assertNotNull(headerFooterWOResource);
		headerFooterAPIResource = (UtilComponentAPIFileResource) _project.resourceForKey(ResourceType.API_FILE, codeRepository.getName()
				+ "." + _project.getPrefix() + "HeaderFooter");
		assertNotNull(headerFooterAPIResource);

		daJavaResource = (UtilJavaFileResource) _project.resourceForKey(ResourceType.JAVA_FILE,
				codeRepository.getName() + "." + _project.getPrefix() + "DA");
		System.out.println("_project.resourceForKey(" + ResourceType.JAVA_FILE.getName() + "," + codeRepository.getName() + "."
				+ _project.getPrefix() + "DA) = " + daJavaResource);
		// assertNotNull(daJavaResource);
		cstJavaResource = (UtilJavaFileResource) _project.resourceForKey(ResourceType.JAVA_FILE,
				codeRepository.getName() + "." + _project.getPrefix() + "Constants");
		// assertNotNull(cstJavaResource);
		JSFileResource rootProcessJS = (JSFileResource) _project.resourceForKey(ResourceType.JS_FILE,
				ProcessJSFileResource.nameForRepositoryAndProcess(docRepository, _rootProcessResource.getFlexoProcess()));
		if (rootProcessJS != null) {
			rootProcessJSCopy = (FlexoCopyOfFlexoResource) _project.resourceForKey(ResourceType.COPIED_FILE,
					CopyOfFlexoResource.nameForRepositoryAndResource(codeRepository, rootProcessJS));
		}
		JSFileResource subProcessJS = (JSFileResource) _project.resourceForKey(ResourceType.JS_FILE,
				ProcessJSFileResource.nameForRepositoryAndProcess(docRepository, _subProcessResource.getFlexoProcess()));
		if (subProcessJS != null) {
			subProcessJSCopy = (FlexoCopyOfFlexoResource) _project.resourceForKey(ResourceType.COPIED_FILE,
					CopyOfFlexoResource.nameForRepositoryAndResource(codeRepository, subProcessJS));
		}

		WorkflowTextFileResource htmlProperties = (WorkflowTextFileResource) _project.resourceForKey(ResourceType.TEXT_FILE,
				WorkflowTextFileResource.nameForRepositoryAndWorkflow(docRepository, _project.getFlexoWorkflow()));
		htmlPropertiesCopy = (FlexoCopyOfFlexoResource) _project.resourceForKey(ResourceType.COPIED_FILE,
				CopyOfFlexoResource.nameForRepositoryAndResource(codeRepository, htmlProperties));

		operationComponent1ScreenshotCopyOfCopy = getCopyOfReaderScreenshotResourceForObject(_operationComponent1);
		operationComponent2ScreenshotCopyOfCopy = getCopyOfReaderScreenshotResourceForObject(_operationComponent2);
		operationComponent3ScreenshotCopyOfCopy = getCopyOfReaderScreenshotResourceForObject(_operationComponent3);
		tabComponent1ScreenshotCopyOfCopy = getCopyOfReaderScreenshotResourceForObject(_tab1);
		tabComponent2ScreenshotCopyOfCopy = getCopyOfReaderScreenshotResourceForObject(_tab2);

		rootProcessScreenshotCopyOfCopy = getCopyOfReaderScreenshotResourceForObject(_rootProcessResource.getFlexoProcess());
		subProcessNodeScreenshotInRootProcessCopyOfCopy = getCopyOfReaderScreenshotResourceForObject(_subProcessNode);
		operationNode1ScreenshotCopyOfCopy = getCopyOfReaderScreenshotResourceForObject(_operationNode);

		subProcessScreenshotCopyOfCopy = getCopyOfReaderScreenshotResourceForObject(_subProcessResource.getFlexoProcess());
		AbstractActivityNode activity = _subProcessResource.getFlexoProcess().getAbstractActivityNodeNamed(TEST_ACTIVITY_IN_SUB_PROCESS);
		activityInSubProcessScreenshotCopyOfCopy = getCopyOfReaderScreenshotResourceForObject(activity);
		operationNode2ScreenshotCopyOfCopy = getCopyOfReaderScreenshotResourceForObject(activity
				.getOperationNodeNamed(TEST_OPERATION_NODE_2));
		operationNode3ScreenshotCopyOfCopy = getCopyOfReaderScreenshotResourceForObject(activity
				.getOperationNodeNamed(TEST_OPERATION_NODE_3));
		/*
		ScreenshotResource subProcessNodeScreenshot = _project.getScreenshotResource(_rootProcessResource.getFlexoProcess().getAbstractActivityNodeNamed(TEST_SUB_PROCESS));
		if (subProcessNodeScreenshot!=null)
			subProcessNodeScreenshotCopy = (FlexoCopyOfFlexoResource) _project.resourceForKey(ResourceType.COPIED_FILE,CopyOfFlexoResource.nameForRepositoryAndResource(codeRepository, subProcessNodeScreenshot));
		ScreenshotResource subProcessScreenshot = _project.getScreenshotResource(_subProcessResource.getFlexoProcess());
		if (subProcessScreenshot!=null)
			subProcessScreenshotCopy = (FlexoCopyOfFlexoResource) _project.resourceForKey(ResourceType.COPIED_FILE,CopyOfFlexoResource.nameForRepositoryAndResource(codeRepository, subProcessScreenshot));
		ScreenshotResource activityScreenshot = _project.getScreenshotResource(_subProcessResource.getFlexoProcess().getAbstractActivityNodeNamed(TEST_ACTIVITY_IN_SUB_PROCESS));
		if (activityScreenshot!=null)
			activityInSubProcessScreenshotCopy = (FlexoCopyOfFlexoResource) _project.resourceForKey(ResourceType.COPIED_FILE,CopyOfFlexoResource.nameForRepositoryAndResource(codeRepository, activityScreenshot));*/
		/*		processorRootProcessResource = (ProcessorJavaFileResource) _project.resourceForKey(ResourceType.JAVA_FILE, codeRepository.getName()+"."+_project.getRootProcess().getExecutionGroupName()+"."+_project.getRootProcess().getExecutionClassName());
		//assertNotNull(processorRootProcessResource);
		processorSubProcessResource = (ProcessorJavaFileResource) _project.resourceForKey(ResourceType.JAVA_FILE, codeRepository.getName()+"."+_subProcessResource.getFlexoProcess().getExecutionGroupName()+"."+_subProcessResource.getFlexoProcess().getExecutionClassName());
		//assertNotNull(processorSubProcessResource);
		 */
	}

	protected FlexoCopyOfFlexoResource getCopyOfReaderScreenshotResourceForObject(FlexoModelObject object) {
		if (object == null) {
			return null;
		}
		ScreenshotResource screenshot = _project.getScreenshotResource(object);
		if (screenshot != null) {
			FlexoCopiedResource screenshotCopy = (FlexoCopiedResource) _project.resourceForKey(ResourceType.COPIED_FILE,
					FlexoCopiedResource.nameForCopiedResource(docRepository, screenshot));
			if (screenshotCopy != null) {
				return (FlexoCopyOfFlexoResource) _project.resourceForKey(ResourceType.COPIED_FILE,
						CopyOfFlexoResource.nameForRepositoryAndResource(codeRepository, screenshotCopy));
			}
		}
		return null;
	}

	/**
	 * 
	 */
	protected void createDefaultDGRepository() {
		AddGeneratedCodeRepository add = AddGeneratedCodeRepository.actionType.makeNewAction(_project.getGeneratedDoc(), null, _editor);
		add.setNewGeneratedCodeRepositoryName("Reader repository test");
		File directory = new File(_projectDirectory.getParentFile(), "GeneratedReaderFor" + _project.getProjectName());
		directory.mkdirs();
		add.setNewGeneratedCodeRepositoryDirectory(directory);
		add.setFormat(Format.HTML);
		add.doAction();
		docRepository = (DGRepository) add.getNewGeneratedCodeRepository();
		assertNotNull(docRepository);
		assertTrue(add.hasActionExecutionSucceeded());

	}

	/**
	 * 
	 */
	protected void createDefaultGCRepository() {
		if (docRepository == null) {
			createDefaultDGRepository();
		}
		AddGeneratedCodeRepository add = AddGeneratedCodeRepository.actionType.makeNewAction(_project.getGeneratedCode(), null, _editor);
		add.setNewGeneratedCodeRepositoryName("Code repository test");
		File directory = new File(_projectDirectory.getParentFile(), "GeneratedCodeFor" + _project.getProjectName());
		directory.mkdirs();
		add.setNewGeneratedCodeRepositoryDirectory(directory);
		add.setIncludeReader(true);
		add.setReaderRepository(docRepository);
		add.doAction();
		codeRepository = (CGRepository) add.getNewGeneratedCodeRepository();
		assertNotNull(codeRepository);
		assertTrue(add.hasActionExecutionSucceeded());
		codeRepository.setTargetType(CodeType.PROTOTYPE);
	}

	protected void reloadProject(boolean fullLoading) {
		reloadProject(fullLoading, TAB_COMPONENT1, TAB_COMPONENT2);
	}

	protected void resetVariables() {
		_project = null;
		_rmResource = null;
		_wkfResource = null;
		_dmResource = null;
		_dkvResource = null;
		_clResource = null;
		_menuResource = null;
		_rootProcessResource = null;
		_subProcessResource = null;
		_eoPrototypesResource = null;
		_executionModelResource = null;
		_subProcessNode = null;
		_operationNode = null;
		_operationComponentResource1 = null;
		_operationComponent1 = null;
		_operationComponentResource2 = null;
		_operationComponent2 = null;
		_bloc2 = null;
		_tab1ComponentResource = null;
		_tab2ComponentResource = null;

		codeRepository = null;
		docRepository = null;

		rootProcessJSCopy = null;
		subProcessJSCopy = null;
		htmlPropertiesCopy = null;

		operationComponent1ScreenshotCopyOfCopy = null;
		operationComponent2ScreenshotCopyOfCopy = null;
		operationComponent3ScreenshotCopyOfCopy = null;
		tabComponent1ScreenshotCopyOfCopy = null;
		tabComponent2ScreenshotCopyOfCopy = null;

		rootProcessScreenshotCopyOfCopy = null;
		subProcessNodeScreenshotInRootProcessCopyOfCopy = null;
		operationNode1ScreenshotCopyOfCopy = null;

		subProcessScreenshotCopyOfCopy = null;
		activityInSubProcessScreenshotCopyOfCopy = null;
		operationNode2ScreenshotCopyOfCopy = null;
		operationNode3ScreenshotCopyOfCopy = null;

		/*rootProcessScreenshotCopy = null;
		subProcessNodeScreenshotCopy = null;
		subProcessScreenshotCopy = null;
		activityInSubProcessScreenshotCopy = null;*/
		/*		processorRootProcessResource = null;
		processorSubProcessResource = null;*/
	}

	protected void reloadProject(boolean fullLoading, String tab1Name, String tab2Name) {
		if (_project != null) {
			_project.close();
		}
		resetVariables();

		try {
			assertNotNull(_editor = FlexoResourceManager.initializeExistingProject(_projectDirectory, EDITOR_FACTORY, null));
			_project = _editor.getProject();
			_project.getGeneratedCode().setFactory(this);
		} catch (ProjectInitializerException e) {
			e.printStackTrace();
			fail();
		} catch (ProjectLoadingCancelledException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(_rmResource = _project.getFlexoRMResource());
		assertNotNull(_wkfResource = _project.getFlexoWorkflowResource());
		assertNotNull(_dmResource = _project.getFlexoDMResource());
		assertNotNull(_dkvResource = _project.getFlexoDKVResource());
		assertNotNull(_clResource = _project.getFlexoComponentLibraryResource());
		assertNotNull(_menuResource = _project.getFlexoNavigationMenuResource());
		assertNotNull(_rootProcessResource = _project.getFlexoProcessResource(_projectIdentifier));
		assertNotNull(_executionModelResource = _project.getEOModelResource(FlexoExecutionModelRepository.EXECUTION_MODEL_DIR.getName()));
		assertNotNull(_eoPrototypesResource = _project.getEOModelResource(EOPrototypeRepository.EOPROTOTYPE_REPOSITORY_DIR.getName()));
		assertNotNull(_subProcessResource = _project.getFlexoProcessResource(TEST_SUB_PROCESS));
		if (fullLoading) {
			// strange stuff here since the subprocessnode don't have the name of the process...
			// since a more or less recent change : sub process nodes must have the same name that
			// the process they are referencing.
			_subProcessNode = _rootProcessResource.getFlexoProcess().getActivityPetriGraph().getSubProcessNodeNamed(TEST_SUB_PROCESS_NODE);
		}
		if (fullLoading) {
			assertNotNull(_operationNode = _rootProcessResource.getFlexoProcess().getActivityPetriGraph()
					.getOperationNodeNamed(TEST_OPERATION_NODE_1));
		}
		assertNotNull(_operationComponentResource1 = _project.getFlexoOperationComponentResource(OPERATION_COMPONENT_1));
		assertNotNull(_operationComponentResource2 = _project.getFlexoOperationComponentResource(OPERATION_COMPONENT_2));
		assertNotNull(_operationComponentResource3 = _project.getFlexoOperationComponentResource(OPERATION_COMPONENT_3));
		if (fullLoading) {
			assertNotNull(_operationComponent1 = _operationComponentResource1.getIEOperationComponent());
		}
		if (fullLoading) {
			assertNotNull(_operationComponent2 = _operationComponentResource2.getIEOperationComponent());
		}
		if (fullLoading) {
			assertNotNull(_operationComponent3 = _operationComponentResource3.getIEOperationComponent());
		}
		if (tab1Name != null) {
			assertNotNull(_tab1ComponentResource = _project.getFlexoTabComponentResource(tab1Name));
		}
		if (tab2Name != null) {
			assertNotNull(_tab2ComponentResource = _project.getFlexoTabComponentResource(tab2Name));
		}

		if (tab1Name != null) {
			_tab1 = _tab1ComponentResource.getComponentDefinition();
		}
		if (tab2Name != null) {
			_tab2 = _tab2ComponentResource.getComponentDefinition();
		}

		assertSynchonized(_dmResource, _executionModelResource);
		assertSynchonized(_dmResource, _eoPrototypesResource);
		assertSynchonized(_wkfResource, _rootProcessResource);
		assertDepends(_rootProcessResource, _dmResource);
		assertNotDepends(_rootProcessResource, _clResource);

		assertSynchonized(_subProcessResource, _rmResource);
		assertSynchonized(_subProcessResource, _wkfResource);
		assertDepends(_subProcessResource, _dmResource);
		assertNotDepends(_subProcessResource, _clResource);

		assertSynchonized(_operationComponentResource1, _rmResource);
		assertSynchonized(_operationComponentResource1, _clResource);
		assertDepends(_operationComponentResource1, _dmResource);

		assertDepends(_rootProcessResource, _operationComponentResource1);

		assertDepends(_subProcessResource, _operationComponentResource2);
		assertDepends(_subProcessResource, _operationComponentResource3);

		assertSynchonized(_operationComponentResource2, _rmResource);
		assertSynchonized(_operationComponentResource2, _clResource);
		assertDepends(_operationComponentResource2, _dmResource);

		assertSynchonized(_operationComponentResource3, _rmResource);
		assertSynchonized(_operationComponentResource3, _clResource);
		assertDepends(_operationComponentResource3, _dmResource);

		if (tab1Name != null) {
			assertSynchonized(_tab1ComponentResource, _rmResource);
			assertSynchonized(_tab1ComponentResource, _clResource);
			assertDepends(_tab1ComponentResource, _dmResource);
		}

		if (tab2Name != null) {
			assertSynchonized(_tab2ComponentResource, _rmResource);
			assertSynchonized(_tab2ComponentResource, _clResource);
			assertDepends(_tab2ComponentResource, _dmResource);
		}

		if (tab1Name != null) {
			assertDepends(_operationComponentResource1, _tab1ComponentResource);
		}
		if (tab2Name != null) {
			assertDepends(_operationComponentResource1, _tab2ComponentResource);
		}

		if (tab1Name != null) {
			assertDepends(_operationComponentResource2, _tab1ComponentResource);
		}
		if (tab2Name != null) {
			assertDepends(_operationComponentResource3, _tab2ComponentResource);
		}

	}

	protected void saveProject() {
		try {
			_project.save();
		} catch (SaveResourceException e) {
			fail("Cannot save project");
		}
		for (FlexoStorageResource<? extends StorageResourceData> resource : _project.getStorageResources()) {
			assertNotModified(resource);
		}
	}

	protected String tagStringWithLineNb(String fileContent, String marker, int beginIndex, int endIndex) {
		StringBuilder sb = new StringBuilder();
		StringTokenizer st = new StringTokenizer(fileContent, "\n");
		int i = 0;
		while (st.hasMoreTokens()) {
			if (i >= beginIndex && i < endIndex) {
				sb.append(marker).append(i);
			}
			sb.append(st.nextToken()).append("\n");
			i++;
		}
		return sb.toString();
	}

	protected String tagFileWithLineNb(File file, String marker, int beginIndex, int endIndex) {
		try {
			return tagStringWithLineNb(FileUtils.fileContents(file), marker, beginIndex, endIndex);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
			return null;
		}
	}

	protected void assertChange(MergeChange change, MergeChangeSource changeSource, MergeChangeType changeType, int first0, int last0,
			int first1, int last1, int first2, int last2) {
		assertEquals(change.getMergeChangeSource(), changeSource);
		assertEquals(change.getMergeChangeType(), changeType);
		assertEquals(first0, change.getFirst0());
		assertEquals(first1, change.getFirst1());
		assertEquals(first2, change.getFirst2());
		assertEquals(last0, change.getLast0());
		assertEquals(last1, change.getLast1());
		assertEquals(last2, change.getLast2());
	}

	protected CGRepository createNewCodeRepository(FlexoProject prj, CodeType targetType, String repositoryName) {
		File directory = new File(prj.getProjectDirectory().getParentFile(), "GeneratedCodeFor" + prj.getProjectName());
		directory.mkdirs();
		CGRepository reply = null;
		prj.setTargetType(targetType);
		AddGeneratedCodeRepository addCGRepository = AddGeneratedCodeRepository.actionType.makeNewAction(prj.getGeneratedCode(), null,
				_editor);
		addCGRepository.setNewGeneratedCodeRepositoryName(repositoryName);
		addCGRepository.setNewGeneratedCodeRepositoryDirectory(directory);
		addCGRepository.doAction();
		assertNotNull(reply = (CGRepository) addCGRepository.getNewGeneratedCodeRepository());
		reply.setTargetType(targetType);
		return reply;
	}

	public void synchronizeCodeGeneration(CGRepository codeRep) {
		// Synchronize code generation
		SynchronizeRepositoryCodeGeneration synchronizeCodeGeneration = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(
				codeRep, null, _editor);
		// Do it even if validation failed
		synchronizeCodeGeneration.setContinueAfterValidation(true);
		synchronizeCodeGeneration.doAction();

		// Write generated files to disk
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRep, null, _editor);
		writeToDisk.doAction();
		saveProject();
	}

	public void assertProjectIsValid(CGRepository codeRep) {
		validateProject(codeRep, true);
	}

	public void validateProject(CGRepository codeRep, boolean assertValid) {

		ValidateProject validateProject = ValidateProject.actionType.makeNewAction(codeRep, null, _editor);
		validateProject.doAction();
		if (logger.isLoggable(Level.INFO)) {
			logger.info(validateProject.readableValidationErrors());
		}

		if (assertValid) {
			// First project is not valid
			assertFalse(validateProject.isProjectValid());
		}

		// First project is not valid
		/*try {
		assertFalse(validateProject.isProjectValid());
		}
		catch (AssertionFailedError e) {
		}
		fail();*/

		// Try to fix errors
		FlexoComponentFolder rootFolder = _project.getFlexoComponentLibrary().getRootFolder();
		// rootFolder.setComponentPrefix("TST");
		_project.getFlexoNavigationMenu().getRootMenu().setProcess(_operationNode.getProcess());
		_project.getFlexoNavigationMenu().getRootMenu().setOperation(_operationNode);

		// Project should be without errors now
		validateProject = ValidateProject.actionType.makeNewAction(codeRepository, null, _editor);
		validateProject.doAction();
		if (logger.isLoggable(Level.INFO)) {
			logger.info(validateProject.readableValidationErrors());
		}

		if (assertValid) {
			assertTrue(validateProject.isProjectValid());
			saveProject();
		}

	}

	protected void defineStatusColumn(FlexoProcess process) {
		process.getBusinessDataType().createDMProperty("status",
				DMType.makeResolvedDMType(String.class, _rootProcessResource.getProject()),
				DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD);

	}

	protected void associateTabWithOperations() {
		// now we have to define tabs for operations
		Enumeration<OperationComponentInstance> en = _operationComponent1.getProject().getFlexoWorkflow()
				.getAllComponentInstances(_operationComponent1.getComponentDefinition()).elements();
		while (en.hasMoreElements()) {
			OperationNode operationNode = en.nextElement().getOperationNode();
			operationNode.setTabComponent(_tab1);
			assertDepends(operationNode.getProcess().getFlexoResource(), _tab1.getComponentResource());
		}
		en = _operationComponent2.getProject().getFlexoWorkflow().getAllComponentInstances(_operationComponent2.getComponentDefinition())
				.elements();
		while (en.hasMoreElements()) {
			OperationNode operationNode = en.nextElement().getOperationNode();
			operationNode.setTabComponent(_tab1);
			assertDepends(operationNode.getProcess().getFlexoResource(), _tab1.getComponentResource());
		}
		en = _operationComponent3.getProject().getFlexoWorkflow().getAllComponentInstances(_operationComponent3.getComponentDefinition())
				.elements();
		while (en.hasMoreElements()) {
			OperationNode operationNode = en.nextElement().getOperationNode();
			operationNode.setTabComponent(_tab2);
			assertDepends(operationNode.getProcess().getFlexoResource(), _tab2.getComponentResource());
		}
	}

	/**
	 * Sleeps so that the Velocity resource manager updates properly a modified template
	 */
	protected void waitForVelocityRefresh() {
		// We sleep for 5 seconds so that Velocity will refresh the template resource
		// This can be adapted in the velocity.properties file (property: file.resource.loader.modificationCheckInterval).
		// See org.apache.velocity.runtime.resource.ResourceManagerImpl.getResource(String, int, String)
		// and org.apache.velocity.runtime.resource.ResourceManagerImpl.refreshResource(Resource, String) for details
		int seconds = ToolBox.getPLATFORM() == ToolBox.MACOS ? 7 : 5;
		try {
			Thread.sleep(seconds * 1000 + 1);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
}
