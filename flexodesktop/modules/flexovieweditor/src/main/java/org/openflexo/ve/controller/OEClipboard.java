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
package org.openflexo.ve.controller;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.FlexoClipboard;
import org.openflexo.selection.PastingGraphicalContext;
import org.openflexo.view.controller.FlexoController;

/**
 * XXXClipboard is intented to be the object working with the XXXSelectionManager and storing copied, cutted and pasted objects. Handled
 * objects are instances implementing {@link org.openflexo.selection.SelectableView}.
 * 
 * @author yourname
 */
public class OEClipboard extends FlexoClipboard {

	private static final Logger logger = Logger.getLogger(OEClipboard.class.getPackage().getName());

	protected OESelectionManager _xxxSelectionManager;

	public OEClipboard(OESelectionManager aSelectionManager, JMenuItem copyMenuItem, JMenuItem pasteMenuItem, JMenuItem cutMenuItem) {
		super(aSelectionManager, copyMenuItem, pasteMenuItem, cutMenuItem);
		_xxxSelectionManager = aSelectionManager;
		resetClipboard();
	}

	public OESelectionManager getSelectionManager() {
		return _xxxSelectionManager;
	}

	public OEController getXXXController() {
		return getSelectionManager().getXXXController();
	}

	@Override
	public boolean performSelectionPaste() {
		if (_isPasteEnabled) {
			return super.performSelectionPaste();
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Sorry, PASTE disabled");
			}
			return false;
		}
	}

	@Override
	protected void performSelectionPaste(FlexoModelObject pastingContext, PastingGraphicalContext graphicalContext) {
		JComponent targetContainer = graphicalContext.targetContainer;
		if (isTargetValidForPasting(targetContainer)) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Paste is legal");
				// Handle paste here
			}
		} else {
			FlexoController.notify(FlexoLocalization.localizedForKey("cannot_paste_at_this_place_wrong_level"));
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Paste is NOT legal");
			}
		}
	}

	@Override
	protected boolean isCurrentSelectionValidForCopy(Vector currentlySelectedObjects) {
		return (getSelectionManager().getSelectionSize() > 0);
	}

	protected void resetClipboard() {
	}

	/**
	 * Selection procedure for copy
	 */
	@Override
	protected boolean performCopyOfSelection(Vector currentlySelectedObjects) {
		resetClipboard();
		// Put some code here
		// _clipboardData = ....
		return true;
	}

	protected boolean isTargetValidForPasting(JComponent targetContainer) {
		// Put some code here
		return false;
	}
}
