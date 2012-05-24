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
package org.openflexo.fib.view.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a widget able to edit a Text (more than one line) object
 * 
 * @author bmangez,sguerin
 */
public class FIBTextAreaWidget extends FIBWidgetView<FIBTextArea, JTextArea, String> {

	private static final Logger logger = Logger.getLogger(FIBTextAreaWidget.class.getPackage().getName());

	private static final int DEFAULT_COLUMNS = 30;
	private static final int DEFAULT_ROWS = 5;

	private JPanel panel;
	private final JTextArea textArea;
	private JScrollPane scrollPane;
	boolean validateOnReturn;

	public FIBTextAreaWidget(FIBTextArea model, FIBController controller) {
		super(model, controller);
		textArea = new JTextArea();
		panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.add(textArea, BorderLayout.CENTER);
		validateOnReturn = model.validateOnReturn;
		if (model.columns != null && model.columns > 0) {
			textArea.setColumns(model.columns);
		} else {
			textArea.setColumns(DEFAULT_COLUMNS);
		}
		if (model.rows != null && model.rows > 0) {
			textArea.setRows(model.rows);
		} else {
			textArea.setRows(DEFAULT_ROWS);
		}
		Border border;
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			border = BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER, BOTTOM_COMPENSATING_BORDER,
					RIGHT_COMPENSATING_BORDER);
		} else {
			border = BorderFactory.createEmptyBorder(2, 3, 2, 3);
		}
		border = BorderFactory.createCompoundBorder(border, BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		panel.setBorder(border);
		/*else {
				textArea.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
				}*/

		textArea.setEditable(!isReadOnly());
		if (model.text != null) {
			textArea.setText(model.text);
		}

		textArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if (!validateOnReturn && !widgetUpdating) {
					updateModelFromWidget();
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (!validateOnReturn && !widgetUpdating) {
					updateModelFromWidget();
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (!validateOnReturn && !widgetUpdating) {
					updateModelFromWidget();
				}
			}
		});
		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					updateModelFromWidget();
				}
			}
		});
		textArea.addFocusListener(this);

		textArea.setAutoscrolls(true);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEnabled(true);
		/*pane = new JScrollPane(_textArea);
		
		pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		pane.setMinimumSize(MINIMUM_SIZE);*/

		updateFont();
	}

	@Override
	public void focusGained(FocusEvent event) {
		super.focusGained(event);
		textArea.selectAll();
	}

	@Override
	public void updateDataObject(Object aDataObject) {
		super.updateDataObject(aDataObject);
		textArea.setEditable(!isReadOnly());
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		if (notEquals(getValue(), textArea.getText())) {
			if (modelUpdating) {
				return false;
			}
			if (getValue() != null && (getValue() + "\n").equals(textArea.getText())) {
				return false;
			}
			widgetUpdating = true;
			try {
				textArea.setText(getValue());
			} finally {
				widgetUpdating = false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		if (notEquals(getValue(), textArea.getText())) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateModelFromWidget() in TextAreaWidget");
			}
			modelUpdating = true;
			try {
				setValue(textArea.getText());
			} finally {
				modelUpdating = false;
			}
			return true;
		}
		return false;
	}

	@Override
	public JPanel getJComponent() {
		return panel;
	}

	@Override
	public JTextArea getDynamicJComponent() {
		return textArea;
	}

	/**
	 * Return the effective component to be added to swing hierarchy This component may be the same as the one returned by
	 * {@link #getJComponent()} or a encapsulation in a JScrollPane
	 * 
	 * @return JComponent
	 */
	@Override
	public JComponent getResultingJComponent() {
		if (getComponent().getUseScrollBar()) {
			if (scrollPane == null) {
				scrollPane = new JScrollPane(textArea, getComponent().getVerticalScrollbarPolicy().getPolicy(), getComponent()
						.getHorizontalScrollbarPolicy().getPolicy());
				scrollPane.setOpaque(false);
				scrollPane.getViewport().setOpaque(false);
				scrollPane.setBorder(BorderFactory.createEmptyBorder());
			}
			panel.add(scrollPane);
			return panel;
		} else {
			return getJComponent();
		}
	}

	@Override
	public void updateFont() {
		super.updateFont();
		if (getFont() != null) {
			textArea.setFont(getFont());
		}
	}
}
