package org.openflexo.foundation.dm;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class JarClassLoader extends ClassLoader {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(JarClassLoader.class.getPackage()
			.getName());

	private Map<String, Class<?>> classForClassName = new Hashtable<String, Class<?>>();

	JarClassLoader() {
		super();
	}

	public Class<?> findClass(JarFile jarFile, JarEntry entry) {
		String className = entry.getName();
		if (isClassFile(className)) {
			String parsedClassName = parseClassName(className);
			return findClass(parsedClassName, jarFile, entry);
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("This JarEntry does not match a class definition");
		}
		return null;
	}

	public Class<?> findClass(String className, JarFile jarFile, JarEntry entry) {
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
				getClassForClassName().put(className, returned);
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

	@Override
	public Class<?> findClass(String className) {
		className = parseClassName(className);
		Class<?> tryToLookup = lookupClass(className);
		if (tryToLookup != null) {
			return tryToLookup;
		} else {
			Class<?> klass = getClassForClassName().get(className);
			if (klass != null) {
				return klass;
			}
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
		if (getClassForClassName().get(className) != null) {
			return getClassForClassName().get(className);
		}

		// Nowhere, sorry...
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
}
