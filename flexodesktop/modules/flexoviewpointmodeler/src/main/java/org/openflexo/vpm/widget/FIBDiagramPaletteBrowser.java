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
package org.openflexo.vpm.widget;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.TestApplicationContext;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.rm.DiagramPaletteResource;
import org.openflexo.foundation.rm.ViewPointResource;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.FIBBrowserView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.FlexoFIBController;

/**
 * Browser allowing to browse through example diagram<br>
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class FIBDiagramPaletteBrowser extends FIBBrowserView<DiagramPalette> {
	static final Logger logger = Logger.getLogger(FIBDiagramPaletteBrowser.class.getPackage().getName());

	public static final FileResource FIB_FILE = new FileResource("Fib/Widget/FIBDiagramPaletteBrowser.fib");

	public FIBDiagramPaletteBrowser(DiagramPalette diagramPalette, FlexoController controller) {
		super(diagramPalette, controller, FIB_FILE);
	}

	// Please uncomment this for a live test
	// Never commit this uncommented since it will not compile on continuous build
	// To have icon, you need to choose "Test interface" in the editor (otherwise, flexo controller is not insanciated in EDIT mode)
	public static void main(String[] args) {

		try {
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TestApplicationContext testApplicationContext = new TestApplicationContext(
				new FileResource("src/test/resources/TestResourceCenter"));
		final ViewPointLibrary viewPointLibrary = testApplicationContext.getViewPointLibrary();

		ViewPointResource vpRes = viewPointLibrary
				.getViewPointResource("http://www.agilebirds.com/openflexo/ViewPoints/Basic/BasicOntology.owl");

		DiagramPaletteResource dpRes = vpRes.getContents(DiagramPaletteResource.class).get(0);
		final DiagramPalette diagramPalette = dpRes.getDiagramPalette();

		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				return makeArray(diagramPalette);
			}

			@Override
			public File getFIBFile() {
				return FIB_FILE;
			}

			@Override
			public FIBController makeNewController(FIBComponent component) {
				return new FlexoFIBController(component);
			}
		};
		editor.launch();

	}

}