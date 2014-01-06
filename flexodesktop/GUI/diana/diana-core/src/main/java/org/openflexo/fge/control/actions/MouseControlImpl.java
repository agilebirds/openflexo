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
package org.openflexo.fge.control.actions;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.MouseControl;
import org.openflexo.fge.control.MouseControlContext;

public abstract class MouseControlImpl<E extends AbstractDianaEditor<?, ?, ?>> implements MouseControl<E> {
	static final Logger logger = Logger.getLogger(MouseControlImpl.class.getPackage().getName());

	private String name;
	private boolean shiftPressed = false;
	private boolean ctrlPressed = false;
	private boolean metaPressed = false;
	private boolean altPressed = false;
	private MouseButton button;

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
	public boolean isApplicable(DrawingTreeNode<?, ?> node, E controller, MouseControlContext context) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Called isApplicable(MouseEvent) for " + this + " event=" + context);
		}

		if (context.isConsumed()) {
			return false;
		}

		if (button != context.getButton()) {
			return false;
		}

		if (shiftPressed != context.isShiftDown()) {
			return false;
		}
		if (ctrlPressed != context.isControlDown()) {
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
			if (metaPressed != context.isMetaDown()) {
				return false;
			}
		}

		if (button == MouseButton.CENTER) {
		} else if (altPressed != context.isAltDown()) {
			return false;
		}

		// Everything seems ok, now delegate this to the action
		if (getControlAction() != null) {
			return getControlAction().isApplicable(node, controller, context);
		} else { // No action, return false
			return false;
		}
	}

	protected String getModifiersAsString() {
		return button.name() + (shiftPressed ? ",SHIFT" : "") + (ctrlPressed ? ",CTRL" : "") + (metaPressed ? ",META" : "")
				+ (altPressed ? ",ALT" : "");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isShiftPressed() {
		return shiftPressed;
	}

	public void setShiftPressed(boolean shiftPressed) {
		this.shiftPressed = shiftPressed;
	}

	public boolean isCtrlPressed() {
		return ctrlPressed;
	}

	public void setCtrlPressed(boolean ctrlPressed) {
		this.ctrlPressed = ctrlPressed;
	}

	public boolean isMetaPressed() {
		return metaPressed;
	}

	public void setMetaPressed(boolean metaPressed) {
		this.metaPressed = metaPressed;
	}

	public boolean isAltPressed() {
		return altPressed;
	}

	public void setAltPressed(boolean altPressed) {
		this.altPressed = altPressed;
	}

	public MouseButton getButton() {
		return button;
	}

	public void setButton(MouseButton button) {
		this.button = button;
	}

}
