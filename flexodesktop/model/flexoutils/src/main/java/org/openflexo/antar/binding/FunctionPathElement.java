package org.openflexo.antar.binding;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.logging.Logger;

/**
 * Modelize a compound path element in a binding path, which is the symbolic representation of a call to a function and with a given amount
 * of arguments
 * 
 * @author sylvain
 * 
 */
public abstract class FunctionPathElement extends Observable implements BindingPathElement {

	private static final Logger logger = Logger.getLogger(FunctionPathElement.class.getPackage().getName());

	private BindingPathElement parent;
	private Function function;
	private Type type;
	private HashMap<Function.FunctionArgument, DataBinding<?>> parameters;

	public FunctionPathElement(BindingPathElement parent, Function function, List<DataBinding<?>> paramValues) {
		this.parent = parent;
		this.function = function;
		parameters = new HashMap<Function.FunctionArgument, DataBinding<?>>();
		if (function == null) {
			logger.warning("FunctionPathElement called with null function");
		} else {
			this.type = function.getReturnType();
			if (paramValues != null) {
				int i = 0;
				for (Function.FunctionArgument arg : function.getArguments()) {
					if (i < paramValues.size()) {
						DataBinding<?> paramValue = paramValues.get(i);
						setParameter(arg, paramValue);
					}
					i++;
				}
			}
		}
	}

	public void instanciateParameters(Bindable bindable) {
		for (Function.FunctionArgument arg : function.getArguments()) {
			DataBinding<?> parameter = getParameter(arg);
			if (parameter == null) {
				parameter = new DataBinding<Object>(bindable, arg.getArgumentType(), DataBinding.BindingDefinitionType.GET);
				parameter.setBindingName(arg.getArgumentName());
				parameter.setUnparsedBinding("");
				setParameter(arg, parameter);
			} else {
				parameter.setOwner(bindable);
				parameter.setDeclaredType(arg.getArgumentType());
				parameter.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
		}
	}

	@Override
	public BindingPathElement getParent() {
		return parent;
	}

	public Function getFunction() {
		return function;
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
			if (getFunction() != null) {
				returned.append(getFunction().getName());
				returned.append("(");
				boolean isFirst = true;
				for (Function.FunctionArgument a : getFunction().getArguments()) {
					returned.append((isFirst ? "" : ",") + getParameter(a));
					isFirst = false;
				}
				returned.append(")");
			} else {
				returned.append("unknown_function()");
			}
			serializationRepresentation = returned.toString();
		}
		return serializationRepresentation;
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	public DataBinding<?> getParameter(Function.FunctionArgument argument) {
		return parameters.get(argument);
	}

	public void setParameter(Function.FunctionArgument argument, DataBinding<?> value) {
		serializationRepresentation = null;
		// System.out.println("setParameter " + argument + " for " + this + " with " + value);
		parameters.put(argument, value);
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

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + "/" + getSerializationRepresentation();
	}

}
