package org.openflexo.foundation.dm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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

public class JarClassLoader extends ClassLoader {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(JarClassLoader.class.getPackage()
			.getName());

	private Map<String, Class<?>> classForClassName;

	private List<File> jarDirectories;

	private Set<JarFile> jarFiles;

	private Set<File> bannedZip;

	public JarClassLoader(List<File> jarDirectories) {
		super();
		this.jarDirectories = jarDirectories;
		jarFiles = new HashSet<JarFile>();
		classForClassName = Collections.synchronizedMap(new HashMap<String, Class<?>>());
	}

	@Override
	protected void finalize() throws Throwable {
		System.err.println("Finalizing Jar Class Loader");
	}

	private boolean isClassFile(String jarentryname) {
		return !jarentryname.startsWith("WebServerResources/");
	}

	private String parseClassName(String jarentryname) {
		String classname;
		int index = jarentryname.lastIndexOf(".class");
		if (index - 1 > 0) {
			classname = jarentryname.substring(0, index);
		} else {
			classname = jarentryname;
		}
		classname = classname.replace('/', '.');
		return classname;
	}

	@Override
	public Class<?> findClass(String className) {
		className = parseClassName(className);
		if (!isClassFile(className)) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Class " + className + " is not a valid class");
			}
			return null;
		}
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
		Class<?> tryToLookup = lookupClass(className, null, null);
		if (tryToLookup != null) {
			return tryToLookup;
		}
		return null;
	}

	private Class<?> lookupClass(String className, JarFile jarFile, JarEntry entry) {
		return lookupClass(className, jarFile, entry, false);
	}

	private Class<?> lookupClass(String className, JarFile jarFile, JarEntry entry, boolean useClassForNameOnly) {
		boolean addedJarToList = jarFile != null && jarFiles.add(jarFile);
		try {
		// Put the class name in right format
		className = parseClassName(className);
		// Look in this class loader
		Class<?> class1 = getClassForClassName().get(className);
		if (class1 != null) {
			return class1;
		}
		// Look in the application class loader
		try {
			Class<?> forName = Class.forName(className);
			if (forName != null) {
				classForClassName.put(className, forName);
			}
			return forName;
		} catch (ClassNotFoundException e) {
		} catch (NoClassDefFoundError e) {
		} catch (ExceptionInInitializerError e) {
		} catch (LinkageError e) {
		}
		if (useClassForNameOnly) {
			return null;
		}

		if (jarFile != null && entry != null) {
			return loadClass(jarFile, entry, className);
		}
		class1 = findInJars(className);
		if (class1 != null) {
			return class1;
		}
		if (className.indexOf('.') == -1) {
			class1 = lookupClass("java.lang." + className, null, null, true);
		}
		classForClassName.put(className, class1);
		if (class1 == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Looked everywhere but I could not find " + className);
			}
		}
		return null;
		} finally {
			if (addedJarToList) {
				jarFiles.remove(jarFile);
	}
		}
	}

	private Class<?> findInJars(String className) {
		String jarEntryName = className.replace('.', '/') + ".class";
		for (JarFile jarFile : this.jarFiles) {
			Class<?> klass = lookupClassInJarFile(jarFile, jarEntryName, className);
			if (klass != null) {
				return klass;
			}
		}
		List<File> jarFiles = new ArrayList<File>();
		for (File jarDir : jarDirectories) {
			jarFiles.addAll(FileUtils.listFiles(jarDir, new String[] { "jar" }, false));
		}
		if (bannedZip != null) {
			jarFiles.removeAll(bannedZip);
		}
		for (File file : jarFiles) {
			try {
				JarFile jarFile = new JarFile(file);
				Class<?> klass = lookupClassInJarFile(jarFile, jarEntryName, className);
				if (klass != null) {
					return klass;
				}
			} catch (IOException e) {
				e.printStackTrace();
				if (e instanceof ZipException) {
					if (bannedZip == null) {
						bannedZip = new HashSet<File>();
					}
					bannedZip.add(file);
				}
			}
		}
		return null;
	}

	public Class<?> lookupClassInJarFile(JarFile jarFile, String jarEntryName, String className) {
				JarEntry e = jarFile.getJarEntry(jarEntryName);
				if (e != null) {
					Class<?> klass = loadClass(jarFile, e, className);
					if (klass != null) {
						return klass;
					}
				}
		return null;
	}

	private Class<?> loadClass(JarFile jarFile, JarEntry entry, String className) {
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
		} catch (LinkageError err) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Error: " + err + " : class not loaded: " + className);
			}
		}
		return null;
	}

	public Map<String, Class<?>> getClassForClassName() {
		return new HashMap<String, Class<?>>(classForClassName);
	}

	public void unloadClasses(List<String> classNames) {
		for (String className : classNames) {
			classForClassName.remove(className);
		}
	}

	public void unloadClass(Class<?> klass) {
		Class<?> class1 = classForClassName.get(klass.getName());
		if (class1 != null) {
			if (class1 == klass) {
				classForClassName.remove(klass.getName());
			} else {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Class " + klass.getName() + " has been loaded twice in project.");
				}
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not find class " + klass.getName());
			}
		}
	}

	public Class<?> findClass(JarFile jarFile, JarEntry entry) {
		return lookupClass(parseClassName(entry.getName()), jarFile, entry);
	}
}
