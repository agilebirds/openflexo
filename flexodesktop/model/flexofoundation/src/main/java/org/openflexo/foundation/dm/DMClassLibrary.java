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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import com.thoughtworks.qdox.model.ClassLibrary;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaClassParent;
import com.thoughtworks.qdox.model.Type;

public class DMClassLibrary extends ClassLibrary {

	protected static final Logger logger = Logger.getLogger(DMClassLibrary.class.getPackage().getName());

	private Hashtable<String, JavaClass> _javaClassCache;
	private HashSet<Type> _unresolvedTypes;

	public DMClassLibrary(DMModel dataModel) {
		super(null);
		_javaClassCache = new Hashtable<String, JavaClass>();
		addDefaultLoader();
		_cachedTypes = new Hashtable<JavaClassParent, Hashtable<String, Hashtable<Integer, Type>>>();
		_unresolvedTypes = new HashSet<Type>();
		_unresolvedClassName = new HashSet<String>();
	}

	public void clearLibrary() {
		_javaClassCache.clear();
		_cachedTypes.clear();
		_unresolvedTypes.clear();
		_unresolvedClassName.clear();
		_javaClassCache = new Hashtable<String, JavaClass>();
		_cachedTypes = new Hashtable<JavaClassParent, Hashtable<String, Hashtable<Integer, Type>>>();
		_unresolvedTypes = new HashSet<Type>();
		_unresolvedClassName = new HashSet<String>();
	}

	@Override
	public synchronized JavaClass getClassByName(String name) {
		return _javaClassCache.get(name);
	}

	public synchronized void registerClassForName(JavaClass aClass) {
		// logger.info("Registering "+aClass.getFullyQualifiedName());
		_javaClassCache.put(aClass.getFullyQualifiedName(), aClass);
	}

	private Hashtable<JavaClassParent, Hashtable<String, Hashtable<Integer, Type>>> _cachedTypes;

	private JavaClass retrieveCachedContext(JavaClass context) {
		JavaClass returned = _javaClassCache.get(context.getFullyQualifiedName());
		if (returned == null) {
			returned = context;
		}
		return returned;
	}

	public synchronized void clearUnresolvedTypesForNewRegisteredEntity(DMEntity entity) {
		for (Type t : _unresolvedTypes) {
			if (t instanceof DMType) {
				// logger.info("Unresolve "+t+" because "+entity+" newly registered");
				((DMType) t).clearUnresolved();
			}
		}
		if (_unresolvedClassName.contains(entity.getFullQualifiedName())) {
			_unresolvedClassName.remove(_unresolvedClassName);
		}
	}

	public synchronized Type retrieveType(String name, int dimensions, JavaClassParent context) {
		// logger.info("retrieveType "+name);
		if (context instanceof JavaClass) {
			// Try to retrieve cache data
			context = retrieveCachedContext((JavaClass) context);
		}
		Hashtable<String, Hashtable<Integer, Type>> _typesForContext = _cachedTypes.get(context);
		if (_typesForContext != null) {
			Hashtable<Integer, Type> _typesWithName = _typesForContext.get(name);
			if (_typesWithName != null) {
				Type returned = _typesWithName.get(dimensions);
				if (returned != null && returned.isResolved() && _unresolvedTypes.contains(returned)) {
					// logger.info("Found "+returned+" newly resolved !");
					_unresolvedTypes.remove(returned);
				}
				return returned;
			}
		}
		return null;
	}

	public synchronized void registerType(Type typeToStore, String name, int dimensions, JavaClassParent context) {
		// logger.info("registerType "+name);
		if (context instanceof JavaClass) {
			// Try to retrieve cache data
			context = retrieveCachedContext((JavaClass) context);
		}
		Hashtable<String, Hashtable<Integer, Type>> _typesForContext = _cachedTypes.get(context);
		if (_typesForContext == null) {
			_typesForContext = new Hashtable<String, Hashtable<Integer, Type>>();
			_cachedTypes.put(context, _typesForContext);
		}
		Hashtable<Integer, Type> _typesWithName = _typesForContext.get(name);
		if (_typesWithName == null) {
			_typesWithName = new Hashtable<Integer, Type>();
			_typesForContext.put(name, _typesWithName);
		}
		_typesWithName.put(dimensions, typeToStore);
		if (!typeToStore.isResolved()) {
			// logger.info("Add "+typeToStore+" to unresolved types");
			_unresolvedTypes.add(typeToStore);
		}
	}

	private boolean potentiallyModifyingCL = false;
	private Vector<ClassLoader> _classLoaderBeingAddedDuringClassResolution = new Vector<ClassLoader>();

	private HashSet<String> _unresolvedClassName;

	@Override
	public synchronized Class<?> getClass(String className) {
		if (_unresolvedClassName.contains(className)) {
			return null;
		}
		potentiallyModifyingCL = true;
		Class<?> returned = super.getClass(className);
		if (returned == null) {
			for (JarClassLoader cl : loadedJarClassLoaders) {
				// Don't search in all jars since we are already iterating
				returned = cl.findClass(className);
				if (returned != null) {
					break;
				}
			}
		}
		if (returned != null) {
			// logger.info("Found "+returned+" in "+returned.getClassLoader());
		} else {
			_unresolvedClassName.add(className);
		}
		potentiallyModifyingCL = false;
		if (_classLoaderBeingAddedDuringClassResolution.size() > 0) {
			for (ClassLoader cl : _classLoaderBeingAddedDuringClassResolution) {
				addClassLoader(cl);
			}
			_classLoaderBeingAddedDuringClassResolution.clear();
		}
		return returned;
	}

	private transient List<JarClassLoader> loadedJarClassLoaders = new ArrayList<JarClassLoader>();

	@Override
	public synchronized boolean contains(String className) {
		return super.contains(className);
	}

	@Override
	public synchronized void addClassLoader(ClassLoader classLoader) {
		if (potentiallyModifyingCL) {
			_classLoaderBeingAddedDuringClassResolution.add(classLoader);
		} else {
			if (classLoader instanceof JarClassLoader) {
				loadedJarClassLoaders.add((JarClassLoader) classLoader);
			} else {
				super.addClassLoader(classLoader);
			}
			// Some unresolved classes may become resolved !
			_unresolvedClassName.clear();
		}
	}
}
