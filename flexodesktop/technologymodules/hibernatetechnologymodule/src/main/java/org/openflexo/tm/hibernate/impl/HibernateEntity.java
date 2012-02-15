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
package org.openflexo.tm.hibernate.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.apache.commons.lang.StringUtils;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.dm.DMEntityNameChanged;
import org.openflexo.foundation.dm.dm.PropertyRegistered;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.LinkableTechnologyModelObject;
import org.openflexo.foundation.sg.implmodel.event.SGAttributeModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectAddedToListModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectDeletedModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectRemovedFromListModification;
import org.openflexo.foundation.sg.implmodel.metamodel.DerivedAttribute;
import org.openflexo.foundation.xml.ImplementationModelBuilder;
import org.openflexo.tm.hibernate.impl.comparator.HibernateLinkableObjectComparator;
import org.openflexo.tm.hibernate.impl.enums.HibernateAttributeType;
import org.openflexo.tm.hibernate.impl.utils.HibernateUtils;
import org.openflexo.toolbox.JavaUtils;

/**
 * This class defines an entity in the context of an Hibernate implementation.
 * 
 * @author Emmanuel Koch, Blue Pimento Services SPRL
 */
public class HibernateEntity extends LinkableTechnologyModelObject<DMEntity> implements FlexoObserver {

	protected static final Logger logger = Logger.getLogger(HibernateEntity.class.getPackage().getName());

	public static final String CLASS_NAME_KEY = "hibernate_entity";

	protected HibernateModel hibernateModel;

	/**
	 * Returns the name of the table that will be used to store this entity in the storage system. If <code>null</code>, the name define the
	 * table name.
	 */
	protected String tableName;
	protected boolean isTableNameSynchronized;

	/** Indicates if an additional column "Version" must be added to implements optimistic locking. */
	protected boolean optimisticLocking;

	/** This is the parent entity, when it extends another one. The inverse relation is given by {@link #children}. */
	protected HibernateEntity father;

	/** This is the sub-entities, extending this one. The inverse relation is given by {@link #father}. */
	protected Vector<HibernateEntity> children = new Vector<HibernateEntity>();

	/** The attributes of this entity. */
	protected Vector<HibernateAttribute> attributes = new Vector<HibernateAttribute>();

	/** The relationships of this entity. */
	protected Vector<HibernateRelationship> relationships = new Vector<HibernateRelationship>();

	// ================ //
	// = Constructors = //
	// ================ //

	/**
	 * Build a new Hibernate entity for the specified implementation model builder.<br/>
	 * This constructor is namely invoked during unserialization.
	 * 
	 * @param builder
	 *            the builder that will create this entity
	 */
	public HibernateEntity(ImplementationModelBuilder builder) {
		this(builder.implementationModel);
		initializeDeserialization(builder);
	}

	/**
	 * Build a new Hibernate entity for the specified implementation model.
	 * 
	 * @param implementationModel
	 *            the implementation model where to create this Hibernate entity
	 */
	public HibernateEntity(ImplementationModel implementationModel) {
		super(implementationModel);
		createDefaultPrimaryKey();
	}

	/**
	 * @param implementationModel
	 *            the implementation model where to create this Hibernate entity
	 * @param linkedFlexoModelObject
	 *            Can be null
	 */
	public HibernateEntity(HibernateModel hibernateModel1, DMEntity linkedFlexoModelObject) {
		super(hibernateModel1.getImplementationModel(), linkedFlexoModelObject);
        setHibernateModel(hibernateModel1);
        synchronizeWithLinkedFlexoModelObject();
		createDefaultPrimaryKey();
		if (linkedFlexoModelObject != null) {
			isTableNameSynchronized = true;
		}
	}

	// =========== //
	// = Methods = //
	// =========== //

	/**
	 * @Override
	 */
	@Override
	public String getClassNameKey() {
		return CLASS_NAME_KEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getHasInspector() {
		return true;
	}

	/**
	 * @Override
	 */
	@Override
	public String getFullyQualifiedName() {
		return getHibernateModel().getFullyQualifiedName() + "." + getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeWithLinkedFlexoModelObject() {
        super.synchronizeWithLinkedFlexoModelObject();
		if (getLinkedFlexoModelObject() != null) {

			List<LinkableTechnologyModelObject<?>> deletedChildren = new ArrayList<LinkableTechnologyModelObject<?>>();

			Map<DMProperty, LinkableTechnologyModelObject<?>> alreadyCreatedChildren = new HashMap<DMProperty, LinkableTechnologyModelObject<?>>();
			for (HibernateAttribute hibernateAttribute : this.attributes) {
				if (hibernateAttribute.getLinkedFlexoModelObject() != null) {
					alreadyCreatedChildren.put(hibernateAttribute.getLinkedFlexoModelObject(), hibernateAttribute);
				} else if (hibernateAttribute.getWasLinkedAtLastDeserialization()) {
					deletedChildren.add(hibernateAttribute);
				}
			}

			for (HibernateRelationship hibernateRelationship : this.relationships) {
				if (hibernateRelationship.getLinkedFlexoModelObject() != null) {
					alreadyCreatedChildren.put(hibernateRelationship.getLinkedFlexoModelObject(), hibernateRelationship);
				} else if (hibernateRelationship.getWasLinkedAtLastDeserialization()) {
					deletedChildren.add(hibernateRelationship);
				}
			}

			// Create missing attributes & relationships and update existing ones
			for (DMProperty property : getLinkedFlexoModelObject().getProperties().values()) {
				if (!alreadyCreatedChildren.containsKey(property)) {
					createHibernateObjectBasedOnDMProperty(property);
				} else {
					alreadyCreatedChildren.get(property).synchronizeWithLinkedFlexoModelObject();
					alreadyCreatedChildren.remove(property);
				}
			}

			// Remove deleted entities & enums
			for (LinkableTechnologyModelObject<?> hibernateObj : deletedChildren) {
				hibernateObj.delete();
			}
		}
	}

	/**
	 * Add a new Hibernate Attribute or Hibernate Relationshp to this entity. The newly created Hibernate object is based and linked to the
	 * specified DMProperty. <br>
	 * If an Hibernate object was already existing for this DMProperty, nothing is performed.
	 * 
	 * @param dmProperty
	 *            the DMProperty the newly created Hibernate object should be linked to.
	 */
	public Object createHibernateObjectBasedOnDMProperty(DMProperty dmProperty) {
		if (HibernateUtils.getIsHibernateAttributeRepresented(dmProperty)) {
			if (getAttribute(dmProperty) == null) {
				return new HibernateAttribute(getImplementationModel(), dmProperty,this);
			}
		} else {
			if (getRelationship(dmProperty) == null) {
				return new HibernateRelationship(getImplementationModel(), dmProperty, this);
			}
		}
        return null;
	}

	/**
	 * Create a new default primary key attribute on this entity. If the default primary key attribute was already existing, nothing is
	 * performed
	 */
	public void createDefaultPrimaryKey() {
		if (getAttribute("id") == null) {
			try {
				// Create default primary key
				HibernateAttribute attribute = new HibernateAttribute(getImplementationModel());
				attribute.setName("id");
				attribute.setType(HibernateAttributeType.LONG);
				attribute.setPrimaryKey(true);
				attribute.setNotNull(true);
				attribute.setUnique(true);
				addToAttributes(attribute);
			} catch (Exception e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.log(Level.WARNING, "Cannot create default primary key on Hibernate entity " + getName(), e);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String escapeName(String name) {
		return JavaUtils.getClassName(name);
	}

	/**
	 * Retrieve the default table name to use for this entity based on the entity name.
	 * 
	 * @return the default table name to use for this entity.
	 */
	public String getDerivedTableName() {
		return escapeTableName(getName());
	}

	private String escapeTableName(String tableName) {
		return getTechnologyModuleImplementation().getDbObjectName(tableName);
	}

//	public void updateIsTableNameSynchronized() {
//		if (!isDeserializing) {
//			String defaultTableName = getDefaultTableName();
//			setIsTableNameSynchronized(defaultTableName != null
//					&& (StringUtils.isEmpty(getTableName()) || StringUtils.equals(defaultTableName, getTableName())));
//		}
//	}

	/**
	 * Update the table name with this object name if necessary.
	 */
    //todo : ensure this is covered
//	public void updateTableNameIfNecessary() {
//		if (getIsTableNameSynchronized()) {
//			setTableName(getName());
//		} else {
//			updateIsTableNameSynchronized(); // Update this anyway in case the name is set to tableName. In this case synchronization is set
//												// back to true.
//		}
//	}

	/* ===================== */
	/* ====== Actions ====== */
	/* ===================== */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {

		for (HibernateRelationship hibernateRelationship : new Vector<HibernateRelationship>(getRelationships())) {
			hibernateRelationship.delete();
		}

		for (HibernateAttribute hibernateAttribute : new Vector<HibernateAttribute>(getAttributes())) {
			hibernateAttribute.delete();
		}

		for (HibernateEntity hibernateEntity : new Vector<HibernateEntity>(getChildren())) {
			hibernateEntity.delete();
		}

		if (getFather() != null) {
			getFather().removeFromChildren(this);
		}

		if (getHibernateModel() != null) {
			getHibernateModel().removeFromEntities(this);
		}

		setChanged();
		notifyObservers(new SGObjectDeletedModification<HibernateEntity>(this));
		super.delete();
		deleteObservers();
	}

	/**
	 * Retrieve all entities allowed as parent for this entity
	 * 
	 * @return the retrieved entities
	 */
	public Vector<HibernateEntity> getEntitiesAllowedAsParent() {
		Vector<HibernateEntity> result = new Vector<HibernateEntity>();
		for (HibernateEntity entity : getHibernateModel().getEntities()) {
			if (entity != this && entity.getFather() == null) {
				result.add(entity);
			}
		}

		return result;
	}

	/**
	 * Sort attributes stored in this entity by their name.
	 */
	public void sortAttributes() {
		Collections.sort(this.attributes, new Comparator<HibernateAttribute>() {

			private HibernateLinkableObjectComparator subComparator = new HibernateLinkableObjectComparator();

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int compare(HibernateAttribute o1, HibernateAttribute o2) {
				if (o1.getPrimaryKey() && !o2.getPrimaryKey()) {
					return -1;
				}
				if (!o1.getPrimaryKey() && o2.getPrimaryKey()) {
					return 1;
				}
				return subComparator.compare(o1, o2);
			}

		});
	}

	/**
	 * Sort relationships stored in this entity by their name.
	 */
	public void sortRelationships() {
		Collections.sort(this.relationships, new HibernateLinkableObjectComparator());
	}

	/* ============== */
	/* == Observer == */
	/* ============== */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		super.update(observable, dataModification);

		if (dataModification instanceof NameChanged) {
			if (observable instanceof HibernateAttribute) {
				sortAttributes();
			}
			if (observable instanceof HibernateRelationship) {
				sortRelationships();
			}
		}
	}

	/* ===================== */
	/* == Getter / Setter == */
	/* ===================== */

	/**
	 * {@inheritDoc}
	 */
//	@Override
//	public void setName(String name) {
//		name = escapeName(name);
//
//		if (StringUtils.isEmpty(name)) {
//			name = getDerivedName();
//		}
//
//		if (requireChange(getName(), name)) {
//			super.setName(name);
//			updateTableNameIfNecessary();
//		}
//	}

	public String getTableName() {
		return tableName;
	}

    @DerivedAttribute
	public void setTableName(String tableName) {
		tableName = escapeTableName(tableName);
        checkSynchronizationStatus("tableName",tableName);
//		if (StringUtils.isEmpty(tableName)) {
//			tableName = getDefaultTableName();
//		}
//
//		if (requireChange(getTableName(), tableName)) {
//			String oldValue = getTableName();
//			this.tableName = tableName;
//			updateIsTableNameSynchronized();
//			setChanged();
//			notifyObservers(new SGAttributeModification("tableName", oldValue, tableName));
//		}
	}

	public boolean getIsTableNameSynchronized() {
		return isTableNameSynchronized;
	}

	public void setIsTableNameSynchronized(boolean isTableNameSynchronized) {
		if (requireChange(this.isTableNameSynchronized, isTableNameSynchronized)) {
			boolean oldValue = this.isTableNameSynchronized;
			this.isTableNameSynchronized = isTableNameSynchronized;
			setChanged();
			notifyObservers(new SGAttributeModification("isTableNameSynchronized", oldValue, isTableNameSynchronized));
		}
	}

	public boolean getOptimisticLocking() {
		return optimisticLocking;
	}

	public void setOptimisticLocking(boolean optimisticLocking) {
		if (requireChange(this.optimisticLocking, optimisticLocking)) {
			Object oldValue = this.optimisticLocking;
			this.optimisticLocking = optimisticLocking;
			setChanged();
			notifyObservers(new SGAttributeModification("optimisticLocking", oldValue, optimisticLocking));
		}
	}

	public HibernateEntity getFather() {
		return father;
	}

	public void setFather(HibernateEntity father) {
		if (requireChange(this.father, father)) {
			Object oldValue = this.father;
			this.father = father;
			setChanged();
			notifyObservers(new SGAttributeModification("father", oldValue, father));
		}
	}

	/**
	 * Return the attributes stored in this entity. Attributes are sorted by name.
	 * 
	 * @return sorted attributes stored in this entity.
	 */
	public Vector<HibernateAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Vector<HibernateAttribute> attributes) {
		if (requireChange(this.attributes, attributes)) {
			Vector<HibernateAttribute> oldValue = this.attributes;

			for (HibernateAttribute attribute : oldValue) {
				attribute.deleteObserver(this);
			}

			this.attributes = attributes;

			for (HibernateAttribute attribute : attributes) {
				attribute.addObserver(this);
			}

			sortAttributes();
			setChanged();
			notifyObservers(new SGAttributeModification("attributes", oldValue, attributes));
		}
	}

	public void addToAttributes(HibernateAttribute attribute) {
		attribute.setHibernateEntity(this);
		attributes.add(attribute);

		attribute.addObserver(this);
		sortAttributes();

		setChanged();
		notifyObservers(new SGObjectAddedToListModification<HibernateAttribute>("attributes", attribute));
	}

	public void removeFromAttributes(HibernateAttribute attribute) {
		if (attributes.remove(attribute)) {
			attribute.deleteObserver(this);
			setChanged();
			notifyObservers(new SGObjectRemovedFromListModification<HibernateAttribute>("attributes", attribute));
		}
	}

	/**
	 * Retrieve the Hibernate Attribute with the specified name.
	 * 
	 * @param attributeName
	 * @return the retrieved Hibernate Attribute if any, null otherwise.
	 */
	public HibernateAttribute getAttribute(String attributeName) {
		for (HibernateAttribute attribute : attributes) {
			if (attribute.getName().equals(attributeName)) {
				return attribute;
			}
		}

		return null;
	}

	/**
	 * Retrieve the Hibernate Attribute linked to the specified DMProperty.
	 * 
	 * @param dmProperty
	 * @return the retrieved Hibernate Attribute if any, null otherwise.
	 */
	public HibernateAttribute getAttribute(DMProperty dmProperty) {
		for (HibernateAttribute attribute : attributes) {
			if (attribute.getLinkedFlexoModelObject() == dmProperty) {
				return attribute;
			}
		}

		return null;
	}

	/**
	 * Return the relationships stored in this entity. Relationships are sorted by name.
	 * 
	 * @return sorted relationships stored in this entity.
	 */
	public Vector<HibernateRelationship> getRelationships() {
		return relationships;
	}

	public void setRelationships(Vector<HibernateRelationship> relationships) {
		if (requireChange(this.relationships, relationships)) {
			Vector<HibernateRelationship> oldValue = this.relationships;

			for (HibernateRelationship relationship : oldValue) {
				relationship.deleteObserver(this);
			}

			this.relationships = relationships;

			for (HibernateRelationship relationship : relationships) {
				relationship.addObserver(this);
			}

			sortRelationships();
			setChanged();
			notifyObservers(new SGAttributeModification("relationships", oldValue, relationships));
		}
	}

	public void addToRelationships(HibernateRelationship relationship) {
		relationship.setHibernateEntity(this);
		relationships.add(relationship);

		relationship.addObserver(this);
		sortRelationships();

		setChanged();
		notifyObservers(new SGObjectAddedToListModification<HibernateRelationship>("relationships", relationship));
	}

	public void removeFromRelationships(HibernateRelationship relationship) {
		if (relationships.remove(relationship)) {
			relationship.deleteObserver(this);
			setChanged();
			notifyObservers(new SGObjectRemovedFromListModification<HibernateRelationship>("relationships", relationship));
		}
	}

	/**
	 * Retrieve the Hibernate Relationship with the specified name.
	 * 
	 * @param relationshipName
	 * @return the retrieved Relationship Attribute if any, null otherwise.
	 */
	public HibernateRelationship getRelationship(String relationshipName) {
		for (HibernateRelationship relationship : relationships) {
			if (relationship.getName().equals(relationshipName)) {
				return relationship;
			}
		}

		return null;
	}

	/**
	 * Retrieve the Hibernate Relationship linked to the specified DMProperty.
	 * 
	 * @param dmProperty
	 * @return the retrieved Relationship Attribute if any, null otherwise.
	 */
	public HibernateRelationship getRelationship(DMProperty dmProperty) {
		for (HibernateRelationship relationship : relationships) {
			if (relationship.getLinkedFlexoModelObject() == dmProperty) {
				return relationship;
			}
		}

		return null;
	}

	public Vector<HibernateEntity> getChildren() {
		return children;
	}

	public void setChildren(Vector<HibernateEntity> children) {
		if (requireChange(this.children, children)) {
			Object oldValue = this.children;
			this.children = children;
			setChanged();
			notifyObservers(new SGAttributeModification("children", oldValue, children));
		}
	}

	public void addToChildren(HibernateEntity child) {
		children.add(child);
		setChanged();
		notifyObservers(new SGObjectAddedToListModification<HibernateEntity>("children", child));
	}

	public void removeFromChildren(HibernateEntity child) {
		if (children.remove(child)) {
			setChanged();
			notifyObservers(new SGObjectRemovedFromListModification<HibernateEntity>("children", child));
		}
	}

	public HibernateModel getHibernateModel() {
		return hibernateModel;
	}

	/**
	 * Called only from HibernateImplementation at deserialisation or at entity creation
	 * 
	 * @param hibernateModel
	 */
	protected void setHibernateModel(HibernateModel hibernateModel) {
		this.hibernateModel = hibernateModel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HibernateImplementation getTechnologyModuleImplementation() {
		return HibernateImplementation.getHibernateImplementation(getImplementationModel());
	}
}
