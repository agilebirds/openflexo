package org.openflexo.technologyadapter.excel.model;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;

public abstract class ExcelObject extends FlexoObject implements TechnologyObject {

	private ExcelTechnologyAdapter technologyAdapter;

	public ExcelObject(ExcelTechnologyAdapter adapter) {
		super();
		technologyAdapter = adapter;
	}

	@Override
	public ExcelTechnologyAdapter getTechnologyAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		return null;
	}

}