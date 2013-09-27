package org.openflexo.technologyadapter.excel.viewpoint.editionaction;

import java.lang.reflect.Type;

import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.technologyadapter.excel.BasicExcelModelSlot;
import org.openflexo.technologyadapter.excel.model.ExcelRow;

@FIBPanel("Fib/AddExcelRowPanel.fib")
public class AddExcelRow extends AssignableAction<BasicExcelModelSlot, ExcelRow> {

	public AddExcelRow(VirtualModelBuilder builder) {
		super(builder);
		// TODO Auto-generated constructor stub
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