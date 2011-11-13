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

import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DMTypeOwner;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.WidgetFocusListener;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMTypeInspectorWidget extends CustomInspectorWidget<DMType> {

	protected static final Logger logger = Logger.getLogger(DMTypeInspectorWidget.class.getPackage().getName());

	protected DMTypeSelector _selector;

	public DMTypeInspectorWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		_selector = new DMTypeSelector(null, null, false) {
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
	public Class getDefaultType() {
		return DMType.class;
	}

	@Override
	public synchronized void updateWidgetFromModel() {
		_selector.setEditedObject(getObjectValue() != null ? getObjectValue().clone() : null);
		_selector.setRevertValue(getObjectValue());
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
		if (_selector.getEditedObject() != null)
			setObjectValue(_selector.getEditedObject().clone());
		else
			setObjectValue(null);
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

	@Override
	protected void performModelUpdating(InspectableObject value) {
		if (hasValueForParameter("displayTypeAsSimplified")) {
			if (_selector != null)
				_selector.setDisplayTypeAsSimplified(getBooleanValueForParameter("displayTypeAsSimplified"));
		}
		if (hasValueForParameter("readOnly")) {
			if (_selector != null)
				_selector.setReadOnly(getBooleanValueForParameter("readOnly"));
		}
		if (hasValueForParameter("project")) {
			setProject((FlexoProject) getDynamicValueForParameter("project", value));
		}
		if (hasValueForParameter("owner")) {
			if (getDynamicValueForParameter("owner", value) instanceof DMTypeOwner)
				setOwner((DMTypeOwner) getDynamicValueForParameter("owner", value));
			else if (getDynamicValueForParameter("owner", value) != null)
				logger.warning("Object " + getDynamicValueForParameter("owner", value) + " is not an instanceof DMTypeOwner");
		}
	}

	public void setOwner(DMTypeOwner owner) {
		_selector.setOwner(owner);
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
	public boolean disableTerminateEditOnFocusLost() {
		return true;
	}
}
