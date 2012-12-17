package org.openflexo.foundation.resource;

import java.io.File;

import javax.xml.bind.annotation.XmlAttribute;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.FlexoVersion;

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

	public static final String MODEL_VERSION = "modelVersion";

	@Getter(value = MODEL_VERSION, isStringConvertable = true)
	@XmlAttribute
	public FlexoVersion getModelVersion();

	@Setter(MODEL_VERSION)
	public void setModelVersion(FlexoVersion file);

	/**
	 * Returns a boolean indicating if this resource needs a builder to be loaded
	 * 
	 * @return boolean
	 */
	public boolean hasBuilder();

	public Object instanciateNewBuilder();

	public boolean isConverting();
}