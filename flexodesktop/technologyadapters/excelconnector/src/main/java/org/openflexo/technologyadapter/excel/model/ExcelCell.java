package org.openflexo.technologyadapter.excel.model;

import org.apache.poi.ss.usermodel.Cell;
import org.openflexo.foundation.FlexoObject;

public class ExcelCell extends FlexoObject{

	private Cell cell;
	
	public Cell getCell() {
		return cell;
	}

	public ExcelCell(Cell cell) {
		super();
		this.cell = cell;
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

}
