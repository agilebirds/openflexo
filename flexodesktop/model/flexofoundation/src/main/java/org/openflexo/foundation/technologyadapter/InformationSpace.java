package org.openflexo.foundation.technologyadapter;

import java.util.List;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoService;
import org.openflexo.foundation.FlexoServiceImpl;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.DefaultResourceCenterService.ResourceCenterAdded;
import org.openflexo.foundation.resource.DefaultResourceCenterService.ResourceCenterRemoved;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;

/**
 * The {@link InformationSpace} is a {@link FlexoService} providing access to modelling elements from their original technological context.<br>
 * The information space is obtained through two services from the {@link FlexoServiceManager}, and results from the merging of the
 * {@link FlexoResourceCenterService} and the {@link TechnologyAdapterService}.<br>
 * For each {@link FlexoResourceCenter} and for each {@link TechnologyAdapter}, a repository of {@link FlexoModel} and
 * {@link FlexoMetaModel} are managed.
 * 
 * @author sylvain
 * 
 */
public class InformationSpace extends FlexoServiceImpl {

	public InformationSpace() {
	}

	@Override
	public void initialize() {
	}

	public List<TechnologyAdapter<?, ?>> getTechnologyAdapters() {
		if (getServiceManager() != null) {
			return getServiceManager().getTechnologyAdapterService().getTechnologyAdapters();
		}
		return null;
	}

	@Override
	public void receiveNotification(FlexoService caller, ServiceNotification notification) {
		if (notification instanceof ResourceCenterAdded) {
			setChanged();
			notifyObservers(new DataModification("getAllModelRepositories(TechnologyAdapter<?,?>)",
					((ResourceCenterAdded) notification).getAddedResourceCenter()));
		}
		if (notification instanceof ResourceCenterRemoved) {
			setChanged();
			notifyObservers(new DataModification(((ResourceCenterRemoved) notification).getRemovedResourceCenter(), null));
		}
		super.receiveNotification(caller, notification);
	}

	/**
	 * Return the list of all non-empty {@link ModelRepository} discoverable in the scope of {@link FlexoServiceManager}, related to
	 * technology as supplied by {@link TechnologyAdapter} parameter
	 * 
	 * @param technologyAdapter
	 * @return
	 */
	public List<ModelRepository<?, ?, ?, ?>> getAllModelRepositories(TechnologyAdapter<?, ?> technologyAdapter) {
		if (getServiceManager() != null) {
			return getServiceManager().getTechnologyAdapterService().getAllModelRepositories(technologyAdapter);
		}
		return null;
	}

	/**
	 * Return the list of all non-empty {@link MetaModelRepository} discoverable in the scope of {@link FlexoServiceManager}, related to
	 * technology as supplied by {@link TechnologyAdapter} parameter
	 * 
	 * @param technologyAdapter
	 * @return
	 */
	public List<MetaModelRepository<?, ?, ?, ?>> getAllMetaModelRepositories(TechnologyAdapter<?, ?> technologyAdapter) {
		if (getServiceManager() != null) {
			return getServiceManager().getTechnologyAdapterService().getAllMetaModelRepositories(technologyAdapter);
		}
		return null;
	}

	public FlexoMetaModelResource<?, ?> getMetaModelWithURI(String uri) {
		for (TechnologyAdapter ta : getServiceManager().getTechnologyAdapterService().getTechnologyAdapters()) {
			FlexoMetaModelResource<?, ?> returned = getMetaModelWithURI(uri, ta);
			if (returned != null) {
				return returned;
			}
		}
		return null;
	}

	public FlexoMetaModelResource<?, ?> getMetaModelWithURI(String uri, TechnologyAdapter<?, ?> technologyAdapter) {
		if (technologyAdapter != null && technologyAdapter.getTechnologyContextManager() != null) {
			return technologyAdapter.getTechnologyContextManager().getMetaModelWithURI(uri);
		}
		return null;
		/*for (MetaModelRepository<?, ?, ?, ?> mmRep : getAllMetaModelRepositories(technologyAdapter)) {
			FlexoResource<?> resource = mmRep.getResource(uri);
			if (resource != null) {
				return (FlexoMetaModelResource<?, ?>) resource;
			}
		}
		return null;*/
	}

	public FlexoModelResource<?, ?> getModelWithURI(String uri) {
		for (TechnologyAdapter ta : getServiceManager().getTechnologyAdapterService().getTechnologyAdapters()) {
			FlexoModelResource<?, ?> returned = getModelWithURI(uri, ta);
			if (returned != null) {
				return returned;
			}
		}
		return null;
	}

	public FlexoModelResource<?, ?> getModelWithURI(String uri, TechnologyAdapter<?, ?> technologyAdapter) {
		if (technologyAdapter == null) {
			logger.warning("Unexpected null " + technologyAdapter);
			return null;
		} else if (technologyAdapter.getTechnologyContextManager() == null) {
			// logger.warning("Unexpected null technologyContextManager for " + technologyAdapter);
			return null;
		}
		return technologyAdapter.getTechnologyContextManager().getModelWithURI(uri);
		/*for (ModelRepository<?, ?, ?, ?> mRep : getAllModelRepositories(technologyAdapter)) {
			FlexoResource<?> resource = mRep.getResource(uri);
			if (resource != null) {
				return (FlexoModelResource<?, ?>) resource;
			}
		}
		return null;*/
	}
}
