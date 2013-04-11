package org.openflexo.resource;

import java.util.List;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;

@ModelEntity
public interface PAMFlexoResource<RD> {

	public static final String CONTAINER = "container";
	public static final String CONTENTS = "contents";

	/**
	 * Returns the resource in which this resource is contained.
	 * 
	 * @return the container of this resource.
	 */
	@Getter(value = CONTAINER, inverse = CONTENTS)
	public PAMFlexoResource<?> getContainer();

	/**
	 * Sets the resource in which this resource is contained.
	 * 
	 * 
	 * @param resource
	 */
	@Setter(CONTAINER)
	public void setContainer(PAMFlexoResource<?> resource);

	/**
	 * Returns a list of resources contained by this resource.
	 * 
	 * @return the list of contained resources.
	 */
	@Getter(value = CONTENTS, cardinality = Cardinality.LIST, inverse = CONTAINER)
	public List<PAMFlexoResource<?>> getContents();

	/**
	 * Adds a resource to the contents.
	 * 
	 * @param resource
	 *            the resource to add
	 */
	@Adder(CONTENTS)
	public void addToContents(PAMFlexoResource<?> resource);

	/**
	 * Removes a resource from the contents.
	 * 
	 * @param resource
	 *            the resource to remove
	 */
	@Remover(CONTENTS)
	public void removeFromContents(PAMFlexoResource<?> resource);

}
