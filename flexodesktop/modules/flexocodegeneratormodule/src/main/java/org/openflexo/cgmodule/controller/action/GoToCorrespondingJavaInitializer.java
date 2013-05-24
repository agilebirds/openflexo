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
package org.openflexo.cgmodule.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.generator.action.GoToCorrespondingJava;
import org.openflexo.generator.cg.CGJavaFile;
import org.openflexo.generator.cg.CGWOFile;
import org.openflexo.icon.FilesIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class GoToCorrespondingJavaInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	GoToCorrespondingJavaInitializer(GeneratorControllerActionInitializer actionInitializer) {
		super(GoToCorrespondingJava.actionType, actionInitializer);
	}

	@Override
	protected GeneratorControllerActionInitializer getControllerActionInitializer() {
		return (GeneratorControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionFinalizer<GoToCorrespondingJava> getDefaultFinalizer() {
		return new FlexoActionFinalizer<GoToCorrespondingJava>() {
			@Override
			public boolean run(EventObject e, GoToCorrespondingJava action) {
				CGWOFile file = action.getFocusedObject();
				for (CGFile f : file.getRepository().getFiles()) {
					if (f != file && f instanceof CGJavaFile && f.getResource() != null
							&& f.getResource().getGenerator() == file.getResource().getGenerator()) {
						getController().setCurrentEditedObjectAsModuleView(f);
						return true;
					}
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return FilesIconLibrary.SMALL_JAVA_FILE_ICON;
	}
}
