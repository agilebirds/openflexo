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
package org.openflexo.components.widget;

import org.openflexo.foundation.ie.IERegExp;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.AgileBirdsFlexoController;

/**
 * Widget allowing to select an OperationComponent while browing the component library
 * 
 * @author sguerin
 * 
 */
public class OperationComponentSelector extends AbstractComponentSelector<OperationComponentDefinition> {

	public OperationComponentSelector(OperationComponentDefinition operationComponent) {
		this(operationComponent != null ? operationComponent.getProject() : null, operationComponent);
	}

	public OperationComponentSelector(FlexoProject project, OperationComponentDefinition operationComponent) {
		super(project, operationComponent, OperationComponentDefinition.class);
	}

	public OperationComponentSelector(FlexoProject project, OperationComponentDefinition operationComponent, int cols) {
		super(project, operationComponent, OperationComponentDefinition.class, cols);
	}

	@Override
	public void newComponent() {
		FlexoComponentFolder folder = null;
		if (getSelectedObject() instanceof FlexoComponentFolder) {
			folder = (FlexoComponentFolder) getSelectedObject();
		} else if (getSelectedObject() instanceof ComponentDefinition) {
			folder = ((ComponentDefinition) getSelectedObject()).getFolder();
		} else {
			folder = getComponentLibrary().getRootFolder();
		}
		String newComponentName = AgileBirdsFlexoController.askForStringMatchingPattern(FlexoLocalization.localizedForKey("enter_a_component_name"),
				IERegExp.JAVA_CLASS_NAME_PATTERN,
				FlexoLocalization.localizedForKey("must_start_with_a_letter_followed_by_any_letter_or_number"));
		if (newComponentName == null) {
			return;
		}
		try {
			OperationComponentDefinition newComponent = new OperationComponentDefinition(newComponentName, getComponentLibrary(), folder,
					getProject());
			setEditedObject(newComponent);
		} catch (DuplicateResourceException e) {
			// Warns about the exception
			AgileBirdsFlexoController.notify(FlexoLocalization.localizedForKey("invalid_name_a_component_with_this_name_already_exists"));
		}

	}
}
