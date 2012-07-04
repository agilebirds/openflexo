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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.ScenarioRecorder;

/**
 * This class is intended to model preferences of OpenFlexo application<br>
 * To be accessed through all the application, all methods are statically defined.
 * 
 * @author sguerin
 */
public class FlexoProperties {
	public static final String ALLOWSDOCSUBMISSION = "allowsDocSubmission";
	public static final String LOGCOUNT = "logCount";
	public static final String KEEPLOGTRACE = "keepLogTrace";
	public static final String DEFAULT_LOG_LEVEL = "default.logging.level";
	public static final String CUSTOM_LOG_CONFIG_FILE = "logging.file.name";

	private static FlexoProperties _instance;
	private Properties applicationProperties = null;

	protected FlexoProperties(File preferencesFile) {
		super();
		applicationProperties = new Properties();
		try {
			applicationProperties.load(new FileInputStream(preferencesFile));
		} catch (FileNotFoundException e) {
			FlexoController.showError("Cannot find configuration file : " + preferencesFile.getAbsolutePath());
			System.exit(0);
		} catch (IOException e) {
			FlexoController.showError("Cannot read configuration file : " + preferencesFile.getAbsolutePath());
			System.exit(0);
		}

		ScenarioRecorder.ENABLE = "true".equals(applicationProperties.getProperty("record"));
		setIsLoggingTrace(isLoggingTrace());
		setMaxLogCount(getMaxLogCount());
		setDefaultLoggingLevel(getDefaultLoggingLevel());
		System.setProperty("java.util.logging.config.file", new FileResource("Config/logging.properties").getAbsolutePath());
		if (applicationProperties.getProperty(DEFAULT_LOG_LEVEL) != null) {
			System.setProperty("java.util.logging.config.file",
					new FileResource("Config/logging_" + applicationProperties.getProperty(DEFAULT_LOG_LEVEL) + ".properties")
							.getAbsolutePath());
		}
		if (applicationProperties.getProperty(CUSTOM_LOG_CONFIG_FILE) != null) {
			System.setProperty("java.util.logging.config.file",
					new File(applicationProperties.getProperty(CUSTOM_LOG_CONFIG_FILE)).getAbsolutePath());
		}
	}

	public String getApplicationProperty(String property) {
		return applicationProperties.getProperty(property);
	}

	public void setApplicationProperty(String property, String value) {
		applicationProperties.setProperty(property, value);
	}

	public boolean isLoggingTrace() {
		return "true".equals(applicationProperties.getProperty(KEEPLOGTRACE));
	}

	public String getLoggingFileName() {
		return applicationProperties.getProperty(CUSTOM_LOG_CONFIG_FILE);
	}

	public void setLoggingFileName(String loggingFileName) {
		if (loggingFileName != null) {
			applicationProperties.setProperty(CUSTOM_LOG_CONFIG_FILE, loggingFileName);
		} else {
			applicationProperties.remove(CUSTOM_LOG_CONFIG_FILE);
		}
	}

	public File getCustomLoggingFile() {
		if (getLoggingFileName() == null) {
			return null;
		}
		return new File(getLoggingFileName());
	}

	public Level getDefaultLoggingLevel() {
		String returned = applicationProperties.getProperty(DEFAULT_LOG_LEVEL);
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

	public void setDefaultLoggingLevel(Level l) {
		applicationProperties.setProperty(DEFAULT_LOG_LEVEL, l.getName());
	}

	public boolean getIsLoggingTrace() {
		return applicationProperties.getProperty(KEEPLOGTRACE).equalsIgnoreCase("true");
	}

	public void setIsLoggingTrace(boolean b) {
		applicationProperties.setProperty(KEEPLOGTRACE, b ? "true" : "false");
	}

	public int getMaxLogCount() {
		return applicationProperties.getProperty(LOGCOUNT) == null ? 0 : Integer.valueOf(applicationProperties.getProperty(LOGCOUNT));
	}

	public void setMaxLogCount(int c) {
		applicationProperties.setProperty(LOGCOUNT, String.valueOf(c));
	}

	public boolean getAllowsDocSubmission() {
		return "true".equals(applicationProperties.getProperty(ALLOWSDOCSUBMISSION));
	}

	public void setAllowsDocSubmission(boolean b) {
		applicationProperties.setProperty(ALLOWSDOCSUBMISSION, String.valueOf(b));
	}

	public static FlexoProperties load() {
		return instance();
	}

	public static FlexoProperties instance() {
		if (_instance == null) {
			_instance = new FlexoProperties(new FileResource("Config/Flexo.properties"));
		}
		return _instance;
	}

}
