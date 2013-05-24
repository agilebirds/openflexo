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

	/**
	 * Return the model this resource is storing (same as {@link #getModel()}
	 * 
	 * @return
	 */
	public M getModelData();

	/**
	 * Return the model this resource is storing (same as {@link #getModelData()}
	 * 
	 * @return
	 */
	public M getModel();

	/**
	 * Return flag indicating in this model resource appear to conform to supplied meta-model resource. Assertion is performed, that
	 * execution of this method should not cause any of both resource to be loaded (lazy evalution method). If strong checking is required,
	 * prefer to use {@link #isConformTo(FlexoMetaModelResource)} method.
	 * 
	 * 
	 * @param aMetaModelResource
	 * @return
	 */
	// public boolean appearToConformTo(FlexoMetaModelResource<M, MM> aMetaModelResource);

	/**
	 * Return flag indicating in this model resource appear to conform to supplied meta-model resource.<br>
	 * As strong checking is required, the loading of the resources might be necessary
	 * 
	 * 
	 * @param aMetaModelResource
	 * @return
	 */
	// public boolean isConformTo(FlexoMetaModelResource<M, MM> aMetaModelResource);

}