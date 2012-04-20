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
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
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

import org.openflexo.ColorCst;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.dm.table.WidgetRemovedFromTable;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.ie.IECst;
import org.openflexo.ie.IEPreferences;
import org.openflexo.ie.util.TriggerRepaintDocumentListener;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.listener.DoubleClickResponder;

/**
 * @author bmangez
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class IEHyperlinkWidgetView extends AbstractInnerTableWidgetView<IEHyperlinkWidget> implements DoubleClickResponder, LabeledWidget {
	private static final Logger logger = Logger.getLogger(IEHyperlinkWidgetView.class.getPackage().getName());

	private static final int ROUNDED_BORDER_SIZE = 1;

	private JLabel _jLabel;

	public IEHyperlinkWidgetView(IEController ieController, IEHyperlinkWidget model, boolean addDnDSupport, IEWOComponentView view) {
		super(ieController, model, addDnDSupport, view);
		// _model = model;
		_jLabel = new JLabel(getModel().getValue()) {
			/**
			 * Overrides paint
			 * 
			 * @see javax.swing.JComponent#paint(java.awt.Graphics)
			 */
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				if (getModel().isCustomButton()) {
					Rectangle bounds = getBounds();
					g.setColor(getBackgroundColor());
					g.fillRect(0, 0, ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE);
					g.fillRect(0, bounds.height - ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE);
					g.fillRect(bounds.width - ROUNDED_BORDER_SIZE, 0, ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE);
					g.fillRect(bounds.width - ROUNDED_BORDER_SIZE, bounds.height - ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE,
							ROUNDED_BORDER_SIZE);
				}
			}

			/**
			 * Overrides getPreferredSize
			 * 
			 * @see javax.swing.JComponent#getPreferredSize()
			 */
			@Override
			public Dimension getPreferredSize() {
				if (getModel().isCustomButton()) {
					String s = getModel().getValue();
					if (s == null) {
						return new Dimension(30, 15);
					} else {
						return new Dimension((int) (getFontMetrics(getFont()).getStringBounds(s, getGraphics()).getWidth() + 32), 15);
					}
				} else {
					return super.getPreferredSize();
				}
			}
		};
		performLabelTransformation();
		_jLabel.setMinimumSize(new Dimension(30, 15));
		_jLabel.setVerticalTextPosition(SwingConstants.CENTER);
		_jLabel.setHorizontalAlignment(SwingConstants.CENTER);
		TransparentMouseListener tml = new TransparentMouseListener(_jLabel, this);
		_jLabel.addMouseListener(tml);
		_jLabel.addMouseMotionListener(tml);
		add(_jLabel);
		setBackground(getBackgroundColor());
		validate();
	}

	private void performLabelTransformation() {
		if (getModel().isCustomButton()) {
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			_jLabel.setFont(IEHyperlinkWidget.getButtonFont());
			_jLabel.setForeground(Color.WHITE);
			_jLabel.setBackground(getModel().getFlexoCSS().getButtonColor());
			_jLabel.setOpaque(true);
			_jLabel.setBorder(BorderFactory.createMatteBorder(0, 15, 1, 15, getModel().getFlexoCSS().getButtonColor()));
		} else {
			setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			_jLabel.setFont(IECst.WOSTRING_FONT);
			_jLabel.setForeground(ColorCst.HYPERLINK_COLOR);
			_jLabel.setBackground(getBackgroundColor());
			_jLabel.setOpaque(false);
			_jLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 1, 10));
		}
	}

	@Override
	public void update(FlexoObservable arg0, DataModification modif) {
		String propertyName = modif.propertyName();
		if (propertyName != null) {
			if (propertyName.equals(BINDING_VALUE_NAME) || propertyName.equals("bindingValue")) {
				updateDisplayedValue();
			} else if (propertyName.equals("isCustomButton")) {
				performLabelTransformation();
				repaint();
			}
		}
		if (modif instanceof WidgetRemovedFromTable && arg0 == getModel()) {
			delete();
		} else {
			super.update(arg0, modif);
		}
	}

	public void updateDisplayedValue() {
		if (IEPreferences.getDisplayBindingValue() && getModel().getBindingValue() != null) {
			_jLabel.setText(getModel().getBindingValue() != null ? getModel().getBindingValue().getCodeStringRepresentation() : "UNBOUND");
		} else {
			refreshView();
		}
	}

	private void refreshView() {
		_jLabel.setText(getModel().getIsHTML() ? spanMyText(getModel().getValue()) : getModel().getValue());
		_jLabel.doLayout();
		_jLabel.repaint();
	}

	private static final String SPAN_OPEN = "<html><body><div><FONT FACE=\"Verdana, Arial, Helvetica, sans-serif\" SIZE=3>";// SIZE is not
																															// pixel for the
																															// FONT tag

	private static final String SPAN_CLOSE = "</FONT></div></body></html>";

	private String spanMyText(String s) {
		return SPAN_OPEN + s + SPAN_CLOSE;
	}

	protected JTextField _jLabelTextField = null;

	protected boolean labelEditing = false;

	@Override
	public void performDoubleClick(JComponent clickedContainer, Point clickedPoint, boolean isShiftDown) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("performDoubleClick() ");
		}
		editLabel();
	}

	@Override
	public void editLabel() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Edit ie hyperlink");
		}
		labelEditing = true;
		_jLabelTextField = new JTextField(getModel().getValue()) {
			/**
			 * Overrides paint
			 * 
			 * @see javax.swing.JComponent#paint(java.awt.Graphics)
			 */
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				if (getModel().isCustomButton()) {
					Rectangle bounds = getBounds();
					g.setColor(getBackgroundColor());
					g.fillRect(0, 0, ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE);
					g.fillRect(0, bounds.height - ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE);
					g.fillRect(bounds.width - ROUNDED_BORDER_SIZE, 0, ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE);
					g.fillRect(bounds.width - ROUNDED_BORDER_SIZE, bounds.height - ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE,
							ROUNDED_BORDER_SIZE);
				}
			}

			/**
			 * Overrides getPreferredSize
			 * 
			 * @see javax.swing.JComponent#getPreferredSize()
			 */
			@Override
			public Dimension getPreferredSize() {
				if (getModel().isCustomButton()) {
					String s = _jLabelTextField.getText();
					if (s == null) {
						return new Dimension(30, 15);
					} else {
						return new Dimension((int) (getFontMetrics(getFont()).getStringBounds(s, getGraphics()).getWidth() + 32), 15);
					}
				} else {
					return super.getPreferredSize();
				}
			}
		};
		_jLabelTextField.setFont(_jLabel.getFont());
		_jLabelTextField.setForeground(_jLabel.getForeground());
		_jLabelTextField.setBackground(_jLabel.getBackground());
		if (getModel().isCustomButton()) {
			_jLabelTextField.setBorder(BorderFactory.createMatteBorder(0, 15, 1, 15, _jLabel.getBackground()));
		} else {
			_jLabelTextField.setBorder(BorderFactory.createEmptyBorder(0, 10, 1, 10));
		}
		// _jLabelTextField.setForeground(getFlexoNode().getTextColor());
		_jLabelTextField.setBounds(_jLabel.getBounds());
		_jLabelTextField.setHorizontalAlignment(SwingConstants.CENTER);
		_jLabelTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				finalizeEditHyperlink();
			}
		});
		_jLabelTextField.getDocument().addDocumentListener(new TriggerRepaintDocumentListener(this));
		_jLabelTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				finalizeEditHyperlink();
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

	/**
	 * Overrides getPreferredSize
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		if (getHoldsNextComputedPreferredSize()) {
			Dimension storedSize = storedPrefSize();
			if (storedSize != null) {
				return storedSize;
			}
		}
		Dimension d;
		if (getModel().isCustomButton()) {
			d = new Dimension(
					(int) (labelEditing ? _jLabelTextField.getPreferredSize().getWidth() : _jLabel.getPreferredSize().getWidth()) + 2, 17);
		} else {
			d = super.getPreferredSize();
		}
		if (getHoldsNextComputedPreferredSize()) {
			storePrefSize(d);
		}
		return d;
	}

	public void finalizeEditHyperlink() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Finalize edit ie hyperlink");
		}
		// _jLabel.setText(_jLabelTextField.getText());
		if (labelEditing) {
			getModel().setValue(_jLabelTextField.getText());
		}
		labelEditing = false;
		remove(_jLabelTextField);
		add(_jLabel);
		// layoutComponent();
		revalidate();
		repaint();
	}
}
