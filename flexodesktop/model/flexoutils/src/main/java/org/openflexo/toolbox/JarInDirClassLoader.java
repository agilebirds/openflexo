/*
 * (c) Copyright 2013 Openflexo
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipException;

import org.apache.commons.io.FileUtils;
/**
 * 
 * This class Loader looks for classes and resources in a set of jars found in a set of directories
 * Any Jar in the directory List ist 
 * 
 * Directories can anly be added to the list and not removed.
 * 
 * @author sylvain,xtof
 *
 */

public class JarInDirClassLoader extends URLClassLoader {


	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(JarInDirClassLoader.class.getPackage()
			.getName());



	private List<File> jarDirectories;


	public JarInDirClassLoader(List<File> jarDirectories) {
		super(new URL[]{}, JarInDirClassLoader.class.getClassLoader());
		this.jarDirectories = jarDirectories;
		for (File jarDir : jarDirectories){
			addFilesFromDirectory(jarDir);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		logger.info("Finalizing Jar Class Loader");
	}
	
	/**
	 * adds a Directory in the list of directories to search for
	 * 
	 * @param directory
	 */

	public void addJarDirectory(File directory){
		if (directory != null && directory.isDirectory()){
			this.jarDirectories.add(directory);
			addFilesFromDirectory(directory);
		}
	}


	/**
	 * 
	 * looks for files in directory and add them to the list of files to be used to resolve class and resources
	 * 
	 * @param jarDir
	 */
	private void addFilesFromDirectory(File jarDir){
		if (jarDir != null && jarDir.isDirectory()){
			Collection<File> lstFile = FileUtils.listFiles(jarDir, new String[] { "jar" }, false);
			for (File f : lstFile){
				try {
					this.addURL(f.toURI().toURL());
				} catch (MalformedURLException e) {
					logger.severe("JarInDirClassLoader: cannot load file from Malformed URI: " + jarDir.toString());
				}
			}
		}
	}


}
