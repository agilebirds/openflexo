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
package org.openflexo.components;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.view.controller.FlexoController;

public class NewPaletteComponent extends PaletteChooserComponent {

	private static final Logger logger = Logger.getLogger(OpenProjectComponent.class.getPackage().getName());

	protected NewPaletteComponent() {
		super();
	}

	public static File getPaletteDirectory() {
		NewPaletteComponent chooser = new NewPaletteComponent();
		File newProjectDir = null;
		int returnVal = chooser.showSaveDialog(null);
		if (returnVal == JFileChooser.CANCEL_OPTION) {
			return null;
		}
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (isValidProjectName(chooser.getSelectedFile().getName())) {
				newProjectDir = chooser.getSelectedFile();
				File newFileDir = chooser.getSelectedFile().getParentFile();
				if (!newProjectDir.getName().toLowerCase().endsWith(".iepalette")) {
					newProjectDir = new File(newProjectDir.getAbsolutePath() + ".iepalette");
				}
				if (!newProjectDir.exists()) {
					newProjectDir.mkdir();
				} else {
					if (!FlexoController.confirm(FlexoLocalization.localizedForKey("palette_already_exists_do_you_want_to_replace_it"))) {
						newProjectDir = null;
					}
				}
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Invalid palette name. The following characters are not allowed: "
							+ FileUtils.BAD_CHARACTERS_FOR_FILE_NAME_REG_EXP);
				}
				FlexoController.notify(FlexoLocalization.localizedForKey("palette_name_cannot_contain_\\___&_#_{_}_[_]_%_~"));
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("No project specified !");
			}
		}
		return newProjectDir;
	}

	/**
	 * @param absolutePath
	 * @return
	 */
	public static boolean isValidProjectName(String absolutePath) {
		return absolutePath != null && absolutePath.trim().length() > 0
				&& !FileUtils.BAD_CHARACTERS_FOR_FILE_NAME_PATTERN.matcher(absolutePath).find();
	}

}
