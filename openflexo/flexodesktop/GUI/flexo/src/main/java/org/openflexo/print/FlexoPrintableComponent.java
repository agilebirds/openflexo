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
package org.openflexo.print;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import org.openflexo.foundation.FlexoModelObject;


public interface FlexoPrintableComponent {

    public FlexoPrintableDelegate getPrintableDelegate();
    
    public String getDefaultPrintableName();
    
    public void paint(Graphics graphics);

    public Rectangle getOptimalBounds();

    public void resizeComponent(Dimension aSize);
    
    public void refreshComponent();
 
    public int getWidth();

    public int getHeight();

	public void print(Graphics graphics);
	
	public FlexoModelObject getFlexoModelObject();
}
