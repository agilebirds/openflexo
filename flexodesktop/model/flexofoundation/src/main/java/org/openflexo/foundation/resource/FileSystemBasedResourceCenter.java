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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.openflexo.foundation.rm.ViewPointResource;
import org.openflexo.foundation.rm.ViewPointResourceImpl;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.foundation.technologyadapter.ModelRepository;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.ViewPointRepository;

/**
 * An abstract implementation of a {@link FlexoResourceCenter} based on a file system.
 * 
 * It defines a {@link File} which is a directory containing all resources
 * 
 * @author sylvain
 * 
 */
public abstract class FileSystemBasedResourceCenter extends FileResourceRepository<FlexoResource<?>> implements FlexoResourceCenter {

	protected static final Logger logger = Logger.getLogger(FileSystemBasedResourceCenter.class.getPackage().getName());

	private File rootDirectory;

	private ViewPointRepository viewPointRepository;
	private HashMap<TechnologyAdapter<?, ?>, ModelRepository<?, ?, ?, ?>> modelRepositories = new HashMap<TechnologyAdapter<?, ?>, ModelRepository<?, ?, ?, ?>>();
	private HashMap<TechnologyAdapter<?, ?>, MetaModelRepository<?, ?, ?, ?>> metaModelRepositories = new HashMap<TechnologyAdapter<?, ?>, MetaModelRepository<?, ?, ?, ?>>();

	private TechnologyAdapterService technologyAdapterService;

	public FileSystemBasedResourceCenter(File rootDirectory) {
		super(null, rootDirectory);
		setOwner(this);
		this.rootDirectory = rootDirectory;
		startDirectoryWatching();
	}

	public File getRootDirectory() {
		return rootDirectory;
	}

	@Override
	public String toString() {
		return super.toString() + " directory=" + (getRootDirectory() != null ? getRootDirectory().getAbsolutePath() : null);
	}

	@Override
	public ViewPointRepository getViewPointRepository() {
		return viewPointRepository;
	}

	@Override
	public void initialize(ViewPointLibrary viewPointLibrary) {
		logger.info("Initializing ViewPointLibrary for " + this);
		viewPointRepository = new ViewPointRepository(this, viewPointLibrary);
		exploreDirectoryLookingForViewPoints(rootDirectory, viewPointLibrary);
	}

	/**
	 * Retrieve (creates it when not existant) folder containing supplied file
	 * 
	 * @param repository
	 * @param aFile
	 * @return
	 */
	protected <R extends FlexoResource<?>> RepositoryFolder<R> retrieveRepositoryFolder(ResourceRepository<R> repository, File aFile) {
		try {
			return repository.getRepositoryFolder(aFile, true);
		} catch (IOException e) {
			e.printStackTrace();
			return repository.getRootFolder();
		}
	}

	/**
	 * 
	 * @param directory
	 * @param folder
	 * @param viewPointLibrary
	 * @return a flag indicating if some ViewPoints were found
	 */
	private boolean exploreDirectoryLookingForViewPoints(File directory, ViewPointLibrary viewPointLibrary) {
		boolean returned = false;
		logger.fine("Exploring " + directory);
		if (directory.exists() && directory.isDirectory() && directory.canRead()) {
			for (File f : directory.listFiles()) {
				ViewPointResource vpRes = analyseAsViewPoint(f);
				if (f.isDirectory() && !f.getName().equals("CVS")) {
					if (exploreDirectoryLookingForViewPoints(f, viewPointLibrary)) {
						returned = true;
					}
				}
			}
		}
		return returned;
	}

	/**
	 * 
	 * @param directory
	 * @param folder
	 * @param viewPointLibrary
	 * @return a flag indicating if some ViewPoints were found
	 */
	private ViewPointResource analyseAsViewPoint(File candidateFile) {
		if (candidateFile.exists() && candidateFile.isDirectory() && candidateFile.canRead()
				&& candidateFile.getName().endsWith(".viewpoint")) {
			ViewPointResource vpRes = ViewPointResourceImpl.retrieveViewPointResource(candidateFile,
					viewPointRepository.getViewPointLibrary());
			if (vpRes != null) {
				logger.info("Found and register viewpoint " + vpRes.getURI()
						+ (vpRes instanceof FlexoFileResource ? " file=" + ((FlexoFileResource) vpRes).getFile().getAbsolutePath() : ""));
				RepositoryFolder<ViewPointResource> folder = retrieveRepositoryFolder(viewPointRepository, candidateFile);
				viewPointRepository.registerResource(vpRes, folder);
				// Also register the resource in the ResourceCenter seen as a ResourceRepository
				try {
					registerResource(vpRes, getRepositoryFolder(candidateFile, true));
				} catch (IOException e) {
					e.printStackTrace();
				}
				return vpRes;
			} else {
				logger.warning("While exploring resource center looking for viewpoints : cannot retrieve resource for file "
						+ candidateFile.getAbsolutePath());
			}
		}

		return null;
	}

	@Override
	public void initialize(TechnologyAdapterService technologyAdapterService) {
		logger.info("Initializing " + technologyAdapterService);
		this.technologyAdapterService = technologyAdapterService;
		for (TechnologyAdapter<?, ?> adapter : technologyAdapterService.getTechnologyAdapters()) {
			logger.info("Initializing resource center " + this + " with adapter " + adapter.getName());
			TechnologyContextManager<?, ?> technologyContextManager = technologyAdapterService.getTechnologyContextManager(adapter);
			initializeForTechnology(adapter, technologyContextManager);
		}
	}

	private <MR extends FlexoModelResource<M, MM>, M extends FlexoModel<M, MM>, MMR extends FlexoMetaModelResource<M, MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM>> void initializeForTechnology(
			TA technologyAdapter, TechnologyContextManager<?, ?> technologyContextManager) {
		MetaModelRepository<MMR, M, MM, TA> mmRepository = (MetaModelRepository<MMR, M, MM, TA>) technologyAdapter
				.createMetaModelRepository(this);
		if (mmRepository != null) {
			metaModelRepositories.put(technologyAdapter, mmRepository);
			ModelRepository<MR, M, MM, TA> modelRepository = (ModelRepository<MR, M, MM, TA>) technologyAdapter.createModelRepository(this);
			modelRepositories.put(technologyAdapter, modelRepository);
			exploreDirectoryLookingForMetaModels(rootDirectory, technologyAdapter, technologyContextManager, mmRepository);
			exploreDirectoryLookingForModels(rootDirectory, technologyAdapter, technologyContextManager, mmRepository, modelRepository);
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
			File directory, TA technologyAdapter, TechnologyContextManager technologyContextManager,
			MetaModelRepository<MMR, M, MM, TA> mmRepository) {
		logger.fine("Exploring " + directory);
		boolean returned = false;
		if (directory.exists() && directory.isDirectory() && directory.canRead()) {
			for (File f : directory.listFiles()) {
				MMR mmRes = tryToRetrieveMetaModel(f, technologyAdapter, technologyContextManager);
				if (mmRes != null) {
					registerAsMetaModel(f, mmRes, technologyAdapter, technologyContextManager);
				}
				if (f.isDirectory() && !f.getName().equals("CVS")) {
					if (exploreDirectoryLookingForMetaModels(f, technologyAdapter, technologyContextManager, mmRepository)) {
						returned = true;
					}
				}
			}
		}
		return returned;
	}

	private <MR extends FlexoResource<M>, M extends FlexoModel<M, MM>, MMR extends FlexoResource<MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM>> MMR registerAsMetaModel(
			File candidateFile, MMR mmRes, TA technologyAdapter, TechnologyContextManager technologyContextManager) {
		logger.fine("TechnologyAdapter " + technologyAdapter.getName() + ": found and register metamodel " + mmRes.getURI()
				+ (mmRes instanceof FlexoFileResource ? " file=" + ((FlexoFileResource) mmRes).getFile().getAbsolutePath() : ""));

		MetaModelRepository<MMR, M, MM, TA> mmRepository = getMetaModelRepository(technologyAdapter);

		RepositoryFolder<MMR> folder = retrieveRepositoryFolder(mmRepository, candidateFile);
		mmRepository.registerResource(mmRes, folder);

		// Also register the resource in the ResourceCenter seen as a ResourceRepository
		try {
			registerResource(mmRes, getRepositoryFolder(candidateFile, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mmRes;
	}

	private <MR extends FlexoModelResource<M, MM>, M extends FlexoModel<M, MM>, MMR extends FlexoMetaModelResource<M, MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM>> MMR tryToRetrieveMetaModel(
			File candidateFile, TA technologyAdapter, TechnologyContextManager technologyContextManager) {
		if (technologyAdapter.isValidMetaModelFile(candidateFile, technologyContextManager)) {
			return (MMR) technologyAdapter.retrieveMetaModelResource(candidateFile, technologyContextManager);
		}
		return null;
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
	private <MR extends FlexoModelResource<M, MM>, M extends FlexoModel<M, MM>, MMR extends FlexoMetaModelResource<M, MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM>> boolean exploreDirectoryLookingForModels(
			File directory, TA technologyAdapter, TechnologyContextManager technologyContextManager,
			MetaModelRepository<MMR, ?, ?, ?> mmRepository, ModelRepository<MR, ?, ?, ?> modelRepository) {
		logger.fine("Exploring " + directory);
		boolean returned = false;
		if (directory.exists() && directory.isDirectory() && directory.canRead()) {
			for (File f : directory.listFiles()) {
				MR modelRes = tryToRetrieveModel(f, technologyAdapter, technologyContextManager);
				if (modelRes != null) {
					registerAsModel(f, modelRes, modelRes.getMetaModelResource(), technologyAdapter, technologyContextManager);
				}
				if (f.isDirectory() && !f.getName().equals("CVS")) {
					if (exploreDirectoryLookingForModels(f, technologyAdapter, technologyContextManager, mmRepository, modelRepository)) {
						returned = true;
					}
				}
			}
		}
		return returned;
	}

	private <MR extends FlexoResource<M>, M extends FlexoModel<M, MM>, MMR extends FlexoResource<MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM>> MR registerAsModel(
			File candidateFile, MR modelRes, MMR metaModelResource, TA technologyAdapter, TechnologyContextManager technologyContextManager) {

		MetaModelRepository<MMR, M, MM, TA> mmRepository = getMetaModelRepository(technologyAdapter);
		ModelRepository<MR, M, MM, TA> modelRepository = getModelRepository(technologyAdapter);

		if (technologyAdapter.isValidModelFile(candidateFile, metaModelResource, technologyContextManager)) {
			logger.fine("TechnologyAdapter " + technologyAdapter.getName() + ": found and register model " + modelRes.getURI()
					+ (modelRes instanceof FlexoFileResource ? " file=" + ((FlexoFileResource) modelRes).getFile().getAbsolutePath() : ""));
			// ((FlexoModelResource<M, MM>)modelRes).setMetaModel(metaModelResource.getResourceData(null));
			RepositoryFolder<MR> folder = retrieveRepositoryFolder(modelRepository, candidateFile);
			modelRepository.registerResource(modelRes, folder);
			// Also register the resource in the ResourceCenter seen as a ResourceRepository
			try {
				registerResource(modelRes, getRepositoryFolder(candidateFile, true));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return modelRes;
		}
		return null;
	}

	private <MR extends FlexoModelResource<M, MM>, M extends FlexoModel<M, MM>, MMR extends FlexoMetaModelResource<M, MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM>> MR tryToRetrieveModel(
			File candidateFile, TechnologyAdapter<?, MM> technologyAdapter, TechnologyContextManager technologyContextManager) {
		if (technologyAdapter.isValidModelFile(candidateFile, technologyContextManager)) {
			return (MR) technologyAdapter.retrieveModelResource(candidateFile, technologyContextManager);
		}
		return null;
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
		ModelRepository<R, M, MM, TA> modelRepository = (ModelRepository<R, M, MM, TA>) modelRepositories.get(technologyAdapter);
		if (modelRepository == null) {
			modelRepository = (ModelRepository<R, M, MM, TA>) technologyAdapter.createModelRepository(this);
			if (modelRepository != null) {
				modelRepositories.put(technologyAdapter, modelRepository);
			}
		}
		return modelRepository;
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
		MetaModelRepository<R, M, MM, TA> mmRepository = (MetaModelRepository<R, M, MM, TA>) metaModelRepositories.get(technologyAdapter);
		if (mmRepository == null) {
			mmRepository = (MetaModelRepository<R, M, MM, TA>) technologyAdapter.createMetaModelRepository(this);
			if (mmRepository != null) {
				metaModelRepositories.put(technologyAdapter, mmRepository);
			}
		}
		return mmRepository;
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

	@Override
	public String getName() {
		if (getRootDirectory() != null) {
			return getRootDirectory().getAbsolutePath();
		}
		return "unset";
	}

	private DirectoryWatcher directoryWatcher;

	private ScheduledFuture<?> scheduleWithFixedDelay;

	public void startDirectoryWatching() {
		if (getRootDirectory() != null && getRootDirectory().exists()) {
			directoryWatcher = new DirectoryWatcher(getRootDirectory()) {
				@Override
				protected void fileModified(File file) {
					FileSystemBasedResourceCenter.this.fileModified(file);
				}

				@Override
				protected void fileAdded(File file) {
					FileSystemBasedResourceCenter.this.fileAdded(file);
				}

				@Override
				protected void fileDeleted(File file) {
					FileSystemBasedResourceCenter.this.fileDeleted(file);
				}
			};

			ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(1);
			scheduleWithFixedDelay = newScheduledThreadPool.scheduleWithFixedDelay(directoryWatcher, 0, 1, TimeUnit.SECONDS);
		}
	}

	public void stopDirectoryWatching() {
		if (getRootDirectory() != null && getRootDirectory().exists()) {
			scheduleWithFixedDelay.cancel(true);
		}
	}

	protected void fileModified(File file) {
		System.out.println("File MODIFIED " + file.getName() + " in " + file.getParentFile().getAbsolutePath());
		// System.out.println("Aborting in FileSystemBasedResourceCenter");
		// System.exit(-1);
	}

	protected void fileAdded(File file) {
		System.out.println("File ADDED " + file.getName() + " in " + file.getParentFile().getAbsolutePath());
		analyseAsViewPoint(file);
		if (technologyAdapterService != null) {
			for (TechnologyAdapter adapter : technologyAdapterService.getTechnologyAdapters()) {
				logger.info("Initializing resource center " + this + " with adapter " + adapter.getName());
				TechnologyContextManager technologyContextManager = technologyAdapterService.getTechnologyContextManager(adapter);
				FlexoResource<? extends FlexoMetaModel> mmRes = tryToRetrieveMetaModel(file, adapter, technologyContextManager);
				if (mmRes != null) {
					registerAsMetaModel(file, mmRes, adapter, technologyContextManager);
				}
				FlexoModelResource<?, ?> modelRes = tryToRetrieveModel(file, adapter, technologyContextManager);
				if (modelRes != null) {
					registerAsModel(file, modelRes, modelRes.getMetaModelResource(), adapter, technologyContextManager);
				}
			}
		}
	}

	protected void fileDeleted(File file) {
		System.out.println("File DELETED " + file.getName() + " in " + file.getParentFile().getAbsolutePath());
	}

}
