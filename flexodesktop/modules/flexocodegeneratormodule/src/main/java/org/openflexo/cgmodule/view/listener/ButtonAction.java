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
package org.openflexo.cgmodule.view.listener;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.localization.FlexoLocalization;

public abstract class ButtonAction implements ActionListener {

    private FlexoActionType _actionType;
    private String _unlocalizedName = null;
    private FlexoEditor _editor;
    
    public ButtonAction(FlexoActionType actionType, FlexoEditor editor)
    {
        super();
        _actionType = actionType;
        _editor = editor;
    }
    
    public ButtonAction(FlexoActionType actionType, String actionName, FlexoEditor editor)
    {
        this(actionType,editor);
        _unlocalizedName = actionName;
    }
    
    @Override
	public void actionPerformed(ActionEvent event)
    {
        FlexoAction action = _actionType.makeNewAction (getFocusedObject(),getGlobalSelection(), _editor);
        action.actionPerformed(event);
    }

    protected abstract Vector getGlobalSelection();

    protected abstract FlexoModelObject getFocusedObject();

    public FlexoActionType getActionType() 
    {
        return _actionType;
    }

    public String getLocalizedName (Component component)
    {
        if (_unlocalizedName == null) {
            return _actionType.getLocalizedName(component);
                       
        }
        else {
            return FlexoLocalization.localizedForKey(_unlocalizedName,component);
        }
    }

}
