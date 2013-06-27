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
package org.openflexo.toolbox;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyUtils {
	private static final String WIN_REG_PROXY_PATH = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";
	private static final String WIN_REG_PROXY_ENABLE = "ProxyEnable"; // DWORD
	private static final String WIN_REG_PROXY_SERVER = "ProxyServer";
	private static final String WIN_REG_AUTO_CONFIG_URL = "AutoConfigURL";

	private static final Pattern PROXY_PATTERN = Pattern.compile("PROXY\\s+([a-zA-Z-.0-9]+)(:(\\p{Digit}+))?");

	public static boolean isProxyEnabled() {
		if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
			return Integer.decode(WinRegistryAccess.getRegistryValue(WIN_REG_PROXY_PATH, WIN_REG_PROXY_ENABLE,
					WinRegistryAccess.REG_DWORD_TOKEN)) != 0;
		}
		return false;
	}

	public static boolean autoDetectSettingsEnabled() {
		if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
			return WinRegistryAccess.getRegistryValue(WIN_REG_PROXY_PATH, WIN_REG_AUTO_CONFIG_URL, WinRegistryAccess.REG_SZ_TOKEN) != null;
		}
		return false;
	}

	public static String[] getHTTPProxyPort(boolean secure) {
		String[] proxyHost = null;
		if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
			try {
				String proxyServer = WinRegistryAccess.getRegistryValue(WIN_REG_PROXY_PATH, WIN_REG_PROXY_SERVER,
						WinRegistryAccess.REG_SZ_TOKEN);
				if (proxyServer != null) {
					int defaultPort = secure ? 443 : 80;
					if (proxyServer.indexOf(";") > -1) {
						String[] s = proxyServer.split(";");
						for (String string : s) {
							if (string.toLowerCase().startsWith("https=")) {
								string = string.substring("https=".length());
								proxyHost = ToolBox.getHostPortFromString(string, defaultPort);
								if (secure) {
									break;
								}
							}
							if (string.toLowerCase().startsWith("http=")) {
								string = string.substring("http=".length());
								proxyHost = ToolBox.getHostPortFromString(string, defaultPort);
								if (!secure) {
									break;
								}
							}
						}
					} else {
						proxyHost = ToolBox.getHostPortFromString(proxyServer, defaultPort);
					}
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		return proxyHost;
	}

	public static List<String[]> getProxiesFromAutoConfigURL(URL autoConfigURL, int defaultPort) {
		List<String[]> proxies = new ArrayList<String[]>();
		if (autoConfigURL == null) {
			return proxies;
		}
		try {
			String pac = ToolBox.getContentAtURL(autoConfigURL);
			if (pac == null) {
				return proxies;
			}
			Matcher m = PROXY_PATTERN.matcher(pac);
			while (m.find()) {
				String proxyHost = m.group(1);
				int port = defaultPort;
				if (m.groupCount() > 2) {
					port = Integer.valueOf(m.group(3));
				}
				proxies.add(new String[] { proxyHost, String.valueOf(port) });
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Can't read auto config file: " + autoConfigURL.toString());
		}
		return proxies;
	}

	public static URL getAutoConfigURL() {
		URL autoConfigURL = null;
		if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
			try {
				/*String proxyPath = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Connections";
				String s ="DefaultConnectionSettings";
				s=WinRegistryAccess.getRegistryValue(proxyPath, s, WinRegistryAccess.REG_BINARY);
				boolean autoDetectNetworkSettings = Integer.parseInt(s.substring(8, 10), 16)%2!=0;
				if (autoDetectNetworkSettings) {
					// OK let's go for WPAD
					autoConfigURL = WPADURL();
				}*/
				if (autoConfigURL == null) {
					String autoConfig = WinRegistryAccess.getRegistryValue(WIN_REG_PROXY_PATH, WIN_REG_AUTO_CONFIG_URL,
							WinRegistryAccess.REG_SZ_TOKEN);
					if (autoConfig != null) {
						try {
							return new URL(autoConfig);
						} catch (MalformedURLException e) {
							e.printStackTrace();
							return null;
						}
					}
				}

			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		return autoConfigURL;
	}

	/**
	 * Implements the WPAD lookup like explained here: http://en.wikipedia.org/wiki/Web_Proxy_Autodiscovery_Protocol This is insane.
	 * 
	 * @return
	 */
	public static URL WPADURL() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String host = addr.getHostName();
			while (host.indexOf(".") > -1) {
				try {
					URL attempt = new URL("http://wpad" + host.substring(host.indexOf(".")) + "/wpad.dat");
					try {
						HttpURLConnection conn = (HttpURLConnection) attempt.openConnection();
						int code = conn.getResponseCode();
						if (code >= 200 && code < 300) {
							return attempt;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return null;
				}
				host = host.substring(host.indexOf(".") + 1);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getLocalDomain() {
		try {
			InetAddress localaddr = InetAddress.getLocalHost();
			String hostName = localaddr.getHostName();
			// my.local.domain.com
			if (hostName != null) {
				int index = hostName.lastIndexOf('.');
				index = hostName.lastIndexOf('.', index - 1);
				if (index > -1) {
					return hostName.substring(index + 1); // domain.com
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		System.err.println("Proxy enabled?" + isProxyEnabled());
		System.err.println("Autodetect?" + autoDetectSettingsEnabled());
	}

}
