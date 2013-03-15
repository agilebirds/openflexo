package org.openflexo.technologyadapter.xsd.rm;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.FlexoResourceTree;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModel;
import org.openflexo.toolbox.IProgress;

public abstract class XSDMetaModelResourceImpl extends FlexoFileResourceImpl<XSDMetaModel> implements XSDMetaModelResource {

	private static final Logger logger = Logger.getLogger(XSDMetaModelResourceImpl.class.getPackage().getName());

	@Override
	public XSDMetaModel getMetaModelData() {
		try {
			return getResourceData(null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
		} catch (ResourceDependencyLoopException e) {
			e.printStackTrace();
		} catch (FlexoException e) {
			e.printStackTrace();
		}
		return null;
	}

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
		XSDMetaModel returned = new XSDMetaModel(getURI(), getFile(), (XSDTechnologyAdapter) getTechnologyAdapter());
		returned.loadWhenUnloaded();
		return returned;
	}

	/**
	 * Save the &quot;real&quot; resource data of this resource.
	 */
	@Override
	public void save(IProgress progress) {
		logger.info("Not implemented yet");
	}

	/**
	 * This method updates the resource.
	 */
	@Override
	public FlexoResourceTree update() {
		return null;
	}

	@Override
	public Class<XSDMetaModel> getResourceDataClass() {
		return XSDMetaModel.class;
	}

}
