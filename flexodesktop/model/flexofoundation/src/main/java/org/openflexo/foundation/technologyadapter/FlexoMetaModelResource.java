package org.openflexo.foundation.technologyadapter;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

/**
 * A {@link FlexoMetaModelResource} is a {@link FlexoResource} specific to a technology and storing a metamodel
 * 
 * @see FlexoModelResource
 * @author sylvain
 * 
 * @param <M>
 *            type of model being handled as resource data
 * @param <MM>
 *            type of metamodel
 * @param <TA>
 *            type of {@link TechnologyAdapter} handling this conforming pattern
 */
@ModelEntity
@XMLElement
public interface FlexoMetaModelResource<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter> extends
		TechnologyAdapterResource<MM, TA> {

	public MM getMetaModelData();
}