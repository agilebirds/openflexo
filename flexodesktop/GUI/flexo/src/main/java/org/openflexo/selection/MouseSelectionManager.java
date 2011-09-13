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
package org.openflexo.selection;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.controller.FlexoController;


/**
 * A MouseSelectionManager extends the concept of SelectionManager by providing
 * a basic implementation of a MouseListener (and a MouseMotionListener) allowing
 * to basically handle custom view selection manageent.
 * 
 * @author sguerin
 */
public abstract class MouseSelectionManager extends SelectionManager implements MouseListener
{

    private static final Logger logger = Logger.getLogger(MouseSelectionManager.class.getPackage().getName());

    protected FocusableView _focusedPanel;
        
    private PastingGraphicalContext _pastingGraphicalContext;
    
    // ==========================================================================
    // ============================= Constructor ================================
    // ==========================================================================

    public MouseSelectionManager(FlexoController controller)
    {
        super(controller);
        _pastingGraphicalContext = new PastingGraphicalContext();
     }

    // ==========================================================================
    // ============================= MouseListenerInterface =====================
    // ==========================================================================

    public abstract void processMouseClicked(JComponent clickedContainer, Point clickedPoint, int clickCount, boolean isShiftDown);

    public abstract void processMouseEntered(MouseEvent e);

    public abstract void processMouseExited(MouseEvent e);

    public abstract void processMousePressed(MouseEvent e);

    public abstract void processMouseReleased(MouseEvent e);

    @Override
	public void mouseClicked(MouseEvent e)
    {
        JComponent clickedContainer = (JComponent) e.getSource();
        Point clickedPoint = e.getPoint();
        
        setLastClickedContainer(clickedContainer);
        setLastClickedPoint(clickedPoint);
        if (ToolBox.getPLATFORM()!=ToolBox.MACOS || e.getButton()!=MouseEvent.BUTTON3)
        	processMouseClicked(clickedContainer, clickedPoint, e.getClickCount(), e.getModifiersEx() == FlexoCst.MULTI_SELECTION_MASK);
    }

    @Override
	public void mouseEntered(MouseEvent e)
    {
        processMouseEntered(e);
    }

    @Override
	public void mouseExited(MouseEvent e)
    {
        processMouseExited(e);
    }

    @Override
	public void mousePressed(MouseEvent e)
    {
       processMousePressed(e);
       if ((!e.isConsumed()) && (_contextualMenuManager != null)) {
           _contextualMenuManager.processMousePressed(e);
       }
    }

    @Override
	public void mouseReleased(MouseEvent e)
    {
        processMouseReleased(e);
        if ((!e.isConsumed()) && (_contextualMenuManager != null)) {
            _contextualMenuManager.processMouseReleased(e);
        }
    }
    
    public void processMouseMoved(MouseEvent e)
    {
        if (_contextualMenuManager != null) {
            _contextualMenuManager.processMouseMoved(e);
       }
    }

     // ==========================================================================
    // ============================= Focus Management ===========================
    // ==========================================================================

    /**
     * Return currently focused view, if any
     * 
     * @return a FocusableView instance
     */
    public FocusableView getFocusedView()
    {
        return _focusedPanel;
    }

    /**
     * Remove focus on supplied view
     */
    public void removeFocus(FocusableView p)
    {
        if (logger.isLoggable(Level.FINEST))
            logger.finest("Remove focus on " + p);
        _focusedPanel = null;
        p.setIsFocused(false);
    }

    /**
     * Add focus on supplied view
     */
    protected void setIsFocused(FocusableView p)
    {
        if (logger.isLoggable(Level.FINEST))
            logger.finest("Set focus on " + p);
        _focusedPanel = p;
        p.setIsFocused(true);
        setFocusedObject(p.getObject());
    }

    /**
     * Return boolean indicating if supplied view if the currently focused view
     */
     protected boolean isCurrentlyFocused(FocusableView p)
    {
        return _focusedPanel == p;
    }


    // ==========================================================================
    // ============================= Cut&Paste Management =======================
    // ==========================================================================

    @Override
	public FlexoModelObject getPasteContext()
    {
        return pasteContextForComponent(getLastClickedContainer());
    }
    
    public abstract FlexoModelObject pasteContextForComponent(JComponent aComponent);

    @Override
	public PastingGraphicalContext getPastingGraphicalContext()
    {
        return _pastingGraphicalContext;
    }
    

    // ===============================================================
    // ================= Graphical utilities =========================
    // ===============================================================

    public Point getLastClickedPoint()
    {
        return _pastingGraphicalContext.pastingLocation;
    }

    public JComponent getLastClickedContainer()
    {
        return _pastingGraphicalContext.targetContainer;
    }

    public void setLastClickedPoint(Point aPoint)
    {
    	//logger.info("setLastClickedPoint="+aPoint);
        _pastingGraphicalContext.pastingLocation = aPoint;
    }

    public void setLastClickedContainer(JComponent aContainer)
    {
        _pastingGraphicalContext.targetContainer = aContainer;
    }

}
