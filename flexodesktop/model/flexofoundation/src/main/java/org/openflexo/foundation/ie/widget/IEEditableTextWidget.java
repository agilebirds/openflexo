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

import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.RequiredBindingValidationRule;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.ie.HTMLListDescriptor;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.dm.IETextFieldCssClassChange;
import org.openflexo.foundation.ie.util.TextFieldClass;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Represents a dynamic component used to edit text
 * 
 * @author sguerin
 * 
 */
public abstract class IEEditableTextWidget extends IEControlWidget implements IEEditableFieldWidget, IEWidgetWithValueList,
		IEWidgetWithMainBinding {

	private String _value;

	private String _fieldLabel;

	private TextFieldClass _tfcssClass;

	private boolean _isMandatory;

	private BindingValue _bindingValue; // Could be considered as a BindingValue because defined as GET_SET

	public static final String BINDING_VALUE = "bindingValue";

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IEEditableTextWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
	}

	public boolean getIsMandatory() {
		return _isMandatory;
	}

	public void setIsMandatory(boolean value) {
		_isMandatory = value;
		setChanged();
		notifyObservers(new IEDataModification("isMandatory", null, new Boolean(value)));
	}

	public WidgetBindingDefinition getBindingValueDefinition() {
		return WidgetBindingDefinition.get(this, BINDING_VALUE, String.class, BindingDefinitionType.GET_SET, true);
	}

	public final BindingValue getBindingValue() {
		if (isBeingCloned()) {
			return null;
		}
		return _bindingValue;
	}

	public String getBindingValuePath() {
		return _bindingValue.getCodeStringRepresentation();
	}

	public final void setBindingValue(BindingValue bindingValue) {
		BindingValue oldBindingValue = _bindingValue;
		_bindingValue = bindingValue;
		if (_bindingValue != null) {
			_bindingValue.setOwner(this);
			_bindingValue.setBindingDefinition(getBindingValueDefinition());
		}
		setChanged();
		notifyObservers(new IEDataModification(BINDING_VALUE, oldBindingValue, bindingValue));
	}

	public String getFieldLabel() {
		return _fieldLabel;
	}

	public void setFieldLabel(String label) {
		_fieldLabel = label;
		setChanged();
		notifyObservers(new IEDataModification("fieldLabel", null, label));
	}

	public TextFieldClass getTfcssClass() {
		return _tfcssClass;
	}

	public void setTfcssClass(TextFieldClass css) {
		_tfcssClass = css;
		setChanged();
		notifyObservers(new IETextFieldCssClassChange(css));
	}

	public String getValue() {
		return _value;
	}

	public void setValue(String value) {
		if (value != null && value.trim().length() == 0) {
			value = null;
		}
		this._value = value;
		setChanged();
		notifyObservers(new IEDataModification("value", null, value));
	}

	/*public static class EditableFieldMustHaveABindingValue extends ValidationRule
	{
	    public EditableFieldMustHaveABindingValue()
	    {
	        super(IEEditableTextWidget.class, "editable_fields_must_have_a_value");
	    }

	    public ValidationIssue applyValidation(final Validable object)
	    {
	        final IEEditableTextWidget tf = (IEEditableTextWidget) object;
	        if (tf.getBindingValue() == null) {
	            ValidationError error = new ValidationError(this, object, "editable_fields_must_have_a_value");
	            Vector allAvailableBV = tf.getBindingValueDefinition().searchMatchingBindingValue(tf,2);
	            for (int i=0; i<allAvailableBV.size(); i++) {
	                BindingValue bv = (BindingValue) allAvailableBV.elementAt(i);
	                error.addToFixProposals(new SetBinding(bv));
	             }
	            return error;
	        }
	        return null;
	    }
	}

	public static class SetBinding extends FixProposal
	{
	    public BindingValue bindingValue;

	    public SetBinding(BindingValue aBindingValue)
	    {
	        super("set_binding_value_to_($bindingValue.stringRepresentation)");
	        bindingValue = aBindingValue;
	    }

	    protected void fixAction()
	    {
	        ((IEEditableTextWidget) getObject()).setBindingValue(bindingValue);
	    }
	}
	*/

	public abstract boolean isNumber();

	public abstract boolean isDate();

	public abstract boolean isText();

	public static class EditableFieldMustHaveABindingValue extends RequiredBindingValidationRule<IEEditableTextWidget> {
		public EditableFieldMustHaveABindingValue() {
			super(IEEditableTextWidget.class, "bindingValue", "bindingValueDefinition");
		}
	}

	/**
	 * Overrides getRawRowKeyPath
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEWidget#getRawRowKeyPath()
	 */
	@Override
	public String getRawRowKeyPath() {
		HTMLListDescriptor desc = getHTMLListDescriptor();
		if (desc == null) {
			return null;
		}
		String item = desc.getItemName();
		if (item == null) {
			return null;
		}
		if (getBindingValue() == null) {
			return null;
		}
		if (getBindingValue().getCodeStringRepresentation().indexOf(item) > -1) {
			return getBindingValue().getCodeStringRepresentation().substring(
					getBindingValue().getCodeStringRepresentation().indexOf(item) + item.length() + 1);
		} else {
			return null;
		}
	}

	/**
	 * @see org.openflexo.foundation.ie.widget.IEWidgetWithValueList#getValueList()
	 */
	@Override
	public List<Object> getValueList() {
		return getValueList(null);
	}

	/**
	 * @see org.openflexo.foundation.ie.widget.IEWidgetWithMainBinding#getMainBinding()
	 */
	@Override
	public AbstractBinding getMainBinding() {
		return getBindingValue();
	}
}
