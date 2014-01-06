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
package org.openflexo.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.openflexo.toolbox.FileResource;

/**
 * Flexo logging manager: manages logs for the application above java.util.logging<br>
 * Also read and parse logs of an expired session of Flexo
 * 
 * @author sguerin
 */

// TODO: we can now no more save or load a log file: proceed as a service in flexofoundation layer
public class FlexoLoggingManager {

	static final Logger logger = Logger.getLogger(FlexoLoggingManager.class.getPackage().getName());

	// private static XMLMapping _loggingMapping = null;
	private static FlexoLoggingManager _instance;

	public LogRecords logRecords = null;

	private FlexoLoggingHandler _flexoLoggingHandler;

	private LoggingManagerDelegate _delegate;

	private boolean _keepLogTrace;
	private int _maxLogCount;
	private Level _loggingLevel;
	private String _configurationFile;

	public static interface LoggingManagerDelegate {
		public void setKeepLogTrace(boolean logTrace);

		public void setMaxLogCount(Integer maxLogCount);

		public void setDefaultLoggingLevel(Level lev);

		public void setConfigurationFileName(String configurationFile);
	}

	public static FlexoLoggingManager initialize(int numberOfLogsToKeep, boolean keepLogTraceInMemory, File configurationFile,
			Level logLevel, LoggingManagerDelegate delegate) throws SecurityException, IOException {
		if (isInitialized()) {
			return _instance;
		}
		return forceInitialize(numberOfLogsToKeep, keepLogTraceInMemory, configurationFile, logLevel, delegate);
	}

	public static FlexoLoggingManager forceInitialize(int numberOfLogsToKeep, boolean keepLogTraceInMemory, File configurationFile,
			Level logLevel, LoggingManagerDelegate delegate) {
		_instance = new FlexoLoggingManager();
		_instance._delegate = delegate;
		_instance._maxLogCount = numberOfLogsToKeep;
		_instance._keepLogTrace = keepLogTraceInMemory;
		_instance._loggingLevel = logLevel;
		if (configurationFile != null) {
			_instance._configurationFile = configurationFile.getAbsolutePath();
			File f = new File(System.getProperty("user.home"), "Library/Logs/Flexo/");
			if (!f.exists()) {
				f.mkdirs();
			}
		}
		_instance.logRecords = new LogRecords();
		try {
			LogManager.getLogManager().readConfiguration();
		} catch (SecurityException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning(e.getMessage());
			}
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning(e.getMessage());
			}
		}
		return _instance;
	}

	public static boolean isInitialized() {
		return _instance != null;
	}

	public static FlexoLoggingManager instance() {
		return _instance;
	}

	public static FlexoLoggingManager instance(FlexoLoggingHandler handler) {
		if (_instance != null) {
			_instance.setFlexoLoggingHandler(handler);
		}
		return _instance;
	}

	public void unhandledException(Exception e) {
		FlexoLoggingHandler flexoLoggingHandler = getFlexoLoggingHandler();
		if (flexoLoggingHandler != null) {
			flexoLoggingHandler.publishUnhandledException(new java.util.logging.LogRecord(java.util.logging.Level.WARNING,
					"Unhandled exception occured: " + e.getClass().getName()), e);
		} else {
			Logger.global.warning("Unexpected exception occured: " + e.getClass().getName());
		}
	}

	public void setFlexoLoggingHandler(FlexoLoggingHandler handler) {
		_flexoLoggingHandler = handler;
	}

	public FlexoLoggingHandler getFlexoLoggingHandler() {
		return _flexoLoggingHandler;
	}

	/*public static XMLMapping getLoggingMapping() {
		if (_loggingMapping == null) {
			File loggingModelFile;
			loggingModelFile = new FileResource("Models/LoggingModel.xml");
			if (!loggingModelFile.exists()) {
				System.err.println("File " + loggingModelFile.getAbsolutePath() + " doesn't exist. Maybe you have to check your paths ?");
				return null;
			} else {
				try {
					_loggingMapping = new XMLMapping(loggingModelFile);
				} catch (IOException e) {
					System.out.println(e.toString());
				} catch (SAXException e) {
					System.out.println(e.toString());
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					System.out.println(e.toString());
				}
			}
		}
		return _loggingMapping;
	}*/

	public static LogRecords loadLogFile(File logFile) {

		// TODO

		return null;

		/*try {
			FileInputStream fis = new FileInputStream(logFile);
			return (LogRecords) XMLDecoder.decodeObjectWithMapping(fis, getLoggingMapping());
		} catch (InvalidXMLDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidObjectSpecificationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessorInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.warning("Could not read " + logFile.getAbsolutePath());
		return null;*/
	}

	public String logsReport() {
		// StringEncoder.getDefaultInstance()._addConverter(new LevelConverter());
		/*try {
			return XMLCoder.encodeObjectWithMapping(logRecords, getLoggingMapping(), StringEncoder.getDefaultInstance());
		} catch (Exception e) {
			e.printStackTrace();
			return "No report available";
		}*/
		// TODO

		return logRecords.toString();
	}

	public boolean getKeepLogTrace() {
		return _keepLogTrace;
	}

	public void setKeepLogTrace(boolean logTrace) {
		_keepLogTrace = logTrace;
		if (_delegate != null) {
			_delegate.setKeepLogTrace(logTrace);
		}
	}

	public int getMaxLogCount() {
		return _maxLogCount;
	}

	public void setMaxLogCount(Integer maxLogCount) {
		_maxLogCount = maxLogCount;
		if (_delegate != null) {
			_delegate.setMaxLogCount(maxLogCount);
		}
	}

	public Level getDefaultLoggingLevel() {
		return _loggingLevel;
	}

	public void setDefaultLoggingLevel(Level lev) {
		_loggingLevel = lev;
		if (_delegate != null) {
			_delegate.setDefaultLoggingLevel(lev);
		}
		String fileName = "SEVERE";
		if (lev == Level.SEVERE) {
			fileName = "SEVERE";
		}
		if (lev == Level.WARNING) {
			fileName = "WARNING";
		}
		if (lev == Level.INFO) {
			fileName = "INFO";
		}
		if (lev == Level.FINE) {
			fileName = "FINE";
		}
		if (lev == Level.FINER) {
			fileName = "FINER";
		}
		if (lev == Level.FINEST) {
			fileName = "FINEST";
		}
		File newConfigurationFile = new FileResource("Config/logging_" + fileName + ".properties");
		_configurationFile = newConfigurationFile.getAbsolutePath();
		if (_delegate != null) {
			_delegate.setConfigurationFileName(_configurationFile);
		}
		reloadLoggingFile(newConfigurationFile.getAbsolutePath());
	}

	public String getConfigurationFileName() {
		return _configurationFile;
	}

	public void setConfigurationFileName(String configurationFile) {
		_configurationFile = configurationFile;
		if (_delegate != null) {
			_delegate.setConfigurationFileName(configurationFile);
		}
		reloadLoggingFile(configurationFile);
	}

	private boolean reloadLoggingFile(String filePath) {
		logger.info("reloadLoggingFile with " + filePath);
		System.setProperty("java.util.logging.config.file", filePath);
		try {
			LogManager.getLogManager().readConfiguration();
		} catch (SecurityException e) {
			logger.warning("The specified logging configuration file can't be read (not enough privileges).");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			logger.warning("The specified logging configuration file cannot be read.");
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
