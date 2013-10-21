package org.openflexo.technologyadapter.excel.viewpoint.editionaction;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;

import org.openflexo.antar.binding.DataBinding;
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
		FreeModelSlotInstance<ExcelWorkbook, BasicExcelModelSlot> modelSlotInstance = getModelSlotInstance(action);
		return null;
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

	
}
