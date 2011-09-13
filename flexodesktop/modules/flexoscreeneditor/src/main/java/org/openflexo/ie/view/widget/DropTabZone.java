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
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.ie.view.IEContainer;
import org.openflexo.ie.view.IEViewManaging;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.Layoutable;
import org.openflexo.ie.view.ViewFinder;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.controller.dnd.IEDTListener;


/**
 * @author bmangez
 */
public class DropTabZone extends JTabbedPane implements IEContainer, IEViewManaging, ChangeListener, Layoutable
{

    private IEWOComponentView _rootView;

    private IEController _ieController;

    private boolean holdsNextComputedPreferredSize = false;

    private Dimension preferredSize = null;

    public DropTabZone(IEController ieController, IETabContainerWidgetView parent)
    {
        super();
        _ieController = ieController;
        _parent = parent;
        _rootView = parent._componentView;
        setOpaque(false);
        this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY, new IEDTListener(ieController, this, parent.getModel()), true));
        addMouseListener(ieController.getIESelectionManager());
        addChangeListener(this);
    }

    public void delete() {
        removeAll();
        if (getParent()!=null)
            getParent().remove(this);
    }

    public IEWidget getIEModel()
    {
        if (getTabCount() > 0)
            return ((IETabWidgetView) getSelectedComponent()).getTabWidget();
        else
            return getTabModel();
    }

    @Override
	public IEWidget getContainerModel()
    {
        return getIEModel();
    }

    public IESequenceTab getTabModel()
    {
        return _parent.getModel();
    }

    private IETabContainerWidgetView _parent;

    @Override
	public IEWOComponentView getRootView()
    {
        if (_rootView == null) {
            _rootView = ViewFinder.getRootView(this);
        }
        return _rootView;
    }

    @Override
	public IEWidgetView findViewForModel(IEObject object)
    {
        return ViewFinder.findViewForModel(this, object);
    }

    @Override
	public IEWidgetView internallyFindViewForModel(IEObject object)
    {
        return ViewFinder.internallyFindViewForModel(this, object);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    @Override
	public void stateChanged(ChangeEvent arg0)
    {
        IETabWidgetView selectedView = (IETabWidgetView) getSelectedComponent();
        if (selectedView != null) {
            if (!selectedView.getTabVisibility()) {
                Component[] comps = getComponents();
                for (Component c : comps) {
                    if (c instanceof IETabWidgetView) {
                        ((IETabWidgetView)c).setTabVisibility(false);
                    }
                }
                selectedView.setTabVisibility(true);

            }
            _ieController.getIESelectionManager().processMouseClicked(selectedView, new Point(1, 1), 1, false);
        }
    }

    public void updateTabInsertion(IETabWidget newTab, int index)
    {
    	IEReusableWidgetView view = (IEReusableWidgetView) _rootView.getViewForWidget(newTab, false);
        view.setName(newTab.getTitle());
        try {
            add(view, index);
            if (((IESequenceTab) newTab.getParent()).isConditional())
                setIconAt(index, BrowserElementType.CONDITIONAL.getIcon());
            else
                setIconAt(index, null);
            validate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        view.validate();
        view.doLayout();
        view.repaint();
        doLayout();
        repaint();
        setSelectedComponent(view);
    }

    public void updateTabReodering()
    {
    	Component c = getSelectedComponent();
        Vector<IETabWidgetView> orderedView = new Vector<IETabWidgetView>();
        Enumeration<IETabWidget> en = getTabModel().getAllTabs().elements();
        while (en.hasMoreElements()) {
            orderedView.add((IETabWidgetView) findViewForModel(en.nextElement()));
        }
        removeAll();
        Enumeration<IETabWidgetView> en1 = orderedView.elements();
        int i = 0;
        while (en1.hasMoreElements()) {
            add(en1.nextElement(), i);
            i++;
        }
        updateConditionalIcons();
        if (c!=null && indexOfComponent(c)>-1)
        	setSelectedComponent(c);
    }

    /**
     * Overrides setSelectedComponent
     *
     * @see javax.swing.JTabbedPane#setSelectedComponent(java.awt.Component)
     */
    @Override
    public void setSelectedComponent(Component c)
    {
        super.setSelectedComponent(c);
        if (c!=null && c instanceof Layoutable)
            propagateResize();
    }

    /**
     * Overrides setSelectedIndex
     * @see javax.swing.JTabbedPane#setSelectedIndex(int)
     */
    @Override
    public void setSelectedIndex(int index)
    {
        if (getSelectedComponent() instanceof IETabWidgetView)
            ((IETabWidgetView)getSelectedComponent()).setTabVisibility(false);
        if (getComponentAt(index) instanceof IETabWidgetView)
            ((IETabWidgetView)getComponentAt(index)).setTabVisibility(true);
        super.setSelectedIndex(index);
    }

    /**
     *
     */
    public void updateConditionalIcons()
    {
        for (int i = 0; i < getTabCount(); i++) {
            Component component = getComponentAt(i);
            if (((IESequenceTab) ((IETabWidgetView) component).getTabWidget().getParent()).isConditional())
                setIconAt(i, BrowserElementType.CONDITIONAL.getIcon());
            else
                setIconAt(i, null);
        }
    }

    /**
     * Overrides doLayout
     *
     * @see java.awt.Container#doLayout()
     */
    @Override
    public void doLayout()
    {
        _rootView.notifyAllViewsToHoldTheirNextComputedPreferredSize(this);
        if (getParent() instanceof Layoutable) {
            ((Layoutable) getParent()).doLayout();
        }
        super.doLayout();
        _rootView.resetAllViewsPreferredSize(this);
    }

    /**
     * Overrides propagateResize
     *
     * @see org.openflexo.ie.view.Layoutable#propagateResize()
     */
    @Override
	public void propagateResize()
    {
        if (getSelectedComponent()!=null && getSelectedComponent() instanceof Layoutable) {
            ((Layoutable) getSelectedComponent()).propagateResize();
        }
    }

    /**
     * Overrides getHoldsNextComputedPreferredSize
     *
     * @see org.openflexo.ie.view.Layoutable#getHoldsNextComputedPreferredSize()
     */
    @Override
	public boolean getHoldsNextComputedPreferredSize()
    {
        return holdsNextComputedPreferredSize;
    }

    /**
     * Overrides resetPreferredSize
     *
     * @see org.openflexo.ie.view.Layoutable#resetPreferredSize()
     */
    @Override
	public void resetPreferredSize()
    {
        holdsNextComputedPreferredSize = false;
        preferredSize = null;
    }

    /**
     * Overrides setHoldsNextComputedPreferredSize
     *
     * @see org.openflexo.ie.view.Layoutable#setHoldsNextComputedPreferredSize()
     */
    @Override
	public void setHoldsNextComputedPreferredSize()
    {
        holdsNextComputedPreferredSize = true;
    }

    /**
     * Overrides getPreferredSize
     *
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize()
    {
        if (getHoldsNextComputedPreferredSize() && preferredSize != null)
            return preferredSize;
        Dimension d = super.getPreferredSize();
        if (getHoldsNextComputedPreferredSize())
            preferredSize = d;
        return d;
    }

	@Override
	public IEWOComponent getWOComponent() {
		return getTabModel().getWOComponent();
	}
}
