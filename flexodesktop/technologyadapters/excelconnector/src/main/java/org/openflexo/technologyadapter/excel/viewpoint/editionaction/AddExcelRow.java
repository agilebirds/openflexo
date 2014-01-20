package org.openflexo.technologyadapter.excel.viewpoint.editionaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Row;
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
import org.openflexo.technologyadapter.excel.model.ExcelRow;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;

@FIBPanel("Fib/AddExcelRowPanel.fib")
@ModelEntity
@ImplementationClass(AddExcelRow.AddExcelRowImpl.class)
@XMLElement
public interface AddExcelRow extends AssignableAction<BasicExcelModelSlot, ExcelRow> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String EXCEL_SHEET_KEY = "excelSheet";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String EXCEL_CELLS_KEY = "excelCells";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String ROW_INDEX_KEY = "rowIndex";

	@Getter(value = EXCEL_SHEET_KEY)
	@XMLAttribute
	public DataBinding<ExcelSheet> getExcelSheet();

	@Setter(EXCEL_SHEET_KEY)
	public void setExcelSheet(DataBinding<ExcelSheet> excelSheet);

	@Getter(value = EXCEL_CELLS_KEY)
	@XMLElement
	public DataBinding<List<ExcelCell>> getExcelCells();

	@Setter(EXCEL_CELLS_KEY)
	public void setExcelCells(DataBinding<List<ExcelCell>> excelCells);

	@Getter(value = ROW_INDEX_KEY)
	@XMLElement
	public DataBinding<Integer> getRowIndex();

	@Setter(ROW_INDEX_KEY)
	public void setRowIndex(DataBinding<Integer> rowIndex);

	public static abstract class AddExcelRowImpl extends AssignableActionImpl<BasicExcelModelSlot, ExcelRow> implements AddExcelRow {

		private static final Logger logger = Logger.getLogger(AddExcelRow.class.getPackage().getName());

		private DataBinding<List<ExcelCell>> excelCells;

		private DataBinding<ExcelSheet> excelSheet;

		private DataBinding<Integer> rowIndex;

		public AddExcelRowImpl() {
			super();
			// TODO Auto-generated constructor stub
		}

		@Override
		public Type getAssignableType() {
			return ExcelRow.class;
		}

		@Override
		public ExcelRow performAction(EditionSchemeAction action) {
			ExcelRow excelRow = null;

			FreeModelSlotInstance<ExcelWorkbook, BasicExcelModelSlot> modelSlotInstance = getModelSlotInstance(action);
			if (modelSlotInstance.getResourceData() != null) {

				try {
					ExcelSheet excelSheet = getExcelSheet().getBindingValue(action);
					if (excelSheet != null) {
						Integer rowIndex = getRowIndex().getBindingValue(action);
						if (rowIndex != null) {
							if (excelSheet.getRowAt(rowIndex) != null) {
								excelRow = excelSheet.getRowAt(rowIndex);
							} else {
								Row row = excelSheet.getSheet().createRow(rowIndex);
								excelRow = modelSlotInstance.getAccessedResourceData().getConverter()
										.convertExcelRowToRow(row, excelSheet, null);
							}
							if (getExcelCells().getBindingValue(action) != null) {
								excelRow.getExcelCells().addAll(getExcelCells().getBindingValue(action));
							}
							modelSlotInstance.getResourceData().setIsModified();
						} else {
							logger.warning("Create a row requires a rowindex");
						}
					} else {
						logger.warning("Create a row requires a sheet");
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

			return excelRow;
		}

		@Override
		public DataBinding<List<ExcelCell>> getExcelCells() {
			if (excelCells == null) {
				excelCells = new DataBinding<List<ExcelCell>>(this, List.class, DataBinding.BindingDefinitionType.GET);
				excelCells.setBindingName("excelCells");
			}
			return excelCells;
		}

		@Override
		public void setExcelCells(DataBinding<List<ExcelCell>> excelCells) {
			if (excelCells != null) {
				excelCells.setOwner(this);
				excelCells.setDeclaredType(List.class);
				excelCells.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				excelCells.setBindingName("excelCells");
			}
			this.excelCells = excelCells;
		}

		@Override
		public DataBinding<ExcelSheet> getExcelSheet() {
			if (excelSheet == null) {
				excelSheet = new DataBinding<ExcelSheet>(this, ExcelSheet.class, DataBinding.BindingDefinitionType.GET);
				excelSheet.setBindingName("excelSheet");
			}
			return excelSheet;
		}

		@Override
		public void setExcelSheet(DataBinding<ExcelSheet> excelSheet) {
			if (excelSheet != null) {
				excelSheet.setOwner(this);
				excelSheet.setDeclaredType(ExcelSheet.class);
				excelSheet.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				excelSheet.setBindingName("excelSheet");
			}
			this.excelSheet = excelSheet;
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
		public FreeModelSlotInstance<ExcelWorkbook, BasicExcelModelSlot> getModelSlotInstance(EditionSchemeAction action) {
			return (FreeModelSlotInstance<ExcelWorkbook, BasicExcelModelSlot>) super.getModelSlotInstance(action);
		}

	}
}
