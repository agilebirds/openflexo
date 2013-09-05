package org.openflexo.technologyadapter.excel.model.io;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;
import org.openflexo.technologyadapter.excel.model.ExcelCell;
import org.openflexo.technologyadapter.excel.model.ExcelObject;
import org.openflexo.technologyadapter.excel.model.ExcelRow;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;

public class BasicExcelModelConverter {

	private static final Logger logger = Logger.getLogger(BasicExcelModelConverter.class.getPackage().getName());

	/** Excel Objects. */
	protected final Map<Object, ExcelObject> excelObjects = new HashMap<Object, ExcelObject>();

	/**
	 * Constructor.
	 */
	public BasicExcelModelConverter() {
	}

	/**
	 * Convert a Workbook into an Excel Workbook
	 */
	public ExcelWorkbook convertExcelWorkbook(Workbook workbook, ExcelTechnologyAdapter technologyAdapter) {
		ExcelWorkbook excelWorkbook = new ExcelWorkbook(workbook, this, technologyAdapter);
		excelObjects.put(workbook, excelWorkbook);
		for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
			Sheet sheet = workbook.getSheetAt(index);
			ExcelSheet excelSheet = convertExcelSheetToSheet(sheet, technologyAdapter);
			excelWorkbook.addExcelSheet(excelSheet);
		}
		return excelWorkbook;
	}

	/**
	 * Convert a Sheet into an Excel Sheet
	 */
	public ExcelSheet convertExcelSheetToSheet(Sheet sheet, ExcelTechnologyAdapter technologyAdapter) {
		ExcelSheet excelSheet = null;
		if (excelObjects.get(sheet) == null) {
			excelSheet = new ExcelSheet(sheet, technologyAdapter);
			excelObjects.put(sheet, excelSheet);
			for (Row row : sheet) {
				ExcelRow excelRow = convertExcelRowToRow(row, technologyAdapter);
			}
		} else {
			excelSheet = (ExcelSheet) excelObjects.get(sheet);
		}
		return excelSheet;
	}

	/**
	 * Convert a Row into an Excel Row
	 */
	public ExcelRow convertExcelRowToRow(Row row, ExcelTechnologyAdapter technologyAdapter) {
		ExcelRow excelRow;
		if (excelObjects.get(row) == null) {
			excelRow = new ExcelRow(row, technologyAdapter);
			excelObjects.put(row, excelRow);
			for (Cell cell : row) {
				ExcelCell excelCell = convertExcelCellToCell(cell, technologyAdapter);
			}
		} else {
			excelRow = (ExcelRow) excelObjects.get(row);
		}

		return excelRow;
	}

	/**
	 * Convert a Cell into an Excel Cell
	 */
	public ExcelCell convertExcelCellToCell(Cell cell, ExcelTechnologyAdapter technologyAdapter) {
		ExcelCell excelCell = null;
		if (excelObjects.get(cell) == null) {
			excelCell = new ExcelCell(cell, technologyAdapter);
			excelObjects.put(cell, excelCell);
		} else {
			excelCell = (ExcelCell) excelObjects.get(cell);
		}
		return excelCell;
	}

	/**
	 * Getter of excel objects.
	 * 
	 * @return the individuals value
	 */
	public Map<Object, ExcelObject> getExcelObjects() {
		return excelObjects;
	}

}
