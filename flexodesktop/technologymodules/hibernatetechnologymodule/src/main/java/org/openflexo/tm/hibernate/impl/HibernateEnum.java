/**
 * 
 */
package org.openflexo.tm.hibernate.impl;

import java.util.Vector;

import javax.naming.InvalidNameException;

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
 *
 * @author Nicolas Daniels
 */
public class HibernateEnum extends TechnologyModelObject {

	public static final String CLASS_NAME_KEY = "hibernate_enum";

	protected HibernateEnumContainer hibernateEnumContainer;
	protected Vector<HibernateEnumValue> hibernateEnumValues = new Vector<HibernateEnumValue>();

	// ================ //
	// = Constructors = //
	// ================ //

	/**
	 * Build a new Hibernate enum for the specified implementation model builder.<br/>
	 * This constructor is namely invoked during deserialization.
	 * 
	 * @param builder the builder that will create this enum
	 */
	public HibernateEnum(ImplementationModelBuilder builder) {
		this(builder.implementationModel);
		initializeDeserialization(builder);
	}

	/**
	 * Build a new Hibernate enum for the specified implementation model.
	 * 
	 * @param implementationModel the implementation model where to create this Hibernate enum
	 */
	public HibernateEnum(ImplementationModel implementationModel) {
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
		return getHibernateEnumContainer().getFullyQualifiedName() + "." + getName();
	}

	/* ===================== */
	/* ====== Actions ====== */
	/* ===================== */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {

		if (getHibernateEnumContainer() != null)
			getHibernateEnumContainer().removeFromHibernateEnums(this);

		setChanged();
		notifyObservers(new SGObjectDeletedModification());
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
	public void setName(String name) throws DuplicateResourceException, InvalidNameException {
		name = JavaUtils.getClassName(name);
		super.setName(name);
	}

	/**
	 * Return the hibernateEnumValues stored in this model. HibernateEnumValues are sorted by name.
	 * 
	 * @return sorted hibernateEnumValues stored in this model.
	 */
	public Vector<HibernateEnumValue> getHibernateEnumValues() {
		return hibernateEnumValues;
	}

	public void setHibernateEnumValues(Vector<HibernateEnumValue> hibernateEnumValues) {
		if (requireChange(this.hibernateEnumValues, hibernateEnumValues)) {
			Vector<HibernateEnumValue> oldValue = this.hibernateEnumValues;
			this.hibernateEnumValues = hibernateEnumValues;
			setChanged();
			notifyObservers(new SGAttributeModification("hibernateEnumValues", oldValue, hibernateEnumValues));
		}
	}

	public void addToHibernateEnumValues(HibernateEnumValue hibernateEnumValue) {
		hibernateEnumValue.setHibernateEnum(this);
		hibernateEnumValues.add(hibernateEnumValue);

		setChanged();
		notifyObservers(new SGObjectAddedToListModification("hibernateEnumValues", hibernateEnumValue));
	}

	public void removeFromHibernateEnumValues(HibernateEnumValue hibernateEnumValue) {
		if (hibernateEnumValues.remove(hibernateEnumValue)) {
			setChanged();
			notifyObservers(new SGObjectRemovedFromListModification("hibernateEnumValues", hibernateEnumValue));
		}
	}

	public HibernateEnumContainer getHibernateEnumContainer() {
		return hibernateEnumContainer;
	}

	/**
	 * Called only from HibernateEnumContainer at deserialisation or at entity creation
	 * 
	 * @param hibernateModel
	 */
	protected void setHibernateEnumContainer(HibernateEnumContainer hibernateEnumContainer) {
		this.hibernateEnumContainer = hibernateEnumContainer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TechnologyModuleImplementation getTechnologyModuleImplementation() {
		return getHibernateEnumContainer().getTechnologyModuleImplementation();
	}

}
