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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.util.WidgetType;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.ie.view.IEContainer;
import org.openflexo.ie.view.IEPanel;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.Layoutable;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.controller.dnd.IEDTListener;
import org.openflexo.toolbox.ToolBox;


public class DropTableZone extends IEPanel implements IEContainer, Layoutable {

	private IEWOComponentView _componentView;

	private boolean holdsNextComputedPreferredSize = false;

	private Dimension preferredSize = null;

	private IEBlocWidgetView _parent;

	private IEWidgetView _tableView;

	public DropTableZone(IEController ieController, IEBlocWidgetView parent, IEWOComponentView componentView) {
		super(ieController);
		_componentView = componentView;
		setLayout(new BorderLayout());
		_parent = parent;
		setBackground(Color.WHITE);

		setMinimumSize(new Dimension(_parent.getWidth() + 5, 24));

		this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY, new IEDTListener(ieController, this, parent.getModel()), true));
		addMouseListener(ieController.getIESelectionManager());
		addMouseListener(new MouseAdapter() {
			private MouseEvent previousEvent;

			@Override
			public void mouseClicked(MouseEvent e) {
				if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
					if (e.getClickCount() == 2 && previousEvent != null) {
						if (previousEvent.getClickCount() == 1 && previousEvent.getComponent() == e.getComponent() && previousEvent
								.getButton() != e.getButton()) {
							e = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX(), e.getY(), 1,
									e.isPopupTrigger());
						}
					}
				}
				previousEvent = e;
				if (e.getClickCount() == 2 && e.getButton() == 1) {
					DropIEElement dropTable = DropIEElement.actionType.makeNewAction(getBlocModel(), null, getIEController().getEditor());
					dropTable.setElementType(WidgetType.HTMLTable);
					dropTable.doAction();
				}
			}
		});
		if (parent.getModel().getContent() != null && parent.getModel().getContent() instanceof IEWidget) {
			_tableView = _componentView.getViewForWidget((IEWidget) parent.getModel().getContent(), true);
			add(_tableView);
		}
	}

	/**
     *
     */
	public void delete() {
		if (_tableView != null) {
			_tableView.delete();
			_tableView = null;
		}
		if (getParent() != null) {
			getParent().remove(this);
		}
		_parent = null;
		removeAll();
	}

	/**
	 * Overrides doLayout
	 * 
	 * @see java.awt.Container#doLayout()
	 */
	@Override
	public void doLayout() {
		_componentView.notifyAllViewsToHoldTheirNextComputedPreferredSize(this);
		if (getParent() instanceof Layoutable) {
			((Layoutable) getParent()).doLayout();
		}
		super.doLayout();
		_componentView.resetAllViewsPreferredSize(this);
	}

	/**
	 * Overrides propagateResize
	 * 
	 * @see org.openflexo.ie.view.Layoutable#propagateResize()
	 */
	@Override
	public void propagateResize() {
		Component[] c = getComponents();
		for (int i = 0; i < c.length; i++) {
			if (c[i] instanceof Layoutable) {
				((Layoutable) c[i]).propagateResize();
			}
		}
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(_parent.getWidth() + 5, 24);
	}

	/**
	 * Overrides getPreferredSize
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		if (getHoldsNextComputedPreferredSize() && preferredSize != null) {
			return preferredSize;
		}
		Dimension d;
		if (_tableView != null) {
			d = new Dimension(_tableView.getPreferredSize().width, _tableView.getPreferredSize().height + 24);
		} else {
			d = super.getPreferredSize();
		}
		if (getHoldsNextComputedPreferredSize()) {
			preferredSize = d;
		}
		return d;
	}

	@Override
	public IEWidget getContainerModel() {
		return getBlocModel();
	}

	public IEBlocWidget getBlocModel() {
		return _parent.getModel();
	}

	public void setTableView(IEWidgetView view) {
		_tableView = view;
	}

	public IEWidgetView getTableView() {
		return _tableView;
	}

	public void removeTableView() {
		_tableView = null;
	}

	public IEBlocWidgetView getBlock() {
		return _parent;
	}

	/**
	 * @param widget
	 * @param view
	 */
	public void add(IEHTMLTableWidget widget) {
		getBlocModel().insertContent(widget);
	}

	/**
	 * Overrides getHoldsNextComputedPreferredSize
	 * 
	 * @see org.openflexo.ie.view.Layoutable#getHoldsNextComputedPreferredSize()
	 */
	@Override
	public boolean getHoldsNextComputedPreferredSize() {
		return holdsNextComputedPreferredSize;
	}

	/**
	 * Overrides resetPreferredSize
	 * 
	 * @see org.openflexo.ie.view.Layoutable#resetPreferredSize()
	 */
	@Override
	public void resetPreferredSize() {
		holdsNextComputedPreferredSize = false;
		preferredSize = null;
	}

	/**
	 * Overrides setHoldsNextComputedPreferredSize
	 * 
	 * @see org.openflexo.ie.view.Layoutable#setHoldsNextComputedPreferredSize()
	 */
	@Override
	public void setHoldsNextComputedPreferredSize() {
		holdsNextComputedPreferredSize = true;
	}

	@Override
	public IEWOComponent getWOComponent() {
		return getBlocModel().getWOComponent();
	}

}
