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
package org.openflexo.foundation.resource;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.foundation.technologyadapter.ModelRepository;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;

/**
 * An abstract implementation of a {@link FlexoResourceCenter} based on a file system.
 * 
 * It defines a {@link File} which is a directory containing all resources
 * 
 * @author sylvain
 * 
 */
public abstract class FileSystemBasedResourceCenter implements FlexoResourceCenter {

	protected static final Logger logger = Logger.getLogger(FileSystemBasedResourceCenter.class.getPackage().getName());

	private File rootDirectory;

	private HashMap<TechnologyAdapter<?, ?, ?>, ModelRepository<?, ?, ?, ?>> modelRepositories = new HashMap<TechnologyAdapter<?, ?, ?>, ModelRepository<?, ?, ?, ?>>();
	private HashMap<TechnologyAdapter<?, ?, ?>, MetaModelRepository<?, ?, ?, ?>> metaModelRepositories = new HashMap<TechnologyAdapter<?, ?, ?>, MetaModelRepository<?, ?, ?, ?>>();

	public FileSystemBasedResourceCenter(File rootDirectory) {
		super();
		this.rootDirectory = rootDirectory;
	}

	public File getRootDirectory() {
		return rootDirectory;
	}

	@Override
	public void initialize(TechnologyAdapterService technologyAdapterService) {
		for (TechnologyAdapter<?, ?, ?> adapter : technologyAdapterService.getTechnologyAdapters()) {
			initializeForTechnology(adapter);
		}
	}

	private <MR extends FlexoResource<M>, M extends FlexoModel<M, MM>, MMR extends FlexoResource<MM>, MM extends FlexoMetaModel<MM>, MS extends ModelSlot<M, MM>, TA extends TechnologyAdapter<M, MM, MS>> void initializeForTechnology(
			TA technologyAdapter) {
		MetaModelRepository<MMR, M, MM, TA> mmRepository = (MetaModelRepository<MMR, M, MM, TA>) technologyAdapter
				.createMetaModelRepository(this);
		metaModelRepositories.put(technologyAdapter, mmRepository);
		ModelRepository<MR, M, MM, TA> modelRepository = (ModelRepository<MR, M, MM, TA>) technologyAdapter.createModelRepository(this);
		modelRepositories.put(technologyAdapter, modelRepository);
		exploreDirectoryLookingForMetaModels(rootDirectory, mmRepository.getRootFolder(), technologyAdapter, mmRepository);
		exploreDirectoryLookingForModels(rootDirectory, mmRepository.getRootFolder(), technologyAdapter, mmRepository, modelRepository);
	}

	private <MR extends FlexoResource<M>, M extends FlexoModel<M, MM>, MMR extends FlexoResource<MM>, MM extends FlexoMetaModel<MM>, MS extends ModelSlot<M, MM>, TA extends TechnologyAdapter<M, MM, MS>> void exploreDirectoryLookingForMetaModels(
			File directory, RepositoryFolder folder, TA technologyAdapter, MetaModelRepository<MMR, M, MM, TA> mmRepository) {
		for (File f : directory.listFiles()) {
			if (technologyAdapter.isValidMetaModelFile(f)) {
				mmRepository.registerResource((MMR) technologyAdapter.retrieveMetaModelResource(f), folder);
			}
			if (f.isDirectory() && !f.getName().equals("CVS")) {
				RepositoryFolder newFolder = new RepositoryFolder(f.getName(), folder, mmRepository);
				exploreDirectoryLookingForMetaModels(f, newFolder, technologyAdapter, mmRepository);
			}
		}
	}

	private <MR extends FlexoResource<M>, M extends FlexoModel<M, MM>, MMR extends FlexoResource<MM>, MM extends FlexoMetaModel<MM>, MS extends ModelSlot<M, MM>, TA extends TechnologyAdapter<M, MM, MS>> void exploreDirectoryLookingForModels(
			File directory, RepositoryFolder folder, TechnologyAdapter<?, MM, ?> technologyAdapter,
			MetaModelRepository<MMR, ?, ?, ?> mmRepository, ModelRepository<MR, ?, ?, ?> modelRepository) {
		for (File f : directory.listFiles()) {
			for (MMR metaModelResource : mmRepository.getAllResources()) {
				if (technologyAdapter.isValidModelFile(f, metaModelResource)) {
					modelRepository.registerResource((MR) technologyAdapter.retrieveModelResource(f), folder);
				}
			}
			if (f.isDirectory() && !f.getName().equals("CVS")) {
				RepositoryFolder newFolder = new RepositoryFolder(f.getName(), folder, mmRepository);
				exploreDirectoryLookingForModels(f, newFolder, technologyAdapter, mmRepository, modelRepository);
			}
		}

	}

	/**
	 * Retrieve model repository for a given {@link TechnologyAdapter}
	 * 
	 * @param technologyAdapter
	 * @return
	 */
	@Override
	public final <R extends FlexoResource<? extends M>, M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM, ? extends ModelSlot<M, MM>>> ModelRepository<R, M, MM, TA> getModelRepository(
			TA technologyAdapter) {
		return (ModelRepository<R, M, MM, TA>) modelRepositories.get(technologyAdapter);
	}

	/**
	 * Retrieve meta-model repository for a given {@link TechnologyAdapter}
	 * 
	 * @param technologyAdapter
	 * @return
	 */
	@Override
	public final <R extends FlexoResource<? extends MM>, M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM, ? extends ModelSlot<M, MM>>> MetaModelRepository<R, M, MM, TA> getMetaModelRepository(
			TA technologyAdapter) {
		return (MetaModelRepository<R, M, MM, TA>) metaModelRepositories.get(technologyAdapter);
	}

}
