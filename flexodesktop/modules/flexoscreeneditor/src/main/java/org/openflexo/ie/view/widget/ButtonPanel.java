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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingConstants;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.dm.ButtonAdded;
import org.openflexo.foundation.ie.dm.WidgetAddedToSequence;
import org.openflexo.foundation.ie.dm.WidgetRemovedFromSequence;
import org.openflexo.foundation.ie.util.FlexoConceptualColor;
import org.openflexo.foundation.ie.util.WidgetType;
import org.openflexo.foundation.ie.widget.ButtonedWidgetInterface;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.ie.view.IEContainer;
import org.openflexo.ie.view.IEPanel;
import org.openflexo.ie.view.IEViewUtils;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.Layoutable;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.controller.dnd.IEDTListener;
import org.openflexo.toolbox.ToolBox;


public class ButtonPanel extends IEPanel implements IEContainer, GraphicalFlexoObserver, Layoutable
{
    private static final Logger logger = Logger.getLogger(ButtonPanel.class.getPackage().getName());

    // private final AbstractColoredWidgetView _view;

    private final ButtonedWidgetInterface _model;

    private IEWOComponentView _componentView;

    private boolean holdsNextComputedPreferredSize=false;

    private Dimension preferredSize=null;

    public ButtonPanel(IEController ieController, ButtonedWidgetInterface model, IEWOComponentView componentView)
    {
        super(ieController);
        _componentView = componentView;
        setLayout(new IETDFlowLayout(FlowLayout.LEFT, 3, 1,SwingConstants.CENTER));
        this._model = model;
        setBackground(IEViewUtils.colorFromConceptualColor(FlexoConceptualColor.MAIN_COLOR, model.getFlexoCSS()));
        this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY, new IEDTListener(ieController, this, (IEObject) model), true));
        Enumeration<IEWidget> en = model.getSequenceWidget().elements();
        IEWidgetView view;
        while (en.hasMoreElements()) {
            IEWidget w = en.nextElement();
            view = _componentView.getViewForWidget(w, true);
            super.add(view);
        }
        validate();
        model.addObserver(this);
        model.getSequenceWidget().addObserver(this);
        addMouseListener(ieController.getIESelectionManager());
        addMouseListener(new MouseAdapter() {
        	
        	private MouseEvent previousEvent;
        	
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		if (ToolBox.getPLATFORM()==ToolBox.MACOS) {
        			if (e.getClickCount()==2 && previousEvent!=null) {
        				if (previousEvent.getClickCount()==1 && previousEvent.getComponent()==e.getComponent() && previousEvent.getButton()!=e.getButton()) {
        					e = new MouseEvent(e.getComponent(),e.getID(),e.getWhen(),e.getModifiers(),e.getX(),e.getY(),1,e.isPopupTrigger());
        				}
        			}
        		}
        		previousEvent = e;
        		if (e.getClickCount()==2 && e.getButton()==1) {
        			DropIEElement dropButton = DropIEElement.actionType.makeNewAction((IEObject)getButtonedWidgetModel(), null, getIEController().getEditor());
        			dropButton.setElementType(WidgetType.CUSTOMBUTTON);
        			dropButton.setIndex(findInsertionIndex(e.getX()));
        			dropButton.doAction();
        		}
        	}
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e)
            {
                getIEController().getIESelectionManager().processMouseMoved(e);
            }
        });
    }
    /**
     *
     */
    public void delete()
    {
        if (_model!=null) {
            _model.deleteObserver(this);
            if (_model.getSequenceWidget()!=null)
                _model.getSequenceWidget().deleteObserver(this);
        }
        Component[] comp = getComponents();
        for (int i=0;i<comp.length;i++)
            ((IEWidgetView)comp[i]).delete();
        removeAll();
        if (getParent()!=null)
            getParent().remove(this);
        _componentView = null;
    }

    /**
     * Overrides doLayout
     * @see java.awt.Container#doLayout()
     */
    @Override
    public void doLayout()
    {
        _componentView.notifyAllViewsToHoldTheirNextComputedPreferredSize(this);
        if (getParent() instanceof Layoutable) {
            ((Layoutable)getParent()).doLayout();
        }
        super.doLayout();
        _componentView.resetAllViewsPreferredSize(this);
    }

    /**
     * Overrides getPreferredSize
     *
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize()
    {
        if (getHoldsNextComputedPreferredSize() && preferredSize!=null) {
            return preferredSize;
        }
        Dimension d = super.getPreferredSize();
        if (d.height < 18)
            d.height = 18;
        if (getHoldsNextComputedPreferredSize()) {
            preferredSize = d;
        }
        return d;
    }

    /**
     * Overrides getMaximumSize
     * @see javax.swing.JComponent#getMaximumSize()
     */
    @Override
    public Dimension getMaximumSize()
    {
        return getSize();
    }

    /**
     * Overrides getBackground
     *
     * @see java.awt.Component#getBackground()
     */
    @Override
    public Color getBackground()
    {
        return super.getBackground();
    }

    @Override
	public void update(FlexoObservable observable, DataModification modif)
    {
        if (modif instanceof ButtonAdded) {
            IEWidgetView view = findViewForModel((IEWidget) modif.newValue());
            if (view == null) 
            	if(modif.newValue() instanceof IEHyperlinkWidget) {
            		// the view must be created
            		view = _componentView.getViewForWidget((IEWidget) modif.newValue(), true);
	            } else {
	                if (logger.isLoggable(Level.WARNING))
	                    logger.warning("cannot create a view from a model of class :" + modif.newValue().getClass()
	                            + " in a org.openflexo.ie.view.widget.ButtonPanel");
	                return;
	            }
            if (this.getComponentCount() == 0) {
                super.add(view);
            } else {
                Vector<Component> removedComponent = new Vector<Component>();
                int i = ((IEHyperlinkWidget) view.getModel()).getIndex();
                while (i < getComponentCount()) {
                    removedComponent.add(getComponent(i));
                    remove(i);
                }
                super.add(view);

                Enumeration en = removedComponent.elements();
                while (en.hasMoreElements()) {
                    super.add((Component) en.nextElement());
                }

            }
        } else if (modif instanceof WidgetRemovedFromSequence && observable==getButtonedWidgetModel().getSequenceWidget()) {
            IEWidgetView view = findViewForModel((IEWidget) modif.oldValue());
            if (view != null) {
                remove(view);
                validate();
                doLayout();
                repaint();
                if (getParent() != null) {
                    getParent().doLayout();
                    getParent().repaint();
                }
            }
        } else if (modif instanceof WidgetAddedToSequence && observable==getButtonedWidgetModel().getSequenceWidget()) {
            IEWidgetView view = _componentView.getViewForWidget((IEWidget) modif.newValue(), true);
            if (view != null) {
                add(view, ((WidgetAddedToSequence) modif).getIndex());
                validate();
                doLayout();
                repaint();
            }
        }
        /*
         * else if (modif instanceof ButtonRemoved) { //loc has been removed
         * IEWidgetView view = findViewForModel((IEWidget)modif.oldValue());
         * if(view!=null)remove(view);
         * if(view!=null)view.getModel().deleteObserver(view); updateUI(); }
         */

    }

    public IEWidgetView findViewForModel(IEWidget button)
    {
        for (int i = 0; i < getComponents().length; i++) {
            if (((IEWidgetView) getComponent(i)).getModel().equals(button)) {
                return (IEWidgetView) getComponent(i);
            }
        }
        return null;
    }

    public int findInsertionIndex(int dropX)
    {
        int i = 0;
        if (getComponentCount() > 0) {
            while (i < getComponentCount() && dropX > getComponent(i).getX()) {
                i++;
            }
        }
        return i;
    }

    public ButtonedWidgetInterface getButtonedWidgetModel()
    {
        return _model;
    }

    @Override
	public IEWidget getContainerModel()
    {
        return (IEWidget) getButtonedWidgetModel();
    }

    /**
     * Overrides propagateResize
     * @see org.openflexo.ie.view.Layoutable#propagateResize()
     */
    @Override
	public void propagateResize()
    {
        doLayout();
        Component[] c = getComponents();
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof Layoutable)
                ((Layoutable) c[i]).propagateResize();
        }
        repaint();

    }

    /**
     * Overrides getHoldsNextComputedPreferredSize
     * @see org.openflexo.ie.view.Layoutable#getHoldsNextComputedPreferredSize()
     */
    @Override
	public boolean getHoldsNextComputedPreferredSize()
    {
        return holdsNextComputedPreferredSize;
    }

    /**
     * Overrides resetPreferredSize
     * @see org.openflexo.ie.view.Layoutable#resetPreferredSize()
     */
    @Override
	public void resetPreferredSize()
    {
        holdsNextComputedPreferredSize = false;
        preferredSize=null;
    }

    /**
     * Overrides setHoldsNextComputedPreferredSize
     * @see org.openflexo.ie.view.Layoutable#setHoldsNextComputedPreferredSize()
     */
    @Override
	public void setHoldsNextComputedPreferredSize()
    {
        holdsNextComputedPreferredSize=true;
    }
	@Override
	public IEWOComponent getWOComponent() {
		return getButtonedWidgetModel().getWOComponent();
	}

}
