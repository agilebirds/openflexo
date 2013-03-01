package org.openflexo.foundation.resource;

import java.util.List;

import org.openflexo.foundation.FlexoService;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.factory.AccessibleProxyObject;

/**
 * This service implements access policy to resource centers in the context of a {@link FlexoServiceManager} (which, in interactive mode, is
 * an ApplicationContext)
 * 
 * One {@link UserResourceCenter} is declared to be the user resource center
 * 
 * @author sylvain
 * 
 */
@ModelEntity
public interface FlexoResourceCenterService extends FlexoService, AccessibleProxyObject {
	public static final String RESOURCE_CENTERS = "resourceCenters";

	@Getter(value = RESOURCE_CENTERS, cardinality = Cardinality.LIST)
	public List<FlexoResourceCenter> getResourceCenters();

	@Setter(RESOURCE_CENTERS)
	public void setResourceCenters(List<FlexoResourceCenter> resourceCenters);

	@Adder(RESOURCE_CENTERS)
	public void addToResourceCenters(FlexoResourceCenter resourceCenter);

	@Remover(RESOURCE_CENTERS)
	public void removeFromResourceCenters(FlexoResourceCenter resourceCenter);

	public UserResourceCenter getUserResourceCenter();

}
