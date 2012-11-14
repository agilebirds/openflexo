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
package org.openflexo.dre;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.openflexo.FlexoCst;
import org.openflexo.drm.DocItemAction;
import org.openflexo.drm.DocSubmissionReport;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.controller.FlexoController;

/**
 * Dialog allowing to show and select doc submission items
 * 
 * @author sguerin
 * 
 */
public class DocSubmissionReportDialog extends FlexoDialog {

	static final Logger logger = Logger.getLogger(DocSubmissionReportDialog.class.getPackage().getName());

	DocSubmissionReportModel _docSubmissionReportModel;

	int returned;

	public static final int IMPORT = 0;

	public static final int CANCEL = 1;

	protected JPanel controlPanel;

	protected JTable reviewTable;
	protected HTMLPreviewPanel previewPanel;

	/**
	 * Constructor
	 * 
	 * @param title
	 * @param resources
	 *            : a vector of FlexoStorageResource
	 */
	public DocSubmissionReportDialog(DocSubmissionReport report) {
		super(FlexoController.getActiveFrame(), true);
		returned = CANCEL;
		setTitle(FlexoLocalization.localizedForKey("import_doc_submission_report"));
		getContentPane().setLayout(new BorderLayout());
		_docSubmissionReportModel = new DocSubmissionReportModel(report);

		JLabel question = new JLabel(" ", SwingConstants.CENTER);
		question.setFont(FlexoCst.BIG_FONT);
		JLabel hint1 = new JLabel(FlexoLocalization.localizedForKey("select_the_doc_submissions_you_want_to_import"), SwingConstants.CENTER);
		// hint1.setFont(FlexoCst.MEDIUM_FONT);
		// JLabel hint2 = new
		// JLabel(FlexoLocalization.localizedForKey("select_the_resources_that_you_want_to_save"),JLabel.CENTER);
		JLabel hint2 = new JLabel(" ", SwingConstants.CENTER);
		// hint2.setFont(FlexoCst.MEDIUM_FONT);

		JPanel textPanel = new JPanel();
		textPanel.setSize(1000, 50);
		textPanel.setLayout(new BorderLayout());
		textPanel.add(question, BorderLayout.NORTH);
		textPanel.add(hint1, BorderLayout.CENTER);
		textPanel.add(hint2, BorderLayout.SOUTH);

		reviewTable = new JTable(_docSubmissionReportModel);
		for (int i = 0; i < _docSubmissionReportModel.getColumnCount(); i++) {
			TableColumn col = reviewTable.getColumnModel().getColumn(i);
			col.setPreferredWidth(getPreferedColumnSize(i));
		}

		JScrollPane scrollPane = new JScrollPane(reviewTable);
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BorderLayout());
		tablePanel.add(reviewTable.getTableHeader(), BorderLayout.NORTH);
		tablePanel.add(scrollPane, BorderLayout.CENTER);
		tablePanel.setPreferredSize(new Dimension(800, 200));
		reviewTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		reviewTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// Ignore extra messages.
				if (e.getValueIsAdjusting()) {
					return;
				}
				previewPanel.update();
			}
		});

		previewPanel = new HTMLPreviewPanel();

		JSplitPane reportPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tablePanel, previewPanel);

		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());

		JButton confirmButton = new JButton(FlexoLocalization.localizedForKey("import"));
		JButton cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel"));
		JButton selectAllButton = new JButton(FlexoLocalization.localizedForKey("select_all"));
		JButton deselectAllButton = new JButton(FlexoLocalization.localizedForKey("deselect_all"));

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				returned = CANCEL;
				dispose();
			}
		});
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				returned = IMPORT;
				dispose();
			}
		});
		selectAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_docSubmissionReportModel.selectAll();
			}
		});
		deselectAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_docSubmissionReportModel.deselectAll();
			}
		});
		controlPanel.add(selectAllButton);
		controlPanel.add(deselectAllButton);
		controlPanel.add(cancelButton);
		controlPanel.add(confirmButton);

		getRootPane().setDefaultButton(confirmButton);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		contentPanel.add(textPanel, BorderLayout.NORTH);
		contentPanel.add(reportPanel, BorderLayout.CENTER);
		contentPanel.add(controlPanel, BorderLayout.SOUTH);

		getContentPane().add(contentPanel, BorderLayout.CENTER);

		setModal(true);
		setSize(1000, 700);
		validate();
		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
		show();
	}

	protected class HTMLPreviewPanel extends JPanel {
		private JPanel shortHTMLDescriptionPanel;
		private JPanel fullHTMLDescriptionPanel;
		private JLabel shortHTMLDescriptionLabel;
		private JLabel fullHTMLDescriptionLabel;

		protected HTMLPreviewPanel() {
			super(new BorderLayout());

			shortHTMLDescriptionPanel = new JPanel(new BorderLayout());
			JLabel shortDescription = new JLabel();
			shortDescription.setText(FlexoLocalization.localizedForKey("short_formatted_description", shortDescription));
			shortDescription.setHorizontalAlignment(SwingConstants.CENTER);
			shortDescription.setForeground(Color.DARK_GRAY);
			shortHTMLDescriptionLabel = new JLabel();
			shortHTMLDescriptionLabel.setBackground(Color.WHITE);
			shortHTMLDescriptionLabel.setOpaque(true);
			shortHTMLDescriptionLabel.setVerticalAlignment(SwingConstants.TOP);
			shortHTMLDescriptionLabel.setHorizontalAlignment(SwingConstants.LEFT);
			shortHTMLDescriptionLabel.setPreferredSize(new Dimension(500, 100));
			shortHTMLDescriptionPanel.add(shortDescription, BorderLayout.NORTH);
			shortHTMLDescriptionPanel.add(new JScrollPane(shortHTMLDescriptionLabel), BorderLayout.CENTER);

			fullHTMLDescriptionPanel = new JPanel(new BorderLayout());
			JLabel fullDescription = new JLabel();
			fullDescription.setText(FlexoLocalization.localizedForKey("full_formatted_description", fullDescription));
			fullDescription.setHorizontalAlignment(SwingConstants.CENTER);
			fullDescription.setForeground(Color.DARK_GRAY);
			fullHTMLDescriptionLabel = new JLabel();
			fullHTMLDescriptionLabel.setPreferredSize(new Dimension(500, 200));
			fullHTMLDescriptionLabel.setBackground(Color.WHITE);
			fullHTMLDescriptionLabel.setOpaque(true);
			fullHTMLDescriptionLabel.setVerticalAlignment(SwingConstants.TOP);
			fullHTMLDescriptionLabel.setHorizontalAlignment(SwingConstants.LEFT);
			fullHTMLDescriptionPanel.add(fullDescription, BorderLayout.NORTH);
			fullHTMLDescriptionPanel.add(new JScrollPane(fullHTMLDescriptionLabel), BorderLayout.CENTER);

			add(shortHTMLDescriptionPanel, BorderLayout.NORTH);
			add(fullHTMLDescriptionPanel, BorderLayout.CENTER);
		}

		protected void update() {
			DocItemAction action = _docSubmissionReportModel.getActionAt(reviewTable.getSelectedRow());
			if (action != null && action.getVersion() != null) {
				shortHTMLDescriptionLabel.setText("<html>" + action.getVersion().getShortHTMLDescription() + "</html>");
				fullHTMLDescriptionLabel.setText("<html>" + action.getVersion().getFullHTMLDescription() + "</html>");
			} else {
				shortHTMLDescriptionLabel.setText("");
				fullHTMLDescriptionLabel.setText("");
			}
			revalidate();
			repaint();
		}
	}

	/**
	 * Return status which could be IMPORT or CANCEL
	 * 
	 * @return
	 */
	public int getStatus() {
		return returned;
	}

	public int getPreferedColumnSize(int arg0) {
		switch (arg0) {
		case 0:
			return 25; // checkbox
		case 1:
			return 100; // name
		case 2:
			return 80; // author
		case 3:
			return 150; // date
		case 4:
			return 80; // action
		case 5:
			return 80; // language
		case 6:
			return 50; // version
		case 7:
			return 200; // note
		default:
			return 50;
		}
	}

	public class DocSubmissionReportModel extends AbstractTableModel {

		private DocSubmissionReport _report;

		private Vector<Boolean> _shouldImport;

		public DocSubmissionReportModel(DocSubmissionReport report) {
			super();
			_report = report;
			_shouldImport = new Vector<Boolean>();
			for (int i = 0; i < _report.size(); i++) {
				_shouldImport.add(new Boolean(true));
			}
		}

		@Override
		public int getRowCount() {
			if (_report == null) {
				return 0;
			}
			return _report.size();
		}

		@Override
		public int getColumnCount() {
			return 8;
		}

		public DocItemAction getActionAt(int row) {
			if (row < getRowCount()) {
				return (DocItemAction) _report.getSubmissionActions().elementAt(row);
			}
			return null;
		}

		@Override
		public String getColumnName(int columnIndex) {
			if (columnIndex == 0) {
				return " ";
			} else if (columnIndex == 1) {
				return FlexoLocalization.localizedForKey("doc_item");
			} else if (columnIndex == 2) {
				return FlexoLocalization.localizedForKey("author");
			} else if (columnIndex == 3) {
				return FlexoLocalization.localizedForKey("date");
			} else if (columnIndex == 4) {
				return FlexoLocalization.localizedForKey("action");
			} else if (columnIndex == 5) {
				return FlexoLocalization.localizedForKey("language");
			} else if (columnIndex == 6) {
				return FlexoLocalization.localizedForKey("version");
			} else if (columnIndex == 7) {
				return FlexoLocalization.localizedForKey("note");
			}
			return "???";
		}

		@Override
		public Class getColumnClass(int columnIndex) {
			if (columnIndex == 0) {
				return Boolean.class;
			} else {
				return String.class;
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 0 ? true : false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (_report == null) {
				return null;
			}
			DocItemAction action = getActionAt(rowIndex);
			if (columnIndex == 0) {
				return _shouldImport.elementAt(rowIndex);
			} else if (columnIndex == 1) {
				return action.getItem().getIdentifier();
			} else if (columnIndex == 2) {
				return action.getAuthorId();
			} else if (columnIndex == 3) {
				return action.getLocalizedFullActionDate();
			} else if (columnIndex == 4) {
				return action.getLocalizedActionType();
			} else if (columnIndex == 5) {
				return action.getVersion().getLanguageId();
			} else if (columnIndex == 6) {
				return action.getVersion().getVersion();
			} else if (columnIndex == 7) {
				return action.getNote();
			}
			return null;
		}

		public void setValueAt(Boolean value, int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				_shouldImport.setElementAt(value, rowIndex);
			}
		}

		public void selectAll() {
			for (int i = 0; i < _shouldImport.size(); i++) {
				_shouldImport.setElementAt(new Boolean(true), i);
				fireTableCellUpdated(i, 0);
			}
		}

		public void deselectAll() {
			for (int i = 0; i < _shouldImport.size(); i++) {
				_shouldImport.setElementAt(new Boolean(false), i);
				fireTableCellUpdated(i, 0);
			}
		}

		public Vector<DocItemAction> getSelectedActions() {
			Vector<DocItemAction> returned = new Vector<DocItemAction>();

			for (int i = 0; i < _shouldImport.size(); i++) {
				if (_shouldImport.elementAt(i).booleanValue()) {
					returned.add(getActionAt(i));
				}
			}
			return returned;
		}
	}

	public Vector getSelectedActions() {
		return _docSubmissionReportModel.getSelectedActions();
	}
}
