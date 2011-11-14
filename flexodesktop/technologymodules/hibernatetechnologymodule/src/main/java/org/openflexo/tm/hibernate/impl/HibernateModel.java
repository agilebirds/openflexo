/**
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
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModelObject;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleImplementation;
import org.openflexo.foundation.sg.implmodel.event.SGAttributeModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectAddedToListModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectDeletedModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectRemovedFromListModification;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.xml.ImplementationModelBuilder;

/**
 * 
 * @author Nicolas Daniels
 */
public class HibernateModel extends TechnologyModelObject implements FlexoObserver {

	public static final String CLASS_NAME_KEY = "hibernate_model";

	protected HibernateImplementation hibernateImplementation;

	protected Vector<HibernateEntity> entities = new Vector<HibernateEntity>();
	protected HibernateEnumContainer hibernateEnumContainer;
	protected DMRepository watchedRepository;

	// ================ //
	// = Constructors = //
	// ================ //

	/**
	 * Build a new Hibernate mode for the specified implementation model builder.<br/>
	 * This constructor is namely invoked during unserialization.
	 * 
	 * @param builder
	 *            the builder that will create this entity
	 */
	public HibernateModel(ImplementationModelBuilder builder) {
		this(builder.implementationModel);
		initializeDeserialization(builder);
	}

	/**
	 * Build a new Hibernate model for the specified implementation model.
	 * 
	 * @param implementationModel
	 *            the implementation model where to create this Hibernate entity
	 */
	protected HibernateModel(ImplementationModel implementationModel) {
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
		return getHibernateImplementation().getFullyQualifiedName() + "." + getName();
	}

	/* ===================== */
	/* ====== Actions ====== */
	/* ===================== */

	public static HibernateModel createNewHibernateModel(String name, HibernateImplementation hibernateImplementation)
			throws DuplicateResourceException, InvalidNameException {
		HibernateModel newModel = new HibernateModel(hibernateImplementation.getImplementationModel());
		newModel.setName(name);
		hibernateImplementation.addToModels(newModel);

		HibernateEnumContainer hibernateEnumContainer = new HibernateEnumContainer(hibernateImplementation.getImplementationModel());
		hibernateEnumContainer.setName("Enums");
		newModel.setHibernateEnumContainer(hibernateEnumContainer);

		return newModel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {

		for (HibernateEntity hibernateEntity : new Vector<HibernateEntity>(getEntities())) {
			hibernateEntity.delete();
		}

		if (getHibernateImplementation() != null) {
			getHibernateImplementation().removeFromModels(this);
		}

		setChanged();
		notifyObservers(new SGObjectDeletedModification());
		super.delete();
		deleteObservers();
	}

	/**
	 * Creates all missing entities from the specified repository.
	 * 
	 * @param repository
	 */
	public void createEntitiesFromRepository(DMRepository repository) {

	}

	/**
	 * Sort entities stored in this model by their name.
	 */
	public void sortEntities() {
		Collections.sort(this.entities, new FlexoModelObject.FlexoNameComparator<FlexoModelObject>());
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
			sortEntities();
		}
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

			for (HibernateEntity entity : oldValue) {
				entity.deleteObserver(this);
			}

			this.entities = entities;

			for (HibernateEntity entity : entities) {
				entity.addObserver(this);
			}

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
		notifyObservers(new SGObjectAddedToListModification("entities", entity));
	}

	public void removeFromEntities(HibernateEntity entity) {
		if (entities.remove(entity)) {
			entity.deleteObserver(this);
			setChanged();
			notifyObservers(new SGObjectRemovedFromListModification("entities", entity));
		}
	}

	public DMRepository getWatchedRepository() {
		return watchedRepository;
	}

	public void setWatchedRepository(DMRepository watchedRepository) {
		if (requireChange(this.watchedRepository, watchedRepository)) {
			DMRepository oldValue = this.watchedRepository;
			this.watchedRepository = watchedRepository;
			setChanged();
			notifyObservers(new SGAttributeModification("watchedRepository", oldValue, watchedRepository));
		}
	}

	public FlexoModelObjectReference<DMRepository> getWatchedRepositoryReference() {
		if (getWatchedRepository() != null) {
			return new FlexoModelObjectReference<DMRepository>(getProject(), getWatchedRepository());
		}
		return null;
	}

	public void setWatchedRepositoryReference(FlexoModelObjectReference<DMRepository> watchedRepositoryReference) {
		setWatchedRepository(watchedRepositoryReference == null ? null : watchedRepositoryReference.getObject());
	}

	public HibernateEnumContainer getHibernateEnumContainer() {
		return hibernateEnumContainer;
	}

	public void setHibernateEnumContainer(HibernateEnumContainer hibernateEnumContainer) {
		if (requireChange(this.hibernateEnumContainer, hibernateEnumContainer)) {
			HibernateEnumContainer oldValue = this.hibernateEnumContainer;
			if (oldValue != null) {
				hibernateEnumContainer.setHibernateModel(null);
			}
			this.hibernateEnumContainer = hibernateEnumContainer;
			if (hibernateEnumContainer != null) {
				hibernateEnumContainer.setHibernateModel(this);
			}
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
	public TechnologyModuleImplementation getTechnologyModuleImplementation() {
		return getHibernateImplementation();
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
