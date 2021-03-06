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
package org.openflexo.components.widget;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.toolbox.FileResource;

/**
 * Widget allowing to select a ViewPoint
 * 
 * @author sguerin
 * 
 */
public class FIBERDiagramSelector extends FIBModelObjectSelector<ERDiagram> {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBERDiagramSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/ERDiagramSelector.fib");

	public FIBERDiagramSelector(ERDiagram editedObject) {
		super(editedObject);
	}

	@Override
	public File getFIBFile() {
		return FIB_FILE;
	}

	@Override
	public Class<ERDiagram> getRepresentedType() {
		return ERDiagram.class;
	}

	@Override
	public String renderedString(ERDiagram editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return "";
	}

	// Please uncomment this for a live test
	// Never commit this uncommented since it will not compile on continuous build
	// To have icon, you need to choose "Test interface" in the editor (otherwise, flexo controller is not insanciated in EDIT mode)
	/*public static void main(String[] args) {
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				FlexoEditor editor = loadProject(new FileResource("Prj/TestVE.prj"));
				FlexoProject project = editor.getProject();
				FIBERDiagramSelector selector = new FIBERDiagramSelector(null);
				selector.setProject(project);
				return makeArray(selector);
			}

			@Override
			public File getFIBFile() {
				return FIB_FILE;
			}

			@Override
			public FIBController makeNewController(FIBComponent component) {
				return new FlexoFIBController<FIBERDiagramSelector>(component);
			}
		};
		editor.launch();
	}*/

}