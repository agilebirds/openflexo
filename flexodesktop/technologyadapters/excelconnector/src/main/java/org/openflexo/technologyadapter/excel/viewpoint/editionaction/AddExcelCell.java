package org.openflexo.technologyadapter.excel.viewpoint.editionaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.view.FreeModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.technologyadapter.excel.BasicExcelModelSlot;
import org.openflexo.technologyadapter.excel.model.ExcelCell;
import org.openflexo.technologyadapter.excel.model.ExcelCell.CellType;
import org.openflexo.technologyadapter.excel.model.ExcelRow;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;

@FIBPanel("Fib/AddExcelCellPanel.fib")
public class AddExcelCell extends AssignableAction<BasicExcelModelSlot, ExcelCell> {

	private static final Logger logger = Logger.getLogger(AddExcelCell.class.getPackage().getName());
	
	private DataBinding<String> value;
	
	private DataBinding<Integer> columnIndex;
	
	private DataBinding<Integer> rowIndex;
	
	private DataBinding<ExcelRow> row;
	
	private CellType cellType = null;
	
	
	public AddExcelCell(VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public Type getAssignableType() {
		return ExcelCell.class;
	}

	@Override
	public ExcelCell performAction(EditionSchemeAction action) {
		
		ExcelCell exceCell = null;
		
		FreeModelSlotInstance<ExcelWorkbook, BasicExcelModelSlot> modelSlotInstance = getModelSlotInstance(action);
		if(modelSlotInstance.getResourceData()!=null){
			
			try {
				ExcelRow excelRow = getRow().getBindingValue(action);
				Integer columnIndex = getColumnIndex().getBindingValue(action);
				Cell cell = excelRow.getRow().createCell(columnIndex);
				exceCell = modelSlotInstance.getResourceData().getConverter()
					.convertExcelCellToCell(cell, excelRow, null);
				exceCell.setCellValue(getValue().getBindingValue(action));
			} catch (TypeMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullReferenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		} else {
			logger.warning("Model slot not correctly initialised : model is null");
			return null;
		}

		return exceCell;
	}

	@Override
	public FreeModelSlotInstance<ExcelWorkbook, BasicExcelModelSlot> getModelSlotInstance(EditionSchemeAction action) {
		return (FreeModelSlotInstance<ExcelWorkbook, BasicExcelModelSlot>) super.getModelSlotInstance(action);
	}
	
	public DataBinding<String> getValue() {
		if (value == null) {
			value = new DataBinding<String>(this, String.class, DataBinding.BindingDefinitionType.GET);
			value.setBindingName("value");
		}
		return value;
	}

	public void setValue(DataBinding<String> value) {
		if (value != null) {
			value.setOwner(this);
			value.setDeclaredType(String.class);
			value.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			value.setBindingName("value");
		}
		this.value = value;
	}
	
	public DataBinding<Integer> getRowIndex() {
		if (rowIndex == null) {
			rowIndex = new DataBinding<Integer>(this, Integer.class, DataBinding.BindingDefinitionType.GET);
			rowIndex.setBindingName("rowIndex");
		}
		return rowIndex;
	}

	public void setRowIndex(DataBinding<Integer> rowIndex) {
		if (rowIndex != null) {
			rowIndex.setOwner(this);
			rowIndex.setDeclaredType(Integer.class);
			rowIndex.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			rowIndex.setBindingName("rowIndex");
		}
		this.rowIndex = rowIndex;
	}
	
	public DataBinding<Integer> getColumnIndex() {
		if (columnIndex == null) {
			columnIndex = new DataBinding<Integer>(this, Integer.class, DataBinding.BindingDefinitionType.GET);
			columnIndex.setBindingName("columnIndex");
		}
		return columnIndex;
	}

	public void setColumnIndex(DataBinding<Integer> columnIndex) {
		if (columnIndex != null) {
			columnIndex.setOwner(this);
			columnIndex.setDeclaredType(Integer.class);
			columnIndex.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			columnIndex.setBindingName("columnIndex");
		}
		this.columnIndex = columnIndex;
	}
	
	public CellType getCellType() {
		if (cellType == null) {
			if (_cellTypeName != null) {
				for (CellType cellType : getAvailableCellTypes()) {
					if (cellType.name().equals(_cellTypeName)) {
						return cellType;
					}
				}
			}
		}
		return cellType;
	}

	public void setCellType(CellType cellType) {
		this.cellType = cellType;
	}

	private List<CellType> availableCellTypes = null;
	
	public List<CellType> getAvailableCellTypes() {
		if (availableCellTypes == null) {
			availableCellTypes = new Vector<CellType>();
			for (CellType cellType : ExcelCell.CellType.values()) {
				availableCellTypes.add(cellType);
			}
		}
		return availableCellTypes;
	}

	private String _cellTypeName = null;

	public String _getGraphicalFeatureName() {
		if (getCellType() == null) {
			return _cellTypeName;
		}
		return getCellType().name();
	}

	public void _setCellTypeName(String cellTypeName) {
		_cellTypeName = cellTypeName;
	}

	public DataBinding<ExcelRow> getRow() {
		if (row == null) {
			row = new DataBinding<ExcelRow>(this, ExcelRow.class, DataBinding.BindingDefinitionType.GET);
			row.setBindingName("row");
		}
		return row;
	}

	public void setRow(DataBinding<ExcelRow> row) {
		if (row != null) {
			row.setOwner(this);
			row.setDeclaredType(ExcelRow.class);
			row.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			row.setBindingName("row");
		}
		this.row = row;
	}

	
}
