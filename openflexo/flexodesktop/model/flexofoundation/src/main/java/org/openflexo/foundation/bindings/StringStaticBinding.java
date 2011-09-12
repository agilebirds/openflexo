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
package org.openflexo.foundation.bindings;

import org.openflexo.antar.expr.EvaluationType;
import org.openflexo.foundation.FlexoModelObject;


public class StringStaticBinding extends StaticBinding<String> {

	private String value;
	
	public StringStaticBinding()
	{
		super();
	}
	
	public StringStaticBinding(String aValue)
	{
		super();
		value = aValue;
	}
	
   public StringStaticBinding(BindingDefinition bindingDefinition, FlexoModelObject owner, String aValue)
    {
    	super(bindingDefinition,owner);
		value = aValue;
   }

	@Override
	public EvaluationType getEvaluationType()
	{
		return EvaluationType.STRING;
	}

	@Override
	public String getSerializationRepresentation() 
	{
		return "$'"+value+"'";
	}

	@Override
	public String getStringRepresentation() 
	{
		return '"'+value+'"';
	}
	
	@Override
	public String getWodStringRepresentation() {
		return getStringRepresentation();
	}
	
	@Override
	public String getValue() 
	{
		return value;
	}

	@Override
	public void setValue(String aValue) 
	{
		value = aValue;
	}

	@Override
	public Class<String> getStaticBindingClass()
	{
		return String.class;
	}

	@Override
	public StringStaticBinding clone()
	{
		StringStaticBinding returned = new StringStaticBinding();
		returned.setsWith(this);
		return returned;
	}

}
