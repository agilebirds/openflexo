package org.openflexo.technologyadapter.excel.model;

import org.apache.poi.ss.usermodel.Row;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;

public class ExcelRow extends ExcelObject {

	private Row row;

	public Row getRow() {
		return row;
	}

	public ExcelRow(Row row, ExcelTechnologyAdapter adapter) {
		super(adapter);
		this.row = row;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int getRowNum(){
		return row.getRowNum();
	}	

}
