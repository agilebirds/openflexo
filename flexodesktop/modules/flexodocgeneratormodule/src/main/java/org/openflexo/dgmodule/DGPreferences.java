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
package org.openflexo.dgmodule;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.doceditor.DEPreferences;
import org.openflexo.module.GeneratedResourceModifiedChoice;
import org.openflexo.module.Module;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.LatexUtils;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DGPreferences extends DEPreferences {

	private static final Logger logger = Logger.getLogger(DGPreferences.class.getPackage().getName());

	private static final Class DG_PREFERENCES = DGPreferences.class;

	protected static final String automaticallyDismissUnchangedFiles = "DGautomaticallyDismissUnchangedFiles";

	protected static final String latexCommand = "latexCommand";

	protected static final String openPDF = "openPDF";

	protected static final String openDocx = "openDocx";

	protected static final String showZIP = "showZIP";

	protected static final String choiceWhenGeneratedResourceModifiedKey = "DG-generatedResourceModifiedChoice";

	protected static final String saveBeforeGeneratingKey = "DG-saveBeforeGenerating";

	protected static final String pdfLatexTimeout = "pdfLatexTimeout";

	public static void init() {
		preferences(DG_PREFERENCES);
	}

	public DGPreferences() {
		super(Module.DG_MODULE);
	}

	@Override
	public File getInspectorFile() {
		return new FileResource("Config/Preferences/DGPrefs.inspector");
	}

	public static GeneratedResourceModifiedChoice getGeneratedResourceModifiedChoice() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getGeneratedResourceModifiedChoice");
		}
		return GeneratedResourceModifiedChoice.get(preferences(DG_PREFERENCES).getProperty(choiceWhenGeneratedResourceModifiedKey));
	}

	public static void setGeneratedResourceModifiedChoice(GeneratedResourceModifiedChoice choice) {
		if (choice != null) {
			preferences(DG_PREFERENCES).setProperty(choiceWhenGeneratedResourceModifiedKey, choice.getIdentifier());
		}
	}

	/**
	 * Returns wheter to automatically dismiss unchanged files after a generation pass or let the user do it manually.
	 * 
	 * @return wheter to automatically dismiss unchanged files after a generation pass or let the user do it manually.
	 */
	public static boolean getAutomaticallyDismissUnchangedFiles() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getAutomaticallyDismissUnchangedFiles");
		}
		Boolean returned = preferences(DG_PREFERENCES).getBooleanProperty(automaticallyDismissUnchangedFiles);
		if (returned == null) {
			setAutomaticallyDismissUnchangedFiles(true);
			return true;
		}
		return returned;
	}

	public static void setAutomaticallyDismissUnchangedFiles(boolean choice) {
		preferences(DG_PREFERENCES).setBooleanProperty(automaticallyDismissUnchangedFiles, choice);
	}

	/**
	 * Returns wheter to automatically dismiss unchanged files after a generation pass or let the user do it manually.
	 * 
	 * @return wheter to automatically dismiss unchanged files after a generation pass or let the user do it manually.
	 */
	public static String getLatexCommand() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getLatexCommand");
		}
		String returned = preferences(DG_PREFERENCES).getProperty(latexCommand);
		if (returned == null) {
			String cmd = LatexUtils.getDefaultLatex2PDFCommand();
			if (cmd == null) {
				setLatexCommand(LatexUtils.TEXIFY);
			} else {
				setLatexCommand(cmd);
			}
			return getLatexCommand();
		}
		return returned;
	}

	public static void setLatexCommand(String latexCommandValue) {
		preferences(DG_PREFERENCES).setProperty(latexCommand, latexCommandValue);
	}

	public static Boolean getOpenPDF() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getOpenPDF");
		}
		Boolean returned = preferences(DG_PREFERENCES).getBooleanProperty(openPDF);
		if (returned == null) {
			setOpenPDF(true);
			return getOpenPDF();
		}
		return returned;
	}

	public static void setOpenPDF(Boolean value) {
		preferences(DG_PREFERENCES).setBooleanProperty(openPDF, value);
	}

	public static Boolean getOpenDocx() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getOpenDocx");
		}
		Boolean returned = preferences(DG_PREFERENCES).getBooleanProperty(openDocx);
		if (returned == null) {
			setOpenDocx(true);
			return true;
		}
		return returned;
	}

	public static void setOpenDocx(Boolean value) {
		preferences(DG_PREFERENCES).setBooleanProperty(openDocx, value);
	}

	public static Boolean getShowZIP() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getShowZIP");
		}
		Boolean returned = preferences(DG_PREFERENCES).getBooleanProperty(showZIP);
		if (returned == null) {
			setShowZIP(true);
			return getShowZIP();
		}
		return returned;
	}

	public static void setShowZIP(Boolean value) {
		preferences(DG_PREFERENCES).setBooleanProperty(showZIP, value);
	}

	public static boolean getSaveBeforeGenerating() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getSaveBeforeGenerating");
		}
		Boolean returned = preferences(DG_PREFERENCES).getBooleanProperty(saveBeforeGeneratingKey);
		if (returned == null) {
			setSaveBeforeGenerating(true);
			return true;
		}
		return returned;
	}

	public static void setSaveBeforeGenerating(boolean aBoolean) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setSaveBeforeGenerating");
		}
		preferences(DG_PREFERENCES).setBooleanProperty(saveBeforeGeneratingKey, aBoolean);

	}

	/**
	 * 
	 * @return the number of seconds to wait before affirming that pdflatex has timed out
	 */
	public static Integer getPdfLatexTimeout() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getPdfLatexTimeout");
		}
		Integer returned = preferences(DG_PREFERENCES).getIntegerProperty(pdfLatexTimeout);
		if (returned == null) {
			setPdfLatexTimeout(15);
			return 15;
		}
		return returned;
	}

	public static void setPdfLatexTimeout(Integer timeout) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setPdfLatexTimeout");
		}
		preferences(DG_PREFERENCES).setIntegerProperty(pdfLatexTimeout, timeout);

	}

}
