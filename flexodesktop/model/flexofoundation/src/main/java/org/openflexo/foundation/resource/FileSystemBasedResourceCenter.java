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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.openflexo.foundation.rm.ViewPointResource;
import org.openflexo.foundation.rm.ViewPointResourceImpl;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.foundation.technologyadapter.ModelRepository;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.ViewPointRepository;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.IProgress;

/**
 * An abstract implementation of a {@link FlexoResourceCenter} based on a file system.
 * 
 * It defines a {@link File} which is a directory containing all resources
 * 
 * @author sylvain
 * 
 */
public abstract class FileSystemBasedResourceCenter extends FileResourceRepository<FlexoFileResource<?>> implements
		FlexoResourceCenter<File> {

	protected static final Logger logger = Logger.getLogger(FileSystemBasedResourceCenter.class.getPackage().getName());

	private File rootDirectory;

	private ViewPointRepository viewPointRepository;
	private HashMap<TechnologyAdapter, ModelRepository<?, ?, ?, ?>> modelRepositories = new HashMap<TechnologyAdapter, ModelRepository<?, ?, ?, ?>>();
	private HashMap<TechnologyAdapter, MetaModelRepository<?, ?, ?, ?>> metaModelRepositories = new HashMap<TechnologyAdapter, MetaModelRepository<?, ?, ?, ?>>();

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
		for (TechnologyAdapter technologyAdapter : technologyAdapterService.getTechnologyAdapters()) {
			logger.info("Initializing resource center " + this + " with adapter " + technologyAdapter.getName());
			technologyAdapter.initializeResourceCenter(this);
		}
	}

	@Override
	public Iterator<File> iterator() {
		List<File> allFiles = new ArrayList<File>();
		appendFiles(getRootDirectory(), allFiles);
		return allFiles.iterator();
	}

	private void appendFiles(File directory, List<File> files) {
		if (directory.exists() && directory.isDirectory() && directory.canRead()) {
			for (File f : directory.listFiles()) {
				if (!isIgnorable(f)) {
					files.add(f);
					if (f.isDirectory()) {
						appendFiles(f, files);
					}
				}
			}
		}
	}

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

	protected synchronized void fileModified(File file) {
		if (!isIgnorable(file)) {
			System.out.println("File MODIFIED " + file.getName() + " in " + file.getParentFile().getAbsolutePath());
		}
	}

	protected synchronized void fileAdded(File file) {
		if (!isIgnorable(file)) {
			System.out.println("File ADDED " + file.getName() + " in " + file.getParentFile().getAbsolutePath());
			analyseAsViewPoint(file);
			if (technologyAdapterService != null) {
				for (TechnologyAdapter adapter : technologyAdapterService.getTechnologyAdapters()) {
					logger.info("fileAdded " + file + " with adapter " + adapter.getName());
					adapter.contentsAdded(this, file);
				}
			}
		}
	}

	protected synchronized void fileDeleted(File file) {
		if (!isIgnorable(file)) {
			System.out.println("File DELETED " + file.getName() + " in " + file.getParentFile().getAbsolutePath());
		}
	}

	private List<File> willBeWrittenFiles = new ArrayList<File>();

	public synchronized void willWrite(File file) {
		willBeWrittenFiles.add(file);
	}

	public synchronized boolean isIgnorable(File file) {
		if (file.getName().equals("CVS")) {
			return true;
		}
		if (willBeWrittenFiles.contains(file)) {
			System.out.println("File IGNORED: " + file);
			willBeWrittenFiles.remove(file);
			return true;
		}
		return false;
	}

	private HashMap<TechnologyAdapter, HashMap<Class<? extends ResourceRepository<?>>, ResourceRepository<?>>> repositories = new HashMap<TechnologyAdapter, HashMap<Class<? extends ResourceRepository<?>>, ResourceRepository<?>>>();

	private HashMap<Class<? extends ResourceRepository<?>>, ResourceRepository<?>> getRepositoriesForAdapter(
			TechnologyAdapter technologyAdapter) {
		HashMap<Class<? extends ResourceRepository<?>>, ResourceRepository<?>> map = repositories.get(technologyAdapter);
		if (map == null) {
			map = new HashMap<Class<? extends ResourceRepository<?>>, ResourceRepository<?>>();
			repositories.put(technologyAdapter, map);
		}
		return map;
	}

	@Override
	public final <R extends ResourceRepository<?>> R getRepository(Class<? extends R> repositoryType, TechnologyAdapter technologyAdapter) {
		HashMap<Class<? extends ResourceRepository<?>>, ResourceRepository<?>> map = getRepositoriesForAdapter(technologyAdapter);
		return (R) map.get(repositoryType);
	}

	@Override
	public final <R extends ResourceRepository<?>> void registerRepository(R repository, Class<? extends R> repositoryType,
			TechnologyAdapter technologyAdapter) {
		HashMap<Class<? extends ResourceRepository<?>>, ResourceRepository<?>> map = getRepositoriesForAdapter(technologyAdapter);
		if (map.get(repositoryType) == null) {
			map.put(repositoryType, repository);
		} else {
			logger.warning("Repository already registered: " + repositoryType + " for " + repository);
		}
	}

	@Override
	public Collection<ResourceRepository<?>> getRegistedRepositories(TechnologyAdapter technologyAdapter) {
		return getRepositoriesForAdapter(technologyAdapter).values();
	}

	@Override
	public <T extends ResourceData<T>> List<FlexoResource<T>> retrieveResource(String uri, Class<T> type, IProgress progress) {
		// TODO: provide support for class and version
		FlexoResource<T> uniqueResource = retrieveResource(uri, null, null, progress);
		return Collections.singletonList(uniqueResource);
	}

	@Override
	public <T extends ResourceData<T>> FlexoResource<T> retrieveResource(String uri, FlexoVersion version, Class<T> type, IProgress progress) {
		// TODO: provide support for class and version
		return (FlexoResource<T>) retrieveResource(uri, progress);
	}

	@Override
	public FlexoResource<?> retrieveResource(String uri, IProgress progress) {
		return getResource(uri);
	}

}
