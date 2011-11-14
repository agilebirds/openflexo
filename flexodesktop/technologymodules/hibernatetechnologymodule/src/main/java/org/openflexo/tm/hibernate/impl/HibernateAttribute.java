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

import javax.naming.InvalidNameException;

import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModelObject;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleImplementation;
import org.openflexo.foundation.sg.implmodel.event.SGAttributeModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectDeletedModification;
import org.openflexo.foundation.xml.ImplementationModelBuilder;
import org.openflexo.tm.hibernate.impl.enums.HibernateAttributeType;
import org.openflexo.toolbox.JavaUtils;

/**
 * This class defines property fields for the mapping attributes in an Hibernate implementation.
 * 
 * @author Emmanuel Koch, Blue Pimento Services SPRL
 */
public class HibernateAttribute extends TechnologyModelObject {

	public static final String CLASS_NAME_KEY = "hibernate_attribute";

	/** Entity of this attribute. */
	protected HibernateEntity hibernateEntity;

	/** Name of the database column to bind with this attribute. */
	protected String columnName;

	/** Type of this attribute: string , integer, boolean... */
	public HibernateAttributeType type;

	/** When this attribute is type of enumeration, this defines the enumeration content. */
	public HibernateEnum hibernateEnum;

	/** Indicate if this attribute can be <code>null</code>. */
	protected boolean notNull;

	/** Indicate if this attribute is (a part of) the primary key of the entity this attribute belongs. */
	protected boolean primaryKey;

	/** Indicate if this attribute value must be unique for all this entity records. */
	protected boolean unique;

	/** When this attribute is type of string, this returns the maximum number of chars it can contains */
	protected Integer length = 255;

	/** Indicates if Hibernate must persist this attribute on entity save */
	protected boolean update = true;

	// ================ //
	// = Constructors = //
	// ================ //

	/**
	 * Build a new Hibernate attribute for the specified implementation model builder.<br/>
	 * This constructor is namely invoked during deserialization.
	 * 
	 * @param builder
	 *            the builder that will create this attribute
	 */
	public HibernateAttribute(ImplementationModelBuilder builder) {
		this(builder.implementationModel);
		initializeDeserialization(builder);
	}

	/**
	 * Build a new Hibernate attribute for the specified implementation model.
	 * 
	 * @param implementationModel
	 *            the implementation model where to create this Hibernate attribute
	 */
	public HibernateAttribute(ImplementationModel implementationModel) {
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
			getHibernateEntity().removeFromAttributes(this);
		}

		setChanged();
		notifyObservers(new SGObjectDeletedModification());
		super.delete();
		deleteObservers();
	}

	/**
	 * Update the column name based on the attribute name
	 */
	public void updateColumnName() {
		if (!isDeserializing && getHibernateEntity() != null) {
			setColumnName(getHibernateEntity().getHibernateModel().getHibernateImplementation().getDbObjectName(getName()));
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
		name = JavaUtils.getVariableName(name);
		if (requireChange(getName(), name)) {
			super.setName(name);
			updateColumnName();
		}
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		if (requireChange(this.columnName, columnName)) {
			String oldValue = this.columnName;
			this.columnName = columnName;
			setChanged();
			notifyObservers(new SGAttributeModification("columnName", oldValue, columnName));
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

	public boolean getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		if (requireChange(this.primaryKey, primaryKey)) {
			Object oldValue = this.primaryKey;
			this.primaryKey = primaryKey;
			setChanged();
			notifyObservers(new SGAttributeModification("primaryKey", oldValue, primaryKey));
		}
	}

	public boolean getUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		if (requireChange(this.unique, unique)) {
			Object oldValue = this.unique;
			this.unique = unique;
			setChanged();
			notifyObservers(new SGAttributeModification("unique", oldValue, unique));
		}
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		if (requireChange(this.length, length)) {
			Object oldValue = this.length;
			this.length = length;
			setChanged();
			notifyObservers(new SGAttributeModification("length", oldValue, length));
		}
	}

	public boolean getUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		if (requireChange(this.update, update)) {
			Object oldValue = this.update;
			this.update = update;
			setChanged();
			notifyObservers(new SGAttributeModification("update", oldValue, update));
		}
	}

	public HibernateAttributeType getType() {
		return type;
	}

	public void setType(HibernateAttributeType type) {
		if (requireChange(this.type, type)) {
			Object oldValue = this.type;
			this.type = type;
			setChanged();
			notifyObservers(new SGAttributeModification("type", oldValue, type));
		}
	}

	public HibernateEnum getHibernateEnum() {
		return hibernateEnum;
	}

	public void setHibernateEnum(HibernateEnum hibernateEnum) {
		if (requireChange(this.hibernateEnum, hibernateEnum)) {
			Object oldValue = this.hibernateEnum;
			this.hibernateEnum = hibernateEnum;
			setChanged();
			notifyObservers(new SGAttributeModification("hibernateEnum", oldValue, hibernateEnum));
		}
	}

	public HibernateEntity getHibernateEntity() {
		return hibernateEntity;
	}

	/**
	 * Called only from HibernateEntity at deserialization or at attribute creation
	 * 
	 * @param entity
	 */
	protected void setHibernateEntity(HibernateEntity entity) {
		this.hibernateEntity = entity;
		updateColumnName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TechnologyModuleImplementation getTechnologyModuleImplementation() {
		return getHibernateEntity().getTechnologyModuleImplementation();
	}
}
