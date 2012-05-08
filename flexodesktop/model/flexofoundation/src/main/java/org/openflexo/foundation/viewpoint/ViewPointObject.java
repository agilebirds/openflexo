/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.viewpoint;

import org.openflexo.antar.binding.AbstractBinding;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModelChanged;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.foundation.viewpoint.inspector.InspectorBindingAttribute;

public abstract class ViewPointObject extends ViewPointLibraryObject implements Bindable {

	public abstract ViewPoint getViewPoint();

	@Override
	public ViewPointLibrary getViewPointLibrary() {
		if (getViewPoint() != null) {
			return getViewPoint().getViewPointLibrary();
		}
		return null;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (getViewPoint() != null) {
			getViewPoint().setIsModified();
		}
	}

	public void notifyBindingChanged(ViewPointDataBinding binding) {
	}

	public void notifyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
	}

	public void notifyChange(InspectorBindingAttribute bindingAttribute, AbstractBinding oldValue, AbstractBinding newValue) {
		getPropertyChangeSupport().firePropertyChange(bindingAttribute.toString(), oldValue, newValue);
	}

	@Override
	public BindingFactory getBindingFactory() {
		return getViewPoint().getBindingFactory();
	}

	public void notifyBindingModelChanged() {
		getPropertyChangeSupport().firePropertyChange(BindingModelChanged.BINDING_MODEL_CHANGED, null, null);
	}

	public LocalizedDictionary getLocalizedDictionary() {
		return getViewPoint().getLocalizedDictionary();
	}

	@Deprecated
	public ViewPoint getCalc() {
		return getViewPoint();
	}

}
