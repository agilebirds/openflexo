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
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.dm.CSSChanged;
import org.openflexo.foundation.ie.dm.table.WidgetRemovedFromTable;
import org.openflexo.foundation.ie.util.TextCSSClass;
import org.openflexo.foundation.ie.widget.IELabelWidget;
import org.openflexo.ie.util.TriggerRepaintDocumentListener;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.listener.DoubleClickResponder;

/**
 * 'Label' widget
 * 
 * @author bmangez
 */
public class IELabelWidgetView extends AbstractInnerTableWidgetView<IELabelWidget> implements DoubleClickResponder, ActionListener,
		LabeledWidget {

	private static final Logger logger = Logger.getLogger(IELabelWidgetView.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================
	private JLabel _jLabel;

	private JTextField editableLabel;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IELabelWidgetView(IEController ieController, IELabelWidget model, boolean addDnDSupport, IEWOComponentView componentView) {
		super(ieController, model, addDnDSupport, componentView);
		// _model = model;
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		layout.setVgap(4);
		setLayout(layout);
		_jLabel = new JLabel(getLabelModel().getValue());
		_jLabel.setFont(getLabelModel().getTextCSSClass() != null ? getLabelModel().getTextCSSClass().font() : TextCSSClass.BLOC_BODY_TITLE
				.font());
		_jLabel.setAlignmentY(0.5f);
		_jLabel.setOpaque(false);
		TransparentMouseListener tml = new TransparentMouseListener(_jLabel, this);
		_jLabel.addMouseListener(tml);
		_jLabel.addMouseMotionListener(tml);
		if (getLabelModel().getTooltip() != null) {
			_jLabel.setToolTipText(getLabelModel().getTooltip());
		}
		add(_jLabel);
	}

	public IELabelWidget getLabelModel() {
		return getModel();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("actionPerformed with " + event);
		}
		getLabelModel().setValue(editableLabel.getText());
		remove(editableLabel);
		add(_jLabel);
		editableLabel.removeActionListener(this);
		editableLabel = null;
		repaint();
	}

	@Override
	public void performDoubleClick(JComponent clickedContainer, Point clickedPoint, boolean isShiftDown) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("performDoubleClick() ");
		}
		editLabel();
	}

	// ==========================================================================
	// ============================= Observer
	// ===================================
	// ==========================================================================

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(FlexoObservable arg0, DataModification modif) {
		if (modif instanceof CSSChanged) {
			_jLabel.setFont(getLabelModel().getTextCSSClass().font());
		}
		String propertyName = modif.propertyName();
		if (propertyName != null) {
			if (propertyName.equals(BINDING_VALUE_NAME)) {
				_jLabel.setText(getLabelModel().getValue());
			} else if (propertyName.equals("cssClass")) {
				_jLabel.setFont(getLabelModel().getTextCSSClass().font());
			}
		}
		if (modif instanceof WidgetRemovedFromTable && arg0 == getModel()) {
			delete();
		} else {
			super.update(arg0, modif);
		}
	}

	protected JTextField _jLabelTextField = null;

	protected boolean labelEditing = false;

	@Override
	public void editLabel() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Edit ie label");
		}
		labelEditing = true;
		_jLabelTextField = new JTextField(getLabelModel().getValue());
		_jLabelTextField.setSelectionStart(0);
		_jLabelTextField.setSelectionEnd(getLabelModel().getValue().length());
		_jLabelTextField.setFont(_jLabel.getFont());
		_jLabelTextField.setBorder(BorderFactory.createEmptyBorder());
		_jLabelTextField.setBounds(_jLabel.getBounds());
		_jLabelTextField.setHorizontalAlignment(SwingConstants.CENTER);
		_jLabelTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				finalizeEditIELabel();
			}
		});
		_jLabelTextField.getDocument().addDocumentListener(new TriggerRepaintDocumentListener(this));
		_jLabelTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				finalizeEditIELabel();
			}
		});
		remove(_jLabel);
		add(_jLabelTextField);
		_jLabelTextField.requestFocusInWindow();
		if (getLabelModel().getValue() != null && getLabelModel().getValue().endsWith(":")) {
			_jLabelTextField.select(0, _jLabelTextField.getText().length() - 1);
		} else {
			_jLabelTextField.selectAll();
		}
		_jLabelTextField.revalidate();
		_jLabelTextField.repaint();
		revalidate();
		repaint();
	}

	public void finalizeEditIELabel() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Finalize edit ie label");
		}
		// _jLabel.setText(_jLabelTextField.getText());
		if (labelEditing) {
			getLabelModel().setValue(_jLabelTextField.getText());
		}
		labelEditing = false;
		remove(_jLabelTextField);
		add(_jLabel);
		revalidate();
		repaint();
		// getIEController().clearEditedNodeLabel();
	}

}
