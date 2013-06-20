package org.openflexo.technologyadapter.excel.model;

import org.apache.poi.ss.usermodel.Row;

public class ExcelRow extends ExcelObject {

	private Row row;

	public Row getRow() {
		return row;
	}

	public ExcelRow(Row row) {
		super();
		this.row = row;
	}

}
