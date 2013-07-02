package org.openflexo.technologyadapter.excel.viewpoint.editionaction;

import java.util.List;

import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.EditionAction.EditionActionType;
import org.openflexo.technologyadapter.excel.BasicExcelModelSlot;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;
import org.openflexo.technologyadapter.excel.viewpoint.binding.ExcelSheetType;

public class SelectExcelSheet extends FetchRequest<BasicExcelModelSlot, ExcelTechnologyAdapter>{

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
	public List<ExcelTechnologyAdapter> performAction(EditionSchemeAction action) {
		// TODO Auto-generated method stub
		return null;
	}

}
