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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.ViewPointRepository;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.IProgress;

/**
 * A {@link FlexoResourceCenter} is a symbolic repository storing {@link FlexoResource}
 * 
 * @param <I>
 *            I is the type of iterable items this resource center stores
 * 
 * @author sylvain
 * 
 */
@ModelEntity
public interface FlexoResourceCenter<I> {

	/**
	 * Return a user-friendly named identifier for this resource center
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Initialize the FlexoResourceCenter by retrieving viewpoints defined in this {@link FlexoResourceCenter}<br>
	 * 
	 * @param technologyAdapterService
	 */
	public void initialize(ViewPointLibrary viewPointLibrary);

	/**
	 * Initialize the FlexoResourceCenter by matching contents of this {@link FlexoResourceCenter} with all available technologies.<br>
	 * 
	 * @param technologyAdapterService
	 */
	public void initialize(TechnologyAdapterService technologyAdapterService);

	/**
	 * Returns all resources available in this resource center
	 * 
	 * @param progress
	 *            a progress monitor that will be notified of the progress of this task. This parameter can be <code>null</code>
	 * @return a list of all resources available in this resource center.
	 */
	public @Nonnull
	Collection<FlexoResource<?>> getAllResources(@Nullable IProgress progress);

	/**
	 * Returns the resource identified by the given <code>uri</code> and the provided <code>version</code>.
	 * 
	 * @param uri
	 *            the URI of the resource
	 * @param version
	 *            the version of the resource
	 * @param type
	 *            the type of the resource data reference by the resource to retrieve. The implementation is responsible to make the
	 *            appropriate type verifications.
	 * @param progress
	 *            a progress monitor that will be notified of the progress of this task. This parameter can be <code>null</code>
	 * @return the resource with the given <code>uri</code> and the provided <code>version</code>, or null if it cannot be found.
	 */
	public @Nullable
	<T extends ResourceData<T>> FlexoResource<T> retrieveResource(@Nonnull String uri, @Nonnull FlexoVersion version,
			@Nonnull Class<T> type, @Nullable IProgress progress);

	/**
	 * Returns all available versions of the resource identified by the given <code>uri</code>
	 * 
	 * @param uri
	 *            the URI of the resource
	 * @param type
	 *            the type of the resource data reference by the resource to retrieve. The implementation is responsible to make the
	 *            appropriate type verifications.
	 * @param progress
	 *            a progress monitor that will be notified of the progress of this task. This parameter can be <code>null</code>
	 * @return all available versions of the resource identified by the given <code>uri</code>. An empty list is returned if no match were
	 *         found
	 */
	public @Nonnull
	<T extends ResourceData<T>> List<FlexoResource<T>> retrieveResource(@Nonnull String uri, @Nonnull Class<T> type,
			@Nullable IProgress progress);

	/**
	 * Publishes the resource in this resource center.
	 * 
	 * @param resource
	 *            the resource to publish
	 * @param newVersion
	 *            the new version of this resource. If this value is null, the implementation is responsible to set the version
	 *            appropriately (can be left unchanged or updated)
	 * @param progress
	 *            a progress monitor that will be notified of the progress of this task. This parameter can be <code>null</code>
	 * @throws Exception
	 *             in case the publication of this resource failed.
	 */
	public void publishResource(@Nonnull FlexoResource<?> resource, @Nullable FlexoVersion newVersion, @Nullable IProgress progress)
			throws Exception;

	/**
	 * Refreshes this resource center. This can be particularly useful for caching implementations.
	 */
	public void update() throws IOException;

	/**
	 * Retrieve ViewPoint repository (containing all resources storing a ViewPoint) for this {@link FlexoResourceCenter}
	 * 
	 * @return
	 */
	public ViewPointRepository getViewPointRepository();

	/**
	 * Returns an iterator over a set of elements of type I, which are iterables items this resource center stores
	 * 
	 * @return an Iterator.
	 */
	public Iterator<I> iterator();

	/**
	 * Retrieve repository matching supplied type and technology
	 * 
	 * @param repositoryType
	 * @param technologyAdapter
	 * @return the registered repository
	 */
	public <R extends ResourceRepository<?>> R getRepository(Class<? extends R> repositoryType, TechnologyAdapter technologyAdapter);

	/**
	 * Register supplied repository for a given type and technology
	 * 
	 * @param repository
	 *            the non-null repository to register
	 * @param repositoryType
	 * @param technologyAdapter
	 */
	public <R extends ResourceRepository<?>> void registerRepository(R repository, Class<? extends R> repositoryType,
			TechnologyAdapter technologyAdapter);

}
