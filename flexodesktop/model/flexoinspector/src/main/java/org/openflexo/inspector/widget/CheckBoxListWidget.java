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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;

/**
 * Represents a widget able to edit a list of objects from a given list (maps a Vector)
 * 
 * @author sguerin
 */
public class CheckBoxListWidget extends MultipleValuesWidget {

	private static final Logger logger = Logger.getLogger(CheckBoxListWidget.class.getPackage().getName());

	private static final int DEFAULT_COLUMNS = 1;

	private DenaliListModel _fullList;

	private Vector _selectedList;

	private JCheckBox[] _checkBoxArray;

	private JPanel _panel;

	private int columns;

	/**
	 * @param model
	 */
	public CheckBoxListWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		_panel = new JPanel();
		_panel.setOpaque(false);
		getDynamicComponent().addFocusListener(new WidgetFocusListener(this));

		if (model.hasValueForParameter("columns")) {
			columns = model.getIntValueForParameter("columns");
		} else {
			columns = DEFAULT_COLUMNS;
		}

	}

	private class MyCheckBoxListener implements ActionListener {
		JCheckBox cb;

		Object val;

		public MyCheckBoxListener(JCheckBox checkBox, Object v) {
			super();
			cb = checkBox;
			val = v;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (cb.isSelected()) {
				addToSelected(val);
			} else {
				removeFromSelected(val);
			}
		}
	}

	@Override
	public synchronized void updateWidgetFromModel() {
		widgetUpdating = true;
		Object objectValue = getObjectValue();
		_fullList = getListModel();
		_panel.removeAll();
		/*if (_fullList.getSize() < 6) {
		    _panel.setLayout(new GridLayout(_fullList.getSize(), 1));
		} else {*/
		_panel.setLayout(new GridLayout(_fullList.getSize() / columns + (_fullList.getSize() % columns > 0 ? 1 : 0), columns));
		// }
		// _panel.setBackground(InspectorCst.BACK_COLOR);
		_checkBoxArray = new JCheckBox[_fullList.getSize()];
		for (int i = 0; i < _fullList.getSize(); i++) {
			Object curItem = _fullList.getElementAt(i);
			JCheckBox cb = new JCheckBox(getStringRepresentation(curItem), false);
			cb.setOpaque(false);
			// cb.setBackground(InspectorCst.BACK_COLOR);
			cb.addActionListener(new MyCheckBoxListener(cb, curItem));
			_panel.add(cb);
			_checkBoxArray[i] = cb;
		}
		_selectedList = new Vector();
		if (objectValue != null) {
			if (objectValue instanceof Vector || objectValue instanceof String && ((String) objectValue).startsWith("[")) {
				if (objectValue instanceof String) {
					objectValue = convertStringToVector((String) objectValue);
				}
				Enumeration en2 = ((Vector) objectValue).elements();

				while (en2.hasMoreElements()) {
					Object cur = en2.nextElement();
					_selectedList.add(cur);
					int i = _fullList.indexOf(cur);
					_checkBoxArray[i].setSelected(true);
				}
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Property " + _propertyModel.name + " is supposed to be a Vector or a String, not a " + getType());
				}
			}
		}
		widgetUpdating = false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
		if (getType() == Vector.class) {
			setObjectValue(_selectedList);
		} else if (getType() == String.class) {
			setObjectValue(convertVectorToString(_selectedList));
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Property " + _propertyModel.name + " is supposed to be a Vector or a String, not a " + getType());
			}
		}
	}

	private Vector convertStringToVector(String s) {
		Vector answer = new Vector();
		s = s.replace('[', ' ');
		s = s.replace(']', ' ');
		s = s.trim();
		StringTokenizer strTok = new StringTokenizer(s, ",", false);
		while (strTok.hasMoreTokens()) {
			answer.add(strTok.nextToken().trim());
		}
		return answer;
	}

	private String convertVectorToString(Vector v) {
		String returned = "[";
		boolean isFirst = true;
		for (Enumeration e = v.elements(); e.hasMoreElements();) {
			String next = (String) e.nextElement();
			if (!isFirst) {
				returned = returned + ",";
				isFirst = false;
			}
			returned = returned + next;
		}
		returned = returned + "]";
		return returned;
	}

	@Override
	public JComponent getDynamicComponent() {
		return _panel;
	}

	public void addToSelected(Object v) {
		_selectedList.add(v);
		updateModelFromWidget();
	}

	public void removeFromSelected(Object v) {
		_selectedList.remove(v);
		updateModelFromWidget();
	}

}
