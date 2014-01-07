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

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBTextFieldColumn.FIBTextFieldColumnImpl.class)
@XMLElement(xmlTag = "TextFieldColumn")
public interface FIBTextFieldColumn extends FIBTableColumn {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String IS_EDITABLE_KEY = "isEditable";

	@Getter(value = IS_EDITABLE_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getIsEditable();

	@Setter(IS_EDITABLE_KEY)
	public void setIsEditable(DataBinding<Boolean> isEditable);

	public static abstract class FIBTextFieldColumnImpl extends FIBTableColumnImpl implements FIBTextFieldColumn {

		@Deprecated
		public static final BindingDefinition IS_EDITABLE = new BindingDefinition("isEditable", Boolean.class,
				DataBinding.BindingDefinitionType.GET, false);

		private DataBinding<Boolean> isEditable;

		@Override
		public DataBinding<Boolean> getIsEditable() {
			if (isEditable == null) {
				isEditable = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
			}
			return isEditable;
		}

		@Override
		public void setIsEditable(DataBinding<Boolean> isEditable) {
			if (isEditable != null) {
				isEditable.setOwner(this);
				isEditable.setDeclaredType(Boolean.class);
				isEditable.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.isEditable = isEditable;
		}

		@Override
		public void finalizeTableDeserialization() {
			super.finalizeTableDeserialization();
			if (isEditable != null) {
				isEditable.decode();
			}
		}

		@Override
		public Type getDefaultDataClass() {
			return String.class;
		}

		@Override
		public ColumnType getColumnType() {
			return ColumnType.TextField;
		}

	}
}
