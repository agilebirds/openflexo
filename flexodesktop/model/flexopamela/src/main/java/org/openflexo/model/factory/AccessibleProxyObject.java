package org.openflexo.model.factory;

import org.openflexo.toolbox.HasPropertyChangeSupport;

public interface AccessibleProxyObject extends HasPropertyChangeSupport {

	public Object performSuperGetter(String propertyIdentifier);

	public void performSuperSetter(String propertyIdentifier, Object value);

	public void performSuperAdder(String propertyIdentifier, Object value);

	public void performSuperRemover(String propertyIdentifier, Object value);

	public void performSuperDeleter();

	public void performSuperSetModified(boolean modified);

	public Object performSuperFinder(String finderIdentifier, Object value);

	public boolean isSerializing();

	public boolean isDeserializing();

	public boolean isModified();

	public void setModified(boolean modified);

}
