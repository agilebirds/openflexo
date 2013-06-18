package org.openflexo.technologyadapter.excel.model;

import org.apache.poi.ss.usermodel.Sheet;
import org.openflexo.foundation.FlexoObject;

public class ExcelSheet extends FlexoObject{

	private Sheet sheet;
	
	public Sheet getSheet() {
		return sheet;
	}

	public ExcelSheet(Sheet sheet) {
		super();
		this.sheet = sheet;
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

}
