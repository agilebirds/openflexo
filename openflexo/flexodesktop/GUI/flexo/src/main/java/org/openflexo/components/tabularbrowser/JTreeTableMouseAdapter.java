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
package org.openflexo.components.tabularbrowser;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;


/**
 * 
 * Default mouse adapter for a JTreeTable
 * 
 * @author sguerin
 */
public class JTreeTableMouseAdapter extends MouseAdapter implements
MouseMotionListener {
    
    private static final Logger logger = Logger.getLogger(JTreeTableMouseAdapter.class.getPackage().getName());

    private JTreeTable _table;
    private TabularBrowserModel _model;
   
    public JTreeTableMouseAdapter(JTreeTable table)
    {
        super();
        _table = table;
        _model = table.getTreeTableModel();
    }
    
    @Override
	public void mouseClicked(MouseEvent e)
    {
        Point p = e.getPoint();
        int col = _table.columnAtPoint(p);
        int row = _table.rowAtPoint(p);
        
        FlexoModelObject selectedObject = _table.getObjectAt(row);
        
        /*logger.info("Click on row "+row+" and col "+col);
        logger.info("Represented object is a "+selectedObject);
        logger.info("Event is CONSUMED =  "+e.isConsumed());*/       
        
       /* if (!(e.isShiftDown() || e.isMetaDown()))
            _table.fireResetSelection();
        
        _table.fireObjectSelected(selectedObject);*/

    }
    @Override
	public void mousePressed(MouseEvent e)
    {
        super.mousePressed(e);
     }
    
    @Override
	public void mouseReleased(MouseEvent e)
    {
        super.mouseReleased(e);
    }
    
    @Override
	public void mouseMoved(MouseEvent e)
    {
    }
    
    @Override
	public void mouseDragged(MouseEvent e)
    {
    }
    


}
