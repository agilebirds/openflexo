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
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.border.Border;


import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.dm.AlignementChanged;
import org.openflexo.foundation.ie.dm.PourcentageChanged;
import org.openflexo.foundation.ie.dm.VerticalAlignementChanged;
import org.openflexo.foundation.ie.dm.table.ColSpanDecrease;
import org.openflexo.foundation.ie.dm.table.ColSpanIncrease;
import org.openflexo.foundation.ie.util.WidgetType;
import org.openflexo.foundation.ie.widget.IEHTMLTableConstraints;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IESpanTDWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.SequenceOfTDChanged;
import org.openflexo.ie.view.IEReusableWidgetComponentView;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.ToolBox;

/**
 * @author gpolet
 *
 */
public class IETDWidgetView extends IESequenceWidgetWidgetView
{
    private static final Logger logger = FlexoLogger.getLogger(IETDWidgetView.class.getPackage().getName());

    private Border defaultBorder = BorderFactory.createEtchedBorder();

    private IEHTMLTableWidget _htmlTable;

	private CursorSetterHTMLTable cursorSetter;

    /**
     * @param ieController
     * @param model
     * @param componentView
     */
    public IETDWidgetView(IEController ieController, IESequenceWidget model, IEWOComponentView componentView)
    {
        super(ieController, model, false, componentView);
        ((IETDFlowLayout)getLayout()).setVerticalAlignement(findVerticalAlignement(td()));
        if (td() != null) {
            td().addObserver(this);
            if (td().tr() != null)
                td().tr().addObserver(this);
        } else if (logger.isLoggable(Level.WARNING))
            logger.warning("Instanciated a new TDWidgetView but the sequence is not inside a TD");
        removeTransparentMouseListener();
        cursorSetter = new CursorSetterHTMLTable(this, model.htmlTable());
        updateCursorSetter();
		addMouseMotionListener(cursorSetter);
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
        			DropIEElement dropLabel = DropIEElement.actionType.makeNewAction(getModel(), null, getIEController().getEditor());
        			dropLabel.setElementType(WidgetType.LABEL);
        			dropLabel.setIndex(findInsertionIndex(e.getX(), e.getY()));
        			dropLabel.doAction();
        		}
        	}
        });
        setDefaultBorder();
        setOpaque(true);
    }

	/**
	 * @param cursorSetter
	 */
	private void updateCursorSetter() {
		if (td().getXLocation()==0)
        	cursorSetter.setIgnoreLeftSide(true);
        else
        	cursorSetter.setIgnoreLeftSide(false);
        if (td().isLastCell())
        	cursorSetter.setIgnoreRightSide(true);
        else
        	cursorSetter.setIgnoreRightSide(false);
	}

    private int findVerticalAlignement(IETDWidget td){
    	if(td.getVerticalAlignement()==null)return SwingConstants.CENTER;
    	if(td.getVerticalAlignement().equals(IETDWidget.VALIGN_MIDDLE))return SwingConstants.CENTER;
    	if(td.getVerticalAlignement().equals(IETDWidget.VALIGN_TOP))return SwingConstants.TOP;
    	if(td.getVerticalAlignement().equals(IETDWidget.VALIGN_BOTTOM))return SwingConstants.BOTTOM;
    	return SwingConstants.CENTER;
    }

    public static int alignement(String tdAlignement)
    {
        if (tdAlignement == null)
            return FlowLayout.LEFT;
        if (tdAlignement.equals(IETDWidget.ALIGN_CENTER))
            return FlowLayout.CENTER;
        if (tdAlignement.equals(IETDWidget.ALIGN_RIGHT))
            return FlowLayout.RIGHT;
        return FlowLayout.LEFT;
    }

    //TODO: Fix all kinds of problems with reusable components.
    public IEHTMLTableWidget getHTMLTable()
    {
    	if(_htmlTable!=null)
    		return _htmlTable;
    	_htmlTable = getSequenceModel().htmlTable();
        if(_htmlTable==null){
        	if(_componentView instanceof IEReusableWidgetComponentView){
        		//_htmlTable = ((ReusableWidgetComponentView)_componentView).getHTMLTable();

        	}
        }
        return _htmlTable;
    }

    public int getRowIndex()
    {
        return td().tr().getIndex();
    }

    public int getColIndex()
    {
        return td().getIndex();
    }

    public IETDWidget td()
    {
        return getModel().td();
    }

    @Override
	public void updateConstraints()
    {
        if (getSequenceModel().isInTD()) {
            IEHTMLTableLayout layout = null;
            IEWidgetView p = (IEWidgetView) getParent();
            if (p == null)
                return;
            while (layout == null) {
                if (p.getLayout() != null && p.getLayout() instanceof IEHTMLTableLayout) {
                    layout = (IEHTMLTableLayout) p.getLayout();
                } else {
                    p = (IEWidgetView) p.getParent();
                    if (p == null)
                        return;
                }
            }
            IEHTMLTableConstraints c = td().constraints;
            layout.setConstraints(this, c);
        }
    }

    @Override
	public void setDefaultBorder()
    {
    	if(getHTMLTable()==null){
    		System.out.println("break point here");
    		getHTMLTable();
    	}
        if (getHTMLTable() != null && getHTMLTable().isShowingBorder()) {
            setBorder(defaultBorder);
        }else{
            setBorder(null);
        }
    }

    public void adjustPourcentage(int increment, boolean startFromEnd)
    {
        IETDWidget prev = td().getPrevious();
        IETDWidget next = td().getNext();
        if (startFromEnd) {
            while (next instanceof IESpanTDWidget) {
                next = next.getNext();
            }
            if (next == null) {
                if (logger.isLoggable(Level.INFO))
                    logger.info("Could not find next td of td located at (" + td().getYLocation() + "," + td().getXLocation() + ")");
                return;
            }
        } else {
            if (prev instanceof IESpanTDWidget)
                prev = ((IESpanTDWidget) prev).getSpanner();
            if (prev == null) {
                if (logger.isLoggable(Level.INFO))
                    logger.info("Could not find prev td of td located at (" + td().getYLocation() + "," + td().getXLocation() + ")");
                return;
            }
        }
        IETDWidgetView leftView = (IETDWidgetView) (startFromEnd ? this : findViewForModel(prev));
        IETDWidgetView rightView = (IETDWidgetView) (startFromEnd ? findViewForModel(next) : this);
        if (rightView == null || leftView == null) {
            if (logger.isLoggable(Level.FINE))
                logger.fine("Could not find view for TD");
            return;
        }
        double deltaPourcentage = (increment + 0.0d) / (0.0d + getParent().getSize().width);
        double leftViewPourcentage = (leftView.getWidth() + 0.0d) / (0.0d + getParent().getSize().width);
        double rightViewPourcentage = (rightView.getWidth() + 0.0d) / (0.0d + getParent().getSize().width);
        if (leftViewPourcentage + deltaPourcentage < 0.01d)
            deltaPourcentage = 0.01d - leftViewPourcentage;
        else if (rightViewPourcentage - deltaPourcentage < 0.01d)
            deltaPourcentage = rightViewPourcentage - 0.01d;
        getHTMLTable()
                .adjustPourcentage(leftView.td(), deltaPourcentage, rightView.td() );
    }

    /**
     * Overrides update
     *
     * @see org.openflexo.ie.view.widget.IESequenceWidgetWidgetView#update(org.openflexo.foundation.FlexoObservable,
     *      org.openflexo.foundation.DataModification)
     */
    @Override
    public void update(FlexoObservable arg0, DataModification modif)
    {
    	if (modif instanceof AlignementChanged) {
            ((FlowLayout)getLayout()).setAlignment(alignement(getSequenceModel().getAlignement()));
            doLayout();
            repaint();
        } else if (modif instanceof VerticalAlignementChanged) {
        	((IETDFlowLayout)getLayout()).setVerticalAlignement(findVerticalAlignement(td()));
            doLayout();
            repaint();
        } else if (modif instanceof PourcentageChanged) {
            doLayout();
            repaint();
        } else if (modif.propertyName() != null && modif.propertyName().equals("rowIndex")) {
            setBackground(getBackground());
        } else if (modif instanceof SequenceOfTDChanged) {
            Component[] c = getComponents();
            for (int i = 0; i < c.length; i++) {
                Component component = c[i];
                remove(component);
            }
            validate();
            doLayout();
            repaint();
            switchToModel(((SequenceOfTDChanged)modif).getTD().getSequenceWidget());
        } else if (modif instanceof ColSpanIncrease || modif instanceof ColSpanDecrease) {
        	updateCursorSetter();
        }
        super.update(arg0, modif);

    }

    /**
     * Overrides getIEModel
     * @see org.openflexo.ie.view.widget.IEWidgetView#getIEModel()
     */
    @Override
    public IEWidget getIEModel()
    {
        return td();
    }

    /**
     * Overrides getAlignement
     *
     * @see org.openflexo.ie.view.widget.IESequenceWidgetWidgetView#getAlignement()
     */
    @Override
    protected int getAlignement()
    {
        return alignement(getModel().getAlignement());
    }

    /**
     * Overrides getInsets
     *
     * @see javax.swing.JComponent#getInsets()
     */
    @Override
    public Insets getInsets()
    {
        if (defaultBorder != null)
            return defaultBorder.getBorderInsets(this);
        else
            return super.getInsets();
    }

}