package org.openflexo.foundation.rm;

import org.openflexo.foundation.resource.UserResourceCenter.FlexoFileResource;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Modify;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.ReturnedValue;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.factory.AccessibleProxyObject;

@ModelEntity
@XMLElement
@ImplementationClass(FlexoProjectReferenceImpl.class)
@Modify(forward = FlexoProjectReference.PROJECT_DATA)
public interface FlexoProjectReference extends AccessibleProxyObject, FlexoFileResource<FlexoProject> {

	public static final String PROJECT_DATA = "projectData";
	public static final String REFERRING_PROJECT = "referringProject";
	public static final String REFERRED_PROJECT = "referredProject";
	public static final String PROJECT = "project";
	public static final String WORKFLOW = "workflow";

	@Initializer
	public FlexoProjectReference init(@Parameter(REFERRED_PROJECT) FlexoProject referredProject);

	/**
	 * Synchronized the resource internal properties with the one declared in the resource data (if any). Typically, URI, version and
	 * revision are updated with the referred project (if available).
	 */
	public void syncWithResourceData();

	/**
	 * Getter for the project data
	 * 
	 * @return the project data of this project reference
	 */
	@Getter(value = PROJECT_DATA, inverse = ProjectData.IMPORTED_PROJECTS)
	public ProjectData getProjectData();

	/**
	 * Sets the project data
	 * 
	 * @param data
	 *            the project data
	 */
	@Setter(PROJECT_DATA)
	public void setProjectData(ProjectData data);

	/**
	 * Returns the referring project, ie, the project in which this project reference belongs, is used. The returned value is equivalent
	 * {@link #getProjectData()}.{@link ProjectData#getProject()}.
	 * 
	 * @return the referring project.
	 */
	@Getter(value = REFERRING_PROJECT, ignoreType = true)
	@ReturnedValue(PROJECT_DATA + "." + ProjectData.PROJECT)
	public FlexoProject getReferringProject();

	/**
	 * Returns the referred project, the project to which this reference refers to.
	 * 
	 * @return the referred project
	 * 
	 */
	@Getter(value = REFERRED_PROJECT, ignoreType = true)
	public FlexoProject getReferredProject();

	/**
	 * Returns the referred project, the project to which this reference refers to.
	 * 
	 * @param force
	 *            flag to indicate whether all measures should be taken to load the project or not. Yet, passing <code>true</code> does not
	 *            guarantee that the project will eventually be loaded and callers should handle a <code>null</code> value.
	 * @return the referred project
	 * 
	 */
	public FlexoProject getReferredProject(boolean force);

	/**
	 * Sets the referred project.
	 */
	@Setter(value = REFERRED_PROJECT)
	public void setReferredProject(FlexoProject project);

	/**
	 * Returns the FlexoWorkflow of this project reference. This can either be a cached value or the live object (if the corresponding
	 * project has been loaded and set)
	 */
	public FlexoWorkflow getWorkflow();

}