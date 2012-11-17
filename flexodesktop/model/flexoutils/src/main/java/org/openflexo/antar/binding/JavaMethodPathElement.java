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
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;

/**
 * Modelize a java call which is a call to a method and with some arguments
 * 
 * @author sylvain
 * 
 */
public class JavaMethodPathElement extends FunctionPathElement {

	static final Logger logger = Logger.getLogger(JavaMethodPathElement.class.getPackage().getName());

	private MethodDefinition method;

	public JavaMethodPathElement(BindingPathElement parent, String methodName, Type type, List<DataBinding<?>> args) {
		super(parent, methodName, type, args);
		method = retrieveMethod();
	}

	public JavaMethodPathElement(BindingPathElement parent, MethodDefinition method, List<DataBinding<?>> args) {
		super(parent, method.getMethodName(), method.getMethod().getGenericReturnType(), args);
		this.method = method;
	}

	public String getMethodName() {
		return super.getFunctionName();
	}

	@Override
	public Type getType() {
		if (method != null) {
			return TypeUtils.makeInstantiatedType(method.getMethod().getGenericReturnType(), getParent().getType());
		}
		return super.getType();
	}

	private MethodDefinition retrieveMethod() {
		Vector<Method> possiblyMatchingMethods = new Vector<Method>();
		Class<?> typeClass = TypeUtils.getBaseClass(getType());
		Method[] allMethods = typeClass.getMethods();
		for (Method method : allMethods) {
			if (method.getName().equals(getMethodName()) && method.getGenericParameterTypes().length == getArguments().size()) {
				possiblyMatchingMethods.add(method);
			}
		}
		if (possiblyMatchingMethods.size() > 1) {
			logger.warning("Please implement disambiguity here");
			// Return the first one
			// TODO: try to find the best one
			return MethodDefinition.getMethodDefinition(getType(), possiblyMatchingMethods.get(0));
		} else if (possiblyMatchingMethods.size() == 1) {
			return MethodDefinition.getMethodDefinition(getType(), possiblyMatchingMethods.get(0));
		} else {
			return null;
		}
	}

	@Override
	public String getLabel() {
		return method.getLabel();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return method.getTooltipText(resultingType);
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) {
		Object[] args = new Object[getArguments().size()];
		int i = 0;

		for (DataBinding<?> a : getArguments()) {
			args[i] = TypeUtils.castTo(a.getBindingValue(context), method.getMethod().getGenericParameterTypes()[i]);
			i++;
		}
		try {
			return method.getMethod().invoke(target, args);
		} catch (IllegalArgumentException e) {
			StringBuffer warningMessage = new StringBuffer("While evaluating method " + method.getMethod() + " exception occured: "
					+ e.getMessage());
			warningMessage.append(", object = " + target);
			for (i = 0; i < getArguments().size(); i++) {
				warningMessage.append(", arg[" + i + "] = " + args[i]);
			}
			logger.warning(warningMessage.toString());
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			logger.info("InvocationTargetException while evaluating method " + method.getMethod() + " with args: ");
			for (int j = 0; j < args.length; j++) {
				logger.info("arg " + j + " = " + args[j]);
			}
			e.printStackTrace();
			logger.info("Caused by:");
			e.getTargetException().printStackTrace();
		}
		return null;

	}

}
