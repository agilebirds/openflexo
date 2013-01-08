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
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.openflexo.foundation.rm.ViewPointResource;
import org.openflexo.foundation.rm.ViewPointResourceImpl;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.foundation.technologyadapter.ModelRepository;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.ViewPointRepository;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FileUtils.CopyStrategy;

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

	private static final File VIEWPOINT_LIBRARY_DIR = new FileResource("ViewPoints");

	private File rootDirectory;

	private ViewPointRepository viewPointRepository;
	private HashMap<TechnologyAdapter<?, ?>, ModelRepository<?, ?, ?, ?>> modelRepositories = new HashMap<TechnologyAdapter<?, ?>, ModelRepository<?, ?, ?, ?>>();
	private HashMap<TechnologyAdapter<?, ?>, MetaModelRepository<?, ?, ?, ?>> metaModelRepositories = new HashMap<TechnologyAdapter<?, ?>, MetaModelRepository<?, ?, ?, ?>>();

	public FileSystemBasedResourceCenter(File rootDirectory) {
		super();
		this.rootDirectory = rootDirectory;
	}

	public File getRootDirectory() {
		return rootDirectory;
	}

	@Override
	public String toString() {
		return super.toString() + " directory=" + getRootDirectory().getAbsolutePath();
	}

	@Override
	public ViewPointRepository getViewPointRepository() {
		return viewPointRepository;
	}

	@Override
	public void initialize(ViewPointLibrary viewPointLibrary) {
		logger.info("Initializing ViewPointLibrary");
		viewPointRepository = new ViewPointRepository(this, viewPointLibrary);
		exploreDirectoryLookingForViewPoints(rootDirectory, viewPointRepository.getRootFolder(), viewPointLibrary);
	}

	/**
	 * 
	 * @param directory
	 * @param folder
	 * @param viewPointLibrary
	 * @return a flag indicating if some ViewPoints were found
	 */
	private boolean exploreDirectoryLookingForViewPoints(File directory, RepositoryFolder<ViewPointResource> folder,
			ViewPointLibrary viewPointLibrary) {
		boolean returned = false;
		logger.fine("Exploring " + directory);
		if (directory.exists() && directory.isDirectory()) {
			for (File f : directory.listFiles()) {
				if (f.isDirectory() && f.getName().endsWith(".viewpoint")) {
					ViewPointResource vpRes = ViewPointResourceImpl.makeViewPointResource(f, viewPointLibrary);
					if (vpRes != null) {
						logger.info("Found and register viewpoint "
								+ vpRes.getURI()
								+ (vpRes instanceof FlexoFileResource ? " file=" + ((FlexoFileResource) vpRes).getFile().getAbsolutePath()
										: ""));
						viewPointRepository.registerResource(vpRes, folder);
						returned = true;
					} else {
						logger.warning("While exploring resource center looking for viewpoints : cannot retrieve resource for file "
								+ f.getAbsolutePath());
					}
				}
				if (f.isDirectory() && !f.getName().equals("CVS")) {
					RepositoryFolder newFolder = new RepositoryFolder(f.getName(), folder, viewPointRepository);
					if (exploreDirectoryLookingForViewPoints(f, newFolder, viewPointLibrary)) {
						returned = true;
					} else {
						folder.removeFromChildren(newFolder);
					}
				}
			}
		}
		return returned;
	}

	@Override
	public void initialize(TechnologyAdapterService technologyAdapterService) {
		logger.info("Initializing " + technologyAdapterService);
		for (TechnologyAdapter<?, ?> adapter : technologyAdapterService.getTechnologyAdapters()) {
			logger.info("Initializing resource center " + this + " with adapter " + adapter.getName());
			TechnologyContextManager<?, ?> technologyContextManager = technologyAdapterService.getTechnologyContextManager(adapter);
			initializeForTechnology(adapter, technologyContextManager);
		}
	}

	private <MR extends FlexoResource<M>, M extends FlexoModel<M, MM>, MMR extends FlexoResource<MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM>> void initializeForTechnology(
			TA technologyAdapter, TechnologyContextManager<?, ?> technologyContextManager) {
		MetaModelRepository<MMR, M, MM, TA> mmRepository = (MetaModelRepository<MMR, M, MM, TA>) technologyAdapter
				.createMetaModelRepository(this);
		if (mmRepository != null) {
			metaModelRepositories.put(technologyAdapter, mmRepository);
			ModelRepository<MR, M, MM, TA> modelRepository = (ModelRepository<MR, M, MM, TA>) technologyAdapter.createModelRepository(this);
			modelRepositories.put(technologyAdapter, modelRepository);
			exploreDirectoryLookingForMetaModels(rootDirectory, mmRepository.getRootFolder(), technologyAdapter, technologyContextManager,
					mmRepository);
			exploreDirectoryLookingForModels(rootDirectory, modelRepository.getRootFolder(), technologyAdapter, technologyContextManager,
					mmRepository, modelRepository);
		}
	}

	/**
	 * Explore a given directory, deeply searching about {@link FlexoMetaModel} conform to supplied technology
	 * 
	 * @param directory
	 * @param folder
	 * @param technologyAdapter
	 * @param technologyContextManager
	 * @param mmRepository
	 * @return a flag indicating if some metamodels were found
	 */
	private <MR extends FlexoResource<M>, M extends FlexoModel<M, MM>, MMR extends FlexoResource<MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM>> boolean exploreDirectoryLookingForMetaModels(
			File directory, RepositoryFolder folder, TA technologyAdapter, TechnologyContextManager technologyContextManager,
			MetaModelRepository<MMR, M, MM, TA> mmRepository) {
		logger.fine("Exploring " + directory);
		boolean returned = false;
		if (directory.exists() && directory.isDirectory()) {
			for (File f : directory.listFiles()) {
				if (technologyAdapter.isValidMetaModelFile(f, technologyContextManager)) {
					MMR mmRes = (MMR) technologyAdapter.retrieveMetaModelResource(f, technologyContextManager);
					if (mmRes != null) {
						logger.fine("TechnologyAdapter "
								+ technologyAdapter.getName()
								+ ": found and register metamodel "
								+ mmRes.getURI()
								+ (mmRes instanceof FlexoFileResource ? " file=" + ((FlexoFileResource) mmRes).getFile().getAbsolutePath()
										: ""));
						mmRepository.registerResource(mmRes, folder);
						returned = true;
					} else {
						logger.warning("TechnologyAdapter " + technologyAdapter.getName() + ": Cannot retrieve resource for metamodel "
								+ f.getAbsolutePath());
					}
				}
				if (f.isDirectory() && !f.getName().equals("CVS")) {
					RepositoryFolder newFolder = new RepositoryFolder(f.getName(), folder, mmRepository);
					if (exploreDirectoryLookingForMetaModels(f, newFolder, technologyAdapter, technologyContextManager, mmRepository)) {
						returned = true;
					} else {
						folder.removeFromChildren(newFolder);
					}
				}
			}
		}
		return returned;
	}

	/**
	 * Explore a given directory, deeply searching about {@link FlexoModel} conform to a given FlexoMetaModel in supplied technology
	 * 
	 * @param directory
	 * @param folder
	 * @param technologyAdapter
	 * @param technologyContextManager
	 * @param mmRepository
	 * @param modelRepository
	 * @return a flag indicating if some metamodels were found
	 */
	private <MR extends FlexoResource<M>, M extends FlexoModel<M, MM>, MMR extends FlexoResource<MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM>> boolean exploreDirectoryLookingForModels(
			File directory, RepositoryFolder folder, TechnologyAdapter<?, MM> technologyAdapter,
			TechnologyContextManager technologyContextManager, MetaModelRepository<MMR, ?, ?, ?> mmRepository,
			ModelRepository<MR, ?, ?, ?> modelRepository) {
		logger.fine("Exploring " + directory);
		boolean returned = false;
		if (directory.exists() && directory.isDirectory()) {
			for (File f : directory.listFiles()) {
				for (MMR metaModelResource : mmRepository.getAllResources()) {
					if (technologyAdapter.isValidModelFile(f, metaModelResource, technologyContextManager)) {
						MR modelRes = (MR) technologyAdapter.retrieveModelResource(f, metaModelResource, technologyContextManager);
						if (modelRes != null) {
							logger.fine("TechnologyAdapter "
									+ technologyAdapter.getName()
									+ ": found and register model "
									+ modelRes.getURI()
									+ (modelRes instanceof FlexoFileResource ? " file="
											+ ((FlexoFileResource) modelRes).getFile().getAbsolutePath() : ""));
							modelRepository.registerResource(modelRes, folder);
							returned = true;
						} else {
							logger.warning("TechnologyAdapter " + technologyAdapter.getName() + ": Cannot retrieve resource for model "
									+ f.getAbsolutePath());
						}
					}
				}
				if (f.isDirectory() && !f.getName().equals("CVS")) {
					RepositoryFolder newFolder = new RepositoryFolder(f.getName(), folder, mmRepository);
					if (exploreDirectoryLookingForModels(f, newFolder, technologyAdapter, technologyContextManager, mmRepository,
							modelRepository)) {
						returned = true;
					} else {
						folder.removeFromChildren(newFolder);
					}

				}
			}
		}
		return returned;
	}

	/**
	 * Retrieve model repository for a given {@link TechnologyAdapter}
	 * 
	 * @param technologyAdapter
	 * @return
	 */
	@Override
	public final <R extends FlexoResource<? extends M>, M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM>> ModelRepository<R, M, MM, TA> getModelRepository(
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
	public final <R extends FlexoResource<? extends MM>, M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM>> MetaModelRepository<R, M, MM, TA> getMetaModelRepository(
			TA technologyAdapter) {
		return (MetaModelRepository<R, M, MM, TA>) metaModelRepositories.get(technologyAdapter);
	}

	@Override
	public void update() throws IOException {
		copyViewPoints(VIEWPOINT_LIBRARY_DIR, getRootDirectory(), CopyStrategy.REPLACE_OLD_ONLY);
	}

	/*@Override
	public ViewPointLibrary getViewPointLibrary() {
		if (viewPointLibrary == null) {
			viewPointLibrary = new ViewPointLibrary(this);
		}
		return viewPointLibrary;
	}

	@Override
	public ViewPointLibrary retrieveViewPointLibrary() {
		if (viewPointLibrary == null) {
			viewPointLibrary = new ViewPointLibrary(this);
			findViewPoints(new File(getRootDirectory(), "ViewPoints"), viewPointLibrary.getRootFolder());
		}
		return viewPointLibrary;
	}

	private void findViewPoints(File dir, ViewPointFolder folder) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (dir.listFiles().length == 0) {
			copyViewPoints(VIEWPOINT_LIBRARY_DIR, getRootDirectory(), CopyStrategy.REPLACE);
		}
		for (File f : dir.listFiles()) {
			if (f.isDirectory() && f.getName().endsWith(".viewpoint")) {
				if (f.listFiles().length > 0) {
					try {
						viewPointLibrary.importViewPoint(f, folder);
					} catch (Exception e) {
						logger.warning("Could not load ViewPoint " + f.getAbsolutePath());
						e.printStackTrace();
					}
				}
			} else if (f.isDirectory() && !f.getName().equals("CVS")) {
				ViewPointFolder newFolder = new ViewPointFolder(f.getName(), folder, viewPointLibrary);
				findViewPoints(f, newFolder);
			}
		}
	}
	*/

	@Deprecated
	private static void copyViewPoints(File initialDirectory, File resourceCenterDirectory, CopyStrategy copyStrategy) {
		if (initialDirectory.getParentFile().equals(resourceCenterDirectory)) {
			return;
		}

		try {
			FileUtils.copyDirToDir(VIEWPOINT_LIBRARY_DIR, resourceCenterDirectory, copyStrategy);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return getRootDirectory().getAbsolutePath();
	}

}
