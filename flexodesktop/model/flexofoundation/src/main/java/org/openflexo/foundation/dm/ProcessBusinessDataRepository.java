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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.dm.DMMethod.DMMethodParameter;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;

/**
 * Represents a logical group of objects representing the auto generated process business data
 * 
 * @author ndaniels
 * 
 */
public class ProcessBusinessDataRepository extends DMRepository {

	private static final Logger logger = Logger.getLogger(ProcessBusinessDataRepository.class.getPackage().getName());

	private static final String BUSINESSDATA_PACKAGE = "org.openflexo.businessdata";

	public static final String PROCESSBUSINESSDATAENTITY_KEY = "processBusinessDataEntity";

	private DMEntity processBusinessDataEntity;

	public static ProcessBusinessDataRepository createNewProcessBusinessDataRepository(DMModel dmModel) {
		ProcessBusinessDataRepository newProcessInstanceRepository = new ProcessBusinessDataRepository(dmModel);
		dmModel.setProcessBusinessDataRepository(newProcessInstanceRepository);
		return newProcessInstanceRepository;
	}

	/**
	 * Constructor used during deserialization
	 */
	public ProcessBusinessDataRepository(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	private ProcessBusinessDataRepository(DMModel dmModel) {
		super(dmModel);
	}

	@Override
	public DMRepositoryFolder getRepositoryFolder() {
		return getDMModel().getInternalRepositoryFolder();
	}

	@Override
	public int getOrder() {
		return 13;
	}

	@Override
	public String getName() {
		return "process_business_data_repository";
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

	@Override
	public String getFullyQualifiedName() {
		return getDMModel().getFullyQualifiedName() + ".PROCESSBUSINESSDATA";
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public boolean isDeletable() {
		return false;
	}

	public DMPackage getBusinessDataPackage() {
		DMPackage businessDataPackage = getPackageWithName(BUSINESSDATA_PACKAGE);
		if (businessDataPackage == null) {
			return createPackage(BUSINESSDATA_PACKAGE);
		}
		return businessDataPackage;
	}

	public DMEntity getProcessBusinessDataEntity() {
		if (this.processBusinessDataEntity == null) {
			DMEntity processBusinessDataEntity = new DMEntity(getDMModel(), "ProcessBusinessData", getBusinessDataPackage().getName(),
					"ProcessBusinessData", null);
			registerEntity(processBusinessDataEntity);

			if (getDMModel() != null && getDMModel().getWORepository() != null && getDMModel().getWORepository().isWebObjectsAvailable()) {
				processBusinessDataEntity.setParentType(
						DMType.makeResolvedDMType(getDMModel().getEntityNamed("com.webobjects.foundation.NSMutableDictionary")), true);
			}

			setProcessBusinessDataEntity(processBusinessDataEntity);
		}
		return this.processBusinessDataEntity;
	}

	public void setProcessBusinessDataEntity(DMEntity processBusinessDataEntity) {
		DMEntity oldValue = this.processBusinessDataEntity;
		this.processBusinessDataEntity = processBusinessDataEntity;
		setChanged();
		notifyObservers(new DMAttributeDataModification(PROCESSBUSINESSDATAENTITY_KEY, oldValue, processBusinessDataEntity));
	}

	public void generateProcessBusinessData() {
		// Generates process business data methods on custom components and on processBusinessDataEntity
		DMEntity customComponentEntity = getDMModel().getWORepository().getCustomComponentEntity();
		if (customComponentEntity.getOrderedMethodNamed(getCustomComponentCurrentProcessBusinessDataGetterName()) == null) {
			DMMethod method = new DMMethod(getDMModel(), getCustomComponentCurrentProcessBusinessDataGetterName());
			method.setReturnType(DMType.makeResolvedDMType(getProcessBusinessDataEntity()));
			method.setIsStaticallyDefinedInTemplate(true);
			customComponentEntity.registerMethod(method);

			try {
				method = new DMMethod(getDMModel(), getCustomComponentCurrentProcessBusinessDataGetterName());
				method.setReturnType(DMType.makeResolvedDMType(getProcessBusinessDataEntity()));
				DMMethodParameter parameter = new DMMethodParameter(getDMModel());
				parameter.setType(DMType.makeStringDMType(getProject()));
				parameter.setName("processKey");
				method.addToParameters(parameter);
				method.setIsStaticallyDefinedInTemplate(true);
				customComponentEntity.registerMethod(method);
			} catch (DuplicateMethodSignatureException e) {// Should never happen
				logger.log(Level.WARNING, "DuplicateMethodSignatureException in init process business data methods on custom components", e);
			}
		}

		if (getProcessBusinessDataEntity().getOrderedMethodNamed(getParentProcessBusinessDataGetterName()) == null) {
			try {
				DMMethod method = new DMMethod(getDMModel(), getParentProcessBusinessDataGetterName());
				method.setReturnType(DMType.makeResolvedDMType(getProcessBusinessDataEntity()));

				DMMethodParameter parameter = new DMMethodParameter(getDMModel());
				parameter.setType(DMType.makeStringDMType(getProject()));
				parameter.setName("processKey");
				method.addToParameters(parameter);

				method.setIsStaticallyDefinedInTemplate(true);
				getProcessBusinessDataEntity().registerMethod(method);
			} catch (DuplicateMethodSignatureException e) {// Should never happen
				logger.log(Level.WARNING,
						"DuplicateMethodSignatureException in init process business data methods on processBusinessDataEntity", e);
			}
		}

		if (getProcessBusinessDataEntity().getOrderedMethodNamed(getChildrenProcessBusinessDataGetterName()) == null) {
			try {
				DMMethod method = new DMMethod(getDMModel(), getChildrenProcessBusinessDataGetterName());
				method.setReturnType(DMType.makeVectorDMType(DMType.makeResolvedDMType(getProcessBusinessDataEntity()), getProject()));

				DMMethodParameter parameter = new DMMethodParameter(getDMModel());
				parameter.setType(DMType.makeStringDMType(getProject()));
				parameter.setName("childProcessKey");
				method.addToParameters(parameter);

				method.setIsStaticallyDefinedInTemplate(true);
				getProcessBusinessDataEntity().registerMethod(method);
			} catch (DuplicateMethodSignatureException e) {// Should never happen
				logger.log(Level.WARNING,
						"DuplicateMethodSignatureException in init process business data methods on processBusinessDataEntity", e);
			}
		}

		generateProcessBusinessData(null);
	}

	/**
	 * Generates or update a process business data entity for each local process. Only processes with no binded business data or with an
	 * auto generated business data are taken into account.
	 */
	public void generateProcessBusinessData(List<AutoGeneratedProcessBusinessDataDMEntity> entitiesToGenerate) {
		Map<AutoGeneratedProcessBusinessDataDMEntity, Set<DMObject>> keptEntitiesWithProperties = new HashMap<AutoGeneratedProcessBusinessDataDMEntity, Set<DMObject>>();
		Map<FlexoProcess, AutoGeneratedProcessBusinessDataDMEntity> businessEntityForProcesses = new HashMap<FlexoProcess, AutoGeneratedProcessBusinessDataDMEntity>();
		Enumeration<FlexoProcess> processes = getProject().getSortedProcesses();
		while (processes.hasMoreElements()) {
			FlexoProcess process = processes.nextElement();

			AutoGeneratedProcessBusinessDataDMEntity processBusinessDataEntity = null;
			if (process.getBusinessDataType() == null && entitiesToGenerate == null) {
				processBusinessDataEntity = getAutoGeneratedProcessBusinessDataDMEntity(process, true);
			} else if (process.getBusinessDataType() instanceof AutoGeneratedProcessBusinessDataDMEntity
					&& (entitiesToGenerate == null || entitiesToGenerate.contains(process.getBusinessDataType()))) {
				processBusinessDataEntity = (AutoGeneratedProcessBusinessDataDMEntity) process.getBusinessDataType();
			}

			if (processBusinessDataEntity != null) {
				businessEntityForProcesses.put(process, processBusinessDataEntity);
				Set<DMObject> keptProperties = processBusinessDataEntity.updateProcessBusinessDataProperties(process);

				if (!keptEntitiesWithProperties.containsKey(processBusinessDataEntity)) {
					keptEntitiesWithProperties.put(processBusinessDataEntity, keptProperties);
				} else {
					keptEntitiesWithProperties.get(processBusinessDataEntity).addAll(keptProperties);
				}
			}
		}

		// Generates child process access methods (Need to be performed after business data is set on process)
		for (FlexoProcess process : businessEntityForProcesses.keySet()) {
			AutoGeneratedProcessBusinessDataDMEntity entity = businessEntityForProcesses.get(process);
			keptEntitiesWithProperties.get(entity).addAll(entity.createPropertiesForChildrenProcessBusinessData(process));
		}

		// Remove unused auto generated process business data entities
		for (DMEntity entity : new ArrayList<DMEntity>(getBusinessDataPackage().getEntities())) {
			if (entity instanceof AutoGeneratedProcessBusinessDataDMEntity && !keptEntitiesWithProperties.containsKey(entity)
					&& (entitiesToGenerate == null || entitiesToGenerate.contains(entity))) {
				entity.delete();
			}
		}

		// For each generated process business data, remove all unused read-only properties and all unused
		// DMProcessBusinessDataAccessingMethod
		for (Map.Entry<AutoGeneratedProcessBusinessDataDMEntity, Set<DMObject>> entry : keptEntitiesWithProperties.entrySet()) {
			for (DMProperty property : new ArrayList<DMProperty>(entry.getKey().getProperties().values())) {
				if (property.getIsReadOnly() && !entry.getValue().contains(property)) {
					property.delete();
				}
			}

			for (DMMethod method : new ArrayList<DMMethod>(entry.getKey().getDeclaredMethods())) {
				if (method instanceof DMProcessBusinessDataAccessingMethod && !entry.getValue().contains(method)) {
					method.delete();
				}
			}
		}
	}

	/**
	 * Get or create the auto generated process business data for the specified process.
	 * 
	 * @param process
	 * @return the auto generated process business data for the specified process.
	 */
	public AutoGeneratedProcessBusinessDataDMEntity getAutoGeneratedProcessBusinessDataDMEntity(FlexoProcess process,
			boolean setAsBusinessDataType) {
		DMEntity found = getDMEntity(getBusinessDataPackage().getName(),
				AutoGeneratedProcessBusinessDataDMEntity.getProcessBusinessDataEntityNameForProcess(process));
		if (found == null) { // Create it
			found = new AutoGeneratedProcessBusinessDataDMEntity(this, process);
		}

		if (found instanceof AutoGeneratedProcessBusinessDataDMEntity) {
			if (setAsBusinessDataType) {
				process.setBusinessDataType(found);
			}
			return (AutoGeneratedProcessBusinessDataDMEntity) found;
		}

		logger.severe("Cannot generate AutoGeneratedProcessBusinessDataDMEntity for process " + process.getName()
				+ " because another DMEntity already exists with the same name");
		return null;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return getName();
	}

	public static String getCustomComponentCurrentProcessBusinessDataGetterName() {
		return "getCurrentProcessBusinessData";
	}

	public static String getParentProcessBusinessDataGetterName() {
		return "getParentBusinessData";
	}

	public static String getChildrenProcessBusinessDataGetterName() {
		return "getChildrendBusinessData";
	}

}
