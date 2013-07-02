package org.openflexo.technologyadapter.excel.model;

import org.apache.poi.ss.usermodel.Cell;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;

public class ExcelCell extends ExcelObject {

	private Cell cell;

	public Cell getCell() {
		return cell;
	}

	public ExcelCell(Cell cell, ExcelTechnologyAdapter adapter) {
		super(adapter);
		this.cell = cell;
	}

}
