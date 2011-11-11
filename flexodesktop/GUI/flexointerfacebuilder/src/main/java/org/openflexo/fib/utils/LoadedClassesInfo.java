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
package org.openflexo.fib.utils;

import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Icon;

import org.openflexo.toolbox.ClassScope;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.StringUtils;

public class LoadedClassesInfo implements HasPropertyChangeSupport {

	private static final Logger logger = Logger.getLogger(LoadedClassesInfo.class.getPackage().getName());

	static ClassLoader appLoader = ClassLoader.getSystemClassLoader();
	static ClassLoader currentLoader = LoadedClassesInfo.class.getClassLoader();
	static ClassLoader[] loaders = new ClassLoader[] { appLoader, currentLoader };

	private static LoadedClassesInfo instance;

	static {
		appLoader = ClassLoader.getSystemClassLoader();
		currentLoader = LoadedClassesInfo.class.getClassLoader();
		if (appLoader != currentLoader)
			loaders = new ClassLoader[] { appLoader, currentLoader };
		else
			loaders = new ClassLoader[] { appLoader };
		instance = new LoadedClassesInfo();
	}

	private Hashtable<Package, PackageInfo> packages;
	private Vector<PackageInfo> packageList;
	private boolean needsReordering = true;

	public Vector<ClassInfo> matchingClasses = new Vector<ClassInfo>();

	private Hashtable<String, Vector<ClassInfo>> classesForName;

	private PropertyChangeSupport pcSupport;

	public static LoadedClassesInfo instance() {
		return instance;
	}

	public static LoadedClassesInfo instance(Class aClass) {
		if (aClass != null) {
			ClassInfo ci = instance.registerClass(aClass);
			instance.setFilteredClassName(aClass.getSimpleName());
			// instance.setFilteredPackageName(aClass.getPackage().getName());
			instance.setSelectedClassInfo(ci);
		} else {
			instance.setFilteredPackageName("*");
			instance.setFilteredClassName("");
			instance.setSelectedClassInfo(null);
		}
		return instance;
	}

	private LoadedClassesInfo() {
		pcSupport = new PropertyChangeSupport(this);
		classesForName = new Hashtable<String, Vector<ClassInfo>>();
		packages = new Hashtable<Package, PackageInfo>() {
			@Override
			public synchronized PackageInfo put(Package key, PackageInfo value) {
				PackageInfo returned = super.put(key, value);
				needsReordering = true;
				pcSupport.firePropertyChange("packages", null, null);
				return returned;
			};
		};
		for (Package p : Package.getPackages()) {
			registerPackage(p);
		}
		final Class<?>[] classes = ClassScope.getLoadedClasses(loaders);
		for (Class<?> cls : classes) {
			registerClass(cls);
			String className = cls.getName();
			URL classLocation = ClassScope.getClassLocation(cls);
			// System.out.println("Registered class: " + className + " from " +classLocation);
		}
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	public List<PackageInfo> getPackages() {
		if (needsReordering) {
			packageList = new Vector<PackageInfo>();
			for (Package p : packages.keySet()) {
				packageList.add(packages.get(p));
			}
			Collections.sort(packageList, new Comparator<PackageInfo>() {
				@Override
				public int compare(PackageInfo o1, PackageInfo o2) {
					return Collator.getInstance().compare(o1.packageName, o2.packageName);
				}
			});
			needsReordering = false;
		}
		return packageList;
	}

	private PackageInfo registerPackage(Package p) {
		PackageInfo returned = packages.get(p);
		if (returned == null) {
			packages.put(p, returned = new PackageInfo(p));
		}
		return returned;
	}

	private ClassInfo registerClass(Class c) {
		if (c.getPackage() == null) {
			logger.warning("No package for class " + c);
			return null;
		}

		PackageInfo p = registerPackage(c.getPackage());

		logger.fine("Register class " + c);

		if (!c.isMemberClass() && !c.isAnonymousClass() && !c.isLocalClass()) {
			ClassInfo returned = p.classes.get(c);
			if (returned == null) {
				p.classes.put(c, returned = new ClassInfo(c));
				logger.fine("Store " + returned + " in package " + p.packageName);
			}
			return returned;
		} else if (c.isMemberClass()) {
			// System.out.println("Member class: "+c+" of "+c.getDeclaringClass());
			ClassInfo parentClass = registerClass(c.getEnclosingClass());
			ClassInfo returned = parentClass.declareMember(c);
			return returned;
		} else {
			// System.out.println("Ignored class: "+c);
			return null;
		}

	}

	public class PackageInfo implements HasPropertyChangeSupport {
		public String packageName;
		private Hashtable<Class, ClassInfo> classes = new Hashtable<Class, ClassInfo>() {
			@Override
			public synchronized ClassInfo put(Class key, ClassInfo value) {
				ClassInfo returned = super.put(key, value);
				needsReordering = true;
				pcSupport.firePropertyChange("classes", null, null);
				return returned;
			};
		};
		private Vector<ClassInfo> classesList;
		private boolean needsReordering = true;
		private PropertyChangeSupport pcSupport;

		public PackageInfo(Package aPackage) {
			pcSupport = new PropertyChangeSupport(this);
			packageName = aPackage.getName();
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		public List<ClassInfo> getClasses() {
			if (needsReordering) {
				classesList = new Vector<ClassInfo>();
				for (Class c : classes.keySet()) {
					classesList.add(classes.get(c));
				}
				Collections.sort(classesList, new Comparator<ClassInfo>() {
					@Override
					public int compare(ClassInfo o1, ClassInfo o2) {
						return Collator.getInstance().compare(o1.className, o2.className);
					}
				});
				needsReordering = false;
			}
			return classesList;
		}

		public boolean isFiltered() {
			if (getFilteredPackageName() == null || StringUtils.isEmpty(getFilteredPackageName()))
				return false;
			if (packageName.startsWith(getFilteredPackageName()))
				return false;
			String patternString = getFilteredPackageName();
			if (patternString.startsWith("*"))
				patternString = "." + getFilteredPackageName();
			try {
				Pattern pattern = Pattern.compile(patternString);
				Matcher matcher = pattern.matcher(packageName);
				return !matcher.find();
			} catch (PatternSyntaxException e) {
				logger.warning("PatternSyntaxException: " + patternString);
				return false;
			}
		}

		public Icon getIcon() {
			return FIBIconLibrary.PACKAGE_ICON;
		}

	}

	public class ClassInfo implements HasPropertyChangeSupport {
		private Class clazz;
		public String className;
		public String packageName;
		public String fullQualifiedName;

		private Hashtable<Class, ClassInfo> memberClasses = new Hashtable<Class, ClassInfo>() {
			@Override
			public synchronized ClassInfo put(Class key, ClassInfo value) {
				ClassInfo returned = super.put(key, value);
				needsReordering = true;
				pcSupport.firePropertyChange("memberClasses", null, null);
				return returned;
			};
		};
		private Vector<ClassInfo> memberClassesList;
		private boolean needsReordering = true;
		private PropertyChangeSupport pcSupport;

		public ClassInfo(Class aClass) {
			pcSupport = new PropertyChangeSupport(this);
			Vector<ClassInfo> listOfClassesWithThatName = classesForName.get(aClass.getSimpleName());
			if (listOfClassesWithThatName == null) {
				classesForName.put(aClass.getSimpleName(), listOfClassesWithThatName = new Vector<ClassInfo>());
			}
			listOfClassesWithThatName.add(this);
			className = aClass.getSimpleName();
			packageName = aClass.getPackage().getName();
			fullQualifiedName = aClass.getName();
			clazz = aClass;
			logger.fine("Instanciate new ClassInfo for " + aClass);
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		private ClassInfo declareMember(Class c) {
			ClassInfo returned = memberClasses.get(c);
			if (returned == null) {
				memberClasses.put(c, returned = new ClassInfo(c));
				needsReordering = true;
				logger.fine(toString() + ": declare member: " + returned);
			}
			return returned;
		}

		public List<ClassInfo> getMemberClasses() {
			if (needsReordering) {
				memberClassesList = new Vector<ClassInfo>();
				for (Class c : memberClasses.keySet()) {
					memberClassesList.add(memberClasses.get(c));
				}
				Collections.sort(memberClassesList, new Comparator<ClassInfo>() {
					@Override
					public int compare(ClassInfo o1, ClassInfo o2) {
						return Collator.getInstance().compare(o1.className, o2.className);
					}
				});
				needsReordering = false;
			}
			return memberClassesList;
		}

		public Class getRepresentedClass() {
			return clazz;
		}

		@Override
		public String toString() {
			return "ClassInfo[" + clazz.getName() + "]";
		}

		public Icon getIcon() {
			if (clazz.isEnum())
				return FIBIconLibrary.ENUM_ICON;
			if (clazz.isInterface())
				return FIBIconLibrary.INTERFACE_ICON;
			return FIBIconLibrary.CLASS_ICON;
		}

	}

	private String filteredPackageName = "*";
	private String filteredClassName = "";

	public String getFilteredPackageName() {
		return filteredPackageName;
	}

	public void setFilteredPackageName(String filter) {
		if (filter == null || !filter.equals(this.filteredPackageName)) {
			this.filteredPackageName = filter;
			updateMatchingClasses();
		}
	}

	public String getFilteredClassName() {
		return filteredClassName;
	}

	public void setFilteredClassName(String filteredClassName) {
		if (filteredClassName == null || !filteredClassName.equals(this.filteredClassName)) {
			this.filteredClassName = filteredClassName;
			/*Vector<Class> foundClasses = new Vector<Class>();
			try {
				Class foundClass = Class.forName(getFilteredPackageName()+"."+filteredClassName);
				foundClasses.add(foundClass);
				logger.info("Found class "+foundClass);
			} catch (ClassNotFoundException e) {
			}
			for (Package p : packages.keySet()) {
				try {
					Class foundClass = Class.forName(p.getName()+"."+filteredClassName);
					foundClasses.add(foundClass);
					logger.info("Found class "+foundClass);
				} catch (ClassNotFoundException e) {
				}
			}
			for (Class c : foundClasses) {
				registerClass(c);
			}*/
			updateMatchingClasses();
		}
	}

	public void search() {
		Vector<Class> foundClasses = new Vector<Class>();
		try {
			Class foundClass = Class.forName(getFilteredPackageName() + "." + filteredClassName);
			foundClasses.add(foundClass);
			logger.info("Found class " + foundClass);
		} catch (ClassNotFoundException e) {
		}
		for (Package p : packages.keySet()) {
			try {
				Class foundClass = Class.forName(p.getName() + "." + filteredClassName);
				foundClasses.add(foundClass);
				logger.info("Found class " + foundClass);
			} catch (ClassNotFoundException e) {
			}
		}
		for (Class c : foundClasses) {
			registerClass(c);
		}
		updateMatchingClasses();
	}

	private void updateMatchingClasses() {
		matchingClasses.clear();

		if (!StringUtils.isEmpty(filteredClassName)) {
			String patternString = filteredClassName;
			if (patternString.startsWith("*"))
				patternString = "." + filteredClassName;
			try {
				Vector<ClassInfo> exactMatches = new Vector<ClassInfo>();
				if (classesForName.get(filteredClassName) != null) {
					exactMatches = classesForName.get(filteredClassName);
					matchingClasses.addAll(exactMatches);
				}
				Pattern pattern = Pattern.compile(patternString);
				for (String s : classesForName.keySet()) {
					Matcher matcher = pattern.matcher(s);
					if (matcher.find()) {
						for (ClassInfo potentialMatch : classesForName.get(s)) {
							PackageInfo packageInfo = registerPackage(potentialMatch.clazz.getPackage());
							if (!packageInfo.isFiltered()) {
								if (!exactMatches.contains(potentialMatch))
									matchingClasses.add(potentialMatch);
								// System.out.println("Found "+potentialMatch);
							}
						}
					}
				}
			} catch (PatternSyntaxException e) {
				logger.warning("PatternSyntaxException: " + patternString);
			}
			if (matchingClasses.size() == 1) {
				setSelectedClassInfo(matchingClasses.firstElement());
			}
		}

		instance.pcSupport.firePropertyChange("packages", null, null);
		pcSupport.firePropertyChange("matchingClasses", null, null);

	}

	private ClassInfo selectedClassInfo;

	public ClassInfo getSelectedClassInfo() {
		return selectedClassInfo;
	}

	public void setSelectedClassInfo(ClassInfo selectedClassInfo) {
		if (selectedClassInfo != this.selectedClassInfo) {
			ClassInfo oldSelectedClassInfo = this.selectedClassInfo;
			logger.info("setSelectedClassInfo with " + selectedClassInfo);
			this.selectedClassInfo = selectedClassInfo;
			// if (selectedClassInfo != null) setFilteredClassName(selectedClassInfo.className);
			pcSupport.firePropertyChange("selectedClassInfo", oldSelectedClassInfo, selectedClassInfo);
		}
	}
}
