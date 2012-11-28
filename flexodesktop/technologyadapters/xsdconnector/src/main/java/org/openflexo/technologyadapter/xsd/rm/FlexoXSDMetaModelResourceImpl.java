package org.openflexo.technologyadapter.xsd.rm;

import org.openflexo.foundation.resource.FlexoResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.FlexoResourceTree;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModel;
import org.openflexo.toolbox.IProgress;

public abstract class FlexoXSDMetaModelResourceImpl /*extends FlexoStorageResource<XMLModel>*/extends FlexoResourceImpl<XSDMetaModel>
		implements FlexoXSDMetaModelResource {

	/**
	 * Load the &quot;real&quot; load resource data of this resource.
	 * 
	 * @param progress
	 *            a progress monitor in case the resource data is not immediately available.
	 * @return the resource data.
	 * @throws ResourceLoadingCancelledException
	 */
	@Override
	public XSDMetaModel loadResourceData(IProgress progress) throws ResourceLoadingCancelledException {
		return null;
	}

	/**
	 * Save the &quot;real&quot; resource data of this resource.
	 */
	@Override
	public void save(IProgress progress) {

	}

	/**
	 * This method updates the resource.
	 */
	@Override
	public FlexoResourceTree update() {
		return null;
	}

}
