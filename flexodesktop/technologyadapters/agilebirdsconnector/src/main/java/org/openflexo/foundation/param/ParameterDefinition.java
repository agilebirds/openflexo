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
package org.openflexo.foundation.param;

import java.util.Hashtable;
import java.util.Vector;

import org.openflexo.foundation.KVCFlexoObject;
import org.openflexo.inspector.model.ParamModel;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.DenaliWidget;
import org.openflexo.inspector.widget.DenaliWidget.WidgetLayout;

/**
 * 
 * Abstract definition of a parameter of type T
 * 
 * @author sguerin
 */
public abstract class ParameterDefinition<T> extends KVCFlexoObject {

	private String _name;
	private String _label;
	private String _depends;
	private String _help;
	private String _conditional;

	private T _defaultValue;
	private T _value;

	private PropertyModel _propertyModel;

	protected ParameterDefinition(String name, String label, T defaultValue) {
		super();
		_name = name;
		_label = label;
		_defaultValue = defaultValue;
		_value = _defaultValue;
		parameters = new Hashtable<String, ParamModel>();
	}

	public String getConditional() {
		return _conditional;
	}

	public void setConditional(String conditional) {
		_conditional = conditional;
	}

	public String getDepends() {
		return _depends;
	}

	public void setDepends(String depends) {
		_depends = depends;
	}

	public String getHelp() {
		return _help;
	}

	public void setHelp(String help) {
		_help = help;
	}

	public String getLabel() {
		return _label;
	}

	public void setLabel(String label) {
		_label = label;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setExpandHorizontally(boolean expandHorizontally) {
		addParameter(DenaliWidget.EXPAND_HORIZONTALLY, "" + expandHorizontally);
	}

	public void setExpandVertically(boolean expandVertically) {
		addParameter(DenaliWidget.EXPAND_VERTICALLY, "" + expandVertically);
	}

	public void setDisplayLabel(boolean displayLabel) {
		addParameter(DenaliWidget.DISPLAY_LABEL, "" + displayLabel);
	}

	public void setWidgetLayout(WidgetLayout layout) {
		if (layout == WidgetLayout.LABEL_ABOVE_WIDGET_LAYOUT) {
			addParameter(DenaliWidget.WIDGET_LAYOUT, "1COL");
		} else if (layout == WidgetLayout.LABEL_NEXTTO_WIDGET_LAYOUT) {
			addParameter(DenaliWidget.WIDGET_LAYOUT, "2COL");
		}

	}

	public abstract String getWidgetName();

	public T getValue() {
		return _value;
	}

	public void setValue(T value) {
		T oldValue = _value;
		_value = value;
		notifyValueListeners(oldValue, value);
	}

	protected void notifyValueListeners(T oldValue, T newValue) {
		for (ValueListener<T> l : _valueListeners) {
			l.newValueWasSet(this, oldValue, newValue);
		}
	}

	private Vector<ValueListener<T>> _valueListeners = new Vector<ValueListener<T>>();

	public void addValueListener(ValueListener<T> l) {
		_valueListeners.add(l);
	}

	public void removeValueListener(ValueListener<T> l) {
		_valueListeners.remove(l);
	}

	public interface ValueListener<T> {
		public void newValueWasSet(ParameterDefinition<T> param, T oldValue, T newValue);
	}

	public Class getValueClass() {
		if (getValue() != null) {
			return getValue().getClass();
		}
		return null;
	}

	public boolean getBooleanValue() {
		return false;
	}

	public void setBooleanValue(boolean aBoolean) {
	}

	public int getIntegerValue() {
		return 0;
	}

	public void setIntegerValue(int anInteger) {
	}

	public PropertyModel getPropertyModel() {
		if (_propertyModel == null) {
			_propertyModel = new PropertyModel();
			_propertyModel.name = getName();
			_propertyModel.label = getLabel();
			_propertyModel.setWidget(getWidgetName());
			_propertyModel.depends = getDepends();
			_propertyModel.help = getHelp();
			_propertyModel.conditional = getConditional();
			_propertyModel.parameters = parameters;
			_propertyModel.setLocalizedLabel(_localizedLabel);
		}

		return _propertyModel;
	}

	protected Hashtable<String, ParamModel> parameters;

	public ParamModel addParameter(String aName, String aValue) {
		ParamModel param = new ParamModel();
		param.name = aName;
		param.value = aValue;
		parameters.put(aName, param);
		return param;
	}

	public void setFormatter(String formatterName) {
		addParameter("format", formatterName);
	}

	private String _localizedLabel = null;

	public String getLocalizedLabel() {
		return _localizedLabel;
	}

	public void setLocalizedLabel(String aString) {
		_localizedLabel = aString;
	}

}
