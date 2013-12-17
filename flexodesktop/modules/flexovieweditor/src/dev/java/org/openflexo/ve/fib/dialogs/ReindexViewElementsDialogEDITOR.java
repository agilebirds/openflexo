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
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.diagram.action.ReindexDiagramElements;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.toolbox.FileResource;
import org.openflexo.ve.VECst;

public class ReindexViewElementsDialogEDITOR extends ProjectDialogEDITOR {

	@Override
	public Object[] getData() {
		FlexoEditor editor = loadProject(new FileResource("Prj/TestVE.prj"));
		FlexoProject project = editor.getProject();
		View view = project.getViewLibrary().getViewResourceNamed("R&DDefinition").getView();
		Diagram diagram = (Diagram) view.getVirtualModelInstances().get(0);
		ReindexDiagramElements action = ReindexDiagramElements.actionType.makeNewAction(diagram.getRootPane().getChilds().get(0), null, null);
		return makeArray(action);
	}

	@Override
	public File getFIBFile() {
		return VECst.REINDEX_DIAGRAM_ELEMENTS_DIALOG_FIB;
	}

	public static void main(String[] args) {
		main(ReindexViewElementsDialogEDITOR.class);
	}

}
