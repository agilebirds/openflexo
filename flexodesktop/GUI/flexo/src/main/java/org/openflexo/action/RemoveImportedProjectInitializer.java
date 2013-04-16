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
package org.openflexo.action;

import java.util.EventObject;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoActionVisibleCondition;
import org.openflexo.foundation.action.RemoveImportedProject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.icon.IconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class RemoveImportedProjectInitializer extends ActionInitializer<RemoveImportedProject, FlexoModelObject, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public RemoveImportedProjectInitializer(ControllerActionInitializer actionInitializer) {
		super(RemoveImportedProject.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<RemoveImportedProject> getDefaultInitializer() {
		return new FlexoActionInitializer<RemoveImportedProject>() {
			@Override
			public boolean run(EventObject e, RemoveImportedProject action) {
				action.setImportingProject(getEditor().getProject());
				return action.getImportingProject() != null;
			}
		};
	}

	@Override
	protected FlexoActionVisibleCondition<RemoveImportedProject, FlexoModelObject, FlexoModelObject> getVisibleCondition() {
		return new FlexoActionVisibleCondition<RemoveImportedProject, FlexoModelObject, FlexoModelObject>() {
			@Override
			public boolean isVisible(FlexoActionType<RemoveImportedProject, FlexoModelObject, FlexoModelObject> actionType,
					FlexoModelObject object, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
				String uri = null;
				if (object instanceof FlexoWorkflow) {
					uri = ((FlexoWorkflow) object).getProjectURI();
				} else if (object instanceof RoleList) {
					uri = ((RoleList) object).getWorkflow().getProjectURI();
				} else if (object instanceof FlexoProject) {
					uri = ((FlexoProject) object).getURI();
				}
				return editor.getProject() != null && editor.getProject().getProjectData() != null
						&& editor.getProject().getProjectData().getProjectReferenceWithURI(uri) != null;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.IMPORT_ICON;
	}

}
