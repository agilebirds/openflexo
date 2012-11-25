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

import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;

/**
 * Modelize a java call which is a call to a method and with some arguments
 * 
 * @author sylvain
 * 
 */
public class JavaMethodPathElement extends FunctionPathElement {

	static final Logger logger = Logger.getLogger(JavaMethodPathElement.class.getPackage().getName());

	public JavaMethodPathElement(BindingPathElement parent, String methodName, List<DataBinding<?>> args) {
		super(parent, retrieveMethod(parent.getType(), methodName, args), args);

		if (getMethodDefinition() == null) {
			logger.warning("Cannot retrieve method " + methodName + " with " + args.size() + " parameters from " + parent.getType());
		} else {
			setType(getMethodDefinition().getMethod().getGenericReturnType());
		}
	}

	public JavaMethodPathElement(BindingPathElement parent, MethodDefinition method, List<DataBinding<?>> args) {
		super(parent, method, args);
	}

	public MethodDefinition getMethodDefinition() {
		return getFunction();
	}

	@Override
	public MethodDefinition getFunction() {
		return (MethodDefinition) super.getFunction();
	}

	public String getMethodName() {
		return getMethodDefinition().getMethod().getName();
	}

	@Override
	public Type getType() {
		if (getMethodDefinition() != null) {
			return TypeUtils.makeInstantiatedType(getMethodDefinition().getMethod().getGenericReturnType(), getParent().getType());
		}
		return super.getType();
	}

	protected static MethodDefinition retrieveMethod(Type parentType, String methodName, List<DataBinding<?>> args) {
		Vector<Method> possiblyMatchingMethods = new Vector<Method>();
		Class<?> typeClass = TypeUtils.getBaseClass(parentType);
		Method[] allMethods = typeClass.getMethods();
		for (Method method : allMethods) {
			if (method.getName().equals(methodName) && method.getGenericParameterTypes().length == args.size()) {
				possiblyMatchingMethods.add(method);
			}
		}
		if (possiblyMatchingMethods.size() > 1) {
			logger.warning("Please implement disambiguity here");
			// Return the first one
			// TODO: try to find the best one
			return MethodDefinition.getMethodDefinition(parentType, possiblyMatchingMethods.get(0));
		} else if (possiblyMatchingMethods.size() == 1) {
			return MethodDefinition.getMethodDefinition(parentType, possiblyMatchingMethods.get(0));
		} else {
			return null;
		}
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
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {

		System.out.println("evaluate " + getMethodDefinition().getSignature() + " for " + target);

		Object[] args = new Object[getArguments().size()];
		int i = 0;

		for (FunctionArgument a : getArguments()) {
			args[i] = TypeUtils.castTo(a.getValue().getBindingValue(context),
					getMethodDefinition().getMethod().getGenericParameterTypes()[i]);
			i++;
		}
		try {
			return getMethodDefinition().getMethod().invoke(target, args);
		} catch (IllegalArgumentException e) {
			StringBuffer warningMessage = new StringBuffer("While evaluating method " + getMethodDefinition().getMethod()
					+ " exception occured: " + e.getMessage());
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
			logger.info("InvocationTargetException while evaluating method " + getMethodDefinition().getMethod() + " with args: ");
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
