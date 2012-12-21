package org.openflexo.model;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ModelMethod {

	private String methodName;

	private Class<?>[] parameterTypes;

	public ModelMethod(Method method) {
		methodName = method.getName();
		parameterTypes = method.getParameterTypes();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (methodName == null ? 0 : methodName.hashCode());
		result = prime * result + Arrays.hashCode(parameterTypes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ModelMethod other = (ModelMethod) obj;
		if (methodName == null) {
			if (other.methodName != null) {
				return false;
			}
		} else if (!methodName.equals(other.methodName)) {
			return false;
		}
		if (!Arrays.equals(parameterTypes, other.parameterTypes)) {
			return false;
		}
		return true;
	}

}
