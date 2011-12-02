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
package org.openflexo.view.listener;

/*
 * FlexoKeyEventListener.java
 * Project WorkflowEditor
 * 
 * Created by benoit on Mar 12, 2004
 */

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.view.controller.FlexoController;

/**
 * Abstract listeners for key events At this level are managed short-cuts
 * 
 * @author benoit, sylvain
 */
public abstract class FlexoKeyEventListener extends KeyAdapter {

	private static final Logger logger = Logger.getLogger(FlexoKeyEventListener.class.getPackage().getName());

	private FlexoController _controller;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public FlexoKeyEventListener(FlexoController controller) {
		super();
		_controller = controller;
	}

	public static final String KEY_PRESSED = "KeyPressed";

	/*
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent event) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("KeyPressed in " + getClass().getName() + " with " + event);
		}
		ActionListener listener = _controller.getActionForKeyStroke(KeyStroke.getKeyStroke(event.getKeyCode(), event.getModifiers()));
		if (listener != null) {
			Object eventSource = event.getSource();
			if (!(eventSource instanceof FlexoActionSource) && eventSource instanceof Component) {
				eventSource = SwingUtilities.getAncestorOfClass(FlexoActionSource.class, (Component) eventSource);
			}
			listener.actionPerformed(new ActionEvent(eventSource != null ? eventSource : event.getSource(), event.getID(), KEY_PRESSED));
		}
		event.consume();
		if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
			_controller.cancelCurrentAction();
		}
	}

	public FlexoController getController() {
		return _controller;
	}
}
