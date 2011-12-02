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
package org.openflexo.br;

import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.openflexo.components.ProgressWindow;
import org.openflexo.kvc.KVCObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLSerializable;

/**
 * This class is used to encode all Bug Reports of Flexo
 * 
 * @author sguerin
 */
public class BugReports extends KVCObject implements XMLSerializable, TableModel {

	public Vector records;

	private DefaultTableModel model;

	public BugReports() {
		super();
		records = new Vector();
		model = new DefaultTableModel();
		reload();
	}

	public void reload() {
		records = new Vector();
		File[] allBRFiles = getDatabaseDirectory().listFiles();
		if (allBRFiles == null) {
			allBRFiles = new File[0];
		}
		if (!ProgressWindow.hasInstance()) {
			ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("loading_bug_reports"), allBRFiles.length);
		}
		for (int i = 0; i < allBRFiles.length; i++) {
			try {
				if (allBRFiles[i].isFile() && (allBRFiles[i].getName().endsWith(".brxml"))) {
					ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("loading") + " " + allBRFiles[i].getName());
					FileInputStream in = new FileInputStream(allBRFiles[i]);
					BugReport record = (BugReport) XMLDecoder.decodeObjectWithMapping(in, BugReport.getBRMapping());
					in.close();
					String fileName = allBRFiles[i].getName();
					record.identifier = fileName.substring(0, fileName.length() - 6);
					add(record);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		ProgressWindow.hideProgressWindow();

	}

	public void add(BugReport record) {
		records.add(record);
		model.fireTableDataChanged();
	}

	public void remove(BugReport record) {
		record.getFile().delete();
		records.remove(record);
		model.fireTableDataChanged();
	}

	public BugReport elementAt(int row) {
		return (BugReport) records.elementAt(row);
	}

	public void saveAll() {
		if (!ProgressWindow.hasInstance()) {
			ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("saving_bug_reports"), getRowCount());
		}
		for (int i = 0; i < getRowCount(); i++) {
			BugReport record = elementAt(i);
			ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("saving") + " " + record.getFile().getName());
			record.save();
		}
		ProgressWindow.hideProgressWindow();
	}

	@Override
	public int getRowCount() {
		return records.size();
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public String getColumnName(int arg0) {
		switch (arg0) {
		case 0:
			return "Identifier";
		case 1:
			return "Title";
		case 2:
			return "Severity";
		case 3:
			return "Status";
		case 4:
			return "Assigned to";
		default:
			return null;
		}
	}

	@Override
	public Class getColumnClass(int arg0) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	@Override
	public Object getValueAt(int row, int col) {
		BugReport record = (BugReport) records.elementAt(row);
		switch (col) {
		case 0:
			return record.identifier;
		case 1:
			return record.title;
		case 2:
			return record.severityAsString();
		case 3:
			return record.statusAsString();
		case 4:
			return record.assignedUserName;
		default:
			return "";
		}
	}

	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {

	}

	@Override
	public void addTableModelListener(TableModelListener arg0) {
		model.addTableModelListener(arg0);
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {
		model.removeTableModelListener(arg0);
	}

	private static BugReports _instance;

	public static BugReports instance() {
		if (_instance == null) {
			_instance = new BugReports();
		}
		return _instance;
	}

	public static File getDatabaseDirectory() {
		return new FileResource("BugReportsDatabase");
	}

}
