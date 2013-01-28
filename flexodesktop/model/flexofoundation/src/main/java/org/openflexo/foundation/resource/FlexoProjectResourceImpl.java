package org.openflexo.foundation.resource;


/**
 * Default implementation for {@link FlexoProjectResource}
 * 
 * 
 * @param <RD>
 *            the type of the resource data reference by this resource
 * @author Sylvain
 * 
 */
public abstract class FlexoProjectResourceImpl<RD extends ResourceData<RD>> extends FlexoResourceImpl<RD> implements
		FlexoProjectResource<RD> {

}
