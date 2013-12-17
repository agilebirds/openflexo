package org.openflexo.foundation.resource;


/**
 * A listener of resource loading status
 * 
 * @author sylvain
 * 
 */
public interface ResourceLoadingListener {

	public void notifyResourceWillBeLoaded(FlexoResource<?> resource);

	public void notifyResourceHasBeenLoaded(FlexoResource<?> resource);

}
