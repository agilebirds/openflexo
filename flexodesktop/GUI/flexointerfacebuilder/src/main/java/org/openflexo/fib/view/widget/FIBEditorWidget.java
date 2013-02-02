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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBEditor;
import org.openflexo.fib.model.FIBEditor.FIBTokenMarkerStyle;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.jedit.BatchFileTokenMarker;
import org.openflexo.jedit.CCTokenMarker;
import org.openflexo.jedit.CTokenMarker;
import org.openflexo.jedit.EiffelTokenMarker;
import org.openflexo.jedit.FMLTokenMarker;
import org.openflexo.jedit.HTMLTokenMarker;
import org.openflexo.jedit.IDLTokenMarker;
import org.openflexo.jedit.JEditTextArea;
import org.openflexo.jedit.JavaScriptTokenMarker;
import org.openflexo.jedit.JavaTokenMarker;
import org.openflexo.jedit.PHPTokenMarker;
import org.openflexo.jedit.PatchTokenMarker;
import org.openflexo.jedit.PerlTokenMarker;
import org.openflexo.jedit.PropsTokenMarker;
import org.openflexo.jedit.PythonTokenMarker;
import org.openflexo.jedit.SQLTokenMarker;
import org.openflexo.jedit.ShellScriptTokenMarker;
import org.openflexo.jedit.TSQLTokenMarker;
import org.openflexo.jedit.TeXTokenMarker;
import org.openflexo.jedit.TokenMarker;
import org.openflexo.jedit.WODTokenMarker;
import org.openflexo.jedit.XMLTokenMarker;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a widget able to edit a Text (more than one line) object using a syntax-colored editor (eg code editor)
 * 
 * @author bmangez,sguerin
 */
public class FIBEditorWidget extends FIBWidgetView<FIBEditor, JEditTextArea, String> {

	private static final Logger logger = Logger.getLogger(FIBEditorWidget.class.getPackage().getName());

	private static final int DEFAULT_COLUMNS = 30;
	private static final int DEFAULT_ROWS = 5;

	private JPanel panel;
	private final JEditTextArea textArea;
	private JScrollPane scrollPane;
	boolean validateOnReturn;

	public FIBEditorWidget(FIBEditor model, FIBController controller) {
		super(model, controller);
		textArea = new JEditTextArea();
		panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.add(textArea, BorderLayout.CENTER);
		validateOnReturn = model.isValidateOnReturn();
		if (model.getColumns() != null && model.getColumns() > 0) {
			textArea.setColumns(model.getColumns());
		} else {
			textArea.setColumns(DEFAULT_COLUMNS);
		}
		if (model.getRows() != null && model.getRows() > 0) {
			textArea.setRows(model.getRows());
		} else {
			textArea.setRows(DEFAULT_ROWS);
		}
		Border border;
		if (!ToolBox.isMacOSLaf()) {
			border = BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER, BOTTOM_COMPENSATING_BORDER,
					RIGHT_COMPENSATING_BORDER);
		} else {
			border = BorderFactory.createEmptyBorder(2, 3, 2, 3);
		}
		border = BorderFactory.createCompoundBorder(border, BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		panel.setBorder(border);
		/*
		 * else { textArea.setBorder(new EtchedBorder(EtchedBorder.LOWERED)); }
		 */

		textArea.setEditable(!isReadOnly());
		if (model.getText() != null) {
			textArea.setText(model.getText());
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
		textArea.setEnabled(true);
		/*
		 * pane = new JScrollPane(_textArea);
		 * 
		 * pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		 * pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		 * 
		 * pane.setMinimumSize(MINIMUM_SIZE);
		 */

		updateFont();
		updateTokenMarkerStyle();
	}

	@Override
	public List<DataBinding<?>> getDependencyBindings() {
		List<DataBinding<?>> returned = super.getDependencyBindings();
		returned.add(getWidget().getEditable());
		return returned;
	}

	@Override
	public void focusGained(FocusEvent event) {
		super.focusGained(event);
		textArea.selectAll();
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
	public JEditTextArea getDynamicJComponent() {
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

	public void updateTokenMarkerStyle() {
		if (getWidget().getTokenMarkerStyle() != null) {
			textArea.setTokenMarker(makeTokenMarker(getWidget().getTokenMarkerStyle()));
		}
	}

	public static TokenMarker makeTokenMarker(FIBTokenMarkerStyle tokenMarkerStyle) {
		if (tokenMarkerStyle == FIBTokenMarkerStyle.BatchFile) {
			return new BatchFileTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.C) {
			return new CTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.CC) {
			return new CCTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.IDL) {
			return new IDLTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.JavaScript) {
			return new JavaScriptTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.Java) {
			return new JavaTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.Eiffel) {
			return new EiffelTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.HTML) {
			return new HTMLTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.Patch) {
			return new PatchTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.Perl) {
			return new PerlTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.PHP) {
			return new PHPTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.Props) {
			return new PropsTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.Python) {
			return new PythonTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.ShellScript) {
			return new ShellScriptTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.SQL) {
			return new SQLTokenMarker(null);
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.TSQL) {
			return new TSQLTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.TeX) {
			return new TeXTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.WOD) {
			return new WODTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.XML) {
			return new XMLTokenMarker();
		} else if (tokenMarkerStyle == FIBTokenMarkerStyle.FML) {
			return new FMLTokenMarker();
		} else {
			return null;
		}
	}

}
