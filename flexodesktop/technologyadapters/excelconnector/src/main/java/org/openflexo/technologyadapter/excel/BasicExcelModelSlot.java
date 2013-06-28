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
package org.openflexo.technologyadapter.excel;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclareFetchRequest;
import org.openflexo.foundation.technologyadapter.DeclareFetchRequests;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;
import org.openflexo.technologyadapter.excel.rm.ExcelModelResource;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelCellPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelColumnPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelRowPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelSheetPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelWorkbookPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.AddExcelCell;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.AddExcelRow;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.AddExcelSheet;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.AddExcelWorkbook;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.SelectExcelSheet;

/**
 * Implementation of a basic ModelSlot class for the Excel technology adapter<br>
 * This model slot reflects a basic interpretation of a workbook, with basic excel notions, such as workbook, sheet, row, col, and cell
 * 
 * @author Vincent Leildé, Sylvain Guérin
 * 
 */
@DeclarePatternRoles({ // All pattern roles available through this model slot
		@DeclarePatternRole(FML = "ExcelWorkbook", patternRoleClass = ExcelWorkbookPatternRole.class), // Workbook
		@DeclarePatternRole(FML = "ExcelSheet", patternRoleClass = ExcelSheetPatternRole.class), // Sheet
		@DeclarePatternRole(FML = "ExcelColumn", patternRoleClass = ExcelColumnPatternRole.class), // Sheet
		@DeclarePatternRole(FML = "ExcelRow", patternRoleClass = ExcelRowPatternRole.class), // Row
		@DeclarePatternRole(FML = "ExcelCell", patternRoleClass = ExcelCellPatternRole.class) // Cell
})
@DeclareEditionActions({ // All edition actions available through this model slot
		@DeclareEditionAction(FML = "AddExcelWorkbook", editionActionClass = AddExcelWorkbook.class), // Add workbook
		@DeclareEditionAction(FML = "AddExcelCell", editionActionClass = AddExcelCell.class), // Add cell
		@DeclareEditionAction(FML = "AddExcelRow", editionActionClass = AddExcelRow.class), // Add row
		@DeclareEditionAction(FML = "AddExcelSheet", editionActionClass = AddExcelSheet.class) // Add sheet
})
@DeclareFetchRequests({ // All requests available through this model slot
		@DeclareFetchRequest(FML = "RemoveReferencePropertyValue", fetchRequestClass = SelectExcelSheet.class) 
		})

public class BasicExcelModelSlot extends ModelSlot<ExcelWorkbook> {

	private static final Logger logger = Logger.getLogger(BasicExcelModelSlot.class.getPackage().getName());

	public BasicExcelModelSlot(VirtualModel<?> virtualModel, ExcelTechnologyAdapter adapter) {
		super(virtualModel, adapter);
	}

	public BasicExcelModelSlot(VirtualModelBuilder builder) {
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
		} else if (ExcelRowPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "row";
		} else if (ExcelSheetPatternRole.class.isAssignableFrom(patternRoleClass)) {
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
	public <EA extends EditionAction<?, ?>> EA makeEditionAction(Class<EA> editionActionClass) {
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
	public Type getType() {
		return ExcelWorkbook.class;
	}

	@Override
	public <FR extends FetchRequest<?, ?>> FR makeFetchRequest(Class<FR> fetchRequestClass) {
		if (SelectExcelSheet.class.isAssignableFrom(fetchRequestClass)) {
			return (FR) new SelectExcelSheet(null);
		} else {
			return null;
		}
	}

	@Override
	public ModelSlotInstanceConfiguration<BasicExcelModelSlot, ExcelWorkbook> createConfiguration(CreateVirtualModelInstance<?> action) {
		return new BasicExcelModelSlotInstanceConfiguration(this, action);
	}

	@Override
	public String getURIForObject(ModelSlotInstance<? extends ModelSlot<ExcelWorkbook>, ExcelWorkbook> msInstance, Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object retrieveObjectWithURI(ModelSlotInstance<? extends ModelSlot<ExcelWorkbook>, ExcelWorkbook> msInstance, String objectURI) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
