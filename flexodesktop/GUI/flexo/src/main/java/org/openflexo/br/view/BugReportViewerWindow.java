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
package org.openflexo.br.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.openflexo.ColorCst;
import org.openflexo.br.BugReport;
import org.openflexo.br.BugReports;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.ModuleLoader;
import org.openflexo.view.FlexoDialog;

/**
 * Class used to graphically represent bug reports
 * 
 * @author sguerin
 */
public class BugReportViewerWindow extends FlexoDialog {

	BugReports _bugReports = null;

	private JTable _brTable;

	private BugReportView _brView;

	public BugReportViewerWindow() {
		this(BugReports.instance());
	}

	public BugReportViewerWindow(BugReports bugReports) {
		super((Frame) null, FlexoLocalization.localizedForKey("bug_reports_viewer"));
		_bugReports = bugReports;
		init();
		_brTable.setModel(_bugReports);
		for (int i = 0; i < _bugReports.getColumnCount(); i++) {
			TableColumn col = _brTable.getColumnModel().getColumn(i);
			col.setPreferredWidth(getPreferedColumnSize(i));
		}
		pack();
		setVisible(true);
	}

	public void reload() {
		_bugReports.reload();
	}

	public void save() {
		if (getSelectedBugReport() != null) {
			getSelectedBugReport().save();
			_brView.setEdited(false);
			_brTable.setModel(_bugReports);
			_brTable.validate();
			_brTable.repaint();
		}
	}

	public void saveAll() {
		_bugReports.saveAll();
	}

	public void delete() {
		if (getSelectedBugReport() != null) {
			_bugReports.remove(getSelectedBugReport());
		}
	}

	public void edit() {
		if (getSelectedBugReport() != null) {
			_brView.setEdited(true);
		}
	}

	private BugReport getSelectedBugReport() {
		return _brView.getBugReport();
	}

	void setSelectedBugReport(BugReport aBugReport) {
		_brView.setBugReport(aBugReport);
		deleteButton.setEnabled(true);
		saveButton.setEnabled(true);
		editButton.setEnabled(true);
	}

	private void init() {
		_brTable = new JTable();
		_brTable.setDefaultRenderer(String.class, new BugReportViewerCellRenderer());
		_brTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel rowSM = _brTable.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// Ignore extra messages.
				if (e.getValueIsAdjusting()) {
					return;
				}
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				if (lsm.isSelectionEmpty()) {
					// no rows are selected
				} else {
					int selectedRow = lsm.getMinSelectionIndex();
					setSelectedBugReport(_bugReports.elementAt(selectedRow));
				}
			}
		});

		JScrollPane scrollpane = new JScrollPane(_brTable);
		JPanel viewerPanel = new JPanel(new BorderLayout());
		viewerPanel.setBackground(ColorCst.GUI_BACK_COLOR);
		viewerPanel.add(scrollpane, BorderLayout.CENTER);
		viewerPanel.add(buttonPanel(), BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, viewerPanel, _brView = new BugReportView(false, null));

		getContentPane().add(splitPane);
	}

	public int getPreferedColumnSize(int arg0) {
		switch (arg0) {
		case 0:
			return 40; // identifier
		case 1:
			return 200; // title
		case 2:
			return 50; // severity
		case 3:
			return 50; // status
		case 4:
			return 50; // assigned
		default:
			return 50;
		}
	}

	private JButton deleteButton;

	private JButton editButton;

	private JButton saveButton;

	private ModuleLoader getModuleLoader(){
        return ModuleLoader.instance();
    }

	protected JPanel buttonPanel() {
		JPanel answer = new JPanel(new FlowLayout());
		JButton closeButton = new JButton(FlexoLocalization.localizedForKey("close"));
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				if (getModuleLoader().getProject() == null) {
					System.exit(0);
				}
			}
		});
		answer.add(closeButton);
		JButton reloadButton = new JButton(FlexoLocalization.localizedForKey("reload"));
		reloadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reload();
			}
		});
		answer.add(reloadButton);
		deleteButton = new JButton(FlexoLocalization.localizedForKey("delete"));
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				delete();
			}
		});
		answer.add(deleteButton);
		editButton = new JButton(FlexoLocalization.localizedForKey("edit"));
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				edit();
			}
		});
		answer.add(editButton);
		saveButton = new JButton(FlexoLocalization.localizedForKey("save"));
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		answer.add(saveButton);
		/*
		 * saveAllButton = new
		 * JButton(FlexoLocalization.localizedForKey("save_all"));
		 * saveAllButton.addActionListener(new ActionListener(){ public void
		 * actionPerformed(ActionEvent e){ saveAll(); } });
		 * answer.add(saveAllButton);
		 */
		deleteButton.setEnabled(false);
		editButton.setEnabled(false);
		saveButton.setEnabled(false);
		return answer;
	}

	protected class BugReportViewerCellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (column == 3) {
				BugReport br = _bugReports.elementAt(row);
				int status = br.getStatus();
				switch (status) {
				case 0: // SUBMITTED
					returned.setForeground(Color.RED);
					break;
				case 2: // FIXED
					returned.setForeground(Color.GREEN);
					break;
				case 5: // NREP
					returned.setForeground(Color.ORANGE);
					break;
				default:
					returned.setForeground(Color.BLACK);
				}
			} else {
				returned.setForeground(Color.BLACK);
			}
			return returned;
		}
	}
}
