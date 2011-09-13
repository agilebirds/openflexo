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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.ie.HTMLListDescriptor;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoComponentBuilder;


/**
 * Represents a checkbox widget
 * 
 * @author bmangez
 */
public class IECheckBoxWidget extends IEControlWidget implements Serializable, IEWidgetWithValueList, IEWidgetWithMainBinding
{
    /**
     * 
     */
    public static final String CHECKBOX_WIDGET = "checkbox_widget";

    public static final String BINDING_ISCHECKED_NAME = "isChecked";

    public static final String ATTRIB_DESCRIPTION_NAME = "description";

    public static final String ATTRIB_DEFAULTVALUE_NAME = "value";

    protected boolean _value = false;

    protected BindingValue _bindingChecked; // Could be considered as a BindingValue because defined as GET_SET

    private boolean _submitForm = false;
    
    private String _behavior;

    private String _funcName;
    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    public IECheckBoxWidget(FlexoComponentBuilder builder)
    {
        this(builder.woComponent, null, builder.getProject());
        initializeDeserialization(builder);
    }

    public IECheckBoxWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj)
    {
        super(woComponent, parent, prj);
    }

    @Override
	public String getDefaultInspectorName()
    {
        return "CheckBox.inspector";
    }

    // ==========================================================================
    // ============================= XMLSerialize
    // ===============================
    // ==========================================================================

    // ==========================================================================
    // ============================= Variables
    // ==================================
    // ==========================================================================

    public WidgetBindingDefinition getBindingCheckedDefinition()
    {
        return WidgetBindingDefinition.get(this, "bindingChecked", Boolean.TYPE, BindingDefinitionType.GET_SET, true);
    }

    public BindingValue getBindingChecked()
    {
        if (isBeingCloned())
            return null;
        return _bindingChecked;
    }

    public void setBindingChecked(BindingValue bindingChecked)
    {
        _bindingChecked = bindingChecked;
        setChanged();
        if (_bindingChecked != null) {
            _bindingChecked.setOwner(this);
            _bindingChecked.setBindingDefinition(getBindingCheckedDefinition());
        }
        setChanged();
        notifyObservers(new IEDataModification("bindingChecked",null,_bindingChecked));
    }

    public boolean getValue()
    {
        return _value;
    }

    public void setValue(boolean value)
    {
        this._value = value;
        setChanged();
        notifyObservers(new DataModification(DataModification.ATTRIBUTE, ATTRIB_DEFAULTVALUE_NAME, null, null));
    }

    public boolean getSubmitForm()
    {
        return _submitForm;
    }

    public void setSubmitForm(boolean aBoolean)
    {
        _submitForm = aBoolean;
        setChanged();
        notifyObservers(new IEDataModification("submitForm",null,new Boolean(_submitForm)));
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
        return EMPTY_IOBJECT_VECTOR;
    }

    @Override
	public String getFullyQualifiedName()
    {
        return "CheckBox";
    }

    public String getBehavior()
    {
        return _behavior;
    }

    public void setBehavior(String behavior)
    {
        _behavior = behavior;
        setChanged();
        notifyObservers(new IEDataModification("behavior",null,_behavior));
    }

    public String getFuncName()
    {
        return _funcName;
    }

    public void setFuncName(String funcName)
    {
        _funcName = funcName;
        setChanged();
        notifyObservers(new IEDataModification("funcName",null,_funcName));
    }

    /**
     * Overrides getClassNameKey
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return CHECKBOX_WIDGET;
    }
    
    public String getOperatorCodeStringRepresentation(){
        //TODO... but is it needed ?
    	return null;
    }
    
    /**
     * Overrides getRawRowKeyPath
     * @see org.openflexo.foundation.ie.widget.IEWidget#getRawRowKeyPath()
     */
    @Override
    public String getRawRowKeyPath()
    {
        HTMLListDescriptor desc = getHTMLListDescriptor();
        if (desc==null)
            return null;
        String item = desc.getItemName();
        if (item==null)
            return null;
        if (getBindingChecked()==null)
            return null;
        if (getBindingChecked().getCodeStringRepresentation().indexOf(item)>-1){
            String reply = getBindingChecked().getCodeStringRepresentation().substring(getBindingChecked().getCodeStringRepresentation().indexOf(item)+item.length()+1);
            if(reply.endsWith("Boolean"))return reply.substring(0,reply.length()-7);
            return reply;
        }else
            return null;
    }
    
    public static class RadioButtonReloadOnChange extends ValidationRule<RadioButtonReloadOnChange, IECheckBoxWidget> {

		public RadioButtonReloadOnChange() {
			super(IECheckBoxWidget.class, "checkbox_reload_on_change");
		}

		@Override
		public ValidationIssue<RadioButtonReloadOnChange, IECheckBoxWidget> applyValidation(IECheckBoxWidget radio) {
			if (!radio.getSubmitForm())
				return new ValidationWarning<RadioButtonReloadOnChange, IECheckBoxWidget>(this,radio,"checkbox_reload_on_change",new SetReloadOnChange());
			return null;
		}

		@Override
		public boolean isValidForTarget(TargetType targetType) {
			return targetType==CodeType.PROTOTYPE;
		}
    	
    }
    
    public static class SetReloadOnChange extends FixProposal<RadioButtonReloadOnChange, IECheckBoxWidget> {

		public SetReloadOnChange() {
			super("set_checkbox_to_reload_on_value_change");
		}

		@Override
		protected void fixAction() {
			getObject().setSubmitForm(true);
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
		List<Object> result = new ArrayList<Object>();
		result.add(getValue());
		return result;
	}

	/**
	 * @see org.openflexo.foundation.ie.widget.IEWidgetWithMainBinding#getMainBinding()
	 */
	@Override
	public AbstractBinding getMainBinding()
	{
		return getBindingChecked();
	}
}
