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
package org.openflexo;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.utils.FlexoDocFormat;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.help.FlexoHelp;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.module.UserType;
import org.openflexo.prefs.ContextPreferences;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.prefs.PreferencesHaveChanged;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.RectangleConverter;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public final class GeneralPreferences extends ContextPreferences {

	private static final Logger logger = Logger.getLogger(GeneralPreferences.class.getPackage().getName());

	private static final Class<GeneralPreferences> GENERAL_PREFERENCES = GeneralPreferences.class;

	public static final String LANGUAGE_KEY = "language";

	public static final String SMTP_SERVER_KEY = "smtpServer";

	public static final String FAVORITE_MODULE_KEY = "favoriteModule";

	public static final String BUG_REPORT_URL_KEY = "secureBugReportDirectActionUrl";

	public static final String DEFAULT_DOC_FORMAT = "defaultDocFormat";

	public static final String USER_IDENTIFIER_KEY = "userIdentifier";

	public static final String LAST_SERVER_PROJECTS = "lastServerProjects";

	public static final String LAST_OPENED_PROJECTS = "lastProjects";

	public static final String SYNCHRONIZED_BROWSER = "synchronizedBrowser";

	public static final String INSPECTOR_ON_TOP = "inspector_always_on_top";

	public static final String CLOSE_POPUP_ON_CLICK_OUT = "close_popup_on_click_out";

	public static final String NOTIFY_VALID_PROJECT = "notify_valid_project";

	public static final String BOUNDS_FOR_FRAME = "BoundsForFrame_";

	public static final String SHOW_LEFT_VIEW = "showBrowserIn";

	public static final String SHOW_RIGHT_VIEW = "showPaletteIn";

	public static final String STATE_FOR_FRAME = "StateForFrame_";

	public static final String LAYOUT_FOR = "LayoutFor_";

	public static final String AUTO_SAVE_ENABLED = "AutoSaveEnabled";

	public static final String AUTO_SAVE_INTERVAL = "AutoSaveInterval";

	public static final String AUTO_SAVE_LIMIT = "AutoSaveLimit";

	private static final String LAST_IMAGE_DIRECTORY = "LAST_IMAGE_DIRECTORY";

	private static final String SPLIT_DIVIDER_LOCATION = "SPLIT_DIVIDER_LOCATION_";

	private static final String LOCAL_RESOURCE_CENTER_DIRECTORY = "localResourceCenterDirectory";

	private static final String LOCAL_RESOURCE_CENTER_DIRECTORY2 = "localResourceCenterDirectory2";

	private static final String REMEMBER_LAST_PROJECTS_SIZE = "rememberLastProjectSize";

	private static final FlexoObserver observer = new FlexoObserver() {

		@Override
		public void update(FlexoObservable observable, DataModification dataModification) {
			if (dataModification instanceof PreferencesHaveChanged
					&& ((PreferencesHaveChanged) dataModification).propertyName().equals(LANGUAGE_KEY)) {
				if (dataModification.oldValue() != dataModification.newValue()) {
					setLanguage(getLanguage());
				}
			}
		}

	};

	private static final String RESOURCE_LOCATION = "_location";

	static {
		getPreferences().addObserver(observer);
	}

	public static GeneralPreferences getPreferences() {
		return preferences(GENERAL_PREFERENCES);
	}

	@Override
	public String getName() {
		return "general";
	}

	@Override
	public File getInspectorFile() {
		return new FileResource("Config/Preferences/GeneralPrefs.inspector");
		// return new File
		// (ModuleLoader.getWorkspaceDirectory(),"Flexo/Config/Preferences/GeneralPrefs.inspector");
	}

	public static Language getLanguage() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getLanguage");
		}
		return Language.get(getPreferences().getProperty(LANGUAGE_KEY));
	}

	public static void setLanguage(Language language) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setLanguage");
		}
		if (language != null) {
			getPreferences().setProperty(LANGUAGE_KEY, language.getName());
		}
		if (language != null && language.equals(Language.FRENCH)) {
			Locale.setDefault(Locale.FRANCE);
		} else {
			Locale.setDefault(Locale.US);
		}
		FlexoLocalization.setCurrentLanguage(language);
		FlexoLocalization.updateGUILocalized();
		if (language != null && UserType.getCurrentUserType() != null) {
			FlexoHelp.configure(language.getIdentifier(), UserType.getCurrentUserType().getIdentifier());
			FlexoHelp.reloadHelpSet();
		}
	}

	public static String getSmtpServer() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getSmtpServer");
		}
		return getPreferences().getProperty(SMTP_SERVER_KEY);
	}

	public static void setSmtpServer(String hasWebobjects) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setSmtpServer");
		}
		getPreferences().setProperty(SMTP_SERVER_KEY, hasWebobjects);
	}

	public static String getFavoriteModuleName() {
		return getPreferences().getProperty(FAVORITE_MODULE_KEY);
	}

	public static void setFavoriteModuleName(String value) {
		getPreferences().setProperty(FAVORITE_MODULE_KEY, value);
	}

	public static boolean getSynchronizedBrowser() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getSynchronizedBrowser");
		}
		return getPreferences().getProperty(SYNCHRONIZED_BROWSER) != "false";
	}

	public static void setSynchronizedBrowser(boolean synchronizedBrowser) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setSynchronizedBrowser");
		}
		getPreferences().setProperty(SYNCHRONIZED_BROWSER, synchronizedBrowser ? "true" : "false");
	}

	public static File getLastOpenedProject(int i) {
		return getPreferences().getDirectoryProperty(LAST_OPENED_PROJECTS + "_" + i, true);
	}

	public static void setLastOpenedProject(File f, int i) {
		getPreferences().setDirectoryProperty(LAST_OPENED_PROJECTS + "_" + i, f, true);
	}

	public static int getRememberLastProjectsSize() {
		Integer r = getPreferences().getIntegerProperty(REMEMBER_LAST_PROJECTS_SIZE);
		if (r == null) {
			setRememberLastProjectsSize(r = 10);
		}
		return r;
	}

	public static void setRememberLastProjectsSize(int i) {
		getPreferences().setIntegerProperty(REMEMBER_LAST_PROJECTS_SIZE, i);
	}

	public static Vector<File> getRememberedLastOpenedProjects() {
		return getLastOpenedProjects(getRememberLastProjectsSize());
	}

	public static Vector<File> getLastOpenedProjects(int maxListSize) {
		Vector<File> files = new Vector<File>();
		int i = 1;
		File f;
		while ((maxListSize < 0 || i <= maxListSize) && (f = getLastOpenedProject(i++)) != null) {
			files.add(f);
		}
		return files;
	}

	public static Vector<File> getLastOpenedProjects() {
		return getLastOpenedProjects(-1);
	}

	/**
	 * @param files
	 */
	public static void setLastOpenedProjects(Vector<File> files) {
		int i = 1;
		for (File f : files) {
			setLastOpenedProject(f, i++);
		}
	}

	public static void addToLastOpenedProjects(File project) {
		Vector<File> files = getLastOpenedProjects();
		Iterator<File> i = files.iterator();
		while (i.hasNext()) {
			File file = i.next();
			if (project.equals(file)) {
				i.remove();
			}
		}
		files.insertElementAt(project, 0);
		setLastOpenedProjects(files);
	}

	public static void removeFromLastOpenedProjects(File project) {
		Vector<File> files = getLastOpenedProjects();
		while (files.remove(project)) {
			;
		}
		setLastOpenedProjects(files);
	}

	public static List<String> getLastServerProjects() {
		String s = getPreferences().getProperty(LAST_SERVER_PROJECTS);
		if (s == null || s.length() == 0) {
			return Collections.emptyList();
		} else {
			return Arrays.asList(StringUtils.split(s, '|'));
		}
	}

	public static void setLastServerProjects(List<String> serverProjects) {
		if (serverProjects.size() > 0) {
			StringBuilder sb = new StringBuilder(serverProjects.size() * 75);
			for (String string : serverProjects) {
				if (sb.length() > 0) {
					sb.append('|');
				}
				sb.append(string);
			}
			getPreferences().setProperty(LAST_SERVER_PROJECTS, sb.toString());
		} else {
			getPreferences().setProperty(LAST_SERVER_PROJECTS, null);
		}
	}

	public static void addToLastServerProjects(String projectURI) {
		List<String> list = new ArrayList<String>(getLastServerProjects());
		while (list.remove(projectURI)) {
			;
		}
		list.add(0, projectURI);
		if (list.size() > 50) {
			list = list.subList(0, 50);
		}
		setLastServerProjects(list);
	}

	public static boolean isValidationRuleEnabled(ValidationRule rule) {
		Boolean b = getPreferences().getBooleanProperty("VR-" + rule.getClass().getName());
		return b == null || b.booleanValue();
	}

	public static void setValidationRuleEnabled(ValidationRule rule, boolean enabled) {
		getPreferences().setBooleanProperty("VR-" + rule.getClass().getName(), enabled);
	}

	public static String getUserIdentifier() {
		String returned = getPreferences().getProperty(USER_IDENTIFIER_KEY);
		if (returned == null) {
			String userName = System.getProperty("user.name");
			if (userName.length() > 3) {
				returned = userName.substring(0, 3);
				returned = returned.toUpperCase();
			} else if (userName.length() > 0) {
				returned = userName.substring(0, userName.length());
				returned = returned.toUpperCase();
			} else {
				returned = "FLX";
			}
			setUserIdentifier(returned);
		}
		return returned;
	}

	public static void setUserIdentifier(String aUserIdentifier) {
		getPreferences().setProperty(USER_IDENTIFIER_KEY, aUserIdentifier);
		FlexoModelObject.setCurrentUserIdentifier(aUserIdentifier);
	}

	/*
	 * public static boolean getInspectorAlwaysOnTop() { String answer = preferences(GENERAL_PREFERENCES).getProperty(INSPECTOR_ON_TOP); if
	 * (answer == null) { setInspectorAlwaysOnTop(true); return true; } return Boolean.parseBoolean(answer); }
	 * 
	 * public static void setInspectorAlwaysOnTop(boolean alwaysOnTop) { preferences(GENERAL_PREFERENCES).setProperty(INSPECTOR_ON_TOP,
	 * String.valueOf(alwaysOnTop)); if (FlexoSharedInspectorController.hasSharedInstance()) {
	 * FlexoSharedInspectorController.sharedInstance().setInspectorWindowsAlwaysOnTop(alwaysOnTop); } }
	 */

	public static boolean getNotifyValidProject() {
		Boolean answer = getPreferences().getBooleanProperty(NOTIFY_VALID_PROJECT);
		if (answer == null) {
			setNotifyValidProject(true);
			return true;
		}
		return answer;
	}

	public static void setNotifyValidProject(boolean alwaysOnTop) {
		getPreferences().setBooleanProperty(NOTIFY_VALID_PROJECT, alwaysOnTop);
	}

	public static FlexoDocFormat getDefaultDocFormat() {
		FlexoDocFormat returned = FlexoDocFormat.get(getPreferences().getProperty(DEFAULT_DOC_FORMAT));
		if (returned == null) {
			returned = FlexoDocFormat.HTML;
			setDefaultDocFormat(returned);
		}
		return returned;
	}

	public static void setDefaultDocFormat(FlexoDocFormat value) {
		getPreferences().setProperty(DEFAULT_DOC_FORMAT, FlexoDocFormat.flexoDocFormatConverter.convertToString(value));
	}

	public static Rectangle getBoundForFrameWithID(String id) {
		String returned = getPreferences().getProperty(BOUNDS_FOR_FRAME + id);
		return RectangleConverter.instance.convertFromString(returned);
	}

	public static void setBoundForFrameWithID(String id, Rectangle bounds) {
		getPreferences().setProperty(BOUNDS_FOR_FRAME + id, RectangleConverter.instance.convertToString(bounds));
	}

	public static boolean getShowLeftView(String id) {
		Boolean value = getPreferences().getBooleanProperty(SHOW_LEFT_VIEW + id);
		if (value == null) {
			return Boolean.TRUE;
		}
		return value;
	}

	public static void setShowLeftView(String id, boolean status) {
		getPreferences().setBooleanProperty(SHOW_LEFT_VIEW + id, status);
	}

	public static boolean getShowRightView(String id) {
		Boolean value = getPreferences().getBooleanProperty(SHOW_RIGHT_VIEW + id);
		if (value == null) {
			return Boolean.TRUE;
		}
		return value;
	}

	public static void setShowRightView(String id, boolean status) {
		getPreferences().setBooleanProperty(SHOW_RIGHT_VIEW + id, status);
	}

	public static int getFrameStateForFrameWithID(String id) {
		Integer i = getPreferences().getIntegerProperty(STATE_FOR_FRAME + id);
		if (i == null) {
			return -1;
		} else {
			return i;
		}
	}

	/**
	 * @param extendedState
	 */
	public static void setFrameStateForFrameWithID(String id, int extendedState) {
		getPreferences().setIntegerProperty(STATE_FOR_FRAME + id, extendedState);
	}

	public static String getLayoutFor(String id) {
		return getPreferences().getProperty(LAYOUT_FOR + id);
	}

	/**
	 * @param extendedState
	 */
	public static void setLayoutFor(String layout, String id) {
		getPreferences().setProperty(LAYOUT_FOR + id, layout);
	}

	public static boolean getAutoSaveEnabled() {
		Boolean autoSaveEnabled = getPreferences().getBooleanProperty(AUTO_SAVE_ENABLED);
		if (autoSaveEnabled == null) {
			setAutoSaveEnabled(true);
			autoSaveEnabled = Boolean.TRUE;
		}
		return autoSaveEnabled;
	}

	public static boolean isAutoSavedEnabled() {
		Boolean autoSaveEnabled = getPreferences().getBooleanProperty(AUTO_SAVE_ENABLED);
		if (autoSaveEnabled == null) {
			return false;
		}
		return autoSaveEnabled;
	}

	public static boolean isAutoSavedPrefDefined() {
		Boolean autoSaveEnabled = getPreferences().getBooleanProperty(AUTO_SAVE_ENABLED);
		return autoSaveEnabled != null;
	}

	public static void setAutoSaveEnabled(boolean enabled) {
		getPreferences().setBooleanProperty(AUTO_SAVE_ENABLED, enabled);
	}

	/**
	 * 
	 * @return the number of <b>minutes</b> to wait between 2 saves
	 */
	public static int getAutoSaveInterval() {
		Integer interval = getPreferences().getIntegerProperty(AUTO_SAVE_INTERVAL);
		if (interval == null) {
			setAutoSaveInterval(5);
			interval = 5;
		}
		return interval;
	}

	public static void setAutoSaveInterval(int interval) {
		if (interval > 0) {
			getPreferences().setIntegerProperty(AUTO_SAVE_INTERVAL, interval);
		}
	}

	/**
	 * 
	 * @return the maximum number of automatic save to perform before deleting the first one
	 */
	public static int getAutoSaveLimit() {
		Integer limit = getPreferences().getIntegerProperty(AUTO_SAVE_LIMIT);
		if (limit == null) {
			setAutoSaveLimit(12);
			limit = 12;
		}
		return limit;
	}

	public static void setAutoSaveLimit(int limit) {
		getPreferences().setIntegerProperty(AUTO_SAVE_LIMIT, limit);
	}

	public static File getLastImageDirectory() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getLastImageDirectory");
		}
		return getPreferences().getDirectoryProperty(LAST_IMAGE_DIRECTORY, true);
	}

	public static void setLastImageDirectory(File f) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setLastImageDirectory");
		}
		getPreferences().setDirectoryProperty(LAST_IMAGE_DIRECTORY, f, true);
	}

	public static void save() {
		FlexoPreferences.savePreferences(true);
	}

	public static int getDividerLocationForSplitPaneWithID(String id) {
		Integer interval = getPreferences().getIntegerProperty(SPLIT_DIVIDER_LOCATION + id);
		if (interval == null) {
			setDividerLocationForSplitPaneWithID(-1, id);
			interval = -1;
		}
		return interval;
	}

	public static void setDividerLocationForSplitPaneWithID(int value, String id) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setDividerLocationForSplitPaneWithID: " + id + " to " + value);
		}
		getPreferences().setIntegerProperty(SPLIT_DIVIDER_LOCATION + id, value);
	}

	public static File getLocalResourceCenterDirectory() {
		File file = getPreferences().getDirectoryProperty(LOCAL_RESOURCE_CENTER_DIRECTORY2);
		if (file == null) {
			file = getPreferences().getDirectoryProperty(LOCAL_RESOURCE_CENTER_DIRECTORY);
			if (file == null || file.isFile()) {
				setLocalResourceCenterDirectory(file = new File(FileUtils.getApplicationDataDirectory(), "FlexoResourceCenter"));
			}
		}
		return file;
	}

	public static void setLocalResourceCenterDirectory(File directory) {
		getPreferences().setDirectoryProperty(LOCAL_RESOURCE_CENTER_DIRECTORY2, directory);
	}

	public static File getLocationForResource(String uri) {
		return getPreferences().getFileProperty(uri + RESOURCE_LOCATION);
	}

	public static void setLocationForResource(File file, String uri) {
		getPreferences().setFileProperty(uri + RESOURCE_LOCATION, file, false);
	}
}
