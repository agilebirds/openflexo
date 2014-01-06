package org.openflexo.foundation.resource;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.FlexoProjectObject;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

/**
 * A {@link FlexoProjectResource} is a resource which is managed inside a {@link FlexoProject}
 * 
 * @param <RD>
 *            the type of the resource data reference by this resource
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement
public interface FlexoProjectResource<RD extends ResourceData<RD>> extends FlexoResource<RD>, FlexoProjectObject {
	/*public static final String PROJECT = "project";

	@Getter(value = PROJECT, ignoreType = true)
	@XmlAttribute
	public FlexoProject getProject();

	@Setter(PROJECT)
	public void setProject(FlexoProject project);*/

}
