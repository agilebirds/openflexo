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
package org.openflexo.br.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.Flexo;
import org.openflexo.application.FlexoApplication;

/**
 * Utility class allowing to launch a small application used to visualize bug reports of Flexo
 * 
 * @author sguerin
 */
public class BugReportEditor {

	private static final Logger logger = Logger.getLogger(BugReportEditor.class.getPackage().getName());

	public static void main(String[] args) {

		Flexo.initializeLoggingManager();
		FlexoApplication.initialize();
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Starting BugReportsEditor");
		}
		BugReportViewerWindow w = new BugReportViewerWindow();
		w.setVisible(true);
	}
}
