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
 * FlexoMenuItem.java
 * Project WorkflowEditor
 * 
 * Created by benoit on Mar 12, 2004
 */

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.selection.SelectionManager;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.action.EditionAction;

/**
 * Give a shortcut to the item and register the action near the FlexoMainController
 * 
 * @author benoit
 */
public class FlexoMenuItem extends JMenuItem implements FlexoActionSource, PropertyChangeListener {

	private static final Logger logger = FlexoLogger.getLogger(FlexoMenuItem.class.getPackage().getName());

	private final FlexoController _controller;

	private FlexoActionType actionType;

	public FlexoMenuItem(FlexoController controller, String unlocalizedMenuName) {
		super();
		_controller = controller;
		setText(FlexoLocalization.localizedForKey(unlocalizedMenuName, this));
	}

	public FlexoMenuItem(AbstractAction action, String flexoActionName, KeyStroke accelerator, FlexoController controller,
			boolean localizeActionName) {
		super(action);
		_controller = controller;
		if (accelerator != null) {
			setAccelerator(accelerator);
			_controller.registerActionForKeyStroke(action, accelerator);
		}
		if (localizeActionName) {
			setText(FlexoLocalization.localizedForKey(flexoActionName, this));
		} else {
			setText(flexoActionName);
		}
	}

	public FlexoMenuItem(AbstractAction action, String flexoActionName, KeyStroke accelerator, Icon icon, FlexoController controller) {
		this(action, flexoActionName, accelerator, controller, true);
		setIcon(icon);
	}

	public FlexoMenuItem(FlexoActionType actionType, Icon icon, FlexoController controller) {
		this(actionType, controller);
		setIcon(icon);
	}

	public FlexoMenuItem(FlexoActionType actionType, FlexoController controller) {
		super();
		this.actionType = actionType;
		_controller = controller;
		setAction(new EditionAction(actionType, this));
		setText(FlexoLocalization.localizedForKey(actionType.getUnlocalizedName(), this));
	}

	@Override
	public FlexoModelObject getFocusedObject() {
		return _controller.getSelectionManager().getLastSelectedObject();
	}

	@Override
	public Vector<FlexoModelObject> getGlobalSelection() {
		return _controller.getSelectionManager().getSelection();
	}

	@Override
	public FlexoEditor getEditor() {
		return _controller.getEditor();
	}

	private SelectionManager getSelectionManager() {
		return _controller.getSelectionManager();
	}

	/**
     * 
     */
	public void itemWillShow() {
		if (actionType instanceof FlexoActionType && getSelectionManager() != null) {
			if (getFocusedObject() == null || getFocusedObject().getActionList().indexOf(actionType) > -1) {
				setEnabled(actionType.isEnabled(getFocusedObject(), getGlobalSelection(), _controller.getEditor()));
			} else {
				setEnabled(false);
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Nothing is done by default
	}
}
