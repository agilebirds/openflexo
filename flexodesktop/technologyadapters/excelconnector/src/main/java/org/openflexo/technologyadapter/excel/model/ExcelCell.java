package org.openflexo.technologyadapter.excel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;

/**
 * Represents an Excel cell, implemented as a wrapper of a POI Cell
 * 
 * @author vincent, sylvain
 * 
 */
public class ExcelCell extends ExcelObject {

	private Map<ExcelProperty, ExcelPropertyValue> values = new HashMap<ExcelProperty, ExcelPropertyValue>();

	private Cell cell;
	private ExcelRow excelRow;

	private CellRangeAddress cellRange = null;

	public enum CellType {
		Blank, Numeric, String, NumericFormula, StringFormula, Boolean, Error, Empty, Unknown
	}

	public Cell getCell() {
		return cell;
	}

	public ExcelCell(Cell cell, ExcelRow excelRow, ExcelTechnologyAdapter adapter) {
		super(adapter);
		this.cell = cell;
		this.excelRow = excelRow;
	}

	public ExcelRow getExcelRow() {
		return excelRow;
	}

	public ExcelSheet getExcelSheet() {
		return getExcelRow().getExcelSheet();
	}

	public int getColumnIndex() {
		if (cell != null) {
			return cell.getColumnIndex();
		} else {
			return -1;
		}
	}

	public int getRowIndex() {
		if (cell != null) {
			return cell.getRowIndex();
		} else {
			return -1;
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ROW:" + cell.getRowIndex() + "/" + "COL:" + cell.getColumnIndex();
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

	public void merge(CellRangeAddress cellRange) {
		this.cellRange = cellRange;
	}

	/**
	 * Indicated if this cell is merged
	 * 
	 * @return
	 */
	public boolean isMerged() {
		return cellRange != null;
	}

	/**
	 * Return the list of cells with which this cell has been merged
	 * 
	 * @return
	 */
	public List<ExcelCell> getMergedCells() {
		if (isMerged()) {
			List<ExcelCell> returned = new ArrayList<ExcelCell>();
			for (int row = cellRange.getFirstRow(); row <= cellRange.getLastRow(); row++) {
				for (int col = cellRange.getFirstColumn(); col <= cellRange.getLastColumn(); col++) {
					returned.add(getExcelSheet().getCellAt(row, col));
				}
			}
			return returned;
		} else {
			return null;
		}
	}

	/**
	 * Return the top left merged cell
	 * 
	 * @return
	 */
	public ExcelCell getTopLeftMergedCell() {
		if (isMerged()) {
			return getExcelSheet().getCellAt(cellRange.getFirstRow(), cellRange.getFirstColumn());
		}
		return null;
	}

	/**
	 * Return the top right merged cell
	 * 
	 * @return
	 */
	public ExcelCell getTopRightMergedCell() {
		if (isMerged()) {
			return getExcelSheet().getCellAt(cellRange.getFirstRow(), cellRange.getLastColumn());
		}
		return null;
	}

	/**
	 * Return the bottom left merged cell
	 * 
	 * @return
	 */
	public ExcelCell getBottomLeftMergedCell() {
		if (isMerged()) {
			return getExcelSheet().getCellAt(cellRange.getLastRow(), cellRange.getFirstColumn());
		}
		return null;
	}

	/**
	 * Return the bottom right merged cell
	 * 
	 * @return
	 */
	public ExcelCell getBottomRightMergedCell() {
		if (isMerged()) {
			return getExcelSheet().getCellAt(cellRange.getLastRow(), cellRange.getLastColumn());
		}
		return null;
	}

	/**
	 * Return the merged cell located at the top and at same column
	 * 
	 * @return
	 */
	public ExcelCell getTopMergedCell() {
		if (isMerged()) {
			return getExcelSheet().getCellAt(cellRange.getFirstRow(), getColumnIndex());
		}
		return null;
	}

	/**
	 * Return the merged cell located at the bottom and at same column
	 * 
	 * @return
	 */
	public ExcelCell getBottomMergedCell() {
		if (isMerged()) {
			return getExcelSheet().getCellAt(cellRange.getLastRow(), getColumnIndex());
		}
		return null;
	}

	/**
	 * Return the merged cell located at the left and at same row
	 * 
	 * @return
	 */
	public ExcelCell getLeftMergedCell() {
		if (isMerged()) {
			return getExcelSheet().getCellAt(getRowIndex(), cellRange.getFirstColumn());
		}
		return null;
	}

	/**
	 * Return the merged cell located at the right and at same row
	 * 
	 * @return
	 */
	public ExcelCell getRightMergedCell() {
		if (isMerged()) {
			return getExcelSheet().getCellAt(getRowIndex(), cellRange.getLastColumn());
		}
		return null;
	}

	/**
	 * Return the cell located at the top of this cell.<br>
	 * If cell is merged, return first non-merged cell located at the top of this cell
	 * 
	 * @return
	 */
	public ExcelCell getUpperCell() {
		if (isMerged()) {
			return getExcelSheet().getCellAt(cellRange.getFirstRow() - 1, getColumnIndex());
		}
		return getExcelSheet().getCellAt(getRowIndex() - 1, getColumnIndex());
	}

	/**
	 * Return the cell located at the bottom of this cell.<br>
	 * If cell is merged, return first non-merged cell located at the bottom of this cell
	 * 
	 * @return
	 */
	public ExcelCell getLowerCell() {
		if (isMerged()) {
			return getExcelSheet().getCellAt(cellRange.getLastRow() + 1, getColumnIndex());
		}
		return getExcelSheet().getCellAt(getRowIndex() + 1, getColumnIndex());
	}

	/**
	 * Return the cell located at the left of this cell.<br>
	 * If cell is merged, return first non-merged cell located at the left of this cell
	 * 
	 * @return
	 */
	public ExcelCell getPreviousCell() {
		if (isMerged()) {
			return getExcelSheet().getCellAt(getRowIndex(), cellRange.getFirstColumn() - 1);
		}
		return getExcelSheet().getCellAt(getRowIndex(), getColumnIndex() - 1);
	}

	/**
	 * Return the cell located at the right of this cell.<br>
	 * If cell is merged, return first non-merged cell located at the right of this cell
	 * 
	 * @return
	 */
	public ExcelCell getNextCell() {
		if (isMerged()) {
			return getExcelSheet().getCellAt(getRowIndex(), cellRange.getLastColumn() + 1);
		}
		return getExcelSheet().getCellAt(getRowIndex(), getColumnIndex() + 1);
	}

	/**
	 * Return type of this cell
	 * 
	 * @return
	 */
	public CellType getCellType() {
		if (cell == null) {
			return CellType.Empty;
		}
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			return CellType.Blank;
		case Cell.CELL_TYPE_NUMERIC:
			return CellType.Numeric;
		case Cell.CELL_TYPE_STRING:
			return CellType.String;
		case Cell.CELL_TYPE_FORMULA:
			try {
				cell.getNumericCellValue();
				return CellType.NumericFormula;
			} catch (IllegalStateException e1) {
				try {
					cell.getStringCellValue();
					return CellType.StringFormula;
				} catch (IllegalStateException e2) {
					return CellType.Unknown;
				}
			}
		case Cell.CELL_TYPE_BOOLEAN:
			return CellType.Boolean;
		case Cell.CELL_TYPE_ERROR:
			return CellType.Error;
		default:
			return CellType.Unknown;
		}
	}

	/**
	 * Return the value to be displayed in a generic viewer
	 * 
	 * @return
	 */
	public String getDisplayValue() {
		Object o = getCellValue();
		if (o == null)
			return "";
		if (getCellType() == CellType.Error) {
			return "!Error:" + o;
		}
		return o.toString();
	}

	/**
	 * Return the value (set or computed) associated with this cell
	 * 
	 * @return
	 */
	public Object getCellValue() {
		switch (getCellType()) {
		case Blank:
			return null;
		case Boolean:
			return cell.getBooleanCellValue();
		case Numeric:
			return cell.getNumericCellValue();
		case NumericFormula:
			return cell.getNumericCellValue();
		case String:
			return cell.getStringCellValue();
		case StringFormula:
			return cell.getStringCellValue();
		case Empty:
			return null;
		case Error:
			return cell.getErrorCellValue();
		case Unknown:
			return "???";
		default:
			return "????";
		}
	};

	/**
	 * Return a String identifying this cell (eg. (0,0) will return "A1")
	 * 
	 * @return
	 */
	public String getCellIdentifier() {
		return "" + Character.toChars(getColumnIndex() + 65)[0] + (getRowIndex() + 1);
	}

	/**
	 * Return string representation for this cell (debug)
	 */
	@Override
	public String toString() {
		return "["
				+ getCellIdentifier()
				+ "]/"
				+ getCellType().name()
				+ "/"
				+ (isMerged() ? "MergedWith:" + "[" + getTopLeftMergedCell().getCellIdentifier() + ":"
						+ getBottomRightMergedCell().getCellIdentifier() + "]" + "/" : "") + getDisplayValue();
	}

	public boolean hasTopBorder() {
		if ((cell != null && cell.getCellStyle().getBorderTop() != CellStyle.BORDER_NONE)) {
			return true;
		}
		if (isMerged()) {
			return getTopMergedCell().cell != null && getTopMergedCell().cell.getCellStyle().getBorderTop() != CellStyle.BORDER_NONE;
		}
		return false;
	}

	public boolean hasLeftBorder() {
		if (cell != null && cell.getCellStyle().getBorderLeft() != CellStyle.BORDER_NONE) {
			return true;
		}
		if (isMerged()) {
			return getLeftMergedCell().cell != null && getLeftMergedCell().cell.getCellStyle().getBorderLeft() != CellStyle.BORDER_NONE;
		}
		return false;
	}

	public boolean hasRightBorder() {
		if (cell != null && cell.getCellStyle().getBorderRight() != CellStyle.BORDER_NONE) {
			return true;
		}
		if (isMerged()) {
			return getRightMergedCell().cell != null && getRightMergedCell().cell.getCellStyle().getBorderRight() != CellStyle.BORDER_NONE;
		}
		return false;
	}

	public boolean hasBottomBorder() {
		if (cell != null && cell.getCellStyle().getBorderBottom() != CellStyle.BORDER_NONE) {
			return true;
		}
		if (isMerged()) {
			return getBottomMergedCell().cell != null
					&& getBottomMergedCell().cell.getCellStyle().getBorderBottom() != CellStyle.BORDER_NONE;
		}
		return false;
	}

}
