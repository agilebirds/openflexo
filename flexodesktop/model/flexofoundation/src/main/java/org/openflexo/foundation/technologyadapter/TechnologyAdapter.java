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
package org.openflexo.foundation.technologyadapter;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel;

/**
 * This class represents a technology adapter<br>
 * A {@link TechnologyAdapter} is plugin loaded at run-time which defines and implements the required A.P.I used to connect Flexo Modelling
 * Language Virtual Machine to a technology.<br>
 * 
 * Note: this code was partially adapted from Nicolas Daniels (Blue Pimento team)
 * 
 * @author sylvain
 * 
 */
public abstract class TechnologyAdapter<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> {

	private static final Logger logger = Logger.getLogger(TechnologyAdapter.class.getPackage().getName());

	private TechnologyAdapterService technologyAdapterService;

	/**
	 * Return human-understandable name for this technology adapter<br>
	 * Unique id to consider must be the class name
	 * 
	 * @return
	 */
	public abstract String getName();

	/**
	 * Creates a new ModelSlot in the scope of the supplied {@link ViewPoint}
	 * 
	 * @return a new {@link ModelSlot}
	 */
	public abstract ModelSlot<M, MM> createNewModelSlot(ViewPoint viewPoint);

	/**
	 * Creates a new ModelSlot in the scope of supplied {@link VirtualModel}
	 * 
	 * @return a new {@link ModelSlot}
	 */
	public abstract ModelSlot<M, MM> createNewModelSlot(VirtualModel<?> virtualModel);

	/**
	 * Return flag indicating if supplied file represents a valid XSD schema
	 * 
	 * @param aMetaModelFile
	 * @param rc
	 * 
	 * @return
	 */
	public abstract boolean isValidMetaModelFile(File aMetaModelFile, TechnologyContextManager<M, MM> technologyContextManager);

	/**
	 * Retrieve and return URI for supplied meta model file, if supplied file represents a valid meta model
	 * 
	 * @param aMetaModelFile
	 * @param rc
	 *            TODO
	 * @return
	 */
	public abstract String retrieveMetaModelURI(File aMetaModelFile, TechnologyContextManager<M, MM> technologyContextManager);

	/**
	 * Instantiate new meta model resource stored in supplied meta model file
	 * 
	 * @param aMetaModelFile
	 * @param rc
	 *            TODO
	 * @return
	 */
	public abstract FlexoResource<MM> retrieveMetaModelResource(File aMetaModelFile,
			TechnologyContextManager<M, MM> technologyContextManager);

	/**
	 * Return flag indicating if supplied file represents a valid model conform to supplied meta-model
	 * 
	 * @param aModelFile
	 * @param metaModelResource
	 * @param rc
	 *            TODO
	 * @return
	 */
	public abstract boolean isValidModelFile(File aModelFile, FlexoResource<MM> metaModelResource,
			TechnologyContextManager<M, MM> technologyContextManager);

	/**
	 * Return flag indicating if supplied file represents a valid model<br>
	 * Note that the meta-model is not yet known
	 * 
	 * @param aModelFile
	 * @param metaModelResource
	 * @param rc
	 *            TODO
	 * @return
	 */
	public abstract boolean isValidModelFile(File aModelFile, TechnologyContextManager<M, MM> technologyContextManager);

	/**
	 * Retrieve and return URI for supplied model file
	 * 
	 * @param aModelFile
	 * @param rc
	 *            TODO
	 * @return
	 */
	public abstract String retrieveModelURI(File aModelFile, FlexoResource<MM> metaModelResource,
			TechnologyContextManager<M, MM> technologyContextManager);

	/**
	 * Instantiate new model resource stored in supplied model file<br>
	 * The metamodel is not yet known, so we have to iterate on all known metamodels of this technology to find one (or many) which is
	 * relevant
	 * 
	 * @return
	 */
	public abstract FlexoResource<M> retrieveModelResource(File aModelFile, TechnologyContextManager<M, MM> technologyContextManager);

	/**
	 * Instantiate new model resource stored in supplied model file, given the conformant metamodel<br>
	 * We assert here that model resource is conform to supplied metamodel, ie we will not try to lookup the metamodel but take the one
	 * which was supplied
	 * 
	 * @return
	 */
	public abstract FlexoResource<M> retrieveModelResource(File aModelFile, FlexoResource<MM> metaModelResource,
			TechnologyContextManager<M, MM> technologyContextManager);

	/**
	 * Creates new model conform to the supplied meta model in a FlexoResourceCenter.
	 * 
	 * @param resourceCenter
	 * @param relativePath
	 * @param filename
	 * @param modelUri
	 * @param metaModelResource
	 * @param technologyContextManager
	 * @return
	 */
	public abstract FlexoResource<M> createEmptyModel(FileSystemBasedResourceCenter resourceCenter, String relativePath, String filename,
			String modelUri, FlexoResource<MM> metaModelResource, TechnologyContextManager<M, MM> technologyContextManager);

	/**
	 * Creates new model conform to the supplied meta model in a FlexoProject.
	 * 
	 * @param project
	 * @param filename
	 * @param modelUri
	 * @param metaModelResource
	 * @param technologyContextManager
	 * @return
	 */
	public abstract FlexoResource<M> createEmptyModel(FlexoProject project, String filename, String modelUri,
			FlexoResource<MM> metaModelResource, TechnologyContextManager<M, MM> technologyContextManager);

	/**
	 * Create a model repository for current {@link TechnologyAdapter} and supplied {@link FlexoResourceCenter}
	 * 
	 * @param resourceCenter
	 * @return
	 */
	public abstract <R extends FlexoResource<? extends M>> ModelRepository<R, M, MM, ? extends TechnologyAdapter<M, MM>> createModelRepository(
			FlexoResourceCenter resourceCenter);

	/**
	 * Create a metamodel repository for current {@link TechnologyAdapter} and supplied {@link FlexoResourceCenter}
	 * 
	 * @param resourceCenter
	 * @return
	 */
	public abstract <R extends FlexoResource<? extends MM>> MetaModelRepository<R, M, MM, ? extends TechnologyAdapter<M, MM>> createMetaModelRepository(
			FlexoResourceCenter resourceCenter);

	/**
	 * Returns applicable {@link TechnologyAdapterService}
	 * 
	 * @return
	 */
	public TechnologyAdapterService getTechnologyAdapterService() {
		return technologyAdapterService;
	}

	/**
	 * Sets applicable {@link TechnologyAdapterService}
	 * 
	 * @param technologyAdapterService
	 */
	public void setTechnologyAdapterService(TechnologyAdapterService technologyAdapterService) {
		this.technologyAdapterService = technologyAdapterService;
	}

	/**
	 * Creates and return the {@link TechnologyContextManager} for this technology and for all {@link FlexoResourceCenter} declared in the
	 * scope of {@link FlexoResourceCenterService}
	 * 
	 * @return
	 */
	public abstract TechnologyContextManager<M, MM> createTechnologyContextManager(FlexoResourceCenterService service);

	/**
	 * Return the {@link TechnologyContextManager} for this technology shared by all {@link FlexoResourceCenter} declared in the scope of
	 * {@link FlexoResourceCenterService}
	 * 
	 * @return
	 */
	public TechnologyContextManager<M, MM> getTechnologyContextManager() {
		return (TechnologyContextManager<M, MM>) getTechnologyAdapterService().getTechnologyContextManager(this);
	}

	/**
	 * Return the technology-specific binding factory
	 * 
	 * @return
	 */
	public abstract TechnologyAdapterBindingFactory getTechnologyAdapterBindingFactory();

	/**
	 * Provides a hook to finalize initialization of a TechnologyAdapter.<br>
	 * This method is called:
	 * <ul>
	 * <li>after all TechnologyAdapter have been loaded</li>
	 * <li>after all {@link FlexoResourceCenter} have been initialized</li>
	 * </ul>
	 */
	public void initialize() {
	}

	/**
	 * Provides a hook to detect when a new resource center was added or discovered
	 * 
	 * @param newResourceCenter
	 */
	public void resourceCenterAdded(FlexoResourceCenter newResourceCenter) {
	}

	/**
	 * Provides a hook to detect when a new resource center was removed
	 * 
	 * @param newResourceCenter
	 */
	public void resourceCenterRemoved(FlexoResourceCenter removedResourceCenter) {
	}

	/**
	 * Return model resource identified by its uri. Lookup is performed on all known resource centers.
	 * 
	 * @param modelURI
	 * @return
	 */
	public FlexoModelResource<M, MM> getModelResource(String modelURI) {
		for (ModelRepository<?, ?, ?, ?> modelRepository : getTechnologyAdapterService().getAllModelRepositories(this)) {
			FlexoResource<?> r = modelRepository.getResource(modelURI);
			if (r != null) {
				return (FlexoModelResource<M, MM>) r;
			}
		}
		return null;
	}

	/**
	 * Return model resource identified by its uri. Lookup is performed on all known resource centers.
	 * 
	 * @param modelURI
	 * @return
	 */
	public FlexoMetaModelResource<M, MM> getMetaModelResource(String modelURI) {
		for (MetaModelRepository<?, ?, ?, ?> mmRepository : getTechnologyAdapterService().getAllMetaModelRepositories(this)) {
			FlexoResource<?> r = mmRepository.getResource(modelURI);
			if (r != null) {
				return (FlexoMetaModelResource<M, MM>) r;
			}
		}
		return null;
	}

	public abstract String getExpectedMetaModelExtension();

	public abstract String getExpectedModelExtension(FlexoResource<MM> metaModel);
}
