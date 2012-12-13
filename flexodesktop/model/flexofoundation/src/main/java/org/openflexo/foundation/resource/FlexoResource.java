package org.openflexo.foundation.resource;

import java.io.FileNotFoundException;
import java.util.List;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.rm.FlexoResourceTree;
import org.openflexo.foundation.rm.LoadResourceException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.FlexoVersion;
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
@ImplementationClass(FlexoResourceImpl.class)
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
	 * Sets the name of this resource
	 * 
	 * @param aName
	 */
	@Setter(NAME)
	public void setName(String aName);

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
	 * Sets the unique resource identifier of this resource.
	 * 
	 * @param anURI
	 */
	@Setter(URI)
	public void setURI(String anURI);

	/**
	 * Returns a displayable version that the end-user will understand.
	 * 
	 * @return a displayable version that the end-user will understand.
	 */
	@Getter(value = VERSION, isStringConvertable = true)
	@XMLAttribute
	public FlexoVersion getVersion();

	/**
	 * Sets version for this resource.
	 * 
	 * @param anURI
	 */
	@Setter(VERSION)
	public void setVersion(FlexoVersion aVersion);

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
	@Getter(value = CONTAINER, inverse = CONTENTS)
	public FlexoResource<?> getContainer();

	/**
	 * Sets the resource in which this resource is contained.
	 * 
	 * 
	 * @param resource
	 */
	@Setter(CONTAINER)
	public void setContainer(FlexoResource<?> resource);

	/**
	 * Returns a list of resources contained by this resource.
	 * 
	 * @return the list of contained resources.
	 */
	@Getter(value = CONTENTS, cardinality = Cardinality.LIST, inverse = CONTAINER)
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
	 * Return flag indicating if this resource is loaded
	 * 
	 * @return
	 */
	public boolean isLoaded();

	/**
	 * Returns the &quot;real&quot; resource data of this resource. This may cause the loading of the resource data.
	 * 
	 * @param progress
	 *            a progress monitor in case the resource data is not immediately available.
	 * @return the resource data.
	 * @throws ResourceLoadingCancelledException
	 */
	public RD getResourceData(IProgress progress) throws ResourceLoadingCancelledException, ResourceDependencyLoopException,
			FileNotFoundException, FlexoException;

	/**
	 * Sets {@link ResourceData} for this resource
	 * 
	 * @param resourceData
	 */
	public void setResourceData(RD resourceData);

	/**
	 * Load the &quot;real&quot; load resource data of this resource.
	 * 
	 * @param progress
	 *            a progress monitor in case the resource data is not immediately available.
	 * @return the resource data.
	 * @throws ResourceLoadingCancelledException
	 * @throws ResourceDependencyLoopException
	 * @throws FileNotFoundException
	 * @throws FlexoException
	 */
	public RD loadResourceData(IProgress progress) throws ResourceLoadingCancelledException, ResourceDependencyLoopException,
			FileNotFoundException, FlexoException;

	/**
	 * Save the &quot;real&quot; resource data of this resource.
	 * 
	 * @throws SaveResourceException
	 */
	public void save(IProgress progress) throws SaveResourceException;

	/**
	 * This method updates the resource.
	 */
	public FlexoResourceTree update() throws ResourceDependencyLoopException, LoadResourceException, FileNotFoundException,
			ProjectLoadingCancelledException, FlexoException;

}
