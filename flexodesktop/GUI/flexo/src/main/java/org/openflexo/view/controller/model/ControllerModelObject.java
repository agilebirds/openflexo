package org.openflexo.view.controller.model;

import java.beans.PropertyChangeSupport;

import org.openflexo.toolbox.HasPropertyChangeSupport;

public abstract class ControllerModelObject implements HasPropertyChangeSupport {

	public static final String DELETED = "deleted";
	private PropertyChangeSupport propertyChangeSupport;

	private boolean deleted = false;

	public ControllerModelObject() {
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public void delete() {
		deleted = true;
		getPropertyChangeSupport().firePropertyChange(DELETED, false, true);
	}

	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public final PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	@Override
	public String getDeletedProperty() {
		return DELETED;
	}

}
