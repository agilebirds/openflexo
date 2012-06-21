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
package org.openflexo.inspector.widget;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.kvc.KeyValueCoding;
import org.openflexo.swing.JRadioButtonWithIcon;

/**
 * @author bmangez
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class RadioButtonListWidget extends MultipleValuesWidget {
	private static final Logger logger = Logger.getLogger(RadioButtonListWidget.class.getPackage().getName());

	private static final int DEFAULT_COLUMNS = 1;

	Object _selectedObject;

	private DenaliListModel listModel;

	private JRadioButton[] _radioButtonArray;

	private JPanel _panel;

	private ButtonGroup _buttonGroup;

	private int columns;

	/**
	 * @param model
	 */
	public RadioButtonListWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		if (model.hasValueForParameter("columns")) {
			columns = model.getIntValueForParameter("columns");
		} else {
			columns = DEFAULT_COLUMNS;
		}
		_panel = new JPanel();
		getDynamicComponent().addFocusListener(new WidgetFocusListener(this));

	}

	private class MyRadioButtonListener implements ActionListener {
		JRadioButton rb;

		Object val;

		public MyRadioButtonListener(JRadioButton radioButton, Object v) {
			super();
			rb = radioButton;
			val = v;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (rb.isSelected()) {
				_selectedObject = val;
				updateModelFromWidget();
			}
		}
	}

	@Override
	public synchronized void updateWidgetFromModel() {
		widgetUpdating = true;
		_selectedObject = getObjectValue();
		listModel = getListModel();
		_panel.removeAll();
		_buttonGroup = new ButtonGroup();

		int r = listModel.getSize() / columns + (listModel.getSize() % columns >= 1 ? 1 : 0);
		int col = columns;
		// logger.info("Grid: "+r+"x"+col);
		_panel.setLayout(new GridLayout(r, col));

		// _panel.setBackground(InspectorCst.BACK_COLOR);
		_radioButtonArray = new JRadioButton[listModel.getSize()];

		boolean showIcons = false;
		if (getPropertyModel().hasValueForParameter("displayIcon")) {
			showIcons = getPropertyModel().getBooleanValueForParameter("displayIcon");
		}
		for (int i = 0; i < listModel.getSize(); i++) {
			Object curItem = listModel.getElementAt(i);

			JRadioButton rb;
			if (showIcons) {
				rb = new JRadioButtonWithIcon(getStringRepresentation(curItem), getIconFile(curItem), curItem.equals(_selectedObject));
			} else {
				rb = new JRadioButton(getStringRepresentation(curItem), curItem.equals(_selectedObject));
			}
			rb.setOpaque(false);
			// rb.setBackground(InspectorCst.BACK_COLOR);
			rb.addActionListener(new MyRadioButtonListener(rb, curItem));
			JPanel _subPanel = new JPanel(new BorderLayout());
			_subPanel.add(rb, BorderLayout.WEST);
			_subPanel.add(Box.createGlue(), BorderLayout.CENTER);
			_panel.add(_subPanel);
			_radioButtonArray[i] = rb;
			_buttonGroup.add(rb);
		}
		for (int i = 0; i < r * col - listModel.getSize(); i++) {
			_panel.add(new JPanel());
		}
		widgetUpdating = false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
		if (!typeIsString() && typeIsStringConvertable() && _selectedObject instanceof String) {
			setStringValue((String) _selectedObject);
		} else {
			setObjectValue(_selectedObject);
		}
	}

	@Override
	public JComponent getDynamicComponent() {
		return _panel;
	}

	public File getIconFile(Object object) {
		if (object instanceof KeyValueCoding) {
			// return _propertyModel.getFormattedObject((KeyValueCoding) object);
			// //GET FORMATTED OBJECT
			if (getPropertyModel().hasValueForParameter("icon")) {
				try {
					String listAccessor = getPropertyModel().getValueForParameter("icon");
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Get icon file for object" + object + " with icon accessor "
								+ getPropertyModel().getValueForParameter("icon"));
					}
					Object currentObject = PropertyModel.getObjectForMultipleAccessors((KeyValueCoding) object, listAccessor);
					if (currentObject instanceof File) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Get icon file for object" + object + " with icon accessor "
									+ getPropertyModel().getValueForParameter("icon") + " returns " + currentObject);
						}
						return (File) currentObject;
					} else {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Property " + getPropertyModel().name + ": succeeded acces to " + listAccessor
									+ " but answer is not a FILE");
						}
						return null;
					}
				} catch (Exception e) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("getDynamicList() failed for property " + getPropertyModel().name + " for object " + object
								+ " : exception " + e.getMessage());
					}
					return null;
				}
			} else {
				return null;
			}

		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("There is an error in some configuration file :\n the property named '" + _propertyModel.name
						+ "' has no FILE representation formatter ! Object is a " + (object != null ? object.getClass().getName() : "null"));
			}
			return null;
		}
	}
}
