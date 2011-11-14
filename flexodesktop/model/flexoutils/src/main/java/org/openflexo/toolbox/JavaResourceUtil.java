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
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Nicolas Daniels
 * 
 */
public class JavaResourceUtil {

	private static final Logger logger = Logger.getLogger(JavaResourceUtil.class.getPackage().getName());

	/**
	 * Retrieve all resources with the specified suffix in the resources where the specified class belongs (from jar or folder).
	 * 
	 * @param clazz
	 *            : the class belonging to the jar or to the folder denoting a project
	 * @param suffix
	 *            : the suffix resources must match. If null all resources will be retrieved
	 * @return the retrieved resource names.
	 */
	public static List<String> getMatchingResources(Class<?> clazz, String suffix) {

		try {
			URI uri = clazz.getProtectionDomain().getCodeSource().getLocation().toURI();
			File file = new File(uri);
			if (file.isDirectory()) {
				return getMatchingResourcesFromFolder(new File(uri), suffix);
			} else if (file.getName().endsWith(".jar")) {
				JarFile jarFile = new JarFile(new File(uri));

				try {
					return getMatchingResourcesFromJar(jarFile, suffix);
				} finally {
					try {
						if (jarFile != null) {
							jarFile.close();
						}
					} catch (IOException e) {
						logger.log(Level.WARNING, "Cannot close jar file '" + jarFile.getName() + "' !", e);
					}
				}
			} else {
				logger.log(Level.WARNING, "Cannot handle file type for loading resources !");
			}

		} catch (URISyntaxException e) {
			logger.log(Level.SEVERE, "Cannot load files from jar !", e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Cannot load files from jar !", e);
		}

		return new ArrayList<String>();
	}

	/**
	 * Retrieve all resources with the specified suffix in the jar.
	 * 
	 * @param jarFile
	 *            : the jar in which search for resources must be performed
	 * @param suffix
	 *            : the suffix resources must match. If null all resources will be retrieved
	 * @return the retrieved resource names.
	 */
	public static List<String> getMatchingResourcesFromJar(JarFile jarFile, String suffix) {
		List<String> result = new ArrayList<String>();
		Enumeration<JarEntry> entries = jarFile.entries(); // gives ALL entries in jar
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (suffix == null || entry.getName().endsWith(suffix)) {
				result.add("/" + entry.getName());
			}
		}

		return result;
	}

	/**
	 * Retrieve all resources with the specified suffix in the project folder.
	 * 
	 * @param folder
	 *            : the folder in which search for resources must be performed
	 * @param suffix
	 *            : the suffix resources must match. If null all resources will be retrieved
	 * @return the retrieved resource names.
	 */
	public static List<String> getMatchingResourcesFromFolder(File folder, final String suffix) {
		List<File> files = FileUtils.listFilesRecursively(folder, new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(suffix);
			}
		});

		List<String> result = new ArrayList<String>();
		for (File file : files) {
			result.add(file.getAbsolutePath().substring(folder.getAbsolutePath().length()).replace('\\', '/'));
		}

		return result;
	}

	/**
	 * Return the last modified date of the specified resource. <br>
	 * If the resource doesn't exist, null is returned.
	 * 
	 * @param resourcePath
	 * @return the last modified date of the specified resource, null if resource doesn't exist.
	 */
	public static Date getResourceLastModifiedDate(String resourcePath) {
		try {
			URL url = JavaResourceUtil.class.getResource(resourcePath);

			if (url == null) {
				return null;
			}

			return new Date(url.openConnection().getLastModified());
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Cannot retrieve last modified date for resources '" + resourcePath + "' !");
			e.printStackTrace();
			return null;
		}
	}
}
