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
package org.openflexo.components.widget;

import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.openflexo.swing.DurationSelector;
import org.openflexo.toolbox.Duration;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.WidgetFocusListener;
import org.openflexo.localization.FlexoLocalization;

/**
 * widget for selecting a duration...
 * 
 * @author sylvain
 * 
 */
public class DurationInspectorWidget extends CustomInspectorWidget<Duration> {

	static final Logger logger = Logger.getLogger(DurationInspectorWidget.class.getPackage().getName());

	DurationSelector _selector;

	public DurationInspectorWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		_selector = new DurationSelector() {

			@Override
			public String localizedForKey(String aKey) {
				return FlexoLocalization.localizedForKey(aKey);
			}

			@Override
			public String localizedForKeyAndButton(String key, JButton component) {
				return FlexoLocalization.localizedForKey(key, component);
			}

			@Override
			public void apply() {
				super.apply();
				updateModelFromWidget();
			}

			@Override
			public void cancel() {
				super.cancel();
				updateModelFromWidget();
			}

		};
		getDynamicComponent().addFocusListener(new WidgetFocusListener(this) {
			@Override
			public void focusGained(FocusEvent arg0) {
				if (logger.isLoggable(Level.FINE))
					logger.fine("Focus gained in " + getClass().getName());
				super.focusGained(arg0);
				_selector.getTextField().requestFocus();
				_selector.getTextField().selectAll();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				if (logger.isLoggable(Level.FINE))
					logger.fine("Focus lost in " + getClass().getName());
				super.focusLost(arg0);
			}
		});
	}

	@Override
	public synchronized void updateWidgetFromModel() {
		if (modelUpdating)
			return;
		widgetUpdating = true;
		try {
			_selector.setEditedObject(getObjectValue());
			_selector.setRevertValue(getObjectValue());
		} finally {
			widgetUpdating = false;
		}
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
		if (widgetUpdating)
			return;
		modelUpdating = true;
		try {
			setObjectValue(_selector.getEditedObject() != null ? _selector.getEditedObject().clone() : null);
			super.updateModelFromWidget();
		} finally {
			modelUpdating = false;
		}
	}

	@Override
	public JComponent getDynamicComponent() {
		return _selector;
	}

	@Override
	public Class getDefaultType() {
		return Duration.class;
	}

	@Override
	public void fireEditingCanceled() {
		if (_selector != null)
			_selector.closePopup();
	}

	@Override
	public void fireEditingStopped() {
		if (_selector != null)
			_selector.closePopup();
	}

}
