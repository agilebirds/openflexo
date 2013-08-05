package org.openflexo.technologyadapter.excel.model;

import java.util.List;

import org.openflexo.foundation.technologyadapter.TechnologyObject;

public interface IFlexoTechnologyObjectContainer {

	/**
	 * Sub container of container.
	 * 
	 * @return
	 */
	public List<? extends IFlexoTechnologyObjectContainer> getSubContainers();

	/**
	 * Concepts defined by Ontology.
	 * 
	 * @return
	 */
	public List<? extends TechnologyObject> getConcepts();

	/**
	 * Return all flexoObjects explicitely defined in this container
	 * 
	 * @return
	 */
	public List<? extends TechnologyObject> getTechnologyObjects();

}
