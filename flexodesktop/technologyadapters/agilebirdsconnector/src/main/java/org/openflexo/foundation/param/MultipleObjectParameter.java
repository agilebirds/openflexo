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
package org.openflexo.foundation.param;

import java.util.Vector;

import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.inspector.model.ParamModel;
import org.openflexo.inspector.widget.DenaliWidget;

public class MultipleObjectParameter<E extends AgileBirdsObject> extends ParameterDefinition<Vector<E>> {

	private ParamModel selectabilityParams;
	private ParamModel visibilityParams;

	public MultipleObjectParameter(String name, String label, Vector<E> defaultValue) {
		super(name, label, defaultValue);
		addParameter("className", "org.openflexo.components.widget.MultipleObjectInspectorWidget");
	}

	public void setRootObject(String path) {
		addParameter("rootObject", path);
	}

	@Override
	public String getWidgetName() {
		return DenaliWidget.CUSTOM;
	}

	public void addSelectableElements(String elementType) {
		if (selectabilityParams == null) {
			selectabilityParams = addParameter("isSelectable", null);
		}
		selectabilityParams.parameters.put(elementType, new ParamModel(elementType, "true"));
	}

	public void defineVisibility(String elementType, String visibility) {
		if (visibilityParams == null) {
			visibilityParams = addParameter("visibility", null);
		}
		visibilityParams.parameters.put(elementType, new ParamModel(elementType, visibility));
	}
}
