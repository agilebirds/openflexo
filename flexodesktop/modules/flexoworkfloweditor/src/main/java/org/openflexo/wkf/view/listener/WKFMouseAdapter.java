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
package org.openflexo.wkf.view.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import org.openflexo.wkf.controller.WKFController;

public abstract class WKFMouseAdapter extends MouseAdapter implements MouseMotionListener {

	public abstract WKFController getController();

	@Override
	public final void mouseClicked(MouseEvent e) {
		proceedToMouseClicked(e);
	}

	@Override
	public final void mousePressed(MouseEvent e) {
		proceedToMousePressed(e);
	}

	@Override
	public final void mouseMoved(MouseEvent e) {
		proceedToMouseMoved(e);
	}

	@Override
	public final void mouseDragged(MouseEvent e) {
		proceedToMouseDragged(e);
	}

	@Override
	public final void mouseReleased(MouseEvent e) {
		proceedToMouseReleased(e);
	}

	/**
	 * Invoked when the mouse enters a component.
	 */
	@Override
	public final void mouseEntered(MouseEvent e) {
		proceedToMouseEntered(e);
	}

	/**
	 * Invoked when the mouse exits a component.
	 */
	@Override
	public final void mouseExited(MouseEvent e) {
		proceedToMouseExited(e);
	}

	public void proceedToMouseClicked(MouseEvent e) {
	}

	public void proceedToMousePressed(MouseEvent e) {
	}

	public void proceedToMouseMoved(MouseEvent e) {
	}

	public void proceedToMouseDragged(MouseEvent e) {
	}

	public void proceedToMouseReleased(MouseEvent e) {
	}

	public void proceedToMouseEntered(MouseEvent e) {
	}

	public void proceedToMouseExited(MouseEvent e) {
	}

}
