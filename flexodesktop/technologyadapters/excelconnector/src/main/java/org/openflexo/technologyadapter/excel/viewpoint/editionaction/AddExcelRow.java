package org.openflexo.technologyadapter.excel.viewpoint.editionaction;

import java.lang.reflect.Type;

import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.technologyadapter.excel.model.ExcelMetaModel;
import org.openflexo.technologyadapter.excel.model.ExcelModel;
import org.openflexo.technologyadapter.excel.model.ExcelRow;

public class AddExcelRow extends AssignableAction<ExcelModel,ExcelMetaModel,ExcelRow>{

	public AddExcelRow(VirtualModelBuilder builder) {
		super(builder);
		// TODO Auto-generated constructor stub
	}

	@Override
	public org.openflexo.foundation.viewpoint.EditionAction.EditionActionType getEditionActionType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getAssignableType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExcelRow performAction(EditionSchemeAction action) {
		// TODO Auto-generated method stub
		return null;
	}

}
