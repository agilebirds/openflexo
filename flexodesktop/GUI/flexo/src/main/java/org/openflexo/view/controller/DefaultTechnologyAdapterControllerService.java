package org.openflexo.view.controller;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoService;
import org.openflexo.foundation.FlexoServiceImpl;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.module.ModuleLoader.ModuleLoaded;

/**
 * Default implementation for {@link TechnologyAdapterService}
 * 
 * @author sylvain
 * 
 */
public abstract class DefaultTechnologyAdapterControllerService extends FlexoServiceImpl implements TechnologyAdapterControllerService {

	private static final Logger logger = Logger.getLogger(DefaultTechnologyAdapterControllerService.class.getPackage().getName());

	private Map<Class, TechnologyAdapterController<?>> loadedAdapters;

	public static TechnologyAdapterControllerService getNewInstance() {
		try {
			ModelFactory factory = new ModelFactory(TechnologyAdapterControllerService.class);
			factory.setImplementingClassForInterface(DefaultTechnologyAdapterControllerService.class,
					TechnologyAdapterControllerService.class);
			return factory.newInstance(TechnologyAdapterControllerService.class);
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Load all available technology adapters<br>
	 * Retrieve all {@link TechnologyAdapter} available from classpath. <br>
	 * Map contains the TechnologyAdapter class name as key and the TechnologyAdapter itself as value.
	 * 
	 * @return the retrieved TechnologyModuleDefinition map.
	 */
	private void loadAvailableTechnologyAdapterControllers() {
		if (loadedAdapters == null) {
			loadedAdapters = new Hashtable<Class, TechnologyAdapterController<?>>();
			logger.info("Loading available technology adapter controllers...");
			ServiceLoader<TechnologyAdapterController> loader = ServiceLoader.load(TechnologyAdapterController.class);
			Iterator<TechnologyAdapterController> iterator = loader.iterator();
			while (iterator.hasNext()) {
				TechnologyAdapterController technologyAdapterController = iterator.next();
				technologyAdapterController.setTechnologyAdapterService(this);
				logger.info("Loaded as " + technologyAdapterController.getClass());

				if (loadedAdapters.containsKey(technologyAdapterController.getClass())) {
					logger.severe("Cannot include TechnologyAdapter with classname '" + technologyAdapterController.getClass().getName()
							+ "' because it already exists !!!! A TechnologyAdapter name MUST be unique !");
				} else {
					loadedAdapters.put(technologyAdapterController.getClass(), technologyAdapterController);
				}
			}
			logger.info("Loading available technology adapters. Done.");
		}

	}

	/**
	 * Return loaded technology adapter controller mapping supplied class<br>
	 * If adapter is not loaded, return null
	 * 
	 * @param technologyAdapterClass
	 * @return
	 */
	@Override
	public <TAC extends TechnologyAdapterController<TA>, TA extends TechnologyAdapter<?, ?>> TAC getTechnologyAdapterController(
			Class<TAC> technologyAdapterControllerClass) {
		return (TAC) loadedAdapters.get(technologyAdapterControllerClass);
	}

	/**
	 * Return loaded technology adapter controller mapping supplied technology adapter<br>
	 * If adapter is not loaded, return null
	 * 
	 * @param technologyAdapterClass
	 * @return
	 */
	@Override
	public <TAC extends TechnologyAdapterController<TA>, TA extends TechnologyAdapter<?, ?>> TAC getTechnologyAdapterController(
			TA technologyAdapter) {
		for (TechnologyAdapterController<?> tac : loadedAdapters.values()) {
			if (tac.getTechnologyAdapter() == technologyAdapter) {
				return (TAC) tac;
			}
		}
		return null;
	}

	/**
	 * Iterates over loaded technology adapters
	 * 
	 * @return
	 */
	public Collection<TechnologyAdapterController<?>> getLoadedAdapterControllers() {
		return loadedAdapters.values();
	}

	@Override
	public void receiveNotification(FlexoService caller, ServiceNotification notification) {
		logger.fine("TechnologyAdapterController service received notification " + notification + " from " + caller);
		if (notification instanceof ModuleLoaded) {
			// When a module is loaded, register all loaded technology adapter controllers with new new loaded module action initializer
			// The newly loaded module will be able to provide all tooling provided by the technology adapter
			for (TechnologyAdapterController<?> adapterController : getLoadedAdapterControllers()) {
				adapterController.initializeActions(((ModuleLoaded) notification).getLoadedModule().getController()
						.getControllerActionInitializer());
			}
		}
	}

	@Override
	public void initialize() {
		loadAvailableTechnologyAdapterControllers();
		for (TechnologyAdapterController<?> ta : getLoadedAdapterControllers()) {
			ta.initialize();
		}
	}

}
