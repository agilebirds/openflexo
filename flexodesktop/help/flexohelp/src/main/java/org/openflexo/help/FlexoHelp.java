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
package org.openflexo.help;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.help.HelpBroker;
import javax.help.HelpSet;

import org.openflexo.toolbox.FileResource;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FlexoHelp extends Observable {

	private static final Logger logger = Logger.getLogger(FlexoHelp.class.getPackage().getName());

	private static HelpSet _hs = null;
	private static HelpBroker _hb = null;
	private static File _helpSetFile;

	private static String languageIdentifier = null;
	private static String distributionName = null;
	private static boolean initialized = false;

	public static void configure(String aLanguageIdentifier, String aDistributionName) {
		languageIdentifier = aLanguageIdentifier;
		distributionName = aDistributionName;
		initialized = true;
	}

	public static FlexoHelp instance = new FlexoHelp();

	private FlexoHelp() {
		super();
	}

	public static HelpBroker getHelpBroker() {
		if (!initialized) {
			logger.warning("HelpSet not initialized !");
		}
		if (_hb == null && getHelpSet() != null) {
			_hb = getHelpSet().createHelpBroker();
		}
		return _hb;
	}

	public static HelpSet getHelpSet() {
		if (!initialized) {
			logger.warning("HelpSet not initialized !");
		}
		if (_hs == null) {
			_hs = buildHelpSet();
		}
		return _hs;
	}

	public static void reloadHelpSet() {
		reloadHelpSet(null);
	}

	public static void reloadHelpSet(File helpSetFile) {
		if (!initialized) {
			logger.warning("HelpSet not initialized !");
		}
		if (_hs != null) {
			_hs.remove(_hs);
		}
		_hs = null;
		_hb = null;
		_helpSetFile = helpSetFile;
		getHelpSet();
		getHelpBroker();
		instance.setChanged();
		instance.notifyObservers();
	}

	private static HelpSet buildHelpSet() {
		// String helpsetfile = getHelpSetFile();
		HelpSet hs = null;
		// ClassLoader cl = FlexoHelp.class.getClassLoader();
		try {
			// URL hsURL=HelpSet.findHelpSet(cl,helpsetfile);
			URL hsURL = getHelpSetUrl();
			if (hsURL == null) {
				return null;
			}
			hs = new HelpSet(null, hsURL);
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Help files have not been found: " + e.getMessage());
			}
			e.printStackTrace();
		}
		return hs;
	}

	private static URL getHelpSetUrl() throws MalformedURLException {
		if (_helpSetFile == null) {
			_helpSetFile = getMostRecentHelpsetFile();
			if (_helpSetFile == null) {
				return null;
			}
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("HelpSetFile:" + _helpSetFile.getAbsolutePath());
		}
		return _helpSetFile.toURL();
	}

	public static File getHelpSetDirectory() {
		return new FileResource("Help");
	}

	private static File getMostRecentHelpsetFile() {
		String endPattern = "_" + distributionName + "_" + languageIdentifier + ".helpset";

		File[] allFiles = getHelpSetDirectory().listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (!pathname.isDirectory()) {
					return false;
				}
				File[] all_Files = pathname.listFiles();
				if (all_Files == null) {
					return false;
				}
				for (int i = 0; i < all_Files.length; i++) {
					if (all_Files[i].getName().endsWith(".hs")) {
						return true;
					}
				}
				return false;
			}
		});
		if (allFiles == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Missing help directory: " + getHelpSetDirectory().getAbsolutePath());
			}
			return null;
		}
		File directory = null;
		for (int i = 0; i < allFiles.length; i++) {
			if (allFiles[i].getName().endsWith(endPattern) && (directory == null || allFiles[i].lastModified() > directory.lastModified())) {
				directory = allFiles[i];
				break;
			}
		}
		if (directory == null) {
			for (int i = 0; i < allFiles.length; i++) {
				if (allFiles[i].getName().endsWith(".helpset")
						&& (directory == null || allFiles[i].lastModified() > directory.lastModified())) {
					directory = allFiles[i];
				}
			}
		}
		if (directory == null) {
			return null;
		}
		allFiles = directory.listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			if (allFiles[i].getName().endsWith(".hs")) {
				return allFiles[i];
			}
		}
		return null;
	}

	public static boolean isAvailable() {
		return getHelpSet() != null && getHelpBroker() != null;
	}

	/*
	 * private static String getHelpSetFile() { //return ".."+File.separator+"FlexoHelp"+File.separator+"Help"+File.separator+"Flexo.hs";
	 * String helpSetFile = new FileResource(File.separator+"Help"+File.separator+"Flexo.hs").getAbsolutePath(); if
	 * (logger.isLoggable(Level.INFO)) logger.info("HelpSetFile:"+helpSetFile); return helpSetFile; }
	 */

}