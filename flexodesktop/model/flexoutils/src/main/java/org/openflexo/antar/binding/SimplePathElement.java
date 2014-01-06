package org.openflexo.antar.binding;

import java.lang.reflect.Type;

/**
 * Model a simple path element in a binding path, represented by a simple get/set access through a property
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

	@Override
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

	public final void setType(Type type) {
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SimplePathElement) {
			return getParent().equals(((SimplePathElement) obj).getParent())
					&& getPropertyName().equals(((SimplePathElement) obj).getPropertyName());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return getPropertyName().hashCode();
	}
}
