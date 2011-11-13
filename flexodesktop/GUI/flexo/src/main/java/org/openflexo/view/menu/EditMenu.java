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
package org.openflexo.view.menu;

/*
 * MenuFile.java
 * Project WorkflowEditor
 *
 * Created by benoit on Mar 10, 2004
 */
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.icon.IconLibrary;
import org.openflexo.module.ModuleLoader;
import org.openflexo.view.controller.FlexoController;

/**
 * 'Edit' menu
 * 
 * @author sguerin
 */
public class EditMenu extends FlexoMenu {

	public FlexoMenuItem undoItem;
	public FlexoMenuItem redoItem;

	// Following fields might be null if non-implemented
	public FlexoMenuItem deleteItem;
	public FlexoMenuItem cutItem;
	public FlexoMenuItem copyItem;
	public FlexoMenuItem pasteItem;
	public FlexoMenuItem selectAllItem;

	protected FlexoController _controller;

	public EditMenu(FlexoController controller) {
		super("edit", controller);
		_controller = controller;
		if (ModuleLoader.isMaintainerRelease() || ModuleLoader.isDevelopperRelease()) {
			add(undoItem = new UndoItem());
			add(redoItem = new RedoItem());
			undoItem.setEnabled(false);
			redoItem.setEnabled(false);
		}
	}

	// ==============================================
	// ================== Undo ======================
	// ==============================================

	public class UndoItem extends FlexoMenuItem {

		public UndoItem() {
			super(new UndoAction(), "undo", KeyStroke.getKeyStroke(KeyEvent.VK_Z, FlexoCst.META_MASK), IconLibrary.UNDO_ICON,
					getController());
			_controller.getEditor().getUndoManager().registerUndoControl(this);
		}

	}

	public class UndoAction extends AbstractAction {
		public UndoAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			_controller.getEditor().getUndoManager().undo();
		}

	}

	// ==============================================
	// ================== Redo ======================
	// ==============================================

	public class RedoItem extends FlexoMenuItem {

		public RedoItem() {
			super(new RedoAction(), "redo", KeyStroke.getKeyStroke(KeyEvent.VK_Y, FlexoCst.META_MASK), IconLibrary.REDO_ICON,
					getController());
			_controller.getEditor().getUndoManager().registerRedoControl(this);
		}

	}

	public class RedoAction extends AbstractAction {
		public RedoAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			_controller.getEditor().getUndoManager().redo();
		}

	}

}
