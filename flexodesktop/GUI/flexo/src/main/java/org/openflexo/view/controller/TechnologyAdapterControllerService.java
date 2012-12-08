package org.openflexo.view.controller;

import org.openflexo.foundation.FlexoService;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

/**
 * This service provides access to all technology adapters controllers available in a given environment.
 * 
 * Please note that this service MUST use a {@link FlexoResourceCenterService}
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(DefaultTechnologyAdapterControllerService.class)
public interface TechnologyAdapterControllerService extends FlexoService {

	/**
	 * Load all available technology adapters controllers
	 */
	// public void loadAvailableTechnologyAdapterControllers();

	/**
	 * Return loaded technology adapter controller mapping supplied class<br>
	 * If adapter is not loaded, return null
	 * 
	 * @param technologyAdapterClass
	 * @return
	 */
	public <TAC extends TechnologyAdapterController<TA>, TA extends TechnologyAdapter<?, ?>> TAC getTechnologyAdapterController(
			Class<TAC> technologyAdapterControllerClass);

}
