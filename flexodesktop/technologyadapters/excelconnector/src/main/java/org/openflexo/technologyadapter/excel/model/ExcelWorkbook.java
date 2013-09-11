package org.openflexo.technologyadapter.excel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;
import org.openflexo.technologyadapter.excel.model.io.BasicExcelModelConverter;
import org.openflexo.technologyadapter.excel.rm.ExcelWorkbookResource;

public class ExcelWorkbook extends ExcelObject implements ResourceData<ExcelWorkbook>, IFlexoTechnology {

	private Workbook workbook;
	private ExcelWorkbookResource resource;
	private List<ExcelSheet> excelSheets;
	private BasicExcelModelConverter converter;
	private Map<ExcelProperty, ExcelPropertyValue> values = new HashMap<ExcelProperty, ExcelPropertyValue>();

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
		return getResource().getName();
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

	@Override
	public List<? extends TechnologyObject> getAccessibleTechnologyObject() {
		return getExcelSheets();
	}

	@Override
	public TechnologyObject getRootConcept() {
		return this;
	}

	@Override
	public List<? extends IFlexoTechnologyObjectContainer> getSubContainers() {
		return null;
	}

	@Override
	public List<? extends TechnologyObject> getConcepts() {
		return getExcelSheets();
	}

	@Override
	public List<? extends TechnologyObject> getTechnologyObjects() {
		return getExcelSheets();
	}

	@Override
	public List<? extends ExcelPropertyValue> getPropertyValues() {
		ArrayList<ExcelPropertyValue> returned = new ArrayList<ExcelPropertyValue>();
		returned.addAll(values.values());
		return returned;
	}

	@Override
	public ExcelPropertyValue getPropertyValue(ExcelProperty property) {
		return values.get(property);
	}

	@Override
	public ExcelPropertyValue addToPropertyValue(ExcelProperty property, Object newValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExcelPropertyValue removeFromPropertyValue(ExcelProperty property, Object valueToRemove) {
		// TODO Auto-generated method stub
		return null;
	}
}