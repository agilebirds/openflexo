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

/**
 * Resource Flexo matching an EMF Model.
 * 
 * @author gbesancon
 */
public class FlexoEMFModelResource extends FlexoStorageResource<EMFModel> {
	/** Logger. */
	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(FlexoEMFModelResource.class
			.getPackage().getName());

	/**
	 * 
	 * Constructor.
	 * 
	 * @param builder
	 */
	public FlexoEMFModelResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	/**
	 * 
	 * Constructor.
	 * 
	 * @param aProject
	 */
	public FlexoEMFModelResource(FlexoProject aProject) {
		super(aProject);
	}

	/**
	 * 
	 * Constructor.
	 * 
	 * @param project
	 * @param newEMFModel
	 * @param ontologyFile
	 * @throws InvalidFileNameException
	 * @throws DuplicateResourceException
	 */
	public FlexoEMFModelResource(FlexoProject project, EMFModel newEMFModel, FlexoProjectFile ontologyFile)
			throws InvalidFileNameException, DuplicateResourceException {
		super(project);
		_resourceData = newEMFModel;
		newEMFModel.setFlexoResource(this);
		this.setResourceFile(ontologyFile);
	}

	/**
	 * 
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

	private void writeToFile() throws SaveResourceException {

		// Here comes the code to write EMF model on disk

		// logger.info("Wrote " + getFile());
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#getResourceType()
	 */
	@Override
	public ResourceType getResourceType() {
		return ResourceType.EMF;
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#getName()
	 */
	@Override
	public String getName() {
		return getProject().getProjectName();
	}

	/**
	 * 
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
}
