package org.openflexo.technologyadapter.excel.model;

import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;

public class ExcelColumn extends ExcelObject {
	
	private int colNumber;

	public ExcelColumn(int colNumber, ExcelTechnologyAdapter adapter) {
		super(adapter);
		this.colNumber = colNumber;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Column"+colNumber;
	}

}
