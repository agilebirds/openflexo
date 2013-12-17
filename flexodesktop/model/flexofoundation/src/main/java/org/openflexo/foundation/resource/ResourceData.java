package org.openflexo.foundation.resource;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;

/**
 * Represents the data a {@link FlexoResource} give access to
 * 
 * @author sylvain
 * 
 * @param <RD>
 */
@ModelEntity
public interface ResourceData<RD extends ResourceData<RD>> /*extends AccessibleProxyObject, DeletableProxyObject, KeyValueCoding,
CloneableProxyObject*/{

	public static final String FLEXO_RESOURCE = "flexoResource";

	@Getter(value = FLEXO_RESOURCE, ignoreType = true)
	public FlexoResource<RD> getResource();

	@Setter(FLEXO_RESOURCE)
	public void setResource(FlexoResource<RD> resource);

	/**
	 * Return flag indicating if this resource data is modified
	 * 
	 * @return
	 */
	public boolean isModified();

	/**
	 * Sets "modify" status of this resource data to be true
	 */
	public void setIsModified();

	/**
	 * Sets "modify" status of this resource data to be false
	 */
	public void clearIsModified();

	/**
	 * Sets "modify" status of this resource data to be false<br>
	 * Also resets last memory update if required (default the passed value should be false)
	 */
	public void clearIsModified(boolean clearLastMemoryUpdate);

	/**
	 * Delete object
	 */
	public boolean delete();

}
