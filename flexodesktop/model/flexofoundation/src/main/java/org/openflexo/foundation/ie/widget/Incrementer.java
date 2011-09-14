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
package org.openflexo.foundation.ie.widget;

/**
 * This class implements an incrementer that can be passed as a parameter.
 * 
 * @author gpolet
 * 
 */
public class Incrementer
{

    private int currentValue = 0;

    /**
     * Default constructor
     */
    public Incrementer()
    {
    }
    
    public Incrementer(int start)
    {
    	currentValue=start;
    }

    /**
     * Increase the current value of 1
     * 
     */
    public void increment()
    {
        currentValue++;
    }

    /**
     * Returns the current value of this incrementer
     * 
     * @return - the current value.
     */
    public int getValue()
    {
        return currentValue;
    }

	public void increment(int value) {
		for(int i = 0; i<value;i++)
			increment();
		
	}
}