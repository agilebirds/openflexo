package org.openflexo.foundation.resource;

/**
 * Represents the data a {@link FlexoResource} give access to
 * 
 * @author sylvain
 * 
 * @param <RD>
 */
public interface ResourceData<RD extends ResourceData<RD>> {

	public FlexoResource<RD> getResource();

	public void setResource(FlexoResource<RD> resource);

	public boolean isModified();

	public void setIsModified();

	public void clearIsModified();

	/**
	 * Sets the resource to be not modified anymore. Also resets last memory update if required (default the passed value should be false)
	 */
	public void clearIsModified(boolean clearLastMemoryUpdate);

	/**
	 * Delete object
	 */
	public boolean delete();
}
