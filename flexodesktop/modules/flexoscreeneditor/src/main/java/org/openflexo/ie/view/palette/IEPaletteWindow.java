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
package org.openflexo.ie.view.palette;

/**
 * @author bmangez
 *
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */

import java.util.logging.Logger;

import org.openflexo.ie.IECst;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.palette.FlexoPalette;
import org.openflexo.view.palette.FlexoPaletteWindow;

/**
 * The palette that contains the dragable elements. Currently draft version.
 * 
 * @author benoit
 */
public class IEPaletteWindow extends FlexoPaletteWindow {

	private static final Logger logger = Logger.getLogger(IEPaletteWindow.class.getPackage().getName());

	/**
	 * Create a palette.
	 * 
	 * @param mainFrame
	 * @throws java.awt.HeadlessException
	 */
	public IEPaletteWindow(FlexoFrame mainFrame) {
		super(mainFrame);

		setLocation(IECst.IE_WINDOW_WIDTH + 2, 0);
		setSize(IECst.DEFAULT_PALETTE_WIDTH + 50, IECst.DEFAULT_PALETTE_HEIGHT + 50);
		setTitle(getLocalizedName());
	}

	@Override
	public FlexoPalette buildNewPalette(FlexoController controller) {
		return new IEPalette((IEController) controller);
	}

	@Override
	public IEPalette getPalette() {
		return (IEPalette) super.getPalette();
	}

	public void setCurrentCSSStyle(String css) {
		getPalette().setCurrentCSSStyle(css);
	}

	@Override
	public String getName() {
		return IECst.DEFAULT_PALETTE_TITLE;
	}

}
