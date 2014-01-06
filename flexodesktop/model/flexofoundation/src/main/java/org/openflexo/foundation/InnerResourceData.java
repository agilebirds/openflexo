package org.openflexo.foundation;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceData;

/**
 * This interface is implemented by all objects beeing part of a container object declared as a {@link ResourceData}
 * 
 * Thus, all objects implementing this interface are guaranteed to access a {@link FlexoResource}
 * 
 * @author sylvain
 * 
 */
public interface InnerResourceData {

	/**
	 * Return the {@link ResourceData} where this object is defined (the global functional root object giving access to the
	 * {@link FlexoResource})
	 * 
	 * @return
	 */
	public abstract ResourceData<?> getResourceData();

}
