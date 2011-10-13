package org.openflexo.antar.binding;

import java.lang.reflect.Type;

public interface BindingVariable<E extends Bindable,T> extends BindingPathElement<E,T> {

    public E getContainer();
    
    public String getVariableName();

	@Override
	public Type getType();
}
