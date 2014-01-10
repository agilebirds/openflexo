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
package org.openflexo.fib.editor.controller;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DragSource;
import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.editor.FIBEditor;
import org.openflexo.fib.editor.FIBPreferences;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.ComponentBoundSaver;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.ToolBox;

public class FIBEditorPalette extends JDialog {

	static final Logger logger = FlexoLogger.getLogger(FIBEditor.class.getPackage().getName());

	private static final Image DROP_OK_IMAGE = FIBIconLibrary.DROP_OK_CURSOR.getImage();
	private static final Image DROP_KO_IMAGE = FIBIconLibrary.DROP_KO_CURSOR.getImage();

	public static final Cursor dropOK = ToolBox.getPLATFORM() == ToolBox.MACOS ? Toolkit.getDefaultToolkit().createCustomCursor(
			DROP_OK_IMAGE, new Point(16, 16), "Drop OK") : DragSource.DefaultMoveDrop;
	public static final Cursor dropKO = ToolBox.getPLATFORM() == ToolBox.MACOS ? Toolkit.getDefaultToolkit().createCustomCursor(
			DROP_KO_IMAGE, new Point(16, 16), "Drop KO") : DragSource.DefaultMoveNoDrop;

	private final JPanel paletteContent;

	private FIBEditorController editorController;

	public FIBEditorPalette(JFrame frame) {
		super(frame, "Palette", false);

		paletteContent = new JPanel(null);

		File dir = new FileResource("FIBEditorPalette");

		for (File f : dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return dir.isDirectory();
			}
		})) {
			// System.out.println("Read "+f.getAbsolutePath());

			File modelFIBFile = new File(f, f.getName() + ".fib");
			if (modelFIBFile.exists()) {
				FIBComponent modelComponent = FIBLibrary.instance().retrieveFIBComponent(modelFIBFile);
				if (modelComponent != null) {
					File representationFIBFile = new File(f, f.getName() + ".palette");
					FIBComponent representationComponent = null;
					if (representationFIBFile.exists()) {
						representationComponent = FIBLibrary.instance().retrieveFIBComponent(representationFIBFile);
					} else {
						representationComponent = FIBLibrary.instance().retrieveFIBComponent(modelFIBFile);
					}
					addPaletteElement(modelComponent, representationComponent);
					logger.info("Loaded palette element: " + modelComponent + " file: " + f.getName());
				} else {
					logger.warning("Not found: " + f.getAbsolutePath());
				}
			}
		}

		getContentPane().add(paletteContent);
		setBounds(FIBPreferences.getPaletteBounds());
		new ComponentBoundSaver(this) {

			@Override
			public void saveBounds(Rectangle bounds) {
				FIBPreferences.setPaletteBounds(bounds);
			}
		};

	}

	private PaletteElement addPaletteElement(FIBComponent modelComponent, FIBComponent representationComponent) {
		PaletteElement el = new PaletteElement(modelComponent, representationComponent, this);
		paletteContent.add(el.getView().getResultingJComponent());
		return el;
	}

	public FIBEditorController getEditorController() {
		return editorController;
	}

	public void setEditorController(FIBEditorController editorController) {
		this.editorController = editorController;
	}

}
