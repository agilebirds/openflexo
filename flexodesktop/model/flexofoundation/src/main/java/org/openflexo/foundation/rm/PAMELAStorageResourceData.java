package org.openflexo.foundation.rm;

import java.util.Date;
import java.util.logging.Level;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.ReturnedValue;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.factory.AccessibleProxyObject;

@ModelEntity
public interface PAMELAStorageResourceData extends StorageResourceData, AccessibleProxyObject {

	public static final String FLEXO_RESOURCE = "flexoResource";
	public static final String PROJECT = "project";

	@Override
	@Getter(value = FLEXO_RESOURCE, ignoreType = true)
	public FlexoStorageResource getFlexoResource();

	@Setter(FLEXO_RESOURCE)
	public void setFlexoStorageResource(FlexoStorageResource resource) throws DuplicateResourceException;

	@Override
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException;

	@Override
	@Getter(value = PROJECT, ignoreType = true)
	@ReturnedValue(FLEXO_RESOURCE + ".project")
	public FlexoProject getProject();

	public static abstract class PAMELAStorageResourceDataImpl implements PAMELAStorageResourceData {

		private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger
				.getLogger(PAMELAStorageResourceDataImpl.class.getPackage().getName());
		private Date lastMemoryUpdate;

		@Override
		public FlexoProject getProject() {
			return getFlexoResource().getProject();
		}

		@Override
		public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {
			setFlexoStorageResource((FlexoStorageResource) resource);
		}

		@Override
		public Date lastMemoryUpdate() {
			return lastMemoryUpdate;
		}

		@Override
		public void setModified(boolean modified) {
			boolean old = isModified();
			performSuperSetModified(modified);
			lastMemoryUpdate = new Date();
			if (modified && !old && !isDeserializing()) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info(">>>>>>>> PAMELA resource " + getFlexoResource() + " has been modified");
				}
				if (getFlexoResource() != null) {
					getFlexoResource().notifyResourceStatusChanged();
				}
			}
		}
	}

}