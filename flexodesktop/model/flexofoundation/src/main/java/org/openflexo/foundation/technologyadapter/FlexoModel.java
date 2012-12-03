package org.openflexo.foundation.technologyadapter;

import org.openflexo.foundation.rm.StorageResourceData;

/**
 * Flexo Model.
 * 
 * @author squerin
 * 
 * @param <MM>
 */
public interface FlexoModel<MM extends FlexoMetaModel> extends StorageResourceData {

	/**
	 * Meta Model of the Model.
	 * 
	 * @return
	 */
	public MM getMetaModel();
}
