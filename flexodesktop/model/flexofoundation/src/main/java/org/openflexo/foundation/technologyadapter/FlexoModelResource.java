package org.openflexo.foundation.technologyadapter;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

/**
 * A {@link FlexoModelResource} is a {@link FlexoResource} specific to a technology and storing a model
 * 
 * @author sylvain
 * 
 * @param <RD>
 */
@ModelEntity
@XMLElement
public interface FlexoModelResource<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends TechnologyAdapterResource<M> {

	public static final String META_MODEL_RESOURCE = "metaModelResource";

	@Getter(value = META_MODEL_RESOURCE, ignoreType = true)
	public FlexoMetaModelResource<M, MM> getMetaModelResource();

	@Setter(META_MODEL_RESOURCE)
	public void setMetaModelResource(FlexoMetaModelResource<M, MM> aMetaModelResource);

	public M getModelData();

	public M getModel();
}