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
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;

public class MethodCall extends Observable implements
		ComplexPathElement<Object> {

	static final Logger logger = Logger.getLogger(MethodCall.class.getPackage()
			.getName());

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
				returned += (isFirst ? "" : ",")
						+ (arg.getBinding() != null ? arg.getBinding()
								.getStringRepresentation() : "");
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
			_args.clear();
			int argNb = 0;
			for (Type paramType : method.getGenericParameterTypes()) {
				_args.add(new MethodCallArgument("arg" + argNb++, TypeUtils
						.makeInstantiatedType(paramType, _declaringType)));
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

	public void setBindingValueForParam(AbstractBinding binding,
			String paramName) {
		MethodCallArgument arg = argumentForParam(paramName);

		if (arg == null) {
			logger.warning("Could not find argument matching param "
					+ paramName);
			return;
		} else {
			binding.setOwner(_owner);
			binding.setBindingDefinition(arg.getBindingDefinition());
			arg.setBinding(binding);
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
		// TODO MethodCall with all other params as constants are also settable
		// !!!!
		return false;
	}

	@Override
	public Object getBindingValue(Object target,
			BindingEvaluationContext context) {
		Object[] args = new Object[_args.size()];
		int i = 0;
		// System.out.println("Invoke method "+_method+" on class "+_method.getDeclaringClass());

		for (MethodCallArgument a : _args) {
			args[i] = TypeUtils.castTo(a.getBinding().getBindingValue(context),
					_method.getGenericParameterTypes()[i]);
			// System.out.println("arg"+i+"="+args[i]+" of "+args[i].getClass().getSimpleName());
			i++;
		}
		try {
			return _method.invoke(target, args);
		} catch (IllegalArgumentException e) {
			logger.warning("While evaluating method " + _method
					+ " exception occured: " + e.getMessage());
			for (i = 0; i < _args.size(); i++) {
				logger.info("arg[" + i + "] = " + args[i]);
			}
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			logger.info("InvocationTargetException while evaluating method "
					+ _method + " with args: ");
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
	public void setBindingValue(Object value, Object target,
			BindingEvaluationContext context) {
		// TODO MethodCall with all other params as constants are also settable
		// !!!!
		logger.warning("Please implement me !!!");
	}

	public class MethodCallArgument extends Observable {
		private final String paramName;
		private final Type paramType;
		private final MethodCallParamBindingDefinition bindingDefinition;
		private AbstractBinding binding;

		protected MethodCallArgument(String aName, Type aParamType) {
			paramName = aName;
			paramType = aParamType;
			bindingDefinition = new MethodCallParamBindingDefinition(aName,
					aParamType);
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
			return "MethodCallArg:" + paramName + "/"
					+ Integer.toHexString(hashCode());
		}
	}

}
