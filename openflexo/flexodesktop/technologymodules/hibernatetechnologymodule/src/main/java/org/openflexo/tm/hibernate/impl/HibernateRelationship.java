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

import java.util.Arrays;
import java.util.Vector;

import javax.naming.InvalidNameException;

import org.apache.commons.lang.StringUtils;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModelObject;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleImplementation;
import org.openflexo.foundation.sg.implmodel.event.SGAttributeModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectAddedToListModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectDeletedModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectRemovedFromListModification;
import org.openflexo.foundation.xml.ImplementationModelBuilder;
import org.openflexo.tm.hibernate.impl.enums.HibernateCascade;
import org.openflexo.toolbox.JavaUtils;


/**
 * This class defines relationships for the mapping relationships in a Hibernate implementation.
 * 
 * @author Emmanuel Koch, Blue Pimento Services SPRL
 */
public class HibernateRelationship extends TechnologyModelObject implements FlexoObserver {

	public static final String CLASS_NAME_KEY = "hibernate_entity";

	/** Returns the cascade behaviors of this relationship. */
	protected Vector<HibernateCascade> cascadeTypes = new Vector<HibernateCascade>();

	/** Indicate that the relation entities must not be fetched when fetching the origin entity. */
	protected boolean lazy = true;

	/** Indicate that the relation cannot be <code>null</code>. */
	protected boolean notNull;

	/**
	 * Flag to indicate this relation is the inverse of another one. A two ways relation must always define which one is the original or the
	 * inverse one.
	 */
	protected boolean isInverse;

	/** Indicates if this relationship is a to-many relation. */
	protected boolean toMany;

	/** The name of the index column, to naturally sort records in this to-many relationship (generate List instead of Set in POJOs). */
	protected String indexColumnName;

	/** The entity where this relationship is defined. */
	protected HibernateEntity hibernateEntity;

	/** The entity / entities returned by this relationship. */
	protected HibernateEntity destination;

	/** The inverse relation of this one. */
	protected HibernateRelationship inverse;

	// ================ //
	// = Constructors = //
	// ================ //

	/**
	 * Build a new Hibernate relationship for the specified implementation model builder.<br/>
	 * This constructor is namely invoked during unserialization.
	 * 
	 * @param builder
	 *            the builder that will create this relationship
	 */
	public HibernateRelationship(ImplementationModelBuilder builder) {
		this(builder.implementationModel);
		initializeDeserialization(builder);
	}

	/**
	 * Build a new Hibernate relationship for the specified implementation model.
	 * 
	 * @param implementationModel
	 *            the implementation model where to create this Hibernate relationship
	 */
	public HibernateRelationship(ImplementationModel implementationModel) {
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
		return getHibernateEntity().getFullyQualifiedName() + "." + getName();
	}

	/* ===================== */
	/* ====== Actions ====== */
	/* ===================== */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {
		if (getHibernateEntity() != null) {
			getHibernateEntity().removeFromRelationships(this);
		}

		if (getInverse() != null) {
			if (getInverse().getIsInverse())
				getInverse().delete();
			else
				getInverse().setInverse(null);
		}

		setChanged();
		notifyObservers(new SGObjectDeletedModification());
		super.delete();
		deleteObservers();
	}

	/**
	 * Used in inspector
	 * 
	 * @return All HibernateCascades
	 */
	public Vector<HibernateCascade> getAllHibernateCascades() {
		return new Vector<HibernateCascade>(Arrays.asList(HibernateCascade.values()));
	}

	public boolean getHasInverse() {
		return getInverse() != null;
	}

	/**
	 * Set if this relationship inverse must exists. <br>
	 * If <code>hasInverse</code> is true and if the inverse relationship is currently null, a new relation ship will be created on the destination entity and will be set as inverse of this one. <br>
	 * If <code>hasInverse</code> is true and if the inverse relationship is currently not null, the inverse relationship will be deleted
	 * 
	 * @param hasInverse
	 */
	public void setHasInverse(boolean hasInverse) {

		if (hasInverse && !getHasInverse() && getDestination() != null) {
			HibernateRelationship relationship = new HibernateRelationship(getImplementationModel());
			getDestination().addToRelationships(relationship);

			relationship.setDestination(this.getHibernateEntity());
			relationship.setIsInverse(true);
			relationship.setToMany(!getToMany());
			setInverse(relationship);

			setChanged();
			notifyObservers(new SGAttributeModification("hasInverse", false, true));
		} else if (!hasInverse && getHasInverse()) {
			setIsInverse(false);
			getInverse().delete();
			setChanged();
			notifyObservers(new SGAttributeModification("hasInverse", true, false));
		}
	}

	/**
	 * Get the relationship name if any non empty one exists, otherwise build an automatic name for this relationship based on its destination and its cardinality.
	 * 
	 * @return the relationship name if exists, the built name otherwise.
	 */
	public String getNameOrBuiltAutomaticOne() {

		if (!StringUtils.isBlank(getName()))
			return getName();

		if (getDestination() == null)
			return JavaUtils.getVariableName("relationship" + getHibernateEntity().getRelationships().size());

		String builtName = getDestination().getName();
		if (getToMany() && !builtName.endsWith("s") && !builtName.endsWith("x"))
			builtName = builtName + "s";

		builtName = JavaUtils.getVariableName(builtName);

		if (getHasInverse() && getIsInverse()) { // Checks that the inverse has not the same name.
			if (builtName.equals(getInverse().getName()))
				builtName = builtName + "Inverse";
		}

		return builtName;
	}

	public void setNameOrBuiltAutomaticOne(String name) throws DuplicateResourceException, InvalidNameException {
		setName(name);
	}

	/* ============== */
	/* == Observer == */
	/* ============== */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getDestination() && dataModification instanceof SGObjectDeletedModification)
			setDestination(null);
	}

	/* ===================== */
	/* == Getter / Setter == */
	/* ===================== */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) throws DuplicateResourceException, InvalidNameException {
		name = JavaUtils.getVariableName(name);
		super.setName(name);
	}

	public Vector<HibernateCascade> getCascadeTypes() {
		return cascadeTypes;
	}

	public void setCascadeTypes(Vector<HibernateCascade> cascadeTypes) {
		if (requireChange(this.cascadeTypes, cascadeTypes)) {
			Object oldValue = this.cascadeTypes;
			this.cascadeTypes = cascadeTypes;
			setChanged();
			notifyObservers(new SGAttributeModification("cascadeTypes", oldValue, cascadeTypes));
		}
	}

	public void addToCascadeTypes(HibernateCascade cascadeType) {
		cascadeTypes.add(cascadeType);
		setChanged();
		notifyObservers(new SGObjectAddedToListModification("cascadeTypes", cascadeType));
	}

	public void removeFromCascadeTypes(HibernateCascade cascadeType) {
		if (cascadeTypes.remove(cascadeType)) {
			setChanged();
			notifyObservers(new SGObjectRemovedFromListModification("cascadeTypes", cascadeType));
		}
	}

	public boolean getLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		if (requireChange(this.lazy, lazy)) {
			Object oldValue = this.lazy;
			this.lazy = lazy;
			setChanged();
			notifyObservers(new SGAttributeModification("lazy", oldValue, lazy));
		}
	}

	public boolean getNotNull() {
		return notNull;
	}

	public void setNotNull(boolean notNull) {
		if (requireChange(this.notNull, notNull)) {
			Object oldValue = this.notNull;
			this.notNull = notNull;
			setChanged();
			notifyObservers(new SGAttributeModification("notNull", oldValue, notNull));
		}
	}

	public boolean getIsInverse() {
		return isInverse;
	}

	public void setIsInverse(boolean isInverse) {
		if (requireChange(this.isInverse, isInverse)) {
			Object oldValue = this.isInverse;
			this.isInverse = isInverse;

			setChanged();
			notifyObservers(new SGAttributeModification("isInverse", oldValue, isInverse));

			if (getInverse() != null)
				getInverse().setIsInverse(!isInverse);
		}
	}

	public boolean getToMany() {
		return toMany;
	}

	public void setToMany(boolean toMany) {
		if (requireChange(this.toMany, toMany)) {
			Object oldValue = this.toMany;
			this.toMany = toMany;
			setChanged();
			notifyObservers(new SGAttributeModification("toMany", oldValue, toMany));
		}
	}

	public String getIndexColumnName() {
		return indexColumnName;
	}

	public void setIndexColumnName(String indexColumnName) {
		if (requireChange(this.indexColumnName, indexColumnName)) {
			Object oldValue = this.indexColumnName;
			this.indexColumnName = indexColumnName;
			setChanged();
			notifyObservers(new SGAttributeModification("indexColumnName", oldValue, indexColumnName));
		}
	}

	public HibernateEntity getDestination() {
		return destination;
	}

	public boolean getHasDestination() {
		return getDestination() != null;
	}

	public void setDestination(HibernateEntity destination) {
		if (requireChange(this.destination, destination)) {
			HibernateEntity oldValue = this.destination;
			if (oldValue != null)
				oldValue.deleteObserver(this);
			this.destination = destination;
			if (this.destination != null)
				this.destination.addObserver(this);
			setChanged();
			notifyObservers(new SGAttributeModification("destination", oldValue, destination));
		}
	}

	public HibernateRelationship getInverse() {
		return inverse;
	}

	public void setInverse(HibernateRelationship inverse) {
		if (requireChange(this.inverse, inverse)) {
			HibernateRelationship oldValue = this.inverse;
			this.inverse = inverse;

			setChanged();
			notifyObservers(new SGAttributeModification("inverse", oldValue, inverse));

			if (this.inverse != null)
				this.inverse.setInverse(this);
			if (oldValue != null)
				oldValue.setInverse(null);
		}
	}

	public HibernateEntity getHibernateEntity() {
		return hibernateEntity;
	}

	/**
	 * Called only from HibernateEntity at deserialization or at relationship creation
	 * 
	 * @param entity
	 */
	protected void setHibernateEntity(HibernateEntity entity) {
		this.hibernateEntity = entity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TechnologyModuleImplementation getTechnologyModuleImplementation() {
		return getHibernateEntity().getTechnologyModuleImplementation();
	}
}
