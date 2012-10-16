package org.openflexo.foundation.rm;

import java.io.FileNotFoundException;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

public class FlexoPamelaResource<SRD extends StorageResourceData> extends FlexoStorageResource<SRD> {

	private String name;
	private ResourceType resourceType;
	private Class<SRD> resourceDataClass;
	private ModelFactory modelFactory;

	public FlexoPamelaResource(FlexoProject project, String name, ResourceType resourceType, Class<SRD> resourceDataClass,
			FlexoProjectFile file) throws ModelDefinitionException, InvalidFileNameException, DuplicateResourceException {
		this(project);
		this.name = name;
		this.resourceType = resourceType;
		this.resourceDataClass = resourceDataClass;
		setResourceFile(file);
		this.modelFactory = new ModelFactory();
		this.modelFactory.importClass(resourceDataClass);
		this._resourceData = modelFactory.newInstance(resourceDataClass);
		this._resourceData.setFlexoResource(this);
	}

	public FlexoPamelaResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	private FlexoPamelaResource(FlexoProject aProject) {
		super(aProject);
	}

	public Class<SRD> getResourceDataClass() {
		return resourceDataClass;
	}

	public void setResourceDataClass(Class<SRD> resourceDataClass) {
		this.resourceDataClass = resourceDataClass;
	}

	@Override
	public ResourceType getResourceType() {
		if (resourceType == null) {
			return ResourceType.PAMELA_RESOURCE;
		}
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	protected void performUpdating(FlexoResourceTree updatedResources) throws ResourceDependencyLoopException, LoadResourceException,
			FileNotFoundException, ProjectLoadingCancelledException, FlexoException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void saveResourceData(boolean clearIsModified) throws SaveResourceException {
		// TODO Auto-generated method stub

	}

	@Override
	protected SRD performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler) throws LoadResourceException,
			FileNotFoundException, ProjectLoadingCancelledException {
		// TODO Auto-generated method stub
		return null;
	}

}
