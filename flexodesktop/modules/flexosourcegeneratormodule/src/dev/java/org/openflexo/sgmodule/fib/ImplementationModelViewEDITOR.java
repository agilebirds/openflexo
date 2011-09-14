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
package org.openflexo.sgmodule.fib;

import java.io.File;

import org.openflexo.fib.ProjectDialogEDITOR;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.sgmodule.SGCst;
import org.openflexo.toolbox.FileResource;


public class ImplementationModelViewEDITOR extends ProjectDialogEDITOR {

	public static void main(String[] args)
	{
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			public Object[] getData() {
				FlexoEditor editor = loadProject(new FileResource("Prj/TestSG.prj"));
				FlexoProject project = editor.getProject();
				ImplementationModel im = project.getGeneratedSources().getImplementationModels().firstElement().getImplementationModel();
				return makeArray(im);
			}
			public File getFIBFile() {
				return SGCst.IMPLEMENTATION_MODEL_VIEW_FIB;
			}
		};
		editor.launch();
	}
}
