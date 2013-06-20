package org.openflexo.technologyadapter.excel.model;

import org.apache.poi.ss.usermodel.Cell;

public class ExcelCell extends ExcelObject {

	private Cell cell;

	public Cell getCell() {
		return cell;
	}

	public ExcelCell(Cell cell) {
		super();
		this.cell = cell;
	}

}
