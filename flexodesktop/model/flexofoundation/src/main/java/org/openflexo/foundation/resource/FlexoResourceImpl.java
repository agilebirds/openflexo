package org.openflexo.foundation.resource;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.model.factory.AccessibleProxyObject;
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

	private FlexoServiceManager serviceManager = null;
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
		logger.info("notifyResourceLoaded(), resource=" + this);

		ResourceLoaded notification = new ResourceLoaded(this, resourceData);
		setChanged();
		notifyObservers(notification);
		// Also notify that the contents of the resource may also have changed
		setChanged();
		notifyObservers(new DataModification("contents", null, getContents()));
		getServiceManager().notify(getServiceManager().getResourceManager(), notification);
	}

	/**
	 * Called to notify that a resource has successfully been saved
	 */
	public void notifyResourceSaved() {
		logger.info("notifyResourceSaved(), resource=" + this);

		ResourceSaved notification = new ResourceSaved(this, resourceData);
		setChanged();
		notifyObservers(notification);
		getServiceManager().notify(getServiceManager().getResourceManager(), notification);
	}

	/**
	 * Called to notify that a resource has been added to contents TODO: integrate this in setContents() when this interface will extends
	 * {@link AccessibleProxyObject}
	 */
	public void notifyContentsAdded(FlexoResource<?> resource) {
		logger.info("notifyResourceLoaded(), resource=" + this);

		ContentsAdded notification = new ContentsAdded(this, resource);
		setChanged();
		notifyObservers(notification);
		getServiceManager().notify(getServiceManager().getResourceManager(), notification);
	}

	public void notifyResourceStatusChanged() {
	}

	@Override
	public final String toString() {
		return getClass().getSimpleName() + "." + getURI() + "." + getVersion() + "." + getRevision();
	}

	/**
	 * Returns a list of resources of supplied type contained by this resource.
	 * 
	 * @return the list of contained resources.
	 */
	@Override
	public <R extends FlexoResource<?>> List<R> getContents(Class<R> resourceClass) {
		ArrayList<R> returned = new ArrayList<R>();
		for (FlexoResource<?> r : getContents()) {
			if (resourceClass.isAssignableFrom(r.getClass())) {
				returned.add((R) r);
			}
		}
		return returned;
	}

	@Override
	public FlexoServiceManager getServiceManager() {
		return serviceManager;
	}

	@Override
	public void setServiceManager(FlexoServiceManager serviceManager) {
		this.serviceManager = serviceManager;
		getServiceManager().getResourceManager().registerResource(this);
	}
}
