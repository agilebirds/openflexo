/*
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

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclareFetchRequest;
import org.openflexo.foundation.technologyadapter.DeclareFetchRequests;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.FreeModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.foundation.view.FreeModelSlotInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.excel.model.ExcelObject;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;
import org.openflexo.technologyadapter.excel.rm.ExcelWorkbookResource;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelCellPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelColumnPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelRowPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelSheetPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.AddExcelCell;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.AddExcelRow;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.AddExcelSheet;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.CellStyleAction;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.SelectExcelCell;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.SelectExcelRow;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.SelectExcelSheet;

/**
 * Implementation of a basic ModelSlot class for the Excel technology adapter<br>
 * This model slot reflects a basic interpretation of a workbook, with basic excel notions, such as workbook, sheet, row, col, and cell
 * 
 * @author Vincent LeildÃ©, Sylvain GuÃ©rin
 * 
 */
@DeclarePatternRoles({ // All pattern roles available through this model slot
@DeclarePatternRole(FML = "ExcelSheet", patternRoleClass = ExcelSheetPatternRole.class), // Sheet
		@DeclarePatternRole(FML = "ExcelColumn", patternRoleClass = ExcelColumnPatternRole.class), // Sheet
		@DeclarePatternRole(FML = "ExcelRow", patternRoleClass = ExcelRowPatternRole.class), // Row
		@DeclarePatternRole(FML = "ExcelCell", patternRoleClass = ExcelCellPatternRole.class) // Cell
})
@DeclareEditionActions({ // All edition actions available through this model slot
@DeclareEditionAction(FML = "AddExcelCell", editionActionClass = AddExcelCell.class), // Add cell
		@DeclareEditionAction(FML = "AddExcelRow", editionActionClass = AddExcelRow.class), // Add row
		@DeclareEditionAction(FML = "AddExcelSheet", editionActionClass = AddExcelSheet.class), // Add sheet
		@DeclareEditionAction(FML = "CellStyleAction", editionActionClass = CellStyleAction.class) // Cell Style
})
@DeclareFetchRequests({ // All requests available through this model slot
@DeclareFetchRequest(FML = "SelectExcelSheet", fetchRequestClass = SelectExcelSheet.class), // Select Excel Sheet
		@DeclareFetchRequest(FML = "SelectExcelRow", fetchRequestClass = SelectExcelRow.class), // Select Excel Row
		@DeclareFetchRequest(FML = "SelectExcelCell", fetchRequestClass = SelectExcelCell.class) // Select Excel Cell
})
@ModelEntity
@ImplementationClass(BasicExcelModelSlot.BasicExcelModelSlotImpl.class)
@XMLElement
public interface BasicExcelModelSlot extends FreeModelSlot<ExcelWorkbook> {

	public static abstract class BasicExcelModelSlotImpl extends FreeModelSlotImpl<ExcelWorkbook> implements BasicExcelModelSlot {

		private static final Logger logger = Logger.getLogger(BasicExcelModelSlot.class.getPackage().getName());

		private final BasicExcelModelSlotURIProcessor uriProcessor;

		public BasicExcelModelSlotImpl(VirtualModel virtualModel, ExcelTechnologyAdapter adapter) {
			super(virtualModel, adapter);
			uriProcessor = virtualModel.getVirtualModelFactory().newInstance(BasicExcelModelSlotURIProcessor.class);

		}

		@Override
		public Class<ExcelTechnologyAdapter> getTechnologyAdapterClass() {
			return ExcelTechnologyAdapter.class;
		}

		@Override
		public <PR extends PatternRole<?>> String defaultPatternRoleName(Class<PR> patternRoleClass) {
			if (ExcelCellPatternRole.class.isAssignableFrom(patternRoleClass)) {
				return "cell";
			} else if (ExcelRowPatternRole.class.isAssignableFrom(patternRoleClass)) {
				return "row";
			} else if (ExcelSheetPatternRole.class.isAssignableFrom(patternRoleClass)) {
				return "sheet";
			}
			return super.defaultPatternRoleName(patternRoleClass);
		}

		@Override
		public Type getType() {
			return ExcelWorkbook.class;
		}

		@Override
		public ModelSlotInstanceConfiguration<BasicExcelModelSlot, ExcelWorkbook> createConfiguration(CreateVirtualModelInstance<?> action) {
			return new BasicExcelModelSlotInstanceConfiguration(this, action);
		}

		@Override
		public String getURIForObject(FreeModelSlotInstance<ExcelWorkbook, ? extends FreeModelSlot<ExcelWorkbook>> msInstance, Object o) {
			ExcelObject excelObject = (ExcelObject) o;
			return uriProcessor.getURIForObject(msInstance, excelObject);
		}

		@Override
		public Object retrieveObjectWithURI(FreeModelSlotInstance<ExcelWorkbook, ? extends FreeModelSlot<ExcelWorkbook>> msInstance,
				String objectURI) {

			try {
				return uriProcessor.retrieveObjectWithURI(msInstance, objectURI);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public ExcelTechnologyAdapter getTechnologyAdapter() {
			return (ExcelTechnologyAdapter) super.getTechnologyAdapter();
		}

		@Override
		public ExcelWorkbookResource createProjectSpecificEmptyResource(View view, String filename, String modelUri) {
			return getTechnologyAdapter().createNewWorkbook(view.getProject(), filename, modelUri);
		}

		@Override
		public TechnologyAdapterResource<ExcelWorkbook, ExcelTechnologyAdapter> createSharedEmptyResource(
				FlexoResourceCenter<?> resourceCenter, String relativePath, String filename, String modelUri) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
