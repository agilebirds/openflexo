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
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.diagram.action.DeleteDiagramElements;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.toolbox.FileResource;
import org.openflexo.ve.VECst;

public class DeleteViewElementsDialogEDITOR extends ProjectDialogEDITOR {

	@Override
	public Object[] getData() {
		FlexoEditor editor = loadProject(new FileResource("Prj/TestVE.prj"));
		FlexoProject project = editor.getProject();
		View view = project.getViewLibrary().getViewResourceNamed("BasicOrganization").getView();
		Diagram diagram = (Diagram) view.getVirtualModelInstances().get(0);
		DiagramShape a = diagram.getRootPane().getShapeNamed("A");
		DiagramShape b = a.getShapeNamed("B");
		DiagramShape c = a.getShapeNamed("C");
		DiagramShape x = b.getShapeNamed("X");
		DiagramShape worker1 = b.getShapeNamed("Worker");
		DiagramShape worker2 = b.getShapeNamed("Worker2");
		DiagramShape y = c.getShapeNamed("Y");
		DiagramShape z = a.getShapeNamed("Z");
		Vector<DiagramElement> selection = new Vector<DiagramElement>();
		selection.add(b);
		selection.add(x);
		selection.add(c);
		DeleteDiagramElements action = DeleteDiagramElements.actionType.makeNewAction(null, selection, null);
		return makeArray(action);
	}

	@Override
	public File getFIBFile() {
		return VECst.DELETE_DIAGRAM_ELEMENTS_DIALOG_FIB;
	}

	public static void main(String[] args) {
		main(DeleteViewElementsDialogEDITOR.class);
	}

}
