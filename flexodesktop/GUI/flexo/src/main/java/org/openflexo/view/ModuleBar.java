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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.module.ProjectLoader;
import org.openflexo.view.controller.FlexoController;

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
		if (moduleButtons.get(m) != null) {
			moduleButtons.get(m).setAsActive();
		}
	}

	private Hashtable<Module, ModuleButton> moduleButtons;

	/**
     *
     */
	public ModuleBar() {
		moduleButtons = new Hashtable<Module, ModuleButton>();
		moduleBars.add(this);
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		for (Module m : getModuleLoader().availableModules()) {
			ModuleButton mb = new ModuleButton(m);
			moduleButtons.put(m, mb);
			add(mb);
		}
		validate();
		repaint();
	}

	private ModuleLoader getModuleLoader() {
		return ModuleLoader.instance();
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
				public void mouseClicked(MouseEvent event) {
					FlexoProject currentProject = getModuleLoader().getProject();
					if (currentProject == null && module.requireProject()) {
						try {
							ModuleLoader.instance().openProject(null, module);
						} catch (ProjectLoadingCancelledException e) {
							e.printStackTrace();
						} catch (ModuleLoadingException e) {
							e.printStackTrace();
							FlexoController.notify(FlexoLocalization.localizedForKey("could_not_load_module") + " " + e.getModule());
						} catch (ProjectInitializerException e) {
							e.printStackTrace();
							FlexoController.notify(FlexoLocalization.localizedForKey("could_not_open_project_located_at")
									+ e.getProjectDirectory().getAbsolutePath());
						}
					} else {
						try {
							getModuleLoader().switchToModule(module, currentProject);
						} catch (ModuleLoadingException e) {
							FlexoController.notify("Cannot load module." + e.getMessage());
							return;
						}
					}

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

		private ModuleLoader getModuleLoader() {
			return ModuleLoader.instance();
		}

		private ProjectLoader getProjectLoader() {
			return ProjectLoader.instance();
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
