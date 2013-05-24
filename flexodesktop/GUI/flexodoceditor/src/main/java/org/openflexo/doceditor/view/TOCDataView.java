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
package org.openflexo.doceditor.view;

import java.io.File;

import org.openflexo.doceditor.controller.DEController;
import org.openflexo.doceditor.controller.TOCPerspective;
import org.openflexo.foundation.toc.TOCData;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.FIBModuleView;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class TOCDataView extends FIBModuleView<TOCData> {

	public static final File FIB_FILE = new FileResource("Fib/TOCDataView.fib");

	public TOCDataView(TOCData tocEntry, DEController controller) {
		super(tocEntry, controller, FIB_FILE);
	}

	@Override
	public DEController getFlexoController() {
		return (DEController) super.getFlexoController();
	}

	@Override
	public TOCPerspective getPerspective() {
		return getFlexoController().TOC_PERSPECTIVE;
	}

	// Please uncomment this for a live test
	// Never commit this uncommented since it will not compile on continuous build
	// To have icon, you need to choose "Test interface" in the editor (otherwise, flexo controller is not insanciated in EDIT mode)
	/*public static void main(String[] args) {

		try {
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// final FlexoEditor editor = loadProject(new FileResource("Prj/TestVE.prj"));
		final FlexoProject project = loadProject(new FileResource("Prj/TestVE.prj")).getProject();

		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				return makeArray(project.getTOCData());
			}

			@Override
			public File getFIBFile() {
				return FIB_FILE;
			}

			@Override
			public FIBController makeNewController(FIBComponent component) {
				return new FlexoFIBController<OntologyLibrary>(component);
			}
		};
		editor.launch();
	}*/

}
