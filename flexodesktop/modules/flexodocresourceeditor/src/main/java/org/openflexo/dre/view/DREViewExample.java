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
package org.openflexo.dre.view;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openflexo.dre.controller.DREController;
import org.openflexo.drm.DRMObject;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;


/**
 * Please comment this class
 * 
 * @author yourname
 * 
 */
public class DREViewExample extends JPanel implements ModuleView<DRMObject>, GraphicalFlexoObserver
{
    
    private DREController _controller;
    private DRMObject _object;

     public DREViewExample(DRMObject object, DREController controller)
    {
        super(new BorderLayout());
        add(new JLabel(object.getFullyQualifiedName(),SwingConstants.CENTER), BorderLayout.CENTER);
        _controller = controller;
        _object = object;
    }

    public DREController getDREController()
    {
        return _controller;
    }

   @Override
public DRMObject getRepresentedObject()
   {
       return _object;
   }
    
     @Override
	public void update(FlexoObservable observable, DataModification dataModification)
    {
        // TODO: Implements this
    }

    @Override
	public void deleteModuleView()
    {
         getDREController().removeModuleView(this);   
    }

    @Override
	public FlexoPerspective<DRMObject> getPerspective()
    {
        return getDREController().DRE_PERSPECTIVE;
    }

    /**
     * Overrides willShow
     * @see org.openflexo.view.ModuleView#willShow()
     */
    @Override
	public void willShow()
    {
        // TODO Auto-generated method stub
        
    }

    /**
     * Overrides willHide
     * @see org.openflexo.view.ModuleView#willHide()
     */
    @Override
	public void willHide()
    {
        // TODO Auto-generated method stub
        
    }

	/**
	 * Returns flag indicating if this view is itself responsible for scroll management
	 * When not, Flexo will manage it's own scrollbar for you
	 * 
	 * @return
	 */
	@Override
	public boolean isAutoscrolled() 
	{
		return false;
	}



}
