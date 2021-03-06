package org.openflexo.foundation.resource;

import java.util.List;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.IProgress;

/**
 * A FlexoResource is a resource that can be managed by OpenFlexo.
 * 
 * @param <RD>
 *            the type of the resource data reference by this resource
 * @author Guillaume
 * 
 */
@ModelEntity
@XMLElement
public interface FlexoResource<RD extends ResourceData<RD>> {

	public static final String NAME = "name";
	public static final String URI = "URI";
	public static final String VERSION = "version";
	public static final String REVISION = "revision";
	public static final String CONTAINER = "container";
	public static final String CONTENTS = "contents";
	public static final String DEPENDENCIES = "dependencies";
	public static final String LAST_UPDATE = "lastUpdate";

	/**
	 * Returns the name of this resource. The name of the resource is a displayable name that the end-user will understand. There are no
	 * restrictions on this value. Restrictions are resource implementation dependent.
	 * 
	 * @return the name of this resource.
	 */
	@Getter(NAME)
	@XMLAttribute()
	public String getName();

	/**
	 * Returns the unique resource identifier of this resource. A URI is unique in the whole universe and clearly and uniquely identifies
	 * this resource.
	 * 
	 * @return the unique resource identifier of this resource
	 */
	@Getter(URI)
	@XMLAttribute()
	public String getURI();

	/**
	 * Returns a displayable version that the end-user will understand.
	 * 
	 * @return a displayable version that the end-user will understand.
	 */
	@Getter(VERSION)
	@XMLAttribute
	public String getVersion();

	/**
	 * Returns the revision of this resource. Each resource should ensure that upon each time it is edited, the revision number is
	 * incremented. If a merge of two different revisions of the same resource must be made, the resulting revision should be the greatest
	 * revision of the merged resource incremented.
	 * 
	 * @return the revision of this resource.
	 */
	@Getter(REVISION)
	@XMLAttribute
	public Long getRevision();

	/**
	 * Returns the class of the resource data held by this resource.
	 * 
	 * @return the class of the resource data.
	 */
	public Class<RD> getResourceDataClass();

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
	@Getter(CONTAINER)
	public FlexoResource<?> getContainer();

	/**
	 * Returns a list of resources contained by this resource.
	 * 
	 * @return the list of contained resources.
	 */
	@Getter(value = CONTENTS, cardinality = Cardinality.LIST)
	public List<FlexoResource<?>> getContents();

	/**
	 * Adds a resource to the contents.
	 * 
	 * @param resource
	 *            the resource to add
	 */
	@Adder(CONTENTS)
	public void addToContents(FlexoResource<?> resource);

	/**
	 * Removes a resource from the contents.
	 * 
	 * @param resource
	 *            the resource to remove
	 */
	@Remover(CONTENTS)
	public void removeFromContents(FlexoResource<?> resource);

	/**
	 * Returns a list of resources required by this resource.
	 * 
	 * @return a list of resources required by this resource.
	 */
	@Getter(value = DEPENDENCIES, cardinality = Cardinality.LIST)
	public List<FlexoResource<?>> getDependencies();

	/**
	 * Adds a resource to the dependencies.
	 * 
	 * @param resource
	 *            the resource to add
	 */
	@Adder(DEPENDENCIES)
	public void addToDependencies(FlexoResource<?> resource);

	/**
	 * Removes a resource from the dependencies.
	 * 
	 * @param resource
	 *            the resource to remove
	 */
	@Remover(DEPENDENCIES)
	public void removeFromDependencies(FlexoResource<?> resource);

	/**
	 * Returns the &quot;real&quot; resource data of this resource. This may cause the loading of the resource data.
	 * 
	 * @param progress
	 *            a progress monitor in case the resource data is not immediately available.
	 * @return the resource data.
	 * @throws ResourceLoadingCancelledException
	 */
	public RD getResourceData(IProgress progress) throws ResourceLoadingCancelledException;

	/**
	 * This method updates the resource.
	 */
	public void update();
}
