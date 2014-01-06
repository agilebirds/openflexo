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

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.parser.ExpressionParser;
import org.openflexo.antar.expr.parser.ParseException;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.bindings.MethodCall.MethodCallArgument;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.dm.DMPropertyNameChanged;
import org.openflexo.foundation.dm.dm.EntityDeleted;
import org.openflexo.foundation.dm.dm.PropertyDeleted;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.ComponentDefinition.ComponentDefinitionBindingModel;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.wkf.WKFObject;

/**
 * Represents a binding value. A binding value MUST refer to a BindingDefinition object, and an owner which is the Bindable objet used to
 * get the BindingModel. The root object defining the binding value is the BindingVariable object, while properties path defined as a Vector
 * defines all the properties leading to accessed value.
 * 
 * @author sguerin
 * 
 */
public class BindingValue extends AbstractBinding {

	static final Logger logger = Logger.getLogger(BindingValue.class.getPackage().getName());

	BindingVariable _bindingVariable;

	protected BindingPath _bindingPath;

	protected boolean _isConnected;

	public boolean isCompoundBinding() {
		for (BindingPathElement e : _bindingPath) {
			if (e instanceof MethodCall) {
				return true;
			}
		}
		return false;
	}

	public BindingValue() {
		super();
		init();
	}

	public BindingValue(BindingDefinition bindingDefinition, AgileBirdsObject owner) {
		super(bindingDefinition, owner);
		init();
		if (owner != null && !(owner instanceof Bindable)) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Declared non-null owner is not a Bindable !");
			}
		}
	}

	private void init() {
		_bindingVariable = null;
		_bindingPath = new BindingPath();
		_isConnected = false;
	}

	@Override
	public BindingValue clone() {
		BindingValue clone = new BindingValue(getBindingDefinition(), getOwner());
		clone.setsWith(this);
		return clone;
	}

	public boolean isProperty(BindingPathElement p) {
		return getBindingPath() != null && getBindingPath().size() == 1 && getBindingPathElementAtIndex(0).equals(p);
	}

	@Override
	public void setsWith(AbstractBinding aValue) {
		super.setsWith(aValue);
		if (aValue != null) {
			if (aValue instanceof BindingValue) {
				BindingValue bv = (BindingValue) aValue;
				setBindingVariable(bv.getBindingVariable());
				if (getBindingVariable() != null && getBindingVariable().getType() != null
						&& getBindingVariable().getType().getBaseEntity() != null) {
					getBindingVariable().getType().getBaseEntity().addObserver(this);
				}
				BindingPath bindingPath = new BindingPath();
				for (BindingPathElement e : bv.getBindingPath()) {
					bindingPath.add(e);
				}
				// bindingPath.addAll(aValue.getBindingPath());
				setBindingPath(bindingPath);
				for (BindingPathElement element : bindingPath) {
					element.addObserver(this);
				}
			} else {
				logger.warning("setsWith called with mismatched type " + aValue.getClass().getSimpleName() + ", expected BindingValue");
			}
		}
	}

	public boolean isConnected() {
		return _isConnected;
	}

	public void connect() {
		unparsableValue = null;
		if (isBindingValid()) {
			_isConnected = true;
			setChanged();
			if (getOwner() instanceof WKFObject) {
				getOwner().setChanged();
				((WKFObject) getOwner()).notifyAttributeModification(getBindingDefinition().getVariableName(), null, this);
			} else if (getOwner() instanceof IEObject) {
				getOwner().setChanged();
				((IEObject) getOwner()).notifyObservers(new IEDataModification(getBindingDefinition().getVariableName(), null, this));
			}
		}
	}

	@Override
	public boolean isStaticValue() {
		return false;
	}

	@Override
	public boolean isBindingValid() {
		// logger.setLevel(Level.FINE);

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Is BindingValue " + this + " valid ?");
		}

		if (getAccessedType() == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Invalid binding because accessed type is null");
			}
			return false;
		}

		if (getBindingDefinition() == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Invalid binding because _bindingDefinition is null");
			}
			return false;
		}

		if (_bindingVariable == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Invalid binding because _bindingVariable is null");
			}
			return false;
		}
		if (!_checkBindingPathValid()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Invalid binding because binding path not valid");
			}
			return false;
		}

		if (getBindingDefinition() != null && getBindingDefinition().getIsSettable()) {
			if (getBindingPathLastElement() == null || !(getBindingPathLastElement() instanceof DMProperty)
					|| !((DMProperty) getBindingPathLastElement()).getIsSettable()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Invalid binding because binding definition declared as settable and definition cannot satisfy it");
				}
				return false;
			}
		}

		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("_bindingDefinition.getType()=" + getBindingDefinition().getType());
			logger.finest("_bindingDefinition.getType().getBaseEntity()=" + getBindingDefinition().getType().getBaseEntity());
			logger.finest("getAccessedType()=" + getAccessedType());
			logger.finest("_bindingPath.size()=" + _bindingPath.size());
			logger.finest("_bindingDefinition.getType().isAssignableFrom(getAccessedType())="
					+ getBindingDefinition().getType().isAssignableFrom(getAccessedType(), true));
		}

		if (getBindingDefinition().getType() != null && getBindingDefinition().getType().isAssignableFrom(getAccessedType(), true)) {
			if (getOwner() instanceof IEObject) {
				if (ComponentDefinitionBindingModel.COMPONENT_BINDING_VARIABLE_NAME.equals(getStringRepresentation())) {
					return false;
				}
			}
		}
		if (getBindingDefinition().getType() == null || getBindingDefinition().getType().isAssignableFrom(getAccessedType(), true)) {
			return true;
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Invalid binding because types are not matching searched " + getBindingDefinition().getType() + " having "
					+ getAccessedType());
		}
		return false;

	}

	/**
	 * Return accessed type
	 * 
	 * @return
	 */
	@Override
	public DMType getAccessedType() {

		if (_bindingPath.size() == 0) {
			if (_bindingVariable != null) {
				return _bindingVariable.getType();
			}
			return null;
		}

		return _bindingPath.getResultingTypeAtIndex(_bindingPath.size() - 1);

	}

	protected boolean isBindingValidWithoutBindingDefinition() {
		return _checkBindingPathValid();
	}

	private boolean _checkBindingPathValid() {
		if (_bindingVariable == null) {
			return false;
		}
		DMType currentType = _bindingVariable.getType();
		if (currentType == null) {
			return false;
		}

		for (int i = 0; i < _bindingPath.size(); i++) {
			BindingPathElement element = _bindingPath.elementAt(i);
			if (element.getEntity() == null || currentType.getBaseEntity() == null
					|| !element.getEntity().isAncestorOf(currentType.getBaseEntity())) {
				return false;
			}
			if (!element.isBindingValid()) {
				return false;
			}
			currentType = _bindingPath.getResultingTypeAtIndex(i);
			if (currentType == null) {
				return false;
			}
		}

		return true;
	}

	@Override
	protected void _applyNewBindingDefinition() {
		if (!isDeserializing() && isConnected() && !isBindingValid()) {
			_isConnected = false;
		}
	}

	public BindingVariable getBindingVariable() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Binding Variable of bindingValue for Entry:"
					+ (getBindingDefinition() != null ? getBindingDefinition().getVariableName() : null) + " var:"
					+ (_bindingVariable != null ? _bindingVariable.getVariableName() : null) + " " + _bindingVariable);
		}
		return _bindingVariable;
	}

	public void setBindingVariable(BindingVariable bindingVariable) {
		unparsableValue = null;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Set binding variable to " + bindingVariable);
		}
		/*if (bindingVariable != null)
		    _isStaticValue = false;*/
		if (bindingVariable != _bindingVariable) {
			BindingVariable oldBindingVariable = _bindingVariable;
			if (oldBindingVariable != null && oldBindingVariable.getType() != null) {
				oldBindingVariable.getType().getBaseEntity().deleteObserver(this);
			}
			_bindingVariable = bindingVariable;
			if (_bindingVariable != null && _bindingVariable.getType() != null) {
				if (_bindingVariable.getType().getBaseEntity() != null) {
					_bindingVariable.getType().getBaseEntity().addObserver(this);
				}
			}
			_bindingPath.removeAllElements();
			_isConnected = false;
		}
	}

	public BindingPath getBindingPath() {
		return _bindingPath;
	}

	public void setBindingPath(BindingPath bindingPath) {
		unparsableValue = null;
		_bindingPath = bindingPath;
	}

	/**
	 * @param element
	 * @param i
	 */
	public DMType addBindingPathElement(BindingPathElement element) {
		unparsableValue = null;
		int index = _bindingPath.size();
		setBindingPathElementAtIndex(element, index);
		return _bindingPath.getResultingTypeAtIndex(index);
	}

	/**
	 * @param element
	 * @param i
	 */
	public void setBindingPathElementAtIndex(BindingPathElement element, int i) {
		unparsableValue = null;
		if (i < _bindingPath.size() && _bindingPath.elementAt(i) == element) {
			return;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Set property " + element + " at index " + i);
		}
		if (i < _bindingPath.size()) {
			_bindingPath.set(i, element);
			int size = _bindingPath.size();
			for (int j = i + 1; j < size; j++) {
				_bindingPath.removeElementAt(i + 1);
			}
		} else if (i == _bindingPath.size()) {
			_bindingPath.add(element);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not set property at index " + i);
			}
		}
		_isConnected = false;
	}

	public void disconnect() {
		unparsableValue = null;
		_isConnected = false;
	}

	public BindingPathElement getBindingPathElementAtIndex(int i) {
		if (i < _bindingPath.size()) {
			return _bindingPath.elementAt(i);
		}
		return null;
	}

	public int getBindingPathElementCount() {
		return _bindingPath.size();
	}

	public void removeBindingPathElementAfter(BindingPathElement requestedLast) {
		unparsableValue = null;
		if (_bindingPath != null && _bindingPath.lastElement() != null && _bindingPath.lastElement().equals(requestedLast)) {
			return;
		} else if (_bindingPath != null && _bindingPath.lastElement() != null) {
			_bindingPath.removeElement(_bindingPath.lastElement());
			removeBindingPathElementAfter(requestedLast);
		}
	}

	public boolean isLastBindingPathElement(BindingPathElement element, int index) {
		if (_bindingPath.size() < 1) {
			return false;
		}
		return _bindingPath.lastElement() == element && index == _bindingPath.size() - 1;
	}

	public DMType getBindingPathLastElementType() {
		if (getBindingPath() != null && getBindingPath().size() > 0 && getBindingPath().lastElement() != null) {
			return getBindingPath().lastElement().getType();
		}
		return null;
	}

	public BindingPathElement getBindingPathLastElement() {
		if (getBindingPath() != null && getBindingPath().size() > 0 && getBindingPath().lastElement() != null) {
			return getBindingPath().lastElement();
		}
		return null;
	}

	@Override
	public void setOwner(AgileBirdsObject owner) {
		super.setOwner(owner);
		for (BindingPathElement e : _bindingPath) {
			if (e instanceof MethodCall) {
				MethodCall mc = (MethodCall) e;
				for (MethodCallArgument arg : mc.getArgs()) {
					if (arg.getBinding() != null) {
						arg.getBinding().setOwner(owner);
					}
				}
			}
		}
	}

	public String getStringRepresentationWithoutNfirstElements(int n) {
		String s = getStringRepresentation();
		for (int k = n; k > 0; k--) {
			try {
				s = s.substring(s.indexOf(".") + 1);
			} catch (Exception e) {
				return null;
			}
		}
		return s;
	}

	public String getStringRepresentationWithoutNlastElements(int n) {
		if (_bindingVariable != null) {
			StringBuilder sb = new StringBuilder(_bindingVariable.getVariableName());
			int i = 0;
			for (BindingPathElement element : _bindingPath) {
				if (i < _bindingPath.size() - n) {
					if (element != null) {
						sb.append(".").append(element.getSerializationRepresentation());
					} else {
						sb.append(".null");
					}
				}
				i++;
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	@Override
	public String getStringRepresentation() {
		if (getUnparsableValue() != null) {
			return getUnparsableValue();
		}
		if (_bindingVariable != null) {
			StringBuilder sb = new StringBuilder(_bindingVariable.getVariableName());
			for (BindingPathElement element : _bindingPath) {
				if (element != null) {
					sb.append(".").append(element.getSerializationRepresentation());
				} else {
					sb.append(".null");
				}
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	public boolean isPointingToSessionVariable() {
		if (_bindingPath.size() != 2) {
			return false;
		}
		DMEntity _bdPath0Entity = _bindingPath.get(0).getResultingType().getBaseEntity();
		DMEntity _sessionType = getProject().getDataModel().getWORepository().getCustomSessionEntity();
		return _bdPath0Entity.equals(_sessionType);
	}

	public boolean isPointingToComponentVariable() {
		return _bindingPath.size() == 1;
	}

	public boolean isPointingToSessionOrComponentVariable() {
		return isBindingValid() && (isPointingToSessionVariable() || isPointingToComponentVariable());
	}

	@Override
	public String getWodStringRepresentation() {
		return getCodeStringRepresentation().replaceAll("\\(\\s*\\)", "");
	}

	@Override
	public String getCodeStringRepresentation() {
		String answer = getStringRepresentation();
		// TODO: we must find a way to handle properly those issues
		// by supplying a parameter to that method for example
		if (answer.startsWith("component.")) {
			return answer.substring(10);
		}
		return answer;
	}

	@Override
	public String getJavaCodeStringRepresentation() {
		if (_bindingVariable == null) {
			return "???";
		}
		StringBuilder sb = new StringBuilder();
		// TODO: we must find a way to handle properly those issues
		// by supplying a parameter to that method for example
		if (!ComponentDefinition.ComponentDefinitionBindingModel.COMPONENT_BINDING_VARIABLE_NAME.equals(_bindingVariable.getVariableName())) {
			sb.append(_bindingVariable.getJavaAccess());
		}
		DMType currentType = _bindingVariable.getType();
		for (BindingPathElement element : _bindingPath) {
			if (element.getEntity() == null || !element.getEntity().isAncestorOf(currentType.getBaseEntity())) {
				return getCodeStringRepresentation();
			}
			if (!element.isBindingValid()) {
				return getCodeStringRepresentation();
			}
			if (sb.length() > 0) {
				sb.append(".");
			}
			if (element instanceof DMProperty) {
				if (!((DMProperty) element).hasAccessors()) {
					sb.append(((DMProperty) element).getName());
				} else {
					sb.append(((DMProperty) element).getGetterName());
					sb.append("()");
				}
			} else if (element instanceof MethodCall) {
				sb.append(((MethodCall) element).getJavaCodeStringRepresentation());
			} else {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("BindingPathElement is neither a MethodCall nor a property!!!");
				}
			}
			currentType = element.getType();
		}
		String reply = sb.toString();
		// TODO: we must find a way to handle properly those issues
		// by supplying a parameter to that method for example
		if (reply.startsWith("session().")) {
			return "getSession()." + reply.substring(10);
		}
		return reply;
	}

	public String getSetterJavaCodeStringRepresentation(AbstractBinding value) {
		return getSetterJavaCodeStringRepresentation(value.getJavaCodeStringRepresentation());
	}

	public String getSetterJavaCodeStringRepresentation(String value) {
		StringBuilder sb = new StringBuilder();
		// TODO: we must find a way to handle properly those issues
		// by supplying a parameter to that method for example
		if (!ComponentDefinition.ComponentDefinitionBindingModel.COMPONENT_BINDING_VARIABLE_NAME.equals(_bindingVariable.getVariableName())) {
			sb.append(_bindingVariable.getJavaAccess());
		}
		DMType currentType = _bindingVariable.getType();
		for (BindingPathElement element : _bindingPath) {
			if (element.getEntity() == null || !element.getEntity().isAncestorOf(currentType.getBaseEntity())) {
				return getCodeStringRepresentation();
			}
			if (!element.isBindingValid()) {
				return getCodeStringRepresentation();
			}
			if (sb.length() > 0) {
				sb.append(".");
			}
			if (element == getBindingPathLastElement()) {
				if (element instanceof DMProperty && ((DMProperty) element).getIsSettable()) {
					if (!((DMProperty) element).hasAccessors()) {
						sb.append(((DMProperty) element).getName());
						sb.append(" = ");
						sb.append(value);
					} else {
						sb.append(((DMProperty) element).getSetterName());
						sb.append("(");
						sb.append(value);
						sb.append(")");
					}
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Last BindingPathElement for a BindingValue considered as settable is not a property or not settable !!!");
					}
				}
			} else {
				if (element instanceof DMProperty) {
					if (!((DMProperty) element).hasAccessors()) {
						sb.append(((DMProperty) element).getName());
					} else {
						sb.append(((DMProperty) element).getGetterName());
						sb.append("()");
					}
				} else if (element instanceof MethodCall) {
					sb.append(((MethodCall) element).getJavaCodeStringRepresentation());
				} else {
					if (logger.isLoggable(Level.SEVERE)) {
						logger.severe("BindingPathElement is neither a MethodCall nor a property!!!");
					}
				}
			}
			currentType = element.getType();
		}
		String reply = sb.toString();
		// TODO: we must find a way to handle properly those issues
		// by supplying a parameter to that method for example
		if (reply.startsWith("session().")) {
			return "getSession()." + reply.substring(10);
		}
		return reply;
	}

	@Override
	public String getFullyQualifiedName() {
		if (getBindingDefinition() != null) {
			return new StringBuffer("BINDING_VALUE.").append(getBindingDefinition().getVariableName()).append("=")
					.append(getCodeStringRepresentation()).toString();
		} else {
			return "BINDING_VALUE.<null>=" + getCodeStringRepresentation();
		}
	}

	// ==========================================================
	// ================= Serialization stuff ====================
	// ==========================================================

	public static class BindingValueStringConverter extends AbstractBindingStringConverter<BindingValue> {
		private Bindable _bindable;

		private DecodingPreProcessor _preProcessor = null;

		private AbstractBindingStringConverter _abstractBindingStringConverter;

		public BindingValueStringConverter(AbstractBindingStringConverter abstractBindingStringConverter) {
			super(BindingValue.class);
			_abstractBindingStringConverter = abstractBindingStringConverter;
		}

		private MethodCall tryToDecodeAsMethodCall(BindingValue owner, DMType currentType, String aValue) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("tryToDecodeAsMethodCall " + aValue);
			}

			String methodName;
			Vector<String> paramsAsString;

			try {
				Expression parsedExpression = ExpressionParser.parse(aValue);
				if (parsedExpression instanceof Function) {
					methodName = ((Function) parsedExpression).getName();
					paramsAsString = new Vector<String>();
					for (Expression e : ((Function) parsedExpression).getArgs()) {
						paramsAsString.add(e.toString());
					}
				} else {
					if (logger.isLoggable(Level.WARNING) && warnOnFailure) {
						logger.warning("Could not decode BindingValue : trying to find method call matching '" + aValue
								+ " this is not a function call");
					}
					return null;
				}
			} catch (ParseException e) {
				if (logger.isLoggable(Level.WARNING) && warnOnFailure) {
					logger.warning("Could not decode BindingValue : parse error while trying to find method call matching '" + aValue);
				}
				return null;
			}

			Vector<DMMethod> allMethods = currentType.getBaseEntity().getAccessibleMethods();

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("allMethods=" + allMethods);
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("paramsAsString=" + paramsAsString);
			}
			Vector<DMMethod> possiblyMatchingMethods = new Vector<DMMethod>();
			for (DMMethod method : allMethods) {
				if (method.getName() != null && method.getName().equals(methodName)
						&& method.getParameters().size() == paramsAsString.size()) {
					possiblyMatchingMethods.add(method);
				}
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("possiblyMatchingMethods=" + possiblyMatchingMethods);
			}
			Vector<MethodCall> results = new Vector<MethodCall>();
			for (DMMethod method : possiblyMatchingMethods) {
				boolean successfull = true;
				MethodCall methodCall = new MethodCall(owner, method);
				for (int i = 0; i < method.getParameters().size(); i++) {
					DMMethod.DMMethodParameter param = method.getParameters().elementAt(i);
					String bindingAsString = paramsAsString.elementAt(i);
					DMType type = param.getType();
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Attempt to parse: " + bindingAsString);
					}
					AbstractBinding paramBindingValue = _abstractBindingStringConverter.convertFromString(bindingAsString);
					if (paramBindingValue != null) {
						paramBindingValue.setOwner((AgileBirdsObject) _bindable);
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("paramBindingValue=" + paramBindingValue + " of " + paramBindingValue.getAccessedType());
						}
						if (paramBindingValue.isStaticValue()) {
							paramBindingValue.setBindingDefinition(methodCall.new MethodCallParamBindingDefinition(param));
						}
						if (type != null && paramBindingValue.getAccessedType() != null
								&& type.isAssignableFrom(paramBindingValue.getAccessedType(), true)) {
							if (logger.isLoggable(Level.FINE)) {
								logger.fine("Lookup on " + type.getName() + " succeded: " + paramBindingValue.getStringRepresentation());
							}
							methodCall.setBindingValueForParam(paramBindingValue, param);
						} else {
							if (logger.isLoggable(Level.FINE)) {
								logger.fine("Lookup on type " + type + " failed (wrong type): "
										+ paramBindingValue.getStringRepresentation() + "types: " + "looking " + type + " found "
										+ paramBindingValue.getAccessedType());
							}
							successfull = false;
						}
					} else {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Lookup on " + type.getName() + " failed (cannot analysing): " + bindingAsString);
						}
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
				if (logger.isLoggable(Level.WARNING) && warnOnFailure) {
					logger.warning("While decoding BindingValue '" + aValue + "' : found ambigous methods " + methodName);
				}
				return results.firstElement();
			}
			if (logger.isLoggable(Level.WARNING) && warnOnFailure) {
				logger.warning("Could not decode BindingValue : cannot find method call matching '" + aValue);
			}
			return null;
		}

		protected class PathTokenizer {
			private Vector<String> _tokens;

			private Enumeration<String> enumeration;

			protected PathTokenizer(String value) {
				super();
				_tokens = new Vector<String>();
				StringTokenizer st = new StringTokenizer(value, ".()", true);
				String current = "";
				int level = 0;
				while (st.hasMoreElements()) {
					String next = st.nextToken();
					if (next.equals(".") && current.trim().length() > 0 && level == 0) {
						_tokens.add(current);
						current = "";
					} else if (next.equals("(")) {
						current += next;
						level++;
					} else if (next.equals(")")) {
						current += next;
						level--;
					} else {
						current += next;
					}
				}
				if (current.trim().length() > 0 && level == 0) {
					_tokens.add(current);
					current = "";
				}
				enumeration = _tokens.elements();
			}

			public boolean hasMoreTokens() {
				return enumeration.hasMoreElements();
			}

			public String nextToken() {
				String returned = enumeration.nextElement();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("> " + returned);
				}
				return returned;
			}
		}

		@Override
		public BindingValue convertFromString(String aValue) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Decode " + aValue);
			}
			if (_bindable == null) {
				if (logger.isLoggable(Level.WARNING) && warnOnFailure) {
					logger.warning("Could not decode BindingValue '" + aValue + "' : bindable not set !");
				}
				return null;
			} else {
				String value;
				if (_preProcessor != null) {
					value = _preProcessor.preProcessString(aValue);
				} else {
					value = aValue;
				}
				if ("null".equals(value)) {
					return null;
				}

				BindingValue returned = new BindingValue();

				PathTokenizer st = new PathTokenizer(value);
				if (st.hasMoreTokens()) {
					String bindingVariableName = st.nextToken();
					if (_bindable == null) {
						if (logger.isLoggable(Level.WARNING) && warnOnFailure) {
							logger.warning("Could not decode BindingValue '" + value + "' : no declared bindable !");
						}
						return null;
					}
					if (_bindable.getBindingModel() == null) {
						if (logger.isLoggable(Level.WARNING) && warnOnFailure) {
							logger.warning("Could not decode BindingValue '" + value + "' : declared bindable has a null binding model !");
						}
						return null;
					}
					BindingVariable bv = _bindable.getBindingModel().bindingVariableNamed(bindingVariableName);
					if (bv == null) {
						if (logger.isLoggable(Level.WARNING) && warnOnFailure) {
							logger.warning("Could not decode BindingValue '" + value + "' : variable " + bindingVariableName
									+ " not found in binding model !");
						}
						return null;
					} else {
						DMType currentType = bv.getType();
						if (currentType == null) {
							if (logger.isLoggable(Level.WARNING) && warnOnFailure) {
								logger.warning("Could not decode BindingValue '" + value + "' : variable " + bindingVariableName
										+ " doesn't implement any type !");
							}
							return null;
						}
						returned.setBindingVariable(bv);
						while (st.hasMoreTokens()) {
							String nextTokenName = st.nextToken();
							BindingPathElement nextElement;
							if (currentType.getBaseEntity() == null) {
								if (logger.isLoggable(Level.WARNING) && warnOnFailure) {
									logger.warning("Could not decode BindingValue '" + value + "' : cannot find base entity for type '"
											+ currentType);
								}
								return null;
							}
							nextElement = currentType.getBaseEntity().getProperty(nextTokenName);
							if (nextElement == null) {
								// OK, may be this is a MethodCall
								nextElement = tryToDecodeAsMethodCall(returned, currentType, nextTokenName);
							}
							if (nextElement == null) {
								if (logger.isLoggable(Level.WARNING) && warnOnFailure) {
									logger.warning("Could not decode BindingValue '" + value
											+ "' : cannot find property nor method matching '" + nextTokenName + "' for type "
											+ currentType + " !");
								}
								return null;
							} else {
								currentType = returned.addBindingPathElement(nextElement);
								// returned.addBindingPathElement(nextElement);
								// currentType = nextElement.getType();
								if (currentType == null) {
									if (logger.isLoggable(Level.WARNING) && warnOnFailure) {
										logger.warning("Could not decode BindingValue '" + value + "' : token " + nextTokenName
												+ " doesn't implement any type !");
									}
									return null;
								}
							}
						}
						// Before to receive its owner, we set to knonwn bindable, in order to check validity
						returned.setOwner((AgileBirdsObject) _bindable);
						if (returned.isBindingValidWithoutBindingDefinition()) {
							returned._isConnected = true;
							return returned;
						} else {
							if (logger.isLoggable(Level.WARNING) && warnOnFailure) {
								logger.warning("Could not decode BindingValue '" + value + "' : invalid binding !");
							}
							return null;
						}
					}
				} else {
					if (logger.isLoggable(Level.WARNING) && warnOnFailure) {
						logger.warning("Could not decode BindingValue '" + value + "' : variable not set !");
					}
					return null;
				}
			}
		}

		@Override
		public String convertToString(BindingValue value) {
			return value.getStringRepresentation();
		}

		public Bindable getBindable() {
			return _bindable;
		}

		@Override
		public void setBindable(Bindable bindable) {
			_bindable = bindable;
		}

		public DecodingPreProcessor getPreProcessor() {
			return _preProcessor;
		}

		public void setPreProcessor(DecodingPreProcessor preProcessor) {
			_preProcessor = preProcessor;
		}
	}

	public static abstract class DecodingPreProcessor {
		public abstract String preProcessString(String aString);
	}

	@Override
	public BindingValueStringConverter getConverter() {
		if (getProject() != null) {
			return getProject().getBindingValueConverter();
		}
		return null;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("update in BindingValue " + getStringRepresentation() + " for " + dataModification.getClass().getName()
					+ " owner is " + getOwner());
		}
		if (dataModification instanceof EntityDeleted && getBindingVariable() != null
				&& ((EntityDeleted) dataModification).oldValue() == getBindingVariable().getType()) {
			setBindingVariable(null);
			setChanged();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Handled entity deleted");
			}
		} else if (dataModification instanceof PropertyDeleted) {
			PropertyDeleted propertyDeleted = (PropertyDeleted) dataModification;
			if (getBindingPath().contains(propertyDeleted.oldValue())) {
				setBindingVariable(null);
				// For me it is not consistant to keep a part of the binding if one previously selected part has been deleted, thus I prefer
				// to delete the entire binding
				// int size = getBindingPath().size();
				// for (int j = getBindingPath().indexOf(propertyDeleted.oldValue()); j < size; j++) {
				// getBindingPath().removeElementAt(getBindingPath().size() - 1);
				// }
				_isConnected = false;
				setChanged();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Handled property deleted");
				}
			}
		} else if (dataModification instanceof DMPropertyNameChanged) {
			if (getBindingPath().contains(((DMPropertyNameChanged) dataModification).getDMProperty())) {
				setChanged();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Handled property renamed");
				}
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("finalize called for BindingValue " + getStringRepresentation());
		}
		setBindingVariable(null);
		super.finalize();
	}

	// GPO: Please leave the extends with the full qualified name of the interface to allow ant to properly compile this class
	public static interface BindingPathElement extends org.openflexo.foundation.dm.Typed {
		public DMEntity getEntity();

		@Override
		public DMType getType();

		public DMType getResultingType();

		public void addObserver(FlexoObserver o);

		public void deleteObserver(FlexoObserver o);

		public String getSerializationRepresentation();

		public boolean isBindingValid();
	}

	/**
	 * Internal class extending Vector to provide observing management for BindingPathElement Additionnaly, provides generics type resolving
	 * by maintaining a resulting type vector
	 * 
	 * @author sguerin
	 * 
	 */
	public class BindingPath extends Vector<BindingPathElement> {
		private Vector<DMType> _resultingTypes;

		BindingPath() {
			super();
			_resultingTypes = new Vector<DMType>();
		}

		@Override
		public synchronized void removeAllElements() {
			for (BindingPathElement element : this) {
				element.deleteObserver(BindingValue.this);
			}
			super.removeAllElements();
			_resultingTypes.clear();
		}

		@Override
		public synchronized BindingPathElement set(int index, BindingPathElement element) {
			if (get(index) != null) {
				get(index).deleteObserver(BindingValue.this);
			}
			element.addObserver(BindingValue.this);
			BindingPathElement returned = super.set(index, element);
			setResultingTypeAtIndex(index);
			return returned;
		}

		@Override
		public synchronized void removeElementAt(int index) {
			if (get(index) != null) {
				get(index).deleteObserver(BindingValue.this);
			}
			super.removeElementAt(index);
		}

		@Override
		public synchronized boolean add(BindingPathElement o) {
			o.addObserver(BindingValue.this);
			boolean returned = super.add(o);
			setResultingTypeAtIndex(size() - 1);
			return returned;
		}

		private void setResultingTypeAtIndex(int index) {
			if (_bindingVariable != null) {

				BindingPathElement bpe = elementAt(index);
				if (bpe == null) {
					logger.warning("Unexpected null element at index " + index);
					return;
				}
				DMType type = bpe.getResultingType();
				if (type == null) {
					logger.warning("Cannot find resuling type for bindingPathElement " + bpe.getSerializationRepresentation()
							+ " in path :" + getStringRepresentation());
					return;
				}
				DMType parentType = null;

				// TODO java.lang.ArrayIndexOutOfBoundsException may happen here, fix this
				// BMA : java.lang.ArrayIndexOutOfBoundsException won't append anymore...
				if (index > 0 && _resultingTypes.size() >= index) {
					parentType = _resultingTypes.elementAt(index - 1);
				} else {
					parentType = _bindingVariable.getType();
				}

				if (type.isGeneric()) {
					type = DMType.makeInstantiatedDMType(type, parentType);
				}

				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Set resulting type at index " + index + " with " + type + " parent=" + parentType);
				}

				if (_resultingTypes.size() > index) {
					_resultingTypes.setElementAt(type, index);
				} else if (_resultingTypes.size() == index) {
					_resultingTypes.add(type);
				} else {
					logger.warning("Unexpected index " + index + " for _resultingTypes.size()=" + _resultingTypes.size());
				}
			}
		}

		public DMType getResultingTypeAtIndex(int index) {
			if (index < _resultingTypes.size()) {
				return _resultingTypes.elementAt(index);
			}
			return null;
		}
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "binding_value";
	}

	public BindingPathElement findFirstBindingPathElementOfType(DMEntity entity) {
		Enumeration<BindingPathElement> en = getBindingPath().elements();
		BindingPathElement reply = null;
		while (en.hasMoreElements()) {
			reply = en.nextElement();
			if (reply.getType().getBaseEntity().equals(entity)) {
				return reply;
			}
		}
		return null;
	}

	public void addBindingPathElementAfterFirstPathElementOfType(DMEntity entity, DMProperty newProperty) {
		BindingPathElement beforeLast = findFirstBindingPathElementOfType(entity);
		if (beforeLast != null) {
			removeBindingPathElementAfter(beforeLast);
		} else {
			while (getBindingPathLastElement() != null) {
				getBindingPath().removeAllElements();
			}
		}
		addBindingPathElement(newProperty);
	}

	public DMEntity getLastEditableEntity() {
		DMEntity reply = getBindingVariable().getContainer() instanceof DMEntity ? (DMEntity) getBindingVariable().getContainer() : null;
		Enumeration<BindingPathElement> en = getBindingPath().elements();
		DMEntity temp = null;
		while (en.hasMoreElements()) {
			temp = en.nextElement().getType().getBaseEntity();
			if (!temp.getIsReadOnly()) {
				reply = temp;
			}
		}
		return reply;
	}

	public BindingPathElement getLastBindingPathElementOfType(DMEntity newEntryEntity) {
		BindingPathElement reply = null;
		Enumeration<BindingPathElement> en = getBindingPath().elements();
		BindingPathElement temp = null;
		while (en.hasMoreElements()) {
			temp = en.nextElement();
			if (!temp.getType().getBaseEntity().equals(newEntryEntity)) {
				reply = temp;
			}
		}
		return reply;
	}

}
