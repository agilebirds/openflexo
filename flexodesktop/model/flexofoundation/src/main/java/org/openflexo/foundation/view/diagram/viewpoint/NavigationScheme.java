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
package org.openflexo.foundation.view.diagram.viewpoint;

import java.lang.reflect.InvocationTargetException;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.AbstractActionScheme;
import org.openflexo.foundation.viewpoint.VirtualModel;

public class NavigationScheme extends AbstractActionScheme implements DiagramEditionScheme {

	private DataBinding<Object> targetObject;

	public NavigationScheme(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	public DataBinding<Object> getTargetObject() {
		if (targetObject == null) {
			targetObject = new DataBinding<Object>(this, FlexoModelObject.class, BindingDefinitionType.GET);
			targetObject.setBindingName("targetObject");
		}
		return targetObject;
	}

	public void setTargetObject(DataBinding<Object> targetObject) {
		if (targetObject != null) {
			targetObject.setOwner(this);
			targetObject.setBindingName("targetObject");
			targetObject.setDeclaredType(FlexoModelObject.class);
			targetObject.setBindingDefinitionType(BindingDefinitionType.GET);
		}
		this.targetObject = targetObject;
	}

	public FlexoModelObject evaluateTargetObject(EditionPatternInstance editionPatternInstance) {
		if (getTargetObject().isValid()) {
			try {
				return (FlexoModelObject) getTargetObject().getBindingValue(editionPatternInstance);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
