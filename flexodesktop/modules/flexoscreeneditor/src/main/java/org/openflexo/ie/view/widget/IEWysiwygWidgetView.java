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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JEditorPane;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.dm.table.WidgetRemovedFromTable;
import org.openflexo.foundation.ie.widget.IEWysiwygWidget;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.wysiwyg.EditableHtmlWidget;
import org.openflexo.wysiwyg.FlexoWysiwygPopup;

/**
 * @author bmangez
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class IEWysiwygWidgetView extends AbstractInnerTableWidgetView<IEWysiwygWidget> implements EditableHtmlWidget {

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	private JEditorPane _jEditorPane;
	private MouseListener mouseListener;
	protected FlexoWysiwygPopup popup;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IEWysiwygWidgetView(IEController ieController, IEWysiwygWidget model, boolean addDnDSupport, IEWOComponentView view) {
		super(ieController, model, addDnDSupport, view);
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 2, 2);
		layout.setVgap(4);
		setLayout(layout);
		_jEditorPane = new JEditorPane() {
			/**
			 * Overrides getPreferredSize
			 * 
			 * @see javax.swing.JEditorPane#getPreferredSize()
			 */
			@Override
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				if (IEWysiwygWidgetView.this.getParent() instanceof IESequenceWidgetWidgetView
						&& ((IESequenceWidgetWidgetView) IEWysiwygWidgetView.this.getParent()).getAvailableWidth() > 0) {
					d.width = ((IESequenceWidgetWidgetView) IEWysiwygWidgetView.this.getParent()).getAvailableWidth() - 4;
				}
				return d;
			}
		};
		_jEditorPane.setContentType("text/html");
		_jEditorPane.setText(model.getValue());
		_jEditorPane.setEditable(false);
		_jEditorPane.setOpaque(false);
		_jEditorPane.setAlignmentY(0.5f);
		TransparentMouseListener tml = new TransparentMouseListener(_jEditorPane, this);
		_jEditorPane.addMouseListener(tml);
		_jEditorPane.addMouseMotionListener(tml);
		if (model.getDescription() != null) {
			_jEditorPane.setToolTipText(model.getDescription());
		}
		add(_jEditorPane);
		addMouseListener(mouseListener = new MyMouseListener(this));
	}

	private class MyMouseListener extends MouseAdapter {
		private EditableHtmlWidget _widget;

		public MyMouseListener(EditableHtmlWidget widget) {
			_widget = widget;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				if (popup == null) {
					popup = new FlexoWysiwygPopup(_widget, null);
				} else {
					// set the new text in case of it has been changed from the inspector view
					popup.getWysiwyg().setContent(getValue());
					popup.pack();
					popup.setVisible(true);
				}
			}
		}
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

		String propertyName = modif.propertyName();
		if (propertyName != null) {
			if (propertyName.equals(BINDING_VALUE_NAME)) {
				_jEditorPane.setText(getValue());
				if (getParent() != null) {
					getParent().doLayout();
					getParent().repaint();
				}
			} else if (propertyName.equals("cssClass")) {
				_jEditorPane.setFont(getWysiwygModel().getTextCSSClass().font());
			}
		}
		if (modif instanceof WidgetRemovedFromTable && arg0 == getModel()) {
			delete();
		} else {
			super.update(arg0, modif);
		}
	}

	public boolean isHyperlink() {
		return false;
	}

	@Override
	public boolean getHoldsNextComputedPreferredSize() {
		return false;
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
		Dimension d = _jEditorPane.getPreferredSize();
		d.width += 4;
		d.height += 6;
		if (getHoldsNextComputedPreferredSize()) {
			storePrefSize(d);
		}
		return d;
	}

	@Override
	public String getValue() {
		return getWysiwygModel().getValue();
	}

	@Override
	public void setValue(String value) {
		getWysiwygModel().setValue(value);
	}

	public IEWysiwygWidget getWysiwygModel() {
		return getModel();
	}

	@Override
	public void delete() {
		popup = null;
		super.delete();
	}

}
