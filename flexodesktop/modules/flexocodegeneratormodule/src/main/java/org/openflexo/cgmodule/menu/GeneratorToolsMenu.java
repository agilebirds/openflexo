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
package org.openflexo.cgmodule.menu;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.menu.FlexoMenuItem;
import org.openflexo.view.menu.ToolsMenu;

import de.hunsicker.jalopy.storage.Convention;
import de.hunsicker.jalopy.swing.SettingsDialog;

/**
 * @author gpolet
 * 
 */
public class GeneratorToolsMenu extends ToolsMenu {

	private JMenuItem javaFormat;

	/**
	 * @param controller
	 */
	public GeneratorToolsMenu(FlexoController controller) {
		super(controller);
		add(javaFormat = new JavaFormatItem());
	}

	public class JavaFormatAction extends AbstractAction {

		/**
		 * Overrides actionPerformed
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Convention.importSettings(getController().getProject().getJavaFormatterSettings().getFile());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			SettingsDialog settings = SettingsDialog.create(getController().getFlexoFrame(),
					FlexoLocalization.localizedForKey("java_format_settings"));
			settings.setSize(700, 550);
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			settings.setLocation((dim.width - settings.getWidth()) / 2, (int) ((dim.getHeight() - settings.getHeight()) / 2));
			settings.addWindowListener(new WindowListener() {

				@Override
				public void windowActivated(WindowEvent e) {
				}

				@Override
				public void windowClosed(WindowEvent e) {
					try {
						Convention.getInstance().exportSettings(getController().getProject().getJavaFormatterSettings().getFile());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				@Override
				public void windowClosing(WindowEvent e) {
				}

				@Override
				public void windowDeactivated(WindowEvent e) {
				}

				@Override
				public void windowDeiconified(WindowEvent e) {
				}

				@Override
				public void windowIconified(WindowEvent e) {
				}

				@Override
				public void windowOpened(WindowEvent e) {
				}

			});
			settings.show();
		}

	}

	public class JavaFormatItem extends FlexoMenuItem {

		/**
		 * @param actionType
		 * @param controller
		 */
		public JavaFormatItem() {
			super(new JavaFormatAction(), "java_format", null, null, getController());
		}

	}
}
