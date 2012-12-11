package org.openflexo.foundation.technologyadapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoService;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.DefaultResourceCenterService.ResourceCenterAdded;
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
	private Map<TechnologyAdapter<?, ?>, TechnologyContextManager<?, ?>> technologyContextManagers;

	private FlexoServiceManager serviceManager;

	public static TechnologyAdapterService getNewInstance(FlexoResourceCenterService resourceCenterService) {
		try {
			ModelFactory factory = new ModelFactory(TechnologyAdapterService.class);
			factory.setImplementingClassForInterface(DefaultTechnologyAdapterService.class, TechnologyAdapterService.class);
			TechnologyAdapterService returned = factory.newInstance(TechnologyAdapterService.class);
			returned.setFlexoResourceCenterService(resourceCenterService);
			// returned.loadAvailableTechnologyAdapters();
			return returned;
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
	private void loadAvailableTechnologyAdapters() {
		if (loadedAdapters == null) {
			loadedAdapters = new Hashtable<Class, TechnologyAdapter<?, ?>>();
			technologyContextManagers = new Hashtable<TechnologyAdapter<?, ?>, TechnologyContextManager<?, ?>>();
			logger.info("Loading available technology adapters...");
			ServiceLoader<TechnologyAdapter> loader = ServiceLoader.load(TechnologyAdapter.class);
			Iterator<TechnologyAdapter> iterator = loader.iterator();
			while (iterator.hasNext()) {
				TechnologyAdapter technologyAdapter = iterator.next();
				technologyAdapter.setTechnologyAdapterService(this);
				TechnologyContextManager tcm = technologyAdapter.createTechnologyContextManager(getFlexoResourceCenterService());
				technologyContextManagers.put(technologyAdapter, tcm);
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
		return technologyContextManagers.get(technologyAdapter);
	}

	@Override
	public void receiveNotification(FlexoService caller, ServiceNotification notification) {
		if (caller instanceof FlexoResourceCenterService) {
			if (notification instanceof ResourceCenterAdded) {
				FlexoResourceCenter rc = ((ResourceCenterAdded) notification).getAddedResourceCenter();
				rc.initialize(this);
				for (TechnologyAdapter<?, ?> ta : getTechnologyAdapters()) {
					ta.resourceCenterAdded(rc);
				}
			}
		}
	}

	@Override
	public void register(FlexoServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	@Override
	public FlexoServiceManager getFlexoServiceManager() {
		return serviceManager;
	}

	@Override
	public void initialize() {
		loadAvailableTechnologyAdapters();
		for (TechnologyAdapter<?, ?> ta : getTechnologyAdapters()) {
			ta.initialize();
		}
		for (FlexoResourceCenter rc : getFlexoResourceCenterService().getResourceCenters()) {
			rc.initialize(this);
		}
	}

	/**
	 * Return the list of all non-empty {@link ModelRepository} discoverable in the scope of {@link FlexoServiceManager}, related to
	 * technology as supplied by {@link TechnologyAdapter} parameter
	 * 
	 * @param technologyAdapter
	 * @return
	 */
	/*@Override
	public <R extends FlexoResource<? extends M>, M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM>> List<ModelRepository<R, M, MM, TA>> getAllModelRepositories(
			TA technologyAdapter) {
		List<ModelRepository<R, M, MM, TA>> returned = new ArrayList<ModelRepository<R, M, MM, TA>>();
		for (FlexoResourceCenter rc : getFlexoResourceCenterService().getResourceCenters()) {
			returned.add((ModelRepository<R, M, MM, TA>) rc.getModelRepository(technologyAdapter));
		}
		return returned;
	}*/
	@Override
	public List<ModelRepository<?, ?, ?, ?>> getAllModelRepositories(TechnologyAdapter<?, ?> technologyAdapter) {
		List<ModelRepository<?, ?, ?, ?>> returned = new ArrayList<ModelRepository<?, ?, ?, ?>>();
		for (FlexoResourceCenter rc : getFlexoResourceCenterService().getResourceCenters()) {
			if (rc.getModelRepository(technologyAdapter).getSize() > 0)
				returned.add(rc.getModelRepository(technologyAdapter));
		}
		return returned;
	}

	/**
	 * Return the list of all non-empty {@link MetaModelRepository} discoverable in the scope of {@link FlexoServiceManager}, related to
	 * technology as supplied by {@link TechnologyAdapter} parameter
	 * 
	 * @param technologyAdapter
	 * @return
	 */
	/*@Override
	public <R extends FlexoResource<? extends MM>, M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM>> List<MetaModelRepository<R, M, MM, TA>> getAllMetaModelRepositories(
			TA technologyAdapter) {
		List<MetaModelRepository<R, M, MM, TA>> returned = new ArrayList<MetaModelRepository<R, M, MM, TA>>();
		for (FlexoResourceCenter rc : getFlexoResourceCenterService().getResourceCenters()) {
			returned.add((MetaModelRepository<R, M, MM, TA>) rc.getMetaModelRepository(technologyAdapter));
		}
		return returned;
	}*/
	@Override
	public List<MetaModelRepository<?, ?, ?, ?>> getAllMetaModelRepositories(TechnologyAdapter<?, ?> technologyAdapter) {
		List<MetaModelRepository<?, ?, ?, ?>> returned = new ArrayList<MetaModelRepository<?, ?, ?, ?>>();
		for (FlexoResourceCenter rc : getFlexoResourceCenterService().getResourceCenters()) {
			if (rc.getMetaModelRepository(technologyAdapter).getSize() > 0)
				returned.add(rc.getMetaModelRepository(technologyAdapter));
		}
		return returned;
	}

}
