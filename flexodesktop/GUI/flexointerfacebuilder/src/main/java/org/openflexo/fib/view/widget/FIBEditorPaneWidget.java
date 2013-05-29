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
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBEditorPane;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a widget able to edit a Text (more than one line) object
 * 
 * @author bmangez,sguerin
 */
public class FIBEditorPaneWidget extends FIBWidgetView<FIBEditorPane, JEditorPane, String> {

	private static final Logger logger = Logger.getLogger(FIBEditorPaneWidget.class.getPackage().getName());

	private JPanel panel;
	private final JEditorPane editorPane;
	private JScrollPane scrollPane;
	boolean validateOnReturn;

	public FIBEditorPaneWidget(FIBEditorPane model, FIBController controller) {
		super(model, controller);
		editorPane = new JEditorPane() /*{
										@Override
										public void setCaretPosition(int position) {
										logger.info("setCaretPosition with " + position + " in FIBEditorPaneWidget");
										super.setCaretPosition(position);
										};

										@Override
										public void selectAll() {
										logger.info("selectAll() in FIBEditorPaneWidget");
										super.selectAll();
										};

										@Override
										public void setText(String t) {
										logger.info("setText with " + t + " in FIBEditorPaneWidget");
										super.setText(t);
										}
										}*/;
		panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.add(editorPane, BorderLayout.CENTER);
		validateOnReturn = model.isValidateOnReturn();
		Border border;
		if (!ToolBox.isMacOSLaf()) {
			border = BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER, BOTTOM_COMPENSATING_BORDER,
					RIGHT_COMPENSATING_BORDER);
		} else {
			border = BorderFactory.createEmptyBorder(2, 3, 2, 3);
		}
		panel.setBorder(border);
		editorPane.setEditable(!isReadOnly());
		if (model.getText() != null) {
			editorPane.setText(model.getText());
		}

		editorPane.getDocument().addDocumentListener(new DocumentListener() {
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
		editorPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					updateModelFromWidget();
				}
			}
		});
		updateContentType();
		editorPane.addFocusListener(this);

		editorPane.setAutoscrolls(true);
		editorPane.setEnabled(true);

		updateFont();

		editorPane.setCaretPosition(0);
	}

	protected void updateContentType() {
		if (getComponent().getContentType() != null) {
			editorPane.setContentType(getComponent().getContentType().getContentType());
		} else {
			editorPane.setContentType("text/html");
		}
	}

	@Override
	public void focusGained(FocusEvent event) {
		super.focusGained(event);
		System.out.println("Ici on rechoppe le focus");
		// editorPane.selectAll();
	}

	@Override
	public void updateDataObject(final Object dataObject) {
		if (!SwingUtilities.isEventDispatchThread()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Update data object invoked outside the EDT!!! please investigate and make sure this is no longer the case. \n\tThis is a very SERIOUS problem! Do not let this pass.");
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateDataObject(dataObject);
				}
			});
			return;
		}
		super.updateDataObject(dataObject);
		editorPane.setEditable(!isReadOnly());
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		if (notEquals(getValue(), editorPane.getText())) {
			if (modelUpdating) {
				return false;
			}
			if (getValue() != null && (getValue() + "\n").equals(editorPane.getText())) {
				return false;
			}
			widgetUpdating = true;
			try {
				editorPane.setText(getValue());
				updateFont();
				editorPane.setCaretPosition(0);
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
		if (notEquals(getValue(), editorPane.getText())) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateModelFromWidget() in TextAreaWidget");
			}
			modelUpdating = true;
			try {
				setValue(editorPane.getText());
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
	public JEditorPane getDynamicJComponent() {
		return editorPane;
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
				scrollPane = new JScrollPane(editorPane, getComponent().getVerticalScrollbarPolicy().getPolicy(), getComponent()
						.getHorizontalScrollbarPolicy().getPolicy());
				scrollPane.setOpaque(false);
				scrollPane.getViewport().setOpaque(false);
				scrollPane.setBorder(BorderFactory.createEmptyBorder());
				scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
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
			if (editorPane.getDocument() instanceof StyledDocument) {
				SimpleAttributeSet sas = new SimpleAttributeSet();
				StyleConstants.setFontFamily(sas, getFont().getFamily());
				StyleConstants.setFontSize(sas, getFont().getSize());
				((StyledDocument) editorPane.getDocument()).setParagraphAttributes(0, editorPane.getDocument().getLength(), sas, false);
			}
		}
	}
}
