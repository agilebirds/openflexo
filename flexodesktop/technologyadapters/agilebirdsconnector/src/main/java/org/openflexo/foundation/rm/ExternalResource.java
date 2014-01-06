package org.openflexo.foundation.rm;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.resource.ImportedProjectLoaded;
import org.openflexo.xmlcode.XMLSerializable;

public class ExternalResource implements FlexoObserver, XMLSerializable {

	public static interface ExternalResourceOwner {
		public void externalResourceFound(ExternalResource externalResource, FlexoResource resource);

		public void externalResourceNotFound(ExternalResource externalResource);
	}

	private ExternalResourceOwner owner;
	private String projectURI;
	private String resourceIdentifier;
	private FlexoProject project;
	private FlexoProject observedProject;

	// Created by deserialization
	public ExternalResource(FlexoProjectBuilder builder) {
		this.project = builder.project;
		this.project.addObserver(this);
	}

	// Created during serialization
	public ExternalResource(FlexoResource<?> resource) {
		this.projectURI = resource.getProject().getProjectURI();
		this.resourceIdentifier = resource.getResourceIdentifier();
	}

	public void delete() {
		if (project != null) {
			project.deleteObserver(this);
		}
		if (observedProject != null) {
			observedProject.deleteObserver(this);
		}
		owner = null;
	}

	public String getProjectURI() {
		return projectURI;
	}

	public void setProjectURI(String projectURI) {
		this.projectURI = projectURI;
	}

	public String getResourceIdentifier() {
		return resourceIdentifier;
	}

	public void setResourceIdentifier(String resourceIdentifier) {
		this.resourceIdentifier = resourceIdentifier;
	}

	public ExternalResourceOwner getOwner() {
		return owner;
	}

	public void setOwner(ExternalResourceOwner owner) {
		if (owner != null) {
			this.owner = owner;
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof ImportedProjectLoaded && ((ImportedProjectLoaded) dataModification).getProject() != null
				&& ((ImportedProjectLoaded) dataModification).getProject().getURI().equals(getProjectURI())) {
			FlexoProject project = ((ImportedProjectLoaded) dataModification).getProject();
			FlexoResource<FlexoResourceData> resource = (FlexoResource<FlexoResourceData>) project.resourceForKey(getResourceIdentifier());
			if (resource != null) {
				owner.externalResourceFound(this, resource);
				delete();
			} else {
				if (this.project != null) {
					this.project.deleteObserver(this);
				}
				project.addObserver(this);
				observedProject = project;
				owner.externalResourceNotFound(this);
			}
		} else if (observable instanceof FlexoProject && dataModification instanceof ResourceAdded
				&& ((FlexoProject) observable).getProjectURI().equals(getProjectURI())) {
			FlexoProject project = (FlexoProject) observable;
			FlexoResource<FlexoResourceData> resource = (FlexoResource<FlexoResourceData>) project.resourceForKey(getResourceIdentifier());
			if (resource != null) {
				owner.externalResourceFound(this, resource);
				delete();
			}
		}
	}
}