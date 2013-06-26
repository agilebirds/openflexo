package org.openflexo.technologyadapter.excel.viewpoint.editionaction;

import java.lang.reflect.Type;


import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.TypeSafeModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.foundation.viewpoint.EditionAction.EditionActionType;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.technologyadapter.excel.BasicExcelModelSlot;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.technologyadapter.excel.rm.ExcelModelRepository;

public class AddExcelSheet extends AssignableAction<BasicExcelModelSlot, ExcelSheet> {

	public AddExcelSheet(VirtualModelBuilder builder) {
		super(builder);
		// TODO Auto-generated constructor stub
	}

	@Override
	public org.openflexo.foundation.viewpoint.EditionAction.EditionActionType getEditionActionType() {
		return EditionActionType.AddIndividual;
	}

	@Override
	public Type getAssignableType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExcelSheet performAction(EditionSchemeAction action) {
	/*	ModelSlotInstance<BasicExcelModelSlot,?> modelSlotInstance = (ModelSlotInstance<BasicExcelModelSlot,?>) getModelSlotInstance(action);
		modelSlotInstance.getResourceData().getResource();*/
		return null;
	}

}
