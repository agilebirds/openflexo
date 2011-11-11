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

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.expr.DefaultExpressionParser;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.Function;
import org.openflexo.antar.expr.parser.ExpressionParser;
import org.openflexo.antar.expr.parser.ParseException;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMTranstyper;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DMTranstyper.DMTranstyperEntry;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.xmlcode.XMLMapping;

public class TranstypedBinding extends AbstractBinding {

	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(TranstypedBinding.class.getPackage().getName());

	private DMTranstyper transtyper;
	private Vector<TranstypedBindingValue> values;

	public class TranstypedBindingValue extends FlexoModelObject implements InspectableObject {
		protected DMTranstyperEntry entry;
		private BindingDefinition bindingDefinition;
		private AbstractBinding bindingValue;

		protected TranstypedBindingValue(DMTranstyperEntry anEntry) {
			super();
			entry = anEntry;
			bindingValue = null;
			bindingDefinition = new BindingDefinition(entry.getName(), entry.getType(), TranstypedBinding.this, BindingDefinitionType.GET,
					true) {
				@Override
				public String getVariableName() {
					return entry.getName();
				}

				@Override
				public DMType getType() {
					return entry.getType();
				}
			};
		}

		public BindingDefinition getBindingDefinition() {
			return bindingDefinition;
		}

		public AbstractBinding getBindingValue() {
			return bindingValue;
		}

		public void setBindingValue(AbstractBinding aValue) {
			bindingValue = aValue;
			if (bindingValue != null) {
				bindingValue.setBindingDefinition(getBindingDefinition());
				bindingValue.setOwner(TranstypedBinding.this);
			}
		}

		public DMTranstyperEntry getEntry() {
			return entry;
		}

		@Override
		public String getInspectorName() {
			// never inspected alone
			return null;
		}

		@Override
		public FlexoProject getProject() {
			return TranstypedBinding.this.getProject();
		}

		@Override
		public String getFullyQualifiedName() {
			return TranstypedBinding.this.getFullyQualifiedName() + "." + (getEntry() != null ? entry.getName() : null);
		}

		@Override
		public XMLMapping getXMLMapping() {
			return TranstypedBinding.this.getXMLMapping();
		}

		@Override
		public XMLStorageResourceData getXMLResourceData() {
			return TranstypedBinding.this.getProject();
		}

		@Override
		public String getClassNameKey() {
			return "transtyped_binding_value";
		}

		@Override
		public boolean equals(Object object) {
			if (object == null)
				return false;
			if (object instanceof TranstypedBindingValue) {
				TranstypedBindingValue o = (TranstypedBindingValue) object;
				return ((o.getEntry() == getEntry()) && (o.getBindingValue() == getBindingValue()));
			} else {
				return false;
			}
		}

	}

	public TranstypedBinding() {
		super();
		values = new Vector<TranstypedBindingValue>();
	}

	public TranstypedBinding(BindingDefinition bindingDefinition, FlexoModelObject owner) {
		super(bindingDefinition, owner);
		values = new Vector<TranstypedBindingValue>();
	}

	public DMTranstyper getTranstyper() {
		return transtyper;
	}

	public void setTranstyper(DMTranstyper aTranstyper) {
		if (aTranstyper != transtyper) {
			transtyper = aTranstyper;
			values.clear();
			if (transtyper != null) {
				for (DMTranstyperEntry entry : transtyper.getEntries()) {
					values.add(new TranstypedBindingValue(entry));
				}
			}
		}
	}

	public Vector<TranstypedBindingValue> getValues() {
		return values;
	}

	@Override
	public String getCodeStringRepresentation() {
		return getStringRepresentation();
	}

	@Override
	public String getClassNameKey() {
		return "transtyped_binding";
	}

	@Override
	public String getFullyQualifiedName() {
		return "TRANSTYPED_BINDING=" + getStringRepresentation();
	}

	@Override
	protected void _applyNewBindingDefinition() {
		// TODO Auto-generated method stub
	}

	// ==========================================================
	// ================= Serialization stuff ====================
	// ==========================================================

	public static TranstypedBinding makeTranstypedBinding(String value, FlexoModelObject owner) {
		if (owner != null && owner.getProject() != null && owner instanceof Bindable) {
			TranstypedBindingStringConverter converter = owner.getProject().getTranstypedBindingStringConverter();
			TranstypedBinding returned = converter.convertFromString(value);
			returned.setOwner(owner);
			return returned;
		}
		return null;
	}

	public TranstypedBinding getStaticBindingFromString(String aValue) {
		return getConverter().convertFromString(aValue);
	}

	public static class TranstypedBindingStringConverter extends AbstractBindingStringConverter<TranstypedBinding> {

		private Bindable _bindable;

		private AbstractBindingStringConverter _abstractBindingStringConverter;

		public TranstypedBindingStringConverter(AbstractBindingStringConverter abstractBindingStringConverter) {
			super(TranstypedBinding.class);
			_abstractBindingStringConverter = abstractBindingStringConverter;
		}

		public Bindable getBindable() {
			return _bindable;
		}

		@Override
		public void setBindable(Bindable bindable) {
			_bindable = bindable;
		}

		private MethodCall tryToDecodeAsMethodCall(BindingValue owner, DMType currentType, String aValue) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("tryToDecodeAsMethodCall " + aValue);

			String methodName;
			Vector<String> paramsAsString;

			try {
				ExpressionParser parser = new DefaultExpressionParser();
				Expression parsedExpression = parser.parse(aValue);
				if (parsedExpression instanceof Function) {
					methodName = ((Function) parsedExpression).getName();
					paramsAsString = new Vector<String>();
					for (Expression e : ((Function) parsedExpression).getArgs()) {
						paramsAsString.add(e.toString());
					}
				} else {
					if (logger.isLoggable(Level.WARNING) && warnOnFailure)
						logger.warning("Could not decode BindingValue : trying to find method call matching '" + aValue
								+ " this is not a function call");
					return null;
				}
			} catch (ParseException e) {
				if (logger.isLoggable(Level.WARNING) && warnOnFailure)
					logger.warning("Could not decode BindingValue : parse error while trying to find method call matching '" + aValue);
				return null;
			}

			Vector<DMMethod> allMethods = currentType.getBaseEntity().getAccessibleMethods();

			if (logger.isLoggable(Level.FINE))
				logger.fine("allMethods=" + allMethods);
			if (logger.isLoggable(Level.FINE))
				logger.fine("paramsAsString=" + paramsAsString);
			Vector<DMMethod> possiblyMatchingMethods = new Vector<DMMethod>();
			for (DMMethod method : allMethods) {
				if ((method.getName().equals(methodName)) && (method.getParameters().size() == paramsAsString.size()))
					possiblyMatchingMethods.add(method);
			}
			if (logger.isLoggable(Level.FINE))
				logger.fine("possiblyMatchingMethods=" + possiblyMatchingMethods);
			Vector<MethodCall> results = new Vector<MethodCall>();
			for (DMMethod method : possiblyMatchingMethods) {
				boolean successfull = true;
				MethodCall methodCall = new MethodCall(owner, method);
				for (int i = 0; i < method.getParameters().size(); i++) {
					DMMethod.DMMethodParameter param = method.getParameters().elementAt(i);
					String bindingAsString = paramsAsString.elementAt(i);
					DMType type = param.getType();
					if (logger.isLoggable(Level.FINE))
						logger.fine("Attempt to parse: " + bindingAsString);
					AbstractBinding paramBindingValue = _abstractBindingStringConverter.convertFromString(bindingAsString);
					if ((paramBindingValue != null)) {
						paramBindingValue.setOwner((FlexoModelObject) _bindable);
						if (logger.isLoggable(Level.FINE))
							logger.fine("paramBindingValue=" + paramBindingValue + " of " + paramBindingValue.getAccessedType());
						if (paramBindingValue.isStaticValue()) {
							paramBindingValue.setBindingDefinition(methodCall.new MethodCallParamBindingDefinition(param));
						}
						if (type != null && paramBindingValue.getAccessedType() != null
								&& type.isAssignableFrom(paramBindingValue.getAccessedType(), true)) {
							if (logger.isLoggable(Level.FINE))
								logger.fine("Lookup on " + type.getName() + " succeded: " + paramBindingValue.getStringRepresentation());
							methodCall.setBindingValueForParam(paramBindingValue, param);
						} else {
							if (logger.isLoggable(Level.FINE))
								logger.fine("Lookup on type " + type + " failed (wrong type): "
										+ paramBindingValue.getStringRepresentation() + "types: " + "looking " + type + " found "
										+ paramBindingValue.getAccessedType());
							successfull = false;
						}
					} else {
						if (logger.isLoggable(Level.FINE))
							logger.fine("Lookup on " + type.getName() + " failed (cannot analysing): " + bindingAsString);
						successfull = false;
					}
				}
				if (successfull) {
					results.add(methodCall);
				}
			}
			if (results.size() == 1) {
				return results.firstElement();
			} else if (results.size() > 1) {
				if (logger.isLoggable(Level.WARNING) && warnOnFailure)
					logger.warning(("While decoding BindingValue '" + aValue + "' : found ambigous methods " + methodName));
				return results.firstElement();
			}
			if (logger.isLoggable(Level.WARNING) && warnOnFailure)
				logger.warning("Could not decode BindingValue : cannot find method call matching '" + aValue);
			return null;
		}

		@Override
		public TranstypedBinding convertFromString(String aValue) {

			String fullyQualifiedTranstyperName;
			Vector<String> paramsAsString;

			try {
				ExpressionParser parser = new DefaultExpressionParser();
				Expression parsedExpression = parser.parse(aValue);
				if (parsedExpression instanceof Function) {
					fullyQualifiedTranstyperName = ((Function) parsedExpression).getName();
					paramsAsString = new Vector<String>();
					for (Expression e : ((Function) parsedExpression).getArgs()) {
						paramsAsString.add(e.toString());
					}
				} else {
					if (logger.isLoggable(Level.WARNING) && warnOnFailure)
						logger.warning("Could not decode TranstypedBinding : trying to find transtyper syntax with '" + aValue
								+ " : wrong syntax");
					return null;
				}
			} catch (ParseException e) {
				if (logger.isLoggable(Level.WARNING) && warnOnFailure)
					logger.warning("Could not decode TranstypedBinding : parse error while trying to decode as transtyped binding '"
							+ aValue);
				return null;
			}

			String fullyQualifiedEntityName = fullyQualifiedTranstyperName.substring(0, fullyQualifiedTranstyperName.lastIndexOf("."));
			String transtyperName = fullyQualifiedTranstyperName.substring(fullyQualifiedTranstyperName.lastIndexOf(".") + 1);

			if (_bindable == null) {
				if (logger.isLoggable(Level.WARNING) && warnOnFailure)
					logger.warning(("Could not decode TranstypedBinding '" + aValue + "' : no declared bindable !"));
				return null;
			}

			if (_bindable.getBindingModel() == null) {
				if (logger.isLoggable(Level.WARNING) && warnOnFailure)
					logger.warning(("Could not decode TranstypedBinding '" + aValue + "' : declared bindable has a null binding model !"));
				return null;
			}

			DMEntity declaringEntity = ((FlexoModelObject) _bindable).getProject().getDataModel().getEntityNamed(fullyQualifiedEntityName);
			if (declaringEntity == null) {
				if (logger.isLoggable(Level.WARNING) && warnOnFailure)
					logger.warning(("Could not decode TranstypedBinding '" + aValue + "' : no declaring entity !"));
				return null;
			}
			DMTranstyper transtyper = declaringEntity.getDMTranstyper(transtyperName);
			if (transtyper == null) {
				if (logger.isLoggable(Level.WARNING) && warnOnFailure)
					logger.warning(("Could not decode TranstypedBinding '" + aValue + "' : could not find transtyper " + transtyperName
							+ " for " + declaringEntity.getFullyQualifiedName()));
				return null;
			}

			TranstypedBinding returned = new TranstypedBinding();
			returned.setTranstyper(transtyper);

			logger.info("Built new TranstypedBinding: " + returned);

			if (returned.getValues().size() != paramsAsString.size()) {
				if (logger.isLoggable(Level.WARNING) && warnOnFailure)
					logger.warning("Could not decode TranstypedBinding '" + aValue + "' : parameters size does not match");
				return null;
			}

			_abstractBindingStringConverter.setBindable(_bindable);
			for (int i = 0; i < paramsAsString.size(); i++) {
				String bindingAsString = paramsAsString.elementAt(i);
				if (logger.isLoggable(Level.FINE))
					logger.fine("Attempt to parse: " + bindingAsString);
				AbstractBinding paramBindingValue = _abstractBindingStringConverter.convertFromString(bindingAsString);
				if ((paramBindingValue != null)) {
					returned.getValues().elementAt(i).setBindingValue(paramBindingValue);
				} else {
					if (logger.isLoggable(Level.WARNING) && warnOnFailure)
						logger.warning("Could not decode TranstypedBinding '" + aValue + "' : failed to decode '" + bindingAsString + "'");
					return null;
				}
			}

			return returned;

		}

		@Override
		public String convertToString(TranstypedBinding value) {
			return value.getStringRepresentation();
		}

	}

	@Override
	public TranstypedBindingStringConverter getConverter() {
		if (getProject() != null)
			return getProject().getTranstypedBindingStringConverter();
		return null;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// TODO to be implemented
	}

	@Override
	public void setsWith(AbstractBinding aValue) {
		super.setsWith(aValue);
		if (aValue != null) {
			if (aValue instanceof TranstypedBinding) {
				TranstypedBinding otherBinding = (TranstypedBinding) aValue;
				setTranstyper(otherBinding.getTranstyper());
				if (transtyper != null) {
					for (int i = 0; i < transtyper.getEntries().size(); i++) {
						values.elementAt(i).setBindingValue(otherBinding.getValues().elementAt(i).getBindingValue());
					}
				}
			} else {
				logger.warning("setsWith called with mismatched type " + aValue.getClass().getSimpleName() + ", expected TranstypedBinding");
			}

		}
	}

	@Override
	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (object instanceof TranstypedBinding) {
			TranstypedBinding tb = (TranstypedBinding) object;
			if (getBindingDefinition() == null) {
				if (tb.getBindingDefinition() != null)
					return false;
			} else {
				if (!getBindingDefinition().equals(tb.getBindingDefinition()))
					return false;
			}
			return ((_owner == tb._owner) && (getTranstyper() == tb.getTranstyper()) && (getValues().equals(tb.getValues())));
		} else {
			return super.equals(object);
		}
	}

	/**
	 * Return accessed type
	 * 
	 * @return
	 */
	@Override
	public DMType getAccessedType() {
		if (transtyper != null)
			return transtyper.getReturnedType();
		return null;
	}

	@Override
	public boolean isBindingValid() {
		if (logger.isLoggable(Level.FINE))
			logger.fine("Is StaticBinding " + this + " valid ?");

		if (getAccessedType() == null) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Invalid binding because accessed type is null");
			return false;
		}

		if (getBindingDefinition() == null) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Invalid binding because binding definition is null");
			return false;
		} else if (getBindingDefinition().getIsSettable()) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Invalid binding because binding definition is declared as settable");
			return false;
		} else if (getBindingDefinition().getBindingDefinitionType() == BindingDefinitionType.EXECUTE) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Invalid binding because binding definition is declared as executable");
			return false;
		}

		if (getTranstyper() == null) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Invalid binding because transtyper is null");
			return false;
		}

		for (TranstypedBindingValue v : getValues()) {
			if (v.getEntry() == null) {
				if (logger.isLoggable(Level.FINE))
					logger.fine("Invalid binding because value entry is null");
				return false;
			}
			if (v.getBindingValue() == null) {
				if (logger.isLoggable(Level.FINE))
					logger.fine("Invalid binding because value of " + v.getEntry().getName() + " is null");
				return false;
			} else if (!v.getBindingValue().isBindingValid()) {
				if (logger.isLoggable(Level.FINE))
					logger.fine("Invalid binding because value of " + v.getEntry().getName() + " is not valid");
				return false;
			}
		}

		if (getBindingDefinition().getType() == null || getBindingDefinition().getType().isAssignableFrom(getAccessedType(), true)) {
			return true;
		}

		if (logger.isLoggable(Level.FINE))
			logger.fine("Invalid binding because types are not matching searched " + getBindingDefinition().getType() + " having "
					+ getAccessedType());
		return false;

	}

	protected boolean _areTypesMatching() {
		return getBindingDefinition().getType() == null || getBindingDefinition().getType().isAssignableFrom(getAccessedType(), true);
	}

	@Override
	public boolean isStaticValue() {
		return false;
	}

	@Override
	public TranstypedBinding clone() {
		TranstypedBinding clone = new TranstypedBinding(getBindingDefinition(), getOwner());
		clone.setsWith(this);
		return clone;
	}

	@Override
	public String getJavaCodeStringRepresentation() {
		return getStringRepresentation();
	}

	@Override
	public String getStringRepresentation() {
		if (getTranstyper() == null)
			return "null";

		StringBuffer sb = new StringBuffer();
		sb.append(getTranstyper().getDeclaringEntity().getFullyQualifiedName());
		sb.append("." + getTranstyper().getJavaMethodName());
		sb.append("(");
		boolean isFirst = true;
		for (TranstypedBindingValue entry : getValues()) {
			sb.append((isFirst ? "" : ",") + (entry.getBindingValue() != null ? entry.getBindingValue().getStringRepresentation() : "null"));
			isFirst = false;
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String getWodStringRepresentation() {
		logger.severe("transtyper in wod files isn't supported yet");
		return "\"transtyper in wod files isn't supported yet\"";
	}

	public boolean isBasicTranstyping() {
		return ((getTranstyper() != null) && (getTranstyper().getEntries().size() == 1));
	}
}
