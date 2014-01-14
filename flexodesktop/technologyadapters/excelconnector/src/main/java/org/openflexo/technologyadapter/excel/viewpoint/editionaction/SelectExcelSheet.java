package org.openflexo.technologyadapter.excel.viewpoint.editionaction;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.technologyadapter.excel.BasicExcelModelSlot;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;

@FIBPanel("Fib/SelectExcelSheetPanel.fib")
@ModelEntity
@ImplementationClass(SelectExcelSheet.SelectExcelSheetImpl.class)
@XMLElement
public interface SelectExcelSheet extends FetchRequest<BasicExcelModelSlot, ExcelSheet>{


public static abstract  class SelectExcelSheetImpl extends FetchRequest<BasicExcelModelSlot, ExcelSheet>Impl implements SelectExcelSheet
{

	private static final Logger logger = Logger.getLogger(SelectExcelSheet.class.getPackage().getName());

	public SelectExcelSheetImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Type getFetchedType() {
		return ExcelSheet.class;
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

		ExcelWorkbook excelWorkbook = (ExcelWorkbook) getModelSlotInstance(action).getAccessedResourceData();

		List<ExcelSheet> selectedExcelSheets = new ArrayList<ExcelSheet>(0);

		selectedExcelSheets.addAll(excelWorkbook.getExcelSheets());

		List<ExcelSheet> returned = filterWithConditions(selectedExcelSheets, action);

		return returned;
	}
}
}
