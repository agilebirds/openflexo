package org.openflexo.technologyadapter.excel.model;

import java.util.List;

import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;

public interface IFlexoTechnology extends TechnologyObject<ExcelTechnologyAdapter>, IFlexoTechnologyObjectContainer {

	public List<? extends TechnologyObject<ExcelTechnologyAdapter>> getAccessibleTechnologyObject();

	public TechnologyObject<ExcelTechnologyAdapter> getRootConcept();
}
