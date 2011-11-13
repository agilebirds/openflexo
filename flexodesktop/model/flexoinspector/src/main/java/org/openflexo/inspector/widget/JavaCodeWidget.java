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

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.jedit.InputHandler.InputListener;
import org.openflexo.jedit.cd.JavaCodeDisplayer;

/**
 * Represents a widget able to view/edit Java code
 * 
 * @author sguerin
 */
public class JavaCodeWidget extends DenaliWidget<String> {

	private static final Logger logger = Logger.getLogger(JavaCodeWidget.class.getPackage().getName());

	public static final String COLUMNS = "columns";
	public static final String ROWS = "rows";
	public static final String READ_ONLY = "readOnly";
	public static final String VALIDATE_ON_RETURN = "validateOnReturn";

	private static final int DEFAULT_COLUMNS = 10;
	private static final int DEFAULT_ROWS = 4;
	public static Font FIXED_SIZE_FONT = new Font("Monospaced", Font.PLAIN, 11);
	public static Font SANS_SERIF_FONT = new Font("SansSerif", Font.PLAIN, 11);

	private JavaCodeDisplayer _textArea;
	boolean validateOnReturn;

	public JavaCodeWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		_textArea = new JavaCodeDisplayer(getStringValue());
		_textArea.setEditable(true);

		if (model.hasValueForParameter(VALIDATE_ON_RETURN)) {
			validateOnReturn = model.getBooleanValueForParameter(VALIDATE_ON_RETURN);
		} else {
			validateOnReturn = true;
		}

		if (model.hasValueForParameter(READ_ONLY)) {
			_textArea.setEditable(!model.getBooleanValueForParameter(READ_ONLY));
		} else {
			_textArea.setEditable(true);
		}

		if (model.hasValueForParameter(COLUMNS)) {
			_textArea.setColumns(model.getIntValueForParameter(COLUMNS));
		} else {
			_textArea.setColumns(DEFAULT_COLUMNS);
		}

		if (model.hasValueForParameter(ROWS)) {
			_textArea.setRows(model.getIntValueForParameter(ROWS));
		} else {
			_textArea.setRows(DEFAULT_ROWS);
		}

		if (model.hasValueForParameter("font")) {
			if (model.getValueForParameter("font").equals("SansSerif")) {
				_textArea.setFont(SANS_SERIF_FONT);
			} else if (model.getValueForParameter("font").equals("FixedSize")) {
				_textArea.setFont(FIXED_SIZE_FONT);
			}
		}

		_textArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if ((!validateOnReturn) && (!widgetUpdating))
					updateModelFromWidget();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if ((!validateOnReturn) && (!widgetUpdating))
					updateModelFromWidget();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if ((!validateOnReturn) && (!widgetUpdating))
					updateModelFromWidget();
			}
		});

		_textArea.getInputHandler().addToInputListener(new InputListener() {
			@Override
			public void enterPressed(KeyEvent event) {
				updateModelFromWidget();
			}
		});

		_textArea.getPainter().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateModelFromWidget();
			}
		});
		_textArea.addFocusListener(new WidgetFocusListener(this));

		_textArea.setAutoscrolls(true);
		_textArea.setBorder(BorderFactory.createLoweredBevelBorder());
		_textArea.setEnabled(true);
	}

	@Override
	public Class getDefaultType() {
		return String.class;
	}

	@Override
	public synchronized void updateWidgetFromModel() {
		widgetUpdating = true;
		if (logger.isLoggable(Level.FINE))
			logger.fine("updateWidgetFromModel() in JavaCodeWidget with " + getStringValue());
		int oldPosition = _textArea.getCaretPosition();
		_textArea.setText(getStringValue());
		if (oldPosition < _textArea.getDocumentLength())
			_textArea.setCaretPosition(oldPosition);
		widgetUpdating = false;

	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
		modelUpdating = true;
		if (logger.isLoggable(Level.FINE))
			logger.fine("updateModelFromWidget() in JavaCodeWidget with " + _textArea.getText());
		setStringValue(_textArea.getText());
		modelUpdating = false;
	}

	@Override
	public JComponent getDynamicComponent() {
		return _textArea;
	}

	@Override
	public boolean defaultShouldExpandVertically() {
		return true;
	}

}
