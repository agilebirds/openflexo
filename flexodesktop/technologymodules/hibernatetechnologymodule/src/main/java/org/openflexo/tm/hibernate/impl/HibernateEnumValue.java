/**
 * 
 */
package org.openflexo.tm.hibernate.impl;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.data.FlexoEnumValue;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.dm.DMEntityNameChanged;
import org.openflexo.foundation.dm.dm.DMPropertyNameChanged;
import org.openflexo.foundation.dm.dm.PropertyRegistered;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.LinkableTechnologyModelObject;
import org.openflexo.foundation.sg.implmodel.event.SGObjectDeletedModification;
import org.openflexo.foundation.xml.ImplementationModelBuilder;
import org.openflexo.toolbox.JavaUtils;

/**
 * 
 * @author Nicolas Daniels
 */
public class HibernateEnumValue extends LinkableTechnologyModelObject<DMProperty> {

	public static final String CLASS_NAME_KEY = "hibernate_enum_value";

	protected HibernateEnum hibernateEnum;

	// ================ //
	// = Constructors = //
	// ================ //

	/**
	 * Build a new Hibernate enum value for the specified implementation model builder.<br/>
	 * This constructor is namely invoked during deserialization.
	 * 
	 * @param builder
	 *            the builder that will create this enum value
	 */
	public HibernateEnumValue(ImplementationModelBuilder builder) {
		this(builder.implementationModel);
		initializeDeserialization(builder);
	}

	/**
	 * Build a new Hibernate enum value for the specified implementation model.
	 * 
	 * @param implementationModel
	 *            the implementation model where to create this Hibernate enum value
	 */
	public HibernateEnumValue(ImplementationModel implementationModel) {
		super(implementationModel);
	}

    /**
	 * @param implementationModel
	 *            the implementation model where to create this Hibernate enum
	 * @param linkedFlexoModelObject
	 *            Can be null
	 */
	public HibernateEnumValue(ImplementationModel implementationModel, DMProperty linkedFlexoModelObject) {
		super(implementationModel, linkedFlexoModelObject);
        setName(linkedFlexoModelObject.getName());
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
		return getHibernateEnum().getFullyQualifiedName() + "." + getName();
	}

    public String getDerivedName() {
        return getLinkedFlexoModelObject() != null ? escapeName(getLinkedFlexoModelObject().getName().toUpperCase()) : null;
    }
	/* ===================== */
	/* ====== Actions ====== */
	/* ===================== */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {

		if (getHibernateEnum() != null) {
			getHibernateEnum().removeFromHibernateEnumValues(this);
		}

		setChanged();
		notifyObservers(new SGObjectDeletedModification<HibernateEnumValue>(this));
		super.delete();
		deleteObservers();
	}

	/* ===================== */
	/* == Getter / Setter == */
	/* ===================== */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		name = JavaUtils.getConstantName(name);
		super.setName(name);
	}

	public HibernateEnum getHibernateEnum() {
		return hibernateEnum;
	}

	/**
	 * Called only from HibernateEnumat deserialisation or at entity creation
	 * 
	 * @param hibernateEnum
	 */
	protected void setHibernateEnum(HibernateEnum hibernateEnum) {
		this.hibernateEnum = hibernateEnum;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HibernateImplementation getTechnologyModuleImplementation() {
		return HibernateImplementation.getHibernateImplementation(getImplementationModel());
	}

}
