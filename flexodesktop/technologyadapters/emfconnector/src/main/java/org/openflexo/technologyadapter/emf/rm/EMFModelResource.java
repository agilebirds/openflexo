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

import java.io.FileNotFoundException;
import java.util.logging.Level;

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
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;

/**
 * EMF Model Resource.
 * 
 * @author gbesancon
 */
public class EMFModelResource extends FlexoStorageResource<EMFModel> implements FlexoModelResource<EMFModel, EMFMetaModel> {

	/** Logger. */
	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(EMFModelResource.class.getPackage()
			.getName());

	/**
	 * Constructor.
	 * 
	 * @param builder
	 */
	public EMFModelResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	/**
	 * Constructor.
	 * 
	 * @param aProject
	 */
	public EMFModelResource(FlexoProject aProject) {
		super(aProject);
	}

	/**
	 * Constructor.
	 * 
	 * @param project
	 * @param newEMFModel
	 * @param ontologyFile
	 * @throws InvalidFileNameException
	 * @throws DuplicateResourceException
	 */
	public EMFModelResource(FlexoProject project, EMFModel newEMFModel, FlexoProjectFile ontologyFile) throws InvalidFileNameException,
			DuplicateResourceException {
		super(project);
		_resourceData = newEMFModel;
		newEMFModel.setFlexoResource(this);
		this.setResourceFile(ontologyFile);
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

		// Here comes the code to write EMF model on disk

		// logger.info("Wrote " + getFile());
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
		return getProject().getProjectName();
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

		// Here comes the code to read EMF model from disk

		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTechnologyAdapter(TechnologyAdapter<?, ?> technologyAdapter) {
		// TODO Auto-generated method stub
	}

	@Override
	public EMFMetaModel getMetaModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMetaModel(EMFMetaModel aMetaModel) {
		// TODO Auto-generated method stub

	}

	@Override
	public EMFModel getModelData() {
		// TODO Auto-generated method stub
		return null;
	}
}
