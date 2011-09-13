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
package org.openflexo.foundation.action;

import javax.swing.Icon;

import org.openflexo.localization.FlexoLocalization;

public class ActionGroup {

    private int _index;
    private String _actionMenuName;
    private Icon _smallIcon;
   
    public ActionGroup (String actionMenuName, int index)
    {
        super();
        _actionMenuName = actionMenuName;
        _index = index;
    }
    
    public ActionGroup (String actionMenuName, int index, Icon icon)
    {
        this(actionMenuName,index);
        setSmallIcon(icon);
    }
    
    public String getUnlocalizedName ()
    {
        return _actionMenuName;
    }

    public String getLocalizedName ()
    {
        return FlexoLocalization.localizedForKey(_actionMenuName);
    }

    public String getLocalizedDescription ()
    {
        return FlexoLocalization.localizedForKey(_actionMenuName+"_description");
    }
    
    public Icon getSmallIcon() 
    {
        return _smallIcon;
    }

    public void setSmallIcon(Icon smallIcon) 
    {
        _smallIcon = smallIcon;
    }

    public int getIndex() 
    {
        return _index;
    }
}
