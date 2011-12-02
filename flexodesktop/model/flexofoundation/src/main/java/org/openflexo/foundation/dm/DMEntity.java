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

import java.text.Collator;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.swing.tree.TreeNode;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMMethod.DMMethodParameter;
import org.openflexo.foundation.dm.DMType.DMTypeTokenizer;
import org.openflexo.foundation.dm.action.CreateComponentFromEntity;
import org.openflexo.foundation.dm.action.CreateDMMethod;
import org.openflexo.foundation.dm.action.CreateDMProperty;
import org.openflexo.foundation.dm.action.CreateDMTranstyper;
import org.openflexo.foundation.dm.action.ShowTypeHierarchyAction;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.dm.DMEntityClassNameChanged;
import org.openflexo.foundation.dm.dm.DMEntityNameChanged;
import org.openflexo.foundation.dm.dm.DMObjectDeleted;
import org.openflexo.foundation.dm.dm.MethodRegistered;
import org.openflexo.foundation.dm.dm.MethodUnregistered;
import org.openflexo.foundation.dm.dm.PropertiesReordered;
import org.openflexo.foundation.dm.dm.PropertyRegistered;
import org.openflexo.foundation.dm.dm.PropertyUnregistered;
import org.openflexo.foundation.dm.dm.TranstyperRegistered;
import org.openflexo.foundation.dm.dm.TranstyperUnregistered;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOProperty;
import org.openflexo.foundation.ie.IERegExp;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;

/**
 * Represents a java entity (class, interface or enum) and its definition
 * 
 * @author sguerin
 * 
 */
public class DMEntity extends DMObject implements DMGenericDeclaration, DMTypeOwner, Typed {

	private static final Logger logger = Logger.getLogger(DMEntity.class.getPackage().getName());

	// ===============================================================
	// ======================= Instance variables ====================
	// ===============================================================

	protected String name;

	protected String entityPackageName;

	protected String entityClassName;

	protected DMType _parentType;

	protected Vector<DMType> _implementedTypes;

	private final Vector<DMEntity> childEntities;

	// private Vector<Typed> typedWithThisEntity;

	private DMRepository repository;

	// List of all properties declared in this entity (hashtable of DMProperty)
	private DMPropertyHashtable properties;

	// List of all methods declared for this entity (hashtable of DMMethod)
	private DMMethodHashtable methods;

	private boolean readOnly;

	private Date _lastUpdate;

	private boolean _isInterface;

	private boolean _isEnumeration;

	// List of all type variables declared for this entity
	private Vector<DMTypeVariable> _typeVariables;

	// List of transtypers declared for this entity
	private final Vector<DMTranstyper> _declaredTranstypers;

	private class DMPropertyHashtable extends Hashtable<String, DMProperty> {
		public DMPropertyHashtable() {
			super();
		}

		public DMPropertyHashtable(Hashtable<String, DMProperty> ht) {
			super(ht);
		}

		@Override
		public Enumeration<String> keys() {
			if (isSerializing()) {
				// Order keys in this case
				Vector<String> orderedKeys = new Vector<String>();
				for (Enumeration<String> en = super.keys(); en.hasMoreElements();) {
					orderedKeys.add(en.nextElement());
				}
				Collections.sort(orderedKeys, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return Collator.getInstance().compare(o1, o2);
					}
				});
				return orderedKeys.elements();
			}
			return super.keys();
		}
	}

	private class DMMethodHashtable extends Hashtable<String, DMMethod> {
		public DMMethodHashtable() {
			super();
		}

		public DMMethodHashtable(Hashtable<String, DMMethod> ht) {
			super(ht);
		}

		@Override
		public Enumeration<String> keys() {
			if (isSerializing()) {
				// Order keys in this case
				Vector<String> orderedKeys = new Vector<String>();
				for (Enumeration<String> en = super.keys(); en.hasMoreElements();) {
					orderedKeys.add(en.nextElement());
				}
				Collections.sort(orderedKeys, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return Collator.getInstance().compare(o1, o2);
					}
				});
				return orderedKeys.elements();
			}
			return super.keys();
		}
	}

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public DMEntity(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public DMEntity(DMModel dmModel) {
		super(dmModel);
		properties = new DMPropertyHashtable();
		methods = new DMMethodHashtable();
		childEntities = new Vector<DMEntity>();
		orderedProperties = new Vector<DMProperty>();
		accessibleProperties = new Vector<DMProperty>();
		orderedMethods = new Vector<DMMethod>();
		accessibleMethods = new Vector<DMMethod>();
		_typeVariables = new Vector<DMTypeVariable>();
		_implementedTypes = new Vector<DMType>();
		_declaredTranstypers = new Vector<DMTranstyper>();
	}

	/**
	 * Constructor used for dynamic creation
	 */
	public DMEntity(DMModel dmModel, String name, String packageName, String className, DMType parentType) {
		this(dmModel);
		this.name = name;
		entityPackageName = packageName;
		entityClassName = className;
		setParentType(parentType, true);
	}

	@Override
	public String getFullyQualifiedName() {
		return getEntityPackageName() + "." + getEntityClassName();
	}

	/**
	 * Return String uniquely identifying inspector template which must be applied when trying to inspect this object
	 * 
	 * @return a String value
	 */
	@Override
	public String getInspectorName() {
		if ((getRepository() == null) || getRepository().isReadOnly()) {
			return Inspectors.DM.DM_RO_ENTITY_INSPECTOR;
		} else {
			return Inspectors.DM.DM_ENTITY_INSPECTOR;
		}
	}

	/**
	 * Return a Vector of embedded DMObjects at this level.
	 * 
	 * @return a Vector of embedded DMPackage instances
	 */
	@Override
	public Vector<DMObject> getEmbeddedDMObjects() {
		Vector<DMObject> returned = new Vector<DMObject>();
		returned.addAll(getOrderedChildren());
		return returned;
	}

	@Override
	public String getLocalizedName() {
		return getEntityClassName() + getTypesVariablesAsString();
	}

	@Override
	public String getName() {
		return name;
	}

	public final String getFullQualifiedName() {
		return ((getPackage() == null) || getPackage().isDefaultPackage() ? getEntityClassName() : getPackage().getName() + "."
				+ getEntityClassName());
	}

	@Override
	public void setName(String newName) throws InvalidNameException {
		if (!isDeserializing() && ((newName == null) || !DMRegExp.ENTITY_NAME_PATTERN.matcher(newName).matches())) {
			throw new InvalidNameException(newName + " is not a valid name.");
		}
		if ((name == null) || (!name.equals(newName))) {
			String oldName = name;
			name = newName;
			setChanged();
			notifyObservers(new DMEntityNameChanged(this, oldName, newName));
			if (getPackage() != null) {
				getPackage().notifyReordering(this);
			}
		}
	}

	@Override
	public boolean isDeletable() {
		if (this.equals(getDMModel().getWORepository().getCustomApplicationEntity())) {
			return false;
		}
		if (this.equals(getDMModel().getWORepository().getCustomSessionEntity())) {
			return false;
		}
		if (this.equals(getDMModel().getWORepository().getCustomDirectActionEntity())) {
			return false;
		}
		if (this.equals(getDMModel().getWORepository().getCustomComponentEntity())) {
			return false;
		}
		if (this.equals(getDMModel().getProcessBusinessDataRepository().getProcessBusinessDataEntity())) {
			return false;
		}
		return !isDeleted() && (getRepository() != null) && !getRepository().isReadOnly();
	}

	public String getEntityClassName() {
		return entityClassName;
	}

	public String getClassName() {
		return getEntityClassName();
	}

	/*
	 * protected void updateTypeObject(String oldClassName,String newClassName) { if(isDeserializing())return; Enumeration<Typed> en = getTypedWithThisEntity().elements(); while(en.hasMoreElements()){
	 * en.nextElement().updateTypeClassNameChange(oldClassName,newClassName); } }
	 */

	public void setEntityClassName(String newEntityClassName) throws DuplicateClassNameException, InvalidNameException {
		/*
		 * if (entityClassName == null) { entityClassName = newEntityClassName; updateTypeObject(null,newEntityClassName); return; }
		 */
		if (!isDeserializing() && ((newEntityClassName == null) || !DMRegExp.ENTITY_NAME_PATTERN.matcher(newEntityClassName).matches())) {
			logger.warning("Invalid name: " + newEntityClassName);
			throw new InvalidNameException();
		}
		if ((entityClassName == null) || (!entityClassName.equals(newEntityClassName))) {
			if (!isDeserializing() && (getDMModel().getDMEntity(getEntityPackageName(), newEntityClassName) != null)) {
				throw new DuplicateClassNameException(getEntityPackageName() + "." + newEntityClassName);
			}
			DMRepository containerRepository = getRepository();
			if ((containerRepository != null) && (entityClassName != null)) {
				containerRepository.unregisterEntity(this, false);
			}
			String oldEntityClassName = entityClassName;
			entityClassName = newEntityClassName;
			// updateTypeObject(oldEntityClassName,newEntityClassName);
			if (containerRepository != null) {
				containerRepository.registerEntity(this);
			}
			setChanged();
			notifyObservers(new DMEntityClassNameChanged(this, oldEntityClassName, newEntityClassName));
		}
	}

	public boolean isInDefaultPackage() {
		return (getEntityPackageName() != null) && getEntityPackageName().equals(getRepository().getDefaultPackage().getName())
				&& !isPrimitiveType();
	}

	public String getEntityPackageName() {
		if (entityPackageName == null) {
			if (getRepository() != null) {
				entityPackageName = getRepository().getDefaultPackage().getName();
			}
			/*
			 * else { if (logger.isLoggable(Level.WARNING)) logger.warning("Could not determine default package since repository was not set !"); }
			 */
		}
		return entityPackageName;
	}

	public void setEntityPackageName(String anEntityPackageName) {
		String oldEntityPackageName = entityPackageName;
		if ((oldEntityPackageName == null) && (anEntityPackageName == null)) {
			return;
		}
		if ((entityPackageName != null) && entityPackageName.equals(DMPackage.DEFAULT_PACKAGE_NAME) && (anEntityPackageName == null)) {
			return;
		}
		if ((oldEntityPackageName == null) || (anEntityPackageName == null) || !oldEntityPackageName.equals(anEntityPackageName)) {
			entityPackageName = anEntityPackageName;
			setChanged();
			notifyObservers(new DMAttributeDataModification("entityPackageName", oldEntityPackageName, entityPackageName));
		}
	}

	public void moveToPackage(String anEntityPackageName) {
		String oldEntityPackageName = entityPackageName;
		String oldFullyQualifiedName = getFullyQualifiedName();
		if ((oldEntityPackageName == null) && (anEntityPackageName == null)) {
			return;
		}
		if ((entityPackageName != null) && entityPackageName.equals(DMPackage.DEFAULT_PACKAGE_NAME) && (anEntityPackageName == null)) {
			return;
		}
		if ((oldEntityPackageName == null) || (anEntityPackageName == null) || !oldEntityPackageName.equals(anEntityPackageName)) {
			// logger.info("Package change from "+oldEntityPackageName+" to "+anEntityPackageName+" count="+count);
			if (!isDeserializing()) {
				getPackage().unregisterEntity(this);
				// getDMModel().unregisterEntity(this);
			}
			entityPackageName = anEntityPackageName;
			if (!isDeserializing()) {
				getPackage().registerEntity(this);
				// getDMModel().registerEntity(this);
				String newFullyQualifiedName = getFullyQualifiedName();
				getRepository().changePackage(this, oldFullyQualifiedName, newFullyQualifiedName);
			}
			setChanged();
			notifyObservers(new DMAttributeDataModification("entityPackageName", oldEntityPackageName, entityPackageName));
		}
	}

	public DMPackage getPackage() {
		if (getRepository() != null) {
			return getRepository().packageForEntity(this);
		}
		return null;
	}

	public String getPathForPackage() {
		return getPackage().getRelativePath();
	}

	@Override
	public DMType getType() {
		return getParentType();
	}

	@Override
	public void setType(DMType type) {
		setParentType(type, true);
	}

	public DMType getParentType() {
		/*
		 * if (_parentType==null && parentTypeAsString!=null) { setParentType(getDMModel().getDmTypeConverter().convertFromString(parentTypeAsString), false); parentTypeAsString = null; }
		 */
		return _parentType;
	}

	public void setParentType(DMType parentType) {
		setParentType(parentType, true);
	}

	public void setParentType(DMType parentType, boolean notify) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setParentType in " + name + " with " + (parentType == null ? "null" : parentType.getStringRepresentation()));
		}
		if (((parentType == null) && (_parentType != null)) || ((parentType != null) && !parentType.equals(_parentType))) {
			DMType oldParentType = _parentType;
			if (oldParentType != null) {
				oldParentType.removeFromTypedWithThisType(this);
				DMEntity oldParentEntity = oldParentType.getBaseEntity();
				if (oldParentEntity != null) {
					oldParentEntity.removeFromChildEntities(this);
				}
			}
			_parentType = parentType;
			if (_parentType != null) {
				_parentType.setOwner(this);
				_parentType.addToTypedWithThisType(this);
				DMEntity newParentEntity = _parentType.getBaseEntity();
				if (newParentEntity != null) {
					newParentEntity.addToChildEntities(this);
				}
			}
			if (notify) {
				setChanged();
				notifyObservers(new DMAttributeDataModification("parentType", oldParentType, parentType));
			}
		}
	}

	/*
	 * private String parentTypeAsString;
	 * 
	 * public String getParentTypeAsString() { if (getParentType()!=null) return getDMModel().getDmTypeConverter().convertToString(getParentType()); else return null; }
	 * 
	 * public void setParentTypeAsString(String parentType) { parentTypeAsString = parentType; }
	 */

	public final DMEntity getParentBaseEntity() {
		if (getParentType() != null) {
			return getParentType().getBaseEntity();
		}
		return null;
	}

	public final void setParentBaseEntity(DMEntity aParentEntity) {
		setType(DMType.makeResolvedDMType(aParentEntity));
	}

	public Vector<DMEntity> getChildEntities() {
		return childEntities;
	}

	public void addToChildEntities(DMEntity anEntity) {
		childEntities.add(anEntity);
	}

	public void removeFromChildEntities(DMEntity anEntity) {
		childEntities.remove(anEntity);
	}

	/*
	 * public Vector<Typed> getTypedWithThisEntity() { return typedWithThisEntity; }
	 * 
	 * public void addToTypedWithThisType(Typed aTyped) { if (aTyped.getType().getBaseEntity() == this) { typedWithThisEntity.add(aTyped); } else { if (logger.isLoggable(Level.WARNING))
	 * logger.warning("Type doesn' match !"); } }
	 * 
	 * public void removeFromTypedWithThisType(Typed aTyped) { typedWithThisEntity.remove(aTyped); }
	 */

	// ===========================================================
	// ===================== FlexoObserver =======================
	// ===========================================================

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if ((dataModification instanceof TypeResolved) && (((TypeResolved) dataModification).getType() == _parentType)) {
			DMEntity newParentEntity = _parentType.getBaseEntity();
			if (newParentEntity != null) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Parent type " + _parentType + " decoded for " + this);
				}
				newParentEntity.addToChildEntities(this);
			}
		} else if ((dataModification instanceof DMObjectDeleted) && (getParentType() != null)
				&& (observable == getParentType().getBaseEntity())) {
			DMEntity parent = getParentBaseEntity();
			while ((parent != null) && parent.isDeleted()) {
				parent = parent.getParentBaseEntity();
			}
			if ((parent != this) && (parent != null)) {
				setParentType(DMType.makeResolvedDMType(parent));
			} else {
				setParentType(null);
			}
		} else {
			super.update(observable, dataModification);
		}
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(CreateDMProperty.actionType);
		returned.add(CreateDMMethod.actionType);
		returned.add(CreateDMTranstyper.actionType);
		returned.add(ShowTypeHierarchyAction.actionType);
		returned.add(CreateComponentFromEntity.actionType);
		return returned;
	}

	public DMRepository getRepository() {
		return repository;
	}

	public void setRepository(DMRepository repository) {
		setRepository(repository, true);
	}

	public void setRepository(DMRepository repository, boolean notify) {
		this.repository = repository;
		if (notify) {
			setChanged();
		}
	}

	public boolean getIsReadOnly() {
		return readOnly || ((getRepository() != null) && getRepository().isReadOnly());
	}

	public void setIsReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		setChanged();
	}

	public Hashtable<String, DMMethod> getMethods() {
		return methods;
	}

	/**
	 * Return a vector of up-to-date and ordered DMMethod vector representing all methods declared for this entity
	 * 
	 * @return a Vector<DMMethod>
	 */
	public Vector<DMMethod> getDeclaredMethods() {
		return getOrderedMethods();
	}

	/**
	 * Return a vector of accessible DMMethods. An accessible mehod is defined as a method defined in this entity of somewhere in an
	 * ancestor entity.
	 * 
	 * @return a Vector of DMMethod objects
	 */
	public Vector<DMMethod> getAccessibleMethods() {
		if (methodsNeedsReordering) {
			reorderMethods();
		}
		return accessibleMethods;
	}

	public void setMethods(Hashtable<String, DMMethod> methods) {
		this.methods = new DMMethodHashtable(methods);
		setMethodsNeedsReordering();
		setChanged();
	}

	public void setMethodForKey(DMMethod method, String methodSignature) {
		if ((method._getRegisteredWithSignature() != null) && !method._getRegisteredWithSignature().equals(methodSignature)
				&& (methods.get(method._getRegisteredWithSignature()) != null)) {
			removeMethodWithKey(method._getRegisteredWithSignature());
		}
		method.setEntity(this);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Entity: " + toString() + " setMethodForKey() with " + method.getSignature());
		}
		methods.put(methodSignature, method);
		method._setRegisteredWithSignature(methodSignature);
		setMethodsNeedsReordering();
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
	}

	public void removeMethodWithKey(String methodSignature) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Entity: " + toString() + " removeMethodWithKey() with " + methodSignature);
		}
		methods.remove(methodSignature);
		setMethodsNeedsReordering();
		setChanged();
	}

	/**
	 * Register method for this entity
	 * 
	 * @param method
	 *            : the method to register
	 * @return true if method has been effectively registered, false otherwise
	 */
	public boolean registerMethod(DMMethod method) {
		return registerMethod(method, true);
	}

	/**
	 * Register method for this entity
	 * 
	 * @param method
	 *            : the method to register
	 * @param notify
	 *            : true if notification has to be forwarded
	 * @return true if method has been effectively registered, false otherwise
	 */
	public boolean registerMethod(DMMethod method, boolean notify) {
		if (methods.get(method.getSignature()) == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Register method " + method.getSignature());
			}
			setMethodForKey(method, method.getSignature());// Sets the entity
			// as well
			if (notify) {
				setChanged();
				notifyObservers(new MethodRegistered(method));
			}
			method._isRegistered = true;
			return true;
		} else if (method != methods.get(method.getSignature())) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Trying to redefine method " + method.getSignature() + ": operation not allowed !");
			}
		}
		return false;
	}

	/**
	 * Un-register method for this entity
	 * 
	 * @param method
	 *            : the method to unregister
	 * @return true if method has been effectively un-registered, false otherwise
	 */
	public boolean unregisterMethod(DMMethod method) {
		return unregisterMethod(method, true);
	}

	/**
	 * Un-register method for this entity
	 * 
	 * @param method
	 *            : the method to unregister
	 * @param notify
	 *            : true if notification has to be forwarded
	 * @return true if method has been effectively un-registered, false otherwise
	 */
	public boolean unregisterMethod(DMMethod method, boolean notify) {
		method._isRegistered = false;
		if (methods.get(method.getSignature()) != null) {
			method.setEntity(null);
			removeMethodWithKey(method.getSignature());
			if (notify) {
				setChanged();
				notifyObservers(new MethodUnregistered(method));
			}
			return true;
		}
		return false;
	}

	/**
	 * Return method whose signature matches 'methodSignature' explicitely declared in this entity
	 * 
	 * @param propertyName
	 * @return
	 */
	public DMMethod getDeclaredMethod(String methodSignature) {
		return methods.get(methodSignature);
	}

	/**
	 * Return method whose signature matches 'methodSignature' accessible from this entity. This method allows to access inherited methods
	 * from parent classes.
	 * 
	 * @param methodSignature
	 * @return
	 */
	public DMMethod getMethod(String methodSignature) {
		DMMethod returned = getDeclaredMethod(methodSignature);
		if ((returned == null) && (getParentBaseEntity() != null)) {
			return getParentBaseEntity().getMethod(methodSignature);
		} else {
			return returned;
		}
	}

	/**
	 * Return methods with name 'methodName'
	 * 
	 * @param methodSignature
	 * @return a Vector of DMMethod objects
	 */
	public Vector<DMMethod> getDeclaredMethodNamed(String methodName) {
		Vector<DMMethod> returned = new Vector<DMMethod>();
		for (Enumeration en = methods.elements(); en.hasMoreElements();) {
			DMMethod next = (DMMethod) en.nextElement();
			if (next.getName().equals(methodName)) {
				returned.add(next);
			}
		}
		return returned;
	}

	public Hashtable<String, DMProperty> getProperties() {
		return properties;
	}

	/**
	 * Return a vector of up-to-date and reordered DMProperty
	 * 
	 * @return
	 */
	public Vector<DMProperty> getOrderedSingleProperties() {
		return getOrderedProperties();
	}

	/**
	 * Return a vector of up-to-date and ordered DMProperty vector representing all properties declared for this entity
	 * 
	 * @return a Vector<DMProperty>
	 */
	public Vector<DMProperty> getDeclaredProperties() {
		return getOrderedProperties();
	}

	/**
	 * Return a vector of accessible DMProperties An accessible property is defined as a property defined in this entity of somewhere in an
	 * ancestor entity. If this entity is a DMEOProperty, it must be a class property.
	 * 
	 * @return
	 */
	public Vector<DMProperty> getAccessibleProperties() {
		if (propertiesNeedsReordering) {
			reorderProperties();
		}
		return accessibleProperties;
	}

	public void setProperties(Hashtable<String, DMProperty> properties) {
		this.properties = new DMPropertyHashtable(properties);
		setPropertiesNeedsReordering();
		setChanged();
	}

	public void setPropertyForKey(DMProperty property, String propertyName) {
		property.setEntity(this);
		properties.put(propertyName, property);
		setPropertiesNeedsReordering();
	}

	public void removePropertyWithKey(String propertyName) {
		removePropertyWithKey(propertyName, true);
	}

	public void removePropertyWithKey(String propertyName, boolean notify) {
		properties.remove(propertyName);
		if (notify) {
			setPropertiesNeedsReordering();
		}
	}

	/**
	 * Register property for this entity
	 * 
	 * @param property
	 *            : the property to register
	 * @return true if property has been effectively registered, false otherwise
	 */
	public boolean registerProperty(DMProperty property, boolean isBindable) {
		return registerProperty(property, true, isBindable);
	}

	/**
	 * Register property for this entity
	 * 
	 * @param property
	 *            : the property to register
	 * @param notify
	 *            : true if notification has to be forwarded
	 * @return true if property has been effectively registered, false otherwise
	 */
	public boolean registerProperty(DMProperty property, boolean notify, boolean isBindable) {
		if (properties.get(property.getName()) == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Register property " + property.getName());
			}
			if (this instanceof ComponentDMEntity) {
				((ComponentDMEntity) this).setPropertyForKey(property, property.getName(), isBindable);// Sets the entity
			} else {
				setPropertyForKey(property, property.getName());// Sets the entity
			}
			// as well
			if (notify) {
				setChanged();
				notifyObservers(new PropertyRegistered(property));
			}
			return true;
		} else if (property != properties.get(property.getName())) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("You are trying to redefine property " + property.getName() + "(" + property.getType() + ") "
						+ " but property " + property.getName() + "(" + properties.get(property.getName()).getType()
						+ ") is already existing");
			}
		}
		return false;
	}

	/**
	 * Un-register property for this entity
	 * 
	 * @param property
	 *            : the property to unregister
	 * @return true if property has been effectively un-registered, false otherwise
	 */
	public boolean unregisterProperty(DMProperty property) {
		return unregisterProperty(property, true);
	}

	/**
	 * Un-register property for this entity
	 * 
	 * @param property
	 *            : the property to unregister
	 * @param notify
	 *            : true if notification has to be forwarded
	 * @return true if property has been effectively un-registered, false otherwise
	 */
	public boolean unregisterProperty(DMProperty property, boolean notify) {
		if (properties.get(property.getName()) != null) {
			property.setEntity(null);
			removePropertyWithKey(property.getName(), notify);
			if (notify) {
				setChanged();
				notifyObservers(new PropertyUnregistered(property));
			}
			return true;
		}
		return false;
	}

	/**
	 * Return property named 'propertyName' explicitely declared in this entity
	 * 
	 * @param propertyName
	 * @return
	 */
	public DMProperty getDeclaredProperty(String propertyName) {
		return properties.get(propertyName);
	}

	/**
	 * Return property named 'propertyName' accessible from this entity. This method allows to access inherited properties from parent
	 * classes.
	 * 
	 * @param propertyName
	 * @return
	 */
	public DMProperty getProperty(String propertyName) {
		DMProperty returned = getDeclaredProperty(propertyName);
		if ((returned == null) && (getParentBaseEntity() != null)) {
			return getParentBaseEntity().getProperty(propertyName);
		} else {
			return returned;
		}
	}

	/**
	 * Returns boolean indicating if this entity is an ancestor of supplied entity. Returns true if entity equals this entity, or if this
	 * entity is a parent of supplied entity (parent class or parent interface)
	 * 
	 * Note that both 'extends' and 'implements' schemes are implemented by this method
	 * 
	 * @param entity
	 * @return a boolean
	 */
	public boolean isAncestorOf(DMEntity entity) {
		if (entity == null) {
			return false;
		}
		if (entity == this) {
			return true;
		}
		if ((this == getDMModel().getDMEntity(Object.class))/* && (!(entity instanceof JDKPrimitive)) */) {
			return true;
		}
		if (entity.getParentBaseEntity() != null) {
			if (isAncestorOf(entity.getParentBaseEntity())) {
				return true;
			}
		}
		for (DMType implementedType : entity.getImplementedTypes()) {
			if (isAncestorOf(implementedType.getBaseEntity())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns most direct ancestor of supplied entity, on it's way to this type as ancestor, assuming this type is an ancestor of supplied
	 * entity
	 * 
	 * @param entity
	 * @return
	 */
	public DMType getClosestAncestorOf(DMEntity entity) {
		if (entity == null) {
			return null;
		}
		if (entity == this) {
			return DMType.makeResolvedDMType(entity);
		}
		if ((this == getDMModel().getDMEntity(Object.class))/* && (!(entity instanceof JDKPrimitive)) */) {
			return DMType.makeResolvedDMType(getDMModel().getDMEntity(Object.class));
		}
		;
		if (entity.getParentBaseEntity() != null) {
			if (isAncestorOf(entity.getParentBaseEntity())) {
				return entity.getParentType();
			}
		}
		for (DMType implementedType : entity.getImplementedTypes()) {
			if (isAncestorOf(implementedType.getBaseEntity())) {
				return implementedType;
			}
		}
		return null;
	}

	public boolean isDMEOEntity() {
		return this instanceof DMEOEntity;
	}

	public boolean getIsEnumeration() {
		return _isEnumeration;
	}

	public void setIsEnumeration(boolean isEnumeration) {
		if (_isEnumeration != isEnumeration) {
			_isEnumeration = isEnumeration;
			setChanged();
			notifyObservers(new DMAttributeDataModification("isEnumeration", !isEnumeration, isEnumeration));
		}
	}

	public boolean getIsInterface() {
		return _isInterface;
	}

	public void setIsInterface(boolean isInterface) {
		if (_isInterface != isInterface) {
			_isInterface = isInterface;
			setChanged();
			notifyObservers(new DMAttributeDataModification("isInterface", !isInterface, isInterface));
		}
	}

	public boolean getIsNormalClass() {
		return !getIsEnumeration() && !getIsInterface();
	}

	@Override
	public void delete() {
		Vector<DMProperty> propertiesToDelete = new Vector<DMProperty>();
		propertiesToDelete.addAll(getOrderedProperties());
		for (Enumeration en = propertiesToDelete.elements(); en.hasMoreElements();) {
			DMProperty next = (DMProperty) en.nextElement();
			next.delete();
		}
		DMPackage package1 = getPackage();
		if (getRepository() != null) {
			getRepository().unregisterEntity(this);
		}
		if (package1 != null) {
			package1.unregisterEntity(this);
			// // Handles 'parent' relations
			// Vector<DMEntity> entitiesToUpdate = new Vector<DMEntity>();
			// entitiesToUpdate.addAll(getChildEntities());
			// for (Enumeration en = entitiesToUpdate.elements(); en.hasMoreElements();) {
			// DMEntity next = (DMEntity) en.nextElement();
			// next.setParentType(null, true);
			// if (logger.isLoggable(Level.FINE))
			// logger.fine("Sets " + next + " to have null parent");
			// }
		}

		// Handles 'typedWithThisType' relations
		/*
		 * Vector<Typed> propertiesToUpdate = new Vector<Typed>(); propertiesToUpdate.addAll(getTypedWithThisEntity()); for (Enumeration en = propertiesToUpdate.elements(); en.hasMoreElements();) {
		 * Typed next = (Typed) en.nextElement(); next.setType(null); if (logger.isLoggable(Level.FINE)) logger.fine("Sets " + next + " to have null type"); }
		 */

		super.delete();
		setChanged();
		notifyObservers(new DMObjectDeleted<DMEntity>(this));
		deleteObservers();
		name = null;
		entityPackageName = null;
		entityClassName = null;
		_parentType = null;
		repository = null;
	}

	public DMProperty getDMProperty(String aPropertyName) {
		return getProperties().get(aPropertyName);
	}

	public String getStringRepresentation() {
		String returned = "Name = " + getName() + " inherits from " + (getParentType() != null ? getParentType().getName() : "none") + "\n";
		for (Enumeration e = properties.elements(); e.hasMoreElements();) {
			DMProperty property = (DMProperty) e.nextElement();
			returned += "    " + property.getStringRepresentation() + "\n";
		}
		return returned;
	}

	private Vector<DMProperty> orderedProperties;

	private Vector<DMProperty> accessibleProperties;

	protected boolean propertiesNeedsReordering = true;

	private Vector<DMMethod> orderedMethods;

	private Vector<DMMethod> accessibleMethods;

	protected boolean methodsNeedsReordering = true;

	private Vector<DMObject> orderedChildren;

	public void setPropertiesNeedsReordering() {
		propertiesNeedsReordering = true;
		for (Enumeration en = getChildEntities().elements(); en.hasMoreElements();) {
			((DMEntity) en.nextElement()).setPropertiesNeedsReordering();
		}
		orderedChildren = null;
		setChanged();
		notifyObservers(new PropertiesReordered(this));

	}

	private void setMethodsNeedsReordering() {
		methodsNeedsReordering = true;
		for (Enumeration en = getChildEntities().elements(); en.hasMoreElements();) {
			((DMEntity) en.nextElement()).setMethodsNeedsReordering();
		}
		orderedChildren = null;
	}

	/**
	 * Return a vector of up-to-date and reordered DMProperty
	 * 
	 * @return
	 */
	public synchronized Vector<DMProperty> getOrderedProperties() {
		if (propertiesNeedsReordering) {
			reorderProperties();
		}
		return orderedProperties;
	}

	/**
	 * Return a vector of up-to-date and reordered DMMethod
	 * 
	 * @return a Vector of DMMethod objects
	 */
	public synchronized Vector<DMMethod> getOrderedMethods() {
		if (methodsNeedsReordering) {
			reorderMethods();
		}
		return orderedMethods;
	}

	@Override
	public synchronized Vector<DMObject> getOrderedChildren() {
		if (orderedChildren == null) {
			orderedChildren = new Vector<DMObject>();
			orderedChildren.addAll(getOrderedProperties());
			orderedChildren.addAll(getOrderedMethods());
			orderedChildren.addAll(getDeclaredTranstypers());
		}
		return orderedChildren;
	}

	public synchronized DMProperty getOrderedPropertyNamed(String name) {
		Enumeration en = getOrderedSingleProperties().elements();
		while (en.hasMoreElements()) {
			DMProperty p = (DMProperty) en.nextElement();
			if (p.name.equals(name)) {
				return p;
			}
		}
		return null;
	}

	public synchronized DMMethod getOrderedMethodNamed(String name) {
		Enumeration en = getOrderedMethods().elements();
		while (en.hasMoreElements()) {
			DMMethod m = (DMMethod) en.nextElement();
			if (m.name.equals(name)) {
				return m;
			}
		}
		return null;
	}

	protected synchronized void reorderMethods() {
		if (methodsNeedsReordering) {
			if (orderedMethods != null) {
				orderedMethods.removeAllElements();
			} else {
				orderedMethods = new Vector<DMMethod>();
			}
			orderedMethods.addAll(getMethods().values());
			Collections.sort(orderedMethods, methodComparator);
			if (accessibleMethods != null) {
				accessibleMethods.removeAllElements();
			} else {
				accessibleMethods = new Vector<DMMethod>();
			}
			for (Enumeration en = getMethods().elements(); en.hasMoreElements();) {
				DMMethod method = (DMMethod) en.nextElement();
				accessibleMethods.add(method);
			}
			DMEntity currentParent = getParentBaseEntity();
			while (currentParent != null) {
				for (Enumeration en = currentParent.getAccessibleMethods().elements(); en.hasMoreElements();) {
					DMMethod method = (DMMethod) en.nextElement();
					boolean isOverriding = false;
					for (int i = 0; ((i < accessibleMethods.size()) && (!isOverriding)); i++) {
						DMMethod temp = accessibleMethods.elementAt(i);
						if (temp.overrides(method)) {
							// if (logger.isLoggable(Level.INFO)) logger.info
							// ("Property "+temp+" overrides "+property);
							isOverriding = true;
						}
					}
					if (!isOverriding) {
						accessibleMethods.add(method);
					}
				}
				currentParent = currentParent.getParentBaseEntity();
			}
			Collections.sort(accessibleMethods, methodComparator);
			methodsNeedsReordering = false;
		}
	}

	protected synchronized void reorderProperties() {
		if (propertiesNeedsReordering) {
			if (orderedProperties != null) {
				orderedProperties.removeAllElements();
			} else {
				orderedProperties = new Vector<DMProperty>();
			}
			orderedProperties.addAll(getProperties().values());
			Collections.sort(orderedProperties, propertyComparator);
			if (accessibleProperties != null) {
				accessibleProperties.removeAllElements();
			} else {
				accessibleProperties = new Vector<DMProperty>();
			}
			for (Enumeration en = getProperties().elements(); en.hasMoreElements();) {
				DMProperty property = (DMProperty) en.nextElement();
				if (property instanceof DMEOProperty) {
					if (((DMEOProperty) property).getIsClassProperty()) {
						accessibleProperties.add(property);
					}
				} else {
					accessibleProperties.add(property);
				}
			}
			DMEntity currentParent = getParentBaseEntity();
			while (currentParent != null) {
				for (Enumeration en = currentParent.getAccessibleProperties().elements(); en.hasMoreElements();) {
					DMProperty property = (DMProperty) en.nextElement();
					boolean isOverriding = false;
					for (int i = 0; ((i < accessibleProperties.size()) && (!isOverriding)); i++) {
						DMProperty temp = accessibleProperties.elementAt(i);
						if (temp.overrides(property)) {
							// if (logger.isLoggable(Level.INFO)) logger.info
							// ("Property "+temp+" overrides "+property);
							isOverriding = true;
						}
					}
					if (!isOverriding) {
						accessibleProperties.add(property);
					}
				}
				currentParent = currentParent.getParentBaseEntity();
			}
			Collections.sort(accessibleProperties, propertyComparator);
			propertiesNeedsReordering = false;
		}
	}

	protected static final PropertyComparator propertyComparator = new PropertyComparator();

	/**
	 * Used to sort properties according to name alphabetic ordering
	 * 
	 * @author sguerin
	 * 
	 */
	protected static class PropertyComparator implements Comparator<DMProperty> {

		/**
		 * Implements
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(DMProperty o1, DMProperty o2) {
			String s1 = o1.getName();
			String s2 = o2.getName();
			if ((s1 != null) && (s2 != null)) {
				return Collator.getInstance().compare(s1, s2);
			} else {
				return 0;
			}
		}

	}

	protected static final MethodComparator methodComparator = new MethodComparator();

	/**
	 * Used to sort methods according to signature alphabetic ordering
	 * 
	 * @author sguerin
	 * 
	 */
	protected static class MethodComparator implements Comparator<DMMethod> {

		/**
		 * Implements
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(DMMethod o1, DMMethod o2) {

			String s1 = o1.getSignature();
			String s2 = o2.getSignature();
			if ((s1 != null) && (s2 != null)) {
				return Collator.getInstance().compare(s1, s2);
			} else {
				return 0;
			}
		}

	}

	// ==========================================================================
	// ======================== TreeNode implementation
	// =========================
	// ==========================================================================

	@Override
	public TreeNode getParent() {
		return getPackage();
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public void notifyReordering(DMObject cause) {
		propertiesNeedsReordering = true;
		methodsNeedsReordering = true;
		super.notifyReordering(cause);
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "dm_entity";
	}

	public DMProperty createDMProperty(String name, DMType type, DMPropertyImplementationType implementationType) {
		DMProperty newProperty = new DMProperty(getProject().getDataModel(), name, type, DMCardinality.SINGLE, false, true,
				implementationType);
		registerProperty(newProperty, true);
		return newProperty;
	}

	/*
	 * public void createBindingVariablesFromEntity(Bindable bindable, BindingModel bdmodel){ Enumeration<DMProperty> en = getProperties().elements(); DMProperty p = null; while(en.hasMoreElements()){
	 * p = en.nextElement(); BindingVariable newBV = new BindingVariable(bindable, getDMModel(), ""); newBV.setType(p.getType()); newBV.setVariableName(p.getName());
	 * bdmodel.addToBindingVariables(newBV); } }
	 */

	/**
	 * This date is use to perform fine tuning resource dependancies computing
	 * 
	 * @return
	 */
	@Override
	public Date getLastUpdate() {
		if (_lastUpdate == null) {
			_lastUpdate = lastMemoryUpdate();
		}
		if (_lastUpdate == null) {
			_lastUpdate = super.getLastUpdate();
		}
		return _lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		_lastUpdate = lastUpdate;
	}

	@Override
	public void setIsModified() {
		if (ignoreNotifications()) {
			return;
		}
		_lastUpdate = null;
		super.setIsModified();
		// Do this to reset dependant resources cache, in order to get up-to_date
		// needsGeneration information on generated resources
		if ((getXMLResourceData() != null) && (getXMLResourceData().getFlexoResource() != null)) {
			getXMLResourceData().getFlexoResource().notifyResourceStatusChanged();
		}
	}

	public boolean hasCreatorProperty() {
		return getProperty("creator") != null;
	}

	public Collection<DMProperty> getPropertiesExcludingEOTypes() {
		Vector<DMProperty> reply = new Vector<DMProperty>();
		Enumeration<DMProperty> en = getProperties().elements();
		DMProperty p = null;
		while (en.hasMoreElements()) {
			p = en.nextElement();
			if ((p.getType() != null) && !(p.getType().getBaseEntity() instanceof DMEOEntity)) {
				reply.add(p);
			}
		}
		return reply;
	}

	public Collection<DMProperty> getPropertiesEOTypesOnly() {
		Vector<DMProperty> reply = new Vector<DMProperty>();
		Enumeration<DMProperty> en = getProperties().elements();
		DMProperty p = null;
		while (en.hasMoreElements()) {
			p = en.nextElement();
			if ((p.getType() != null) && (p.getType().getBaseEntity() instanceof DMEOEntity)) {
				reply.add(p);
			}
		}
		return reply;
	}

	public boolean isVoid() {
		return (this == getDMModel().getEntityNamed("void"));
	}

	public String getNiceRelationshipNameToMe() {
		StringBuffer reply = new StringBuffer();
		boolean isPrefixRemoved = false;
		for (int i = 0; i < getName().length(); i++) {
			if (!isPrefixRemoved && (i + 1 < getName().length())) {
				if (Character.isUpperCase(getName().charAt(i + 1))) {
					// switch this char
				} else {
					isPrefixRemoved = true;
					reply.append(Character.toLowerCase(getName().charAt(i)));
				}
			} else {
				reply.append(getName().charAt(i));
			}
		}
		return reply.toString();
	}

	public boolean isBusinessDataClass() {
		if (getProject().getAllLocalFlexoProcesses() != null) {
			Enumeration<FlexoProcess> en = getProject().getAllLocalFlexoProcesses().elements();
			while (en.hasMoreElements()) {
				if (this.equals(en.nextElement().getBusinessDataType())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isPrimitiveType() {
		return getPackage().isDefaultPackage() && (getRepository() == getDMModel().getJDKRepository());
	}

	public boolean isBooleanType() {
		return getName().toLowerCase().equals("boolean");
	}

	/**
	 * Overrides isNameValid
	 * 
	 * @see org.openflexo.foundation.dm.DMObject#isNameValid()
	 */
	@Override
	public boolean isNameValid() {
		return (getName() != null) && getName().matches(IERegExp.JAVA_CLASS_NAME_REGEXP);
	}

	// ===================================================================
	// ==================== Type variables management ====================
	// ===================================================================

	@Override
	public Vector<DMTypeVariable> getTypeVariables() {
		return _typeVariables;
	}

	public void setTypeVariables(Vector<DMTypeVariable> typeVariables) {
		_typeVariables = typeVariables;
		setChanged();
	}

	public void addToTypeVariables(DMTypeVariable typeVariable) {
		_typeVariables.add(typeVariable);
		typeVariable.setEntity(this);
		setChanged();
	}

	public void removeFromTypeVariables(DMTypeVariable typeVariable) {
		_typeVariables.remove(typeVariable);
		setChanged();
	}

	public DMTypeVariable createNewTypeVariable() throws DuplicateMethodSignatureException {
		DMTypeVariable newTypeVariable = new DMTypeVariable(getDMModel(), this);
		newTypeVariable.setName("X");
		newTypeVariable.setBounds("");
		newTypeVariable.setDescription(FlexoLocalization.localizedForKey("no_description"));
		addToTypeVariables(newTypeVariable);
		return newTypeVariable;
	}

	public void deleteTypeVariable(DMTypeVariable typeVariable) throws DuplicateMethodSignatureException {
		removeFromTypeVariables(typeVariable);
	}

	public boolean isTypeVariableAddable(DMTypeVariable typeVariable) {
		return (!getIsReadOnly());
	}

	public boolean isTypeVariableDeletable(DMTypeVariable typeVariable) {
		return (!getIsReadOnly());
	}

	public String getTypesVariablesAsString() {
		if (getTypeVariables().size() == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<");
		boolean isFirst = true;
		for (DMTypeVariable tv : getTypeVariables()) {
			sb.append((isFirst ? "" : ",") + tv.getName());
			isFirst = false;
		}
		sb.append(">");
		return sb.toString();
	}

	public static class DMTypeVariable extends DMObject {

		@SuppressWarnings("hiding")
		private static final Logger logger = Logger.getLogger(DMEntity.DMTypeVariable.class.getPackage().getName());

		private DMEntity _entity;
		private String _name;
		private String _bounds;

		/**
		 * Constructor used during deserialization
		 */
		public DMTypeVariable(FlexoDMBuilder builder) {
			this(builder.dmModel);
			initializeDeserialization(builder);
		}

		/**
		 * Default constructor
		 */
		public DMTypeVariable(DMModel dmModel) {
			super(dmModel);
		}

		/**
		 * Constructor used for dynamic creation
		 */
		public DMTypeVariable(DMModel dmModel, DMEntity entity) {
			this(dmModel);
			this._entity = entity;
		}

		public void update(DMTypeVariable typeVariable) {
			// Name is supposed to be the same, but check anyway
			if (!getName().equals(typeVariable.getName())) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Update name");
				}
				_name = typeVariable.getName();
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Name is up-to-date");
				}
			}

			// Description
			if (((getDescription() == null) && (typeVariable.getDescription() != null))
					|| (!getDescription().equals(typeVariable.getDescription()))) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Update description");
				}
				setDescription(typeVariable.getDescription());
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Description is up-to-date");
				}
			}
		}

		@Override
		public String getName() {
			return _name;
		}

		@Override
		public void setName(String name) {
			_name = name;
		}

		@Override
		public boolean isDeletable() {
			return _entity.isDeletable();
		}

		public DMEntity getEntity() {
			return _entity;
		}

		public void setEntity(DMEntity entity) {
			_entity = entity;
		}

		@Override
		public String getFullyQualifiedName() {
			if (getEntity() != null) {
				return getEntity().getFullyQualifiedName() + ".TV." + getName();
			}
			return "NULL." + getName();
		}

		@Override
		public Vector<? extends DMObject> getOrderedChildren() {
			return EMPTY_VECTOR;
		}

		@Override
		public TreeNode getParent() {
			return getEntity();
		}

		@Override
		public boolean getAllowsChildren() {
			return false;
		}

		/**
		 * Return null since parameter is never inspected by its own
		 * 
		 * @return null
		 */
		@Override
		public String getInspectorName() {
			return null;
		}

		/**
		 * Return a Vector of embedded DMObjects at this level.
		 * 
		 * @return null
		 */
		@Override
		public Vector<DMObject> getEmbeddedDMObjects() {
			return EMPTY_VECTOR;
		}

		/**
		 * Overrides getClassNameKey
		 * 
		 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
		 */
		@Override
		public String getClassNameKey() {
			return "dm_type_variable";
		}

		public String getBounds() {
			return _bounds;
		}

		public void setBounds(String bounds) {
			_bounds = bounds;
		}
	}

	// ===================================================================
	// =============== Implemented types management ======================
	// ===================================================================

	public Vector<DMType> getImplementedTypes() {
		return _implementedTypes;
	}

	public void setImplementedTypes(Vector<DMType> implementedTypes) {
		_implementedTypes = implementedTypes;
		setChanged();
	}

	public void addToImplementedTypes(DMType aType) {
		if (aType == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Attempt to add null type");
			}
			return;
		}
		_implementedTypes.add(aType);
		aType.setOwner(this);
		setChanged();
	}

	public void removeFromImplementedTypes(DMType aType) {
		_implementedTypes.remove(aType);
		aType.setOwner(null);
		setChanged();
	}

	public DMType createNewImplementedType() throws DuplicateMethodSignatureException {
		DMType newImplementedType = DMType.makeResolvedDMType(getDMModel().getDMEntity(Object.class));
		addToImplementedTypes(newImplementedType);
		return newImplementedType;
	}

	public void deleteImplementedType(DMType type) throws DuplicateMethodSignatureException {
		removeFromImplementedTypes(type);
	}

	public boolean isImplementedTypeAddable(DMType type) {
		return (!getIsReadOnly());
	}

	public boolean isImplementedTypeDeletable(DMType type) {
		return (!getIsReadOnly());
	}

	// Used while serializing
	public String _getSerializedImplementedTypes() {
		if (getImplementedTypes().size() == 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		boolean isFirst = true;
		for (DMType t : getImplementedTypes()) {
			sb.append((isFirst ? "" : ",") + t.getStringRepresentation());
			isFirst = false;
		}
		return sb.toString();
	}

	// Used while deserializing
	public void _setSerializedImplementedTypes(String aString) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Trying to decode [" + aString + "]");
		}
		DMTypeTokenizer tt = new DMTypeTokenizer(aString);
		while (tt.hasMoreTokens()) {
			String next = tt.nextToken();
			DMType type = getDMModel().getDmTypeConverter().convertFromString(next, this, getProject());
			addToImplementedTypes(type);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Decoded " + type.getStringRepresentation());
			}
		}

	}

	public Vector<DMTranstyper> getDeclaredTranstypers() {
		return _declaredTranstypers;
	}

	public void setDeclaredTranstypers(Vector<DMTranstyper> declaredTranstypers) {
		for (DMTranstyper tt : declaredTranstypers) {
			addToDeclaredTranstypers(tt);
		}
	}

	public void addToDeclaredTranstypers(DMTranstyper aTranstyper) {
		aTranstyper.setDeclaringEntity(this);
		_declaredTranstypers.add(aTranstyper);
		orderedChildren = null;
		if (getDMModel() != null) {
			getDMModel().addToDeclaredTranstypers(aTranstyper);
		}
		setChanged();
		notifyObservers(new TranstyperRegistered(aTranstyper));
	}

	public void removeFromDeclaredTranstypers(DMTranstyper aTranstyper) {
		aTranstyper.setDeclaringEntity(null);
		_declaredTranstypers.remove(aTranstyper);
		orderedChildren = null;
		if (getDMModel() != null) {
			getDMModel().removeFromDeclaredTranstypers(aTranstyper);
		}
		setChanged();
		notifyObservers(new TranstyperUnregistered(aTranstyper));
	}

	public DMTranstyper getDMTranstyper(String aTranstyperName) {
		for (DMTranstyper tt : getDeclaredTranstypers()) {
			if (tt.getName().equals(aTranstyperName)) {
				return tt;
			}
			if (tt.getJavaMethodName().equals(aTranstyperName)) {
				return tt;
			}
		}
		return null;
	}

	private boolean mustBeImported(String className) {
		if (className.indexOf(".") == -1) {
			return false;
		}
		if (className.startsWith("default_package")) {
			return false;
		}
		if (className.startsWith("java.lang")) {
			return false;
		}
		return (getPackage() == null) || getPackage().isDefaultPackage()
				|| !getPackage().getName().equals(className.substring(0, className.lastIndexOf('.')));
	}

	private void appendParameters(List<DMType> types, List<String> imports) {
		if (types.size() == 0) {
			return;
		}
		for (DMType t : types) {
			appendToImports(t, imports);
			appendParameters(t.getParameters(), imports);
		}
	}

	private void appendToImports(DMType t, List<String> imports) {
		if (t == null) {
			return;
		}
		if (t.getBaseEntity() == null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Cannot resolve type : " + t);
			}
			return;
		}
		if (t.getBaseEntity().getEntityClassName() == null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("No className for type : " + t);
			}
			return;
		}
		String candidate = t.getBaseEntity().getFullQualifiedName();
		if (mustBeImported(candidate) && !imports.contains(candidate)) {
			imports.add(candidate);
		}
	}

	public List<String> getAllNeededImports() {
		Vector<String> reply = new Vector<String>();
		if (getParentType() != null) {
			appendToImports(getParentType(), reply);
			appendParameters(getParentType().getParameters(), reply);
		}
		for (DMProperty p : getProperties().values()) {
			appendToImports(p.getType(), reply);
			if (p.getType() != null) {
				appendParameters(p.getType().getParameters(), reply);
			}
		}
		for (DMMethod m : getMethods().values()) {
			if (m.getReturnType() != null) {
				appendToImports(m.getReturnType(), reply);
				appendParameters(m.getReturnType().getParameters(), reply);
			}
			for (DMMethodParameter param : m.getParameters()) {
				appendToImports(param.getType(), reply);
				if (param.getType() != null) {
					appendParameters(param.getType().getParameters(), reply);
				}
			}
		}
		return reply;
	}

	/**
	 * Tells if code generation is applicable for related DMEntity
	 * 
	 * @return
	 */
	public boolean isCodeGenerationApplicable() {
		return true;
	}

	public static class EntityInPackageCannotUseEntityOfTheDefaultPackage extends
			ValidationRule<EntityInPackageCannotUseEntityOfTheDefaultPackage, DMEntity> {

		public EntityInPackageCannotUseEntityOfTheDefaultPackage() {
			super(DMEntity.class, "entity_in_package_cannot_use_entity_of_the_default_package");
		}

		private void addToVectorIfInDefaultPackage(DMType type, Vector<DMEntity> v) {
			if (type == null) {
				return;
			}
			if (type.getBaseEntity() == null) {
				return;
			}
			if (type.getBaseEntity().isInDefaultPackage() && !v.contains(type.getBaseEntity())) {
				v.add(type.getBaseEntity());
			}
			for (DMType t : type.getParameters()) {
				addToVectorIfInDefaultPackage(t, v);
			}
		}

		@Override
		public ValidationIssue<EntityInPackageCannotUseEntityOfTheDefaultPackage, DMEntity> applyValidation(DMEntity entity) {
			if (entity.isInDefaultPackage()) {
				return null;
			}
			Vector<DMEntity> v = new Vector<DMEntity>();
			addToVectorIfInDefaultPackage(entity.getParentType(), v);
			for (DMProperty p : entity.getProperties().values()) {
				addToVectorIfInDefaultPackage(p.getType(), v);
			}
			for (DMMethod m : entity.getMethods().values()) {
				if (m.getReturnType() != null) {
					addToVectorIfInDefaultPackage(m.getReturnType(), v);
				}
				for (DMMethodParameter param : m.getParameters()) {
					addToVectorIfInDefaultPackage(param.getType(), v);
				}
			}
			if (v.size() > 0) {
				StringBuilder sb = new StringBuilder(
						FlexoLocalization.localizedForKey("the_following_entities_are_in_the_default_package_and_cannot_be_used_by") + " "
								+ entity.getFullQualifiedName()).append(": ");
				for (DMEntity e : v) {
					sb.append(e.getName()).append(" ");
				}
				ValidationError<EntityInPackageCannotUseEntityOfTheDefaultPackage, DMEntity> vr = new ValidationError<EntityInPackageCannotUseEntityOfTheDefaultPackage, DMEntity>(
						this, entity, sb.toString());
				vr.setLocalized(true);
				return vr;
			}
			return null;
		}

	}

	public DMPropertyImplementationType getPropertyDefaultImplementationType() {
		return DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD;
	}

}
