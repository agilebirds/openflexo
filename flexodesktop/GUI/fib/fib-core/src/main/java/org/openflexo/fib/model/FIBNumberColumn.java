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

import org.openflexo.fib.model.FIBNumber.NumberType;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBNumberColumn.FIBNumberColumnImpl.class)
@XMLElement(xmlTag = "NumberColumn")
public interface FIBNumberColumn extends FIBTableColumn {

	@PropertyIdentifier(type = NumberType.class)
	public static final String NUMBER_TYPE_KEY = "numberType";

	@Getter(value = NUMBER_TYPE_KEY)
	@XMLAttribute
	public NumberType getNumberType();

	@Setter(NUMBER_TYPE_KEY)
	public void setNumberType(NumberType numberType);

	public static abstract class FIBNumberColumnImpl extends FIBTableColumnImpl implements FIBNumberColumn {

		private NumberType numberType = NumberType.IntegerType;

		@Override
		public NumberType getNumberType() {
			return numberType;
		}

		@Override
		public void setNumberType(NumberType numberType) {
			FIBPropertyNotification<NumberType> notification = requireChange(NUMBER_TYPE_KEY, numberType);
			if (notification != null) {
				this.numberType = numberType;
				hasChanged(notification);
			}
		}

		@Override
		public Type getDefaultDataClass() {
			return Number.class;
		}

		@Override
		public ColumnType getColumnType() {
			return ColumnType.Number;
		}

	}
}
