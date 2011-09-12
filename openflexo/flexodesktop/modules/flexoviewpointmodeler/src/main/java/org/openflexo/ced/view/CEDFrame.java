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
package org.openflexo.ced.view;

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.util.logging.Logger;

import org.openflexo.FlexoCst;
import org.openflexo.ced.CEDCst;
import org.openflexo.ced.controller.CEDController;
import org.openflexo.ced.controller.CEDKeyEventListener;
import org.openflexo.ced.view.menu.CEDMenuBar;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.view.FlexoFrame;


/**
 * The main window of this module
 * 
 * @author yourname
 */
public class CEDFrame extends FlexoFrame
{

    private static final Logger logger = Logger.getLogger(CEDFrame.class.getPackage().getName());

    // ==========================================================================
    // ============================= Instance variables
    // =========================
    // ==========================================================================

    protected CEDController _cedController;

    protected CEDMenuBar _cedMenuBar;

    protected CEDKeyEventListener _cedKeyEventListener;


    // ================================================
    // ================ Constructor ===================
    // ================================================

    /**
     * Constructor for XXXFrame
     */
    public CEDFrame(String title, CEDController controller, CEDKeyEventListener cedKeyEventListener, CEDMenuBar menuBar) throws HeadlessException
    {
        super(title, controller, cedKeyEventListener, menuBar);
        _cedController = controller;
        _cedMenuBar = menuBar;
        _cedKeyEventListener = cedKeyEventListener;
        setSize(CEDCst.DEFAULT_MAINFRAME_WIDTH, CEDCst.DEFAULT_MAINFRAME_HEIGHT);
        updateTitle();
        getContentPane().setLayout(new BorderLayout());
        // You may observe here some model objects
    }

    @Override
	public void update(FlexoObservable observable, DataModification dataModification)
    {
       super.update(observable,dataModification);
    }

    @Override
	public String getName()
    {
        return CEDCst.CED_MODULE_NAME;
    }

    /**
     * @return Returns the controller.
     */
    public CEDController getCEDController()
    {
        return _cedController;
    }

    @Override
	public void updateTitle()
    {
    	String projectTitle = "";
    	if (getController().getCurrentModuleView() != null && getModule()!=null) {
			setTitle(getModule().getName() + " : " + getViewTitle()
    				+ projectTitle);
    	} else {
    		if (getModule()==null)
    			setTitle(FlexoCst.BUSINESS_APPLICATION_VERSION_NAME + projectTitle);
    		else
    			setTitle(FlexoCst.BUSINESS_APPLICATION_VERSION_NAME + " - " + getModule().getName() + projectTitle);
    	}
    }



}
