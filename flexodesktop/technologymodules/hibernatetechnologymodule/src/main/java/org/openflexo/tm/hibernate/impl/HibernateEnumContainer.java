/**
 * 
 */
package org.openflexo.tm.hibernate.impl;

import java.util.Collections;
import java.util.Vector;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModelObject;
import org.openflexo.foundation.sg.implmodel.event.SGAttributeModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectAddedToListModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectDeletedModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectRemovedFromListModification;
import org.openflexo.foundation.xml.ImplementationModelBuilder;
import org.openflexo.tm.hibernate.impl.comparator.HibernateLinkableObjectComparator;

/**
 * 
 * @author Nicolas Daniels
 */
public class HibernateEnumContainer extends TechnologyModelObject implements FlexoObserver {

	public static final String CLASS_NAME_KEY = "hibernate_enum_container";

	protected HibernateModel hibernateModel;
	protected Vector<HibernateEnum> hibernateEnums = new Vector<HibernateEnum>();

	// ================ //
	// = Constructors = //
	// ================ //

	/**
	 * Build a new Hibernate enum container for the specified implementation model builder.<br/>
	 * This constructor is namely invoked during unserialization.
	 * 
	 * @param builder the builder that will create this enum container
	 */
	public HibernateEnumContainer(ImplementationModelBuilder builder) {
		this(builder.implementationModel);
		initializeDeserialization(builder);
	}

	/**
	 * Build a new Hibernate enum container for the specified implementation model.
	 * 
	 * @param implementationModel the implementation model where to create this enum container
	 */
	public HibernateEnumContainer(ImplementationModel implementationModel) {
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
		return false;
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

		for (HibernateEnum hibernateEnum : new Vector<HibernateEnum>(getHibernateEnums()))
			hibernateEnum.delete();

		if (getHibernateModel() != null)
			getHibernateModel().setHibernateEnumContainer(null);

		setChanged();
		notifyObservers(new SGObjectDeletedModification<HibernateEnumContainer>(this));
		super.delete();
		deleteObservers();
	}

	/**
	 * Sort entities stored in this model by their name.
	 */
	public void sortHibernateEnums() {
		Collections.sort(this.hibernateEnums, new HibernateLinkableObjectComparator());
	}

	/* ============== */
	/* == Observer == */
	/* ============== */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof NameChanged)
			sortHibernateEnums();
	}

	/* ===================== */
	/* == Getter / Setter == */
	/* ===================== */

	/**
	 * Return the hibernateEnums stored in this model. HibernateEnums are sorted by name.
	 * 
	 * @return sorted hibernateEnums stored in this model.
	 */
	public Vector<HibernateEnum> getHibernateEnums() {
		return hibernateEnums;
	}

	public void setHibernateEnums(Vector<HibernateEnum> hibernateEnums) {
		if (requireChange(this.hibernateEnums, hibernateEnums)) {
			Vector<HibernateEnum> oldValue = this.hibernateEnums;

			for (HibernateEnum hibernateEnum : oldValue)
				hibernateEnum.deleteObserver(this);

			this.hibernateEnums = hibernateEnums;

			for (HibernateEnum hibernateEnum : hibernateEnums)
				hibernateEnum.addObserver(this);

			sortHibernateEnums();
			setChanged();
			notifyObservers(new SGAttributeModification("hibernateEnums", oldValue, hibernateEnums));
		}
	}

	public void addToHibernateEnums(HibernateEnum hibernateEnum) {
		hibernateEnum.setHibernateEnumContainer(this);
		hibernateEnums.add(hibernateEnum);

		hibernateEnum.addObserver(this);
		sortHibernateEnums();

		setChanged();
		notifyObservers(new SGObjectAddedToListModification<HibernateEnum>("hibernateEnums", hibernateEnum));
	}

	public void removeFromHibernateEnums(HibernateEnum hibernateEnum) {
		if (hibernateEnums.remove(hibernateEnum)) {
			hibernateEnum.deleteObserver(this);
			setChanged();
			notifyObservers(new SGObjectRemovedFromListModification<HibernateEnum>("hibernateEnums", hibernateEnum));
		}
	}

	/**
	 * Retrieve the Hibernate Enum with the specified name.
	 * 
	 * @param entityName
	 * @return the retrieved Hibernate Enum if any, null otherwise.
	 */
	public HibernateEnum getHibernateEnum(String name) {
		for (HibernateEnum hibernateEnum : hibernateEnums) {
			if (hibernateEnum.getName().equals(name))
				return hibernateEnum;
		}
		return null;
	}

	/**
	 * Retrieve the Hibernate Enum linked to the specified DMEntity.
	 * 
	 * @param dmEntity
	 * @return the retrieved Hibernate Enum if any, null otherwise.
	 */
	public HibernateEnum getHibernateEnum(DMEntity dmEntity) {
		for (HibernateEnum hibernateEnum : hibernateEnums) {
			if (hibernateEnum.getLinkedFlexoModelObject() == dmEntity)
				return hibernateEnum;
		}
		return null;
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
