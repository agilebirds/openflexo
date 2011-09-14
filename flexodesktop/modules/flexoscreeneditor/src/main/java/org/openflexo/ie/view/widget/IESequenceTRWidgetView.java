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
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.DummyComponentInstance;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.dm.ActivateDisplayRefresh;
import org.openflexo.foundation.ie.dm.DisplayNeedUpdate;
import org.openflexo.foundation.ie.dm.HoldDisplayRefresh;
import org.openflexo.foundation.ie.dm.PourcentageChanged;
import org.openflexo.foundation.ie.dm.SubsequenceInserted;
import org.openflexo.foundation.ie.dm.TDInserted;
import org.openflexo.foundation.ie.dm.TDRemoved;
import org.openflexo.foundation.ie.dm.TRInserted;
import org.openflexo.foundation.ie.dm.WidgetAddedToSequence;
import org.openflexo.foundation.ie.dm.WidgetRemovedFromSequence;
import org.openflexo.foundation.ie.dm.table.DisplayBorderChanged;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IESequenceTR;
import org.openflexo.foundation.ie.widget.IESpanTDWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IETRWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.ITableRow;
import org.openflexo.foundation.ie.widget.ITableRowReusableWidget;
import org.openflexo.ie.view.IEContainer;
import org.openflexo.ie.view.IESelectable;
import org.openflexo.ie.view.IEViewUtils;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.Layoutable;
import org.openflexo.ie.view.controller.IEController;


public class IESequenceTRWidgetView extends IEWidgetView<IEWidget> implements FlexoObserver, IEContainer, IESelectable, ITableRowView
{

    private static final Logger logger = Logger.getLogger(IESequenceTRWidgetView.class.getPackage().getName());

    private boolean holdDoLayout = false;
    
    protected IESequenceTRWidgetView(IEController ieController, IETRWidget model, boolean addDndSupport, IEWOComponentView componentView){
    	super(ieController, model, addDndSupport, componentView);
    }
    
    public IESequenceTRWidgetView(IEController ieController, IESequenceTR model, boolean addDndSupport, IEWOComponentView componentView)
    {
        super(ieController, model, !model.isSubsequence(), componentView);
        setOpaque(false);
        IEHTMLTableWidget htmlTable = model.htmlTable();
        if(htmlTable==null)htmlTable = findHTMLTableForReusableRow(model,componentView);
        setLayout(new IEHTMLTableLayout(htmlTable, this));
        if (model.getParent() == htmlTable)
        	htmlTable.addObserver(this);
        ITableRow itemTR = null;
        Enumeration en = model.elements();
        while (en.hasMoreElements()) {
            itemTR = (ITableRow) en.nextElement();
            Object view = null;
            if (itemTR instanceof IETRWidget) {
                ((IETRWidget) itemTR).getSequenceTD().addObserver(this);
                Enumeration<IETDWidget> en1 = ((IETRWidget) itemTR).getAllTD().elements();
                while (en1.hasMoreElements()) {
                    IETDWidget td = en1.nextElement();
                    if (td instanceof IESpanTDWidget)
                        continue;
                    else {
                        IETDWidgetView tdView = (IETDWidgetView) _componentView.getViewForWidget(td, false);
                        add(tdView);
                        tdView.updateConstraints();
                    }
                }
            } else {
                view = _componentView.getViewForWidget((IEWidget)itemTR, false);
                if(view instanceof IESequenceTRWidgetView){
                	add((IESequenceTRWidgetView) view);
                }else{
                	add((IEReusableWidgetView) view);
                }
            }

        }
        validate();
    }

    private IEHTMLTableWidget findHTMLTableForReusableRow(IESequenceTR model, IEWOComponentView componentView){
    	//TODO: fix this method in the case of reusable widgets.
    	if (true)
    		return null;
    	//let's try to find the HTMLTable where the row is instantiate
    	if(componentView.getComponentInstance() instanceof DummyComponentInstance){
    		//we are displaying the row 'hors context'
    		return null;
    	}else{
    		// This code below seems completely "foireux"
    		ComponentInstance ci = componentView.getComponentInstance();
    		return ((IEWidget)ci.getOwner()).getParentTable();
    	}
    }
    
    /**
     * Overrides doLayout
     * @see org.openflexo.ie.view.widget.IEWidgetView#doLayout()
     */
    @Override
    public void doLayout()
    {
        if (holdDoLayout)
            return;
        super.doLayout();
    }
    
    public void add(IESequenceTRWidgetView sequenceView)
    {
    	IEHTMLTableWidget htmlTable = getModel().htmlTable();
    	if(htmlTable==null) htmlTable = findHTMLTableForReusableRow(getModel(),_componentView);
    	///sequenceView.setHTMLTableWidget(htmlTable);
    	add(sequenceView, (sequenceView.getModel()).constraints);
    	sequenceView.getLayout().layoutContainer(sequenceView);
        sequenceView.validate();
        sequenceView.repaint();
        sequenceView.getModel().addObserver(this);
    }
    
    /*public void setHTMLTableWidget(IEHTMLTableWidget htmlTable) {
		Enumeration<IETDWidgetView> en = getAllTDView().elements();
		while(en.hasMoreElements()){
			en.nextElement().setHTMLTableWidget(htmlTable);
		}
		
	}*/

	Vector<IETDWidgetView> getAllTDView() {
		Vector<IETDWidgetView> reply = new Vector<IETDWidgetView>();
		for(int i = 0; i<getComponentCount();i++){
			if(getComponent(i) instanceof IETDWidgetView)
				reply.add((IETDWidgetView)getComponent(i));
			else if(getComponent(i) instanceof IESequenceTRWidgetView){
				reply.addAll(((IESequenceTRWidgetView)getComponent(i)).getAllTDView());
			}else if(getComponent(i) instanceof IEReusableWidgetView){
				//TODO: implement this when reusable sequence tr are implemented
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Implement me!");
				//reply.addAll(((IEReusableWidgetView)getComponent(i)).getReusableWidgetComponentView().);
			}
		}
		return reply;
	}

	public void add(IEReusableWidgetView reusableView)
    {
    	IEHTMLTableWidget htmlTable = getModel().htmlTable();
    	if(htmlTable==null) htmlTable = findHTMLTableForReusableRow(getModel(),_componentView);
    	//reusableView.setHTMLTableWidget(htmlTable);
    	add(reusableView, ((ITableRowReusableWidget)reusableView.getModel()).constraints);
    	reusableView.getLayout().layoutContainer(reusableView);
    	reusableView.validate();
    	reusableView.repaint();
    	reusableView.getModel().addObserver(this);
    }
    
    private Vector<IESequenceWidgetWidgetView> getAllIESequenceWidgetWidgetView(Vector<IESequenceWidgetWidgetView> answer)
    {
        for (int i = 0; i < getComponentCount(); i++) {
            if (getComponent(i) instanceof IESequenceWidgetWidgetView) {
                answer.add((IESequenceWidgetWidgetView) getComponent(i));
            } else if (getComponent(i) instanceof IESequenceTRWidgetView) {
                ((IESequenceTRWidgetView) getComponent(i)).getAllIESequenceWidgetWidgetView(answer);
            } else if (getComponent(i) instanceof IEReusableWidgetView) {
				//TODO: implement this when reusable sequence tr are implemented
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Implement me!");
                //((IESequenceTRWidgetView)((IEReusableWidgetView) getComponent(i))._reusableWidgetComponentView.getRootWidgetView()).getAllIESequenceWidgetWidgetView(answer);
            }
        }
        return answer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
	public void update(FlexoObservable arg0, DataModification modif)
    {
        if (modif instanceof PourcentageChanged) {
            Vector<IESequenceWidgetWidgetView> v = new Vector<IESequenceWidgetWidgetView>();
            getAllIESequenceWidgetWidgetView(v);
            Enumeration<IESequenceWidgetWidgetView> en = v.elements();
            while (en.hasMoreElements()) {
                IESequenceWidgetWidgetView view = en.nextElement();
                view.updateConstraints();
                view.doLayout();
            }
            doLayout();
            updateConstraints();
            repaint();
        } else if (modif instanceof WidgetAddedToSequence && ((WidgetAddedToSequence) modif).newValue() instanceof ITableRowReusableWidget) {
        	ITableRowReusableWidget reusableRow = (ITableRowReusableWidget)((WidgetAddedToSequence) modif).newValue();
        	
        	add(((IEReusableWidgetView)_componentView.getViewForWidget(reusableRow, false)));
        	
        	validate();
            doLayout();
            repaint();
        	/*IETRWidget tr = reusableRow.getFirstTR();
        	tr.getSequenceTD().addObserver(this);
            Enumeration<IETDWidget> en1 = tr.getAllTD().elements();
            while (en1.hasMoreElements()) {
                IETDWidget td = (IETDWidget) en1.nextElement();
                if (td instanceof IESpanTDWidget)
                    continue;
                else {
                    IETDWidgetView tdView = (IETDWidgetView) _componentView.getViewForWidget(td, false);
                    add(tdView);
                    tdView.updateConstraints();
                }
            }
            validate();
            doLayout();
            repaint();*/

        } else if (modif instanceof TRInserted && ((TRInserted) modif).getTR().getSequenceTR() == getModel()) {
            ((TRInserted) modif).getTR().getSequenceTD().addObserver(this);
            Enumeration<IETDWidget> en1 = ((TRInserted) modif).getTR().getAllTD().elements();
            while (en1.hasMoreElements()) {
                IETDWidget td = en1.nextElement();
                if (td instanceof IESpanTDWidget)
                    continue;
                else {
                    IETDWidgetView tdView = (IETDWidgetView) _componentView.getViewForWidget(td, false);
                    add(tdView);
                    tdView.updateConstraints();
                }
            }
            validate();
            doLayout();
            repaint();

        } else if (modif instanceof WidgetRemovedFromSequence && arg0.equals(getModel())) {
        	Component viewToRemove = findViewForTR((ITableRow) ((WidgetRemovedFromSequence) modif).oldValue());
            if (viewToRemove != null) {
                remove(viewToRemove);
                validate();
                doLayout();
                repaint();
            }
        } else if (modif instanceof DisplayBorderChanged) {
            for (int i = 0; i < getComponentCount(); i++) {
                ((IESequenceWidgetWidgetView) getComponent(i)).setDefaultBorder();
            }
        } else if (modif instanceof SubsequenceInserted) {
            if (logger.isLoggable(Level.INFO))
                logger.info("SubsequenceInserted in sequence..." + this);
            IESequenceTR tr = (IESequenceTR) ((SubsequenceInserted) modif).getSequence();
            Enumeration<ITableRow> en = tr.elements();
            while (en.hasMoreElements()) {
                ITableRow widget = en.nextElement();
                if (widget instanceof IETRWidget)
                    ((IETRWidget) widget).getSequenceTD().deleteObserver(this);
            }
            IESequenceTRWidgetView newView = (IESequenceTRWidgetView) _componentView.getViewForWidget(tr, false);
            add(newView);
            validate();
            doLayout();
            repaint();
        } else if (modif instanceof DisplayNeedUpdate) {
            updateConstraints();
            doLayout();
            repaint();
        } else if (modif instanceof TDInserted) {
            IETDWidget td = (IETDWidget) ((TDInserted) modif).newValue();
            if (td instanceof IESpanTDWidget)
                return;
            IETDWidgetView tdView = (IETDWidgetView) _componentView.getViewForWidget(td, false);
            add(tdView);
            tdView.updateConstraints();
            validate();
            doLayout();
            repaint();
        } else if (modif instanceof TDRemoved) {
            IETDWidget td = (IETDWidget) ((TDRemoved) modif).oldValue();
            if (td instanceof IESpanTDWidget)
                return;
            IETDWidgetView tdView = (IETDWidgetView) _componentView.getViewForWidget(td, false);
            remove(tdView);
            validate();
            doLayout();
            repaint();
        } else if (modif instanceof HoldDisplayRefresh){
            holdDoLayout = true;
        } else if (modif instanceof ActivateDisplayRefresh){
            holdDoLayout = false;
        } else
            super.update(arg0, modif);
    }
    public void add(IETDWidgetView tdView){
    	super.add(tdView);
    	IEHTMLTableWidget htmlTable = getModel().htmlTable();
        if(htmlTable==null)htmlTable = findHTMLTableForReusableRow(getModel(),_componentView);
        //tdView.setHTMLTableWidget(htmlTable);
    }
    @Override
	public Dimension getPreferredSize()
    {
    	if (getHoldsNextComputedPreferredSize()){
        	Dimension storedSize = storedPrefSize();
            if(storedSize!=null)return storedSize;
        }
        Dimension d= super.getPreferredSize();
        if (getParent() instanceof IESequenceWidgetWidgetView) {
            d = new Dimension(((IESequenceWidgetWidgetView) getParent()).getAvailableWidth() - (2*IEHTMLTableLayout.BORDER_SIZE), d.height);
        }
        storePrefSize(d);
        return d;
    }

    /**
     * Overrides propagateResize
     * @see org.openflexo.ie.view.Layoutable#propagateResize()
     */
    @Override
	public void propagateResize()
    {
        Component[] c = getComponents();
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof Layoutable)
                ((Layoutable)c[i]).propagateResize();
        }
    }
    
    @Override
	public void updateConstraints()
    {
        if (getParent() instanceof IEWidgetView) {
            IEHTMLTableLayout layout = null;
            IEWidgetView p = (IEWidgetView) getParent();
            while (layout == null) {
                if (p.getLayout() != null && p.getLayout() instanceof IEHTMLTableLayout) {
                    layout = (IEHTMLTableLayout) p.getLayout();
                } else {
                    if (!(p.getParent() instanceof IEWidgetView))
                        return;
                    p = (IEWidgetView) p.getParent();
                }
            }
            layout.setConstraints(this, getSequenceTRModel().constraints);
        }
    }

    private Component findViewForTR(ITableRow tr)
    {
        return _componentView.findViewForModel((IEObject) tr);
    }

    public int getTotalWidth()
    {
        return getPreferredSize().width;
    }

    private Border sequence_border;

    @Override
	public Border getBorder()
    {
        if (getSequenceTRModel() == null || getSequenceTRModel().getOperator() == null) {
            return super.getBorder();
        }
        if (sequence_border == null) {
            sequence_border = IEViewUtils.getBorderForOperator(getSequenceTRModel().getOperator());
        }
        return new CompoundBorder(sequence_border, super.getBorder());
    }

    /**
     * Overrides getObject
     * @see org.openflexo.ie.view.widget.IEWidgetView#getObject()
     */
    @Override
    public FlexoModelObject getObject()
    {
        if (getModel().getParent() == getModel().htmlTable())
            return getModel().getParent();
        else
            return super.getObject();
    }
    
    /**
     * Overrides getModel
     * @see org.openflexo.ie.view.widget.IEWidgetView#getModel()
     */
    @Override
    public IESequenceTR getModel()
    {
        return (IESequenceTR) super.getModel();
    }
    
    @Override
	public int getIndex()
    {
        return getModel().getIndex();
    }

    public IESequenceTR getSequenceTRModel()
    {
        return getModel();
    }

}
