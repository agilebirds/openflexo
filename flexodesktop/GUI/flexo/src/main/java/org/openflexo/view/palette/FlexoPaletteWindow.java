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
package org.openflexo.view.palette;

import java.awt.BorderLayout;

import org.openflexo.view.FlexoFrame;
import org.openflexo.view.FlexoRelativeWindow;
import org.openflexo.view.controller.FlexoController;




/**
 * Abstract definition of a palette that will be used in a particular module
 * Contains dragging components and supports drag operations
 * 
 * @author sguerin
 */
public abstract class FlexoPaletteWindow extends FlexoRelativeWindow
{
    
    private FlexoPalette palette;
    
    protected FlexoPaletteWindow(FlexoFrame frame)
    {
        super(frame);
        setFocusableWindowState(false);
        palette = buildNewPalette(frame.getController());
        getContentPane().add(palette,BorderLayout.CENTER);
    }
    
    @Override
    public void dispose() {
    	super.dispose();
    	palette = null;
    }
    
    public FlexoPalette getPalette() 
    {
        return palette;
    }

    public abstract FlexoPalette buildNewPalette(FlexoController controller);

}
