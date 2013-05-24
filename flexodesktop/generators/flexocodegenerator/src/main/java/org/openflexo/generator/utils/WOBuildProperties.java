package org.openflexo.generator.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.openflexo.toolbox.ToolBox;

public class WOBuildProperties {
	private static final String WO_BUILD_PROPERTIES = "wobuild.properties";

	private static final String[] ABSOLUTE_KEYS = new String[] { "wo.dir.root", "wo.woroot", "wo.dir.local.library", "wo.wolocalroot" };

	private static final String[] USER_HOME_KEYS = new String[] { "wo.dir.user.home.library.frameworks", "wo.dir.user.home.library" };

	private static final String[] WO_DIR_KEYS = new String[] { "wo.dir.local", "wo.wosystemroot", "wo.dir.library",
			"wo.dir.library.frameworks", "wo.dir.local.library.frameworks", "wo.dir.system" };

	private static final String[] DEFAULT_ABSOLUTE_PATH = new String[] { "/", "/", "/Library", "/" };

	private static final String[] DEFAULT_USER_HOME_RELATIVE_PATH = new String[] { "Library/Frameworks", "Library" };

	private static final String[] DEFAULT_WO_DIR_RELATIVE_PATH = new String[] { "", "", "Library", "Library/Frameworks",
			"Local/Library/Frameworks", "" };

	/**
	 *
	 */
	public static void createWoBuildProperties() {
		File userHome = new File(System.getProperty("user.home"));
		File f = new File(userHome, "Library/" + WO_BUILD_PROPERTIES);
		if (!f.exists()) {
			try {
				synchronized (WOBuildProperties.class) {
					if (!f.exists()) {
						if (!f.getParentFile().exists()) {
							f.getParentFile().mkdirs();
						}
						f.createNewFile();
						Properties p = new Properties();
						for (int i = 0; i < ABSOLUTE_KEYS.length; i++) {
							String s = ABSOLUTE_KEYS[i];
							try {
								p.put(s, new File(DEFAULT_ABSOLUTE_PATH[i]).getCanonicalPath());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						File WO_DIR = null;
						if (System.getenv("NEXT_ROOT") != null) {
							WO_DIR = new File(System.getenv("NEXT_ROOT"));
						}
						if (WO_DIR == null) {
							if (ToolBox.isWindows()) {
								File test = new File("c:\\System");
								if (test.exists()) {
									WO_DIR = test;
								} else {
									WO_DIR = new File("c:\\Apple");
								}
							} else {
								WO_DIR = new File("/System");
							}
						}
						for (int i = 0; i < WO_DIR_KEYS.length; i++) {
							String s = WO_DIR_KEYS[i];
							try {
								p.put(s, new File(WO_DIR, DEFAULT_WO_DIR_RELATIVE_PATH[i]).getCanonicalPath());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						for (int i = 0; i < USER_HOME_KEYS.length; i++) {
							String s = USER_HOME_KEYS[i];
							try {
								p.put(s, new File(userHome, DEFAULT_USER_HOME_RELATIVE_PATH[i]).getCanonicalPath());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						try {
							p.store(new FileOutputStream(f), "This file stores the wo specific properties");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
