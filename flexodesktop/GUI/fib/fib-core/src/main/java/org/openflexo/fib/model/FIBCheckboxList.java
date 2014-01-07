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
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBCheckboxList.FIBCheckboxListImpl.class)
@XMLElement(xmlTag = "CheckboxList")
public interface FIBCheckboxList extends FIBMultipleValues {

	@PropertyIdentifier(type = int.class)
	public static final String COLUMNS_KEY = "columns";
	@PropertyIdentifier(type = int.class)
	public static final String H_GAP_KEY = "HGap";
	@PropertyIdentifier(type = int.class)
	public static final String V_GAP_KEY = "VGap";

	@Getter(value = COLUMNS_KEY, defaultValue = "0")
	@XMLAttribute
	public int getColumns();

	@Setter(COLUMNS_KEY)
	public void setColumns(int columns);

	@Getter(value = H_GAP_KEY, defaultValue = "0")
	@XMLAttribute(xmlTag = "hGap")
	public int getHGap();

	@Setter(H_GAP_KEY)
	public void setHGap(int HGap);

	@Getter(value = V_GAP_KEY, defaultValue = "0")
	@XMLAttribute(xmlTag = "vGap")
	public int getVGap();

	@Setter(V_GAP_KEY)
	public void setVGap(int VGap);

	public static abstract class FIBCheckboxListImpl extends FIBMultipleValuesImpl implements FIBCheckboxList {

		private int columns = 1;
		private int hGap = 0;
		private int vGap = -2;

		private static final Logger logger = Logger.getLogger(FIBCheckboxList.class.getPackage().getName());

		public FIBCheckboxListImpl() {
		}

		@Override
		public String getBaseName() {
			return "CheckboxList";
		}

		@Override
		public int getColumns() {
			return columns;
		}

		@Override
		public void setColumns(int columns) {
			FIBPropertyNotification<Integer> notification = requireChange(COLUMNS_KEY, columns);
			if (notification != null) {
				this.columns = columns;
				hasChanged(notification);
			}
		}

		@Override
		public int getHGap() {
			return hGap;
		}

		@Override
		public void setHGap(int hGap) {
			FIBPropertyNotification<Integer> notification = requireChange(H_GAP_KEY, hGap);
			if (notification != null) {
				this.hGap = hGap;
				hasChanged(notification);
			}
		}

		@Override
		public int getVGap() {
			return vGap;
		}

		@Override
		public void setVGap(int vGap) {
			FIBPropertyNotification<Integer> notification = requireChange(V_GAP_KEY, vGap);
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
}
