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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
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
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.ie.HTMLListDescriptor;
import org.openflexo.foundation.ie.dm.ExcellButtonStateChange;
import org.openflexo.foundation.ie.dm.InnerBlocInserted;
import org.openflexo.foundation.ie.dm.InnerBlocRemoved;
import org.openflexo.foundation.ie.dm.RefreshButtonStateChange;
import org.openflexo.foundation.ie.dm.TopComponentRemoved;
import org.openflexo.foundation.ie.operator.RepetitionOperator;
import org.openflexo.foundation.ie.widget.ContentSizeChanged;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.icon.IconLibrary;
import org.openflexo.ie.util.TriggerRepaintDocumentListener;
import org.openflexo.ie.view.DropZoneTopComponent;
import org.openflexo.ie.view.IEContainer;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.listener.DoubleClickResponder;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author bmangez
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class IEBlocWidgetView extends IEWidgetView<IEBlocWidget> implements DoubleClickResponder, IEContainer, LabeledWidget {

	private static final Logger logger = Logger.getLogger(IEBlocWidgetView.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	private TopTitle _topTitle;

	private ButtonPanel _buttonPanel;

	private DropTableZone _dropTableZone;

	public static final Font BLOC_TITLE_FONT = new Font("SansSerif", Font.BOLD, 10);

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IEBlocWidgetView(IEController ieController, IEBlocWidget model, boolean addDnDSupport, IEWOComponentView componentView) {
		super(ieController, model, addDnDSupport, componentView);
		setLayout(new BorderLayout());
		setDefaultBorder();
		new ObserverRegistation(this, model.getWOComponent());
		setBackground(Color.WHITE);
		_topTitle = new TopTitle(model);
		_buttonPanel = new ButtonPanel(getIEController(), model, _componentView);
		_dropTableZone = new DropTableZone(getIEController(), this, _componentView);
		if (model.getContent() instanceof FlexoModelObject) {
			new ObserverRegistation(this, (FlexoModelObject) model.getContent());
		}
		add(_topTitle, BorderLayout.NORTH);
		add(_buttonPanel, BorderLayout.SOUTH);
		add(_dropTableZone, BorderLayout.CENTER);
	}

	@Override
	public void setDefaultBorder() {
		setBorder(BorderFactory.createLineBorder(getMainColor()));
	}

	@Override
	public void delete() {
		if (getModel().getContent() != null && getModel().getContent() instanceof FlexoModelObject) {
			((FlexoModelObject) getModel().getContent()).deleteObserver(this);
		}

		if (_dropTableZone != null) {
			_dropTableZone.delete();
		}
		_dropTableZone = null;
		if (_buttonPanel != null) {
			_buttonPanel.delete();
		}
		_buttonPanel = null;
		super.delete();
	}

	@Override
	public void performDoubleClick(JComponent clickedContainer, Point clickedPoint, boolean isShiftDown) {
		if (_topTitle.contains(SwingUtilities.convertPoint(clickedContainer, clickedPoint, this))) {
			editLabel();
		}
	}

	/**
	 * Overrides propagateResize
	 * 
	 * @see org.openflexo.ie.view.widget.IEWidgetView#propagateResize()
	 */
	@Override
	public void propagateResize() {
		super.propagateResize();
		_buttonPanel.propagateResize();
		_dropTableZone.propagateResize();
	}

	// ==========================================================================
	// ============================= Observer
	// ===================================
	// ==========================================================================
	private void updateInnerBlocInsertion(IEWidget widget) {
		IEWidgetView view = _componentView.getViewForWidget(widget, true);
		_dropTableZone.add(view, BorderLayout.CENTER);
		_dropTableZone.setTableView(view);
		_dropTableZone.validate();
		_dropTableZone.doLayout();
		((JPanel) _dropTableZone.getParent()).repaint();
		new ObserverRegistation(this, widget);
		handleContentResize();
	}

	private void updateInnerBlocRemoved(IEWidget widget) {
		stopObserving(widget);
		if (widget.getParent() != null && widget.getParent() == getModel()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateHTMLTableRemoval");
			}
			if (_dropTableZone.getTableView() != null) {
				_dropTableZone.getTableView().getModel().deleteObserver(_dropTableZone.getTableView());
			}
			_dropTableZone.removeAll();// (_dropTableZone.getTableView());
			_dropTableZone.removeTableView();
			_dropTableZone.validate();
			((JPanel) _dropTableZone.getParent()).repaint();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(FlexoObservable arg0, DataModification modif) {
		if (modif instanceof TopComponentRemoved && arg0 == getModel()) {
			delete();
		} else if (modif instanceof RefreshButtonStateChange && arg0 == getModel()) {
			if (((Boolean) modif.newValue()).booleanValue()) {
				if (_topTitle.buttonPane == null) {
					_topTitle.initButtonPane();
				}
				_topTitle.buttonPane.add(_topTitle._refreshButton);
			} else {
				_topTitle.buttonPane.remove(_topTitle._refreshButton);
			}
			_topTitle.buttonPane.revalidate();
			_topTitle.buttonPane.repaint();
		} else if (modif instanceof ExcellButtonStateChange && arg0 == getModel()) {
			if (((Boolean) modif.newValue()).booleanValue()) {
				if (_topTitle.buttonPane == null) {
					_topTitle.initButtonPane();
				}
				_topTitle.buttonPane.add(_topTitle._excelButton);
			} else {
				_topTitle.buttonPane.remove(_topTitle._excelButton);
			}
			_topTitle.buttonPane.revalidate();
			_topTitle.buttonPane.repaint();
		}
		if (IEBlocWidget.BLOC_TITLE_ATTRIBUTE_NAME.equals(modif.propertyName())) {
			setTitle(getModel().getTitle());
		} else if (modif instanceof InnerBlocInserted) {
			updateInnerBlocInsertion((IEWidget) modif.newValue());
			_topTitle.initButtonPane();
		} else if (modif instanceof InnerBlocRemoved) {
			updateInnerBlocRemoved((IEWidget) modif.oldValue());
			_topTitle.initButtonPane();
		} else if (modif instanceof ContentSizeChanged) {
			handleContentResize();
		} else {
			super.update(arg0, modif);
		}
	}

	public void handleContentResize() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Bloc resize due to content size change.");
		}
		_dropTableZone.setPreferredSize(new Dimension(_dropTableZone.getPreferredSize().width,
				_dropTableZone.getPreferredSize().height + 24));
		doLayout();
		repaint();
	}

	public int getColSpan() {
		return getModel().getColSpan();
	}

	private Border getParentBorder() {
		return ((JComponent) getParent()).getBorder();
	}

	private Insets getParentBorderInsets() {
		return getParentBorder().getBorderInsets(getParent());
	}

	private Border getGrandParentBorder() {
		if (getParent().getParent() == null) {
			return null;
		}
		return ((JComponent) getParent().getParent()).getBorder();
	}

	private Insets getGrandParentBorderInsets() {
		return getGrandParentBorder().getBorderInsets(getParent().getParent());
	}

	@Override
	public Dimension getPreferredSize() {
		if (getHoldsNextComputedPreferredSize()) {
			Dimension storedSize = storedPrefSize();
			if (storedSize != null) {
				return storedSize;
			}
		}
		// int k = getModel().getParent() instanceof IETDWidget ? 8 : 0;
		IESequenceWidgetWidgetView parentSequenceView = null;
		if (getParent() instanceof IESequenceWidgetWidgetView) {
			parentSequenceView = (IESequenceWidgetWidgetView) getParent();
		}
		int titleAndButtonPanelHeight = _topTitle.getPreferredSize().height + _buttonPanel.getPreferredSize().height;
		if (titleAndButtonPanelHeight < 36) {
			titleAndButtonPanelHeight = 36;
		}
		if (parentSequenceView != null) {
			int width = parentSequenceView.getAvailableWidth();
			Dimension d = new Dimension(width, _dropTableZone.getComponentCount() > 0 ? new Double(_dropTableZone.getComponent(0)
					.getPreferredSize().getHeight()).intValue()
					+ titleAndButtonPanelHeight : 24 + titleAndButtonPanelHeight);
			if (getHoldsNextComputedPreferredSize()) {
				storePrefSize(d);
			}
			return d;
		}
		int parentInsetsWidth = getParent().getInsets().left + getParent().getInsets().right;
		int parentBorderInsetsWidth = getParentBorder() != null ? getParentBorderInsets().left + getParentBorderInsets().right : 0;

		int grandParentInsetsWidth = getParent().getParent() == null || getParent().getParent().getInsets() == null ? 0 : getParent()
				.getParent().getInsets().left + getParent().getParent().getInsets().right;
		int grandParentBorderInsetsWidth = getGrandParentBorder() != null ? getGrandParentBorderInsets().left
				+ getGrandParentBorderInsets().right : 0;
		int totalWidth = parentInsetsWidth + parentBorderInsetsWidth + grandParentInsetsWidth + grandParentBorderInsetsWidth;
		Dimension dim = new Dimension(getDropZoneWith() - totalWidth, _dropTableZone.getComponentCount() > 0 ? new Double(_dropTableZone
				.getComponent(0).getPreferredSize().getHeight()).intValue()
				+ titleAndButtonPanelHeight : 24 + titleAndButtonPanelHeight);
		if (getHoldsNextComputedPreferredSize()) {
			storePrefSize(dim);
		}
		return dim;
	}

	private int getDropZoneWith() {
		if (getDropZone() != null) {
			return getDropZone().getAvailableWidth();
		}
		return _componentView.getMaxWidth() - IESequenceWidgetWidgetView.LAYOUT_GAP * 2;
	}

	private DropZoneTopComponent getDropZone() {
		Container c = getParent();
		while (c != null && !(c instanceof DropZoneTopComponent)) {
			c = c.getParent();
		}
		return (DropZoneTopComponent) c;
	}

	public String getTitle() {
		return getModel().getTitle();
	}

	private void setTitle(String s) {
		_topTitle.setText(s.toUpperCase());
	}

	protected JTextField _jLabelTextField = null;

	protected boolean labelEditing = false;

	private JLabel topTitleLabel() {
		return _topTitle._label;
	}

	@Override
	public void editLabel() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Edit ie bloc label");
		}
		labelEditing = true;
		_jLabelTextField = new JTextField(getTitle());
		_jLabelTextField.setFont(BLOC_TITLE_FONT);
		_jLabelTextField.setBorder(BorderFactory.createEmptyBorder());
		// _jLabelTextField.setForeground(getFlexoNode().getTextColor());
		_jLabelTextField.setBounds(topTitleLabel().getBounds());
		_jLabelTextField.setHorizontalAlignment(SwingConstants.CENTER);
		_jLabelTextField.getDocument().addDocumentListener(new TriggerRepaintDocumentListener(this));
		_topTitle.removeLabel(topTitleLabel());
		_topTitle.addTextField(_jLabelTextField);
		_jLabelTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				finalizeEditBloc();
			}
		});
		_jLabelTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				finalizeEditBloc();
			}
		});
		_jLabelTextField.requestFocus();
		_jLabelTextField.selectAll();
		_jLabelTextField.revalidate();
		_jLabelTextField.repaint();
		_topTitle.revalidate();
		_topTitle.repaint();
	}

	public void finalizeEditBloc() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Finalize edit bloc");
		}
		if (labelEditing) {
			getModel().setTitle(_jLabelTextField.getText());
		}
		labelEditing = false;
		_topTitle.removeTextField(_jLabelTextField);
		_topTitle.addLabel(topTitleLabel());
		// layoutComponent();
		_topTitle.revalidate();
		_topTitle.repaint();
	}

	// ==========================================================================
	// ============================= TopTitle
	// ===================================
	// ==========================================================================

	private class TopTitle extends JPanel implements GraphicalFlexoObserver {

		public TopTitle(IEBlocWidget model) {
			super(new BorderLayout());
			_model = model;
			labelPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
			setBackground(getMainColor());
			labelPane.setBackground(getMainColor());
			if (getTitle() != null) {
				_label = new JLabel(getTitle().toUpperCase());
			} else {
				_label = new JLabel(FlexoLocalization.localizedForKey("unnamed"));
			}
			_label.setBackground(getMainColor());
			_label.setForeground(getTextColor());
			_label.setFont(BLOC_TITLE_FONT);
			labelPane.setPreferredSize(new Dimension(-1, 18));
			labelPane.add(_label);
			add(labelPane, BorderLayout.CENTER);

			initButtonPane();

			// model.addObserver(this);
		}

		public void initButtonPane() {
			HTMLListDescriptor desc = HTMLListDescriptor.createInstanceForBloc(_model);
			RepetitionOperator rep = null;
			if (desc != null) {
				rep = desc.getRepetitionOperator();
			}
			if (buttonPane == null) {
				buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
			}
			buttonPane.setBackground(getMainColor());
			_excelButton.setBackground(getMainColor());
			_refreshButton.setBackground(getMainColor());
			if (rep != null && rep.refreshButton()) {
				buttonPane.add(_refreshButton, 0);
			} else {
				buttonPane.remove(_refreshButton);
			}
			if (rep != null && rep.excelButton()) {
				buttonPane.add(_excelButton);
			} else {
				buttonPane.remove(_excelButton);
			}
			buttonPane.validate();
			if (buttonPane.getParent() == null) {
				add(buttonPane, BorderLayout.EAST);
			}
		}

		@Override
		public void update(FlexoObservable arg0, DataModification modif) {
			/*
			 * if (arg0.equals(_model) && modif instanceof InnerBlocRemoved) { ((FlexoObservable) modif.oldValue()).deleteObserver(this); if
			 * (buttonPane != null && buttonPane.getParent() != null) { remove(buttonPane); } }
			 */
		}

		public void setText(String s) {
			_label.setText(s.toUpperCase());
		}

		public void setLabelBackground(Color c) {
			_label.setBackground(c);
		}

		public void setLabelForeground(Color c) {
			_label.setForeground(c);
		}

		public void addLabel(JLabel l) {
			labelPane.add(l);
		}

		public void removeLabel(JLabel l) {
			labelPane.remove(l);
		}

		public void removeTextField(JTextField t) {
			labelPane.remove(t);
		}

		public void addTextField(JTextField t) {
			labelPane.add(t);
		}

		private JPanel labelPane;

		protected JPanel buttonPane;

		protected JLabel _label;

		private IEBlocWidget _model;

		protected JLabel _excelButton = new JLabel(IconLibrary.SMALL_EXCEL_ICON);

		protected JLabel _refreshButton = new JLabel(IconLibrary.REFRESH_ICON);
	}

}
