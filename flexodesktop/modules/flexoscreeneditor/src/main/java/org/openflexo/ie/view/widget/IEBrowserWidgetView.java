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

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.widget.IEBrowserWidget;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;

public class IEBrowserWidgetView extends AbstractInnerTableWidgetView<IEBrowserWidget> implements ListDataListener {

	private JList _jList;

	private JScrollPane scrollPane;

	public IEBrowserWidgetView(IEController ieController, IEBrowserWidget model, boolean addDnDSupport, IEWOComponentView view) {
		super(ieController, model, addDnDSupport, view);
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
		setLayout(layout);
		_jList = new JList(getBrowserModel());
		_jList.setFont(IETextAreaWidgetView.TEXTAREA_FONT);
		_jList.setFocusable(false);
		_jList.setOpaque(false);
		_jList.setEnabled(false);
		_jList.setVisibleRowCount(model.getVisibleRows());
		_jList.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		TransparentMouseListener tml = new TransparentMouseListener(_jList, this);
		_jList.addMouseListener(tml);
		_jList.addMouseMotionListener(tml);
		model.addListDataListener(this);
		scrollPane = new JListPane(_jList);
		add(scrollPane);
	}

	protected class JListPane extends JScrollPane {

		protected JList jList;

		/**
		 * @param textArea
		 */
		public JListPane(JList textArea) {
			super(textArea, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
			this.jList = textArea;
		}

		/**
		 * Overrides getPreferredSize
		 * 
		 * @see javax.swing.JComponent#getPreferredSize()
		 */
		@Override
		public Dimension getPreferredSize() {
			Dimension d;
			d = jList.getPreferredScrollableViewportSize();
			if (getBrowserModel().getSize() == 0) {
				d.width = 30;// Minimal size
			} else {
				d.width += getInsets().left + getInsets().right + jList.getInsets().left + jList.getInsets().right;
				if (getVerticalScrollBar() != null && getVerticalScrollBar().isVisible()) {
					d.width += getVerticalScrollBar().getWidth();
				}
			}
			d.height += jList.getInsets().top + jList.getInsets().bottom;
			return d;
		}
	}

	public IEBrowserWidget getBrowserModel() {
		return getModel();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification.propertyName() != null && dataModification.propertyName().equals("visibleRows")) {
			_jList.setVisibleRowCount(getBrowserModel().getVisibleRows());
			revalidate();
			repaint();
		} else {
			super.update(observable, dataModification);
		}
	}

	/**
	 * Overrides contentsChanged
	 * 
	 * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
	 */
	@Override
	public void contentsChanged(ListDataEvent e) {
		revalidate();
		repaint();
	}

	/**
	 * Overrides intervalAdded
	 * 
	 * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
	 */
	@Override
	public void intervalAdded(ListDataEvent e) {
		revalidate();
		repaint();
	}

	/**
	 * Overrides intervalRemoved
	 * 
	 * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
	 */
	@Override
	public void intervalRemoved(ListDataEvent e) {
		revalidate();
		repaint();
	}

}
