package org.openflexo.technologyadapter.excel.viewpoint.editionaction;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Sheet;
import org.openflexo.foundation.view.FreeModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.technologyadapter.excel.BasicExcelModelSlot;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;

public class AddExcelSheet extends AssignableAction<BasicExcelModelSlot, ExcelSheet> {

	private static final Logger logger = Logger.getLogger(AddExcelSheet.class.getPackage().getName());

	public AddExcelSheet(VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public org.openflexo.foundation.viewpoint.EditionAction.EditionActionType getEditionActionType() {
		return EditionActionType.Assignation;
	}

	@Override
	public Type getAssignableType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExcelSheet performAction(EditionSchemeAction action) {

		ExcelSheet result = null;

		FreeModelSlotInstance<ExcelWorkbook, BasicExcelModelSlot> modelSlotInstance = getModelSlotInstance(action);
		if (modelSlotInstance.getResourceData() != null) {

			// Create an Excel Sheet
			Sheet sheet = modelSlotInstance.getResourceData().getWorkbook().createSheet();

			// Instanciate Wrapper.
			result = modelSlotInstance.getResourceData().getConverter().convertExcelSheetToSheet(sheet, null);
			modelSlotInstance.getResourceData().addExcelSheet(result);
			modelSlotInstance.getResourceData().setIsModified();

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

}
