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
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.technologyadapter.excel.BasicExcelModelSlot;
import org.openflexo.technologyadapter.excel.model.ExcelRow;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;

@FIBPanel("Fib/AddExcelSheetPanel.fib")
public class AddExcelSheet extends AssignableAction<BasicExcelModelSlot, ExcelSheet> {

	private static final Logger logger = Logger.getLogger(AddExcelSheet.class.getPackage().getName());

	private DataBinding<String> excelObjectName;
	
	private DataBinding<List<ExcelRow>> sheetRows;
	
	public AddExcelSheet(VirtualModelBuilder builder) {
		super(builder);
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
			Workbook wb = modelSlotInstance.getResourceData().getWorkbook();
			Sheet sheet = null;
			try {
				String name = getExcelObjectName().getBindingValue(action);
				// Create an Excel Sheet
				sheet = wb.createSheet(name);
				// Instanciate Wrapper.
				result = modelSlotInstance.getResourceData().getConverter()
						.convertExcelSheetToSheet(sheet, modelSlotInstance.getResourceData(), null);
				modelSlotInstance.getResourceData().addToExcelSheets(result);
				modelSlotInstance.getResourceData().setIsModified();
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

	public DataBinding<String> getExcelObjectName() {
		if (excelObjectName == null) {
			excelObjectName = new DataBinding<String>(this, String.class, DataBinding.BindingDefinitionType.GET);
			excelObjectName.setBindingName("excelObjectName");
		}
		return excelObjectName;
	}

	public void setExcelObjectName(DataBinding<String> excelObjectName) {
		if (excelObjectName != null) {
			excelObjectName.setOwner(this);
			excelObjectName.setDeclaredType(String.class);
			excelObjectName.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			excelObjectName.setBindingName("excelObjectName");
		}
		this.excelObjectName = excelObjectName;
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
