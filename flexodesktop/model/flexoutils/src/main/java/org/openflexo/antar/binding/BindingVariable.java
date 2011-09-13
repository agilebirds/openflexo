package org.openflexo.antar.binding;

import java.lang.reflect.Type;

public interface BindingVariable extends BindingPathElement {

    public Bindable getContainer();
    
    public String getVariableName();

	public Type getType();
}
