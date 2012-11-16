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
package org.openflexo.foundation;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.openflexo.antar.binding.KeyValueLibrary;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.dm.ComponentDMEntity;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.action.CreateDMEOEntity;
import org.openflexo.foundation.dm.action.CreateDMEOModel;
import org.openflexo.foundation.dm.action.CreateDMProperty;
import org.openflexo.foundation.dm.action.CreateProjectDatabaseRepository;
import org.openflexo.foundation.dm.eo.DMEOAdaptorType;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEReusableComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.OperationComponentInstance;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.action.DropPartialComponent;
import org.openflexo.foundation.ie.action.MakePartialComponent;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.action.AddComponent;
import org.openflexo.foundation.ie.cl.action.AddComponentFolder;
import org.openflexo.foundation.ie.util.WidgetType;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IEReusableWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.rm.FlexoOperationComponentResource;
import org.openflexo.foundation.rm.FlexoProcessResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.utils.DefaultProjectLoadingHandler;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFElementType;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.AddSubProcess;
import org.openflexo.foundation.wkf.action.CreateEdge;
import org.openflexo.foundation.wkf.action.CreatePetriGraph;
import org.openflexo.foundation.wkf.action.CreatePreCondition;
import org.openflexo.foundation.wkf.action.DropWKFElement;
import org.openflexo.foundation.wkf.action.OpenActionLevel;
import org.openflexo.foundation.wkf.action.OpenOperationLevel;
import org.openflexo.foundation.wkf.action.SetAndOpenOperationComponent;
import org.openflexo.foundation.wkf.action.WKFDelete;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.FatherNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ResourceLocator;
import org.openflexo.xmlcode.KeyValueCoder;

/**
 * @author gpolet
 * 
 */
public abstract class FlexoTestCase extends TestCase {

	private static final Logger logger = FlexoLogger.getLogger(FlexoTestCase.class.getPackage().getName());

	static {
		try {
			FlexoLoggingManager.initialize(-1, true, null, Level.WARNING, null);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected static final FlexoEditorFactory EDITOR_FACTORY = new FlexoEditorFactory() {
		@Override
		public DefaultFlexoEditor makeFlexoEditor(FlexoProject project) {
			return new FlexoTestEditor(project);
		}
	};

	public static class FlexoTestEditor extends DefaultFlexoEditor {
		public FlexoTestEditor(FlexoProject project) {
			super(project);
		}

	}

	public FlexoTestCase() {
	}

	protected void initResourceLocatorFromSystemProperty() {
		// GPO: Kept in case we need this, but we should try not to depend on this.
		logger.severe("Here is the system property : " + System.getProperty("flexo.resources.location"));
		if (System.getProperty("flexo.resources.location") != null) {
			ResourceLocator.resetFlexoResourceLocation(new File(System.getProperty("flexo.resources.location")));
		}
	}

	public FlexoTestCase(String name) {
		super(name);
		// initResourceLocatorFromSystemProperty();
		FlexoObject.initialize(false);
	}

	public File getResource(String resourceRelativeName) {
		File retval = new File("src/test/resources", resourceRelativeName);
		if (retval.exists()) {
			return retval;
		}
		retval = new File("../flexofoundation/src/test/resources", resourceRelativeName);
		if (retval.exists()) {
			return retval;
		}
		retval = new File("tmp/tests/FlexoResources/", resourceRelativeName);
		if (retval.exists()) {
			return retval;
		} else if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not find resource " + resourceRelativeName);
		}
		return null;
	}

	protected FlexoEditor createProject(String projectName) {
		return createProject(projectName, getNewResourceCenter(projectName));
	}

	protected LocalResourceCenterImplementation getNewResourceCenter(String name) {
		try {
			return LocalResourceCenterImplementation.instanciateNewLocalResourceCenterImplementation(FileUtils.createTempDirectory(name,
					"ResourceCenter"));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		return null;
	}

	protected FlexoEditor createProject(String projectName, FlexoResourceCenter resourceCenter) {
		FlexoLoggingManager.forceInitialize(-1, true, null, Level.INFO, null);
		File _projectDirectory = null;
		try {
			File tempFile = File.createTempFile(projectName, "");
			_projectDirectory = new File(tempFile.getParentFile(), tempFile.getName() + ".prj");
			tempFile.delete();
		} catch (IOException e) {
			fail();
		}
		logger.info("Project directory: " + _projectDirectory.getAbsolutePath());
		String _projectIdentifier = _projectDirectory.getName().substring(0, _projectDirectory.getName().length() - 4);
		logger.info("Project identifier: " + _projectIdentifier);
		FlexoEditor reply = FlexoResourceManager.initializeNewProject(_projectDirectory, EDITOR_FACTORY, resourceCenter);
		logger.info("Project has been SUCCESSFULLY created");
		try {
			reply.getProject().setProjectName(_projectIdentifier/*projectName*/);
			reply.getProject().saveModifiedResources(null);
		} catch (InvalidNameException e) {
			e.printStackTrace();
			fail();
		} catch (SaveResourceException e) {
			e.printStackTrace();
			fail();
		}
		return reply;
	}

	protected void saveProject(FlexoProject prj) {
		try {
			prj.save();
		} catch (SaveResourceException e) {
			fail("Cannot save project");
		}
	}

	@Deprecated
	protected FlexoEditor reloadProject(File prjDir) {
		try {
			FlexoEditor _editor = null;
			assertNotNull(_editor = FlexoResourceManager.initializeExistingProject(prjDir, EDITOR_FACTORY, null));
			_editor.getProject().setProjectName(_editor.getProject().getProjectName() + new Random().nextInt());
			return _editor;
		} catch (ProjectInitializerException e) {
			e.printStackTrace();
			fail();
		} catch (ProjectLoadingCancelledException e) {
			e.printStackTrace();
			fail();
		} catch (InvalidNameException e) {
			e.printStackTrace();
			fail();
		}
		return null;
	}

	protected FlexoEditor reloadProject(File prjDir, FlexoResourceCenter resourceCenter, FlexoProjectReferenceLoader projectReferenceLoader) {
		try {
			FlexoEditor _editor = null;
			assertNotNull(_editor = FlexoResourceManager.initializeExistingProject(prjDir, null, EDITOR_FACTORY,
					new DefaultProjectLoadingHandler(), projectReferenceLoader, resourceCenter));
			_editor.getProject().setProjectName(_editor.getProject().getProjectName() + new Random().nextInt());
			return _editor;
		} catch (ProjectInitializerException e) {
			e.printStackTrace();
			fail();
		} catch (ProjectLoadingCancelledException e) {
			e.printStackTrace();
			fail();
		} catch (InvalidNameException e) {
			e.printStackTrace();
			fail();
		}
		return null;
	}

	protected void assertSynchonized(FlexoResource resource1, FlexoResource resource2) {
		try {
			assertTrue(resource1.getSynchronizedResources().contains(resource2));
			assertTrue(resource2.getSynchronizedResources().contains(resource1));
		} catch (AssertionFailedError e) {
			logger.severe("RESOURCE synchonization problem: " + resource1 + " MUST be synchronized with " + resource2);
			throw e;
		}
	}

	/**
	 * Assert resource1 depends of resource2
	 * 
	 * @param resource1
	 * @param resource2
	 */
	protected void assertDepends(FlexoResource resource1, FlexoResource resource2) {
		try {
			assertTrue(resource1.getDependentResources().contains(resource2));
			assertTrue(resource2.getAlteredResources().contains(resource1));
		} catch (AssertionFailedError e) {
			logger.severe("RESOURCE synchonization problem: " + resource1 + " MUST depends on " + resource2);
			throw e;
		}
	}

	/**
	 * Assert resource1 depends of resource2
	 * 
	 * @param resource1
	 * @param resource2
	 */
	protected void assertNotDepends(FlexoResource resource1, FlexoResource resource2) {
		try {
			assertFalse(resource1.getDependentResources().contains(resource2));
			assertFalse(resource2.getAlteredResources().contains(resource1));
		} catch (AssertionFailedError e) {
			logger.severe("RESOURCE synchonization problem: " + resource1 + " MUST depends on " + resource2);
			throw e;
		}
	}

	protected void assertNotModified(FlexoStorageResource resource) {
		try {
			assertFalse("Resource " + resource.getResourceIdentifier() + " should not be modfied", resource.isModified());
		} catch (AssertionFailedError e) {
			logger.warning("RESOURCE status problem: " + resource + " MUST be NOT modified");
			throw e;
		}
	}

	protected void assertModified(FlexoStorageResource resource) {
		try {
			assertTrue("Resource " + resource.getResourceIdentifier() + " should be modfied", resource.isModified());
		} catch (AssertionFailedError e) {
			logger.warning("RESOURCE status problem: " + resource + " MUST be modified");
			throw e;
		}
	}

	protected void assertNotLoaded(FlexoStorageResource resource) {
		try {
			assertFalse("Resource " + resource.getResourceIdentifier() + " should not be loaded", resource.isLoaded());
		} catch (AssertionFailedError e) {
			logger.warning("RESOURCE status problem: " + resource + " MUST be NOT loaded");
			throw e;
		}
	}

	protected void assertLoaded(FlexoStorageResource resource) {
		try {
			assertTrue("Resource " + resource.getResourceIdentifier() + " should be loaded", resource.isLoaded());
		} catch (AssertionFailedError e) {
			logger.warning("RESOURCE status problem: " + resource + " MUST be loaded");
			throw e;
		}
	}

	protected static void log(String step) {
		logger.info("\n******************************************************************************\n" + step
				+ "\n******************************************************************************\n");
	}

	/**
	 * Assert this is the same list, doesn't care about order
	 * 
	 * @param aList
	 * @param objects
	 * @throws AssertionFailedError
	 */
	public <T> void assertSameList(Collection<T> aList, T... objects) throws AssertionFailedError {
		Set<T> set1 = new HashSet<T>(aList);
		Set<T> set2 = new HashSet<T>();
		for (T o : objects) {
			set2.add(o);
		}
		if (!set1.equals(set2)) {
			StringBuffer message = new StringBuffer();
			for (T o : set1) {
				if (!set2.contains(o)) {
					message.append(" Extra: " + o);
				}
			}
			for (T o : set2) {
				if (!set1.contains(o)) {
					message.append(" Missing: " + o);
				}
			}
			throw new AssertionFailedError("AssertionFailedError when comparing lists, expected: " + set1 + " but was " + set2
					+ " Details = " + message);
		}
	}

	public static IEWOComponent createComponent(String componentName, FlexoComponentFolder folder, AddComponent.ComponentType type,
			FlexoEditor editor) {
		AddComponent addComponent = AddComponent.actionType.makeNewAction(folder, null, editor);
		addComponent.setNewComponentName(componentName);
		addComponent.setComponentType(type);
		addComponent.doAction();
		assertTrue(addComponent.hasActionExecutionSucceeded());
		IEWOComponent reply = null;
		if (type.equals(AddComponent.ComponentType.OPERATION_COMPONENT)) {
			reply = folder.getProject().getOperationComponent(componentName);
		} else if (type.equals(AddComponent.ComponentType.POPUP_COMPONENT)) {
			reply = folder.getProject().getPopupComponent(componentName);
		} else if (type.equals(AddComponent.ComponentType.TAB_COMPONENT)) {
			reply = folder.getProject().getTabComponent(componentName);
		} else if (type.equals(AddComponent.ComponentType.PARTIAL_COMPONENT)) {
			reply = folder.getProject().getSingleWidgetComponent(componentName);
		} else if (type.equals(AddComponent.ComponentType.DATA_COMPONENT)) {
			fail("Data component not implemented");
		} else if (type.equals(AddComponent.ComponentType.MONITORING_COMPONENT)) {
			fail("Monitoring component not implemented");
		} else if (type.equals(AddComponent.ComponentType.MONITORING_SCREEN)) {
			fail("Monitoring screen not implemented");
		}
		assertNotNull(reply);
		return reply;
	}

	public static FlexoComponentFolder createFolder(String folderName, FlexoComponentFolder parentFolder, FlexoEditor editor) {
		AddComponentFolder addComponentFolder = AddComponentFolder.actionType.makeNewAction(editor.getProject().getFlexoComponentLibrary(),
				null, editor);
		addComponentFolder.setNewFolderName(folderName);
		addComponentFolder.setParentFolder(parentFolder);
		addComponentFolder.doAction();
		assertTrue(addComponentFolder.hasActionExecutionSucceeded());
		FlexoComponentFolder reply = editor.getProject().getFlexoComponentLibrary().getFlexoComponentFolderWithName(folderName);
		assertNotNull(reply);
		if (parentFolder != null) {
			assertEquals(parentFolder, reply.getParent());
		} else {
			assertEquals(editor.getProject().getFlexoComponentLibrary().getRootFolder(), reply.getParent());
		}
		return reply;
	}

	protected static IEBlocWidget dropBlocAtIndex(String blocTitle, IEWOComponent wo, int index, FlexoEditor editor) {
		DropIEElement dropBloc1 = DropIEElement.createBlocInComponent(wo, index, editor);
		assertTrue(dropBloc1.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc1 = (IEBlocWidget) dropBloc1.getDroppedWidget();
		assertNotNull(bloc1);
		bloc1.setTitle("Bloc1");
		return bloc1;
	}

	public static void assertHTMLTableIsValid(IEHTMLTableWidget table) {
		Assert.assertNotNull(table.getProject());
		int rowCount = table.getRowCount();
		int colCount = table.getColCount();
		for (int i = 0; i < rowCount; i++) {
			if (table.getTR(i) == null) {
				System.out.println("Here we have a null TR. Breakpoint it");
			}
			System.out.println(table.getTR(i).getRowIndex() + "==" + i);
			if (table.getTR(i).getRowIndex() == 0 && i == 2) {
				System.out.println("will fail. Breakpoint it");
			}
			// Assert.assertTrue(getTR(i).getRowIndex()==i);
			Assert.assertTrue(table.getTR(i).getColCount() == colCount);
		}

	}

	protected static IEHTMLTableWidget dropTableInBloc(IEBlocWidget bloc1, FlexoEditor editor) {
		DropIEElement dropTable = DropIEElement.createTableInBloc(bloc1, editor);
		assertTrue(dropTable.doAction().hasActionExecutionSucceeded());
		IEHTMLTableWidget table = (IEHTMLTableWidget) dropTable.getDroppedWidget();
		assertNotNull(table);
		assertHTMLTableIsValid(table);
		return table;
	}

	protected static IEWidget dropWidgetInTable(WidgetType widgetType, IEHTMLTableWidget table, int tdx, int tdy, int indexInTD,
			FlexoEditor editor) {
		DropIEElement dropIEWidget = DropIEElement.insertWidgetInTable(table, widgetType, tdx, tdy, indexInTD, editor);
		assertTrue(dropIEWidget.doAction().hasActionExecutionSucceeded());
		IEWidget widget = dropIEWidget.getDroppedWidget();
		assertNotNull(widget);
		assertHTMLTableIsValid(table);
		return widget;
	}

	protected static IEReusableWidget makePartial(String partialComponentName, FlexoComponentFolder folder, IEWidget rootWidget,
			FlexoEditor editor) {
		MakePartialComponent makePartial = MakePartialComponent.actionType.makeNewAction(rootWidget, null, editor);
		makePartial.setNewComponentName(partialComponentName);
		makePartial.setNewComponentFolder(folder);
		assertTrue(makePartial.doAction().hasActionExecutionSucceeded());
		assertEquals(rootWidget.getWOComponent(), makePartial.getComponent());
		return makePartial.getReusableWidget();
	}

	protected static IEReusableWidget dropPartialComponent(IEReusableComponent droppedComponent, IEObject targetObject, FlexoEditor editor) {
		DropPartialComponent dropPartial = DropPartialComponent.actionType.makeNewAction(targetObject, null, editor);
		dropPartial.setPartialComponent(droppedComponent.getComponentDefinition());
		assertTrue(dropPartial.doAction().hasActionExecutionSucceeded());
		return dropPartial.getDroppedWidget();
	}

	public static DMProperty createProperty(DMEntity entity, String newPropertyName, FlexoEditor editor) {
		CreateDMProperty createDMProperty = CreateDMProperty.actionType.makeNewAction(entity, null, editor);
		createDMProperty.setNewPropertyName(newPropertyName);
		createDMProperty.doAction();
		assertTrue(createDMProperty.hasActionExecutionSucceeded());
		DMProperty property = createDMProperty.getNewProperty();
		assertNotNull(property);
		if (entity instanceof ComponentDMEntity) {
			ComponentDMEntity componentEntity = (ComponentDMEntity) entity;
			assertFalse(componentEntity.isMandatory(property));
			assertFalse(componentEntity.isSettable(property));
			assertFalse(componentEntity.isBindable(property));
			componentEntity.setMandatory(property, true);
			assertTrue(componentEntity.isBindable(property));
			assertTrue(componentEntity.isMandatory(property));
			assertFalse(componentEntity.isSettable(property));
			componentEntity.setSettable(property, true);
			assertTrue(componentEntity.isBindable(property));
			assertTrue(componentEntity.isMandatory(property));
			assertTrue(componentEntity.isSettable(property));
			componentEntity.setBindable(property, false);
			assertFalse(componentEntity.isBindable(property));
			assertFalse(componentEntity.isMandatory(property));
			assertFalse(componentEntity.isSettable(property));
		}
		return property;
	}

	public static DMEOEntity createDMEOEntity(FlexoEditor editor, DMEOModel model, String name) {
		CreateDMEOEntity eoEntity = CreateDMEOEntity.actionType.makeNewAction(model, null, editor);
		eoEntity.doAction();
		if (!eoEntity.hasActionExecutionSucceeded()) {
			fail("Could not create EOEntity");
		}
		DMEOEntity e = eoEntity.getNewEntity();
		if (name != null) {
			try {
				e.setName(name);
			} catch (InvalidNameException e1) {
				e1.printStackTrace();
				fail("Entity name '" + name + "' is invalid!");
			}
		}
		return e;
	}

	public static DMEOModel createDMEOModel(FlexoEditor editor, DMEORepository rep, String modelName) {
		if (modelName == null) {
			modelName = "MyEOModel";
		}
		if (!modelName.endsWith(".eomodeld")) {
			modelName += ".eomodeld";
		}
		CreateDMEOModel eoModel = CreateDMEOModel.actionType.makeNewAction(rep, null, editor);
		eoModel.setEOModelFile(new File(modelName));
		eoModel.setAdaptorType(DMEOAdaptorType.JDBC);
		eoModel.doAction();
		if (!eoModel.hasActionExecutionSucceeded()) {
			fail("Could not create EOModel.");
		}
		return eoModel.getNewDMEOModel();
	}

	public static DMEORepository createDMEORepository(FlexoEditor editor, String repName) {
		if (repName == null) {
			repName = "MyNewRepository";
		}
		CreateProjectDatabaseRepository rep = CreateProjectDatabaseRepository.actionType.makeNewAction(editor.getProject().getDataModel(),
				null, editor);
		rep.setNewRepositoryName(repName);
		rep.doAction();
		if (!rep.hasActionExecutionSucceeded()) {
			fail("Could not create database repository.");
		}
		return (DMEORepository) rep.getNewRepository();
	}

	public static FlexoProcess createSubProcess(String subProcessName, FlexoProcess parentProcess, FlexoEditor editor) {
		AddSubProcess action = AddSubProcess.actionType.makeNewAction(parentProcess != null ? parentProcess : editor.getProject()
				.getWorkflow(), null, editor);
		action.setParentProcess(parentProcess);
		action.setNewProcessName(subProcessName);
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		FlexoProcessResource _subProcessResource = editor.getProject().getFlexoProcessResource(subProcessName);
		assertNotNull(_subProcessResource);
		assertNotNull(_subProcessResource.getFlexoProcess());
		assertEquals(_subProcessResource.getFlexoProcess().getParentProcess(), parentProcess);
		assertEquals(_subProcessResource.getFlexoProcess(), editor.getProject().getLocalFlexoProcess(subProcessName));
		return _subProcessResource.getFlexoProcess();
	}

	public static SubProcessNode instanciateForkSubProcess(FlexoProcess subProcess, FlexoProcess parentProcess, int x, int y,
			FlexoEditor editor) {
		return instanciateSubProcess(subProcess, parentProcess, x, y, WKFElementType.MULTIPLE_INSTANCE_PARALLEL_SUB_PROCESS_NODE, editor);
	}

	public static SubProcessNode instanciateLoopSubProcess(FlexoProcess subProcess, FlexoProcess parentProcess, int x, int y,
			FlexoEditor editor) {
		return instanciateSubProcess(subProcess, parentProcess, x, y, WKFElementType.MULTIPLE_INSTANCE_SEQUENTIAL_SUB_PROCESS_NODE, editor);
	}

	public static SubProcessNode instanciateSingleSubProcess(FlexoProcess subProcess, FlexoProcess parentProcess, int x, int y,
			FlexoEditor editor) {
		return instanciateSubProcess(subProcess, parentProcess, x, y, WKFElementType.SINGLE_INSTANCE_SUB_PROCESS_NODE, editor);
	}

	private static SubProcessNode instanciateSubProcess(FlexoProcess subProcess, FlexoProcess parentProcess, int x, int y,
			WKFElementType elementType, FlexoEditor editor) {
		DropWKFElement action = DropWKFElement.actionType.makeNewAction(parentProcess.getActivityPetriGraph(), null, editor);
		action.setElementType(elementType);
		action.setParameter(DropWKFElement.SUB_PROCESS, subProcess);
		action.setLocation(x, y);
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		assertNotNull(action.getObject());
		assertEquals(((SubProcessNode) action.getObject()).getProcess(), parentProcess);
		assertEquals(((SubProcessNode) action.getObject()).getSubProcess(), subProcess);

		return (SubProcessNode) action.getObject();
	}

	private static AbstractNode createNode(FlexoPetriGraph petriGraph, int x, int y, String nodeName, WKFElementType elementType,
			FlexoEditor editor) {
		DropWKFElement action = DropWKFElement.actionType.makeNewAction(petriGraph, null, editor);
		action.setElementType(elementType);
		((AbstractNode) action.getObject()).setName(nodeName);
		action.setLocation(x, y);
		action.setResetNodeName(false);
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		assertNotNull(action.getObject());
		assertEquals(nodeName, action.getObject().getName());
		return (AbstractNode) action.getObject();
	}

	public static AbstractActivityNode createActivityNode(FlexoProcess process, int x, int y, String activityName, FlexoEditor editor) {
		AbstractActivityNode reply = (AbstractActivityNode) createNode(process.getActivityPetriGraph(), x, y, activityName,
				WKFElementType.NORMAL_ACTIVITY, editor);
		assertNotNull(reply);
		assertEquals(reply.getProcess(), process);
		return reply;
	}

	public static AbstractActivityNode createBeginActivityNode(FlexoProcess process, int x, int y, String activityName, FlexoEditor editor) {
		return (AbstractActivityNode) createNode(process.getActivityPetriGraph(), x, y, activityName, WKFElementType.BEGIN_ACTIVITY, editor);
	}

	public static AbstractActivityNode createEndActivityNode(FlexoProcess process, int x, int y, String activityName, FlexoEditor editor) {
		return (AbstractActivityNode) createNode(process.getActivityPetriGraph(), x, y, activityName, WKFElementType.END_ACTIVITY, editor);
	}

	public static AbstractActivityNode createSelfExcutableActivityNode(FlexoProcess process, int x, int y, String activityName,
			FlexoEditor editor) {
		return (AbstractActivityNode) createNode(process.getActivityPetriGraph(), x, y, activityName,
				WKFElementType.SELF_EXECUTABLE_ACTIVITY, editor);
	}

	public static AbstractActivityNode createANDNode(FlexoPetriGraph pg, int x, int y, String activityName, FlexoEditor editor) {
		return (AbstractActivityNode) createNode(pg, x, y, activityName, WKFElementType.AND_OPERATOR, editor);
	}

	public static AbstractActivityNode createORNode(FlexoPetriGraph pg, int x, int y, String activityName, FlexoEditor editor) {
		return (AbstractActivityNode) createNode(pg, x, y, activityName, WKFElementType.OR_OPERATOR, editor);
	}

	public static AbstractActivityNode createIFNode(FlexoPetriGraph pg, int x, int y, String activityName, FlexoEditor editor) {
		return (AbstractActivityNode) createNode(pg, x, y, activityName, WKFElementType.IF_OPERATOR, editor);
	}

	public static AbstractActivityNode createLoopNode(FlexoPetriGraph pg, int x, int y, String activityName, FlexoEditor editor) {
		return (AbstractActivityNode) createNode(pg, x, y, activityName, WKFElementType.LOOP_OPERATOR, editor);
	}

	public static OperationNode createOperationNode(String operationNodeName, AbstractActivityNode activityNode, int x, int y,
			FlexoEditor editor) {
		if (activityNode.getOperationPetriGraph() == null) {
			openOperationLevel(activityNode, editor);
		}
		OperationNode reply = (OperationNode) createNode(activityNode.getOperationPetriGraph(), x, y, operationNodeName,
				WKFElementType.NORMAL_OPERATION, editor);
		assertNotNull(reply);
		assertEquals(reply.getProcess(), activityNode.getProcess());
		assertEquals(reply.getParentPetriGraph().getProcess(), activityNode.getProcess());
		return reply;
	}

	public static OperationNode createBeginOperationNode(String operationNodeName, AbstractActivityNode activityNode, int x, int y,
			FlexoEditor editor) {
		if (activityNode.getOperationPetriGraph() == null) {
			openOperationLevel(activityNode, editor);
		}
		return (OperationNode) createNode(activityNode.getOperationPetriGraph(), x, y, operationNodeName, WKFElementType.BEGIN_OPERATION,
				editor);
	}

	public static OperationNode createEndOperationNode(String operationNodeName, AbstractActivityNode activityNode, int x, int y,
			FlexoEditor editor) {
		if (activityNode.getOperationPetriGraph() == null) {
			openOperationLevel(activityNode, editor);
		}
		return (OperationNode) createNode(activityNode.getOperationPetriGraph(), x, y, operationNodeName, WKFElementType.END_OPERATION,
				editor);
	}

	public static OperationNode createSelfExcutableOperationNode(String operationNodeName, AbstractActivityNode activityNode, int x, int y,
			FlexoEditor editor) {
		if (activityNode.getOperationPetriGraph() == null) {
			openOperationLevel(activityNode, editor);
		}
		return (OperationNode) createNode(activityNode.getOperationPetriGraph(), x, y, operationNodeName,
				WKFElementType.SELF_EXECUTABLE_OPERATION, editor);
	}

	public static FlexoPreCondition createPreCondition(FlexoNode attachedToNode, FlexoNode attachedBeginNode, FlexoEditor editor) {
		CreatePreCondition create = CreatePreCondition.actionType.makeNewAction(attachedToNode, null, editor);
		create.setAttachedBeginNode(attachedBeginNode);
		create.doAction();
		assertNotNull(create.getNewPreCondition());
		return create.getNewPreCondition();
	}

	public static FlexoPetriGraph createPetriGraph(FatherNode father, FlexoEditor editor) {
		CreatePetriGraph create = CreatePetriGraph.actionType.makeNewAction(father, null, editor);
		create.doAction();
		assertNotNull(father.getContainedPetriGraph());
		return father.getContainedPetriGraph();
	}

	public static void openOperationLevel(AbstractActivityNode activityNode, FlexoEditor editor) {
		OpenOperationLevel openOperationLevel = OpenOperationLevel.actionType.makeNewAction(activityNode, null, editor);
		openOperationLevel.doAction();
	}

	public static void openActionLevel(OperationNode operationNode, FlexoEditor editor) {
		OpenActionLevel openOperationLevel = OpenActionLevel.actionType.makeNewAction(operationNode, null, editor);
		openOperationLevel.doAction();
	}

	public static ActionNode createBeginActionNode(String actionNodeName, OperationNode operationNode, int x, int y, FlexoEditor editor) {
		if (operationNode.getActionPetriGraph() == null) {
			openActionLevel(operationNode, editor);
		}
		return (ActionNode) createNode(operationNode.getActionPetriGraph(), x, y, actionNodeName, WKFElementType.BEGIN_ACTION, editor);
	}

	public static ActionNode createEndActionNode(String actionNodeName, OperationNode operationNode, int x, int y, FlexoEditor editor) {
		if (operationNode.getActionPetriGraph() == null) {
			openActionLevel(operationNode, editor);
		}
		return (ActionNode) createNode(operationNode.getActionPetriGraph(), x, y, actionNodeName, WKFElementType.END_ACTION, editor);
	}

	public static ActionNode createFlexoActionNode(String actionNodeName, OperationNode operationNode, int x, int y, FlexoEditor editor) {
		if (operationNode.getActionPetriGraph() == null) {
			openActionLevel(operationNode, editor);
		}
		return (ActionNode) createNode(operationNode.getActionPetriGraph(), x, y, actionNodeName, WKFElementType.FLEXO_ACTION, editor);
	}

	public static ActionNode createSelfActivatedActionNode(String actionNodeName, OperationNode operationNode, int x, int y,
			FlexoEditor editor) {
		if (operationNode.getActionPetriGraph() == null) {
			openActionLevel(operationNode, editor);
		}
		return (ActionNode) createNode(operationNode.getActionPetriGraph(), x, y, actionNodeName, WKFElementType.END_ACTIVITY, editor);
	}

	public static ActionNode createSelfExecutableActionNode(String actionNodeName, OperationNode operationNode, int x, int y,
			FlexoEditor editor) {
		if (operationNode.getActionPetriGraph() == null) {
			openActionLevel(operationNode, editor);
		}
		return (ActionNode) createNode(operationNode.getActionPetriGraph(), x, y, actionNodeName, WKFElementType.SELF_EXECUTABLE_ACTION,
				editor);
	}

	@Deprecated
	public static ActionNode createNextPageActionNode(String actionNodeName, OperationNode operationNode, int x, int y, FlexoEditor editor) {
		if (operationNode.getActionPetriGraph() == null) {
			openActionLevel(operationNode, editor);
		}
		return (ActionNode) createNode(operationNode.getActionPetriGraph(), x, y, actionNodeName, null, editor);
	}

	@Deprecated
	public static ActionNode createCreateSubProcessActionNode(String actionNodeName, OperationNode operationNode, int x, int y,
			FlexoEditor editor) {
		if (operationNode.getActionPetriGraph() == null) {
			openActionLevel(operationNode, editor);
		}
		return (ActionNode) createNode(operationNode.getActionPetriGraph(), x, y, actionNodeName, null, editor);
	}

	@Deprecated
	public static ActionNode createExecuteSubProcessActionNode(String actionNodeName, OperationNode operationNode, int x, int y,
			FlexoEditor editor) {
		if (operationNode.getActionPetriGraph() == null) {
			openActionLevel(operationNode, editor);
		}
		return (ActionNode) createNode(operationNode.getActionPetriGraph(), x, y, actionNodeName, null, editor);
	}

	public static EventNode createMailInNode(String nodeName, FlexoPetriGraph pg, int x, int y, FlexoEditor editor) {
		return (EventNode) createNode(pg, x, y, nodeName, WKFElementType.MAIL_IN, editor);
	}

	public static EventNode createMailOutNode(String nodeName, FlexoPetriGraph pg, int x, int y, FlexoEditor editor) {
		return (EventNode) createNode(pg, x, y, nodeName, WKFElementType.MAIL_OUT, editor);
	}

	public static EventNode createCancelHandlerNode(String nodeName, FlexoPetriGraph pg, int x, int y, FlexoEditor editor) {
		return (EventNode) createNode(pg, x, y, nodeName, WKFElementType.CANCEL_HANDLER, editor);
	}

	public static EventNode createCancelThrowerNode(String nodeName, FlexoPetriGraph pg, int x, int y, FlexoEditor editor) {
		return (EventNode) createNode(pg, x, y, nodeName, WKFElementType.CANCEL_THROWER, editor);
	}

	public static EventNode createCheckPointNode(String nodeName, FlexoPetriGraph pg, int x, int y, FlexoEditor editor) {
		return (EventNode) createNode(pg, x, y, nodeName, WKFElementType.CHECKPOINT, editor);
	}

	public static EventNode createFaultHandlerNode(String nodeName, FlexoPetriGraph pg, int x, int y, FlexoEditor editor) {
		return (EventNode) createNode(pg, x, y, nodeName, WKFElementType.FAULT_HANDLER, editor);
	}

	public static EventNode createFaultThrowerNode(String nodeName, FlexoPetriGraph pg, int x, int y, FlexoEditor editor) {
		return (EventNode) createNode(pg, x, y, nodeName, WKFElementType.FAULT_THROWER, editor);
	}

	public static EventNode createRevertNode(String nodeName, FlexoPetriGraph pg, int x, int y, FlexoEditor editor) {
		return (EventNode) createNode(pg, x, y, nodeName, WKFElementType.REVERT, editor);
	}

	public static EventNode createTimeOutNode(String nodeName, FlexoPetriGraph pg, int x, int y, FlexoEditor editor) {
		return (EventNode) createNode(pg, x, y, nodeName, WKFElementType.TIME_OUT, editor);
	}

	public static EventNode createTimerNode(String nodeName, FlexoPetriGraph pg, int x, int y, FlexoEditor editor) {
		return (EventNode) createNode(pg, x, y, nodeName, WKFElementType.TIMER, editor);
	}

	public static OperationComponentInstance bindOperationWithComponentNamed(OperationNode operationNode, String componentName,
			FlexoEditor editor) {
		SetAndOpenOperationComponent setOperationComponent = SetAndOpenOperationComponent.actionType.makeNewAction(operationNode, null,
				editor);
		setOperationComponent.setNewComponentName(componentName);
		setOperationComponent.doAction();
		assertTrue(setOperationComponent.hasActionExecutionSucceeded());
		FlexoOperationComponentResource _operationComponentResource1 = operationNode.getProject().getFlexoOperationComponentResource(
				componentName);
		assertNotNull(_operationComponentResource1);
		assertNotNull(operationNode.getComponentInstance());
		return operationNode.getComponentInstance();
	}

	public static FlexoPostCondition createEdge(AbstractNode start, AbstractNode end, FlexoEditor editor) {
		CreateEdge createEdge = CreateEdge.actionType.makeNewAction(start, null, editor);
		createEdge.setStartingNode(start);
		createEdge.setEndNode(end);
		createEdge.doAction();
		assertTrue(createEdge.hasActionExecutionSucceeded());
		return createEdge.getNewPostCondition();
	}

	public static void deleteWKFObjects(WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		WKFDelete deleteAction = WKFDelete.actionType.makeNewAction(focusedObject, globalSelection, editor);
		assertTrue(deleteAction.doAction().hasActionExecutionSucceeded());
	}

	@Override
	protected void tearDown() throws Exception {
		KeyValueCoder.clearClassCache();
		KeyValueLibrary.clearCache();
		super.tearDown();
	}
}
