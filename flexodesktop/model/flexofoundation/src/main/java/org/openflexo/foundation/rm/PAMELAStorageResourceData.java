package org.openflexo.foundation.rm;

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
		@Override
		public FlexoProject getProject() {
			return getFlexoResource().getProject();
		}

		@Override
		public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {
			setFlexoStorageResource((FlexoStorageResource) resource);
		}

		@Override
		public void setModified(boolean modified) {
			boolean old = isModified();
			performSuperSetModified(modified);
			if (modified && !old) {
				getFlexoResource().notifyResourceStatusChanged();
			}
		}
	}

}