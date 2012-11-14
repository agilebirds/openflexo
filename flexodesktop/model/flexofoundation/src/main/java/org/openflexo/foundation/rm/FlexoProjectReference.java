package org.openflexo.foundation.rm;

import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.ReturnedValue;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.factory.AccessibleProxyObject;

@ModelEntity
@XMLElement
@ImplementationClass(FlexoProjectReferenceImpl.class)
public interface FlexoProjectReference extends AccessibleProxyObject {

	public static final String PROJECT_DATA = "projectData";
	public static final String PROJECT_NAME = "projectName";
	public static final String PROJECT_URI = "projectURI";
	public static final String PROJECT_VERSION = "projectVersion";
	public static final String PROJECT_REVISION = "projectRevision";
	public static final String REFERRING_PROJECT = "referringProject";
	public static final String REFERRED_PROJECT = "referredProject";
	public static final String PROJECT = "project";
	public static final String STATUS = "status";

	public enum ReferenceStatus {
		RESOLVED, UNRESOLVED;
	}

	@Initializer
	public FlexoProjectReference init(@Parameter(REFERRED_PROJECT) FlexoProject referredProject);

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
	 * The status of this reference.
	 * 
	 * @return the status of this reference
	 */
	@Getter(STATUS)
	public ReferenceStatus getStatus();

	/**
	 * Returns the referred project, the project to which this reference refers to.
	 * 
	 * @return the referred project
	 * @throws ProjectLoadingCancelledException
	 *             in case the user cancels the loading of this project.
	 */
	@Getter(value = REFERRED_PROJECT, ignoreType = true)
	public FlexoProject getReferredProject() throws ProjectLoadingCancelledException;

	/**
	 * Returns the name of the referred project. This getter can be used to display the name of the project
	 * 
	 * @return the name of the referred project
	 */
	@Getter(PROJECT_NAME)
	@XMLAttribute
	public String getProjectName();

	/**
	 * Returns the URI of the referred project.
	 * 
	 * @return the URI of the referred project
	 */
	@Getter(PROJECT_URI)
	@XMLAttribute
	public String getProjectURI();

	/**
	 * Returns the version of the referred project
	 * 
	 * @return
	 */
	@Getter(PROJECT_VERSION)
	@XMLAttribute
	public String getProjectVersion();

	/**
	 * Returns the revision of the referred project
	 * 
	 * @return
	 */
	@Getter(PROJECT_REVISION)
	@XMLAttribute
	public Long getProjectRevision();

}