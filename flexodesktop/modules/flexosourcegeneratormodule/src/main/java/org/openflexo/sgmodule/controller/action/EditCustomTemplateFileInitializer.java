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
package org.openflexo.sgmodule.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.action.EditCustomTemplateFile;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.sgmodule.view.CGTemplateFileModuleView;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class EditCustomTemplateFileInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	EditCustomTemplateFileInitializer(SGControllerActionInitializer actionInitializer) {
		super(EditCustomTemplateFile.actionType, actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() {
		return (SGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<EditCustomTemplateFile> getDefaultInitializer() {
		return new FlexoActionInitializer<EditCustomTemplateFile>() {
			@Override
			public boolean run(EventObject e, EditCustomTemplateFile action) {
				CGTemplateFileModuleView editor;
				if ((action.getInvoker() != null) && (action.getInvoker() instanceof CGTemplateFileModuleView)
						&& (((CGTemplateFileModuleView) action.getInvoker()).isOpenedInSeparateWindow())) {
					editor = (CGTemplateFileModuleView) action.getInvoker();
				} else {
					editor = (CGTemplateFileModuleView) getControllerActionInitializer().getSGController().moduleViewForObject(
							(CGTemplate) action.getFocusedObject());
				}
				if (editor != null) {
					action.setTemplateFileContentEditor(editor);
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<EditCustomTemplateFile> getDefaultFinalizer() {
		return new FlexoActionFinalizer<EditCustomTemplateFile>() {
			@Override
			public boolean run(EventObject e, EditCustomTemplateFile action) {
				if ((action.getInvoker() == null) || !(action.getInvoker() instanceof CGTemplateFileModuleView)
						|| !(((CGTemplateFileModuleView) action.getInvoker()).isOpenedInSeparateWindow())) {
					getControllerActionInitializer().getSGController().setCurrentEditedObjectAsModuleView(action.getFocusedObject());
					getControllerActionInitializer().getSGController().selectAndFocusObject(action.getFocusedObject());
				}
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return GeneratorIconLibrary.EDIT_ICON;
	}

	@Override
	protected Icon getDisabledIcon() {
		return GeneratorIconLibrary.EDIT_DISABLED_ICON;
	}

}
