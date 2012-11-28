package org.openflexo.foundation.technologyadapter;

import org.openflexo.foundation.resource.ResourceData;

/**
 * This interface is implemented by all objects considered in Flexo Modelling Language as a meta-model, as defined to be metamodels of
 * {@link FlexoModel} (see {@link FlexoModel}<br>
 * 
 * Note than any {@link FlexoModel} is conform to a {@link FlexoMetaModel}.
 * 
 * Its access it made available by the notion of {@link ModelSlot} provided by a {@link TechnologyAdapter} dedicated to a particular
 * technological space (a technology).
 * 
 * @author sylvain
 * 
 * @param <MM>
 */
public interface FlexoMetaModel<MM extends FlexoMetaModel<MM>> extends /*StorageResourceData,*/ResourceData<MM> {

	public String getURI();

	public boolean isReadOnly();

	public void setIsReadOnly(boolean b);

}
