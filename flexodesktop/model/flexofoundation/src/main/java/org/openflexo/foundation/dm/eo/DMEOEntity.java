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
package org.openflexo.foundation.dm.eo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.swing.tree.TreeNode;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMRegExp;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DuplicateClassNameException;
import org.openflexo.foundation.dm.DuplicateMethodSignatureException;
import org.openflexo.foundation.dm.action.CreateDMEOAttribute;
import org.openflexo.foundation.dm.action.CreateDMEORelationship;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.dm.DMEntityClassNameChanged;
import org.openflexo.foundation.dm.dm.DMEntityNameChanged;
import org.openflexo.foundation.dm.eo.model.EOAttribute;
import org.openflexo.foundation.dm.eo.model.EOEntity;
import org.openflexo.foundation.dm.eo.model.EOProperty;
import org.openflexo.foundation.dm.eo.model.EORelationship;
import org.openflexo.foundation.dm.javaparser.ClassSourceCode;
import org.openflexo.foundation.dm.javaparser.ParsedJavaClass;
import org.openflexo.foundation.dm.javaparser.ParserNotInstalledException;
import org.openflexo.foundation.dm.javaparser.SourceCodeOwner;
import org.openflexo.foundation.stats.DMEOEntityStatistics;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a class and its definition for objects stored in database
 * 
 * @author sguerin
 * 
 */
public class DMEOEntity extends DMEntity implements DMEOObject, SourceCodeOwner {

	private static final Logger logger = Logger.getLogger(DMEOEntity.class.getPackage().getName());

	public static final String ENTITY_CLASS_NAME_KEY = "entityClassName";

	protected EOEntity _eoEntity;

	protected EOEntity _parentEOEntity;

	private DMEOModel _dmEOModel;

	private Vector<DMEOAttribute> _primaryKeyAttributes;

	private boolean _primaryKeyAttributesNeedsRecomputing;

	private Vector<DMProperty> _classProperties;

	private boolean _classPropertiesNeedsRecomputing;

	private Vector<DMEOAttribute> _attributesUsedForLocking;

	private boolean _attributesUsedForLockingNeedsRecomputing;

	private Hashtable<EOAttribute, DMEOAttribute> _attributesForEOAttributes;

	private Hashtable<EORelationship, DMEORelationship> _relationshipsForEORelationships;

	private DMEOEntityStatistics statistics;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public DMEOEntity(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public DMEOEntity(DMModel dmModel) {
		super(dmModel);
		_attributesForEOAttributes = new Hashtable<EOAttribute, DMEOAttribute>();
		_relationshipsForEORelationships = new Hashtable<EORelationship, DMEORelationship>();
		_orderedAttributes = new Vector<DMEOAttribute>();
		_orderedRelationships = new Vector<DMEORelationship>();
		_primaryKeyAttributes = new Vector<DMEOAttribute>();
		_primaryKeyAttributesNeedsRecomputing = true;
		_classProperties = new Vector<DMProperty>();
		_classPropertiesNeedsRecomputing = true;
		_attributesUsedForLocking = new Vector<DMEOAttribute>();
		_attributesUsedForLockingNeedsRecomputing = true;
	}

	/**
	 * Constructor for dynamic creation
	 */
	public DMEOEntity(DMModel dmModel, DMEOModel dmEOModel, EOEntity eoEntity) throws EOAccessException {
		this(dmModel, dmEOModel, eoEntity, false);
	}

	/**
	 * Constructor for dynamic creation
	 */
	public DMEOEntity(DMModel dmModel, DMEOModel dmEOModel, EOEntity eoEntity, boolean isPrototypeEntity) throws EOAccessException {
		this(dmModel);
		_dmEOModel = dmEOModel;
		_eoEntity = eoEntity;
		if (eoEntity != null) {
			name = eoEntity.getName();
			entityPackageName = null;
			StringTokenizer st = new StringTokenizer(eoEntity.getClassName(), ".");
			while (st.hasMoreTokens()) {
				String nextToken = st.nextToken();
				if (st.hasMoreTokens()) {
					if (entityPackageName == null) {
						entityPackageName = nextToken;
					} else {
						entityPackageName += "." + nextToken;
					}
				} else {
					entityClassName = nextToken;
				}
			}
			_parentEOEntity = eoEntity.getParentEntity();

			updateFromEOEntity(eoEntity, isPrototypeEntity);
		}
	}

	/**
	 * Constructor used for dynamic creation
	 */
	public static DMEOEntity createsNewDMEOEntity(DMModel dmModel, String name, DMEOModel dmEOModel, String className)
			throws EOAccessException {
		EOEntity newEOEntity = new EOEntity();
		newEOEntity.setName(name);
		newEOEntity.setClassName(dmEOModel.derivePackageName() + "." + className);
		newEOEntity.setExternalName(ToolBox.convertJavaStringToDBName(name));
		try {
			dmEOModel.getEOModel().addEntity(newEOEntity);
		} catch (IllegalArgumentException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("EOAccess management failed while trying to creates entity " + name + " : " + e.getMessage());
			}
			throw new EOAccessException(e);
		}
		return new DMEOEntity(dmModel, dmEOModel, newEOEntity);
	}

	public void updateFromEOEntity() throws EOAccessException {
		if (getEOEntity() != null) {
			updateFromEOEntity(getEOEntity(), isPrototypeEntity());
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("updateFromEOEntity() FAILED because EOEntity is not retrievable !");
			}
		}
	}

	private void updateFromEOEntity(EOEntity eoEntity, boolean isPrototypeEntity) throws EOAccessException {
		if (eoEntity != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateFromEOEntity()");
			}

			updateAttributesFromEOEntity(eoEntity, isPrototypeEntity);
			if (!isPrototypeEntity) {
				updateRelationshipsFromEOEntity(eoEntity);
			}
			// May be there are some more dereferenced attributes or
			// relationship
			// (objects with no reference to a EOObject): remove them
			Vector<DMProperty> propertiesToDelete = new Vector<DMProperty>();
			for (Enumeration en = getProperties().elements(); en.hasMoreElements();) {
				DMProperty next = (DMProperty) en.nextElement();
				if ((next instanceof DMEOAttribute) && (((DMEOAttribute) next).getEOAttribute() == null)) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Delete dereferenced attribute " + next.getName());
					}
					propertiesToDelete.add(next);
				}
				if ((next instanceof DMEORelationship) && (((DMEORelationship) next).getEORelationship() == null)) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Delete dereferenced relationship " + next.getName());
					}
					propertiesToDelete.add(next);
				}
			}
			for (Enumeration en = propertiesToDelete.elements(); en.hasMoreElements();) {
				DMProperty toDelete = (DMProperty) en.nextElement();
				toDelete.delete();
			}
			_primaryKeyAttributesNeedsRecomputing = true;
			_classPropertiesNeedsRecomputing = true;
			_attributesUsedForLockingNeedsRecomputing = true;
		}

	}

	private void updateAttributesFromEOEntity(EOEntity eoEntity, boolean isPrototypeEntity) throws EOAccessException {
		if (eoEntity != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateAttributesFromEOEntity()");
			}

			try {
				Vector<DMProperty> attributesToDelete = new Vector<DMProperty>();
				attributesToDelete.addAll(getOrderedAttributes());

				for (Iterator<EOAttribute> i = new Vector<EOAttribute>(eoEntity.getAttributes()).iterator(); i.hasNext();) {

					EOAttribute eoAttribute = i.next();

					DMEOAttribute foundAttribute = lookupDMEOAttributeWithName(eoAttribute.getName());
					if ((foundAttribute != null) && (foundAttribute.getDMEOEntity() != this)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Lookup dereferenced EOAttribute " + foundAttribute.getName() + "! Trying to repair...");
						}
						foundAttribute.delete();
						foundAttribute = null;
					}
					if (foundAttribute == null) {
						if (!isPrototypeEntity) {
							if (logger.isLoggable(Level.FINE)) {
								logger.fine("Found NEW EOAttribute " + eoAttribute.getName() + ". Creates the related DMEOAttribute.");
							}
							DMEOAttribute newDMEOAttribute = new DMEOAttribute(getDMModel(), eoAttribute);
							registerProperty(newDMEOAttribute);
						} else {
							if (logger.isLoggable(Level.FINE)) {
								logger.fine("Found NEW EOAttribute " + eoAttribute.getName() + ". Creates the related DMEOPrototype.");
							}
							DMEOPrototype newDMEOPrototype = new DMEOPrototype(getDMModel(), eoAttribute);
							registerProperty(newDMEOPrototype);
						}
					} else {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Lookup EOAttribute " + foundAttribute.getName());
						}
						unregisterProperty(foundAttribute);
						foundAttribute.setEOAttribute(eoAttribute);
						registerProperty(foundAttribute);
						attributesToDelete.remove(foundAttribute);
					}
				}

				for (Enumeration en = attributesToDelete.elements(); en.hasMoreElements();) {
					DMEOAttribute toDelete = (DMEOAttribute) en.nextElement();
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Delete DMEOAttribute " + toDelete.getName());
					}
					toDelete.delete();
				}

			} catch (IllegalArgumentException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("EOAccess management failed :" + e.getMessage());
				}
				throw new EOAccessException(e);
			}
		}

	}

	private void updateRelationshipsFromEOEntity(EOEntity eoEntity) throws EOAccessException {
		if (eoEntity != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateRelationshipsFromEOEntity()");
			}

			try {
				Vector<DMProperty> relationshipsToDelete = new Vector<DMProperty>();
				relationshipsToDelete.addAll(getOrderedRelationships());

				for (Iterator<EORelationship> i = new Vector<EORelationship>(eoEntity.getRelationships()).iterator(); i.hasNext();) {
					EORelationship eoRelationship = i.next();
					DMEORelationship foundRelationship = lookupDMEORelationshipWithName(eoRelationship.getName());
					if ((foundRelationship != null) && (foundRelationship.getDMEOEntity() != this)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Lookup dereferenced EORelationship " + foundRelationship.getName() + "! Trying to repair...");
						}
						foundRelationship.delete();
						foundRelationship = null;
					}
					if (foundRelationship == null) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Found NEW EORelationship " + eoRelationship.getName() + ". Creates the related DMEORelationship"
									+ ".");
						}
						DMEORelationship newDMEORelationship = new DMEORelationship(getDMModel(), eoRelationship);
						registerProperty(newDMEORelationship);
					} else {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Lookup EORelationship " + foundRelationship.getName());
						}
						unregisterProperty(foundRelationship);
						foundRelationship.setEORelationship(eoRelationship);
						registerProperty(foundRelationship);
						relationshipsToDelete.remove(foundRelationship);
						foundRelationship.updateJoins();
					}
				}

				for (Enumeration en = relationshipsToDelete.elements(); en.hasMoreElements();) {
					DMEORelationship toDelete = (DMEORelationship) en.nextElement();
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Delete DMEORelationship " + toDelete.getName());
					}
					toDelete.delete();
				}
			} catch (IllegalArgumentException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("EOAccess management failed :" + e.getMessage());
				}
				throw new EOAccessException(e);
			}
		}

	}

	/**
	 * Overrides getEmbeddedDMObjects
	 * 
	 * @see org.openflexo.foundation.dm.DMEntity#getEmbeddedDMObjects()
	 */
	@Override
	public Vector<DMObject> getEmbeddedDMObjects() {
		Vector v = super.getEmbeddedDMObjects();
		v.addAll(getAttributes().values());
		v.addAll(getRelationships().values());
		return v;
	}

	public boolean isPrototypeEntity() {
		return (getRepository() instanceof EOPrototypeRepository);
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
	}

	@Override
	public void delete() {
		if (getEOEntity() != null) {
			try {
				getDMEOModel().getEOModel().removeEntity(getEOEntity());
			} catch (NullPointerException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("NullPointerException raised in EOF access layer !");
				}
				e.printStackTrace();
				try {
					getDMEOModel().getEOModel().removeEntity(getEOEntity());
				} catch (Exception e2) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Unexpected exception: " + e2.getMessage());
					}
					e2.printStackTrace();
				}
			} catch (IllegalArgumentException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("EOControl management failed :" + e.getMessage());
				}

			}
		}
		super.delete();
		_eoEntity = null;
		_parentEOEntity = null;
		_primaryKeyAttributes.clear();
		_primaryKeyAttributes = null;
		_primaryKeyAttributesNeedsRecomputing = true;
		_classProperties.clear();
		_classProperties = null;
		_classPropertiesNeedsRecomputing = true;
		_attributesUsedForLocking.clear();
		_attributesUsedForLocking = null;
		_attributesUsedForLockingNeedsRecomputing = true;
		_attributesForEOAttributes.clear();
		_attributesForEOAttributes = null;
		_relationshipsForEORelationships.clear();
		_relationshipsForEORelationships = null;
	}

	/**
	 * Return String uniquely identifying inspector template which must be applied when trying to inspect this object
	 * 
	 * @return a String value
	 */
	@Override
	public String getInspectorName() {
		if (getRepository() == null || getRepository().isReadOnly()) {
			return Inspectors.DM.DM_RO_EO_ENTITY_INSPECTOR;
		} else {
			return Inspectors.DM.DM_EO_ENTITY_INSPECTOR;
		}
	}

	@Override
	public String getFullyQualifiedName() {
		if (getEntityClassName() != null) {
			if (getEntityClassName().equals("EOGenericRecord")) {
				return getEntityPackageName() + ".EOGenericRecord$" + getName();
			}
		}
		return super.getFullyQualifiedName();
	}

	@Override
	public String getLocalizedName() {
		return getName();
	}

	@Override
	public void setName(String newName) throws InvalidNameException {
		if (!isDeserializing() && (newName == null || !DMRegExp.ENTITY_NAME_PATTERN.matcher(newName).matches())) {
			throw new InvalidNameException();
		}
		if ((name == null) || (!name.equals(newName))) {
			DMRepository containerRepository = getRepository();
			if (!isDeserializing()) {
				if (containerRepository != null) {
					containerRepository.unregisterEntity(this, false);
				}
			}
			String oldName = name;
			if (!isDeserializing() && _eoEntity != null) {
				try {
					_eoEntity.setName(newName);
					name = newName;
				} catch (IllegalStateException e) {
					throw new DuplicateNameException(newName);
				} finally {
					if (!isDeserializing()) {
						if (containerRepository != null) {
							containerRepository.registerEntity(this);
						}
					}
				}
			}
			setChanged();
			notifyObservers(new DMEntityNameChanged(this, oldName, newName));
			if (!isDeserializing() && getEOEntity() != null) {
				Iterator<EORelationship> i = getEOEntity().getIncomingRelationships().iterator();
				while (i.hasNext()) {
					EORelationship r = i.next();
					DMEOEntity e = getDMModel().getDMEOEntity(r.getEntity());
					e.setChanged();
				}
			}
			if (getDMEOModel() != null) {
				getDMEOModel().notifyReordering(this);
			}
			if (!isDeserializing() && newName != null && newName.trim().length() > 0
					&& (getExternalName() == null || getExternalName().equals(ToolBox.convertJavaStringToDBName(oldName)))) {
				setExternalName(ToolBox.convertJavaStringToDBName(newName));
			}
			if (!isDeserializing() && newName != null && newName.trim().length() > 0
					&& (getEntityClassName() == null || getEntityClassName().equals(oldName))) {
				try {
					setEntityClassName(newName);
				} catch (InvalidNameException e) {
				} catch (DuplicateClassNameException e) {
				}
			}
		}
	}

	public DMEORepository getDMEORepository() {
		return (DMEORepository) getRepository();
	}

	@Override
	public void setRepository(DMRepository repository, boolean notify) {
		if ((repository instanceof DMEORepository) || (repository == null)) {
			super.setRepository(repository, notify);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Repository of a DMEOEntity MUST be a DMEORepository !");
			}
		}
	}

	@Override
	public DMType getParentType() {
		if (_parentType == null) {
			if ((_parentEOEntity != null) && (getDMEORepository() != null)) {
				_parentType = DMType.makeResolvedDMType(getDMModel().getDMEOEntity(_parentEOEntity));
			} else {
				DMEntity defaultParentEntity = getDMModel().getDefaultParentDMEOEntity();
				if (defaultParentEntity != null) {
					super.setParentType(DMType.makeResolvedDMType(defaultParentEntity), true);
				}
			}
		}
		return _parentType;
	}

	/**
	 * @deprecated Use {@link #setParentType(DMType, boolean)} instead
	 */
	@Deprecated
	@Override
	public void setParentType(DMType parentType) {
		setParentType(parentType, true);
	}

	@Override
	public void setParentType(DMType parentType, boolean notify) {
		if ((parentType == null && _parentType != null) || (parentType != null && !parentType.equals(_parentType))) {

			if (parentType == null) {
				super.setParentType(parentType, notify);
				return;
			}

			if (parentType.getKindOfType() == DMType.KindOfType.RESOLVED) {
				super.setParentType(parentType, notify);
				internallySetParentType(parentType);
			}
			if (parentType.getKindOfType() == DMType.KindOfType.UNRESOLVED) {
				super.setParentType(parentType, notify);
				// Will do it later, while type will be resolved
			} else {
				logger.warning("Invalid type : " + parentType + " " + parentType.getKindOfType());
			}
		}
	}

	private void internallySetParentType(DMType aType) {
		DMEntity oldParentEntity = getParentBaseEntity();
		DMEntity newParentEntity = aType.getBaseEntity();
		_parentType = DMType.makeResolvedDMType(newParentEntity);
		if (getEOEntity() != null) {
			if (newParentEntity instanceof DMEOEntity) {
				if (((DMEOEntity) newParentEntity).getEOEntity() != null) {
					try {
						getEOEntity().setParentEntity(((DMEOEntity) newParentEntity).getEOEntity());
						_parentEOEntity = ((DMEOEntity) newParentEntity).getEOEntity();
					} catch (IllegalArgumentException e) {
						_parentType = null;
						getEOEntity().setParentEntity(null);
					}
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Could not set parent entity which refer to null EOEntity");
					}
				}
			} else if (newParentEntity != null) {
				newParentEntity.addToChildEntities(this);
			}
		}
		setChanged();
		notifyObservers(new DMAttributeDataModification("parentEntity", oldParentEntity, newParentEntity));

	}

	@Override
	public boolean getIsEnumeration() {
		return false;
	}

	@Override
	public boolean getIsInterface() {
		return false;
	}

	@Override
	public boolean getIsNormalClass() {
		return true;
	}

	@Override
	public String getName() {
		if (_eoEntity != null) {
			return _eoEntity.getName();
		} else {
			return super.getName();
		}
	}

	public EOEntity getEOEntity() {
		if (_eoEntity == null) {
			if ((getDMEOModel() != null) && (getDMEOModel().getEOModel() != null)) {
				try {
					_eoEntity = getDMEOModel().getEOModel().entityNamed(getName());
				} catch (IllegalArgumentException e) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Could not find EOEntity named " + getName() + " : EOControl management failed");
					}
				}
			}
		}
		return _eoEntity;
	}

	public void setEOEntity(EOEntity eoEntity) {
		_eoEntity = eoEntity;
		_primaryKeyAttributesNeedsRecomputing = true;
		_attributesUsedForLockingNeedsRecomputing = true;
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(CreateDMEOAttribute.actionType);
		returned.add(CreateDMEORelationship.actionType);
		return returned;
	}

	@Override
	public DMEOModel getDMEOModel() {
		return _dmEOModel;
	}

	public void setDMEOModel(DMEOModel aDMEOModel) {
		_dmEOModel = aDMEOModel;
	}

	public Hashtable<EOAttribute, DMEOAttribute> getAttributes() {
		return _attributesForEOAttributes;
	}

	public Collection<EOAttribute> getOrderedEOAttributes() {
		ArrayList<EOAttribute> list = new ArrayList<EOAttribute>(getAttributes().keySet());
		Collections.sort(list, new EOAttributeComparator());
		return list;
	}

	protected class EOAttributeComparator implements Comparator<EOAttribute> {
		@Override
		public int compare(EOAttribute arg0, EOAttribute arg1) {
			if (arg0 == null && arg1 == null) {
				return 0;
			}
			if (arg0 == null) {
				return -1;
			}
			if (arg1 == null) {
				return 1;
			}
			return arg0.getName().compareTo(arg1.getName());
		}
	}

	public Hashtable<EORelationship, DMEORelationship> getRelationships() {
		return _relationshipsForEORelationships;
	}

	public Collection<EORelationship> getOrderedEORelationship() {
		ArrayList<EORelationship> list = new ArrayList<EORelationship>(getRelationships().keySet());
		Collections.sort(list, new EORelationshipComparator());
		return list;
	}

	class EORelationshipComparator implements Comparator<EORelationship> {
		@Override
		public int compare(EORelationship arg0, EORelationship arg1) {
			if (arg0 == null && arg1 == null) {
				return 0;
			}
			if (arg0 == null) {
				return -1;
			}
			if (arg1 == null) {
				return 1;
			}
			return arg0.getName().compareTo(arg1.getName());
		}
	}

	public DMEOAttribute getAttribute(EOAttribute eoAttribute) {
		return _attributesForEOAttributes.get(eoAttribute);
	}

	public DMEOAttribute getAttributeNamed(String attributeName) {
		EOAttribute a = getEOEntity().attributeNamed(attributeName);
		if (a != null) {
			return getAttribute(a);
		}
		return null;
	}

	public DMEOAttribute getAttributeNamedIgnoreCase(String attributeName) {
		EOAttribute a = getEOEntity().attributeNamedIgnoreCase(attributeName);
		if (a != null) {
			return getAttribute(a);
		}
		return null;
	}

	public DMEORelationship getRelationship(EORelationship eoRelationship) {
		return _relationshipsForEORelationships.get(eoRelationship);
	}

	public DMEORelationship getRelationshipNamed(String attributeName) {
		EORelationship r = getEOEntity().relationshipNamed(attributeName);
		if (r != null) {
			return getRelationship(r);
		}
		return null;
	}

	public boolean hasNullify() {
		Enumeration en = _relationshipsForEORelationships.elements();
		while (en.hasMoreElements()) {
			if (((DMEORelationship) en.nextElement()).isNullify()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasCascade() {
		Enumeration en = _relationshipsForEORelationships.elements();
		while (en.hasMoreElements()) {
			if (((DMEORelationship) en.nextElement()).isCascade()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getEntityClassName() {
		if (getEOEntity() != null) {
			String fullQualifiedClassName = getEOEntity().getClassName();
			return fullQualifiedClassName.substring(fullQualifiedClassName.lastIndexOf(".") + 1);
		} else {
			return super.getEntityClassName();
		}
	}

	@Override
	public void setEntityClassName(String anEntityClassName) throws InvalidNameException, DuplicateClassNameException {
		if (!isDeserializing() && (anEntityClassName == null || !DMRegExp.ENTITY_NAME_PATTERN.matcher(anEntityClassName).matches())) {
			throw new InvalidNameException();
		}
		if ((getEntityClassName() == null) || (!getEntityClassName().equals(anEntityClassName))) {
			if (getEOEntity() != null) {
				if (getDMModel().getDMEntity(getEntityPackageName(), anEntityClassName) != null) {
					throw new DuplicateClassNameException(getEntityPackageName() + "." + anEntityClassName);
				}
				DMRepository containerRepository = getRepository();
				if (containerRepository != null) {
					containerRepository.unregisterEntity(this, false);
				}
				String oldEntityClassName = getFullQualifiedName();
				getEOEntity().setClassName(
						(getPackage() == null || getPackage().isDefaultPackage() ? "" : getPackage().getName() + ".") + anEntityClassName);
				// updateTypeObject(oldEntityClassName, anEntityClassName);
				if (containerRepository != null) {
					containerRepository.registerEntity(this);
				}
				setChanged();
				notifyObservers(new DMEntityClassNameChanged(this, oldEntityClassName, entityClassName));
			} else {
				super.setEntityClassName(anEntityClassName);
			}
		}
	}

	public String getExternalName() {
		if (getEOEntity() != null) {
			return getEOEntity().getExternalName();
		}
		return null;
	}

	public void setExternalName(String externalName) throws InvalidNameException {
		if (!isDeserializing() && (externalName == null || !DMRegExp.ENTITY_NAME_PATTERN.matcher(externalName).matches())) {
			throw new InvalidNameException();
		}
		if (getEOEntity() != null) {
			String oldExternalName = getExternalName();
			getEOEntity().setExternalName(externalName);
			setChanged();
			notifyObservers(new DMAttributeDataModification("externalName", oldExternalName, externalName));
		}
	}

	/**
	 * Return primary key attributes, as a vector of DMEOAttribute
	 * 
	 * @return a Vector of DMEOAttribute
	 */
	public Vector<DMEOAttribute> getPrimaryKeyAttributes() {
		if (_primaryKeyAttributesNeedsRecomputing) {
			_primaryKeyAttributes = buildPrimaryKeyAttributes();
		}
		return _primaryKeyAttributes;
	}

	protected void rebuildPrimaryKeyAttributes() {
		_primaryKeyAttributesNeedsRecomputing = true;
		buildPrimaryKeyAttributes();
	}

	private Vector<DMEOAttribute> buildPrimaryKeyAttributes() {
		synchronized (_primaryKeyAttributes) {
			_primaryKeyAttributes.clear();
			if (getEOEntity() != null) {
				for (Iterator<EOAttribute> i = getEOEntity().getPrimaryKeyAttributes().iterator(); i.hasNext();) {
					EOAttribute eoAttribute = i.next();
					DMEOAttribute dmEOAttribute = getDMEOAttribute(eoAttribute);
					if (dmEOAttribute != null) {
						_primaryKeyAttributes.add(dmEOAttribute);
					} else {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Could not find attribute named " + eoAttribute.getName());
						}
					}
				}
				_primaryKeyAttributesNeedsRecomputing = false;
			}
			return _primaryKeyAttributes;
		}

	}

	/**
	 * Return primary key attributes, as a vector of DMEOAttribute
	 * 
	 * @return a Vector of DMEOAttribute
	 */
	public Vector getAttributesUsedForLocking() {
		if (_attributesUsedForLockingNeedsRecomputing) {
			_attributesUsedForLocking = buildAttributesUsedForLocking();
		}
		return _attributesUsedForLocking;
	}

	protected void rebuildAttributesUsedForLocking() {
		_attributesUsedForLockingNeedsRecomputing = true;
		buildAttributesUsedForLocking();
	}

	private Vector<DMEOAttribute> buildAttributesUsedForLocking() {
		_attributesUsedForLocking.clear();
		if (getEOEntity() != null) {
			for (Iterator<EOAttribute> i = getEOEntity().getAttributesUsedForLocking().iterator(); i.hasNext();) {
				EOAttribute eoAttribute = i.next();
				DMEOAttribute dmEOAttribute = getDMEOAttribute(eoAttribute);
				if (dmEOAttribute != null) {
					_attributesUsedForLocking.add(dmEOAttribute);
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Could not find attribute named " + eoAttribute.getName());
					}
				}
			}
			_attributesUsedForLockingNeedsRecomputing = false;
		}
		return _attributesUsedForLocking;
	}

	/**
	 * Return class properties, as a vector of DMEOProperty
	 * 
	 * @return a Vector of DMEOProperty
	 */
	public Vector<DMProperty> getClassProperties() {
		if (_classPropertiesNeedsRecomputing) {
			_classProperties = buildClassProperties();
		}
		return _classProperties;
	}

	protected void rebuildClassProperties() {
		_classPropertiesNeedsRecomputing = true;
		buildClassProperties();
	}

	private Vector<DMProperty> buildClassProperties() {
		_classProperties.clear();
		if (getEOEntity() != null) {
			for (Iterator<EOProperty> i = getEOEntity().getClassProperties().iterator(); i.hasNext();) {
				EOProperty eoProperty = i.next();
				if (eoProperty instanceof EOAttribute) {
					EOAttribute eoAttribute = (EOAttribute) eoProperty;
					DMEOAttribute dmEOAttribute = getDMEOAttribute(eoAttribute);
					if (dmEOAttribute != null) {
						_classProperties.add(dmEOAttribute);
					}
				}
				if (eoProperty instanceof EORelationship) {
					EORelationship eoRelationship = (EORelationship) eoProperty;
					DMEORelationship dmEORelationship = getDMEORelationship(eoRelationship);
					if (dmEORelationship != null) {
						_classProperties.add(dmEORelationship);
					}
				}
			}
		}
		_classPropertiesNeedsRecomputing = false;
		return _classProperties;
	}

	/**
	 * Called during DMEORepository finalizeSerialization()
	 */
	public void finalizePropertiesRegistering() {
		for (Enumeration en = getProperties().elements(); en.hasMoreElements();) {
			DMProperty next = (DMProperty) en.nextElement();
			if (next instanceof DMEOAttribute) {
				internallyRegisterDMEOAttribute((DMEOAttribute) next);
			}
			if (next instanceof DMEORelationship) {
				internallyRegisterDMEORelationship((DMEORelationship) next);
			}
		}
	}

	private void internallyRegisterDMEOAttribute(DMEOAttribute dmEOAttribute) {
		if (dmEOAttribute.getEOAttribute() != null) {
			if (_attributesForEOAttributes.get(dmEOAttribute.getEOAttribute()) != null) {
				if (_attributesForEOAttributes.get(dmEOAttribute.getEOAttribute()) != dmEOAttribute) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Trying to redefine DMEOAttribute: operation not allowed !");
					}
				}
			} else {
				_attributesForEOAttributes.put(dmEOAttribute.getEOAttribute(), dmEOAttribute);
			}
		}
	}

	private void internallyUnregisterDMEOAttribute(DMEOAttribute dmEOAttribute) {
		if (dmEOAttribute.getEOAttribute() != null) {
			_attributesForEOAttributes.remove(dmEOAttribute.getEOAttribute());
		}
		_orderedAttributes.remove(dmEOAttribute);
		_classProperties.remove(dmEOAttribute);
		_primaryKeyAttributes.remove(dmEOAttribute);
		_attributesUsedForLocking.remove(dmEOAttribute);
	}

	private void internallyRegisterDMEORelationship(DMEORelationship dmEORelationship) {
		if (dmEORelationship.getEORelationship() != null) {
			if (_relationshipsForEORelationships.get(dmEORelationship.getEORelationship()) != null) {
				if (_relationshipsForEORelationships.get(dmEORelationship.getEORelationship()) != dmEORelationship) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Trying to redefine DMEORelationship: operation not allowed !");
					}
				}
			} else {
				_relationshipsForEORelationships.put(dmEORelationship.getEORelationship(), dmEORelationship);
			}
		}
	}

	private void internallyUnregisterDMEORelationship(DMEORelationship dmEORelationship) {
		if (dmEORelationship.getEORelationship() != null) {
			_relationshipsForEORelationships.remove(dmEORelationship.getEORelationship());
		}
		_orderedRelationships.remove(dmEORelationship);

	}

	/**
	 * Register property for this entity
	 * 
	 * @param property
	 *            : the property to register
	 * @return true if property has been effectively registered, false otherwise
	 */
	public boolean registerProperty(DMProperty property) {
		return registerProperty(property, true);
	}

	@Override
	public void setPropertyForKey(DMProperty property, String propertyName) {
		if (property instanceof DMEOAttribute) {
			DMEOAttribute dmEOAttribute = (DMEOAttribute) property;
			internallyRegisterDMEOAttribute(dmEOAttribute);
		}
		if (property instanceof DMEORelationship) {
			DMEORelationship dmEORelationship = (DMEORelationship) property;
			internallyRegisterDMEORelationship(dmEORelationship);
		}
		super.setPropertyForKey(property, propertyName);
	}

	@Override
	public void removePropertyWithKey(String propertyName) {
		removePropertyWithKey(propertyName, true);
	}

	@Override
	public void removePropertyWithKey(String propertyName, boolean notify) {
		DMProperty property = getDMProperty(propertyName);
		if ((property != null) && (property instanceof DMEOAttribute)) {
			DMEOAttribute dmEOAttribute = (DMEOAttribute) property;
			internallyUnregisterDMEOAttribute(dmEOAttribute);
		}
		if ((property != null) && (property instanceof DMEORelationship)) {
			DMEORelationship dmEORelationship = (DMEORelationship) property;
			internallyUnregisterDMEORelationship(dmEORelationship);
		}
		super.removePropertyWithKey(propertyName, notify);
	}

	public DMEOAttribute getDMEOAttribute(EOAttribute eoAttribute) {
		if (eoAttribute == null) {
			return null;
		}
		return _attributesForEOAttributes.get(eoAttribute);
	}

	private DMEOAttribute lookupDMEOAttributeWithName(String attributeName) {
		DMProperty returned = getDeclaredProperty(attributeName);
		if (returned instanceof DMEOAttribute) {
			return (DMEOAttribute) returned;
		}
		if (returned != null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Found property named " + attributeName + " but doesn't match a DMEOAttribute: "
						+ returned.getClass().getName());
			}
		}
		return null;
	}

	public DMEORelationship getDMEORelationship(EORelationship eoRelationship) {
		return _relationshipsForEORelationships.get(eoRelationship);
	}

	private DMEORelationship lookupDMEORelationshipWithName(String relationshipName) {
		DMProperty returned = getDeclaredProperty(relationshipName);
		if (returned instanceof DMEORelationship) {
			return (DMEORelationship) returned;
		}
		if (returned != null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Found property named " + relationshipName + " but doesn't match a DMEORelationship: "
						+ returned.getClass().getName());
			}
		}
		return null;
	}

	public Vector<DMEOAttribute> getMandatoryAttributes() {
		Vector<DMEOAttribute> answer = new Vector<DMEOAttribute>();
		Iterator i = getEOEntity().getAttributes().iterator();
		while (i.hasNext()) {
			EOAttribute att = (EOAttribute) i.next();
			if (!(att.getIsPrimaryKey() && att.getIsClassProperty()) && !getAttribute(att).getAllowsNull()) {
				if (!isSourceAttributeForRelationship(att)) {
					answer.add(getAttribute(att));
				}
			}
		}
		return answer;
	}

	public Vector<DMProperty> getEntityConstructorArguments() {
		Vector<DMProperty> answer = new Vector<DMProperty>();
		answer.addAll(getMandatoryAttributes());
		answer.addAll(getMandatoryRelationships());
		return answer;
	}

	public Vector<DMEORelationship> getMandatoryRelationships() {
		Vector<DMEORelationship> answer = new Vector<DMEORelationship>();
		Iterator<EORelationship> i = getEOEntity().getRelationships().iterator();
		while (i.hasNext()) {
			EORelationship rel = i.next();
			if (rel.getIsMandatory()) {
				answer.add(getRelationship(rel));
			}
		}
		return answer;
	}

	private boolean isSourceAttributeForRelationship(EOAttribute att) {
		Iterator<EORelationship> i = getEOEntity().getRelationships().iterator();
		while (i.hasNext()) {
			EORelationship rel = i.next();
			if (rel.getSourceAttributes().contains(att)) {
				return true;
			}
		}
		return false;
	}

	private Vector<DMProperty> _orderedSingleProperties;

	private Vector<DMEOAttribute> _orderedAttributes;

	private Vector<DMEORelationship> _orderedRelationships;

	/**
	 * Return a vector of up-to-date and reordered DMProperty which are not instances of DMEOAttribute or DMEORelationship
	 * 
	 * @return
	 */
	@Override
	public Vector<DMProperty> getOrderedSingleProperties() {
		if (propertiesNeedsReordering) {
			reorderProperties();
		}
		return _orderedSingleProperties;
	}

	public Vector<DMEOAttribute> getOrderedAttributes() {
		if (propertiesNeedsReordering) {
			reorderProperties();
		}
		return _orderedAttributes;
	}

	public Vector<DMEORelationship> getOrderedRelationships() {
		if (propertiesNeedsReordering) {
			reorderProperties();
		}
		return _orderedRelationships;
	}

	@Override
	protected void reorderProperties() {
		if (propertiesNeedsReordering) {

			// Sort single properties
			if (_orderedSingleProperties != null) {
				_orderedSingleProperties.removeAllElements();
			} else {
				_orderedSingleProperties = new Vector<DMProperty>();
			}
			for (Enumeration en = getProperties().elements(); en.hasMoreElements();) {
				DMProperty next = (DMProperty) en.nextElement();
				if ((!(next instanceof DMEOAttribute)) && (!(next instanceof DMEORelationship))) {
					_orderedSingleProperties.add(next);
				}
			}
			Collections.sort(_orderedSingleProperties, propertyComparator);

			// Sort attributes
			if (_orderedAttributes != null) {
				_orderedAttributes.removeAllElements();
			} else {
				_orderedAttributes = new Vector();
			}
			if (_attributesForEOAttributes != null) {
				_orderedAttributes.addAll(_attributesForEOAttributes.values());
			}
			Collections.sort(_orderedAttributes, propertyComparator);

			// Sort relationships
			if (_orderedRelationships != null) {
				_orderedRelationships.removeAllElements();
			} else {
				_orderedRelationships = new Vector();
			}
			_orderedRelationships.addAll(_relationshipsForEORelationships.values());
			Collections.sort(_orderedRelationships, propertyComparator);
		}
		super.reorderProperties();
	}

	@Override
	public boolean registerProperty(DMProperty property, boolean notify) {
		boolean b = super.registerProperty(property, notify);
		_primaryKeyAttributesNeedsRecomputing = true;
		_classPropertiesNeedsRecomputing = true;
		_attributesUsedForLockingNeedsRecomputing = true;
		return b;
	}

	@Override
	public TreeNode getParent() {
		return getDMEOModel();
	}

	public static class DMEOEntityMustReferToAValidEOEntity extends ValidationRule {
		public DMEOEntityMustReferToAValidEOEntity() {
			super(DMEOEntity.class, "eoentity_must_refer_to_a_valid_eo_entity");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			final DMEOEntity entity = (DMEOEntity) object;
			if (entity.getEOEntity() == null) {
				ValidationError error = new ValidationError(this, object, "eoentity_($object.name)_must_refer_to_a_valid_eo_entity");
				return error;
			}
			return null;
		}

	}

	public static class DMEOEntityMustAtLeastOnePrimaryKey extends ValidationRule {
		public DMEOEntityMustAtLeastOnePrimaryKey() {
			super(DMEOEntity.class, "eoentity_must_have_at_least_one_primary_key");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			final DMEOEntity entity = (DMEOEntity) object;
			if (entity.getEOEntity() != null && entity.getEOEntity().getPrimaryKeyAttributes().size() == 0) {
				ValidationError error = new ValidationError(this, object,
						"eoentity_($object.name)_must_have_at_least_one_primary_key_attribute");
				Iterator<DMEOAttribute> it = entity.getAttributes().values().iterator();
				while (it.hasNext()) {
					error.addToFixProposals(new SetAttributePrimaryKey(it.next()));
				}
				return error;
			}
			return null;
		}

	}

	public static class SetAttributePrimaryKey extends FixProposal {

		DMEOAttribute attribute;

		public SetAttributePrimaryKey(DMEOAttribute attribute) {
			super("use_attribute_($attribute.name)_as_primary_key");
			this.attribute = attribute;
		}

		public DMEOAttribute getAttribute() {
			return attribute;
		}

		public void setAttribute(DMEOAttribute att) {
			attribute = att;
		}

		@Override
		protected void fixAction() {
			attribute.setIsPrimaryKeyAttribute(true);
		}
	}

	public Vector<DMEOAttribute> pkAttributes() {
		Iterator<EOAttribute> it = getEOEntity().getPrimaryKeyAttributes().iterator();
		Vector<DMEOAttribute> reply = new Vector<DMEOAttribute>();
		EOAttribute att = null;
		DMEOAttribute a = null;
		while (it.hasNext()) {
			att = it.next();
			a = getDMEOAttribute(att);
			if (a != null) {
				reply.add(a);
			}
		}
		return reply;
	}

	public boolean isIntegerPrimaryKey() {
		Vector<DMEOAttribute> pks = pkAttributes();
		if (pks.size() != 1) {
			return false;
		}
		DMEOAttribute att = pks.get(0);
		return att.getPrototype().getName().startsWith("id");
	}

	public Vector<DMProperty> getAllNonEOProperties() {
		Vector<DMProperty> reply = new Vector<DMProperty>();
		Iterator<DMProperty> it = getProperties().values().iterator();
		DMProperty p = null;
		while (it.hasNext()) {
			p = it.next();
			if (!(p instanceof DMEOAttribute) && !(p instanceof DMEORelationship)) {
				reply.add(p);
			}
		}
		return reply;
	}

	@Override
	public boolean hasCreatorProperty() {
		return getAttributeNamed("creator") != null;
	}

	public DMEOEntityStatistics getStatistics() {
		if (statistics == null) {
			statistics = new DMEOEntityStatistics(this);
		}
		return statistics;
	}

	/**
	 * @param entity
	 * @return
	 */
	public int compareTo(DMEOEntity entity) {
		if (hasRelationshipTo(entity, new Vector<DMEOEntity>())) {
			if (!entity.hasRelationshipTo(this, new Vector<DMEOEntity>())) {
				return -1;
			} else {
				return 0;
			}
		} else if (entity.hasRelationshipTo(this, new Vector<DMEOEntity>())) {
			return 1;
		} else {
			return 0;
		}
	}

	private boolean hasRelationshipTo(DMEOEntity to, Vector<DMEOEntity> alreadyVisited) {
		alreadyVisited.add(this);
		Enumeration<DMEORelationship> en = getOrderedRelationships().elements();
		while (en.hasMoreElements()) {
			DMEORelationship r = en.nextElement();
			if (!r.getIsToMany()) {
				if (r.getDestinationEntity() == to) {
					return true;
				} else if (r.getDestinationEntity() != null && !alreadyVisited.contains(r.getDestinationEntity())
						&& r.getDestinationEntity().hasRelationshipTo(to, alreadyVisited)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof TypeResolved && ((TypeResolved) dataModification).getType() == _parentType) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("EOEntity: Parent type " + _parentType + " decoded for " + this);
			}
			internallySetParentType(((TypeResolved) dataModification).getType());
		} else {
			super.update(observable, dataModification);
		}
	}

	// =============================================================
	// ===================== Code generation =======================
	// =============================================================

	public boolean isCodeGenerationAvailable() {
		if (getDMModel() != null) {
			return getDMModel().getEOCodeGenerationAvailable();
		}
		return false;
	}

	/**
	 * Tells if code generation is applicable for related DMEntity Applicable only if EOCodeGeneration was activated
	 * 
	 * @return
	 */
	@Override
	public boolean isCodeGenerationApplicable() {
		return isCodeGenerationActivated();
	}

	public boolean isCodeGenerationActivated() {
		if (getDMModel() != null) {
			return getDMModel().getEOCodeGenerationActivated();
		}
		return false;
	}

	public String getCodeGenerationNotApplicableLabel() {
		if (!getDMModel().getEOCodeGenerationAvailable()) {
			return FlexoLocalization.localizedForKey("sorry_EO_code_generation_is_not_available_in_this_flexo_edition");
		}
		return FlexoLocalization.localizedForKey("sorry_EO_code_generation_is_not_activated_for_this_project");
	}

	public void activateEOCodeGeneration() {
		if (getDMModel() != null) {
			getDMModel().activateEOCodeGeneration();
		}
	}

	public void desactivateEOCodeGeneration() {
		if (getDMModel() != null) {
			getDMModel().desactivateEOCodeGeneration();
		}
	}

	@Override
	public void setIsModified() {
		// When a DMEOEntity is modified, reset needsRegeneration flag
		codeNeedsToBeRegenerated = true;
		super.setIsModified();
	}

	private boolean codeNeedsToBeRegenerated = true;
	private ClassSourceCode generatedCode = null;

	public ClassSourceCode getGeneratedCode() {
		if (codeNeedsToBeRegenerated && getDMModel().getEOCodeGenerationActivated() && codeIsComputable()) {
			if (generatedCode == null) {
				generatedCode = new ClassSourceCode(this) {
					@Override
					public void interpretEditedJavaClass(ParsedJavaClass javaClass) {
						// We won't use this functionality, code will remain read-only, only generated
					}

					@Override
					public String makeComputedCode() {
						// We won't use this functionality, code will remain read-only, only generated
						return null;
					}
				};
			}

			String generatedCodeAsString = getDMModel().getEOEntityCodeGenerator().generateCodeForEntity(this);
			try {
				generatedCode.setCode(generatedCodeAsString);

			} catch (ParserNotInstalledException e) {
				e.printStackTrace();
			} catch (DuplicateMethodSignatureException e) {
				e.printStackTrace();
			}
			getDMModel().getClassLibrary().clearLibrary();
			codeNeedsToBeRegenerated = false;
		}
		return generatedCode;
	}

	@Override
	public boolean isDescriptionImportant() {
		return getClassProperties().size() > 0;
	}

	@Override
	public boolean codeIsComputable() {
		Enumeration<DMProperty> en = getClassProperties().elements();
		while (en.hasMoreElements()) {
			if (!en.nextElement().codeIsComputable()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void resetSourceCode() throws ParserNotInstalledException, DuplicateMethodSignatureException {
		if (generatedCode != null) {
			generatedCode.setCode("");
		}
	}
}
