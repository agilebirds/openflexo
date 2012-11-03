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
import java.util.Vector;

import org.openflexo.fib.ProjectDialogEDITOR;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewElement;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.view.action.DeleteViewElements;
import org.openflexo.toolbox.FileResource;
import org.openflexo.ve.VECst;


public class DeleteViewElementsDialogEDITOR extends ProjectDialogEDITOR {

	
	public static void main(String[] args)
	{
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			public Object[] getData() 
			{
				FlexoEditor editor = loadProject(new FileResource("Prj/TestVE.prj"));
				FlexoProject project = editor.getProject();
				View shema = project.getShemaLibrary().getShemaNamed("BasicOrganization").getShema();
				ViewShape a = shema.getShapeNamed("A");
				ViewShape b = a.getShapeNamed("B");
				ViewShape c = a.getShapeNamed("C");
				ViewShape x = b.getShapeNamed("X");
				ViewShape worker1 = b.getShapeNamed("Worker");
				ViewShape worker2 = b.getShapeNamed("Worker2");
				ViewShape y = c.getShapeNamed("Y");
				ViewShape z = a.getShapeNamed("Z");
				Vector<ViewElement> selection = new Vector<ViewElement>();
				selection.add(b);
				selection.add(x);
				selection.add(c);
				DeleteViewElements action = DeleteViewElements.actionType.makeNewAction(null, selection,null);
				return makeArray(action);
			}
			public File getFIBFile() {
				return VECst.DELETE_VIEW_ELEMENTS_DIALOG_FIB;
			}
		};
		editor.launch();
	}
}
