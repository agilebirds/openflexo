package org.openflexo.foundation.resource;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.FlexoProjectObject;
import org.openflexo.model.annotations.ModelEntity;

/**
 * Represents the data a {@link FlexoResource} give access to, in the context of a {@link FlexoProject}
 * 
 * @author sylvain
 * 
 * @param <RD>
 */
@ModelEntity
public interface ProjectResourceData<RD extends ProjectResourceData<RD>> extends ResourceData<RD>, FlexoProjectObject {

	/*public static final String PROJECT = "project";

	@Getter(value = PROJECT, ignoreType = true)
	@ReturnedValue(FLEXO_RESOURCE + ".project")
	public FlexoProject getProject();

	@Setter(PROJECT)
	public void setProject(FlexoProject aProject);*/

}
