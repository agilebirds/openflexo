package org.openflexo.foundation.technologyadapter;

import java.util.List;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;

/**
 * This service provides access to all technology adapters available in a given environment.
 * 
 * Please note that this service MUST use a {@link FlexoResourceCenterService}
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(DefaultTechnologyAdapterService.class)
public interface TechnologyAdapterService {
	public static final String TECHNOLOGY_ADAPTERS = "technologyAdapters";
	public static final String RESOURCE_CENTER_SERVICE = "flexoResourceCenterService";

	/**
	 * Load all available technology adapters
	 */
	public void loadAvailableTechnologyAdapters();

	@Getter(value = TECHNOLOGY_ADAPTERS, cardinality = Cardinality.LIST, ignoreType = true)
	public List<TechnologyAdapter<?, ?, ?>> getTechnologyAdapters();

	@Setter(TECHNOLOGY_ADAPTERS)
	public void setTechnologyAdapters(List<TechnologyAdapter<?, ?, ?>> technologyAdapters);

	@Adder(TECHNOLOGY_ADAPTERS)
	public void addToTechnologyAdapters(TechnologyAdapter<?, ?, ?> technologyAdapters);

	@Remover(TECHNOLOGY_ADAPTERS)
	public void removeFromTechnologyAdapters(TechnologyAdapter<?, ?, ?> technologyAdapters);

	@Getter(value = RESOURCE_CENTER_SERVICE, ignoreType = true)
	public FlexoResourceCenterService getFlexoResourceCenterService();

	@Setter(RESOURCE_CENTER_SERVICE)
	public void setFlexoResourceCenterService(FlexoResourceCenterService flexoResourceCenterService);

	/**
	 * Return loaded technology adapter mapping supplied class<br>
	 * If adapter is not loaded, return null
	 * 
	 * @param technologyAdapterClass
	 * @return
	 */
	public <TA extends TechnologyAdapter<?, ?, ?>> TA getTechnologyAdapter(Class<TA> technologyAdapterClass);

	/**
	 * Return the {@link TechnologyContextManager} for this technology shared by all {@link FlexoResourceCenter} declared in the scope of
	 * {@link FlexoResourceCenterService}
	 * 
	 * @return
	 */
	public TechnologyContextManager<?, ?, ?> getTechnologyContextManager(TechnologyAdapter<?, ?, ?> technologyAdapter);

}
