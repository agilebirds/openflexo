/**
 * 
 */
package org.openflexo.tm.hibernate.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.dm.EntityRegistered;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.LinkableTechnologyModelObject;
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
public class HibernateModel extends LinkableTechnologyModelObject<DMRepository> implements FlexoObserver {

	public static final String CLASS_NAME_KEY = "hibernate_model";

	protected HibernateImplementation hibernateImplementation;

	protected Vector<HibernateEntity> entities = new Vector<HibernateEntity>();
	protected HibernateEnumContainer hibernateEnumContainer;

	// ================ //
	// = Constructors = //
	// ================ //

	/**
	 * Build a new Hibernate mode for the specified implementation model builder.<br/>
	 * This constructor is namely invoked during unserialization.
	 * 
	 * @param builder the builder that will create this entity
	 */
	public HibernateModel(ImplementationModelBuilder builder) {
		this(builder.implementationModel);
		initializeDeserialization(builder);
	}

	/**
	 * Build a new Hibernate model for the specified implementation model.
	 * 
	 * @param implementationModel the implementation model where to create this Hibernate entity
	 */
	protected HibernateModel(ImplementationModel implementationModel) {
		super(implementationModel);
	}

	/**
	 * Build a new Hibernate model for the specified implementation model.
	 * 
	 * @param implementationModel the implementation model where to create this Hibernate entity
	 */
	protected HibernateModel(ImplementationModel implementationModel, DMRepository watchedRepository) {
		super(implementationModel, watchedRepository);
	}

	// =========== //
	// = Methods = //
	// =========== //

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		synchronizeWithLinkedFlexoModelObject();
	}

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
		return getHibernateImplementation().getFullyQualifiedName() + "." + getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeWithLinkedFlexoModelObject() {
		if (getLinkedFlexoModelObject() != null) {
			List<LinkableTechnologyModelObject<?>> deletedChildren = new ArrayList<LinkableTechnologyModelObject<?>>();

			Map<DMEntity, LinkableTechnologyModelObject<?>> alreadyCreatedChildren = new HashMap<DMEntity, LinkableTechnologyModelObject<?>>();
			for (HibernateEntity hibernateEntity : this.entities) {
				if (hibernateEntity.getLinkedFlexoModelObject() != null)
					alreadyCreatedChildren.put(hibernateEntity.getLinkedFlexoModelObject(), hibernateEntity);
				else if (hibernateEntity.getWasLinkedAtLastDeserialization())
					deletedChildren.add(hibernateEntity);
			}

			for (HibernateEnum hibernateEnum : getHibernateEnumContainer().getHibernateEnums()) {
				if (hibernateEnum.getLinkedFlexoModelObject() != null)
					alreadyCreatedChildren.put(hibernateEnum.getLinkedFlexoModelObject(), hibernateEnum);
				else if (hibernateEnum.getWasLinkedAtLastDeserialization())
					deletedChildren.add(hibernateEnum);
			}

			// Create missing entities & enums and update existing ones
			for (DMEntity entity : getLinkedFlexoModelObject().getEntities().values()) {
				if (!alreadyCreatedChildren.containsKey(entity)) {
					createHibernateObjectBasedOnDMEntity(entity);
				} else {
					alreadyCreatedChildren.get(entity).synchronizeWithLinkedFlexoModelObject();
				}
			}

			// Remove deleted entities & enums
			for (LinkableTechnologyModelObject<?> hibernateObj : deletedChildren)
				hibernateObj.delete();
		}
	}

	/* ===================== */
	/* ====== Actions ====== */
	/* ===================== */

	public static HibernateModel createNewHibernateModel(String name, HibernateImplementation hibernateImplementation, DMRepository watchedRepository) throws DuplicateResourceException,
			InvalidNameException {
		HibernateModel newModel = new HibernateModel(hibernateImplementation.getImplementationModel(), watchedRepository);
		newModel.setName(name);
		hibernateImplementation.addToModels(newModel);

		HibernateEnumContainer hibernateEnumContainer = new HibernateEnumContainer(hibernateImplementation.getImplementationModel());
		hibernateEnumContainer.setName("Enums");
		newModel.setHibernateEnumContainer(hibernateEnumContainer);

		newModel.synchronizeWithLinkedFlexoModelObject();

		return newModel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {

		for (HibernateEntity hibernateEntity : new Vector<HibernateEntity>(getEntities()))
			hibernateEntity.delete();

		if (getHibernateImplementation() != null)
			getHibernateImplementation().removeFromModels(this);

		setChanged();
		notifyObservers(new SGObjectDeletedModification<HibernateModel>(this));
		super.delete();
		deleteObservers();
	}

	/**
	 * Add a new Hibernate Entity or Hibernate Enum to this model. The newly created hibernate object is based and linked to the specified DMEntity. <br>
	 * If an Hibernate object was already existing for this DMEntity, nothing is performed.
	 * 
	 * @param dmEntity the DMEntity the newly created Hibernate object should be linked to.
	 */
	public void createHibernateObjectBasedOnDMEntity(DMEntity dmEntity) {
		if (dmEntity.getIsEnumeration()) {
			if (getHibernateEnumContainer().getHibernateEnum(dmEntity) == null) {
				HibernateEnum hibernateEnum = new HibernateEnum(getImplementationModel(), dmEntity);
				getHibernateEnumContainer().addToHibernateEnums(hibernateEnum);
				hibernateEnum.synchronizeWithLinkedFlexoModelObject();
			}
		} else {
			if (getEntity(dmEntity) == null) {
				HibernateEntity entity = new HibernateEntity(getImplementationModel(), dmEntity);
				addToEntities(entity);
				entity.synchronizeWithLinkedFlexoModelObject();
			}
		}
	}

	/**
	 * Sort entities stored in this model by their name.
	 */
	public void sortEntities() {
		Collections.sort(this.entities, new HibernateLinkableObjectComparator());
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
		if (observable == getLinkedFlexoModelObject()) {
			if (dataModification instanceof EntityRegistered)
				createHibernateObjectBasedOnDMEntity((DMEntity) dataModification.newValue());
		}
		if (dataModification instanceof NameChanged)
			sortEntities();
	}

	/* ===================== */
	/* == Getter / Setter == */
	/* ===================== */

	/**
	 * Return the entities stored in this model. Entities are sorted by name.
	 * 
	 * @return sorted entities stored in this model.
	 */
	public Vector<HibernateEntity> getEntities() {
		return entities;
	}

	public void setEntities(Vector<HibernateEntity> entities) {
		if (requireChange(this.entities, entities)) {
			Vector<HibernateEntity> oldValue = this.entities;

			for (HibernateEntity entity : oldValue)
				entity.deleteObserver(this);

			this.entities = entities;

			for (HibernateEntity entity : entities)
				entity.addObserver(this);

			sortEntities();
			setChanged();
			notifyObservers(new SGAttributeModification("entities", oldValue, entities));
		}
	}

	public void addToEntities(HibernateEntity entity) {
		entity.setHibernateModel(this);
		entities.add(entity);

		entity.addObserver(this);
		sortEntities();

		setChanged();
		notifyObservers(new SGObjectAddedToListModification<HibernateEntity>("entities", entity));
	}

	public void removeFromEntities(HibernateEntity entity) {
		if (entities.remove(entity)) {
			entity.deleteObserver(this);
			setChanged();
			notifyObservers(new SGObjectRemovedFromListModification<HibernateEntity>("entities", entity));
		}
	}

	/**
	 * Retrieve the Hibernate Entity with the specified name.
	 * 
	 * @param entityName
	 * @return the retrieved Hibernate Entity if any, null otherwise.
	 */
	public HibernateEntity getEntity(String entityName) {
		for (HibernateEntity entity : entities) {
			if (entity.getName().equals(entityName))
				return entity;
		}

		return null;
	}

	/**
	 * Retrieve the Hibernate Entity linked to the specified DMEntity.
	 * 
	 * @param dmEntity
	 * @return the retrieved Hibernate Entity if any, null otherwise.
	 */
	public HibernateEntity getEntity(DMEntity dmEntity) {
		for (HibernateEntity entity : entities) {
			if (entity.getLinkedFlexoModelObject() == dmEntity)
				return entity;
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLinkedFlexoModelObject(DMRepository linkedFlexoModelObject) {
		super.setLinkedFlexoModelObject(linkedFlexoModelObject);
	}

	public HibernateEnumContainer getHibernateEnumContainer() {
		return hibernateEnumContainer;
	}

	public void setHibernateEnumContainer(HibernateEnumContainer hibernateEnumContainer) {
		if (requireChange(this.hibernateEnumContainer, hibernateEnumContainer)) {
			HibernateEnumContainer oldValue = this.hibernateEnumContainer;
			if (oldValue != null)
				hibernateEnumContainer.setHibernateModel(null);
			this.hibernateEnumContainer = hibernateEnumContainer;
			if (hibernateEnumContainer != null)
				hibernateEnumContainer.setHibernateModel(this);
			setChanged();
			notifyObservers(new SGAttributeModification("hibernateEnumContainer", oldValue, hibernateEnumContainer));
		}
	}

	public HibernateImplementation getHibernateImplementation() {
		return hibernateImplementation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HibernateImplementation getTechnologyModuleImplementation() {
		return HibernateImplementation.getHibernateImplementation(getImplementationModel());
	}

	/**
	 * Called only from HibernateImplementation at deserialisation or at entity creation
	 * 
	 * @param hibernateImplementation
	 */
	protected void setHibernateImplementation(HibernateImplementation hibernateImplementation) {
		this.hibernateImplementation = hibernateImplementation;
	}
}
