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

import java.awt.Color;
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
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.dm.SortChanged;
import org.openflexo.foundation.ie.dm.table.WidgetRemovedFromTable;
import org.openflexo.foundation.ie.widget.IEHeaderWidget;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.ie.IECst;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.listener.DoubleClickResponder;

/**
 * @author bmangez
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class IEHeaderWidgetView extends AbstractInnerTableWidgetView<IEHeaderWidget> implements DoubleClickResponder, LabeledWidget {

	private JLabel _jLabel;

	public static final String ISSORTABLE_ATTRIBUTENAME = "isSortable";

	public static final String ISSORTED_ATTRIBUTENAME = "isSorted";

	private static final Logger logger = Logger.getLogger(IEHeaderWidgetView.class.getPackage().getName());

	public IEHeaderWidgetView(IEController ieController, IEHeaderWidget model, boolean addDnDSupport, IEWOComponentView view) {
		super(ieController, model, addDnDSupport, view);
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 4, 4);
		setLayout(layout);
		_jLabel = new JLabel(getHeaderModel().getValue());
		_jLabel.setFont(IECst.LABEL_BOLD_FONT);
		TransparentMouseListener tml = new TransparentMouseListener(_jLabel, this);
		_jLabel.addMouseListener(tml);
		_jLabel.addMouseMotionListener(tml);
		if (getHeaderModel().getTooltip() != null) {
			_jLabel.setToolTipText(getHeaderModel().getTooltip());
		}
		_jLabel.setIcon(getSortIcon());
		setOpaque(true);
		add(_jLabel);
		setBackground(getFlexoCSS().getMainColor());
	}

	/**
	 * Overrides setBackground
	 * 
	 * @see javax.swing.JComponent#setBackground(java.awt.Color)
	 */
	@Override
	public void setBackground(Color bg) {
		if (getModel() != null) {
			super.setBackground(getFlexoCSS().getMainColor());
		}
	}

	public IEHeaderWidget getHeaderModel() {
		return getModel();
	}

	@Override
	public Dimension getPreferredSize() {
		if (getHoldsNextComputedPreferredSize()) {
			Dimension storedSize = storedPrefSize();
			if (storedSize != null) {
				return storedSize;
			}
		}
		IESequenceWidgetWidgetView parentSequenceView = null;
		if (getParent() instanceof IESequenceWidgetWidgetView) {
			parentSequenceView = (IESequenceWidgetWidgetView) getParent();
		}
		if (parentSequenceView != null) {
			int width = parentSequenceView.getAvailableWidth();
			Dimension d = super.getPreferredSize();
			d = new Dimension(width, d.height);
			if (getHoldsNextComputedPreferredSize()) {
				storePrefSize(d);
			}
			return d;
		}
		Dimension d = super.getPreferredSize();
		if (getHoldsNextComputedPreferredSize()) {
			storePrefSize(d);
		}
		return d;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(FlexoObservable arg0, DataModification modif) {
		if (modif.modificationType() == DataModification.ATTRIBUTE) {
			if (modif.propertyName().equals(BINDING_VALUE_NAME)) {
				_jLabel.setText(getHeaderModel().getValue());
			} else if (modif.propertyName().equals(ISSORTABLE_ATTRIBUTENAME) || modif.propertyName().equals(ISSORTED_ATTRIBUTENAME)) {
				_jLabel.setIcon(getSortIcon());
			} else if (modif.propertyName().equals("colSpan") || modif.propertyName().equals("rowSpan")) {
				getParent().doLayout();
				((JComponent) getParent()).repaint();
			}
		}
		if (modif instanceof WidgetRemovedFromTable && arg0 == getModel()) {
			delete();
		} else if (modif instanceof SortChanged) {
			_jLabel.setIcon(getSortIcon());
		} else {
			super.update(arg0, modif);
		}
	}

	private ImageIcon getSortIcon() {
		if (!getHeaderModel().getIsSortable()) {
			return null;
		} else {
			if (getHeaderModel().getIsSorted()) {
				if (getHeaderModel().getDefaultAscending()) {
					return SEIconLibrary.ICON_DOWN;
				} else {
					return SEIconLibrary.ICON_UP;
				}
			} else {
				return SEIconLibrary.ICON_RIGHT;
			}
		}
	}

	protected JTextField _jLabelTextField = null;

	protected boolean labelEditing = false;

	@Override
	public void performDoubleClick(JComponent clickedContainer, Point clickedPoint, boolean isShiftDown) {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("performDoubleClick() ");
		}
		editLabel();
	}

	@Override
	public void editLabel() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Edit ie header");
		}
		labelEditing = true;
		_jLabelTextField = new JTextField(getHeaderModel().getValue());
		_jLabelTextField.setFont(_jLabel.getFont());
		_jLabelTextField.setBorder(BorderFactory.createEmptyBorder());
		_jLabelTextField.setBounds(_jLabel.getBounds());
		_jLabelTextField.setHorizontalAlignment(SwingConstants.CENTER);
		_jLabelTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				finalizeEditHeader();
			}
		});
		_jLabelTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent event) {
				updateSize();
			}

			@Override
			public void removeUpdate(DocumentEvent event) {
				updateSize();
			}

			@Override
			public void changedUpdate(DocumentEvent event) {
				updateSize();
			}

			public void updateSize() {
				revalidate();
				repaint();
			}
		});
		_jLabelTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				finalizeEditHeader();
			}
		});
		remove(_jLabel);
		add(_jLabelTextField);
		_jLabelTextField.requestFocus();
		_jLabelTextField.selectAll();
		_jLabelTextField.revalidate();
		_jLabelTextField.repaint();
		revalidate();
		repaint();
	}

	public void finalizeEditHeader() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Finalize edit ie header");
		}
		_jLabel.setText(_jLabelTextField.getText());
		if (labelEditing) {
			getHeaderModel().setValue(_jLabelTextField.getText());
		}
		labelEditing = false;
		remove(_jLabelTextField);
		add(_jLabel);
		revalidate();
		repaint();
	}
}
