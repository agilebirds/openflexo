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

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.binding.GenericArrayTypeImpl;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.WilcardTypeImpl;

import com.google.common.reflect.TypeToken;

public class FIBDropDownColumn extends FIBTableColumn {

	private static final Logger logger = Logger.getLogger(FIBMultipleValues.class.getPackage().getName());

	public static enum Parameters implements FIBModelAttribute {
		staticList, list, array
	}

	@Deprecated
	public static BindingDefinition LIST = new BindingDefinition("list", new ParameterizedTypeImpl(List.class, new WilcardTypeImpl(
			Object.class)), DataBinding.BindingDefinitionType.GET, false);
	@Deprecated
	public static BindingDefinition ARRAY = new BindingDefinition("array", new GenericArrayTypeImpl(new WilcardTypeImpl(Object.class)),
			DataBinding.BindingDefinitionType.GET, false);

	public String staticList;

	private DataBinding<List<?>> list;
	private DataBinding<Object[]> array;

	public FIBDropDownColumn() {
	}

	public DataBinding<List<?>> getList() {
		if (list == null) {
			list = new DataBinding<List<?>>(this, new TypeToken<List<?>>() {
			}.getType(), DataBinding.BindingDefinitionType.GET);
		}
		return list;
	}

	public void setList(DataBinding<List<?>> list) {
		if (list != null) {
			list.setOwner(this);
			list.setDeclaredType(new TypeToken<List<?>>() {
			}.getType());
			list.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.list = list;
	}

	public DataBinding<Object[]> getArray() {
		if (array == null) {
			array = new DataBinding<Object[]>(this, new TypeToken<Object[]>() {
			}.getType(), DataBinding.BindingDefinitionType.GET);
		}
		return array;
	}

	public void setArray(DataBinding<Object[]> array) {
		if (array != null) {
			array.setOwner(this);
			array.setDeclaredType(new TypeToken<Object[]>() {
			}.getType());
			array.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.array = array;
	}

	@Override
	public void finalizeTableDeserialization() {
		super.finalizeTableDeserialization();
		if (list != null) {
			list.decode();
		}
		if (array != null) {
			array.decode();
		}
	}

	@Override
	public Type getDefaultDataClass() {
		return Object.class;
	}

	@Override
	public ColumnType getColumnType() {
		return ColumnType.DropDown;
	}

}
