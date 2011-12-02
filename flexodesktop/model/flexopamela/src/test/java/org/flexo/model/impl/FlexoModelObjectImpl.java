package org.flexo.model.impl;

import java.beans.PropertyChangeSupport;

import javassist.util.proxy.ProxyObject;

import org.flexo.model.FlexoModelObject;
import org.openflexo.model.factory.ModelEntity;
import org.openflexo.model.factory.ProxyMethodHandler;

public abstract class FlexoModelObjectImpl implements FlexoModelObject {

	private PropertyChangeSupport pcSupport;

	public FlexoModelObjectImpl() {
		pcSupport = new PropertyChangeSupport(this);
	}

	@Override
	public String deriveName() {
		return getName() + "1";
	}

	@Override
	public String toString() {
		if (this instanceof ProxyObject) {
			ProxyMethodHandler handler = (ProxyMethodHandler) ((ProxyObject) this).getHandler();
			ModelEntity factory = handler.getModelEntity();
			return factory.getImplementedInterface().getSimpleName() + "(id=" + getFlexoID() + "," + getName() + ")";
		}
		return super.toString();
	}

	@Override
	public void firePropertyChanged(String propertyIdentifier, Object oldValue, Object newValue) {
		pcSupport.firePropertyChange(propertyIdentifier, oldValue, newValue);
		// System.out.println(">>>> Fired PropertyChanged("+propertyIdentifier+",old="+oldValue+",new="+newValue);
	}

}
