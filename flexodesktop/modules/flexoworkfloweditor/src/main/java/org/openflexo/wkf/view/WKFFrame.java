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
package org.openflexo.wkf.view;

/*
 * MainFrame.java
 * Project WorkflowEditor
 *
 * Created by benoit on Feb 27, 2004
 */

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.view.FlexoFrame;
import org.openflexo.wkf.WKFCst;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.view.listener.WKFKeyEventListener;
import org.openflexo.wkf.view.menu.WKFMenuBar;


/**
 * The main window of the WKF Module.
 *
 * @author benoit
 */
public class WKFFrame extends FlexoFrame implements FlexoActionSource
{

    private static final Logger logger = Logger.getLogger(WKFFrame.class.getPackage().getName());

    // ==========================================================================
    // ============================= Instance variables
    // =========================
    // ==========================================================================

    protected WKFController _wkfController;

    protected WKFMenuBar _wkfMenuBar;

    protected WKFKeyEventListener _wkfKeyEventListener;

     // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    /**
     * Constructor for WKFFrame
     */
    public WKFFrame(String title, WKFController controller, WKFKeyEventListener wkfKeyEventListener, WKFMenuBar menuBar) throws HeadlessException
    {
        super(title, controller, wkfKeyEventListener, menuBar);
        _wkfController = controller;
        _wkfMenuBar = menuBar;
        _wkfKeyEventListener = wkfKeyEventListener;
        setSize(WKFCst.DEFAULT_MAINFRAME_WIDTH, WKFCst.DEFAULT_MAINFRAME_HEIGHT);
        updateTitle();
        getContentPane().setLayout(new BorderLayout());
        if (_wkfController.getFlexoWorkflow() != null) {
            _wkfController.getFlexoWorkflow().addObserver(this);
        }
    }

    @Override
    public void update(FlexoObservable observable, DataModification dataModification)
    {
       super.update(observable,dataModification);
    }

    /**
     * @return Returns the controller.
     */
    public WKFController getWKFController()
    {
        return _wkfController;
    }

    @Override
    public FlexoModelObject getFocusedObject()
    {
        return getWKFController().getSelectionManager().getFocusedObject();
    }

    @Override
    public Vector<FlexoModelObject> getGlobalSelection()
    {
        return getWKFController().getSelectionManager().getSelection();
    }

	@Override
    public FlexoEditor getEditor()
	{
		return getWKFController().getEditor();
	}

	/**
	 * Overrides dispose
	 * @see java.awt.Window#dispose()
	 */
	@Override
	public void dispose()
	{
		if (_wkfController!=null)
			_wkfController.getFlexoWorkflow().deleteObserver(this);
		_wkfController = null;
		_wkfKeyEventListener = null;
		_wkfMenuBar = null;
	    super.dispose();
	}

}
