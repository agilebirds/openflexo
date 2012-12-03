package org.openflexo.technologyadapter.emf.rm;

import org.openflexo.foundation.resource.FlexoResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.FlexoResourceTree;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.toolbox.IProgress;

public abstract class EMFMetaModelResourceImpl extends FlexoResourceImpl<EMFMetaModel> implements EMFMetaModelResource {

	/**
	 * Load the &quot;real&quot; load resource data of this resource.
	 * 
	 * @param progress
	 *            a progress monitor in case the resource data is not immediately available.
	 * @return the resource data.
	 * @throws ResourceLoadingCancelledException
	 */
	@Override
	public EMFMetaModel loadResourceData(IProgress progress) throws ResourceLoadingCancelledException {
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
	 * 
	 * @return
	 */
	@Override
	public FlexoResourceTree update() {
		return null;
	}

}
