package org.openflexo.foundation.rm;

import org.openflexo.foundation.resource.FlexoProjectResource;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoXMLFileResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

/**
 * This is the {@link FlexoResource} encoding a {@link VirtualModelInstance}
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(VirtualModelInstanceResourceImpl.class)
@XMLElement
public interface VirtualModelInstanceResource<VMI extends VirtualModelInstance<VMI, ?>> extends FlexoXMLFileResource<VMI>,
		FlexoProjectResource<VMI>, TechnologyAdapterResource<VMI> {

	public static final String VIRTUAL_MODEL_SUFFIX = ".vmxml";

	public static final String VIRTUAL_MODEL_RESOURCE = "virtualModelResource";

	@Getter(value = VIRTUAL_MODEL_RESOURCE, ignoreType = true)
	public VirtualModelResource<?> getVirtualModelResource();

	@Setter(VIRTUAL_MODEL_RESOURCE)
	public void setVirtualModelResource(VirtualModelResource<?> virtualModelResource);

	public VMI getVirtualModelInstance();

	@Override
	public ViewResource getContainer();

}
