package org.flexo.model.impl;

import javassist.util.proxy.ProxyObject;

import org.flexo.model.FlexoModelObject;
import org.openflexo.model.factory.ModelEntity;
import org.openflexo.model.factory.ProxyMethodHandler;

public abstract class FlexoModelObjectImpl implements FlexoModelObject {

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

}
