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
package org.openflexo.fib.editor.test;

import java.net.URL;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Icon;

import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.toolbox.ClassScope;
import org.openflexo.toolbox.StringUtils;

public class LoadedClassesInfo extends Observable {

	private static final Logger logger = Logger.getLogger(LoadedClassesInfo.class.getPackage().getName());

	static ClassLoader appLoader = ClassLoader.getSystemClassLoader();
	static ClassLoader currentLoader = LoadedClassesInfo.class.getClassLoader();
	static ClassLoader[] loaders = new ClassLoader[] { appLoader, currentLoader };

	public static LoadedClassesInfo instance;

	static {
		appLoader = ClassLoader.getSystemClassLoader();
		currentLoader = LoadedClassesInfo.class.getClassLoader();
		if (appLoader != currentLoader) {
			loaders = new ClassLoader[] { appLoader, currentLoader };
		} else {
			loaders = new ClassLoader[] { appLoader };
		}
		instance = new LoadedClassesInfo();
	}

	private final Map<Package, PackageInfo> packages;
	private Vector<PackageInfo> packageList;
	private boolean needsReordering = true;

	private final Hashtable<String, Vector<ClassInfo>> classesForName;

	private LoadedClassesInfo() {
		classesForName = new Hashtable<String, Vector<ClassInfo>>();
		packages = Collections.synchronizedMap(new HashMap<Package, PackageInfo>() {
			@Override
			public synchronized PackageInfo put(Package key, PackageInfo value) {
				PackageInfo returned = super.put(key, value);
				needsReordering = true;
				setChanged();
				notifyObservers();
				return returned;
			};
		});
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

	public class PackageInfo extends Observable {
		public String packageName;
		private final Hashtable<Class, ClassInfo> classes = new Hashtable<Class, ClassInfo>() {
			@Override
			public synchronized ClassInfo put(Class key, ClassInfo value) {
				ClassInfo returned = super.put(key, value);
				needsReordering = true;
				setChanged();
				notifyObservers();
				return returned;
			};
		};
		private Vector<ClassInfo> classesList;
		private boolean needsReordering = true;

		public PackageInfo(Package aPackage) {
			if (aPackage != null) {
				packageName = aPackage.getName();
			}
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
			if (getFilteredPackageName() == null || StringUtils.isEmpty(getFilteredPackageName())) {
				return false;
			}
			if (packageName == null) {
				return true;
			}
			if (packageName.startsWith(getFilteredPackageName())) {
				return false;
			}
			String patternString = getFilteredPackageName();
			if (patternString.startsWith("*")) {
				patternString = "." + getFilteredPackageName();
			}
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

	public class ClassInfo extends Observable {
		private final Class clazz;
		public String className;
		public String packageName;
		public String fullQualifiedName;

		private final Hashtable<Class, ClassInfo> memberClasses = new Hashtable<Class, ClassInfo>() {
			@Override
			public synchronized ClassInfo put(Class key, ClassInfo value) {
				ClassInfo returned = super.put(key, value);
				needsReordering = true;
				setChanged();
				notifyObservers();
				return returned;
			};
		};
		private Vector<ClassInfo> memberClassesList;
		private boolean needsReordering = true;

		public ClassInfo(Class aClass) {
			Vector<ClassInfo> listOfClassesWithThatName = classesForName.get(aClass.getSimpleName());
			if (listOfClassesWithThatName == null) {
				classesForName.put(aClass.getSimpleName(), listOfClassesWithThatName = new Vector<ClassInfo>());
			}
			listOfClassesWithThatName.add(this);
			className = aClass.getSimpleName();
			if (aClass.getPackage() != null) {
				packageName = aClass.getPackage().getName();
			}
			fullQualifiedName = aClass.getName();
			clazz = aClass;
			logger.fine("Instanciate new ClassInfo for " + aClass);
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
				for (Class<?> c : memberClasses.keySet()) {
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

		@Override
		public String toString() {
			return "ClassInfo[" + clazz.getName() + "]";
		}

		public Icon getIcon() {
			if (clazz.isEnum()) {
				return FIBIconLibrary.ENUM_ICON;
			}
			if (clazz.isInterface()) {
				return FIBIconLibrary.INTERFACE_ICON;
			}
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
			for (Package p : packages.keySet()) {
				try {
					Class foundClass = Class.forName(p.getName() + "." + filteredClassName);
					registerClass(foundClass);
					logger.info("Found class " + foundClass);
				} catch (ClassNotFoundException e) {
				}
			}
			updateMatchingClasses();
		}
	}

	private void updateMatchingClasses() {
		matchingClasses.clear();

		if (!StringUtils.isEmpty(filteredClassName)) {
			String patternString = filteredClassName;
			if (patternString.startsWith("*")) {
				patternString = "." + filteredClassName;
			}
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
								if (!exactMatches.contains(potentialMatch)) {
									matchingClasses.add(potentialMatch);
									// System.out.println("Found "+potentialMatch);
								}
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

		setChanged();
		notifyObservers();

	}

	public Vector<ClassInfo> matchingClasses = new Vector<ClassInfo>();

	private ClassInfo selectedClassInfo;

	public ClassInfo getSelectedClassInfo() {
		return selectedClassInfo;
	}

	public void setSelectedClassInfo(ClassInfo selectedClassInfo) {
		if (selectedClassInfo != this.selectedClassInfo) {
			logger.info("setSelectedClassInfo with " + selectedClassInfo);
			this.selectedClassInfo = selectedClassInfo;
			setChanged();
			notifyObservers();
		}
	}

	/*public static void main(String[] args) 
	{
		System.out.println("Hello world");
		try {
			Class.forName("org.openflexo.fib.TestFIBBrowser");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//updateClasses();
		String classPath = System.getProperty("java.class.path",".");
		System.out.println("classPath="+classPath);
		String libraryPath = System.getProperty("java.library.path",".");
		System.out.println("libraryPath="+libraryPath);
		System.out.println("java.home="+System.getProperty("java.home","."));
		
		System.out.println("Packages: "+Package.getPackages());
		for (Package p : Package.getPackages()) {
			System.out.println("package:"+p.getName());
		}
	}*/

	public static void main(String[] args) {

		Pattern pattern = Pattern.compile("javadsq.*");

		Matcher matcher = pattern.matcher("java.util.coucou");

		boolean found = false;
		while (matcher.find()) {
			System.out.println("I found the text  starting at index and ending at index" + matcher.group() + matcher.start()
					+ matcher.end());
			found = true;
		}
		if (!found) {
			System.out.println("No match found.");
		}
	}

}
