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
import java.beans.PropertyChangeEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.UndoManager;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.UserType;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.ControllerModel;

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
		if (UserType.isMaintainerRelease() || UserType.isDevelopperRelease()) {
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
			manager.addListener(ControllerModel.CURRENT_EDITOR, this, _controller.getControllerModel());
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == _controller) {
				if (evt.getOldValue() != null) {
					FlexoEditor old = (FlexoEditor) evt.getOldValue();
					if (old.getUndoManager() != null) {
						manager.removeListener(UndoManager.ACTION_HISTORY, this, old.getUndoManager());
						manager.removeListener(UndoManager.ENABLED, this, old.getUndoManager());
					}
				}
				if (evt.getNewValue() != null) {
					FlexoEditor editor = (FlexoEditor) evt.getNewValue();
					if (editor.getUndoManager() != null) {
						manager.addListener(UndoManager.ACTION_HISTORY, this, editor.getUndoManager());
						manager.addListener(UndoManager.ENABLED, this, editor.getUndoManager());
					}
					updateWithUndoManagerState();
				}
			} else {
				if (evt.getPropertyName().equals(UndoManager.ACTION_HISTORY) || evt.getPropertyName().equals(UndoManager.ENABLED)) {
					updateWithUndoManagerState();
				}
			}
		}

		private void updateWithUndoManagerState() {
			if (_controller.getEditor().getUndoManager() != null) {
				setEnabled(_controller.getEditor().getUndoManager().isUndoActive());
				if (_controller.getEditor().getUndoManager().isUndoActive()) {
					setText(FlexoLocalization.localizedForKey("undo") + " ("
							+ _controller.getEditor().getUndoManager().getNextUndoAction().getLocalizedName() + ")");
				} else {
					setText(FlexoLocalization.localizedForKey("undo"));
				}
			} else {
				setText(FlexoLocalization.localizedForKey("undo"));
				setEnabled(false);
			}
		}

		@Override
		public void itemWillShow() {

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
			manager.addListener(ControllerModel.CURRENT_EDITOR, this, _controller.getControllerModel());
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == _controller) {
				if (evt.getOldValue() != null) {
					FlexoEditor old = (FlexoEditor) evt.getOldValue();
					if (old.getUndoManager() != null) {
						manager.removeListener(UndoManager.ACTION_HISTORY, this, old.getUndoManager());
						manager.removeListener(UndoManager.ENABLED, this, old.getUndoManager());
					}
				}
				if (evt.getNewValue() != null) {
					FlexoEditor editor = (FlexoEditor) evt.getNewValue();
					if (editor.getUndoManager() != null) {
						manager.addListener(UndoManager.ACTION_HISTORY, this, editor.getUndoManager());
						manager.addListener(UndoManager.ENABLED, this, editor.getUndoManager());
					}
					updateWithUndoManagerState();
				}
			} else {
				if (evt.getPropertyName().equals(UndoManager.ACTION_HISTORY) || evt.getPropertyName().equals(UndoManager.ENABLED)) {
					updateWithUndoManagerState();
				}
			}
		}

		private void updateWithUndoManagerState() {
			if (_controller.getEditor().getUndoManager() != null) {
				setEnabled(_controller.getEditor().getUndoManager().isRedoActive());
				if (_controller.getEditor().getUndoManager().isRedoActive()) {
					setText(FlexoLocalization.localizedForKey("redo") + " ("
							+ _controller.getEditor().getUndoManager().getNextRedoAction().getLocalizedName() + ")");
				} else {
					setText(FlexoLocalization.localizedForKey("redo"));
				}
			} else {
				setEnabled(false);
				setText(FlexoLocalization.localizedForKey("redo"));
			}
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
