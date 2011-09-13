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

import java.util.Collections;
import java.util.Vector;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModelObject;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleImplementation;
import org.openflexo.foundation.sg.implmodel.event.SGAttributeModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectAddedToListModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectDeletedModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectRemovedFromListModification;
import org.openflexo.foundation.xml.ImplementationModelBuilder;
import org.openflexo.toolbox.JavaUtils;


/**
 * This class defines an entity in the context of an Hibernate implementation.
 * 
 * @author Emmanuel Koch, Blue Pimento Services SPRL
 */
public class HibernateEntity extends TechnologyModelObject implements FlexoObserver {

	public static final String CLASS_NAME_KEY = "hibernate_entity";

	protected HibernateModel hibernateModel;

	/**
	 * Returns the name of the table that will be used to store this entity in the storage system. If <code>null</code>, the name define the
	 * table name.
	 */
	protected String tableName;

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

	/* ===================== */
	/* ====== Actions ====== */
	/* ===================== */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {

		for (HibernateRelationship hibernateRelationship : new Vector<HibernateRelationship>(getRelationships()))
			hibernateRelationship.delete();

		for (HibernateAttribute hibernateAttribute : new Vector<HibernateAttribute>(getAttributes()))
			hibernateAttribute.delete();

		for (HibernateEntity hibernateEntity : new Vector<HibernateEntity>(getChildren()))
			hibernateEntity.delete();

		if (getFather() != null)
			getFather().removeFromChildren(this);

		if (getHibernateModel() != null)
			getHibernateModel().removeFromEntities(this);
		
		setChanged();
		notifyObservers(new SGObjectDeletedModification());
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
			if (entity != this && entity.getFather() == null)
				result.add(entity);
		}

		return result;
	}

	/**
	 * Sort attributes stored in this entity by their name.
	 */
	public void sortAttributes() {
		Collections.sort(this.attributes, new FlexoModelObject.FlexoNameComparator<FlexoModelObject>());
	}

	/**
	 * Sort relationships stored in this entity by their name.
	 */
	public void sortRelationships() {
		Collections.sort(this.relationships, new FlexoModelObject.FlexoNameComparator<FlexoModelObject>());
	}

	/**
	 * Update the table name based on the entity name
	 */
	public void updateTableName() {
		if (!isDeserializing && getHibernateModel() != null)
			setTableName(getHibernateModel().getHibernateImplementation().getDbObjectName(getName()));
	}

	/* ============== */
	/* == Observer == */
	/* ============== */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof NameChanged) {
			if (observable instanceof HibernateAttribute)
				sortAttributes();
			if (observable instanceof HibernateRelationship)
				sortRelationships();
		}
	}

	/* ===================== */
	/* == Getter / Setter == */
	/* ===================== */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) throws DuplicateResourceException, InvalidNameException {
		name = JavaUtils.getClassName(name);
		if (requireChange(getName(), name)) {
			super.setName(name);
			updateTableName();
		}
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		if (requireChange(this.tableName, tableName)) {
			String oldValue = this.tableName;
			this.tableName = tableName;
			setChanged();
			notifyObservers(new SGAttributeModification("tableName", oldValue, tableName));
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

			for (HibernateAttribute attribute : oldValue)
				attribute.deleteObserver(this);

			this.attributes = attributes;

			for (HibernateAttribute attribute : attributes)
				attribute.addObserver(this);

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
		notifyObservers(new SGObjectAddedToListModification("attributes", attribute));
	}

	public void removeFromAttributes(HibernateAttribute attribute) {
		if (attributes.remove(attribute)) {
			attribute.deleteObserver(this);
			setChanged();
			notifyObservers(new SGObjectRemovedFromListModification("attributes", attribute));
		}
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

			for (HibernateRelationship relationship : oldValue)
				relationship.deleteObserver(this);

			this.relationships = relationships;

			for (HibernateRelationship relationship : relationships)
				relationship.addObserver(this);

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
		notifyObservers(new SGObjectAddedToListModification("relationships", relationship));
	}

	public void removeFromRelationships(HibernateRelationship relationship) {
		if (relationships.remove(relationship)) {
			relationship.deleteObserver(this);
			setChanged();
			notifyObservers(new SGObjectRemovedFromListModification("relationships", relationship));
		}
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
		notifyObservers(new SGObjectAddedToListModification("children", child));
	}

	public void removeFromChildren(HibernateEntity child) {
		if (children.remove(child)) {
			setChanged();
			notifyObservers(new SGObjectRemovedFromListModification("children", child));
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
		updateTableName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TechnologyModuleImplementation getTechnologyModuleImplementation() {
		return getHibernateModel().getTechnologyModuleImplementation();
	}
}
