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
import java.io.InputStream;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gpolet
 * 
 */
public class WinRegistryAccess {

	private static final String REGQUERY_UTIL = "reg query ";

	private static final String REGSET_UTIL = "reg add ";

	public static final String REG_SZ_TOKEN = "REG_SZ";

	public static final String REG_EXPAND_SZ_TOKEN = "REG_EXPAND_SZ";

	public static final String REG_BINARY = "REG_BINARY";

	public static final String REG_DWORD_TOKEN = "REG_DWORD";

	private static final String ENVIRONMENT_VARIABLE_REGEXP = "%([^%=]+)%";

	private static final Pattern ENVIRONMENT_VARIABLE_PATTERN = Pattern.compile(ENVIRONMENT_VARIABLE_REGEXP);

	/**
	 * Returns the value for an attribute of the registry in Windows. If you want to now the processor speed of the machine, you will pass
	 * the following path: "HKLM\HARDWARE\DESCRIPTION\System\CentralProcessor\0" and the following attribute name: ~MHz
	 * 
	 * @param path
	 *            - the registry path to the desired value
	 * @param attributeName
	 *            - the name of the attribute or null for the default
	 * @param attributeType
	 *            - the type of attribute (DWORD/SZ/...) default is REG_SZ
	 * @return - the value for the attribute located in the given path
	 */
	public static String getRegistryValue(String path, String attributeName, String attributeType) {
		if (attributeType == null) {
			attributeType = REG_SZ_TOKEN;
		}
		try {
			if (!path.startsWith("\"")) {
				path = "\"" + path + "\"";
			}
			StringBuilder sb = new StringBuilder();
			sb.append(REGQUERY_UTIL);
			sb.append(path);
			sb.append(' ');
			if (attributeName != null) {
				sb.append("/v ");
				sb.append(attributeName);
			} else {
				sb.append("/ve");
			}
			Process process = Runtime.getRuntime().exec(sb.toString());
			ConsoleReader reader = new ConsoleReader(process.getInputStream());
			reader.start();
			process.waitFor();
			reader.join();
			String result = reader.getResult();
			int p = result.indexOf(attributeType);
			if (p == -1) {
				return null;
			}
			return result.substring(p + attributeType.length()).trim();
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean setRegistryValue(String path, String attributeName, String attributeType, String value) {
		if (attributeType == null) {
			attributeType = REG_SZ_TOKEN;
		}
		try {
			if (!path.startsWith("\"")) {
				path = "\"" + path + "\"";
			}
			StringBuilder sb = new StringBuilder();
			sb.append(REGSET_UTIL);
			sb.append(path);
			sb.append(' ');
			if (attributeName != null) {
				sb.append("/v ");
				sb.append(attributeName);
			} else {
				sb.append("/ve");
			}
			sb.append(" /t ").append(attributeType);
			sb.append(" /d ").append(value);
			sb.append(" /f");
			Process process = Runtime.getRuntime().exec(sb.toString());
			ConsoleReader reader = new ConsoleReader(process.getInputStream());
			reader.start();
			process.waitFor();
			reader.join();
			return process.exitValue() == 0;
		} catch (Exception e) {
			return false;
		}
	}

	public static class ConsoleReader extends Thread {
		private InputStream is;

		private StringWriter sw;

		ConsoleReader(InputStream is) {
			this.is = is;
			sw = new StringWriter();
		}

		@Override
		public void run() {
			try {
				int c;
				while ((c = is.read()) != -1) {
					sw.write(c);
				}
			} catch (IOException e) {
				;
			}
		}

		String getResult() {
			return sw.toString();
		}
	}

	public static String getJDKHome() {
		String key = "\"HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\"";
		String currentVersionAtt = "CurrentVersion";
		String javaHomeAtt = "JavaHome";
		String res1 = getRegistryValue(key, currentVersionAtt, null);
		String res2 = getRegistryValue(key + "\\" + res1, javaHomeAtt, null);
		return res2;
	}

	public static String substituteEnvironmentVariable(String string) {
		if (string == null || string.length() == 0) {
			return string;
		}
		if (string.indexOf('%') == -1) {
			return string;
		}
		StringBuffer sb = new StringBuffer();
		Matcher m = ENVIRONMENT_VARIABLE_PATTERN.matcher(string);
		while (m.find()) {
			String replacement = System.getenv(m.group(1));
			if (replacement == null) {
				replacement = m.group();
			}
			replacement = Matcher.quoteReplacement(replacement);
			m.appendReplacement(sb, replacement);
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static enum Style {
		STRETCHED(2, 0), CENTERED(1, 0), TILED(1, 1);

		private int style;
		private int tile;

		private Style(int style, int tile) {
			this.style = style;
			this.tile = tile;
		}

		public int getStyle() {
			return style;
		}

		public int getTile() {
			return tile;
		}
	}

	public static void main(String s[]) {
		String path = "\"HKEY_CURRENT_USER\\Control Panel\\Desktop\"";
		String wallpaperStyle = "WallpaperStyle";
		String wallpaperStyleTile = "TileWallpaper";
		String wallpaper = "Wallpaper";
		setRegistryValue(path, wallpaperStyle, null, String.valueOf(Style.STRETCHED.getStyle()));
		setRegistryValue(path, wallpaperStyleTile, null, String.valueOf(Style.STRETCHED.getTile()));
		setRegistryValue(path, wallpaper, null, "D:\\share\\Wallpaper\\Canyon.jpg");
	}
}
