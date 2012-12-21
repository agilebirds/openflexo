package org.openflexo.foundation.resource;

public interface ResourceData<RD extends ResourceData<RD>> {

	public FlexoResource<RD> getResource();

	public void setResource(FlexoResource<RD> resource);

	public void setIsModified();

	public void clearIsModified();

	/**
	 * Sets the resource to be not modified anymore. Also resets last memory update if required (default the passed value should be false)
	 */
	public void clearIsModified(boolean clearLastMemoryUpdate);
}
