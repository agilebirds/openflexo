package org.openflexo.technologyadapter.excel.model.io;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
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
			ExcelSheet excelSheet = convertExcelSheetToSheet(sheet, excelWorkbook, technologyAdapter);
			excelWorkbook.addToExcelSheets(excelSheet);
		}
		return excelWorkbook;
	}

	/**
	 * Convert a Sheet into an Excel Sheet
	 */
	public ExcelSheet convertExcelSheetToSheet(Sheet sheet, ExcelWorkbook workbook, ExcelTechnologyAdapter technologyAdapter) {
		ExcelSheet excelSheet = null;
		if (excelObjects.get(sheet) == null) {
			excelSheet = new ExcelSheet(sheet, workbook, technologyAdapter);
			excelObjects.put(sheet, excelSheet);
			int lastRow = -1;
			for (Row row : sheet) {
				while (row.getRowNum() > lastRow + 1) {
					// Missing row
					ExcelRow excelRow = new ExcelRow(null, excelSheet, technologyAdapter);
					excelSheet.addToExcelRows(excelRow);
					lastRow++;
				}
				ExcelRow excelRow = convertExcelRowToRow(row, excelSheet, technologyAdapter);
				excelSheet.addToExcelRows(excelRow);
				lastRow = excelRow.getRowNum();
			}
			for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
				CellRangeAddress cellRange = sheet.getMergedRegion(i);
				for (int row = cellRange.getFirstRow(); row <= cellRange.getLastRow(); row++) {
					for (int col = cellRange.getFirstColumn(); col <= cellRange.getLastColumn(); col++) {
						excelSheet.getCellAt(row, col).merge(cellRange);
					}
				}
			}
		} else {
			excelSheet = (ExcelSheet) excelObjects.get(sheet);
		}
		return excelSheet;
	}

	/**
	 * Convert a Row into an Excel Row
	 */
	public ExcelRow convertExcelRowToRow(Row row, ExcelSheet excelSheet, ExcelTechnologyAdapter technologyAdapter) {
		ExcelRow excelRow;
		if (excelObjects.get(row) == null) {
			excelRow = new ExcelRow(row, excelSheet, technologyAdapter);
			excelObjects.put(row, excelRow);
			int lastCell = -1;
			for (Cell cell : row) {
				// System.out.println("Adding cell " + cell.getColumnIndex());
				while (cell.getColumnIndex() > lastCell + 1) {
					// Missing cell
					// System.out.println("Adding a missing cell");
					ExcelCell excelCell = new ExcelCell(null, excelRow, technologyAdapter);
					excelRow.addToExcelCells(excelCell);
					lastCell++;
				}
				ExcelCell excelCell = convertExcelCellToCell(cell, excelRow, technologyAdapter);
				excelRow.addToExcelCells(excelCell);
				lastCell = excelCell.getColumnIndex();
			}
			// System.out.println("Created a row with " + excelRow.getExcelCells().size() + " cells");
			/*int i = 0;
			for (ExcelCell cell : excelRow.getExcelCells()) {
				System.out.println("Index " + i + ": Cell with " + cell.getCell()
						+ (cell.getCell() != null ? " index=" + cell.getCell().getColumnIndex() : "n/a"));
				i++;

			}*/
		} else {
			excelRow = (ExcelRow) excelObjects.get(row);
		}

		return excelRow;
	}

	/**
	 * Convert a Cell into an Excel Cell
	 */
	public ExcelCell convertExcelCellToCell(Cell cell, ExcelRow excelRow, ExcelTechnologyAdapter technologyAdapter) {
		ExcelCell excelCell = null;
		if (excelObjects.get(cell) == null) {
			excelCell = new ExcelCell(cell, excelRow, technologyAdapter);
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
