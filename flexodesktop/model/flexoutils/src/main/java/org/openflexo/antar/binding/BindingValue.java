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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.MethodCall.MethodCallArgument;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;

/**
 * Represents a binding value. A binding value MUST refer to a BindingDefinition object, and an owner which is the Bindable objet used to
 * get the BindingModel. The root object defining the binding value is the BindingVariable object, while properties path defined as a Vector
 * defines all the properties leading to accessed value.
 * 
 * @author sguerin
 * 
 */
public class BindingValue extends AbstractBinding {

	@SuppressWarnings("hiding")
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

	// public Exception creationException;

	public BindingValue() {
		super();
		init();
		// logger.info(">>>>>>>>>>>>> Make binding value");
	}

	public BindingValue(BindingDefinition bindingDefinition, Bindable owner) {
		super(bindingDefinition, owner);
		init();
		// logger.info(">>>>>>>>>>>>> Make binding value for " + bindingDefinition);
	}

	private void init() {
		// creationException = new Exception("Binding creation");
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
				BindingPath bindingPath = new BindingPath();
				for (BindingPathElement e : bv.getBindingPath()) {
					bindingPath.add(e);
				}
				// bindingPath.addAll(aValue.getBindingPath());
				setBindingPath(bindingPath);
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
		if (!_checkBindingPathValid(false)) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Invalid binding because binding path not valid");
			}
			return false;
		}

		if (getBindingDefinition() != null && getBindingDefinition().getIsSettable()) {
			if (getBindingPath().size() == 0) {
				if (!_bindingVariable.isSettable()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Invalid binding because binding definition declared as settable and definition cannot satisfy it (binding variable not settable)");
					}
					return false;
				}
			} else if (getBindingPathLastElement() == null || !getBindingPathLastElement().isSettable()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Invalid binding because binding definition declared as settable and definition cannot satisfy it (last binding path not settable)");
				}
				return false;
			}
		}

		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("_bindingDefinition.getType()=" + getBindingDefinition().getType());
			logger.finest("_bindingDefinition.getType().getBaseClass()=" + TypeUtils.getBaseClass(getBindingDefinition().getType()));
			logger.finest("getAccessedType()=" + getAccessedType());
			logger.finest("_bindingPath.size()=" + _bindingPath.size());
			logger.finest("_bindingDefinition.getType().isAssignableFrom(getAccessedType())="
					+ TypeUtils.isTypeAssignableFrom(getBindingDefinition().getType(), getAccessedType(), true));
		}

		if (getBindingDefinition().getType() != null
				&& TypeUtils.isTypeAssignableFrom(getBindingDefinition().getType(), getAccessedType(), true)) {
			// System.out.println("getBindingDefinition().getType()="+getBindingDefinition().getType());
			// System.out.println("getAccessedType()="+getAccessedType());
			return true;
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Invalid binding because types are not matching searched " + getBindingDefinition().getType() + " having "
					+ getAccessedType());
		}
		return false;

	}

	@Override
	public String invalidBindingReason() {
		// logger.setLevel(Level.FINE);

		if (getAccessedType() == null) {
			return "Invalid binding " + this + " because accessed type is null path count=" + _bindingPath.size();
		}

		if (getBindingDefinition() == null) {
			return "Invalid binding " + this + " because _bindingDefinition is null";
		}

		if (_bindingVariable == null) {
			return "Invalid binding " + this + " because _bindingVariable is null";
		}

		if (!_checkBindingPathValid(false)) {
			return "Invalid binding " + this + " because binding path not valid";
		}

		if (getBindingDefinition() != null && getBindingDefinition().getIsSettable()) {
			if (getBindingPath().size() == 0) {
				if (!_bindingVariable.isSettable()) {
					return "Invalid binding because binding definition declared as settable and definition cannot satisfy it (binding variable not settable)";
				}
			} else if (getBindingPathLastElement() == null || !getBindingPathLastElement().isSettable()) {
				return "Invalid binding because binding definition declared as settable and definition cannot satisfy it (last binding path not settable)";
			}
		}

		// System.out.println("getBindingDefinition().getType()="+getBindingDefinition().getType());
		// System.out.println("getAccessedType()="+getAccessedType());

		if (getBindingDefinition().getType() != null
				&& TypeUtils.isTypeAssignableFrom(getBindingDefinition().getType(), getAccessedType(), true)) {
			return "binding is valid";
		}

		return "Invalid binding because types are not matching searched " + getBindingDefinition().getType() + " having "
				+ getAccessedType();

	}

	@Override
	public boolean debugIsBindingValid() {
		// logger.setLevel(Level.FINE);

		logger.info("Is BindingValue " + this + " valid ?");
		logger.info("BindingModel=" + getBindingModel());
		logger.info("BindingFactory=" + getBindingFactory());

		if (getAccessedType() == null) {
			logger.info("Invalid binding " + this + " because accessed type is null path count=" + _bindingPath.size());
			// creationException.printStackTrace();
			for (BindingPathElement e : _bindingPath) {
				logger.info("> Element " + e + " of " + e.getClass().getSimpleName() + " type=" + e.getType());
			}
			return false;
		}

		if (getBindingDefinition() == null) {
			logger.info("Invalid binding " + this + " because _bindingDefinition is null");
			return false;
		}

		if (_bindingVariable == null) {
			logger.info("Invalid binding " + this + " because _bindingVariable is null");
			return false;
		}
		if (!_checkBindingPathValid(true)) {
			logger.info("Invalid binding " + this + " because binding path not valid");
			return false;
		}

		if (getBindingDefinition() != null && getBindingDefinition().getIsSettable()) {
			if (getBindingPath().size() == 0) {
				if (!_bindingVariable.isSettable()) {
					logger.info("Invalid binding because binding definition declared as settable and definition cannot satisfy it (binding variable not settable)");
					return false;
				}
			} else if (getBindingPathLastElement() == null || !getBindingPathLastElement().isSettable()) {
				logger.info("Invalid binding because binding definition declared as settable and definition cannot satisfy it (last binding path not settable)");
				return false;
			}
		}

		logger.info("_bindingDefinition=" + getBindingDefinition());
		logger.info("_bindingDefinition.getType()=" + getBindingDefinition().getType());
		logger.info("_bindingDefinition.getType().getBaseClass()=" + TypeUtils.getBaseClass(getBindingDefinition().getType()));
		logger.info("getAccessedType()=" + getAccessedType());
		logger.info("_bindingPath.size()=" + _bindingPath.size());
		logger.info("_bindingDefinition.getType().isAssignableFrom(getAccessedType())="
				+ TypeUtils.isTypeAssignableFrom(getBindingDefinition().getType(), getAccessedType(), true));

		if (getBindingDefinition().getType() != null
				&& TypeUtils.isTypeAssignableFrom(getBindingDefinition().getType(), getAccessedType(), true)) {
			// System.out.println("getBindingDefinition().getType()="+getBindingDefinition().getType());
			// System.out.println("getAccessedType()="+getAccessedType());
			return true;
		}

		logger.info("Invalid binding because types are not matching searched " + getBindingDefinition().getType() + " having "
				+ getAccessedType());
		return false;

	}

	/**
	 * Return accessed type
	 * 
	 * @return
	 */
	@Override
	public Type getAccessedType() {

		// System.out.println("Compute accessed type for "+this);

		if (_bindingPath.size() == 0) {
			if (_bindingVariable != null) {
				return _bindingVariable.getType();
			}
			return null;
		}

		return _bindingPath.getResultingTypeAtIndex(_bindingPath.size() - 1);

		/*Type returned = _bindingPath.getResultingTypeAtIndex(_bindingPath.size()-1);
		if (returned.equals(Vector.class)) {
		System.out.println("OK, je l'ai pour "+this);
		System.out.println("BV: "+_bindingVariable+" of "+_bindingVariable.getType());
		for (int i=0; i<_bindingPath.size(); i++) {
			System.out.println("index "+i+" : "+_bindingPath.getResultingTypeAtIndex(i)+" for "+_bindingPath.get(i));
		}
		}
		return returned;*/

	}

	protected boolean isBindingValidWithoutBindingDefinition(boolean debug) {
		return _checkBindingPathValid(debug);
	}

	private boolean _checkBindingPathValid(boolean debug) {
		if (_bindingVariable == null) {
			if (debug) {
				System.out.println("BindingVariable is null");
			}
			return false;
		}
		Type currentType = _bindingVariable.getType();
		if (currentType == null) {
			if (debug) {
				System.out.println("currentType is null");
			}
			return false;
		}

		for (int i = 0; i < _bindingPath.size(); i++) {
			BindingPathElement element = _bindingPath.elementAt(i);
			if (i > 0
					&& (element.getDeclaringClass() == null || TypeUtils.getBaseClass(currentType) == null || !TypeUtils.isClassAncestorOf(
							element.getDeclaringClass(), TypeUtils.getBaseClass(currentType)))) {
				if (debug) {
					System.out.println("Mismatched: " + element.getDeclaringClass() + " " + TypeUtils.getBaseClass(currentType)
							+ " element is a " + element.getClass());
				}
				return false;
			}
			if (!element.isBindingValid()) {
				if (debug) {
					System.out.println("element.isBindingValid() = false");
				}
				return false;
			}
			currentType = _bindingPath.getResultingTypeAtIndex(i);
			if (currentType == null) {
				if (debug) {
					System.out.println("currentType is null 2");
				}
				return false;
			}
		}

		return true;
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
			_bindingVariable = bindingVariable;
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
	public Type addBindingPathElement(BindingPathElement element) {
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

	public Type getBindingPathLastElementType() {
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
	public void setOwner(Bindable owner) {
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

	// ==========================================================
	// ================= Serialization stuff ====================
	// ==========================================================

	public static abstract class DecodingPreProcessor {
		public abstract String preProcessString(String aString);
	}

	@Override
	public BindingValueFactory getConverter() {
		if (getOwner() != null) {
			return getOwner().getBindingFactory().getBindingValueFactory();
		}
		return null;
		// return AbstractBinding.bindingValueConverter;
	}

	@Override
	protected void finalize() throws Throwable {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("finalize called for BindingValue " + getStringRepresentation());
		}
		setBindingVariable(null);
		super.finalize();
	}

	/**
	 * Internal class extending Vector to provide observing management for BindingPathElement Additionnaly, provides generics type resolving
	 * by maintaining a resulting type vector
	 * 
	 * @author sguerin
	 * 
	 */
	public class BindingPath extends Vector<BindingPathElement> {
		private final Vector<Type> _resultingTypes;

		BindingPath() {
			super();
			_resultingTypes = new Vector<Type>();
		}

		@Override
		public synchronized void removeAllElements() {
			super.removeAllElements();
			_resultingTypes.clear();
		}

		@Override
		public synchronized BindingPathElement set(int index, BindingPathElement element) {
			BindingPathElement returned = super.set(index, element);
			setResultingTypeAtIndex(index);
			return returned;
		}

		@Override
		public synchronized void removeElementAt(int index) {
			super.removeElementAt(index);
		}

		@Override
		public synchronized boolean add(BindingPathElement o) {
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
				Type type = bpe.getType();
				if (type == null) {
					logger.warning("Cannot find resulting type for bindingPathElement " + bpe.getSerializationRepresentation()
							+ " in path :" + getStringRepresentation());
					return;
				}
				Type parentType = null;

				// TODO java.lang.ArrayIndexOutOfBoundsException may happen here, fix this
				// BMA : java.lang.ArrayIndexOutOfBoundsException won't append anymore...
				if (index > 0 && _resultingTypes.size() >= index) {
					parentType = _resultingTypes.elementAt(index - 1);
				} else {
					parentType = _bindingVariable.getType();
				}

				if (TypeUtils.isGeneric(type)) {
					// System.out.println("bpe: "+bpe+" from "+type+" to "+TypeUtils.makeInstantiatedType(type, parentType));
					type = TypeUtils.makeInstantiatedType(type, parentType);
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

		public Type getResultingTypeAtIndex(int index) {
			if (index < _resultingTypes.size()) {
				return _resultingTypes.elementAt(index);
			}
			return null;
		}
	}

	public BindingPathElement findFirstBindingPathElementOfType(Class aClass) {
		Enumeration<BindingPathElement> en = getBindingPath().elements();
		BindingPathElement reply = null;
		while (en.hasMoreElements()) {
			reply = en.nextElement();
			if (TypeUtils.getBaseClass(reply.getType()).equals(aClass)) {
				return reply;
			}
		}
		return null;
	}

	public void addBindingPathElementAfterFirstPathElementOfType(Class aClass, KeyValueProperty newProperty) {
		BindingPathElement beforeLast = findFirstBindingPathElementOfType(aClass);
		if (beforeLast != null) {
			removeBindingPathElementAfter(beforeLast);
		} else {
			while (getBindingPathLastElement() != null) {
				getBindingPath().removeAllElements();
			}
		}
		addBindingPathElement(newProperty);
	}

	// public static boolean debug = false;

	@Override
	public Object getBindingValue(BindingEvaluationContext context) {
		if (!isBindingValid()) {
			return null;
		}

		// if (getStringRepresentation().equals("an.interesting.binding")) debug=true;

		/*if (_bindingVariable == null) {
		logger.warning("BindingVariable is null for "+getStringRepresentation());
		logger.warning("isBindingValid()="+isBindingValid());
		logger.warning("getBindingPath()="+getBindingPath());
		return null;
		}*/

		Object returned = context.getValue(_bindingVariable);

		if (returned == null) {
			return null;
		}

		try {
			// if (debug) System.out.println("bv="+returned);
			for (BindingPathElement element : getBindingPath()) {
				if (returned == null) {
					// System.out.println("Pb with binding "+getStringRepresentation());
					// System.out.println("Object is null while applying "+((KeyValueProperty)element).getName());
					return null;
				}
				returned = element.getBindingValue(returned, context);

				/*if (element instanceof KeyValueProperty) {
				returned = element.evaluate(returned, context);

				returned = KeyValueDecoder.objectForKey(returned,((KeyValueProperty)element).getName());
				//if (debug) System.out.println("returned="+returned+" after "+((KeyValueProperty)element).getName());
				}
				else if (element instanceof MethodCall) {
				returned = ((MethodCall)element).evaluateBinding(returned, context);
				//if (debug) System.out.println("returned="+returned+" after method "+((MethodCall)element).getMethod());
				}
				else {
				logger.warning("Unexpected: "+element);
				}*/
			}
			return returned;
		} catch (InvalidObjectSpecificationException e) {
			// System.out.println("Trying to evaluate "+getStringRepresentation()+" variable="+context.getValue(_bindingVariable)+" returned="+returned);
			logger.warning("InvalidObjectSpecificationException raised while evaluating GET " + getStringRepresentation() + " : "
					+ e.getMessage());
			return null;
		}

	}

	private final ArrayList<Object> EMPTY_LIST = new ArrayList<Object>();

	/**
	 * Build and return a list of objects (the current object path computed from supplied context)
	 * 
	 * @param context
	 * @return
	 */
	@Override
	public List<Object> getConcernedObjects(BindingEvaluationContext context) {
		if (!isBindingValid()) {
			return EMPTY_LIST;
		}
		if (!isSettable()) {
			return EMPTY_LIST;
		}

		List<Object> returned = new ArrayList<Object>();

		Object current = context.getValue(_bindingVariable);
		returned.add(current);

		for (BindingPathElement<?> element : getBindingPath()) {
			if (element != getBindingPath().lastElement()) {
				// System.out.println("Apply "+element);
				/*if (element instanceof KeyValueProperty) {
				current = KeyValueDecoder.objectForKey(current,((KeyValueProperty)element).getName());
				}
				else if (element instanceof MethodCall) {
				current = ((MethodCall)element).evaluateBinding(current, context);
				}
				else {
				logger.warning("Unexpected: "+element);
				}*/
				current = element.getBindingValue(current, context);
				if (current == null) {
					return returned;
				} else {
					returned.add(current);
				}
			}
		}
		if (current == null) {
			return null;
		}

		return returned;
	}

	/**
	 * Build and return a list of target objects (the current object path computed from supplied context)
	 * 
	 * @param context
	 * @return
	 */
	@Override
	public List<TargetObject> getTargetObjects(BindingEvaluationContext context) {
		if (!isBindingValid()) {
			return null;
		}

		ArrayList<TargetObject> returned = new ArrayList<TargetObject>();

		Object current = context.getValue(_bindingVariable);

		returned.add(new TargetObject(context, _bindingVariable.getVariableName()));

		if (current == null) {
			return returned;
		}

		try {
			for (BindingPathElement element : getBindingPath()) {
				returned.add(new TargetObject(current, element.getLabel()));
				current = element.getBindingValue(current, context);
				/*if (element instanceof KeyValueProperty) {
				returned.add(new TargetObject(current, ((KeyValueProperty)element).getName()));
				current = KeyValueDecoder.objectForKey(current,((KeyValueProperty)element).getName());
				}
				else if (element instanceof MethodCall) {
				returned.add(new TargetObject(current, ((MethodCall)element).getMethod().getName()));
				current = ((MethodCall)element).evaluateBinding(current, context);
				}
				else {
				logger.warning("Unexpected: "+element);
				}*/
				if (current == null) {
					return returned;
				}
			}
		} catch (InvalidObjectSpecificationException e) {
			logger.warning(e.getMessage());
			return returned;
		}

		return returned;
	}

	public Object getTargetObject(BindingEvaluationContext context) {
		if (!isBindingValid()) {
			return null;
		}
		if (!isSettable()) {
			return null;
		}

		Object returned = context.getValue(_bindingVariable);

		for (BindingPathElement element : getBindingPath()) {
			if (element != getBindingPath().lastElement()) {
				// System.out.println("Apply "+element);
				returned = element.getBindingValue(returned, context);
				/*if (element instanceof KeyValueProperty) {
				returned = KeyValueDecoder.objectForKey(returned,((KeyValueProperty)element).getName());
				}
				else if (element instanceof MethodCall) {
				returned = ((MethodCall)element).evaluateBinding(returned, context);
				}
				else {
				logger.warning("Unexpected: "+element);
				}*/
				if (returned == null) {
					logger.warning("Null value when executing setBindingValue() for " + getStringRepresentation());
					return null;
				}
				// System.out.println("Obtain "+returned);
			}
		}
		if (returned == null) {
			logger.warning("Null value when executing setBindingValue() for " + getStringRepresentation());
			return null;
		}

		return returned;
	}

	@Override
	public void setBindingValue(Object value, BindingEvaluationContext context) {
		if (!isBindingValid()) {
			return;
		}

		if (!isSettable()) {
			return;
		}

		// System.out.println("Sets value: "+value);
		// System.out.println("Binding: "+getStringRepresentation());

		Object returned = context.getValue(_bindingVariable);

		// System.out.println("For variable "+_bindingVariable+" object is "+returned);

		try {
			for (BindingPathElement element : getBindingPath()) {
				if (element != getBindingPath().lastElement()) {
					// System.out.println("Apply "+element);
					returned = element.getBindingValue(returned, context);
					/*if (element instanceof KeyValueProperty) {
					returned = KeyValueDecoder.objectForKey(returned,((KeyValueProperty)element).getName());
					}
					else if (element instanceof MethodCall) {
					returned = ((MethodCall)element).evaluateBinding(returned, context);
					}
					else {
					logger.warning("Unexpected: "+element);
					}*/
					if (returned == null) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Null value when executing setBindingValue() for " + getStringRepresentation());
						}
						return;
					}
					// System.out.println("Obtain "+returned);
				}
			}
			if (returned == null) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Null value when executing setBindingValue() for " + getStringRepresentation());
				}
				return;
			}

			// logger.info("returned="+returned);
			// logger.info("lastElement="+getBindingPath().lastElement());

			if (getBindingPath().lastElement().isSettable()) {
				getBindingPath().lastElement().setBindingValue(value, returned, context);
			} else {
				logger.warning("Binding " + getStringRepresentation() + " is not settable");
			}
		} catch (InvalidObjectSpecificationException e) {
			logger.warning("InvalidObjectSpecificationException raised while evaluating SET " + getStringRepresentation() + " : "
					+ e.getMessage());
			// System.out.println("returned="+returned);
			// System.out.println("value="+value);
			// System.out.println("((KeyValueProperty)getBindingPath().lastElement()).getName()="+((KeyValueProperty)getBindingPath().lastElement()).getName());
		}

	}

	@Override
	public boolean isSettable() {
		if (getBindingPath().size() == 0) {
			return _bindingVariable.isSettable();
		} else {
			return getBindingPath().lastElement().isSettable();
		}

		/*	if (getBindingPath().lastElement() instanceof KeyValueProperty) {
			return ((KeyValueProperty)getBindingPath().lastElement()).isSettable();
		}
		if (getBindingPath().lastElement() instanceof MethodCall) {
			// TODO MethodCall with all other params as constants are also settable !!!!
			logger.warning("Please implement me !!!");
			return true;
		}
		else {
			return false;
		}*/
	}

}