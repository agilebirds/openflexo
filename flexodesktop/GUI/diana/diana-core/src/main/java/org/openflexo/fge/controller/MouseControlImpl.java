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
package org.openflexo.fge.controller;

import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.control.DrawingController;
import org.openflexo.fge.control.MouseControl;

public abstract class MouseControlImpl implements MouseControl {
	static final Logger logger = Logger.getLogger(MouseControlImpl.class.getPackage().getName());

	public String name;
	public boolean shiftPressed = false;
	public boolean ctrlPressed = false;
	public boolean metaPressed = false;
	public boolean altPressed = false;
	public MouseButton button;

	private boolean modelEditionAction = true;

	private FGEModelFactory factory;

	protected MouseControlImpl(String aName, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed,
			MouseButton button, FGEModelFactory factory) {
		super();
		name = aName;
		this.shiftPressed = shiftPressed;
		this.ctrlPressed = ctrlPressed;
		this.metaPressed = metaPressed;
		this.altPressed = altPressed;
		this.button = button;
		this.factory = factory;
	}

	public FGEModelFactory getFactory() {
		return factory;
	}

	@Override
	public boolean isApplicable(DrawingTreeNode<?, ?> node, DrawingController<?> controller, MouseEvent e) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Called isApplicable(MouseEvent) for " + this + " event=" + e);
		}

		if (e.isConsumed()) {
			return false;
		}

		if (button == MouseButton.LEFT && e.getButton() != MouseEvent.BUTTON1) {
			return false;
		}
		if (button == MouseButton.CENTER && e.getButton() != MouseEvent.BUTTON2) {
			return false;
		}
		if (button == MouseButton.RIGHT && e.getButton() != MouseEvent.BUTTON3) {
			return false;
		}

		// logger.info("shiftPressed="+shiftPressed+" e.isShiftDown()="+e.isShiftDown());
		// logger.info("ctrlPressed="+ctrlPressed+" e.isControlDown()="+e.isControlDown());
		// logger.info("metaPressed="+metaPressed+" e.isMetaDown()="+e.isMetaDown());
		// logger.info("altPressed="+altPressed+" e.isAltDown()="+e.isAltDown());

		if (shiftPressed != e.isShiftDown()) {
			return false;
		}
		if (ctrlPressed != e.isControlDown()) {
			return false;
		}

		if (button == MouseButton.RIGHT) {
			// Correction here: on all platforms, it is impossible to
			// distinguish right-click with meta key down from right-click
			// without meta key down (simply because the masks are the same! the
			// same goes for the middle button and alt-down). However, the
			// distinction is that on MacOS, the Meta-key is used to perform
			// multiple non-contiguous selection, when on Windows (and Linux)
			// this is performed with the CTRL key.
			/*
			// Special case for MacOS platform: right-click is emuled by APPLE key (=<META>)
			// cannot distinguish both, so just skip this test
			 */
		} else {
			if (metaPressed != e.isMetaDown()) {
				return false;
			}
		}
		if (button == MouseButton.CENTER) {

		} else if (altPressed != e.isAltDown()) {
			return false;
		}

		// Everything seems ok, return true
		return true;
	}

	@Override
	public boolean isModelEditionAction() {
		return modelEditionAction;
	}

	protected String getModifiersAsString() {
		return button.name() + (shiftPressed ? ",SHIFT" : "") + (ctrlPressed ? ",CTRL" : "") + (metaPressed ? ",META" : "")
				+ (altPressed ? ",ALT" : "");
	}

}
