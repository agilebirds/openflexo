package org.openflexo.foundation.rm;

import java.io.File;

import javax.xml.bind.annotation.XmlAttribute;

import org.openflexo.foundation.resource.FlexoXMLFileResource;
import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.foundation.viewpoint.FMLModelFactory;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(VirtualModelResourceImpl.class)
@XMLElement
public interface VirtualModelResource<VM extends VirtualModel<VM>> extends FlexoXMLFileResource<VM>, PamelaResource<VM, FMLModelFactory> {

	public static final String VIEW_POINT_LIBRARY = "viewPointLibrary";
	public static final String DIRECTORY = "directory";

	/**
	 * Return virtual model stored by this resource<br>
	 * Load the resource data when unloaded
	 */
	public VM getVirtualModel();

	/**
	 * Return virtual model stored by this resource when loaded<br>
	 * Do not force the resource data to be loaded
	 */
	public VM getLoadedVirtualModel();

	@Getter(value = VIEW_POINT_LIBRARY, ignoreType = true)
	public ViewPointLibrary getViewPointLibrary();

	@Setter(VIEW_POINT_LIBRARY)
	public void setViewPointLibrary(ViewPointLibrary viewPointLibrary);

	@Getter(DIRECTORY)
	@XmlAttribute
	public File getDirectory();

	@Setter(DIRECTORY)
	public void setDirectory(File file);

	@Override
	public ViewPointResource getContainer();

}
