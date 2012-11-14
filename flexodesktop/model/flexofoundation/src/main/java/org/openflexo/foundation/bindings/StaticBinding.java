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
package org.openflexo.foundation.bindings;

import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.expr.EvaluationType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dkv.Key;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.toolbox.Duration.DurationStringConverter;
import org.openflexo.xmlcode.StringEncoder.DateConverter;

public abstract class StaticBinding<T> extends AbstractBinding {

	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(StaticBinding.class.getPackage().getName());

	public StaticBinding() {
		super();
	}

	public StaticBinding(BindingDefinition bindingDefinition, FlexoModelObject owner) {
		super(bindingDefinition, owner);
	}

	@Override
	public String getCodeStringRepresentation() {
		return getStringRepresentation();
	}

	@Override
	public String getClassNameKey() {
		return "static_binding";
	}

	@Override
	public String getFullyQualifiedName() {
		return "STATIC_BINDING=" + getSerializationRepresentation();
	}

	public String getSerializationRepresentation() {
		return "$" + getStringRepresentation();
	}

	@Override
	protected void _applyNewBindingDefinition() {
		// TODO Auto-generated method stub
	}

	// ==========================================================
	// ================= Serialization stuff ====================
	// ==========================================================

	public static StaticBinding makeStaticBinding(String value, FlexoModelObject owner) {
		if (owner != null && owner.getProject() != null && owner instanceof Bindable) {
			StaticBindingStringConverter converter = owner.getProject().getStaticBindingConverter();
			StaticBinding returned = converter.convertFromString(value);
			returned.setOwner(owner);
			return returned;
		}
		return null;
	}

	public StaticBinding getStaticBindingFromString(String aValue) {
		return getConverter().convertFromString(aValue);
	}

	public static class StaticBindingStringConverter extends AbstractBindingStringConverter<StaticBinding> {

		private DateConverter dateConverter = new DateConverter();
		private DurationStringConverter durationConverter = new DurationStringConverter();
		private Bindable _bindable;

		public StaticBindingStringConverter() {
			super(StaticBinding.class);
		}

		public Bindable getBindable() {
			return _bindable;
		}

		@Override
		public void setBindable(Bindable bindable) {
			_bindable = bindable;
		}

		@Override
		public StaticBinding convertFromString(String aValue) {
			if (aValue.startsWith("$") && aValue.length() > 1) {
				return convertFromString(aValue.substring(1));
			}

			if (aValue.startsWith(DMType.DKV_PREFIX)) {
				if (_bindable != null) {
					StringTokenizer st = new StringTokenizer(aValue.substring(DMType.DKV_PREFIX.length()), ".");
					if (st.hasMoreTokens()) {
						String domainName = st.nextToken();
						Domain domain = ((FlexoModelObject) _bindable).getProject().getDKVModel().getDomainNamed(domainName);
						if (domain != null && st.hasMoreTokens()) {
							Key key = domain.getKeyNamed(st.nextToken());
							if (logger.isLoggable(Level.FINE)) {
								logger.fine("StaticBinding, found key: " + key);
							}
							if (key != null) {
								return new DKVBinding(null, null, key);
							}
						}
					}
				}
			}
			if (aValue.equalsIgnoreCase("true") || aValue.equalsIgnoreCase("yes")) {
				return new BooleanStaticBinding(true);
			} else if (aValue.equalsIgnoreCase("false") || aValue.equalsIgnoreCase("no")) {
				return new BooleanStaticBinding(false);
			} else if (aValue.startsWith("\"") && aValue.endsWith("\"") && aValue.length() > 1) {
				return new StringStaticBinding(aValue.substring(1, aValue.length() - 1));
			} else if (aValue.startsWith("'") && aValue.endsWith("'") && aValue.length() > 1) {
				return new StringStaticBinding(aValue.substring(1, aValue.length() - 1));
			} else if (aValue.indexOf(",") > -1) {
				try {
					return new DateStaticBinding(dateConverter.tryToConvertFromString(aValue));
				} catch (ParseException e3) {
					// Lets continue...
				}
			}
			try {
				return new DurationStaticBinding(durationConverter.tryToConvertFromString(aValue));
			} catch (ParseException e3) {
				// Lets continue...
			}
			try {
				return new IntegerStaticBinding(Long.parseLong(aValue));
			} catch (NumberFormatException e) {
				try {
					return new FloatStaticBinding(Double.parseDouble(aValue));
				} catch (NumberFormatException e2) {
				}
			}
			// Found nothing....
			return null;
		}

		@Override
		public String convertToString(StaticBinding value) {
			return value.getSerializationRepresentation();
		}

	}

	@Override
	public StaticBindingStringConverter getConverter() {
		if (getProject() != null) {
			return getProject().getStaticBindingConverter();
		}
		return null;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// TODO to be implemented
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
			return _owner == sb._owner && sb.getValue() != null && getValue().equals(sb.getValue());
		} else {
			return super.equals(object);
		}
	}

	protected DMType accessedType = null;

	@Override
	public DMType getAccessedType() {
		if (getOwner() != null && getOwner().getProject() != null && accessedType == null) {
			return DMType.makeResolvedDMType(getOwner().getProject().getDataModel().getDMEntity(getStaticBindingClass()));
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

		if (getProject() == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Project is null because owner is null for this binding value: " + getStringRepresentation()
						+ " cannot determine if binding is valid");
			}
			return true;
		}

		if (getAccessedType().isObject()) {
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

	protected boolean _areTypesMatching() {
		return getBindingDefinition().getType() == null || getBindingDefinition().getType().isAssignableFrom(getAccessedType(), true);
	}

	@Override
	public boolean isStaticValue() {
		return true;
	}

	@Override
	public abstract StaticBinding<T> clone();

	@Override
	public String getJavaCodeStringRepresentation() {
		return getStringRepresentation();
	}
}
