package org.openflexo.fib.utils;

import java.awt.Color;
import java.awt.Window;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.logging.LogRecord;
import org.openflexo.logging.LogRecords;
import org.openflexo.logging.LoggingFilter;
import org.openflexo.logging.LoggingFilter.FilterType;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.ImageIconResource;
import org.openflexo.toolbox.StringUtils;

public class FlexoLoggingViewer implements HasPropertyChangeSupport {

	static final Logger logger = Logger.getLogger(FlexoLoggingViewer.class.getPackage().getName());

	public static final File LOGGING_VIEWER_FIB = new FileResource("Fib/LoggingViewer.fib");

	public static final ImageIcon FILTER_ICON = new ImageIconResource("Icons/Utils/Search.png");

	private FlexoLoggingManager loggingManager;

	private PropertyChangeSupport _pcSupport;

	public Vector<LoggingFilter> filters = new Vector<LoggingFilter>();
	public String searchedText;

	public boolean displayLogLevel = false;
	public boolean displayPackage = true;
	public boolean displayClass = true;
	public boolean displayMethod = true;
	public boolean displaySequence = true;
	public boolean displayDate = true;
	public boolean displayMillis = true;
	public boolean displayThread = true;

	private static FlexoLoggingViewer instance;
	private static FIBDialog<FlexoLoggingViewer> dialog;

	public static void showLoggingViewer(FlexoLoggingManager loggingManager, Window parent) {
		System.out.println("showLoggingViewer with " + loggingManager);
		FIBComponent loggingViewerComponent = FIBLibrary.instance().retrieveFIBComponent(LOGGING_VIEWER_FIB);
		if (instance == null || dialog == null) {
			instance = new FlexoLoggingViewer(loggingManager);
			dialog = FIBDialog.instanciateComponent(loggingViewerComponent, instance, parent, false, FlexoLocalization.getMainLocalizer());
		} else {
			dialog.showDialog();
		}
	}

	public FlexoLoggingViewer(FlexoLoggingManager loggingManager) {
		_pcSupport = new PropertyChangeSupport(this);
		this.loggingManager = loggingManager;
	}

	public LogRecords getRecords() {
		return loggingManager.logRecords;
	}

	public Icon getIconForFilter(LoggingFilter filter) {
		return FILTER_ICON;
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

	public Color getBgColorForLogRecord(LogRecord record) {
		if (getRecords().filtersApplied()) {
			for (LoggingFilter f : filters) {
				if (f.type == FilterType.Highlight && f.filterDoesApply(record)) {
					return Color.YELLOW;
				}
			}
		}
		return null;
	}

	private static final Level[] LEVELS = { Level.SEVERE, Level.WARNING, Level.INFO, Level.FINE, Level.FINER, Level.FINEST };

	public Level[] getAvailableLevels() {
		return LEVELS;
	}

	public int getNumberOfLogsToKeep() {
		return loggingManager.getMaxLogCount();
	}

	public void setNumberOfLogsToKeep(int numberOfLogsToKeep) {
		loggingManager.setMaxLogCount(numberOfLogsToKeep);
	}

	public boolean getIsInfiniteNumberOfLogs() {
		return getNumberOfLogsToKeep() == -1;
	}

	public void setIsInfiniteNumberOfLogs(boolean isInfinite) {
		if (isInfinite) {
			setNumberOfLogsToKeep(-1);
		} else {
			setNumberOfLogsToKeep(500);
		}
	}

	public boolean isKeepLogTraceInMemory() {
		return loggingManager.getKeepLogTrace();
	}

	public void setKeepLogTraceInMemory(boolean keepLogTraceInMemory) {
		loggingManager.setKeepLogTrace(keepLogTraceInMemory);
	}

	private File configurationFile;

	public File getConfigurationFile() {
		if (configurationFile == null) {
			String loggingFileName = loggingManager.getConfigurationFileName();
			if (loggingFileName != null && new File(loggingFileName).exists()) {
				configurationFile = new File(loggingFileName);
			}
		}
		return configurationFile;
	}

	public void setConfigurationFile(File configurationFile) {
		this.configurationFile = configurationFile;
		loggingManager.setConfigurationFileName(configurationFile.getAbsolutePath());
		_pcSupport.firePropertyChange("configurationFile", null, configurationFile);
	}

	public Level getLogLevel() {
		return loggingManager.getDefaultLoggingLevel();
	}

	public void setLogLevel(Level lev) {
		loggingManager.setDefaultLoggingLevel(lev);
		configurationFile = null;
		_pcSupport.firePropertyChange("configurationFile", null, configurationFile);
	}

	public void refresh() {
		_pcSupport.firePropertyChange("records", null, getRecords());
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

	public LoggingFilter createFilter() {
		LoggingFilter newFilter = new LoggingFilter("New filter");
		filters.add(newFilter);
		return newFilter;
	}

	public void deleteFilter(LoggingFilter filter) {
		filters.remove(filter);
	}

	public void applyFilters() {
		getRecords().applyFilters(filters);
	}

	public void dismissFilters() {
		getRecords().dismissFilters();
	}

	public void searchText() {
		if (StringUtils.isNotEmpty(searchedText)) {
			getRecords().searchText(searchedText);
		}
	}

	public void dismissSearchText() {
		getRecords().dismissSearchText();
	}
}
