package org.openflexo.foundation.resource;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.foundation.TemporaryFlexoModelObject;

/**
 * Represents a folder, as an organization item inside a {@link ResourceRepository}
 * 
 * @author sylvain
 * 
 */
public class RepositoryFolder<R extends FlexoResource<?>> extends TemporaryFlexoModelObject {

	private final ResourceRepository<R> resourceRepository;
	private final String name;
	private final RepositoryFolder<R> parent;
	private final ArrayList<RepositoryFolder<R>> children;
	private final ArrayList<R> resources;

	public RepositoryFolder(String name, RepositoryFolder<R> parentFolder, ResourceRepository<R> resourceRepository) {
		this.resourceRepository = resourceRepository;
		this.name = name;
		this.parent = parentFolder;
		children = new ArrayList<RepositoryFolder<R>>();
		resources = new ArrayList<R>();
		if (parentFolder != null) {
			parentFolder.addToChildren(this);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public List<RepositoryFolder<R>> getChildren() {
		return children;
	}

	public void addToChildren(RepositoryFolder<R> aFolder) {
		children.add(aFolder);
	}

	public void removeFromChildren(RepositoryFolder<R> aFolder) {
		children.remove(aFolder);
	}

	public List<R> getResources() {
		return resources;
	}

	public void addToResources(R resource) {
		resources.add(resource);
	}

	public void removeFromResources(R resource) {
		resources.remove(resource);
	}

	public ResourceRepository<?> getResourceRepository() {
		return resourceRepository;
	}

}
