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
package org.openflexo.ie.view.widget;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.dm.CSSChanged;
import org.openflexo.foundation.ie.dm.table.WidgetRemovedFromTable;
import org.openflexo.foundation.ie.util.TextCSSClass;
import org.openflexo.foundation.ie.util.TextFieldType;
import org.openflexo.foundation.ie.widget.IEStringWidget;
import org.openflexo.ie.IEPreferences;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.listener.DoubleClickResponder;

/**
 * @author bmangez
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class IEStringWidgetView extends AbstractInnerTableWidgetView<IEStringWidget> implements DoubleClickResponder,
		DisplayableBindingValue, LabeledWidget {

	private static final Logger logger = Logger.getLogger(IEStringWidgetView.class.getPackage().getName());

	private JEditorPane jLabel;

	protected JTextField _jLabelTextField = null;

	protected boolean labelEditing = false;

	public IEStringWidgetView(IEController ieController, IEStringWidget model, boolean addDnDSupport, IEWOComponentView view) {
		super(ieController, model, addDnDSupport, view);
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		// setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		setAlignmentY(Component.CENTER_ALIGNMENT);
		layout.setVgap(0);
		setAlignmentY(0.5f);
		setLayout(layout);
		jLabel = new JEditorPane() {
			/**
			 * Overrides getPreferredSize
			 * 
			 * @see javax.swing.JEditorPane#getPreferredSize()
			 */
			@Override
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				if (IEStringWidgetView.this.getParent() instanceof IESequenceWidgetWidgetView) {
					String text = displayAsHTML() ? getModel().getValue() : getText();
					int a = ((IESequenceWidgetWidgetView) IEStringWidgetView.this.getParent()).getAvailableWidth();
					int stringWidth = getFontMetrics(getFont()).stringWidth(text) + text.length() + 2;
					d.width = Math.min(stringWidth, a - 7);// stringWidth
				}
				return d;
			}

			@Override
			public void setText(String t) {
				super.setText(t);
			}

		};
		// jLabel.setLayout(new BorderLayout());
		jLabel.setOpaque(false);
		jLabel.setEditable(false);
		jLabel.setContentType(displayAsHTML() ? "text/html" : "text/plain");
		jLabel.setFont(getModel().getTextCSSClass() == null ? TextCSSClass.BLOC_BODY_CONTENT.font() : getModel().getTextCSSClass().font());
		jLabel.setBorder(null);
		jLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
		TransparentMouseListener tml = new TransparentMouseListener(jLabel, this);
		jLabel.addMouseListener(tml);
		jLabel.addMouseMotionListener(tml);
		if (getModel().getDescription() != null) {
			jLabel.setToolTipText(getModel().getDescription());
		}
		add(jLabel);
		doLayout();
		updateDisplayedValue();
		setBackground(getBackgroundColor());

	}

	private static final String SPAN_OPEN = "<html><FONT FACE=\"Verdana, Arial, Helvetica, sans-serif\" SIZE=2>";// SIZE is not pixel for
																													// the FONT tag

	private static final String SPAN_CLOSE = "</FONT></html>";

	private String spanMyText(String s) {
		return SPAN_OPEN + s + SPAN_CLOSE;
	}

	private boolean displayAsHTML() {
		return !IEPreferences.getDisplayBindingValue() && getModel().getIsHTML()
				&& (getModel().getFieldType() == null || getModel().getFieldType() == TextFieldType.TEXT);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(FlexoObservable arg0, DataModification modif) {
		if (modif instanceof CSSChanged) {
			jLabel.setFont(getModel().getTextCSSClass().font());
		} else if (modif.modificationType() == DataModification.ATTRIBUTE) {
			if (modif.propertyName().equals(BINDING_VALUE_NAME) || modif.propertyName().equals("bindingValue")) {
				updateDisplayedValue();
			} else if (modif.propertyName().equals("cssClass")) {
				jLabel.setFont(getModel().getTextCSSClass().font());
			} else if (modif.propertyName().equals("colSpan") || modif.propertyName().equals("rowSpan")) {
				getParent().doLayout();
				((JComponent) getParent()).repaint();
			} else if (modif.propertyName().equals("isHTML") || modif.propertyName().equals("fieldType")) {
				updateDisplayedValue();
			}
		}
		if (modif instanceof WidgetRemovedFromTable && arg0 == getModel()) {
			delete();
		} else {
			super.update(arg0, modif);
		}
	}

	@Override
	public void updateDisplayedValue() {
		if (IEPreferences.getDisplayBindingValue()) {
			jLabel.setContentType("text/plain");
			jLabel.setText(getModel().getBindingValue() != null ? getModel().getBindingValue().getCodeStringRepresentation() : "UNBOUND");
		} else {
			refreshView();
		}
	}

	private void refreshView() {
		jLabel.setContentType(displayAsHTML() ? "text/html" : "text/plain");
		jLabel.setText(displayAsHTML() ? spanMyText(getModel().getValue()) : getModel().getValue());
		jLabel.doLayout();
		jLabel.repaint();
	}

	@Override
	public void performDoubleClick(JComponent clickedContainer, Point clickedPoint, boolean isShiftDown) {
		if (getModel().isDKVField() || getModel().isStatusField()) {
			return;
		}
		if (IEPreferences.getDisplayBindingValue()) {
			return;
		}
		editLabel();
	}

	@Override
	public void editLabel() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Edit ie string");
		}
		labelEditing = true;
		_jLabelTextField = new JTextField(getModel().getValue()) {

			@Override
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				if (IEStringWidgetView.this.getParent() instanceof IESequenceWidgetWidgetView) {
					int a = ((IESequenceWidgetWidgetView) IEStringWidgetView.this.getParent()).getAvailableWidth();
					int stringWidth = getFontMetrics(getFont()).stringWidth(getText());
					d.width = Math.min(stringWidth, a - 4);// stringWidth
				}
				return d;
			}
		};
		_jLabelTextField.setOpaque(false);
		_jLabelTextField.setFont(jLabel.getFont());
		_jLabelTextField.setBorder(BorderFactory.createEmptyBorder());
		_jLabelTextField.setBounds(jLabel.getBounds());
		_jLabelTextField.setHorizontalAlignment(SwingConstants.CENTER);
		_jLabelTextField.setAlignmentY(Component.CENTER_ALIGNMENT);
		_jLabelTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				finalizeEditString();
			}
		});
		_jLabelTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent event) {
				// getStringModel().setValue(_jLabelTextField.getText());
				updateSize();
			}

			@Override
			public void removeUpdate(DocumentEvent event) {
				// getStringModel().setValue(_jLabelTextField.getText());
				updateSize();
			}

			@Override
			public void changedUpdate(DocumentEvent event) {
				// getStringModel().setValue(_jLabelTextField.getText());
				updateSize();
			}

			public void updateSize() {
				_jLabelTextField.setPreferredSize(_jLabelTextField.getPreferredSize());
				doLayout();
				repaint();
			}
		});
		_jLabelTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				finalizeEditString();
			}
		});
		remove(jLabel);
		add(_jLabelTextField);
		_jLabelTextField.requestFocus();
		_jLabelTextField.selectAll();
		_jLabelTextField.revalidate();
		_jLabelTextField.repaint();
		revalidate();
		repaint();
	}

	public void finalizeEditString() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Finalize edit ie string");
		}
		remove(_jLabelTextField);
		add(jLabel);
		if (labelEditing) {
			getModel().setValue(_jLabelTextField.getText());
		}
		labelEditing = false;
		revalidate();
		repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		// if (getHoldsNextComputedPreferredSize()){
		// Dimension storedSize = storedPrefSize();
		// if(storedSize!=null)return storedSize;
		// }
		Dimension d = labelEditing ? _jLabelTextField.getPreferredSize() : jLabel.getPreferredSize();
		d.width += 8;
		d.height += 4;
		// if (getHoldsNextComputedPreferredSize())
		// storePrefSize(d);
		return d;
	}

}
