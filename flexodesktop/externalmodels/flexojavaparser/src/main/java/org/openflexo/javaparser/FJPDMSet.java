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
package org.openflexo.javaparser;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMSet;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference.MethodReference;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference.PropertyReference;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.javaparser.FJPDMSet.FJPPackageReference.FJPClassReference;
import org.openflexo.logging.FlexoLogger;

public class FJPDMSet extends DMSet {

	@SuppressWarnings("unused")
	private Logger logger = FlexoLogger.getLogger(FJPDMSet.class.getPackage().getName());

	public FJPDMSet(FlexoProject project, String unlocalizedName, FJPJavaSource source, DMEntity entity) {
		this(project, unlocalizedName, entriesForUniqueSource(source, entity));
	}

	private static Hashtable<FJPJavaSource, DMEntity> entriesForUniqueSource(FJPJavaSource source, DMEntity entity) {
		Hashtable<FJPJavaSource, DMEntity> returned = new Hashtable<FJPJavaSource, DMEntity>();
		returned.put(source, entity);
		return returned;
	}

	public FJPDMSet(FlexoProject project, String unlocalizedName, Hashtable<FJPJavaSource, DMEntity> entries) {
		super(project);
		// Date date1 = new Date();
		_unlocalizedName = unlocalizedName;
		setImportGetOnlyProperties(true);
		setImportMethods(true);
		for (FJPJavaSource javaSource : entries.keySet()) {
			addJavaClass(javaSource, entries.get(javaSource));
		}
		addToSelectedObjects(this);
		// Date date2 = new Date();
		// logger.info("TimeForBuilding FJPDMSet : "+(date2.getTime()-date1.getTime())+" ms");

	}

	private FJPPackageReference packageForClass(FJPJavaClass aClass) {
		String packageName = aClass.getPackage() != null ? aClass.getPackage() : DMPackage.DEFAULT_PACKAGE_NAME;
		return packageNamed(packageName);
	}

	private FJPPackageReference packageNamed(String packageName) {
		if (packageName == null) {
			packageName = DMPackage.DEFAULT_PACKAGE_NAME;
		}
		if (_packages.get(packageName) == null) {
			FJPPackageReference newPackageReference = new FJPPackageReference(packageName);
			_packages.put(packageName, newPackageReference);
			addToSelectedObjects(newPackageReference);
		}
		return (FJPPackageReference) _packages.get(packageName);
	}

	private void addJavaClass(FJPJavaSource javaSource, DMEntity entity) {
		if (javaSource == null) {
			return;
		}
		if (javaSource.getRootClass() == null) {
			return;
		}
		FJPPackageReference packageRef = packageForClass(javaSource.getRootClass());
		addToSelectedObjects(packageRef);
		ClassReference aClassReference = packageRef.add(javaSource, entity);
		addToSelectedObjects(aClassReference);
	}

	public class FJPPackageReference extends org.openflexo.foundation.dm.DMSet.PackageReference {
		public FJPPackageReference(String packageName) {
			super(packageName);
			// logger.info("new FJPPackageReference "+getName());
		}

		public FJPClassReference add(FJPJavaSource javaSource, DMEntity entity) {
			FJPClassReference aClassReference = new FJPClassReference(javaSource, entity);
			_classes.add(aClassReference);
			return aClassReference;
		}

		public class FJPClassReference extends ClassReference {
			private FJPJavaSource _javaSource;
			private FJPJavaClass _referencedClass;

			public FJPClassReference(FJPJavaSource javaSource, DMEntity entity) {
				super(javaSource.getRootClass().getName());
				// FJPTypeResolver.startDebug();
				// Date date1 = new Date();
				_referencedClass = javaSource.getRootClass();
				_javaSource = javaSource;
				Vector<String> excludedSignatures = new Vector<String>();
				Vector<DMProperty> properties = FJPDMMapper.searchForProperties(javaSource.getRootClass(), _project.getDataModel(),
						FJPDMSet.this, javaSource, getImportGetOnlyProperties(), false, excludedSignatures);
				for (Enumeration en = properties.elements(); en.hasMoreElements();) {
					DMProperty next = (DMProperty) en.nextElement();
					PropertyReference propertyReference = new PropertyReference(next.getName(), next.getIsSettable(),
							next.getCardinality(),
							// (next.getType()!=null?next.getType().getName():next.getUnresolvedTypeName()));
							next.getType().getStringRepresentation(), next.getType().getSimplifiedStringRepresentation());
					propertyReference.setResolvable(next.isResolvable());
					_properties.add(propertyReference);
					if (entity != null && entity.getDMProperty(next.getName()) != null) {
						addToSelectedObjects(propertyReference);
						propertyReference.setNewlyDiscovered(false);
					} else {
						propertyReference.setNewlyDiscovered(true);
					}
				}
				if (getImportMethods()) {
					Vector methods = FJPDMMapper.searchForMethods(javaSource.getRootClass(), _project.getDataModel(), FJPDMSet.this,
							javaSource, false, excludedSignatures);
					for (Enumeration en = methods.elements(); en.hasMoreElements();) {
						DMMethod next = (DMMethod) en.nextElement();
						MethodReference methodReference = new MethodReference(next.getSignature(), next.getSimplifiedSignature(),
						// (next.getReturnType()!=null?next.getReturnType().getName():next.getUnresolvedReturnTypeName()));
								next.getReturnType().getStringRepresentation(), next.getReturnType().getSimplifiedStringRepresentation());
						methodReference.setResolvable(next.isResolvable());
						_methods.add(methodReference);
						if (entity != null && entity.getDeclaredMethod(next.getSignature()) != null) {
							addToSelectedObjects(methodReference);
							methodReference.setNewlyDiscovered(false);
						} else {
							methodReference.setNewlyDiscovered(true);
						}
					}
				}
				// Date date2 = new Date();
				// logger.info("Time for building class "+javaSource.getRootClass().getName()+": "+(date2.getTime()-date1.getTime())+" ms (resolving types took "+FJPTypeResolver.timeSpendResolvingTypes+" ms)");
				// FJPTypeResolver.stopDebug();
			}
		}
	}

	public FJPClassReference getClassReference(FJPJavaClass aClass) {
		String className = aClass.getName();
		PackageReference aPackage = packageForClass(aClass);
		if (aPackage != null) {
			return (FJPClassReference) aPackage.getClassReference(className);
		}
		return null;
	}

	@Override
	public FJPClassReference getClassReference(String fullQualifiedName) {
		PackageReference aPackage = packageNamed(DMSet.packageNameForClassName(fullQualifiedName));
		if (aPackage != null) {
			return (FJPClassReference) aPackage.getClassReference(fullQualifiedName);
		}
		return null;
	}

	public boolean containsSelectedClass(FJPJavaClass aClass) {
		ClassReference searchedClass = getClassReference(aClass);
		return searchedClass != null && searchedClass.isSelected();
	}

	public void notifyKnownAndIgnoredProperties(Hashtable<FJPJavaClass, Vector<String>> ignoredProperties) {
		for (FJPJavaClass aClass : ignoredProperties.keySet()) {
			FJPClassReference classReference = getClassReference(aClass);
			if (classReference != null) {
				Vector<String> propertiesToIgnore = ignoredProperties.get(aClass);
				for (PropertyReference prop : classReference.getProperties()) {
					if (propertiesToIgnore.contains(prop.getName())) {
						prop.setToBeIgnored(true);
						prop.setNewlyDiscovered(false);
						removeFromSelectedObjects(prop);
					}
				}
			}
		}
	}

	public void notifyKnownAndIgnoredMethods(Hashtable<FJPJavaClass, Vector<String>> ignoredMethods) {
		for (FJPJavaClass aClass : ignoredMethods.keySet()) {
			FJPClassReference classReference = getClassReference(aClass);
			if (classReference != null) {
				Vector<String> methodsToIgnore = ignoredMethods.get(aClass);
				for (MethodReference meth : classReference.getMethods()) {
					if (methodsToIgnore.contains(meth.getSignature())) {
						meth.setToBeIgnored(true);
						meth.setNewlyDiscovered(false);
						removeFromSelectedObjects(meth);
					}
				}
			}
		}
	}

	public void selectAllNewlyDiscoveredPropertiesAndMethods() {
		for (PackageReference aPackage : _packages.values()) {
			for (ClassReference aClass : aPackage.getClasses()) {
				for (PropertyReference prop : aClass.getProperties()) {
					if (prop.isNewlyDiscovered() && prop.isResolvable()) {
						addToSelectedObjects(prop);
					}
				}
				for (MethodReference meth : aClass.getMethods()) {
					if (meth.isNewlyDiscovered() && meth.isResolvable()) {
						addToSelectedObjects(meth);
					}
				}
			}
		}
	}

}
