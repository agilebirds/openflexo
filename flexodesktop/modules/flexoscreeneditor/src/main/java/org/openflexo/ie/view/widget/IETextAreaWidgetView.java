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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.dm.table.WidgetRemovedFromTable;
import org.openflexo.foundation.ie.widget.IEEditableTextWidget;
import org.openflexo.foundation.ie.widget.IETextAreaWidget;
import org.openflexo.ie.IEPreferences;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.logging.FlexoLogger;

/**
 * @author bmangez
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class IETextAreaWidgetView extends AbstractInnerTableWidgetView<IETextAreaWidget> implements DisplayableBindingValue {

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================
	protected JTextArea _jTextArea;

	private JScrollPane scrollPane;

	protected static final Logger logger = FlexoLogger.getLogger(IETextFieldWidgetView.class.getPackage().getName());

	protected boolean labelEditing = false;

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

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

	protected JTextArea _jLabelTextArea;// Edition of default value

	protected JScrollPane labelScrollPane;

	public static final Font TEXTAREA_FONT = new Font("SansSerif", Font.PLAIN, 10);

	// private IETextAreaWidget _model;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IETextAreaWidgetView(IEController ieController, IETextAreaWidget model, boolean addDnDSupport, IEWOComponentView view) {
		super(ieController, model, addDnDSupport, view);
		// _model = model;
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
		// layout.setVgap(4);
		setLayout(layout);
		if (getTextAreaModel().getBindingValue() != null) {
			_jTextArea = new JTextArea(getTextAreaModel().getBindingValue().getCodeStringRepresentation());
			scrollPane = new TextAreaPane(_jTextArea);
		} else {
			_jTextArea = new JTextArea(getTextAreaModel().getValue());
			_jTextArea.addMouseListener(mouseListener);
			scrollPane = new TextAreaPane(_jTextArea);
		}
		_jTextArea.setFont(TEXTAREA_FONT);
		_jTextArea.setEditable(false);
		_jTextArea.setFocusable(false);
		_jTextArea.setAutoscrolls(true);
		_jTextArea.setLineWrap(true);
		_jTextArea.setWrapStyleWord(true);
		_jTextArea.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		TransparentMouseListener tml = new TransparentMouseListener(_jTextArea, this);
		/* scrollPane. */_jTextArea.addMouseListener(tml);
		/* scrollPane. */_jTextArea.addMouseMotionListener(tml);
		_jTextArea.setRows(model.getRows());
		// scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane);
		_jTextArea.getParent().validate();
		// scrollPane.validate();
		validate();
		if (getTextAreaModel().getDescription() != null) {
			_jTextArea.setToolTipText(getTextAreaModel().getDescription());
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
		scrollPane.doLayout();
	}

	public IETextAreaWidget getTextAreaModel() {
		return getModel();
	}

	protected class TextAreaPane extends JScrollPane {

		protected JTextArea textArea;

		/**
		 * @param textArea
		 */
		public TextAreaPane(JTextArea textArea) {
			super(textArea, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
			this.textArea = textArea;
		}

		/**
		 * Overrides doLayout
		 * 
		 * @see java.awt.Container#doLayout()
		 */
		@Override
		public void doLayout() {
			super.doLayout();
			getViewport().doLayout();
			this.textArea.doLayout();
		}

		/**
		 * Overrides getPreferredSize
		 * 
		 * @see javax.swing.JComponent#getPreferredSize()
		 */
		@Override
		public Dimension getPreferredSize() {
			Dimension d;
			d = textArea.getPreferredScrollableViewportSize();
			d.width = IETextAreaWidgetView.this.getSize().width - 2;
			d.height += (getInsets().bottom + getInsets().top + textArea.getInsets().top + textArea.getInsets().bottom);
			return d;
		}
	}

	// ==========================================================================
	// ============================= Observer
	// ===================================
	// ==========================================================================
	@Override
	public void updateDisplayedValue() {
		if (IEPreferences.getDisplayBindingValue()) {
			_jTextArea.setText(getTextAreaModel().getBindingValue() != null ? getTextAreaModel().getBindingValue()
					.getCodeStringRepresentation() : "UNBOUND");
			removeDoubleClickListener();
		} else {
			_jTextArea.setText(getTextAreaModel().getValue());
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
		if (modif.modificationType() == DataModification.ATTRIBUTE) {
			if (modif.propertyName().equals(IEEditableTextWidget.BINDING_VALUE)) {
				updateDisplayedValue();
			} else if (modif.propertyName().equals("colSpan") || modif.propertyName().equals("rowSpan")) {
				if (getParent() != null) {
					getParent().doLayout();
					resizeMySelf();
					((JComponent) getParent()).repaint();
				}

			} else if (modif.propertyName().equals("value")) {
				if (getTextAreaModel().getBindingValue() == null)
					_jTextArea.setText(getTextAreaModel().getValue());
			} else if (modif.propertyName().equals("rows")) {
				resizeMySelf();
			}
		}
		if (modif instanceof WidgetRemovedFromTable && arg0 == getModel()) {
			delete();
		} else
			super.update(arg0, modif);
	}

	public void resizeMySelf() {
		_jTextArea.setRows(getTextAreaModel().getRows());
		if (labelEditing && _jLabelTextArea != null)
			_jLabelTextArea.setRows(_jTextArea.getRows());
		doLayout();
		repaint();
		scrollPane.doLayout();
		scrollPane.repaint();
		scrollPane.getViewport().doLayout();
		scrollPane.getViewport().repaint();
		_jTextArea.doLayout();
		_jTextArea.repaint();
	}

	// public Dimension getFavoriteDimension()
	// {
	// if (getParent() != null)
	// return new Dimension(getParent().getWidth() - 5, getHeight() - 5);
	// else
	// return new Dimension(getWidth() - 5, getHeight() - 5);
	// }

	/**
	 * Overrides getPreferredSize
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		if (getHoldsNextComputedPreferredSize()) {
			Dimension storedSize = storedPrefSize();
			if (storedSize != null)
				return storedSize;
		}
		IESequenceWidgetWidgetView parentSequenceView = null;
		if (getParent() instanceof IESequenceWidgetWidgetView) {
			parentSequenceView = (IESequenceWidgetWidgetView) getParent();
		}
		Dimension d = super.getPreferredSize();
		if (parentSequenceView != null) {
			int width = parentSequenceView.getAvailableWidth();
			d.width = width;
		}
		if (getHoldsNextComputedPreferredSize())
			storePrefSize(d);
		return d;
	}

	/**
     * 
     */
	private void addDoubleClickListener() {
		MouseListener[] ml = _jTextArea.getMouseListeners();
		for (int i = 0; i < ml.length; i++) {
			if (ml[i] == mouseListener)
				return;
		}
		_jTextArea.addMouseListener(mouseListener);
	}

	/**
     * 
     */
	private void removeDoubleClickListener() {
		_jTextArea.removeMouseListener(mouseListener);
	}

	public void editValue() {
		if (logger.isLoggable(Level.FINE))
			logger.fine("Edit ie textarea");
		labelEditing = true;
		_jLabelTextArea = new JTextArea(getTextAreaModel().getValue());
		labelScrollPane = new TextAreaPane(_jLabelTextArea);
		// _jLabelTextArea.setBounds(_jTextArea.getBounds());
		_jLabelTextArea.setMinimumSize(new Dimension(30, 15));
		_jLabelTextArea.setFont(_jTextArea.getFont());
		_jLabelTextArea.setFocusable(true);
		_jLabelTextArea.setAutoscrolls(true);
		_jLabelTextArea.setLineWrap(true);
		_jLabelTextArea.setWrapStyleWord(true);
		_jLabelTextArea.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		_jLabelTextArea.setRows(_jTextArea.getRows());
		_jLabelTextArea.getDocument().addDocumentListener(new DocumentListener() {
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
				repaint();
			}
		});
		_jLabelTextArea.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if (logger.isLoggable(Level.FINEST))
					logger.finest("Focus lost" + arg0.getOppositeComponent() + " and this=" + IETextAreaWidgetView.this);
				if (arg0.getOppositeComponent() != IETextAreaWidgetView.this)
					finalizeEditValue();
				else
					_jLabelTextArea.grabFocus();
			}
		});
		remove(scrollPane);
		add(labelScrollPane);
		_jLabelTextArea.grabFocus();
		_jLabelTextArea.selectAll();
		validate();
		repaint();
	}

	public void finalizeEditValue() {
		if (logger.isLoggable(Level.FINE))
			logger.fine("Finalize edit ie textarea");
		if (labelEditing) {
			getTextAreaModel().setValue(_jLabelTextArea.getText());
			labelEditing = false;
			remove(labelScrollPane);
			add(scrollPane);
			validate();
			repaint();
		}
	}

}
