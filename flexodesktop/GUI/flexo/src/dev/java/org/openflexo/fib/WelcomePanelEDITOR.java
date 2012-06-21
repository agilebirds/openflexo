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
package org.openflexo.fib;

import java.io.File;

import org.openflexo.ApplicationContext;
import org.openflexo.ApplicationData;
import org.openflexo.components.WelcomeDialog;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.DefaultProjectLoadingHandler;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.view.controller.InteractiveFlexoEditor;

public class WelcomePanelEDITOR {

	public static void main(String[] args) {
		// ModuleLoader.initializeModules(UserType.MAINTAINER, false);
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				ApplicationData applicationData = new ApplicationData(new ApplicationContext() {

					@Override
					public FlexoEditor makeFlexoEditor(FlexoProject project) {
						return new InteractiveFlexoEditor(this, project);
					}

					@Override
					public ProjectLoadingHandler getProjectLoadingHandler(File projectDirectory) {
						return new DefaultProjectLoadingHandler();
					}
				});
				return FIBAbstractEditor.makeArray(applicationData);
			}

			@Override
			public File getFIBFile() {
				return WelcomeDialog.FIB_FILE;
			}
		};
		editor.launch();
	}
}
