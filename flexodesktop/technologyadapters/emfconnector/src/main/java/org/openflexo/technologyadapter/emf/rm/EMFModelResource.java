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
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.technologyadapter.emf.model.EMFModel;

public class EMFModelResource extends FlexoStorageResource<EMFModel> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(EMFModelResource.class
			.getPackage().getName());

	public EMFModelResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public EMFModelResource(FlexoProject aProject) {
		super(aProject);
	}

	public EMFModelResource(FlexoProject project, EMFModel newEMFModel, FlexoProjectFile ontologyFile)
			throws InvalidFileNameException, DuplicateResourceException {
		super(project);
		_resourceData = newEMFModel;
		newEMFModel.setFlexoResource(this);
		this.setResourceFile(ontologyFile);
	}

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

	private void writeToFile() throws SaveResourceException {

		// Here comes the code to write EMF model on disk

		// logger.info("Wrote " + getFile());
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.EMF;
	}

	@Override
	public String getName() {
		return getProject().getProjectName();
	}

	@Override
	protected EMFModel performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler) throws LoadResourceException,
			FileNotFoundException, ProjectLoadingCancelledException {

		// Here comes the code to read EMF model from disk

		return null;
	}

	@Override
	public Class<EMFModel> getResourceDataClass() {
		return EMFModel.class;
	}
}
