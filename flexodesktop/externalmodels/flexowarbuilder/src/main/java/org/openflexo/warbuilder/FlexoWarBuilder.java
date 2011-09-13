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
package org.openflexo.warbuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tools.ant.BuildListener;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.gen.GenerationProgressNotification;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.toolbox.ZipUtils;

public class FlexoWarBuilder extends FlexoObservable {
	protected static final Logger logger = FlexoLogger
			.getLogger(FlexoWarBuilder.class.getPackage().getName());

	private static final String ZIPPED_TEMPLATE_PROJECT_PATH = "Config/Generator/Libraries/template-project.zip";

	private static final String WO_BUILD_PROPERTIES = "wobuild.properties";

	private static final String[] ABSOLUTE_KEYS = new String[] { "wo.dir.root",
			"wo.woroot", "wo.dir.local.library", "wo.wolocalroot" };

	private static final String[] USER_HOME_KEYS = new String[] {
			"wo.dir.user.home.library.frameworks", "wo.dir.user.home.library" };

	private static final String[] WO_DIR_KEYS = new String[] { "wo.dir.local",
			"wo.wosystemroot", "wo.dir.library", "wo.dir.library.frameworks",
			"wo.dir.local.library.frameworks", "wo.dir.system" };

	private static final String[] DEFAULT_ABSOLUTE_PATH = new String[] { "/",
			"/", "/Library", "/" };

	private static final String[] DEFAULT_USER_HOME_RELATIVE_PATH = new String[] {
			"Library/Frameworks", "Library" };

	private static final String[] DEFAULT_WO_DIR_RELATIVE_PATH = new String[] {
			"", "", "Library", "Library/Frameworks",
			"Local/Library/Frameworks", "" };

	private File _warProjectDir;

	private File _buildAna;

	private File _xdocsDir;

	private File _distDir;

	private File _buildTempDir;

	private File _buildDistDir;

	private File _buildWarDir;

	private File _buildWSDir;

	private File _defaultPropertiesFile;

	private boolean checkoutLibraries = true;

	private String zipped_api_file_path;

	private String zipped_api_framework_path;

	private String flexo_embedded_framework_path;

	private AntRunner runner;

	private Vector<BuildListener> listeners;

	public FlexoWarBuilder(FlexoProject project, File buildAna,
			File distDirectory, boolean isProto) {
		super();
		FileResource apiZippedFile = new FileResource(isProto?
				"Config/Generator/Libraries/api-proto.zip":"Config/Generator/Libraries/api-app.zip");
		try {
			if (apiZippedFile != null && apiZippedFile.exists()) {
				checkoutLibraries = false;
				zipped_api_file_path = apiZippedFile.getCanonicalPath();
				FileResource apiFmkFile = new FileResource(isProto?
						"Config/Generator/Libraries/framework-proto.zip":"Config/Generator/Libraries/framework-app.zip");
				zipped_api_framework_path = apiFmkFile.getCanonicalPath();
			}
		} catch (IOException e) {
			e.printStackTrace();
			checkoutLibraries = true;
		} catch (SecurityException e) {
			e.printStackTrace();
			checkoutLibraries = true;
		}
		flexo_embedded_framework_path = project.getFrameworksToEmbedDirectory()
				.getAbsolutePath();
		// checkoutLibraries = true;
		_buildAna = buildAna;
		_distDir = distDirectory;
		runner = new AntRunner(this);
		listeners = new Vector<BuildListener>();
		if (logger.isLoggable(Level.FINE))
			logger.fine("################### checkout libraries = "
				+ checkoutLibraries + " ##################");
	}

	private String getWarProjectName() {
		return "template-project";
	}

	protected File getWarProjectDir(File outputDirectory) throws IOException {
		if (_warProjectDir == null)
			_warProjectDir = new File(outputDirectory, getWarProjectName());
		if (!_warProjectDir.exists())
			_warProjectDir.mkdirs();
		return _warProjectDir;
	}

	private File getXDocsDir(File outputDir) throws Exception {
		if (_xdocsDir == null)
			_xdocsDir = new File(getWarProjectDir(outputDir), "xdocs");
		if (!_xdocsDir.exists())
			checkoutProjectTemplate(outputDir);
		System.err.println("_xdocsDir:" + _xdocsDir.getAbsolutePath());
		return _xdocsDir;
	}

	private File getBuildTempDir(File outputDir) throws IOException {
		if (_buildTempDir == null)
			_buildTempDir = new File(getWarProjectDir(outputDir), "temp");
		return _buildTempDir;
	}

	private File getBuildDistDir(File outputDir) throws IOException {
		if (_buildDistDir == null)
			_buildDistDir = new File(getBuildTempDir(outputDir), "dist");
		return _buildDistDir;
	}

	private File getBuildWarDirectory(File outputDir) throws IOException {
		if (_buildWarDir == null)
			_buildWarDir = new File(getBuildDistDir(outputDir), "war");
		return _buildWarDir;
	}

	private File getBuildWSDirectory(File outputDir) throws IOException {
		if (_buildWSDir == null)
			_buildWSDir = new File(getBuildTempDir(outputDir), "ws");
		return _buildWSDir;
	}

	private void cleanWarProjectDir(final File outputDir,boolean cleanImmediately) throws IOException {
		_xdocsDir = null;
		_buildAna = null;
		_xdocsDir = null;
		_distDir = null;
		_buildTempDir = null;
		_buildDistDir = null;
		_buildWarDir = null;
		_defaultPropertiesFile = null;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (logger.isLoggable(Level.INFO))
						logger.info("Deleting "+outputDir.getAbsolutePath());
					FileUtils.recursiveDeleteFile(outputDir);
					if (logger.isLoggable(Level.INFO))
						logger.info("Done deleting "+outputDir.getAbsolutePath());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
				}
			}
		});
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
		if (cleanImmediately)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	private void copyBuildAna(File outputDir) throws Exception {
		File destFile = new File(getXDocsDir(outputDir), "build.ana");
		if (!destFile.exists())
			destFile.createNewFile();
		FileUtils.copyFileToFile(_buildAna, destFile);
	}

//	private void moveAntUserFramework(File generatedCodeDirectory) throws IOException {
//		File destDirectory = new File(generatedCodeDirectory, "woproject");
//		File antFrameworkFile = new File(generatedCodeDirectory, "ant.frameworks.user.home.manual");
//		File destFile = new File(destDirectory,antFrameworkFile.getName());
//		if (destFile.exists())
//			return;
//		FileUtils.copyFileToDir(antFrameworkFile, destDirectory);
//	}

	public void makeWar(File outputDir, File generatedCodeDirectory,boolean cleanImmediately) throws Exception {
		createWoBuildProperties();
		copyBuildAna(outputDir);
		//moveAntUserFramework(generatedCodeDirectory);
		modifyDefaultProperties(outputDir);
		runBuildScriptGenerator(outputDir);
		runBuildScript(outputDir);
		notifyProgress("Copying WAR to: "+outputDir.getAbsolutePath());
		if (logger.isLoggable(Level.INFO))
			logger.info("Copying WAR to: "+outputDir.getAbsolutePath());
		copyWarToDist(outputDir);
		//copyWebResourcesToDist(outputDir);
		notifyProgress("Clean temporary files");
		cleanWarProjectDir(outputDir,cleanImmediately);
	}

	/**
	 *
	 */
	private void createWoBuildProperties() {
		File userHome = new File(System.getProperty("user.home"));
		File f = new File(userHome, "Library/" + WO_BUILD_PROPERTIES);
		try {
			if (!f.exists()) {
				if (!f.getParentFile().exists())
					f.getParentFile().mkdirs();
				f.createNewFile();
				Properties p = new Properties();
				for (int i = 0; i < ABSOLUTE_KEYS.length; i++) {
					String s = ABSOLUTE_KEYS[i];
					try {
						p.put(s, new File(DEFAULT_ABSOLUTE_PATH[i])
								.getCanonicalPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				File WO_DIR = null;
				if (System.getenv("NEXT_ROOT") != null)
					WO_DIR = new File(System.getenv("NEXT_ROOT"));
				if (WO_DIR == null) {
					if ((ToolBox.getPLATFORM() == ToolBox.WINDOWS)) {
						File test = new File("c:\\System");
						if (test.exists())
							WO_DIR = test;
						else {
							WO_DIR = new File("c:\\Apple");
						}
					} else {
						WO_DIR = new File("/System");
					}
				}
				for (int i = 0; i < WO_DIR_KEYS.length; i++) {
					String s = WO_DIR_KEYS[i];
					try {
						p.put(s, new File(WO_DIR,
								DEFAULT_WO_DIR_RELATIVE_PATH[i])
								.getCanonicalPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				for (int i = 0; i < USER_HOME_KEYS.length; i++) {
					String s = USER_HOME_KEYS[i];
					try {
						p.put(s, new File(userHome,
								DEFAULT_USER_HOME_RELATIVE_PATH[i])
								.getCanonicalPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					p.store(new FileOutputStream(f),
							"This file stores the wo specific properties");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkoutProjectTemplate(File outputDir) throws Exception {
		notifyProgress("Connect to source repository");
		outputDir.mkdirs();
		if (checkoutLibraries) {
			File buildxmlfile = new FileResource("Resources/build.xml");
			File buildxmldir = buildxmlfile.getParentFile();
			AntRunner.initAndRunTask(runner, buildxmlfile.getCanonicalPath(), buildxmldir
					.getCanonicalPath(), null, null,listeners);
		} else {
			ZipUtils.unzip(new FileResource(ZIPPED_TEMPLATE_PROJECT_PATH),
					outputDir);
		}
		File libraries = new File(getWarProjectDir(outputDir), "libraries");
		if (!libraries.exists())
			libraries.mkdirs();

	}

	private void modifyDefaultProperties(File outputDir)
			throws FileNotFoundException, IOException {
		Properties defaultProperties = new Properties();
		defaultProperties.load(new FileInputStream(
				getDefaultPropertiesFile(outputDir)));
		defaultProperties.put("project.dir", getWarProjectName());
		if (!checkoutLibraries) {
			defaultProperties.put("zipped_api_file", zipped_api_file_path);
			defaultProperties.put("zipped_framework_file",
					zipped_api_framework_path);
			defaultProperties.put("no_versionning", "true");
			defaultProperties.remove("database");
		}
		// flexo_framework_dir
		if (flexo_embedded_framework_path != null) {
			if (logger.isLoggable(Level.INFO))
				logger.info("Frameworks path: " + flexo_embedded_framework_path);
			defaultProperties.put("flexo_framework_dir",
					flexo_embedded_framework_path);
		}
		defaultProperties.store(new FileOutputStream(
				getDefaultPropertiesFile(outputDir)), null);
	}

	private File getDefaultPropertiesFile(File outputDir) throws IOException {
		if (_defaultPropertiesFile == null)
			_defaultPropertiesFile = new File(getWarProjectDir(outputDir),
					"default.properties");
		return _defaultPropertiesFile;
	}

	private void runBuildScriptGenerator(File outputDir) throws Exception {
		File buildxmlfile = new File(getWarProjectDir(outputDir), "build.xml");
		File buildxmldir = getWarProjectDir(outputDir);
		notifyProgress("Generate build scripts");
		AntRunner.initAndRunTask(runner,buildxmlfile.getCanonicalPath(), buildxmldir
				.getCanonicalPath(), null, "generate",listeners);

	}

	private void runBuildScript(File outputDir) throws Exception {
		File buildxmlfile = new File(getWarProjectDir(outputDir), "build.xml");
		File buildxmldir = getWarProjectDir(outputDir);
		if (checkoutLibraries) {
			notifyProgress("Checkout api's");
			AntRunner.initAndRunTask(runner,buildxmlfile.getCanonicalPath(), buildxmldir
					.getCanonicalPath(), null, "checkout.api",listeners);
			notifyProgress("Checkout frameworks");
			AntRunner.initAndRunTask(runner,buildxmlfile.getCanonicalPath(), buildxmldir
					.getCanonicalPath(), null, "checkout.framework",listeners);
			notifyProgress("Clean install");
			AntRunner.initAndRunTask(runner,buildxmlfile.getCanonicalPath(), buildxmldir
					.getCanonicalPath(), null, "clean",listeners);
		}
		notifyProgress("Build war");
		// flexo_framework_dir
		AntRunner.initAndRunTask(runner,buildxmlfile.getCanonicalPath(), buildxmldir
				.getCanonicalPath(), null, checkoutLibraries ? "dist"
				: "flexo_fast",listeners);

	}

	private void copyWarToDist(File outputDir) throws IOException {
		FileUtils.copyContentDirToDir(getBuildWarDirectory(outputDir), _distDir);
	}

	private void copyWebResourcesToDist(File outputDir) throws IOException {
		FileUtils.copyContentDirToDir(getBuildWSDirectory(outputDir), _distDir);
	}

	public void notifyProgress(String msg) {
		setChanged();
		notifyObservers(new GenerationProgressNotification(msg));
	}

	@Override
	public void notifyObservers(DataModification arg) {
		super.setChanged();
		super.notifyObservers(arg);
	}

    /**
     * @param buildListeners
     */
    public void addBuildListeners(Vector<BuildListener> buildListeners)
    {
        listeners.addAll(buildListeners);
    }

}