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
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;

public class FIBTextFieldColumn extends FIBTableColumn {

	public static enum Parameters implements FIBModelAttribute
	{
		isEditable
	}

	public static BindingDefinition IS_EDITABLE = new BindingDefinition("isEditable", Boolean.class , BindingDefinitionType.GET, false);

	private DataBinding isEditable;

	public DataBinding getIsEditable() 
	{
		if (isEditable == null) isEditable = new DataBinding(this,Parameters.isEditable,IS_EDITABLE);
		return isEditable;
	}

	public void setIsEditable(DataBinding isEditable) 
	{
		isEditable.setOwner(this);
		isEditable.setBindingAttribute(Parameters.isEditable);
		isEditable.setBindingDefinition(IS_EDITABLE);
		this.isEditable = isEditable;
	}
	
	@Override
	public void finalizeTableDeserialization() 
	{
		super.finalizeTableDeserialization();
		if (isEditable != null) isEditable.finalizeDeserialization();
	}

	@Override
	public Type getDefaultDataClass()
	{
		return String.class;
	}

	@Override
	public ColumnType getColumnType()
	{
		return ColumnType.TextField;
	}


}
