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

import java.awt.Frame;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import org.openflexo.ApplicationContext;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.FlexoController;

/**
 * Component allowing to choose a new flexo project
 * 
 * @author sguerin
 */
public class NewProjectComponent extends ProjectChooserComponent {

	private static final Logger logger = Logger.getLogger(OpenProjectComponent.class.getPackage().getName());

	protected NewProjectComponent(Frame owner, ApplicationContext applicationContext) {
		super(owner, applicationContext);
		setApproveButtonText(FlexoLocalization.localizedForKey("create"));
	}

	public static File getProjectDirectory(ApplicationContext applicationContext) {
		return getProjectDirectory(FlexoFrame.getActiveFrame(), applicationContext);
	}

	public static File getProjectDirectory(Frame owner, ApplicationContext applicationContext) {
		NewProjectComponent chooser = new NewProjectComponent(owner, applicationContext);
		File newProjectDir = null;
		int returnVal = chooser.showSaveDialog();
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (isValidProjectName(chooser.getSelectedFile().getName())) {
				newProjectDir = chooser.getSelectedFile();
				File newFileDir = chooser.getSelectedFile().getParentFile();
				applicationContext.getAdvancedPrefs().setLastVisitedDirectory(newFileDir);
				if (!newProjectDir.getName().toLowerCase().endsWith(".prj")) {
					newProjectDir = new File(newProjectDir.getAbsolutePath() + ".prj");
				}
				if (!newProjectDir.exists()) {
					newProjectDir.mkdir();
				} else {
					if (!FlexoController.confirmWithWarning(FlexoLocalization
							.localizedForKey("project_already_exists_do_you_want_to_replace_it"))) {
						newProjectDir = null;
					}
				}
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Invalid project name. The following characters are not allowed: "
							+ FileUtils.BAD_CHARACTERS_FOR_FILE_NAME_REG_EXP);
				}
				FlexoController.notify(FlexoLocalization.localizedForKey("project_name_cannot_contain_\\___&_#_{_}_[_]_%_~"));
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("No project specified !");
			}
			return null;
		}
		return newProjectDir;
	}

	/**
	 * @param absolutePath
	 * @return
	 */
	public static boolean isValidProjectName(String fileName) {
		String trimmed = fileName != null ? fileName.trim() : "";
		boolean notEmpty = trimmed.length() > 0;
		if (!notEmpty) {
			return false;
		}
		boolean containsInvalidChar = FileUtils.BAD_CHARACTERS_FOR_FILE_NAME_PATTERN.matcher(fileName).find();
		if (containsInvalidChar) {
			return false;
		}
		boolean isTooSmall = trimmed.length() < 3;
		if (isTooSmall) {
			return false;
		}
		return !Character.isDigit(trimmed.charAt(0));
	}

	public static void main(String[] args) {
		String s = "cou\\cou";
		if (isValidProjectName(s)) {
			System.err.println("Error for " + s);
		}
		s = "cou|couc";
		if (isValidProjectName(s)) {
			System.err.println("Error for " + s);
		}
		s = "cou/couc";
		if (isValidProjectName(s)) {
			System.err.println("Error for " + s);
		}
		s = "cou?couc";
		if (isValidProjectName(s)) {
			System.err.println("Error for " + s);
		}
		s = "cou:couc";
		if (isValidProjectName(s)) {
			System.err.println("Error for " + s);
		}
		s = "cou\"couc";
		if (isValidProjectName(s)) {
			System.err.println("Error for " + s);
		}
		s = "cou*couc";
		if (isValidProjectName(s)) {
			System.err.println("Error for " + s);
		}
		s = "cou?couc";
		if (isValidProjectName(s)) {
			System.err.println("Error for " + s);
		}
		s = "cou<couc";
		if (isValidProjectName(s)) {
			System.err.println("Error for " + s);
		}
		s = "cou>couc";
		if (isValidProjectName(s)) {
			System.err.println("Error for " + s);
		}
	}

}
