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
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.foundation.viewpoint.inspector.InspectorBindingAttribute;

public class NavigationScheme extends AbstractActionScheme {

	private ViewPointDataBinding targetDiagram;

	public static enum NavigationSchemeBindingAttribute implements InspectorBindingAttribute {
		targetDiagram
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

	private BindingDefinition TARGET_DIAGRAM = new BindingDefinition("targetDiagram", View.class, BindingDefinitionType.GET, false);

	public BindingDefinition getTargetDiagramBindingDefinition() {
		return TARGET_DIAGRAM;
	}

	public ViewPointDataBinding getTargetDiagram() {
		if (targetDiagram == null) {
			targetDiagram = new ViewPointDataBinding(this, NavigationSchemeBindingAttribute.targetDiagram,
					getTargetDiagramBindingDefinition());
		}
		return targetDiagram;
	}

	public void setTargetDiagram(ViewPointDataBinding targetDiagram) {
		targetDiagram.setOwner(this);
		targetDiagram.setBindingAttribute(NavigationSchemeBindingAttribute.targetDiagram);
		targetDiagram.setBindingDefinition(getTargetDiagramBindingDefinition());
		this.targetDiagram = targetDiagram;
	}

	public View evaluateTargetDiagram(EditionPatternReference editionPatternReference) {
		if (getTargetDiagram().isValid()) {
			return (View) getTargetDiagram().getBindingValue(editionPatternReference);
		}
		return null;
	}

}
