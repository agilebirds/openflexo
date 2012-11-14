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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.toolbox.ToolBox;

/**
 * Simple widget allowing to display/edit a String
 * 
 * @author bmangez
 */
public class TextFieldWidget extends DenaliWidget<String> {

	private static final Logger logger = Logger.getLogger(TextFieldWidget.class.getPackage().getName());

	private JTextField _textField;

	boolean validateOnReturn;

	// private boolean hasFocus = false;

	private static final int DEFAULT_COLUMNS = 5;

	public static final String COLUMNS_PARAM = "columns";
	public static final String PASSWORD_PARAM = "password";

	public static final String VALIDATE_ON_RETURN = "validateOnReturn";

	public TextFieldWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		if (model.hasValueForParameter(PASSWORD_PARAM) && model.getBooleanValueForParameter(PASSWORD_PARAM)) {
			_textField = new JPasswordField() {
				/**
				 * Overrides getMinimumSize
				 * 
				 * @see javax.swing.JComponent#getMinimumSize()
				 */
				@Override
				public Dimension getMinimumSize() {
					return MINIMUM_SIZE;
				}
			};
		} else {
			_textField = new JTextField() {
				/**
				 * Overrides getMinimumSize
				 * 
				 * @see javax.swing.JComponent#getMinimumSize()
				 */
				@Override
				public Dimension getMinimumSize() {
					return MINIMUM_SIZE;
				}
			};
		}
		if (model.hasValueForParameter(VALIDATE_ON_RETURN)) {
			validateOnReturn = model.getBooleanValueForParameter(VALIDATE_ON_RETURN);
		} else {
			validateOnReturn = false;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("validateOnReturn=" + validateOnReturn);
		}
		if (model.hasValueForParameter(COLUMNS_PARAM)) {
			int colNb = model.getIntValueForParameter(COLUMNS_PARAM);
			_textField.setColumns(colNb > 0 ? colNb : DEFAULT_COLUMNS);
		} else {
			_textField.setColumns(DEFAULT_COLUMNS);
		}
		_textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				// if (logger.isLoggable(Level.FINE)) logger.finer
				// ("changedUpdate() validateOnReturn="+validateOnReturn+"
				// widgetUpdating="+widgetUpdating);
				if (!validateOnReturn && !widgetUpdating) {
					// if (logger.isLoggable(Level.FINE)) logger.fine
					// ("changedUpdate()");e
					updateModelFromWidget();
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				// if (logger.isLoggable(Level.FINE)) logger.finer
				// ("insertUpdate() validateOnReturn="+validateOnReturn+"
				// widgetUpdating="+widgetUpdating);
				if (!validateOnReturn && !widgetUpdating) {
					// if (logger.isLoggable(Level.FINE)) logger.fine
					// ("insertUpdate()");
					try {
						if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
							if (e.getLength() == 1) {
								char c = _textField.getText().charAt(e.getOffset());
								if (c == '´' || c == 'ˆ' || c == '˜' || c == '`' || c == '¨') {
									return;
								}
							}
						}
					} catch (RuntimeException e1) {
						e1.printStackTrace();
					}

					updateModelFromWidget();
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// if (logger.isLoggable(Level.FINE)) logger.finer
				// ("removeUpdate() validateOnReturn="+validateOnReturn+"
				// widgetUpdating="+widgetUpdating);
				if (!validateOnReturn && !widgetUpdating) {
					// if (logger.isLoggable(Level.FINE)) logger.fine
					// ("removeUpdate()");
					updateModelFromWidget();
				}
			}
		});
		_textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// if (logger.isLoggable(Level.FINE)) logger.fine
				// ("actionPerformed()");
				updateModelFromWidget();
			}
		});
		getDynamicComponent().addFocusListener(new WidgetFocusListener(this) {
			@Override
			public void focusGained(FocusEvent arg0) {
				super.focusGained(arg0);
				((JTextField) getDynamicComponent()).selectAll();
			}
		});
	}

	@Override
	public Class getDefaultType() {
		return String.class;
	}

	@Override
	public synchronized void updateWidgetFromModel() {
		// if (logger.isLoggable(Level.FINE)) logger.fine ("BEGIN
		// updateWidgetFromModel()");
		if (modelUpdating) {
			return;
		}
		widgetUpdating = true;
		int caret = _textField.getCaretPosition();
		_textField.setText(getStringValue());
		if (caret > -1 && caret < _textField.getDocument().getLength()) {
			_textField.setCaretPosition(caret);
		}
		widgetUpdating = false;
		// if (logger.isLoggable(Level.INFO)) logger.info ("END
		// updateWidgetFromModel()");
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
		// if (logger.isLoggable(Level.INFO)) logger.info ("BEGIN
		// updateModelFromWidget()");
		modelUpdating = true;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("updateModelFromWidget() in TextFieldWidget");
		}
		setStringValue(_textField.getText());
		modelUpdating = false;
		// if (logger.isLoggable(Level.INFO)) logger.info ("END
		// updateModelFromWidget()");
	}

	@Override
	public JComponent getDynamicComponent() {
		return _textField;
	}

}
