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
package org.openflexo.foundation.dm;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.dm.EntityDeleted;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoDMBuilder;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class ProcessDMEntity extends DMEntity {

	private static final Logger logger = Logger.getLogger(ProcessDMEntity.class.getPackage().getName());

	private static final String PARENT_PROCESS_PROPERTY_NAME = "parent";

	// ==========================================================================
	// ============================= Instance variables =========================
	// ==========================================================================

	private FlexoProcess _process;

	// ==========================================================================
	// ============================= Constructor ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public ProcessDMEntity(FlexoDMBuilder builder) {
		super(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Constructor used for dynamic creation
	 */
	public ProcessDMEntity(DMModel dmModel, FlexoProcess process) {
		super(dmModel, process.getProcessInstanceEntityName(), process.getExecutionGroupName(), process.getProcessInstanceEntityName(),
				DMType.makeResolvedDMType(dmModel.getExecutionModelRepository().getProcessInstanceEntity()));
		setProcess(process);
		createParentProcessPropertyIfRequired();
	}

	@Override
	public boolean isDeletable() {
		return getProcess() == null || getProcess().getProcessDMEntity() != this;
	}

	/*
	 * public String getFullyQualifiedName() { return getEntityPackageName() + "." + getName(); }
	 */

	public FlexoProcess getProcess() {
		return _process;
	}

	public void setProcess(FlexoProcess process) {
		_process = process;
	}

	public DMProperty createBusinessDataProperty(DMEntity type) {
		return createBusinessDataProperty("businessData", type);
	}

	public DMProperty createBusinessDataProperty(String propertyName) {
		return createBusinessDataProperty(propertyName, null);
	}

	public DMProperty createBusinessDataProperty(String propertyName, DMEntity type) {
		DMProperty property = getDMProperty(propertyName);
		if (property == null) {
			property = new DMProperty(getDMModel(), propertyName, DMType.makeResolvedDMType(type), DMCardinality.SINGLE, true, true,
					DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD);
			registerProperty(property, false);
			if (type != null) {
				type.addObserver(this);
			}
		} else {
			property.setType(DMType.makeResolvedDMType(type), true);
			if (type != null) {
				type.addObserver(this);
			}
		}
		return property;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof EntityDeleted) {
			if (getBusinessDataProperty() != null && ((EntityDeleted) dataModification).getEntity().equals(
					getBusinessDataProperty().getType().getBaseEntity())) {
				getBusinessDataProperty().delete();
				((EntityDeleted) dataModification).getEntity().deleteObserver(this);
			}
		}
		super.update(observable, dataModification);
	}

	public DMProperty createParentProcessPropertyIfRequired() {
		if (getParentProcessProperty() == null && _process.getParentProcess() != null) {
			DMProperty newProperty = new DMProperty(getDMModel(), PARENT_PROCESS_PROPERTY_NAME, DMType.makeResolvedDMType(_process
					.getParentProcess().getProcessDMEntity()), DMCardinality.SINGLE, true, true,
					DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD);
			registerProperty(newProperty, false);
			logger.info("CREATES parent process property !");
		}
		return getParentProcessProperty();
	}

	public DMProperty updateParentProcessPropertyIfRequired() {
		if (_process.getParentProcess() == null) {
			// Remove old property if existant
			if (getParentProcessProperty() != null) {
				logger.info("REMOVE parent process property !");
				getParentProcessProperty().delete();
				return null;
			}
		} else {
			if (getParentProcessProperty() == null) {
				return createParentProcessPropertyIfRequired();
			}
		}
		if (getParentProcessProperty() != null && (getParentProcessProperty().getType() == null || getParentProcessProperty().getType()
				.getBaseEntity() != _process.getParentProcess().getProcessDMEntity())) {
			logger.info("UPDATE parent process property !");
			getParentProcessProperty().setType(DMType.makeResolvedDMType(_process.getParentProcess().getProcessDMEntity()));
		}
		return getParentProcessProperty();
	}

	public DMProperty getBusinessDataProperty() {
		Enumeration en = getProperties().elements();
		while (en.hasMoreElements()) {
			DMProperty next = (DMProperty) en.nextElement();
			if (next.getType() != null && !getDMModel().getExecutionModelRepository().getProcessInstanceEntity()
					.isAncestorOf(next.getType().getBaseEntity())) {
				return next;
			}
		}
		if (getProperties().size() > 2) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("More than two properties declared for a ProcessDMEntity !");
			}
		}
		return null;
	}

	public DMProperty getParentProcessProperty() {
		Enumeration en = getProperties().elements();
		while (en.hasMoreElements()) {
			DMProperty next = (DMProperty) en.nextElement();
			if (next.getType() != null && getDMModel().getExecutionModelRepository().getProcessInstanceEntity()
					.isAncestorOf(next.getType().getBaseEntity())) {
				return next;
			}
		}
		if (getProperties().size() > 2) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("More than two properties declared for a ProcessDMEntity !");
			}
		}
		return null;
	}

	@Override
	public void setName(String newName) throws InvalidNameException {
		boolean notify = !isDeserializing();
		try {
			internallyUpdateName(newName);
			notify = false;
		} catch (DuplicateClassNameException e) {
			e.printStackTrace();
			throw new InvalidNameException(e.getLocalizedMessage());
		} finally {
			if (notify) {
				setChanged();
				notifyObserversAsReentrantModification(new NameChanged(newName, getName()));
			}
		}
	}

	private void internallyUpdateName(String newName) throws DuplicateClassNameException, InvalidNameException {
		super.setEntityClassName(newName);
		super.setName(newName);
	}

	@Override
	public void setEntityClassName(String newEntityClassName) throws DuplicateClassNameException, InvalidNameException {
		if (isDeserializing()) {
			return;
		}
		boolean notify = true;
		try {
			internallyUpdateName(newEntityClassName);
			notify = false;
		} finally {
			if (notify) {
				setChanged();
				notifyObserversAsReentrantModification(new DMAttributeDataModification("entityClassName", newEntityClassName,
						getEntityClassName()));
			}
		}
	}

	/**
	 * Tells if code generation is applicable for related DMEntity
	 * 
	 * @return
	 */
	@Override
	public boolean isCodeGenerationApplicable() {
		return true;
	}

}
