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

import java.awt.Color;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.action.DecreaseColSpan;
import org.openflexo.foundation.ie.action.DecreaseRowSpan;
import org.openflexo.foundation.ie.action.DeleteCol;
import org.openflexo.foundation.ie.action.DeleteRow;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.action.DropPartialComponent;
import org.openflexo.foundation.ie.action.IEDelete;
import org.openflexo.foundation.ie.action.IncreaseColSpan;
import org.openflexo.foundation.ie.action.IncreaseRowSpan;
import org.openflexo.foundation.ie.action.InsertColAfter;
import org.openflexo.foundation.ie.action.InsertColBefore;
import org.openflexo.foundation.ie.action.InsertRowAfter;
import org.openflexo.foundation.ie.action.InsertRowBefore;
import org.openflexo.foundation.ie.dm.DisplayNeedsRefresh;
import org.openflexo.foundation.ie.dm.WidgetRemovedFromSequence;
import org.openflexo.foundation.ie.util.TextFieldType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.logging.FlexoLogger;

public class IESequenceWidget extends IESequence<IEWidget> implements WidgetsContainer
{

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(IESequenceWidget.class.getPackage().getName());
	
    public IESequenceWidget(FlexoComponentBuilder builder)
    {
        this(builder.woComponent, null, builder.getProject());
        initializeDeserialization(builder);
    }

    public IESequenceWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj)
    {
        super(woComponent, parent, prj);
    }

    @Override
    public boolean isTopComponent() {
    	return getNonSequenceParent() instanceof IEWOComponent;
    }
    
    @Override
	public void addToInnerWidgets(IEWidget w)
    {
        if (w instanceof IESequenceOperator)
            setSequenceOperator((IESequenceOperator) w);
        else
            super.addToInnerWidgets(w);
    }

    @Override
	public void removeFromInnerWidgets(IEWidget w)
    {
        super.removeFromInnerWidgets(w);
        setChanged();
        notifyObservers(new WidgetRemovedFromSequence(w));
        if (getParent() instanceof IESequenceWidget && size() == 0) {
            this.delete();
        }
        if (getParent() instanceof IETDWidget) {
            ((IETDWidget)getParent()).notifyWidgetRemoved(w);
        }
    }

    public boolean isInTD()
    {
        return getParent() != null && getParent() instanceof IETDWidget;
    }

    public boolean isInButtonPanel() {
        return getNonSequenceParent() instanceof ButtonedWidgetInterface;
    }
    
    public IETDWidget td()
    {
        return getAncestorOfClass(IETDWidget.class);
    }

    public String getAlignement()
    {
        if (isInTD())
            return ((IETDWidget) getParent()).getAlignement();
        else {
            if (getParent() instanceof IESequenceWidget) {
                return ((IESequenceWidget) getParent()).getAlignement();
            } else {
                return null;
            }

        }
    }

    public Color getBackground()
    {
        if (isInTD()) {
            return getAncestorOfClass(IETDWidget.class).getBackground();
        } else if (isInButtonPanel()) {
        	return getProject().getCssSheet().getMainColor();
        } else {
            if (getParent() instanceof IESequenceWidget) {
                return ((IESequenceWidget) getParent()).getBackground();
            } else {
                return null;
            }
        }
    }

    public IEHTMLTableWidget htmlTable()
    {
        if (isInTD())
            return ((IETDWidget) getParent()).htmlTable();
        else {
            if (getParent() instanceof IESequenceWidget) {
                return ((IESequenceWidget) getParent()).htmlTable();
            } else {
                return null;
            }

        }
    }

    @Override
	public void insertElementAt(IEWidget o, int i)
    {
        super.insertElementAt(o, i);
        if (!isDeserializing() && !isCreatedByCloning() && getParent() instanceof IETDWidget) {
            ((IETDWidget)getParent()).notifyWidgetInserted(o);
            if (htmlTable()!=null)
                htmlTable().handleResize();
        }
    }

    @Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass()
    {
        Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
        returned.add(InsertColAfter.actionType);
        returned.add(InsertColBefore.actionType);
        returned.add(InsertRowAfter.actionType);
        returned.add(InsertRowBefore.actionType);
        returned.add(IncreaseColSpan.actionType);
        returned.add(DecreaseColSpan.actionType);
        returned.add(IncreaseRowSpan.actionType);
        returned.add(DecreaseRowSpan.actionType);
        returned.add(DeleteRow.actionType);
        returned.add(DeleteCol.actionType);
        returned.remove(IEDelete.actionType);
        returned.add(DropIEElement.actionType);
        returned.add(DropPartialComponent.actionType);
        return returned;
    }

    @Override
	public boolean isSubsequence()
    {
        return getParent() instanceof IESequenceWidget;
    }

    /*
     * public String getLabelForChild(IEWidget widget) { Object o =
     * getPrevious(widget); if (o instanceof IELabelWidget) return
     * ((IELabelWidget)o).getValue(); else if (o instanceof IEWidget) return
     * ((IEWidget)o).lookForLabelInTD(); else return null; }
     */

    public IEWidget findFirstWidgetOfClass(Class c)
    {
        Enumeration en = elements();
        IEWidget temp = null;
        while (en.hasMoreElements()) {
            temp = (IEWidget) en.nextElement();
            if (temp.getClass().equals(c))
                return temp;
        }
        return null;
    }

    @Override
	public void notifyDisplayNeedsRefresh()
    {
        Enumeration<IEWidget> en = elements();
        while (en.hasMoreElements()) {
            IEWidget w = en.nextElement();
            w.notifyDisplayNeedsRefresh();
        }
        setChanged();
        notifyObservers(new DisplayNeedsRefresh(this));
    }
    
	public Vector<IETextFieldWidget> getAllDateTextfields() {
		Vector<IETextFieldWidget> v = new Vector<IETextFieldWidget>();
        Enumeration<IEWidget> en = elements();
        while (en.hasMoreElements()) {
            IEWidget element = en.nextElement();
            if (element instanceof IETextFieldWidget && ((IETextFieldWidget) element).getFieldType() == TextFieldType.DATE) {
                v.add((IETextFieldWidget) element);
            } else if (element instanceof IESequenceWidget)
            	v.addAll(((IESequenceWidget)element).getAllDateTextfields());
            else if (element instanceof InnerBlocWidgetInterface)
            	v.addAll(((InnerBlocWidgetInterface)element).getAllDateTextfields());
            else if (element instanceof IEBlocWidget)
            	v.addAll(((IEBlocWidget)element).getAllDateTextfields());
        }
        return v;
	}
	
}
