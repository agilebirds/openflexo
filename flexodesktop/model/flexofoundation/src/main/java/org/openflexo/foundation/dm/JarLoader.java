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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.rm.FlexoJarResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.ImportedResourceData;
import org.openflexo.foundation.rm.RMNotification;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.localization.FlexoLocalization;

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
	Hashtable<String, JarEntry> jarEntriesForClassName = new Hashtable<String, JarEntry>();
	private JarClassLoader classLoader;
	private Hashtable<String, Class<?>> classes;
	private ExternalRepository _jarRepository = null;
	FlexoProject _project;
	private FlexoJarResource _jarResource;

	ExternalRepository getJarRepository() {
		if (_jarResource != null) {
			return _jarResource.getJarRepository();
		}
		return _jarRepository;
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
		_jarRepository = jarRepository;
		_jarResource = jarResource;
		classLoader = new JarClassLoader();
		if (getJarRepository() != null) {
			getJarRepository().getDMModel().getClassLibrary().addClassLoader(classLoader);
		}
		classes = new Hashtable<String, Class<?>>();
		_project = project;
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

	@Override
	protected void finalize() throws Throwable {
		System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!JarLoader finalized");
	}

	public Hashtable<String, Class<?>> getContainedClasses() {
		return classes;
	}

	public Class<?> getClassForName(String aName) {
		return classes.get(aName);
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
		for (Enumeration<JarEntry> en = jarFile.entries(); en.hasMoreElements();) {
			JarEntry entry = en.nextElement();
			if (progress != null) {
				progress.setSecondaryProgress(FlexoLocalization.localizedForKey("loading_and_parsing") + " " + entry.getName());
			}
			if (isClassFile(entry.getName())) {
				String className = parseClassName(entry.getName());
				jarEntriesForClassName.put(className, entry);
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Entry " + entry.getName());
				}
			}
		}
		int i = 0;
		for (Enumeration<JarEntry> en = jarEntriesForClassName.elements(); en.hasMoreElements();) {
			JarEntry entry = en.nextElement();
			// logger.info("Entry: "+entry.getName()+" / "+entry);
			Class<?> loadedClass = classLoader.findClass(entry);
			if (loadedClass != null) {
				classes.put(loadedClass.getName(), loadedClass);
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

	public boolean contains(Class aClass) {
		return classes.get(aClass.getName()) != null;
	}

	boolean isClassFile(String jarentryname) {
		return jarentryname.endsWith(".class");
	}

	String parseClassName(String jarentryname) {
		String classname;
		if (jarentryname.indexOf("WebServerResources/Java/") == 0) {
			jarentryname = jarentryname.substring("WebServerResources/Java/".length());
		}
		int index = jarentryname.indexOf("class");
		if (index - 1 > 0) {
			classname = jarentryname.substring(0, index - 1);
		} else {
			classname = jarentryname;
		}
		classname = classname.replace('/', '.');
		return classname;
	}

	public class JarClassLoader extends ClassLoader {

		private Hashtable<String, Class<?>> classForClassName = new Hashtable<String, Class<?>>();

		JarClassLoader() {
			super();
			classForClassName = new Hashtable<String, Class<?>>();
		}

		public Class<?> findClass(JarEntry entry) {
			String className = entry.getName();
			if (isClassFile(className)) {
				String parsedClassName = parseClassName(className);
				return findClass(parsedClassName, entry);
			}
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("This JarEntry does not match a class definition");
			}
			return null;
		}

		public Class<?> findClass(String className, JarEntry entry) {
			className = parseClassName(className);
			if (className != null && className.indexOf("WORPCProvider") > -1) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Skipping load of class " + className
							+ " because it can lead to System.exit(1) if invoked within a WOApplication");
				}
				// We don't try to load this class because it contains a static block that tries to load an additional class which is not
				// automatically on the classpath.
				// If the load of that second class fails, then, if we are within a wo_application, the static block will perform
				// System.exit(1) causing the application to stop
				return null;
			}
			Class<?> tryToLookup = lookupClass(className);
			if (tryToLookup != null) {
				return tryToLookup;
			} else {
				try {
					InputStream is = jarFile.getInputStream(entry);
					BufferedInputStream bis = new BufferedInputStream(is);
					byte[] content = new byte[(int) entry.getSize()];
					bis.read(content, 0, (int) entry.getSize());
					Class<?> returned = defineClass(className, content, 0, content.length);
					classForClassName.put(className, returned);
					return returned;
				} catch (ClassFormatError err) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Error: " + err + " : class not loaded: " + className);
					}
				} catch (NoClassDefFoundError err) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Error: " + err + " : class not loaded: " + className);
					}
				} catch (IllegalAccessError err) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Error: " + err + " : class not loaded: " + className);
					}
				} catch (IOException err) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + err.getClass().getName() + ". See console for details.");
					}
					err.printStackTrace();
				} catch (LinkageError err) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Error: " + err + " : class not loaded: " + className);
					}
				}
			}
			return null;
		}

		@Override
		public Class<?> findClass(String className) {
			return findClass(className, true);
		}

		public Class<?> findClass(String className, boolean searchInAllJarsInProject) {
			className = parseClassName(className);
			Class<?> tryToLookup = lookupClass(className);
			if (tryToLookup != null) {
				return tryToLookup;
			} else {
				if (classForClassName.get(className) != null) {
					return classForClassName.get(className);
				}
				JarEntry foundJar = jarEntriesForClassName.get(className);
				if (foundJar != null) {
					return findClass(className, foundJar);
				} else if (searchInAllJarsInProject) {
					// Search in dependant jars
					if (getJarResource() != null) {
						for (FlexoResource res : getJarResource().getDependantResources()) {
							if (res instanceof FlexoJarResource) {
								JarLoader jarLoader = ((FlexoJarResource) res).getJarLoader();
								Class tryThis = jarLoader.getClassForName(className);
								if (tryThis != null) {
									// logger.info("Found in "+jarLoader+" !!!");
									return tryThis;
								}
							}
						}
					}
					if (getJarRepository() != null && getJarRepository().getJarResource() != null) {
						for (FlexoResource res : getJarRepository().getJarResource().getDependantResources()) {
							if (res instanceof FlexoJarResource) {
								JarLoader jarLoader = ((FlexoJarResource) res).getJarLoader();
								Class tryThis = jarLoader.getClassForName(className);
								if (tryThis != null) {
									// logger.info("Found in "+jarLoader+" !!!");
									return tryThis;
								}
							}
						}
					}

					/*	logger.info("_project="+_project);
					logger.info("_project.getFlexoDMResource()="+_project.getFlexoDMResource());
					logger.info("_project.getDataModel()="+_project.getDataModel());*/

					// Our last chance is now to search in all other known Jars in current project
					// Normally we don't go there until dependancies are broken or a class really not resolvable
					if (_project != null && _project.getFlexoDMResource() != null && _project.getFlexoDMResource().isLoaded()
							&& _project.getDataModel() != null) {
						for (ExternalRepository rep : _project.getDataModel().getExternalRepositories()) {
							if (rep != getJarRepository()) {
								// logger.info("Searching "+className+" in "+rep.getName());
								if (rep.getJarLoader() != null) {
									Class tryThis = rep.getJarLoader().getClassForName(className);
									if (tryThis != null) {
										// logger.info("Found in "+rep.getName()+" !!!");
										// Found a dependancy between resources
										if (getJarRepository() != null && getJarRepository().getJarResource() != null
												&& rep.getJarResource() != null) {
											getJarRepository().getJarResource().addToDependantResources(rep.getJarResource());
										}
										return tryThis;
									}
								}
							}
						}
					}
				}
				/*if (logger.isLoggable(Level.WARNING))
				    logger.warning("Could not find Class for " + className);*/
				return null;
			}
		}

		private Class<?> lookupClass(String className) {
			// Put the class name in right format
			className = parseClassName(className);

			// Look in the application class loader
			try {
				return Class.forName(className);
			} catch (ClassNotFoundException e) {
			} catch (NoClassDefFoundError e) {
			} catch (ExceptionInInitializerError e) {
			} catch (LinkageError e) {
			}

			// Look in this class loader
			if (classForClassName.get(className) != null) {
				return classForClassName.get(className);
			}

			// Nowhere, sorry...
			return null;
		}

		public ExternalRepository getJarRepository() {
			return JarLoader.this.getJarRepository();
		}

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
		if (_jarResource != null) {
			_jarRepository = _jarResource.getJarRepository();
		}
	}

	@Override
	public FlexoProject getProject() {
		return _project;
	}

	@Override
	public void setProject(FlexoProject aProject) {
		_project = aProject;
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
