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
package org.openflexo.ie.view.controller.dnd;

/**
 * @author bmangez
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.openflexo.foundation.ie.widget.IEWidget;


/**
 * TODO : Description for this file
 * 
 * @author benoit
 */
public class WidgetMovable implements Transferable
{

    public WidgetMovable(IEWidget widget)
    {
        _movedWidget = new MovedWidget(widget);
    }

    /*
     * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
     */
    @Override
	public DataFlavor[] getTransferDataFlavors()
    {
        return new DataFlavor[] { widgetFlavor() };
    }

    /*
     * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
     */
    @Override
	public boolean isDataFlavorSupported(DataFlavor arg0)
    {
        return true;
    }

    /*
     * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
     */
    @Override
	public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException
    {
        return _movedWidget;
    }

    public static DataFlavor widgetFlavor()
    {
        if (_currentWidgetFlavor == null) {
            _currentWidgetFlavor = new DataFlavor(WidgetMovable.class, "Widget");
        }
        return _currentWidgetFlavor;
    }

    private static DataFlavor _currentWidgetFlavor;

    private MovedWidget _movedWidget;

}
