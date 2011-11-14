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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.dm.DuplicateMethodSignatureException;
import org.openflexo.foundation.dm.javaparser.AbstractSourceCode;
import org.openflexo.foundation.dm.javaparser.ParserNotInstalledException;
import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.WidgetFocusListener;
import org.openflexo.jedit.InputHandler.InputListener;
import org.openflexo.jedit.cd.JavaCodeDisplayer;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.diff.DiffPanel;
import org.openflexo.toolbox.TokenMarkerStyle;

/**
 * This widget may be used to represent source code that can be edited (a diff-view shows differences between edited code and default
 * computed code) Parsing errors are also shown in this widget This widget requires a
 * 
 * <pre>
 * AbstractSourceCode
 * </pre>
 * 
 * object.
 * 
 * @author sylvain
 * 
 */
public class JavaSourceCodeInspectorWidget extends CustomInspectorWidget<AbstractSourceCode> {
	private static final Logger logger = FlexoLogger.getLogger(JavaSourceCodeInspectorWidget.class.getPackage().getName());

	public static final String COLUMNS = "columns";
	public static final String ROWS = "rows";
	public static final String VALIDATE_ON_RETURN = "validateOnReturn";

	private static final int DEFAULT_COLUMNS = 10;
	private static final int DEFAULT_ROWS = 4;
	public static Font FIXED_SIZE_FONT = new Font("Monospaced", Font.PLAIN, 11);
	public static Font SANS_SERIF_FONT = new Font("SansSerif", Font.PLAIN, 11);

	protected class SourceCodePanel extends JPanel {
		protected JPanel top;
		protected JLabel statusLabel;
		protected JLabel errorLabel;

		protected JLabel resetImplementationLabel;
		protected JLabel editCodeLabel;
		protected JLabel showDiffLabel;

		protected JavaCodeDisplayer _textArea;

		protected boolean showDiffMode = false;
		protected boolean diffPanelDisplayed = false;

		protected boolean validateOnReturn;

		/**
         * 
         */
		public SourceCodePanel(PropertyModel model) {
			super(new BorderLayout());
			setOpaque(false);

			_textArea = new JavaCodeDisplayer(getStringValue());
			_textArea.setEditable(true);

			if (model.hasValueForParameter(VALIDATE_ON_RETURN)) {
				validateOnReturn = model.getBooleanValueForParameter(VALIDATE_ON_RETURN);
			} else {
				validateOnReturn = true;
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
					if ((!validateOnReturn) && (!widgetUpdating)) {
						updateModelFromWidget();
					}
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					if ((!validateOnReturn) && (!widgetUpdating)) {
						updateModelFromWidget();
					}
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					if ((!validateOnReturn) && (!widgetUpdating)) {
						updateModelFromWidget();
					}
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
			_textArea.addFocusListener(new WidgetFocusListener(JavaSourceCodeInspectorWidget.this));

			_textArea.setAutoscrolls(true);
			_textArea.setBorder(BorderFactory.createLoweredBevelBorder());
			_textArea.setEnabled(true);

			top = new JPanel(new BorderLayout());
			top.setOpaque(false);

			statusLabel = new JLabel("status", SwingConstants.LEFT);
			statusLabel.setFont(FlexoCst.SMALL_FONT);

			resetImplementationLabel = new JLabel("<html><u>" + FlexoLocalization.localizedForKey("reset_to_default_implementation")
					+ "</u>" + "</html>", SwingConstants.RIGHT);
			resetImplementationLabel.setForeground(Color.BLUE);
			resetImplementationLabel.setFont(FlexoCst.SMALL_FONT);
			resetImplementationLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					resetImplementationLabel.setForeground(Color.MAGENTA);
					resetImplementationLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					resetImplementationLabel.setForeground(Color.BLUE);
					resetImplementationLabel.setCursor(Cursor.getDefaultCursor());
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					editionRequested = false;
					showDiffMode = false;
					try {
						getSourceCode().setCode("");
					} catch (ParserNotInstalledException e1) {
						e1.printStackTrace();
					} catch (DuplicateMethodSignatureException e1) {
						e1.printStackTrace();
					}
					refreshPanel();
				}
			});

			editCodeLabel = new JLabel("<html><u>" + FlexoLocalization.localizedForKey("edit_code") + "</u>" + "</html>",
					SwingConstants.RIGHT);
			editCodeLabel.setForeground(Color.BLUE);
			editCodeLabel.setFont(FlexoCst.SMALL_FONT);
			editCodeLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					editCodeLabel.setForeground(Color.MAGENTA);
					editCodeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					editCodeLabel.setForeground(Color.BLUE);
					editCodeLabel.setCursor(Cursor.getDefaultCursor());
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					editionRequested = true;
					showDiffMode = false;
					refreshPanel();
				}
			});

			showDiffLabel = new JLabel("<html><u>" + FlexoLocalization.localizedForKey("show_diffs") + "</u>" + "</html>",
					SwingConstants.RIGHT);
			showDiffLabel.setForeground(Color.BLUE);
			showDiffLabel.setFont(FlexoCst.SMALL_FONT);
			showDiffLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					showDiffLabel.setForeground(Color.MAGENTA);
					showDiffLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					showDiffLabel.setForeground(Color.BLUE);
					showDiffLabel.setCursor(Cursor.getDefaultCursor());
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					updateModelFromWidget();
					showDiffMode = true;
					editionRequested = false;
					refreshPanel();
				}
			});

			top.add(statusLabel, BorderLayout.WEST);

			JPanel actionsPanel = new JPanel(new FlowLayout());
			actionsPanel.add(showDiffLabel);
			actionsPanel.add(editCodeLabel);
			actionsPanel.add(resetImplementationLabel);

			top.add(actionsPanel, BorderLayout.EAST);

			errorLabel = new JLabel("error", SwingConstants.CENTER);

			add(top, BorderLayout.NORTH);
			add(_textArea, BorderLayout.CENTER);
			add(errorLabel, BorderLayout.SOUTH);

			validate();
			doLayout();
		}

		protected boolean editionRequested = false;

		public void updateModelFromWidget() {
			if (isUpdatingWidget || isUpdatingModel || getSourceCode() == null || !(getSourceCode().isEdited() || editionRequested)) {
				return;
			}
			isUpdatingModel = true;
			try {
				getSourceCode().setCode(_textArea.getText());
				refreshPanel();
			} catch (ParserNotInstalledException e) {
				e.printStackTrace();
			} catch (DuplicateMethodSignatureException e) {
				e.printStackTrace();
			} finally {
				isUpdatingModel = false;
			}
		}

		private DiffPanel diffPanel;

		private void switchToDiffMode() {
			diffPanel = new DiffPanel(getSourceCode().getDiffReport(), TokenMarkerStyle.Java,
					FlexoLocalization.localizedForKey("edited_code"), FlexoLocalization.localizedForKey("default_implementation"),
					FlexoLocalization.localizedForKey("no_differences"), true);
			remove(_textArea);
			add(diffPanel, BorderLayout.CENTER);
			diffPanelDisplayed = true;
		}

		private void switchToNormalMode() {
			remove(diffPanel);
			diffPanel = null;
			add(_textArea, BorderLayout.CENTER);
			diffPanelDisplayed = false;
		}

		private void resetToDefault() {
			if (showDiffMode) {
				switchToNormalMode();
			}
			showDiffMode = false;
			editionRequested = false;
		}

		protected void refreshPanel() {
			boolean codeModified = !_textArea.getText().equals(getSourceCode().getCode());

			int caretPos = 0;

			if (showDiffMode && !diffPanelDisplayed) {
				switchToDiffMode();
			} else if (!showDiffMode && diffPanelDisplayed) {
				switchToNormalMode();
			}

			if (codeModified) {
				caretPos = _textArea.getCaretPosition();
				_textArea.setText(getSourceCode().getCode());
			}

			if (!showDiffMode) {
				if (getSourceCode().isEdited() || editionRequested) {
					statusLabel.setText("[" + FlexoLocalization.localizedForKey("edited_code") + "]");
					if (!_textArea.isEditable()) {
						_textArea.setEditable(true);
					}
					resetImplementationLabel.setVisible(true);
					showDiffLabel.setVisible(true);
					editCodeLabel.setVisible(false);
				} else {
					statusLabel.setText("[" + FlexoLocalization.localizedForKey("default_implementation") + "]");
					if (_textArea.isEditable()) {
						_textArea.setEditable(false);
					}
					resetImplementationLabel.setVisible(false);
					showDiffLabel.setVisible(false);
					editCodeLabel.setVisible(true);
				}
			} else {
				resetImplementationLabel.setVisible(true);
				showDiffLabel.setVisible(false);
				editCodeLabel.setVisible(true);
			}

			if (getSourceCode().hasParseErrors()) {
				errorLabel.setText(getSourceCode().getParseErrorWarning());
				errorLabel.setVisible(true);
			} else {
				errorLabel.setVisible(false);
			}

			if (codeModified) {
				_textArea.setCaretPosition(caretPos);
			}

			revalidate();
			doLayout();
		}

		public void updateWidgetFromModel() {
			if (isUpdatingModel || getSourceCode() == null) {
				return;
			}

			isUpdatingWidget = true;
			try {
				refreshPanel();

			} finally {
				isUpdatingWidget = false;
			}

		}

	}

	private SourceCodePanel panel;

	protected boolean isUpdatingModel = false;

	protected boolean isUpdatingWidget = false;

	/**
     * 
     * 
     */
	public AbstractSourceCode getSourceCode() {
		return getObjectValue();
	}

	/**
	 * @param model
	 */
	public JavaSourceCodeInspectorWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		panel = new SourceCodePanel(model);
	}

	/**
	 * Overrides getDefaultType
	 * 
	 * @see org.openflexo.inspector.widget.DenaliWidget#getDefaultType()
	 */
	@Override
	public Class getDefaultType() {
		return AbstractSourceCode.class;
	}

	/**
	 * Overrides getDynamicComponent
	 * 
	 * @see org.openflexo.inspector.widget.DenaliWidget#getDynamicComponent()
	 */
	@Override
	public JComponent getDynamicComponent() {
		return panel;
	}

	/**
	 * Overrides updateModelFromWidget
	 * 
	 * @see org.openflexo.inspector.widget.DenaliWidget#updateModelFromWidget()
	 */
	@Override
	public void updateModelFromWidget() {
		if (isUpdatingWidget) {
			return;
		}
		isUpdatingModel = true;
		try {
			panel.updateModelFromWidget();
		} finally {
			isUpdatingModel = false;
		}
		super.updateModelFromWidget();
	}

	/**
	 * Overrides updateWidgetFromModel
	 * 
	 * @see org.openflexo.inspector.widget.DenaliWidget#updateWidgetFromModel()
	 */
	@Override
	public void updateWidgetFromModel() {
		if (isUpdatingModel) {
			return;
		}
		isUpdatingWidget = true;
		try {
			panel.updateWidgetFromModel();
		} finally {
			isUpdatingWidget = false;
		}
	}

	@Override
	public boolean defaultShouldExpandVertically() {
		return true;
	}

	@Override
	public void setModel(InspectableObject value) {
		if (value != getModel()) {
			panel.resetToDefault();
		}
		super.setModel(value);
	}

}