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
package org.openflexo.fib.model;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.ParameterizedTypeImpl;

public class FIBCheckboxList extends FIBMultipleValues {

	public static enum Parameters implements FIBModelAttribute {
		columns, hGap, vGap
	}

	private int columns = 1;
	private int hGap = 0;
	private int vGap = -2;

	private static final Logger logger = Logger.getLogger(FIBCheckboxList.class.getPackage().getName());

	public FIBCheckboxList() {
	}

	@Override
	protected String getBaseName() {
		return "CheckboxList";
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.columns, columns);
		if (notification != null) {
			this.columns = columns;
			hasChanged(notification);
		}
	}

	public int getHGap() {
		return hGap;
	}

	public void setHGap(int hGap) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.hGap, hGap);
		if (notification != null) {
			this.hGap = hGap;
			hasChanged(notification);
		}
	}

	public int getVGap() {
		return vGap;
	}

	public void setVGap(int vGap) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.vGap, vGap);
		if (notification != null) {
			this.vGap = vGap;
			hasChanged(notification);
		}
	}

	@Override
	public Type getDataType() {
		Type[] args = new Type[1];
		args[0] = getIteratorType();
		return new ParameterizedTypeImpl(List.class, args);
	}

}
