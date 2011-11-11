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

import java.awt.Component;
import java.awt.event.MouseEvent;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.selection.ContextualMenuManager;

public class DEContextualMenuManager extends ContextualMenuManager {

	private DEController _controller;

	public DEContextualMenuManager(DESelectionManager selectionManager, FlexoEditor editor, DEController controller) {
		super(selectionManager, editor);
		_controller = controller;
	}

	@Override
	public FlexoModelObject getFocusedObject(Component focusedComponent, MouseEvent e) {
		return super.getFocusedObject(focusedComponent, e);
	}

	public DEController getController() {
		return _controller;
	}

}
