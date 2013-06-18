/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2013 Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package openflexo.technologyadapter.excel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.technologyadapter.excel.model.ExcelModel;
import org.openflexo.technologyadapter.excel.model.ExcelMetaModel;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelCellPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelRowPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelSheetPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelWorkbookPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.AddExcelCell;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.AddExcelRow;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.AddExcelSheet;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.AddExcelWorkbook;

/**
 * Implementation of the ModelSlot class for the Excel technology adapter
 * 
 * @author Vincent Leildé, Sylvain Guérin
 * 
 */
@DeclarePatternRoles({ 
		@DeclarePatternRole(ExcelWorkbookPatternRole.class), // Workbook
		@DeclarePatternRole(ExcelCellPatternRole.class), // Cell
		@DeclarePatternRole(ExcelRowPatternRole.class), // Row
		@DeclarePatternRole(ExcelSheetPatternRole.class) // Sheet
})
@DeclareEditionActions({ 
		@DeclareEditionAction(AddExcelWorkbook.class), // Add workbook
		@DeclareEditionAction(AddExcelCell.class), // Add cell
		@DeclareEditionAction(AddExcelRow.class), // Add row
		@DeclareEditionAction(AddExcelSheet.class) // Add sheet
})
public class BasicExcelModelSlot extends ModelSlot<ExcelModel, ExcelMetaModel> {

	private static final Logger logger = Logger.getLogger(BasicExcelModelSlot.class.getPackage().getName());

	public BasicExcelModelSlot(ViewPoint viewPoint, ExcelTechnologyAdapter adapter) {
		super(viewPoint, adapter);
	}

	public BasicExcelModelSlot(VirtualModel<?> virtualModel, ExcelTechnologyAdapter adapter) {
		super(virtualModel, adapter);
	}

	public BasicExcelModelSlot(VirtualModelBuilder builder) {
		super(builder);
	}

	public BasicExcelModelSlot(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Class<ExcelTechnologyAdapter> getTechnologyAdapterClass() {
		return ExcelTechnologyAdapter.class;
	}

	/**
	 * Creates and return a new {@link PatternRole} of supplied class.<br>
	 * This responsability is delegated to the OWL-specific {@link OWLModelSlot} which manages with introspection its own
	 * {@link PatternRole} types related to OWL technology
	 * 
	 * @param patternRoleClass
	 * @return
	 */
	@Override
	public <PR extends PatternRole<?>> PR makePatternRole(Class<PR> patternRoleClass) {
		if (ExcelSheetPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new ExcelSheetPatternRole(null);
		} else if (ExcelWorkbookPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new ExcelWorkbookPatternRole(null);
		} else if (ExcelCellPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new ExcelCellPatternRole(null);
		} else if (ExcelRowPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new ExcelRowPatternRole(null);
		}
		logger.warning("Unexpected pattern role: " + patternRoleClass.getName());
		return null;
	}

	@Override
	public <PR extends PatternRole<?>> String defaultPatternRoleName(Class<PR> patternRoleClass) {
		if (ExcelWorkbookPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "workbook";
		} else if (ExcelCellPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "cell";
		}
		else if (ExcelRowPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "row";
		}
		else if (ExcelSheetPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "sheet";
		}
		return super.defaultPatternRoleName(patternRoleClass);
	}

	/**
	 * Creates and return a new {@link EditionAction} of supplied class.<br>
	 * This responsability is delegated to the Excel-specific {@link BasicExcelModelSlot} which manages with introspection its own
	 * {@link EditionAction} types related to Excel technology
	 * 
	 * @param editionActionClass
	 * @return
	 */
	@Override
	public <EA extends EditionAction<?, ?, ?>> EA makeEditionAction(Class<EA> editionActionClass) {
		if (AddExcelSheet.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddExcelSheet(null);
		} else if (AddExcelWorkbook.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddExcelWorkbook(null);
		} else if (AddExcelCell.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddExcelCell(null);
		} else if (AddExcelRow.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddExcelRow(null);
		} else {
			return null;
		}
	}

	@Override
	public <FR extends FetchRequest<?, ?, ?>> FR makeFetchRequest(Class<FR> fetchRequestClass) {
		return null;
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModelSlotInstanceConfiguration<? extends ModelSlot<ExcelModel, ExcelMetaModel>> createConfiguration(
			CreateVirtualModelInstance<?> action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURIForObject(ModelSlotInstance msInstance, Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object retrieveObjectWithURI(ModelSlotInstance msInstance,
			String objectURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BindingVariable makePatternRolePathElement(PatternRole<?> pr,
			Bindable container) {
		// TODO Auto-generated method stub
		return null;
	}


	
}
