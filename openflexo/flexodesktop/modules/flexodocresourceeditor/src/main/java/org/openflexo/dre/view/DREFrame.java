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

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.util.logging.Logger;

import org.openflexo.dre.DRECst;
import org.openflexo.dre.controller.DREController;
import org.openflexo.dre.view.listener.DREKeyEventListener;
import org.openflexo.dre.view.menu.DREMenuBar;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.view.FlexoFrame;


/**
 * The main window of this module
 * 
 * @author yourname
 */
public class DREFrame extends FlexoFrame
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DREFrame.class.getPackage().getName());

    protected DREController _DREController;

    protected DREMenuBar _DREMenuBar;

    protected DREKeyEventListener _DREKeyEventListener;

    /**
     * Constructor for DREFrame
     */
    public DREFrame(String title, DREController controller, DREKeyEventListener wkfKeyEventListener, DREMenuBar menuBar) throws HeadlessException
    {
        super(title, controller, wkfKeyEventListener, menuBar);
        _DREController = controller;
        _DREMenuBar = menuBar;
        _DREKeyEventListener = wkfKeyEventListener;
        setSize(DRECst.DEFAULT_MAINFRAME_WIDTH, DRECst.DEFAULT_MAINFRAME_HEIGHT);
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
    public DREController getDREController()
    {
        return _DREController;
    }


}
