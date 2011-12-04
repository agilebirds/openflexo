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

package org.openflexo.fge;

import java.io.File;

import org.openflexo.fge.shapes.Shape;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fge.view.widget.FIBShapeSelector;
import org.openflexo.fge.view.widget.FIBShapeSelector.ShapeFactory;
import org.openflexo.fib.editor.FIBAbstractEditor;

public class ShapeSelectorPanelEDITOR {

	public static void main(String[] args) {
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				ShapeFactory shapeFactory = new ShapeFactory(Shape.makeShape(ShapeType.STAR, null));
				return FIBAbstractEditor.makeArray(shapeFactory);
			}

			@Override
			public File getFIBFile() {
				return FIBShapeSelector.FIB_FILE;
			}
		};
		editor.launch();
	}
}
