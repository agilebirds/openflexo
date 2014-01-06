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

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;

/**
 * Represents a logical group of objects representing WO components
 * 
 * @author sguerin
 * 
 */
public class ProcessInstanceRepository extends DMRepository {

	private static final Logger logger = Logger.getLogger(ProcessInstanceRepository.class.getPackage().getName());

	/**
	 * Constructor used during deserialization
	 */
	public ProcessInstanceRepository(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	private ProcessInstanceRepository(DMModel dmModel) {
		super(dmModel);
	}

	@Override
	public DMRepositoryFolder getRepositoryFolder() {
		return getDMModel().getInternalRepositoryFolder();
	}

	@Override
	public int getOrder() {
		return 12;
	}

	@Override
	public String getName() {
		return "process_instance_repository";
	}

	@Override
	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	@Override
	public void setName(String name) {
		// Not allowed
	}

	/**
	 * Overrides getInspectorName
	 * 
	 * @see org.openflexo.foundation.dm.DMRepository#getInspectorName()
	 */
	@Override
	public String getInspectorName() {
		return Inspectors.DM.DM_RO_REPOSITORY_INSPECTOR;
	}

	/**
	 * @param dmModel
	 * @return
	 */
	public static ProcessInstanceRepository createNewProcessInstanceRepository(DMModel dmModel) {
		ProcessInstanceRepository newProcessInstanceRepository = new ProcessInstanceRepository(dmModel);
		dmModel.setProcessInstanceRepository(newProcessInstanceRepository);
		return newProcessInstanceRepository;
	}

	@Override
	public String getFullyQualifiedName() {
		return getDMModel().getFullyQualifiedName() + ".PROCESSES";
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public boolean isDeletable() {
		return false;
	}

	public DMPackage getDefaultProcessInstancePackage() {
		return getDefaultPackage();
	}

	public ProcessDMEntity getProcessDMEntity(FlexoProcess process) {
		for (Enumeration en = getPackages().elements(); en.hasMoreElements();) {
			DMPackage next = (DMPackage) en.nextElement();
			DMEntity found = getDMEntity(next.getName(), process.getProcessInstanceEntityName());
			if (found != null) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Found process instance entity for process " + process.getName());
				}
				ProcessDMEntity returned = (ProcessDMEntity) found;
				returned.setProcess(process);
				returned.createParentProcessPropertyIfRequired();
				// Check that name matches
				if (!returned.getName().equals(process.getProcessInstanceEntityName())) {
					try {
						returned.setName(process.getProcessInstanceEntityName());
					} catch (InvalidNameException e) {
						e.printStackTrace();
					}
				}
				// Check that class name matches
				if (!returned.getEntityClassName().equals(process.getProcessInstanceEntityName())) {
					try {
						returned.setEntityClassName(process.getProcessInstanceEntityName());
					} catch (DuplicateClassNameException e) {
						e.printStackTrace();
					} catch (InvalidNameException e) {
						e.printStackTrace();
					}
				}
				DMType processInstanceType = DMType.makeResolvedDMType(getDMModel().getExecutionModelRepository()
						.getProcessInstanceEntity());
				if (returned.getParentType() == null || !returned.getParentType().equals(processInstanceType)) {
					returned.setParentType(processInstanceType, true);
				}
				return returned;
			} else {
				logger.info("NOT Found process instance entity for process " + process.getName());
			}
		}
		return null;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return getName();
	}

}
