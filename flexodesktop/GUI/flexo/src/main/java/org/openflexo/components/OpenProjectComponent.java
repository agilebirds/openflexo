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
import org.openflexo.view.FlexoFrame;

/**
 * Component allowing to choose an existing flexo project
 * 
 * @author sguerin
 */
public class OpenProjectComponent extends ProjectChooserComponent {

	private static final Logger logger = Logger.getLogger(OpenProjectComponent.class.getPackage().getName());

	protected OpenProjectComponent(Frame owner, ApplicationContext applicationContext) {
		super(owner, applicationContext);
		logger.info("Build OpenProjectComponent");
	}

	public static File getProjectDirectory(ApplicationContext applicationContext) {
		return getProjectDirectory(FlexoFrame.getActiveFrame(), applicationContext);
	}

	public static File getProjectDirectory(Frame owner, ApplicationContext applicationContext) {
		OpenProjectComponent chooser = new OpenProjectComponent(owner, applicationContext);
		File returned = null;
		int returnVal = -1;
		boolean ok = false;
		while (!ok) {
			try {
				returnVal = chooser.showOpenDialog();
				ok = true;
			} catch (ArrayIndexOutOfBoundsException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Caught ArrayIndexOutOfBoundsException, hope this will stop");
				}
			}
		}
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			returned = chooser.getSelectedFile();
			applicationContext.getAdvancedPrefs().setLastVisitedDirectory(returned.getParentFile());
			// FlexoPreferences.savePreferences(true);
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("No project supplied");
			}
			return null;
		}
		return returned;
	}
}
