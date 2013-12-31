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
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;

import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.ch.FCH;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.FlexoController;

public class FlexoActionButton extends JButton {
	private final ButtonAction action;
	private final FlexoActionSource actionSource;
	private final FlexoController controller;

	public FlexoActionButton(FlexoActionType<?, ?, ?> actionType, FlexoActionSource source, FlexoController controller) {
		this(actionType, null, source, controller);
	}

	public FlexoActionButton(FlexoActionType<?, ?, ?> actionType, String unlocalizedActionName, FlexoActionSource source,
			FlexoController controller) {
		super();
		actionSource = source;
		this.controller = controller;
		action = new ButtonAction(actionType, unlocalizedActionName);
		setText(action.getLocalizedName(this));
		setToolTipText(FlexoLocalization.localizedTooltipForKey(action._unlocalizedName, this));
		if (getEditor() != null) {
			if (getEditor().getEnabledIconFor(actionType) != null) {
				setIcon(getEditor().getEnabledIconFor(actionType));
			}
			if (getEditor().getDisabledIconFor(actionType) != null) {
				setDisabledIcon(getEditor().getDisabledIconFor(actionType));
			}
		}
		addActionListener(action);
		FCH.setHelpItem(this, action.getActionType().getUnlocalizedName());
	}

	private FlexoEditor getEditor() {
		if (controller != null) {
			return controller.getEditor();
		} else {
			return null;
		}
	}

	public void update() {
		setEnabled(action.isEnabled());
	}

	protected List<? extends FlexoObject> getGlobalSelection() {
		return actionSource.getGlobalSelection();
	}

	protected FlexoObject getFocusedObject() {
		return actionSource.getFocusedObject();
	}

	public class ButtonAction<A extends FlexoAction<A, T1, T2>, T1 extends FlexoObject, T2 extends FlexoObject> implements ActionListener {

		private final FlexoActionType<A, T1, T2> actionType;
		private String _unlocalizedName = null;

		public ButtonAction(FlexoActionType<A, T1, T2> actionType) {
			super();
			this.actionType = actionType;
		}

		public ButtonAction(FlexoActionType<A, T1, T2> actionType, String actionName) {
			this(actionType);
			_unlocalizedName = actionName;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			List<? extends FlexoObject> globalSelection = getGlobalSelection();
			if (TypeUtils.isAssignableTo(getFocusedObject(), actionType.getFocusedObjectType())
					&& (globalSelection == null || TypeUtils.isAssignableTo(globalSelection, actionType.getGlobalSelectionType()))) {
				getEditor().performActionType(actionType, (T1) getFocusedObject(), (Vector<T2>) globalSelection, event);
			}
		}

		public boolean isEnabled() {
			List<? extends FlexoObject> globalSelection = getGlobalSelection();
			if (TypeUtils.isAssignableTo(getFocusedObject(), actionType.getFocusedObjectType())
					&& (globalSelection == null || TypeUtils.isAssignableTo(globalSelection, actionType.getGlobalSelectionType()))) {
				return getEditor().isActionEnabled(actionType, (T1) getFocusedObject(), (Vector<T2>) globalSelection);
			}
			return false;
		}

		public FlexoActionType<A, T1, T2> getActionType() {
			return actionType;
		}

		public String getLocalizedName(Component component) {
			if (_unlocalizedName == null) {
				return actionType.getLocalizedName(component);

			} else {
				return FlexoLocalization.localizedForKey(_unlocalizedName, component);
			}
		}

	}
}
