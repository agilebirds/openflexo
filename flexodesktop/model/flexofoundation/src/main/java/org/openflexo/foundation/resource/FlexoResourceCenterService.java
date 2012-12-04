package org.openflexo.foundation.resource;

import java.util.List;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;

@ModelEntity
public interface FlexoResourceCenterService {
	public static final String RESOURCE_CENTERS = "resourceCenters";

	@Getter(value = RESOURCE_CENTERS, cardinality = Cardinality.LIST)
	public List<FlexoResourceCenter> getResourceCenters();

	@Setter(RESOURCE_CENTERS)
	public void setResourceCenters(List<FlexoResourceCenter> resourceCenters);

	@Adder(RESOURCE_CENTERS)
	public void addToResourceCenters(FlexoResourceCenter resourceCenter);

	@Remover(RESOURCE_CENTERS)
	public void removeFromResourceCenters(FlexoResourceCenter resourceCenter);

	public FlexoResourceCenter getUserResourceCenter();

	public FlexoResourceCenter getOpenFlexoResourceCenter();

	// public void registerTechnologyAdapterService(TechnologyAdapterService technologyAdapterService);
}
