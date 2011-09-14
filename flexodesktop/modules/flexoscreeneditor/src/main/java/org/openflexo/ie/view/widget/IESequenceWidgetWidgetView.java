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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingConstants;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.dm.DisplayNeedUpdate;
import org.openflexo.foundation.ie.dm.DisplayNeedsRefresh;
import org.openflexo.foundation.ie.dm.WidgetAddedToSequence;
import org.openflexo.foundation.ie.dm.WidgetRemovedFromSequence;
import org.openflexo.foundation.ie.dm.table.ColSpanDecrease;
import org.openflexo.foundation.ie.dm.table.ColSpanIncrease;
import org.openflexo.foundation.ie.dm.table.ConstraintUpdated;
import org.openflexo.foundation.ie.dm.table.RowSpanDecrease;
import org.openflexo.foundation.ie.dm.table.RowSpanIncrease;
import org.openflexo.foundation.ie.widget.IEHTMLTableConstraints;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.OperatorChanged;
import org.openflexo.ie.view.IEContainer;
import org.openflexo.ie.view.IEViewUtils;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.Layoutable;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.controller.dnd.IEDTListener;


public class IESequenceWidgetWidgetView extends IEWidgetView<IESequenceWidget> implements IEContainer
{

    private static final Logger logger = Logger.getLogger(IESequenceWidgetWidgetView.class.getPackage().getName());

    public static final int LAYOUT_GAP = 2;

    /**
     * @param model
     */
    public IESequenceWidgetWidgetView(IEController ieController, IESequenceWidget model, boolean addDnDSupport,
            IEWOComponentView componentView)
    {
        super(ieController, model, addDnDSupport, componentView);
        setOpaque(false);
        setLayout(new IETDFlowLayout(getAlignement(), getHorizontalGap(), getVerticalGap(), SwingConstants.TOP));
        Enumeration en = model.elements();
        IEWidget widget = null;
        while (en.hasMoreElements()) {
            widget = (IEWidget) en.nextElement();
            add(_componentView.getViewForWidget(widget, true));
        }
        if (logger.isLoggable(Level.FINE))
            logger.fine("Sequence Widget Widget View Bounds:" + getBounds());
        this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY, new IEDTListener(ieController, this, getModel())));
        if (getModel().getOperator() != null)
            setBorder(IEViewUtils.getBorderForOperator(getModel().getOperator()));
        validate();
    }

    public int getHorizontalGap() {
    	return LAYOUT_GAP;
    }
    
    public int getVerticalGap() {
    	return LAYOUT_GAP;
    }
    
    /**
     * @return the FlowLayout alignement for this view. Default value is
     *         FlowLayout.LEFT
     * @see FlowLayout
     */
    protected int getAlignement()
    {
        return FlowLayout.LEFT;
    }

    /**
     * Overrides propagateResize
     * 
     * @see org.openflexo.ie.view.widget.IEWidgetView#propagateResize()
     */
    @Override
    public void propagateResize()
    {
        Component[] c = getComponents();
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof Layoutable)
                ((Layoutable) c[i]).propagateResize();
        }
        invalidate();
        doLayout();
        repaint();
    }

    public int getAvailableWidth()
    {
        Dimension d = getMaximumSize();
        Insets i = getInsets();
        int retval = d.width - 2 * getHorizontalGap() - i.left - i.right;
        if (getModel().isSubsequence())
            retval -= 2 * getHorizontalGap() * getModel().getSequenceDepth();
        return retval;
    }
    
    /**
     * Overrides getMaximumSize
     * 
     * @see javax.swing.JComponent#getMaximumSize()
     */
    @Override
    public Dimension getMaximumSize()
    {
    	Container p = getParent();
        if (p instanceof IESequenceWidgetWidgetView) {
            Dimension d = ((IESequenceWidgetWidgetView) p).getMaximumSize();
            if (d.width == 0)
                return d;
            d.height -= 2 * getVerticalGap();
            d.width -= 2 * getHorizontalGap();
            return d;
        } else if (p instanceof IEWOComponentView) {
    		Dimension d = ((IEWOComponentView) p).getMaximumSize();
    		int subsequenceCount = getModel().getSequenceDepth();
    		d.width = ((IEWOComponentView) p).getMaxWidth()-(subsequenceCount*getHorizontalGap()*2);
    		return d;
        } else if (p instanceof ButtonPanel) {
            return p.getMaximumSize();
        } else
            return getSize();
    }

    @Override
	public Dimension getPreferredSize()
    {
    	if (getHoldsNextComputedPreferredSize()){
        	Dimension storedSize = storedPrefSize();
            if(storedSize!=null)
            	return storedSize;
        }
        Dimension d = super.getPreferredSize();
        if (d.height < IETDWidget.MIN_HEIGHT)
            d.height = IETDWidget.MIN_HEIGHT;
        if (d.width < IETDWidget.MIN_WIDTH)
            d.width = IETDWidget.MIN_WIDTH;
        if (getHoldsNextComputedPreferredSize())
            storePrefSize(d);
        return d;
    }

    public int getChildCount()
    {
        return getSequenceModel().length();
    }

    public IEHTMLTableConstraints getConstraints()
    {
        return getSequenceModel().td().getConstraints();
    }

    protected Component findView(IEWidget w)
    {
        for (int i = 0; i < getComponentCount(); i++) {
            if (((IEWidgetView) getComponent(i)).getModel().equals(w))
                return getComponent(i);
        }
        if (logger.isLoggable(Level.FINE))
            logger.fine("cannot find a view for widget : " + w);
        return null;
    }

    /**
     * Overrides setDefaultBorder
     * 
     * @see org.openflexo.ie.view.widget.IEWidgetView#setDefaultBorder()
     */
    @Override
    public void setDefaultBorder()
    {
        if (getModel().getOperator() != null)
            setBorder(IEViewUtils.getBorderForOperator(getModel().getOperator()));
        else
        	setBorder(EMPTY_BORDER_1);
    }

    protected void handleWidgetInserted(IEWidget widget) {
    	add(_componentView.getViewForWidget(widget, true), widget.getIndex());
        validate();
        doLayout();
        repaint();
    }
    
    protected void handleWidgetRemoved(IEWidget widget) {
        if (widget instanceof IEHTMLTableWidget)
            widget = ((IEHTMLTableWidget) widget).getSequenceTR();
        Component v = findView(widget);
        if (v != null) {
            remove(v);
            validate();
            doLayout();
            repaint();
        }

    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
	public void update(FlexoObservable arg0, DataModification modif)
    {
        if (modif instanceof DisplayNeedUpdate || modif instanceof ConstraintUpdated || modif instanceof ColSpanDecrease
                || modif instanceof RowSpanDecrease || modif instanceof ColSpanIncrease || modif instanceof RowSpanIncrease) {
            if (getParent() != null) {
                doLayout();
                repaint();
            }
        } else if (modif instanceof WidgetAddedToSequence && arg0 == getModel()) {
            handleWidgetInserted((IEWidget) modif.newValue());
        } else if (modif instanceof WidgetRemovedFromSequence && arg0 == getModel()) {
            handleWidgetRemoved((IEWidget) modif.oldValue());
        } else if (modif instanceof OperatorChanged && arg0 == getModel()) {
            if (getModel().getOperator() != null)
                setBorder(IEViewUtils.getBorderForOperator(getModel().getOperator()));
        } else if (modif instanceof DisplayNeedsRefresh && arg0 == getModel()) {
            revalidate();
            doLayout();
            repaint();
        } else
        	super.update(arg0, modif);
    }

    public IESequenceWidget getSequenceModel()
    {
        return getModel();
    }

    @Override
	public Color getBackground()
    {
    	if (getSequenceModel()!=null)
    		return getSequenceModel().getBackground();
    	else
    		return super.getBackground();
    }

    public int findInsertionIndex(int dropX, int dropY)
    {
        int i = 0;
        for (i = 0; i < getComponentCount(); i++) {
            if (dropX < getComponent(i).getX() + getComponent(i).getWidth() / 2
                    && dropY < getComponent(i).getY() + getComponent(i).getHeight())
                return i;
            else if (dropY < getComponent(i).getY())
                return i;
        }
        return i;
    }

    public int getContentLength()
    {
        int r = 0;
        for (int i = 0; i < getComponentCount(); i++) {
            r = r + getComponent(i).getPreferredSize().width;
        }
        return r;
    }

}
