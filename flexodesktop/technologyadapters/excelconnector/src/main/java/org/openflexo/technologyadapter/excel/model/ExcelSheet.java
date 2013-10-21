package org.openflexo.technologyadapter.excel.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;

/**
 * Represents an Excel sheet, implemented as a wrapper of a POI sheet
 * 
 * @author vincent, sylvain
 * 
 */
public class ExcelSheet extends ExcelObject {
	private Sheet sheet;
	private ExcelWorkbook workbook;
	private List<ExcelRow> excelRows;

	private FormulaEvaluator evaluator;

	public Sheet getSheet() {
		return sheet;
	}

	public ExcelSheet(Sheet sheet, ExcelWorkbook workbook, ExcelTechnologyAdapter adapter) {
		super(adapter);
		this.sheet = sheet;
		this.workbook = workbook;
		excelRows = new ArrayList<ExcelRow>();
		evaluator = workbook.getWorkbook().getCreationHelper().createFormulaEvaluator();
	}

	public FormulaEvaluator getEvaluator() {
		return evaluator;
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
	public String getUri() {
		String uri = getWorkbook().getUri()+"Cell="+getName();
		return uri;
	}

}
