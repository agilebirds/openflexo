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
import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;
import org.openflexo.components.SaveProjectsDialog;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.rm.FlexoProject;

public class SaveProjectDialogEDITOR extends FIBAbstractEditor {

	@Override
	public Object[] getData() {
		List<FlexoProject> projects = new ArrayList<FlexoProject>();
		for (int i = 1; i < 6; i++) {
			FlexoProject project = Mockito.mock(FlexoProject.class);
			Mockito.when(project.getName()).thenReturn("test-project-" + i);
			Mockito.when(project.getProjectDirectory()).thenReturn(new File(System.getProperty("user.home"), "test-project-" + i));
			projects.add(project);
		}
		return FIBAbstractEditor.makeArray(new SaveProjectsDialog.ProjectList(projects));
	}

	@Override
	public File getFIBFile() {
		return SaveProjectsDialog.FIB_FILE;
	}

	public static void main(String[] args) {
		main(SaveProjectDialogEDITOR.class);
	}
}
