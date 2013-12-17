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
package org.openflexo.ve.fib.dialogs;

import java.io.File;

import org.openflexo.fib.ProjectDialogEDITOR;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.view.action.CreateView;
import org.openflexo.toolbox.FileResource;
import org.openflexo.ve.VECst;

public class CreateViewDialogEDITOR extends ProjectDialogEDITOR {

	@Override
	public Object[] getData() {
		FlexoEditor editor = loadProject(new FileResource("TestProjects/1.6/Test1.6.prj"));
		FlexoProject project = editor.getProject();
		CreateView action = CreateView.actionType.makeNewAction(project.getViewLibrary().getRootFolder(), null, editor);
		return makeArray(action);
	}

	@Override
	public File getFIBFile() {
		return VECst.CREATE_VIEW_DIALOG_FIB;
	}

	public static void main(String[] args) {
		main(CreateViewDialogEDITOR.class);
	}

}
