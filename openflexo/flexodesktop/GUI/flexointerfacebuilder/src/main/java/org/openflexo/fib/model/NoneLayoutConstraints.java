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

import javax.swing.JComponent;

import org.openflexo.fib.model.FIBPanel.Layout;



public class NoneLayoutConstraints extends ComponentConstraints {

	private static final String X = "x";
	private static final String Y = "y";
	
	public int getX() 
	{
		return getIntValue(X,0);
	}

	public void setX(int x) 
	{
		setIntValue(X,x);
	}

	public int getY() 
	{
		return getIntValue(Y,0);
	}

	public void setY(int y) 
	{
		setIntValue(Y,y);
	}

	public NoneLayoutConstraints() 
	{
		super();
	}
	
	protected NoneLayoutConstraints(String someConstraints) 
	{
		super(someConstraints);
	}
	
	NoneLayoutConstraints(ComponentConstraints someConstraints) 
	{
		super(someConstraints);
	}
	
	@Override
	protected Layout getType()
	{
		return Layout.none;
	}

	@Override
	public void performConstrainedAddition(JComponent container,
			JComponent contained)
	{
		contained.setLocation(getX(),getY());
		container.add(contained);
	}


}
