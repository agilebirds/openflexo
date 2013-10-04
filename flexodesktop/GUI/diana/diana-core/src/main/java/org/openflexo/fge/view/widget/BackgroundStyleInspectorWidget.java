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
package org.openflexo.fge.view.widget;

import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.CustomWidget;
import org.openflexo.inspector.widget.WidgetFocusListener;

@Deprecated
public class BackgroundStyleInspectorWidget extends CustomWidget<BackgroundStyle> {

	static final Logger logger = Logger.getLogger(BackgroundStyleInspectorWidget.class.getPackage().getName());

	FIBBackgroundStyleSelector _selector;

	public BackgroundStyleInspectorWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		_selector = new FIBBackgroundStyleSelector(null) {
			@Override
			public void setEditedObject(BackgroundStyle aBackgroundStyle) {
				super.setEditedObject(aBackgroundStyle);
				updateModelFromWidget();
			}
		};
		getDynamicComponent().addFocusListener(new WidgetFocusListener(this) {
			@Override
			public void focusGained(FocusEvent arg0) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Focus gained in " + getClass().getName());
				}
				super.focusGained(arg0);
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Focus lost in " + getClass().getName());
				}
				super.focusLost(arg0);
			}
		});
	}

	@Override
	public synchronized void updateWidgetFromModel() {
		widgetUpdating = true;
		_selector.setEditedObject(getObjectValue());
		_selector.setRevertValue(getObjectValue());
		widgetUpdating = false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
		modelUpdating = true;
		setObjectValue(_selector.getEditedObject());
		modelUpdating = false;
	}

	@Override
	public JComponent getDynamicComponent() {
		return _selector;
	}

	@Override
	public Class getDefaultType() {
		return BackgroundStyle.class;
	}

	@Override
	public void fireEditingCanceled() {
		if (_selector != null) {
			_selector.closePopup();
		}
	}

	@Override
	public void fireEditingStopped() {
		if (_selector != null) {
			_selector.closePopup();
		}
	}

	@Override
	public boolean defaultShouldExpandHorizontally() {
		return false;
	}

}
