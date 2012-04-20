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

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.foundation.viewpoint.inspector.InspectorBindingAttribute;

public class NavigationScheme extends AbstractActionScheme {

	private ViewPointDataBinding targetObject;

	public static enum NavigationSchemeBindingAttribute implements InspectorBindingAttribute {
		targetObject
	}

	public NavigationScheme() {
		super();
	}

	@Override
	public EditionSchemeType getEditionSchemeType() {
		return EditionSchemeType.NavigationScheme;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.NAVIGATION_SCHEME_INSPECTOR;
	}

	private BindingDefinition TARGET_OBJECT = new BindingDefinition("targetObject", FlexoModelObject.class, BindingDefinitionType.GET,
			false);

	public BindingDefinition getTargetObjectBindingDefinition() {
		return TARGET_OBJECT;
	}

	public ViewPointDataBinding getTargetObject() {
		if (targetObject == null) {
			targetObject = new ViewPointDataBinding(this, NavigationSchemeBindingAttribute.targetObject, getTargetObjectBindingDefinition());
		}
		return targetObject;
	}

	public void setTargetObject(ViewPointDataBinding targetObject) {
		targetObject.setOwner(this);
		targetObject.setBindingAttribute(NavigationSchemeBindingAttribute.targetObject);
		targetObject.setBindingDefinition(getTargetObjectBindingDefinition());
		this.targetObject = targetObject;
	}

	public FlexoModelObject evaluateTargetObject(EditionPatternReference editionPatternReference) {
		if (getTargetObject().isValid()) {
			return (FlexoModelObject) getTargetObject().getBindingValue(editionPatternReference);
		}
		return null;
	}

}
