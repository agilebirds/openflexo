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
package org.openflexo.wse.view;

/*
 * Created in March 06 by Denis VANVYVE
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.view.FlexoFrame;
import org.openflexo.wse.WSECst;
import org.openflexo.wse.controller.WSEController;
import org.openflexo.wse.view.listener.WSEKeyEventListener;
import org.openflexo.wse.view.menu.WSEMenuBar;


/**
 * The main window of this module
 * 
 * @author yourname
 */
public class WSEFrame extends FlexoFrame
{

    private static final Logger logger = Logger.getLogger(WSEFrame.class.getPackage().getName());

    // ==========================================================================
    // ============================= Instance variables
    // =========================
    // ==========================================================================

    protected WSEController _wseController;

    protected WSEMenuBar _wseMenuBar;

    protected WSEKeyEventListener _wseKeyEventListener;


    // ================================================
    // ================ Constructor ===================
    // ================================================

    /**
     * Constructor for WSEFrame
     */
    public WSEFrame(String title, WSEController controller, WSEKeyEventListener wkfKeyEventListener, WSEMenuBar menuBar) throws HeadlessException
    {
        super(title, controller, wkfKeyEventListener, menuBar);
        _wseController = controller;
        _wseMenuBar = menuBar;
        _wseKeyEventListener = wkfKeyEventListener;
        setSize(WSECst.DEFAULT_MAINFRAME_WIDTH, WSECst.DEFAULT_MAINFRAME_HEIGHT);
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
    public WSEController getWSEController()
    {
        return _wseController;
    }


}
