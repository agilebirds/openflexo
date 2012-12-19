package org.openflexo.foundation.technologyadapter;

import java.util.List;

import org.openflexo.foundation.FlexoService;
import org.openflexo.foundation.FlexoServiceImpl;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;

/**
 * The {@link InformationSpace} is a {@link FlexoService} providing access to modelling elements from their original technological context.<br>
 * The information space is obtained through two services from the {@link FlexoServiceManager}, and results from the merging of the
 * {@link FlexoResourceCenterService} and the {@link TechnologyAdapterService}.<br>
 * For each {@link FlexoResourceCenter} and for each {@link TechnologyAdapter}, a repository of {@link FlexoModel} and
 * {@link FlexoMetaModel} are managed.
 * 
 * @author sguerin
 * 
 */
public class InformationSpace extends FlexoServiceImpl {

	public InformationSpace() {
	}

	@Override
	public void initialize() {
	}

	public List<TechnologyAdapter<?, ?>> getTechnologyAdapters() {
		if (getFlexoServiceManager() != null) {
			return getFlexoServiceManager().getTechnologyAdapterService().getTechnologyAdapters();
		}
		return null;
	}

	/**
	 * Return the list of all non-empty {@link ModelRepository} discoverable in the scope of {@link FlexoServiceManager}, related to
	 * technology as supplied by {@link TechnologyAdapter} parameter
	 * 
	 * @param technologyAdapter
	 * @return
	 */
	public List<ModelRepository<?, ?, ?, ?>> getAllModelRepositories(TechnologyAdapter<?, ?> technologyAdapter) {
		if (getFlexoServiceManager() != null) {
			return getFlexoServiceManager().getTechnologyAdapterService().getAllModelRepositories(technologyAdapter);
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
		if (getFlexoServiceManager() != null) {
			return getFlexoServiceManager().getTechnologyAdapterService().getAllMetaModelRepositories(technologyAdapter);
		}
		return null;
	}

	public FlexoMetaModelResource<?, ?> getMetaModel(String uri) {
		for (TechnologyAdapter ta : getFlexoServiceManager().getTechnologyAdapterService().getTechnologyAdapters()) {
			FlexoMetaModelResource<?, ?> returned = getMetaModel(uri, ta);
			if (returned != null) {
				return returned;
			}
		}
		return null;
	}

	public FlexoMetaModelResource<?, ?> getMetaModel(String uri, TechnologyAdapter<?, ?> technologyAdapter) {
		for (MetaModelRepository<?, ?, ?, ?> mmRep : getAllMetaModelRepositories(technologyAdapter)) {
			FlexoResource<?> resource = mmRep.getResource(uri);
			if (resource != null) {
				return (FlexoMetaModelResource<?, ?>) resource;
			}
		}
		return null;
	}
}
