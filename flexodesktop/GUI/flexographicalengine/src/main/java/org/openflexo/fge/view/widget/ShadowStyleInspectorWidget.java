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

import org.openflexo.fge.graphics.ShadowStyle;
import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.CustomWidget;
import org.openflexo.inspector.widget.WidgetFocusListener;

@Deprecated
public class ShadowStyleInspectorWidget extends CustomWidget<ShadowStyle> {

	static final Logger logger = Logger.getLogger(ShadowStyleInspectorWidget.class.getPackage().getName());

	FIBShadowStyleSelector _selector;

	public ShadowStyleInspectorWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		_selector = new FIBShadowStyleSelector(null) {
			@Override
			public void setEditedObject(ShadowStyle aShadowStyle) {
				super.setEditedObject(aShadowStyle);
				updateModelFromWidget();
			}
		};
		getDynamicComponent().addFocusListener(new WidgetFocusListener(this) {
			@Override
			public void focusGained(FocusEvent arg0) {
				if (logger.isLoggable(Level.FINE))
					logger.fine("Focus gained in " + getClass().getName());
				super.focusGained(arg0);
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
		widgetUpdating = true;
		// logger.info("getObjectValue()="+getObjectValue());
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
		return ShadowStyle.class;
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

	@Override
	public boolean defaultShouldExpandHorizontally() {
		return false;
	}

}
