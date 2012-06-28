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

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.expr.EvaluationType;

public abstract class StaticBinding<T> extends AbstractBinding {

	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(StaticBinding.class.getPackage().getName());

	public StaticBinding() {
		super();
	}

	public StaticBinding(BindingDefinition bindingDefinition, Bindable owner) {
		super(bindingDefinition, owner);
	}

	public String getSerializationRepresentation() {
		return "$" + getStringRepresentation();
	}

	// ==========================================================
	// ================= Serialization stuff ====================
	// ==========================================================

	public static StaticBinding makeStaticBinding(String value, Bindable owner) {
		if (owner != null) {
			StaticBindingFactory factory = owner.getBindingFactory().getStaticBindingFactory();
			factory.setBindable(owner);
			StaticBinding returned = factory.convertFromString(value);
			returned.setOwner(owner);
			return returned;
		}
		return null;
	}

	public StaticBinding getStaticBindingFromString(String aValue) {
		return getConverter().convertFromString(aValue);
	}

	@Override
	public StaticBindingFactory getConverter() {
		if (getOwner() != null) {
			return getOwner().getBindingFactory().getStaticBindingFactory();
		}
		return null;
		// return staticBindingConverter;
	}

	public abstract EvaluationType getEvaluationType();

	public abstract T getValue();

	public abstract void setValue(T aValue);

	@Override
	public void setsWith(AbstractBinding aValue) {
		super.setsWith(aValue);
		if (aValue != null) {
			if (aValue instanceof StaticBinding) {
				Object value = ((StaticBinding) aValue).getValue();
				try {
					setValue((T) value);
				} catch (ClassCastException e) {
					logger.warning("setsWith() with mismatched types !!!");
				}
			} else {
				logger.warning("setsWith called with mismatched type " + aValue.getClass().getSimpleName() + ", expected StaticBinding");
			}

		}
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object instanceof StaticBinding) {
			StaticBinding sb = (StaticBinding) object;
			if (getBindingDefinition() == null) {
				if (sb.getBindingDefinition() != null) {
					return false;
				}
			} else {
				if (!getBindingDefinition().equals(sb.getBindingDefinition())) {
					return false;
				}
			}
			return ((_owner == sb._owner) && sb.getValue() != null && getValue().equals(sb.getValue()));
		} else {
			return super.equals(object);
		}
	}

	protected Type accessedType = null;

	@Override
	public Type getAccessedType() {
		if (accessedType == null) {
			accessedType = getStaticBindingClass();
		}
		return accessedType;
	}

	public abstract Class<T> getStaticBindingClass();

	@Override
	public boolean isBindingValid() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Is StaticBinding " + this + " valid ?");
		}

		if (getAccessedType() == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Invalid binding because accessed type is null");
			}
			return false;
		}

		if (getBindingDefinition() == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Invalid binding because binding definition is null");
			}
			return false;
		} else if (getBindingDefinition().getIsSettable()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Invalid binding because binding definition is declared as settable");
			}
			return false;
		} else if (getBindingDefinition().getBindingDefinitionType() == BindingDefinitionType.EXECUTE) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Invalid binding because binding definition is declared as executable");
			}
			return false;
		}

		if (getAccessedType().equals(Object.class)) {
			return true;
		}

		if (_areTypesMatching()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Valid binding");
			}
			return true;
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Invalid binding because types doesn't match: " + getAccessedType() + " cannot be assigned to "
					+ getBindingDefinition().getType());
		}
		return false;

	}

	@Override
	public String invalidBindingReason() {
		// logger.setLevel(Level.FINE);

		if (getAccessedType() == null) {
			return "Invalid binding " + this + " because accessed type is null";
		}

		if (getBindingDefinition() == null) {
			return "Invalid binding because binding definition is null";
		} else if (getBindingDefinition().getIsSettable()) {
			return "Invalid binding because binding definition is declared as settable for a constant value";
		} else if (getBindingDefinition().getBindingDefinitionType() == BindingDefinitionType.EXECUTE) {
			return "Invalid binding because binding definition is declared as executable for a constant value";
		}

		return "Invalid binding because types doesn't match: " + getAccessedType() + " cannot be assigned to "
				+ getBindingDefinition().getType();

	}

	@Override
	public boolean debugIsBindingValid() {
		logger.info("Is StaticBinding " + this + " valid ?");

		if (getAccessedType() == null) {
			logger.info("Invalid binding because accessed type is null");
			return false;
		}

		if (getBindingDefinition() == null) {
			logger.info("Invalid binding because binding definition is null");
			return false;
		} else if (getBindingDefinition().getIsSettable()) {
			logger.info("Invalid binding because binding definition is declared as settable");
			return false;
		} else if (getBindingDefinition().getBindingDefinitionType() == BindingDefinitionType.EXECUTE) {
			logger.info("Invalid binding because binding definition is declared as executable");
			return false;
		}

		if (getAccessedType().equals(Object.class)) {
			logger.info("Valid binding since accessed type is Object");
			return true;
		}

		if (_areTypesMatching()) {
			logger.info("Valid binding");
			return true;
		}

		logger.info("Invalid binding because types doesn't match: " + getAccessedType() + " cannot be assigned to "
				+ getBindingDefinition().getType());
		return false;

	}

	protected boolean _areTypesMatching() {
		return TypeUtils.isTypeAssignableFrom(getBindingDefinition().getType(), getAccessedType(), true);
	}

	@Override
	public boolean isStaticValue() {
		return true;
	}

	@Override
	public abstract StaticBinding<T> clone();

	@Override
	public T getBindingValue(BindingEvaluationContext context) {
		return getValue();
	}

	@Override
	public void setBindingValue(Object value, BindingEvaluationContext context) {
		logger.warning("Binding " + getStringRepresentation() + " is not settable");
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	/**
	 * Build and return a list of objects (the current object path computed from supplied context)
	 * 
	 * @param context
	 * @return
	 */
	@Override
	public List<Object> getConcernedObjects(BindingEvaluationContext context) {
		return null;
	}

	/**
	 * Build and return a list of target objects (the current object path computed from supplied context)
	 * 
	 * @param context
	 * @return
	 */
	@Override
	public List<TargetObject> getTargetObjects(BindingEvaluationContext context) {
		return null;
	}
}
