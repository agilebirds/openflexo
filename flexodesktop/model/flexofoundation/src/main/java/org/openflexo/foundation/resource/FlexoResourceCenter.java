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
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.toolbox.IProgress;

@ModelEntity
public interface FlexoResourceCenter {

	/**
	 * Returns all resources available in this resource center
	 * 
	 * @param progress
	 *            a progress monitor that will be notified of the progress of this task. This parameter can be <code>null</code>
	 * @return a list of all resources available in this resource center.
	 */
	public @Nonnull
	List<FlexoResource<?>> getAllResources(@Nullable IProgress progress);

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
	<T extends ResourceData<T>> FlexoResource<T> retrieveResource(@Nonnull String uri, @Nonnull String version, @Nonnull Class<T> type,
			@Nullable IProgress progress);

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
	public void publishResource(@Nonnull FlexoResource<?> resource, @Nullable String newVersion, @Nullable IProgress progress)
			throws Exception;

	/**
	 * Refreshes this resource center. This can be particularly useful for caching implementations.
	 */
	public void update() throws IOException;

	@Deprecated
	public OntologyLibrary retrieveBaseOntologyLibrary();

	@Deprecated
	public ViewPointLibrary retrieveViewPointLibrary();

	@Deprecated
	public ViewPoint getOntologyCalc(String ontologyCalcUri);

	@Deprecated
	public File getNewCalcSandboxDirectory();

}
