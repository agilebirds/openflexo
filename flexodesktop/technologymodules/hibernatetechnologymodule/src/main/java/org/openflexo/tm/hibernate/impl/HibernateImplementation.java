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

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleDefinition;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleImplementation;
import org.openflexo.foundation.sg.implmodel.event.SGAttributeModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectAddedToListModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectDeletedModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectRemovedFromListModification;
import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleCompatibilityCheckException;
import org.openflexo.foundation.sg.implmodel.layer.DatabaseTechnologyModuleImplementation;
import org.openflexo.foundation.xml.ImplementationModelBuilder;

/**
 * This class defines properties related to a Spring implementation.
 * 
 * @author Emmanuel Koch, Blue Pimento Services SPRL
 */
public class HibernateImplementation extends TechnologyModuleImplementation {

	public static final String TECHNOLOGY_MODULE_NAME = "Hibernate";

	protected TechnologyModuleImplementation database;
	protected Vector<HibernateModel> models = new Vector<HibernateModel>();

	// ================ //
	// = Constructors = //
	// ================ //

	/**
	 * Build a new Hibernate implementation for the specified implementation model builder.<br/>
	 * This constructor is namely invoked during unserialization.
	 * 
	 * @param builder the builder that will create this implementation
	 * @throws TechnologyModuleCompatibilityCheckException
	 */
	public HibernateImplementation(ImplementationModelBuilder builder) throws TechnologyModuleCompatibilityCheckException {
		this(builder.implementationModel);
		initializeDeserialization(builder);
	}

	/**
	 * Build a new Hibernate implementation for the specified implementation model.
	 * 
	 * @param implementationModel the implementation model where to create this Spring implementation
	 * @throws TechnologyModuleCompatibilityCheckException
	 */
	public HibernateImplementation(ImplementationModel implementationModel) throws TechnologyModuleCompatibilityCheckException {
		super(implementationModel);
	}

	// =========== //
	// = Methods = //
	// =========== //

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TechnologyModuleDefinition getTechnologyModuleDefinition() {
		return TechnologyModuleDefinition.getTechnologyModuleDefinition(TECHNOLOGY_MODULE_NAME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getHasInspector() {
		return true;
	}

	/**
	 * Retrieve all repositories which can be watched.
	 * 
	 * @return the retrieved repositories
	 */
	public Vector<? extends DMRepository> getWatchableRepositories() {
		return getProject().getDataModel().getProjectRepositories();
	}

	/**
	 * Retrieve the instance of HibernateImplementation from the implementation model.
	 * 
	 * @param implementationModel
	 * @return the retrieved instance if any, null otherwise.
	 */
	public static HibernateImplementation getHibernateImplementation(ImplementationModel implementationModel) {
		return (HibernateImplementation) implementationModel.getTechnologyModule(TECHNOLOGY_MODULE_NAME);
	}

	/* ===================== */
	/* ====== Actions ====== */
	/* ===================== */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {

		for (HibernateModel hibernateModel : new Vector<HibernateModel>(getModels()))
			hibernateModel.delete();

		setChanged();
		notifyObservers(new SGObjectDeletedModification<HibernateImplementation>(this));
		super.delete();
		deleteObservers();
	}

	/**
	 * Transform the specified name into a suitable name for the represented database.
	 * 
	 * @param name
	 * @return the transformed name.
	 */
	public String getDbObjectName(String name) {
		if (database != null && database instanceof DatabaseTechnologyModuleImplementation)
			return ((DatabaseTechnologyModuleImplementation) database).getDbObjectName(name);

		// Default implementation is to set all lower case without spaces
		return name == null ? null : DatabaseTechnologyModuleImplementation.escapeDbObjectName(name).toLowerCase();
	}

	/* ===================== */
	/* == Getter / Setter == */
	/* ===================== */

	public TechnologyModuleImplementation getDatabase() {
		return database;
	}

	public void setDatabase(TechnologyModuleImplementation database) {
		if (requireChange(this.database, database)) {
			TechnologyModuleImplementation oldValue = this.database;
			this.database = database;
			setChanged();
			notifyObservers(new SGAttributeModification("database", oldValue, database));
		}
	}

	public Vector<HibernateModel> getModels() {
		return models;
	}

	public void setModels(Vector<HibernateModel> models) {
		if (requireChange(this.models, models)) {
			Object oldValue = this.models;
			this.models = models;
			Collections.sort(this.models, new FlexoModelObject.FlexoNameComparator<FlexoModelObject>());
			setChanged();
			notifyObservers(new SGAttributeModification("models", oldValue, models));
		}
	}

	public void addToModels(HibernateModel model) {
		model.setHibernateImplementation(this);
		models.add(model);
		Collections.sort(this.models, new FlexoModelObject.FlexoNameComparator<FlexoModelObject>());
		setChanged();
		notifyObservers(new SGObjectAddedToListModification<HibernateModel>("models", model));
	}

	public void removeFromModels(HibernateModel model) {
		if (models.remove(model)) {
			setChanged();
			notifyObservers(new SGObjectRemovedFromListModification<HibernateModel>("models", model));
		}
	}
}
