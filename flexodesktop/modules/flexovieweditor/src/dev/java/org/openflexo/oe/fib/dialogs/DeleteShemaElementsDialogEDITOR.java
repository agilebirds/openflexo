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
package org.openflexo.oe.fib.dialogs;

import java.io.File;
import java.util.Vector;

import org.openflexo.fib.ProjectDialogEDITOR;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.ontology.action.DeleteShemaElements;
import org.openflexo.foundation.ontology.shema.OEShape;
import org.openflexo.foundation.ontology.shema.OEShema;
import org.openflexo.foundation.ontology.shema.OEShemaElement;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.oe.OECst;
import org.openflexo.toolbox.FileResource;


public class DeleteShemaElementsDialogEDITOR extends ProjectDialogEDITOR {

	
	public static void main(String[] args)
	{
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			public Object[] getData() 
			{
				FlexoEditor editor = loadProject(new FileResource("Prj/TestOE.prj"));
				FlexoProject project = editor.getProject();
				OEShema shema = project.getShemaLibrary().getShemaNamed("BasicOrganization").getShema();
				OEShape a = shema.getShapeNamed("A");
				OEShape b = a.getShapeNamed("B");
				OEShape c = a.getShapeNamed("C");
				OEShape x = b.getShapeNamed("X");
				OEShape worker1 = b.getShapeNamed("Worker");
				OEShape worker2 = b.getShapeNamed("Worker2");
				OEShape y = c.getShapeNamed("Y");
				OEShape z = a.getShapeNamed("Z");
				Vector<OEShemaElement> selection = new Vector<OEShemaElement>();
				selection.add(b);
				selection.add(x);
				selection.add(c);
				DeleteShemaElements action = DeleteShemaElements.actionType.makeNewAction(null, selection,null);
				return makeArray(action);
			}
			public File getFIBFile() {
				return OECst.DELETE_SHEMA_ELEMENTS_DIALOG_FIB;
			}
		};
		editor.launch();
	}
}
