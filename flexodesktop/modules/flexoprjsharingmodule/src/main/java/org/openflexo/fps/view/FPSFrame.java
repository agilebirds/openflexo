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
package org.openflexo.fps.view;

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.awt.BorderLayout;
import java.awt.HeadlessException;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.fps.FPSCst;
import org.openflexo.fps.controller.FPSController;
import org.openflexo.fps.view.listener.FPSKeyEventListener;
import org.openflexo.fps.view.menu.FPSMenuBar;
import org.openflexo.view.FlexoFrame;


/**
 * The main window of this module
 * 
 * @author yourname
 */
public class FPSFrame extends FlexoFrame
{

    // ==========================================================================
    // ============================= Instance variables
    // =========================
    // ==========================================================================

    protected FPSController _fpsController;

    protected FPSMenuBar _fpsMenuBar;

    protected FPSKeyEventListener _fpsKeyEventListener;


    // ================================================
    // ================ Constructor ===================
    // ================================================

    /**
     * Constructor for FPSFrame
     */
    public FPSFrame(String title, FPSController controller, FPSKeyEventListener wkfKeyEventListener, FPSMenuBar menuBar) throws HeadlessException
    {
        super(title, controller, wkfKeyEventListener, menuBar);
        _fpsController = controller;
        _fpsMenuBar = menuBar;
        _fpsKeyEventListener = wkfKeyEventListener;
        setSize(FPSCst.DEFAULT_MAINFRAME_WIDTH, FPSCst.DEFAULT_MAINFRAME_HEIGHT);
        updateTitle();
        getContentPane().setLayout(new BorderLayout());
        // You may observe here some model objects
    }

    @Override
	public void update(FlexoObservable observable, DataModification dataModification)
    {
       super.update(observable,dataModification);
    }

    /**
     * @return Returns the controller.
     */
    public FPSController getFPSController()
    {
        return _fpsController;
    }

    @Override
	public void updateTitle()
    {
    	if (getController().getCurrentModuleView() != null) {
    		setTitle(FlexoCst.BUSINESS_APPLICATION_VERSION_NAME + (getModule()!=null?" - "+getModule().getName():"") + " : " + getViewTitle() 
    				+ (getFPSController().getSharedProject()!=null?" - "+getFPSController().getSharedProject().getCVSModule().getModuleName()
    						+" - "+getFPSController().getSharedProject().getLocalDirectory().getAbsolutePath():""));
    	} else {
    		setTitle(FlexoCst.BUSINESS_APPLICATION_VERSION_NAME + (getModule()!=null?" - "+getModule().getName():""));
    	}
    }


}
