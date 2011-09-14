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
package org.openflexo.foundation.ie.widget;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.SingleWidgetComponent;
import org.openflexo.foundation.ie.dm.TDInserted;
import org.openflexo.foundation.ie.dm.TDRemoved;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.logging.FlexoLogger;

public class IESequenceTD extends IESequence<ITableData> implements ITableData
{

    private static final Logger logger = FlexoLogger.getLogger(IESequenceTD.class.getPackage().getName());

    public IESequenceTDConstraints constraints;

    public IESequenceTD(FlexoComponentBuilder builder)
    {
        this(builder.woComponent, null, builder.getProject());
        initializeDeserialization(builder);
    }

    public IESequenceTD(IEWOComponent woComponent, IEObject parent, FlexoProject prj)
    {
        super(woComponent, parent, prj);
        constraints = new IESequenceTDConstraints();
        constraints.gridy = getIndex();
        constraints.gridx = 0;
        constraints.gridwidth = getColSpan();
    }

    @Override
    public void addToInnerWidgets(ITableData w) {
    	if (isDeserializing() && w instanceof IESpanTDWidget) {
    		if (logger.isLoggable(Level.SEVERE))
    			logger.severe("Tried to deserialize an IESpanTDWidget: not adding it. A new one should automatically be created");
    		return;
    	}
    	super.addToInnerWidgets(w);
    }
    
    @Override
	public void removeFromInnerWidgets(ITableData td)
    {
        super.removeFromInnerWidgets(td);
        refreshIndexes();
        setChanged();
        if (td instanceof IETDWidget)
            notifyObservers(new TDRemoved((IETDWidget) td));
        if (!isDeserializing() && !isCreatedByCloning() && getParent() == tr()) {
            tr().notifyTDRemoved(td);
        }
    }

    @Override
	public void insertElementAt(ITableData td, int i)
    {
        super.insertElementAt(td, i);
        td.setParent(this);
        setChanged();
        if (td.getClass() == IETDWidget.class)
            notifyObservers(new TDInserted((IETDWidget) td));
        refreshIndexes();
        if (!isDeserializing() && !isCreatedByCloning() && getParent() == tr() && tr()!=null) {
            tr().notifyTDAdded(td);
        }
    }

    @Override
	public int getColSpan()
    {
        int reply = 0;
        Enumeration en = elements();
        while (en.hasMoreElements()) {
            reply = reply + ((ITableData) en.nextElement()).getColSpan();
        }
        return reply;
    }

    @Override
	public IEHTMLTableConstraints getConstraints()
    {
        return null;
    }

    @Override
	public IETRWidget tr()
    {
        if (getParent()==null)
            return null;
        if (getParent() instanceof IETRWidget)
            return (IETRWidget) getParent();
        return ((IESequenceTD) getParent()).tr();
    }

    @Override
	public IETDWidget getFirstTD()
    {
        Enumeration<ITableData> en = elements();
        while (en.hasMoreElements()) {
            ITableData td = en.nextElement();
            return td.getFirstTD();
        }
        return null;
    }
    
    @Override
	public void deleteCol()
    {
        if (htmlTable() != null)
            htmlTable().deleteCol(getFirstTD().getXLocation());
        return;
    }

    @Override
	public void deleteRow()
    {
        if (htmlTable() != null)
            htmlTable().deleteRow(tr());
        return;
    }

    public IEHTMLTableWidget htmlTable()
    {
        IEWidget o = (IEWidget) getParent();
        while (!(o instanceof IEHTMLTableWidget) && o != null) {
            if (o.getParent() instanceof SingleWidgetComponent)
                return null;
            o = (IEWidget) o.getParent();
        }
        if (o instanceof IEHTMLTableWidget)
            return (IEHTMLTableWidget) o;
        return null;
    }

    public class IESequenceTDConstraints extends IEHTMLTableConstraints
    {

        public IESequenceTDConstraints()
        {
            super();
            fill = BOTH;
            anchor = NORTHWEST;
            weightx = 1.0;
            weighty = 1.0;
        }

    }

    @Override
	public boolean isSubsequence()
    {
        return getParent() instanceof IESequenceTD;
    }

    @Override
	protected void refreshIndexes()
    {
        Enumeration en = elements();
        int i = 0;
        while (en.hasMoreElements()) {
            ((IEWidget) en.nextElement()).setIndex(i);
            i++;
        }
    }

    @Override
	public double getPourcentage()
    {
        if (!isSubsequence())
            return 1.0d;
        logger.warning("TODO pourcentage pour subsequences");
        return 0.25d;
    }

    /**
     * Overrides insertSpannedTD
     * 
     * @see org.openflexo.foundation.ie.widget.ITableData#insertSpannedTD()
     */
    @Override
	public void insertSpannedTD()
    {
        Enumeration<ITableData> en = elements();
        while (en.hasMoreElements()) {
            ITableData td = en.nextElement();
            td.insertSpannedTD();
        }
    }

    /**
     * 
     * @param col
     * @return
     */
    public IETDWidget getTDAtCol(int col)
    {
        IETDWidget retval = null;
        Enumeration<ITableData> en = elements();
        while (en.hasMoreElements()) {
            ITableData td = en.nextElement();
            if (td instanceof IESequenceTD) {
                retval = ((IESequenceTD) td).getTDAtCol(col);
                if (retval != null)
                    return retval;
            } else {
                if (((IETDWidget) td).getXLocation() == col)
                    return (IETDWidget) td;
            }
        }
        return null;
    }

    /**
     * @param col
     */
    public IETDWidget getCellAtIndex(int col)
    {
        if (col < 0)
            return null;
        Enumeration<ITableData> en = elements();
        while (en.hasMoreElements()) {
            ITableData td = en.nextElement();
            if (td instanceof IETDWidget) {
                if (((IETDWidget) td).getXLocation() == col)
                    return (IETDWidget) td;
            } else {
                IETDWidget retval = ((IESequenceTD) td).getCellAtIndex(col);
                if (retval != null)
                    return retval;
            }
        }
        return null;
    }

    /**
     * This methods returns all the TD's located in this sequence.
     * 
     * @return all the TD's located in this sequence (and its subsequence)
     *         ordered by the column (xLocation)
     */
    @Override
	public Vector<IETDWidget> getAllTD()
    {
        Vector<IETDWidget> v = new Vector<IETDWidget>();
        Enumeration<ITableData> en = elements();
        ITableData td = null;
        IESequenceTD std = null;
        while (en.hasMoreElements()) {
            td = en.nextElement();
            if (td instanceof IESequenceTD) {
                std = (IESequenceTD) td;
                v.addAll(std.getAllTD());
            } else if (td instanceof IETDWidget) {
                v.add((IETDWidget) td);
            }
        }
        return v;
    }

    /**
     * @param widget
     */
    public void replaceByNormalTD(IESpanTDWidget widget)
    {
        int index = indexOf(widget);
        removeFromInnerWidgets(widget);
        IETDWidget td = new IETDWidget(getWOComponent(), this, getProject());
        td.setXLocation(widget.getXLocation());
        insertElementAt(td, index);
    }

    /**
     * Overrides makeRealDelete
     * 
     * @see org.openflexo.foundation.ie.widget.ITableData#makeRealDelete(boolean)
     */
    @Override
	public void makeRealDelete(boolean b)
    {
        Enumeration<ITableData> en = elements();
        while (en.hasMoreElements()) {
            ITableData td = en.nextElement();
            td.makeRealDelete(b);
        }
        this.delete();
    }

    /**
     * Replace a normal TD by a SpanTD
     * 
     * @param tdToReplace -
     *            the TD to replace in this sequence
     * @param spanner -
     *            the TD that originated the span
     */
    public void replaceTDBySpanTD(IETDWidget tdToReplace, IETDWidget spanner)
    {
        int index = indexOf(tdToReplace);
        IESpanTDWidget span = new IESpanTDWidget(getWOComponent(), this, spanner, getProject());
        span.setXLocation(tdToReplace.getXLocation());
        removeFromInnerWidgets(tdToReplace);
        insertElementAt(span, index);
    }

    /**
     * @param incrementer
     */
    @Override
	public void setXLocation(Incrementer incrementer)
    {
        Enumeration<ITableData> en = elements();
        while (en.hasMoreElements()) {
            ITableData td = en.nextElement();
            td.setXLocation(incrementer);
        }
    }

    @Override
	public Vector<IETextFieldWidget> getAllDateTextfields()
    {
        Vector<IETextFieldWidget> v = new Vector<IETextFieldWidget>();
        Enumeration<ITableData> en = elements();
        while (en.hasMoreElements()) {
            ITableData element = en.nextElement();
            v.addAll(element.getAllDateTextfields());

        }
        return v;
    }

    /**
     * Overrides getInnerWidgets
     * 
     * @see org.openflexo.foundation.ie.widget.IESequence#getInnerWidgets()
     */
    @Override
    public Vector<ITableData> getInnerWidgets()
    {
        Vector<ITableData> orig = super.getInnerWidgets();
        if (isSerializing || isSerializing()|| isBeingCloned()) {
        	return getAllRealTD(orig);
        } else
            return orig;
    }

    public Vector<ITableData> getAllRealTD() {
    	return getAllRealTD(getInnerWidgets());
    }
    
	/**
	 * @param orig
	 * @return
	 */
	private Vector<ITableData> getAllRealTD(Vector<ITableData> orig) {
		Vector<ITableData> v = new Vector<ITableData>(orig);
		Enumeration<ITableData> en = orig.elements();
		while (en.hasMoreElements()) {
		    ITableData td = en.nextElement();
		    if (td instanceof IESpanTDWidget)
		        v.remove(td);
		}
		return v;
	}
	 
    @Override
    public void setInnerWidgets(Vector<ITableData> v) {
    	Iterator<ITableData> i = v.iterator();
    	while (i.hasNext()) {
			ITableData td = i.next();
			if (td instanceof IESpanTDWidget) {
				if (logger.isLoggable(Level.SEVERE))
					logger.severe("Trying to set a vector containing spanTD's!!!");
				i.remove();
			}
		}
    	super.setInnerWidgets(v);
    }
    
	 @Override
	public Vector<IESequenceTab> getAllTabContainers(){
	    	Vector<IESequenceTab> reply = new Vector<IESequenceTab>();
	    	Enumeration en = elements();
	    	while(en.hasMoreElements()){
	    		ITableData top = (ITableData)en.nextElement();
	    		reply.addAll(top.getAllTabContainers());
	    	}
	    	return reply;
	    }

    /**
     * @return
     */
    public Vector<IWidget> getAllInnerTableWidget()
    {
        Vector<IWidget> v = new Vector<IWidget>();
        Enumeration en = getAllNonSequenceWidget().elements();
        while (en.hasMoreElements()) {
            IETDWidget td= (IETDWidget) en.nextElement();
            v.addAll(td.getAllInnerTableWidget());
        }
        return v;
    }
}
