package org.openflexo.antar.binding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Observable;
import java.util.Vector;

/**
 * Modelize a compound path element in a binding path, which is the symbolic representation of a call to a function and with a given amount
 * of arguments
 * 
 * @author sylvain
 * 
 */
public abstract class FunctionPathElement extends Observable implements BindingPathElement {

	private BindingPathElement parent;
	private String functionName;
	private Type type;
	private List<FunctionArgument> arguments;

	public class FunctionArgument extends Observable {

		private String argumentName;
		private Type argumentType;
		private DataBinding<?> value;

		public FunctionArgument(String argumentName, Type argumentType, DataBinding<?> value) {
			super();
			this.argumentName = argumentName;
			this.argumentType = argumentType;
			this.value = value;
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

		public DataBinding<?> getValue() {
			return value;
		}

		public void setValue(DataBinding<?> value) {
			this.value = value;
		}
	}

	public FunctionPathElement(BindingPathElement parent, String functionName, Type type, List<DataBinding<?>> args) {
		this.parent = parent;
		this.functionName = functionName;
		this.type = type;
		arguments = new Vector<FunctionArgument>();
		if (args != null) {
			int i = 0;
			for (DataBinding<?> v : args) {
				FunctionArgument arg = new FunctionArgument("arg" + i, v.getAnalyzedType(), v);
				arguments.add(arg);
				i++;
			}
		}
	}

	@Override
	public BindingPathElement getParent() {
		return parent;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String propertyName) {
		this.functionName = propertyName;
	}

	@Override
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	private String serializationRepresentation = null;

	@Override
	public String getSerializationRepresentation() {
		if (serializationRepresentation == null) {
			StringBuffer returned = new StringBuffer();
			returned.append(getFunctionName());
			returned.append("(");
			boolean isFirst = true;
			for (FunctionArgument a : getArguments()) {
				returned.append((isFirst ? "" : ",") + a.getValue());
			}
			returned.append(")");
			serializationRepresentation = returned.toString();
		}
		return serializationRepresentation;
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	public List<FunctionArgument> getArguments() {
		return arguments;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FunctionPathElement) {
			return getSerializationRepresentation().equals(((FunctionPathElement) obj).getSerializationRepresentation());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return getSerializationRepresentation().hashCode();
	}

}