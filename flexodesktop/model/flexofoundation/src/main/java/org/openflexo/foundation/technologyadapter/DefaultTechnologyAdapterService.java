package org.openflexo.foundation.technologyadapter;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

/**
 * Default implementation for {@link TechnologyAdapterService}
 * 
 * @author sylvain
 * 
 */
public abstract class DefaultTechnologyAdapterService implements TechnologyAdapterService {

	private static final Logger logger = Logger.getLogger(DefaultTechnologyAdapterService.class.getPackage().getName());

	private FlexoResourceCenterService flexoResourceCenterService;

	private Map<Class, TechnologyAdapter<?, ?>> loadedAdapters;
	private Map<TechnologyAdapter<?, ?>, TechnologyContextManager<?, ?>> technologyContextManager;

	public static TechnologyAdapterService getNewInstance() {
		try {
			ModelFactory factory = new ModelFactory().importClass(TechnologyAdapterService.class);
			factory.setImplementingClassForInterface(DefaultTechnologyAdapterService.class, TechnologyAdapterService.class);
			return factory.newInstance(TechnologyAdapterService.class);
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
	@Override
	public void loadAvailableTechnologyAdapters() {
		if (loadedAdapters == null) {
			loadedAdapters = new Hashtable<Class, TechnologyAdapter<?, ?>>();
			logger.info("Loading available technology adapters...");
			ServiceLoader<TechnologyAdapter> loader = ServiceLoader.load(TechnologyAdapter.class);
			Iterator<TechnologyAdapter> iterator = loader.iterator();
			while (iterator.hasNext()) {
				TechnologyAdapter technologyAdapter = iterator.next();
				technologyAdapter.setTechnologyAdapterService(this);
				TechnologyContextManager tcm = technologyAdapter.createTechnologyContextManager(getFlexoResourceCenterService());
				technologyContextManager.put(technologyAdapter, tcm);
				addToTechnologyAdapters(technologyAdapter);

				logger.info("Load " + technologyAdapter.getName() + " as " + technologyAdapter.getClass());

				if (loadedAdapters.containsKey(technologyAdapter.getClass())) {
					logger.severe("Cannot include TechnologyAdapter with classname '" + technologyAdapter.getClass().getName()
							+ "' because it already exists !!!! A TechnologyAdapter name MUST be unique !");
				} else {
					loadedAdapters.put(technologyAdapter.getClass(), technologyAdapter);
				}
			}
			logger.info("Loading available technology adapters. Done.");
		}

	}

	/**
	 * Return loaded technology adapter mapping supplied class<br>
	 * If adapter is not loaded, return null
	 * 
	 * @param technologyAdapterClass
	 * @return
	 */
	@Override
	public <TA extends TechnologyAdapter<?, ?>> TA getTechnologyAdapter(Class<TA> technologyAdapterClass) {
		return (TA) loadedAdapters.get(technologyAdapterClass);
	}

	/**
	 * Iterates over loaded technology adapters
	 * 
	 * @return
	 */
	public Collection<TechnologyAdapter<?, ?>> getLoadedAdapters() {
		return loadedAdapters.values();
	}

	/**
	 * Return the {@link TechnologyContextManager} for this technology for this technology shared by all {@link FlexoResourceCenter}
	 * declared in the scope of {@link FlexoResourceCenterService}
	 * 
	 * @return
	 */
	@Override
	public TechnologyContextManager<?, ?> getTechnologyContextManager(TechnologyAdapter<?, ?> technologyAdapter) {
		return technologyContextManager.get(technologyAdapter);
	}

}
