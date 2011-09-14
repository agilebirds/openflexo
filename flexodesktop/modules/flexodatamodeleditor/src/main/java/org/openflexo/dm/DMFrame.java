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
package org.openflexo.dm;

import java.awt.BorderLayout;

import org.openflexo.FlexoCst;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.dm.view.listener.DMKeyEventListener;
import org.openflexo.dm.view.menu.DMMenuBar;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.view.FlexoFrame;


/**
 * Main frame for DM Module
 * 
 * @author sguerin
 */
public class DMFrame extends FlexoFrame
{

    protected DMController _dmController;

    protected DMMenuBar _dmMenuBar;

    protected DMKeyEventListener _dmKeyEventListener;

    /**
     * Constructor for DMFrame
     */
    public DMFrame(String title, DMController controller, DMKeyEventListener dmKeyEventListener, DMMenuBar menuBar)
    {
        super(title, controller, dmKeyEventListener, menuBar);
        _dmController = controller;
        _dmMenuBar = menuBar;
        _dmKeyEventListener = dmKeyEventListener;
        setSize(DMCst.DEFAULT_DMEDITOR_WIDTH + FlexoCst.MINIMUM_BROWSER_VIEW_WIDTH + 10, DMCst.DEFAULT_DMEDITOR_HEIGHT);
        getContentPane().setLayout(new BorderLayout());
        setTitle("DataBase Layout");
    }

    @Override
	public void update(FlexoObservable observable, DataModification dataModification)
    {
       super.update(observable,dataModification);
    }
    
}
