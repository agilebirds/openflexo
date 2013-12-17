/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.FlexoProjectObject;
import org.openflexo.foundation.InnerResourceData;
import org.openflexo.foundation.KVCFlexoObject;
import org.openflexo.foundation.ProjectData;
import org.openflexo.foundation.resource.FlexoProjectReference;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.ResourceLoadingListener;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.factory.EmbeddingType;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * 
 * TODO: actually support both FlexoResource implementation.<br>
 * Please review code when only new scheme will be implemented
 * 
 * 
 * @author sylvain
 * 
 * @param <O>
 */
public class FlexoModelObjectReference<O extends FlexoProjectObject> extends KVCFlexoObject implements
		StringConvertable<FlexoModelObjectReference<O>>, ResourceLoadingListener, PropertyChangeListener {

	private static final Logger logger = FlexoLogger.getLogger(FlexoModelObjectReference.class.getPackage().getName());

	public static interface ReferenceOwner {

		public void notifyObjectLoaded(FlexoModelObjectReference<?> reference);

		public void objectCantBeFound(FlexoModelObjectReference<?> reference);

		public void objectDeleted(FlexoModelObjectReference<?> reference);

		public void objectSerializationIdChanged(FlexoModelObjectReference<?> reference);

		public FlexoProject getProject();
	}

	public enum ReferenceStatus {
		RESOLVED, UNRESOLVED, NOT_FOUND, RESOURCE_NOT_FOUND, DELETED
	}

	private static final String SEPARATOR = "#";
	private static final String PROJECT_SEPARATOR = "|";
	private static final String ID_SEPARATOR = "_";

	/**
	 * @return
	 */
	public static String getSerializationRepresentationForObject(FlexoProjectObject modelObject, boolean serializeClassName) {

		if (modelObject instanceof InnerResourceData) {

			if (((InnerResourceData) modelObject).getResourceData() != null
					&& ((InnerResourceData) modelObject).getResourceData().getResource() != null) {
				return modelObject.getProject().getURI() + PROJECT_SEPARATOR
						+ ((InnerResourceData) modelObject).getResourceData().getResource().getURI() + SEPARATOR + SEPARATOR
						+ modelObject.getUserIdentifier() + ID_SEPARATOR + String.valueOf(modelObject.getFlexoID())
						+ (serializeClassName ? SEPARATOR + modelObject.getClass().getName() : "");
			}
		}

		return null;
	}

	private String resourceIdentifier;

	private String userIdentifier;

	private String className;

	private long flexoID;

	private String enclosingProjectIdentifier;

	/** The project of the referring object. */
	private FlexoProject referringProject;

	private ReferenceOwner owner;

	private O modelObject;

	private boolean serializeClassName = false;

	private ReferenceStatus status = ReferenceStatus.UNRESOLVED;

	private PamelaResource<?, ?> resource;

	private boolean deleted = false;
	private String modelObjectIdentifier;

	public FlexoModelObjectReference(O object, ReferenceOwner owner) {
		this(object);
		setOwner(owner);
	}

	public FlexoModelObjectReference(O object) {
		this.modelObject = object;
		this.modelObject.addToReferencers(this);
		this.status = ReferenceStatus.RESOLVED;
		/**
		 * We also initialize the string representation for the following reason: Let's say the user creates a reference to the given
		 * <code>object</code> and then later on, the user deletes that <code>object</code> but the reference owner does not remove its
		 * reference, we will have to serialize this without any data which will be a disaster (we should expect NullPointer,
		 * ArrayIndexOutOfBounds, etc...
		 */
		if (modelObject instanceof InnerResourceData && ((InnerResourceData) modelObject).getResourceData() != null) {
			if (((InnerResourceData) modelObject).getResourceData().getResource() != null) {
				this.resourceIdentifier = ((InnerResourceData) modelObject).getResourceData().getResource().getURI();
			} else {
				logger.warning("object " + modelObject + " has a resource data (" + ((InnerResourceData) modelObject).getResourceData()
						+ ") with null resource ");
			}
		} else {
			logger.warning("object " + modelObject + " has no resource data !");
		}
		this.userIdentifier = modelObject.getUserIdentifier();
		this.flexoID = modelObject.getFlexoID();
		this.className = modelObject.getClass().getName();
	}

	@Override
	public String toString() {
		return "FlexoModelObjectReference resource=" + resourceIdentifier + " modelObject=" + modelObject + " status=" + status + " owner="
				+ owner + " userIdentifier=" + userIdentifier + " className=" + className + " flexoID=" + flexoID;
	}

	public FlexoModelObjectReference(FlexoProject project, String modelObjectIdentifier) {
		this.referringProject = project;
		this.modelObjectIdentifier = modelObjectIdentifier;
		if (referringProject != null) {
			referringProject.addToObjectReferences(this);
		}
		try {
			int indexOf = modelObjectIdentifier.indexOf(PROJECT_SEPARATOR);
			if (indexOf > 0) {
				enclosingProjectIdentifier = modelObjectIdentifier.substring(0, indexOf);
				modelObjectIdentifier = modelObjectIdentifier.substring(indexOf + PROJECT_SEPARATOR.length());
			}
			String[] s = modelObjectIdentifier.split(SEPARATOR);
			this.resourceIdentifier = s[0];
			this.userIdentifier = s[1].substring(0, s[1].lastIndexOf(ID_SEPARATOR));
			this.flexoID = Long.valueOf(s[1].substring(s[1].lastIndexOf(ID_SEPARATOR) + ID_SEPARATOR.length()));
			if (s.length == 3) {
				this.className = s[2];
				serializeClassName = true;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public void delete() {
		delete(true);
	}

	public void delete(boolean notify) {
		if (!deleted) {
			deleted = true;
			if (getReferringProject() != null) {
				getReferringProject().removeObjectReferences(this);
			}
			// TODO: OLD FlexoResource scheme
			/*if (getResource(false) instanceof FlexoXMLStorageResource) {
				((FlexoXMLStorageResource) getResource(false)).removeResourceLoadingListener(this);
				((FlexoXMLStorageResource) getResource(false)).getPropertyChangeSupport().removePropertyChangeListener("name", this);
			}*/
			if (modelObject != null) {
				modelObject.removeFromReferencers(this);
			}
			if (owner != null) {
				owner.objectDeleted(this);
			}
			owner = null;
			modelObject = null;
		}
	}

	public O getObject() {
		return getObject(false);
	}

	public String getClassName() {
		return className;
	}

	public Class<O> getKlass() {
		if (getClassName() != null) {
			try {
				return (Class<O>) Class.forName(getClassName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (ClassCastException e) {
				e.printStackTrace();
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("There seems to be a problem in the code. Attempt to retrieve " + getClassName()
							+ " but was something else (see stacktrace)");
				}
			}
		}
		return null;
	}

	public O getObject(boolean force) {
		if (modelObject == null) {
			modelObject = findObject(force);
			if (modelObject != null) {
				modelObject.addToReferencers(this);
			}
			if (owner != null) {
				if (modelObject != null) {
					status = ReferenceStatus.RESOLVED;
					owner.notifyObjectLoaded(this);
				} else if (getResource(force) == null || getResource(force).isLoaded()
				// TODO: OLD FlexoResource scheme
				/*&& (!(getResource(force) instanceof FlexoXMLStorageResource) || !((FlexoXMLStorageResource) getResource(force))
						.getIsLoading())*/) {
					if (getResource(force) == null) {
						status = ReferenceStatus.RESOURCE_NOT_FOUND;
					} else {
						status = ReferenceStatus.NOT_FOUND;
					}
					if (force) {
						owner.objectCantBeFound(this);
					}
				}
			}
		}
		return modelObject;
	}

	private O findObjectInResource(PamelaResource<?, ?> resource) {
		try {
			// Ensure the resource is loaded
			ResourceData<?> resourceData = resource.getResourceData(null);

			List<Object> allObjects = resource.getFactory().getEmbeddedObjects(resourceData, EmbeddingType.CLOSURE);
			for (Object temp : allObjects) {
				if (temp instanceof FlexoObject) {
					FlexoObject o = (FlexoObject) temp;
					if (o.getFlexoID() == flexoID && o.getUserIdentifier().equals(userIdentifier)) {
						logger.info("Found object " + userIdentifier + "_" + flexoID + " : SUCCEEDED (is " + temp + ")");
						return (O) temp;
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ResourceLoadingCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FlexoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.warning("Cannot find object " + userIdentifier + "_" + flexoID + " in resource " + resource);
		return null;
	}

	private O findObject(boolean force) {
		if (getEnclosingProject(force) != null) {
			PamelaResource<?, ?> res = getResource(force);
			if (res == null) {
				return null;
			} else {
				return findObjectInResource(res);
			}
			// TODO: OLD FlexoResource scheme
			/*if (res instanceof FlexoXMLStorageResource) {
				if (force && !res.isLoaded()) {
					((FlexoXMLStorageResource) res).getXMLResourceData();
				}
				if (res.isLoaded() && !((FlexoXMLStorageResource) res).getIsLoading()) {
					return (O) getEnclosingProject(force).findObject(userIdentifier, flexoID);
				}
			} else {
				// TODO: New FlexoResource scheme
				try {
					res.getResourceData(null);
					if (res.isLoaded()) {
						return (O) getEnclosingProject(force).findObject(userIdentifier, flexoID);
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ResourceLoadingCancelledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ResourceDependencyLoopException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FlexoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
		}
		return null;
	}

	public PamelaResource<?, ?> getResource(boolean force) {
		if (resourceIdentifier == null || getEnclosingProject(force) == null) {
			return null;
		}
		if (resource == null) {
			FlexoProject enclosingProject = getEnclosingProject(force);
			if (enclosingProject != null) {
				resource = (PamelaResource<?, ?>) enclosingProject.getServiceManager().getResourceManager().getResource(resourceIdentifier);
			}
		}
		return resource;
	}

	public String getResourceIdentifier() {
		if (resource != null) {
			return resource.getURI();
		} else {
			return resourceIdentifier;
		}
	}

	@Override
	public Converter getConverter() {
		if (getReferringProject() != null) {
			return getReferringProject().getObjectReferenceConverter();
		}
		return null;
	}

	private FlexoProject getEnclosingProject(boolean force) {
		if (modelObject != null) {
			return modelObject.getProject();
		} else {
			if (enclosingProjectIdentifier != null) {
				if (getReferringProject() != null) {
					if (getReferringProject().getURI().equals(enclosingProjectIdentifier)) {
						return getReferringProject();
					}
					ProjectData data = getReferringProject().getProjectData();
					if (data != null) {
						FlexoProjectReference projectReference = data.getProjectReferenceWithURI(enclosingProjectIdentifier);
						if (projectReference != null) {
							return projectReference.getReferredProject(force);
						} else {
							return getReferringProject();
						}
					}
				}
			} else {
				return getReferringProject();
			}
			return null;
		}
	}

	public long getFlexoID() {
		return flexoID;
	}

	public String getEnclosingProjectIdentifier() {
		if (modelObject != null) {
			return modelObject.getProject().getProjectURI();
		} else {
			return enclosingProjectIdentifier;
		}
	}

	private FlexoProject getReferringProject() {
		if (owner != null) {
			return owner.getProject();
		} else {
			return referringProject;
		}
	}

	public ReferenceOwner getOwner() {
		return owner;
	}

	public void setOwner(ReferenceOwner owner) {
		if (this.owner != owner) {
			if (this.owner != null && this.owner.getProject() != null) {
				this.owner.getProject().removeObjectReferences(this);
			}
			this.owner = owner;
			if (this.owner != null && this.owner.getProject() != null) {
				this.owner.getProject().addToObjectReferences(this);
			} else {
				if (owner != null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("No project found for " + owner + " " + getStringRepresentation());
					}
				}
			}
		}
	}

	public String getStringRepresentation() {
		if (modelObject != null) {
			return getSerializationRepresentationForObject(modelObject, serializeClassName);
		} else {
			return (enclosingProjectIdentifier != null ? enclosingProjectIdentifier + PROJECT_SEPARATOR : "") + resourceIdentifier
					+ SEPARATOR + userIdentifier + ID_SEPARATOR + flexoID + (serializeClassName ? SEPARATOR + className : "");
		}
	}

	public void notifyObjectDeletion() {
		status = ReferenceStatus.DELETED;
		try {
			if (getOwner() != null) {
				getOwner().objectDeleted(this);
			}
		} finally {
			modelObject = null;// If this is not done, we may end-up with memory leaks.
		}
	}

	public void notifySerializationIdHasChanged() {
		try {
			if (getOwner() != null) {
				getOwner().objectSerializationIdChanged(this);
			}
		} finally {
			modelObject = null;// If this is not done, we may end-up with memory leaks.
		}
	}

	@Override
	public void notifyResourceHasBeenLoaded(FlexoResource<?> resource) {
		findObject(false);
	}

	@Override
	public void notifyResourceWillBeLoaded(FlexoResource<?> resource) {
		// Nothing to do
	}

	/**
	 * Tells wheter the class name of the referenced model object should be serialized or not.
	 * 
	 * @return true if the class name of the referenced model object should be serialized, false otherwise.
	 */
	public boolean getSerializeClassName() {
		return serializeClassName;
	}

	/**
	 * Sets wheter the class name of the referenced model object should be serialized or not.
	 */
	public void setSerializeClassName(boolean serializeClassName) {
		this.serializeClassName = serializeClassName;
	}

	public ReferenceStatus getStatus() {
		if (getResource(false) == null && (status == ReferenceStatus.RESOLVED || status == ReferenceStatus.UNRESOLVED)) {
			status = ReferenceStatus.RESOURCE_NOT_FOUND;
		}
		return status;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == resource && "name".equals(evt.getPropertyName())) {
			resourceIdentifier = resource.getURI();
			if (getOwner() != null) {
				getOwner().objectSerializationIdChanged(this);
			}
		}
	}

	public void _setEnclosingProjectIdentifier(String uri) {
		if (enclosingProjectIdentifier == null) {
			enclosingProjectIdentifier = uri;
			if (getOwner() != null) {
				getOwner().objectSerializationIdChanged(this);
			}
		}
	}
}
