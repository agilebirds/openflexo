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
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.foundation.viewpoint.inspector.InspectorBindingAttribute;

public abstract class ViewPointObject extends ViewPointLibraryObject implements Bindable {

	public abstract ViewPoint getCalc();

	@Override
	public ViewPointLibrary getViewPointLibrary() {
		if (getCalc() != null)
			return getCalc().getViewPointLibrary();
		return null;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (getCalc() != null)
			getCalc().setIsModified();
	}

	public void notifyBindingChanged(ViewPointDataBinding binding) {
	}

	public void notifyChange(InspectorBindingAttribute bindingAttribute, AbstractBinding oldValue, AbstractBinding value) {
	}

	@Override
	public BindingFactory getBindingFactory() {
		return getCalc().getBindingFactory();
	}

}
