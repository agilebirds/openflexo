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
package org.openflexo.technologyadapter.excel.model;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.foundation.technologyadapter.TechnologyAdapter;

public class ExcelObjectPropertyValue extends ExcelPropertyValue {

	private ExcelProperty property;
	private List<Object> values;

	public ExcelObjectPropertyValue(ExcelProperty property, Object value) {
		super();
		this.property = property;
		this.values = new ArrayList<Object>();
		this.values.add(value);
	}

	@Override
	public ExcelProperty getProperty() {
		return property;
	}

	public List<Object> getValues() {
		return values;
	}

	public void addToValues(Object value) {
		values.add(value);
	}

	public void removeFromValues(Object value) {
		values.remove(value);
	}

	@Override
	public TechnologyAdapter getTechnologyAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

}
