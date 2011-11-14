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
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.FontChooser;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Represents a widget able to edit a Font or a StringConvertable object able to be instanciated using a String under the form:
 * '$RED,$GREEN,$BLUE'.
 * 
 * @author sguerin
 */
public class FontWidget extends DenaliWidget {

	private static final Logger logger = Logger.getLogger(FontWidget.class.getPackage().getName());

	protected JPanel _mySmallPanel;

	protected JButton _chooseButton;

	protected JLabel _currentFontLabel;

	protected Font _font;

	private String sampleTextKey;
	private static final String DEFAULT_SAMPLE_TEXT_KEY = "here_s_a_sample_of_this_font";

	public FontWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		_mySmallPanel = new JPanel(new BorderLayout());
		_chooseButton = new JButton();
		_chooseButton.setText(FlexoLocalization.localizedForKey("choose", _chooseButton));
		addActionListenerToChooseButton();
		_font = FontChooser.NORMAL_FONT;

		_currentFontLabel = new JLabel();
		if (model.hasValueForParameter("sampleText")) {
			sampleTextKey = model.getValueForParameter("sampleText");
		} else {
			sampleTextKey = DEFAULT_SAMPLE_TEXT_KEY;
		}
		_currentFontLabel.setText(FlexoLocalization.localizedForKey(sampleTextKey, _currentFontLabel));

		_mySmallPanel.add(_currentFontLabel, BorderLayout.CENTER);
		_mySmallPanel.add(_chooseButton, BorderLayout.EAST);
		getDynamicComponent().addFocusListener(new WidgetFocusListener(this));

	}

	public void addActionListenerToChooseButton() {
		_chooseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// get the new color
				Window parent = SwingUtilities.getWindowAncestor(_chooseButton);
				Font f = FontChooser.showDialog(parent, _font);
				if (f != null) {
					setTheFont(f);
					updateModelFromWidget();
				} else {
					updateWidgetFromModel();
				}
			}
		});
	}

	@Override
	public synchronized void updateWidgetFromModel() {
		widgetUpdating = true;
		try {
			Object object = getObjectValue();
			if (object == null) {
				setTheFont(null);
				return;
			}
			if (object instanceof Font) {
				setTheFont((Font) object);
			} else if (typeIsStringConvertable()) {
				try {
					Method m = object.getClass().getMethod("getFont", new Class[] {});
					setTheFont((Font) m.invoke(object, new Object[] {}));
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Property " + _propertyModel.name + " is supposed to be a Font or a StringConvertable object, not a "
							+ object);
				}
			}
		} finally {
			widgetUpdating = false;
		}
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
		modelUpdating = true;
		try {
			if (getType() == Font.class) {
				setObjectValue(_font);
			} else if (typeIsStringConvertable()) {
				// System.out.println("getType()="+getType());
				// System.out.println("_font="+_font);
				if (getTypeConverter() == null) {
					// System.out.println("getTypeConverter()==null");
					// _typeConverter = ((StringConvertable)
					// getType()).getConverter();
					try {
						Method m = getType().getMethod("getConverterStatic", new Class[] {});
						_typeConverter = (Converter) m.invoke(getType(), new Object[] {});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				setObjectValue(getTypeConverter().convertFromString(_font.getName() + "," + _font.getStyle() + "," + _font.getSize()));
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Property " + _propertyModel.name + " is supposed to be a Color, not a " + getType());
				}
			}
		} finally {
			modelUpdating = false;
		}
	}

	@Override
	public JComponent getDynamicComponent() {
		return _mySmallPanel;
	}

	protected void setTheFont(Font aFont) {
		_font = aFont;
		if (_font != null) {
			_currentFontLabel.setFont(aFont);
			_currentFontLabel.repaint();
		}
	}

	@Override
	public Class getDefaultType() {
		return Font.class;
	}

}
