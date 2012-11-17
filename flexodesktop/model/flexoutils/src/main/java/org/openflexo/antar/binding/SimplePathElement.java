package org.openflexo.antar.binding;

import java.lang.reflect.Type;

/**
 * Modelize a simple path element in a binding path, represented by a simple get/set access through a property
 * 
 * @author sylvain
 * 
 */
public abstract class SimplePathElement implements BindingPathElement, SettableBindingPathElement {

	private BindingPathElement parent;
	private String propertyName;
	private Type type;

	public SimplePathElement(BindingPathElement parent, String propertyName, Type type) {
		this.parent = parent;
		this.propertyName = propertyName;
		this.type = type;
	}

	public BindingPathElement getParent() {
		return parent;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public boolean isSettable() {
		return true;
	}

	@Override
	public String getSerializationRepresentation() {
		return getPropertyName();
	}
}
