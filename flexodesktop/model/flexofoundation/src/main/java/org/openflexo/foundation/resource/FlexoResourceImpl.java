package org.openflexo.foundation.resource;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.toolbox.IProgress;

/**
 * Default implementation for {@link FlexoResource}<br>
 * Note that this default implementation extends {@link FlexoObject}: all resources in Openflexo model are instances of {@link FlexoObject}
 * 
 * Very first draft for implementation, only implements get/load scheme
 * 
 * @param <RD>
 *            the type of the resource data reference by this resource
 * @author Sylvain
 * 
 */
public abstract class FlexoResourceImpl<RD extends ResourceData<RD>> extends FlexoObject implements FlexoResource<RD> {

	static final Logger logger = Logger.getLogger(FlexoResourceImpl.class.getPackage().getName());

	protected RD resourceData = null;

	/**
	 * Return flag indicating if this resource is loaded
	 * 
	 * @return
	 */
	@Override
	public boolean isLoaded() {
		return resourceData != null;
	}

	/**
	 * Returns the &quot;real&quot; resource data of this resource. This may cause the loading of the resource data.
	 * 
	 * @param progress
	 *            a progress monitor in case the resource data is not immediately available.
	 * @return the resource data.
	 * @throws ResourceLoadingCancelledException
	 */
	@Override
	public RD getResourceData(IProgress progress) throws ResourceLoadingCancelledException, ResourceLoadingCancelledException,
			ResourceDependencyLoopException, FileNotFoundException, FlexoException {
		if (resourceData == null) {
			// The resourceData is null, we try to load it
			resourceData = loadResourceData(progress);
			// That's fine, resource is loaded, now let's notify the loading of the resources
			notifyResourceLoaded();
		}
		return resourceData;
	}

	/**
	 * Sets {@link ResourceData} for this resource
	 * 
	 * @param resourceData
	 */
	@Override
	public void setResourceData(RD resourceData) {
		this.resourceData = resourceData;
	}

	/**
	 * Called to notify that a resource has successfully been loaded
	 */
	public void notifyResourceLoaded() {
		logger.info("***************** notify resource loaded !!!!!!!!!!!!!!!!!");
		setChanged();
		notifyObservers(new ResourceLoaded(this, resourceData));
		// Also notify that the contents of the resource may also have changed
		setChanged();
		notifyObservers(new DataModification("contents", null, getContents()));
	}

	/**
	 * Called to notify that a resource has successfully been saved
	 */
	public void notifyResourceSaved() {
		setChanged();
		notifyObservers(new ResourceSaved(this, resourceData));
	}

	public void notifyResourceStatusChanged() {
	}

	@Override
	public final String toString() {
		return getClass().getSimpleName() + "." + getURI() + "." + getVersion() + "." + getRevision();
	}
}
