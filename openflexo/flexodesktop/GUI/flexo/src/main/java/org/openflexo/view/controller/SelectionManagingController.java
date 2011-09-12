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
package org.openflexo.view.controller;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.selection.SelectionManager;


/**
 * Implemented by all the module controller implementing selection managing
 * 
 * @author sguerin
 * 
 */
public interface SelectionManagingController
{

    /**
     * Return the SelectionManager this controller is working with
     * 
     * @return
     */
    public SelectionManager getSelectionManager();

     /**
     * Select the view representing supplied object, if this view exists.
     * Try all to really display supplied object, even if required view
     * is not the current displayed view
     * 
     * @param object: the object to focus on
      */
    public void selectAndFocusObject(FlexoModelObject object);

    public FlexoProject getProject();
    
    public FlexoEditor getEditor();
}
