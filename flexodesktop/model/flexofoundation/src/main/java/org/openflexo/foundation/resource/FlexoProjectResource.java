package org.openflexo.foundation.resource;

import javax.xml.bind.annotation.XmlAttribute;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
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
@ImplementationClass(FlexoProjectResourceImpl.class)
@XMLElement
public interface FlexoProjectResource<RD extends ResourceData<RD>> extends FlexoResource<RD> {
	public static final String PROJECT = "project";

	@Getter(value = PROJECT, ignoreType = true)
	@XmlAttribute
	public FlexoProject getProject();

	@Setter(PROJECT)
	public void setProject(FlexoProject project);

}
