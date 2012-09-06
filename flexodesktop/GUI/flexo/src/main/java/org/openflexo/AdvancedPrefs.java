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

import java.awt.Font;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.openflexo.foundation.utils.FlexoFont;
import org.openflexo.module.ModuleLoader;
import org.openflexo.prefs.ContextPreferences;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.swing.CustomPopup;
import org.openflexo.swing.LookAndFeel;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.ProxyUtils;
import org.openflexo.toolbox.ToolBox;

public class AdvancedPrefs extends ContextPreferences {

	private static final Logger logger = Logger.getLogger(GeneralPreferences.class.getPackage().getName());

	private static final Class<AdvancedPrefs> ADVANCED_PREFERENCES = AdvancedPrefs.class;

	public static final String LAST_VISITED_DIRECTORY_KEY = "lastVisitedDirectory";

	public static final String ECLIPSE_WORKSPACE_DIRECTORY_KEY = "eclipseWorkspaceDirectory";

	public static final String BROWSERFONT_KEY = "browser_font";

	public static final String BUG_REPORT_URL_KEY = "bug_report_url";
	public static final String BUG_REPORT_USER = "bug_report_user";
	public static final String BUG_REPORT_PASWORD = "bug_report_password";

	public static final String WEB_SERVICE_INSTANCE = "webServiceInstance";
	public static final String WEB_SERVICE_URL_KEY = "webServiceUrl";
	public static final String WEB_SERVICE_LOGIN_KEY = "webServiceLogin";
	public static final String WEB_SERVICE_PWD_KEY = "webServicePwd";
	public static final String WEB_SERVICE_REMEMBERANDDONTASKPARAMSANYMORE_KEY = "rememberAndDontAskWebServiceParamsAnymore";
	public static final String FLEXO_SERVER_INSTANCE_URL = "flexoserver_instance_url";

	public static final String ENABLE_UNDO_MANAGER = "enableUndoManager";

	public static final String UNDO_LEVELS = "undoLevels";

	public static final String SYNCHRONIZED_BROWSER = "synchronizedBrowser";

	public static final String HIGHLIGHT_UNCOMMENTED_ITEMS = "hightlightUncommentedItem";

	public static final String CLOSE_POPUP_ON_MOUSE_OUT = "close_popup_on_mouse_out";

	public static final String LOOK_AND_FEEL = "look_and_feel";

	public static final String HIDE_FILTERED_OBJECTS = "hideFilteredObjects";

	private static final String USE_DEFAULT_PROXY_SETTINGS = "UseDefaultProxySettings";
	private static final String NO_PROXY = "NoProxy";
	private static final String HTTP_PROXY_HOST = "HTTPProxyHost";
	private static final String HTTP_PROXY_PORT = "HTTPProxyPort";
	private static final String HTTPS_PROXY_HOST = "HTTPSProxyHost";
	private static final String HTTPS_PROXY_PORT = "HTTPSProxyPort";
	private static final String PROXY_LOGIN = "ProxyLogin";
	private static final String PROXY_PASSWORD = "ProxyPassword";

	@Override
	public String getName() {
		return "advanced";
	}

	@Override
	public File getInspectorFile() {
		return new FileResource("Config/Preferences/AdvancedPrefs.inspector");
	}

	public static File getLastVisitedDirectory() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getLastVisitedDirectory");
		}
		return getPreferences().getDirectoryProperty(LAST_VISITED_DIRECTORY_KEY, true);
	}

	public static void setLastVisitedDirectory(File f) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setLastVisitedDirectory");
		}
		getPreferences().setDirectoryProperty(LAST_VISITED_DIRECTORY_KEY, f, true);
	}

	public static File getEclipseWorkspaceDirectory() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getEclipseWorkspaceDirectory");
		}
		return getPreferences().getDirectoryProperty(ECLIPSE_WORKSPACE_DIRECTORY_KEY, true);
	}

	public static void setEclipseWorkspaceDirectory(File f) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setEclipseWorkspaceDirectory");
		}
		getPreferences().setDirectoryProperty(ECLIPSE_WORKSPACE_DIRECTORY_KEY, f, true);
	}

	public static String getBugReportUrl() {
		String answer = getPreferences().getProperty(BUG_REPORT_URL_KEY);
		if (answer == null) {
			setBugReportUrl(answer = "https://bugs.openflexo.com");
		}
		return answer;
	}

	public static void setBugReportUrl(String bugReportURL) {
		getPreferences().setProperty(BUG_REPORT_URL_KEY, bugReportURL);
	}

	public static String getBugReportPassword() {
		String answer = getPreferences().getPasswordProperty(BUG_REPORT_PASWORD);
		if (answer == null) {
			setBugReportUrl(answer = "https://bugs.openflexo.com");
		}
		return answer;
	}

	public static void setBugReportPassword(String password) {
		getPreferences().setPasswordProperty(BUG_REPORT_PASWORD, password);
	}

	public static String getBugReportUser() {
		return getPreferences().getProperty(BUG_REPORT_USER);
	}

	public static void setBugReportUser(String user) {
		getPreferences().setProperty(BUG_REPORT_USER, user);
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

	public static FlexoFont getBrowserFont() {
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("getBrowserFont");
		}
		FlexoFont reply = FlexoFont.get(getPreferences().getProperty(BROWSERFONT_KEY));
		return reply == null ? new FlexoFont("Sans Serif", Font.PLAIN, 11) : reply;
	}

	public static void setBrowserFont(FlexoFont font) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setBrowserFont");
		}
		getPreferences().setProperty(BROWSERFONT_KEY, font.toString());
	}

	public static boolean getCloseOnMouseOut() {
		String answer = getPreferences().getProperty(CLOSE_POPUP_ON_MOUSE_OUT);
		if (answer == null) {
			setCloseOnMouseOut(false);
			return false;
		}
		return Boolean.parseBoolean(answer);
	}

	public static void setCloseOnMouseOut(boolean closeOnMouseOut) {
		getPreferences().setProperty(CLOSE_POPUP_ON_MOUSE_OUT, String.valueOf(closeOnMouseOut));
		CustomPopup.configuration.setCloseWhenPointerLeavesPopup(closeOnMouseOut);
	}

	public static boolean getEnableUndoManager() {
		String answer = getPreferences().getProperty(ENABLE_UNDO_MANAGER);
		if (answer == null) {
			setEnableUndoManager(true);
			return getEnableUndoManager();
		}
		return Boolean.parseBoolean(answer);
	}

	public static void setEnableUndoManager(boolean enableUndoManager) {
		getPreferences().setProperty(ENABLE_UNDO_MANAGER, String.valueOf(enableUndoManager));
	}

	public static boolean getHightlightUncommentedItem() {
		String answer = getPreferences().getProperty(HIGHLIGHT_UNCOMMENTED_ITEMS);
		if (answer == null) {
			setHightlightUncommentedItem(false);
			return getHightlightUncommentedItem();
		}
		return Boolean.parseBoolean(answer);
	}

	public static void setHightlightUncommentedItem(boolean enableUndoManager) {
		getPreferences().setProperty(HIGHLIGHT_UNCOMMENTED_ITEMS, String.valueOf(enableUndoManager));
	}

	public static Integer getUndoLevels() {
		Integer answer = getPreferences().getIntegerProperty(UNDO_LEVELS);
		if (answer == null) {
			setUndoLevels(20);
			return getUndoLevels();
		}
		return answer;
	}

	public static void setUndoLevels(Integer undoLevels) {
		getPreferences().setProperty(UNDO_LEVELS, String.valueOf(undoLevels.intValue()));
	}

	public static LookAndFeel getLookAndFeel() {
		String returned = getPreferences().getProperty(ToolBox.getPLATFORM() + LOOK_AND_FEEL);
		if (returned == null) {
			returned = getPreferences().getProperty(LOOK_AND_FEEL);
		}
		if (returned == null) {
			returned = UIManager.getSystemLookAndFeelClassName();
		}
		setLookAndFeel(LookAndFeel.get(returned));
		return LookAndFeel.get(returned);
	}

	public static String getLookAndFeelString() {
		return LookAndFeel.lookAndFeelConverter.convertToString(getLookAndFeel());
	}

	public static void setLookAndFeel(LookAndFeel value) {
		if (value == null) {
			value = LookAndFeel.getDefaultLookAndFeel();
		}
		getPreferences().setProperty(ToolBox.getPLATFORM() + LOOK_AND_FEEL, LookAndFeel.lookAndFeelConverter.convertToString(value));
		try {
			ModuleLoader.setLookAndFeel(LookAndFeel.lookAndFeelConverter.convertToString(value));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public static void save() {
		FlexoPreferences.savePreferences(true);
	}

	public static boolean hideFilteredObjects() {
		Boolean hideFilteredObjects = getPreferences().getBooleanProperty(HIDE_FILTERED_OBJECTS);
		if (hideFilteredObjects == null) {
			setHideFilteredObjects(true);
			hideFilteredObjects = Boolean.TRUE;
		}
		return hideFilteredObjects;
	}

	public static void setHideFilteredObjects(boolean enabled) {
		getPreferences().setBooleanProperty(HIDE_FILTERED_OBJECTS, enabled);
	}

	public static String getWebServiceUrl() {
		String answer = getPreferences().getProperty(WEB_SERVICE_URL_KEY);
		if (answer == null) {
			setWebServiceUrl("https://www.flexobpmserver.com/Flexo/WebObjects/FlexoServer.woa/ws/PPMWebService");
			return getWebServiceUrl();
		}
		return answer;
	}

	public static AdvancedPrefs getPreferences() {
		return preferences(ADVANCED_PREFERENCES);
	}

	public static void setWebServiceUrl(String webServiceUrl) {
		getPreferences().setProperty(WEB_SERVICE_URL_KEY, webServiceUrl);
	}

	public static String getWebServiceLogin() {
		String answer = getPreferences().getProperty(WEB_SERVICE_LOGIN_KEY);
		return answer;
	}

	public static void setWebServiceLogin(String webServiceLogin) {
		getPreferences().setProperty(WEB_SERVICE_LOGIN_KEY, webServiceLogin);
	}

	public static String getWebServiceMd5Password() {
		String answer = getPreferences().getProperty(WEB_SERVICE_PWD_KEY);
		return answer;
	}

	public static void setWebServiceMd5Password(String webServiceMd5Password) {
		getPreferences().setProperty(WEB_SERVICE_PWD_KEY, webServiceMd5Password);
	}

	public static boolean getRememberAndDontAskWebServiceParamsAnymore() {
		Boolean b = getPreferences().getBooleanProperty(WEB_SERVICE_REMEMBERANDDONTASKPARAMSANYMORE_KEY);
		if (b == null) {
			setRememberAndDontAskWebServiceParamsAnymore(false);
			return getRememberAndDontAskWebServiceParamsAnymore();
		}
		return b;
	}

	public static void setRememberAndDontAskWebServiceParamsAnymore(boolean rememberAndDontAskWebServiceParamsAnymore) {
		getPreferences().setBooleanProperty(WEB_SERVICE_REMEMBERANDDONTASKPARAMSANYMORE_KEY, rememberAndDontAskWebServiceParamsAnymore);
	}

	private static boolean isApplying = false;

	public static void applyProxySettings() {
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
				} else {
					System.clearProperty("http.proxyHost");
					System.clearProperty("http.proxyPort");
					System.clearProperty("https.proxyHost");
					System.clearProperty("https.proxyPort");
				}
			} finally {
				isApplying = false;
			}
		}
	}

	public static boolean getUseDefaultProxySettings() {
		Boolean b = getPreferences().getBooleanProperty(USE_DEFAULT_PROXY_SETTINGS);
		if (b == null) {
			setUseDefaultProxySettings(ProxyUtils.getAutoConfigURL() == null);
			return getUseDefaultProxySettings();
		}
		return b;
	}

	public static void setUseDefaultProxySettings(boolean useDefault) {
		getPreferences().setBooleanProperty(USE_DEFAULT_PROXY_SETTINGS, useDefault);
		applyProxySettings();
	}

	public static boolean getNoProxy() {
		Boolean b = getPreferences().getBooleanProperty(NO_PROXY);
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

	public static void setNoProxy(boolean noProxy) {
		getPreferences().setBooleanProperty(NO_PROXY, noProxy);
		applyProxySettings();
	}

	public static String getProxyHost() {
		String proxyHost = getPreferences().getProperty(HTTP_PROXY_HOST);
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

	public static void setProxyHost(String proxyHost) {
		getPreferences().setProperty(HTTP_PROXY_HOST, proxyHost, "proxyHost");
		applyProxySettings();
	}

	public static Integer getProxyPort() {
		Integer proxyPort = getPreferences().getIntegerProperty(HTTP_PROXY_PORT);
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

	public static void setProxyPort(Integer proxyPort) {
		getPreferences().setIntegerProperty(HTTP_PROXY_PORT, proxyPort, "proxyPort");
		applyProxySettings();
	}

	public static String getSProxyHost() {
		String proxyHost = getPreferences().getProperty(HTTPS_PROXY_HOST);
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

	public static void setSProxyHost(String proxyHost) {
		getPreferences().setProperty(HTTPS_PROXY_HOST, proxyHost, "sProxyHost");
		applyProxySettings();
	}

	public static Integer getSProxyPort() {
		Integer proxyPort = getPreferences().getIntegerProperty(HTTPS_PROXY_PORT);
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

	public static void setSProxyPort(Integer proxyPort) {
		getPreferences().setIntegerProperty(HTTPS_PROXY_PORT, proxyPort, "sProxyPort");
		applyProxySettings();
	}

	public static void redetectProxySettings() {
		setProxyHost(null);
		setProxyPort(null);
		setSProxyHost(null);
		setSProxyPort(null);
	}

	public static String getProxyLogin() {
		return getPreferences().getProperty(PROXY_LOGIN);
	}

	public static void setProxyLogin(String proxyLogin) {
		getPreferences().setProperty(PROXY_LOGIN, proxyLogin);
		applyProxySettings();
	}

	public static String getProxyPassword() {
		return getPreferences().getPasswordProperty(PROXY_PASSWORD);
	}

	public static void setProxyPassword(String proxyPassword) {
		getPreferences().setPasswordProperty(PROXY_PASSWORD, proxyPassword);
		applyProxySettings();
	}

	public static String getFlexoServerInstanceURL() {
		String answer = getPreferences().getProperty(FLEXO_SERVER_INSTANCE_URL);
		if (answer == null || answer.trim().isEmpty()) {
			setFlexoServerInstanceURL("http://flexoserverinstances.openflexo.com");
			return getFlexoServerInstanceURL();
		}
		return answer;
	}

	public static void setFlexoServerInstanceURL(String url) {
		getPreferences().setProperty(FLEXO_SERVER_INSTANCE_URL, url);
	}

	public static String getWebServiceInstance() {
		String answer = getPreferences().getProperty(WEB_SERVICE_INSTANCE);
		if (answer == null || answer.trim().isEmpty()) {
			setWebServiceInstance("prod");
			return getWebServiceInstance();
		}
		return answer;
	}

	public static void setWebServiceInstance(String wsInstanceID) {
		getPreferences().setProperty(WEB_SERVICE_INSTANCE, wsInstanceID);
	}

}
