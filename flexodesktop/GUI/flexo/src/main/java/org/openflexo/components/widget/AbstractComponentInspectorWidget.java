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

import javax.swing.JComponent;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.WidgetFocusListener;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class AbstractComponentInspectorWidget extends CustomInspectorWidget {

	static final Logger logger = Logger.getLogger(AbstractComponentInspectorWidget.class.getPackage().getName());

	AbstractComponentSelector _selector;

	public AbstractComponentInspectorWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		_selector = createComponentSelector();
		getDynamicComponent().addFocusListener(new WidgetFocusListener(this) {
			@Override
			public void focusGained(FocusEvent arg0) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Focus gained in " + getClass().getName());
				}
				super.focusGained(arg0);
				_selector.getTextField().requestFocusInWindow();
				_selector.getTextField().selectAll();
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

	protected abstract AbstractComponentSelector createComponentSelector();

	@Override
	public abstract Class getDefaultType();

	@Override
	public synchronized void updateWidgetFromModel() {
		_selector.setEditedObject(getObjectValue());
		_selector.setRevertValue((FlexoModelObject) getObjectValue());
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
		setObjectValue(_selector.getEditedObject());
		super.updateModelFromWidget();
	}

	@Override
	public JComponent getDynamicComponent() {
		return _selector;
	}

	@Override
	public void setProject(FlexoProject aProject) {
		super.setProject(aProject);
		_selector.setProject(aProject);
	}
}
