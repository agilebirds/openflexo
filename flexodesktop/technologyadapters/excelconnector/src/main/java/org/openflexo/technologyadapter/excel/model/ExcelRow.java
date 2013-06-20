package org.openflexo.technologyadapter.excel.model;

import org.apache.poi.ss.usermodel.Row;
import org.openflexo.foundation.FlexoObject;

public class ExcelRow extends FlexoObject{

	private Row row;

	public Row getRow() {
		return row;
	}

	public ExcelRow(Row row) {
		super();
		this.row = row;
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

}
