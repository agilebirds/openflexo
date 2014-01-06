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

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.openflexo.logging.LoggingFilter.FilterType;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * This class is used to encode all log records of a session (current or expired) of Flexo.<br>
 * An instance of LogRecords can be represented in a FlexoLoggingViewer.
 * 
 * @author sguerin
 */
public class LogRecords implements HasPropertyChangeSupport {

	private final LinkedList<LogRecord> allRecords;
	private final ArrayList<LogRecord> filteredRecords = new ArrayList<LogRecord>();
	private List<LogRecord> records;

	private int totalLogs = 0;
	private int totalWarningLogs = 0;
	private int totalSevereLogs = 0;

	private int logCount = 0;
	private int warningCount = 0;
	private int severeCount = 0;

	private boolean filtersApplied = false;
	private boolean textSearchApplied = false;

	private final PropertyChangeSupport pcSupport;

	public LogRecords() {
		super();
		pcSupport = new PropertyChangeSupport(this);
		allRecords = new LinkedList<LogRecord>();
		records = allRecords;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	public void add(LogRecord record, FlexoLoggingManager loggingManager) {
		synchronized (allRecords) {
			if (loggingManager.getMaxLogCount() > -1 && allRecords.size() > loggingManager.getMaxLogCount()) {
				allRecords.remove(0);
			}
			allRecords.add(record);
			if (record.level == Level.WARNING) {
				totalWarningLogs++;
			}
			if (record.level == Level.SEVERE) {
				totalSevereLogs++;
			}
			totalLogs++;
		}
	}

	public LogRecord elementAt(int row) {
		return allRecords.get(row);
	}

	/*
	@Override
	public int getRowCount() {
		return allRecords.size();
	}

	@Override
	public int getColumnCount() {
		return 9;
	}

	@Override
	public String getColumnName(int arg0) {
		switch (arg0) {
		case 0:
			return "Level";
		case 1:
			return "Message";
		case 2:
			return "Package";
		case 3:
			return "Class";
		case 4:
			return "Method";
		case 5:
			return "Seq";
		case 6:
			return "Date";
		case 7:
			return "Millis";
		case 8:
			return "Thread";
		default:
			return "";
		}
	}

	@Override
	public Class<String> getColumnClass(int arg0) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	@Override
	public Object getValueAt(int row, int col) {
		LogRecord record = allRecords.get(row);
		switch (col) {
		case 0:
			return record.level;
		case 1:
			return record.message;
		case 2:
			return record.logger;
		case 3:
			return record.classAsString();
		case 4:
			return record.methodName;
		case 5:
			return record.sequenceAsString();
		case 6:
			return record.dateAsString();
		case 7:
			return record.millisAsString();
		case 8:
			return record.threadAsString();
		default:
			return "";
		}
	}

	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {
		// do nothing : a log record is not editable

	}

	@Override
	public void addTableModelListener(TableModelListener arg0) {
		model.addTableModelListener(arg0);
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {
		model.removeTableModelListener(arg0);
	}
	*/

	public List<LogRecord> getRecords() {
		return records;
	}

	public void setRecords(List<LogRecord> records) {
		this.records = records;
	}

	public void addToRecords(LogRecord record) {
		records.add(record);
	}

	public void removeFromRecords(LogRecord record) {
		records.remove(record);
	}

	public int getTotalLogs() {
		return totalLogs;
	}

	public int getWarningLogs() {
		return totalWarningLogs;
	}

	public int getSevereLogs() {
		return totalSevereLogs;
	}

	public int getLogCount() {
		if (!filtersApplied && !textSearchApplied) {
			return totalLogs;
		}
		return logCount;
	}

	public int getWarningCount() {
		if (!filtersApplied && !textSearchApplied) {
			return totalWarningLogs;
		}
		return warningCount;
	}

	public int getSevereCount() {
		if (!filtersApplied && !textSearchApplied) {
			return totalSevereLogs;
		}
		return severeCount;
	}

	public void applyFilters(List<LoggingFilter> filters) {
		logCount = 0;
		warningCount = 0;
		severeCount = 0;
		filtersApplied = true;
		filteredRecords.clear();
		boolean onlyKeep = false;
		for (LoggingFilter f : filters) {
			if (f.type == FilterType.OnlyKeep) {
				onlyKeep = true;
			}
		}
		for (LogRecord r : allRecords) {
			boolean keepRecord = !onlyKeep;
			for (LoggingFilter f : filters) {
				if (f.filterDoesApply(r)) {
					if (f.type == FilterType.OnlyKeep) {
						keepRecord = true;
					}
				}
			}
			for (LoggingFilter f : filters) {
				if (f.filterDoesApply(r)) {
					if (f.type == FilterType.Dismiss) {
						keepRecord = false;
					}
				}
			}
			if (keepRecord) {
				filteredRecords.add(r);
				logCount++;
				if (r.level == Level.WARNING) {
					warningCount++;
				} else if (r.level == Level.SEVERE) {
					severeCount++;
				}
			}
		}
		records = filteredRecords;
		notifyFilteringChange();
	}

	public void dismissFilters() {
		filtersApplied = false;
		records = allRecords;
		notifyFilteringChange();
	}

	public void searchText(String someText) {
		logCount = 0;
		warningCount = 0;
		severeCount = 0;
		textSearchApplied = true;
		records = new ArrayList<LogRecord>();
		LoggingFilter f = new LoggingFilter("search");
		f.setHasFilteredMessage(true);
		f.filteredContent = someText;
		for (LogRecord r : filtersApplied() ? filteredRecords : allRecords) {
			if (f.filterDoesApply(r)) {
				records.add(r);
				logCount++;
				if (r.level == Level.WARNING) {
					warningCount++;
				} else if (r.level == Level.SEVERE) {
					severeCount++;
				}
			}
		}
		notifyFilteringChange();
	}

	public void dismissSearchText() {
		textSearchApplied = false;
		if (filtersApplied()) {
			records = filteredRecords;
			notifyFilteringChange();
		} else {
			records = allRecords;
			notifyFilteringChange();
		}
	}

	private void notifyFilteringChange() {
		pcSupport.firePropertyChange("logCount", -1, logCount);
		pcSupport.firePropertyChange("warningCount", -1, warningCount);
		pcSupport.firePropertyChange("severeCount", -1, severeCount);
		pcSupport.firePropertyChange("records", null, records);
		pcSupport.firePropertyChange("filtersApplied", false, true);
		pcSupport.firePropertyChange("textSearchApplied", false, true);
	}

	public boolean filtersApplied() {
		return filtersApplied;
	}

	public boolean textSearchApplied() {
		return textSearchApplied;
	}

}
