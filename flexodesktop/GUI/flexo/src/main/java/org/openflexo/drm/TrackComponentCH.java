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
package org.openflexo.drm;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

import org.openflexo.ApplicationContext;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.FlexoController;

public abstract class TrackComponentCH {

	private static final Logger logger = Logger.getLogger(TrackComponentCH.class.getPackage().getName());

	private final FlexoFrame _frame;
	protected JComponent focusedComponent = null;
	private final Border oldBorder = null;
	private final DocResourceManager docResourceManager;

	private final Hashtable<JComponent, ComponentCHListener> listeners;

	public TrackComponentCH(FlexoFrame frame, ApplicationContext applicationContext) {
		super();
		_frame = frame;
		docResourceManager = applicationContext.getDocResourceManager();
		listeners = new Hashtable<JComponent, ComponentCHListener>();
		addMouseListener(_frame, null);
	}

	public FlexoController getController() {
		return _frame.getController();
	}

	public DocResourceManager getDocResourceManager() {
		return docResourceManager;
	}

	private ComponentCHListener addMouseListener(JComponent component) {
		listeners.put(component, new ComponentCHListener(component));
		return listeners.get(component);
	}

	private void addMouseListener(Container component, MouseListener listener) {
		if (component instanceof JComponent && docResourceManager.getDocForComponent((JComponent) component) != null) {
			listener = addMouseListener((JComponent) component);
		} else if (listener != null) {
			component.addMouseListener(listener);
		}
		for (int i = 0; i < component.getComponentCount(); i++) {
			Component comp = component.getComponent(i);
			if (comp instanceof Container) {
				addMouseListener((Container) comp, listener);
			}
		}
	}

	private ComponentCHListener removeMouseListener(JComponent component) {
		component.removeMouseListener(listeners.get(component));
		return listeners.get(component);
	}

	private void removeMouseListener(Container component, ComponentCHListener listener) {
		if (component instanceof JComponent) {
			if (listeners.get(component) != null) {
				listener = removeMouseListener((JComponent) component);
			} else if (listener != null) {
				component.removeMouseListener(listener);
			}
		}
		for (int i = 0; i < component.getComponentCount(); i++) {
			Component comp = component.getComponent(i);
			if (comp instanceof Container) {
				removeMouseListener((Container) comp, listener);
			}
		}
	}

	protected void finalizeTracking() {
		focusedComponent.setBorder(oldBorder);
		focusedComponent.validate();
		focusedComponent.repaint();
		removeMouseListener(_frame, null);
		_frame.getContentPane().setCursor(Cursor.getDefaultCursor());
		listeners.clear();
	}

	public abstract void applyTracking(JComponent component);

	public class ComponentCHListener implements MouseListener {

		private final JComponent componentWithHelp;
		private Border oldBorder;

		/**
         * 
         */
		public ComponentCHListener(JComponent componentWithHelp) {
			this.componentWithHelp = componentWithHelp;
		}

		/**
		 * Overrides mouseClicked
		 * 
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() instanceof JComponent) {
				finalizeTracking();
				try {
					applyTracking(focusedComponent);
				} catch (javax.help.BadIDException ex) {
					FlexoController.notify(FlexoLocalization.localizedForKey("no_help_for_this_part"));
				}
			}
		}

		/**
		 * Overrides mouseEntered
		 * 
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseEntered(MouseEvent e) {
			if (e.getSource() instanceof JComponent) {
				focusedComponent = componentWithHelp;
				oldBorder = focusedComponent.getBorder();
				focusedComponent.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
				focusedComponent.validate();
				focusedComponent.repaint();
			}
		}

		/**
		 * Overrides mouseExited
		 * 
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			if (e.getSource() instanceof JComponent) {
				componentWithHelp.setBorder(oldBorder);
				componentWithHelp.validate();
				componentWithHelp.repaint();
			}
		}

		/**
		 * Overrides mousePressed
		 * 
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent e) {

		}

		/**
		 * Overrides mouseReleased
		 * 
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent e) {

		}

	}

}
