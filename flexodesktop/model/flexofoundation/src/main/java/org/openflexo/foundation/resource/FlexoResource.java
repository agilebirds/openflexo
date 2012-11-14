package org.openflexo.foundation.resource;

import java.util.List;

/**
 * A FlexoResource is a resource that can be managed by OpenFlexo.
 * 
 * @author Guillaume
 * 
 */
public interface FlexoResource {

	/**
	 * Returns the name of this resource. The name of the resource is a displayable name that the end-user will understand. There are no
	 * restrictions on this value. Restrictions are resource implementation dependent.
	 * 
	 * @return the name of this resource.
	 */
	public String getName();

	/**
	 * Returns the unique resource identifier of this resource. A URI is unique in the whole universe and clearly and uniquely identifies
	 * this resource.
	 * 
	 * @return the unique resource identifier of this resource
	 */
	public String getURI();

	/**
	 * Returns a displayable version that the end-user will understand.
	 * 
	 * @return a displayable version that the end-user will understand.
	 */
	public String getVersion();

	/**
	 * Returns the revision of this resource. Each resource should ensure that upon each time it is edited, the revision number is
	 * incremented. If a merge of two different revisions of the same resource must be made, the resulting revision should be the greatest
	 * revision of the merged resource incremented.
	 * 
	 * @return the revision of this resource.
	 */
	public long getRevision();

	/**
	 * Indicates whether this resource can be edited or not. Returns <code>true</code> if the resource cannot be edited, else returns
	 * <code>false</code>.
	 * 
	 * @return <code>true</code> if the resource cannot be edited, else returns <code>false</code>.
	 */
	public boolean isReadOnly();

	/**
	 * Returns the resource in which this resource is contained.
	 * 
	 * @return the container of this resource.
	 */
	public FlexoResource getContainer();

	/**
	 * Returns a list of resources contained by this resource.
	 * 
	 * @return the list of contained resources.
	 */
	public List<FlexoResource> getContents();

	/**
	 * Returns a list of resources required by this resource.
	 * 
	 * @return a list of resources required by this resource.
	 */
	public List<FlexoResource> getDependencies();

}
