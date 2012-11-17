package org.openflexo.antar.binding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;

/**
 * Modelize a compound path element in a binding path, which is the symbolic representation of a call to a function and with a given amount
 * of arguments
 * 
 * @author sylvain
 * 
 */
public abstract class FunctionPathElement implements BindingPathElement {

	private BindingPathElement parent;
	private String functionName;
	private Type type;
	private List<DataBinding<?>> arguments;

	public FunctionPathElement(BindingPathElement parent, String functionName, Type type, List<DataBinding<?>> args) {
		this.parent = parent;
		this.functionName = functionName;
		this.type = type;
		arguments = new Vector<DataBinding<?>>();
		if (args != null) {
			arguments.addAll(args);
		}
	}

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

	@Override
	public String getSerializationRepresentation() {
		return getFunctionName();
	}

	public List<DataBinding<?>> getArguments() {
		return arguments;
	}
}
