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
package org.openflexo.view;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.components.OpenProjectComponent;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;

/**
 * @author gpolet
 * 
 */
public class ModuleBar extends JPanel {
	protected static Logger logger = FlexoLogger.getLogger(ModuleBar.class.getPackage().getName());

	private static Vector<ModuleBar> moduleBars = new Vector<ModuleBar>();

	public static void notifyStaticallyModuleHasBeenLoaded(Module m) {
		Enumeration<ModuleBar> en = moduleBars.elements();
		while (en.hasMoreElements()) {
			ModuleBar bar = en.nextElement();
			bar.notifyModuleHasBeenLoaded(m);
		}
	}

	public static void notifyStaticallyModuleHasBeenUnLoaded(Module m) {
		Enumeration<ModuleBar> en = moduleBars.elements();
		while (en.hasMoreElements()) {
			ModuleBar bar = en.nextElement();
			bar.notifyModuleHasBeenUnLoaded(m);
		}
	}

	public static void notifyStaticallySwitchToModule(Module m) {
		Enumeration en = moduleBars.elements();
		while (en.hasMoreElements()) {
			ModuleBar bar = (ModuleBar) en.nextElement();
			bar.notifySwitchToModule(m);
		}
	}

	public void notifyModuleHasBeenLoaded(Module m) {
		refresh();
		repaint();
	}

	public void notifyModuleHasBeenUnLoaded(Module m) {
		refresh();
		repaint();
	}

	public void notifySwitchToModule(Module m) {
		refresh();
		if (moduleButtons.get(m) != null)
			(moduleButtons.get(m)).setAsActive();
	}

	private Hashtable<Module, ModuleButton> moduleButtons;

	/**
     *
     */
	public ModuleBar() {
		moduleButtons = new Hashtable<Module, ModuleButton>();
		moduleBars.add(this);
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		Enumeration en = ModuleLoader.availableModules().elements();
		while (en.hasMoreElements()) {
			Module m = (Module) en.nextElement();
			ModuleButton mb = new ModuleButton(m);
			moduleButtons.put(m, mb);
			add(mb);
		}
		validate();
		repaint();
	}

	private void refresh() {
		Enumeration<ModuleButton> en = moduleButtons.elements();
		while (en.hasMoreElements()) {
			ModuleButton mb = en.nextElement();
			mb.refresh();
		}
	}

	private class ModuleButton extends JLabel {
		protected Module module;

		protected ModuleButton(Module m) {
			this.module = m;
			addMouseListener(new MouseAdapter() {
				/**
				 * Overrides mouseClicked
				 * 
				 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
				 */
				@Override
				public void mouseClicked(MouseEvent e) {
					if (ModuleLoader.getProject() == null && module.requireProject()) {
						File projectDirectory;
						try {
							projectDirectory = OpenProjectComponent.getProjectDirectory();
						} catch (ProjectLoadingCancelledException e1) {
							return;
						}
						ModuleLoader.loadProject(projectDirectory);
					}

					ModuleLoader.switchToModule(module);
				}

				/**
				 * Overrides mouseEntered
				 * 
				 * @see java.awt.event.MouseAdapter#mouseEntered(java.awt.event.MouseEvent)
				 */
				@Override
				public void mouseEntered(MouseEvent e) {
					setBorder(BorderFactory.createEtchedBorder());
				}

				/**
				 * Overrides mouseExited
				 * 
				 * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
				 */
				@Override
				public void mouseExited(MouseEvent e) {
					setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
				}
			});
			setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			setToolTipText(module.getLocalizedName());
			refresh();
		}

		protected void setAsActive() {
			setIcon(module.getMediumIconWithHover());
		}

		protected void refresh() {
			if (module.isActive()) {
				setIcon(module.getMediumIconWithHover());
			} else {
				setIcon(module.getMediumIcon());
			}
		}
	}

	public static void reset() {
		moduleBars.clear();
	}
}
