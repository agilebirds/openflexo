package org.openflexo.foundation.technologyadapter;

import org.openflexo.foundation.rm.StorageResourceData;

public interface FlexoModel<MM extends FlexoMetaModel> extends StorageResourceData {

	public MM getMetaModel();
}
