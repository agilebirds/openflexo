package org.openflexo.antar.binding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Observable;

public abstract class KeyValueBindingImpl extends Observable implements BindingPathElement {

	public abstract Type getType();

	public BindingPathElement getBindingPathElement(String propertyName)
	{
		if (getType() != null) {
			if (TypeUtils.getBaseClass(getType()) == null) {
				return null;
			}               
			Type currentType = getType();
			if (currentType instanceof Class
					&& ((Class)currentType).isPrimitive()) {
				currentType = TypeUtils.fromPrimitive((Class)currentType);
			}
			return KeyValueLibrary.getKeyValueProperty(currentType, propertyName);			
		}
		return null;
	}
	
	public List<? extends BindingPathElement> getAccessibleBindingPathElements()
	{
		if (getType() != null) {
			if (TypeUtils.getBaseClass(getType()) == null) {
				return null;
			}               
			Type currentType = getType();
			if (currentType instanceof Class
					&& ((Class)currentType).isPrimitive()) {
				currentType = TypeUtils.fromPrimitive((Class)currentType);
			}
			return KeyValueLibrary.getAccessibleProperties(currentType);			
		}
		return null;
		
	}

	public List<? extends BindingPathElement> getAccessibleCompoundBindingPathElements()
	{
		if (getType() != null) {
			if (TypeUtils.getBaseClass(getType()) == null) {
				return null;
			}               
			Type currentType = getType();
			if (currentType instanceof Class
					&& ((Class)currentType).isPrimitive()) {
				currentType = TypeUtils.fromPrimitive((Class)currentType);
			}
			return KeyValueLibrary.getAccessibleMethods(currentType);			
		}
		return null;
		
	}
}
