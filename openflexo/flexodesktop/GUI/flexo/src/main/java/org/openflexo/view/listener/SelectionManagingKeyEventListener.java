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
package org.openflexo.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.selection.SelectionManager;
import org.openflexo.view.controller.FlexoController;


/**
 * Abstract listeners for key events for flexo modules managing a
 * {@link SelectionManager} At this level are managed 'delete' events (which is
 * linked to deletion process on corresponding {@link SelectionManager})
 * 
 * @author sylvain
 */
public abstract class SelectionManagingKeyEventListener extends FlexoKeyEventListener implements FlexoActionSource
{

    public SelectionManagingKeyEventListener(FlexoController controller)
    {
        super(controller);
    }
    
    @Override
	public void keyPressed(KeyEvent event)
    {
        if (event.getKeyCode() == KeyEvent.VK_DELETE || event.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            ActionListener listener = getController().getActionForKeyStroke(KeyStroke.getKeyStroke(FlexoCst.DELETE_KEY_CODE, 0));
            if (listener != null) {
                listener.actionPerformed(new ActionEvent(this, event.getID(), ""));
                event.consume();
           }
        }
        if (!event.isConsumed()) {
            super.keyPressed(event);
        }
    }

    protected abstract SelectionManager getSelectionManager();
    
    @Override
	public FlexoModelObject getFocusedObject()
    {
        return getSelectionManager().getLastSelectedObject();
    }
    
    @Override
	public Vector getGlobalSelection()
    {
        return getSelectionManager().getSelection();
    }

}
