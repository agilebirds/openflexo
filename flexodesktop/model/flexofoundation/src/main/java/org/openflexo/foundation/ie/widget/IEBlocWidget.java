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

/**
 * Represents a bloc widget
 * 
 * @author bmangez
 */

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.HTMLListDescriptor;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IETopComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.action.TopComponentDown;
import org.openflexo.foundation.ie.action.TopComponentUp;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.dm.InnerBlocInserted;
import org.openflexo.foundation.ie.dm.InnerBlocRemoved;
import org.openflexo.foundation.ie.dm.TableSizeChanged;
import org.openflexo.foundation.ie.dm.table.AddEmptyRow;
import org.openflexo.foundation.ie.dm.table.RemoveEmptyRow;
import org.openflexo.foundation.ie.dm.table.WidgetAddedToTable;
import org.openflexo.foundation.ie.dm.table.WidgetRemovedFromTable;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;


public class IEBlocWidget extends AbstractButtonedWidget implements IETopComponent, ExtensibleWidget
{

    /**
     * 
     */
    public static final String BLOC_WIDGET = "bloc_widget";

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(IEBlocWidget.class.getPackage().getName());

    // ==========================================================================
    // ============================= Variables
    // ==================================
    // ==========================================================================

    public static final String BLOC_TITLE_ATTRIBUTE_NAME = "title";

    public static final String IE_BLOC_WIDGET_TAG_NAME = "IEBloc";

    private String _title;

    private int _colSpan = 1;

    private int _rowSpan = 1;

    private String _value;

    private boolean _hideTable = false;

    private InnerBlocWidgetInterface _innerBloc;

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    public IEBlocWidget(FlexoComponentBuilder builder)
    {
        this(builder.woComponent, null, builder.getProject());
        initializeDeserialization(builder);
    }

    public IEBlocWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj)
    {
        super(woComponent, parent, prj);
        _colSpan = 1;
        _rowSpan = 1;
    }


    public void setColSpan(int colSpan)
    {
        _colSpan = colSpan;
        setChanged();
        notifyObservers(new IEDataModification("description", null, new Integer(colSpan)));
    }

    public void setRowSpan(int rowSpan)
    {
        _rowSpan = rowSpan;
        setChanged();
        notifyObservers(new IEDataModification("rowSpan", null, new Integer(rowSpan)));
    }

    public int getColSpan()
    {
        return _colSpan;
    }

    public int getRowSpan()
    {
        return _rowSpan;
    }

    public boolean getHideTable()
    {
        return _hideTable;
    }

    public void setHideTable(boolean table)
    {
        _hideTable = table;
        setChanged();
        notifyObservers(new IEDataModification("hideTable", null, new Boolean(table)));
    }

    /**
     * @deprecated
     * @return
     */
    @Deprecated
	public String getValue()
    {
        return _value;
    }

    /**
     * @deprecated
     * @param _value
     */
    @Deprecated
	public void setValue(String _value)
    {
        this._value = _value;
        setChanged();
        notifyObservers(new IEDataModification("value", null, _value));
    }

    // ==========================================================================
    // ============================= Instance Methods
    // ===========================
    // ==========================================================================

    @Override
    public void performOnDeleteOperations() {
    	if (_innerBloc != null) {
            _innerBloc.delete();
        }
    	super.performOnDeleteOperations();
    }
    
    public void removeContent(InnerBlocWidgetInterface table)
    {
        if (table != null && table.equals(_innerBloc)) {
            DataModification dm = new InnerBlocRemoved(_innerBloc);
            _innerBloc = null;
            setChanged();
            notifyObservers(dm);
        }
    }

    public void insertContent(InnerBlocWidgetInterface table)
    {
        if (table != null) {
            setContent(table);
            setChanged();
            notifyObservers(new InnerBlocInserted(table));
        }
    }

    public void replaceWidgetByReusable(IEWidget oldWidget, InnerBlocReusableWidget newWidget)
    {
        removeContent((InnerBlocWidgetInterface) oldWidget);
        insertContent(newWidget);
    }

    // ==========================================================================
    // ============================= Accessors
    // ==================================
    // ==========================================================================

    @Override
    public boolean isTopComponent() {
    	return true;
    }
    
    public void setTitle(String title)
    {
        _title = title;
        setChanged();
        notifyObservers(new DataModification(DataModification.ATTRIBUTE, BLOC_TITLE_ATTRIBUTE_NAME, null, title));
    }

    @Override
	public String getTitle()
    {
        return _title;
    }

    public InnerBlocWidgetInterface getContent()
    {
        return _innerBloc;
    }

    public void setContent(InnerBlocWidgetInterface content)
    {
        _innerBloc = content;
        _innerBloc.setParent(this);
        setChanged();
    }

    @Override
	public String getDefaultInspectorName()
    {
        return "Bloc.inspector";
    }

    /**
     * Return a Vector of embedded IEObjects at this level. NOTE that this is
     * NOT a recursive method
     * 
     * @return a Vector of IEObject instances
     */
    @Override
	public Vector<IObject> getEmbeddedIEObjects()
    {
        Vector answer = new Vector();
        if (_innerBloc != null)
            answer.add(_innerBloc);
        answer.add(getButtonList());
        return answer;
    }

    public HTMLListDescriptor getContainedDescriptor() {
    	if (getContent()!=null)
    		return getContent().getHTMLListDescriptor();
    	return null;
    }
    
    @Override
	public String getFullyQualifiedName()
    {
        return "Bloc" + getTitle();
    }

    public void notifyListActionButtonStateChange(IEDataModification modif)
    {
        setChanged();
        notifyObservers(modif);
    }

    /**
     * Overrides getSpecificActionListForThatClass
     * 
     * @see org.openflexo.foundation.ie.widget.IEWidget#getSpecificActionListForThatClass()
     */
    @Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass()
    {
        Vector<FlexoActionType> v = super.getSpecificActionListForThatClass();
        v.add(TopComponentUp.actionType);
        v.add(TopComponentDown.actionType);
        v.add(DropIEElement.actionType);
        return v;
    }

    public void handleContentResize()
    {
        setChanged();
        notifyObservers(new ContentSizeChanged());
    }

    /**
     * Overrides update
     * 
     * @see org.openflexo.foundation.ie.IEObject#update(org.openflexo.foundation.FlexoObservable,
     *      org.openflexo.foundation.DataModification)
     */
    @Override
	public void update(FlexoObservable observable, DataModification obj)
    {
        if (observable == _innerBloc
                && (obj instanceof WidgetAddedToTable || obj instanceof WidgetRemovedFromTable || obj instanceof TableSizeChanged
                        || obj instanceof AddEmptyRow || obj instanceof RemoveEmptyRow)) {
            setChanged();
            notifyObservers(new ContentSizeChanged());
        } else
            super.update(observable, obj);
    }

    /**
     * Overrides notifyDisplayNeedsRefresh
     * @see org.openflexo.foundation.ie.widget.IEWidget#notifyDisplayNeedsRefresh()
     */
    @Override
    public void notifyDisplayNeedsRefresh()
    {
        super.notifyDisplayNeedsRefresh();
        if (_innerBloc !=null)
            _innerBloc.notifyDisplayNeedsRefresh();
    }
    
    /**
     * Overrides getClassNameKey
     * 
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return BLOC_WIDGET;
    }

    public Vector<IETextFieldWidget> getAllDateTextfields()
    {
        Vector<IETextFieldWidget> v = new Vector<IETextFieldWidget>();
        if (_innerBloc != null)
            v.addAll(_innerBloc.getAllDateTextfields());
        return v;
    }
    
    @Override
	public Vector<IESequenceTab> getAllTabContainers(){
    	Vector<IESequenceTab> reply = new Vector<IESequenceTab>();
    	if(_innerBloc==null)return reply;
    	if(_innerBloc instanceof IESequenceTab)reply.add((IESequenceTab)_innerBloc);
    	if(_innerBloc instanceof IEHTMLTableWidget)reply.addAll(((IEHTMLTableWidget)_innerBloc).getAllTabContainers());
    	return reply;
    }

    @Override
	public Vector<IEHyperlinkWidget> getAllButtonInterface() {
		Vector<IEHyperlinkWidget> v = new Vector<IEHyperlinkWidget>();
		if (_innerBloc!=null)
			v.addAll(_innerBloc.getAllButtonInterface());
		if(getButtonList()!=null)
			v.addAll(getButtonList().getAllButtonInterface());
		return v;
	}
    
    @Override
    public void setWOComponent(IEWOComponent woComponent) {
    	if(noWOChange(woComponent))return;
    	super.setWOComponent(woComponent);
    	if (getContent()!=null)
    		((IEWidget)getContent()).setWOComponent(woComponent);// This call is very important because it will update the WOComponent components cache
    	if(getButtonList()!=null)getButtonList().setWOComponent(woComponent);
    }

    @Override
	public boolean areComponentInstancesValid() {
    	if (getContent()!=null)
			return ((IEWidget)getContent()).areComponentInstancesValid();
    	else
    		return true;
}

	@Override
	public void removeInvalidComponentInstances() {
		super.removeInvalidComponentInstances();
		if (getContent()!=null)
			((IEWidget)getContent()).removeInvalidComponentInstances();
	}
	
	@Override
	protected Hashtable<String,String> getLocalizableProperties(Hashtable<String,String> props){
    	if(StringUtils.isNotEmpty(getTitle()))props.put("title",getTitle());
    	return super.getLocalizableProperties(props);
    }
}
