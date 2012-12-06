package org.openflexo.foundation.technologyadapter;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

/**
 * A {@link FlexoMetaModelResource} is a {@link FlexoResource} specific to a technology and storing a model
 * 
 * @author sylvain
 * 
 * @param <RD>
 */
@ModelEntity
@XMLElement
public interface FlexoMetaModelResource<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends TechnologyAdapterResource<MM> {
}