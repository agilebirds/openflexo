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
package org.openflexo.dgmodule.view;

import java.util.logging.Logger;

import org.openflexo.dgmodule.DGCst;
import org.openflexo.dgmodule.controller.DGController;
import org.openflexo.dgmodule.menu.DGMenuBar;
import org.openflexo.dgmodule.view.listener.DGKeyEventListener;
import org.openflexo.doceditor.view.DEFrame;


/**
 * Frame of DocGenerator Module
 * 
 * @author gpolet
 */

public class DGFrame extends DEFrame
{

    private static final Logger logger = Logger.getLogger(DGFrame.class.getPackage().getName());

    // ==========================================================================
    // ============================= Static variables
    // ==================================
    // ==========================================================================

    /**
     * Constructor for GeneratorFrame
     */
    public DGFrame(String title, DGController controller, DGKeyEventListener generatorKeyEventListener,
            DGMenuBar menuBar)
    {
        super(title, controller, generatorKeyEventListener, menuBar);
        setSize(DGCst.DEFAULT_DG_WIDTH, DGCst.DEFAULT_DG_HEIGHT);
    }

}
