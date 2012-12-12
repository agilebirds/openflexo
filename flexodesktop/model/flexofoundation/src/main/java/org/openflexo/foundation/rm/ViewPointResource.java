package org.openflexo.foundation.rm;

import org.openflexo.foundation.resource.FlexoXMLFileResource;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.FlexoVersion;

@ModelEntity
@ImplementationClass(ViewPointResourceImpl.class)
@XMLElement
public interface ViewPointResource extends FlexoXMLFileResource<ViewPoint> {

	public static final String VIEW_POINT_LIBRARY = "viewPointLibrary";
	public static final String OPENFLEXO_VERSION = "openflexoVersion";

	public ViewPoint getViewPoint();

	@Getter(value = VIEW_POINT_LIBRARY, ignoreType = true)
	public ViewPointLibrary getViewPointLibrary();

	@Setter(VIEW_POINT_LIBRARY)
	public void setViewPointLibrary(ViewPointLibrary viewPointLibrary);

	/**
	 * Returns openflexo version in which this resource was serialized
	 * 
	 * @return
	 */
	@Getter(value = OPENFLEXO_VERSION, isStringConvertable = true)
	@XMLAttribute
	public FlexoVersion getOpenflexoVersion();

	/**
	 * Sets openflexo version for this resource.
	 * 
	 * @param anURI
	 */
	@Setter(OPENFLEXO_VERSION)
	public void setOpenflexoVersion(FlexoVersion openflexoVersion);

}
