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

import java.util.Date;

import org.openflexo.antar.expr.EvaluationType;
import org.openflexo.antar.java.JavaExpressionPrettyPrinter;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.xmlcode.StringEncoder.DateConverter;


public class DateStaticBinding extends StaticBinding<Date> {

	public static final DateConverter dateConverter = new DateConverter();
	
	private Date value;
	
	public DateStaticBinding()
	{
		super();
	}
	
	public DateStaticBinding(Date aValue)
	{
		super();
		value = aValue;
	}
	
   public DateStaticBinding(BindingDefinition bindingDefinition, FlexoModelObject owner, Date aValue)
    {
    	super(bindingDefinition,owner);
		if (aValue != null) value = (Date)aValue.clone(); else value = null;
   }

	@Override
	public EvaluationType getEvaluationType()
	{
		return EvaluationType.DATE;
	}

	@Override
	public String getStringRepresentation() 
	{
		return "["+dateConverter.convertToString(value)+"]";
	}
	@Override
	public String getWodStringRepresentation() {
		logger.severe("static date in wod files isn't supported yet");
		return "\"static date in wod files isn't supported yet\"";
	}
	@Override
	public Date getValue() 
	{
		return value;
	}

	@Override
	public void setValue(Date aValue) 
	{
		if (aValue != null)
			value = (Date)aValue.clone();
	}

	@Override
	public Class<Date> getStaticBindingClass()
	{
		return Date.class;
	}

	@Override
	public DateStaticBinding clone()
	{
		DateStaticBinding returned = new DateStaticBinding();
		returned.setsWith(this);
		return returned;
	}

    @Override
    public String getJavaCodeStringRepresentation()
    {
    	return JavaExpressionPrettyPrinter.getJavaStringRepresentation(getValue());
    }


}
