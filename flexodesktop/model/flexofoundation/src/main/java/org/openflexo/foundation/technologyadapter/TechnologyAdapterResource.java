package org.openflexo.foundation.technologyadapter;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

/**
 * A {@link TechnologyAdapterResource} is a {@link FlexoResource} specific to a technology
 * 
 * @author sylvain
 * 
 * @param <RD>
 */
@ModelEntity
@XMLElement
public interface TechnologyAdapterResource<RD extends ResourceData<RD>> extends FlexoResource<RD> {
	public static final String TECHNOLOGY_ADAPTER = "technologyAdapter";

	@Getter(value = TECHNOLOGY_ADAPTER, ignoreType = true)
	public TechnologyAdapter<?, ?> getTechnologyAdapter();

	@Setter(TECHNOLOGY_ADAPTER)
	public void setTechnologyAdapter(TechnologyAdapter<?, ?> technologyAdapter);
}