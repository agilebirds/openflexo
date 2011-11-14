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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.selection.SelectionManager;

/**
 * Selection manager dedicated to Doc Editor module
 * 
 * @author gpolet
 */
public class DESelectionManager extends SelectionManager {

	protected static final Logger logger = Logger.getLogger(DESelectionManager.class.getPackage().getName());

	public DESelectionManager(DEController controller) {
		super(controller);
		_clipboard = controller.createClipboard(this);
		_contextualMenuManager = controller.createContextualMenuManager(this);
	}

	public DEController getGeneratorController() {
		return (DEController) getController();
	}

	@Override
	public boolean performSelectionSelectAll() {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("'Select All' not implemented yet in DocGenerator");
		}
		return false;
	}

	/**
	 * Returns the root object that can be currently edited
	 * 
	 * @return FlexoObservable
	 */
	@Override
	public FlexoModelObject getRootFocusedObject() {
		return getGeneratorController().getProject().getGeneratedDoc();
	}

	@Override
	public FlexoModelObject getPasteContext() {
		return getFocusedObject();
	}

}
