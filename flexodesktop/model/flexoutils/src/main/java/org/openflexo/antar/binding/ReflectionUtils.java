package org.openflexo.antar.binding;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

	private ReflectionUtils() {
	}

	/**
	 * Returns all methods that are overridden by the specified method. If no method is overridden, it returns an empty list.
	 * 
	 * @param method
	 *            the method to check for.
	 * @return all methods that are overridden by the specified method
	 */
	public static List<Method> getOverridenMethods(Method method) {
		return appendOverriddenMethods(new ArrayList<Method>(), method.getDeclaringClass(), method);
	}

	private static List<Method> appendOverriddenMethods(List<Method> methods, Class<?> klass, Method method) {
		if (klass == null) {
			return methods;
		}
		if (klass != method.getDeclaringClass()) {
			try {
				Method m = klass.getMethod(method.getName(), method.getParameterTypes());
				methods.add(m);
			} catch (SecurityException e) {
			} catch (NoSuchMethodException e) {
			}
		}
		appendOverriddenMethods(methods, klass.getSuperclass(), method);
		for (Class<?> superInterface : klass.getInterfaces()) {
			appendOverriddenMethods(methods, superInterface, method);
		}
		return methods;
	}
}
