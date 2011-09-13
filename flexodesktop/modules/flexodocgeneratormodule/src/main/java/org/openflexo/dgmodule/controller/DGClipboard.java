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
package org.openflexo.dgmodule.controller;

import java.util.logging.Logger;

import javax.swing.JMenuItem;

import org.openflexo.doceditor.controller.DEClipboard;


/**
 * DGClipboard is intented to be the object working with the DGSelectionManager
 * and storing copied, cut and pasted objects. 
 * 
 * @author gpolet
 */
public class DGClipboard extends DEClipboard
{

    private static final Logger logger = Logger.getLogger(DGClipboard.class.getPackage().getName());

    public DGClipboard(DGSelectionManager aSelectionManager, JMenuItem copyMenuItem, JMenuItem pasteMenuItem, JMenuItem cutMenuItem)
    {
        super(aSelectionManager, copyMenuItem, pasteMenuItem, cutMenuItem);
    }
 }
