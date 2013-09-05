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
package org.openflexo.technologyadapter.excel.model.semantics;

import java.util.List;

import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;
import org.openflexo.technologyadapter.excel.model.ExcelObject;
import org.openflexo.technologyadapter.excel.model.ExcelProperty;
import org.openflexo.technologyadapter.excel.model.ExcelPropertyValue;

public class ExcelMetaModel extends ExcelObject implements FlexoMetaModel<ExcelMetaModel> {

	public ExcelMetaModel(ExcelTechnologyAdapter adapter) {
		super(adapter);
	}

	@Override
	public org.openflexo.foundation.resource.FlexoResource<ExcelMetaModel> getResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResource(org.openflexo.foundation.resource.FlexoResource<ExcelMetaModel> resource) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setIsReadOnly(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getObject(String objectURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends ExcelPropertyValue> getPropertyValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExcelPropertyValue getPropertyValue(ExcelProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExcelPropertyValue addToPropertyValue(ExcelProperty property, Object newValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExcelPropertyValue removeFromPropertyValue(ExcelProperty property, Object valueToRemove) {
		// TODO Auto-generated method stub
		return null;
	}

}
