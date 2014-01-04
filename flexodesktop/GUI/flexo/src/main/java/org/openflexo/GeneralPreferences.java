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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.help.FlexoHelp;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.prefs.PreferencesContainer;
import org.openflexo.toolbox.RectangleConverter;
import org.openflexo.toolbox.StringUtils;

/**
 * Encodes general preferences for the whole application
 * 
 * @author sguerin
 * 
 */
@ModelEntity
@ImplementationClass(GeneralPreferences.GeneralPreferencesImpl.class)
@XMLElement(xmlTag = "GeneralPreferences")
public interface GeneralPreferences extends PreferencesContainer {

	public static final String LANGUAGE_KEY = "language";
	public static final String SMTP_SERVER_KEY = "smtpServer";
	public static final String FAVORITE_MODULE_KEY = "favoriteModule";
	public static final String BUG_REPORT_URL_KEY = "secureBugReportDirectActionUrl";
	public static final String DEFAULT_DOC_FORMAT = "defaultDocFormat";
	public static final String USER_IDENTIFIER_KEY = "userIdentifier";
	public static final String LAST_OPENED_PROJECTS_1 = "lastProjects_1";
	public static final String LAST_OPENED_PROJECTS_2 = "lastProjects_2";
	public static final String LAST_OPENED_PROJECTS_3 = "lastProjects_3";
	public static final String LAST_OPENED_PROJECTS_4 = "lastProjects_4";
	public static final String LAST_OPENED_PROJECTS_5 = "lastProjects_5";
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
	public static final String LAST_IMAGE_DIRECTORY = "LAST_IMAGE_DIRECTORY";
	public static final String SPLIT_DIVIDER_LOCATION = "SPLIT_DIVIDER_LOCATION_";

	public static final String LOCAL_RESOURCE_CENTER_DIRECTORY = "localResourceCenterDirectory";

	public static final String LOCAL_RESOURCE_CENTER_DIRECTORY2 = "localResourceCenterDirectory2";

	public static final String DIRECTORY_RESOURCE_CENTER_LIST = "directoryResourceCenterList";

	/*private static final FlexoObserver observer = new FlexoObserver() {

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
	}*/

	/*public static GeneralPreferences getPreferences() {
		return preferences(GENERAL_PREFERENCES);
	}*/

	/*@Override
	public File getInspectorFile() {
		return new FileResource("Config/Preferences/GeneralPrefs.inspector");
	}*/

	@Getter(LANGUAGE_KEY)
	@XMLAttribute
	public Language getLanguage();

	@Setter(LANGUAGE_KEY)
	public void setLanguage(Language language);

	@Getter(SMTP_SERVER_KEY)
	@XMLAttribute
	public String getSmtpServer();

	@Setter(SMTP_SERVER_KEY)
	public void setSmtpServer(String smtpServer);

	@Getter(FAVORITE_MODULE_KEY)
	@XMLAttribute
	public String getFavoriteModuleName();

	@Setter(FAVORITE_MODULE_KEY)
	public void setFavoriteModuleName(String value);

	@Getter(SYNCHRONIZED_BROWSER)
	@XMLAttribute
	public boolean getSynchronizedBrowser();

	@Setter(SYNCHRONIZED_BROWSER)
	public void setSynchronizedBrowser(boolean synchronizedBrowser);

	@Getter(LAST_OPENED_PROJECTS_1)
	@XMLAttribute
	public String getLastOpenedProject1();

	@Setter(LAST_OPENED_PROJECTS_1)
	public void setLastOpenedProject1(String lastOpenedProjects);

	@Getter(LAST_OPENED_PROJECTS_2)
	@XMLAttribute
	public String getLastOpenedProject2();

	@Setter(LAST_OPENED_PROJECTS_2)
	public void setLastOpenedProject2(String lastOpenedProjects);

	@Getter(LAST_OPENED_PROJECTS_3)
	@XMLAttribute
	public String getLastOpenedProject3();

	@Setter(LAST_OPENED_PROJECTS_3)
	public void setLastOpenedProject3(String lastOpenedProjects);

	@Getter(LAST_OPENED_PROJECTS_4)
	@XMLAttribute
	public String getLastOpenedProject4();

	@Setter(LAST_OPENED_PROJECTS_4)
	public void setLastOpenedProject4(String lastOpenedProjects);

	@Getter(LAST_OPENED_PROJECTS_5)
	@XMLAttribute
	public String getLastOpenedProject5();

	@Setter(LAST_OPENED_PROJECTS_5)
	public void setLastOpenedProject5(String lastOpenedProjects);

	public List<File> getLastOpenedProjects();

	public void setLastOpenedProjects(List<File> files);

	public void addToLastOpenedProjects(File project);

	public boolean isValidationRuleEnabled(ValidationRule<?, ?> rule);

	public void setValidationRuleEnabled(ValidationRule<?, ?> rule, boolean enabled);

	@Override
	@Getter(USER_IDENTIFIER_KEY)
	@XMLAttribute
	public String getUserIdentifier();

	@Override
	@Setter(USER_IDENTIFIER_KEY)
	public void setUserIdentifier(String aUserIdentifier);

	@Getter(value = NOTIFY_VALID_PROJECT, defaultValue = "true")
	@XMLAttribute
	public boolean getNotifyValidProject();

	@Setter(NOTIFY_VALID_PROJECT)
	public void setNotifyValidProject(boolean flag);

	public Rectangle getBoundForFrameWithID(String id);

	public void setBoundForFrameWithID(String id, Rectangle bounds);

	public boolean getShowLeftView(String id);

	public void setShowLeftView(String id, boolean status);

	public boolean getShowRightView(String id);

	public void setShowRightView(String id, boolean status);

	public int getFrameStateForFrameWithID(String id);

	/**
	 * @param extendedState
	 */
	public void setFrameStateForFrameWithID(String id, int extendedState);

	public String getLayoutFor(String id);

	/**
	 * @param extendedState
	 */
	public void setLayoutFor(String layout, String id);

	@Getter(value = AUTO_SAVE_ENABLED, defaultValue = "true")
	@XMLAttribute
	public boolean getAutoSaveEnabled();

	@Setter(AUTO_SAVE_ENABLED)
	public void setAutoSaveEnabled(boolean enabled);

	@Getter(value = AUTO_SAVE_INTERVAL, defaultValue = "5")
	@XMLAttribute
	public int getAutoSaveInterval();

	@Setter(AUTO_SAVE_INTERVAL)
	public void setAutoSaveInterval(int interval);

	/**
	 * 
	 * @return the maximum number of automatic save to perform before deleting the first one
	 */
	@Getter(value = AUTO_SAVE_LIMIT, defaultValue = "12")
	@XMLAttribute
	public int getAutoSaveLimit();

	@Setter(AUTO_SAVE_LIMIT)
	public void setAutoSaveLimit(int limit);

	@Getter(LAST_IMAGE_DIRECTORY)
	@XMLAttribute
	public File getLastImageDirectory();

	@Setter(LAST_IMAGE_DIRECTORY)
	public void setLastImageDirectory(File f);

	public int getDividerLocationForSplitPaneWithID(String id);

	public void setDividerLocationForSplitPaneWithID(int value, String id);

	/*@Deprecated
	private static File getLocalResourceCenterDirectory() {
		File file = getPreferences().getDirectoryProperty(LOCAL_RESOURCE_CENTER_DIRECTORY2);
		if (file == null) {
			file = getPreferences().getDirectoryProperty(LOCAL_RESOURCE_CENTER_DIRECTORY);
			if (file == null || file.isFile()) {
				setLocalResourceCenterDirectory(file = new File(FileUtils.getApplicationDataDirectory(), "FlexoResourceCenter"));
			}
		}
		return file;
	}*/

	/*@Deprecated
	private static void setLocalResourceCenterDirectory(File directory) {
		getPreferences().setDirectoryProperty(LOCAL_RESOURCE_CENTER_DIRECTORY2, directory);
	}*/

	@Getter(DIRECTORY_RESOURCE_CENTER_LIST)
	@XMLAttribute
	public String getDirectoryResourceCenterListAsString();

	@Setter(DIRECTORY_RESOURCE_CENTER_LIST)
	public void setDirectoryResourceCenterListAsString(String aString);

	/**
	 * Return the list all all {@link DirectoryResourceCenter} registered for the session
	 * 
	 * @return
	 */
	public List<File> getDirectoryResourceCenterList();

	public void assertDirectoryResourceCenterRegistered(File dirRC);

	public void setDirectoryResourceCenterList(List<File> rcList);

	/*public static File getLocationForResource(String uri) {
		return getPreferences().getFileProperty(uri + RESOURCE_LOCATION);
	}

	public static void setLocationForResource(File file, String uri) {
		getPreferences().setFileProperty(uri + RESOURCE_LOCATION, file, false);
	}*/

	public abstract class GeneralPreferencesImpl extends PreferencesContainerImpl implements GeneralPreferences {

		private static final Logger logger = Logger.getLogger(GeneralPreferences.class.getPackage().getName());

		@Override
		public void setLanguage(Language language) {
			performSuperSetter(LANGUAGE_KEY, language);
			if (language != null && language.equals(Language.FRENCH)) {
				Locale.setDefault(Locale.FRANCE);
			} else {
				Locale.setDefault(Locale.US);
			}
			FlexoLocalization.setCurrentLanguage(language);
			FlexoLocalization.updateGUILocalized();
			if (language != null) {
				FlexoHelp.configure(language.getIdentifier(), null/*UserType.getCurrentUserType().getIdentifier()*/);
				FlexoHelp.reloadHelpSet();
			}
		}

		@Override
		public List<File> getLastOpenedProjects() {
			List<File> files = new ArrayList<File>();
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
		public void setLastOpenedProjects(Vector<File> files) {
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

		@Override
		public void addToLastOpenedProjects(File project) {
			List<File> files = getLastOpenedProjects();
			for (File f : new ArrayList<File>(files)) {
				if (project.equals(f)) {
					files.remove(f);
					break;
				}
			}

			files.add(0, project);
			setLastOpenedProjects(files);
		}

		@Override
		public boolean isValidationRuleEnabled(ValidationRule<?, ?> rule) {
			return assertProperty("VR-" + rule.getClass().getName()).booleanValue();
		}

		@Override
		public void setValidationRuleEnabled(ValidationRule<?, ?> rule, boolean enabled) {
			assertProperty("VR-" + rule.getClass().getName()).setBooleanValue(enabled);
		}

		@Override
		public String getUserIdentifier() {
			String returned = (String) performSuperGetter(GeneralPreferences.USER_IDENTIFIER_KEY);
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

		@Override
		public void setUserIdentifier(String aUserIdentifier) {
			performSuperSetter(GeneralPreferences.USER_IDENTIFIER_KEY, aUserIdentifier);
			FlexoObjectImpl.setCurrentUserIdentifier(aUserIdentifier);
		}

		@Override
		public Rectangle getBoundForFrameWithID(String id) {
			return RectangleConverter.instance.convertFromString(assertProperty(BOUNDS_FOR_FRAME + id).getValue());
		}

		@Override
		public void setBoundForFrameWithID(String id, Rectangle bounds) {
			assertProperty(BOUNDS_FOR_FRAME + id).setValue(RectangleConverter.instance.convertToString(bounds));
		}

		@Override
		public boolean getShowLeftView(String id) {
			return assertProperty(SHOW_LEFT_VIEW + id).booleanValue();
		}

		@Override
		public void setShowLeftView(String id, boolean status) {
			assertProperty(SHOW_LEFT_VIEW + id).setBooleanValue(status);
		}

		@Override
		public boolean getShowRightView(String id) {
			return assertProperty(SHOW_RIGHT_VIEW + id).booleanValue();
		}

		@Override
		public void setShowRightView(String id, boolean status) {
			assertProperty(SHOW_RIGHT_VIEW + id).setBooleanValue(status);
		}

		@Override
		public int getFrameStateForFrameWithID(String id) {
			return assertProperty(SHOW_RIGHT_VIEW + id).integerValue();
		}

		/**
		 * @param extendedState
		 */
		@Override
		public void setFrameStateForFrameWithID(String id, int extendedState) {
			assertProperty(STATE_FOR_FRAME + id).setIntegerValue(extendedState);
		}

		@Override
		public int getDividerLocationForSplitPaneWithID(String id) {
			return assertProperty(SPLIT_DIVIDER_LOCATION + id).integerValue();
		}

		@Override
		public void setDividerLocationForSplitPaneWithID(int value, String id) {
			assertProperty(SPLIT_DIVIDER_LOCATION + id).setIntegerValue(value);
		}

		@Override
		public String getLayoutFor(String id) {
			return assertProperty(LAYOUT_FOR + id).getValue();
		}

		/**
		 * @param extendedState
		 */
		@Override
		public void setLayoutFor(String layout, String id) {
			assertProperty(LAYOUT_FOR + id).setValue(layout);
		}

		/**
		 * Return the list all all {@link DirectoryResourceCenter} registered for the session
		 * 
		 * @return
		 */
		@Override
		public List<File> getDirectoryResourceCenterList() {
			String directoriesAsString = getDirectoryResourceCenterListAsString();
			if (StringUtils.isEmpty(directoriesAsString)) {
				return Collections.emptyList();
			} else {
				List<File> returned = new ArrayList<File>();
				StringTokenizer st = new StringTokenizer(directoriesAsString, ",");
				while (st.hasMoreTokens()) {
					String next = st.nextToken();
					File f = new File(next);
					if (f.exists()) {
						returned.add(f);
					}
				}
				return returned;
			}
		}

		@Override
		public void assertDirectoryResourceCenterRegistered(File dirRC) {
			List<File> alreadyRegistered = getDirectoryResourceCenterList();
			if (alreadyRegistered.contains(dirRC)) {
				return;
			}
			if (alreadyRegistered.size() == 0) {
				setDirectoryResourceCenterListAsString(dirRC.getAbsolutePath());
			} else {
				setDirectoryResourceCenterListAsString(getDirectoryResourceCenterList() + "," + dirRC.getAbsolutePath());
			}
		}

		@Override
		public void setDirectoryResourceCenterList(List<File> rcList) {
			boolean isFirst = true;
			StringBuffer s = new StringBuffer();
			for (File f : rcList) {
				s.append((isFirst ? "" : ",") + f.getAbsolutePath());
				isFirst = false;
			}
			System.out.println("Sets " + s.toString() + " for " + DIRECTORY_RESOURCE_CENTER_LIST);
			setDirectoryResourceCenterListAsString(s.toString());
		}

	}

}
