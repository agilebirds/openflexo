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
package org.openflexo.view.listener;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;

import org.openflexo.ch.FCH;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.localization.FlexoLocalization;

public class FlexoActionButton extends JButton {
	private final ButtonAction action;
	private final FlexoActionSource actionSource;
	private final FlexoEditor _editor;

	public FlexoActionButton(FlexoActionType actionType, FlexoActionSource source, FlexoEditor editor) {
		this(actionType, null, source, editor);
	}

	public FlexoActionButton(FlexoActionType actionType, String unlocalizedActionName, FlexoActionSource source, FlexoEditor editor) {
		super();
		_editor = editor;
		actionSource = source;
		action = new ButtonAction(actionType, unlocalizedActionName);
		setText(action.getLocalizedName(this));
		setToolTipText(action.getLocalizedName(this));
		if (editor.getEnabledIconFor(actionType) != null) {
			setIcon(editor.getEnabledIconFor(actionType));
		}
		if (editor.getDisabledIconFor(actionType) != null) {
			setDisabledIcon(editor.getDisabledIconFor(actionType));
		}
		addActionListener(action);
		FCH.setHelpItem(this, action.getActionType().getUnlocalizedName());
	}

	public void update() {
		setEnabled(action.isEnabled());
	}

	protected Vector getGlobalSelection() {
		return actionSource.getGlobalSelection();
	}

	protected FlexoModelObject getFocusedObject() {
		return actionSource.getFocusedObject();
	}

	public class ButtonAction implements ActionListener {

		private final FlexoActionType _actionType;
		private String _unlocalizedName = null;

		public ButtonAction(FlexoActionType actionType) {
			super();
			_actionType = actionType;
		}

		public ButtonAction(FlexoActionType actionType, String actionName) {
			this(actionType);
			_unlocalizedName = actionName;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			FlexoAction action = _actionType.makeNewAction(getFocusedObject(), getGlobalSelection(), _editor);
			action.setInvoker(actionSource);
			action.actionPerformed(event);
		}

		public boolean isEnabled() {
			return _actionType.isEnabled(getFocusedObject(), getGlobalSelection(), _editor);
		}

		public FlexoActionType getActionType() {
			return _actionType;
		}

		public String getLocalizedName(Component component) {
			if (_unlocalizedName == null) {
				return _actionType.getLocalizedName(component);

			} else {
				return FlexoLocalization.localizedForKey(_unlocalizedName, component);
			}
		}

	}
}
