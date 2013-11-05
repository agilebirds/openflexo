package org.openflexo.technologyadapter.excel.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;

/**
 * Represents an Excel cell, implemented as a wrapper of a POI Cell
 * 
 * @author vincent, sylvain
 * 
 */
public class ExcelCell extends ExcelObject {

	static final Logger logger = Logger.getLogger(ExcelCell.class.getPackage().getName());

	private Cell cell;
	private ExcelRow excelRow;

	private CellRangeAddress cellRange = null;

	public enum CellType {
		Blank, Numeric, String, NumericFormula, StringFormula, Boolean, Error, Empty, Unknown
	}
	
	public enum CellStyleFeature {
		Foreground, Background, Pattern, Alignment, Font, BorderBottom, BorderTop, BorderLeft, BorderRight
	}
	
	public enum CellBorderStyleFeature {
		BORDER_DASH_DOT, BORDER_DASH_DOT_DOT, BORDER_DASHED, BORDER_DOTTED, BORDER_DOUBLE, 
		BORDER_HAIR, BORDER_MEDIUM, BORDER_MEDIUM_DASH_DOT, BORDER_MEDIUM_DASH_DOT_DOT, BORDER_MEDIUM_DASHED,
		BORDER_NONE, BORDER_SLANTED_DASH_DOT, BORDER_THICK, BORDER_THIN
	}

	public enum CellAlignmentStyleFeature {
		ALIGN_CENTER, ALIGN_CENTER_SELECTION, ALIGN_FILL, ALIGN_GENERAL, ALIGN_JUSTIFY, ALIGN_LEFT, ALIGN_RIGHT,
		VERTICAL_BOTTOM, VERTICAL_CENTER, VERTICAL_JUSTIFY, VERTICAL_TOP
	}
	
	public enum CellFontStyleFeature {
		ANSI_CHARSET, BOLDWEIGHT_BOLD, BOLDWEIGHT_NORMAL, COLOR_NORMAL, COLOR_RED, DEFAULT_CHARSET, SS_NONE, SS_SUB, SS_SUPER, SYMBOL_CHARSET, U_DOUBLE, U_DOUBLE_ACCOUNTING, U_NONE, U_SINGLE, U_SINGLE_ACCOUNTING
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
			return getExcelRow().getExcelCells().indexOf(this);
		}
	}

	public int getRowIndex() {
		if (cell != null) {
			return cell.getRowIndex();
		} else {
			return getExcelRow().getRowIndex();
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ROW:" + cell.getRowIndex() + "/" + "COL:" + cell.getColumnIndex();
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

	private static final DataFormatter FORMATTER = new DataFormatter();

	/**
	 * Return the value to be displayed in a generic viewer
	 * 
	 * @return
	 */
	public String getDisplayValue() {
		try {
			return FORMATTER.formatCellValue(cell, getExcelSheet().getEvaluator());
		} catch (RuntimeException e) {
			return "!ERROR: " + e.getMessage();
		}
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
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			}
			return cell.getNumericCellValue();
		case NumericFormula:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			}
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

	private void setCellFormula(String formula) {
		if (formula.startsWith("=")) {
			formula = formula.substring(formula.indexOf("=") + 1);
		}
		try {
			cell.setCellFormula(formula);
		} catch (IllegalArgumentException e) {
			logger.warning("Cannot parse forumla: " + formula);
		}
		getExcelSheet().getEvaluator().clearAllCachedResultValues();
	}

	private CellValue evaluateFormula(){
		return getExcelSheet().getEvaluator().evaluate(cell);
	}
	
	protected void createCellWhenNonExistant() {
		if (cell == null) {
			getExcelRow().createRowWhenNonExistant();
			cell = getExcelRow().getRow().createCell(getColumnIndex());
		}
	}

	public void setCellValue(String value) {

		createCellWhenNonExistant();

		if (value.startsWith("=")) {
			setCellFormula(value);
			setCellValue(evaluateFormula().formatAsString());
			return;
		}
		if (value.equalsIgnoreCase("true")) {
			cell.setCellValue(true);
			getExcelSheet().getEvaluator().clearAllCachedResultValues();
			return;
		} else if (value.equalsIgnoreCase("false")) {
			cell.setCellValue(false);
			getExcelSheet().getEvaluator().clearAllCachedResultValues();
			return;
		}
		try {
			double doubleValue = Double.parseDouble(value);
			cell.setCellValue(doubleValue);
			getExcelSheet().getEvaluator().clearAllCachedResultValues();
			return;
		} catch (NumberFormatException e) {
			cell.setCellValue(value);
			getExcelSheet().getEvaluator().clearAllCachedResultValues();
			return;
		}
	}

	/**
	 * Return the specification of this cell
	 * 
	 * @return
	 */
	public String getDisplayCellSpecification() {
		try {
			if (getCellType() == CellType.NumericFormula || getCellType() == CellType.StringFormula) {
				return "=" + FORMATTER.formatCellValue(cell);
			}
			return FORMATTER.formatCellValue(cell);
		} catch (RuntimeException e) {
			return "!ERROR: " + e.getMessage();
		}
	};

	/**
	 * Return a String identifying this cell (eg. (0,0) will return "A1")
	 * 
	 * @return
	 */
	public String getCellIdentifier() {
		return CellReference.convertNumToColString(getColumnIndex());
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

	public void setCellStyle(HSSFCellStyle style) {
		if (getCell() != null) {
			getCell().setCellStyle(style);
		}
	}
	
	public HSSFCellStyle getCellStyle() {
		if (getCell() != null) {
			return (HSSFCellStyle) getCell().getCellStyle();
		}
		return null;
	}
	
	public void setCellStyle(CellStyleFeature cellStyle, Object value) {
		if (getCell() != null && cellStyle!=null) {
			
			// First get the old style
			HSSFCellStyle oldStyle = getCellStyle();
			// Then create a new style
			HSSFCellStyle newStyle = (HSSFCellStyle) getExcelSheet().getWorkbook().getWorkbook().createCellStyle();
			// Apply the old parameters to the new style
			newStyle.cloneStyleFrom(oldStyle);
			// Then apply the new parameter to the new style
			switch (cellStyle) {
			case Alignment:
				newStyle.setAlignment(getPOIAlignmentStyle((CellAlignmentStyleFeature)value));
				break;
			case Font:
				newStyle.setFont((Font)value);
				break;
			case BorderBottom:
				newStyle.setBorderBottom(getPOIBorderStyle((CellBorderStyleFeature)value));
				break;
			case BorderLeft:
				newStyle.setBorderLeft(getPOIBorderStyle((CellBorderStyleFeature)value));
				break;
			case BorderRight:
				newStyle.setBorderRight(getPOIBorderStyle((CellBorderStyleFeature)value));
				break;
			case BorderTop:
				newStyle.setBorderTop(getPOIBorderStyle((CellBorderStyleFeature)value));
				break;
			case Foreground:
				if(value instanceof String){
					newStyle.setFillForegroundColor(Short.parseShort((String)value));
				}
				if(value instanceof Long){
					newStyle.setFillForegroundColor(((Long)value).shortValue());
				}
				else{
					break;
				}
			case Background:
				if(value instanceof Long){
					newStyle.setFillForegroundColor(((Long)value).shortValue());
					newStyle.setFillBackgroundColor(((Long)value).shortValue());
					newStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				}
				else{
					break;
				}
			default:
				break;
			}
			// Set the style of this cell to the new style
			getCell().setCellStyle(newStyle);
		}
		return;
	}
	
	private Short getPOIBorderStyle(CellBorderStyleFeature borderStyle){
		if(borderStyle.name().equals("BORDER_DASH_DOT")){
			return CellStyle.BORDER_DASH_DOT;
		}else if(borderStyle.name().equals("BORDER_DASH_DOT_DOT")){
			return CellStyle.BORDER_DASH_DOT_DOT;
		}else if(borderStyle.name().equals("BORDER_DASHED")){
			return CellStyle.BORDER_DASHED;
		}else if(borderStyle.name().equals("BORDER_DOTTED")){
			return CellStyle.BORDER_DOTTED;
		}else if(borderStyle.name().equals("BORDER_DOUBLE")){
			return CellStyle.BORDER_DOUBLE;
		}else if(borderStyle.name().equals("BORDER_HAIR")){
			return CellStyle.BORDER_HAIR;
		}else if(borderStyle.name().equals("BORDER_MEDIUM")){
			return CellStyle.BORDER_MEDIUM;
		}else if(borderStyle.name().equals("BORDER_MEDIUM_DASH_DOT")){
			return CellStyle.BORDER_MEDIUM_DASH_DOT;
		}else if(borderStyle.name().equals("BORDER_MEDIUM_DASH_DOT_DOT")){
			return CellStyle.BORDER_MEDIUM_DASH_DOT_DOT;
		}else if(borderStyle.name().equals("BORDER_MEDIUM_DASHED")){
			return CellStyle.BORDER_MEDIUM_DASHED;
		}else if(borderStyle.name().equals("BORDER_NONE")){
			return CellStyle.BORDER_NONE;
		}else if(borderStyle.name().equals("BORDER_SLANTED_DASH_DOT")){
			return CellStyle.BORDER_SLANTED_DASH_DOT;
		}else if(borderStyle.name().equals("BORDER_THICK")){
			return CellStyle.BORDER_THICK;
		}else if(borderStyle.name().equals("BORDER_THIN")){
			return CellStyle.BORDER_THIN;
		}
		return null;
	}
	
	private Short getPOIAlignmentStyle(CellAlignmentStyleFeature alignmentStyle){
		if(alignmentStyle.name().equals("ALIGN_CENTER")){
			return CellStyle.ALIGN_CENTER;
		}else if(alignmentStyle.name().equals("ALIGN_CENTER_SELECTION")){
			return CellStyle.ALIGN_CENTER_SELECTION;
		}else if(alignmentStyle.name().equals("ALIGN_FILL")){
			return CellStyle.ALIGN_FILL;
		}else if(alignmentStyle.name().equals("ALIGN_GENERAL")){
			return CellStyle.ALIGN_GENERAL;
		}else if(alignmentStyle.name().equals("ALIGN_JUSTIFY")){
			return CellStyle.ALIGN_JUSTIFY;
		}else if(alignmentStyle.name().equals("ALIGN_LEFT")){
			return CellStyle.ALIGN_LEFT;
		}else if(alignmentStyle.name().equals("ALIGN_RIGHT")){
			return CellStyle.ALIGN_RIGHT;
		}else if(alignmentStyle.name().equals("VERTICAL_BOTTOM")){
			return CellStyle.VERTICAL_BOTTOM;
		}else if(alignmentStyle.name().equals("VERTICAL_JUSTIFY")){
			return CellStyle.VERTICAL_JUSTIFY;
		}else if(alignmentStyle.name().equals("VERTICAL_CENTER")){
			return CellStyle.VERTICAL_CENTER;
		}else if(alignmentStyle.name().equals("VERTICAL_TOP")){
			return CellStyle.VERTICAL_TOP;
		}
		return null;
	}
	
	@Override
	public String getUri() {
		return getExcelRow().getUri()+getName();
	}

}
