package org.openflexo.foundation.technologyadapter;

import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.rm.StorageResourceData;

/**
 * This interface is implemented by all objects considered in Flexo Modelling Language as a model<br>
 * 
 * A model should be seen here as a very higher level than the classical vision of MOF. A model here is any kind of modelling element. It
 * covers all the kind of data or knowleges encoded in a particular technology. It can be a classical model (such as UML, EMF, MOF-like
 * modelling, or a file, or a database, a spreadsheet, a diagram, a document.<br>
 * 
 * A {@link FlexoModel} is conform to a {@link FlexoMetaModel}.
 * 
 * Its access it made available by the notion of {@link ModelSlot} provided by a {@link TechnologyAdapter} dedicated to a particular
 * technological space (a technology).
 * 
 * @author sylvain
 * 
 * @param <MM>
 */
public interface FlexoModel<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends StorageResourceData<M>, ResourceData<M> {

	public MM getMetaModel();

	public String getURI();

	public Object getObject(String objectURI);
}
