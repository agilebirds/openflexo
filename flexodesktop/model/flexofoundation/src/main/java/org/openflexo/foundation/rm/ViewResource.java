package org.openflexo.foundation.rm;

import java.io.File;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

import org.openflexo.foundation.resource.FlexoProjectResource;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoXMLFileResource;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

/**
 * This is the {@link FlexoResource} encoding a {@link View}
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(ViewResourceImpl.class)
@XMLElement
public interface ViewResource extends FlexoXMLFileResource<View>, FlexoProjectResource<View> {

	public static final String VIEW_SUFFIX = ".view";

	public static final String VIEW_LIBRARY = "viewLibrary";
	public static final String DIRECTORY = "directory";
	public static final String VIEWPOINT_RESOURCE = "viewPointResource";

	public ViewPoint getViewPoint();

	@Getter(value = VIEW_LIBRARY, ignoreType = true)
	public ViewLibrary getViewLibrary();

	@Setter(VIEW_LIBRARY)
	public void setViewLibrary(ViewLibrary viewLibrary);

	@Getter(DIRECTORY)
	@XmlAttribute
	public File getDirectory();

	@Setter(DIRECTORY)
	public void setDirectory(File file);

	@Getter(value = VIEWPOINT_RESOURCE, ignoreType = true)
	public ViewPointResource getViewPointResource();

	@Setter(VIEWPOINT_RESOURCE)
	public void setViewPointResource(ViewPointResource viewPointResource);

	public View getView();

	public List<VirtualModelInstanceResource> getVirtualModelInstanceResources();

}
