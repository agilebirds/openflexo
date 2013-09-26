package org.openflexo.technologyadapter.excel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;

/**
 * Represents an Excel sheet, implemented as a wrapper of a POI sheet
 * 
 * @author vincent, sylvain
 * 
 */
public class ExcelSheet extends ExcelObject {

	private Map<ExcelProperty, ExcelPropertyValue> values = new HashMap<ExcelProperty, ExcelPropertyValue>();

	private Sheet sheet;
	private ExcelWorkbook workbook;
	private List<ExcelRow> excelRows;

	public Sheet getSheet() {
		return sheet;
	}

	public ExcelSheet(Sheet sheet, ExcelWorkbook workbook, ExcelTechnologyAdapter adapter) {
		super(adapter);
		this.sheet = sheet;
		this.workbook = workbook;
		excelRows = new ArrayList<ExcelRow>();
		addToPropertyValue(new ExcelProperty("Name", adapter), sheet.getSheetName());
	}

	@Override
	public String getName() {
		return sheet.getSheetName();
	}

	public ExcelWorkbook getWorkbook() {
		return workbook;
	}

	public List<ExcelRow> getExcelRows() {
		return excelRows;
	}

	public void setExcelRows(List<ExcelRow> excelRows) {
		this.excelRows = excelRows;
	}

	public void addToExcelRows(ExcelRow newExcelRow) {
		this.excelRows.add(newExcelRow);
	}

	public void removeFromExcelRows(ExcelRow deletedExcelRow) {
		this.excelRows.remove(deletedExcelRow);
	}

	public int getMaxColNumber() {
		int returned = 0;
		for (ExcelRow row : getExcelRows()) {
			if (row.getRow() != null && row.getRow().getLastCellNum() > returned) {
				returned = row.getRow().getLastCellNum();
			}
		}
		return returned;
	}

	public ExcelRow getRowAt(int row) {
		if (row < 0) {
			return null;
		}
		// Append missing rows
		while (getExcelRows().size() <= row) {
			addToExcelRows(new ExcelRow(null, this, getTechnologyAdapter()));
		}
		return getExcelRows().get(row);
	}

	public ExcelCell getCellAt(int row, int column) {
		if (row < 0) {
			return null;
		}
		if (column < 0) {
			return null;
		}
		return getRowAt(row).getCellAt(column);
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
