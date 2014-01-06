package org.openflexo.antar.binding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Observable;

/**
 * Implemented by all classes which defines a function in the context of data binding
 * 
 * @author sylvain
 * 
 */
public interface Function {

	/**
	 * Implementation of an argument of a {@link Function}
	 * 
	 * @author sylvain
	 * 
	 */
	public static interface FunctionArgument {

		public Function getFunction();

		public String getArgumentName();

		public Type getArgumentType();

	}

	/**
	 * Implementation of an argument of a {@link Function}
	 * 
	 * @author sylvain
	 * 
	 */
	public static class DefaultFunctionArgument extends Observable implements FunctionArgument {

		private Function function;
		private String argumentName;
		private Type argumentType;

		public DefaultFunctionArgument(Function function, String argumentName, Type argumentType) {
			super();
			this.function = function;
			this.argumentName = argumentName;
			this.argumentType = argumentType;
		}

		@Override
		public Function getFunction() {
			return function;
		}

		@Override
		public String getArgumentName() {
			return argumentName;
		}

		public void setArgumentName(String argumentName) {
			this.argumentName = argumentName;
		}

		@Override
		public Type getArgumentType() {
			return argumentType;
		}

		public void setArgumentType(Type argumentType) {
			this.argumentType = argumentType;
		}

		@Override
		public String toString() {
			return getArgumentName() + "/" + TypeUtils.simpleRepresentation(getArgumentType());
		}
	}

	public String getName();

	public Type getReturnType();

	public List<? extends FunctionArgument> getArguments();
}