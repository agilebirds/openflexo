package org.openflexo.technologyadapter.powerpoint.viewpoint.editionaction;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.powerpoint.BasicPowerpointModelSlot;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlide;

@FIBPanel("Fib/SelectPowerpointSlidePanel.fib")
@ModelEntity
@ImplementationClass(SelectPowerpointSlide.SelectPowerpointSlideImpl.class)
@XMLElement
public interface SelectPowerpointSlide extends FetchRequest<BasicPowerpointModelSlot, PowerpointSlide> {

	public static abstract class SelectPowerpointSlideImpl extends FetchRequestImpl<BasicPowerpointModelSlot, PowerpointSlide> {

		private static final Logger logger = Logger.getLogger(SelectPowerpointSlide.class.getPackage().getName());

		@Override
		public Type getFetchedType() {
			return PowerpointSlide.class;
		}

		@Override
		public List<PowerpointSlide> performAction(EditionSchemeAction action) {

			if (getModelSlotInstance(action) == null) {
				logger.warning("Could not access model slot instance. Abort.");
				return null;
			}
			if (getModelSlotInstance(action).getResourceData() == null) {
				logger.warning("Could not access model adressed by model slot instance. Abort.");
				return null;
			}

			/*ExcelWorkbook excelWorkbook = (ExcelWorkbook) getModelSlotInstance(action).getResourceData();

			List<ExcelSheet> selectedExcelSheets = new ArrayList<ExcelSheet>(0);

			selectedExcelSheets.addAll(excelWorkbook.getExcelSheets());

			List<ExcelSheet> returned = filterWithConditions(selectedExcelSheets, action);*/

			return null;
		}
	}

}
