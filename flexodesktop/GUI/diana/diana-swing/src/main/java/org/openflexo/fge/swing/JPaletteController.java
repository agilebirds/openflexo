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

package org.openflexo.fge.swing;

import javax.swing.JComponent;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.PaletteElement;
import org.openflexo.fge.control.tools.PaletteController;
import org.openflexo.fge.swing.control.tools.JDianaPalette;
import org.openflexo.fge.swing.view.JPaletteElementView;
import org.openflexo.fge.swing.view.JShapeView;

/**
 * This is the SWING implementation of a basic read-only viewer of a {@link Drawing}<br>
 * 
 * The {@link Drawing} can only be viewed, without any editing possibility (shapes are all non-movable)
 * 
 * @author sylvain
 * 
 * @param <M>
 */
public class JPaletteController extends PaletteController<SwingViewFactory, JComponent> {

	public static SwingViewFactory PALETTE_VIEW_FACTORY = new SwingViewFactory() {
		public <O> JShapeView<O> makeShapeView(ShapeNode<O> shapeNode, AbstractDianaEditor<?, SwingViewFactory, JComponent> controller) {
			if (shapeNode.getDrawable() instanceof PaletteElement) {
				return (JShapeView<O>) (new JPaletteElementView((ShapeNode<PaletteElement>) shapeNode, (JPaletteController) controller));
			}
			return super.makeShapeView(shapeNode, controller);
		}
	};

	public JPaletteController(JDianaPalette palette) {
		super(palette, PALETTE_VIEW_FACTORY);
	}

	@Override
	public JDianaPalette getPalette() {
		return (JDianaPalette) super.getPalette();
	}
}
