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
package org.openflexo.fps;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.module.Module;
import org.openflexo.prefs.ModulePreferences;
import org.openflexo.toolbox.FileResource;

/**
 * Contains preferences for this module
 * 
 * @author yourname
 * 
 */
public final class FPSPreferences extends ModulePreferences {

	private static final Logger logger = Logger.getLogger(FPSPreferences.class.getPackage().getName());

	private static final Class FPS_PREFERENCES = FPSPreferences.class;

	protected static final String LAST_OPENED_PROJECTS_1 = "FPSLastProjects_1";

	protected static final String LAST_OPENED_PROJECTS_2 = "FPSLastProjects_2";

	protected static final String LAST_OPENED_PROJECTS_3 = "FPSLastProjects_3";

	protected static final String LAST_OPENED_PROJECTS_4 = "FPSLastProjects_4";

	protected static final String LAST_OPENED_PROJECTS_5 = "FPSLastProjects_5";

	protected static final String EXAMPLE_PREF_VALUE = "examplePrefValue";

	protected static final String XMLMERGE_PREF_VALUE = "useXMLMerge";

	public static void init() {
		preferences(FPS_PREFERENCES);
		CVSFile.xmlDiff3MergeEnabled = getUseXMLMerge();
	}

	public FPSPreferences() {
		super(Module.FPS_MODULE);
	}

	@Override
	public File getInspectorFile() {
		return new FileResource("Config/Preferences/FPSPrefs.inspector");
	}

	public static Boolean getExamplePrefValue() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getExamplePrefValue");
		}
		Boolean value = preferences(FPS_PREFERENCES).getBooleanProperty(EXAMPLE_PREF_VALUE);
		if (value == null) {
			setExamplePrefValue(Boolean.FALSE);
			return getExamplePrefValue();
		}
		return value;
	}

	public static void setExamplePrefValue(Boolean alignOnGrid) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setExamplePrefValue");
		}
		preferences(FPS_PREFERENCES).setBooleanProperty(EXAMPLE_PREF_VALUE, alignOnGrid);
	}

	public static Boolean getUseXMLMerge() {
		Boolean value = preferences(FPS_PREFERENCES).getBooleanProperty(XMLMERGE_PREF_VALUE);
		if (value == null) {
			setUseXMLMerge(Boolean.FALSE);
			return getUseXMLMerge();
		}
		return value;
	}

	public static void setUseXMLMerge(Boolean alignOnGrid) {
		preferences(FPS_PREFERENCES).setBooleanProperty(XMLMERGE_PREF_VALUE, alignOnGrid);
		CVSFile.xmlDiff3MergeEnabled = getUseXMLMerge();
	}

	public static String getLastOpenedProject1() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getLastOpenedProjects");
		}
		return preferences(FPS_PREFERENCES).getProperty(LAST_OPENED_PROJECTS_1);
	}

	public static void setLastOpenedProject1(String lastOpenedProjects) {
		preferences(FPS_PREFERENCES).setProperty(LAST_OPENED_PROJECTS_1, lastOpenedProjects);
	}

	public static String getLastOpenedProject2() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getLastOpenedProjects");
		}
		return preferences(FPS_PREFERENCES).getProperty(LAST_OPENED_PROJECTS_2);
	}

	public static void setLastOpenedProject2(String lastOpenedProjects) {
		preferences(FPS_PREFERENCES).setProperty(LAST_OPENED_PROJECTS_2, lastOpenedProjects);
	}

	public static String getLastOpenedProject3() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getLastOpenedProjects");
		}
		return preferences(FPS_PREFERENCES).getProperty(LAST_OPENED_PROJECTS_3);
	}

	public static void setLastOpenedProject3(String lastOpenedProjects) {
		preferences(FPS_PREFERENCES).setProperty(LAST_OPENED_PROJECTS_3, lastOpenedProjects);
	}

	public static String getLastOpenedProject4() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getLastOpenedProjects");
		}
		return preferences(FPS_PREFERENCES).getProperty(LAST_OPENED_PROJECTS_4);
	}

	public static void setLastOpenedProject4(String lastOpenedProjects) {
		preferences(FPS_PREFERENCES).setProperty(LAST_OPENED_PROJECTS_4, lastOpenedProjects);
	}

	public static String getLastOpenedProject5() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getLastOpenedProjects");
		}
		return preferences(FPS_PREFERENCES).getProperty(LAST_OPENED_PROJECTS_5);
	}

	public static void setLastOpenedProject5(String lastOpenedProjects) {
		preferences(FPS_PREFERENCES).setProperty(LAST_OPENED_PROJECTS_5, lastOpenedProjects);
	}

	public static Vector<File> getLastOpenedProjects() {
		Vector<File> files = new Vector<File>();
		String s1 = getLastOpenedProject1();
		String s2 = getLastOpenedProject2();
		String s3 = getLastOpenedProject3();
		String s4 = getLastOpenedProject4();
		String s5 = getLastOpenedProject5();
		File f1 = null;
		File f2 = null;
		File f3 = null;
		File f4 = null;
		File f5 = null;
		if (s1 != null) {
			f1 = new File(s1);
			if (f1.exists()) {
				files.add(f1);
			}
		}
		if (s2 != null) {
			f2 = new File(s2);
			if (f2.exists()) {
				files.add(f2);
			}
		}
		if (s3 != null) {
			f3 = new File(s3);
			if (f3.exists()) {
				files.add(f3);
			}
		}
		if (s4 != null) {
			f4 = new File(s4);
			if (f4.exists()) {
				files.add(f4);
			}
		}
		if (s5 != null) {
			f5 = new File(s5);
			if (f5.exists()) {
				files.add(f5);
			}
		}
		return files;
	}

	/**
	 * @param files
	 */
	public static void setLastOpenedProjects(Vector<File> files) {
		if (files.size() > 0) {
			setLastOpenedProject1(files.get(0).getAbsolutePath());
		}
		if (files.size() > 1) {
			setLastOpenedProject2(files.get(1).getAbsolutePath());
		}
		if (files.size() > 2) {
			setLastOpenedProject3(files.get(2).getAbsolutePath());
		}
		if (files.size() > 3) {
			setLastOpenedProject4(files.get(3).getAbsolutePath());
		}
		if (files.size() > 4) {
			setLastOpenedProject5(files.get(4).getAbsolutePath());
		}
	}

	public static void addToLastOpenedProjects(File project) {
		Vector<File> files = getLastOpenedProjects();
		Enumeration<File> en = ((Vector<File>) files.clone()).elements();
		while (en.hasMoreElements()) {
			File f = en.nextElement();
			if (project.equals(f)) {
				files.remove(f);
				break;
			}
		}
		files.insertElementAt(project, 0);
		setLastOpenedProjects(files);
	}

}
