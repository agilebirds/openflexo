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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.toolbox.ToolBox;

public class MethodCall extends Observable implements ComplexPathElement<Object> {

	static final Logger logger = Logger.getLogger(MethodCall.class.getPackage().getName());

	protected AbstractBinding _owner;
	private Type _declaringType;
	private Method _method;
	private Method _setMethod;
	private final Vector<MethodCallArgument> _args;

	public MethodCall(AbstractBinding owner) {
		super();
		_owner = owner;
		_method = null;
		_args = new Vector<MethodCallArgument>();
	}

	public MethodCall(AbstractBinding owner, Method method, Type declaringType) {
		this(owner);
		_declaringType = declaringType;
		setMethod(method);
	}

	@Override
	public Type getType() {
		if (_method != null) {
			return _method.getGenericReturnType();
		}
		return null;
	}

	@Override
	public Class getDeclaringClass() {
		if (_method != null) {
			return _method.getDeclaringClass();
		}
		return null;
	}

	@Override
	public String getSerializationRepresentation() {
		if (_method == null) {
			return "null";
		}
		String returned = _method.getName();
		returned += "(";
		boolean isFirst = true;
		if (_method.getGenericParameterTypes() != null) {
			for (MethodCallArgument arg : _args) {
				returned += (isFirst ? "" : ",") + (arg.getBinding() != null ? arg.getBinding().getStringRepresentation() : "");
				isFirst = false;
			}
		}
		returned += ")";
		return returned;
	}

	public MethodDefinition getMethodDefinition() {
		return MethodDefinition.getMethodDefinition(_declaringType, _method);
	}

	public Method getMethod() {
		return _method;
	}

	/**
	 * 
	 * @param method
	 */
	public void setMethod(Method method) {
		if (method != _method) {
			_method = method;
			_setMethod = null;
			_args.clear();
			int argNb = 0;
			for (Type paramType : method.getGenericParameterTypes()) {
				_args.add(new MethodCallArgument("arg" + argNb++, TypeUtils.makeInstantiatedType(paramType, _declaringType)));
			}
			updateSetMethod();
		}
	}

	protected void updateSetMethod() {
		if (_method != null) {
			boolean settable = getArgs().size() > 0;
			if (settable) {
				for (MethodCallArgument arg : getArgs()) {
					settable &= arg.getBinding() != null && arg.getBinding().isStaticValue();
				}
			}
			if (settable) {
				Class[] parameterTypes = new Class[_args.size() + 1];
				int i = 0;
				parameterTypes[i++] = _method.getReturnType();
				for (; i <= _args.size(); i++) {
					parameterTypes[i] = TypeUtils.getBaseClass(_args.get(i - 1).getType());
				}
				List<String> methodNames = new ArrayList<String>();
				String methodName = _method.getName();
				if (methodName.startsWith("get")) {
					methodNames.add("set" + methodName.substring(3));
					methodNames.add("_set" + methodName.substring(3));
				} else if (methodName.startsWith("_get")) {
					methodNames.add("_set" + methodName.substring(4));
					methodNames.add("set" + methodName.substring(4));
				} else if (methodName.startsWith("is")) {
					methodNames.add("set" + methodName.substring(2));
					methodNames.add("_set" + methodName.substring(2));
					methodNames.add("set" + ToolBox.capitalize(methodName));
					methodNames.add("_set" + ToolBox.capitalize(methodName));
				} else if (methodName.startsWith("_is")) {
					methodNames.add("_set" + methodName.substring(2));
					methodNames.add("set" + methodName.substring(2));
					methodNames.add("set" + ToolBox.capitalize(methodName, true));
					methodNames.add("_set" + ToolBox.capitalize(methodName, true));
				}
				methodNames.add("set" + methodName);
				methodNames.add("_set" + methodName);
				methodNames.add("set" + ToolBox.capitalize(methodName, true));
				methodNames.add("_set" + ToolBox.capitalize(methodName, true));

				for (String string : methodNames) {
					try {
						Method method2 = getDeclaringClass().getMethod(string, parameterTypes);
						_setMethod = method2;
						break;
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
					}
				}
			}
		}
	}

	public class MethodCallParamBindingDefinition extends BindingDefinition {
		private final Type _paramType;

		public MethodCallParamBindingDefinition(String name, Type paramType) {
			super(name, paramType, BindingDefinitionType.GET, true);
			_paramType = paramType;
		}

		public Type getParamType() {
			return _paramType;
		}
	}

	public Vector<MethodCallArgument> getArgs() {
		return _args;
	}

	public MethodCallArgument argumentForParam(String paramName) {
		if (paramName == null) {
			return null;
		}
		for (MethodCallArgument arg : _args) {
			if (paramName.equals(arg.getName())) {
				return arg;
			}
		}
		return null;
	}

	public void setBindingValueForParam(AbstractBinding binding, String paramName) {
		MethodCallArgument arg = argumentForParam(paramName);

		if (arg == null) {
			logger.warning("Could not find argument matching param " + paramName);
			return;
		} else {
			binding.setOwner(_owner);
			binding.setBindingDefinition(arg.getBindingDefinition());
			arg.setBinding(binding);
			updateSetMethod();
		}
	}

	@Override
	public boolean isBindingValid() {
		for (MethodCallArgument arg : _args) {
			if (arg.getBinding() == null || !arg.getBinding().isBindingValid()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getLabel() {
		return getMethodDefinition().getLabel();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return getMethodDefinition().getTooltipText(resultingType);
	}

	@Override
	public boolean isSettable() {
		return _setMethod != null;
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) {
		Object[] args = new Object[_args.size()];
		int i = 0;
		// System.out.println("Invoke method "+_method+" on class "+_method.getDeclaringClass());

		for (MethodCallArgument a : _args) {
			try {
				args[i] = TypeUtils.castTo(a.getBinding().getBindingValue(context), _method.getGenericParameterTypes()[i]);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
				args[i] = null;
			} catch (NullReferenceException e) {
				e.printStackTrace();
				args[i] = null;
			}
			// System.out.println("arg"+i+"="+args[i]+" of "+args[i].getClass().getSimpleName());
			i++;
		}
		try {
			return _method.invoke(target, args);
		} catch (IllegalArgumentException e) {
			StringBuffer warningMessage = new StringBuffer("While evaluating method " + _method + " exception occured: " + e.getMessage());
			warningMessage.append(", object = " + target);
			for (i = 0; i < _args.size(); i++) {
				warningMessage.append(", arg[" + i + "] = " + args[i]);
			}
			logger.warning(warningMessage.toString());
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			logger.info("InvocationTargetException while evaluating method " + _method + " with args: ");
			for (int j = 0; j < args.length; j++) {
				logger.info("arg " + j + " = " + args[j]);
			}
			e.printStackTrace();
			logger.info("Caused by:");
			e.getTargetException().printStackTrace();
		}
		return null;

	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) {
		// TODO MethodCall with all other params as constants are also settable
		// !!!!
		if (!isBindingValid()) {
			return;
		}
		if (!isSettable()) {
			return;
		}
		Object returned = value;

		// System.out.println("For variable "+_bindingVariable+" object is "+returned);

		try {
			if (returned == null) {
				return;
			}
			if (_setMethod != null) {
				int i = 0;
				Object[] args = new Object[_args.size() + 1];
				args[i++] = value;
				for (; i < _args.size() + 1; i++) {
					args[i] = _args.get(i - 1).getBinding().getBindingValue(context);
				}
				_setMethod.invoke(target, args);
			}
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	public class MethodCallArgument extends Observable {
		private final String paramName;
		private final Type paramType;
		private final MethodCallParamBindingDefinition bindingDefinition;
		private AbstractBinding binding;

		protected MethodCallArgument(String aName, Type aParamType) {
			paramName = aName;
			paramType = aParamType;
			bindingDefinition = new MethodCallParamBindingDefinition(aName, aParamType);
			binding = null;
		}

		public String getName() {
			return paramName;
		}

		public Type getType() {
			return bindingDefinition.getType();
		}

		public void setType(Type type) {
			// Not applicable
		}

		public AbstractBinding getBinding() {
			return binding;
		}

		public void setBinding(AbstractBinding aBinding) {
			this.binding = aBinding;
		}

		public MethodCallParamBindingDefinition getBindingDefinition() {
			return bindingDefinition;
		}

		@Override
		public String toString() {
			return "MethodCallArg:" + paramName + "/" + Integer.toHexString(hashCode());
		}
	}

}
