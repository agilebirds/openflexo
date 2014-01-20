package org.openflexo.technologyadapter.excel.viewpoint.editionaction;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.excel.BasicExcelModelSlot;
import org.openflexo.technologyadapter.excel.model.ExcelCell;
import org.openflexo.technologyadapter.excel.model.ExcelRow;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;

@FIBPanel("Fib/SelectExcelCellPanel.fib")
@ModelEntity
@ImplementationClass(SelectExcelCell.SelectExcelCellImpl.class)
@XMLElement
public interface SelectExcelCell extends FetchRequest<BasicExcelModelSlot, ExcelCell> {

	public static abstract class SelectExcelCellImpl extends FetchRequestImpl<BasicExcelModelSlot, ExcelCell> implements SelectExcelCell {

		private static final Logger logger = Logger.getLogger(SelectExcelCell.class.getPackage().getName());

		public SelectExcelCellImpl() {
			super();
		}

		@Override
		public Type getFetchedType() {
			return ExcelCell.class;
		}

		@Override
		public List<ExcelCell> performAction(EditionSchemeAction action) {

			if (getModelSlotInstance(action) == null) {
				logger.warning("Could not access model slot instance. Abort.");
				return null;
			}
			if (getModelSlotInstance(action).getResourceData() == null) {
				logger.warning("Could not access model adressed by model slot instance. Abort.");
				return null;
			}

			ExcelWorkbook excelWorkbook = (ExcelWorkbook) getModelSlotInstance(action).getAccessedResourceData();

			List<ExcelCell> selectedExcelCells = new ArrayList<ExcelCell>(0);
			for (ExcelSheet excelSheet : excelWorkbook.getExcelSheets()) {
				for (ExcelRow excelRow : excelSheet.getExcelRows()) {
					selectedExcelCells.addAll(excelRow.getExcelCells());
				}
			}

			List<ExcelCell> returned = filterWithConditions(selectedExcelCells, action);

			return returned;
		}
	}
}
