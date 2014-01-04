package org.openflexo.technologyadapter.excel.model;

import org.openflexo.foundation.DefaultFlexoObject;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;

public abstract class ExcelObject extends DefaultFlexoObject implements TechnologyObject<ExcelTechnologyAdapter> {

	private final ExcelTechnologyAdapter technologyAdapter;

	private String uri;

	public ExcelObject(ExcelTechnologyAdapter adapter) {
		super();
		technologyAdapter = adapter;
	}

	/**
	 * Name of Object.
	 * 
	 * @return
	 */
	public abstract String getName();

	@Override
	public ExcelTechnologyAdapter getTechnologyAdapter() {
		// TODO Auto-generated method stub
		return technologyAdapter;
	}

	public String getUri() {
		return uri;
	}

}
