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
import org.openflexo.technologyadapter.excel.BasicExcelModelSlot;
import org.openflexo.technologyadapter.excel.model.ExcelRow;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;

@FIBPanel("Fib/AddExcelSheetPanel.fib")
public class AddExcelSheet extends AssignableAction<BasicExcelModelSlot, ExcelSheet> {

	private static final Logger logger = Logger.getLogger(AddExcelSheet.class.getPackage().getName());

	private DataBinding<String> sheetName;

	private DataBinding<List<ExcelRow>> sheetRows;

	public AddExcelSheet() {
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

	public DataBinding<String> getSheetName() {
		if (sheetName == null) {
			sheetName = new DataBinding<String>(this, String.class, DataBinding.BindingDefinitionType.GET);
			sheetName.setBindingName("sheetName");
		}
		return sheetName;
	}

	public void setSheetName(DataBinding<String> sheetName) {
		if (sheetName != null) {
			sheetName.setOwner(this);
			sheetName.setDeclaredType(String.class);
			sheetName.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			sheetName.setBindingName("sheetName");
		}
		this.sheetName = sheetName;
	}

	public DataBinding<List<ExcelRow>> getSheetRows() {
		if (sheetRows == null) {
			sheetRows = new DataBinding<List<ExcelRow>>(this, List.class, DataBinding.BindingDefinitionType.GET);
			sheetRows.setBindingName("sheetRows");
		}
		return sheetRows;
	}

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
