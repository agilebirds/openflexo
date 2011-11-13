package org.openflexo.model.factory;

public interface ObservableObject {

	public void firePropertyChanged(String propertyIdentifier, Object oldValue, Object newValue);
}
