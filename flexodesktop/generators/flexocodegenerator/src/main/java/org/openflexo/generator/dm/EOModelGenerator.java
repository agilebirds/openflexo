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

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.dm.dm.EOEntityInserted;
import org.openflexo.foundation.dm.eo.DMEOEntity;
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
public class EOModelGenerator extends MetaGenerator<FlexoModelObject, CGRepository> {
	private static final Logger logger = FlexoLogger.getLogger(EOModelGenerator.class.getPackage().getName());

	private Hashtable<DMEOEntity, EOEntityPListGenerator> _entityGenerators;
	private Hashtable<DMEOEntity, GenericRecordGenerator> _entityClassGenerators;
	private DMEOModel _eoModel;
	private EOModelPListGenerator _eomodelPlistGenerator;

	/**
	 * @param projectGenerator
	 * @param object
	 */
	public EOModelGenerator(ProjectGenerator dataModelGenerator, DMEOModel eoModel) {
		super(dataModelGenerator, eoModel);
		_eoModel = eoModel;
		_entityGenerators = new Hashtable<DMEOEntity, EOEntityPListGenerator>();
		_entityClassGenerators = new Hashtable<DMEOEntity, GenericRecordGenerator>();
		// eoModel.addObserver(this);
	}

	@Override
	public ProjectGenerator getProjectGenerator() {
		return (ProjectGenerator) super.getProjectGenerator();
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) {
		if (!getEOModel().getDMModel().getEOCodeGenerationActivated()) {
			getEOModel().getDMModel().activateEOCodeGeneration();
		}
		Hashtable<DMEOEntity, EOEntityPListGenerator> entityGenerators = new Hashtable<DMEOEntity, EOEntityPListGenerator>();
		Hashtable<DMEOEntity, GenericRecordGenerator> entityClassGenerators = new Hashtable<DMEOEntity, GenericRecordGenerator>();

		for (DMEOEntity entity : getEOModel().getEntities()) {
			// if(entity.getClassProperties().size()==0)continue;
			entity.getGeneratedCode(); // Here to force pre-computation of code.
			GenericRecordGenerator generator = getGenericRecordGenerator(entity);
			entityClassGenerators.put(entity, generator);
			generator.buildResourcesAndSetGenerators(repository, resources);
		}
		for (DMEOEntity entity : getEOModel().getEntities()) {
			EOEntityPListGenerator generator = getGenerator(entity);
			entityGenerators.put(entity, generator);
			generator.buildResourcesAndSetGenerators(repository, resources);
		}
		if (_eomodelPlistGenerator == null) {
			_eomodelPlistGenerator = new EOModelPListGenerator(getProjectGenerator(), getEOModel());
		}
		_eomodelPlistGenerator.buildResourcesAndSetGenerators(repository, resources);
		getEOModel().getDMModel().getClassLibrary().clearLibrary();
		_entityClassGenerators.clear();
		_entityGenerators.clear();
		_entityClassGenerators = entityClassGenerators;
		_entityGenerators = entityGenerators;
	}

	private EOEntityPListGenerator getGenerator(DMEOEntity e) {
		if (_entityGenerators.get(e) == null) {
			_entityGenerators.put(e, new EOEntityPListGenerator(getProjectGenerator(), e));
		}
		return _entityGenerators.get(e);
	}

	private GenericRecordGenerator getGenericRecordGenerator(DMEOEntity e) {
		if (_entityClassGenerators.get(e) == null) {
			_entityClassGenerators.put(e, new GenericRecordGenerator(getProjectGenerator(), e));
		}
		return _entityClassGenerators.get(e);
	}

	@Override
	public void generate(boolean forceRegenerate) throws GenerationException {
		startGeneration();
		for (DMEOEntity entity : getEOModel().getEntities()) {
			if (entity.getClassProperties().size() == 0)
				continue;
			getGenerator(entity).generate(forceRegenerate);
		}
		for (DMEOEntity entity : getEOModel().getEntities()) {
			getGenerator(entity).generate(forceRegenerate);
		}
		_eomodelPlistGenerator.generate(forceRegenerate);
		stopGeneration();
	}

	public DMEOModel getEOModel() {
		return _eoModel;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof EOEntityInserted) {
			refreshConcernedResources();
		}
		super.update(observable, dataModification);
	}

}
