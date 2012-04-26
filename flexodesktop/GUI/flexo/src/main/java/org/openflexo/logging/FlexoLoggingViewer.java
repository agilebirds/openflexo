package org.openflexo.logging;

import java.awt.Color;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.properties.FlexoProperties;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.view.controller.FlexoController;

public class FlexoLoggingViewer implements HasPropertyChangeSupport {

	static final Logger logger = Logger.getLogger(FlexoLoggingViewer.class.getPackage().getName());

	public static final File LOGGING_VIEWER_FIB = new FileResource("Fib/LoggingViewer.fib");

	private LogRecords records;

	private PropertyChangeSupport _pcSupport;

	private int numberOfLogsToKeep = -1;
	private boolean keepLogTraceInMemory = true;
	private File configurationFile;
	private Level logLevel = Level.WARNING;

	public boolean displayLogLevel = false;
	public boolean displayPackage = true;
	public boolean displayClass = true;
	public boolean displayMethod = true;
	public boolean displaySequence = true;
	public boolean displayDate = true;
	public boolean displayMillis = true;
	public boolean displayThread = true;

	public FlexoLoggingViewer(LogRecords records) {
		_pcSupport = new PropertyChangeSupport(this);
		this.records = records;
		logLevel = FlexoProperties.instance().getDefaultLoggingLevel();
		System.out.println("logLevel=" + logLevel);
		keepLogTraceInMemory = FlexoProperties.instance().getIsLoggingTrace();
		System.out.println("keepLogTraceInMemory=" + keepLogTraceInMemory);
		numberOfLogsToKeep = FlexoProperties.instance().getMaxLogCount();
		System.out.println("numberOfLogsToKeep=" + numberOfLogsToKeep);
		String loggingFileName = FlexoProperties.instance().getLoggingFileName();
		System.out.println("loggingFileName=" + loggingFileName);
		if (loggingFileName != null && (new File(loggingFileName)).exists()) {
			setConfigurationFile(new File(loggingFileName));
		}
	}

	public LogRecords getRecords() {
		return records;
	}

	public void showLoggingViewer() {
		FIBComponent loggingViewerComponent = FIBLibrary.instance().retrieveFIBComponent(LOGGING_VIEWER_FIB);
		FIBDialog dialog = FIBDialog.instanciateComponent(loggingViewerComponent, this, null, true, FlexoLocalization.getMainLocalizer());
	}

	public Icon getIconForLogRecord(LogRecord record) {
		if (record.level == Level.WARNING) {
			return UtilsIconLibrary.WARNING_ICON;
		}
		if (record.level == Level.SEVERE) {
			return UtilsIconLibrary.ERROR_ICON;
		}
		return null;
	}

	public Color getColorForLogRecord(LogRecord record) {
		if (record.level == Level.INFO) {
			return Color.BLACK;
		} else if (record.level == Level.WARNING) {
			return Color.RED;
		} else if (record.level == Level.SEVERE) {
			return Color.PINK;
		}
		return Color.GRAY;
	}

	private static final Level[] LEVELS = { Level.SEVERE, Level.WARNING, Level.INFO, Level.FINE, Level.FINER, Level.FINEST };

	public Level[] getAvailableLevels() {
		return LEVELS;
	}

	public int getNumberOfLogsToKeep() {
		return numberOfLogsToKeep;
	}

	public void setNumberOfLogsToKeep(int numberOfLogsToKeep) {
		this.numberOfLogsToKeep = numberOfLogsToKeep;
		FlexoProperties.instance().setMaxLogCount(numberOfLogsToKeep);
	}

	public boolean getIsInfiniteNumberOfLogs() {
		return numberOfLogsToKeep == -1;
	}

	public void setIsInfiniteNumberOfLogs(boolean isInfinite) {
		if (isInfinite) {
			numberOfLogsToKeep = -1;
		} else {
			setNumberOfLogsToKeep(500);
		}
	}

	public boolean isKeepLogTraceInMemory() {
		return keepLogTraceInMemory;
	}

	public void setKeepLogTraceInMemory(boolean keepLogTraceInMemory) {
		this.keepLogTraceInMemory = keepLogTraceInMemory;
		FlexoProperties.instance().setIsLoggingTrace(keepLogTraceInMemory);
	}

	public File getConfigurationFile() {
		return configurationFile;
	}

	public void setConfigurationFile(File configurationFile) {
		this.configurationFile = configurationFile;
		reloadLoggingFile(configurationFile.getAbsolutePath());
		FlexoProperties.instance().setLoggingFileName(configurationFile.getAbsolutePath());
	}

	public Level getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(Level lev) {
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
		reloadLoggingFile(new FileResource("Config/logging_" + fileName + ".properties").getAbsolutePath());
		FlexoProperties.instance().setLoggingFileName(null);
		// FlexoProperties.instance().setDefaultLoggingLevel(fileName);
	}

	private boolean reloadLoggingFile(String filePath) {
		System.out.println("reloadLoggingFile with " + filePath);
		logger.info("reloadLoggingFile with " + filePath);
		System.setProperty("java.util.logging.config.file", filePath);
		try {
			LogManager.getLogManager().readConfiguration();
		} catch (SecurityException e) {
			FlexoController.showError("The specified logging configuration file can't be read (not enough privileges).");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			FlexoController.showError("The specified logging configuration file cannot be read.");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void refresh() {
		_pcSupport.firePropertyChange("records", null, null);
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return _pcSupport;
	}

	public void printStackTrace(LogRecord record) {
		if (record == null) {
			return;
		}
		System.err.println("Stack trace for '" + record.message + "':");
		System.err.println(record.getStackTraceAsString());
	}
}
