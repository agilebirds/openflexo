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
package org.openflexo.fib.editor.view.widget;

import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JButton;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.view.widget.FIBButtonWidget;
import org.openflexo.logging.FlexoLogger;

public class FIBEditableButtonWidget extends FIBButtonWidget implements FIBEditableView<FIBButton, JButton> {

	private static final Logger logger = FlexoLogger.getLogger(FIBEditableButtonWidget.class.getPackage().getName());

	private final FIBEditableViewDelegate<FIBButton, JButton> delegate;

	private final FIBEditorController editorController;

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public FIBEditableButtonWidget(FIBButton model, FIBEditorController editorController) {
		super(model, editorController.getController());
		this.editorController = editorController;

		delegate = new FIBEditableViewDelegate<FIBButton, JButton>(this);
		model.getPropertyChangeSupport().addPropertyChangeListener(this);
	}

	@Override
	public void delete() {
		delegate.delete();
		getComponent().getPropertyChangeSupport().removePropertyChangeListener(this);
		super.delete();
	}

	@Override
	public Vector<PlaceHolder> getPlaceHolders() {
		return null;
	}

	@Override
	public FIBEditableViewDelegate<FIBButton, JButton> getDelegate() {
		return delegate;
	}

	@Override
	public void receivedModelNotifications(FIBModelObject o, String propertyName, Object oldValue, Object newValue) {
		super.receivedModelNotifications(o, propertyName, oldValue, newValue);
		if (propertyName.equals(FIBButton.LABEL_KEY)) {
			updateLabel();
		}
		if (propertyName.equals(FIBButton.BUTTON_ICON_KEY)) {
			updateIcon();
		}
		delegate.receivedModelNotifications(o, propertyName, oldValue, newValue);
	}

	@Override
	public synchronized void buttonClicked() {
		logger.info("Button " + getWidget() + " has clicked: NOT ACTIVE in EDIT mode");
	}

}
