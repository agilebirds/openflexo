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

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.view.TypeAwareModelSlotInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.excel.model.semantics.ExcelMetaModel;
import org.openflexo.technologyadapter.excel.model.semantics.ExcelModel;
import org.openflexo.technologyadapter.excel.viewpoint.BusinessConceptInstancePatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.BusinessConceptTypePatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelCellPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelRowPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelSheetPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.AddBusinessConceptInstance;

/**
 * Implementation of the ModelSlot class for the Excel technology adapter<br>
 * We assert here that the spreadsheet is interpretated through a ExcelMetaModel, and data are wrapped into BusinessConcepts.
 * 
 * @author Vincent Leildé, Sylvain Guérin
 * 
 */
@DeclarePatternRoles({ // All pattern roles available through this model slot
@DeclarePatternRole(FML = "BusinessConceptType", patternRoleClass = BusinessConceptTypePatternRole.class), // Workbook
		@DeclarePatternRole(FML = "BusinessConceptInstance", patternRoleClass = BusinessConceptInstancePatternRole.class) // Cell
})
@DeclareEditionActions({ // All edition actions available through this model slot
@DeclareEditionAction(FML = "AddBusinessConceptInstance", editionActionClass = AddBusinessConceptInstance.class) // Add instance of BC
})
public interface SemanticsExcelModelSlot extends TypeAwareModelSlot<ExcelModel, ExcelMetaModel> {

	public abstract static class SemanticsExcelModelSlotImpl extends TypeAwareModelSlotImpl<ExcelModel, ExcelMetaModel> implements
			SemanticsExcelModelSlot {

		private static final Logger logger = Logger.getLogger(SemanticsExcelModelSlot.class.getPackage().getName());

		public SemanticsExcelModelSlotImpl(VirtualModel virtualModel, ExcelTechnologyAdapter adapter) {
			super(virtualModel, adapter);
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ModelSlotInstanceConfiguration<SemanticsExcelModelSlot, ExcelModel> createConfiguration(CreateVirtualModelInstance<?> action) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public FlexoModelResource<ExcelModel, ExcelMetaModel, ?> createProjectSpecificEmptyModel(View view, String filename,
				String modelUri, FlexoMetaModelResource<ExcelModel, ExcelMetaModel, ?> metaModelResource) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public FlexoModelResource<ExcelModel, ExcelMetaModel, ?> createSharedEmptyModel(FlexoResourceCenter<?> resourceCenter,
				String relativePath, String filename, String modelUri,
				FlexoMetaModelResource<ExcelModel, ExcelMetaModel, ?> metaModelResource) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getURIForObject(
				TypeAwareModelSlotInstance<ExcelModel, ExcelMetaModel, ? extends TypeAwareModelSlot<ExcelModel, ExcelMetaModel>> msInstance,
				Object o) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object retrieveObjectWithURI(
				TypeAwareModelSlotInstance<ExcelModel, ExcelMetaModel, ? extends TypeAwareModelSlot<ExcelModel, ExcelMetaModel>> msInstance,
				String objectURI) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isStrictMetaModelling() {
			return true;
		}

	}

}
