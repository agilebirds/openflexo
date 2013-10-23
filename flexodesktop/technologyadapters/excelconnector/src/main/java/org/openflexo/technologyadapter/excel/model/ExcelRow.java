package org.openflexo.technologyadapter.excel.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;

/**
 * Represents an Excel row, implemented as a wrapper of a POI row
 * 
 * @author vincent, sylvain
 * 
 */
public class ExcelRow extends ExcelObject {

	private Row row;
	private ExcelSheet excelSheet;
	private List<ExcelCell> excelCells;

	public Row getRow() {
		return row;
	}

	public ExcelRow(Row row, ExcelSheet excelSheet, ExcelTechnologyAdapter adapter) {
		super(adapter);
		this.row = row;
		this.excelSheet = excelSheet;
		excelCells = new ArrayList<ExcelCell>();
	}

	protected void createRowWhenNonExistant() {
		if (row == null) {
			row = excelSheet.getSheet().createRow(getRowNum());
		}
	}

	public ExcelSheet getExcelSheet() {
		return excelSheet;
	}

	public List<ExcelCell> getExcelCells() {
		return excelCells;
	}

	public void setExcelCells(List<ExcelCell> excelCells) {
		this.excelCells = excelCells;
	}

	public void addToExcelCells(ExcelCell newExcelCell) {
		this.excelCells.add(newExcelCell);
	}

	public void removeFromExcelCells(ExcelCell deletedExcelCell) {
		this.excelCells.remove(deletedExcelCell);
	}

	@Override
	public String getName() {
		return "Row"+getRowNum();
	}

	public int getRowIndex() {
		if (row != null) {
			return row.getRowNum();
		}
		return getExcelSheet().getExcelRows().indexOf(this);
	}

	public int getRowNum() {
		return getRowIndex();
	}

	public ExcelCell getExcelCell(int columnIndex) {
		return getCellAt(columnIndex);
	}

	public ExcelCell getCellAt(int columnIndex) {
		if (columnIndex < 0) {
			return null;
		}
		// Append missing cells
		while (getExcelCells().size() <= columnIndex) {
			addToExcelCells(new ExcelCell(null, this, getTechnologyAdapter()));
		}
		return getExcelCells().get(columnIndex);
	}
	
	
	@Override
	public String getUri() {
		return getExcelSheet().getUri()+getName();
	}

}
