package org.openflexo.technologyadapter.excel.viewpoint.editionaction;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.excel.BasicExcelModelSlot;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;
import org.openflexo.technologyadapter.excel.viewpoint.binding.ExcelSheetType;

public class SelectExcelSheet extends FetchRequest<BasicExcelModelSlot, ExcelSheet> {

	private static final Logger logger = Logger.getLogger(SelectExcelSheet.class.getPackage().getName());

	public SelectExcelSheet(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
		// TODO Auto-generated constructor stub
	}

	@Override
	public org.openflexo.foundation.viewpoint.EditionAction.EditionActionType getEditionActionType() {
		return EditionActionType.SelectIndividual;
	}

	@Override
	public ExcelSheetType getFetchedType() {
		return new ExcelSheetType();
	}

	@Override
	public List<ExcelSheet> performAction(EditionSchemeAction action) {

		if (getModelSlotInstance(action) == null) {
			logger.warning("Could not access model slot instance. Abort.");
			return null;
		}
		if (getModelSlotInstance(action).getResourceData() == null) {
			logger.warning("Could not access model adressed by model slot instance. Abort.");
			return null;
		}

		ExcelWorkbook excelWorkbook = (ExcelWorkbook) getModelSlotInstance(action).getResourceData();

		List<ExcelSheet> selectedExcelSheets = new ArrayList<ExcelSheet>(0);

		selectedExcelSheets.addAll(excelWorkbook.getExcelSheets());

		List<ExcelSheet> returned = filterWithConditions(selectedExcelSheets, action);

		return returned;
	}
}
