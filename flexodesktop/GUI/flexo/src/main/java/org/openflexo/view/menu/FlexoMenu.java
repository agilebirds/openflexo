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
package org.openflexo.view.menu;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.FlexoController;

/**
 * Abstract class implementing a Flexo Menu. Controller management is performed at this level.
 * 
 * @author sguerin
 */
public abstract class FlexoMenu extends JMenu implements MouseListener, MenuListener {

	private static final Logger logger = Logger.getLogger(FlexoMenu.class.getPackage().getName());

	private FlexoController _controller;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	protected FlexoMenu(String value, FlexoController controller) {
		super();
		setController(controller);
		setText(FlexoLocalization.localizedForKey(value, this));
		addMouseListener(this);
		addMenuListener(this);
	}

	// ==========================================================================
	// ============================= Accessors
	// ==================================
	// ==========================================================================

	public FlexoController getController() {
		return _controller;
	}

	protected void setController(FlexoController controller) {
		_controller = controller;
	}

	public boolean moduleHasFocus() {
		boolean returned = getController().getModule().isActive();
		if (logger.isLoggable(Level.FINE))
			logger.fine("moduleHasFocus in " + getClass().getName() + " : " + returned);
		return returned;
	}

	/**
	 * Overrides mouseClicked
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * Overrides mouseEntered
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Overrides mouseExited
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Overrides mousePressed
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (isPopupMenuVisible())
			refreshMenu();
	}

	/**
     *
     */
	public void refreshMenu() {
		for (int i = 0; i < getItemCount(); i++) {
			JMenuItem item = getItem(i);
			if (item instanceof FlexoMenuItem)
				((FlexoMenuItem) item).itemWillShow();
			else if (item instanceof FlexoMenu)
				((FlexoMenu) item).refreshMenu();
		}
	}

	/**
	 * Overrides mouseReleased
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void menuCanceled(MenuEvent e) {

	}

	@Override
	public void menuDeselected(MenuEvent e) {

	}

	@Override
	public void menuSelected(MenuEvent e) {
		refreshMenu();
	}

}
