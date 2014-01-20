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
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.excel.BasicExcelModelSlot;
import org.openflexo.technologyadapter.excel.model.ExcelCell;
import org.openflexo.technologyadapter.excel.model.ExcelCell.CellType;
import org.openflexo.technologyadapter.excel.model.ExcelRow;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;

@FIBPanel("Fib/AddExcelCellPanel.fib")
@ModelEntity
@ImplementationClass(AddExcelCell.AddExcelCellImpl.class)
@XMLElement
public interface AddExcelCell extends AssignableAction<BasicExcelModelSlot, ExcelCell> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String ROW_KEY = "row";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String VALUE_KEY = "value";
	@PropertyIdentifier(type = CellType.class)
	public static final String CELL_TYPE_KEY = "cellType";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String COLUMN_INDEX_KEY = "columnIndex";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String ROW_INDEX_KEY = "rowIndex";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String SHEET_KEY = "sheet";
	@PropertyIdentifier(type = boolean.class)
	public static final String IS_ROW_INDEX_KEY = "isRowIndex";

	@Getter(value = ROW_KEY)
	@XMLAttribute
	public DataBinding<ExcelRow> getRow();

	@Setter(ROW_KEY)
	public void setRow(DataBinding<ExcelRow> row);

	@Getter(value = VALUE_KEY)
	@XMLElement
	public DataBinding<String> getValue();

	@Setter(VALUE_KEY)
	public void setValue(DataBinding<String> value);

	@Getter(value = CELL_TYPE_KEY)
	@XMLAttribute
	public CellType getCellType();

	@Setter(CELL_TYPE_KEY)
	public void setCellType(CellType cellType);

	@Getter(value = COLUMN_INDEX_KEY)
	@XMLElement
	public DataBinding<Integer> getColumnIndex();

	@Setter(COLUMN_INDEX_KEY)
	public void setColumnIndex(DataBinding<Integer> columnIndex);

	@Getter(value = ROW_INDEX_KEY)
	@XMLElement
	public DataBinding<Integer> getRowIndex();

	@Setter(ROW_INDEX_KEY)
	public void setRowIndex(DataBinding<Integer> rowIndex);

	@Getter(value = SHEET_KEY)
	@XMLElement
	public DataBinding<ExcelSheet> getSheet();

	@Setter(SHEET_KEY)
	public void setSheet(DataBinding<ExcelSheet> sheet);

	@Getter(value = IS_ROW_INDEX_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean isRowIndex();

	@Setter(IS_ROW_INDEX_KEY)
	public void setRowIndex(boolean isRowIndex);

	public static abstract class AddExcelCellImpl extends AssignableActionImpl<BasicExcelModelSlot, ExcelCell> implements AddExcelCell {

		private static final Logger logger = Logger.getLogger(AddExcelCell.class.getPackage().getName());

		private DataBinding<String> value;

		private DataBinding<Integer> columnIndex;

		private DataBinding<Integer> rowIndex;

		private DataBinding<ExcelRow> row;

		private DataBinding<ExcelSheet> sheet;

		private CellType cellType = null;

		private boolean isRowIndex = false;

		public AddExcelCellImpl() {
			super();
		}

		@Override
		public Type getAssignableType() {
			return ExcelCell.class;
		}

		@Override
		public ExcelCell performAction(EditionSchemeAction action) {

			ExcelCell excelCell = null;

			FreeModelSlotInstance<ExcelWorkbook, BasicExcelModelSlot> modelSlotInstance = getModelSlotInstance(action);
			if (modelSlotInstance.getResourceData() != null) {

				try {
					ExcelRow excelRow = null;
					if (isRowIndex) {
						Integer rowIndex = getRowIndex().getBindingValue(action);
						ExcelSheet excelSheet = getSheet().getBindingValue(action);
						excelRow = excelSheet.getRowAt(rowIndex);
					} else {
						excelRow = getRow().getBindingValue(action);
					}

					Integer columnIndex = getColumnIndex().getBindingValue(action);
					// If this is possible, create the cell
					if (columnIndex != null) {
						if (excelRow != null) {
							Cell cell = null;
							String value = getValue().getBindingValue(action);
							// If this cell exists, just get it
							if (excelRow.getCellAt(columnIndex) != null) {
								excelCell = excelRow.getCellAt(columnIndex);
							} else {
								cell = excelRow.getRow().createCell(columnIndex);
								excelCell = modelSlotInstance.getAccessedResourceData().getConverter()
										.convertExcelCellToCell(cell, excelRow, null);
							}
							if (value != null) {
								excelCell.setCellValue(value);
							} else {
								logger.warning("Create a cell requires a value.");
							}
							modelSlotInstance.getResourceData().setIsModified();
						} else {
							logger.warning("Create a cell requires a row.");
						}
					} else {
						logger.warning("Create a cell requires a column index.");
					}
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

			return excelCell;
		}

		@Override
		public FreeModelSlotInstance<ExcelWorkbook, BasicExcelModelSlot> getModelSlotInstance(EditionSchemeAction action) {
			return (FreeModelSlotInstance<ExcelWorkbook, BasicExcelModelSlot>) super.getModelSlotInstance(action);
		}

		@Override
		public DataBinding<String> getValue() {
			if (value == null) {
				value = new DataBinding<String>(this, String.class, DataBinding.BindingDefinitionType.GET);
				value.setBindingName("value");
			}
			return value;
		}

		@Override
		public void setValue(DataBinding<String> value) {
			if (value != null) {
				value.setOwner(this);
				value.setDeclaredType(String.class);
				value.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				value.setBindingName("value");
			}
			this.value = value;
		}

		@Override
		public DataBinding<Integer> getRowIndex() {
			if (rowIndex == null) {
				rowIndex = new DataBinding<Integer>(this, Integer.class, DataBinding.BindingDefinitionType.GET);
				rowIndex.setBindingName("rowIndex");
			}
			return rowIndex;
		}

		@Override
		public void setRowIndex(DataBinding<Integer> rowIndex) {
			if (rowIndex != null) {
				rowIndex.setOwner(this);
				rowIndex.setDeclaredType(Integer.class);
				rowIndex.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				rowIndex.setBindingName("rowIndex");
			}
			this.rowIndex = rowIndex;
		}

		@Override
		public DataBinding<Integer> getColumnIndex() {
			if (columnIndex == null) {
				columnIndex = new DataBinding<Integer>(this, Integer.class, DataBinding.BindingDefinitionType.GET);
				columnIndex.setBindingName("columnIndex");
			}
			return columnIndex;
		}

		@Override
		public void setColumnIndex(DataBinding<Integer> columnIndex) {
			if (columnIndex != null) {
				columnIndex.setOwner(this);
				columnIndex.setDeclaredType(Integer.class);
				columnIndex.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				columnIndex.setBindingName("columnIndex");
			}
			this.columnIndex = columnIndex;
		}

		@Override
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

		@Override
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

		@Override
		public DataBinding<ExcelRow> getRow() {
			if (row == null) {
				row = new DataBinding<ExcelRow>(this, ExcelRow.class, DataBinding.BindingDefinitionType.GET);
				row.setBindingName("row");
			}
			return row;
		}

		@Override
		public void setRow(DataBinding<ExcelRow> row) {
			if (row != null) {
				row.setOwner(this);
				row.setDeclaredType(ExcelRow.class);
				row.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				row.setBindingName("row");
			}
			this.row = row;
		}

		@Override
		public DataBinding<ExcelSheet> getSheet() {
			if (sheet == null) {
				sheet = new DataBinding<ExcelSheet>(this, ExcelSheet.class, DataBinding.BindingDefinitionType.GET);
				sheet.setBindingName("sheet");
			}
			return sheet;
		}

		@Override
		public void setSheet(DataBinding<ExcelSheet> sheet) {
			if (sheet != null) {
				sheet.setOwner(this);
				sheet.setDeclaredType(ExcelSheet.class);
				sheet.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				sheet.setBindingName("sheet");
			}
			this.sheet = sheet;
		}

		@Override
		public boolean isRowIndex() {
			return isRowIndex;
		}

		@Override
		public void setRowIndex(boolean isRowIndex) {
			this.isRowIndex = isRowIndex;
		}

	}
}
