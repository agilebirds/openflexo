package org.openflexo.technologyadapter.excel.model;

import openflexo.technologyadapter.excel.rm.ExcelWorkbookResource;

import org.apache.poi.ss.usermodel.Workbook;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceData;

public class ExcelWorkbook extends ExcelObject implements ResourceData<ExcelWorkbook> {

	private Workbook workbook;
	private ExcelWorkbookResource resource;

	public Workbook getWorkbook() {
		return workbook;
	}

	public ExcelWorkbook(Workbook workbook) {
		super();
		this.workbook = workbook;
	}

	@Override
	public FlexoResource<ExcelWorkbook> getResource() {
		return resource;
	}

	@Override
	public void setResource(FlexoResource<ExcelWorkbook> resource) {
		this.resource = (ExcelWorkbookResource) resource;
	}

}
