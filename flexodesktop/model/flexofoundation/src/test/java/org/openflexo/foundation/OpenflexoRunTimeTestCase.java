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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.AfterClass;
import org.openflexo.antar.binding.KeyValueLibrary;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.xmlcode.KeyValueCoder;

/**
 * Provides a JUnit 4 generic environment of Openflexo-core for testing purposes
 * 
 */
public abstract class OpenflexoRunTimeTestCase {

	private static final Logger logger = FlexoLogger.getLogger(OpenflexoRunTimeTestCase.class.getPackage().getName());

	protected static FlexoEditor _editor;
	protected static FlexoProject _project;
	protected static File _projectDirectory;
	protected static String _projectIdentifier;
	protected static FlexoServiceManager serviceManager;
	protected static DirectoryResourceCenter resourceCenter;
	static {
		try {
			FlexoLoggingManager.initialize(-1, true, null, Level.WARNING, null);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownClass() {
		if (_project != null) {
			_project.close();
		}
		/*if (_projectDirectory != null) {
			FileUtils.deleteDir(_projectDirectory);
		}*/
		_editor = null;
		_projectDirectory = null;
		_project = null;

	}

	protected static final FlexoEditorFactory EDITOR_FACTORY = new FlexoEditorFactory() {
		@Override
		public DefaultFlexoEditor makeFlexoEditor(FlexoProject project, FlexoServiceManager serviceManager) {
			return new FlexoTestEditor(project, serviceManager);
		}
	};

	public static class FlexoTestEditor extends DefaultFlexoEditor {
		public FlexoTestEditor(FlexoProject project, FlexoServiceManager sm) {
			super(project, sm);
		}

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

	// TODO: create a project where all those tests don't need a manual import of projects
	// TODO: copy all test VP in tmp dir and work with those VP instead of polling GIT workspace
	protected static FlexoServiceManager instanciateTestServiceManager() {
		serviceManager = new DefaultFlexoServiceManager() {

			@Override
			protected FlexoEditor createApplicationEditor() {
				return new FlexoTestEditor(null, this);
			}

			@Override
			protected FlexoResourceCenterService createResourceCenterService() {
				File tempFile;
				try {
					tempFile = File.createTempFile("TestResourceCenter", "");
					File testResourceCenterDirectory = new File(tempFile.getParentFile(), "TestResourceCenter");
					testResourceCenterDirectory.mkdirs();
					FileUtils.copyContentDirToDir(new FileResource("src/test/resources/TestResourceCenter"), testResourceCenterDirectory);
					FlexoResourceCenterService rcService = DefaultResourceCenterService.getNewInstance();
					rcService.addToResourceCenters(resourceCenter = new DirectoryResourceCenter(testResourceCenterDirectory));
					return rcService;
				} catch (IOException e) {
					e.printStackTrace();
					fail();
					return null;
				}

			}
		};
		return serviceManager;
	}

	protected FlexoEditor createProject(String projectName) {
		if (serviceManager == null) {
			serviceManager = instanciateTestServiceManager();
		}
		return createProject(projectName, serviceManager);
	}

	protected static FlexoResourceCenterService getNewResourceCenter(String name) {
		try {
			return DefaultResourceCenterService.getNewInstance(Collections.singletonList(FileUtils.createTempDirectory(name,
					"ResourceCenter")));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		return null;
	}

	protected FlexoEditor createProject(String projectName, FlexoServiceManager serviceManager) {
		FlexoLoggingManager.forceInitialize(-1, true, null, Level.INFO, null);
		try {
			File tempFile = File.createTempFile(projectName, "");
			_projectDirectory = new File(tempFile.getParentFile(), tempFile.getName() + ".prj");
			tempFile.delete();
		} catch (IOException e) {
			fail();
		}
		logger.info("Project directory: " + _projectDirectory.getAbsolutePath());
		_projectIdentifier = _projectDirectory.getName().substring(0, _projectDirectory.getName().length() - 4);
		logger.info("Project identifier: " + _projectIdentifier);

		FlexoEditor reply;
		try {
			reply = FlexoProject.newProject(_projectDirectory, EDITOR_FACTORY, serviceManager, null);
		} catch (ProjectInitializerException e1) {
			e1.printStackTrace();
			fail(e1.getMessage());
			return null;
		}
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
		_editor = reply;
		_project = _editor.getProject();
		return reply;
	}

	protected static FlexoServiceManager getFlexoServiceManager() {
		return serviceManager;
	}

	protected void saveProject(FlexoProject prj) {
		try {
			prj.save();
		} catch (SaveResourceException e) {
			fail("Cannot save project");
		}
	}

	protected FlexoEditor reloadProject(File prjDir) {
		try {
			FlexoEditor _editor = null;
			assertNotNull(_editor = FlexoProject.openProject(prjDir, EDITOR_FACTORY,
			/*new DefaultProjectLoadingHandler(),*/serviceManager, null));
			// The next line is really a trouble maker and eventually causes more problems than solutions. FlexoProject can't be renamed on
			// the fly
			// without having a severe impact on many resources and importer projects. I therefore now comment this line which made me lost
			// hundreds of hours
			// _editor.getProject().setProjectName(_editor.getProject().getProjectName() + new Random().nextInt());
			_project = _editor.getProject();
			return _editor;
		} catch (ProjectInitializerException e) {
			e.printStackTrace();
			fail();
		} catch (ProjectLoadingCancelledException e) {
			e.printStackTrace();
			fail();
		}
		return null;
	}

	protected void assertNotModified(FlexoResource resource) {
		try {
			if (resource.isLoaded()) {
				assertFalse("Resource " + resource.getURI() + " should not be modfied", resource.getLoadedResourceData().isModified());
			} else {
				fail("Resource " + resource.getURI() + " should not be modified but is not even loaded");
			}
		} catch (AssertionFailedError e) {
			logger.warning("RESOURCE status problem: " + resource + " MUST be NOT modified");
			throw e;
		}
	}

	protected void assertModified(FlexoResource resource) {
		try {
			if (resource.isLoaded()) {
				assertTrue("Resource " + resource.getURI() + " should be modified", resource.getLoadedResourceData().isModified());
			} else {
				fail("Resource " + resource.getURI() + " should be modified but is not even loaded");
			}
		} catch (AssertionFailedError e) {
			logger.warning("RESOURCE status problem: " + resource + " MUST be modified");
			throw e;
		}
	}

	protected void assertNotLoaded(FlexoResource resource) {
		try {
			assertFalse("Resource " + resource.getURI() + " should not be loaded", resource.isLoaded());
		} catch (AssertionFailedError e) {
			logger.warning("RESOURCE status problem: " + resource + " MUST be NOT loaded");
			throw e;
		}
	}

	protected void assertLoaded(FlexoResource resource) {
		try {
			assertTrue("Resource " + resource.getURI() + " should be loaded", resource.isLoaded());
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

	@After
	public void tearDown() throws Exception {
		KeyValueCoder.clearClassCache();
		KeyValueLibrary.clearCache();
	}
}
