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
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.utils.FlexoDocFormat;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.help.FlexoHelp;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.module.AutoSaveService;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.UserType;
import org.openflexo.prefs.ContextPreferences;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.prefs.PreferencesHaveChanged;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.RectangleConverter;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public final class GeneralPreferences extends ContextPreferences {

	private static final Logger logger = Logger.getLogger(GeneralPreferences.class.getPackage().getName());

	private static final Class GENERAL_PREFERENCES = GeneralPreferences.class;

	protected static final String LANGUAGE_KEY = "language";

	protected static final String SMTP_SERVER_KEY = "smtpServer";

	protected static final String FAVORITE_MODULE_KEY = "favoriteModule";

	protected static final String BUG_REPORT_URL_KEY = "secureBugReportDirectActionUrl";

	protected static final String DEFAULT_DOC_FORMAT = "defaultDocFormat";

	protected static final String USER_IDENTIFIER_KEY = "userIdentifier";

	protected static final String LAST_OPENED_PROJECTS_1 = "lastProjects_1";

	protected static final String LAST_OPENED_PROJECTS_2 = "lastProjects_2";

	protected static final String LAST_OPENED_PROJECTS_3 = "lastProjects_3";

	protected static final String LAST_OPENED_PROJECTS_4 = "lastProjects_4";

	protected static final String LAST_OPENED_PROJECTS_5 = "lastProjects_5";

	protected static final String SYNCHRONIZED_BROWSER = "synchronizedBrowser";

	protected static final String INSPECTOR_ON_TOP = "inspector_always_on_top";

	protected static final String CLOSE_POPUP_ON_CLICK_OUT = "close_popup_on_click_out";

	protected static final String NOTIFY_VALID_PROJECT = "notify_valid_project";

	protected static final String BOUNDS_FOR_FRAME = "BoundsForFrame_";

	protected static final String SHOW_LEFT_VIEW = "showBrowserIn";

	protected static final String SHOW_RIGHT_VIEW = "showPaletteIn";

	protected static final String STATE_FOR_FRAME = "StateForFrame_";

	protected static final String AUTO_SAVE_ENABLED = "AutoSaveEnabled";

	protected static final String AUTO_SAVE_INTERVAL = "AutoSaveInterval";

	protected static final String AUTO_SAVE_LIMIT = "AutoSaveLimit";

	private static final String LAST_IMAGE_DIRECTORY = "LAST_IMAGE_DIRECTORY";

	private static final String SPLIT_DIVIDER_LOCATION = "SPLIT_DIVIDER_LOCATION_";

	private static final String LOCAL_RESOURCE_CENTER_DIRECTORY = "localResourceCenterDirectory";

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

	static {
		getPreferences().addObserver(observer);
	}

	public static FlexoPreferences getPreferences() {
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
		updateModuleFrameTitles();
		FlexoLocalization.updateGUILocalized();
		if ((language != null) && (UserType.getCurrentUserType() != null)) {
			FlexoHelp.configure(language.getIdentifier(), UserType.getCurrentUserType().getIdentifier());
			FlexoHelp.reloadHelpSet();
		}
	}

    public static void updateModuleFrameTitles() {
		Enumeration<FlexoModule> en = getModuleLoader().loadedModules();
		while (en.hasMoreElements()) {
			en.nextElement().getFlexoFrame().updateTitle();
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

	public static String getLastOpenedProject1() {
		return getPreferences().getProperty(LAST_OPENED_PROJECTS_1);
	}

	public static void setLastOpenedProject1(String lastOpenedProjects) {
		getPreferences().setProperty(LAST_OPENED_PROJECTS_1, lastOpenedProjects);
	}

	public static String getLastOpenedProject2() {
		return getPreferences().getProperty(LAST_OPENED_PROJECTS_2);
	}

	public static void setLastOpenedProject2(String lastOpenedProjects) {
		getPreferences().setProperty(LAST_OPENED_PROJECTS_2, lastOpenedProjects);
	}

	public static String getLastOpenedProject3() {
		return getPreferences().getProperty(LAST_OPENED_PROJECTS_3);
	}

	public static void setLastOpenedProject3(String lastOpenedProjects) {
		getPreferences().setProperty(LAST_OPENED_PROJECTS_3, lastOpenedProjects);
	}

	public static String getLastOpenedProject4() {
		return getPreferences().getProperty(LAST_OPENED_PROJECTS_4);
	}

	public static void setLastOpenedProject4(String lastOpenedProjects) {
		getPreferences().setProperty(LAST_OPENED_PROJECTS_4, lastOpenedProjects);
	}

	public static String getLastOpenedProject5() {
		return getPreferences().getProperty(LAST_OPENED_PROJECTS_5);
	}

	public static void setLastOpenedProject5(String lastOpenedProjects) {
		getPreferences().setProperty(LAST_OPENED_PROJECTS_5, lastOpenedProjects);
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
		Enumeration<FlexoModule> en = getModuleLoader().loadedModules();
		while (en.hasMoreElements()) {
			FlexoModule module = en.nextElement();
			module.getFlexoController().updateRecentProjectMenu();
		}
	}

    private static ModuleLoader getModuleLoader(){
        return ModuleLoader.instance();
    }

    private static AutoSaveService getAutoSaveService(){
        return AutoSaveService.instance();
    }
	public static void addToLastOpenedProjects(File project) {
		Vector<File> files = getLastOpenedProjects();
		Enumeration<File> en = new Vector<File>(files).elements();
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

	public static String getAutoSaveDirectory() {
		if (getAutoSaveService().getAutoSaveDirectory() != null) {
			return getAutoSaveService().getAutoSaveDirectory().getAbsolutePath();
		} else {
			return FlexoLocalization.localizedForKey("time_traveling_is_disabled");
		}
	}

	public static boolean getAutoSaveEnabled() {
		Boolean autoSaveEnabled = getPreferences().getBooleanProperty(AUTO_SAVE_ENABLED);
		if (autoSaveEnabled == null) {
			setAutoSaveEnabled(true);
			autoSaveEnabled = Boolean.TRUE;
		}
		return autoSaveEnabled;
		// return false;
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
		if (enabled) {
			getAutoSaveService().startAutoSaveThread();
		} else {
			getAutoSaveService().stopAutoSaveThread();
		}
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
			getAutoSaveService().setAutoSaveSleepTime(interval);
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
		getAutoSaveService().setAutoSaveLimit(limit);
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
		return getPreferences().getDirectoryProperty(LOCAL_RESOURCE_CENTER_DIRECTORY);
	}

	public static void setLocalResourceCenterDirectory(File directory) {
		getPreferences().setDirectoryProperty(LOCAL_RESOURCE_CENTER_DIRECTORY, directory);
	}

}
