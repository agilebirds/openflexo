package org.openflexo.antar.binding;

import java.lang.reflect.Type;
import java.util.Observable;

public abstract class KeyValueBindingImpl extends Observable implements BindingPathElement {

	@Override
	public abstract Type getType();
 
}
