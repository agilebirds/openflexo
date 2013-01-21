package org.openflexo.antar.binding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Observable;

import org.openflexo.antar.binding.Function.FunctionArgument;

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
	public static class FunctionArgument extends Observable {
	
		private Function function;
		private String argumentName;
		private Type argumentType;
	
		public FunctionArgument(Function function, String argumentName, Type argumentType) {
			super();
			this.function = function;
			this.argumentName = argumentName;
			this.argumentType = argumentType;
		}
	
		public Function getFunction() {
			return function;
		}
	
		public String getArgumentName() {
			return argumentName;
		}
	
		public void setArgumentName(String argumentName) {
			this.argumentName = argumentName;
		}
	
		public Type getArgumentType() {
			return argumentType;
		}
	
		public void setArgumentType(Type argumentType) {
			this.argumentType = argumentType;
		}
	
	}

	public String getName();

	public Type getReturnType();

	public List<Function.FunctionArgument> getArguments();
}