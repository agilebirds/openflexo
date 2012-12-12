package org.openflexo.foundation.resource;

import java.io.File;

import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

/**
 * A {@link FlexoXMLFileResource} is a {@link FlexoFileResource} stored in a XML {@link File}
 * 
 * @author sylvain
 * 
 * @param <RD>
 */
@ModelEntity
@XMLElement
public interface FlexoXMLFileResource<RD extends ResourceData<RD>> extends FlexoFileResource<RD> {

	public Object instanciateNewBuilder();

	public boolean isConverting();
}