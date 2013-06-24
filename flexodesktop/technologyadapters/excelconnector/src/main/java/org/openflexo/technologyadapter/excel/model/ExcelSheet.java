package org.openflexo.technologyadapter.excel.model;

import org.apache.poi.ss.usermodel.Sheet;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;

public class ExcelSheet extends ExcelObject {

	private Sheet sheet;

	public Sheet getSheet() {
		return sheet;
	}

	public ExcelSheet(Sheet sheet, ExcelTechnologyAdapter adapter) {
		super(adapter);
		this.sheet = sheet;
	}

}
