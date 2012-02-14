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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.apache.commons.lang.StringUtils;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.dm.DMPropertyNameChanged;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.LinkableTechnologyModelObject;
import org.openflexo.foundation.sg.implmodel.event.SGAttributeModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectDeletedModification;
import org.openflexo.foundation.xml.ImplementationModelBuilder;
import org.openflexo.tm.hibernate.impl.enums.HibernateAttributeType;
import org.openflexo.tm.hibernate.impl.utils.HibernateUtils;
import org.openflexo.toolbox.JavaUtils;

/**
 * This class defines property fields for the mapping attributes in an Hibernate implementation.
 * 
 * @author Emmanuel Koch, Blue Pimento Services SPRL
 */
public class HibernateAttribute extends LinkableTechnologyModelObject<DMProperty> {

	protected static final Logger logger = Logger.getLogger(HibernateAttribute.class.getPackage().getName());

	public static final String CLASS_NAME_KEY = "hibernate_attribute";

	/** Entity of this attribute. */
	protected HibernateEntity hibernateEntity;

	/** Name of the database column to bind with this attribute. */
	protected String columnName;
	protected boolean isColumnNameSynchronized;

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

	/**
	 * @param implementationModel
	 *            the implementation model where to create this Hibernate attribute
	 * @param linkedFlexoModelObject
	 *            Can be null
	 */
	public HibernateAttribute(ImplementationModel implementationModel, DMProperty linkedFlexoModelObject) {
		super(implementationModel, linkedFlexoModelObject);
		isColumnNameSynchronized = true;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String escapeName(String name) {
		return JavaUtils.getVariableName(name);
	}

	/**
	 * Retrieve the default column name to use for this attribute based on the entity name.
	 * 
	 * @return the default column name to use for this attribute.
	 */
	public String getDefaultColumnName() {
		return escapeColumnName(getName());
	}

	private String escapeColumnName(String columnName) {
		return getTechnologyModuleImplementation().getDbObjectName(columnName);
	}

	public void updateIsColumnNameSynchronized() {
		if (!isDeserializing) {
			String defaultColumnName = getDefaultColumnName();
			setIsColumnNameSynchronized(defaultColumnName != null
					&& (StringUtils.isEmpty(getColumnName()) || StringUtils.equals(defaultColumnName, getColumnName())));
		}
	}

	/**
	 * Update the column name with this object name if necessary.
	 */
    //todo ensure this is covered
	public void updateColumnNameIfNecessary() {
		if (getIsColumnNameSynchronized()) {
			setColumnName(getName());
		} else {
			updateIsColumnNameSynchronized(); // Update this anyway in case the name is set to columnName. In this case synchronization is
												// set back to true.
		}
	}

	/**
	 * Update the type with the linked Flexo Model Object one if necessary.
	 */
    //todo ensure this is covered
	public void updateTypeIfNecessary() {
		if (getLinkedFlexoModelObject() != null) {
			DMProperty dmProperty = getLinkedFlexoModelObject();
			if (HibernateUtils.getIsHibernateAttributeRepresented(dmProperty)) {
				HibernateAttributeType type = HibernateUtils.getHibernateAttributeTypeFromDMType(dmProperty.getType());
				if (type != null) {
					setType(type);
					if (type == HibernateAttributeType.ENUM) {
						setHibernateEnum(getHibernateEntity().getHibernateModel().getHibernateEnumContainer()
								.getHibernateEnum(dmProperty.getType().getBaseEntity()));
					}
				}
			} else {
				// Type is changed to a relationship one. Delete this attribute and create a new relationship
				this.setLinkedFlexoModelObject(null);
				getHibernateEntity().createHibernateObjectBasedOnDMProperty(dmProperty);
				this.delete();
			}
		}
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
		notifyObservers(new SGObjectDeletedModification<HibernateAttribute>(this));
		super.delete();
		deleteObservers();
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
		if (observable == getHibernateEnum() && dataModification instanceof SGObjectDeletedModification) {
			setHibernateEnum(null);
			updateTypeIfNecessary();
		}
	}

	/* ===================== */
	/* == Getter / Setter == */
	/* ===================== */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLinkedFlexoModelObject(DMProperty linkedFlexoModelObject) {
		if (linkedFlexoModelObject == null || HibernateUtils.getIsHibernateAttributeRepresented(linkedFlexoModelObject)) {
			super.setLinkedFlexoModelObject(linkedFlexoModelObject);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING,
						"Cannot set linked object to Hibernate Attribute with an DM Property with type not basic. DM Property: "
								+ linkedFlexoModelObject.getName());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		name = escapeName(name);

		if (StringUtils.isEmpty(name)) {
			name = getDerivedName();
		}

		if (requireChange(getName(), name)) {
			super.setName(name);
			updateColumnNameIfNecessary();
		}
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		columnName = escapeColumnName(columnName);

		if (StringUtils.isEmpty(columnName)) {
			columnName = getDefaultColumnName();
		}

		if (requireChange(getColumnName(), columnName)) {
			String oldValue = getColumnName();
			this.columnName = columnName;
			updateIsColumnNameSynchronized();
			setChanged();
			notifyObservers(new SGAttributeModification("columnName", oldValue, columnName));
		}
	}

	public boolean getIsColumnNameSynchronized() {
		return isColumnNameSynchronized;
	}

	public void setIsColumnNameSynchronized(boolean isColumnNameSynchronized) {
		if (requireChange(this.isColumnNameSynchronized, isColumnNameSynchronized)) {
			boolean oldValue = this.isColumnNameSynchronized;
			this.isColumnNameSynchronized = isColumnNameSynchronized;
			setChanged();
			notifyObservers(new SGAttributeModification("isColumnNameSynchronized", oldValue, isColumnNameSynchronized));
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
			HibernateEnum oldValue = this.hibernateEnum;

			if (oldValue != null) {
				oldValue.deleteObserver(this);
			}

			this.hibernateEnum = hibernateEnum;

			if (this.hibernateEnum != null) {
				this.hibernateEnum.addObserver(this);
			}

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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HibernateImplementation getTechnologyModuleImplementation() {
		return HibernateImplementation.getHibernateImplementation(getImplementationModel());
	}
}
