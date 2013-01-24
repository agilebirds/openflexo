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
package org.openflexo.technologyadapter.emf.rm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.LoadResourceException;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.SaveResourcePermissionDeniedException;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.io.EMFModelConverter;

/**
 * EMF Model Resource.
 * 
 * @author gbesancon
 */
public class EMFModelResource extends FlexoStorageResource<EMFModel> implements FlexoModelResource<EMFModel, EMFMetaModel> {

	/** Logger. */
	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(EMFModelResource.class.getPackage()
			.getName());

	/** Technological Adapter. */
	protected EMFTechnologyAdapter adapter;

	/** MetaModel Resource. */
	protected final EMFMetaModelResource metaModelResource;

	/** Model File. */
	protected final File modelFile;

	/** Model Resource. */
	protected Resource modelResource;

	/**
	 * Constructor.
	 * 
	 * @param builder
	 */
	public EMFModelResource(FlexoProjectBuilder builder) {
		this(builder != null ? builder.project : null, builder != null ? builder.serviceManager : null);
		if (builder != null) {
			builder.notifyResourceLoading(this);
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param aProject
	 */
	public EMFModelResource(FlexoProject aProject, FlexoServiceManager serviceManager) {
		super(aProject, serviceManager);
		metaModelResource = null;
		adapter = null;
		modelFile = null;
	}

	/**
	 * Constructor.
	 * 
	 * @param project
	 * @param newEMFModel
	 * @param file
	 * @throws InvalidFileNameException
	 * @throws DuplicateResourceException
	 */
	public EMFModelResource(File file, EMFMetaModelResource metaModelResource, EMFTechnologyAdapter adapter, String uri)
			throws InvalidFileNameException, DuplicateResourceException {
		super((FlexoProject) null, adapter.getTechnologyAdapterService().getFlexoServiceManager());
		this.modelFile = file;
		this.metaModelResource = metaModelResource;
		this.adapter = adapter;
		setURI(uri);
	}

	/**
	 * Getter of EMF Model Resource.
	 * 
	 * @return the modelResource value
	 */
	public Resource getEMFResource() {
		return modelResource;
	}

	/**
	 * Setter of EMF Model Resource.
	 * 
	 * @param modelResource
	 *            the modelResource to set
	 */
	public void setEMFResource(Resource modelResource) {
		this.modelResource = modelResource;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.rm.FlexoStorageResource#saveResourceData(boolean)
	 */
	@Override
	protected void saveResourceData(boolean clearIsModified) throws SaveResourceException {
		if (!hasWritePermission()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Permission denied : " + getFile().getAbsolutePath());
			}
			throw new SaveResourcePermissionDeniedException(this);
		}
		if (_resourceData != null) {
			FileWritingLock lock = willWriteOnDisk();
			writeToFile();
			hasWrittenOnDisk(lock);
			notifyResourceStatusChanged();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Succeeding to save Resource " + getResourceIdentifier() + " : " + getFile().getName());
			}
		}
		if (clearIsModified) {
			getResourceData().clearIsModified(false);
		}
	}

	/**
	 * Write file.
	 * 
	 * @throws SaveResourceException
	 */
	private void writeToFile() throws SaveResourceException {
		try {
			modelResource.save(null);
			logger.info("Wrote " + getFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#getResourceType()
	 */
	@Override
	public ResourceType getResourceType() {
		return ResourceType.EMF;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#getName()
	 */
	@Override
	public String getName() {
		return modelFile.getAbsolutePath();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.rm.FlexoFileResource#getFile()
	 */
	@Override
	public File getFile() {
		return modelFile;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.rm.FlexoStorageResource#performLoadResourceData(org.openflexo.foundation.utils.FlexoProgress,
	 *      org.openflexo.foundation.utils.ProjectLoadingHandler)
	 */
	@Override
	protected EMFModel performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler) throws LoadResourceException,
			FileNotFoundException, ProjectLoadingCancelledException {
		try {
			if (modelResource == null) {
				modelResource = metaModelResource.getResourceFactory().createResource(
						org.eclipse.emf.common.util.URI.createFileURI(modelFile.getAbsolutePath()));
			}
			modelResource.load(null);
			EMFModelConverter converter = new EMFModelConverter();
			_resourceData = converter.convertModel(getMetaModel(), modelResource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return _resourceData;
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.resource.FlexoResource#getResourceDataClass()
	 */
	@Override
	public Class<EMFModel> getResourceDataClass() {
		return EMFModel.class;
	}

	@Override
	public TechnologyAdapter<?, ?> getTechnologyAdapter() {
		return adapter;
	}

	@Override
	public void setTechnologyAdapter(TechnologyAdapter<?, ?> technologyAdapter) {
		if (technologyAdapter instanceof EMFTechnologyAdapter) {
			adapter = (EMFTechnologyAdapter) technologyAdapter;
		}
	}

	@Override
	public EMFMetaModel getMetaModel() {
		return metaModelResource.getMetaModelData();
	}

	@Override
	public void setMetaModel(EMFMetaModel aMetaModel) {
	}

	@Override
	public EMFModel getModelData() {
		return _resourceData;
	}

}
