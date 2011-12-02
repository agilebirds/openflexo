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

import java.util.Hashtable;

import org.apache.commons.lang.StringUtils;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.ie.HTMLListDescriptor;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.dm.CSSChanged;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.util.TDCSSType;
import org.openflexo.foundation.ie.util.TextCSSClass;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Represents an abstract component displaying text
 * 
 * @author sguerin
 * 
 */
public abstract class IENonEditableTextWidget extends AbstractInnerTableWidget implements IEWidgetWithMainBinding {

	private String _value;

	private AbstractBinding _bindingValue;

	private TextCSSClass _textCSSClass;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IENonEditableTextWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
	}

	public TextCSSClass getTextCSSClass() {
		return _textCSSClass;
	}

	public TDCSSType getTDCSSTypeDerivedFrowTextCSSSlass() {
		if (_textCSSClass == null) {
			return null;
		}
		if (_textCSSClass.equals(TextCSSClass.BLOC_BODY_CONTENT)) {
			return TDCSSType.DL_BLOCK_BODY_CONTENT;
		}
		if (_textCSSClass.equals(TextCSSClass.BLOC_BODY_TITLE)) {
			return TDCSSType.DL_BLOCK_BODY_TITLE;
		}
		if (_textCSSClass.equals(TextCSSClass.BLOC_BODY_EXTRA)) {
			return TDCSSType.DL_BLOCK_BODY_CONTENT;
		}
		if (_textCSSClass.equals(TextCSSClass.BLOC_BODY_COMMENT)) {
			return TDCSSType.DL_BLOCK_BODY_COMMENT;
		}
		return null;
	}

	public void setTextCSSClass(TextCSSClass aTextCSSClass) {
		_textCSSClass = aTextCSSClass;
		setChanged();
		notifyObservers(new CSSChanged("textCSSClass", aTextCSSClass));
	}

	public String getValue() {
		return _value;
	}

	public void setValue(String value) {
		String oldValue = getValue();
		if (value == null || value.equals("")) {
			value = getDefaultValue();
		}
		this._value = value;
		notifyModification("value", oldValue, value);
	}

	public WidgetBindingDefinition getBindingValueDefinition() {
		return WidgetBindingDefinition.get(this, "bindingValue", Object.class, BindingDefinitionType.GET, bindingValueIsMandatory());
	}

	private boolean bindingValueIsMandatory() {
		return this instanceof IEStringWidget;
	}

	public final AbstractBinding getBindingValue() {
		if (isBeingCloned()) {
			return null;
		}
		return _bindingValue;
	}

	public String getDefaultValue() {
		return null;
	}

	public final void setBindingValue(AbstractBinding value) {
		_bindingValue = value;
		if (_bindingValue != null) {
			_bindingValue.setOwner(this);
			_bindingValue.setBindingDefinition(getBindingValueDefinition());
		}
		setChanged();
		notifyObservers(new IEDataModification("bindingValue", null, value));
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

	@Override
	public boolean areComponentInstancesValid() {
		return true;
	}

	@Override
	public void removeInvalidComponentInstances() {
	}

	@Override
	protected Hashtable<String, String> getLocalizableProperties(Hashtable<String, String> props) {
		if (StringUtils.isNotEmpty(getValue()) && getBindingValue() == null) {
			props.put("value", getValue());
		}
		return super.getLocalizableProperties(props);
	}

	/**
	 * @see org.openflexo.foundation.ie.widget.IEWidgetWithMainBinding#getMainBinding()
	 */
	@Override
	public AbstractBinding getMainBinding() {
		return getBindingValue();
	}
}
