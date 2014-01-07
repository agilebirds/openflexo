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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import org.openflexo.foundation.FlexoProperty;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.prefs.PreferencesContainer;
import org.openflexo.swing.FlexoFont;
import org.openflexo.swing.LookAndFeel;
import org.openflexo.swing.converter.LookAndFeelConverter;
import org.openflexo.toolbox.ProxyUtils;
import org.openflexo.toolbox.ToolBox;

@ModelEntity
@ImplementationClass(AdvancedPrefs.AdvancedPrefsImpl.class)
@XMLElement(xmlTag = "AdvancedPrefs")
public interface AdvancedPrefs extends PreferencesContainer {

	// private static final Class<AdvancedPrefs> ADVANCED_PREFERENCES = AdvancedPrefs.class;

	public static final String LAST_VISITED_DIRECTORY_KEY = "lastVisitedDirectory";

	public static final String ECLIPSE_WORKSPACE_DIRECTORY_KEY = "eclipseWorkspaceDirectory";

	public static final String BROWSERFONT_KEY = "browser_font";

	public static final String BUG_REPORT_URL_KEY = "bug_report_url";

	public static final String WEB_SERVICE_INSTANCE = "webServiceInstance";
	public static final String WEB_SERVICE_URL_KEY = "webServiceUrl";
	public static final String WEB_SERVICE_LOGIN_KEY = "webServiceLogin";
	public static final String WEB_SERVICE_PWD_KEY = "webServicePassword";
	public static final String WEB_SERVICE_REMEMBERANDDONTASKPARAMSANYMORE_KEY = "rememberAndDontAskWebServiceParamsAnymore";
	public static final String FLEXO_SERVER_INSTANCE_URL = "flexoserver_instance_url";

	public static final String ENABLE_UNDO_MANAGER = "enableUndoManager";

	public static final String UNDO_LEVELS = "undoLevels";

	public static final String SYNCHRONIZED_BROWSER = "synchronizedBrowser";

	public static final String HIGHLIGHT_UNCOMMENTED_ITEMS = "hightlightUncommentedItem";

	public static final String CLOSE_POPUP_ON_MOUSE_OUT = "close_popup_on_mouse_out";

	public static final String LOOK_AND_FEEL = "look_and_feel";

	public static final String HIDE_FILTERED_OBJECTS = "hideFilteredObjects";

	public static final String USE_DEFAULT_PROXY_SETTINGS = "UseDefaultProxySettings";
	public static final String NO_PROXY = "NoProxy";
	public static final String NO_PROXY_HOSTS = "NoProxyHosts";
	public static final String HTTP_PROXY_HOST = "HTTPProxyHost";
	public static final String HTTP_PROXY_PORT = "HTTPProxyPort";
	public static final String HTTPS_PROXY_HOST = "HTTPSProxyHost";
	public static final String HTTPS_PROXY_PORT = "HTTPSProxyPort";
	public static final String PROXY_LOGIN = "ProxyLogin";
	public static final String PROXY_PASSWORD = "ProxyPassword";
	public static final String SHOW_ALL_TABS = "show_all_tabs";
	public static final String PREFERENCE_OVERRIDE_FROM_FLEXO_PROPERTIES_DONE = "preference_override_from_flexo_properties_done";

	public static final String ALLOWSDOCSUBMISSION = "allowsDocSubmission";
	public static final String LOGCOUNT = "logCount";
	public static final String KEEPLOGTRACE = "keepLogTrace";
	public static final String DEFAULT_LOG_LEVEL = "default.logging.level";
	public static final String CUSTOM_LOG_CONFIG_FILE = "logging.file.name";

	// public static final String FLEXO_SERVER_INSTANCE_URL = "flexoserver_instance_url";
	public static final String BUG_REPORT_USER = "bug_report_user";
	public static final String BUG_REPORT_PASWORD = "bug_report_password";

	/*@Override
	public String getName() {
		return "advanced";
	}

	@Override
	public File getInspectorFile() {
		return new FileResource("Config/Preferences/AdvancedPrefs.inspector");
	}*/

	@Getter(LAST_VISITED_DIRECTORY_KEY)
	@XMLAttribute
	public File getLastVisitedDirectory();

	@Setter(LAST_VISITED_DIRECTORY_KEY)
	public void setLastVisitedDirectory(File f);

	@Getter(ECLIPSE_WORKSPACE_DIRECTORY_KEY)
	@XMLAttribute
	public File getEclipseWorkspaceDirectory();

	@Setter(ECLIPSE_WORKSPACE_DIRECTORY_KEY)
	public void setEclipseWorkspaceDirectory(File f);

	@Getter(value = BUG_REPORT_URL_KEY, defaultValue = "https://bugs.openflexo.org")
	@XMLAttribute
	public String getBugReportUrl();

	@Setter(BUG_REPORT_URL_KEY)
	public void setBugReportUrl(String bugReportURL);

	@Getter(BUG_REPORT_PASWORD)
	@XMLAttribute
	public String getBugReportPassword();

	@Setter(BUG_REPORT_PASWORD)
	public void setBugReportPassword(String password);

	@Getter(BUG_REPORT_USER)
	@XMLAttribute
	public String getBugReportUser();

	@Setter(BUG_REPORT_USER)
	public void setBugReportUser(String user);

	@Getter(value = SYNCHRONIZED_BROWSER, defaultValue = "false")
	@XMLAttribute
	public boolean getSynchronizedBrowser();

	@Setter(SYNCHRONIZED_BROWSER)
	public void setSynchronizedBrowser(boolean synchronizedBrowser);

	@Getter(value = BROWSERFONT_KEY, defaultValue = "Sans Serif,PLAIN,11")
	@XMLAttribute
	public FlexoFont getBrowserFont();

	@Setter(BROWSERFONT_KEY)
	public void setBrowserFont(FlexoFont font);

	@Getter(value = CLOSE_POPUP_ON_MOUSE_OUT, defaultValue = "false")
	@XMLAttribute
	public boolean getCloseOnMouseOut();

	@Setter(CLOSE_POPUP_ON_MOUSE_OUT)
	public void setCloseOnMouseOut(boolean closeOnMouseOut);

	@Getter(value = ENABLE_UNDO_MANAGER, defaultValue = "true")
	@XMLAttribute
	public boolean getEnableUndoManager();

	@Setter(ENABLE_UNDO_MANAGER)
	public void setEnableUndoManager(boolean enableUndoManager);

	@Getter(value = HIGHLIGHT_UNCOMMENTED_ITEMS, defaultValue = "false")
	@XMLAttribute
	public boolean getHightlightUncommentedItem();

	@Setter(HIGHLIGHT_UNCOMMENTED_ITEMS)
	public void setHightlightUncommentedItem(boolean hightlightUncommentedItem);

	@Getter(value = UNDO_LEVELS, defaultValue = "20")
	@XMLAttribute
	public Integer getUndoLevels();

	@Setter(UNDO_LEVELS)
	public void setUndoLevels(Integer undoLevels);

	@Getter(LOOK_AND_FEEL)
	@XMLAttribute
	public LookAndFeel getLookAndFeel();

	public String getLookAndFeelAsString();

	@Setter(LOOK_AND_FEEL)
	public void setLookAndFeel(LookAndFeel value);

	@Getter(value = HIDE_FILTERED_OBJECTS, defaultValue = "true")
	@XMLAttribute
	public boolean hideFilteredObjects();

	@Setter(HIDE_FILTERED_OBJECTS)
	public void setHideFilteredObjects(boolean enabled);

	@Getter(value = WEB_SERVICE_URL_KEY, defaultValue = "https://server.openflexo.com/Flexo/WebObjects/FlexoServer.woa/ws/PPMWebService")
	@XMLAttribute
	public String getWebServiceUrl();

	@Setter(WEB_SERVICE_URL_KEY)
	public void setWebServiceUrl(String webServiceUrl);

	@Getter(WEB_SERVICE_LOGIN_KEY)
	@XMLAttribute
	public String getWebServiceLogin();

	@Setter(WEB_SERVICE_LOGIN_KEY)
	public void setWebServiceLogin(String webServiceLogin);

	@Getter(WEB_SERVICE_PWD_KEY)
	@XMLAttribute
	public String getWebServicePassword();

	@Setter(WEB_SERVICE_PWD_KEY)
	public void setWebServicePassword(String password);

	@Getter(value = WEB_SERVICE_REMEMBERANDDONTASKPARAMSANYMORE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getRememberAndDontAskWebServiceParamsAnymore();

	@Setter(WEB_SERVICE_REMEMBERANDDONTASKPARAMSANYMORE_KEY)
	public void setRememberAndDontAskWebServiceParamsAnymore(boolean rememberAndDontAskWebServiceParamsAnymore);

	public void applyProxySettings();

	@Getter(value = USE_DEFAULT_PROXY_SETTINGS, defaultValue = "false")
	@XMLAttribute
	public boolean getUseDefaultProxySettings();

	@Setter(USE_DEFAULT_PROXY_SETTINGS)
	public void setUseDefaultProxySettings(boolean useDefault);

	@Getter(NO_PROXY)
	@XMLAttribute
	public Boolean getNoProxy();

	@Setter(NO_PROXY)
	public void setNoProxy(Boolean noProxy);

	@Getter(HTTP_PROXY_HOST)
	@XMLAttribute
	public String getProxyHost();

	@Setter(HTTP_PROXY_HOST)
	public void setProxyHost(String proxyHost);

	@Getter(HTTP_PROXY_PORT)
	@XMLAttribute
	public Integer getProxyPort();

	@Setter(HTTP_PROXY_PORT)
	public void setProxyPort(Integer proxyPort);

	@Getter(HTTPS_PROXY_HOST)
	@XMLAttribute
	public String getSProxyHost();

	@Getter(HTTPS_PROXY_HOST)
	public void setSProxyHost(String proxyHost);

	@Getter(HTTPS_PROXY_PORT)
	@XMLAttribute
	public Integer getSProxyPort();

	@Setter(HTTPS_PROXY_PORT)
	public void setSProxyPort(Integer proxyPort);

	@Getter(value = NO_PROXY_HOSTS, defaultValue = "localhost,127.0.0.1,*.")
	@XMLAttribute
	public String getNoProxyHostsString();

	@Setter(NO_PROXY_HOSTS)
	public void setNoProxyHostsString(String string);

	public List<String> getNoProxyHosts();

	public void redetectProxySettings();

	@Getter(PROXY_LOGIN)
	@XMLAttribute
	public String getProxyLogin();

	@Setter(PROXY_LOGIN)
	public void setProxyLogin(String proxyLogin);

	@Getter(PROXY_PASSWORD)
	@XMLAttribute
	public String getProxyPassword();

	@Setter(PROXY_PASSWORD)
	public void setProxyPassword(String proxyPassword);

	@Getter(value = FLEXO_SERVER_INSTANCE_URL, defaultValue = "http://flexoserverinstances.openflexo.org")
	@XMLAttribute
	public String getFlexoServerInstanceURL();

	@Setter(FLEXO_SERVER_INSTANCE_URL)
	public void setFlexoServerInstanceURL(String url);

	@Getter(value = WEB_SERVICE_INSTANCE, defaultValue = "prod")
	@XMLAttribute
	public String getWebServiceInstance();

	@Setter(WEB_SERVICE_INSTANCE)
	public void setWebServiceInstance(String wsInstanceID);

	@Getter(value = SHOW_ALL_TABS, defaultValue = "false")
	@XMLAttribute
	public boolean getShowAllTabs();

	@Setter(SHOW_ALL_TABS)
	public void setShowAllTabs(boolean showAllTabs);

	@Getter(value = PREFERENCE_OVERRIDE_FROM_FLEXO_PROPERTIES_DONE, defaultValue = "false")
	@XMLAttribute
	public boolean getPreferenceOverrideFromFlexoPropertiesDone();

	@Setter(PREFERENCE_OVERRIDE_FROM_FLEXO_PROPERTIES_DONE)
	public void setPreferenceOverrideFromFlexoPropertiesDone(boolean preferenceOverrideFromFlexoPropertiesDone);

	@Getter(value = KEEPLOGTRACE, defaultValue = "true")
	@XMLAttribute
	public boolean getIsLoggingTrace();

	@Setter(KEEPLOGTRACE)
	public void setIsLoggingTrace(boolean b);

	@Getter(CUSTOM_LOG_CONFIG_FILE)
	@XMLAttribute
	public String getLoggingFileName();

	@Setter(CUSTOM_LOG_CONFIG_FILE)
	public void setLoggingFileName(String loggingFileName);

	public File getCustomLoggingFile();

	@Getter(DEFAULT_LOG_LEVEL)
	@XMLAttribute
	public String getDefaultLoggingLevelAsString();

	@Setter(DEFAULT_LOG_LEVEL)
	public void setDefaultLoggingLevelAsString(String aString);

	public Level getDefaultLoggingLevel();

	public void setDefaultLoggingLevel(Level l);

	@Getter(value = LOGCOUNT, defaultValue = "0")
	@XMLAttribute
	public int getMaxLogCount();

	@Setter(LOGCOUNT)
	public void setMaxLogCount(int c);

	@Getter(value = ALLOWSDOCSUBMISSION, defaultValue = "false")
	@XMLAttribute
	public boolean getAllowsDocSubmission();

	@Setter(ALLOWSDOCSUBMISSION)
	public void setAllowsDocSubmission(boolean b);

	public abstract class AdvancedPrefsImpl extends PreferencesContainerImpl implements AdvancedPrefs {

		private static final Logger logger = Logger.getLogger(GeneralPreferences.class.getPackage().getName());

		private final LookAndFeelConverter LAF_CONVERTER = new LookAndFeelConverter();

		@Override
		public LookAndFeel getLookAndFeel() {
			FlexoProperty p = assertProperty(ToolBox.getPLATFORM() + LOOK_AND_FEEL);
			String returned = p.getValue();
			if (returned == null) {
				p = assertProperty(LOOK_AND_FEEL);
				returned = p.getValue();
			}
			if (returned == null) {
				returned = UIManager.getSystemLookAndFeelClassName();
			}
			setLookAndFeel(LAF_CONVERTER.convertFromString(returned, null));
			return LAF_CONVERTER.convertFromString(returned, null);
		}

		@Override
		public String getLookAndFeelAsString() {
			return LAF_CONVERTER.convertToString(getLookAndFeel());
		}

		@Override
		public void setLookAndFeel(LookAndFeel value) {
			if (value == null) {
				value = LookAndFeel.getDefaultLookAndFeel();
			}
			assertProperty(ToolBox.getPLATFORM() + LOOK_AND_FEEL).setValue(LAF_CONVERTER.convertToString(value));
			Flexo.initUILAF(LAF_CONVERTER.convertToString(value));
		}

		private boolean isApplying = false;

		@Override
		public void applyProxySettings() {
			if (!isApplying) {
				isApplying = true;
				try {
					System.setProperty("java.net.useSystemProxies", String.valueOf(getUseDefaultProxySettings()));
					if (!getUseDefaultProxySettings() && !getNoProxy()) {
						String proxyHost = getProxyHost();
						if (proxyHost != null) {
							System.setProperty("http.proxyHost", proxyHost);
						}
						String sProxyHost = getSProxyHost();
						if (sProxyHost != null) {
							System.setProperty("https.proxyHost", sProxyHost);
						}
						Integer proxyPort = getProxyPort();
						if (proxyPort != null) {
							System.setProperty("http.proxyPort", String.valueOf(proxyPort));
						}
						Integer sProxyPort = getSProxyPort();
						if (sProxyPort != null) {
							System.setProperty("https.proxyPort", String.valueOf(sProxyPort));
						}
						List<String> noProxyHosts = getNoProxyHosts();

						if (noProxyHosts != null && noProxyHosts.size() > 0) {
							StringBuilder sb = new StringBuilder();
							for (String noProxyHost : noProxyHosts) {
								if (sb.length() > 0) {
									sb.append('|');
								}
								sb.append(noProxyHost);
							}
							System.setProperty("http.nonProxyHosts", sb.toString());
						} else {
							System.clearProperty("http.nonProxyHosts");
						}
					} else {
						System.clearProperty("http.proxyHost");
						System.clearProperty("http.proxyPort");
						System.clearProperty("https.proxyHost");
						System.clearProperty("https.proxyPort");
						System.clearProperty("http.nonProxyHosts");
					}
				} finally {
					isApplying = false;
				}
			}
		}

		@Override
		public void setUseDefaultProxySettings(boolean useDefault) {
			performSuperSetter(USE_DEFAULT_PROXY_SETTINGS, useDefault);
			applyProxySettings();
		}

		@Override
		public Boolean getNoProxy() {
			Boolean b = (Boolean) performSuperGetter(NO_PROXY);
			if (b == null) {
				boolean noProxy = true;
				if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
					try {
						noProxy = !ProxyUtils.isProxyEnabled();
						if (logger.isLoggable(Level.INFO)) {
							logger.info("This machine seems to use a proxy? " + !noProxy);
						}
					} catch (RuntimeException e) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.log(Level.WARNING, e.getMessage(), e);
						}
					}
				}
				setNoProxy(noProxy);
				return getNoProxy();
			}
			return b;
		}

		@Override
		public void setNoProxy(Boolean noProxy) {
			performSuperSetter(NO_PROXY, noProxy);
			applyProxySettings();
		}

		@Override
		public String getProxyHost() {
			String proxyHost = (String) performSuperGetter(HTTP_PROXY_HOST);
			if (proxyHost == null) {
				if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
					try {
						if (ProxyUtils.autoDetectSettingsEnabled()) {
							List<String[]> proxies = ProxyUtils.getProxiesFromAutoConfigURL(ProxyUtils.getAutoConfigURL(), 80);
							if (proxies.size() > 0) {
								proxyHost = proxies.get(proxies.size() - 1)[0];
							}
						}
						if (proxyHost == null) {
							String[] s = ProxyUtils.getHTTPProxyPort(false);
							if (s != null && s.length > 0) {
								proxyHost = s[0];
							}
						}
					} catch (RuntimeException e) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.log(Level.WARNING, e.getMessage(), e);
						}
					}
				}
				setProxyHost(proxyHost);
			}
			return proxyHost;
		}

		@Override
		public void setProxyHost(String proxyHost) {
			performSuperSetter(HTTP_PROXY_HOST, proxyHost);
			applyProxySettings();
		}

		@Override
		public Integer getProxyPort() {
			Integer proxyPort = (Integer) performSuperGetter(HTTP_PROXY_PORT);
			if (proxyPort == null) {
				if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
					try {
						if (ProxyUtils.autoDetectSettingsEnabled()) {
							List<String[]> proxies = ProxyUtils.getProxiesFromAutoConfigURL(ProxyUtils.getAutoConfigURL(), 80);
							if (proxies.size() > 0) {
								proxyPort = Integer.parseInt(proxies.get(proxies.size() - 1)[1]);
							}
						}
						if (proxyPort == null) {
							String[] s = ProxyUtils.getHTTPProxyPort(false);
							if (s != null && s.length > 1) {
								proxyPort = Integer.parseInt(s[1]);
							}
						}
					} catch (RuntimeException e) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.log(Level.WARNING, e.getMessage(), e);
						}
					}
				}
				setProxyPort(proxyPort);
			}
			return proxyPort;
		}

		@Override
		public void setProxyPort(Integer proxyPort) {
			performSuperSetter(HTTP_PROXY_PORT, proxyPort);
			applyProxySettings();
		}

		@Override
		public String getSProxyHost() {
			String proxyHost = (String) performSuperGetter(HTTPS_PROXY_HOST);
			if (proxyHost == null) {
				if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
					try {
						if (ProxyUtils.autoDetectSettingsEnabled()) {
							List<String[]> proxies = ProxyUtils.getProxiesFromAutoConfigURL(ProxyUtils.getAutoConfigURL(), 443);
							if (proxies.size() > 0) {
								proxyHost = proxies.get(proxies.size() - 1)[0];
							}
						}
						if (proxyHost == null) {
							String[] s = ProxyUtils.getHTTPProxyPort(true);
							if (s != null && s.length > 0) {
								proxyHost = s[0];
							}
						}
					} catch (RuntimeException e) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.log(Level.WARNING, e.getMessage(), e);
						}
					}
				}
				setSProxyHost(proxyHost);
			}
			return proxyHost;
		}

		@Override
		public void setSProxyHost(String proxyHost) {
			performSuperSetter(HTTPS_PROXY_HOST, proxyHost);
			applyProxySettings();
		}

		@Override
		public Integer getSProxyPort() {
			Integer proxyPort = (Integer) performSuperGetter(HTTPS_PROXY_PORT);
			if (proxyPort == null) {
				if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
					try {
						if (ProxyUtils.autoDetectSettingsEnabled()) {
							List<String[]> proxies = ProxyUtils.getProxiesFromAutoConfigURL(ProxyUtils.getAutoConfigURL(), 443);
							if (proxies.size() > 0) {
								proxyPort = Integer.parseInt(proxies.get(proxies.size() - 1)[1]);
							}
						}
						if (proxyPort == null) {
							String[] s = ProxyUtils.getHTTPProxyPort(true);
							if (s != null && s.length > 1) {
								proxyPort = Integer.parseInt(s[1]);
							}
						}
					} catch (RuntimeException e) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.log(Level.WARNING, e.getMessage(), e);
						}
					}
				}
				setSProxyPort(proxyPort);
			}
			return proxyPort;
		}

		@Override
		public void setSProxyPort(Integer proxyPort) {
			performSuperSetter(HTTPS_PROXY_PORT, proxyPort);
			applyProxySettings();
		}

		@Override
		public void setNoProxyHostsString(String string) {
			performSuperSetter(NO_PROXY_HOSTS, string);
			applyProxySettings();
		}

		@Override
		public List<String> getNoProxyHosts() {
			String string = getNoProxyHostsString();
			if (string != null && string.trim().length() > 0) {
				List<String> list = new ArrayList<String>();
				String[] s = string.split(",");
				for (String string2 : s) {
					list.add(string2.trim());
				}
				return list;
			}
			return null;
		}

		@Override
		public void redetectProxySettings() {
			setProxyHost(null);
			setProxyPort(null);
			setSProxyHost(null);
			setSProxyPort(null);
			setNoProxyHostsString(null);
		}

		@Override
		public void setProxyLogin(String proxyLogin) {
			performSuperSetter(PROXY_LOGIN, proxyLogin);
			applyProxySettings();
		}

		@Override
		public void setProxyPassword(String proxyPassword) {
			performSuperSetter(PROXY_PASSWORD, proxyPassword);
			applyProxySettings();
		}

		@Override
		public File getCustomLoggingFile() {
			if (getLoggingFileName() == null) {
				return null;
			}
			return new File(getLoggingFileName());
		}

		@Override
		public Level getDefaultLoggingLevel() {
			String returned = getDefaultLoggingLevelAsString();
			if (returned == null) {
				return null;
			} else if (returned.equals("SEVERE")) {
				return Level.SEVERE;
			} else if (returned.equals("WARNING")) {
				return Level.WARNING;
			} else if (returned.equals("INFO")) {
				return Level.INFO;
			} else if (returned.equals("FINE")) {
				return Level.FINE;
			} else if (returned.equals("FINER")) {
				return Level.FINER;
			} else if (returned.equals("FINEST")) {
				return Level.FINEST;
			}
			return null;
		}

		@Override
		public void setDefaultLoggingLevel(Level l) {
			setDefaultLoggingLevelAsString(l.getName());
		}

	}

}
