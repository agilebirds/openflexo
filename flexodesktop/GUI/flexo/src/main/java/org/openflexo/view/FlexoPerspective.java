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
package org.openflexo.view;

import javax.swing.ImageIcon;
import javax.swing.JComponent;


import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.FlexoController;

public abstract class FlexoPerspective<O extends FlexoModelObject> {

    private String _name;
    
    public FlexoPerspective(String name)
    {
        super();
        _name = name;
    }
    
    public boolean isAlwaysVisible() 
    {
    	return false;
    }
    
    public String getName() 
    {
        return _name;
    }
    
    public String getLocalizedName() 
    {
        return FlexoLocalization.localizedForKey(getName());
    }
    
    @Override
	public String toString()
    {
        return getName();
    }
    
    public abstract ImageIcon getActiveIcon();
    
    public abstract ImageIcon getSelectedIcon();

    /**
     * Return a flag indicating if perspective control left view.
     * When set to true, use component supplied by #getLeftView()
     * @return
     * @see #getLeftView()
     */
    public boolean doesPerspectiveControlLeftView()
    {
    	return false;
    }
    
    /**
     * Return view to be used as left view controlled by this perspective
     * Default is to return null. Override this method as required.
     * @return
     * @see #doesPerspectiveControlLeftView()
     */
    public JComponent getLeftView() {
    	return null;
    }

    /**
     * Return a flag indicating if perspective control right view.
     * When set to true, use component supplied by #getRightView()
     * @return
     * @see #getRightView()
     */
    public boolean doesPerspectiveControlRightView()
    {
    	return false;
    }
    
    /**
     * Return view to be used as right view controlled by this perspective
     * Default is to return null. Override this method as required.
     * @return
     * @see #doesPerspectiveControlRightView()
     */
    public JComponent getRightView() {
    	return null;
    }

    // Override when required
    public JComponent getHeader() {
    	return null;
    }

    // Override when required
    public JComponent getFooter() {
    	return null;
    }

    public abstract ModuleView<? extends O> createModuleViewForObject(O object, FlexoController controller);

    public abstract boolean hasModuleViewForObject(FlexoModelObject object);

    public abstract O getDefaultObject(FlexoModelObject proposedObject);
	
    public void notifyModuleViewDisplayed(ModuleView<?> moduleView)
    {
    	
    }
}
