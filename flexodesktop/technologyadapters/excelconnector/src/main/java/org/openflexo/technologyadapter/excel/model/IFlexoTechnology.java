package org.openflexo.technologyadapter.excel.model;

import java.util.List;

import org.openflexo.foundation.technologyadapter.TechnologyObject;

public interface IFlexoTechnology extends TechnologyObject, IFlexoTechnologyObjectContainer {

	public List<? extends TechnologyObject> getAccessibleTechnologyObject();

	public TechnologyObject getRootConcept();
}
