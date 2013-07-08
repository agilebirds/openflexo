package org.openflexo.technologyadapter.excel.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;
import org.openflexo.technologyadapter.excel.model.io.BasicExcelModelConverter;
import org.openflexo.technologyadapter.excel.rm.ExcelWorkbookResource;

public class ExcelWorkbook extends ExcelObject implements ResourceData<ExcelWorkbook> {

	private Workbook workbook;
	private ExcelWorkbookResource resource;
	private List<ExcelSheet> excelSheets;
	private BasicExcelModelConverter converter;

	public Workbook getWorkbook() {
		return workbook;
	}

	public ExcelWorkbook(Workbook workbook, ExcelTechnologyAdapter adapter) {
		super(adapter);
		this.workbook = workbook;
		excelSheets = new ArrayList<ExcelSheet>();
	}

	public ExcelWorkbook(Workbook workbook, BasicExcelModelConverter converter, ExcelTechnologyAdapter adapter) {
		super(adapter);
		this.workbook = workbook;
		this.converter = converter;
		excelSheets = new ArrayList<ExcelSheet>();
	}

	public BasicExcelModelConverter getConverter() {
		return converter;
	}

	@Override
	public FlexoResource<ExcelWorkbook> getResource() {
		return resource;
	}

	@Override
	public void setResource(FlexoResource<ExcelWorkbook> resource) {
		this.resource = (ExcelWorkbookResource) resource;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.getResource().getName();
	}

	public List<ExcelSheet> getExcelSheets() {
		return excelSheets;
	}

	public void setExcelSheets(List<ExcelSheet> excelSheets) {
		this.excelSheets = excelSheets;
	}

	public void addExcelSheet(ExcelSheet newExcelSheet) {
		this.excelSheets.add(newExcelSheet);
	}
}
