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
package org.openflexo.doceditor.controller;

import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.selection.FlexoClipboard;
import org.openflexo.selection.PastingGraphicalContext;


/**
 * DEClipboard is intented to be the object working with the DESelectionManager
 * and storing copied, cut and pasted objects. 
 * 
 * @author gpolet
 */
public class DEClipboard extends FlexoClipboard
{

    private static final Logger logger = Logger.getLogger(DEClipboard.class.getPackage().getName());

    private CGObject _clipboardData;

    private DEController _generatorController;

    public DEClipboard(DESelectionManager aSelectionManager, JMenuItem copyMenuItem, JMenuItem pasteMenuItem, JMenuItem cutMenuItem)
    {
        super(aSelectionManager, copyMenuItem, pasteMenuItem, cutMenuItem);
        _generatorController = aSelectionManager.getGeneratorController();
    }

    @Override
	protected void performSelectionPaste(FlexoModelObject pastingContext, PastingGraphicalContext graphicalContext)
    {
            logger.warning("Pasting " + _clipboardData+" not implemented");
    }

    @Override
	protected boolean isCurrentSelectionValidForCopy(Vector currentlySelectedObjects)
    {
        if (currentlySelectedObjects == null || currentlySelectedObjects.size() == 0) {
            return false;
        }
        return true;
    }

    @Override
	protected boolean performCopyOfSelection(Vector currentlySelectedObjects)
    {
        logger.warning("Copy not implemented");
         return true;
    }

    protected boolean isTargetValidForPasting(JComponent targetContainer)
    {
         return false;
    }

	public DEController getController() 
	{
		return _generatorController;
	}

 }
