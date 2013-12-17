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
package org.openflexo.foundation.dm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.rm.FlexoJarResource;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.ImportedResourceData;
import org.openflexo.foundation.rm.RMNotification;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.localization.FlexoLocalization;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class JarLoader implements ImportedResourceData {

	static final Logger logger = Logger.getLogger(JarLoader.class.getPackage().getName());

	JarFile jarFile;

	private Manifest manifest;
	Set<String> classNames = new HashSet<String>();
	private JarClassLoader classLoader;
	private FlexoJarResource _jarResource;

	private final FlexoProject project;

	ExternalRepository getJarRepository() {
		if (_jarResource != null) {
			return _jarResource.getJarRepository();
		}
		return null;
	}

	public JarLoader(File aJarFile, FlexoJarResource jarResource, FlexoProject project) {
		this(aJarFile, jarResource != null ? jarResource.getJarRepository() : null, jarResource, project, null);
	}

	public JarLoader(File aJarFile, ExternalRepository jarRepository, FlexoProject project) {
		this(aJarFile, jarRepository, jarRepository != null ? jarRepository.getJarResource() : null, project, null);
	}

	public JarLoader(File aJarFile, FlexoJarResource jarResource, FlexoProject project, FlexoProgress progress) {
		this(aJarFile, jarResource != null ? jarResource.getJarRepository() : null, jarResource, project, progress);
	}

	public JarLoader(File aJarFile, ExternalRepository jarRepository, FlexoProject project, FlexoProgress progress) {
		this(aJarFile, jarRepository, jarRepository != null ? jarRepository.getJarResource() : null, project, progress);
	}

	private JarLoader(File aJarFile, ExternalRepository jarRepository, FlexoJarResource jarResource, FlexoProject project,
			FlexoProgress progress) {
		super();
		_jarResource = jarResource;
		this.project = project;
		classLoader = getProject().getJarClassLoader();
		try {
			jarFile = new JarFile(aJarFile);
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Loading jar file " + aJarFile.getAbsolutePath());
			}
			manifest = jarFile.getManifest();
			loadJarFile(progress);
		} catch (IOException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not open file " + aJarFile.getAbsolutePath());
			}
		}
	}

	public void delete() {
		if (classLoader != null) {
			// Actually I don't think this is needed as the deletion of the LoadableDMEntities should already do that
			// But let's just be sure.
			classLoader.unloadClasses(new ArrayList<String>(classNames));
		}
		classNames = null;
		_jarResource = null;
		classLoader = null;
		jarFile = null;
		manifest = null;
	}

	public boolean contains(String className) {
		return getContainedClasses().containsKey(className);
	}

	public Map<String, Class<?>> getContainedClasses() {
		return Maps.filterKeys(classLoader.getClassForClassName(), new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return classNames.contains(input);
			}
		});
	}

	public Class<?> getClassForName(String aName) {
		return getContainedClasses().get(aName);
	}

	private void loadJarFile(FlexoProgress progress) {
		logger.info("**************** load JAR file " + jarFile.getName());
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Manifest: " + manifest + "\n" + manifest.getEntries());
		}
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("loading_class_definitions"));
			progress.resetSecondaryProgress(jarFile.size());
		}
		List<JarEntry> jarEntries = new ArrayList<JarEntry>();
		for (Enumeration<JarEntry> en = jarFile.entries(); en.hasMoreElements();) {
			JarEntry entry = en.nextElement();
			if (progress != null) {
				progress.setSecondaryProgress(FlexoLocalization.localizedForKey("loading_and_parsing") + " " + entry.getName());
			}
			if (isClassFile(entry.getName())) {
				jarEntries.add(entry);
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Entry " + entry.getName());
				}
			}
		}
		int i = 0;
		for (JarEntry entry : jarEntries) {
			Class<?> loadedClass = classLoader.findClass(jarFile, entry);
			if (loadedClass != null) {
				classNames.add(loadedClass.getName());
				i++;
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Loaded " + loadedClass.getName());
				}
			}
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Loaded " + i + " classes.");
		}

	}

	boolean isClassFile(String jarentryname) {
		return jarentryname.endsWith(".class") && !jarentryname.startsWith("WebServerResources/");
	}

	public FlexoJarResource getJarResource() {
		return _jarResource;
	}

	@Override
	public FlexoJarResource getFlexoResource() {
		return getJarResource();
	}

	@Override
	public void setFlexoResource(FlexoResource resource) {
		_jarResource = (FlexoJarResource) resource;
	}

	@Override
	public FlexoProject getProject() {
		return project;
	}

	// Degager tout ce qu'il y a en dessous

	public void save() throws SaveResourceException {
		// TODO Auto-generated method stub

	}

	public void setIsModified() {
		// TODO Auto-generated method stub

	}

	public void clearIsModified() {
		// TODO Auto-generated method stub

	}

	public boolean isModified() {
		// TODO Auto-generated method stub
		return false;
	}

	public Date lastMemoryUpdate() {
		// TODO Auto-generated method stub
		return null;
	}

	public void notifyRM(RMNotification notification) throws FlexoException {
		// TODO Auto-generated method stub

	}

	public void receiveRMNotification(RMNotification notification) throws FlexoException {
		// TODO Auto-generated method stub

	}

	/*public ExternalRepository getJarRepository() {
		return _jarRepository;
	}*/

}
