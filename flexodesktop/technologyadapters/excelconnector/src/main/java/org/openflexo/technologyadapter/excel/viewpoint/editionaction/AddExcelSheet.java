package org.openflexo.technologyadapter.excel.viewpoint.editionaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import org.openflexo.technologyadapter.excel.model.ExcelRow;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;

@FIBPanel("Fib/AddExcelSheetPanel.fib")
@ModelEntity
@ImplementationClass(AddExcelSheet.AddExcelSheetImpl.class)
@XMLElement
public interface AddExcelSheet extends AssignableAction<BasicExcelModelSlot, ExcelSheet> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String SHEET_NAME_KEY = "sheetName";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String SHEET_ROWS_KEY = "sheetRows";

	@Getter(value = SHEET_NAME_KEY)
	@XMLAttribute
	public DataBinding<String> getSheetName();

	@Setter(SHEET_NAME_KEY)
	public void setSheetName(DataBinding<String> sheetName);

	@Getter(value = SHEET_ROWS_KEY)
	@XMLElement
	public DataBinding<List<ExcelRow>> getSheetRows();

	@Setter(SHEET_ROWS_KEY)
	public void setSheetRows(DataBinding<List<ExcelRow>> sheetRows);

	public static abstract class AddExcelSheetImpl extends AssignableActionImpl<BasicExcelModelSlot, ExcelSheet> implements AddExcelSheet {

		private static final Logger logger = Logger.getLogger(AddExcelSheet.class.getPackage().getName());

		private DataBinding<String> sheetName;

		private DataBinding<List<ExcelRow>> sheetRows;

		public AddExcelSheetImpl() {
			super();
		}

		@Override
		public Type getAssignableType() {
			return ExcelSheet.class;
		}

		@Override
		public ExcelSheet performAction(EditionSchemeAction action) {

			ExcelSheet result = null;

			FreeModelSlotInstance<ExcelWorkbook, BasicExcelModelSlot> modelSlotInstance = getModelSlotInstance(action);
			if (modelSlotInstance.getResourceData() != null) {
				Workbook wb = modelSlotInstance.getAccessedResourceData().getWorkbook();
				Sheet sheet = null;
				try {
					if (wb != null) {
						String name = getSheetName().getBindingValue(action);
						if (name != null) {
							// Create an Excel Sheet
							sheet = wb.createSheet(name);
							// Instanciate Wrapper.
							result = modelSlotInstance.getAccessedResourceData().getConverter()
									.convertExcelSheetToSheet(sheet, modelSlotInstance.getAccessedResourceData(), null);
							modelSlotInstance.getAccessedResourceData().addToExcelSheets(result);
							modelSlotInstance.getAccessedResourceData().setIsModified();
						} else {
							logger.warning("Create a sheet requires a name");
						}
					} else {
						logger.warning("Create a sheet requires a workbook");
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

			return result;
		}

		@Override
		public FreeModelSlotInstance<ExcelWorkbook, BasicExcelModelSlot> getModelSlotInstance(EditionSchemeAction action) {
			return (FreeModelSlotInstance<ExcelWorkbook, BasicExcelModelSlot>) super.getModelSlotInstance(action);
		}

		@Override
		public DataBinding<String> getSheetName() {
			if (sheetName == null) {
				sheetName = new DataBinding<String>(this, String.class, DataBinding.BindingDefinitionType.GET);
				sheetName.setBindingName("sheetName");
			}
			return sheetName;
		}

		@Override
		public void setSheetName(DataBinding<String> sheetName) {
			if (sheetName != null) {
				sheetName.setOwner(this);
				sheetName.setDeclaredType(String.class);
				sheetName.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				sheetName.setBindingName("sheetName");
			}
			this.sheetName = sheetName;
		}

		@Override
		public DataBinding<List<ExcelRow>> getSheetRows() {
			if (sheetRows == null) {
				sheetRows = new DataBinding<List<ExcelRow>>(this, List.class, DataBinding.BindingDefinitionType.GET);
				sheetRows.setBindingName("sheetRows");
			}
			return sheetRows;
		}

		@Override
		public void setSheetRows(DataBinding<List<ExcelRow>> sheetRows) {
			if (sheetRows != null) {
				sheetRows.setOwner(this);
				sheetRows.setDeclaredType(List.class);
				sheetRows.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				sheetRows.setBindingName("sheetRows");
			}
			this.sheetRows = sheetRows;
		}

	}
}
