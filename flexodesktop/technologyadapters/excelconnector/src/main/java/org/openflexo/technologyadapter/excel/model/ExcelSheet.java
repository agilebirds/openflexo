package org.openflexo.technologyadapter.excel.model;

import org.apache.poi.ss.usermodel.Sheet;

public class ExcelSheet extends ExcelObject {

	private Sheet sheet;

	public Sheet getSheet() {
		return sheet;
	}

	public ExcelSheet(Sheet sheet) {
		super();
		this.sheet = sheet;
	}

}
