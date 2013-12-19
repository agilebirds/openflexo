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
package org.openflexo.toolbox;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author bmangez
 * 
 *         <B>Class Description</B>
 */
public class ResourceLocator {

	private static final Logger logger = Logger.getLogger(ResourceLocator.class.getPackage().getName());

	static File locateFile(String relativePathName) {
		File locateFile = locateFile(relativePathName, false);
		if (locateFile != null && locateFile.exists()) {
			return locateFile;
		}
		return locateFile(relativePathName, true);
	}

	static File locateFile(String relativePathName, boolean lenient) {
		// logger.info("locateFile: "+relativePathName);
		for (Enumeration<File> e = getDirectoriesSearchOrder().elements(); e.hasMoreElements();) {
			File nextTry = new File(e.nextElement(), relativePathName);
			if (nextTry.exists()) {
				if (logger.isLoggable(Level.FINER)) {
					logger.finer("Found " + nextTry.getAbsolutePath());
				}
				try {
					if (nextTry.getCanonicalFile().getName().equals(nextTry.getName()) || lenient) {
						return nextTry;
					}
				} catch (IOException e1) {
				}
			} else {
				if (logger.isLoggable(Level.FINER)) {
					logger.finer("Searched for a " + nextTry.getAbsolutePath());
				}
			}
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not locate resource " + relativePathName);
		}
		return new File(userDirectory, relativePathName);
	}

	static String retrieveRelativePath(FileResource fileResource) {
		for (Enumeration<File> e = getDirectoriesSearchOrder().elements(); e.hasMoreElements();) {
			File f = e.nextElement();
			if (fileResource.getAbsolutePath().startsWith(f.getAbsolutePath())) {
				return fileResource.getAbsolutePath().substring(f.getAbsolutePath().length() + 1).replace('\\', '/');
			}
		}
		if (fileResource.getAbsolutePath().startsWith(userDirectory.getAbsolutePath())) {
			return fileResource.getAbsolutePath().substring(userDirectory.getAbsolutePath().length() + 1).replace('\\', '/');
		}
		if (logger.isLoggable(Level.SEVERE)) {
			logger.severe("File resource cannot be found: " + fileResource.getAbsolutePath());
		}
		return null;
	}

	public static String cleanPath(String relativePathName) {
		try {
			return locateFile(relativePathName).getCanonicalPath();
		} catch (IOException e) {
			return locateFile(relativePathName).getAbsolutePath();
		}
		// return cleanAbsolutePath(dirtyPath);
	}

	private static Vector<File> directoriesSearchOrder = null;

	private static File preferredResourcePath;

	private static File userDirectory = null;

	private static File userHomeDirectory = null;

	public static File getPreferredResourcePath() {
		return preferredResourcePath;
	}

	public static void resetFlexoResourceLocation(File newLocation) {
		preferredResourcePath = newLocation;
		directoriesSearchOrder = null;
	}

	public static void printDirectoriesSearchOrder(PrintStream out) {
		out.println("Directories search order is:");
		for (File file : getDirectoriesSearchOrder()) {
			out.println(file.getAbsolutePath());
		}
	}

	public static void init() {
		getDirectoriesSearchOrder();
	}

	public static void addProjectDirectory(File projectDirectory) {
		init();
		if (projectDirectory.exists()) {
			addProjectResourceDirs(directoriesSearchOrder, projectDirectory);
		}
	}

	private static Vector<File> getDirectoriesSearchOrder() {
		if (directoriesSearchOrder == null) {
			synchronized (ResourceLocator.class) {
				if (directoriesSearchOrder == null) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Initializing directories search order");
					}
					directoriesSearchOrder = new Vector<File>();
					if (preferredResourcePath != null) {
						if (logger.isLoggable(Level.INFO)) {
							logger.info("Adding directory " + preferredResourcePath.getAbsolutePath());
						}
						directoriesSearchOrder.add(preferredResourcePath);
					}
					File workingDirectory = new File(System.getProperty("user.dir"));
					File flexoDesktopDirectory = findProjectDirectoryWithName(workingDirectory, "openflexo");
					if (flexoDesktopDirectory != null) {
						findAllFlexoProjects(flexoDesktopDirectory, directoriesSearchOrder);
						File technologyadaptersintegrationDirectory = new File(flexoDesktopDirectory.getParentFile(),
								"packaging/technologyadaptersintegration");
						if (technologyadaptersintegrationDirectory != null) {
							findAllFlexoProjects(technologyadaptersintegrationDirectory, directoriesSearchOrder);
						}
					}
					directoriesSearchOrder.add(workingDirectory);
				}
			}
		}
		return directoriesSearchOrder;
	}

	public static File findProjectDirectoryWithName(File currentDir, String projectName) {
		if (currentDir != null) {
			File attempt = new File(currentDir, projectName);
			if (attempt.exists()) {
				return attempt;
			} else {
				return findProjectDirectoryWithName(currentDir.getParentFile(), projectName);
			}
		}
		return null;
	}

	public static void findAllFlexoProjects(File dir, List<File> files) {
		if (new File(dir, "pom.xml").exists()) {
			files.add(dir);
			for (File f : dir.listFiles()) {
				if (f.getName().startsWith("flexo") || f.getName().contains("connector")
						|| f.getName().equals("technologyadaptersintegration") || f.getName().startsWith("diana")
						|| f.getName().startsWith("fib") || f.getName().startsWith("agilebirdsconnector") || f.getName().equals("projects")
						|| f.getName().equals("free-modelling-editor")) {
					addProjectResourceDirs(files, f);
				}
				if (f.isDirectory()) {
					findAllFlexoProjects(f, files);
				}
			}
		}
	}

	public static void addProjectResourceDirs(List<File> files, File f) {
		File file1 = new File(f.getAbsolutePath() + "/src/main/resources");
		File file2 = new File(f.getAbsolutePath() + "/src/test/resources");
		File file3 = new File(f.getAbsolutePath() + "/src/dev/resources");
		// File file4 = new File(f.getAbsolutePath());
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Adding directory " + file1.getAbsolutePath());
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Adding directory " + file2.getAbsolutePath());
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Adding directory " + file3.getAbsolutePath());
		}
		/*if (logger.isLoggable(Level.FINE)) {
			logger.fine("Adding directory " + file4.getAbsolutePath());
		}*/
		if (file1.exists()) {
			files.add(file1);
		}
		if (file2.exists()) {
			files.add(file2);
		}
		if (file3.exists()) {
			files.add(file3);
		}
		/*if (file4.exists()) {
			files.add(file4);
		}*/
	}

	public static File getUserDirectory() {
		return userDirectory;
	}

	public static File getUserHomeDirectory() {
		return userHomeDirectory;
	}
}
