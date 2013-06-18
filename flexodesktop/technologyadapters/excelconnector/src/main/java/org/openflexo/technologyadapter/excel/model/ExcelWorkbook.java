package org.openflexo.technologyadapter.excel.model;

import org.apache.poi.ss.usermodel.Workbook;
import org.openflexo.foundation.FlexoObject;

public class ExcelWorkbook extends FlexoObject{

	private Workbook workbook;
	
	public Workbook getWorkbook() {
		return workbook;
	}

	public ExcelWorkbook(Workbook workbook) {
		super();
		this.workbook = workbook;
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

}
