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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.localization.FlexoLocalization;

/**
 * Represents a widget able to edit a Color or a StringConvertable object able to be instanciated using a String under the form:
 * '$RED,$GREEN,$BLUE'.
 * 
 * @author sguerin
 */
public class ColorWidget extends DenaliWidget {

	private static final Logger logger = Logger.getLogger(ColorWidget.class.getPackage().getName());

	protected JPanel _mySmallPanel;

	protected JButton _chooseButton;

	protected JPanel _currentColorLabel;

	protected Color _color;

	public ColorWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		_mySmallPanel = new JPanel(new BorderLayout());
		_chooseButton = new JButton();
		_chooseButton.setText(FlexoLocalization.localizedForKey("choose", _chooseButton));
		addActionListenerToChooseButton();
		_color = Color.WHITE;
		_currentColorLabel = new JPanel();
		_mySmallPanel.add(_currentColorLabel, BorderLayout.CENTER);
		if (!isReadOnly()) {
			_mySmallPanel.add(_chooseButton, BorderLayout.EAST);
			_currentColorLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						showColorChooserDialog();
					}
				}
			});
		}
		getDynamicComponent().addFocusListener(new WidgetFocusListener(this));

	}

	public void addActionListenerToChooseButton() {
		_chooseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showColorChooserDialog();
			}
		});
	}

	@Override
	public synchronized void updateWidgetFromModel() {
		widgetUpdating = true;
		Object value = getObjectValue();
		if (value instanceof Color) {
			setColor((Color) value);
		} else {
			if (value != null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Property " + _propertyModel.name + " is supposed to be a Color or a StringConvertable object, not a "
							+ value);
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
		if (isReadOnly()) {
			return;
		}
		if (getType() == Color.class) {
			setObjectValue(_color);
		} else if (typeIsStringConvertable()) {
			setObjectValue(getTypeConverter().convertFromString(_color.getRed() + "," + _color.getGreen() + "," + _color.getBlue()));
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Property " + _propertyModel.name + " is supposed to be a Color, not a " + getType());
			}
		}
	}

	@Override
	public JComponent getDynamicComponent() {
		return _mySmallPanel;
	}

	protected void setColor(Color aColor) {
		_color = aColor;
		if (_color != null) {
			_currentColorLabel.setBackground(aColor);
			_currentColorLabel.repaint();
		}
	}

	@Override
	public Class getDefaultType() {
		return Color.class;
	}

	/**
	 * 
	 */
	protected void showColorChooserDialog() {
		// get the new color
		setColor(JColorChooser.showDialog(_chooseButton, FlexoLocalization.localizedForKey("select_a_color"), _color));
		if (_color != null) {
			updateModelFromWidget();
		}
	}

}
