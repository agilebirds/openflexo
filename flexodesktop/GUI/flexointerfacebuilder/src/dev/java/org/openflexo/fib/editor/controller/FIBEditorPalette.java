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
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.editor.FIBEditor;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.ToolBox;

public class FIBEditorPalette extends JDialog {

	static final Logger logger = FlexoLogger.getLogger(FIBEditor.class.getPackage().getName());

	private static final Image DROP_OK_IMAGE = FIBIconLibrary.DROP_OK_CURSOR.getImage();
	private static final Image DROP_KO_IMAGE = FIBIconLibrary.DROP_KO_CURSOR.getImage();

	public static final Cursor dropOK = ToolBox.getPLATFORM()==ToolBox.MACOS?Toolkit.getDefaultToolkit().createCustomCursor(DROP_OK_IMAGE, new Point(16, 16), "Drop OK"):DragSource.DefaultMoveDrop;
	public static final Cursor dropKO = ToolBox.getPLATFORM()==ToolBox.MACOS?Toolkit.getDefaultToolkit().createCustomCursor(DROP_KO_IMAGE, new Point(16, 16), "Drop KO"):DragSource.DefaultMoveNoDrop;

	private final JPanel paletteContent;

	//public FIBEditorController controller;

	public DragSourceContext dragSourceContext;

	public FIBEditorPalette(JFrame frame) 
	{
		super(frame,"Palette",false);

		paletteContent = new JPanel(null);
		paletteContent.setPreferredSize(new Dimension(415,375));

		File dir = new FileResource("FIBEditorPalette");
		
		for (File f : dir.listFiles(new FilenameFilter() {		
			public boolean accept(File dir, String name) {
				return name.endsWith(".fib");
			}
		})) {
			// System.out.println("Read "+f.getAbsolutePath());
			FIBComponent paletteComponent = FIBLibrary.instance().retrieveFIBComponent(f);
			if (paletteComponent != null) {
				addPaletteElement(paletteComponent);
				logger.info("Loaded palette element: "+paletteComponent+" file: "+f.getName());
			} else {
				logger.warning("Not found: "+f.getAbsolutePath());
			}
		}
		
		getContentPane().add(paletteContent);
		setLocation(1210,0);
		pack();

	}

	private PaletteElement addPaletteElement(FIBComponent component)
	{
		PaletteElement el = new PaletteElement(component,this);
		paletteContent.add(el.getView().getResultingJComponent());
		return el;
	}


	public PaletteDropListener buildPaletteDropListener(JComponent dropContainer, FIBEditorController controller)
	{
		return new PaletteDropListener(this, dropContainer,controller);
	}


}


