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

import java.util.LinkedList;
import java.util.logging.Level;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.openflexo.kvc.KVCObject;
import org.openflexo.xmlcode.XMLSerializable;

/**
 * This class is used to encode all log records of a session (current or expired) of Flexo. An instance of LogRecords can be represented in
 * a FlexoLoggingViewerWindow.
 * 
 * @author sguerin
 */
public class LogRecords extends KVCObject implements XMLSerializable, TableModel {

	private LinkedList<LogRecord> records;

	private int totalLogs = 0;
	private int warningLogs = 0;
	private int severeLogs = 0;

	private DefaultTableModel model;

	public LogRecords() {
		super();
		records = new LinkedList<LogRecord>();
		model = new DefaultTableModel();
	}

	public void add(LogRecord record) {
		synchronized (records) {
			if (FlexoLoggingManager.getLogCount() > -1 && records.size() > FlexoLoggingManager.getLogCount()) {
				records.remove(0);
			}
			records.add(record);
			if (record.level == Level.WARNING) {
				warningLogs++;
			}
			if (record.level == Level.SEVERE) {
				severeLogs++;
			}
			totalLogs++;
		}
		model.fireTableDataChanged();
	}

	public LogRecord elementAt(int row) {
		return records.get(row);
	}

	@Override
	public int getRowCount() {
		return records.size();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<String> getColumnClass(int arg0) {
		return String.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int col) {
		LogRecord record = records.get(row);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {
		// do nothing : a log record is not editable

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
	 */
	@Override
	public void addTableModelListener(TableModelListener arg0) {
		model.addTableModelListener(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
	 */
	@Override
	public void removeTableModelListener(TableModelListener arg0) {
		model.removeTableModelListener(arg0);
	}

	public LinkedList<LogRecord> getRecords() {
		return records;
	}

	public int getTotalLogs() {
		return totalLogs;
	}

	public int getWarningLogs() {
		return warningLogs;
	}

	public int getSevereLogs() {
		return severeLogs;
	}

}
