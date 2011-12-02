package org.openflexo.antar.binding;

import java.lang.reflect.Type;

public interface BindingVariable<T> extends BindingPathElement<T> {

	public Bindable getContainer();

	public String getVariableName();

	@Override
	public Type getType();
}
