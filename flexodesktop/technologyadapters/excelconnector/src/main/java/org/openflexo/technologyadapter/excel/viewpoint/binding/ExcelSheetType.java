package org.openflexo.technologyadapter.excel.viewpoint.binding;

import java.lang.reflect.Type;

import org.openflexo.antar.binding.CustomType;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;

public class ExcelSheetType implements CustomType {

	private ExcelSheet excelSheet;
	
	public ExcelSheetType(ExcelSheet excelSheet) {
		this.excelSheet = excelSheet;
	}

	public ExcelSheetType() {
		// TODO Auto-generated constructor stub
	}

	public ExcelSheet getExcelSheet() {
		return excelSheet;
	}
	
	@Override
	public String simpleRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String fullQualifiedRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class getBaseClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		return false;
	}

	
}