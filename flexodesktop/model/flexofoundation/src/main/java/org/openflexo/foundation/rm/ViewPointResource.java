package org.openflexo.foundation.rm;

import java.io.File;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

import org.openflexo.foundation.resource.FlexoXMLFileResource;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(ViewPointResourceImpl.class)
@XMLElement
public interface ViewPointResource extends FlexoXMLFileResource<ViewPoint> {

	public static final String VIEW_POINT_LIBRARY = "viewPointLibrary";
	public static final String DIRECTORY = "directory";

	public ViewPoint getViewPoint();

	@Getter(value = VIEW_POINT_LIBRARY, ignoreType = true)
	public ViewPointLibrary getViewPointLibrary();

	@Setter(VIEW_POINT_LIBRARY)
	public void setViewPointLibrary(ViewPointLibrary viewPointLibrary);

	@Getter(DIRECTORY)
	@XmlAttribute
	public File getDirectory();

	@Setter(DIRECTORY)
	public void setDirectory(File file);

	public List<VirtualModelResource> getVirtualModelResources();

	public boolean isDeprecatedVersion();
}
