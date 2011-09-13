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

import java.util.List;
import java.util.Vector;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.BindingVariable;
import org.openflexo.foundation.bindings.RequiredBindingValidationRule;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dkv.DomainDeleted;
import org.openflexo.foundation.dkv.dm.DKVDataModification;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DMTypeOwner;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.dm.ListOfValuesHasChanged;
import org.openflexo.foundation.ie.util.DropDownType;
import org.openflexo.foundation.ie.util.TextFieldType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.FlexoProcess;


/**
 * @author gpolet
 *
 */
public abstract class IEAbstractListWidget extends IEControlWidget implements IEEditableFieldWidget, DMTypeOwner, IEWidgetWithValueList, IEWidgetWithMainBinding
{

    // ==========================================================================
    // ============================= Variables
    // ==================================
    // ==========================================================================

    private String _value;

    private String _exampleList;

    private boolean _submitForm = false;

    private DropDownType _dropdownType;

    private boolean _hasNewOption = false;

    private boolean _showLanguagePopup = false;

    private BindingValue _bindingSelection; // Could be considered as a BindingValue because defined as GET_SET

    private BindingValue _bindingItem; // Could be considered as a BindingValue because defined as GET_SET

    private AbstractBinding _bindingDisplayString;

    private AbstractBinding _bindingNoSelectionString;

    private AbstractBinding _bindingList;

    private String _behavior;

    private Domain domain;

    private String domainName;

    private String _funcName;

    private DMType _contentType;

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    public IEAbstractListWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj)
    {
        super(woComponent, parent, prj);
    }

    @Override
	public String getDefaultInspectorName()
    {
        return "DropDown.inspector";
    }

    // ==========================================================================
    // ============================= XMLSerialize
    // ===============================
    // ==========================================================================

    /**
     * Overrides finalizeDeserialization
     *
     * @see org.openflexo.foundation.ie.widget.IEWidget#finalizeDeserialization(java.lang.Object)
     */
    @Override
	public void finalizeDeserialization(Object builder)
    {
        registerDomainObserving();
        super.finalizeDeserialization(builder);
    }

    @Override
    public void performOnDeleteOperations() {
    	unregisterDomainObserving();
    	super.performOnDeleteOperations();
    }

    /**
     *
     */
    private void registerDomainObserving()
    {
        if (getDomain() != null) {
            getDomain().addObserver(this);
            getDomain().getKeyList().addObserver(this);
            getDomain().getValueList().addObserver(this);
        }
    }

    /**
     *
     */
    private void unregisterDomainObserving()
    {
        if (getDomain() != null) {
            getDomain().deleteObserver(this);
            getDomain().getKeyList().deleteObserver(this);
            getDomain().getValueList().deleteObserver(this);
        }
    }

    /**
	 * @see org.openflexo.foundation.ie.widget.IEWidgetWithValueList#getValueList()
	 */
	@Override
	public List<Object> getValueList()
	{
		return getValueList(null);
	}

	/**
	 * @see org.openflexo.foundation.ie.widget.IEWidgetWithValueList#getValueList(org.openflexo.foundation.wkf.FlexoProcess)
	 */
	@Override
	public List<Object> getValueList(FlexoProcess process)
	{
		TextFieldType type = isDKV() ? TextFieldType.KEYVALUE : isStatusList() ? TextFieldType.STATUS_LIST : TextFieldType.TEXT;
		return parseValueListToAppropriateType(getValue(), getExampleList(), type, getDomain(), process);
	}

	/*
     * @see org.openflexo.foundation.ie.widget.IEControlWidget#setJavaAccessor(java.lang.String)
     * @deprecated used to ensure compatibility. javaaccessor is still defined
     *             in component_model_1.1
     */
    public void setJavaAccessor(String value)
    {
        // setBindingList(value);
    }

    public WidgetBindingDefinition getBindingDisplayStringDefinition()
    {
        return WidgetBindingDefinition.get(this, "bindingDisplayString", String.class, BindingDefinitionType.GET, false);
    }

    private BindingModel _bindingDisplayStringCustomBindingModel = null;

    public BindingModel getBindingDisplayStringCustomBindingModel()
    {
        if (_bindingDisplayStringCustomBindingModel == null) {
            _bindingDisplayStringCustomBindingModel = new BindingModel() {
                @Override
				public int getBindingVariablesCount()
                {
                    if ((getBindingItem() != null) && (getBindingItem().isBindingValid())) {
                        return 1;
                    }
                    return 0;
                }

                @Override
				public BindingVariable getBindingVariableAt(int index)
                {
                    if ((index == 0) && (getBindingItem() != null) && (getBindingItem().isBindingValid())) {
                        return getBindingItem().getBindingVariable();
                    }
                    return null;
                }

        		@Override
				public boolean allowsNewBindingVariableCreation()
        		{
        			return false;
        		}

};
        }
        return _bindingDisplayStringCustomBindingModel;
    }

    public AbstractBinding getBindingDisplayString()
    {
        return _bindingDisplayString;
    }

    public void setBindingDisplayString(AbstractBinding displayString)
    {
        _bindingDisplayString = displayString;
        if (_bindingDisplayString != null) {
            _bindingDisplayString.setOwner(this);
            _bindingDisplayString.setBindingDefinition(getBindingDisplayStringDefinition());
        }
        setChanged();
        notifyObservers(new IEDataModification("bindingDisplayString", null, _bindingDisplayString));
    }

	public DMType getContentType()
	{
		if (_contentType == null) {
			_contentType = DMType.makeWildcardDMType(null, null);
			_contentType.setOwner(this);
		}
		return _contentType;
	}

	public void setContentType(DMType contentType)
	{
		DMType oldContentType = _contentType;
		_contentType = contentType;
		if (_contentType!=null)
			_contentType.setOwner(this);
		 _bindingListDefinition = null;
		 _bindingItemDefinition = null;
		 setChanged();
		 notifyObservers(new IEDataModification("contentType",oldContentType,contentType));
	}

	private WidgetBindingDefinition _bindingListDefinition = null;
	private WidgetBindingDefinition _bindingItemDefinition = null;

    public WidgetBindingDefinition getBindingListDefinition()
    {
    	if (_bindingListDefinition == null) {
    		_bindingListDefinition = new WidgetBindingDefinition("bindingList",DMType.makeListDMType(getContentType(), getProject()),this,BindingDefinitionType.GET,true);
    		if(_bindingList!=null)
    			_bindingList.setBindingDefinition(_bindingListDefinition);
    	}
        return _bindingListDefinition;
    }

    public AbstractBinding getBindingList()
    {
        if (isBeingCloned())
            return null;
        return _bindingList;
    }

    public void setBindingList(AbstractBinding value)
    {
        _bindingList = value;
        if (_bindingList != null) {
            _bindingList.setOwner(this);
            _bindingList.setBindingDefinition(getBindingListDefinition());
        }
        setChanged();
        notifyObservers(new IEDataModification("bindingList", null, value));
    }

    public WidgetBindingDefinition getBindingItemDefinition()
    {
    	if (_bindingItemDefinition == null) {
    		_bindingItemDefinition = new WidgetBindingDefinition("bindingItem",getContentType(),this,BindingDefinitionType.GET_SET,true);
    		if (_bindingItem!=null)
    			_bindingItem.setBindingDefinition(_bindingItemDefinition);
    	}
        return _bindingItemDefinition;
    }

    public BindingValue getBindingItem()
    {
        if (isBeingCloned())
            return null;
        return _bindingItem;
    }

    public void setBindingItem(BindingValue bindingItem)
    {
        _bindingItem = bindingItem;
        if (_bindingItem != null) {
            _bindingItem.setOwner(this);
            _bindingItem.setBindingDefinition(getBindingItemDefinition());
        }
        setChanged();
        notifyObservers(new IEDataModification("bindingItem", null, bindingItem));
    }

    public WidgetBindingDefinition getBindingNoSelectionStringDefinition()
    {
        return WidgetBindingDefinition.get(this, "bindingNoSelectionString", String.class, BindingDefinitionType.GET, false);
    }

    public AbstractBinding getBindingNoSelectionString()
    {
        if (isBeingCloned())
            return null;
        return _bindingNoSelectionString;
    }

    public void setBindingNoSelectionString(AbstractBinding noSelectionString)
    {
        _bindingNoSelectionString = noSelectionString;
        if (_bindingNoSelectionString != null) {
            _bindingNoSelectionString.setOwner(this);
            _bindingNoSelectionString.setBindingDefinition(getBindingNoSelectionStringDefinition());
        }
        setChanged();
        notifyObservers(new IEDataModification("bindingNoSelectionString", null, noSelectionString));
    }

    public abstract WidgetBindingDefinition getBindingSelectionDefinition();

    public BindingValue getBindingSelection()
    {
        if (isBeingCloned())
            return null;
        return _bindingSelection;
    }

    public void setBindingSelection(BindingValue selection)
    {
        _bindingSelection = selection;
        if (_bindingSelection != null) {
            _bindingSelection.setOwner(this);
            _bindingSelection.setBindingDefinition(getBindingSelectionDefinition());
        }
        setChanged();
        notifyObservers(new IEDataModification("bindingSelection", null, selection));
    }

    public DropDownType getDropdownType()
    {
        return _dropdownType;
    }

    public void setDropdownType(DropDownType type)
    {
    	if(_dropdownType==type)
    		return;
    	if (isDKV())
    		setDomain(null);
        _dropdownType = type;
        setChanged();
        notifyObservers(new IEDataModification("dropdownType", null, type));
        setChanged();
        notifyObservers(new ListOfValuesHasChanged(new Object(), new Object()));
    }

    public String getExampleList()
    {
        return _exampleList;
    }

    public void setExampleList(String list)
    {
        _exampleList = list;
        setChanged();
        notifyObservers(new IEDataModification("exampleList", null, list));
    }

    public boolean getHasNewOption()
    {
        return _hasNewOption;
    }

    public void setHasNewOption(boolean newOption)
    {
        _hasNewOption = newOption;
        setChanged();
        notifyObservers(new IEDataModification("hasNewOption", null, new Boolean(newOption)));
    }

    public boolean getShowLanguagePopup()
    {
        return _showLanguagePopup;
    }

    public void setShowLanguagePopup(boolean languagePopup)
    {
        _showLanguagePopup = languagePopup;
        setChanged();
        notifyObservers(new IEDataModification("showLanguagePopup", null, new Boolean(languagePopup)));
    }

    public boolean getSubmitForm()
    {
        return _submitForm;
    }

    public void setSubmitForm(boolean aBoolean)
    {
        _submitForm = aBoolean;
        setChanged();
        notifyObservers(new IEDataModification("submitForm", null, new Boolean(_submitForm)));
    }

    public String getValue()
    {
        return _value;
    }

    public void setValue(String value)
    {
        this._value = value;
        setChanged();
        notifyObservers(new IEDataModification("value", null, value));
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
        return new Vector<IObject>();
    }

    @Override
	public String getFullyQualifiedName()
    {
        if (getLabel() != null && getLabel().trim().length() > 0)
            return getWOComponent().getName() + " - " + getLabel() + " (Dropdown)";
        String tmpLabel = getCalculatedLabel();
        if (tmpLabel != null)
            return getWOComponent().getName() + " - " + tmpLabel + " (Dropdown)";
        else
            return getWOComponent().getName() + " - Flexo ID: " + getFlexoID() + " (Dropdown)";
    }

    public String getBehavior()
    {
        return _behavior;
    }

    public void setBehavior(String behavior)
    {
        _behavior = behavior;
        setChanged();
        notifyObservers(new IEDataModification("behavior", null, behavior));
    }

    public String getFuncName()
    {
        return _funcName;
    }

    public void setFuncName(String funcName)
    {
        _funcName = funcName;
        setChanged();
        notifyObservers(new IEDataModification("funcName", null, funcName));
    }

    public static class DropdownMustDefineABindingSelection extends RequiredBindingValidationRule<IEAbstractListWidget>
    {
        public DropdownMustDefineABindingSelection()
        {
            super(IEAbstractListWidget.class, "bindingSelection", "bindingSelectionDefinition");
        }
    }

    public static class DropdownMustDefineABindingList extends RequiredBindingValidationRule<IEAbstractListWidget>
    {
        public DropdownMustDefineABindingList()
        {
            super(IEAbstractListWidget.class, "bindingList", "bindingListDefinition");
        }

        @Override
		public ValidationIssue<RequiredBindingValidationRule<IEAbstractListWidget>,IEAbstractListWidget> applyValidation(final IEAbstractListWidget object)
        {
            final IEAbstractListWidget dropDown = object;
            if(dropDown.getDropdownType()==null){
            	if(dropDown.getBindingSelection()!=null){
            		if(dropDown.getBindingSelection().getBindingPathLastElementType().isEOEntity()){
            			dropDown.setDropdownType(DropDownType.DBOBJECTS_LIST_TYPE);
            		}
            	}
            }
            if (dropDown.getDropdownType() != DropDownType.DOMAIN_KEY_VALUE && dropDown.getDropdownType() != DropDownType.DBOBJECTS_LIST_TYPE) {
                return super.applyValidation(object);
            }
            return null;
        }
    }

    public static class DropDownWithKeyValueMustDefineADomain extends ValidationRule<DropDownWithKeyValueMustDefineADomain,IEAbstractListWidget>
    {
        public DropDownWithKeyValueMustDefineADomain()
        {
            super(IEAbstractListWidget.class, "dropdown_with_key_value_must_define_a_domain");
        }

        @Override
		public ValidationIssue<DropDownWithKeyValueMustDefineADomain,IEAbstractListWidget> applyValidation(IEAbstractListWidget dropDown)
        {
            if ((dropDown.isDKV() && dropDown.getDomain()==null)) {
                ValidationError<DropDownWithKeyValueMustDefineADomain,IEAbstractListWidget> error = new ValidationError<DropDownWithKeyValueMustDefineADomain,IEAbstractListWidget>(this, dropDown, "dropdown_with_key_value_must_define_a_domain");
                return error;
            }
            return null;
        }

    }

    public Domain getDomain()
    {
        if (domain == null && domainName != null) {
            domain = getProject().getDKVModel().getDomainNamed(domainName);
            if (domain == null)
            	this.domainName = null;
        }
        return domain;
    }

    public void setDomain(Domain domain)
    {
        unregisterDomainObserving();
        Domain old = this.domain;
        this.domain = domain;
        registerDomainObserving();
        setChanged();
        notifyObservers(new IEDataModification("domain", old, domain));
    }

    public String getDomainName()
    {
        if (getDomain() != null)
            return getDomain().getName();
        else
            return null;
    }

    public void setDomainName(String domainName)
    {
        String old = this.domainName;
        this.domainName = domainName;
        domain = null;
        setChanged();
        notifyObservers(new IEDataModification("domainName", old, domainName));
    }

    public boolean isDKV()
    {
        return getDropdownType() == DropDownType.DOMAIN_KEY_VALUE;
    }

    public boolean isStatusList()
    {
        return getDropdownType() == DropDownType.STATUS_LIST_TYPE;
    }

    public boolean isDBList()
    {
        return getDropdownType() == DropDownType.DBOBJECTS_LIST_TYPE;
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
        if (obj instanceof DomainDeleted && getDomain() == ((DomainDeleted) obj).oldValue()) {
            setDomain(null);
        } else if (obj instanceof DKVDataModification) {
            setChanged();
            notifyObservers(new ListOfValuesHasChanged(new Object(), new Object()));
        } else
            super.update(observable, obj);
    }

	/**
	 * @see org.openflexo.foundation.ie.widget.IEWidgetWithMainBinding#getMainBinding()
	 */
	@Override
	public AbstractBinding getMainBinding()
	{
		return getBindingSelection();
	}
    
/*
    public String getBindingValueCodeStringRepresentation()
    {
        String selection = null;
        if (getBindingSelection() != null && getProject().getTargetType() != CodeType.PROTOTYPE) {
            selection = getBindingSelection().getCodeStringRepresentation();
        }
        if (selection == null || selection.trim().equals(""))
            selection = "selectionOf_" + getBindingListCodeStringRepresentation();
        return selection;
    }

    public String getBindingValueWodStringRepresentation()
    {
        String selection = null;
        if (getBindingSelection() != null && getProject().getTargetType() != CodeType.PROTOTYPE) {
            selection = getBindingSelection().getCodeStringRepresentation();
        }
        if (selection == null || selection.trim().equals(""))
            selection = "selectionOf_" + getBindingListCodeStringRepresentation();

        return ToolBox.replaceStringByStringInString("()", "", selection);
    }

    public String getBindingListCodeStringRepresentation()
    {
        String bindingList = null;
        if (getBindingList() != null && getProject().getTargetType() != CodeType.PROTOTYPE) {
            bindingList = getBindingList().getCodeStringRepresentation();
        }
        if (bindingList == null || bindingList.equals("")) {
            bindingList = "dropDown_" + getFlexoID();
        }
        return bindingList;
    }

    public String getBindingListWodStringRepresentation()
    {
        String bindingList = null;
        if (getBindingList() != null && getProject().getTargetType() != CodeType.PROTOTYPE) {
            bindingList = getBindingList().getCodeStringRepresentation();
        }
        if (bindingList == null || bindingList.equals("")) {
            bindingList = "dropDown_" + getFlexoID();
        }
        return ToolBox.replaceStringByStringInString("()", "", bindingList);
    }

    */

}
