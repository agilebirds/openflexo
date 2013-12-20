package org.openflexo.foundation.resource;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.FlexoServiceManager;
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
public abstract class FlexoResourceImpl<RD extends ResourceData<RD>> extends FlexoObjectImpl implements FlexoResource<RD> {

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
	 * Return flag indicating if this resource is loadable<br>
	 * By default, a resource is always loadable, then this method always returns true
	 * 
	 * @return
	 */
	@Override
	public boolean isLoadable() {
		// By default, a resource is always loadable, then this method always returns true
		return true;
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
			FileNotFoundException, FlexoException {
		if (resourceData == null && isLoadable()) {
			// The resourceData is null, we try to load it
			resourceData = loadResourceData(progress);
			// That's fine, resource is loaded, now let's notify the loading of the resources
			notifyResourceLoaded();
		}
		return resourceData;
	}

	/**
	 * Programmatically sets {@link ResourceData} for this resource<br>
	 * The resource is then notified that it has been loaded
	 * 
	 * @param resourceData
	 */
	@Override
	public void setResourceData(RD resourceData) {
		this.resourceData = resourceData;
		notifyResourceLoaded();
	}

	/**
	 * Called to notify that a resource has successfully been loaded
	 */
	@Override
	public void notifyResourceLoaded() {
		logger.info("notifyResourceLoaded(), resource=" + this);

		ResourceLoaded notification = new ResourceLoaded(this, resourceData);
		setChanged();
		notifyObservers(notification);
		// Also notify that the contents of the resource may also have changed
		setChanged();
		notifyObservers(new DataModification("contents", null, getContents()));
		if (getServiceManager() != null) {
			getServiceManager().notify(getServiceManager().getResourceManager(), notification);
		} else {
			logger.warning("Resource " + this + " does not refer to any ServiceManager. Please investigate...");
		}
	}

	/**
	 * Called to notify that a resource has successfully been saved
	 */
	@Override
	public void notifyResourceSaved() {
		logger.info("notifyResourceSaved(), resource=" + this);

		ResourceSaved notification = new ResourceSaved(this, resourceData);
		setChanged();
		notifyObservers(notification);
		getServiceManager().notify(getServiceManager().getResourceManager(), notification);
	}

	/**
	 * Called to notify that a resource has been added to contents<br>
	 * TODO: integrate this in setContents() when this interface will extends {@link AccessibleProxyObject}
	 * 
	 * @param resource
	 *            : resource being added
	 */
	@Override
	public void notifyContentsAdded(FlexoResource<?> resource) {
		logger.info("notifyContentsAdded(), resource=" + this);

		ContentsAdded notification = new ContentsAdded(this, resource);
		setChanged();
		notifyObservers(notification);
		getServiceManager().notify(getServiceManager().getResourceManager(), notification);
	}

	/**
	 * Called to notify that a resource has been remove to contents<br>
	 * TODO: integrate this in setContents() when this interface will extends {@link AccessibleProxyObject}
	 * 
	 * @param resource
	 *            : resource being removed
	 */
	@Override
	public void notifyContentsRemoved(FlexoResource<?> resource) {
		logger.info("notifyContentsRemoved(), resource=" + this);

		ContentsRemoved notification = new ContentsRemoved(this, resource);
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

	/**
	 * Sets and register the service manager<br>
	 * Also (VERY IMPORTANT) register the resource in the ResourceManager !!!
	 */
	@Override
	public void setServiceManager(FlexoServiceManager serviceManager) {
		this.serviceManager = serviceManager;
		getServiceManager().getResourceManager().registerResource(this);
	}

	/**
	 * Indicates whether this resource can be edited or not. Returns <code>true</code> if the resource cannot be edited, else returns
	 * <code>false</code>.<br>
	 * This is here the default implementation, always returned false;
	 * 
	 * @return <code>true</code> if the resource cannot be edited, else returns <code>false</code>.
	 */
	@Override
	public boolean isReadOnly() {
		return false;
	}

	/**
	 * Delete this resource<br>
	 * Contents of this resource are deleted, and resource data is unloaded
	 */
	@Override
	public boolean delete() {
		if (isReadOnly()) {
			logger.warning("Delete requested for READ-ONLY resource " + this);
			return false;
		} else {
			logger.info("Deleting resource " + this);
			if (getContainer() != null) {
				FlexoResource<?> container = getContainer();
				container.removeFromContents(this);
				container.notifyContentsRemoved(this);
			}
			for (org.openflexo.foundation.resource.FlexoResource<?> r : new ArrayList<org.openflexo.foundation.resource.FlexoResource<?>>(
					getContents())) {
				r.delete();
			}
			super.delete();
			if (isLoaded()) {
				unloadResourceData();
			}
			return true;
		}
	}

	/**
	 * Delete (dereference) resource data if resource data is loaded<br>
	 * Also delete the resource data
	 */
	@Override
	public void unloadResourceData() {
		if (isLoaded()) {
			resourceData.delete();
			resourceData = null;
		}
	}

}
