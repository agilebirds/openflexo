package org.openflexo.technologyadapter.excel.model;

import org.apache.poi.ss.usermodel.Workbook;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;
import org.openflexo.technologyadapter.excel.rm.ExcelWorkbookResource;

public class ExcelWorkbook extends ExcelObject implements ResourceData<ExcelWorkbook> {

	private Workbook workbook;
	private ExcelWorkbookResource resource;

	public Workbook getWorkbook() {
		return workbook;
	}

	public ExcelWorkbook(Workbook workbook, ExcelTechnologyAdapter adapter) {
		super(adapter);
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
