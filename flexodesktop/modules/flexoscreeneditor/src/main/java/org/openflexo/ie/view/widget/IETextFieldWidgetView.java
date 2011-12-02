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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.dm.IETextFieldCssClassChange;
import org.openflexo.foundation.ie.dm.table.WidgetRemovedFromTable;
import org.openflexo.foundation.ie.util.TextFieldClass;
import org.openflexo.foundation.ie.widget.IEEditableTextWidget;
import org.openflexo.foundation.ie.widget.IETextFieldWidget;
import org.openflexo.ie.IEPreferences;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.logging.FlexoLogger;

/**
 * @author bmangez
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class IETextFieldWidgetView extends AbstractInnerTableWidgetView<IETextFieldWidget> implements DisplayableBindingValue {

	private static final Logger logger = FlexoLogger.getLogger(IETextFieldWidgetView.class.getPackage().getName());

	protected boolean labelEditing = false;

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================
	private JTextField _jTextField;

	private MouseListener mouseListener = new MouseAdapter() {
		/**
		 * Overrides mouseClicked
		 * 
		 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2 && !labelEditing) {
				editValue();
			}
		}
	};

	protected JTextField _jLabelTextField;// Edition of default value

	public static final Font TEXTFIELD_FONT = new Font("SansSerif", Font.PLAIN, 10);

	// private IETextFieldWidget _model;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IETextFieldWidgetView(IEController ieController, IETextFieldWidget model, boolean addDnDSupport, IEWOComponentView view) {
		super(ieController, model, addDnDSupport, view);
		// _model = model;
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 4, 4);
		// layout.setVgap(4);
		setLayout(layout);
		if (getTextFieldModel().getBindingValue() != null) {
			_jTextField = new JTextField(getTextFieldModel().getBindingValue().getCodeStringRepresentation());
		} else {
			_jTextField = new JTextField(getTextFieldModel().getValue());
			_jTextField.addMouseListener(mouseListener);
		}
		// _jTextField.setPreferredSize(new
		// Dimension(parent.getWidthForCol(model.getCol())-5,16));
		_jTextField.setFont(TEXTFIELD_FONT);
		add(_jTextField);
		// _jTextField.addMouseListener(SelectionManager.instance());
		_jTextField.setEditable(false);
		_jTextField.setFocusable(false);
		TransparentMouseListener tml = new TransparentMouseListener(_jTextField, this);
		_jTextField.addMouseListener(tml);
		_jTextField.addMouseMotionListener(tml);

		if (getTextFieldModel().getDescription() != null) {
			_jTextField.setToolTipText(getTextFieldModel().getDescription());
		}
		updateDisplayedValue();
	}

	/**
	 * Overrides doLayout
	 * 
	 * @see org.openflexo.ie.view.widget.IEWidgetView#doLayout()
	 */
	@Override
	public void doLayout() {
		super.doLayout();
		_jTextField.doLayout();
	}

	public IETextFieldWidget getTextFieldModel() {
		return getModel();
	}

	// ==========================================================================
	// ============================= Observer
	// ===================================
	// ==========================================================================

	private void applyCss() {
		getPreferredSize();
		doLayout();
		repaint();
	}

	@Override
	public void updateDisplayedValue() {
		if (IEPreferences.getDisplayBindingValue()) {
			_jTextField.setText(getTextFieldModel().getBindingValue() != null ? getTextFieldModel().getBindingValue()
					.getCodeStringRepresentation() : "UNBOUND");
			applyCss();
			removeDoubleClickListener();
		} else {
			_jTextField.setText(getTextFieldModel().getValue());
			applyCss();
			addDoubleClickListener();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(FlexoObservable arg0, DataModification modif) {
		if (modif instanceof IETextFieldCssClassChange) {
			applyCss();
		}
		if (modif.modificationType() == DataModification.ATTRIBUTE) {
			if (modif.propertyName().equals(IEEditableTextWidget.BINDING_VALUE) && arg0 == getTextFieldModel()) {
				updateDisplayedValue();
			} else if (modif.propertyName().equals(ATTRIB_DESCRIPTION_NAME)) {
				_jTextField.setToolTipText(getTextFieldModel().getDescription());
			} else if (modif.propertyName().equals("colSpan") || modif.propertyName().equals("rowSpan")) {
				if (getParent() != null) {
					getParent().doLayout();
					((JComponent) getParent()).repaint();
				}
			} else if (modif.propertyName().equals("value")) {
				if (getTextFieldModel().getBindingValue() == null) {
					_jTextField.setText(getTextFieldModel().getValue());
					applyCss();
				}
			}
		}
		if (modif instanceof WidgetRemovedFromTable && arg0 == getModel()) {
			delete();
		} else {
			super.update(arg0, modif);
		}
	}

	/**
     * 
     */
	private void addDoubleClickListener() {
		MouseListener[] ml = _jTextField.getMouseListeners();
		for (int i = 0; i < ml.length; i++) {
			if (ml[i] == mouseListener) {
				return;
			}
		}
		_jTextField.addMouseListener(mouseListener);
	}

	/**
     * 
     */
	private void removeDoubleClickListener() {
		_jTextField.removeMouseListener(mouseListener);
	}

	public void editValue() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Edit ie textfield");
		}
		labelEditing = true;
		_jLabelTextField = new JTextField(getTextFieldModel().getValue());
		// _jLabelTextField.setForeground(getFlexoNode().getTextColor());
		_jLabelTextField.setBounds(_jTextField.getBounds());
		_jLabelTextField.setPreferredSize(_jTextField.getPreferredSize());
		_jLabelTextField.setMinimumSize(new Dimension(60, 15));
		_jLabelTextField.setFont(_jTextField.getFont());
		_jLabelTextField.setFocusable(true);
		_jLabelTextField.setHorizontalAlignment(SwingConstants.LEFT);
		_jLabelTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				finalizeEditValue();
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
				validate();
				repaint();
			}
		});
		_jLabelTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if (arg0.getOppositeComponent() != IETextFieldWidgetView.this) {
					finalizeEditValue();
				} else {
					_jLabelTextField.grabFocus();
				}
			}
		});
		remove(_jTextField);
		add(_jLabelTextField);
		_jLabelTextField.grabFocus();
		_jLabelTextField.selectAll();
		validate();
		repaint();
	}

	public void finalizeEditValue() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Finalize edit ie textfield");
		}
		if (labelEditing) {
			getTextFieldModel().setValue(_jLabelTextField.getText());
			labelEditing = false;
			remove(_jLabelTextField);
			add(_jTextField);
			validate();
			repaint();
		}
	}

	@Override
	public Dimension getPreferredSize() {
		if (getTextFieldModel().getIsExtensible()) {
			IESequenceWidgetWidgetView parentSequenceView = null;
			if (getParent() instanceof IESequenceWidgetWidgetView) {
				parentSequenceView = (IESequenceWidgetWidgetView) getParent();
			}
			if (parentSequenceView != null) {
				int width = parentSequenceView.getAvailableWidth();
				Dimension d = super.getPreferredSize();
				if (width > 0) {
					d.width = width;
				}
				Insets i = _jTextField.getInsets();
				_jTextField.setPreferredSize(new Dimension(d.width - i.left - i.right, _jTextField.getFontMetrics(_jTextField.getFont())
						.getHeight() + i.top + i.bottom));
				return d;
			}
			int w = getParent() == null ? 150 : getParent().getWidth() - getParent().getInsets().left - getParent().getInsets().right;
			int h = _jTextField.getHeight();
			_jTextField.setPreferredSize(new Dimension(w - 4, h));
			return new Dimension(w, h + 4);
		}
		if (getTextFieldModel().getTfcssClass() != null && getTextFieldModel().getTfcssClass().equals(TextFieldClass.DLMEDIUM)) {
			Dimension d = _jTextField.getPreferredSize();
			d.width = 150;
			_jTextField.setPreferredSize(d);
			d = new Dimension(d.width + 8, d.height + 8);
			return d;
		} else if (getTextFieldModel().getTfcssClass() != null && getTextFieldModel().getTfcssClass().equals(TextFieldClass.DLSHORT)) {
			Dimension d = _jTextField.getPreferredSize();
			d.width = 30;
			_jTextField.setPreferredSize(d);
			d = new Dimension(d.width + 8, d.height + 8);
			return d;
		} else if (getTextFieldModel().getTfcssClass() != null && getTextFieldModel().getTfcssClass().equals(TextFieldClass.DLTINY)) {
			Dimension d = _jTextField.getPreferredSize();
			d.width = 20;
			_jTextField.setPreferredSize(d);
			d = new Dimension(d.width + 8, d.height + 8);
			return d;
		} else {
			Dimension d = _jTextField.getPreferredSize();
			d.width = (int) _jTextField.getFontMetrics(_jTextField.getFont())
					.getStringBounds(_jTextField.getText(), _jTextField.getGraphics()).getWidth() + 10;
			if (d.width < 50) {
				d.width = 50;
			}
			_jTextField.setPreferredSize(d);
			d = new Dimension(d.width + 8, d.height + 8);
			return d;
		}
	}
}
