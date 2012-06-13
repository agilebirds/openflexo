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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.logging.FlexoLogger;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.module.Modules;
import org.openflexo.view.controller.FlexoController;

/**
 * @author gpolet
 * 
 */
public class ModuleBar extends JPanel implements PropertyChangeListener {
	protected static Logger logger = FlexoLogger.getLogger(ModuleBar.class.getPackage().getName());

	private Hashtable<Module, ModuleButton> moduleButtons;
	private final ModuleLoader moduleLoader;

	/**
     *
     */
	public ModuleBar(ModuleLoader moduleLoader) {
		this.moduleLoader = moduleLoader;
		moduleButtons = new Hashtable<Module, ModuleButton>();
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		for (Module m : Modules.getInstance().getAvailableModules()) {
			ModuleButton mb = new ModuleButton(m);
			moduleButtons.put(m, mb);
			add(mb);
		}
		moduleLoader.getPropertyChangeSupport().addPropertyChangeListener(ModuleLoader.MODULE_LOADED, this);
		moduleLoader.getPropertyChangeSupport().addPropertyChangeListener(ModuleLoader.MODULE_UNLOADED, this);
		moduleLoader.getPropertyChangeSupport().addPropertyChangeListener(ModuleLoader.MODULE_ACTIVATED, this);
		validate();
		repaint();
	}

	private ModuleLoader getModuleLoader() {
		return moduleLoader;
	}

	private void refresh() {
		for (ModuleButton mb : moduleButtons.values()) {
			mb.refresh();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ModuleLoader.MODULE_LOADED)) {
			refresh();
		} else if (evt.getPropertyName().equals(ModuleLoader.MODULE_UNLOADED)) {
			refresh();
		} else if (evt.getPropertyName().equals(ModuleLoader.MODULE_ACTIVATED)) {
			refresh();
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
					try {
						getModuleLoader().switchToModule(module);
					} catch (ModuleLoadingException e) {
						FlexoController.notify("Cannot load module." + e.getMessage());
						return;
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

		protected void setAsActive() {
			setIcon(module.getMediumIconWithHover());
		}

		protected void refresh() {
			if (getModuleLoader().getActiveModule() != null && getModuleLoader().getActiveModule().getModule() == module) {
				setIcon(module.getMediumIconWithHover());
			} else {
				setIcon(module.getMediumIcon());
			}
		}
	}

}
