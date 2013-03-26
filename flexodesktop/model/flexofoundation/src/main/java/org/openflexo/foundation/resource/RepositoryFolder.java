package org.openflexo.foundation.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObject;

/**
 * Represents a folder, as an organization item inside a {@link ResourceRepository}
 * 
 * @author sylvain
 * 
 */
public class RepositoryFolder<R extends FlexoResource<?>> extends FlexoObject {

	private static final Logger logger = Logger.getLogger(RepositoryFolder.class.getPackage().getName());

	private final ResourceRepository<R> resourceRepository;
	private String name;
	private RepositoryFolder<R> parent;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RepositoryFolder<R>> getChildren() {
		return children;
	}

	public void addToChildren(RepositoryFolder<R> aFolder) {
		children.add(aFolder);
		aFolder.parent = this;
		setChanged();
		notifyObservers(new RepositoryFolderAdded(this, aFolder));
	}

	public void removeFromChildren(RepositoryFolder<R> aFolder) {
		children.remove(aFolder);
		aFolder.parent = null;
		setChanged();
		notifyObservers(new RepositoryFolderRemoved(this, aFolder));
	}

	public RepositoryFolder<R> getParentFolder() {
		return parent;
	}

	public boolean isRootFolder() {
		return getParentFolder() == null;
	}

	public RepositoryFolder<R> getFolderNamed(String newFolderName) {
		for (RepositoryFolder<R> f : children) {
			if (f.getName().equals(newFolderName)) {
				return f;
			}
		}
		return null;
	}

	public List<R> getResources() {
		return resources;
	}

	public void addToResources(R resource) {
		if (resources.contains(resource)) {
			logger.warning("Resource already present in " + this + " : " + resource + ". Ignore it.");
			return;
		}
		resources.add(resource);
		setChanged();
		notifyObservers(new ResourceRegistered(resource, this));
	}

	public void removeFromResources(R resource) {
		resources.remove(resource);
	}

	public ResourceRepository<?> getResourceRepository() {
		return resourceRepository;
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	public File getFile() {
		if (isRootFolder()) {
			if (getResourceRepository() instanceof FileResourceRepository) {
				return ((FileResourceRepository) getResourceRepository()).getDirectory();
			}
			return null;
		} else {
			return new File(getParentFolder().getFile(), getName());
		}
	}

	public R getResourceWithName(String resourceName) {
		for (R resource : getResources()) {
			if (resource.getName().equals(resourceName)) {
				return resource;
			}
		}
		return null;
	}

	public boolean isValidResourceName(String resourceName) {
		return getResourceWithName(resourceName) == null;
	}

	public boolean isFatherOf(RepositoryFolder<R> folder) {
		RepositoryFolder<R> f = folder.getParentFolder();
		while (f != null) {
			if (f.equals(this)) {
				return true;
			}
			f = f.getParentFolder();
		}
		return false;
	}

	@Override
	public void delete() {
		if (getFile().exists()) {
			getFile().delete();
		}
		super.delete();
	}

	@Override
	public String toString() {
		return "RepositoryFolder " + getName() + (!isRootFolder() ? " in " + getParentFolder().getName() : " root") + " of "
				+ getResourceRepository();
	}

	public int getIndex() {
		if (getParentFolder() != null) {
			return getParentFolder().getChildren().indexOf(this);
		}
		return -1;
	}
}
