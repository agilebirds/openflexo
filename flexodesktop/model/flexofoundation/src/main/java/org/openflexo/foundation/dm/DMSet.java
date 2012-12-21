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
import java.lang.reflect.TypeVariable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.localization.FlexoLocalization;

public class DMSet extends TemporaryFlexoModelObject {

	private static final Logger logger = Logger.getLogger(DMSet.class.getPackage().getName());

	private File _jarFile;
	protected Hashtable<String, PackageReference> _packages;
	private boolean _importGetOnlyProperties = false;
	private boolean _importMethods = false;
	protected Vector<FlexoObject> _selectedObjects;
	protected String _unlocalizedName;
	public FlexoProject _project;
	private ExternalRepository _externalRepository;

	protected DMSet(FlexoProject project) {
		super();
		_project = project;
		_selectedObjects = new Vector<FlexoObject>();
		_packages = new Hashtable<String, PackageReference>();
		_justResolvedEntities = new Hashtable<ClassReference, DMEntity>();
	}

	public DMSet(FlexoProject project, File jarFile, boolean parseMethodsAndProperties, FlexoProgress progress) {
		this(project);
		_jarFile = jarFile;
		Iterator<Class<?>> i = ExternalRepository.getContainedClasses(jarFile, null, project, progress);
		while (i.hasNext()) {
			Class<?> aClass = i.next();
			String packageName = packageNameForClass(aClass);
			if (_packages.get(packageName) == null) {
				_packages.put(packageName, new PackageReference(packageName));
			}
			_packages.get(packageName).add(aClass, parseMethodsAndProperties, (LoadableDMEntity) null);
		}

	}

	public DMSet(FlexoProject project, ExternalRepository externalRepository, boolean parseMethodsAndProperties, FlexoProgress progress) {
		this(project);
		_externalRepository = externalRepository;
		for (Class<?> aClass : externalRepository.getJarLoader().getContainedClasses().values()) {
			String packageName = packageNameForClass(aClass);
			if (_packages.get(packageName) == null) {
				_packages.put(packageName, new PackageReference(packageName));
			}
			_packages.get(packageName).add(aClass, parseMethodsAndProperties, externalRepository);
		}
		for (DMEntity e : externalRepository.getEntities().values()) {
			LoadableDMEntity entity = (LoadableDMEntity) e;
			if (entity.getType() != null && entity.getJavaType() != null) {
				ClassReference aClassReference = getClassReference(entity.getJavaType());
				addToSelectedObjects(aClassReference.getPackageReference());
				addToSelectedObjects(aClassReference);
			} else {
				logger.warning("Could not find class for " + entity);
			}
		}
		addToSelectedObjects(this);

	}

	public DMSet(FlexoProject project, String unlocalizedName, LoadableDMEntity entity, boolean parseMethodsAndProperties,
			FlexoProgress progress) {
		this(project, unlocalizedName, makeUniqueElementVector(entity), parseMethodsAndProperties, progress);
	}

	private static Vector<LoadableDMEntity> makeUniqueElementVector(LoadableDMEntity e) {
		Vector<LoadableDMEntity> returned = new Vector<LoadableDMEntity>();
		returned.add(e);
		return returned;
	}

	public DMSet(FlexoProject project, String unlocalizedName, List<LoadableDMEntity> entities, boolean parseMethodsAndProperties,
			FlexoProgress progress) {
		this(project);
		_unlocalizedName = unlocalizedName;
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("analysing_classes"));
			progress.resetSecondaryProgress(entities.size());
		}
		for (LoadableDMEntity entity : entities) {
			Class<?> aClass = entity.retrieveJavaType();
			if (aClass != null) {
				if (progress != null) {
					progress.setSecondaryProgress(FlexoLocalization.localizedForKey("analysing") + " " + aClass.getName());
				}
				String packageName = packageNameForClass(aClass);
				if (_packages.get(packageName) == null) {
					PackageReference newPackageReference = new PackageReference(packageName);
					_packages.put(packageName, newPackageReference);
					addToSelectedObjects(newPackageReference);
				}
				ClassReference aClassReference = _packages.get(packageName).add(aClass, parseMethodsAndProperties, entity);
				addToSelectedObjects(aClassReference);
			} else {
				logger.warning("Could not find class for " + entity);
			}
		}
		addToSelectedObjects(this);

	}

	public Vector<FlexoObject> getSelectedObjects() {
		return _selectedObjects;
	}

	public void setSelectedObjects(Vector<? extends FlexoObject> someSelectedObjects) {
		_selectedObjects.clear();
		_selectedObjects.addAll(someSelectedObjects);
	}

	public void addToSelectedObjects(FlexoModelObject obj) {
		if (!_selectedObjects.contains(obj)) {
			_selectedObjects.add(obj);
		}
	}

	public void removeFromSelectedObjects(FlexoModelObject obj) {
		if (_selectedObjects.contains(obj)) {
			_selectedObjects.remove(obj);
		}
	}

	protected static String packageNameForClass(Class<?> aClass) {
		return packageNameForClassName(aClass.getName());
	}

	protected static String packageNameForClassName(String className) {
		String packageName = null;
		StringTokenizer st = new StringTokenizer(className, ".");
		while (st.hasMoreTokens()) {
			String nextToken = st.nextToken();
			if (st.hasMoreTokens()) {
				if (packageName == null) {
					packageName = nextToken;
				} else {
					packageName += "." + nextToken;
				}
			}
		}
		if (packageName == null) {
			return DMPackage.DEFAULT_PACKAGE_NAME;
		}
		return packageName;
	}

	protected static String classNameForClass(Class<?> aClass) {
		String className = null;
		StringTokenizer st = new StringTokenizer(aClass.getName(), ".");
		while (st.hasMoreTokens()) {
			String nextToken = st.nextToken();
			if (!st.hasMoreTokens()) {
				className = nextToken;
			}
		}
		if (aClass.getTypeParameters().length > 0) {
			className += "<";
			boolean isFirst = true;
			for (TypeVariable<?> tv : aClass.getTypeParameters()) {
				className += (isFirst ? "" : ",") + tv.getName();
				isFirst = false;
			}
			className += ">";
		}
		return className;
	}

	@Override
	public String getName() {
		if (_jarFile != null) {
			return _jarFile.getName();
		}
		if (_externalRepository != null) {
			return _externalRepository.getName();
		}
		if (_unlocalizedName != null) {
			return FlexoLocalization.localizedForKey(_unlocalizedName);
		}
		return FlexoLocalization.localizedForKey("unnamed_set");
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject instanceof DMSet) {
			return ((DMSet) anObject).getName().equals(getName());
		}
		return super.equals(anObject);
	}

	@Override
	public int hashCode() {
		return getName() == null ? 0 : getName().hashCode();
	}

	public Enumeration<PackageReference> getPackages() {
		Vector<PackageReference> orderedPackages = new Vector<PackageReference>(_packages.values());
		Collections.sort(orderedPackages, new Comparator<PackageReference>() {
			@Override
			public int compare(PackageReference o1, PackageReference o2) {
				return Collator.getInstance().compare(o1.getName(), o2.getName());
			}
		});
		return orderedPackages.elements();
	}

	public class PackageReference extends TemporaryFlexoModelObject {
		protected String _packageName;
		protected Vector<ClassReference> _classes;

		public PackageReference(String packageName) {
			super();
			_packageName = packageName;
			_classes = new Vector<ClassReference>();
		}

		public ClassReference getClassReference(String className) {
			for (ClassReference next : _classes) {
				if (next.getName().equals(className)) {
					return next;
				}
				if (next.getName().equals(_packageName + "." + className)) {
					return next;
				}
				if ((_packageName + "." + next.getName()).equals(className)) {
					return next;
				}
			}
			return null;
		}

		public Enumeration<ClassReference> getClassesEnumeration() {
			Collections.sort(_classes, new Comparator<ClassReference>() {
				@Override
				public int compare(ClassReference o1, ClassReference o2) {
					return Collator.getInstance().compare(o1.getName(), o2.getName());
				}
			});
			return _classes.elements();
		}

		@Override
		public String getName() {
			return _packageName;
		}

		public String getLocalizedName() {
			if (_packageName.equals(DMPackage.DEFAULT_PACKAGE_NAME)) {
				return FlexoLocalization.localizedForKey(DMPackage.DEFAULT_PACKAGE_NAME);
			}
			return _packageName;
		}

		@Override
		public String toString() {
			return "PackageReference:" + getName();
		}

		@Override
		public boolean equals(Object anObject) {
			if (anObject instanceof PackageReference) {
				return ((PackageReference) anObject).getName().equals(getName());
			}
			return super.equals(anObject);
		}

		@Override
		public int hashCode() {
			return getName() == null ? 0 : getName().hashCode();
		}

		public ClassReference add(Class<?> aClass, boolean parseMethodsAndProperties, DMRepository repository) {
			ClassReference aClassReference = new ClassReference(aClass, parseMethodsAndProperties, repository);
			_classes.add(aClassReference);
			return aClassReference;
		}

		public ClassReference add(Class<?> aClass, boolean parseMethodsAndProperties, LoadableDMEntity entity) {
			ClassReference aClassReference = new ClassReference(aClass, parseMethodsAndProperties, entity);
			_classes.add(aClassReference);
			return aClassReference;
		}

		public class ClassReference extends TemporaryFlexoModelObject {
			private String _className;
			protected Vector<PropertyReference> _properties;
			protected Vector<MethodReference> _methods;
			private Class<?> _referencedClass;

			public ClassReference(Class<?> aClass, boolean parseMethodsAndProperties, LoadableDMEntity entity) {
				this(aClass, parseMethodsAndProperties, entity, entity != null ? entity.getRepository() : null);
			}

			public ClassReference(Class<?> aClass, boolean parseMethodsAndProperties, DMRepository repository) {
				this(aClass, parseMethodsAndProperties, null, repository);
			}

			private ClassReference(Class<?> aClass, boolean parseMethodsAndProperties, LoadableDMEntity entity, DMRepository repository) {
				this(classNameForClass(aClass));
				_referencedClass = aClass;
				if (parseMethodsAndProperties && repository != null) {
					List<String> excludedSignatures = new ArrayList<String>();
					List<DMProperty> properties = LoadableDMEntity.searchForProperties(aClass, _project.getDataModel(), repository, true,/*true,*/
							false, excludedSignatures);
					for (DMProperty next : properties) {
						PropertyReference propertyReference = new PropertyReference(next.getName(), next.getIsSettable(),
								next.getCardinality(),
								// (next.getType()!=null?next.getType().getName():next.getUnresolvedTypeName()+"<unloaded>"));
								next.getType().getStringRepresentation(), next.getType().getSimplifiedStringRepresentation()
										+ (next.getType().isResolved() ? "" : "<unloaded>"));
						_properties.add(propertyReference);
						if (entity != null && entity.getDMProperty(next.getName()) != null) {
							addToSelectedObjects(propertyReference);
						}
					}
					List<DMMethod> methods = LoadableDMEntity.searchForMethods(aClass, _project.getDataModel(), repository,/*true,true,*/
							false, excludedSignatures);
					for (DMMethod next : methods) {
						MethodReference methodReference = new MethodReference(next.getSignature(), next.getSimplifiedSignature(),
						// (next.getReturnType()!=null?next.getReturnType().getName():next.getUnresolvedReturnType().getValue()+"<unloaded>"));
								next.getReturnType().getStringRepresentation(), next.getReturnType().getSimplifiedStringRepresentation()
										+ (next.getType().isResolved() ? "" : "<unloaded>"));
						_methods.add(methodReference);
						if (entity != null && entity.getDeclaredMethod(next.getSignature()) != null) {
							addToSelectedObjects(methodReference);
						}
					}
				}
			}

			public ClassReference(String className) {
				super();
				_className = className;
				_properties = new Vector<PropertyReference>();
				_methods = new Vector<MethodReference>();
			}

			public Class<?> getReferencedClass() {
				return _referencedClass;
			}

			@Override
			public String getName() {
				return _className;
			}

			public String getPackageName() {
				return _packageName;
			}

			public PackageReference getPackageReference() {
				return PackageReference.this;
			}

			@Override
			public String toString() {
				return "ClassReference:" + getName();
			}

			@Override
			public boolean equals(Object anObject) {
				if (anObject instanceof ClassReference) {
					return ((ClassReference) anObject).getName().equals(getName());
				}
				return super.equals(anObject);
			}

			@Override
			public int hashCode() {
				return getName() == null ? 0 : getName().hashCode();
			}

			public boolean isSelected() {
				return _selectedObjects.contains(ClassReference.this);
			}

			public Enumeration<MethodReference> getMethodsEnumeration() {
				Collections.sort(_methods, new Comparator<MethodReference>() {
					@Override
					public int compare(MethodReference o1, MethodReference o2) {
						return Collator.getInstance().compare(o1.getSignature(), o2.getSignature());
					}
				});
				return _methods.elements();
			}

			public Enumeration<PropertyReference> getPropertiesEnumeration() {
				Collections.sort(_properties, new Comparator<PropertyReference>() {
					@Override
					public int compare(PropertyReference o1, PropertyReference o2) {
						return Collator.getInstance().compare(o1.getName(), o2.getName());
					}
				});
				return _properties.elements();
			}

			public Vector<MethodReference> getMethods() {
				return _methods;
			}

			public Vector<PropertyReference> getProperties() {
				return _properties;
			}

			public class PropertyReference extends TemporaryFlexoModelObject {
				private String _propertyName;
				private String _simplifiedTypeName;
				private boolean _isSettable;
				private DMCardinality _cardinality;
				private String _typeName;
				private boolean _newlyDiscovered = false;
				private boolean _isToBeIgnored = false;

				public PropertyReference(String propertyName, boolean isSettable, DMCardinality cardinality, String typeName,
						String simplifiedTypeName) {
					super();
					_propertyName = propertyName;
					_simplifiedTypeName = simplifiedTypeName;
					_isSettable = isSettable;
					_cardinality = cardinality;
					_typeName = typeName;
				}

				@Override
				public String getName() {
					return _propertyName;
				}

				@Override
				public String toString() {
					return "PropertyReference:" + getName();
				}

				@Override
				public boolean equals(Object anObject) {
					if (anObject instanceof PropertyReference) {
						return ((PropertyReference) anObject).getName().equals(getName());
					}
					return super.equals(anObject);
				}

				@Override
				public int hashCode() {
					return getName().hashCode();
				}

				public boolean isSelected() {
					return _selectedObjects.contains(PropertyReference.this);
				}

				public DMCardinality getCardinality() {
					return _cardinality;
				}

				public boolean isSettable() {
					return _isSettable;
				}

				public String getTypeName() {
					return _typeName;
				}

				public String getSimplifiedTypeName() {
					return _simplifiedTypeName;
				}

				private boolean _isResolvable = true;

				public boolean isResolvable() {
					return _isResolvable;
				}

				public void setResolvable(boolean isResolvable) {
					_isResolvable = isResolvable;
				}

				public boolean isNewlyDiscovered() {
					return _newlyDiscovered;
				}

				public void setNewlyDiscovered(boolean newlyDiscovered) {
					_newlyDiscovered = newlyDiscovered;
				}

				public boolean isToBeIgnored() {
					return _isToBeIgnored;
				}

				public void setToBeIgnored(boolean isToBeIgnored) {
					_isToBeIgnored = isToBeIgnored;
				}
			}

			public class MethodReference extends TemporaryFlexoModelObject {
				private String _signature;
				private String _returnTypeName;
				private String _simplifiedSignature;
				private String _simplifiedReturnTypeName;
				private boolean _newlyDiscovered = false;
				private boolean _isToBeIgnored = false;

				public MethodReference(String signature, String simplifiedSignature, String returnTypeName, String simplifiedReturnTypeName) {
					super();
					_signature = signature;
					_returnTypeName = returnTypeName;
					_simplifiedSignature = simplifiedSignature;
					_simplifiedReturnTypeName = simplifiedReturnTypeName;
				}

				public String getSignature() {
					return _signature;
				}

				public String getSimplifiedSignature() {
					return _simplifiedSignature;
				}

				@Override
				public String toString() {
					return "MethodReference:" + getName();
				}

				@Override
				public boolean equals(Object anObject) {
					if (anObject instanceof MethodReference) {
						return ((MethodReference) anObject).getSignature().equals(getSignature());
					}
					return super.equals(anObject);
				}

				@Override
				public int hashCode() {
					return getSignature().hashCode();
				}

				public boolean isSelected() {
					return _selectedObjects.contains(MethodReference.this);
				}

				public String getReturnTypeName() {
					return _returnTypeName;
				}

				public String getSimplifiedReturnTypeName() {
					return _simplifiedReturnTypeName;
				}

				private boolean _isResolvable = true;

				public boolean isResolvable() {
					return _isResolvable;
				}

				public void setResolvable(boolean isResolvable) {
					_isResolvable = isResolvable;
				}

				public boolean isNewlyDiscovered() {
					return _newlyDiscovered;
				}

				public void setNewlyDiscovered(boolean newlyDiscovered) {
					_newlyDiscovered = newlyDiscovered;
				}

				public boolean isToBeIgnored() {
					return _isToBeIgnored;
				}

				public void setToBeIgnored(boolean isToBeIgnored) {
					_isToBeIgnored = isToBeIgnored;
				}

			}
		}

		public boolean contains(ClassReference searchedClass) {
			return _classes.contains(searchedClass);
		}

		public boolean containsSelected(ClassReference searchedClass) {
			return contains(searchedClass) && searchedClass.isSelected();
		}

		public boolean isSelected() {
			return _selectedObjects.contains(PackageReference.this);
		}

		public Vector<ClassReference> getClasses() {
			return _classes;
		}

	}

	public ClassReference getClassReference(Class<?> aClass) {
		String packageName = packageNameForClass(aClass);
		String className = classNameForClass(aClass);
		PackageReference aPackage = _packages.get(packageName);
		if (aPackage != null) {
			return aPackage.getClassReference(className);
		}
		return null;
	}

	public boolean containsSelectedClass(Class<?> aClass) {
		ClassReference searchedClass = getClassReference(aClass);
		return searchedClass != null && searchedClass.isSelected();
	}

	public ClassReference getClassReference(String fullQualifiedName) {
		PackageReference aPackage = _packages.get(DMSet.packageNameForClassName(fullQualifiedName));
		if (aPackage != null) {
			return aPackage.getClassReference(fullQualifiedName);
		}
		return null;
	}

	public boolean getImportGetOnlyProperties() {
		return _importGetOnlyProperties;
	}

	public void setImportGetOnlyProperties(boolean importGetOnlyProperties) {
		_importGetOnlyProperties = importGetOnlyProperties;
	}

	public boolean getImportMethods() {
		return _importMethods;
	}

	public void setImportMethods(boolean importMethods) {
		_importMethods = importMethods;
	}

	protected Hashtable<ClassReference, DMEntity> _justResolvedEntities;

	public DMEntity justResolvedEntity(ClassReference classRef) {
		return _justResolvedEntities.get(classRef);
	}
}
