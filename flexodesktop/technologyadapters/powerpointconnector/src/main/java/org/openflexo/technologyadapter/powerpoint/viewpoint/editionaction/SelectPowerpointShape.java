package org.openflexo.technologyadapter.powerpoint.viewpoint.editionaction;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.technologyadapter.powerpoint.BasicPowerpointModelSlot;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointShape;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlide;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlideshow;

@FIBPanel("Fib/SelectPowerpointShapePanel.fib")
public class SelectPowerpointShape extends FetchRequest<BasicPowerpointModelSlot, PowerpointShape> {

	private static final Logger logger = Logger.getLogger(SelectPowerpointShape.class.getPackage().getName());

	public SelectPowerpointShape(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Type getFetchedType() {
		return PowerpointShape.class;
	}

	@Override
	public List<PowerpointShape> performAction(EditionSchemeAction action) {

		if (getModelSlotInstance(action) == null) {
			logger.warning("Could not access model slot instance. Abort.");
			return null;
		}
		if (getModelSlotInstance(action).getResourceData() == null) {
			logger.warning("Could not access model adressed by model slot instance. Abort.");
			return null;
		}

		/*ExcelWorkbook excelWorkbook = (ExcelWorkbook) getModelSlotInstance(action).getResourceData();

		List<ExcelCell> selectedExcelCells = new ArrayList<ExcelCell>(0);
		for(ExcelSheet excelSheet : excelWorkbook.getExcelSheets()){
			for(ExcelRow excelRow : excelSheet.getExcelRows()){
				selectedExcelCells.addAll(excelRow.getExcelCells());
			}
		}

		List<ExcelCell> returned = filterWithConditions(selectedExcelCells, action);*/

		return null;
	}
}
