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
package org.openflexo.oe.view;

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.oe.OECst;
import org.openflexo.oe.controller.OEController;
import org.openflexo.oe.controller.OEKeyEventListener;
import org.openflexo.oe.view.menu.OEMenuBar;
import org.openflexo.view.FlexoFrame;


/**
 * The main window of this module
 * 
 * @author yourname
 */
public class OEFrame extends FlexoFrame
{

    private static final Logger logger = Logger.getLogger(OEFrame.class.getPackage().getName());

    // ==========================================================================
    // ============================= Instance variables
    // =========================
    // ==========================================================================

    protected OEController _oeController;

    protected OEMenuBar _oeMenuBar;

    protected OEKeyEventListener _oeKeyEventListener;


    // ================================================
    // ================ Constructor ===================
    // ================================================

    /**
     * Constructor for XXXFrame
     */
    public OEFrame(String title, OEController controller, OEKeyEventListener wkfKeyEventListener, OEMenuBar menuBar) throws HeadlessException
    {
        super(title, controller, wkfKeyEventListener, menuBar);
        _oeController = controller;
        _oeMenuBar = menuBar;
        _oeKeyEventListener = wkfKeyEventListener;
        setSize(OECst.DEFAULT_MAINFRAME_WIDTH, OECst.DEFAULT_MAINFRAME_HEIGHT);
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
    public OEController getOEController()
    {
        return _oeController;
    }


}
