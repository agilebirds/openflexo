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
package org.openflexo.antar.binding;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

public abstract class AbstractBinding implements Bindable, Cloneable, StringConvertable<AbstractBinding>, Serializable {

	static final Logger logger = Logger.getLogger(BindingValue.class.getPackage().getName());

	/*public static BindingFactory abstractBindingConverter  = new BindingFactory();
	public static StaticBindingFactory staticBindingConverter = new StaticBindingFactory();
	public static BindingExpressionFactory bindingExpressionConverter = new BindingExpressionFactory();
	public static BindingValueFactory bindingValueConverter = new BindingValueFactory();*/

	protected Bindable _owner;
	private BindingDefinition _bindingDefinition;
	protected String unparsableValue = null;

	public AbstractBinding() {
		super();
	}

	public AbstractBinding(BindingDefinition bindingDefinition, Bindable owner) {
		this();
		_owner = owner;
		setBindingDefinition(bindingDefinition);
	}

	public abstract String getStringRepresentation();

	public final Bindable getOwner() {
		return _owner;
	}

	public void setOwner(Bindable owner) {
		_owner = owner;
	}

	@Override
	public final BindingModel getBindingModel() {
		if (_owner != null) {
			return _owner.getBindingModel();
		}
		return null;
	}

	@Override
	public BindingFactory getBindingFactory() {
		if (_owner != null) {
			return _owner.getBindingFactory();
		}
		return null;
	}

	@Override
	public final String toString() {
		return getStringRepresentation();
	}

	public BindingDefinition getBindingDefinition() {
		return _bindingDefinition;
	}

	public final void setBindingDefinition(BindingDefinition bindingDefinition) {
		_bindingDefinition = bindingDefinition;
	}

	// ==========================================================
	// ================= Serialization stuff ====================
	// ==========================================================

	public AbstractBinding getBindingFromString(String aValue) {
		return getConverter().convertFromString(aValue);
	}

	@Override
	public StringEncoder.Converter<? extends AbstractBinding> getConverter() {
		if (getOwner() != null) {
			return (Converter<? extends AbstractBinding>) getOwner().getBindingFactory();
		}
		return null;
		// return abstractBindingConverter;
	}

	public void setsWith(AbstractBinding aValue) {
		if (aValue != null) {
			_owner = aValue._owner;
			_bindingDefinition = aValue.getBindingDefinition();
		}

	}

	public abstract Object getBindingValue(BindingEvaluationContext context);

	public abstract void setBindingValue(Object value, BindingEvaluationContext context);

	public static interface BindingEvaluationContext {
		public Object getValue(BindingVariable variable);
	}

	public abstract Type getAccessedType();

	public abstract boolean isBindingValid();

	public abstract boolean debugIsBindingValid();

	public abstract boolean isStaticValue();

	@Override
	public abstract AbstractBinding clone();

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object instanceof AbstractBinding) {
			AbstractBinding bv = (AbstractBinding) object;
			if (getBindingDefinition() == null) {
				if (bv.getBindingDefinition() != null) {
					return false;
				}
			} else {
				if (!getBindingDefinition().equals(bv.getBindingDefinition())) {
					return false;
				}
			}
			return ((_owner == bv._owner) && (getStringRepresentation().equals(bv.getStringRepresentation())));
		} else {
			return super.equals(object);
		}
	}

	public String getUnparsableValue() {
		return unparsableValue;
	}

	public void setUnparsableValue(String unparsableString) {
		this.unparsableValue = unparsableString;
	}

	public abstract boolean isSettable();

	/**
	 * Build and return a list of objects (the current object path computed from supplied context)
	 * 
	 * @param context
	 * @return
	 */
	public abstract List<Object> getConcernedObjects(BindingEvaluationContext context);

	/**
	 * Build and return a list of target objects (the current object path computed from supplied context) Those objects are the one which
	 * are involved in the computation of this binding for the supplied context
	 * 
	 * @param context
	 * @return
	 */
	public abstract List<TargetObject> getTargetObjects(BindingEvaluationContext context);

	public static class TargetObject {
		public Object target;
		public String propertyName;

		public TargetObject(Object target, String propertyName) {
			super();
			this.target = target;
			this.propertyName = propertyName;
		}

		@Override
		public String toString() {
			return "TargetObject[" + target + "/" + propertyName + "]";
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TargetObject) {
				TargetObject t = (TargetObject) obj;
				return (target == t.target && propertyName != null && propertyName.equals(t.propertyName));
			}
			return super.equals(obj);
		}
	}

}
