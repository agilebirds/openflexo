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
package org.openflexo.fme;

import java.io.File;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.toolbox.FileResource;

public class FreeModelingToolbox extends AbstractFIBPanel {
	static final Logger logger = Logger.getLogger(FreeModelingToolbox.class.getPackage().getName());

	private static final File FREE_MODELING_TOOLBOX_FIB = new FileResource("Fib/FreeModelingToolbox.fib");

	public FreeModelingToolbox(DiagramEditor editor) {
		super(editor, FREE_MODELING_TOOLBOX_FIB, false);
	}

	public Icon getPipetteIcon() {
		DiagramEditor de = (DiagramEditor) getDataObject();
		if (de.hasPipette()) {
			return FMEIconLibrary.PIPETTE_ICON;
		}
		if (!de.hasPipette()) {
			return FMEIconLibrary.NO_PIPETTE_ICON;
		}
		return null;
	}

}
