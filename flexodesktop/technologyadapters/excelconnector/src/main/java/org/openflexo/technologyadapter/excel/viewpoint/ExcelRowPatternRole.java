/*
 * (c) Copyright 2010-2011 AgileBirds
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
package org.openflexo.technologyadapter.excel.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.excel.model.ExcelRow;

@ModelEntity
@ImplementationClass(ExcelRowPatternRole.ExcelRowPatternRoleImpl.class)
@XMLElement
public interface ExcelRowPatternRole extends PatternRole<ExcelRow> {

	public static abstract class ExcelRowPatternRoleImpl extends PatternRoleImpl<ExcelRow> implements ExcelRowPatternRole {

		public ExcelRowPatternRoleImpl() {
			super();
			// TODO Auto-generated constructor stub
		}

		@Override
		public Type getType() {
			return ExcelRow.class;
		}

		@Override
		public String getPreciseType() {
			return ExcelRow.class.getSimpleName();
		}

		/*@Override
		public boolean getIsPrimaryRole() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setIsPrimaryRole(boolean isPrimary) {
			// TODO Auto-generated method stub

		}*/

		@Override
		public boolean defaultBehaviourIsToBeDeleted() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public ActorReference<ExcelRow> makeActorReference(ExcelRow object, EditionPatternInstance epi) {
			return new ExcelActorReference(object, this, epi);
		}

	}
}
