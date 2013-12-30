package org.openflexo.foundation.viewpoint.rm;

import java.util.List;

import org.openflexo.foundation.resource.DirectoryContainerResource;
import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.ViewPointModelFactory;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(ViewPointResourceImpl.class)
@XMLElement
public interface ViewPointResource extends PamelaResource<ViewPoint, ViewPointModelFactory>, DirectoryContainerResource<ViewPoint> {

	public static final String VIEW_POINT_LIBRARY = "viewPointLibrary";
	public static final String DIRECTORY = "directory";

	public ViewPoint getViewPoint();

	@Getter(value = VIEW_POINT_LIBRARY, ignoreType = true)
	public ViewPointLibrary getViewPointLibrary();

	@Setter(VIEW_POINT_LIBRARY)
	public void setViewPointLibrary(ViewPointLibrary viewPointLibrary);

	public List<VirtualModelResource> getVirtualModelResources();

	public boolean isDeprecatedVersion();
}