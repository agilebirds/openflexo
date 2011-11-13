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
package org.openflexo.ie.view.listener;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.selection.SelectionManager;
import org.openflexo.view.listener.SelectionManagingKeyEventListener;

/**
 * Key events listener used in the context of Interface Editor
 * 
 * @author sguerin
 */
public class IEKeyEventListener extends SelectionManagingKeyEventListener {

	private IEController _ieController;

	public IEKeyEventListener(IEController controller) {
		super(controller);
		_ieController = controller;
	}

	@Override
	protected SelectionManager getSelectionManager() {
		return _ieController.getIESelectionManager();
	}

	@Override
	public FlexoEditor getEditor() {
		return _ieController.getEditor();
	}
}
