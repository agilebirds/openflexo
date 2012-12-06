package org.openflexo.foundation.resource;

import java.io.File;

import javax.xml.bind.annotation.XmlAttribute;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

/**
 * A {@link FlexoFileResource} is a {@link FlexoResource} stored in a {@link File}
 * 
 * @author sylvain
 * 
 * @param <RD>
 */
@ModelEntity
@XMLElement
public interface FlexoFileResource<RD extends ResourceData<RD>> extends FlexoResource<RD> {
	public static final String FILE = "file";

	@Getter(FILE)
	@XmlAttribute
	public File getFile();

	@Setter(FILE)
	public void setFile(File file);
}