package org.openflexo.builders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ResourceLocator;
import org.openflexo.toolbox.ZipUtils;
import org.openflexo.velocity.FlexoVelocity;

public abstract class AbstractTestExternalBuilders<M extends FlexoExternalMain> {

	private File projectFile;

	private Class<M> mainClass;

	private M main;

	private File workingDir;

	private FlexoResourceCenterService impl;

	protected AbstractTestExternalBuilders(Class<M> mainClass) {
		super();
		this.mainClass = mainClass;
	}

	@BeforeClass
	public static void staticSetUp() {
		ResourceLocator.addProjectDirectory(ResourceLocator.findProjectDirectoryWithName(new File(System.getProperty("user.dir")),
				"flexoexternalbuilders"));
	}

	@Before
	public void setUp() throws IOException, InstantiationException, IllegalAccessException {
		File zippedProject = new FileResource("TestProject.zip");
		workingDir = FileUtils.createTempDirectory("TestExternalBuilder", "");
		ZipUtils.unzip(zippedProject, workingDir, null);
		File[] files = workingDir.listFiles();
		for (File file : files) {
			if (file.isDirectory() && file.getName().endsWith(".prj")) {
				projectFile = file;
				break;
			}
		}
		main = mainClass.newInstance();
		impl = DefaultResourceCenterService.getNewInstance(new File(workingDir, "ResourceCenter"));
		main.setResourceCenterService(impl);
	}

	public List<String> getArgList() {
		return new ArrayList<String>(Arrays.asList(FlexoExternalMain.DEV_ARGUMENT));
	}

	public void addArgument(List<String> argList, String... args) {
		for (String arg : args) {
			argList.add(arg);
		}
	}

	public File getProjectFile() {
		return projectFile;
	}

	public File getWorkingDir() {
		return workingDir;
	}

	@After
	public void tearDown() {
		if (main instanceof FlexoExternalMainWithProject) {
			((FlexoExternalMainWithProject) main).close();
		}
		FlexoVelocity.clearCaches();
		if (projectFile != null) {
			FileUtils.deleteDir(projectFile.getParentFile());
		}
		main = null;
		projectFile = null;
	}

	protected M executeWithArgs(String[] args) {
		main.build(args);
		if (!main.isDone()) {
			synchronized (main) {
				while (!main.isDone()) {
					try {
						main.setNotifyOnExit(true);
						main.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return main;
	}
}
