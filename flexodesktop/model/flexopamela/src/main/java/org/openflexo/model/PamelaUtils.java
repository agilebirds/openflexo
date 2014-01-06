package org.openflexo.model;

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PamelaUtils {

	public static boolean methodIsEquivalentTo(@Nonnull Method method, @Nullable Method to) {
		if (to == null) {
			return method == null;
		}
		return method.getName().equals(to.getName())/* && method.getReturnType().equals(to.getReturnType())*/
				&& Arrays.equals(method.getParameterTypes(), to.getParameterTypes());
	}

}
