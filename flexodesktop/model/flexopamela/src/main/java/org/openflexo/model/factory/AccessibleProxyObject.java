package org.openflexo.model.factory;


public interface AccessibleProxyObject {

	public Object performSuperGetter(String propertyIdentifier);

	public void performSuperSetter(String propertyIdentifier, Object value);

	public void performSuperAdder(String propertyIdentifier, Object value);

	public void performSuperRemover(String propertyIdentifier, Object value);

	public void performSuperDeleter();

	public Object performSuperFinder(Object value);

}
