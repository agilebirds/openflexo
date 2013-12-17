package org.openflexo.foundation.view.rm;

import org.openflexo.foundation.resource.FlexoProjectResource;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoXMLFileResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.viewpoint.VirtualModelTechnologyAdapter;
import org.openflexo.foundation.viewpoint.rm.VirtualModelResource;
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
public interface VirtualModelInstanceResource extends FlexoXMLFileResource<VirtualModelInstance>,
		FlexoProjectResource<VirtualModelInstance>, TechnologyAdapterResource<VirtualModelInstance, VirtualModelTechnologyAdapter> {

	public static final String VIRTUAL_MODEL_SUFFIX = ".vmxml";

	public static final String VIRTUAL_MODEL_RESOURCE = "virtualModelResource";

	@Getter(value = VIRTUAL_MODEL_RESOURCE, ignoreType = true)
	public VirtualModelResource getVirtualModelResource();

	@Setter(VIRTUAL_MODEL_RESOURCE)
	public void setVirtualModelResource(VirtualModelResource virtualModelResource);

	public VirtualModelInstance getVirtualModelInstance();

	@Getter(value = CONTAINER, inverse = CONTENTS)
	@Override
	public ViewResource getContainer();

}
