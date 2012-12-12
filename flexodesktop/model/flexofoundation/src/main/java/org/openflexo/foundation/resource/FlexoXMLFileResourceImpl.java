package org.openflexo.foundation.resource;

/**
 * Default implementation for {@link FlexoFileResource}
 * 
 * Very first draft for implementation, only implements get/load scheme
 * 
 * @param <RD>
 *            the type of the resource data reference by this resource
 * @author Sylvain
 * 
 */
public abstract class FlexoXMLFileResourceImpl<RD extends ResourceData<RD>> extends FlexoFileResourceImpl<RD> implements
		FlexoXMLFileResource<RD> {

}
