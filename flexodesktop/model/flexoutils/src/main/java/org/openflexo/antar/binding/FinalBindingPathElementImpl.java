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
package org.openflexo.antar.binding;

import java.lang.reflect.Type;
import java.util.Observable;

/**
 * Implemented by all path elements flagged as final (no children)
 * 
 * @author sylvain
 *
 */
public abstract class FinalBindingPathElementImpl extends Observable implements FinalBindingPathElement {

	private String label;
	private Type type;
	private Class declaringClass;
	private boolean settable = false;
	private String tooltipText;

	public FinalBindingPathElementImpl(String label, Class declaringClass, Type type, boolean isSettable, String tooltipText) 
	{
		super();
		this.label = label;
		this.type = type;
		this.declaringClass = declaringClass;
		this.settable = isSettable;
		this.tooltipText = tooltipText;
	}

	@Override
	public Class getDeclaringClass() 
	{
		return declaringClass;
	}

	@Override
	public Type getType() 
	{
		return type;
	}

	@Override
	public String getSerializationRepresentation()
	{
		return label;
	}

	@Override
	public boolean isBindingValid() 
	{
		return true;
	}

	@Override
	public String getLabel() 
	{
		return label;
	}

	@Override
	public String getTooltipText(Type resultingType) 
	{
		return tooltipText;
	}

	@Override
	public boolean isSettable() 
	{
		return settable;
	}

}
