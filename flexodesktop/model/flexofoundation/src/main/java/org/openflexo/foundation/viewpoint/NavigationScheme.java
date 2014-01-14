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

import java.lang.reflect.InvocationTargetException;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;

@FIBPanel("Fib/NavigationSchemePanel.fib")
@ModelEntity
@ImplementationClass(NavigationScheme.NavigationSchemeImpl.class)
@XMLElement
public interface NavigationScheme extends AbstractActionScheme{

@PropertyIdentifier(type=DataBinding.class)
public static final String TARGET_OBJECT_KEY = "targetObject";

@Getter(value=TARGET_OBJECT_KEY)
@XMLAttribute
public DataBinding getTargetObject();

@Setter(TARGET_OBJECT_KEY)
public void setTargetObject(DataBinding targetObject);


public static abstract  class NavigationSchemeImpl extends AbstractActionSchemeImpl implements NavigationScheme
{

	private DataBinding<Object> targetObject;

	public NavigationSchemeImpl() {
		super();
	}

	public DataBinding<Object> getTargetObject() {
		if (targetObject == null) {
			targetObject = new DataBinding<Object>(this, FlexoObject.class, BindingDefinitionType.GET);
			targetObject.setBindingName("targetObject");
		}
		return targetObject;
	}

	public void setTargetObject(DataBinding<Object> targetObject) {
		if (targetObject != null) {
			targetObject.setOwner(this);
			targetObject.setBindingName("targetObject");
			targetObject.setDeclaredType(FlexoObject.class);
			targetObject.setBindingDefinitionType(BindingDefinitionType.GET);
		}
		this.targetObject = targetObject;
	}

	public FlexoObject evaluateTargetObject(EditionPatternInstance editionPatternInstance) {
		if (getTargetObject().isValid()) {
			try {
				return (FlexoObject) getTargetObject().getBindingValue(editionPatternInstance);
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
}
