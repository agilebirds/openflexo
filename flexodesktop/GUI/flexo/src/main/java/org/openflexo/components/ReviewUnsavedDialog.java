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

import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.ReviewUnsavedDialog.ReviewUnsavedModel;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.resource.SaveResourceExceptionList;
import org.openflexo.foundation.resource.SaveResourcePermissionDeniedException;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.view.FlexoFrame;

/**
 * Dialog allowing to select resources to save
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class ReviewUnsavedDialog extends FIBDialog<ReviewUnsavedModel> {

	static final Logger logger = Logger.getLogger(ReviewUnsavedDialog.class.getPackage().getName());

	public static final FileResource FIB_FILE = new FileResource("Fib/ReviewUnsavedDialog.fib");

	ReviewUnsavedModel _reviewUnsavedModel;

	// private SaveResourceExceptionList listOfExceptions = null;
	/*int returned;

	public static final int SAVE = 0;

	public static final int CANCEL = 1;

	protected JButton confirmButton;
	protected JPanel controlPanel;
	protected String _validateLabel;
	protected String _emptyValidateLabel;*/

	// public static final int EXCEPTION_RAISED = 2;
	// public static final int PERMISSION_DENIED = 3;

	/**
	 * Constructor
	 * 
	 * @param title
	 * @param resources
	 *            : a vector of FlexoStorageResource
	 */
	public ReviewUnsavedDialog(String title, FlexoEditor editor, Collection<FlexoResource<?>> resources) {

		super(FIBLibrary.instance().retrieveFIBComponent(FIB_FILE), new ReviewUnsavedModel(editor, resources), FlexoFrame.getActiveFrame(),
				true, FlexoLocalization.getMainLocalizer());

		setTitle(title);

		/*super(FlexoFrame.getActiveFrame(), true);
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
		setVisible(true);*/
	}

	/*protected void updateButtonLabel() {
		if (_reviewUnsavedModel.getNbOfFilesToSave() == 0) {
			confirmButton.setText(FlexoLocalization.localizedForKey(_emptyValidateLabel));
		} else {
			confirmButton.setText(FlexoLocalization.localizedForKey(_validateLabel));
		}
		controlPanel.validate();
		controlPanel.repaint();
	}*/

	public void saveSelection() throws SaveResourceExceptionList, SaveResourcePermissionDeniedException {
		_reviewUnsavedModel.saveSelected();
	}

	/*
	 * public SaveResourceExceptionList getListOfExceptions() { return
	 * listOfExceptions; }
	 */

	public String savedFilesList() {
		return _reviewUnsavedModel.savedFilesList();
	}

	/*public int getPreferedColumnSize(int arg0) {
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
	}*/

	public static class ReviewUnsavedModel implements HasPropertyChangeSupport {

		private final FlexoEditor editor;
		private final Hashtable<FlexoResource<?>, Boolean> resourcesToSave;

		private final PropertyChangeSupport pcSupport;

		public ReviewUnsavedModel(FlexoEditor editor, Collection<FlexoResource<?>> resources) {
			super();
			this.editor = editor;
			pcSupport = new PropertyChangeSupport(this);
			resourcesToSave = new Hashtable<FlexoResource<?>, Boolean>();
			for (FlexoResource<?> r : resources) {
				if (r.isLoaded()) {
					System.out.println("loaded resource " + r + " with " + r.getLoadedResourceData());
				}
				if (r.isLoaded() && r.getLoadedResourceData().isModified()) {
					resourcesToSave.put(r, Boolean.TRUE);
				} else {
					resourcesToSave.put(r, Boolean.FALSE);
				}
			}
		}

		@Override
		public String getDeletedProperty() {
			return null;
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		public Hashtable<FlexoResource<?>, Boolean> getResourcesToSave() {
			return resourcesToSave;
		}

		public boolean isSelected(FlexoResource<?> resource) {
			return resourcesToSave.get(resource);
		}

		public void setSelected(boolean selected, FlexoResource<?> resource) {
			System.out.println("setSelected " + selected + " resource=" + resource);
			resourcesToSave.put(resource, selected);
			pcSupport.firePropertyChange("getNbOfFilesToSave()", -1, getNbOfFilesToSave());
		}

		public Icon getIcon(FlexoResource<?> resource) {
			return IconLibrary.getIconForResource(resource);
		}

		/*@Override
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
		}*/

		/*@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (resources == null) {
				return null;
			}
			if (columnIndex == 0) {
				return shouldSave.elementAt(rowIndex);
			} else if (columnIndex == 1) {
				return resources.get(rowIndex).getName();
			} else if (columnIndex == 2) {
				return resources.get(rowIndex).getResourceType().getName();
			} else if (columnIndex == 3) {
				return resources.get(rowIndex).getFile().getName();
			} else if (columnIndex == 4) {
				Date date = resources.get(rowIndex).getDiskLastModifiedDate();
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
				shouldSave.setElementAt((Boolean) value, rowIndex);
				updateButtonLabel();
			}
		}*/

		public int getNbOfFilesToSave() {
			int nbOfFilesToSave = 0;
			for (FlexoResource<?> r : resourcesToSave.keySet()) {
				if (resourcesToSave.get(r)) {
					nbOfFilesToSave++;
				}
			}
			return nbOfFilesToSave;
		}

		public void saveSelected() throws SaveResourceExceptionList, SaveResourcePermissionDeniedException {

			SaveResourceExceptionList listOfRaisedExceptions = null;

			/*savedFilesList = "";
			SaveResourceExceptionList listOfRaisedExceptions = null;
			int i = 0;
			FlexoProject project = null;
			while (project == null && i < resources.size()) {
				project = resources.get(i).getProject();
			}
			List<FlexoStorageResource<? extends StorageResourceData>> resourcesToSave = new ArrayList<FlexoStorageResource<? extends StorageResourceData>>();
			for (i = 0; i < shouldSave.size(); i++) {
				if (shouldSave.get(i)) {
					resourcesToSave.add(resources.get(i));
				}
			}*/

			int nbOfFilesToSave = getNbOfFilesToSave();

			if (nbOfFilesToSave > 0) {

				/*if (!ProgressWindow.hasInstance()) {
					ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("saving_selected_resources"), nbOfFilesToSave);
				}*/

				FlexoProgress progress = editor.getFlexoProgressFactory().makeFlexoProgress(
						FlexoLocalization.localizedForKey("saving_selected_resources"), nbOfFilesToSave);

				for (FlexoResource<?> r : resourcesToSave.keySet()) {
					if (resourcesToSave.get(r)) {
						try {
							r.save(progress);
						} catch (SaveResourceException e) {
							if (listOfRaisedExceptions == null) {
								listOfRaisedExceptions = new SaveResourceExceptionList(e);
							} else {
								listOfRaisedExceptions.registerNewException(e);
							}
							e.printStackTrace();
						}
					}
				}

				progress.hideWindow();

				if (listOfRaisedExceptions != null) {
					throw listOfRaisedExceptions;
				}

				/*	try {
						project.saveStorageResources(resourcesToSave, ProgressWindow.instance());
					} catch (SaveResourceException e) {
						e.printStackTrace();
						listOfRaisedExceptions = new SaveResourceExceptionList(e);
					}*/

				// ProgressWindow.hideProgressWindow();

				/*if (listOfRaisedExceptions != null) {
					throw listOfRaisedExceptions;
				}*/
			}
		}

		private String savedFilesList;

		public String savedFilesList() {
			return savedFilesList;
		}

		public void selectAll() {
			for (FlexoResource<?> r : resourcesToSave.keySet()) {
				if (!resourcesToSave.get(r)) {
					resourcesToSave.put(r, Boolean.TRUE);
				}
			}
		}

		public void deselectAll() {
			for (FlexoResource<?> r : resourcesToSave.keySet()) {
				if (resourcesToSave.get(r)) {
					resourcesToSave.put(r, Boolean.FALSE);
				}
			}
		}
	}

}
