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
package org.openflexo.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.SaveResourceExceptionList;
import org.openflexo.foundation.rm.SaveResourcePermissionDeniedException;
import org.openflexo.foundation.rm.StorageResourceData;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.FlexoFrame;

/**
 * Dialog allowing to select resources to save
 * 
 * @author sguerin
 * 
 */
public class AskForSaveResources extends FlexoDialog {

	static final Logger logger = Logger.getLogger(AskForSaveResources.class.getPackage().getName());

	ReviewUnsavedModel _reviewUnsavedModel;

	// private SaveResourceExceptionList listOfExceptions = null;
	int returned;

	public static final int SAVE = 0;

	public static final int CANCEL = 1;

	protected JButton confirmButton;
	protected JPanel controlPanel;
	protected String _validateLabel;
	protected String _emptyValidateLabel;

	// public static final int EXCEPTION_RAISED = 2;
	// public static final int PERMISSION_DENIED = 3;

	/**
	 * Constructor
	 * 
	 * @param title
	 * @param resources
	 *            : a vector of FlexoStorageResource
	 */
	public AskForSaveResources(String title, String validateLabel, String emptyValidateLabel,
			List<FlexoStorageResource<? extends StorageResourceData>> resources) {
		super(FlexoFrame.getActiveFrame(), true);
		returned = CANCEL;
		setTitle(title);
		getContentPane().setLayout(new BorderLayout());
		_reviewUnsavedModel = new ReviewUnsavedModel(resources);

		_validateLabel = validateLabel;
		_emptyValidateLabel = emptyValidateLabel;

		JLabel question = new JLabel(" ", SwingConstants.CENTER);
		question.setFont(FlexoCst.BIG_FONT);
		JLabel hint1 = new JLabel(FlexoLocalization.localizedForKey("select_the_resources_that_you_want_to_save"), SwingConstants.CENTER);
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

		JTable reviewTable = new JTable(_reviewUnsavedModel);
		for (int i = 0; i < _reviewUnsavedModel.getColumnCount(); i++) {
			TableColumn col = reviewTable.getColumnModel().getColumn(i);
			col.setPreferredWidth(getPreferedColumnSize(i));
		}

		JScrollPane scrollPane = new JScrollPane(reviewTable);
		JPanel resourcesPanel = new JPanel();
		resourcesPanel.setLayout(new BorderLayout());
		resourcesPanel.add(reviewTable.getTableHeader(), BorderLayout.NORTH);
		resourcesPanel.add(scrollPane, BorderLayout.CENTER);
		resourcesPanel.setPreferredSize(new Dimension(800, 200));

		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());

		confirmButton = new JButton(FlexoLocalization.localizedForKey(validateLabel));
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
				/*
				 * try { hide(); _reviewUnsavedModel.saveSelected(); returned =
				 * SAVE; } catch (SaveResourceExceptionList e1) {
				 * listOfExceptions = e1; returned = EXCEPTION_RAISED; } catch
				 * (SaveResourcePermissionDeniedException e1) {
				 * FlexoController.showError(FlexoLocalization.localizedForKey("could_not_save_permission_denied"));
				 * returned = PERMISSION_DENIED; }
				 */
				returned = SAVE;
				dispose();
			}
		});
		selectAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_reviewUnsavedModel.selectAll();
				updateButtonLabel();
			}
		});
		deselectAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_reviewUnsavedModel.deselectAll();
				updateButtonLabel();
			}
		});
		controlPanel.add(selectAllButton);
		controlPanel.add(deselectAllButton);
		controlPanel.add(cancelButton);
		controlPanel.add(confirmButton);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		contentPanel.add(textPanel, BorderLayout.NORTH);
		contentPanel.add(resourcesPanel, BorderLayout.CENTER);
		contentPanel.add(controlPanel, BorderLayout.SOUTH);

		getContentPane().add(contentPanel, BorderLayout.CENTER);

		setModal(true);
		setSize(1000, 200);
		validate();
		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
		setVisible(true);
	}

	protected void updateButtonLabel() {
		if (_reviewUnsavedModel.getNbOfFilesToSave() == 0) {
			confirmButton.setText(FlexoLocalization.localizedForKey(_emptyValidateLabel));
		} else {
			confirmButton.setText(FlexoLocalization.localizedForKey(_validateLabel));
		}
		controlPanel.validate();
		controlPanel.repaint();
	}

	public void saveSelection() throws SaveResourceExceptionList, SaveResourcePermissionDeniedException {
		_reviewUnsavedModel.saveSelected();
	}

	/**
	 * Return status which could be SAVE, CANCEL, EXCEPTION_RAISED, PERMISSION_DENIED
	 * 
	 * @return
	 */
	public int getStatus() {
		return returned;
	}

	/*
	 * public SaveResourceExceptionList getListOfExceptions() { return
	 * listOfExceptions; }
	 */

	public String savedFilesList() {
		return _reviewUnsavedModel.savedFilesList();
	}

	public int getPreferedColumnSize(int arg0) {
		switch (arg0) {
		case 0:
			return 25; // checkbox
		case 1:
			return 150; // name
		case 2:
			return 150; // type
		case 3:
			return 150; // file_name
		case 4:
			return 250; // last_saved_on
		default:
			return 50;
		}
	}

	public class ReviewUnsavedModel extends AbstractTableModel {

		private List<FlexoStorageResource<? extends StorageResourceData>> _resources;

		private Vector<Boolean> _shouldSave;

		public ReviewUnsavedModel(List<FlexoStorageResource<? extends StorageResourceData>> resources) {
			super();
			_resources = resources;
			_shouldSave = new Vector<Boolean>();
			for (int i = 0; i < resources.size(); i++) {
				FlexoStorageResource<? extends StorageResourceData> res = resources.get(i);
				if (res.needsSaving()) {
					_shouldSave.add(Boolean.TRUE);
				} else {
					_shouldSave.add(Boolean.FALSE);
				}
			}
		}

		@Override
		public int getRowCount() {
			if (_resources == null) {
				return 0;
			}
			return _resources.size();
		}

		@Override
		public int getColumnCount() {
			return 5;
		}

		@Override
		public String getColumnName(int columnIndex) {
			if (columnIndex == 0) {
				return " ";
			} else if (columnIndex == 1) {
				return FlexoLocalization.localizedForKey("name");
			} else if (columnIndex == 2) {
				return FlexoLocalization.localizedForKey("type");
			} else if (columnIndex == 3) {
				return FlexoLocalization.localizedForKey("file_name");
			} else if (columnIndex == 4) {
				return FlexoLocalization.localizedForKey("last_saved_on");
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
			if (_resources == null) {
				return null;
			}
			if (columnIndex == 0) {
				return _shouldSave.elementAt(rowIndex);
			} else if (columnIndex == 1) {
				return _resources.get(rowIndex).getName();
			} else if (columnIndex == 2) {
				return _resources.get(rowIndex).getResourceType().getName();
			} else if (columnIndex == 3) {
				return _resources.get(rowIndex).getFile().getName();
			} else if (columnIndex == 4) {
				Date date = _resources.get(rowIndex).getDiskLastModifiedDate();
				if (date != null) {
					return new SimpleDateFormat("dd/MM HH:mm:ss").format(date);
				} else {
					return "-";
				}
			}
			return null;
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				_shouldSave.setElementAt((Boolean) value, rowIndex);
				updateButtonLabel();
			}
		}

		protected int getNbOfFilesToSave() {
			int nbOfFilesToSave = 0;
			for (int i = 0; i < _shouldSave.size(); i++) {
				if (_shouldSave.elementAt(i).booleanValue()) {
					nbOfFilesToSave++;
				}
			}
			return nbOfFilesToSave;
		}

		public void saveSelected() throws SaveResourceExceptionList, SaveResourcePermissionDeniedException {
			savedFilesList = "";
			SaveResourceExceptionList listOfRaisedExceptions = null;
			int i = 0;
			FlexoProject project = null;
			while (project == null && i < _resources.size()) {
				project = _resources.get(i).getProject();
			}
			List<FlexoStorageResource<? extends StorageResourceData>> resourcesToSave = new ArrayList<FlexoStorageResource<? extends StorageResourceData>>();
			for (i = 0; i < _shouldSave.size(); i++) {
				if (_shouldSave.get(i)) {
					resourcesToSave.add(_resources.get(i));
				}
			}

			int nbOfFilesToSave = getNbOfFilesToSave();

			if (nbOfFilesToSave > 0) {

				if (!ProgressWindow.hasInstance()) {
					ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("saving_selected_resources"), nbOfFilesToSave);
				}

				try {
					project.saveStorageResources(resourcesToSave, ProgressWindow.instance());
				} catch (SaveResourceException e) {
					e.printStackTrace();
					listOfRaisedExceptions = new SaveResourceExceptionList(e);
				}

				ProgressWindow.hideProgressWindow();

				if (listOfRaisedExceptions != null) {
					throw listOfRaisedExceptions;
				}
			}
		}

		private String savedFilesList;

		public String savedFilesList() {
			return savedFilesList;
		}

		public void selectAll() {
			for (int i = 0; i < _shouldSave.size(); i++) {
				_shouldSave.setElementAt(new Boolean(true), i);
				fireTableCellUpdated(i, 0);
			}
		}

		public void deselectAll() {
			for (int i = 0; i < _shouldSave.size(); i++) {
				_shouldSave.setElementAt(new Boolean(false), i);
				fireTableCellUpdated(i, 0);
			}
		}
	}

}
