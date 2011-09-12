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
package org.openflexo.generator.dm;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.ProjectDatabaseRepository;
import org.openflexo.foundation.dm.ProjectRepository;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.MetaGenerator;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.logging.FlexoLogger;


/**
 * @author gpolet
 * 
 */
public class DataModelGenerator extends MetaGenerator<DMModel, CGRepository>
{

	private static final Logger logger = FlexoLogger.getLogger(DataModelGenerator.class.getPackage().getName());

	private Hashtable<DMEOModel, EOModelGenerator> eomodelGenerators;
	private Hashtable<DMEntity, ProcessInstanceClassGenerator> piGenerators;
	private Hashtable<DMEntity, ProcessBusinessDataClassGenerator> processBusinessDataGenerators;
	private Hashtable<DMEntity, CustomClassGenerator> customClassGenerators;
	
	public DataModelGenerator(ProjectGenerator projectGenerator)
	{
		super(projectGenerator, projectGenerator.getProject().getDataModel());
		eomodelGenerators = new Hashtable<DMEOModel, EOModelGenerator>();
		piGenerators = new Hashtable<DMEntity, ProcessInstanceClassGenerator>();
		processBusinessDataGenerators = new Hashtable<DMEntity, ProcessBusinessDataClassGenerator>();
		customClassGenerators = new Hashtable<DMEntity, CustomClassGenerator>();
	}

	@Override
	public Logger getGeneratorLogger()
	{
		return logger;
	}

	@Override
	public ProjectGenerator getProjectGenerator() {
		return (ProjectGenerator) super.getProjectGenerator();
	}
	/**
	 * This method is very important, because it is the way we must identify or build all resources
	 * involved in code generation. After this list has been built, we just let ResourceManager do the work.
	 * 
	 * @param repository: repository where resources should be retrieved or built
	 * @param resources: the list of resources we must retrieve or build
	 */
	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) 
	{
		Hashtable<DMEOModel, EOModelGenerator> newEomodelGenerators = new Hashtable<DMEOModel, EOModelGenerator>();
		Hashtable<DMEntity, ProcessInstanceClassGenerator> newPiGenerators = new Hashtable<DMEntity, ProcessInstanceClassGenerator>();
		Hashtable<DMEntity, CustomClassGenerator> newCustomClassGenerators = new Hashtable<DMEntity, CustomClassGenerator>();
		
		int i = 0;
		for (ProjectDatabaseRepository dbRepository : projectGenerator.getProject().getDataModel().getProjectDatabaseRepositories()) {
			for (DMEOModel eoModel : dbRepository.getOrderedDMEOModels()) {
				i = i + eoModel.getOrderedChildren().size();
			}
		}
		resetSecondaryProgressWindow(i);
		DMModel dataModel = projectGenerator.getProject().getDataModel();
		for (ProjectDatabaseRepository dbRepository : dataModel.getProjectDatabaseRepositories()) {
			for (DMEOModel eoModel : dbRepository.getOrderedDMEOModels()) {
				EOModelGenerator generatorForEOModel = getGeneratorForEOModel(eoModel);
				newEomodelGenerators.put(eoModel, generatorForEOModel);
				generatorForEOModel.buildResourcesAndSetGenerators(repository,resources);
			}
		}
		
		for (ProjectRepository projectRepository : dataModel.getProjectRepositories()) {
			for (DMEntity entity : projectRepository.getEntities().values()) {
				CustomClassGenerator generatorForProjectEntity = getGeneratorForProjectEntity(entity);
				newCustomClassGenerators.put(entity,generatorForProjectEntity);
				generatorForProjectEntity.buildResourcesAndSetGenerators(repository,resources);
			}
		}
		
		for (DMEntity entity : projectGenerator.getProject().getDataModel().getProcessInstanceRepository().getEntities().values()) {
			ProcessInstanceClassGenerator generatorForProcessInstanceEntity = getGeneratorForProcessInstanceEntity(entity);
			newPiGenerators.put(entity, generatorForProcessInstanceEntity);
			generatorForProcessInstanceEntity.buildResourcesAndSetGenerators(repository,resources);
		}
		
		processBusinessDataGenerators.clear();
		for (DMEntity entity : projectGenerator.getProject().getDataModel().getProcessBusinessDataRepository().getEntities().values()) {
			ProcessBusinessDataClassGenerator generatorForProcessBusinessDataEntity = getGeneratorForProcessBusinessDataEntity(entity);
			processBusinessDataGenerators.put(entity, generatorForProcessBusinessDataEntity);
			generatorForProcessBusinessDataEntity.buildResourcesAndSetGenerators(repository, resources);
		}
		
		// Frees memory!
		eomodelGenerators.clear();
		customClassGenerators.clear();
		piGenerators.clear();
		
		eomodelGenerators = newEomodelGenerators;
		customClassGenerators = newCustomClassGenerators;
		piGenerators = newPiGenerators;
	}

	private EOModelGenerator getGeneratorForEOModel(DMEOModel eoModel) {
		if (eomodelGenerators.get(eoModel) == null) {
			eomodelGenerators.put(eoModel, new EOModelGenerator(getProjectGenerator(), eoModel));
		}
		return eomodelGenerators.get(eoModel);
	}
	
	private ProcessInstanceClassGenerator getGeneratorForProcessInstanceEntity(DMEntity entity) {
		if (piGenerators.get(entity) == null) {
			piGenerators.put(entity, new ProcessInstanceClassGenerator(getProjectGenerator(), entity));
		}
		return piGenerators.get(entity);
	}
	
	private ProcessBusinessDataClassGenerator getGeneratorForProcessBusinessDataEntity(DMEntity entity) {
		if (processBusinessDataGenerators.get(entity) == null) {
			processBusinessDataGenerators.put(entity, new ProcessBusinessDataClassGenerator(getProjectGenerator(), entity));
		}
		return processBusinessDataGenerators.get(entity);
	}
	
	private CustomClassGenerator getGeneratorForProjectEntity(DMEntity entity) {
		if (customClassGenerators.get(entity) == null) {
			customClassGenerators.put(entity, new CustomClassGenerator(getProjectGenerator(), entity));
		}
		return customClassGenerators.get(entity);
	}
	
	@Override
	public void generate(boolean forceRegenerate) throws GenerationException
	{
		int i = 0;
		for (ProjectDatabaseRepository dbRepository : projectGenerator.getProject().getDataModel().getProjectDatabaseRepositories()) {
			for (DMEOModel eoModel : dbRepository.getOrderedDMEOModels()) {
				i = i + eoModel.getOrderedChildren().size();
			}
		}
		resetSecondaryProgressWindow(i);
		startGeneration();
		DMModel dataModel = projectGenerator.getProject().getDataModel();
		for (ProjectDatabaseRepository dbRepository : dataModel.getProjectDatabaseRepositories()) {
			for (DMEOModel eoModel : dbRepository.getOrderedDMEOModels()) {
				getGeneratorForEOModel(eoModel).generate(forceRegenerate);
			}
		}
		
		for (ProjectRepository projectRepository : dataModel.getProjectRepositories()) {
			for (DMEntity entity : projectRepository.getEntities().values()) {
				getGeneratorForProjectEntity(entity).generate(forceRegenerate);
			}
		}
		
		for (DMEntity entity : projectGenerator.getProject().getDataModel().getProcessInstanceRepository().getEntities().values()) {
			getGeneratorForProcessInstanceEntity(entity).generate(forceRegenerate);
		}
		
		stopGeneration();
	}

	public DMModel getDataModel()
	{
		return getObject();
	}


}
